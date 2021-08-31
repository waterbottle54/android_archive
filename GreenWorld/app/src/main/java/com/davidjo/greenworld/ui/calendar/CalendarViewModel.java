package com.davidjo.greenworld.ui.calendar;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CalendarViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final MutableLiveData<LocalDate> date = new MutableLiveData<>(LocalDate.now());


    @Inject
    public CalendarViewModel() {
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<LocalDate> getDate() {
        return date;
    }


    // UI 이벤트 처리

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        }
    }

    public void onDateSelected(int year, int day, int monthOfDay) {
        date.setValue(LocalDate.of(year, day, monthOfDay));
    }

    public void onShowActionsClick() {

        LocalDate dateValue = date.getValue();

        assert dateValue != null;
        if (dateValue.toEpochDay() > LocalDate.now().toEpochDay()) {
            event.setValue(new Event.ShowInvalidDateMessage("잘못된 날짜를 선택하셨습니다"));
            return;
        }

        event.setValue(new Event.NavigateToTodayFragment(dateValue.toEpochDay()));
    }

    // 프래그먼트에 전송할 이벤트

    public static class Event {

        public static class NavigateBack extends Event { }

        public static class NavigateToTodayFragment extends Event {
            public final long epochDays;
            public NavigateToTodayFragment(long epochDays) {
                this.epochDays = epochDays;
            }
        }

        public static class ShowInvalidDateMessage extends Event {
            public final String message;
            public ShowInvalidDateMessage(String message) {
                this.message = message;
            }
        }
    }

}
