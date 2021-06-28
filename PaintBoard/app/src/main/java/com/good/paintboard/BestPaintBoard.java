package com.good.paintboard;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;


// BestPaintBoard 뷰 : GoodPaintBoard 를 상속하여 추가 기능 구현.
// - Undo 기능
// - Erase 기능

public class BestPaintBoard extends GoodPaintBoard {

    public static final int ERASE_RADIUS = 10;


    public BestPaintBoard(Context context) {
        super(context);
    }

    public BestPaintBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BestPaintBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean undo() {

        // Undo 스택에 기록이 있을 경우, 최신 기록을 불러온다.
        if (!mUndoStack.empty()) {
            mPaths = mUndoStack.pop();
            invalidate();
            return true;
        }

        // Undo 성공 여부 리턴.
        return false;
    }

    public boolean erase() {

        if (!mPaths.isEmpty()) {
            // 현재 Path 를 지우고 화면을 다시 그린다.
            initPaths();
            clearUndo();
            invalidate();
            return true;
        }

        // Erase 성공 여부 리턴
        return false;
    }

}
