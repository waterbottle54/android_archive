package com.good.todoapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.good.todoapp.R;
import com.good.todoapp.models.Duty;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DutyAdapter extends BaseAdapter {

    // 아이템 클릭 리스너
    public interface OnItemClickListener {
        void onItemClicked(int pos);
        void onItemLongClicked(int pos);
    }

    // 할일 리스트
    private final List<Duty> dutyList;
    // 아이템 클릭 리스너의 인스턴스
    public OnItemClickListener onItemClickListener;

    // 생성자
    public DutyAdapter(List<Duty> dutyList) {
        this.dutyList = dutyList;
    }

    // 리스너 설정
    public void setOnItemClickListener(OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    // 어댑터 뷰 함수
    @Override
    public int getCount() {
        return dutyList.size();
    }

    @Override
    public Object getItem(int position) {
        return dutyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            // 생성된 뷰가 없으면 리소스로부터 생성한다
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.duty_item, parent, false);
        }

        // 아이템 뷰의 자식 뷰들을 구한다.
        ImageView stateImage = convertView.findViewById(R.id.image_duty_state);
        TextView nameText = convertView.findViewById(R.id.txt_duty_name);
        TextView stateText = convertView.findViewById(R.id.txt_duty_state);
        TextView dateText = convertView.findViewById(R.id.txt_duty_date);
        TextView remainingDaysText = convertView.findViewById(R.id.txt_duty_remaining_days);

        // 할일 데이터를 상응하는 자식 뷰들에게 입력한다.
        Duty duty = dutyList.get(position);

        // 할일 완료 여부에 따라 이미지 뷰의 드로블을 다르게 한다.
        if (duty.isCompleted()) {
            stateImage.setImageResource(R.drawable.good_job);
        } else {
            stateImage.setImageResource(duty.isImportant() ?
                    R.drawable.ic_note_blue : R.drawable.ic_note);
        }

        // 이름, 완료여부를 표시한다.
        nameText.setText(duty.getName());
        stateText.setText(duty.isCompleted() ? "완료" : "미완료");

        // 할일의 (마감) 날짜로부터 날짜 문자열을 만든다.

        int year = duty.getDate().getYear();
        int month = duty.getDate().getMonthValue();
        int dayOfMonth = duty.getDate().getDayOfMonth();
        DayOfWeek dayOfWeek = duty.getDate().getDayOfWeek();
        String strDate = String.format(Locale.getDefault(),
                "%d년 %d월 %d일 (%s)",
                year, month, dayOfMonth, getDayName(dayOfWeek));

        // 날짜를 표시한다.
        dateText.setText(strDate);

        // 마감까지 남은 일수를 계산한다.
        long epochDayNow = ZonedDateTime.now(ZoneId.systemDefault()).toLocalDate().toEpochDay();
        long remainingDays = duty.getDate().toEpochDay() - epochDayNow;
        String strRemainingDays;
        // 일수를 표시한다.
        if (remainingDays == 0) {
            strRemainingDays = "오늘";
        } else if (remainingDays == 1) {
            strRemainingDays = "내일";
        } else {
            strRemainingDays = String.format(Locale.getDefault(), "%d일 남음", remainingDays);
        }
        remainingDaysText.setText(strRemainingDays);

        // 리스너의 함수들을 아이템 뷰에 연결한다.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    // 클릭 이벤트
                    onItemClickListener.onItemClicked(position);
                }
            }
        });
        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    // 롱클릭 이벤트
                    onItemClickListener.onItemLongClicked(position);
                    return true;
                }
                return false;
            }
        });

        return convertView;
    }

    private String getDayName(DayOfWeek dayOfWeek) {

        // 1~7 의 숫자로 주어지는 dayOfWeek 객체를 요일 문자열로 바꾼다.
        final String[] names = {
             "월", "화", "수", "목", "금", "토", "일",
        };
        return names[dayOfWeek.getValue() - 1];
    }

}
