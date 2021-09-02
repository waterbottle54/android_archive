package com.davidjo.remedialexercise.ui.initiate.plan;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.plan.Plan;
import com.davidjo.remedialexercise.databinding.BottomSheetRuleBinding;
import com.davidjo.remedialexercise.databinding.FragmentPlanBinding;
import com.davidjo.remedialexercise.util.TimeUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PlanFragment extends Fragment {

    private Context context;
    private FragmentPlanBinding binding;
    private PlanViewModel viewModel;

    private BottomSheetDialog ruleDialog;
    private BottomSheetRuleBinding ruleBinding;


    public PlanFragment() {
        super(R.layout.fragment_plan);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentPlanBinding.bind(view);
        ruleBinding = BottomSheetRuleBinding.inflate(getLayoutInflater());
        viewModel = new ViewModelProvider(this).get(PlanViewModel.class);

        buildRuleDialog();

        binding.textViewSchedule.setOnClickListener(v -> showDateRangePicker());

        binding.textViewRule.setOnClickListener(v -> ruleDialog.show());

        binding.fabSavePlan.setOnClickListener(v -> viewModel.onSavePlanClicked());

        viewModel.getPlan().observe(getViewLifecycleOwner(), plan -> {

            String strSchedule = String.format(Locale.getDefault(),
                    getString(R.string.schedule_format),
                    TimeUtils.getDateString(getResources(), plan.getStartTime()),
                    TimeUtils.getDateString(getResources(), plan.getEndTime())
            );
            binding.textViewSchedule.setText(strSchedule);

            String strRule = String.format(Locale.getDefault(),
                    getString(R.string.rule_format),
                    plan.getMinutesPerRepetition(),
                    plan.getRepetitions()
            );
            binding.textViewRule.setText(strRule);

            ruleBinding.numberPickerRepetitions.setValue(plan.getRepetitions());
            ruleBinding.numberPickerMinutes.setValue(plan.getMinutesPerRepetition());
        });

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof PlanViewModel.Event.ShowWrongDateMessage) {
                PlanViewModel.Event.ShowWrongDateMessage showWrongDateMessage =
                        (PlanViewModel.Event.ShowWrongDateMessage) event;
                Snackbar.make(requireView(), showWrongDateMessage.message, Snackbar.LENGTH_LONG)
                        .setAction("다시 설정하기", v -> showDateRangePicker())
                        .show();
            } else if (event instanceof PlanViewModel.Event.NavigateBackWithResult) {
                Bundle result = new Bundle();
                result.putBoolean("plan_result", true);
                getParentFragmentManager().setFragmentResult("plan_request", result);
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof PlanViewModel.Event.ShowExistingPlanMessageAndDisableSavePlan) {
                PlanViewModel.Event.ShowExistingPlanMessageAndDisableSavePlan showExistingPlanMessageAndDisableSavePlan =
                        (PlanViewModel.Event.ShowExistingPlanMessageAndDisableSavePlan) event;
                Snackbar.make(requireView(), showExistingPlanMessageAndDisableSavePlan.message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("계획 수정하기", v -> viewModel.onModifyPlanClicked())
                        .show();
                binding.fabSavePlan.setEnabled(false);
            } else if (event instanceof PlanViewModel.Event.EnableSavePlan) {
                binding.fabSavePlan.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        ruleBinding = null;
    }

    private void buildRuleDialog() {

        ruleDialog = new BottomSheetDialog(context);
        ruleDialog.setContentView(ruleBinding.getRoot());

        ruleBinding.numberPickerRepetitions.setMinValue(1);
        ruleBinding.numberPickerRepetitions.setMaxValue(10);
        ruleBinding.numberPickerMinutes.setMinValue(1);
        ruleBinding.numberPickerMinutes.setMaxValue(30);

        ruleBinding.fabConfirmRule.setOnClickListener(v -> {
            viewModel.onRuleSelected(
                    ruleBinding.numberPickerRepetitions.getValue(),
                    ruleBinding.numberPickerMinutes.getValue()
            );
            ruleDialog.dismiss();
        });
    }

    private void showDateRangePicker() {

        Plan plan = viewModel.getPlan().getValue();
        if (plan == null) {
            return;
        }
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                        .setSelection(new Pair<>(plan.getStartTime(), plan.getEndTime()))
                        .build();
        dateRangePicker.show(getChildFragmentManager(), "date_range_picker");
        dateRangePicker.addOnPositiveButtonClickListener(selection ->
                viewModel.onTimeSelected(selection.first, selection.second));
    }

}
