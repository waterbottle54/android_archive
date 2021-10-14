package com.holy.deliveryapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import org.jetbrains.annotations.NotNull;

public class CoronaFragment extends Fragment {

    public static final String URL_CORONA_MAP = "http://ncov.mohw.go.kr/bdBoardList_Real.do?brdId=1&brdGubun=13&ncvContSeq=&contSeq=&board_id=&gubun=";
    public static final String URL_SOCIAL_DISTANCING_POLICY = "http://ncov.mohw.go.kr/regSocdisBoardView.do";


    private WebView mWebView;
    private ProgressBar mWebViewProgress;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_corona, container, false);

        mWebView = view.findViewById(R.id.webView);
        mWebViewProgress = view.findViewById(R.id.progressWebView);

        Button socialDistancingButton = view.findViewById(R.id.btnSocialDistancingPolicy);
        socialDistancingButton.setOnClickListener(v -> browseSocialDistancingPolicy());

        showWebView(false);

        return view;
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                view.postDelayed(() -> showWebView(true), 1000);
            }
        });
        mWebView.loadUrl(URL_CORONA_MAP);

        int scroll = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 508, getResources().getDisplayMetrics());
        mWebView.scrollTo(0, scroll);
        mWebView.setOnTouchListener((v, event) -> true);
    }

    private void browseSocialDistancingPolicy() {

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_SOCIAL_DISTANCING_POLICY));
        startActivity(intent);
    }

    private void showWebView(boolean show) {

        if (show) {
            mWebView.setVisibility(View.VISIBLE);
            mWebViewProgress.setVisibility(View.INVISIBLE);
        } else {
            mWebView.setVisibility(View.INVISIBLE);
            mWebViewProgress.setVisibility(View.VISIBLE);
        }
    }

}