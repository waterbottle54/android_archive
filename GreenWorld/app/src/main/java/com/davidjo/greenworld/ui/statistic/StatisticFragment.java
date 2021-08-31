package com.davidjo.greenworld.ui.statistic;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.FragmentStatisticBinding;
import com.davidjo.greenworld.util.Utils;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticFragment extends AuthFragment {

    private FragmentStatisticBinding binding;
    private StatisticViewModel viewModel;


    public StatisticFragment() {
        super(R.layout.fragment_statistic);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentStatisticBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(StatisticViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);

        // 월간 점수를 텍스트뷰에 표시하고 이미지뷰에 사진 표시

        viewModel.getMonthlyScore().observe(getViewLifecycleOwner(), monthlyScore -> {
            if (monthlyScore != null) {
                String strScore = String.format(Locale.getDefault(), getString(R.string.monthly_score_format), monthlyScore);
                binding.textViewMonthlyScore.setText(strScore);

                binding.imageViewStaticsticResult.setBackgroundResource(
                        Utils.getMonthlyScoreImageResource(getResources(), monthlyScore));

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
            binding.imageViewStaticsticResult.setVisibility(monthlyScore != null ? View.VISIBLE : View.INVISIBLE);
        });

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof StatisticViewModel.Event.NavigateBack) {
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
