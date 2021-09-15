package com.cool.nfckiosk.util.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.cardview.widget.CardView;

public class InterceptTouchCardView extends CardView {

    public InterceptTouchCardView(Context context) {
        super(context);
    }

    public InterceptTouchCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptTouchCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}