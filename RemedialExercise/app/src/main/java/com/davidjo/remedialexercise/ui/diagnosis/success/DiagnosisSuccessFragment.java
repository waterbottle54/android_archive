package com.davidjo.remedialexercise.ui.diagnosis.success;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.databinding.FragmentDiagnosisSuccessBinding;
import com.davidjo.remedialexercise.util.NameUtils;

import java.util.Locale;

public class DiagnosisSuccessFragment extends Fragment {



    public DiagnosisSuccessFragment() {
        super(R.layout.fragment_diagnosis_success);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentDiagnosisSuccessBinding binding = FragmentDiagnosisSuccessBinding.bind(view);

        if (getArguments() != null) {
            BodyPart bodyPart = DiagnosisSuccessFragmentArgs.fromBundle(getArguments()).getBodyPart();
            String str = String.format(Locale.getDefault(),
                    getString(R.string.would_you_start_rehab),
                    NameUtils.getBodyPartName(bodyPart)
            );
            binding.textViewWouldYouStartRehab.setText(str);

            binding.fabStartRehab.setOnClickListener(v -> {
                NavDirections action = DiagnosisSuccessFragmentDirections.actionDiagnosisSuccessFragmentToInitiateFragment(bodyPart);
                Navigation.findNavController(view).navigate(action);
            });
        }
    }

}


