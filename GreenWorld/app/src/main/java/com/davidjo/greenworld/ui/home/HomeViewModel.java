package com.davidjo.greenworld.ui.home;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();

    private final FirebaseAuth auth;


    @Inject
    public HomeViewModel(FirebaseAuth auth) {
        this.auth = auth;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    // UI 이벤트 처리

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            event.setValue(new Event.NavigateBack());
        }
    }

    public void onBackClick() {
        event.setValue(new Event.ConfirmSignOut("로그아웃 하시겠습니까?"));
    }

    public void onSignOutConfirmed() {
        auth.signOut();
    }

    public void onAddClick() {
        event.setValue(new Event.NavigateToCategoryScreen());
    }

    public void onTodayClick() {
        event.setValue(new Event.NavigateToTodayScreen());
    }

    public void onCalendarClick() {
        event.setValue(new Event.NavigateToCalendarScreen());
    }

    public void onStatisticClick() {
        event.setValue(new Event.NavigateToStatisticScreen());
    }

    public void onTrendClick() {
        event.setValue(new Event.NavigateToTrendScreen());
    }

    public void onAddResult(boolean success) {
        if (success) {
            event.setValue(new Event.ShowAddResultMessage("기록이 완료되었습니다"));
        } else {
            event.setValue(new Event.ShowAddResultMessage("기록에 실패했습니다"));
        }
    }

    // 프래그먼트에 전송할 이벤트

    public static class Event {

        public static class ConfirmSignOut extends Event {
            public final String message;
            public ConfirmSignOut(String message) {
                this.message = message;
            }
        }

        public static class NavigateBack extends Event { }
        public static class NavigateToCategoryScreen extends Event { }
        public static class NavigateToTodayScreen extends Event { }
        public static class NavigateToCalendarScreen extends Event { }
        public static class NavigateToStatisticScreen extends Event { }
        public static class NavigateToTrendScreen extends Event { }

        public static class ShowAddResultMessage extends Event {
            public final String message;
            public ShowAddResultMessage(String message) {
                this.message = message;
            }
        }
    }

}
