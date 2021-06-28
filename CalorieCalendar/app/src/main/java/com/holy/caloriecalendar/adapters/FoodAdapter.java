package com.holy.caloriecalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.caloriecalendar.R;
import com.holy.caloriecalendar.models.Food;

import java.util.List;
import java.util.Locale;


// 음식 정보를 리사이클러뷰로 표시할 어댑터

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    private final List<Food> list;                      // 음식 정보의 리스트
    private OnItemClickListener onItemClickListener;    // 아이템 클릭 리스너

    public FoodAdapter(List<Food> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;     // 음식 이름 텍스트뷰
        TextView kcalText;      // 음식 칼로리 텍스트뷰

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txt_food_title);
            kcalText = itemView.findViewById(R.id.txt_food_kcal);
        }

        public void bind(Food model, OnItemClickListener listener) {

            // 음식 이름, 칼로리를 텍스트뷰에 표시한다
            titleText.setText(model.getTitle());

            String strKcal = String.format(Locale.getDefault(),
                    "%d kcal", model.getKcals());
            kcalText.setText(strKcal);

            if (listener != null) {
                // 아이템 클릭 리스너를 설정한다
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
                // 아이템 롱클릭 리스너를 설정한다
                itemView.setOnLongClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemLongClick(position);
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Food item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(int position);
    }

}