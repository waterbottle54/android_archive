package com.holy.radiorate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.radiorate.R;
import com.holy.radiorate.helpers.UtilHelper;
import com.holy.radiorate.models.NuclearPlant;
import com.holy.radiorate.models.RiskLevel;

import java.util.List;
import java.util.Locale;

public class NuclearPlantAdapter extends RecyclerView.Adapter<NuclearPlantAdapter.ViewHolder> {

    private final List<NuclearPlant> nuclearPlantList;
    private final List<Double> radioactivityList;
    private OnItemClickListener onItemClickListener;
    private final Context context;

    public NuclearPlantAdapter(Context context, List<NuclearPlant> nuclearPlantList, List<Double> radioactivityList) {
        this.context = context;
        this.nuclearPlantList = nuclearPlantList;
        this.radioactivityList = radioactivityList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView addressText;
        private final TextView valueText;
        private final TextView levelText;
        private final ImageView levelImage;

        public ViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.txtNuclearPlantName);
            addressText = itemView.findViewById(R.id.txtNuclearPlantAddress);
            valueText = itemView.findViewById(R.id.txtNuclearPlantValue);
            levelText = itemView.findViewById(R.id.txtNuclearPlantLevel);
            levelImage = itemView.findViewById(R.id.imageNuclearPlantLevel);
        }

        public void bind(NuclearPlant model, double value, String level, int color, OnItemClickListener listener) {

            nameText.setText(model.getName());
            addressText.setText(model.getAddress());

            String strValue = String.format(Locale.getDefault(), "%.3f", value);
            valueText.setText(strValue);

            levelText.setText(level);
            levelImage.setColorFilter(color);

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
                .inflate(R.layout.item_nuclear_plant, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        double value = radioactivityList.get(position);
        RiskLevel riskLevel = new RiskLevel(value);
        String level = UtilHelper.getRiskLevelName(context, riskLevel);
        int color = UtilHelper.getRiskLevelColor(context, riskLevel);

        NuclearPlant item = nuclearPlantList.get(position);
        holder.bind(item, value, level, color, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return nuclearPlantList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}