package com.holy.deliveryapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.holy.deliveryapp.models.Order;
import com.holy.deliveryapp.models.UserData;

import java.text.NumberFormat;
import java.util.Date;
import java.util.List;

public class OrderActivity extends AppCompatActivity {

    public static final String EXTRA_LOCAL_NAME = "com.holy.deliveryapp.local_name";
    public static final String EXTRA_LOCAL_ADDRESS = "com.holy.deliveryapp.local_address";
    public static final String EXTRA_ORDER_LIST = "com.holy.deliveryapp.order_list";
    public static final String EXTRA_TOTAL_PRICE = "com.holy.deliveryapp.total_price";

    public static final int RESULT_ORDER_COMPLETE = 100;
    public static final int RESULT_ORDER_REJECTED = 101;


    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference mUserColl = mFirestore.collection("users");
    private final CollectionReference mOrderColl = mFirestore.collection("orders");

    private String mLocalName;
    private String mLocalAddress;
    private List<String> mOrderList;
    private int mTotalPrice;
    private FirebaseUser mUser;
    private UserData mUserData;

    private View mOrderLayout;
    private ProgressBar mOrderProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mLocalName = getIntent().getStringExtra(EXTRA_LOCAL_NAME);
        mLocalAddress = getIntent().getStringExtra(EXTRA_LOCAL_ADDRESS);
        mOrderList = getIntent().getStringArrayListExtra(EXTRA_ORDER_LIST);
        mTotalPrice = getIntent().getIntExtra(EXTRA_TOTAL_PRICE, 0);

        mOrderLayout = findViewById(R.id.layoutOrder);
        mOrderProgress = findViewById(R.id.progressOrder);

        showOrderView(false);

        mAuthStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                finish();
            } else {
                mUser = firebaseAuth.getCurrentUser();
                mUserColl.document(mUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            mUserData = documentSnapshot.toObject(UserData.class);
                            if (mUserData == null) {
                                Toast.makeText(this,
                                        "회원 정보가 없습니다", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                            if (mUserData.getAddress() == null) {
                                Toast.makeText(this,
                                        "입력된 주소가 없습니다", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                            updateUI();
                            showOrderView(true);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this,
                                    "회원 정보가 없습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        });
            }
        };

        Button orderButton = findViewById(R.id.btnOrder);
        orderButton.setOnClickListener(v -> makeOrder());
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

    private void updateUI() {

        TextView localNameText = findViewById(R.id.txtLocalName);
        TextView localAddressText = findViewById(R.id.txtLocalAddress);
        TextView userNameText = findViewById(R.id.txtUserName);
        TextView orderAddressText = findViewById(R.id.txtOrderAddress);
        TextView orderListText = findViewById(R.id.txtOrderList);
        TextView totalPriceText = findViewById(R.id.txtTotalPrice);
        TextView balanceText = findViewById(R.id.txtBalance);

        // 업소정보 입력
        localNameText.setText(mLocalName);
        localAddressText.setText(mLocalAddress);

        // 주문목록 입력
        StringBuilder strOrderList = new StringBuilder();
        for (int i = 0; i < mOrderList.size(); i++) {
            strOrderList.append(mOrderList.get(i));
            if (i < mOrderList.size() - 1) {
                strOrderList.append(", ");
            }
        }
        orderListText.setText(strOrderList);

        // 금액 입력
        String strTotalPrice = NumberFormat.getInstance().format(mTotalPrice);
        totalPriceText.setText(strTotalPrice);

        // 회원정보 입력
        String name = mUserData.getName();
        userNameText.setText(name);

        String orderAddress = mUserData.getAddress();
        orderAddressText.setText(orderAddress);

        int balance = mUserData.getBalance();
        String strBalance = NumberFormat.getInstance().format(balance);
        balanceText.setText(strBalance);
    }

    private void makeOrder() {

        Order order = new Order(mLocalName, mOrderList, mTotalPrice, new Date(), mUser.getUid());
        int balance = mUserData.getBalance();

        if (balance < mTotalPrice) {
            Toast.makeText(this, "금액이 부족합니다", Toast.LENGTH_SHORT).show();
            setResult(RESULT_ORDER_REJECTED);
            finish();
            return;
        }

        mOrderColl.document().set(order)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "주문이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    mUserColl.document(mUser.getUid()).update("balance", balance - mTotalPrice);
                    setResult(RESULT_ORDER_COMPLETE);
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "주문에 실패하였습니다", Toast.LENGTH_SHORT).show());
    }

    private void showOrderView(boolean show) {

        if (show) {
            mOrderLayout.setVisibility(View.VISIBLE);
            mOrderProgress.setVisibility(View.INVISIBLE);
        } else {
            mOrderLayout.setVisibility(View.INVISIBLE);
            mOrderProgress.setVisibility(View.VISIBLE);
        }
    }

}