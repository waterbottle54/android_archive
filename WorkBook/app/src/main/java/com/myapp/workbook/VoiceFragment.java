package com.myapp.workbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.myapp.workbook.helpers.SQLiteHelper;
import com.myapp.workbook.models.Word;

import java.util.ArrayList;


public class VoiceFragment extends Fragment implements View.OnClickListener {

    public static final int REQUEST_CODE_RECOGNITION = 100;

    private TextView mSpellingText;
    private TextView mMeaningText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_voice, container, false);

        // 마이크 이미지에 리스너 설정
        ImageView micImage = v.findViewById(R.id.img_mic);
        micImage.setOnClickListener(this);

        mSpellingText = v.findViewById(R.id.txt_spelling);
        mMeaningText = v.findViewById(R.id.txt_meaning);

        return v;
    }

    // 버튼 클릭 처리

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.img_mic) {
            // 마이크 이미지 클릭 시, 음성인식 다이얼로그를 보여준다.
            showVoiceRecognitionDialog();
        }
    }

    // 음성인식 다이얼로그를 보여준다.

    private void showVoiceRecognitionDialog() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, R.string.please_speech);

        try {
            startActivityForResult(intent, REQUEST_CODE_RECOGNITION);

        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "음성인식을 실행할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 음성인식 결과 처리

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (requestCode == REQUEST_CODE_RECOGNITION
                && resultCode == Activity.RESULT_OK && data != null) {

            // 인식된 스펠링 확인
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String strSpelling = result.get(0);
            mSpellingText.setText(strSpelling);

            // DB 에서 단어 가져오기 (스펠링으로)
            Word word = SQLiteHelper.getInstance(getContext()).getWordBySpelling(strSpelling);
            if (word != null) {
                mMeaningText.setText(word.getMeaning());
            } else {
                mMeaningText.setText("단어를 찾을 수 없습니다.");
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}