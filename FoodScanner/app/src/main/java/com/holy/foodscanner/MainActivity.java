package com.holy.foodscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.holy.foodscanner.helpers.ProductXmlParser;
import com.holy.foodscanner.models.Product;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String URL_PRODUCT_FORMAT = "http://openapi.foodsafetykorea.go.kr/api/[KEY]/C005/xml/1/1/BAR_CD=[BARCODE]";
    public static final String PLACEHOLDER_KEY = "[KEY]";
    public static final String PLACEHOLDER_BARCODE = "[BARCODE]";
    public static final String API_KEY = "a0b50f135a6445839719";

    private TextView productNameText;
    private TextView productTypeText;
    private TextView manufacturerText;
    private TextView shelfLifeText;

    // http://openapi.foodsafetykorea.go.kr/api/a0b50f135a6445839719/C005/xml/1/1/BAR_CD=[BARCODE]
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 뷰 획득
        productNameText = findViewById(R.id.txtProductName);
        productTypeText = findViewById(R.id.txtProductType);
        manufacturerText = findViewById(R.id.txtManufacturer);
        shelfLifeText = findViewById(R.id.txtShelfLife);

        updateProductUI(null);

        // 스캔 텍스트뷰에 클릭 리스너 설정 (스캐너 액티비티 시작)
        TextView scanText = findViewById(R.id.txtScan);
        scanText.setOnClickListener(v -> new IntentIntegrator(this).initiateScan());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 스캔 결과 전송
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() != null) {
                loadProductData(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // 식품정보 불러오기 (API 로부터)

    private void loadProductData(String barcode) {

        String url = URL_PRODUCT_FORMAT
                .replace(PLACEHOLDER_KEY, API_KEY)
                .replace(PLACEHOLDER_BARCODE, barcode);

        new ProductXmlTask(this).execute(url);
    }

    // 식품정보 UI 업데이트

    private void updateProductUI(Product product) {

        if (product != null) {
            productNameText.setText(product.getName());
            productTypeText.setText(product.getType());
            manufacturerText.setText(product.getManufacturer());
            shelfLifeText.setText(product.getShelfLife());
        } else {
            productNameText.setText("-");
            productTypeText.setText("-");
            manufacturerText.setText("-");
            shelfLifeText.setText("-");
        }
    }


    // XML 로부터 정보를 불러오기 위한 AsyncTask

    @SuppressWarnings("deprecation")
    static class ProductXmlTask extends AsyncTask<String, Void, Product> {

        // 액티비티 레퍼런스
        private final WeakReference<MainActivity> reference;

        // 생성자
        public ProductXmlTask(MainActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = reference.get();
            if (activity == null) {
                return;
            }
        }

        // 백그라운드 태스크 (병원 정보 로드)
        @Override
        protected Product doInBackground(String... strings) {

            // 주어진 URL 링크 확인
            String strUrl = strings[0];

            try {
                // URL 을 다운로드받아 InputStream 획득
                InputStream inputStream = downloadUrl(strUrl);

                // InputStream 을 parse 하여 리스트 구성
                return new ProductXmlParser().parse(inputStream);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Product product) {

            MainActivity activity = reference.get();
            if (activity == null) {
                return;
            }

            if (product == null) {
                Toast.makeText(reference.get(),
                        "식품의약품안전처에 등록된 제품이 아닙니다", Toast.LENGTH_LONG).show();
                activity.updateProductUI(null);
                return;
            }

            // UI 업데이트
            activity.updateProductUI(product);
        }

        // URL 로부터 InputStream 을 생성하기

        private InputStream downloadUrl(String strUrl) throws IOException {

            java.net.URL url = new URL(strUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(60000 /* 밀리초 */);
            conn.setConnectTimeout(60000 /* 밀리초 */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // 쿼리 시작
            conn.connect();
            return conn.getInputStream();
        }
    }

}