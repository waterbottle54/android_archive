package com.holy.watchdog.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.holy.watchdog.models.Criminal;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 3;

    // 범죄자 테이블의 정보
    public static final String TABLE_CRIMINALS = "criminals";
    public static final String COLUMN_CRIMINAL_ID = "id";
    public static final String COLUMN_CRIMINAL_NICKNAME = "nickname";

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

        // 초기화 : 데이터베이스에 범죄자 테이블을 생성한다.
        String CREATE_CRIMINALS_TABLE = "CREATE TABLE " + TABLE_CRIMINALS +
                "(" +
                COLUMN_CRIMINAL_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CRIMINAL_NICKNAME + " TEXT NOT NULL" +
                ")";
        db.execSQL(CREATE_CRIMINALS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CRIMINALS);
            onCreate(db);
        }
    }

    // 범죄자 정보 추가

    public void addCriminal(Criminal criminal) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_CRIMINAL_NICKNAME, criminal.getNickname());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_CRIMINALS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 모든 범죄자 조회

    public List<Criminal> getAllCriminals() {

        List<Criminal> criminalList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_CRIMINALS = "SELECT * FROM " + TABLE_CRIMINALS;
        Cursor cursor = db.rawQuery(SELECT_CRIMINALS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_CRIMINAL_ID));
                    String nickname = cursor.getString(cursor.getColumnIndex(COLUMN_CRIMINAL_NICKNAME));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Criminal criminal = new Criminal(id, nickname);
                    criminalList.add(criminal);

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
        return criminalList;
    }

    // 범죄자 정보 변경

    public void updateCriminal(int id, Criminal criminal) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_CRIMINAL_NICKNAME, criminal.getNickname());

        db.update(TABLE_CRIMINALS, values, COLUMN_CRIMINAL_ID + " =? ",
                new String[] { String.valueOf(id) });
    }

    // 범죄자 정보 삭제

    public void deleteCriminal(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_CRIMINALS, COLUMN_CRIMINAL_ID + " =? ",
                    new String[] { String.valueOf(id) });
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

}

