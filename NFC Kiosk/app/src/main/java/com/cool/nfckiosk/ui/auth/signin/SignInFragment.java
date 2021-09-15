package com.cool.nfckiosk.ui.auth.signin;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.cool.nfckiosk.R;
import com.cool.nfckiosk.databinding.FragmentSignInBinding;
import com.cool.nfckiosk.ui.nfc.NfcDialogFragment;
import com.cool.nfckiosk.util.OnTextChangedListener;
import com.cool.nfckiosk.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignInFragment extends AuthFragment {

    private FragmentSignInBinding binding;
    private SignInViewModel viewModel;

    public SignInFragment() {
        super(R.layout.fragment_sign_in);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSignInBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SignInViewModel.class);

        // UI 에 리스너 부여

        binding.editTextId.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onUserIdChanged(text);
            }
        });
        binding.editTextPassword.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onPasswordChanged(text);
            }
        });
        binding.buttonSignIn.setOnClickListener(v -> viewModel.onSignInClicked());
        binding.buttonOrder.setOnClickListener(v -> viewModel.onOrderClicked());
        binding.textViewSignUp.setOnClickListener(v -> viewModel.onSignUpClicked());

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SignInViewModel.Event.ShowShortUserIdMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignInViewModel.Event.ShowShortUserIdMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignInViewModel.Event.ShowShortPasswordMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignInViewModel.Event.ShowShortPasswordMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignInViewModel.Event.ShowSignInFailureMessage) {
                hideKeyboard(requireView());
                Snackbar.make(requireView(),
                        ((SignInViewModel.Event.ShowSignInFailureMessage)event).message,
                        Snackbar.LENGTH_SHORT)
                        .show();
            } else if (event instanceof SignInViewModel.Event.NavigateToSignUpScreen) {
                NavDirections action = SignInFragmentDirections.actionSignInFragment2ToSignUpFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof SignInViewModel.Event.NavigateToAdminScreen) {
                NavDirections action = SignInFragmentDirections.actionSignInFragmentToStatusFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof SignInViewModel.Event.ShowNfcReadScreen) {
                NavDirections action = SignInFragmentDirections.actionGlobalNfcDialogFragment(null);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof SignInViewModel.Event.ShowInvalidNfcMessage) {
                String message = ((SignInViewModel.Event.ShowInvalidNfcMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof SignInViewModel.Event.NavigateToOrderScreen) {
                String userId = ((SignInViewModel.Event.NavigateToOrderScreen) event).userId;
                int tableNumber = ((SignInViewModel.Event.NavigateToOrderScreen) event).tableNumber;
                NavDirections action = SignInFragmentDirections.actionSignInFragmentToOrderFragment(
                        tableNumber, userId);
                Navigation.findNavController(requireView()).navigate(action);
            }
        });

        getParentFragmentManager().setFragmentResultListener(NfcDialogFragment.NFC_REQUEST, getViewLifecycleOwner(),
                (requestKey, result) -> {
                    String nfcText = result.getString(NfcDialogFragment.NFC_RESULT_TEXT);
                    viewModel.onNfcReadResult(nfcText);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        viewModel.onAuthStateChanged(firebaseAuth);
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}










