package com.cool.nfckiosk.ui.auth.signup;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.cool.nfckiosk.data.store.Store;
import com.cool.nfckiosk.util.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SignUpViewModel extends ViewModel implements FirebaseAuth.AuthStateListener {

    private final MutableLiveData<Event> event = new MutableLiveData<>();         // 프래그먼트에서 처리할 이벤트

    private final MutableLiveData<String> userId = new MutableLiveData<>("");             // 유저 아이디 값
    private final MutableLiveData<String> password = new MutableLiveData<>("");           // 비밀번호 값
    private final MutableLiveData<String> passwordConfirm = new MutableLiveData<>("");    // 비밀번호 확인값

    private final MutableLiveData<List<Store.Table>> tables = new MutableLiveData<>(new ArrayList<>());


    private final FirebaseAuth auth;
    private final CollectionReference storeCollection;


    @Inject
    public SignUpViewModel(FirebaseAuth auth, FirebaseFirestore firestore) {
        this.auth = auth;
        this.storeCollection = firestore.collection("stores");
    }

    public LiveData<Event> getEvent() {
        event.setValue(null);
        return event;
    }

    public LiveData<List<Store.Table>> getTables() {
        return tables;
    }


    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() != null) {
            event.setValue(new Event.NavigateToHomeScreen());
        }
    }

    public void onUserIdChanged(String value) {
        // 아이디 입력값이 변경되었을 때
        userId.setValue(value);

        // 태그 초기화
        tables.setValue(new ArrayList<>());
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
                .addOnSuccessListener(authResult -> {
                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        event.setValue(new Event.ShowSignUpFailureMessage("회원 정보 생성에 실패했습니다"));
                        return;
                    }
                    Store store = new Store(user.getUid(), userIdValue, "", tables.getValue());
                    storeCollection.document(store.getId()).set(store)
                            .addOnSuccessListener(unused ->
                                    auth.signInWithEmailAndPassword(email, passwordValue))
                            .addOnFailureListener(e ->
                                    event.setValue(new Event.ShowSignUpFailureMessage("점포 정보 생성에 실패했습니다")));
                })
                .addOnFailureListener(e -> event.postValue(
                        new Event.ShowSignUpFailureMessage("이미 존재하는 아이디입니다"))
                );
    }

    public void onTagClick() {

        String id = userId.getValue();
        assert id != null;
        if (id.length() < 4) {
            // 에러 : 짧은 아이디
            event.setValue(new Event.ShowShortUserIdMessage("아이디를 4글자 이상 입력해주세요"));
            return;
        }

        assert tables.getValue() != null;
        int tableNumber = tables.getValue().size() + 1;
        String textToWrite = String.format(Locale.getDefault(), "%s#%d", id, tableNumber);
        event.setValue(new Event.ShowNfcWriteScreen(textToWrite));
    }

    public void onNfcWriteResult(boolean success) {

        if (!success) {
            event.setValue(new Event.ShowNfcWriteFailureMessage("NFC 태그 활성화에 실패했습니다"));
            return;
        }

        assert tables.getValue() != null;
        List<Store.Table> tableList = new ArrayList<>(tables.getValue());
        tableList.add(new Store.Table(tableList.size() + 1, true));
        tables.setValue(tableList);

        event.setValue(new Event.ShowNfcActivatedMessage(tableList.size() + "번 태그가 활성화 되었습니다"));
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

        public static class ShowNfcWriteScreen extends Event {
            public final String textToWrite;
            public ShowNfcWriteScreen(String textToWrite) {
                this.textToWrite = textToWrite;
            }
        }

        public static class ShowNfcActivatedMessage extends Event {
            public final String message;
            public ShowNfcActivatedMessage(String message) {
                this.message = message;
            }
        }

        public static class ShowNfcWriteFailureMessage extends Event {
            public final String message;
            public ShowNfcWriteFailureMessage(String message) {
                this.message = message;
            }
        }

    }

}

