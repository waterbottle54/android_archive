package com.davidjo.remedialexercise.ui.training.survey;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.plan.Plan;
import com.davidjo.remedialexercise.data.plan.PlanDao;
import com.davidjo.remedialexercise.util.TimeUtils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SurveyViewModel extends ViewModel {

    private final MutableLiveData<Event> event = new MutableLiveData<>(null);

    private final MutableLiveData<Integer> painLevel = new MutableLiveData<>(1);
    private final MutableLiveData<Boolean> earnest = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> overdo = new MutableLiveData<>(false);

    private final PlanDao planDao;
    private final LiveData<Plan> plan;
    private final Observer<Plan> planObserver;
    private final MutableLiveData<Integer> days = new MutableLiveData<>();


    @Inject
    public SurveyViewModel(PlanDao planDao) {
        this.planDao = planDao;
        this.plan = planDao.getOngoingPlan();

        planObserver = planValue -> {
            if (planValue != null) {
                days.setValue(TimeUtils.getDifferenceInDays(planValue.getStartTime(), planValue.getEndTime()) + 1);
            } else {
                days.setValue(null);
            }
        };
        plan.observeForever(planObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        plan.removeObserver(planObserver);
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<Integer> getPainLevel() {
        return painLevel;
    }

    public LiveData<Boolean> getEarnest() {
        return earnest;
    }

    public LiveData<Boolean> getOverdo() {
        return overdo;
    }

    public LiveData<Plan> getPlan() {
        return plan;
    }

    public LiveData<Integer> getDays() {
        return days;
    }


    public void onPainLevelSelected(int level) {
        painLevel.setValue(level);
    }

    public void onEarnestChecked(boolean checked) {
        earnest.setValue(checked);
    }

    public void onOverdoChecked(boolean checked) {
        overdo.setValue(checked);
    }

    public void onClosePlanClicked() {
        Plan planValue = plan.getValue();
        if (planValue != null) {
            new Thread(() -> planDao.close(planValue.getId())).start();
            event.postValue(new Event.NavigateBack());
        }
    }

    public void onExtendPlanClicked() {
        Plan planValue = plan.getValue();
        if (planValue != null) {
            event.setValue(new Event.NavigateToPlanScreen(planValue));
        }
    }


    public static class Event {
        public static class NavigateBack extends Event { }
        public static class NavigateToPlanScreen extends Event {
            public final Plan plan;
            public NavigateToPlanScreen(Plan plan) {
                this.plan = plan;
            }
        }
    }

}





