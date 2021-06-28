package com.good.memoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.good.memoapp.helpers.MemoDatabase;
import com.good.memoapp.models.Memo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NotesAdapter adapter;
    private List<Memo> memos = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView noNotes;
    private MemoDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 리사이클러 뷰 초기화

        recyclerView = findViewById(R.id.recycler_view);
        noNotes = findViewById(R.id.empty_notes_view);
        db = new MemoDatabase(this);  // SQLite DB 생성되고 테이블이 만들어짐
        memos.addAll(db.getAllMemos()); // SQLite DB 에서 레코드 전체 검색하고 리스트로 저장
        adapter = new NotesAdapter(this, memos);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        // 리사이클러 뷰 터치이벤트 등록

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                showMemoViewDialog(memos.get(position));
            }

            @Override
            public void onLongClick(View view, int position) {
                showActionDialog(position);
            }
        }));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMemoDialog(false, null, -1);
            }
        });

        // 저장된 데이터가 있으면 No diaries found 메세지가 안보이도록 함.
        if(memos.size() > 0) noNotes.setVisibility(View.GONE);
        else noNotes.setVisibility(View.VISIBLE);
    }

    // 데이터 한 건 생성하고 SQLite DB에 저장
    private void createMemo(Memo memo) {

        long id = db.insertMemo(memo);
        Memo n = db.getMemo(id);
        if(n != null) {
            memos.add(0,n);
            adapter.notifyDataSetChanged();
            // 저장된 데이터가 있으면 No diaries found 메세지가 안보이도록 함.
            if(memos.size() > 0) noNotes.setVisibility(View.GONE);
            else noNotes.setVisibility(View.VISIBLE);
        }
    }

    // 데이터 변경하기
    private void updateMemo(Memo memo, int position) {
        Memo n = memos.get(position);
        n.setTitle(memo.getTitle());
        n.setContent(memo.getContent());

        db.updateMemo(n.getId(), n);
        memos.set(position, n);  // 리스트에서 해당 객체를 변경
        adapter.notifyDataSetChanged();
        // 저장된 데이터가 있으면 No diaries found 메세지가 안보이도록 함.
        if(memos.size() > 0) noNotes.setVisibility(View.GONE);
        else noNotes.setVisibility(View.VISIBLE);
    }

    // 데이터 삭제하기
    private void deleteMemo(int position) {

        long id = memos.get(position).getId();
        db.deleteMemo(id);
        memos.remove(position);
        adapter.notifyItemRemoved(position);
        // 저장된 데이터가 있으면 No diaries found 메세지가 안보이도록 함.
        if(memos.size() > 0) noNotes.setVisibility(View.GONE);
        else noNotes.setVisibility(View.VISIBLE);
    }

    private void showMemoDialog(final boolean shouldUpdate, final Memo memo, final int position) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.memo_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText titleEdit = view.findViewById(R.id.edit_memo_title);
        final EditText contentEdit = view.findViewById(R.id.edit_memo_content);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.new_memo) : getString(R.string.edit_memo));

        if (shouldUpdate && memo != null) {
            titleEdit.setText(memo.getTitle());
            contentEdit.setText(memo.getContent());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                    }
                })
                .setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                String strTitle = titleEdit.getText().toString().trim();
                String strContent = contentEdit.getText().toString().trim();

                if (strTitle.isEmpty()) {
                    Toast.makeText(MainActivity.this, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (strContent.isEmpty()) {
                    Toast.makeText(MainActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                Memo newMemo = new Memo(strTitle, strContent);

                // check if user updating note
                if (shouldUpdate && memo != null) {
                    // update note by it's id
                    updateMemo(newMemo, position);
                } else {
                    // create new note
                    createMemo(newMemo);
                }
            }
        });
    }

    private void showActionDialog(int position) {

        CharSequence colors[] = new CharSequence[]{"편집하기", "삭제하기"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("선택");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0) {
                    showMemoDialog(true, memos.get(position), position);
                }
                else {
                    deleteMemo(position);
                }
            }
        });
        builder.show();
    }

    private void showMemoViewDialog(final Memo memo) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.view_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final TextView titleText = view.findViewById(R.id.txt_memo_title);
        final TextView contentText = view.findViewById(R.id.txt_memo_content);
        //TextView dialogTitle = view.findViewById(R.id.dialog_title);
        //dialogTitle.setText("");

        titleText.setText(memo.getTitle());
        contentText.setText(memo.getContent());

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("ok", null)
                .show();
    }

}