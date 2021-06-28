package com.holy.caloriecalendar;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.holy.caloriecalendar.adapters.FoodAdapter;
import com.holy.caloriecalendar.adapters.FoodListAdapter;
import com.holy.caloriecalendar.helpers.SQLiteHelper;
import com.holy.caloriecalendar.models.Food;

import java.util.List;

@SuppressWarnings("deprecation")
public class SearchableActivity extends ListActivity implements AdapterView.OnItemClickListener {

    private List<Food> mFoodList;       // 식품정보 리스트 (검색 제안 리스트뷰에 표시된다)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(android.R.layout.list_content);

        // 인텐트로부터 액션을 확인한다
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // 유저가 쿼리문을 입력한 경우: 쿼리로 검색을 수행하고 결과를 보여준다
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            // 유저가 제안을 선택한 경우: 선택된 식품정보를 반환값으로 MainActivity 를 시작한다
            int selectedFoodId = Integer.parseInt(intent.getDataString());
            startHomeActivity(selectedFoodId);
        }

        getListView().setOnItemClickListener(this);
    }

    // 아이템 클릭 리스너

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // 유저가 제안된 식품정보를 선택함 : 선택된 식품정보를 반환값으로 MainActivity 를 시작한다
        int selectedFoodId = mFoodList.get(position).getId();
        startHomeActivity(selectedFoodId);
    }

    // 쿼리문으로 검색을 수행하고 검색 제안 리스트뷰에 표시한다

    private void doMySearch(String query) {

        mFoodList = SQLiteHelper.getInstance(this).getAllFoods();

        FoodListAdapter adapter = new FoodListAdapter(mFoodList);
        setListAdapter(adapter);
    }

    // 선택된 식품정보를 반환값으로 MainActivity 를 시작한다

    private void startHomeActivity(int foodId) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(MainActivity.EXTRA_SEARCHED_FOOD_ID, foodId);
        startActivity(intent);

    }


}