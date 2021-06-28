package com.holy.interiortalk

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.holy.interiortalk.adapters.WritePagerAdapter
import com.holy.interiortalk.models.FurnitureLabel
import com.holy.interiortalk.models.POST_TAG_LIVING_ROOM
import com.holy.interiortalk.models.Post
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

const val EXTRA_POST_TAG = "com.holy.interiortalk.post_tag"

class WritePostActivity : AppCompatActivity(),
    FirebaseAuth.AuthStateListener,
    PictureFragment.FragmentListener,
    FurnitureFragment.FragmentListener,
    PostFragment.FragmentListener {

    // 뷰 페이징 관련 변수
    private lateinit var pagerAdapter: WritePagerAdapter
    private lateinit var viewPager: ViewPager2

    // 선택된 사진
    private var pictureBitmap: Bitmap? = null

    // 작성 중인 포스트 객체
    private val post = Post()

    // Firestore 인스턴스
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postsRef: CollectionReference

    // Firebase Storage
    private lateinit var storage: FirebaseStorage
    private lateinit var picturesRef: StorageReference

    // Firebase Auth
    private lateinit var auth: FirebaseAuth

    // 태그
    private var postTag: String? = POST_TAG_LIVING_ROOM


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_write_post)

        // 카테고리 확인
        postTag = intent.getStringExtra(EXTRA_POST_TAG)

        // 페이저 어댑터 생성 및 연동
        pagerAdapter = WritePagerAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = 3

        // 페이지 변경시 호출될 콜백 정의
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                // 현재 페이지 위치를 구한다
                val currentFragment = supportFragmentManager.findFragmentByTag("f$position")

                when (position) {
                    0 -> {
                        // 사진 선택 페이지 : 사진 선택을 감지하기 위해 리스너를 설정한다
                        val pictureFragment = currentFragment as PictureFragment?
                        pictureFragment?.setFragmentListener(this@WritePostActivity)
                    }
                    1 -> {
                        // 가구 레이블 페이지 : 가구 레이블 추가를 감지하기 위해 리스너를 설정한다
                        val furnitureFragment = currentFragment as FurnitureFragment?
                        furnitureFragment?.setFragmentListener(this@WritePostActivity)
                    }
                    2 -> {
                        // 포스트 작성 페이지 : 포스트 작성 완료를 감지하기 위해 리스너를 작성한다
                        val postFragment = currentFragment as PostFragment?
                        postFragment?.setFragmentListener(this@WritePostActivity)
                        postFragment?.selectTag(postTag!!)
                    }
                }
            }
        })

        // Firestore 인스턴트 획득
        firestore = FirebaseFirestore.getInstance()
        postsRef = firestore.collection("posts")

        // Firebase Storage 인스턴스 획득
        storage = FirebaseStorage.getInstance()
        picturesRef = storage.reference.child("pictures")

        // Firebase Auth 인스턴스 획득
        auth = FirebaseAuth.getInstance()
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

    // 게시할 사진이 선택되었을 때 처리

    override fun onPictureSelected(bitmap: Bitmap) {

        pictureBitmap = bitmap

        // 가구 프래그먼트의 사진을 바꾸고 가구 레이블을 제거한다
        val furnitureFragment = supportFragmentManager.findFragmentByTag("f1") as FurnitureFragment?
        furnitureFragment?.setPictureImage(pictureBitmap!!)
        furnitureFragment?.clearLabels()

        viewPager.postDelayed({
            // 가구 페이지로 이동한다
            viewPager.setCurrentItem(1, true)
        } , 1000)
    }

    // 가구 레이블이 추가되었을 때 처리

    override fun onFurnitureLabelAdded(label: FurnitureLabel) {

        // 레이블 리스트에 새 레이블을 추가한다
        post.furnitureLabels.add(label)
    }

    // 가구 레이블 확인 버튼 클릭 처리

    override fun onConfirmButtonClicked() {
        viewPager.setCurrentItem(2, true)
    }

    // 포스트 작성 완료되었을 때 처리

    override fun onSubmitPost(tag: String, title: String, description: String) {

        // 태그, 제목, 내용을 설정한다
        post.tag = tag
        post.title = title
        post.description = description

        // 작성자, 작성시간을 설정한다
        post.writer = auth.currentUser!!.email
        post.time = LocalDateTime.now().toString()
        post.id = "${post.writer}_${post.time}"

        // 포스트 컬렉션에 삽입한다
        postsRef.document(post.id!!).set(post)
            .addOnSuccessListener {
                Toast.makeText(this, "작성이 완료되었습니다", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "작성에 실패했습니다", Toast.LENGTH_SHORT).show()
            }

        // 스토리지에 이미지를 업로드한다
        val baos = ByteArrayOutputStream()
        pictureBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        // 이미지 파일 이름은 포스트의 id 와 동일하게 설정
        picturesRef.child("${post.id}.jpg")
            .putBytes(data)
            .addOnSuccessListener {
                finish()
            }
            .addOnFailureListener {
                finish()
            }
    }

}