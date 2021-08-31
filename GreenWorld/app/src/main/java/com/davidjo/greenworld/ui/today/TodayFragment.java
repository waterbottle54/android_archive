package com.davidjo.greenworld.ui.today;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.FragmentTodayBinding;
import com.davidjo.greenworld.util.Utils;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TodayFragment extends AuthFragment {

    private FragmentTodayBinding binding;
    private TodayViewModel viewModel;


    public TodayFragment() {
        super(R.layout.fragment_today);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentTodayBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TodayViewModel.class);

        binding.textViewDate.setText(Utils.formatDate(getResources(), viewModel.getEpochDays()));
        binding.progressBar.setVisibility(View.VISIBLE);

        // 액션 리사이클러에 연결될 어댑터

        DetailedActionsAdapter adapter = new DetailedActionsAdapter(context);
        binding.recyclerAction.setAdapter(adapter);
        binding.recyclerAction.setHasFixedSize(true);

        // 액션들을 리사이클러뷰에 표시

        viewModel.getDetailedActions().observe(getViewLifecycleOwner(), actions -> {
            adapter.submitList(actions);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textViewNoActions.setVisibility(actions.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        });

        // 일간 점수를 텍스트뷰에 표시

        viewModel.getScore().observe(getViewLifecycleOwner(), score -> {
            if (score != null) {
                String strScore = String.format(Locale.getDefault(), getString(R.string.score_format), score);
                binding.textViewDailyScore.setText(strScore);
            }
        });

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof TodayViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            }
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

}
