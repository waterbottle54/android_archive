package com.penelope.imageclassificationapp.api;

import android.util.Log;

import androidx.annotation.WorkerThread;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class TranslationApi {

    public static final String URL_FORMAT = "https://dapi.kakao.com/v2/translation/translate?src_lang={ARG_SRC_LANG}&target_lang={ARG_TARGET_LANG}&query={ARG_QUERY}";
    public static final String ARG_SRC_LANG = "{ARG_SRC_LANG}";
    public static final String ARG_TARGET_LANG = "{ARG_TARGET_LANG}";
    public static final String ARG_QUERY = "{ARG_QUERY}";

    public static final String API_KEY = "933f56f6a693aa2900c96222f55f7304";


    @WorkerThread
    public static String get(String query) {

        try {
            String strUrl = URL_FORMAT
                    .replace(ARG_SRC_LANG, "en")
                    .replace(ARG_TARGET_LANG, "kr")
                    .replace(ARG_QUERY, query);

            String strAuthorization = "KakaoAK " + API_KEY;

            URL url = new URL(strUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", strAuthorization);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            JSONObject response = new JSONObject(sb.toString());
            return TranslationJsonParser.parse(response);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
