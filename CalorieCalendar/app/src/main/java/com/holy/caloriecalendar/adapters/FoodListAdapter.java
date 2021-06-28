package com.holy.caloriecalendar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.holy.caloriecalendar.R;
import com.holy.caloriecalendar.models.Food;

import java.util.List;
import java.util.Locale;


// 음식 정보를 리스트뷰로 표시할 어댑터

public class FoodListAdapter extends BaseAdapter {

    private final List<Food> foodList;      // 음식 정보 리스트

    public FoodListAdapter(List<Food> foodList) {
        this.foodList = foodList;
    }

    @Override
    public int getCount() {
        return foodList.size();
    }

    @Override
    public Object getItem(int position) {
        return foodList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // convertView 가 없으면 새로 inflate 한다
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_food, parent, false);
        }

        // convertView 에 음식 이름과 칼로리 정보를 표시한다.
        TextView titleText = convertView.findViewById(R.id.txt_food_title);
        TextView kcalText = convertView.findViewById(R.id.txt_food_kcal);

        Food model = foodList.get(position);
        titleText.setText(model.getTitle());

        String strKcal = String.format(Locale.getDefault(),
                "%d kcal", model.getKcals());
        kcalText.setText(strKcal);

        return convertView;
    }
}
