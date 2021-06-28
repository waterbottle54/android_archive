package com.holy.caloriecalendar.providers;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.holy.caloriecalendar.R;
import com.holy.caloriecalendar.helpers.SQLiteHelper;
import com.holy.caloriecalendar.helpers.UtilHelper;
import com.holy.caloriecalendar.models.Food;

import java.util.List;

public class FoodSuggestionProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {



        // 유저가 입력한 쿼리 문자열을 획득한다
        String query = uri.getLastPathSegment();

        // DB 에서 쿼리 문자열로 시작하는 음식정보의 명단을 불러온다
        List<Food> foodList = SQLiteHelper.getInstance(getContext()).searchFoods(query);

        // 매트릭스 커서를 만든다 (시스템이 요구하는 포맷대로)
        MatrixCursor matrixCursor = new MatrixCursor(new String[] {
                "_ID",
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                SearchManager.SUGGEST_COLUMN_TEXT_2,
                SearchManager.SUGGEST_COLUMN_ICON_1,    // Icon mustn't have colorControl tint
                SearchManager.SUGGEST_COLUMN_INTENT_DATA
        });

        // 매트릭스 커서에 음식 정보를 삽입한다
        for (int i = 0; i < foodList.size(); i++) {
            Food food = foodList.get(i);
            matrixCursor.addRow(new Object[] {
                    1 + i,
                    food.getTitle(),
                    food.getKcals(),
                    UtilHelper.resourceToUri(getContext(), R.drawable.ic_food_dark).toString(),
                    food.getId()
            });
        }

        // 매트릭스 커서를 리턴한다
        return matrixCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
