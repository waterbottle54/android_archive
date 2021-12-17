package com.penelope.htmlmaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.penelope.htmlmaster.databinding.ActivityMainBinding;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private StorageReference htmlStorage;
    private final MutableLiveData<String> htmlText = new MutableLiveData<>("");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        htmlStorage = FirebaseStorage.getInstance().getReference().child("html");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.searchViewUrl.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        binding.fabPaste.setOnClickListener(v -> doPaste());
        binding.fabUpload.setOnClickListener(v -> uploadHtml());

        buildWebView();

        htmlText.observe(this, html -> {
            if (html != null) {
                binding.textViewHtml.setText(html);
            }
            binding.textViewNoHtml.setVisibility(html == null ? View.VISIBLE : View.INVISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void buildWebView() {

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.addJavascriptInterface(new JsInterface(), "JsInterface");
        binding.webView.setWebChromeClient(new WebChromeClient());
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:window.JsInterface.getHtml" +
                        "('" + url + "', " + "'<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });
    }

    private void doSearch(String query) {
        binding.webView.loadUrl(query);
        binding.progressBar.setVisibility(View.VISIBLE);
        hideKeyboard(getWindow().getDecorView());
    }

    private void doPaste() {
        String strClipboard = getClipboardText();
        if (strClipboard != null) {
            binding.searchViewUrl.setQuery(strClipboard, true);
        } else {
            Snackbar.make(getWindow().getDecorView(), "No text to paste", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void uploadHtml() {

        String html = htmlText.getValue();
        if (html == null) {
            return;
        }

        LocalDateTime ldt = LocalDateTime.now();
        String strNow = String.format(Locale.getDefault(), "%d-%02d-%02d_%02d:%02d",
                ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(),
                ldt.getHour(), ldt.getMinute()
        );
        StorageReference reference = htmlStorage.child(strNow + ".txt");

        binding.progressBar.setVisibility(View.VISIBLE);
        reference.putBytes(html.getBytes(StandardCharsets.UTF_8))
                .addOnSuccessListener(runnable -> {
                    Snackbar.make(getWindow().getDecorView(), "업로드가 완료되었습니다", Snackbar.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.INVISIBLE);
                })
                .addOnFailureListener(e -> {
                    Snackbar.make(getWindow().getDecorView(), "업로드에 실패했습니다", Snackbar.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.INVISIBLE);
                });
    }

    private void hideKeyboard(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private String getClipboardText() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return null;
        ClipData clip = clipboard.getPrimaryClip();
        if (clip == null) return null;
        ClipData.Item item = clip.getItemAt(0);
        if (item == null) return null;
        CharSequence textToPaste = item.getText();
        if (textToPaste == null) return null;

        return textToPaste.toString();
    }

    class JsInterface {

        @JavascriptInterface
        public void getHtml(String url, String html) {
            Document document = Jsoup.parse((String) html);
            htmlText.postValue(document.outerHtml());
        }
    }

}