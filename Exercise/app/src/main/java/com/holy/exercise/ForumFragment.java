package com.holy.exercise;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.holy.exercise.adapters.PostAdapter;
import com.holy.exercise.models.Post;

import java.time.LocalDate;

public class ForumFragment extends Fragment implements
        PostAdapter.OnItemClickListener, View.OnClickListener {

    private final FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
    private final CollectionReference mPostColl = mFirestore.collection("posts");

    private App mApp;
    private PostAdapter mPostAdapter;
    private RecyclerView.AdapterDataObserver mAdapterObserver;
    private TextView mNoPostsText;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 어댑터 옵저버 정의
        mAdapterObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                mNoPostsText.setVisibility(mPostAdapter.getItemCount() == 0 ?
                        View.VISIBLE : View.INVISIBLE);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                mNoPostsText.setVisibility(mPostAdapter.getItemCount() == 0 ?
                        View.VISIBLE : View.INVISIBLE);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forum, container, false);

        mApp = (App)getActivity().getApplication();
        mNoPostsText = view.findViewById(R.id.txtNoPosts);

        updateAdminUI(view);

        // 리사이클러 초기화
        buildPostRecycler(view);

        // 클릭 리스너 설정
        FloatingActionButton writePostButton = view.findViewById(R.id.fbtnWritePost);
        TextView adminText = view.findViewById(R.id.txtAdminLogin);
        writePostButton.setOnClickListener(this);
        adminText.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPostAdapter.startListening();
        mPostAdapter.registerAdapterDataObserver(mAdapterObserver);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPostAdapter.stopListening();
        mPostAdapter.unregisterAdapterDataObserver(mAdapterObserver);
    }

    // 포스트 리사이클러 초기화
    
    private void buildPostRecycler(View view) {

        RecyclerView recycler = view.findViewById(R.id.recyclerPost);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // DB 쿼리 연동
        Query query = mPostColl.orderBy("date", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();
        mPostAdapter = new PostAdapter(options);
        mPostAdapter.setOnItemClickListener(this);

        recycler.setAdapter(mPostAdapter);
    }

    // 포스트 클릭 처리

    @Override
    public void onItemClick(int position) {
        // 포스트 열람 대화상자 띄우기
        showReadPostDialog(mPostAdapter.getItem(position));
    }

    @Override
    public void onDeleteButtonClick(int position) {

        Post post = mPostAdapter.getItem(position);

        // 포스트 삭제 진행
        if (!mApp.isAdmin()) {
            showDeletePostDialog(post);
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle("후기 삭제")
                    .setMessage("관리자님, 삭제하시겠습니까?")
                    .setPositiveButton(android.R.string.ok,
                            (dialog, which) -> deletePost(post.getId()))
                    .show();
        }
    }

    // 클릭 처리

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.fbtnWritePost) {
            // 포스트 작성 대화상자 띄우기
            showWritePostDialog();
        } else if (id == R.id.txtAdminLogin) {
            // 어드민 로그인 대화상자 띄우기
            showAdminLoginDialog();
        }
    }

    // 포스트 작성 대화상자 띄우기

    private void showWritePostDialog() {

        View writePostView = View.inflate(getContext(), R.layout.view_write_post, null);
        EditText writerEdit = writePostView.findViewById(R.id.editPostWriter);
        EditText passwordEdit = writePostView.findViewById(R.id.editPostPassword);
        EditText titleEdit = writePostView.findViewById(R.id.editPostTitle);
        EditText contentsEdit = writePostView.findViewById(R.id.editPostContents);

        new AlertDialog.Builder(getContext())
                .setTitle("후기 작성")
                .setView(writePostView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String writer = writerEdit.getText().toString().trim();
                    String password = passwordEdit.getText().toString().trim();
                    String title = titleEdit.getText().toString().trim();
                    String contents = contentsEdit.getText().toString().trim();
                    if (writer.isEmpty() || password.isEmpty() || title.isEmpty() || contents.isEmpty()) {
                        Toast.makeText(getContext(),
                                "모두 입력해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    writePost(writer, password, title, contents);
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // 포스트 작성 처리

    private void writePost(String writer, String password, String title, String contents) {

        Post post = new Post(title, writer, password, contents, LocalDate.now().toString());
        mPostColl.document(post.getId()).set(post)
                .addOnSuccessListener(command -> Toast.makeText(getContext(),
                        "글이 작성되었습니다.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "작성에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    // 포스트 열람 대화상자 띄우기

    private void showReadPostDialog(Post post) {

        View readPostView = View.inflate(getContext(), R.layout.view_read_post, null);
        EditText writerEdit = readPostView.findViewById(R.id.editPostWriter);
        EditText titleEdit = readPostView.findViewById(R.id.editPostTitle);
        EditText contentsEdit = readPostView.findViewById(R.id.editPostContents);
        writerEdit.setText(post.getWriter());
        titleEdit.setText(post.getTitle());
        contentsEdit.setText(post.getContents());

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("후기")
                .setView(readPostView)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    // 포스트 삭제 처리

    private void deletePost(String postId) {

        mPostColl.document(postId).delete()
                .addOnSuccessListener(command -> Toast.makeText(getContext(),
                        "삭제되었습니다", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),
                            "삭제에 실패했습니다", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                });
    }

    // 어드민 로그인 대화상자 띄우기

    private void showAdminLoginDialog() {

        View adminLoginView = View.inflate(getContext(), R.layout.view_password, null);
        EditText adminPasswordEdit = adminLoginView.findViewById(R.id.editPassword);

        new AlertDialog.Builder(getContext())
                .setTitle("어드민 로그인")
                .setView(adminLoginView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String adminPassword = adminPasswordEdit.getText().toString().trim();
                    if (mApp.loginAdmin(adminPassword)) {
                        updateAdminUI(getView());
                        Toast.makeText(getContext(),
                                "관리자로 로그인하였습니다", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),
                                "비밀번호가 틀립니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // 포스트 삭제 대화상자 띄우기

    private void showDeletePostDialog(Post post) {

        View deletePostDialog = View.inflate(getContext(), R.layout.view_password, null);
        EditText passwordEdit = deletePostDialog.findViewById(R.id.editPassword);

        new AlertDialog.Builder(getContext())
                .setTitle("후기 삭제")
                .setView(deletePostDialog)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    String password = passwordEdit.getText().toString().trim();
                    if (password.equals(post.getPassword())) {
                        deletePost(post.getId());
                    } else {
                        Toast.makeText(getContext(),
                                "비밀번호가 틀립니다", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    // 어드민 UI 업데이트
    
    private void updateAdminUI(View view) {
        
        if (view == null) {
            return;
        }
        
        TextView adminLoginText = view.findViewById(R.id.txtAdminLogin);
        TextView asAdminText = view.findViewById(R.id.txtAsAdmin);
        
        asAdminText.setVisibility(mApp.isAdmin() ? View.VISIBLE : View.INVISIBLE);
        adminLoginText.setVisibility(mApp.isAdmin() ? View.INVISIBLE : View.VISIBLE);
    }

}





