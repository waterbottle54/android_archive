package com.holy.deliveryapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.deliveryapp.R;
import com.holy.deliveryapp.models.Menu;

import java.text.NumberFormat;
import java.util.List;

@SuppressWarnings("unused")
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    private final List<Menu> list;
    private OnItemClickListener onItemClickListener;

    public MenuAdapter(List<Menu> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView priceText;
        private final CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.txtMenuName);
            priceText = itemView.findViewById(R.id.txtMenuPrice);
            checkBox = itemView.findViewById(R.id.checkMenu);
        }

        public void bind(Menu model, OnItemClickListener listener) {

            nameText.setText(model.getName());

            String strPrice = NumberFormat.getInstance().format(model.getPrice());
            priceText.setText(strPrice);

            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });

                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemChecked(position, isChecked);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Menu item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onItemChecked(int position, boolean isChecked);
    }

}