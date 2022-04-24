package com.penelope.imageclassificationapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.penelope.imageclassificationapp.ai.ClassificationAi;
import com.penelope.imageclassificationapp.api.TranslationApi;
import com.penelope.imageclassificationapp.databinding.ActivityMainBinding;
import com.penelope.imageclassificationapp.utils.BitmapUtils;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imageActivityLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    private ActivityMainBinding binding;

    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();
    private LiveData<String> bestResult;
    private LiveData<String> bestResultInKorean;

    private ClassificationAi classificationAi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        classificationAi = new ClassificationAi(this);

        // 사진 입력 시 결과값을 처리하는 런처
        imageActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::onImageResult
        );

        // 사진 촬영, 저장소 읽기 퍼미션을 요청하는 런처
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                this::onPermissionResult
        );

        // 사진 촬영 버튼 시 유자에게 사진을 입력받는다
        binding.fabTakePicture.setOnClickListener(v -> promptImage());

        // 이미지를 이미지뷰에 띄운다
        image.observe(this, bitmap -> binding.imageView.setImageBitmap(bitmap));

        bestResult = Transformations.map(image, bitmap -> classificationAi.getBestResult(bitmap));

        bestResultInKorean = Transformations.switchMap(bestResult, text -> {
            MutableLiveData<String> korean = new MutableLiveData<>();
            new Thread(() -> {
                // 결과값을 한국어로 번역한다
                korean.postValue(TranslationApi.get(text));
            }).start();
            return korean;
        });

        bestResultInKorean.observe(this, name -> {
            if (name != null) {
                binding.textViewResult.setText(name);
            } else {
                binding.textViewResult.setText("인식 결과가 없습니다");
            }
            binding.progressBar.setVisibility(View.INVISIBLE);
        });
    }

    private void onImageResult(ActivityResult result) {

        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
            Bitmap bitmap = null;
            if (result.getData().getExtras() != null) {
                // 카메라 결과 획득
                bitmap = (Bitmap) result.getData().getExtras().get("data");
            } else {
                // 갤러리(포토) 결과 획득
                Uri uri = result.getData().getData();
                if (uri != null) {
                    bitmap = BitmapUtils.getBitmapFromUri(this, uri);
                }
            }
            image.setValue(bitmap);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void onPermissionResult(Map<String, Boolean> result) {

        Boolean permissionExternalStorage = result.get(Manifest.permission.READ_EXTERNAL_STORAGE);
        Boolean permissionCamera = result.get(Manifest.permission.CAMERA);

        if (permissionExternalStorage != null && permissionExternalStorage
                && permissionCamera != null && permissionCamera) {
            showImageDialog();
        }
    }

    private void promptImage() {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            showImageDialog();
        } else {
            requestPermissionLauncher.launch(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}
            );
        }
    }

    private void showImageDialog() {

        // 업로드 방법 선택 대화상자 보이기
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent chooser = Intent.createChooser(galleryIntent, "사진 업로드");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
        imageActivityLauncher.launch(chooser);
    }

}

