package com.holy.interiortalk.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.roundToInt

class LabelableImageView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr),
    GestureDetector.OnGestureListener {

    // 이미지뷰의 이미지가 클릭되었을 때 호출될 리스너
    interface OnTouchPositionListener {
        fun onTouchPosition(posX: Float, posY: Float)
    }

    private var onTouchPositionListener: OnTouchPositionListener? = null

    fun setOnTouchPositionListener(listener: OnTouchPositionListener) {
        this.onTouchPositionListener = listener
    }


    // 탭 감지를 위한 GestureDetector
    private val gestureDetector = GestureDetector(context, this)

    // 이미지 비트맵
    private var imageBitmap: Bitmap? = null

    // 레이블 리스트
    private val labels = ArrayList<PointF>()

    private val labelPaint = Paint()
    private val labelNumberPaint = Paint()

    init {
        labelPaint.color = 0xFF111111.toInt()
        labelNumberPaint.color = 0xFFCCCCCC.toInt()
        labelNumberPaint.textSize = 36f
        labelNumberPaint.textAlign = Paint.Align.CENTER
    }


    // 이미지 비트맵을 설정한다
    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        imageBitmap = bm
    }

    // 새 레이블을 추가한다
    fun addLabel(posX: Float, posY: Float) {
        labels.add(PointF(posX, posY))
        invalidate()
    }

    // 모든 레이블을 삭제한다

    fun clearLabel() {
        labels.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {

        super.onDraw(canvas)

        // 레이블을 이미지 위에 그린다
        labels.forEachIndexed { index, pos ->
            val posView = toViewCoordinates(pos.x, pos.y)
            canvas?.drawCircle(posView.x, posView.y, 28f, labelPaint)
            canvas?.drawText("${1 + index}", posView.x, posView.y + 10, labelNumberPaint)
        }
    }

    // 탭 발생 시 처리

    override fun onSingleTapUp(e: MotionEvent?): Boolean {

        if (e == null || imageBitmap == null) {
            return false
        }

        // 터치된 좌표를 이미지 상에서의 좌표(0~1)로 바꾼다
        val pos = toImageCoordinates(e.x, e.y)

        // 이미지 위의 유효 터치(0~1 사이)로 판정됨 : 리스너 호출
        if (pos.x in 0.0..1.0 && pos.y in 0.0..1.0) {
            onTouchPositionListener?.onTouchPosition(pos.x, pos.y)
            return true
        }

        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {}

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {}

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return gestureDetector.onTouchEvent(event)
    }

    // 뷰 기준 좌표를 이미지 상에서의 좌표(0~1)로 바꾼다
    private fun toImageCoordinates(x: Float, y: Float) : PointF {

        // 이미지뷰에 나타나는 이미지의 사이즈(iw, ih)를 구한다
        var iw = imageBitmap!!.width
        var ih = imageBitmap!!.height
        ih = (ih * width / iw.toDouble()).roundToInt()
        iw = width
        if (ih > height) {
            iw = (iw * height / ih.toDouble()).roundToInt()
            ih = height
        }

        // 이미지뷰에 나타나는 이미지의 좌상단 위치(ix, iy)를 구한다
        val ix = (width - iw) / 2
        val iy = (height - ih) / 2

        // 터치 좌표를 이미지의 좌상단에 대하여 표준화 (0 ~ 1) 한다
        // (이미지 좌상단 = (0, 0), 우하단 = (1, 1)
        val rx = (x - ix) / iw
        val ry = (y - iy) / ih

        return PointF(rx, ry)
    }

    // 이미지 기준 좌표를 뷰 상에서의 좌표로 바꾼다
    private fun toViewCoordinates(x: Float, y: Float) : PointF {

        // 이미지뷰에 나타나는 이미지의 사이즈(iw, ih)를 구한다
        var iw = imageBitmap!!.width
        var ih = imageBitmap!!.height
        ih = (ih * width / iw.toDouble()).roundToInt()
        iw = width
        if (ih > height) {
            iw = (iw * height / ih.toDouble()).roundToInt()
            ih = height
        }

        // 이미지뷰에 나타나는 이미지의 좌상단 위치(ix, iy)를 구한다
        val ix = (width - iw) / 2
        val iy = (height - ih) / 2

        // 터치 좌표를 이미지의 좌상단에 대하여 표준화 (0 ~ 1) 한다
        // (이미지 좌상단 = (0, 0), 우하단 = (1, 1)
        val vx = (x * iw) + ix
        val vy = (y * ih) + iy

        return PointF(vx, vy)
    }

}