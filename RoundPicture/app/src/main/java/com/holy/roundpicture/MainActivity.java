package com.holy.roundpicture;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.holy.roundpicture.helpers.BitmapHelper;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_UPLOAD = 100;
    private static final int REQUEST_PERMISSION_UPLOAD = 100;

    private ImageView mImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imgView);

        // 버튼에 클릭 리스너 지정
        Button uploadButton = findViewById(R.id.btnUploadPicture);
        uploadButton.setOnClickListener(v -> {

            // EXTERNAL_STORAGE 권한 검사
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                showUploadDialog();
            } else {
                requestPermissions(
                        new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA },
                        REQUEST_PERMISSION_UPLOAD);
            }
        });
    }

    private void showUploadDialog() {

        // 업로드 방법 선택 대화상자 보이기
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "사진 업로드");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] { cameraIntent });
        startActivityForResult(chooser, REQUEST_UPLOAD);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EXTERNAL STORAGE 읽기 권한 허용됨
        if (requestCode == REQUEST_PERMISSION_UPLOAD) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showUploadDialog();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 이미지 업로드 결과
        if (requestCode == REQUEST_UPLOAD) {
            if (resultCode == RESULT_OK && data != null) {
                // 비트맵 획득
                Bitmap bitmap = null;
                if (data.getExtras() != null) {
                    // 카메라 결과 획득
                    bitmap = (Bitmap)data.getExtras().get("data");
                } else {
                    // 갤러리(포토) 결과 획득
                    Uri uri = data.getData();
                    if (uri != null) {
                        String path = BitmapHelper.getRealPathFromUri(this, uri);
                        bitmap = BitmapHelper.getBitmapFromPath(path);
                    }
                }
                if (bitmap != null) {
                    mImageView.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(this,
                            "업로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}



