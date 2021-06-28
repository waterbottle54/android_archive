package com.myapp.workcalendar.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.workcalendar.R;
import com.myapp.workcalendar.models.Work;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class WorkAdapter extends RecyclerView.Adapter<WorkAdapter.ViewHolder> {

    private final List<Work> list;
    private OnItemClickListener onItemClickListener;

    public WorkAdapter(List<Work> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        TextView hourlyWageText;
        TextView hoursText;
        TextView wageText;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txt_work_title);
            hourlyWageText = itemView.findViewById(R.id.txt_work_hourly_wage);
            hoursText = itemView.findViewById(R.id.txt_work_hours);
            wageText = itemView.findViewById(R.id.txt_wage);
        }

        public void bind(Work model, OnItemClickListener listener) {

            titleText.setText(model.getTitle());

            String strHourlyWage = String.format(Locale.getDefault(),
                    "%,d원", model.getHourlyWage());
            hourlyWageText.setText(strHourlyWage);

            String strHours = String.format(Locale.getDefault(), "%d시간", model.getHours());
            hoursText.setText(strHours);

            String strWage = String.format(Locale.getDefault(),
                    "%,d원", model.getHours() * model.getHourlyWage());
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
                .inflate(R.layout.item_work, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Work item = list.get(position);
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