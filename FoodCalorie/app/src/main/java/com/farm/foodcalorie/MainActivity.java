package com.farm.foodcalorie;

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

import com.farm.foodcalorie.api.GroceryApi;
import com.farm.foodcalorie.data.Food;
import com.farm.foodcalorie.databinding.ActivityMainBinding;
import com.farm.foodcalorie.ml.FoodModel;
import com.farm.foodcalorie.utils.BitmapUtils;
import com.farm.foodcalorie.utils.NameUtils;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> imageActivityLauncher;
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    private ActivityMainBinding binding;

    private final MutableLiveData<Bitmap> image = new MutableLiveData<>();

    private final Map<String, String> koreanMap = NameUtils.getKoreanMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // 이미지로부터 음식 이름을 획득한다
        LiveData<String> foodName = Transformations.map(image, bitmap -> {
            String name = new FoodModel(this).getFoodName(bitmap);
            return koreanMap.get(name);
        });

        // 음식 이름으로부터 식품 정보를 획득한다
        LiveData<Food> food = Transformations.switchMap(foodName, name -> {
            MutableLiveData<Food> data = new MutableLiveData<>();
            new Thread(() -> data.postValue(GroceryApi.get(name))).start();
            return data;
        });

        // 이미지를 이미지뷰에 띄운다
        image.observe(this, bitmap -> binding.imageView.setImageBitmap(bitmap));

        // 판별된 음식 이름을 텍스트뷰에 띄운다
        foodName.observe(this, name -> {
            String str = "판별값: " + name;
            binding.textViewIdentifiedFood.setText(str);
        });

        // 식품 정보를 텍스트뷰에 띄운다
        food.observe(this, foodValue -> {
            if (foodValue != null) {
                binding.textViewResult.setText(foodValue.toString());
            } else {
                binding.textViewResult.setText("등록되지 않은 음식입니다");
            }
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









