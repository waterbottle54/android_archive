package com.holy.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class SignInActivity extends AppCompatActivity {

    private EditText mIdEdit;
    private EditText mPasswordEdit;


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseAuth.AuthStateListener mAuthStateListener = firebaseAuth -> {
        if (firebaseAuth.getCurrentUser() != null) {
            startHomeActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mIdEdit = findViewById(R.id.editUserId);
        mPasswordEdit = findViewById(R.id.editUserPassword);

        Button signInButton = findViewById(R.id.btnSignIn);
        View signUpView = findViewById(R.id.txtSignUp);

        signInButton.setOnClickListener(v -> trySignIn());
        signUpView.setOnClickListener(v -> startSignUpActivity());
    }

    @Override
    protected void onResume() {
        super.onResume();

        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void trySignIn() {

        String id = mIdEdit.getText().toString().trim();
        String password = mPasswordEdit.getText().toString().trim();

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

        String email = String.format(Locale.getDefault(),
                "%s@%s", id, getString(R.string.email_suffix));

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnFailureListener(e -> Toast.makeText(this,
                        "일치하는 회원정보가 없습니다", Toast.LENGTH_SHORT).show());
    }

    private void startSignUpActivity() {

        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void startHomeActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}