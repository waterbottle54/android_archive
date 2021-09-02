package com.davidjo.remedialexercise.ui.initiate.home;

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
import com.davidjo.remedialexercise.databinding.FragmentInitiateBinding;
import com.google.android.material.snackbar.Snackbar;

public class InitiateFragment extends Fragment {

    public InitiateFragment() {
        super(R.layout.fragment_initiate);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentInitiateBinding binding = FragmentInitiateBinding.bind(view);

        BodyPart bodyPart = getArguments() == null ? BodyPart.NECK
                : InitiateFragmentArgs.fromBundle(getArguments()).getBodyPart();

        binding.cardViewLearnRehab.setOnClickListener(v -> {
            NavDirections action = InitiateFragmentDirections.actionInitiateFragmentToVideoFragment(bodyPart);
            Navigation.findNavController(view).navigate(action);
        });

        binding.cardViewLearnGoods.setOnClickListener(v -> {
            NavDirections action = InitiateFragmentDirections.actionInitiateFragmentToGoodsFragment(bodyPart);
            Navigation.findNavController(view).navigate(action);
        });

        binding.cardViewMakePlan.setOnClickListener(v -> {
            NavDirections action = InitiateFragmentDirections.actionInitiateFragmentToPlanFragment(bodyPart);
            Navigation.findNavController(view).navigate(action);
        });

        getParentFragmentManager().setFragmentResultListener(
                "plan_request",
                getViewLifecycleOwner(),
                (requestKey, result) -> {
                    boolean success = result.getBoolean("plan_result");
                    if (success) {
                        Snackbar.make(requireView(), "재활운동 계획이 작성되었습니다", Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

}
