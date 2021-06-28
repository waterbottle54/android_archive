package com.good.paintboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Pair;

import androidx.annotation.Nullable;


// GoodPaintBoard 뷰 : PaintBoard 를 customizing 할 수 있다.
// - 곡선의 색상 지정
// - 곡선의 굵기 지정

public class GoodPaintBoard extends PaintBoard {

    // 생성자

    public GoodPaintBoard(Context context) {
        super(context);
        init();
    }

    public GoodPaintBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GoodPaintBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    // 패스 색상, 굵기 설정

    public void setPathStyle(int color, int width) {

        setPathStyleProtected(color, width);
    }

}
