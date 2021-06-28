package com.myapp.workcalendar.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.myapp.workcalendar.models.Work;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 4;

    // 일 테이블의 정보
    public static final String TABLE_WORKS = "works";
    public static final String COLUMN_WORK_ID = "id";
    public static final String COLUMN_WORK_TITLE = "title";
    public static final String COLUMN_WORK_HOURS = "hours";
    public static final String COLUMN_WORK_HOURLY_WAGE = "hourlyWage";
    public static final String COLUMN_WORK_YEAR = "year";
    public static final String COLUMN_WORK_MONTH = "month";
    public static final String COLUMN_WORK_DATE = "date";

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

        // 초기화 : 데이터베이스에 식당 테이블을 생성한다.
        String CREATE_WORKS_TABLE = "CREATE TABLE " + TABLE_WORKS +
                "(" +
                COLUMN_WORK_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_WORK_TITLE + " TEXT NOT NULL, " +
                COLUMN_WORK_HOURS + " INTEGER NOT NULL, " +
                COLUMN_WORK_HOURLY_WAGE + " INTEGER NOT NULL, " +
                COLUMN_WORK_YEAR + " INTEGER NOT NULL, " +
                COLUMN_WORK_MONTH + " INTEGER NOT NULL, " +
                COLUMN_WORK_DATE + " INTEGER NOT NULL" +
                ")";
        db.execSQL(CREATE_WORKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORKS);
            onCreate(db);
        }
    }

    // 정보 추가

    public void addWork(Work work) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_WORK_TITLE, work.getTitle());
            values.put(COLUMN_WORK_HOURS, work.getHours());
            values.put(COLUMN_WORK_HOURLY_WAGE, work.getHourlyWage());
            values.put(COLUMN_WORK_YEAR, work.getYear());
            values.put(COLUMN_WORK_MONTH, work.getMonth());
            values.put(COLUMN_WORK_DATE, work.getDate());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_WORKS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 식당 정보 읽기

    public List<Work> getWorkByDate(int year, int month, int date) {

        List<Work> workList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_WORKS =
                "SELECT * FROM " + TABLE_WORKS
                        + " WHERE " + COLUMN_WORK_YEAR + " = " + year
                        + " AND " + COLUMN_WORK_MONTH + " = " + month
                        + " AND " + COLUMN_WORK_DATE + " = " + date;
        Cursor cursor = db.rawQuery(SELECT_WORKS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_WORK_ID));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_WORK_TITLE));
                    int hours = cursor.getInt(cursor.getColumnIndex(COLUMN_WORK_HOURS));
                    int hourlyWage = cursor.getInt(cursor.getColumnIndex(COLUMN_WORK_HOURLY_WAGE));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Work work = new Work(id, title, hours, hourlyWage, year, month, date);
                    workList.add(work);

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
        return workList;
    }

    public int getMonthlyWage(int year, int month) {

        int monthlyWage = 0;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_WORKS =
                "SELECT * FROM " + TABLE_WORKS
                        + " WHERE " + COLUMN_WORK_YEAR + " = " + year
                        + " AND " + COLUMN_WORK_MONTH + " = " + month;
        Cursor cursor = db.rawQuery(SELECT_WORKS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int hours = cursor.getInt(cursor.getColumnIndex(COLUMN_WORK_HOURS));
                    int hourlyWage = cursor.getInt(cursor.getColumnIndex(COLUMN_WORK_HOURLY_WAGE));

                    // 월간 급여 총액을 계산한다.
                    monthlyWage += (hours * hourlyWage);

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

        return monthlyWage;
    }

}
