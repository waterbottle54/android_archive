package com.holy.caloriecalendar;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.holy.caloriecalendar.adapters.IntakeAdapter;
import com.holy.caloriecalendar.helpers.SQLiteHelper;
import com.holy.caloriecalendar.models.Food;
import com.holy.caloriecalendar.models.Intake;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class CalendarFragment extends Fragment implements
        CalendarView.OnDateChangeListener, View.OnClickListener {

    private List<Intake> mIntakeList;       // 현재 선택된 날짜의 섭취정보 리스트
                                            // (달력 아래의 섭취정보 리사이클러뷰에 표시된다)

    private IntakeAdapter mIntakeAdapter;   // 섭취정보 리사이클러뷰에 연결할 어댑터

    private int mYear;                      // 달력에서 현재 선택된 연도 (ex. 2021)
    private int mMonth;                     // 현재 선택된 달 (1~12)
    private int mDayOfMonth;                // 현재 선택된 일자 (1~30)

    private TextView mDateText;             // 현재 선택된 날짜를 표시하는 텍스트뷰
    private TextView mDailyIntakeText;      // 현재 선택된 날짜의 총 섭취 칼로리를 표시하는 텍스트뷰

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        mDateText = v.findViewById(R.id.txt_date);
        mDailyIntakeText = v.findViewById(R.id.txt_daily_intake);

        LocalDate date = LocalDate.now();
        mYear = date.getYear();
        mMonth = date.getMonthValue();
        mDayOfMonth = date.getDayOfMonth();

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

        onSelectedDayChange(calendarView, mYear, mMonth - 1, mDayOfMonth);

        return v;
    }


    // 리사이클러 뷰를 초기화한다.

    private void buildIntakeRecycler(View v) {

        RecyclerView intakeRecycler = v.findViewById(R.id.recycler_intake);
        intakeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        mIntakeList = new ArrayList<>();
        mIntakeAdapter = new IntakeAdapter(mIntakeList);
        intakeRecycler.setAdapter(mIntakeAdapter);

        mIntakeAdapter.setOnItemClickListener(this::showModifyIntakeDialog);
    }

    // 리사이클러 뷰를 업데이트한다 : 날짜에 따라

    private void updateIntakeRecycler() {

        mIntakeList.clear();
        mIntakeList.addAll(SQLiteHelper.getInstance(getContext())
                .getIntakeByDate(mYear, mMonth, mDayOfMonth));
        mIntakeAdapter.notifyDataSetChanged();
    }

    // 일일 섭취량 텍스트뷰를 업데이트한다.

    private void updateDailyIntake() {

        int dailyIntakeInKcal = SQLiteHelper.getInstance(getContext())
                .getDailyIntake(mYear, mMonth, mDayOfMonth);

        String strDailyIntake = String.format(Locale.getDefault(),
                "일일 섭취량 : %dkcal", dailyIntakeInKcal);
        mDailyIntakeText.setText(strDailyIntake);
    }

    // 날짜변경을 처리한다.

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

        // 선택된 날짜를 갱신한다.
        mYear = year;
        mMonth = month + 1;
        mDayOfMonth = dayOfMonth;
        String strDate = String.format(Locale.getDefault(),
                "%d년 %d월 %d일", mYear, mMonth, mDayOfMonth);
        mDateText.setText(strDate);

        // 리사이클러뷰를 업데이트한다.
        updateIntakeRecycler();

        // 일간 섭취량을 업데이트한다.
        updateDailyIntake();
    }

    // 버튼 클릭을 처리한다.

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_add) {
            // 섭취 검색 대화상자를 띄운다.
            getActivity().onSearchRequested();
        }
    }

    // 섭취정보 변경 대화상자를 보여준다.

    private void showModifyIntakeDialog(int position) {

        View modifyIntakeView = View.inflate(getContext(), R.layout.add_intake, null);
        EditText titleEdit = modifyIntakeView.findViewById(R.id.edit_intake_title);
        EditText kcalEdit = modifyIntakeView.findViewById(R.id.edit_intake_kcals);

        Intake selectedIntake = mIntakeList.get(position);
        titleEdit.setText(selectedIntake.getTitle());
        kcalEdit.setText(String.valueOf(selectedIntake.getKcals()));

        new AlertDialog.Builder(getContext())
                .setTitle(R.string.add)
                .setView(modifyIntakeView)
                .setPositiveButton("변경", (dialog, which) -> {

                    // 변경 버튼 클릭 시 입력 정보 DB 에서 업데이트하기.
                    String strTitle = titleEdit.getText().toString().trim();
                    String strKcals = kcalEdit.getText().toString().trim();

                    if (strTitle.isEmpty() || strKcals.isEmpty()) {
                        Toast.makeText(getContext(),
                                "모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intake intake = new Intake(
                            selectedIntake.getId(), strTitle, Integer.parseInt(strKcals),
                            selectedIntake.getYear(), selectedIntake.getMonth(), selectedIntake.getDayOfMonth(),
                            selectedIntake.getHour(), selectedIntake.getMinute()
                    );

                    SQLiteHelper.getInstance(getContext()).updateIntake(intake);

                    // 리사이클러뷰 업데이트
                    updateIntakeRecycler();
                    updateDailyIntake();
                })
                .setNeutralButton("삭제", (dialog, which) -> {

                    // 해당 섭취정보 DB 에서 삭제하기
                    SQLiteHelper.getInstance(getContext()).deleteIntake(selectedIntake.getId());

                    // 섭취정보 리사이클러뷰 업데이트
                    updateIntakeRecycler();
                    updateDailyIntake();
                })
                .setNegativeButton("취소", null)
                .show();
    }

}