package com.holy.interiortalk

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.holy.interiortalk.adapters.PostAdapter
import com.holy.interiortalk.models.*

const val CATEGORY_LIVING_ROOM = 1
const val CATEGORY_BEDROOM = 2
const val CATEGORY_KITCHEN = 3
const val CATEGORY_BATHROOM = 4
const val CATEGORY_RANKING = 5
const val CATEGORY_MY_POSTS = 6

class BoardFragment : Fragment(), View.OnClickListener, PostAdapter.OnItemClickListener {

    // 포스트 리사이클러뷰 / 어댑터
    private lateinit var postRecycler: RecyclerView
    private lateinit var postAdapter: PostAdapter

    // Firebase 관련 변수
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postColl: CollectionReference

    // Firebase Auth 관련 변수
    private lateinit var auth: FirebaseAuth

    // Firebase Storage
    private lateinit var storage: FirebaseStorage
    private lateinit var picturesRef: StorageReference

    var category: Int = CATEGORY_LIVING_ROOM
        set(newCat) {
            field = newCat
            if (view != null) {
                buildPostRecycler(view!!)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_board, container, false)

        // 버튼에 클릭 리스너를 설정한다
        val writePostButton: FloatingActionButton = view.findViewById(R.id.fbtn_write_post)
        writePostButton.setOnClickListener(this)

        // Firestore 인스턴스를 획득한다
        firestore = FirebaseFirestore.getInstance()
        postColl = firestore.collection("posts")

        // Firebase Storage 인스턴스 획득
        storage = FirebaseStorage.getInstance()
        picturesRef = storage.reference.child("pictures")

        // Firebase Auth 인스턴스 획득
        auth = FirebaseAuth.getInstance()

        // 포스트 리사이클러뷰를 Firestore 와 연동하여 빌드한다
        buildPostRecycler(view)

        return view
    }

    // 포스트 리사이클러뷰를 빌드한다

    private fun buildPostRecycler(view: View) {

        // 리사이클러뷰의 속성 설정
        postRecycler = view.findViewById(R.id.recycler_post)
        postRecycler.setHasFixedSize(true)
        postRecycler.layoutManager = LinearLayoutManager(context)

        // 어댑터 생성, 연동 : 카테고리에 따라 다른 쿼리 적용
        val query = when (category) {
            CATEGORY_LIVING_ROOM -> postColl.whereEqualTo("tag", POST_TAG_LIVING_ROOM)
                .orderBy("time", Query.Direction.DESCENDING)
            CATEGORY_BEDROOM -> postColl.whereEqualTo("tag", POST_TAG_BEDROOM)
                .orderBy("time", Query.Direction.DESCENDING)
            CATEGORY_KITCHEN -> postColl.whereEqualTo("tag", POST_TAG_KITCHEN)
                .orderBy("time", Query.Direction.DESCENDING)
            CATEGORY_BATHROOM -> postColl.whereEqualTo("tag", POST_TAG_BATHROOM)
                .orderBy("time", Query.Direction.DESCENDING)
            CATEGORY_MY_POSTS -> postColl.whereEqualTo("writer", auth.currentUser!!.email)
                .orderBy("time", Query.Direction.DESCENDING)
            CATEGORY_RANKING -> postColl.orderBy("likes", Query.Direction.DESCENDING)
            else -> postColl.orderBy("likes", Query.Direction.DESCENDING)
        }

        val options = FirestoreRecyclerOptions.Builder<Post>()
            .setQuery(query, Post::class.java)
            .build()

        postAdapter = PostAdapter(context!!, picturesRef, options)
        postRecycler.adapter = postAdapter
        postAdapter.startListening()

        // 어댑터에 아이템 추가 리스너 설정
        postAdapter.setOnItemClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        postAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        postAdapter.stopListening()
    }

    // 포스트 클릭 처리
    override fun onItemClick(post: Post) {

        // ReadPostActivity 를 시작한다
        val intent = Intent(context, ReadPostActivity::class.java)

        // - 인텐트에 포스트의 작성자와 작성 시간을 전달한다
        intent.putExtra(EXTRA_POST_ID, post.id)

        startActivity(intent)
    }

    // 프래그먼트 내 버튼 클릭 처리

    override fun onClick(v: View?) {

        when (v?.id) {
            // 포스트 작성 버튼 클릭 : WritePostActivity 시작
            R.id.fbtn_write_post -> startWritePostActivity()
        }
    }

    // WritePostActivity 시작

    private fun startWritePostActivity() {

        val intent = Intent(context, WritePostActivity::class.java)
        val postTag = when (category) {
            CATEGORY_LIVING_ROOM -> POST_TAG_LIVING_ROOM
            CATEGORY_BEDROOM -> POST_TAG_BEDROOM
            CATEGORY_KITCHEN -> POST_TAG_KITCHEN
            CATEGORY_BATHROOM -> POST_TAG_BATHROOM
            else -> POST_TAG_LIVING_ROOM
        }
        intent.putExtra(EXTRA_POST_TAG, postTag)
        startActivity(intent)
    }

}