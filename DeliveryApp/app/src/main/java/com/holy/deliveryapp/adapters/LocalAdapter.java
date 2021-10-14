package com.holy.deliveryapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.deliveryapp.R;
import com.holy.deliveryapp.models.Local;

import java.util.List;

@SuppressWarnings("unused")
public class LocalAdapter extends RecyclerView.Adapter<LocalAdapter.ViewHolder> {

    private final List<Local> list;
    private OnItemClickListener onItemClickListener;

    public LocalAdapter(List<Local> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nameText;
        private final TextView categoryText;
        private final TextView subcategoryText;
        private final TextView addressText;
        private final TextView phoneText;

        public ViewHolder(View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.txtLocalName);
            categoryText = itemView.findViewById(R.id.txtLocalCategory);
            subcategoryText = itemView.findViewById(R.id.txtLocalSubcategory);
            addressText = itemView.findViewById(R.id.txtLocalAddress);
            phoneText = itemView.findViewById(R.id.txtLocalPhone);
        }

        public void bind(Local model, OnItemClickListener listener) {

            nameText.setText(model.getName());
            categoryText.setText(model.getCategoryName());
            subcategoryText.setText(model.getSubcategoryName());
            addressText.setText(model.getAddress());
            phoneText.setText(model.getPhone());

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
                .inflate(R.layout.item_local, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Local item = list.get(position);
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