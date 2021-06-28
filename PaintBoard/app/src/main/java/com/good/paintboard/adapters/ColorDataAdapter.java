package com.good.paintboard.adapters;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.good.paintboard.ColorPaletteDialog;

import java.util.List;


public class ColorDataAdapter extends BaseAdapter {

    // 색상 리스트
    private List<Integer> colorList;

    // 그리드 넓이 픽셀
    private int gridWidth;

    // 컬러 선택 리스너
    private ColorPaletteDialog.OnColorSelectedListener onColorSelectedListener;

    // 생성자
    public ColorDataAdapter(List<Integer> colorList, int gridWidth,
                            ColorPaletteDialog.OnColorSelectedListener onColorSelectedListener) {

        this.colorList = colorList;
        this.gridWidth = gridWidth;
        this.onColorSelectedListener = onColorSelectedListener;
    }

    // 어댑터 구현

    @Override
    public int getCount() {
        return colorList.size();
    }

    @Override
    public Object getItem(int position) {
        return colorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 컨버트 뷰가 없으면 생성한다.
        if (convertView == null) {
            convertView = new LinearLayout(parent.getContext());
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(gridWidth, gridWidth);
            convertView.setLayoutParams(params);
        }

        // 컨버트 뷰의 배경을 지정된 색으로 설정한다.
        int color = colorList.get(position);
        convertView.setBackgroundColor(color);

        // 컨버트 뷰에 클릭 리스너를 지정한다.
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onColorSelectedListener != null) {
                    onColorSelectedListener.onColorSelected(color);
                }
            }
        });

        return convertView;
    }
}
