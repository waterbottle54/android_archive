package com.holy.exercise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.holy.exercise.R;
import com.holy.exercise.models.Post;

import java.time.LocalDate;
import java.util.Locale;


public class PostAdapter extends FirestoreRecyclerAdapter<Post, PostAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;


    public PostAdapter(@NonNull FirestoreRecyclerOptions<Post> options) {
        super(options);
    }


    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleText;
        private final TextView writerText;
        private final TextView dateText;
        private final ImageButton deleteButton;

        public ViewHolder(View itemView) {
            super(itemView);

            titleText = itemView.findViewById(R.id.txtPostTitle);
            writerText = itemView.findViewById(R.id.txtPostWriter);
            dateText = itemView.findViewById(R.id.txtPostDate);
            deleteButton = itemView.findViewById(R.id.ibtnDeletePost);
        }

        public void bind(Post model, OnItemClickListener listener) {

            titleText.setText(model.getTitle());
            writerText.setText(model.getWriter());

            LocalDate date = LocalDate.parse(model.getDate());
            String strDate = String.format(Locale.getDefault(),
                    "%d년 %02d월 %02d일",
                    date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            dateText.setText(strDate);

            if (listener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                });
                deleteButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteButtonClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);

        return new ViewHolder(v);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post model) {

        holder.bind(model, onItemClickListener);
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteButtonClick(int position);
    }

}