package com.planted.holiday;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // Open API 의 URL, 서비스 키
    public static final String URL_FORMAT = "http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo?numOfRows=100&serviceKey=%s&solYear=%d";
    public static final String SERVICE_KEY = "MtVBdtw16Ez1YFEXjvMpB0Zws4eaThA35edKG44NT6A0P3gsproUZBkpaFUUVrHmaPUzkazSncH9iukFY%2BdQfw%3D%3D";

    // 공휴일 어댑터뷰의 커스텀 어댑터
    private final HolidaysAdapter adapter = new HolidaysAdapter();
    // 로딩 시 보여줄 프로그레스바
    private ProgressBar progress;
    // 공휴일 목록 제목
    private TextView textViewTableTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 초기화
        progress = findViewById(R.id.progress);
        textViewTableTitle = findViewById(R.id.textViewTableTitle);
        EditText editTextYear = findViewById(R.id.editTextYear);
        Button buttonSearch = findViewById(R.id.buttonSearch);

        // 공휴일 리사이클러뷰에 커스텀 어댑터 연결
        RecyclerView recyclerHoliday = findViewById(R.id.recyclerHoliday);
        recyclerHoliday.setAdapter(adapter);
        recyclerHoliday.setLayoutManager(new LinearLayoutManager(this));
        recyclerHoliday.setHasFixedSize(true);

        // 검색 버튼 누르면 검색 되도록 리스너 구현
        buttonSearch.setOnClickListener(v -> {
            String strYear = editTextYear.getText().toString();
            if (strYear.isEmpty()) {
                return;
            }
            search(Integer.parseInt(strYear));
        });

        // 기본으로 2021년 검색
        search(2021);
    }

    public void search(int year) {

        // url 을 async task 에 전달해서 공휴일검색 수행
        String url = String.format(Locale.getDefault(), URL_FORMAT, SERVICE_KEY, year);
        HolidayTask task = new HolidayTask(year);
        task.execute(url);
    }


    class HolidayTask extends AsyncTask<String, Void, List<Holiday>> {

        final int year; // 검색하는 년도

        public HolidayTask(int year) {
            this.year = year;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // 검색 수행 전에 로딩띄우기
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Holiday> doInBackground(String... strings) {

            String strUrl = strings[0];

            try {
                // HTTP 연결하기
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(60000 /* 밀리초 */);
                conn.setConnectTimeout(60000 /* 밀리초 */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                // API 에서 제공한 자료 받아서 파싱하기
                InputStream inputStream = conn.getInputStream();
                return HolidayXmlParser.parse(inputStream);

            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Holiday> holidays) {
            super.onPostExecute(holidays);

            if (holidays != null) {
                // 공휴일 검색 결과 어댑터뷰에 전송
                adapter.submitList(holidays);

                // 공휴일 목록 제목 설정
                String tableTitle = year + "년의 공휴일 목록입니다";
                textViewTableTitle.setText(tableTitle);
            } else {
                // 검색 실패
                Toast.makeText(MainActivity.this,
                        "연결에 실패하였습니다!", Toast.LENGTH_SHORT).show();
            }

            // 프로그레스바 없앰
            progress.setVisibility(View.INVISIBLE);
        }
    }


}