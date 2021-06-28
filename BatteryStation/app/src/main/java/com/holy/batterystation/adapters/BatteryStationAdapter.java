package com.holy.batterystation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.batterystation.R;
import com.holy.batterystation.models.BatteryStation;

import java.util.List;

@SuppressWarnings("unused")
public class BatteryStationAdapter extends RecyclerView.Adapter<BatteryStationAdapter.ViewHolder> {

    private final List<BatteryStation> list;
    private OnItemClickListener onItemClickListener;

    public BatteryStationAdapter(List<BatteryStation> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;

        public ViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.txtBatteryStationName);
        }

        public void bind(BatteryStation model, OnItemClickListener listener) {

            nameText.setText(model.getName());

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
                .inflate(R.layout.item_battery_station, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BatteryStation item = list.get(position);
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