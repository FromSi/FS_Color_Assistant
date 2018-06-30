package kz.sgq.colorassistant.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kz.sgq.colorassistant.R
import kz.sgq.colorassistant.ui.util.interfaces.OnClickItemColorListener

class ItemColor : View {
    private var valueLightness = 0f
    private var valueSaturation = 0f
    private var positionLightness = 0
    private var positionSaturation = 0
    private var itemPointerRadius = 0
    private var itemPointerHaloRadius = 0
    private var firstItemPointerRadius = 0
    private var firstItemPointerHaloRadius = 0
    private var translationOffset = 0f
    private var angle = -Math.PI / 2
    private var itemPointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var itemPointerHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var enableHalo = false
    private var boolLightness = false
    private var position: FloatArray? = null
    private var movingPointer = false
    private var positionX = 0f
    private var positionY = 0f
    private var positionCurrent = 0f
    private var clickItemColor: OnClickItemColorListener? = null
    private var min = 0
    private var act = false
    private var actY = 0f

    constructor(context: Context?) : super(context) {
        initConstructor(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initConstructor(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initConstructor(attrs, defStyleAttr)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.translate(translationOffset, translationOffset)

        if (enableHalo) canvas?.drawCircle(positionX, positionCurrent, itemPointerHaloRadius.toFloat(), itemPointerHaloPaint)

        canvas?.drawCircle(positionX, positionCurrent, itemPointerRadius.toFloat(), itemPointerPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val intrinsicSize = (firstItemPointerHaloRadius + itemPointerHaloRadius)
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(intrinsicSize, widthSize)
            MeasureSpec.UNSPECIFIED -> intrinsicSize
            else -> 0
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(intrinsicSize, heightSize)
            MeasureSpec.UNSPECIFIED -> intrinsicSize
            else -> 0
        }

        min = Math.min(width, height)
        setMeasuredDimension(min + (min / 4), ((min * 0.3f) * 2 + min).toInt())
        translationOffset = min * 0.5f
        positionX = (translationOffset - (min / 4)) / 2
        positionY = min * 0.3f
        positionCurrent = positionY
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val ave = ((min * 0.3f) * 2 + min) / 2

        parent.requestDisallowInterceptTouchEvent(true)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (positionY <= event.y &&
                        positionY + itemPointerHaloRadius * 2 >= event.y) {
                    actY = event.y
                    movingPointer = true
                    clickItemColor?.onClick()
                }
            }

            MotionEvent.ACTION_MOVE -> {
                val delDistension = (positionY * 0.3)
                val actTopOne = (positionY - (actY - event.y)) + delDistension
                val actBottomOne = positionY + itemPointerHaloRadius * 2
                val actBottomTwo = ((positionY + itemPointerHaloRadius * 2) +
                        (event.y - actY)) - delDistension

                if (movingPointer) {
                    act = if (positionY >= event.y ||
                            positionY >= actTopOne) {
                        true
                    } else if (actBottomOne <= event.y ||
                            actBottomOne <= actBottomTwo) {
                        true
                    } else false
                }
            }

            MotionEvent.ACTION_UP -> {
                if (ave >= event.y && act) {
//                    Log.d("TestACTION_UP", "IF1 ${event.y}")
                } else if (ave <= event.y && act) {
//                    Log.d("TestACTION_UP", "IF2 ${event.y}")
                } else {
//                    Log.d("TestACTION_UP", "IF3 ${event.y}")
                }

                movingPointer = false
            }
        }

        return true
    }

    private fun initConstructor(attrs: AttributeSet?, defStyleAttr: Int) {
        val resources = context.resources
        val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.ItemColor,
                defStyleAttr,
                0
        )

        itemPointerRadius = typedArray.getDimensionPixelSize(
                R.styleable.ItemColor_item_center_radius,
                resources.getDimensionPixelSize(R.dimen.item_pointer_radius)
        )

        itemPointerHaloRadius = typedArray.getDimensionPixelSize(
                R.styleable.ItemColor_item_center_halo_radius,
                resources.getDimensionPixelSize(R.dimen.item_pointer_halo_radius)
        )

        typedArray.recycle()

        firstItemPointerRadius = itemPointerRadius
        firstItemPointerHaloRadius = itemPointerHaloRadius

        itemPointerPaint.color = Color.BLUE
        itemPointerHaloPaint.color = Color.BLUE
        itemPointerHaloPaint.alpha = 0x50
    }

    fun setColor(color: Int) {
        itemPointerPaint.color = color
        itemPointerHaloPaint.color = color
        itemPointerHaloPaint.alpha = 0x50

        invalidate()
    }

    fun setEnable(bool: Boolean) {
        this.enableHalo = bool

        invalidate()
    }

    fun setPosition(position: FloatArray) {
        this.position = position
    }

    fun getPosition(): FloatArray? = position

    fun getLightness(): Float = valueLightness

    fun getBoolLightness(): Boolean = boolLightness

    fun getSaturation(): Float = valueSaturation

    fun setLightness(value: Float) {
        valueLightness = value
    }

    fun setBoolLightness(bool: Boolean) {
        boolLightness = bool
    }

    fun setSaturation(value: Float) {
        valueSaturation = value
    }

    fun setPositionLightness(position: Int) {
        positionLightness = position
    }

    fun setPositionSaturation(position: Int) {
        positionSaturation = position
    }

    fun getPositionLightness(): Int = positionLightness

    fun getPositionSaturation(): Int = positionSaturation

    fun setAngle(double: Double) {
        angle = double
    }

    fun getAngle(): Double = angle

    fun setOnClickItemColorListener(clickItemColor: OnClickItemColorListener) {
        this.clickItemColor = clickItemColor
    }
}