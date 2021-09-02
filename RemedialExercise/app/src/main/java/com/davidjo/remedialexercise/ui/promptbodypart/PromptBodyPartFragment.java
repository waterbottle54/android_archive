package com.davidjo.remedialexercise.ui.promptbodypart;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.databinding.FragmentPromptBodyPartBinding;
import com.davidjo.remedialexercise.ui.home.HomeFragment;
import com.davidjo.remedialexercise.util.NameUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PromptBodyPartFragment extends BottomSheetDialogFragment {

    private Context context;
    private FragmentPromptBodyPartBinding binding;
    private PromptBodyPartViewModel viewModel;

    private String destination;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_prompt_body_part, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentPromptBodyPartBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(PromptBodyPartViewModel.class);

        if (getArguments() != null) {
            destination = PromptBodyPartFragmentArgs.fromBundle(getArguments()).getDestination();
        }

        buildBodyPartSpinner();

        binding.fabConfirmBodyPart.setOnClickListener(v -> viewModel.onConfirmClicked());

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof PromptBodyPartViewModel.Event.NavigateToDestination) {
                navigateToDestination();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

        viewModel.getBodyPart().observe(getViewLifecycleOwner(), bodyPart -> {
            Integer selection = bodyPartMap.get(bodyPart);
            if (selection != null) {
                binding.spinnerBodyPart.setSelection(selection);
            }
        });
    }

    private void navigateToDestination() {

        NavDirections action;
        BodyPart bodyPart = viewModel.getBodyPart().getValue();

        switch (destination) {
            case HomeFragment.DESTINATION_INITIATE_FRAGMENT:
                action = PromptBodyPartFragmentDirections
                        .actionPromptBodyPartFragmentToInitiateFragment(bodyPart);
                break;
            case HomeFragment.DESTINATION_LEARN_FRAGMENT:
                action = PromptBodyPartFragmentDirections
                        .actionPromptBodyPartFragmentToVideoFragment(bodyPart);
                break;
            default:
                return;
        }

        NavHostFragment.findNavController(this).navigate(action);
    }

}
