package com.holy.deliveryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.holy.deliveryapp.models.Local;
import com.holy.deliveryapp.models.UserData;

public class HomeActivity extends AppCompatActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnLocalClickListener, LocalFragment.LocalFragmentCallback {


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference mUserColl = mFirestore.collection("users");

    private final HomeFragment mHomeFragment = new HomeFragment();
    private final LocalFragment mLocalFragment = new LocalFragment();
    private final MyFragment mMyFragment = new MyFragment();
    private final CoronaFragment mCoronaFragment = new CoronaFragment();
    private Fragment mCurrentFragment;

    private BottomNavigationView mNavigationView;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mNavigationView = findViewById(R.id.navBottom);
        mNavigationView.setOnNavigationItemSelectedListener(this);

        showHomeFragment();

        mAuthStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                finish();
            } else {
                mUserColl.document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            UserData userData = documentSnapshot.toObject(UserData.class);
                            if (userData != null && userData.getAddress() == null) {
                                confirmSetAddress();
                            }
                        });
            }
        };
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

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected
            (@NonNull @org.jetbrains.annotations.NotNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.itemHome) {
            showHomeFragment();
            return true;
        } else if (id == R.id.itemMy) {
            showMyFragment();
            return true;
        } else if (id == R.id.itemCorona) {
            showCoronaFragment();
            return true;
        }

        return false;
    }

    private void showHomeFragment() {

        mCurrentFragment = mHomeFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showLocalFragment() {

        mCurrentFragment = mLocalFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showMyFragment() {

        mCurrentFragment = mMyFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void showCoronaFragment() {

        mCurrentFragment = mCoronaFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragContainer, mCurrentFragment)
                .addToBackStack(null)
                .commit();
    }

    private void updateBottomNavigation() {

        if (mCurrentFragment == mHomeFragment) {
            mNavigationView.setSelectedItemId(R.id.itemHome);
        } else if (mCurrentFragment == mLocalFragment) {
            mNavigationView.setSelectedItemId(R.id.itemHome);
        } else if (mCurrentFragment == mMyFragment) {
            mNavigationView.setSelectedItemId(R.id.itemMy);
        } else if (mCurrentFragment == mCoronaFragment) {
            mNavigationView.setSelectedItemId(R.id.itemCorona);
        }
    }

    @Override
    public void onLocalClick(Local local) {

        mLocalFragment.setLocal(local);
        showLocalFragment();
    }

    @Override
    public void onOrderComplete() {

        getSupportFragmentManager().popBackStack();
        showMyFragment();
        updateBottomNavigation();
    }

    @Override
    public void onOrderRejected() {

        showMyFragment();
        updateBottomNavigation();
    }

    private void confirmSetAddress() {

        new AlertDialog.Builder(this)
                .setTitle("주소 설정")
                .setMessage("주소가 설정되어 있지 않습니다. 주소를 설정하시겠습니까?")
                .setPositiveButton("네", (dialog, which) -> {
                    showMyFragment();
                    updateBottomNavigation();
                })
                .setNegativeButton("아니오", null)
                .show();
    }

}