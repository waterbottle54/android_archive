package com.davidjo.remedialexercise.ui.diagnosis.failure;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.databinding.FragmentDiagnosisFailureBinding;

public class DiagnosisFailureFragment extends Fragment {

    public DiagnosisFailureFragment() {
        super(R.layout.fragment_diagnosis_failure);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentDiagnosisFailureBinding binding = FragmentDiagnosisFailureBinding.bind(view);

        if (getArguments() != null) {
            String message = DiagnosisFailureFragmentArgs.fromBundle(getArguments()).getMessage();
            binding.textViewDiagnosisFailureReason.setText(message);
        }

        binding.fabShowHospitals.setOnClickListener(v -> {
            NavDirections action = DiagnosisFailureFragmentDirections
                    .actionDiagnosisFailureFragmentToHospitalsFragment();
            Navigation.findNavController(view).navigate(action);
        });
    }

}
