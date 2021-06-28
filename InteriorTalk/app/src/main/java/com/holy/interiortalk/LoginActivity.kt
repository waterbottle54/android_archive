package com.holy.interiortalk

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


const val REQUEST_GOOGLE_LOGIN = 1

class LoginActivity : AppCompatActivity(), View.OnClickListener, FirebaseAuth.AuthStateListener {

    // Firebase Auth 관련 변수
    private lateinit var auth: FirebaseAuth

    // Google 로그인 클라이언트
    private lateinit var googleSignInClient: GoogleSignInClient

    // Facebook 로그인 관련 변수
    private lateinit var callbackManager: CallbackManager

    // 회원가입을 위한 EditText
    private lateinit var registerEmailEdit: EditText
    private lateinit var registerPasswordEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 앱이 첫 실행될 때 표지를 잠깐 띄웠다가 fade out 한다.
        if (savedInstanceState == null) {
            fadeOutCover()
        }

        // Firebase Auth 초기화
        auth = FirebaseAuth.getInstance()

        // Google 로그인 초기화
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Facebook 로그인 초기화
        callbackManager = CallbackManager.Factory.create()

        // 회원가입을 위한 EditText 를 획득한다
        registerEmailEdit = findViewById(R.id.edit_register_email)
        registerPasswordEdit = findViewById(R.id.edit_register_password)

