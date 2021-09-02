package com.davidjo.remedialexercise.ui.statistic;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.databinding.FragmentStatisticBinding;
import com.davidjo.remedialexercise.util.ChartFilter;
import com.davidjo.remedialexercise.util.NameUtils;
import com.davidjo.remedialexercise.util.TimeUtils;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticFragment extends Fragment {

    private Context context;
    private FragmentStatisticBinding binding;
    private StatisticViewModel viewModel;

    public StatisticFragment() {
        super(R.layout.fragment_statistic);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentStatisticBinding.bind(view);
        viewModel = new ViewModelProvider(this).get(StatisticViewModel.class);

        initializeChart();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_dropdown_item,
                Arrays.stream(ChartFilter.values()).map(NameUtils::getChartFilterName).collect(Collectors.toList())
        );
        binding.spinnerFilter.setAdapter(adapter);

        binding.spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onChartFilterSelected(ChartFilter.values()[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        viewModel.getChartFilter().observe(getViewLifecycleOwner(), filter ->
                binding.spinnerFilter.setSelection(filter.ordinal())
        );

        viewModel.getMinutesList().observe(getViewLifecycleOwner(), minutesList -> {

            ChartFilter filter = viewModel.getChartFilter().getValue();

            if (minutesList == null || filter == null) {
                return;
            }

            List<Entry> entries = new ArrayList<>();
            for (int i = 0; i < minutesList.size(); i++) {
                entries.add(new Entry((float) i, (float) minutesList.get(i).second));
            }

            String label = String.format(Locale.getDefault(),
                    getString(R.string.training_minutes_by),
                    NameUtils.getChartFilterName(filter));
            LineDataSet lineDataSet = new LineDataSet(entries, label);
            styleDataSet(lineDataSet);
            LineData lineData = new LineData(lineDataSet);

            binding.lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
                @Override
                public String getAxisLabel(float value, AxisBase axis) {
                    LocalDate localDate = minutesList.get((int) value).first;
                    switch (filter) {
                        case DAILY:
                            return TimeUtils.formatMonthDay(localDate);
                        case MONTHLY:
                            return TimeUtils.formatYearMonth(localDate);
                        case YEARLY:
                            return String.valueOf(localDate.getYear());
                    }
                    return "";
                }
            });

            binding.lineChart.setData(lineData);
            binding.lineChart.invalidate();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initializeChart() {

        binding.lineChart.setBackgroundColor(Color.WHITE);
        Description description = binding.lineChart.getDescription();
        description.setEnabled(false);

        XAxis xAxis = binding.lineChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftAxis = binding.lineChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = binding.lineChart.getAxisRight();
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










