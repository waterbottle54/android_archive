package com.davidjo.remedialexercise.ui.initiate.plan;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.data.plan.Plan;
import com.davidjo.remedialexercise.data.plan.PlanDao;
import com.davidjo.remedialexercise.util.TimeUtils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PlanViewModel extends ViewModel {

    private final PlanDao planDao;
    private final MutableLiveData<Plan> plan;
    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final LiveData<Plan> ongoingPlan;
    private final Observer<Plan> existingPlanObserver;
    private boolean isPlanSaved;

    @Inject
    public PlanViewModel(SavedStateHandle savedStateHandle, PlanDao planDao) {
        BodyPart bodyPart = savedStateHandle.get("body_part");
        this.planDao = planDao;
        this.plan = new MutableLiveData<>(new Plan(
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                3,
                10,
                bodyPart,
                false
        ));

        ongoingPlan = planDao.getOngoingPlan();
        existingPlanObserver = existingPlan -> {
            if (existingPlan != null && !isPlanSaved) {
                plan.setValue(existingPlan);
                event.setValue(new Event.ShowExistingPlanMessageAndDisableSavePlan("이미 작성된 계획이 존재합니다"));
            }
        };
        ongoingPlan.observeForever(existingPlanObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        ongoingPlan.removeObserver(existingPlanObserver);
    }

    public LiveData<Plan> getPlan() {
        return plan;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public void onSavePlanClicked() {
        if (plan.getValue() == null || isPlanSaved) {
            return;
        }
        int days = TimeUtils.getDifferenceInDays(System.currentTimeMillis(), plan.getValue().getStartTime());
        if (days < 0) {
            event.setValue(new Event.ShowWrongDateMessage("날짜가 올바르지 않습니다"));
            return;
        }
        new Thread(() -> {
            isPlanSaved = true;
            if (ongoingPlan.getValue() == null) {
                planDao.insert(plan.getValue());
            } else {
                planDao.update(plan.getValue());
            }
            event.postValue(new Event.NavigateBackWithResult());
        }).start();
    }

    public void onTimeSelected(long startTime, long endTime) {
        Plan oldPlan = plan.getValue();
        if (oldPlan == null) {
            return;
        }
        oldPlan.setStartTime(startTime);
        oldPlan.setEndTime(endTime);
        plan.setValue(oldPlan);
    }

    public void onRuleSelected(int repetitions, int minutesPerRepetition) {
        Plan oldPlan = plan.getValue();
        if (oldPlan == null) {
            return;
        }
        oldPlan.setRepetitions(repetitions);
        oldPlan.setMinutesPerRepetition(minutesPerRepetition);
        plan.setValue(oldPlan);
    }

    public void onModifyPlanClicked() {
        event.setValue(new Event.EnableSavePlan());
    }

    public static class Event {
        public static class ShowWrongDateMessage extends Event {
            public final String message;
            public ShowWrongDateMessage(String message) {
                this.message = message;
            }
        }
        public static class NavigateBackWithResult extends Event { }
        public static class ShowExistingPlanMessageAndDisableSavePlan extends Event {
            public final String message;
            public ShowExistingPlanMessageAndDisableSavePlan(String message) {
                this.message = message;
            }
        }
        public static class EnableSavePlan extends Event { }
    }

}
