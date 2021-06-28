package com.holy.caloriecalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.holy.caloriecalendar.adapters.FoodAdapter;
import com.holy.caloriecalendar.helpers.SQLiteHelper;
import com.holy.caloriecalendar.models.Food;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class FoodFragment extends Fragment implements View.OnClickListener {

    private List<Food> mFoodList;       // 식품정보 리스트 (식품정보 리사이클러뷰에 표시된다)
    private FoodAdapter mFoodAdapter;   // 식품정보 리사이클러뷰에 연결할 어댑터

    private TextView mFoodNumText;      // 입력된 식품정보 개수를 표시할 텍스트뷰

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_food, container, false);

        // 리사이클러 뷰 초기화
        buildFoodRecycler(v);
        updateFoodRecycler();

        // 버튼에 리스너 설정
        MaterialButton addAlarmButton = v.findViewById(R.id.btn_add_food_data);
        addAlarmButton.setOnClickListener(this);

        // 식품 개수 텍스트뷰 업데이트
        mFoodNumText = v.findViewById(R.id.txt_food_number);
        updateFoodNumber();

        return v;
    }

    // 리사이클러 뷰를 초기화한다.

    private void buildFoodRecycler(View v) {

        RecyclerView alarmRecycler = v.findViewById(R.id.recycler_food);
        alarmRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mFoodList = new ArrayList<>();
        mFoodAdapter = new FoodAdapter(mFoodList);
        alarmRecycler.setAdapter(mFoodAdapter);

        // 식품정보 아이템 뷰에 롱 클릭 리스너 설정
        mFoodAdapter.setOnItemClickListener(new FoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) { }

            @Override
            public void onItemLongClick(int position) {
                // 식품정보 변경 대화상자를 보여준다.
                showModifyFoodDialog(position);
            }
        });
    }

    // 리사이클러 뷰를 업데이트한다

    private void updateFoodRecycler() {

        mFoodList.clear();
        mFoodList.addAll(SQLiteHelper.getInstance(getContext()).getAllFoods());
        mFoodAdapter.notifyDataSetChanged();
    }


    // 식품정보 개수 텍스트뷰 업데이트

    private void updateFoodNumber() {

        String strAlarmNum = String.format(Locale.getDefault(),
                "등록된 식품 수 : %d", mFoodList.size());

        mFoodNumText.setText(strAlarmNum);
    }

    // 버튼 클릭 처리

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add_food_data) {

            // 식품정보 추가 대화상자를 보여준다.
            showAddFoodDialog();
        }
    }

    // 식품 추가 대화상자를 보여준다.

    private void showAddFoodDialog() {

        View addFoodView = View.inflate(getContext(), R.layout.add_food, null);
        EditText titleEdit = addFoodView.findViewById(R.id.edit_food_title);
        EditText kcalEdit = addFoodView.findViewById(R.id.edit_food_kcals);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add)
                .setView(addFoodView)
                .setPositiveButton(R.string.add, (dialog, which) -> {

                    // 추가 버튼 클릭 시 입력 정보 DB에 추가하기.
                    String strTitle = titleEdit.getText().toString().trim();
                    String strKcals = kcalEdit.getText().toString().trim();

                    if (strTitle.isEmpty() || strKcals.isEmpty()) {
                        Toast.makeText(getContext(),
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Food food = new Food(strTitle, Integer.parseInt(strKcals));
                    SQLiteHelper.getInstance(getContext()).addFood(food);

                    // 리사이클러뷰 업데이트
                    updateFoodRecycler();

                    // 식품정보 개수를 업데이트한다.
                    updateFoodNumber();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 식품정보 변경 대화상자를 보여준다.

    private void showModifyFoodDialog(int position) {

        View modifyFoodView = View.inflate(getContext(), R.layout.add_food, null);
        EditText titleEdit = modifyFoodView.findViewById(R.id.edit_food_title);
        EditText kcalEdit = modifyFoodView.findViewById(R.id.edit_food_kcals);

        Food selectedFood = mFoodList.get(position);
        titleEdit.setText(selectedFood.getTitle());
        kcalEdit.setText(String.valueOf(selectedFood.getKcals()));

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add)
                .setView(modifyFoodView)
                .setPositiveButton("변경", (dialog, which) -> {

                    // 변경 버튼 클릭 시 입력 정보 DB 에서 업데이트하기.
                    String strTitle = titleEdit.getText().toString().trim();
                    String strKcals = kcalEdit.getText().toString().trim();

                    if (strTitle.isEmpty() || strKcals.isEmpty()) {
                        Toast.makeText(getContext(),
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Food food = new Food(selectedFood.getId(), strTitle, Integer.parseInt(strKcals));
                    SQLiteHelper.getInstance(getContext()).updateFood(food);

                    // 리사이클러뷰 업데이트
                    updateFoodRecycler();
                })
                .setNeutralButton("삭제", (dialog, which) -> {

                    // 해당 식품정보 DB 에서 삭제하기
                    SQLiteHelper.getInstance(getContext()).deleteFood(selectedFood.getId());

                    // 리사이클러뷰 업데이트
                    updateFoodRecycler();

                    // 식품정보 개수 업데이트
                    updateFoodNumber();
                })
                .setNegativeButton("취소", null)
                .show();
    }

}