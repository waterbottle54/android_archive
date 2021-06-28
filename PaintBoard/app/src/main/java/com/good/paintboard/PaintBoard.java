package com.good.paintboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

// PaintBoard 뷰 : 가장 간단한 형태의 페인트보드.
// - 사용자의 터치 입력을 받아 곡선을 그린다.
// - 곡선 처리에는 Path 클래스를 이용한다.

public class PaintBoard extends View {

    public static final int TOUCH_TOLERANCE = 5;
    private static final int MAX_UNDO_STACK_SIZE = 255;

    // 패스 색상 및 굵기
    private int mPathColor;
    private int mPathWidth;

    // 그리기 객체
    protected List<Pair<Path, Paint>> mPaths;

    // 그리기 기록 스택
    protected Stack<List<Pair<Path, Paint>>> mUndoStack;

    // 그리기 관련 변수
    private int mInvalidateExtraBorder = 3;
    private float mCurveEndX = -1;
    private float mCurveEndY = -1;

    // 터치 상태 변수
    private boolean changed = false;
    private float lastX = -1;
    private float lastY = -1;


    // 생성자

    public PaintBoard(Context context) {
        super(context);
        init();
    }

    public PaintBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {

        mPathWidth = 10;
        mPathColor = Color.BLACK;

        // 첫 빈 패스를 만든다.
        mPaths = new ArrayList<>();
        initPaths();

        mUndoStack = new Stack<>();
    }

    // 패스 색상, 굵기 액세서

    protected void setPathStyleProtected(int color, int width) {
        mPathColor = color;
        mPathWidth = width;

        // 주어진 스타일로 새로운 빈 패스를 만든다.
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setStyle(Paint.Style.STROKE);

        mPaths.add(new Pair<Path, Paint>(
                new Path(), paint
        ));
    }

    public int getPathColor() {
        return mPathColor;
    }

    public int getPathWidth() {
        return mPathWidth;
    }

    // 그리기

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 패스를 그린다.
        for (Pair<Path, Paint> pathPaintPair : mPaths) {
            canvas.drawPath(pathPaintPair.first, pathPaintPair.second);
        }
    }

    // 터치 이벤트 처리

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                // 터치를 끝낸 경우 :
                changed = true;
                Rect rect = touchUp(event, false);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;
            case MotionEvent.ACTION_DOWN:
                // 터치를 시작한 경우 :
                saveUndo();
                rect = touchDown(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                // 터치를 이동한 경우 :
                rect = touchMove(event);
                if (rect != null) {
                    invalidate(rect);
                }
                return true;
        }
        return false;
    }

    private Rect touchDown(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();
        lastX = x;
        lastY = y;
        Rect mInvalidRect = new Rect();

        getLastPath().moveTo(x, y);
        final int border = mInvalidateExtraBorder;

        mInvalidRect.set(
                (int)x - border,
                (int)y - border,
                (int)x + border,
                (int)y + border);

        mCurveEndX = x;
        mCurveEndY = y;
        return mInvalidRect;
    }

    private Rect touchMove(MotionEvent event) {

        Rect rect = processMove(event);
        return rect;
    }

    private Rect touchUp(MotionEvent event, boolean b) {

        Rect rect = processMove(event);
        return rect;
    }

    private Rect processMove(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();
        final float dx = Math.abs(x - lastX);
        final float dy = Math.abs(y - lastY);
        Rect mInvalidRect = new Rect();
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            final int border = mInvalidateExtraBorder;
            mInvalidRect.set(
                    (int)mCurveEndX - border,
                    (int)mCurveEndY - border,
                    (int)mCurveEndX + border,
                    (int)mCurveEndY + border);
            float cX = mCurveEndX = (x + lastX) / 2;
            float cY = mCurveEndY = (y + lastY) / 2;

            getLastPath().quadTo(lastX, lastY, cX, cY);
            mInvalidRect.union(
                    (int)lastX - border,
                    (int)lastY - border,
                    (int)lastX + border,
                    (int)lastY + border);
            mInvalidRect.union(
                    (int)lastX - border,
                    (int)lastY - border,
                    (int)lastX + border,
                    (int)lastY + border);

            lastX = x;
            lastY = y;
        }

        return mInvalidRect;
    }

    protected void saveUndo() {

        // Undo 스택이 가득 찬 경우, 오래된 기록을 지운다.
        if (mUndoStack.size() >= MAX_UNDO_STACK_SIZE) {
            mUndoStack.remove(0);
        }

        // 스택에 현재 기록을 복사하여 추가한다.
        List<Pair<Path, Paint>> now = new ArrayList<>();
        for (Pair<Path, Paint> pair : mPaths) {
            // 스택의 Path 와 Paint 는 독립적이어야 하므로 깊은 복사가 필요하다.
            Path newPath = new Path(pair.first);
            Paint newPaint = new Paint(pair.second);
            now.add(new Pair<>(newPath, newPaint));
        }

        mUndoStack.add(now);
    }

    protected void clearUndo() {

        mUndoStack.clear();
    }

    protected void initPaths() {
        mPaths.clear();
        setPathStyleProtected(mPathColor, mPathWidth);
    }

    // 마지막 패스

    private Path getLastPath() {
        return mPaths.get(mPaths.size() - 1).first;
    }

}
