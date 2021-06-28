package com.holy.interiortalk

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.holy.interiortalk.models.POST_TAG_BATHROOM
import com.holy.interiortalk.models.POST_TAG_BEDROOM
import com.holy.interiortalk.models.POST_TAG_KITCHEN
import com.holy.interiortalk.models.POST_TAG_LIVING_ROOM

class PostFragment : Fragment(), View.OnClickListener {

    // 프래그먼트 리스너

    interface FragmentListener {
        // 작성 버튼이 클릭되었을 때 호출할 리스너
        fun onSubmitPost(tag: String, title: String, description: String)
    }

    private var listener: FragmentListener? = null

    fun setFragmentListener(listener: FragmentListener) {
        this.listener = listener
    }

    // 포스트 작성 위젯
    private var tagSpinner: Spinner? = null
    private var titleEdit: EditText? = null
    private var descriptionEdit: EditText? = null

    // 태그 스피너 아이템
    private val postTags = arrayOf(POST_TAG_LIVING_ROOM,
        POST_TAG_BEDROOM, POST_TAG_KITCHEN, POST_TAG_BATHROOM)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_post, container, false)

        // 버튼에 리스너를 설정한다
        val submitButton: Button = view.findViewById(R.id.btn_submit_post)
        submitButton.setOnClickListener(this)

        // EditText 획득
        tagSpinner = view.findViewById(R.id.spinner_post_tag)
        titleEdit = view.findViewById(R.id.edit_post_title)
        descriptionEdit = view.findViewById(R.id.edit_post_description)

        // 태그 스피너 어댑터 정의
        tagSpinner?.adapter = ArrayAdapter(context!!, android.R.layout.simple_spinner_dropdown_item,
            postTags.map { tag ->
                when (tag) {
                    POST_TAG_LIVING_ROOM -> "거실"
                    POST_TAG_BEDROOM -> "침실"
                    POST_TAG_KITCHEN -> "주방"
                    POST_TAG_BATHROOM -> "욕실"
                    else -> " - "
                }
            })
        tagSpinner?.setSelection(0)

        return view
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.btn_submit_post -> submitPost()
        }
    }

    private fun submitPost() {

        // 입력된 제목과 내용을 읽는다
        val tag = postTags[tagSpinner!!.selectedItemPosition]
        val title = titleEdit?.text?.toString()
        val description = descriptionEdit?.text?.toString()

        if (title.isNullOrBlank() || description.isNullOrBlank()) {
            Toast.makeText(context, "모두 입력해주세요", Toast.LENGTH_SHORT).show()
            return;
        }

        // 리스너를 호출한다
        listener?.onSubmitPost(tag, title, description)
    }

    // 스피너 선택을 바꾼다

    fun selectTag(tag: String) {

        postTags.forEachIndexed { index, s ->
            if (s == tag) {
                tagSpinner?.setSelection(index)
            }
        }

    }

}