package com.davidjo.remedialexercise.ui.initiate.goods;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.davidjo.remedialexercise.R;
import com.davidjo.remedialexercise.data.BodyPart;
import com.davidjo.remedialexercise.databinding.FragmentGoodsBinding;
import com.davidjo.remedialexercise.util.NameUtils;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class GoodsFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goods, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentGoodsBinding binding = FragmentGoodsBinding.bind(view);

        BodyPart bodyPart = BodyPart.NECK;
        if (getArguments() != null) {
            bodyPart = GoodsFragmentArgs.fromBundle(getArguments()).getBodyPart();
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        String url = String.format(Locale.getDefault(),
                getString(R.string.url_goods),
                NameUtils.getBodyPartName(bodyPart) + " 운동"
        );
        binding.webView.loadUrl(url);
    }

}
