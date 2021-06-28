package com.holy.radiorate.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.holy.radiorate.models.NuclearPlant;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 1;

    // 발전소 테이블의 정보
    public static final String TABLE_NUCLEAR_PLANTS = "nuclearPlants";
    public static final String COLUMN_NUCLEAR_PLANT_ID = "id";
    public static final String COLUMN_NUCLEAR_PLANT_NAME = "name";
    public static final String COLUMN_NUCLEAR_PLANT_LATITUDE = "latitude";
    public static final String COLUMN_NUCLEAR_PLANT_LONGITUDE = "longitude";
    public static final String COLUMN_NUCLEAR_PLANT_ADDRESS = "address";

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

        // 초기화 : 데이터베이스에 발전소 테이블을 생성한다.
        String CREATE_TABLE_NUCLEAR_PLANTS = "CREATE TABLE " + TABLE_NUCLEAR_PLANTS +
                "(" +
                COLUMN_NUCLEAR_PLANT_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NUCLEAR_PLANT_NAME + " TEXT NOT NULL, " +
                COLUMN_NUCLEAR_PLANT_LATITUDE + " NUMBER NOT NULL, " +
                COLUMN_NUCLEAR_PLANT_LONGITUDE + " NUMBER NOT NULL, " +
                COLUMN_NUCLEAR_PLANT_ADDRESS + " NUMBER NOT NULL " +
                ")";
        db.execSQL(CREATE_TABLE_NUCLEAR_PLANTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NUCLEAR_PLANTS);
            onCreate(db);
        }
    }

    // 발전소 삽입

    public void addNuclearPlant(NuclearPlant batteryStation) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_NUCLEAR_PLANT_ID, batteryStation.getId());
            values.put(COLUMN_NUCLEAR_PLANT_NAME, batteryStation.getName());
            values.put(COLUMN_NUCLEAR_PLANT_LATITUDE, batteryStation.getLatitude());
            values.put(COLUMN_NUCLEAR_PLANT_LONGITUDE, batteryStation.getLongitude());
            values.put(COLUMN_NUCLEAR_PLANT_ADDRESS, batteryStation.getAddress());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_NUCLEAR_PLANTS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 모든 발전소 읽기

    public List<NuclearPlant> getAllNuclearPlants() {

        List<NuclearPlant> nuclearPlantList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_NUCLEAR_PLANTS = "SELECT * FROM " + TABLE_NUCLEAR_PLANTS;
        Cursor cursor = db.rawQuery(SELECT_NUCLEAR_PLANTS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    String id = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_ID));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_NAME));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_LONGITUDE));
                    String address = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_ADDRESS));

                    NuclearPlant nuclearPlant = new NuclearPlant(
                            id,
                            name,
                            latitude,
                            longitude,
                            address
                    );
                    nuclearPlantList.add(nuclearPlant);

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
        return nuclearPlantList;
    }

    // 모든 발전소 읽기 (쿼리 주소 포함)

    public List<NuclearPlant> getNuclearPlantsIncludeAddress(String query) {

        List<NuclearPlant> nuclearPlantList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_NUCLEAR_PLANTS = "SELECT * FROM " + TABLE_NUCLEAR_PLANTS
                + " WHERE " + COLUMN_NUCLEAR_PLANT_ADDRESS + " LIKE '%" + query + "%'";
        Cursor cursor = db.rawQuery(SELECT_NUCLEAR_PLANTS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    String id = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_ID));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_NAME));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_LONGITUDE));
                    String address = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_ADDRESS));

                    NuclearPlant nuclearPlant = new NuclearPlant(
                            id,
                            name,
                            latitude,
                            longitude,
                            address
                    );
                    nuclearPlantList.add(nuclearPlant);

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
        return nuclearPlantList;
    }

    // 발전소 읽기 (id 값으로)

    public NuclearPlant getNuclearPlant(String id) {

        NuclearPlant nuclearPlant = null;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_NUCLEAR_PLANT = "SELECT * FROM " + TABLE_NUCLEAR_PLANTS
                + " WHERE " + COLUMN_NUCLEAR_PLANT_ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(SELECT_NUCLEAR_PLANT, null);

        try {
            if (cursor.moveToFirst()) {

                // 커서를 움직이면서 테이블의 정보들을 가져온다.
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_NAME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_LONGITUDE));
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_NUCLEAR_PLANT_ADDRESS));

                nuclearPlant = new NuclearPlant(
                        id,
                        name,
                        latitude,
                        longitude,
                        address
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return nuclearPlant;
    }

}
