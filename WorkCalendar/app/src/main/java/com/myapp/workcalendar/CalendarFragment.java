package com.myapp.workcalendar;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.myapp.workcalendar.adapters.WorkAdapter;
import com.myapp.workcalendar.helpers.SQLiteHelper;
import com.myapp.workcalendar.models.Work;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment implements
        CalendarView.OnDateChangeListener, View.OnClickListener {

    private List<Work> mIntakeList;
    private WorkAdapter mIntakeAdapter;

    private int mYear;
    private int mMonth;
    private int mDate;
    private TextView mDateText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        mDateText = v.findViewById(R.id.txt_date);

        // 달력에 날짜변경 리스너를 설정한다.
        CalendarView calendarView = v.findViewById(R.id.calendar);
        calendarView.setOnDateChangeListener(this);

        // 버튼에 리스너를 설정한다.
        FloatingActionButton addButton = v.findViewById(R.id.btn_add);
        addButton.setOnClickListener(this);

        // 리사이클러뷰를 초기화한다.
        buildIntakeRecycler(v);

        ScrollView scrollView = v.findViewById(R.id.scroll);
        scrollView.postDelayed(() -> scrollView.setScrollY(0), 100);

        return v;
    }

    // 리사이클러 뷰를 초기화한다.

    private void buildIntakeRecycler(View v) {

        RecyclerView intakeRecycler = v.findViewById(R.id.recycler_intake);
        intakeRecycler.setHasFixedSize(true);
        intakeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mIntakeList = new ArrayList<>();
        mIntakeAdapter = new WorkAdapter(mIntakeList);
        intakeRecycler.setAdapter(mIntakeAdapter);
    }

    // 리사이클러 뷰를 업데이트한다 : 날짜에 따라

    private void updateIntakeRecycler() {

        mIntakeList.clear();
        mIntakeList.addAll(SQLiteHelper.getInstance(getContext()).getWorkByDate(mYear, mMonth, mDate));
        mIntakeAdapter.notifyDataSetChanged();
    }

    // 날짜변경을 처리한다.

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

        // 선택된 날짜를 갱신한다.
        mYear = year;
        mMonth = month;
        mDate = dayOfMonth;
        String strDate = String.format(Locale.getDefault(),
                "%d년 %d월 %d일", mYear, mMonth+1, mDate);
        mDateText.setText(strDate);

        // 리사이클러뷰를 업데이트한다.
        updateIntakeRecycler();
    }

    // 버튼 클릭을 처리한다.

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add) {
            // 일 추가 대화상자를 띄운다.
            showAddWorkDialog();
        }
    }

    // 일 추가 대화상자를 띄운다.

    private void showAddWorkDialog() {

        View addWorkView = View.inflate(getContext(), R.layout.add_work, null);
        EditText titleEdit = addWorkView.findViewById(R.id.edit_work_title);
        EditText hoursEdit = addWorkView.findViewById(R.id.edit_work_hours);
        EditText hourlyWageEdit = addWorkView.findViewById(R.id.edit_hourly_wage);

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add)
                .setView(addWorkView)
                .setPositiveButton(R.string.add, (dialog, which) -> {

                    // 추가 버튼 클릭 시 입력 정보 DB에 추가하기.
                    String strTitle = titleEdit.getText().toString().trim();
                    String strHours = hoursEdit.getText().toString().trim();
                    String strHourlyWage = hourlyWageEdit.getText().toString().trim();

                    if (strTitle.isEmpty() || strHours.isEmpty() || strHourlyWage.isEmpty()) {
                        Toast.makeText(getContext(),
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    int hours = Integer.parseInt(strHours);
                    int hourlyWage = Integer.parseInt(strHourlyWage);

                    Work work = new Work(strTitle, hours, hourlyWage, mYear, mMonth, mDate);
                    SQLiteHelper.getInstance(getContext()).addWork(work);

                    // 리사이클러뷰 업데이트
                    updateIntakeRecycler();
                })
                .setNegativeButton("취소", null)
                .show();
    }

}





