package com.cool.nfckiosk.util.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;

public abstract class AuthDialogFragment extends DialogFragment implements FirebaseAuth.AuthStateListener {

    protected Context context;
    private final FirebaseAuth auth;

    public AuthDialogFragment() {
        auth = FirebaseAuth.getInstance();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onResume() {
        super.onResume();
        auth.addAuthStateListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        auth.removeAuthStateListener(this);
    }

}
