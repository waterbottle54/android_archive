package com.holy.batterystation.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.holy.batterystation.models.BatteryStation;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스 이름
    private static final String DATABASE_NAME = "database";
    // 현재 버전
    private static final int DATABASE_VERSION = 1;

    // 충전소 테이블의 정보
    public static final String TABLE_BATTERY_STATIONS = "batteryStations";
    public static final String COLUMN_BATTERY_STATION_ID = "id";
    public static final String COLUMN_BATTERY_STATION_ADDRESS = "address";
    public static final String COLUMN_BATTERY_STATION_CHARGE_TYPE = "chargeType";
    public static final String COLUMN_BATTERY_STATION_CP_STATUS = "cpStatus";
    public static final String COLUMN_BATTERY_STATION_CP_TYPE = "cpType";
    public static final String COLUMN_BATTERY_STATION_NAME = "name";
    public static final String COLUMN_BATTERY_STATION_LATITUDE = "latitude";
    public static final String COLUMN_BATTERY_STATION_LONGITUDE = "longitude";

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

        // 초기화 : 데이터베이스에 충전소 테이블을 생성한다.
        String CREATE_BATTERY_STATION_TABLE = "CREATE TABLE " + TABLE_BATTERY_STATIONS +
                "(" +
                COLUMN_BATTERY_STATION_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_BATTERY_STATION_ADDRESS + " TEXT NOT NULL, " +
                COLUMN_BATTERY_STATION_CHARGE_TYPE + " INTEGER NOT NULL, " +
                COLUMN_BATTERY_STATION_CP_STATUS + " INTEGER NOT NULL, " +
                COLUMN_BATTERY_STATION_CP_TYPE + " INTEGER NOT NULL, " +
                COLUMN_BATTERY_STATION_NAME + " TEXT NOT NULL, " +
                COLUMN_BATTERY_STATION_LATITUDE + " NUMBER NOT NULL, " +
                COLUMN_BATTERY_STATION_LONGITUDE + " NUMBER NOT NULL " +
                ")";
        db.execSQL(CREATE_BATTERY_STATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // 기존의 데이터베이스 버전이 현재와 다르면 테이블을 지우고 빈 테이블 다시 만들기.
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BATTERY_STATIONS);
            onCreate(db);
        }
    }

    // 충전소 삽입

    public void addBatteryStation(BatteryStation batteryStation) {

        // 쓰기용 DB 를 연다.
        SQLiteDatabase db = getWritableDatabase();

        // DB 입력 시작
        db.beginTransaction();
        try {
            // 정보를 모두 values 객체에 입력한다
            ContentValues values = new ContentValues();
            values.put(COLUMN_BATTERY_STATION_ID, batteryStation.getStationId());
            values.put(COLUMN_BATTERY_STATION_ADDRESS, batteryStation.getAddress());
            values.put(COLUMN_BATTERY_STATION_CHARGE_TYPE, batteryStation.getChargeType());
            values.put(COLUMN_BATTERY_STATION_CP_STATUS, batteryStation.getCpStatus());
            values.put(COLUMN_BATTERY_STATION_CP_TYPE, batteryStation.getCpType());
            values.put(COLUMN_BATTERY_STATION_NAME, batteryStation.getName());
            values.put(COLUMN_BATTERY_STATION_LATITUDE, batteryStation.getLatitude());
            values.put(COLUMN_BATTERY_STATION_LONGITUDE, batteryStation.getLongitude());

            // 데이터베이스에 values 를 입력한다.
            db.insertOrThrow(TABLE_BATTERY_STATIONS, null, values);
            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    // 모든 충전소 읽기

    public List<BatteryStation> getAllBatteryStations() {

        List<BatteryStation> batteryStationList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_BATTERY_STATIONS = "SELECT * FROM " + TABLE_BATTERY_STATIONS;
        Cursor cursor = db.rawQuery(SELECT_BATTERY_STATIONS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_ID));
                    String address = cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY_STATION_ADDRESS));
                    int chargeType = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CHARGE_TYPE));
                    int cpStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CP_STATUS));
                    int cpType = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CP_TYPE));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY_STATION_NAME));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_BATTERY_STATION_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_BATTERY_STATION_LONGITUDE));

                    BatteryStation batteryStation = new BatteryStation(
                            id,
                            address,
                            chargeType,
                            cpStatus,
                            cpType,
                            name,
                            latitude,
                            longitude
                    );
                    batteryStationList.add(batteryStation);

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
        return batteryStationList;
    }

    // 모든 충전소 읽기 (쿼리 주소 포함)

    public List<BatteryStation> getBatteryStationsIncludeAddress(String query) {

        List<BatteryStation> batteryStationList = new ArrayList<>();

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_BATTERY_STATIONS = "SELECT * FROM " + TABLE_BATTERY_STATIONS
                + " WHERE " + COLUMN_BATTERY_STATION_ADDRESS + " LIKE '%" + query + "%'";
        Cursor cursor = db.rawQuery(SELECT_BATTERY_STATIONS, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    // 커서를 움직이면서 테이블의 정보들을 가져온다.
                    int id = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_ID));
                    String address = cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY_STATION_ADDRESS));
                    int chargeType = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CHARGE_TYPE));
                    int cpStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CP_STATUS));
                    int cpType = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CP_TYPE));
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY_STATION_NAME));
                    double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_BATTERY_STATION_LATITUDE));
                    double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_BATTERY_STATION_LONGITUDE));

                    BatteryStation batteryStation = new BatteryStation(
                            id,
                            address,
                            chargeType,
                            cpStatus,
                            cpType,
                            name,
                            latitude,
                            longitude
                    );
                    batteryStationList.add(batteryStation);

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
        return batteryStationList;
    }

    // 충전소 읽기 (id 값으로)

    public BatteryStation getBatteryStation(int stationId) {

        BatteryStation batteryStation = null;

        // 읽기용 DB 열기
        SQLiteDatabase db = getReadableDatabase();

        // 데이터베이스의 테이블을 가리키는 커서를 가져온다.
        String SELECT_BATTERY_STATION = "SELECT * FROM " + TABLE_BATTERY_STATIONS
                + " WHERE " + COLUMN_BATTERY_STATION_ID + " = " + stationId;
        Cursor cursor = db.rawQuery(SELECT_BATTERY_STATION, null);

        try {
            if (cursor.moveToFirst()) {

                // 커서를 움직이면서 테이블의 정보들을 가져온다.
                String address = cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY_STATION_ADDRESS));
                int chargeType = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CHARGE_TYPE));
                int cpStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CP_STATUS));
                int cpType = cursor.getInt(cursor.getColumnIndex(COLUMN_BATTERY_STATION_CP_TYPE));
                String name = cursor.getString(cursor.getColumnIndex(COLUMN_BATTERY_STATION_NAME));
                double latitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_BATTERY_STATION_LATITUDE));
                double longitude = cursor.getDouble(cursor.getColumnIndex(COLUMN_BATTERY_STATION_LONGITUDE));

                batteryStation = new BatteryStation(
                        stationId,
                        address,
                        chargeType,
                        cpStatus,
                        cpType,
                        name,
                        latitude,
                        longitude
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return batteryStation;
    }

    public void clearBatteryStations() {

        // 쓰기용 DB 열기
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            // 데이터베이스에서 모든 정보를 삭제한다.
            db.delete(TABLE_BATTERY_STATIONS,
                    null,
                    null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

}
