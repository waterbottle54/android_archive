package com.holy.exercise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.exercise.R;
import com.holy.exercise.models.Record;

import java.util.List;
import java.util.Locale;

@SuppressWarnings("unused")
public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.ViewHolder> {

    private final List<Record> list;
    private OnItemClickListener onItemClickListener;

    public RecordAdapter(List<Record> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final TextView secondsText;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txtRecordTitle);
            secondsText = itemView.findViewById(R.id.txtRecordSeconds);
        }

        public void bind(Record model, OnItemClickListener listener) {

            titleText.setText(model.getTitle());

            int sec = model.getSeconds();
            String strSeconds = "";
            if (model.getSeconds() < 60) {
                strSeconds = String.format(Locale.getDefault(), "%d초", sec);
            } else {
                strSeconds = String.format(Locale.getDefault(),
                        "%d분 %02d초", sec / 60, sec % 60);
            }
            secondsText.setText(strSeconds);

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
                .inflate(R.layout.item_record, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Record item = list.get(position);
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