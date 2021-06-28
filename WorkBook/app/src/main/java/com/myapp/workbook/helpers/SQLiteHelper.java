package com.myapp.workbook.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.myapp.workbook.models.Word;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 1;

    // 단어 테이블의 정보
    public static final String TABLE_WORDS = "words";
    public static final String COLUMN_WORD_ID = "id";
    public static final String COLUMN_WORD_SPELLING = "spelling";
    public static final String COLUMN_WORD_MEANING = "meaning";

    // 데이터베이스 헬퍼 객체
    private static SQLiteHelper instance;

    // 데이터베이스 헬퍼 객체 구하기.
    public static synchronized SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SQLiteHelper(context.getApplicationContext());
        }
        return instance;
    }

    // 생성자
    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 초기화 : 데이터베이스에 단어 테이블을 생성한다.
        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS +
                "(" +
                COLUMN_WORD_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_WORD_SPELLING + " TEXT NOT NULL, " +
                COLUMN_WORD_MEANING + " TEXT NOT NULL" +
                ")";
        db.execSQL(CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
            onCreate(db);
        }
    }

    // 단어 정보 추가

    public void addWord(Word word) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_WORD_SPELLING, word.getSpelling());
            values.put(COLUMN_WORD_MEANING, word.getMeaning());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_WORDS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 단어 정보 읽기

    public List<Word> getAllWords() {

        List<Word> wordList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_WORDS = "SELECT * FROM " + TABLE_WORDS;
        Cursor cursor = db.rawQuery(SELECT_WORDS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_WORD_ID));
                    String spelling = cursor.getString(cursor.getColumnIndex(COLUMN_WORD_SPELLING));
                    String meaning = cursor.getString(cursor.getColumnIndex(COLUMN_WORD_MEANING));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Word word = new Word(id, spelling, meaning);
                    wordList.add(word);

                    // 테이블 끝에 도달할 때까지 실시한다.
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return wordList;
    }

    // 단어 정보 검색 : 아이디로

    public Word getWord(int id) {

        Word word = null;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_WORD = "SELECT * FROM " + TABLE_WORDS
                + " WHERE " + COLUMN_WORD_ID + " = " + id;
        Cursor cursor = db.rawQuery(SELECT_WORD, null);

        try {
            if (cursor.moveToFirst()) {

                // 커서를 움직이면서 테이블의 정보들을 가져온다.
                String spelling = cursor.getString(cursor.getColumnIndex(COLUMN_WORD_SPELLING));
                String meaning = cursor.getString(cursor.getColumnIndex(COLUMN_WORD_MEANING));

                // 정보로 객체를 만들어 리스트에 추가한다.
                word = new Word(id, spelling, meaning);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return word;
    }

    // 단어 정보 검색 : 철자로

    public Word getWordBySpelling(String spelling) {

        Word word = null;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_WORD = "SELECT * FROM " + TABLE_WORDS
                + " WHERE " + COLUMN_WORD_SPELLING + " = '" + spelling + "'";
        Cursor cursor = db.rawQuery(SELECT_WORD, null);

        try {
            if (cursor.moveToFirst()) {

                // 커서를 움직이면서 테이블의 정보들을 가져온다.
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_WORD_ID));
                String meaning = cursor.getString(cursor.getColumnIndex(COLUMN_WORD_MEANING));

                // 정보로 객체를 만들어 리스트에 추가한다.
                word = new Word(id, spelling, meaning);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return word;
    }

}
