package com.lotus.vaccinecenters;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    public static final String URL_FORMAT = "https://api.odcloud.kr/api/15077586/v1/centers?page=1&perPage=1000&serviceKey=%s";
    public static final String KEY = "MtVBdtw16Ez1YFEXjvMpB0Zws4eaThA35edKG44NT6A0P3gsproUZBkpaFUUVrHmaPUzkazSncH9iukFY%2BdQfw%3D%3D";

    private final List<Center> centers = new ArrayList<>();             // 예방접종센터 리스트
    private final CenterAdapter adapter = new CenterAdapter(centers);   // 커스텀 어댑터

    private ProgressBar progressBar;    // 로딩 바


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        // 리스트뷰를 어댑터와 연결한다
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        // 리스트뷰의 아이템 클릭 시 맵 띄우기
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Center center = centers.get(position);
            // 인텐트에 위도, 경도, 이름 입력
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.putExtra("latitude", center.getLatitude());
            intent.putExtra("longitude", center.getLongitude());
            intent.putExtra("name", center.getName());
            startActivity(intent);
        });

        // 예방접종센터를 불러온다
        String url = String.format(Locale.getDefault(), URL_FORMAT, KEY);
        new GetCentersAsyncTask().execute(url);
    }


    public class GetCentersAsyncTask extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(String... strings) {

            String strUrl = strings[0];

            try {
                // URL 을 통해 API 를 호출하고 JSON 을 얻는다
                URL url = new URL(strUrl);
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                // JSON 을 onPostExecute 로 리턴한다
                return new JSONObject(sb.toString());

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);

            // 로딩 바를 숨긴다
            progressBar.setVisibility(View.INVISIBLE);

            if (jsonObject == null) {
                Toast.makeText(MainActivity.this,
                        "API 호출 실패", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("TAG", "onPostExecute: " + jsonObject.toString());

            // JSON 을 파싱해서 예방접종센터 목록을 구성한다
            centers.clear();
            try {
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++){
                    JSONObject item = data.getJSONObject(i);
                    String address = item.getString("address");
                    String name = item.getString("centerName");
                    String facility = item.getString("facilityName");
                    String phone = item.getString("phoneNumber");
                    String strLatitude = item.getString("lat");
                    String strLongitude = item.getString("lng");
                    double latitude = Double.parseDouble(strLatitude);
                    double longitude = Double.parseDouble(strLongitude);
                    Center center = new Center(
                            name, facility, address, phone, latitude, longitude
                    );
                    centers.add(center);
                }

                // 리스트뷰를 업데이트한다
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}