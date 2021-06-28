package com.good.todoapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.good.todoapp.models.Duty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 1;

    // 할일 테이블의 정보
    public static final String TABLE_DUTIES = "duties";
    public static final String COLUMN_DUTY_ID = "id";
    public static final String COLUMN_DUTY_NAME = "name";
    public static final String COLUMN_DUTY_COMPLETED = "completed";
    public static final String COLUMN_DUTY_IMPORTANT = "importance";
    public static final String COLUMN_DUTY_DATE = "date";

    // 데이터베이스 헬퍼 객체
    private static DatabaseHelper instance;

    // 데이터베이스 헬퍼 객체 구하기.
    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // 생성자
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 초기화 : 데이터베이스에 할일 테이블을 생성한다.
        // Duty 클래스의 멤버와 일치하도록 생성한다.
        String CREATE_DUTIES_TABLE = "CREATE TABLE " + TABLE_DUTIES +
                "(" +
                COLUMN_DUTY_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_DUTY_NAME + " TEXT NOT NULL, " +
                COLUMN_DUTY_COMPLETED + " INTEGER NOT NULL, " +
                COLUMN_DUTY_IMPORTANT + " INTEGER NOT NULL, " +
                COLUMN_DUTY_DATE + " INTEGER NOT NULL" +
                ")";

        db.execSQL(CREATE_DUTIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DUTIES);
            onCreate(db);
        }
    }

    public void addDuty(Duty duty) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 할 일의 정보들을 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_DUTY_NAME, duty.getName());
            values.put(COLUMN_DUTY_COMPLETED, duty.isCompleted());
            values.put(COLUMN_DUTY_IMPORTANT, duty.isImportant());
            values.put(COLUMN_DUTY_DATE, duty.getDate().toEpochDay());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_DUTIES, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public List<Duty> getAllDuties() {

        List<Duty> dutyList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 할일 테이블을 가리키는 커서를 가져온다.
        String SELECT_ALL_DUTIES = "SELECT * FROM " + TABLE_DUTIES + " ORDER BY " + COLUMN_DUTY_DATE;
        Cursor cursor = db.rawQuery(SELECT_ALL_DUTIES, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 할일 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_DUTY_ID));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_DUTY_NAME));
                    boolean isCompleted = (cursor.getInt(cursor.getColumnIndex(COLUMN_DUTY_COMPLETED)) == 1);
                    boolean isImportant = (cursor.getInt(cursor.getColumnIndex(COLUMN_DUTY_IMPORTANT)) == 1);
                    long epochDays = cursor.getLong(cursor.getColumnIndex(COLUMN_DUTY_DATE));
                    LocalDate date = LocalDate.ofEpochDay(epochDays);

                    // 할일 정보들로 할일 객체를 만들어 리스트에 추가한다.
                    Duty duty = new Duty(id, name, isCompleted, date, isImportant);
                    dutyList.add(duty);

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
        return dutyList;
    }

    public void deleteDuty(int id) {

        // 쓰기용 DB 열기
        SQLiteDatabase db = getWritableDatabase();

        // 입력 시작
        db.beginTransaction();
        try {
            // 데이터베이스에서 주어진 id를 가진 할일을 삭제한다.
            db.delete(TABLE_DUTIES,
                    COLUMN_DUTY_ID + "=?",
                    new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteCompletedDuties() {

        // 쓰기용 DB 얻기
        SQLiteDatabase db = getWritableDatabase();

        // 입력 시작
        db.beginTransaction();
        try {
            // 데이터베이스에서 모든 완료된 할일을 삭제한다.
            db.delete(TABLE_DUTIES,
                    COLUMN_DUTY_COMPLETED + "=?",
                    new String[]{String.valueOf(1)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllDuties() {

        // 쓰기용 DB 얻기
        SQLiteDatabase db = getWritableDatabase();

        // 입력 시작
        db.beginTransaction();
        try {
            // 데이터베이스에서 모든 할일을 삭제한다.
            db.delete(TABLE_DUTIES, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void updateDuty(int id, boolean isCompleted) {

        // 쓰기용 DB 얻기
        SQLiteDatabase db = this.getWritableDatabase();

        // 업데이트를 위해 values 를 만들고 완료 상태만 입력한다.
        ContentValues values = new ContentValues();
        values.put(COLUMN_DUTY_COMPLETED, isCompleted ? 1 : 0);

        // 데이터베이스의 id 위치에 values 를 입력하여 업데이트한다.
        db.update(TABLE_DUTIES, values, COLUMN_DUTY_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

}
