package com.davidjo.greenworld.data.action;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.davidjo.greenworld.data.detailedaction.DetailedAction;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ActionRepository {

    private final CollectionReference actionsCollection;

    @Inject
    public ActionRepository(FirebaseFirestore firestore) {
        this.actionsCollection = firestore.collection("actions");
    }

    public LiveData<List<Action>> getActions(String userId, long epochDays) {

        // 주어진 날짜의 액션을 DB 에서 가져옴
        long beginMillis = epochDays * 86400000;
        long endMillis = (epochDays + 1) * 86400000;

        return getActions(userId, beginMillis, endMillis);
    }

    public LiveData<List<Action>> getMonthlyActions(String userId, int year, int month) {

        // 주어진 달의 액션을 DB 에서 가져옴
        LocalDate beginDay = LocalDate.of(year, month, 1);
        LocalDate endDay = LocalDate.of(year, month + 1, 1);
        long beginMillis = beginDay.toEpochDay() * 86400000;
        long endMillis = endDay.toEpochDay() * 86400000;

        return getActions(userId, beginMillis, endMillis);
    }

    public LiveData<List<Action>> getWeeklyActions(String userId, LocalDate date) {

        // 주어진 날짜가 포함되는 한 주의 액션을 DB 에서 가져옴
        LocalDate today = LocalDate.now();
        int dayOfWeek = today.getDayOfWeek().getValue();
        LocalDate beginDay = today.minusDays(dayOfWeek - 1);
        LocalDate endDay = beginDay.plusWeeks(1);
        long beginMillis = beginDay.toEpochDay() * 86400000;
        long endMillis = endDay.toEpochDay() * 86400000;

        return getActions(userId, beginMillis, endMillis);
    }

    private LiveData<List<Action>> getActions(String userId, long beginMillis, long endMillis) {

        // 주어진 기간의 액션을 DB 에서 가져옴
        MutableLiveData<List<Action>> actions = new MutableLiveData<>();

        actionsCollection
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("created", beginMillis)
                .whereLessThan("created", endMillis)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Action> actionList = new ArrayList<>();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        Action action = snapshot.toObject(Action.class);
                        actionList.add(action);
                    }
                    actions.setValue(actionList);
                });

        return actions;
    }

}




