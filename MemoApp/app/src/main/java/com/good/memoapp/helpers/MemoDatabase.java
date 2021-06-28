package com.good.memoapp.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.good.memoapp.models.Memo;

import java.util.ArrayList;
import java.util.List;

public class MemoDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "db";
    private static final int DB_VERSION = 1;

    // 메모 테이블의 이름
    public static final String TABLE_MEMOS_NAME = "memos";
    // 메모 테이블의 컬럼 (번호, 제목, 내용)
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";

    // 생성자에서 데이터베이스 생성하기
    public MemoDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 메모 Table 생성하기
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_MEMO_TABLE =
                "CREATE TABLE " + TABLE_MEMOS_NAME +
                        "("
                            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COLUMN_TITLE + " TEXT NOT NULL,"
                            + COLUMN_CONTENT + " TEXT NOT NULL"
                        + ")";

        sqLiteDatabase.execSQL(CREATE_MEMO_TABLE);
    }

    // notesdb 데이터베이스를 변경할 때. 스키마를 변경해야 할 때
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        // 저장된 DB가 현재 버전이 아니면, 삭제한 후 다시 생성한다.
        if (sqLiteDatabase.getVersion() != DB_VERSION) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMOS_NAME);
            onCreate(sqLiteDatabase);
        }
    }

    // SELECT ALL : 모든 메모 검색하기
    public List<Memo> getAllMemos() {

        List<Memo> memos = new ArrayList<>();

        // SQLite DB 에서 레코드 검색한 다음 memos 에 저장하기
        String sql = "SELECT * FROM " + TABLE_MEMOS_NAME;
        SQLiteDatabase db = this.getReadableDatabase(); // DB open
        Cursor cursor = db.rawQuery(sql, null); // 검색된 레코드 집합에서 현재 레코드를 가리키는 포인터

        if(cursor.moveToFirst()) {
            do { // 커서가 가리키는 레코드의 컬럼값을 읽어서 Note 객체 생성
                Memo memo = new Memo(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));
                memos.add(memo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return memos;
    }

    // INSERT
    public long insertMemo(Memo memo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        long id = db.insert(TABLE_MEMOS_NAME, null, values);
        db.close();
        return id;
    }

    // DElETE
    public void deleteMemo(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_MEMOS_NAME,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    // UPDATE
    public void updateMemo(long id, Memo memo) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, memo.getTitle());
        values.put(COLUMN_CONTENT, memo.getContent());
        db.update(TABLE_MEMOS_NAME, values,
                COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});

        db.close();
    }

    // SELECT : 한 건 검색
    public Memo getMemo(long id) {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MEMOS_NAME,
                new String[]{COLUMN_ID, COLUMN_TITLE, COLUMN_CONTENT},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)},
                null,null,null,null);

        if(cursor != null) cursor.moveToFirst();

        Memo memo = new Memo(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)),
                cursor.getString(cursor.getColumnIndex(COLUMN_CONTENT)));

        cursor.close();
        db.close();
        return memo;
    }
}









