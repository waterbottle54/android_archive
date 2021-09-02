package com.davidjo.remedialexercise.ui.statistic;

import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.training.Training;
import com.davidjo.remedialexercise.data.training.TrainingDao;
import com.davidjo.remedialexercise.util.ChartFilter;
import com.davidjo.remedialexercise.util.TimeUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StatisticViewModel extends ViewModel {

    private final LiveData<List<Training>> trainings;
    private final MutableLiveData<List<Pair<LocalDate, Integer>>> minutesList = new MutableLiveData<>();

    private final MutableLiveData<ChartFilter> filter = new MutableLiveData<>(ChartFilter.DAILY);

    private final Observer<List<Training>> trainingsObserver;
    private final Observer<ChartFilter> filterObserver;


    @Inject
    public StatisticViewModel(TrainingDao trainingDao) {

        trainings = trainingDao.getTrainings();

        trainingsObserver = trainings -> {
            ChartFilter filter = this.filter.getValue();
            if (trainings != null && filter != null) {
                minutesList.setValue(getTrainingMinutes(trainings, filter));
            }
        };
        trainings.observeForever(trainingsObserver);

        filterObserver = filter -> {
            List<Training> trainings = this.trainings.getValue();
            if (filter != null && trainings != null) {
                minutesList.setValue(getTrainingMinutes(trainings, filter));
            }
        };
        filter.observeForever(filterObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        trainings.removeObserver(trainingsObserver);
        filter.observeForever(filterObserver);
    }

    public LiveData<List<Pair<LocalDate, Integer>>> getMinutesList() {
        return minutesList;
    }

    public LiveData<ChartFilter> getChartFilter() {
        return filter;
    }


    public void onChartFilterSelected(ChartFilter chartFilter) {
        filter.setValue(chartFilter);
    }

    private List<Pair<LocalDate, Integer>> getTrainingMinutes(List<Training> trainings, ChartFilter filter) {

        List<Pair<LocalDate, Integer>> minutesList = new ArrayList<>();
        LocalDate today = LocalDate.now();

        switch (filter) {
            case DAILY:
                for (int days = 6; days >= 0; days--) {
                    LocalDate date = today.minusDays(days);
                    int minutes = 0;
                    for (Training training : trainings) {
                        long created = training.getCreatedTime();
                        if (TimeUtils.getLocalDate(created).equals(date)) {
                            minutes += training.getMinutes();
                        }
                    }
                    minutesList.add(new Pair<>(date, minutes));
                }
                break;
            case MONTHLY:
                for (int months = 6; months >= 0; months--) {
                    LocalDate date = today.minusMonths(months);
                    int minutes = 0;
                    for (Training training : trainings) {
                        LocalDate created = TimeUtils.getLocalDate(training.getCreatedTime());
                        if (TimeUtils.isYearMonthTheSame(date, created)) {
                            minutes += training.getMinutes();
                        }
                    }
                    minutesList.add(new Pair<>(date, minutes));
                }
                break;
            case YEARLY:
                for (int years = 6; years >= 0; years--) {
                    LocalDate date = today.minusYears(years);
                    int minutes = 0;
                    for (Training training : trainings) {
                        LocalDate created = TimeUtils.getLocalDate(training.getCreatedTime());
                        if (date.getYear() == created.getYear()) {
                            minutes += training.getMinutes();
                        }
                    }
                    minutesList.add(new Pair<>(date, minutes));
                }
                break;
        }

        return minutesList;
    }


}
