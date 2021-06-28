package com.holy.watchdog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.watchdog.R;
import com.holy.watchdog.models.Criminal;

import org.w3c.dom.Text;

import java.util.List;

@SuppressWarnings("unused")
public class CriminalAdapter extends RecyclerView.Adapter<CriminalAdapter.ViewHolder> {

    private final List<Criminal> list;
    private OnItemClickListener onItemClickListener;

    public CriminalAdapter(List<Criminal> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView nicknameText;

        public ViewHolder(View itemView) {
            super(itemView);

            nicknameText = itemView.findViewById(R.id.txt_criminal_nickname);
        }

        public void bind(Criminal model, OnItemClickListener listener) {

            nicknameText.setText(model.getNickname());

            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position, itemView);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_criminal, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Criminal item = list.get(position);
        holder.bind(item, onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position, View itemView);
    }

}