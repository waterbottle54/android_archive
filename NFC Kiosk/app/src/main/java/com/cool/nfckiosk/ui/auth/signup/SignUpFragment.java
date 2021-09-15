package com.cool.nfckiosk.ui.auth.signup;

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
import com.cool.nfckiosk.databinding.FragmentSignUpBinding;
import com.cool.nfckiosk.ui.nfc.NfcDialogFragment;
import com.cool.nfckiosk.util.OnTextChangedListener;
import com.cool.nfckiosk.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SignUpFragment extends AuthFragment {

    private FragmentSignUpBinding binding;
    private SignUpViewModel viewModel;

    
    public SignUpFragment() {
        super(R.layout.fragment_sign_up);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSignUpBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);

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
        binding.editTextPasswordConfirm.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(String text) {
                viewModel.onPasswordConfirmChanged(text);
            }
        });
        binding.textViewCompleteSignUp.setOnClickListener(v -> viewModel.onSignUpClicked());
        binding.buttonTag.setOnClickListener(v -> viewModel.onTagClick());

        viewModel.getTables().observe(getViewLifecycleOwner(), tables -> {
            if (!tables.isEmpty()) {
                String strCountTagActivated = String.format(Locale.getDefault(),
                        getString(R.string.count_tag_activated),
                        tables.size());
                binding.textViewNumberTag.setText(strCountTagActivated);
            }
            binding.textViewNumberTag.setVisibility(!tables.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        });
        
        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SignUpViewModel.Event.ShowShortUserIdMessage) {
                String message = ((SignUpViewModel.Event.ShowShortUserIdMessage)event).message;
                hideKeyboard(requireView());
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof SignUpViewModel.Event.ShowShortPasswordMessage) {
                String message = ((SignUpViewModel.Event.ShowShortPasswordMessage)event).message;
                hideKeyboard(requireView());
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof SignUpViewModel.Event.ShowIncorrectPasswordConfirmMessage) {
                String message = ((SignUpViewModel.Event.ShowIncorrectPasswordConfirmMessage)event).message;
                hideKeyboard(requireView());
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof SignUpViewModel.Event.ShowSignUpFailureMessage) {
                String message = ((SignUpViewModel.Event.ShowSignUpFailureMessage) event).message;
                hideKeyboard(requireView());
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof SignUpViewModel.Event.NavigateToHomeScreen) {
                NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToStatusFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof SignUpViewModel.Event.ShowNfcWriteScreen) {
                String textToWrite = ((SignUpViewModel.Event.ShowNfcWriteScreen) event).textToWrite;
                NavDirections action = SignUpFragmentDirections.actionGlobalNfcDialogFragment(textToWrite);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof SignUpViewModel.Event.ShowNfcActivatedMessage) {
                String message = ((SignUpViewModel.Event.ShowNfcActivatedMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            } else if (event instanceof SignUpViewModel.Event.ShowNfcWriteFailureMessage) {
                String message = ((SignUpViewModel.Event.ShowNfcWriteFailureMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        getParentFragmentManager().setFragmentResultListener(NfcDialogFragment.NFC_REQUEST, getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean writeSuccess = result.getBoolean(NfcDialogFragment.NFC_RESULT_WRITE_SUCCESS);
                    viewModel.onNfcWriteResult(writeSuccess);
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
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}