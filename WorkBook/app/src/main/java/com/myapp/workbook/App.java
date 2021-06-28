package com.myapp.workbook;

import android.app.Application;

import com.myapp.workbook.helpers.SQLiteHelper;
import com.myapp.workbook.models.Word;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (SQLiteHelper.getInstance(this).getAllWords().isEmpty()) {
            // DB 를 초기화한다 : 영단어 삽입
            initDatabase();
        }
    }

    // DB 를 초기화한다 : 영단어 삽입

    private void initDatabase() {

        // 리소스에서 영단어 정보들을 불러온다.
        String[] spellings_meanings = getResources().getStringArray(R.array.spellings_meanings);

        // 영단어 정보를 DB에 삽입한다.
        for (String spelling_meaning : spellings_meanings) {
            String[] spelling_and_meaning = spelling_meaning.split("_");
            Word word = new Word(spelling_and_meaning[0], spelling_and_meaning[1]);
            SQLiteHelper.getInstance(this).addWord(word);
        }
    }

}
