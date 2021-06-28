package com.holy.caloriecalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.caloriecalendar.R;
import com.holy.caloriecalendar.models.Intake;

import org.w3c.dom.Text;

import java.util.List;
import java.util.Locale;


// 섭취 정보를 리사이클러뷰에 표시할 리스트

public class IntakeAdapter extends RecyclerView.Adapter<IntakeAdapter.ViewHolder> {


    private final List<Intake> list;                    // 섭취 정보 리스트
    private OnItemClickListener onItemClickListener;    // 아이템 클릭 리스너

    public IntakeAdapter(List<Intake> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView kcalText;       // 섭취 칼로리 텍스트뷰
        TextView titleText;      // 섭취 음식이름 텍스트뷰
        TextView dateText;       // 섭취 날짜 텍스트뷰

        public ViewHolder(View itemView) {
            super(itemView);

            kcalText = itemView.findViewById(R.id.txt_intake_kcal);
            dateText = itemView.findViewById(R.id.txt_intake_date);
            titleText = itemView.findViewById(R.id.txt_intake_title);
        }

        public void bind(Intake model, OnItemClickListener listener) {

            // 텍스트뷰에 섭취 칼로리, 음식명, 날짜를 표시한다.
            String strKcals = String.format(Locale.getDefault(),
                    "%d kcal", model.getKcals());
            kcalText.setText(strKcals);

            titleText.setText(model.getTitle());

            String strDate = String.format(Locale.getDefault(),
                    "%02d : %02d분",
                    model.getHour(), model.getMinute());
            dateText.setText(strDate);

            if (listener != null) {
                // 아이템에 클릭 리스너를 설정한다
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intake, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Intake item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}