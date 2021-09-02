package com.davidjo.remedialexercise.ui.training.survey;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.plan.Plan;
import com.davidjo.remedialexercise.databinding.FragmentSurveyBinding;
import com.davidjo.remedialexercise.util.TimeUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SurveyFragment extends Fragment {

    private Context context;
    private FragmentSurveyBinding binding;
    private SurveyViewModel viewModel;

    public SurveyFragment() {
        super(R.layout.fragment_survey);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentSurveyBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(SurveyViewModel.class);

        buildPainLevelSpinner();

        binding.checkBoxEarnest.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.onEarnestChecked(isChecked));
        binding.checkBoxOverdo.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.onOverdoChecked(isChecked));
        binding.buttonClosePlan.setOnClickListener(v -> viewModel.onClosePlanClicked());
        binding.buttonExtendPlan.setOnClickListener(v -> viewModel.onExtendPlanClicked());

        viewModel.getEarnest().observe(getViewLifecycleOwner(), earnest -> binding.checkBoxEarnest.setChecked(earnest));
        viewModel.getOverdo().observe(getViewLifecycleOwner(), overdo -> binding.checkBoxOverdo.setChecked(overdo));
        viewModel.getDays().observe(getViewLifecycleOwner(), days -> {
            if (days != null) {
                String strYouFinished = String.format(Locale.getDefault(), getString(R.string.you_finished_plan), days);
                binding.textViewYouFinished.setText(strYouFinished);
            }
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof SurveyViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof SurveyViewModel.Event.NavigateToPlanScreen) {
                SurveyViewModel.Event.NavigateToPlanScreen navigateToPlanScreen =
                        (SurveyViewModel.Event.NavigateToPlanScreen) event;
                NavDirections action = SurveyFragmentDirections
                        .actionSurveyFragmentToPlanFragment(navigateToPlanScreen.plan.getBodyPart());
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void buildPainLevelSpinner() {

        int[] painLevels = {1, 2, 3, 4, 5};
        String[] painLevelNames = {"매우 약함", "약함", "보통", "심함", "매우 심함"};
        Map<Integer, Integer> painLevelMap = new HashMap<>();
        for (int i = 0; i < painLevels.length; i++) {
            painLevelMap.put(painLevels[i], i);
        }

        ArrayAdapter<String> painLevelAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, painLevelNames);
        binding.spinnerPainLevel.setAdapter(painLevelAdapter);
        binding.spinnerPainLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onPainLevelSelected(painLevels[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        viewModel.getPainLevel().observe(getViewLifecycleOwner(), level -> {
            Integer painLevelSelection = painLevelMap.get(level);
            binding.spinnerPainLevel.setSelection(painLevelSelection != null ? painLevelSelection : 0);
        });
    }

}





