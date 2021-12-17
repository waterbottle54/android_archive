package com.penelope.highwaycharger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // API URL 및 서비스 키
    private static final String CHARGER_URL_FORMAT = "https://api.odcloud.kr/api/15085543/v1/uddi:b59af645-60ff-4f5a-901d-8361b63ded89?page=1&perPage=1000&returnType=JSON&serviceKey=%s";
    private static final String SERVICE_KEY = "zN%2FcZN8zzkO%2BtyZ3kAUQ%2FyORgTLE7OVvBq34Tk4H6Tvcf02soMOFZLKwe3gRAsSbgn0pH1M1AbqKmALXsvxIDw%3D%3D";

    // Volley 큐
    private RequestQueue requestQueue;

    // UI 위젯
    private EditText editTextQuery;     // 검색어 입력 에딧
    private Button buttonQuery;         // 검색 버튼
    private ListView listViewRecent;    // 최근 검색어 리스트뷰
    private TextView textViewResult;    // 검색 결과 텍스트뷰
    private ProgressBar progressBar;    // 프로그레스 바 (초기 로딩)

    // 모든 주유소 목록
    private final List<Station> stations = new ArrayList<>();

    // 최근 검색어 목록, 어댑터
    private final List<String> recentQueries = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Volley 큐를 생성한다
        requestQueue = Volley.newRequestQueue(this);

        // 위젯을 획득한다
        editTextQuery = findViewById(R.id.editTextQuery);
        buttonQuery = findViewById(R.id.buttonQuery);
        listViewRecent = findViewById(R.id.listViewRecent);
        textViewResult = findViewById(R.id.textViewResult);
        progressBar = findViewById(R.id.progressBar);

        // 검색어 어댑터 연결
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, recentQueries);
        listViewRecent.setAdapter(adapter);

        // 검색 버튼 클릭 시 검색을 실행한다
        buttonQuery.setOnClickListener(v -> {
            // 검색어로 검색을 실행한다
            String query = editTextQuery.getText().toString().trim();
            searchStations(query);
        });

        // 최근 검색어 클릭 시 검색을 실행한다
        listViewRecent.setOnItemClickListener((parent, view, position, id) -> {
            String query = recentQueries.get(position);
            // 최근 검색어로 검색을 실행한다
            if (query != null) {
                searchStations(query);
                editTextQuery.setText(query);
            }
        });

        // 최초에 모든 주유소를 불러온다
        loadStations();
    }

    private void turnProgressbar(boolean isOn) {
        // 프로그래스 바 숨기거나 보이기
        progressBar.setVisibility(isOn ? View.VISIBLE : View.GONE);
    }

    public void loadStations() {

        // API 호출을 위한 URL 을 구성한다
        String url = String.format(Locale.getDefault(), CHARGER_URL_FORMAT, SERVICE_KEY);

        // API 요청 객체를 정의한다
        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    Log.d(TAG, "load: " + response);
                    // API 로부터 리스폰스가 전달되었으면 파싱한다
                    processResponse(response);
                },
                error -> {
                    // 에러 발생 시 메세지를 보여준다
                    error.printStackTrace();
                    Toast.makeText(this, "API 호출 실패", Toast.LENGTH_SHORT).show();
                });

        // API 요청을 실행한다
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void processResponse(String response) {

        // 리스폰스를 파싱해서 Station 객체의 리스트로 추출한다
        Gson gson = new Gson();
        StationListResult result = gson.fromJson(response, StationListResult.class);
        stations.addAll(result.getStations());

        // 모든 Station 을 UI에 보여준다
        showStations(stations);
    }

    private void showStations(List<Station> stations) {

        // 주어진 Station 리스트를 UI에 보여준다

        // 1. 프로그래스바를 숨긴다
        turnProgressbar(false);

        // 2. 리스트가 없으면 메세지를 보여준다
        if (stations == null) {
            textViewResult.setText("데이터 없음");
            return;
        } else if (stations.isEmpty()) {
            textViewResult.setText("결과 없음");
            return;
        }

        // 3. 리스트를 문자열로 표현한다
        StringBuilder sb = new StringBuilder();

        for (Station station : stations) {
            sb.append("수소차 충전소 여부: ").append(station.getSupportHydrogen()).append("\n");
            sb.append("전기차 충전소 여부: ").append(station.getSupportElectric()).append("\n");
            sb.append("전화번호: ").append(station.getPhone()).append("\n");
            sb.append("주유소명: ").append(station.getName()).append("\n\n");
        }

        // 4. 텍스트뷰에 문자열을 표시한다
        textViewResult.setText(sb.toString());
    }

    private void searchStations(String query) {

        // 주유소 검색을 수행한다

        // 1. 검색된 주유소를 획득한다
        List<Station> filtered = filterStations(query);

        // 2. 검색된 주유소를 표시한다
        showStations(filtered);

        // 3. 최근 검색어를 업데이트한다
        if (!query.isEmpty()) {
            recentQueries.add(0, query);
            adapter.notifyDataSetChanged();
        }
    }

    private List<Station> filterStations(String query) {

        // 검색어를 포함하는 주유소만 필터링하여 리턴한다

        if (stations == null) {
            return null;
        }

        List<Station> filtered = new ArrayList<>();

        for (Station station : stations) {
            if (station.getName().contains(query)) {
                filtered.add(station);
            }
        }

        return filtered;
    }

}





