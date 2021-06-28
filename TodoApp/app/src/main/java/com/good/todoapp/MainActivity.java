package com.good.todoapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.good.todoapp.adapters.DutyAdapter;
import com.good.todoapp.helpers.ImageHelper;
import com.good.todoapp.models.Duty;
import com.good.todoapp.helpers.DatabaseHelper;
import com.good.todoapp.services.MaximService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // 액티비티 호출 시의 리퀘스트 코드
    public static final int REQUEST_ADD_ACTIVITY = 100;
    public static final int REQUEST_IMAGE_CAPTURE = 101;
    // 인텐트 데이터의 키 값
    public static final String EXTRA_COMPLETED_RATE = "completedRate";
    public static final String EXTRA_SERVICE_TEXT = "serviceText";
    // 퍼미션 코드
    public static final int PERMISSION_REQUEST_CAPTURE = 100;

    // Duty (할 일) 객체 리스트
    private List<Duty> mDutyList;
    // Duty 어댑터
    private DutyAdapter mDutyAdapter;
    // 화면 중앙 메세지 텍스트뷰
    private TextView mMessageText;
    // 사진 찍기와 연관 Duty 객체
    private Duty mCapturedDuty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 할일 리스트를 DB 에서 불러온다.
        mDutyList = loadDutyList();

        // 할일 리스트를 이용해 할일 리스트뷰를 만든다.
        buildDutyListView(mDutyList);

        // 자식 뷰들을 초기화한다.
        mMessageText = findViewById(R.id.txt_message);
        updateMessageText();

        FloatingActionButton addButton = findViewById(R.id.btn_add);
        addButton.setOnClickListener(this);

        // 격언 서비스를 시작한다.
        startMaximService();

        // 필요한 퍼미션을 검사한다.
        checkPermissions();
    }

    @Override
    protected void onDestroy() {

        // 격언 서비스를 중지한다.
        stopMaximService();
        super.onDestroy();
    }

    private boolean checkPermissions() {

        // 카메라, 외부저장소 쓰기 권한을 체크한다.

        if (checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // 권한이 없으면 퍼미션을 요청한다.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CAPTURE);
            return false;
        }
    }

    private void startMaximService() {

        // 격언 서비스를 시작하되
        // 격언 문자열 배열 리소스로부터 임의의 격언을 전송한다.

        String[] maximList = getResources().getStringArray(R.array.array_maxim);
        int randIdx = (int) (Math.random() * maximList.length);

        Intent serviceIntent = new Intent(this, MaximService.class);
        serviceIntent.putExtra(EXTRA_SERVICE_TEXT, maximList[randIdx]);

        startService(serviceIntent);
    }

    private void stopMaximService() {

        // 격언 서비스를 중단한다.
        Intent serviceIntent = new Intent(this, MaximService.class);
        serviceIntent.putExtra(EXTRA_SERVICE_TEXT, "");

        stopService(serviceIntent);
    }

    private List<Duty> loadDutyList() {

        // 할일 리스트를 SQLite DB 에서 불러온다.
        return DatabaseHelper.getInstance(this).getAllDuties();
    }

    private void buildDutyListView(List<Duty> dutyList) {

        // 할일 리스트뷰를 초기화한다.
        ListView listView = findViewById(R.id.list);

        // 할 일 리스트로 어댑터를 생성하고 리스트뷰와 연결한다.
        mDutyAdapter = new DutyAdapter(dutyList);
        listView.setAdapter(mDutyAdapter);

        // 어댑터에 클릭 리스너를 설정한다.
        mDutyAdapter.setOnItemClickListener(new DutyAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int pos) {
                // 클릭 시, 클릭된 할 일을 완료 처리할지 유저에게 물어본다.
                if (pos > -1 && pos < mDutyList.size()) {
                    askWhetherToCompleteDuty(pos);
                }
            }

            @Override
            public void onItemLongClicked(int pos) {
                // 롱클릭 시, 클릭된 할 일을 삭제할지 유저에게 물어본다.
                if (pos > -1 && pos < mDutyList.size()) {
                    askWhetherToDeleteDuty(pos);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // 옵션 메뉴를 리소스로부터 생성한다.
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // 옵션 메뉴 선택 이벤트를 처리한다.
        switch (item.getItemId()) {
            case R.id.btn_delete_completed_duties:
                // 완료된 할일을 삭제.
                deleteCompletedDuties();
                break;
            case R.id.btn_delete_all_duties:
                // 모든 할일을 삭제.
                deleteAllDuties();
                break;
            case R.id.btn_see_graph:
                // 그래프 액티비티 시작.
                startGraphActivity();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add) {
            // 새로운 할일을 더하기 위해 Add 액티비티를 시작한다.
            startAddActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 액티비티가 리턴한 결과들을 처리한다.

        if (requestCode == REQUEST_ADD_ACTIVITY && resultCode == RESULT_OK) {
            // Add 액티비티에서 새로운 할일을 추가했으므로 리스트를 업데이트한다.
            updateDutyList();
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // 캡쳐 액티비티에서 사진 촬영을 성공하였으므로, 이미지를 갤러리에 저장한다.
            Bitmap image = (Bitmap)data.getExtras().get("data");
            saveImageIntoGallery(image);
        }
    }

    private void saveImageIntoGallery(Bitmap bitmap) {
        try {
            // 저장 대상인 할 일의 이름과 현재 시간을 이용해 파일명을 만든다.
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = mCapturedDuty.getName() + "_" + timeStamp + "_";
            // 이미지를 해당 파일명으로 갤러리에 저장한다.
            ImageHelper.saveImage(this, bitmap, "증거사진", imageFileName);
            Toast.makeText(this, "사진이 갤러리에 저장되었습니다.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "사진을 저장하지 못했습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startAddActivity() {

        // Add 액티비티를 시작한다.
        Intent intent = new Intent(this, AddActivity.class);
        startActivityForResult(intent, REQUEST_ADD_ACTIVITY);
    }

    private void startGraphActivity() {

        // 그래프 액티비티를 시작한다.
        Intent intent = new Intent(this, GraphActivity.class);
        intent.putExtra(EXTRA_COMPLETED_RATE, getCompletedRate());
        startActivity(intent);
    }

    private void captureImage() {

        if (checkPermissions()) {
            // 카메라 액티비티를 시작한다.
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private double getCompletedRate() {

        // 모든 할 일 대비 완료된 할 일의 비율을 구한다.

        if (mDutyList.isEmpty()) {
            return 0;
        }

        int numCompleted = 0;
        for (Duty duty : mDutyList) {
            if (duty.isCompleted()) {
                numCompleted++;
            }
        }

        return (double) numCompleted / mDutyList.size();
    }

    private void askWhetherToCompleteDuty(int pos) {

        // 대화상자로 선택된 할 일을 완료 처리할지 유저에게 묻기.

        // 선택된 할 일
        Duty duty = mDutyList.get(pos);

        // 간단한 대화상자 띄우기 : 선택된 할 일이 미완료상태이면 완료상태로 만들어야 한다.
        if (!duty.isCompleted()) {
            new AlertDialog.Builder(this)
                    .setTitle("완료하기")
                    .setMessage(duty.getName() + ": 완료한 증거를 남기시겠습니까?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 유저가 네 응답할 경우 해당 할 일을 완료 처리하고 사진 캡쳐를 시작한다.
                            updateDuty(pos, true);
                            captureImage();
                            mCapturedDuty = duty;
                        }
                    })
                    .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 아니오 응답할 경우 해당 할일을 완료 처리하는 것 까지만 한다.
                            updateDuty(pos, true);
                        }
                    })
                    .show();
        } else {
            // 반대의 경우. 선택된 대화상자가 이미 완료상태면 미완료상태로 만들기.
            new AlertDialog.Builder(this)
                    .setTitle("완료 취소하기")
                    .setMessage(duty.getName() + ": 아직 완료하지 않으셨나요?")
                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // If user said yes, update the duty's completion state.
                            updateDuty(pos, false);
                        }
                    })
                    .setNegativeButton("아니오", null)
                    .show();
        }
    }

    private void askWhetherToDeleteDuty(int pos) {

        // 선택된 할 일을 삭제할지 묻는다.

        // 선택된 할 일
        Duty duty = mDutyList.get(pos);

        // 간단한 대화상자 띄우기
        new AlertDialog.Builder(this)
                .setTitle("삭제하기")
                .setMessage(duty.getName() + ", 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 네라고 응답하면 해당 할일을 삭제한다.
                        deleteDuty(pos);
                    }
                })
                .setNegativeButton("아니오", null)
                .show();
    }

    private void updateDuty(int pos, boolean isCompleted) {

        // 리스트에서 pos 위치의 할일의 완료 상태를 isCompleted 대로 설정한다.

        // 데이터베이스 상에서 설정
        int id = mDutyList.get(pos).getId();
        DatabaseHelper.getInstance(MainActivity.this).updateDuty(id, isCompleted);

        // 리스트와 리스트 뷰 상에서 설정
        mDutyList.get(pos).setIsCompleted(isCompleted);
        mDutyAdapter.notifyDataSetChanged();
    }

    private void deleteDuty(int pos) {

        // 리스트에서 pos 위치의 할일을 삭제한다.

        Duty duty = mDutyList.get(pos);

        // 데이터베이스 상에서 삭제.
        DatabaseHelper.getInstance(this).deleteDuty(duty.getId());

        // 리스트, 리스트 뷰 상에서 삭제
        mDutyList.remove(pos);
        mDutyAdapter.notifyDataSetChanged();

        updateMessageText();
    }

    private void deleteCompletedDuties() {

        // 완료된 모든 할 일을 삭제한다.

        // 완료된 할 일이 있는지 검사한다.
        boolean hasCompletedDuty = false;
        for (Duty duty : mDutyList) {
            if (duty.isCompleted()) {
                hasCompletedDuty = true;
                break;
            }
        }

        // 완료된 할 일이 없으면 리턴한다.
        if (!hasCompletedDuty) {
            Toast.makeText(this, "완료된 일이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 데이터베이스 상에서 완료된 할일 삭제.
        DatabaseHelper.getInstance(this).deleteCompletedDuties();

        // 리스트와 리스트뷰 상에서 완료된 할일 삭제.
        for (int i = mDutyList.size() - 1; i > -1; i--) {
            if (mDutyList.get(i).isCompleted()) {
                mDutyList.remove(i);
            }
        }
        mDutyAdapter.notifyDataSetChanged();
    }

    private void deleteAllDuties() {

        // 할 일이 없으면 리턴.
        if (mDutyList.isEmpty()) {
            Toast.makeText(this, "할 일이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 데이터베이스에서 모든 할 일 삭제.
        DatabaseHelper.getInstance(this).deleteAllDuties();

        // 리스트, 리스트뷰에서 모든 할 일 삭제.
        mDutyList.clear();
        mDutyAdapter.notifyDataSetChanged();
    }

    private void updateDutyList() {

        // 데이터베이스에 할일이 추가된 경우, 그에 따라 리스트, 리스트뷰를 갱신하낟.

        mDutyList.clear();
        mDutyList.addAll(loadDutyList());
        mDutyAdapter.notifyDataSetChanged();

        // 메세지 텍스트뷰를 갱신한다.
        updateMessageText();
    }

    private void updateMessageText() {

        // 할일이 없으면 새로 만들라는 텍스트를 보여준다.
        mMessageText.setVisibility(mDutyList.isEmpty() ? View.VISIBLE : View.GONE);
    }

}