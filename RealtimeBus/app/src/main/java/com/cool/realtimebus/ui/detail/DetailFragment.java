package com.cool.realtimebus.ui.detail;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.cool.realtimebus.databinding.FragmentDetailBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class DetailFragment extends BottomSheetDialogFragment {

    private FragmentDetailBinding binding;
    private DetailViewModel viewModel;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentDetailBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(DetailViewModel.class);

        // UI를 초기화한다
        binding.imageViewClose.setOnClickListener(v -> viewModel.onCloseClick());
        binding.textViewStationTitle.setText(viewModel.getStationTitle());

        // 도착정보 리사이클러뷰를 초기화한다
        ArrivalsAdapter adapter = new ArrivalsAdapter();
        binding.recyclerViewArrival.setAdapter(adapter);
        binding.recyclerViewArrival.setHasFixedSize(true);

        // 도착정보가 업데이트될 때 리사이클러뷰를 업데이트한다
        viewModel.getArrivals().observe(getViewLifecycleOwner(), arrivals -> {
            adapter.submitList(arrivals);
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.textViewNoArrival.setVisibility(arrivals.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        });

        // 뷰모델에서 전송한 이벤트 처리
        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof DetailViewModel.Event.NavigateBack) {
                NavHostFragment.findNavController(this).popBackStack();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}