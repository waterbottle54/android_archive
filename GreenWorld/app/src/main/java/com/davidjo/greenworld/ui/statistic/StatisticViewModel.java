package com.davidjo.greenworld.ui.statistic;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.data.action.Action;
import com.davidjo.greenworld.data.action.ActionRepository;
import com.davidjo.greenworld.data.detailedaction.DetailedAction;
import com.davidjo.greenworld.data.detailedaction.DetailedActionRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StatisticViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final LiveData<Integer> monthlyScore;


    @Inject
    public StatisticViewModel(ActionRepository actionRepository, DetailedActionRepository detailedActionRepository) {

        // Repository 에서 DB 의 자료 검색

        LocalDate today = LocalDate.now();

        LiveData<List<Action>> monthlyActions = Transformations.switchMap(userId, userIdString ->
                actionRepository.getMonthlyActions(userIdString, today.getYear(), today.getMonthValue())
        );

        LiveData<List<DetailedAction>> detailedActions = Transformations.switchMap(monthlyActions,
                detailedActionRepository::getDetailedActions);

        monthlyScore = Transformations.map(detailedActions, detailedActionList -> {
            int sum = 0;
            for (DetailedAction detailedAction : detailedActionList) {
                sum += detailedAction.getRepetitions() * detailedAction.getScore();
            }
            return sum;
        });
    }

    public LiveData<Event> getEvent() {
        return event;
    }

    public LiveData<Integer> getMonthlyScore() {
        return monthlyScore;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        } else {
            userId.setValue(firebaseAuth.getCurrentUser().getUid());
        }
    }

    // 프래그먼트에 전송할 이벤트

    public static class Event {

        public static class NavigateBack extends Event { }

    }

}
