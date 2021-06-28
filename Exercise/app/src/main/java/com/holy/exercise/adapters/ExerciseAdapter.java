package com.holy.exercise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.holy.exercise.R;
import com.holy.exercise.models.Exercise;

import java.util.List;

@SuppressWarnings("unused")
public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ViewHolder> {

    private final List<Exercise> list;
    private OnItemClickListener onItemClickListener;

    public ExerciseAdapter(List<Exercise> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.txtExercise);
            imageView = itemView.findViewById(R.id.imgExercise);
        }

        public void bind(Exercise model, OnItemClickListener listener) {

            titleText.setText(model.getTitle());
            imageView.setImageResource(model.getImgRes());

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
                .inflate(R.layout.item_exercise, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Exercise item = list.get(position);
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