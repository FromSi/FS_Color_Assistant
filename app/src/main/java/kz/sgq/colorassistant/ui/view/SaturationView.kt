package kz.sgq.colorassistant.ui.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kz.sgq.colorassistant.R

class SaturationView : View {
    private var posToSVFactor = 0f
    private var svToPosFactor = 0f
    private var barThickness = 0
    private var barLength = 0
    private var preferredBarLength = 0
    private var barPointerRadius = 0
    private var barPointerHaloRadius = 0
    private var barPointerPosition = 0
    private var color = 0
    private var movingPointer = false
    private val barPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barPointerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barPointerHaloPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val barRect = RectF()
    private val mHSVColor = FloatArray(3)
    private var shader: Shader? = null

    constructor(context: Context?) : super(context) {
        initConstructor(null, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initConstructor(attrs, 0)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initConstructor(attrs, defStyleAttr)
    }

    private fun initConstructor(attrs: AttributeSet?, defStyleAttr: Int) {
        val resources = context.resources
        val typedArray: TypedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.ColorPicker,
                defStyleAttr,
                0
        )

        barThickness = typedArray.getDimensionPixelSize(
                R.styleable.ColorBar_bar_thickness,
                resources.getDimensionPixelSize(R.dimen.bar_thickness))
        barLength = typedArray.getDimensionPixelSize(R.styleable.ColorBar_bar_length,
                resources.getDimensionPixelSize(R.dimen.bar_length))
        preferredBarLength = barLength
        barPointerRadius = typedArray.getDimensionPixelSize(
                R.styleable.ColorBar_bar_pointer_radius,
                resources.getDimensionPixelSize(R.dimen.bar_pointer_radius))
        barPointerHaloRadius = typedArray.getDimensionPixelSize(
                R.styleable.ColorBar_bar_pointer_halo_radius,
                resources.getDimensionPixelSize(R.dimen.bar_pointer_halo_radius))

        typedArray.recycle()

        barPaint.shader = shader
        barPointerPosition = barPointerRadius + barPointerHaloRadius
        barPointerPaint.color = -0x7e0100
        barPointerHaloPaint.color = -0x7e0100
        barPointerHaloPaint.alpha = 0x50
        posToSVFactor = 1 / barLength.toFloat()
        svToPosFactor = barLength.toFloat() / 1

        setColor(Color.BLUE)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val barPointerRadius = barPointerHaloRadius * 2
        val intrinsicSize = preferredBarLength + (barPointerHaloRadius*2)
        var measureSpec = 0
        val lengthMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val lengthSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val length = when (lengthMode) {
            MeasureSpec.EXACTLY -> lengthSize
            MeasureSpec.AT_MOST -> Math.min(intrinsicSize, lengthSize)
            MeasureSpec.UNSPECIFIED -> intrinsicSize
            else -> 0
        }

        barLength = length - barPointerRadius
        setMeasuredDimension(barPointerRadius, (barLength + barPointerRadius))
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRect(barRect, barPaint)

        canvas?.drawCircle(
                barPointerHaloRadius.toFloat(),
                barPointerPosition.toFloat(),
                barPointerHaloRadius.toFloat(),
                barPointerHaloPaint
        )

        canvas?.drawCircle(
                barPointerHaloRadius.toFloat(),
                barPointerPosition.toFloat(),
                barPointerRadius.toFloat(),
                barPointerPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                movingPointer = true

                if (event.y >= barPointerHaloRadius &&
                        event.y <= barPointerHaloRadius + barLength) {
                    barPointerPosition = Math.round(event.y)
                    calcColor(Math.round(event.y))
                    barPointerPaint.color = color
                    barPointerHaloPaint.color = color
                    barPointerHaloPaint.alpha = 0x50
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (movingPointer) {
                    if (event.y >= barPointerHaloRadius &&
                            event.y <= barPointerHaloRadius + barLength) {
                        barPointerPosition = Math.round(event.y)
                        calcColor(Math.round(event.y))
                        barPointerPaint.color = color
                        barPointerHaloPaint.color = color
                        barPointerHaloPaint.alpha = 0x50
                        invalidate()
                    } else if (event.y < barPointerHaloRadius) {
                        barPointerPosition = barPointerHaloRadius
                        color = Color.HSVToColor(mHSVColor)
                        barPointerPaint.color = color
                        barPointerHaloPaint.color = color
                        barPointerHaloPaint.alpha = 0x50
                        invalidate()
                    } else if (event.y > barPointerHaloRadius + barLength) {
                        barPointerPosition = barPointerHaloRadius + barLength
                        color = Color.WHITE
                        barPointerPaint.color = color
                        barPointerHaloPaint.color = color
                        barPointerHaloPaint.alpha = 0x50
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_UP -> movingPointer = false
        }

        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        barLength = h - barPointerHaloRadius * 2
        barRect.set((barPointerHaloRadius - barThickness / 2).toFloat(),
                barPointerHaloRadius.toFloat(),
                (barPointerHaloRadius + barThickness / 2).toFloat(),
                (barLength + barPointerHaloRadius).toFloat())
    }

    fun setColor(color: Int) {
        Color.colorToHSV(color, mHSVColor)
        shader = LinearGradient(
                barPointerHaloRadius.toFloat(),
                0f,
                barThickness.toFloat(),
                (barLength + barPointerHaloRadius).toFloat(),
                intArrayOf(color, Color.WHITE),
                null,
                Shader.TileMode.CLAMP
        )
        barPaint.shader = shader
        calcColor(barPointerPosition)
        barPointerPaint.color = this.color
        barPointerHaloPaint.color = this.color
        barPointerHaloPaint.alpha = 0x50

        invalidate()
    }

    private fun calcColor(i: Int) {
        var j = i
        if (i < 0) {
            j = 0
        } else if (i > barLength) {
            j = barLength
        }
        Log.d("TAG_CALC_COLOR", (1-(posToSVFactor * j)).toString())
        color = Color.HSVToColor(
                floatArrayOf(mHSVColor[0], 1-(posToSVFactor * j), 1f)
        )
    }
}