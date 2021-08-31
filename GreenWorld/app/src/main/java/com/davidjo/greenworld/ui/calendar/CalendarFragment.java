package com.davidjo.greenworld.ui.calendar;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.FragmentCalendarBinding;
import com.davidjo.greenworld.databinding.FragmentTodayBinding;
import com.davidjo.greenworld.ui.today.TodayViewModel;
import com.davidjo.greenworld.util.Utils;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CalendarFragment extends AuthFragment {

    private FragmentCalendarBinding binding;
    private CalendarViewModel viewModel;


    public CalendarFragment() {
        super(R.layout.fragment_calendar);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentCalendarBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        binding.calendarView.setOnDateChangeListener((v, year, month, dayOfMonth) ->
                viewModel.onDateSelected(year, month + 1, dayOfMonth));

        binding.fabShowActions.setOnClickListener(v -> viewModel.onShowActionsClick());

        // 선택된 날짜값을 텍스트뷰에 표시

        viewModel.getDate().observe(getViewLifecycleOwner(), date ->
                binding.textViewDate.setText(Utils.formatDate(getResources(), date))
        );

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof CalendarViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
            } else if (event instanceof CalendarViewModel.Event.NavigateToTodayFragment) {
                long epochDays = ((CalendarViewModel.Event.NavigateToTodayFragment) event).epochDays;
                NavDirections action = CalendarFragmentDirections.actionCalendarFragmentToTodayFragment(epochDays);
                Navigation.findNavController(requireView()).navigate(action);
            } else if (event instanceof CalendarViewModel.Event.ShowInvalidDateMessage) {
                String message = ((CalendarViewModel.Event.ShowInvalidDateMessage) event).message;
                Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
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
