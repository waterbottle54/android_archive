package com.holy.interiortalk

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity(),
    View.OnClickListener,
    NavigationView.OnNavigationItemSelectedListener,
    CategoryFragment.BoardFragmentListener,
    FirebaseAuth.AuthStateListener {

    // Firebase Auth 관련 변수
    private lateinit var auth: FirebaseAuth

    // 드로어 레이아웃, 네비게이션 드로어
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawer: NavigationView

    // 프래그먼트들
    private lateinit var categoryFragment: CategoryFragment
    private lateinit var boardFragment: BoardFragment
    private lateinit var qnaFragment: QnaFragment

    private lateinit var salutationText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        salutationText = findViewById(R.id.txt_salutation)

        // Firebase Auth 인스턴스 획득
        auth = FirebaseAuth.getInstance()

        // 버튼에 리스너 설정
        val menuButton: ImageButton = findViewById(R.id.ibtn_menu)
        menuButton.setOnClickListener(this)

        // 드로어 관련 초기화
        drawerLayout = findViewById(R.id.drawer_layout)
        navDrawer = findViewById(R.id.nav_drawer)
        navDrawer.setNavigationItemSelectedListener(this)

        // 프래그먼트 생성
        categoryFragment = CategoryFragment()
        boardFragment = BoardFragment()
        qnaFragment = QnaFragment()

        // CategoryFragment 에 리스너 설정하고 띄우기
        categoryFragment.setFragmentListener(this)
        showCategoryFragment()

        // 백스택 리스너 설정
        supportFragmentManager.addOnBackStackChangedListener {
            // 인사말을 업데이트한다
            updateSalutation()
        }
    }

    override fun onResume() {

        super.onResume()
        auth.addAuthStateListener(this)
    }

    override fun onPause() {

        super.onPause()
        auth.removeAuthStateListener(this)
    }

    override fun onBackPressed() {

        when {
            // 네비게이션 드로어가 열린 경우 닫는다
            drawerLayout.isDrawerOpen(navDrawer) -> drawerLayout.closeDrawer(navDrawer)
            // 현재 프래그먼트 뒤에 올 프래그먼트가 없는 경우, 앱을 종료할지 묻는다
            supportFragmentManager.backStackEntryCount == 1 -> askWhetherToFinishApp()
            else -> super.onBackPressed()
        }
    }

    // 액티비티 내 버튼 클릭 처리

    override fun onClick(v: View?) {

        when (v?.id) {
            // 메뉴 버튼 : 네비게이션 드로어를 토글한다
            R.id.ibtn_menu -> toggleNavigationDrawer()
        }
    }

    // 네비게이션 메뉴 선택 시 처리

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        // 네비게이션 드로어를 닫는다
        drawerLayout.closeDrawer(navDrawer)

        // 선택된 메뉴에 따라 다른 처리를 한다
        when (item.itemId) {
            R.id.item_living_room -> showBoardFragment(CATEGORY_LIVING_ROOM)
            R.id.item_bedroom -> showBoardFragment(CATEGORY_BEDROOM)
            R.id.item_kitchen -> showBoardFragment(CATEGORY_KITCHEN)
            R.id.item_bathroom -> showBoardFragment(CATEGORY_BATHROOM)
            R.id.item_ranking -> showBoardFragment(CATEGORY_RANKING)
            R.id.item_my_post -> showBoardFragment(CATEGORY_MY_POSTS)
            R.id.item_qna -> showQnaFragment()
            R.id.item_logout -> askWhetherToLogout()
            else -> return false
        }
        return true
    }

    // 카테고리 카드뷰가 선택되었을 때 처리

    override fun onCategoryViewClicked(viewId: Int) {

        when (viewId) {
            R.id.card_living_room -> showBoardFragment(CATEGORY_LIVING_ROOM)
            R.id.card_bedroom -> showBoardFragment(CATEGORY_BEDROOM)
            R.id.card_kitchen -> showBoardFragment(CATEGORY_KITCHEN)
            R.id.card_bathroom -> showBoardFragment(CATEGORY_BATHROOM)
            R.id.card_ranking -> showBoardFragment(CATEGORY_RANKING)
        }
    }

    // 로그아웃이 이루어졌을 때 처리

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {

        if (firebaseAuth.currentUser == null) {
            // 로그아웃된 경우 : 액티비티를 종료한다
            Toast.makeText(this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // 로그아웃할지 대화상자로 물어보기

    private fun askWhetherToLogout() {

        AlertDialog.Builder(this)
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("네") { _, _ -> auth.signOut() }
            .setNegativeButton("아니오", null)
            .show()
    }

    // 앱을 종료할지 대화상자로 물어보기

    private fun askWhetherToFinishApp() {

        AlertDialog.Builder(this)
            .setMessage("앱을 종료하시겠습니까?")
            .setPositiveButton("네") { _, _ -> finish() }
            .setNegativeButton("아니오", null)
            .show()
    }

    // 네비게이션 드로어를 토글한다

    private fun toggleNavigationDrawer() {

        if (drawerLayout.isDrawerOpen(navDrawer)) {
            drawerLayout.closeDrawer(navDrawer)
        } else {
            drawerLayout.openDrawer(navDrawer)
        }
    }

    // CategoryFragment 보이기

    private fun showCategoryFragment() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_container, categoryFragment)
            .addToBackStack(null)
            .commit()

        updateSalutation()
    }

    // BoardFragment 보이기

    private fun showBoardFragment(category: Int) {

        // BoardFragment 카테고리 변경
        boardFragment.category = category

        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_container, boardFragment)
            .addToBackStack(null)
            .commit()

        updateSalutation()
    }

    // QnaFragment 보이기

    private fun showQnaFragment() {

        supportFragmentManager.beginTransaction()
            .replace(R.id.frag_container, qnaFragment)
            .addToBackStack(null)
            .commit()

        updateSalutation()
    }

    // 현재 띄워진 프래그먼트에 따라 다른 인사말을 설정한다

    private fun updateSalutation() {

        val currentFragment = supportFragmentManager.findFragmentById(R.id.frag_container) ?: return

        when (currentFragment) {
            categoryFragment -> {
                val strSalutation = "${auth.currentUser!!.email}님, 안녕하세요"
                salutationText.text = strSalutation
            }
            boardFragment -> {
                when (boardFragment.category) {
                    CATEGORY_LIVING_ROOM -> salutationText.text = "거실 포스트입니다"
                    CATEGORY_BEDROOM -> salutationText.text = "침실 포스트입니다"
                    CATEGORY_KITCHEN -> salutationText.text = "주방 포스트입니다"
                    CATEGORY_BATHROOM -> salutationText.text = "욕실 포스트입니다"
                    CATEGORY_RANKING -> salutationText.text = "포스트 랭킹입니다"
                    CATEGORY_MY_POSTS -> salutationText.text = "나의 포스트입니다"
                }
            }
            qnaFragment -> {
                salutationText.text = resources.getString(R.string.qna)
            }
        }
    }

}