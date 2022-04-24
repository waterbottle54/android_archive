package com.penelope.imageclassificationapp.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TranslationJsonParser {

    public static String parse(JSONObject jsonObject) throws JSONException {

        JSONArray texts = jsonObject.getJSONArray("translated_text");
        if (texts.length() == 0) {
            return null;
        }

        JSONArray text = texts.getJSONArray(0);
        if (text.length() == 0) {
            return null;
        }

        return text.getString(0);
    }

}
