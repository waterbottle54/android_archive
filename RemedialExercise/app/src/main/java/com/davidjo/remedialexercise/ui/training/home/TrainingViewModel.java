package com.davidjo.remedialexercise.ui.training.home;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.exercise.Exercise;
import com.davidjo.remedialexercise.data.exercise.ExerciseDao;
import com.davidjo.remedialexercise.data.plan.Plan;
import com.davidjo.remedialexercise.data.plan.PlanDao;
import com.davidjo.remedialexercise.data.training.Training;
import com.davidjo.remedialexercise.data.training.TrainingDao;
import com.davidjo.remedialexercise.util.TimeUtils;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrainingViewModel extends ViewModel {

    private final PlanDao planDao;
    private final LiveData<Plan> plan;
    private final MutableLiveData<Integer> daysLeft = new MutableLiveData<>();
    private final MutableLiveData<Integer> dayPlus = new MutableLiveData<>();
    private final MutableLiveData<Integer> totalRepetition = new MutableLiveData<>();
    private final MutableLiveData<Integer> minutesPerRepetition = new MutableLiveData<>();

    private final TrainingDao trainingDao;
    private LiveData<List<Training>> trainings = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentRepetition = new MutableLiveData<>();
    private final MutableLiveData<Boolean> doneRepetition = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isStartable = new MutableLiveData<>(false);

    private final ExerciseDao exerciseDao;
    private List<Exercise> exercises;

    private final Observer<Plan> planObserver;
    private final Observer<List<Training>> trainingsObserver;
    private Observer<Boolean> doneRepetitionObserver;
    private Observer<Integer> daysPlusObserver;

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    @Inject
    public TrainingViewModel(PlanDao planDao, TrainingDao trainingDao, ExerciseDao exerciseDao) {

        this.planDao = planDao;
        this.trainingDao = trainingDao;
        this.exerciseDao = exerciseDao;

        plan = planDao.getOngoingPlan();

        trainingsObserver = todayTrainings -> {
            if (todayTrainings != null) {
                currentRepetition.setValue(todayTrainings.size());
                Plan planValue = plan.getValue();
                if (planValue != null) {
                    doneRepetition.setValue(todayTrainings.size() >= planValue.getRepetitions());
                }
            } else {
                currentRepetition.setValue(0);
            }
        };

        planObserver = plan -> {
            if (plan != null) {
                int dayPlusValue = TimeUtils.getDifferenceInDays(plan.getStartTime(), System.currentTimeMillis());
                int daysLeftValue = TimeUtils.getDifferenceInDays(System.currentTimeMillis(), plan.getEndTime());
                dayPlus.setValue(dayPlusValue);
                daysLeft.setValue(daysLeftValue);
                totalRepetition.setValue(plan.getRepetitions());
                minutesPerRepetition.setValue(plan.getMinutesPerRepetition());

                long epochDay = TimeUtils.getLocalDate(System.currentTimeMillis()).toEpochDay();
                long startTime = epochDay * 86400000;
                long endTime = (epochDay + 1) * 86400000;
                trainings = trainingDao.getTrainings(plan.getId(), startTime, endTime);

                trainings.removeObserver(trainingsObserver);
                trainings.observeForever(trainingsObserver);

                exerciseDao.getExercises(plan.getBodyPart()).observeForever(exercises -> this.exercises = exercises);

            } else {
                dayPlus.setValue(null);
                daysLeft.setValue(null);
                totalRepetition.setValue(null);
                minutesPerRepetition.setValue(null);
                event.setValue(new Event.ShowNoPlanMessage("작성된 계획이 없습니다"));
            }

        };
        if (plan != null) {
            plan.observeForever(planObserver);
        }

        doneRepetitionObserver = done -> {
            if (done != null) {
                Integer days = dayPlus.getValue();
                isStartable.setValue(!done && days != null && days >= 0);
            }
        };

        daysPlusObserver = days -> {
            if (days != null) {
                Boolean done = doneRepetition.getValue();
                isStartable.setValue(days >= 0 && done != null && !done);
            }
        };

        doneRepetition.observeForever(doneRepetitionObserver);
        dayPlus.observeForever(daysPlusObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        plan.removeObserver(planObserver);
        trainings.removeObserver(trainingsObserver);
        doneRepetition.removeObserver(doneRepetitionObserver);
        dayPlus.removeObserver(daysPlusObserver);
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Plan> getPlan() {
        return plan;
    }

    public LiveData<Integer> getDayPlus() {
        return dayPlus;
    }

    public LiveData<Integer> getDaysLeft() {
        return daysLeft;
    }

    public LiveData<Integer> getTotalRepetition() {
        return totalRepetition;
    }

    public LiveData<Integer> getCurrentRepetition() {
        return currentRepetition;
    }

    public LiveData<Integer> getMinutesPerRepetition() {
        return minutesPerRepetition;
    }

    public LiveData<Boolean> doneRepetition() {
        return doneRepetition;
    }

    public LiveData<Boolean> isStartable() {
        return isStartable;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }


    public void onDiscardPlanClick() {
        event.setValue(new Event.ShowDiscardPlanConfirmMessage("정말로 포기하시겠습니까?"));
    }

    public void onDiscardPlanConfirm() {
        new Thread(() -> planDao.delete(plan.getValue())).start();
    }

    public void onCompletePlanClick() {
        if (daysLeft.getValue() != null && daysLeft.getValue() == 0) {
            event.setValue(new Event.NavigateToSurveyScreen());
        }
    }

    public void onStartRepetitionClick() {
        if (doneRepetition.getValue() != null && doneRepetition.getValue()) {
            return;
        }
        if (dayPlus.getValue() != null && dayPlus.getValue() < 0) {
            return;
        }
        event.setValue(new Event.StartTrainingService());
    }


    public static class Event {
        public static class ShowNoPlanMessage extends Event {
            public final String message;

            public ShowNoPlanMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowDiscardPlanConfirmMessage extends Event {
            public final String message;

            public ShowDiscardPlanConfirmMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToSurveyScreen extends Event {
        }

        public static class StartTrainingService extends Event {
        }
    }

}
