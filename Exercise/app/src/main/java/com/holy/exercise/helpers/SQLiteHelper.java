package com.holy.exercise.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.holy.exercise.models.Record;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 1;

    // 일 테이블의 정보
    public static final String TABLE_RECORDS = "records";
    public static final String COLUMN_RECORD_ID = "id";
    public static final String COLUMN_RECORD_TITLE = "title";
    public static final String COLUMN_RECORD_DATE = "date";
    public static final String COLUMN_RECORD_SECONDS = "seconds";

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

        // 초기화 : 데이터베이스에 테이블을 생성한다.
        String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS +
                "(" +
                COLUMN_RECORD_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_RECORD_TITLE + " TEXT NOT NULL, " +
                COLUMN_RECORD_DATE + " TEXT NOT NULL, " +
                COLUMN_RECORD_SECONDS + " INTEGER NOT NULL" +
                ")";
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
            onCreate(db);
        }
    }

    // 정보 추가

    public void addRecord(Record record) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_RECORD_TITLE, record.getTitle());
            values.put(COLUMN_RECORD_DATE, record.getDate().toString());
            values.put(COLUMN_RECORD_SECONDS, record.getSeconds());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_RECORDS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 정보 읽기

    public List<Record> getRecordByDate(int year, int month, int dayOfMonth) {

        List<Record> recordList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        LocalDate date = LocalDate.of(year, month, dayOfMonth);
        String SELECT_RECORDS =
                "SELECT * FROM " + TABLE_RECORDS
                        + " WHERE " + COLUMN_RECORD_DATE + " = '" + date.toString() + "'";
        Cursor cursor = db.rawQuery(SELECT_RECORDS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_RECORD_ID));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_TITLE));
                    String strDate = cursor.getString(cursor.getColumnIndex(COLUMN_RECORD_DATE));
                    int seconds = cursor.getInt(cursor.getColumnIndex(COLUMN_RECORD_SECONDS));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Record record = new Record(id, title, LocalDate.parse(strDate), seconds);
                    recordList.add(record);

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
        return recordList;
    }

}
