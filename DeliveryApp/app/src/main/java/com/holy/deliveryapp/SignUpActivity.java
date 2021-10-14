package com.holy.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.holy.deliveryapp.models.UserData;

import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private EditText mIdEdit;
    private EditText mPasswordEdit;
    private EditText mPasswordConfirmEdit;
    private EditText mNameEdit;
    private EditText mPhoneEdit;
    private CheckBox mAgreementCheck;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference mUserColl = mFirestore.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mIdEdit = findViewById(R.id.editUserId);
        mPasswordEdit = findViewById(R.id.editUserPassword);
        mPasswordConfirmEdit = findViewById(R.id.editUserPasswordConfirm);
        mNameEdit = findViewById(R.id.editUserName);
        mPhoneEdit = findViewById(R.id.editUserPhone);
        mAgreementCheck = findViewById(R.id.checkPrivacyAgreement);

        Button signUpButton = findViewById(R.id.btnSignUp);
        View signInView = findViewById(R.id.txtSignIn);

        signUpButton.setOnClickListener(v -> trySignUp());
        signInView.setOnClickListener(v -> finish());
    }

    private void trySignUp() {

        String id = mIdEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();
        String passwordConfirm = mPasswordConfirmEdit.getText().toString().trim();
        String name = mNameEdit.getText().toString().trim();
        String phone = mPhoneEdit.getText().toString().trim();
        boolean isAgreed = mAgreementCheck.isChecked();

        if (id.length() < 4) {
            Toast.makeText(this,
                    "아이디는 4글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(this,
                    "비밀번호는 6글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this,
                    "비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() < 2) {
            Toast.makeText(this,
                    "이름은 2글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phone.length() < 9) {
            Toast.makeText(this,
                    "전화번호는 9글자 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isAgreed) {
            Toast.makeText(this,
                    "개인정보이용에 동의해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = String.format(Locale.getDefault(),
                "%s@%s", id, getString(R.string.email_suffix));

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() != null) {
                        String uid = authResult.getUser().getUid();
                        UserData userData = new UserData(uid, name, phone, null);
                        mUserColl.document(uid).set(userData);

                        Toast.makeText(this,
                                "회원가입에 성공했습니다", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    Toast.makeText(this,
                            "회원가입에 실패했습니다", Toast.LENGTH_SHORT).show();
                });
    }

}