package com.good.todoapp.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.good.todoapp.R;

import java.lang.ref.WeakReference;


public class GraphView extends View {

    private final Context mContext;

    // onDraw 에서 사용되는 그리기 변수들
    // 그래프 커브를 그릴 페인트
    private Paint mPaintCurve;
    // 그래프 내부를 칠할 페인트
    private Paint mPaintAreaWhole;
    // 그래프의 현재 값보다 왼쪽에 있는 부분을 칠할 페인트
    private Paint mPaintAreaLower;
    // 그래프 곡선을 나타내는 패스
    private final Path mPathWhole = new Path();
    // 그래프의 현재 값보다 왼쪽에 있는 곡선을 나타내는 패스
    private final Path mPathLower = new Path();
    // 배경색
    private int mBackgroundColor;
    // 커브의 두께
    private int mCurveThick;

    // 그래프의 현재 값
    private double mZValue;
    // 그래프의 최대/최소 값
    private double mZRange;

    // 슬라이드 효과를 위한 비동기 태스크
    private SlideAsyncTask mSlideAsyncTask;

    // 생성자
    public GraphView(Context context) {
        super(context);
        mContext = context;
        init(null);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    public GraphView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init(attrs);
    }

    // 생성자 헬퍼 함수
    void init(AttributeSet attrs) {

        // 모든 멤버를 초기화한다.

        mZValue = 0.0;
        mZRange = 8.0;
        mBackgroundColor = getResources().getColor(android.R.color.transparent);
        int colorCurve = getResources().getColor(android.R.color.holo_blue_light);
        int colorUpper = getResources().getColor(android.R.color.holo_blue_bright);
        int colorLower = getResources().getColor(android.R.color.holo_blue_dark);

        // xml 에서 주어진 속성에 따라 초기화한다.

        if (attrs != null) {
            TypedArray ar = mContext.obtainStyledAttributes(attrs, R.styleable.GraphView);
            mZValue = ar.getFloat(R.styleable.GraphView_zValue, (float)mZValue);
            mZRange = ar.getFloat(R.styleable.GraphView_zRange, (float)mZRange);
            mBackgroundColor = ar.getColor(R.styleable.GraphView_colorBackground, mBackgroundColor);
            colorCurve = ar.getColor(R.styleable.GraphView_colorCurve, colorCurve);
            colorUpper = ar.getColor(R.styleable.GraphView_colorUpper, colorUpper);
            colorLower = ar.getColor(R.styleable.GraphView_colorLower, colorLower);
            ar.recycle();
        }

        // 그리기 객체를 초기화한다

        mPaintCurve = new Paint();
        mPaintCurve.setStyle(Paint.Style.STROKE);
        mPaintCurve.setColor(colorCurve);
        mCurveThick = 7;
        mPaintCurve.setStrokeWidth(mCurveThick);
        mPaintCurve.setAntiAlias(true);

        mPaintAreaWhole = new Paint();
        mPaintAreaWhole.setStyle(Paint.Style.FILL);
        mPaintAreaWhole.setColor(colorUpper);

        mPaintAreaLower = new Paint();
        mPaintAreaLower.setStyle(Paint.Style.FILL);
        mPaintAreaLower.setColor(colorLower);

        mSlideAsyncTask = new SlideAsyncTask(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // onDraw 이벤트.

        canvas.drawColor(mBackgroundColor);
        canvas.translate(0, (int) Math.ceil(mCurveThick/2.0));

        // 정규분포함수를 따르는 Path 객체를 생성한다.
        float ptX = 0, ptY;
        boolean lowerPathOver = false;
        final double dz = 0.01;

        mPathWhole.reset();
        mPathLower.reset();
        mPathWhole.moveTo(0, getHeight());
        mPathLower.moveTo(0, getHeight());
        for (double z = -mZRange / 2; z < mZRange / 2; z += dz) {

            // z 값을 서서히 증가시키며 확률밀도를 구한다.
            // 확률밀도로부터 그래프의 y 좌표를 설정한다.
            double f = NormalDistribution.probability(z);
            ptX = getXByZValue(z);
            ptY = getYByProbability(f);
            mPathWhole.lineTo(ptX, ptY);
            if (z < mZValue) {
                mPathLower.lineTo(ptX, ptY);
            } else if (!lowerPathOver) {
                lowerPathOver = true;
                f = NormalDistribution.probability(mZValue);
                ptX = getXByZValue(mZValue);
                ptY = getYByProbability(f);
                mPathLower.lineTo(ptX, ptY);
                mPathLower.lineTo(ptX, getHeight());
                mPathLower.lineTo(0, getHeight());
            }
        }
        mPathWhole.lineTo(ptX, getHeight());
        mPathWhole.lineTo(0, getHeight());

        // 완성된 Path를 그린다.
        canvas.drawPath(mPathWhole, mPaintCurve);
        canvas.drawPath(mPathWhole, mPaintAreaWhole);
        canvas.drawPath(mPathLower, mPaintAreaLower);
    }

    // 접근자 (액세서)

    public double getZValue() {
        return mZValue;
    }

    public void setZValue(double z, boolean smoothSlide) {
        if (smoothSlide) {
            mSlideAsyncTask.setTargetZ(z);
            if (mSlideAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                setZValueWithoutAnimation(-mZRange);
            }
            else {
                mSlideAsyncTask.execute();
            }
        } else {
            setZValueWithoutAnimation(z);
        }
    }

    public double getZRange() {
        return mZRange;
    }

    public void setZRange(double range) {
        mZRange = range;
        invalidate();
    }

    private void setZValueWithoutAnimation(double z) {
        mZValue = z;
        invalidate();
    }

    private float getXByZValue(double z) {
        int width = getWidth();
        return ((float)width / 2) + (float)(z / mZRange * width);
    }

    private float getYByProbability(double f) {
        int height = getHeight();
        final double maximum = 1/ Math.sqrt(2* Math.PI);
        return height - (float)(f / maximum * height);
    }

    // 슬라이드 효과 비동기 객체

    static private final class SlideAsyncTask extends AsyncTask<Void, Void, Void> {

        static final double FRAMERATE = 40;
        static final double SPEED = 5;

        WeakReference<GraphView> reference;
        double mTargetZ;

        SlideAsyncTask(GraphView widget) {
            super();
            reference = new WeakReference<>(widget);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            GraphView widget = reference.get();
            if (widget == null) {
                return;
            }
            widget.setZValueWithoutAnimation(-widget.mZRange);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            // 그래프 값을 서서히 증가시킨다.

            GraphView widget = reference.get();
            if (widget == null) {
                return null;
            }
            while (!isCancelled()) {
                widget.mZValue += SPEED * (mTargetZ - widget.mZValue) / FRAMERATE;
                if (Math.abs(widget.mZValue - mTargetZ) <= 0.01) {
                    widget.mZValue = mTargetZ;
                }
                publishProgress();
                if (widget.mZValue >= mTargetZ)
                    break;
                try { Thread.sleep((int)(1000/FRAMERATE)); } catch (InterruptedException e) {
                    Log.d(getClass().toString(), e.toString());
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            GraphView widget = reference.get();
            if (widget == null) {
                return;
            }
            widget.setZValueWithoutAnimation(widget.mZValue);
        }

        void setTargetZ(double z) {
            mTargetZ = z;
        }

        double getTargetZ() {
            return mTargetZ;
        }
    }

    // 정규분포함수 정적 클래스

    public static final class NormalDistribution {

        // z 값에 따른 확률밀도
        public static double probability(double z) {
            return Math.exp(-z * z / 2) / Math.sqrt(2 * Math.PI);
        }
    }

}