        // 버튼 및 위젯에 리스너를 설정한다
        val loginMemberButton: Button = findViewById(R.id.btn_login_member)
        val loginGoogleButton: Button = findViewById(R.id.btn_login_google)
        val loginFacebookButton: Button = findViewById(R.id.btn_login_facebook)
        val registerButton: Button = findViewById(R.id.btn_register)
        loginMemberButton.setOnClickListener(this)
        loginGoogleButton.setOnClickListener(this)
        loginFacebookButton.setOnClickListener(this)
        registerButton.setOnClickListener(this)
        registerEmailEdit.setOnClickListener(this)
        registerPasswordEdit.setOnClickListener(this)
    }

    // 표지 레이아웃에 fade out 애니메이션 적용
    private fun fadeOutCover() {
        val cover = findViewById<View>(R.id.cover)
        cover.visibility = View.VISIBLE

        // fade out 애니메이션 로드 : 자체 정의한 fade out 리소스를 사용한다.
        // 리스너를 설정하여 애니메이션이 끝날 때 표지 레이아웃을 GONE 상태로 만든다.
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        fadeOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                cover.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })

        // 3초간 보여주다가, fade out 애니메이션 적용
        cover.postDelayed({ cover.startAnimation(fadeOut) }, 3000)
    }

    override fun onResume() {
        super.onResume()
        auth.addAuthStateListener(this)
    }

    override fun onPause() {
        super.onPause()
        auth.removeAuthStateListener(this)
    }

    // 버튼 클릭을 처리한다

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_login_member -> showLoginDialog()
            R.id.btn_login_google -> loginGoogle()
            R.id.btn_login_facebook -> loginFacebook()
            R.id.btn_register -> registerMember()
            R.id.edit_register_email, R.id.edit_register_password -> showRegisterDialog()
        }
    }

    // 로그인 / 로그아웃이 이루어졌을 때 처리

    override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {

        val user = firebaseAuth.currentUser
        if (user != null) {
            // 로그인이 이루어졌을 때 : 홈 액티비티를 시작한다
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 페이스북 로그인 처리
        callbackManager.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            // 구글 로그인 처리
            REQUEST_GOOGLE_LOGIN -> {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result != null && result.isSuccess) {
                    val account = result.signInAccount
                    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credential)
                }
            }
        }
    }

    // 로그인 대화상자를 띄운다

    private fun showLoginDialog() {

        // 로그인 레이아웃을 생성한다
        val loginView = View.inflate(this, R.layout.view_member_login, null)
        // 로그인을 위한 EditText 를 얻는다
        val loginEmailEdit = loginView.findViewById<EditText>(R.id.edit_login_email)
        val loginPasswordEdit = loginView.findViewById<EditText>(R.id.edit_login_password)

        // 키보드를 보인다
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // 로그인 대화상자를 띄운다
        AlertDialog.Builder(this)
            .setView(loginView)
            .setPositiveButton("확인") { _, _ ->
                // 확인 클릭 시, 로그인을 시도한다
                val email = loginEmailEdit.text.toString()
                val password = loginPasswordEdit.text.toString()
                loginMember(email, password)
                // 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .setNegativeButton("취소") { _, _ ->
                // 취소 클릭 시, 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .show()
    }

    // 회원가입 대화상자를 띄운다

    private fun showRegisterDialog() {

        // 회원가입 레이아웃을 생성한다
        val registerView = View.inflate(this, R.layout.view_member_register, null)
        // 회원가입을 위한 EditText 를 얻는다
        val registerEmailEdit = registerView.findViewById<EditText>(R.id.edit_register_email)
        val registerPasswordEdit = registerView.findViewById<EditText>(R.id.edit_register_password)

        // 키보드를 보인다
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // 회원가입 대화상자를 띄운다
        AlertDialog.Builder(this)
            .setView(registerView)
            .setPositiveButton("확인") { _, _ ->
                // 확인 클릭 시, 액티비티의 회원가입 EditText 에 입력값을 입력한다
                this.registerEmailEdit.text = registerEmailEdit.text
                this.registerPasswordEdit.text = registerPasswordEdit.text
                // 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .setNegativeButton("취소") { _, _ ->
                // 취소 클릭 시, 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .show()
    }

    // 회원 로그인을 시도한다

    private fun loginMember(email: String, password: String) {

        // 유효성 검사
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "모두 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase 로그인을 진행한다
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // 로그인 성공 시 성공 메세지를 토스트로 보여준다
                Toast.makeText(this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // 로그인 실패 시 에러 메세지를 토스트로 보여준다
                if (it.message != null) {
                    val strErrorKorean = translateErrorMessageKorean(it.message.toString())
                    Toast.makeText(this, strErrorKorean, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 회원가입을 시도한다

    private fun registerMember() {

        // 유저가 입력한 회원가입 정보를 얻는다
        val email = registerEmailEdit.text.toString()
        val password = registerPasswordEdit.text.toString()

        // 유효성 검사
        if (email.isBlank() || password.isBlank()) {
            Toast.makeText(this, "모두 입력해주세요", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase 회원가입을 진행한다
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                // 회원가입 성공 시 성공 메세지를 토스트로 보여준다
                Toast.makeText(this, "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                // 회원가입 실패 시 에러 메세지를 토스트로 보여준다
                if (it.message != null) {
                    val strErrorKorean = translateErrorMessageKorean(it.message.toString())
                    Toast.makeText(this, strErrorKorean, Toast.LENGTH_SHORT).show()
                }
            }
    }

    // 구글 로그인

    private fun loginGoogle() {

        val signInInClient = googleSignInClient.signInIntent
        startActivityForResult(signInInClient, REQUEST_GOOGLE_LOGIN)
    }

    // 페이스북 로그인

    private fun loginFacebook() {

        LoginManager.getInstance().logInWithReadPermissions(
            this,
            mutableListOf("public_profile", "email")
        )
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult?) {
                    if (result != null) {
                        handleFacebookAccessToken(result.accessToken)
                    }
                }

                override fun onCancel() {
                    // 페이스북 로그인 취소
                }

                override fun onError(error: FacebookException?) {
                    // 페이스북 로그인 실패
                    Toast.makeText(this@LoginActivity, error?.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    // 에러 메세지를 한국어로 번역해서 리턴한다

    private fun translateErrorMessageKorean(message: String): String {
        return when {
            message.contains("email address is badly formatted") -> "올바른 이메일 주소를 입력해주세요"
            message.contains("given password is invalid") -> "6글자 이상의 패스워드를 입력해주세요"
            message.contains("There is no user record") -> "가입되지 않은 아이디입니다"
            message.contains("The password is invalid") -> "비밀번호가 일치하지 않습니다"
            else -> message
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

}