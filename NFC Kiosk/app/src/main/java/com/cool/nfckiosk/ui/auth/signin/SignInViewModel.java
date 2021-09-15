package com.cool.nfckiosk.ui.auth.signin;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.store.StoreRepository;
import com.cool.nfckiosk.util.Utils;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignInViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();         // 프래그먼트에서 처리할 이벤트

    private final MutableLiveData<String> userId = new MutableLiveData<>("");       // 유저 아이디 값
    private final MutableLiveData<String> password = new MutableLiveData<>("");     // 비밀번호 값

    private final FirebaseAuth auth;
    private final StoreRepository storeRepository;


    @Inject
    public SignInViewModel(FirebaseAuth auth, StoreRepository storeRepository) {
        this.auth = auth;
        this.storeRepository = storeRepository;
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            event.setValue(new Event.NavigateToAdminScreen());
        }
    }

    public void onUserIdChanged(String value) {
        // 아이디 입력값이 변경되었을 때
        userId.setValue(value);
    }

    public void onPasswordChanged(String value) {
        // 비밀번호 입력값이 변경되었을 때
        password.setValue(value);
    }

    public void onSignInClicked() {

        // 로그인을 요청했을 때 : 로그인 시도하기
        String userIdValue = userId.getValue();
        String passwordValue = password.getValue();

        assert userIdValue != null && passwordValue != null;

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

        auth.signInWithEmailAndPassword(Utils.getEmailFromUserId(userIdValue), passwordValue)
                .addOnFailureListener(
                        e -> event.setValue(new Event.ShowSignInFailureMessage("회원정보를 확인해주세요"))
                );
    }

    public void onSignUpClicked() {
        event.setValue(new Event.NavigateToSignUpScreen());
    }

    public void onOrderClicked() {
        event.setValue(new Event.ShowNfcReadScreen());
    }

    public void onNfcReadResult(String text) {

        if (text == null || text.isEmpty()) {
            event.setValue(new Event.ShowInvalidNfcMessage("태그 인식에 실패했습니다"));
            return;
        }

        String[] split = text.split("#");
        if (split.length != 2) {
            event.setValue(new Event.ShowInvalidNfcMessage("인식할 수 없는 태그입니다"));
            return;
        }

        String nickname = split[0];
        int tableNumber;
        try {
            tableNumber = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            event.setValue(new Event.ShowInvalidNfcMessage("인식할 수 없는 태그입니다"));
            return;
        }

        storeRepository.getStoreByNickname(nickname, store -> {
            if (store == null) {
                event.setValue(new Event.ShowInvalidNfcMessage("가입된 점포가 아닙니다"));
                return;
            }
            if (tableNumber > store.getTables().size()) {
                event.setValue(new Event.ShowInvalidNfcMessage("사용중인 좌석이 아닙니다"));
                return;
            }
            event.setValue(new Event.NavigateToOrderScreen(store.getAdminId(), tableNumber));
        });
    }


    // 프래그먼트에 전송될 이벤트

    static class Event {

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

        public static class ShowSignInFailureMessage extends Event {
            public final String message;
            public ShowSignInFailureMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToSignUpScreen extends Event {}

        public static class NavigateToAdminScreen extends Event {}

        public static class ShowNfcReadScreen extends Event {}

        public static class ShowInvalidNfcMessage extends Event {
            public final String message;
            public ShowInvalidNfcMessage(String message) {
                this.message = message;
            }
        }

        public static class NavigateToOrderScreen extends Event {
            public final String userId;
            public final int tableNumber;
            public NavigateToOrderScreen(String userId, int tableNumber) {
                this.userId = userId;
                this.tableNumber = tableNumber;
            }
        }

    }

}

