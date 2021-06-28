package com.good.memoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.good.memoapp.models.Memo;

import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private List<Memo> memos;

    // 뷰홀더 클래스
    static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView title, content;

        public MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.memo_title);
            content = view.findViewById(R.id.memo_content);
        }
    }

    // 생성자
    public NotesAdapter(Context context, List<Memo> memos) {
        this.context = context;
        this.memos = memos;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        // 아이템 레이아웃에 해당되는 데이터를 표시한다.
        Memo memo = memos.get(position);

        holder.title.setText(
                String.format(Locale.getDefault(), "제목: %s", memo.getTitle())
        );
        holder.content.setText(memo.getContent());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 아이템 레이아웃을 리소스로부터 생성한다.
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.memo_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }
}
