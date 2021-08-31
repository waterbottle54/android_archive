package com.davidjo.greenworld.ui.authentication.signup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.davidjo.greenworld.util.Utils;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();         // 프래그먼트에서 처리할 이벤트

    private final MutableLiveData<String> userId = new MutableLiveData<>("");             // 유저 아이디 값
    private final MutableLiveData<String> password = new MutableLiveData<>("");           // 비밀번호 값
    private final MutableLiveData<String> passwordConfirm = new MutableLiveData<>("");    // 비밀번호 확인값

    private final FirebaseAuth auth;


    @Inject
    public SignUpViewModel(FirebaseAuth auth) {
        this.auth = auth;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<String> getUserId() {
        return userId;
    }

    public LiveData<String> getPassword() {
        return password;
    }

    public LiveData<String> getPasswordConfirm() {
        return passwordConfirm;
    }


    public void onUserIdChanged(String value) {
        // 아이디 입력값이 변경되었을 때
        userId.setValue(value);
    }

    public void onPasswordChanged(String value) {
        // 비밀번호 입력값이 변경되었을 때
        password.setValue(value);
    }

    public void onPasswordConfirmChanged(String value) {
        // 비밀번호 확인값이 변경되었을 때
        passwordConfirm.setValue(value);
    }

    public void onSignUpClicked() {

        // 회원가입 요청했을 때 : 회원가입 시도하기
        String userIdValue = userId.getValue();
        String passwordValue = password.getValue();
        String passwordConfirmValue = passwordConfirm.getValue();

        assert userIdValue != null && passwordValue != null && passwordConfirmValue != null;

        if (userIdValue.length() < 4) {
            // 에러 : 짧은 아이디
            event.setValue(new Event.ShowShortUserIdMessage("아이디를 4글자 이상 입력해주세요"));
            return;
        }
        if (passwordValue.length() < 6) {
            // 에러 : 짧은 패스워드
            event.setValue(new Event.ShowShortPasswordMessage("비밀번호를 6글자 이상 입력해주세요"));
            return;
        }
        if (!passwordConfirmValue.equals(passwordValue)) {
            // 에러 : 비밀번호 확인 불일치
            event.setValue(new Event.ShowIncorrectPasswordConfirmMessage("비밀번호를 정확하게 입력해주세요"));
            return;
        }

        String email = Utils.getEmailFromUserId(userIdValue);
        auth.createUserWithEmailAndPassword(email, passwordValue)
                .addOnSuccessListener(authResult -> auth.signInWithEmailAndPassword(email, passwordValue))
                .addOnFailureListener(e -> event.postValue(
                        new Event.ShowSignUpFailureMessage("이미 존재하는 아이디입니다"))
                );
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            event.setValue(new Event.NavigateToHomeScreen());
        }
    }

    // 프래그먼트에 전송될 이벤트

    public static class Event {

        public static class ShowShortUserIdMessage extends Event {
            public final String message;
            public ShowShortUserIdMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowShortPasswordMessage extends Event {
            public final String message;
            public ShowShortPasswordMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowIncorrectPasswordConfirmMessage extends Event {
            public final String message;
            public ShowIncorrectPasswordConfirmMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowSignUpFailureMessage extends Event {
            public final String message;
            public ShowSignUpFailureMessage(String message) {
                this.message = message;
            }
        }
        public static class NavigateToHomeScreen extends Event {}
    }

}
