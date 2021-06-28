package com.myapp.workcalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.workcalendar.R;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class WageAdapter extends RecyclerView.Adapter<WageAdapter.ViewHolder> {

    private final List<Integer> wageList;
    private final List<Integer> yearList;
    private final List<Integer> monthList;
    private OnItemClickListener onItemClickListener;

    public WageAdapter(List<Integer> wageList, List<Integer> yearList, List<Integer> monthList) {

        this.wageList = wageList;
        this.yearList = yearList;
        this.monthList = monthList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView monthText;
        TextView wageText;

        public ViewHolder(View itemView) {
            super(itemView);

            monthText = itemView.findViewById(R.id.txt_month);
            wageText = itemView.findViewById(R.id.txt_wage);
        }

        public void bind(int wage, int year, int month, OnItemClickListener listener) {

            String strMonth = String.format(Locale.getDefault(),
                    "%d년 %d월", year, month+1);
            monthText.setText(strMonth);

            String strWage = String.format(Locale.getDefault(),
                    "%,d원", wage);
            wageText.setText(strWage);

            if (listener != null) {
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
                .inflate(R.layout.item_wage, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.bind(
                wageList.get(position),
                yearList.get(position),
                monthList.get(position),
                onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return wageList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}