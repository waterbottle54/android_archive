package com.myapp.workbook;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myapp.workbook.adapters.WordAdapter;
import com.myapp.workbook.helpers.SQLiteHelper;
import com.myapp.workbook.models.Word;

import java.util.List;
import java.util.Locale;


public class WordListFragment extends Fragment implements
    View.OnClickListener, WordAdapter.OnVoiceClickListener {

    // 단어 리스트
    private List<Word> mWordList;
    // 단어 어댑터
    private WordAdapter mWordAdapter;

    // TTS (Text to Speech) 객체
    private TextToSpeech mTTS;
    // 음량 시크바
    private SeekBar mVolumeBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_word_list, container, false);

        // 단어 리사이클러뷰를 초기화한다.
        buildWordRecycler(v);

        // 버튼에 리스너를 설정한다.
        FloatingActionButton addWordButton = v.findViewById(R.id.btn_add_word);
        addWordButton.setOnClickListener(this);

        // TTS 객체를 초기화한다.
        mTTS = new TextToSpeech(getContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                mTTS.setLanguage(Locale.ENGLISH);
            } else {
                Toast.makeText(getContext(),
                        "TTS 초기화에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {

        // TTS 종료료
       if (mTTS != null) {
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroyView();
    }

    // 단어 리사이클러 뷰를 초기화한다.

    private void buildWordRecycler(View v) {

        RecyclerView wordRecycler = v.findViewById(R.id.recycler_word);
        wordRecycler.setHasFixedSize(true);
        wordRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // DB 에서 단어 정보를 불러오고 어댑터를 생성한다.
        mWordList = SQLiteHelper.getInstance(getContext()).getAllWords();
        mWordAdapter = new WordAdapter(mWordList);
        wordRecycler.setAdapter(mWordAdapter);

        // 어댑터에 음성 버튼 리스너를 설정한다.
        mWordAdapter.setOnVoiceClickListener(this);
    }

    // 단어 리사이클러 뷰를 업데이트한다.

    private void updateWordRecycler() {

        mWordList.clear();
        mWordList.addAll(SQLiteHelper.getInstance(getContext()).getAllWords());
        mWordAdapter.notifyDataSetChanged();
    }

    // 버튼 클릭을 처리한다.

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add_word) {
            // 단어 추가 대화상자를 보여준다.
            showAddWordDialog();
        }
    }

    // 단어 추가 대화상자를 보여준다.

    private void showAddWordDialog() {

        // 단어 입력 뷰를 생성한다.
        View addWordView = View.inflate(getContext(), R.layout.view_add_word, null);
        EditText spellingEdit = addWordView.findViewById(R.id.edit_spelling);
        EditText meaningEdit = addWordView.findViewById(R.id.edit_meaning);

        // 대화상자를 띄운다.
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add_word)
                .setView(addWordView)
                .setPositiveButton(R.string.add_word, (dialog, which) -> {
                    // 추가 버튼 클릭 시, 입력된 단어를 DB 에 추가한다.
                    String strSpelling = spellingEdit.getText().toString().trim();
                    String strMeaning = meaningEdit.getText().toString().trim();
                    if (strSpelling.isEmpty() || strMeaning.isEmpty()) {
                        Toast.makeText(getContext(),
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    Word word = new Word(strSpelling, strMeaning);
                    SQLiteHelper.getInstance(getContext()).addWord(word);

                    // 단어 리사이클러뷰를 업데이트한다.
                    updateWordRecycler();
                    Toast.makeText(getContext(),
                            "단어가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    // 단어의 음성 버튼 클릭을 처리한다.

    @Override
    public void onVoiceClick(int position) {

        // 볼륨 시크바를 참조하여 볼륨을 설정한다.
        Bundle params = new Bundle();
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, mVolumeBar.getProgress()/100.0f);

        String strSpelling = mWordList.get(position).getSpelling();
        mTTS.speak(strSpelling, TextToSpeech.QUEUE_FLUSH, params, null);
    }

    // 옵션 메뉴 생성 (볼륨 시크바)

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_options, menu);

        MenuItem volumeItem = menu.findItem(R.id.item_volume);
        View volumeView = volumeItem.getActionView();
        mVolumeBar = volumeView.findViewById(R.id.seek_volume);
    }
}


