package com.davidjo.remedialexercise.ui.diagnosis.home;

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
import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.databinding.FragmentDiagnosisBinding;
import com.davidjo.remedialexercise.util.NameUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DiagnosisFragment extends Fragment {

    private FragmentDiagnosisBinding binding;
    private DiagnosisViewModel viewModel;
    private Context context;


    public DiagnosisFragment() {
        super(R.layout.fragment_diagnosis);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentDiagnosisBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(DiagnosisViewModel.class);

        buildDiagnosisUI();

        binding.fabSubmitDiagnosis.setOnClickListener(v -> viewModel.onSubmitClicked());

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {

            NavDirections action;

            if (event instanceof DiagnosisViewModel.Event.NavigateSuccessScreen) {

                DiagnosisViewModel.Event.NavigateSuccessScreen navigateSuccessScreen =
                        (DiagnosisViewModel.Event.NavigateSuccessScreen) event;
                action = DiagnosisFragmentDirections.actionDiagnosisFragmentToDiagnosisSuccessFragment(
                        navigateSuccessScreen.bodyPart
                );
                Navigation.findNavController(view).navigate(action);

            } else if (event instanceof DiagnosisViewModel.Event.NavigateFailureScreen) {

                DiagnosisViewModel.Event.NavigateFailureScreen navigateFailureScreen =
                        (DiagnosisViewModel.Event.NavigateFailureScreen) event;
                action = DiagnosisFragmentDirections.actionDiagnosisFragmentToDiagnosisFailureFragment(
                        navigateFailureScreen.message
                );
                Navigation.findNavController(view).navigate(action);

            } else if (event instanceof DiagnosisViewModel.Event.ShowMonthsInputUI) {

                DiagnosisViewModel.Event.ShowMonthsInputUI showMonthsInputUI =
                        (DiagnosisViewModel.Event.ShowMonthsInputUI) event;
                binding.groupMonthsAfterSurgery.setVisibility(showMonthsInputUI.show ?
                        View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }

    private void buildDiagnosisUI() {

        buildBodyPartSpinner();
        buildPainLevelSpinner();
        buildMonthsSpinner();

        binding.checkBoxGotSurgery.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.onGotSurgeryChecked(isChecked));

        binding.checkBoxMultiplePain.setOnCheckedChangeListener((buttonView, isChecked) ->
                viewModel.onMultiplePainChecked(isChecked));

        viewModel.getAnswer().observe(getViewLifecycleOwner(), answer -> {
            binding.checkBoxGotSurgery.setChecked(answer.gotSurgery);
            binding.checkBoxMultiplePain.setChecked(answer.multiplePain);
        });
    }

    private void buildBodyPartSpinner() {

        List<BodyPart> bodyParts = Arrays.asList(BodyPart.values());
        List<String> bodyPartNames = bodyParts.stream().map(NameUtils::getBodyPartName).collect(Collectors.toList());
        Map<BodyPart, Integer> bodyPartMap = new HashMap<>();
        for (int i = 0; i < bodyParts.size(); i++) {
            bodyPartMap.put(bodyParts.get(i), i);
        }
        ArrayAdapter<String> bodyPartAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, bodyPartNames);
        binding.spinnerBodyPart.setAdapter(bodyPartAdapter);
        binding.spinnerBodyPart.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onBodyPartSelected(bodyParts.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        viewModel.getAnswer().observe(getViewLifecycleOwner(), answer -> {
            Integer bodyPartSelection = bodyPartMap.get(answer.bodyPart);
            binding.spinnerBodyPart.setSelection(bodyPartSelection != null ? bodyPartSelection : 0);
        });
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

        viewModel.getAnswer().observe(getViewLifecycleOwner(), answer -> {
            Integer painLevelSelection = painLevelMap.get(answer.painLevel);
            binding.spinnerPainLevel.setSelection(painLevelSelection != null ? painLevelSelection : 0);
        });
    }

    private void buildMonthsSpinner() {

        int[] months = {0, 5, 11, 12};
        String[] monthNames = {"1개월 미만", "6개월 미만", "1년 미만", "1년 이상"};
        Map<Integer, Integer> monthsMap = new HashMap<>();
        for (int i = 0; i < months.length; i++) {
            monthsMap.put(months[i], i);
        }
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_dropdown_item,
                monthNames);
        binding.spinnerMonthsAfterSurgery.setAdapter(monthsAdapter);
        binding.spinnerMonthsAfterSurgery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onMonthsAfterSurgerySelected(months[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        viewModel.getAnswer().observe(getViewLifecycleOwner(), answer -> {
            Integer monthsSelection = monthsMap.get(answer.monthsAfterSurgery);
            binding.spinnerMonthsAfterSurgery.setSelection(monthsSelection != null ? monthsSelection : 0);
        });
    }

}






