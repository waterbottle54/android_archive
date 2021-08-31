package com.davidjo.greenworld.ui.trend;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.davidjo.greenworld.R;
import com.davidjo.greenworld.databinding.FragmentTrendBinding;
import com.davidjo.greenworld.util.Utils;
import com.davidjo.greenworld.util.ui.AuthFragment;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TrendFragment extends AuthFragment {

    private FragmentTrendBinding binding;
    private TrendViewModel viewModel;


    public TrendFragment() {
        super(R.layout.fragment_trend);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentTrendBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(TrendViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);

        // LineChart 초기화

        initializeChart();

        // 주간 점수를 LineChart 에 표시

        viewModel.getWeeklyScores().observe(getViewLifecycleOwner(), weeklyScores -> {
            if (weeklyScores != null) {
                List<Entry> entries = new ArrayList<>();
                for (int i = 0; i < weeklyScores.size(); i++) {
                    entries.add(new Entry((float) i, weeklyScores.get(i)));
                }

                LineDataSet dataSet = new LineDataSet(entries, getString(R.string.daily_score));
                styleDataSet(dataSet);  // 데이터셋에 스타일 부여

                binding.trendChart.getXAxis().setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getAxisLabel(float value, AxisBase axis) {
                        return Utils.getDayOfWeekString(getResources(), (int) value);
                    }
                });

                binding.trendChart.setData(new LineData(dataSet));
                binding.trendChart.invalidate();

                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        // 뷰모델에서 전송한 이벤트 처리

        viewModel.getEvent().observe(getViewLifecycleOwner(), event -> {
            if (event instanceof TrendViewModel.Event.NavigateBack) {
                Navigation.findNavController(requireView()).popBackStack();
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

    private void initializeChart() {

        binding.trendChart.setBackgroundColor(Color.WHITE);
        Description description = binding.trendChart.getDescription();
        description.setEnabled(false);

        XAxis xAxis = binding.trendChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = binding.trendChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = binding.trendChart.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void styleDataSet(LineDataSet lineDataSet) {

        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(6f);

        int chartColor = getResources().getColor(R.color.colorChart, null);
        lineDataSet.setColor(chartColor);
        lineDataSet.setCircleColor(chartColor);

        lineDataSet.setValueTextSize(12f);
    }

}
