package com.holy.interiortalk

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.holy.interiortalk.adapters.CommentAdapter
import com.holy.interiortalk.adapters.LabelAdapter
import com.holy.interiortalk.models.Comment
import com.holy.interiortalk.models.Post
import com.holy.interiortalk.widgets.LabelableImageView
import java.time.LocalDateTime
import java.util.*

const val EXTRA_POST_ID = "com.holy.interiortalk.post_id"

class ReadPostActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener,
    LabelAdapter.OnItemClickListener, View.OnClickListener {

    // Firebase Auth 관련 변수
    private lateinit var auth: FirebaseAuth

    // Firebase 관련 변수
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postColl: CollectionReference
    private lateinit var commentColl: CollectionReference

    // Firebase Storage
    private lateinit var storage: FirebaseStorage
    private lateinit var picturesRef: StorageReference

    // 대상 포스트의 ID
    private lateinit var postId: String

    // 댓글 개수 텍스트뷰, 리사이클러
    private lateinit var commentNumberText: TextView
    private lateinit var commentCard: CardView
    private lateinit var commentRecycler: RecyclerView

    // 좋아요 텍스트뷰
    private lateinit var likeItText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_post)

        // 위젯 초기화
        commentNumberText = findViewById(R.id.txt_comment_number)
        commentCard = findViewById(R.id.card_comment)
        commentCard.visibility = View.GONE
        commentRecycler = findViewById(R.id.recycler_comment)
        commentRecycler.layoutManager = LinearLayoutManager(this)
        likeItText = findViewById(R.id.txt_like_it)

        // Firebase Auth 인스턴스 획득
        auth = FirebaseAuth.getInstance()

        // 파이어스토어 인스턴스를 획득한다
        firestore = FirebaseFirestore.getInstance()
        postColl = firestore.collection("posts")
        commentColl = firestore.collection("comments")

        // Firebase Storage 인스턴스 획득
        storage = FirebaseStorage.getInstance()
        picturesRef = storage.reference.child("pictures")

        // 인텐트에 전달된 포스트의 ID를 획득한다
        postId = intent.getStringExtra(EXTRA_POST_ID)!!

        // 콜렉션에서 포스트를 검색하여 화면에 표시한다
        postColl.document(postId).get()
            .addOnCompleteListener { snapshot ->
                val post = snapshot.result?.toObject(Post::class.java)
                if (post != null) {
                    // 포스트 검색 성공 시 UI 를 업데이트한다
                    updatePostUI(post)
                } else {
                    // 포스트 검색 실패 시 액티비티를 종료한다
                    Toast.makeText(this,
                        "포스트를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

        // 댓글 콜렉션에 스냅샷 리스너를 설정한다
        commentColl.whereEqualTo("postId", postId)
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    return@addSnapshotListener
                }
                // 댓글 콜렉션 변경 시, 댓글 UI 를 업데이트한다
                updateCommentUI(snapshot.documents.map { doc ->
                    doc.toObject(Comment::class.java)!!
                })
            }

        // 위젯에 리스너를 설정한다
        val commentEdit: EditText = findViewById(R.id.edit_comment)
        commentEdit.setOnClickListener(this)
        likeItText.setOnClickListener(this)
    }

    // 댓글 UI를 업데이트한다

    private fun updateCommentUI(commentList: List<Comment>) {

        // 리사이클러뷰 업데이트
        val commentAdapter = CommentAdapter(this, commentList)
        commentRecycler.adapter = commentAdapter

        // 댓글 수 업데이트
        commentNumberText.text = "${commentList.size}"
        commentCard.visibility = if (commentList.isEmpty()) View.GONE else View.VISIBLE
    }

    // 포스트 UI 를 업데이트한다

    private fun updatePostUI(post: Post) {

        val titleText: TextView = findViewById(R.id.txt_post_title)
        val writerText: TextView = findViewById(R.id.txt_post_writer)
        val timeText: TextView = findViewById(R.id.txt_post_time)
        val descriptionText: TextView = findViewById(R.id.txt_post_description)
        val pictureImage: LabelableImageView = findViewById(R.id.img_post)
        val labelRecycler: RecyclerView = findViewById(R.id.recycler_label)

        // 제목, 작성자 표시
        titleText.text = post.title
        writerText.text = post.writer

        // 작성시간 표시
        val time = LocalDateTime.parse(post.time)
        with (time) {
            timeText.text = String.format(Locale.getDefault(),
                "%d-%02d-%02d %02d:%02d:%02d",
                year, monthValue, dayOfMonth, hour, minute, second)
        }

        // 내용 표시
        descriptionText.text = post.description

        // 이미지 표시
        val megaByte = (1024 * 1024).toLong()
        picturesRef.child("${post.id}.jpg")
            .getBytes(10 * megaByte)
            .addOnSuccessListener { bytes ->
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                pictureImage.setImageBitmap(bitmap)
                // 레이블 표시
                pictureImage.clearLabel()
                for (label in post.furnitureLabels) {
                    pictureImage.addLabel(label.posX, label.posY)
                }
            }
            .addOnFailureListener {
                // 이미지 로딩 실패시 액티비티 종료
                Toast.makeText(this,
                    "이미지를 불러오지 못했습니다", Toast.LENGTH_SHORT).show()
                finish()
            }

        // 좋아요 표시
        val strLikes = "+ ${post.likes}"
        likeItText.text = strLikes

        // 레이블 목록 표시
        // - 레이블 리사이클러뷰 속성 설정
        labelRecycler.layoutManager = LinearLayoutManager(this)

        // 레이블 어댑터 생성 및 연동
        val labelAdapter = LabelAdapter(this, post.furnitureLabels)
        labelRecycler.adapter = labelAdapter

        // 레이블 클릭 리스너 설정
        labelAdapter.setOnItemClickListener(this)
    }

    override fun onResume() {

        super.onResume()
        auth.addAuthStateListener(this)
    }

    override fun onPause() {

        super.onPause()
        auth.removeAuthStateListener(this)
    }

    // 로그아웃이 이루어졌을 때 처리

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {

        if (firebaseAuth.currentUser == null) {
            // 로그아웃된 경우 : 액티비티를 종료한다
            finish()
        }
    }

    // 레이블의 구매 링크가 클릭되었을 때 처리

    override fun onPurchaseUrlClick(url: String?) {

        var urlTemp = url ?: return

        if (!url.startsWith("http://") && !url.startsWith("https://"))
            urlTemp = "http://$url"

        // 브라우저로 링크된 사이트를 띄운다
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlTemp))
        startActivity(intent)
    }

    // 위젯 클릭 시 처리

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.txt_like_it -> likeIt()
            R.id.edit_comment -> showWriteCommentDialog()
        }
    }

    // 좋아요 처리

    private fun likeIt() {

        // 콜렉션에서 포스트를 가져온다
        postColl.document(postId).get()
            .addOnSuccessListener { snapshot ->

                val post = snapshot.toObject(Post::class.java) ?: return@addOnSuccessListener
                val user = auth.currentUser!!.email

                // 본인 여부를 확인한다
                if (post.writer == user) {
                    Toast.makeText(this,
                        "본인의 글에는 좋아요를 누를 수 없습니다", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 좋아요 중복 여부를 확인한다
                if (post.likedUsers.contains(user)) {
                    Toast.makeText(this,
                        "이미 좋아요를 누르셨습니다", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // 좋아요 수, 좋아요 유저 명단을 업데이트 한다
                val map = mutableMapOf<String, Any>()
                map["likes"] = post.likes + 1
                map["likedUsers"] = post.likedUsers.plus(user)
                postColl.document(postId).update(map)

                // 좋아요 텍스트뷰를 업데이트한다
                val strLikes = "+ ${post.likes + 1}"
                likeItText.text = strLikes
            }
    }

    // 댓글 쓰기 대화상자를 띄운다

    private fun showWriteCommentDialog() {

        // 댓글 쓰기 레이아웃을 생성한다
        val writeCommentView = View.inflate(this, R.layout.view_write_comment, null)
        val commentEdit: EditText = writeCommentView.findViewById(R.id.edit_comment)

        // 키보드를 보인다
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // 대화상자를 띄운다
        AlertDialog.Builder(this)
            .setView(writeCommentView)
            .setPositiveButton("완료") { _, _ ->
                // 완료 클릭 : 입력된 댓글을 받아 DB 에 추가한다
                val strComment = commentEdit.text.toString()
                // - 유효성 검사
                if (strComment.isBlank()) {
                    Toast.makeText(this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show()
                    // 키보드를 숨긴다
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
                    return@setPositiveButton
                }
                // - Comment 객체 구성
                val comment = Comment(
                    postId,
                    auth.currentUser!!.email,
                    LocalDateTime.now().toString(),
                    strComment
                )
                // 컬렉션에 comment 추가
                commentColl.document().set(comment)
                    .addOnFailureListener {
                        Toast.makeText(this,
                            "작성에 실패했습니다", Toast.LENGTH_SHORT).show()
                    }
                // 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .setNegativeButton("취소") { _, _ ->
                // 취소 클릭 : 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .show()
    }

}