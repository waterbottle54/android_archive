package com.holy.interiortalk

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.holy.interiortalk.models.FurnitureLabel
import com.holy.interiortalk.widgets.LabelableImageView

class FurnitureFragment : Fragment() {

    // 프래그먼트 리스너

    interface FragmentListener {
        // 가구 레이블이 추가되었을 때 호출시킴
        fun onFurnitureLabelAdded(label: FurnitureLabel)
        // 확인 버튼이 클릭되었을 때 호출시킴
        fun onConfirmButtonClicked()
    }

    private var listener: FragmentListener? = null

    fun setFragmentListener(listener: FragmentListener) {
        this.listener = listener
    }


    // 선택된 사진이 표시될 이미지뷰
    private var imageView: LabelableImageView? = null
    private var pictureImage: Bitmap? = null

    // 생성된 레이블
    private var labelList = ArrayList<FurnitureLabel>()


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_furniture, container, false)

        // 이미지뷰를 초기화한다
        imageView = view.findViewById(R.id.img_selected_picture)
        if (pictureImage != null) {
            imageView?.setImageBitmap(pictureImage)
        }
        imageView?.clearLabel()
        labelList.forEach { label ->
            imageView?.addLabel(label.posX, label.posY)
        }

        // 이미지뷰에 터치 리스너를 설정한다
        imageView?.setOnTouchPositionListener(object : LabelableImageView.OnTouchPositionListener {
            override fun onTouchPosition(posX: Float, posY: Float) {
                // 레이블을 추가하는 대화상자를 띄운다
                showAddLabelDialog(posX, posY)
            }
        })

        // 확인 버튼 리스너 설정
        val confirmButton: Button = view.findViewById(R.id.btn_confirm)
        confirmButton.setOnClickListener { listener?.onConfirmButtonClicked() }

        return view
    }

    // 레이블을 추가하는 대화상자를 띄운다

    private fun showAddLabelDialog(posX: Float, posY: Float) {

        // 레이블 추가 레이아웃을 생성한다
        val addLabelView = View.inflate(context, R.layout.view_add_label, null)
        // 레이블 추가를 위한 EditText 를 얻는다
        val furnitureTitleEdit = addLabelView.findViewById<EditText>(R.id.edit_furniture_title)
        val purchaseUrlEdit = addLabelView.findViewById<EditText>(R.id.edit_purchase_url)

        // 키보드를 보인다
        val inputMethodManager: InputMethodManager =
            context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        // 레이블 추가 대화상자를 띄운다
        AlertDialog.Builder(context!!)
            .setView(addLabelView)
            .setPositiveButton("추가") { _, _ ->
                // 입력된 레이블 정보를 읽는다
                val furnitureTitle = furnitureTitleEdit.text.toString()
                val purchaseUrl = purchaseUrlEdit.text.toString()
                // 유효성 검사
                if (furnitureTitle.isBlank() || purchaseUrl.isBlank()) {
                    Toast.makeText(context, "모두 입력해주세요", Toast.LENGTH_SHORT).show()
                    // 키보드를 숨긴다
                    inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
                    return@setPositiveButton
                }
                // 레이블 리스트에 새 레이블을 추가한다
                val newLabel = FurnitureLabel(posX, posY, furnitureTitle, purchaseUrl)
                labelList.add(newLabel)
                // 이미지뷰에 레이블을 추가한다
                imageView?.addLabel(posX, posY)
                // 리스너를 호출한다
                listener?.onFurnitureLabelAdded(newLabel)
                // 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .setNegativeButton("취소") { _, _ ->
                // 취소 클릭 시, 키보드를 숨긴다
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0)
            }
            .show()
    }

    fun setPictureImage(bitmap: Bitmap) {

        pictureImage = bitmap
        imageView?.setImageBitmap(pictureImage)
    }

    fun clearLabels() {

        labelList.clear()
        imageView?.clearLabel()
    }

}