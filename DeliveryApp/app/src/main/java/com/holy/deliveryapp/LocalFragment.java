package com.holy.deliveryapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holy.deliveryapp.adapters.MenuAdapter;
import com.holy.deliveryapp.helpers.LocalUrlContentsParser;
import com.holy.deliveryapp.models.Local;
import com.holy.deliveryapp.models.LocalUrlContents;
import com.holy.deliveryapp.models.Menu;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalFragment extends Fragment implements OnMapReadyCallback {

    public interface LocalFragmentCallback {
        void onOrderComplete();
        void onOrderRejected();
    }


    private Context mContext;
    private LocalFragmentCallback mHost;

    private Local mLocal;
    private LocalUrlContents mLocalUrlContents;

    private TextView mLocalNameText;
    private TextView mLocalAddressText;
    private TextView mLocalPhoneText;
    private RecyclerView mMenuRecycler;
    private ProgressBar mMenuRecyclerProgress;
    private TextView mMenuNotPreparedText;
    private TextView mTotalPriceText;

    private NaverMap mMap;

    private WebView mWebView;

    private final Map<Integer, Integer> mCheckedMenuMap = new HashMap<>();

    private ActivityResultLauncher<Intent> mOrderActivityLauncher;


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            mHost = (LocalFragmentCallback) context;
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local, container, false);

        mLocalNameText = view.findViewById(R.id.txtLocalName);
        mLocalAddressText = view.findViewById(R.id.txtLocalAddress);
        mLocalPhoneText = view.findViewById(R.id.txtLocalPhone);
        mMenuRecycler = view.findViewById(R.id.recyclerMenu);
        mMenuRecyclerProgress = view.findViewById(R.id.progressRecyclerMenu);
        mMenuNotPreparedText = view.findViewById(R.id.txtMenuNotPrepared);
        mTotalPriceText = view.findViewById(R.id.txtTotalPrice);
        mWebView = view.findViewById(R.id.webView);

        Button orderButton = view.findViewById(R.id.btnOrder);
        orderButton.setOnClickListener(v -> startOrderActivity());

        mOrderActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    int resultCode = result.getResultCode();
                    if (resultCode == OrderActivity.RESULT_ORDER_COMPLETE) {
                        mHost.onOrderComplete();
                    } else if (resultCode == OrderActivity.RESULT_ORDER_REJECTED) {
                        mHost.onOrderRejected();
                    }
                });

        return view;
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mLocal != null) {
            updateUI();
        }

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "HtmlViewer");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.postDelayed(() -> view.loadUrl("javascript:HtmlViewer.getHtml" +
                                "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');"),
                        1000);
            }
        });
        mWebView.loadUrl(mLocal.getUrl());
        showMenuRecycler(false);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void updateLocalUrlContents(String html) {

        mLocalUrlContents = LocalUrlContentsParser.parse(html);

        mCheckedMenuMap.clear();
        for (int i = 0; i < mLocalUrlContents.getMenuList().size(); i++) {
            mCheckedMenuMap.put(i, 0);
        }

        if (getView() != null) {
            updateLocalUrlContentsUI();
        }
    }

    private void updateUI() {

        mLocalNameText.setText(mLocal.getName());
        mLocalAddressText.setText(mLocal.getAddress());
        mLocalPhoneText.setText(mLocal.getPhone());
    }

    private void updateLocalUrlContentsUI() {

        buildMenuRecycler();
        showMenuRecycler(true);

        if (mLocalUrlContents.getMenuList().isEmpty()) {
            mMenuNotPreparedText.setVisibility(View.VISIBLE);
        } else {
            mMenuNotPreparedText.setVisibility(View.INVISIBLE);
        }
    }

    private void buildMenuRecycler() {

        mMenuRecycler.setHasFixedSize(true);
        mMenuRecycler.setLayoutManager(new LinearLayoutManager(mContext));

        MenuAdapter adapter = new MenuAdapter(mLocalUrlContents.getMenuList());
        mMenuRecycler.setAdapter(adapter);

        adapter.setOnItemClickListener(new MenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
            }

            @Override
            public void onItemChecked(int position, boolean isChecked) {
                mCheckedMenuMap.replace(position, isChecked ? 1 : 0);
                updatePriceUI();
            }
        });
    }

    private void showMenuRecycler(boolean show) {

        if (show) {
            mMenuRecycler.setVisibility(View.VISIBLE);
            mMenuRecyclerProgress.setVisibility(View.INVISIBLE);
        } else {
            mMenuRecycler.setVisibility(View.INVISIBLE);
            mMenuRecyclerProgress.setVisibility(View.VISIBLE);
        }
    }

    public void setLocal(Local mLocal) {

        this.mLocal = mLocal;

        if (getView() != null) {
            updateUI();
        }

        if (mMap != null) {
            moveCamera();
        }
    }

    @Override
    public void onMapReady(@NonNull @NotNull NaverMap naverMap) {

        mMap = naverMap;

        moveCamera();
    }

    private void moveCamera() {

        if (mLocal != null && mMap != null) {
            LatLng target = new LatLng(mLocal.getLatitude(), mLocal.getLongitude());
            mMap.moveCamera(CameraUpdate.scrollAndZoomTo(target, 15));
        }
    }

    private int getTotalPrice() {

        int total = 0;

        for (Map.Entry<Integer, Integer> entry : mCheckedMenuMap.entrySet()) {
            Menu menu = mLocalUrlContents.getMenuList().get(entry.getKey());
            total += menu.getPrice() * entry.getValue();
        }

        return total;
    }

    private List<String> getOrderList() {

        List<String> orderList = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : mCheckedMenuMap.entrySet()) {
            Menu menu = mLocalUrlContents.getMenuList().get(entry.getKey());
            if (entry.getValue() > 0) {
                orderList.add(menu.getName());
            }
        }

        return orderList;
    }

    private void updatePriceUI() {

        int total = getTotalPrice();

        String strTotal = NumberFormat.getInstance().format(total);
        mTotalPriceText.setText(strTotal);
    }

    private void startOrderActivity() {

        List<String> orderList = getOrderList();
        if (orderList.isEmpty()) {
            Toast.makeText(mContext, "주문 내역이 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(mContext, OrderActivity.class);
        intent.putExtra(OrderActivity.EXTRA_LOCAL_NAME, mLocal.getName());
        intent.putExtra(OrderActivity.EXTRA_LOCAL_ADDRESS, mLocal.getAddress());
        intent.putStringArrayListExtra(OrderActivity.EXTRA_ORDER_LIST, new ArrayList<>(orderList));
        intent.putExtra(OrderActivity.EXTRA_TOTAL_PRICE, getTotalPrice());

        mOrderActivityLauncher.launch(intent);
    }


    @SuppressLint("HandlerLeak")
    @SuppressWarnings("deprecation")
    Handler mUpdateLocalUrlContentsHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            updateLocalUrlContents((String) msg.obj);
        }
    };

    class MyJavaScriptInterface {

        @JavascriptInterface
        @SuppressWarnings("unused")
        public void getHtml(String html) {

            Message message = new Message();
            message.obj = html;
            mUpdateLocalUrlContentsHandler.sendMessage(message);
        }
    }

}