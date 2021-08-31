package com.davidjo.greenworld.ui.today;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.data.action.Action;
import com.davidjo.greenworld.data.action.ActionRepository;
import com.davidjo.greenworld.data.detailedaction.DetailedAction;
import com.davidjo.greenworld.data.detailedaction.DetailedActionRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TodayViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final long epochDays;

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final LiveData<List<DetailedAction>> detailedActions;

    private final LiveData<Integer> score;


    @Inject
    public TodayViewModel(SavedStateHandle savedStateHandle, ActionRepository actionRepository, DetailedActionRepository detailedActionRepository) {

        // 프래그먼트에 전달된 인수(argument) 획득

        Long epochDaysObj = savedStateHandle.get("epoch_days");
        if (epochDaysObj == null || epochDaysObj == -1) {
            epochDays = (System.currentTimeMillis() / 86400000);
        } else {
            epochDays = epochDaysObj;
        }

        // Repository 에서 DB 의 자료 검색

        LiveData<List<Action>> actions = Transformations.switchMap(userId, userIdString ->
                actionRepository.getActions(userIdString, epochDays));

        detailedActions = Transformations.switchMap(actions, detailedActionRepository::getDetailedActions);

        score = Transformations.map(detailedActions, detailedActionList -> {
           int sum = 0;
           for (DetailedAction detailedAction : detailedActionList) {
               sum += detailedAction.getRepetitions() * detailedAction.getScore();
           }
           return sum;
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<DetailedAction>> getDetailedActions() {
        return detailedActions;
    }

    public LiveData<Integer> getScore() {
        return score;
    }

    public long getEpochDays() {
        return epochDays;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            userId.setValue(firebaseAuth.getCurrentUser().getUid());
        }
    }

    // 프래그먼트에 전송될 이벤트

    public static class Event {
        public static class NavigateBack extends Event {
        }
    }


}
