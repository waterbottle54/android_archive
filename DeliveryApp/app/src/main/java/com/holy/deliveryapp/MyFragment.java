package com.holy.deliveryapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.holy.deliveryapp.adapters.OrderAdapter;
import com.holy.deliveryapp.models.Order;
import com.holy.deliveryapp.models.UserData;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class MyFragment extends Fragment {

    private Context mContext;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference mUserColl = mFirestore.collection("users");
    private final CollectionReference mOrderColl = mFirestore.collection("orders");

    private TextView mNameText;
    private TextView mBalanceText;
    private TextView mAddressText;

    private RecyclerView mOrderRecycler;
    private ProgressBar mOrderRecyclerProgress;
    private TextView mNoOrdersText;

    private FirebaseUser mUser;
    private UserData mUserData;
    private List<Order> mOrderList;


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my, container, false);

        mNameText = view.findViewById(R.id.txtUserName);
        mBalanceText = view.findViewById(R.id.txtBalance);
        mAddressText = view.findViewById(R.id.txtOrderAddress);
        mNoOrdersText = view.findViewById(R.id.txtNoOrdersText);

        mOrderRecycler = view.findViewById(R.id.recyclerOrder);
        mOrderRecyclerProgress = view.findViewById(R.id.progressRecyclerOrder);

        updateRecyclerVisibility(false);

        mUser = mAuth.getCurrentUser();

        if (mUser != null) {
            queryUserData();
            queryOrderData();
        } else {
            Toast.makeText(mContext,
                    "로그인되어 있지 않습니다", Toast.LENGTH_SHORT).show();
        }

        TextView signOutText = view.findViewById(R.id.txtSignOut);
        Button changeAddressButton = view.findViewById(R.id.btnChangeAddress);
        Button chargeButton = view.findViewById(R.id.btnCharge);

        signOutText.setOnClickListener(v -> confirmSignOut());
        changeAddressButton.setOnClickListener(v -> showChangeAddressDialog());
        chargeButton.setOnClickListener(v -> showChargeDialog());

        return view;
    }

    private void queryUserData() {

        // 유저 데이터 쿼리
        mUserColl.document(mUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    mUserData = documentSnapshot.toObject(UserData.class);
                    if (mUserData == null) {
                        Toast.makeText(mContext,
                                "회원 정보가 없습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateUserUI();
                })
                .addOnFailureListener(e -> Toast.makeText(mContext,
                        "회원 정보가 없습니다", Toast.LENGTH_SHORT).show());
    }

    private void queryOrderData() {

        // 주문 내역 쿼리
        mOrderColl.whereEqualTo("userId", mUser.getUid())
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mOrderList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Order order = documentSnapshot.toObject(Order.class);
                        mOrderList.add(order);
                    }
                    buildOrderRecycler();
                    updateRecyclerVisibility(true);
                })
                .addOnFailureListener(e -> Toast.makeText(mContext,
                        "주문 내역을 불러올 수 없습니다", Toast.LENGTH_SHORT).show());
    }

    private void updateUserUI() {

        mNameText.setText(mUserData.getName());

        String strBalance = NumberFormat.getInstance().format(mUserData.getBalance());
        mBalanceText.setText(strBalance);

        mAddressText.setText(mUserData.getAddress());
    }

    private void buildOrderRecycler() {

        mOrderRecycler.setHasFixedSize(true);
        mOrderRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        OrderAdapter adapter = new OrderAdapter(mOrderList);
        mOrderRecycler.setAdapter(adapter);
    }

    private void updateRecyclerVisibility(boolean show) {

        if (show) {
            mOrderRecycler.setVisibility(View.VISIBLE);
            mOrderRecyclerProgress.setVisibility(View.INVISIBLE);
            if (mOrderList.isEmpty()) {
                mNoOrdersText.setVisibility(View.VISIBLE);
            }
        } else {
            mOrderRecycler.setVisibility(View.INVISIBLE);
            mOrderRecyclerProgress.setVisibility(View.VISIBLE);
            mNoOrdersText.setVisibility(View.INVISIBLE);
        }
    }

    private void confirmSignOut() {

        new AlertDialog.Builder(mContext)
                .setTitle(R.string.sign_out)
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("네", (dialog, which) -> mAuth.signOut())
                .setNegativeButton("아니오", null)
                .show();
    }

    private void showChangeAddressDialog() {

        if (mUser == null || mUserData == null) {
            Toast.makeText(mContext, "회원 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        View changeAddressView = View.inflate(mContext, R.layout.view_change_address, null);
        EditText addressEditText = changeAddressView.findViewById(R.id.editAddress);

        addressEditText.setText(mUserData.getAddress());
        addressEditText.selectAll();

        new AlertDialog.Builder(mContext)
                .setTitle("주소 변경")
                .setView(changeAddressView)
                .setPositiveButton("변경", (dialog, which) -> {
                    String address = addressEditText.getText().toString().trim();
                    if (address.isEmpty()) {
                        Toast.makeText(mContext, "주소를 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mUserColl.document(mUser.getUid()).update("address", address)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(mContext,
                                        "주소가 변경되었습니다", Toast.LENGTH_SHORT).show();
                                queryUserData();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(mContext,
                                        "주소 변경에 실패했습니다", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            });
                })
                .setNegativeButton("취소", null)
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void showChargeDialog() {

        if (mUser == null || mUserData == null) {
            Toast.makeText(mContext, "회원 정보가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        View chargeView = View.inflate(mContext, R.layout.view_charge, null);
        EditText amountEditText = chargeView.findViewById(R.id.editAmountOfMoney);

        amountEditText.setText("100000");
        amountEditText.selectAll();

        new AlertDialog.Builder(mContext)
                .setTitle("충전")
                .setView(chargeView)
                .setPositiveButton("충전", (dialog, which) -> {
                    String strAmount = amountEditText.getText().toString().trim();
                    if (strAmount.isEmpty()) {
                        Toast.makeText(mContext, "금액을 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int amount = Integer.parseInt(strAmount);

                    mUserColl.document(mUser.getUid()).update("balance", mUserData.getBalance() + amount)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(mContext,
                                        "충전이 완료되었습니다", Toast.LENGTH_SHORT).show();
                                queryUserData();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(mContext,
                                        "충전에에 실패했습니다", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            });
                })
                .setNegativeButton("취소", null)
                .show();
    }

}