package com.davidjo.greenworld.ui.home;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.FragmentHomeBinding;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends AuthFragment {

    public static final String REQUEST_ADD = "com.davidjo.greenworld.request_add";
    public static final String RESULT_ADD_SUCCESS = "com.davidjo.greenworld.result_add_success";


    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                viewModel.onBackClick();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentHomeBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // UI 에 리스너 부여

        binding.cardViewAdd.setOnClickListener(v -> viewModel.onAddClick());
        binding.cardViewToday.setOnClickListener(v -> viewModel.onTodayClick());
        binding.cardViewCalendar.setOnClickListener(v -> viewModel.onCalendarClick());
        binding.cardViewStatistic.setOnClickListener(v -> viewModel.onStatisticClick());
        binding.cardViewTrend.setOnClickListener(v -> viewModel.onTrendClick());

        // 뷰모델에서 전송한 이벤트 처리 (각 화면으로 이동하기)

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof HomeViewModel.Event.ConfirmSignOut) {
                String message = ((HomeViewModel.Event.ConfirmSignOut)event).message;
                showConfirmSignOutDialog(message);
            } else if (event instanceof HomeViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof HomeViewModel.Event.NavigateToCategoryScreen) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToCategoryFragment3();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof HomeViewModel.Event.NavigateToTodayScreen) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToTodayFragment(-1);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof HomeViewModel.Event.NavigateToCalendarScreen) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToCalendarFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof HomeViewModel.Event.NavigateToStatisticScreen) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToStatisticFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof HomeViewModel.Event.NavigateToTrendScreen) {
                NavDirections action = HomeFragmentDirections.actionHomeFragmentToTrendFragment();
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof HomeViewModel.Event.ShowAddResultMessage) {
                String message = ((HomeViewModel.Event.ShowAddResultMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        // AddFragment 에서 보낸 결과 처리

        getParentFragmentManager().setFragmentResultListener(REQUEST_ADD, getViewLifecycleOwner(), (requestKey, result) ->
                viewModel.onAddResult(result.getBoolean(RESULT_ADD_SUCCESS)));

        setHasOptionsMenu(true);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.home_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == android.R.id.home) {
            viewModel.onBackClick();
            return true;
        } else if (itemId == R.id.action_privacy_policy) {
            showPrivacyPolicyDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showConfirmSignOutDialog(String message) {
        new AlertDialog.Builder(context)
                .setTitle("로그아웃")
                .setMessage(message)
                .setPositiveButton("네", (dialog, which) -> viewModel.onSignOutConfirmed())
                .setNegativeButton("아니오", null)
                .show();
    }

    private void showPrivacyPolicyDialog() {
        new AlertDialog.Builder(context)
                .setTitle(R.string.privacy_policy)
                .setMessage(R.string.privacy_policy_contents)
                .setPositiveButton("확인", null)
                .show();
    }

}
