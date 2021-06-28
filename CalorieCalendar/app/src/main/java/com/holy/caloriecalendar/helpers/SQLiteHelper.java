package com.holy.caloriecalendar.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.holy.caloriecalendar.models.Food;
import com.holy.caloriecalendar.models.Intake;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("unused")
public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";         // 데이터베이스 이름
    // 현재 버전
    private static final int DATABASE_VERSION = 10;                 // 데이터베이스 버전

    // 칼로리 섭취정보 테이블의 정보
    public static final String TABLE_INTAKES = "intakes";           // 섭취정보 테이블 이름
    public static final String COLUMN_INTAKE_ID = "id";             // 섭취정보 고유키
    public static final String COLUMN_INTAKE_KCALS = "kcals";       // 섭취 칼로리량
    public static final String COLUMN_INTAKE_TITLE = "title";       // 섭취 음식명
    public static final String COLUMN_INTAKE_YEAR = "year";         // 섭취한 연도
    public static final String COLUMN_INTAKE_MONTH = "month";       // 섭취한 달
    public static final String COLUMN_INTAKE_DAY_OF_MONTH = "dayOfMonth";   // 섭취한 일자
    public static final String COLUMN_INTAKE_HOUR = "hour";         // 섭취한 시각
    public static final String COLUMN_INTAKE_MINUTE = "minute";     // 섭취한 분

    // 식품정보 테이블의 정보
    public static final String TABLE_FOODS = "foods";               // 식품정보 테이블 이름
    public static final String COLUMN_FOOD_ID = "id";               // 식품정보 고유키
    public static final String COLUMN_FOOD_TITLE = "title";         // 식품 이름
    public static final String COLUMN_FOOD_KCALS = "kcals";         // 식품 칼로리량

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

        // 초기화 : 데이터베이스에 수분섭취 테이블을 생성한다.
        String CREATE_INTAKE_TABLE = "CREATE TABLE " + TABLE_INTAKES +
                "(" +
                COLUMN_INTAKE_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_INTAKE_KCALS + " INTEGER NOT NULL, " +
                COLUMN_INTAKE_TITLE + " TEXT NOT NULL, " +
                COLUMN_INTAKE_YEAR + " INTEGER NOT NULL, " +
                COLUMN_INTAKE_MONTH + " INTEGER NOT NULL, " +
                COLUMN_INTAKE_DAY_OF_MONTH + " INTEGER NOT NULL, " +
                COLUMN_INTAKE_HOUR + " INTEGER NOT NULL, " +
                COLUMN_INTAKE_MINUTE + " INTEGER NOT NULL" +
                ")";
        db.execSQL(CREATE_INTAKE_TABLE);

        // 초기화 : 데이터베이스에 식품 테이블을 생성한다.
        String CREATE_FOOD_TABLE = "CREATE TABLE " + TABLE_FOODS +
                "(" +
                COLUMN_FOOD_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_FOOD_TITLE + " TEXT NOT NULL, " +
                COLUMN_FOOD_KCALS + " INTEGER NOT NULL" +
                ")";
        db.execSQL(CREATE_FOOD_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_INTAKES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOODS);
            onCreate(db);
        }
    }

    // 수분섭취 정보 추가

    public void addIntake(Intake intake) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_INTAKE_KCALS, intake.getKcals());
            values.put(COLUMN_INTAKE_TITLE, intake.getTitle());
            values.put(COLUMN_INTAKE_YEAR, intake.getYear());
            values.put(COLUMN_INTAKE_MONTH, intake.getMonth());
            values.put(COLUMN_INTAKE_DAY_OF_MONTH, intake.getDayOfMonth());
            values.put(COLUMN_INTAKE_HOUR, intake.getHour());
            values.put(COLUMN_INTAKE_MINUTE, intake.getMinute());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_INTAKES, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 수분섭취 정보 읽기

    public List<Intake> getIntakeByDate(int year, int month, int dayOfMonth) {

        List<Intake> intakeList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_INTAKES =
                "SELECT * FROM " + TABLE_INTAKES
                        + " WHERE " + COLUMN_INTAKE_YEAR + " = " + year
                        + " AND " + COLUMN_INTAKE_MONTH + " = " + month
                        + " AND " + COLUMN_INTAKE_DAY_OF_MONTH + " = " + dayOfMonth
                        + " ORDER BY "
                        + COLUMN_INTAKE_HOUR + " ASC, "
                        + COLUMN_INTAKE_MINUTE + " ASC";
        Cursor cursor = db.rawQuery(SELECT_INTAKES, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_INTAKE_ID));
                    int milliLiters = cursor.getInt(cursor.getColumnIndex(COLUMN_INTAKE_KCALS));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_INTAKE_TITLE));
                    int hour = cursor.getInt(cursor.getColumnIndex(COLUMN_INTAKE_HOUR));
                    int minute = cursor.getInt(cursor.getColumnIndex(COLUMN_INTAKE_MINUTE));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Intake intake = new Intake(id, title, milliLiters, year, month, dayOfMonth, hour, minute);
                    intakeList.add(intake);

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
        return intakeList;
    }

    // 일간 수분 섭취량 조회

    public int getDailyIntake(int year, int month, int dayOfMonth) {

        // 일간 수분 섭취량
        int totalIntakeInMilliLiters = 0;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_WORKS =
                "SELECT * FROM " + TABLE_INTAKES
                        + " WHERE " + COLUMN_INTAKE_YEAR + " = " + year
                        + " AND " + COLUMN_INTAKE_MONTH + " = " + month
                        + " AND " + COLUMN_INTAKE_DAY_OF_MONTH + " = " + dayOfMonth;
        Cursor cursor = db.rawQuery(SELECT_WORKS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int milliLiters = cursor.getInt(cursor.getColumnIndex(COLUMN_INTAKE_KCALS));

                    // 일간 수분 섭취량을 누적한다.
                    totalIntakeInMilliLiters += milliLiters;

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

        return totalIntakeInMilliLiters;
    }

    // 섭취정보 변경

    public void updateIntake(Intake intake) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_INTAKE_TITLE, intake.getTitle());
        values.put(COLUMN_INTAKE_KCALS, intake.getKcals());

        db.update(TABLE_INTAKES, values, COLUMN_INTAKE_ID + " =? ",
                new String[]{String.valueOf(intake.getId())});
    }

    // 섭취정보 삭제

    public void deleteIntake(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_INTAKES, COLUMN_INTAKE_ID + " =? ",
                    new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }


    // 식품 정보 추가

    public void addFood(Food food) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_FOOD_TITLE, food.getTitle());
            values.put(COLUMN_FOOD_KCALS, food.getKcals());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_FOODS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 모든 식품 조회

    public List<Food> getAllFoods() {

        List<Food> foodList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_FOODS =
                "SELECT * FROM " + TABLE_FOODS
                        + " ORDER BY "
                        + COLUMN_FOOD_KCALS + " ASC";
        Cursor cursor = db.rawQuery(SELECT_FOODS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_ID));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_FOOD_TITLE));
                    int kcals = cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_KCALS));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Food food = new Food(id, title, kcals);
                    foodList.add(food);

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
        return foodList;
    }

    // 검색된 식품 조회

    public List<Food> searchFoods(String startWith) {

        List<Food> foodList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_FOODS =
                "SELECT * FROM " + TABLE_FOODS +
                        " WHERE " + COLUMN_FOOD_TITLE + " LIKE '" + startWith + "%'";
        Cursor cursor = db.rawQuery(SELECT_FOODS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_ID));
                    String title = cursor.getString(cursor.getColumnIndex(COLUMN_FOOD_TITLE));
                    int kcals = cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_KCALS));

                    // 정보로 객체를 만들어 리스트에 추가한다.
                    Food food = new Food(id, title, kcals);
                    foodList.add(food);

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
        return foodList;
    }

    // 식품 변경

    public void updateFood(Food food) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_FOOD_TITLE, food.getTitle());
        values.put(COLUMN_FOOD_KCALS, food.getKcals());

        db.update(TABLE_FOODS, values, COLUMN_FOOD_ID + " =? ",
                new String[]{String.valueOf(food.getId())});

    }

    // 식품 삭제

    public void deleteFood(int id) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_FOODS, COLUMN_FOOD_ID + " =? ",
                    new String[]{String.valueOf(id)});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 아이디로 식품 검색

    public Food getFood(int id) {

        Food food = null;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_FOODS =
                "SELECT * FROM " + TABLE_FOODS
                        + " WHERE " + COLUMN_FOOD_ID + " = " + id;
        Cursor cursor = db.rawQuery(SELECT_FOODS, null);

        try {
            if (cursor.moveToFirst()) {
                // 커서를 움직이면서 테이블의 정보들을 가져온다.
                String title = cursor.getString(cursor.getColumnIndex(COLUMN_FOOD_TITLE));
                int kcals = cursor.getInt(cursor.getColumnIndex(COLUMN_FOOD_KCALS));

                // 정보로 객체를 만든다
                food = new Food(id, title, kcals);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return food;
    }


}

