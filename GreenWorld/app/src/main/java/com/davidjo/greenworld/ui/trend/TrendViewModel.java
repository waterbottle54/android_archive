package com.davidjo.greenworld.ui.trend;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.data.action.Action;
import com.davidjo.greenworld.data.action.ActionRepository;
import com.davidjo.greenworld.data.detailedaction.DetailedAction;
import com.davidjo.greenworld.data.detailedaction.DetailedActionRepository;
import com.davidjo.greenworld.util.Utils;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TrendViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<String> userId = new MutableLiveData<>();

    private final LiveData<List<Integer>> weeklyScores;


    @Inject
    public TrendViewModel(ActionRepository actionRepository, DetailedActionRepository detailedActionRepository) {

        // Repository 에서 DB 의 자료 검색

        LiveData<List<Action>> weeklyActions = Transformations.switchMap(userId, userIdString ->
                actionRepository.getWeeklyActions(userIdString, LocalDate.now())
        );

        LiveData<List<DetailedAction>> detailedActions = Transformations.switchMap(weeklyActions,
                detailedActionRepository::getDetailedActions);

        weeklyScores = Transformations.map(detailedActions, detailedActionList -> {
            List<Integer> weeklyScoreList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                weeklyScoreList.add(0);
            }
            for (DetailedAction detailedAction : detailedActionList) {
                long created = detailedAction.getCreated();
                int dayOfWeekFromZero = Utils.getLocalDate(created).getDayOfWeek().getValue() - 1;
                int score = detailedAction.getScore() * detailedAction.getRepetitions();
                weeklyScoreList.set(dayOfWeekFromZero, weeklyScoreList.get(dayOfWeekFromZero) + score);
            }
            return weeklyScoreList;
        });
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Integer>> getWeeklyScores() {
        return weeklyScores;
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
