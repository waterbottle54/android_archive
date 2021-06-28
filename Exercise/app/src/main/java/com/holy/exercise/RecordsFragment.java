package com.holy.exercise;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.holy.exercise.adapters.RecordAdapter;
import com.holy.exercise.helpers.SQLiteHelper;
import com.holy.exercise.models.Record;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class RecordsFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    LocalDate mSelectedDate;
    List<Record> mRecordList;
    RecordAdapter mRecordAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_records, container, false);

        // UI 초기화
        mSelectedDate = LocalDate.now();
        updateDateUI(view);
        buildRecordRecycler(view);
        updateTotalMinutesUI(view);

        // 클릭 리스너 설정
        TextView dateText = view.findViewById(R.id.txtDate);
        dateText.setOnClickListener(v -> showDatePickerDialog());

        return view;
    }

    // 기록 리사이클러 빌드

    private void buildRecordRecycler(View view) {

        RecyclerView recycler = view.findViewById(R.id.recyclerRecord);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        int year = mSelectedDate.getYear();
        int month = mSelectedDate.getMonthValue();
        int dayOfMonth = mSelectedDate.getDayOfMonth();
        mRecordList = SQLiteHelper.getInstance(getContext())
                .getRecordByDate(year, month, dayOfMonth);

        mRecordAdapter = new RecordAdapter(mRecordList);
        recycler.setAdapter(mRecordAdapter);

        // 기록 없음 텍스트뷰 표시
        TextView noRecordsText = view.findViewById(R.id.txtNoRecords);
        noRecordsText.setVisibility(mRecordList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    // 기록 리사이클러 업데이트

    private void updateRecordRecycler(View view) {

        int year = mSelectedDate.getYear();
        int month = mSelectedDate.getMonthValue();
        int dayOfMonth = mSelectedDate.getDayOfMonth();
        mRecordList.clear();
        mRecordList.addAll(SQLiteHelper.getInstance(getContext())
                .getRecordByDate(year, month, dayOfMonth));

        mRecordAdapter.notifyDataSetChanged();

        // 기록 없음 텍스트뷰 표시
        TextView noRecordsText = view.findViewById(R.id.txtNoRecords);
        noRecordsText.setVisibility(mRecordList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
    }

    // 날짜 UI 업데이트

    private void updateDateUI(View view) {

        TextView dateText = view.findViewById(R.id.txtDate);

        int year = mSelectedDate.getYear();
        int month = mSelectedDate.getMonthValue();
        int dayOfMonth = mSelectedDate.getDayOfMonth();
        String strDate = String.format(Locale.getDefault(),
                "%d년 %d월 %d일", year, month, dayOfMonth);
        dateText.setText(strDate);
    }

    // 총 운동시간 UI 업데이트

    private void updateTotalMinutesUI(View view) {

        TextView minutesText = view.findViewById(R.id.txtTotalMinutes);

        int totalSeconds = 0;
        for (Record record : mRecordList) {
            totalSeconds += record.getSeconds();
        }

        String strMinutes = String.format(Locale.getDefault(), "%d분", totalSeconds/60);
        minutesText.setText(strMinutes);
    }

    // 날짜 선택 대화상자 띄우기

    private void showDatePickerDialog() {

        int year = mSelectedDate.getYear();
        int month = mSelectedDate.getMonthValue();
        int dayOfMonth = mSelectedDate.getDayOfMonth();
        new DatePickerDialog(
                getContext(), this, year, month - 1, dayOfMonth).show();
    }

    // 날짜 선택 시 날짜변경 및 UI 업데이트

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        if (getView() == null) {
            return;
        }

        mSelectedDate = LocalDate.of(year, month + 1, dayOfMonth);
        updateDateUI(getView());
        updateRecordRecycler(getView());
        updateTotalMinutesUI(getView());
    }

}




