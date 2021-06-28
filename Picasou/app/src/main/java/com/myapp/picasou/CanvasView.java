package com.myapp.picasou;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

@SuppressWarnings("unused")
public class CanvasView extends View {

    // 그리기 모드 상수 코드
    public static final int PAINT_MODE_LINE = 100;
    public static final int PAINT_MODE_RECT = 101;
    public static final int PAINT_MODE_CIRCLE = 102;
    public static final int PAINT_MODE_CURVE = 103;
    public static final int PAINT_MODE_ERASE = 104;

    // 필터 모드 상수 코드
    public static final int FILTER_NONE = 100;
    public static final int FILTER_EMBOSS = 101;
    public static final int FILTER_BLUR = 102;

    // 그리기 모드
    private int mPaintMode;

    // 필터 모드
    private int mFilterMode;

    // 펜 굵기 (pixel)
    private double mPenWidth;

    // 펜 색상 : 초기값은 흰색
    private int mPenColor;

    // 비트맵 : 실제 그림이 그려진다.
    private Bitmap mBitmap;

    // 비트맵 사각형
    private Rect mBitmapRect;

    // 캔버스 객체 : 비트맵에 그린다.
    private Canvas mCanvas;

    // 페인트 객체 / 필터 객체
    private Paint mErasePaint;
    private Paint mMainPaint;
    private EmbossMaskFilter mEmbossFilter;
    private BlurMaskFilter mBlurFilter;

    // 미리보기 패스 객체
    // - 터치 입력을 받으면 미리보기 패스를 사용자에게 보여준다.
    // - 터치 UP 이 되면 미리도형 패스가 비트맵에 그려진다.
    private Path mPreviewPath;

    // 미리보기 패스의 시작점
    private PointF mPreviewStartPoint;
    // 최근에 터치 이벤트가 일어난 좌표점
    private PointF mLastPoint;

    // 생성자

    public CanvasView(Context context) {
        super(context);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // 메인 페인트 객체 생성
        mMainPaint = new Paint(Paint.DITHER_FLAG);
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setFilterBitmap(true);

        // 지우기 페인트 객체 생성
        mErasePaint = new Paint();
        mErasePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mErasePaint.setColor(Color.WHITE);

        // Emboss, Blur 필터 객체 생성
        mEmbossFilter = new EmbossMaskFilter(
                new float[]{0, 1, 1},0.7f, 6.0f, 7.5f);
        mBlurFilter = new BlurMaskFilter(10, BlurMaskFilter.Blur.NORMAL);

        // 그리기 설정 초기화
        setPaintMode(PAINT_MODE_LINE);
        setFilterMode(FILTER_NONE);
        setPenWidth(4.0);
        setPenColor(Color.WHITE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // 뷰 사이즈에 맞추어 비트맵, 캔버스 객체 생성
        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mBitmapRect = new Rect(0, 0, w, h);

        // 캔버스 객체 생성
        mCanvas = new Canvas(mBitmap);
    }

    // 그리기 모드 설정 / 확인

    public void setPaintMode(int paintMode) {
        mPaintMode = paintMode;
    }

    public int getPaintMode() {
        return mPaintMode;
    }

    // 필터 모드 설정 / 확인

    public void setFilterMode(int filterMode) {

        mFilterMode = filterMode;

        switch (mFilterMode) {
            case FILTER_EMBOSS:
                mMainPaint.setMaskFilter(mEmbossFilter);
                break;
            case FILTER_BLUR:
                mMainPaint.setMaskFilter(mBlurFilter);
                break;
            case FILTER_NONE:
                mMainPaint.setMaskFilter(null);
                break;
        }

        invalidate();
    }

    public int getFilterMode() {
        return mFilterMode;
    }

    // 펜 굵기 설정 / 확인

    public void setPenWidth(double penWidth) {
        this.mPenWidth = penWidth;

        // 메인, 지우기 페인트 객체 갱신
        mMainPaint.setStrokeWidth((int) mPenWidth);
        mErasePaint.setStrokeWidth((int) mPenWidth);
    }

    public double getPenWidth() {
        return mPenWidth;
    }

    // 펜 색상 설정 / 확인

    public void setPenColor(int penColor) {
        this.mPenColor = penColor;

        // 메인 페인트 객체 갱신
        mMainPaint.setColor(mPenColor);
    }

    public int getPenColor() {
        return mPenColor;
    }

    // 전체 지우기

    public void clear() {

        mCanvas.drawRect(mBitmapRect, mErasePaint);
        invalidate();
    }

    // 그리기 메세지 처리

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        // 뷰 전체에 비트맵 그리기 : 필터 적용
        canvas.drawBitmap(mBitmap, mBitmapRect, mBitmapRect, null);

        // 미리보기 패스가 있으면 그린다.
        if (mPreviewPath != null) {
            canvas.drawPath(mPreviewPath, mMainPaint);
        }
    }

    // 터치 입력 처리

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        PointF point = new PointF(event.getX(), event.getY());

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mPaintMode == PAINT_MODE_CURVE || mPaintMode == PAINT_MODE_ERASE) {
                    // Curve 그리기 / Erase 시작
                    mLastPoint = new PointF(point.x, point.y);
                } else {
                    // Line, Rect, Circle 그리기 시작 : 미리보기 시작점 저장
                    mPreviewStartPoint = new PointF(point.x, point.y);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (mPreviewStartPoint != null) {
                    // Line, Rect, Circle 미리보기 생성 : 미리보기 시작 좌표과 현재 터치 좌표 이용
                    mPreviewPath = getPreviewPath(mPreviewStartPoint, point);
                    invalidate();
                    return true;
                } else if (mLastPoint != null) {
                    // Curve 그리기 / Erase 진행 : 비트맵에 선분 추가. 마지막 좌표와 현재 터치 좌표 이용
                    mCanvas.drawLine(mLastPoint.x, mLastPoint.y, point.x, point.y,
                            mPaintMode == PAINT_MODE_CURVE ? mMainPaint : mErasePaint);
                    mLastPoint = new PointF(point.x, point.y);
                    invalidate();
                    return true;
                }

            case MotionEvent.ACTION_UP:
                if (mPreviewPath != null) {
                    // Line, Rect, Circle 그리기 완료 : 미리보기 패스를 비트맵에 출력
                    mCanvas.drawPath(mPreviewPath, mMainPaint);
                    mPreviewPath = null;
                    mPreviewStartPoint = null;
                    invalidate();
                    return true;
                } else if (mLastPoint != null) {
                    // Curve 그리기 / Erase 종료
                    mLastPoint = null;
                }
                break;
        }

        return false;
    }

    // 그리기 모드와 지정 좌표에 의한 패스(도형) 생성

    private Path getPreviewPath(PointF a, PointF b) {

        Path path = new Path();

        switch (mPaintMode) {
            case PAINT_MODE_LINE:
                path.moveTo(a.x, a.y);
                path.lineTo(b.x, b.y);
                break;
            case PAINT_MODE_RECT:
                path.addRect(
                        Math.min(a.x, b.x),
                        Math.min(a.y, b.y),
                        Math.max(a.x, b.x),
                        Math.max(a.y, b.y),
                        Path.Direction.CCW);
                break;
            case PAINT_MODE_CIRCLE:
                path.addArc(
                        Math.min(a.x, b.x),
                        Math.min(a.y, b.y),
                        Math.max(a.x, b.x),
                        Math.max(a.y, b.y),
                        0, 360);
                break;
        }

        return path;
    }


}
