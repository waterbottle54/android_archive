package com.myapp.workcalendar;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myapp.workcalendar.adapters.WageAdapter;
import com.myapp.workcalendar.helpers.SQLiteHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class MonthlyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_monthly, container, false);

        // 리사이클러뷰를 초기화한다.
        buildWageRecycler(v);

        // 이번달 수입을 보여준다.
        Calendar now = new GregorianCalendar();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH);
        int monthlyWage = SQLiteHelper.getInstance(getContext()).getMonthlyWage(year, month);
        String strMonthlyWage = String.format(Locale.getDefault(),
                "%,d원", monthlyWage);

        TextView wageThisMonthText = v.findViewById(R.id.txt_wage_this_month);
        wageThisMonthText.setText(strMonthlyWage);

        return v;
    }

    // 리사이클러 뷰를 초기화한다.

    private void buildWageRecycler(View v) {

        RecyclerView wageRecycler = v.findViewById(R.id.recycler_wage);
        wageRecycler.setHasFixedSize(true);
        wageRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        // DB 에서 월별 수입을 불러온다. (현재 달부터 2000년 1월까지)
        List<Integer> wageList = new ArrayList<>();
        List<Integer> yearList = new ArrayList<>();
        List<Integer> monthList = new ArrayList<>();

        Calendar now = new GregorianCalendar();
        for (int year = now.get(Calendar.YEAR); year >= 2000; year--) {
            for (int month = 11; month >= 0; month--) {
                int monthlyWage = SQLiteHelper.getInstance(getContext()).getMonthlyWage(year, month);
                if (monthlyWage > 0) {
                    wageList.add(monthlyWage);
                    yearList.add(year);
                    monthList.add(month);
                }
            }
        }

        WageAdapter wageAdapter = new WageAdapter(wageList, yearList, monthList);
        wageRecycler.setAdapter(wageAdapter);
    }
}