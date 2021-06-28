package com.myapp.workbook.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.myapp.workbook.R;
import com.myapp.workbook.models.Word;

import java.util.List;


@SuppressWarnings("unused")
public class WordAdapter extends RecyclerView.Adapter<WordAdapter.ViewHolder> {

    private final List<Word> list;
    private OnItemClickListener onItemClickListener;
    private OnVoiceClickListener onVoiceClickListener;

    public WordAdapter(List<Word> list) {
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setOnVoiceClickListener(OnVoiceClickListener listener) {
        this.onVoiceClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView spellingText;
        TextView meaningText;
        ImageButton voiceButton;

        public ViewHolder(View itemView) {
            super(itemView);

            spellingText = itemView.findViewById(R.id.txt_spelling);
            meaningText = itemView.findViewById(R.id.txt_meaning);
            voiceButton = itemView.findViewById(R.id.imgbtn_voice);
        }

        public void bind(Word model, OnItemClickListener onItemClickListener,
                         OnVoiceClickListener onVoiceClickListener) {

            spellingText.setText(model.getSpelling());
            meaningText.setText(model.getMeaning());

            if (onVoiceClickListener != null) {
                voiceButton.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onVoiceClickListener.onVoiceClick(position);
                    }
                });
            }

            if (onItemClickListener != null) {
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(position);
                    }
                });
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_word, parent, false);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Word item = list.get(position);
        holder.bind(item, onItemClickListener, onVoiceClickListener);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnVoiceClickListener {
        void onVoiceClick(int position);
    }

}