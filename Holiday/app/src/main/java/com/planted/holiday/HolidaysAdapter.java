package com.planted.holiday;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class HolidaysAdapter extends ListAdapter<Holiday, HolidaysAdapter.HolidayViewHolder> {

    static class HolidayViewHolder extends RecyclerView.ViewHolder {

        private final TextView textViewName;        // 공휴일 이름 텍스트뷰
        private final TextView textViewDate;        // 공휴일 날짜 텍스트뷰
        private final TextView textViewDayOfWeek;   // 공휴일 요일 텍스트뷰

        public HolidayViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.textViewName);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDayOfWeek = itemView.findViewById(R.id.textViewDayOfWeek);
        }

        public void bind(Holiday model) {

            // 위젯에 공휴일 정보 입력

            textViewName.setText(model.getName());

            String strDate = String.format(Locale.getDefault(),
                    "%d월 %d일",
                    model.getMonth(), model.getDayOfMonth()
            );
            textViewDate.setText(strDate);

            String[] dayNames = {
                "월", "화", "수", "목", "금", "토", "일"
            };
            textViewDayOfWeek.setText(dayNames[model.getDayOfWeek() - 1]);
        }
    }

    public HolidaysAdapter() {
        super(new DiffUtilCallback());
    }

    @NonNull
    @Override
    public HolidayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.holiday_item, parent, false);
        return new HolidayViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HolidayViewHolder holder, int position) {
        holder.bind(getItem(position));
    }


    static class DiffUtilCallback extends DiffUtil.ItemCallback<Holiday> {

        @Override
        public boolean areItemsTheSame(@NonNull Holiday oldItem, @NonNull Holiday newItem) {
            return newItem.getName().equals(oldItem.getName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Holiday oldItem, @NonNull Holiday newItem) {
            return oldItem.equals(newItem);
        }
    }

}