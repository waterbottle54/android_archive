package com.davidjo.remedialexercise.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    public static final String DESTINATION_INITIATE_FRAGMENT = "initiate_fragment";
    public static final String DESTINATION_LEARN_FRAGMENT = "learn_fragment";

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentHomeBinding binding = FragmentHomeBinding.bind(view);

        binding.cardDoDiagnosis.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionIntroFragmentToSymptomsFragment();
            Navigation.findNavController(view).navigate(action);
        });

        binding.cardPlanRehab.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections
                    .actionHomeFragmentToPromptBodyPartFragment(DESTINATION_INITIATE_FRAGMENT);
            Navigation.findNavController(view).navigate(action);
        });

        binding.cardLearnRehab.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections
                    .actionHomeFragmentToPromptBodyPartFragment(DESTINATION_LEARN_FRAGMENT);
            Navigation.findNavController(view).navigate(action);
        });
        binding.cardDoRehab.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToTrainingFragment();
            Navigation.findNavController(view).navigate(action);
        });
        binding.cardShowStatistics.setOnClickListener(v -> {
            NavDirections action = HomeFragmentDirections.actionHomeFragmentToStatisticFragment();
            Navigation.findNavController(view).navigate(action);
        });
    }

}
