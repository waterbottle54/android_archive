package com.holy.interiortalk

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.holy.interiortalk.helpers.BitmapHelper

private const val REQUEST_GALLERY = 100
private const val REQUEST_READ_EXTERNAL = 100

class PictureFragment : Fragment() {

    // 프래그먼트 리스너
    interface FragmentListener {
        fun onPictureSelected(bitmap: Bitmap)
    }

    private var listener: FragmentListener? = null

    fun setFragmentListener(listener: FragmentListener) {
        this.listener = listener
    }


    // 선택된 사진 이미지 뷰
    private lateinit var imageView: ImageView
    // 기본 이미지 뷰
    private lateinit var defaultImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_picture, container, false)

        imageView = view.findViewById(R.id.img_selected_picture)
        defaultImageView = view.findViewById(R.id.img_default)

        // 버튼에 클릭 리스너를 설정한다
        val selectPictureButton: Button = view.findViewById(R.id.btn_select_picture)
        selectPictureButton.setOnClickListener {

            // 외부 저장소 읽기 권한 검사
            if (context?.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                // 사진을 선택하는 갤러리 시작
                startGallery()
            } else {
                // 외부 저장소 읽기 권한 요청
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    REQUEST_READ_EXTERNAL
                )
            }
        }

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            // 외부 저장소 권한 허용됨
            REQUEST_READ_EXTERNAL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 갤러리 시작
                    startGallery()
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data?.data != null) {
            // 이미지 선택 처리
            processImageSelected(data.data!!)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    // 이미지 선택 처리

    private fun processImageSelected(uri: Uri) {

        // 선택된 이미지 경로를 확인한다
        val path = BitmapHelper.getRealPathFromUri(context!!, uri)

        // 경로에 존재하는 이미지 파일로부터 비트맵을 생성한다
        val bitmap = BitmapHelper.getBitmapFromPath(path)

        // 비트맵을 이미지뷰에 표시한다.
        imageView.setImageBitmap(bitmap)
        defaultImageView.visibility = View.GONE

        // 비트맵 선택 리스너 호출
        listener?.onPictureSelected(bitmap)
        Log.d("kakak", "processImageSelected: ")

        // 토스트 출력
        Toast.makeText(context, "사진이 선택되었습니다.", Toast.LENGTH_SHORT).show()
    }

    // 사진을 선택하는 갤러리를 시작한다

    private fun startGallery() {

        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

}