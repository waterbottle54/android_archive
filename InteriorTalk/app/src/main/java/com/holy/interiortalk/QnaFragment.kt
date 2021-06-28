package com.holy.interiortalk

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.holy.interiortalk.adapters.QnaAdapter
import com.holy.interiortalk.models.Qna
import java.time.LocalDateTime

class QnaFragment : Fragment(), View.OnClickListener, QnaAdapter.OnItemClickListener {

    // 포스트 리사이클러뷰 / 어댑터
    private lateinit var qnaRecycler: RecyclerView
    private lateinit var qnaAdapter: QnaAdapter

    // Firebase 관련 변수
    private lateinit var firestore: FirebaseFirestore
    private lateinit var qnaColl: CollectionReference

    // Firebase Auth 관련 변수
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_qna, container, false)

        // 버튼에 리스너 설정
        val writeQnaButton: FloatingActionButton = view.findViewById(R.id.fbtn_write_qna)
        writeQnaButton.setOnClickListener(this)

        // Firestore 인스턴스를 획득한다
        firestore = FirebaseFirestore.getInstance()
        qnaColl = firestore.collection("qnas")

        // Firebase Auth 인스턴스 획득
        auth = FirebaseAuth.getInstance()

        // QNA 리사이클러뷰를 Firestore 와 연동하여 빌드한다
        buildQnaRecycler(view)

        return view
    }

    // QNA 리사이클러뷰를 빌드한다

    private fun buildQnaRecycler(view: View) {

        // 리사이클러뷰의 속성 설정
        qnaRecycler = view.findViewById(R.id.recycler_qna)
        qnaRecycler.setHasFixedSize(true)
        qnaRecycler.layoutManager = LinearLayoutManager(context)

        // 어댑터 생성, 연동 : 카테고리에 따라 다른 쿼리 적용
        val query = qnaColl.orderBy("questionTime", Query.Direction.DESCENDING)

        val options = FirestoreRecyclerOptions.Builder<Qna>()
            .setQuery(query, Qna::class.java)
            .build()

        qnaAdapter = QnaAdapter(context!!, options)
        qnaRecycler.adapter = qnaAdapter
        qnaAdapter.startListening()

        // 어댑터에 아이템 추가 리스너 설정
        qnaAdapter.setOnItemClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        qnaAdapter.startListening()
    }

    override fun onPause() {
        super.onPause()
        qnaAdapter.stopListening()
    }

    // 버튼 클릭 처리

    override fun onClick(v: View?) {

        when (v?.id) {
            // QNA 작성 버튼 클릭 : 대화상자 띄우기
            R.id.fbtn_write_qna -> showQuestionDialog()
        }
    }

    // Qna 아이템 클릭 처리
    override fun onItemClick(qna: Qna) {

        // 질문이 본인 것인지 확인한다
        if (qna.questioner == auth.currentUser!!.email) {
            Toast.makeText(context, "본인의 질문은 답변할 수 없습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 질문에 이미 답변이 있는지 확인한다
        if (qna.answer != null) {
            Toast.makeText(context, "이미 답변이 있습니다", Toast.LENGTH_SHORT).show()
            return
        }

        // 답변 대화상자를 띄운다
        showAnswerDialog(qna.id!!)
    }

    // 질문 대화상자 띄우기

    private fun showQuestionDialog() {

        // 질문하기 레이아웃을 생성한다
        val questionView = View.inflate(context, R.layout.view_question, null)
        val questionEdit: EditText = questionView.findViewById(R.id.edit_question)

        // 키보드를 보인다
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // 대화상자를 띄운다
        AlertDialog.Builder(context!!)
            .setView(questionView)
            .setPositiveButton("완료") { _, _ ->
                // 완료 클릭 : 입력된 질문을 받아 컬렉션에 추가한다
                val strQuestion = questionEdit.text.toString()
                // - 유효성 검사
                if (strQuestion.isBlank()) {
                    Toast.makeText(context, "질문을 입력해주세요", Toast.LENGTH_SHORT).show()
                    // 키보드를 숨긴다
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
                    return@setPositiveButton
                }
                // - Qna 객체 구성
                val user = auth.currentUser!!.email
                val time = LocalDateTime.now().toString()
                val qna = Qna(
                    "$user$time",
                    user,
                    strQuestion,
                    time,
                    null,
                    null
                )
                // 컬렉션에 qna 추가
                qnaColl.document(qna.id!!).set(qna)
                    .addOnFailureListener {
                        Toast.makeText(context,
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

    // 답변 대화상자 띄우기

    private fun showAnswerDialog(qnaId: String) {

        // 답변하기 레이아웃을 생성한다
        val answerView = View.inflate(context, R.layout.view_answer, null)
        val answerEdit: EditText = answerView.findViewById(R.id.edit_answer)

        // 키보드를 보인다
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // 대화상자를 띄운다
        AlertDialog.Builder(context!!)
            .setView(answerView)
            .setPositiveButton("완료") { _, _ ->
                // 완료 클릭 : 입력된 답변을 받아 컬렉션에 업데이트한다
                val strAnswer = answerEdit.text.toString()
                // - 유효성 검사
                if (strAnswer.isBlank()) {
                    Toast.makeText(context, "답변을 입력해주세요", Toast.LENGTH_SHORT).show()
                    // 키보드를 숨긴다
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
                    return@setPositiveButton
                }
                // 콜렉션에서 해당 Qna 에 답변을 업데이트한다
                val map: MutableMap<String, Any> = mutableMapOf()
                map["answer"] = strAnswer
                map["answerer"] = auth.currentUser!!.email!!
                qnaColl.document(qnaId).update(map)
                    .addOnFailureListener {
                        Toast.makeText(context, "답변 작성에 실패했습니다", Toast.LENGTH_SHORT).show()
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