package com.mage.cameraxdemo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

class CircleView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var viewWidth =0f
    var viewHeight =0f
    val paint = Paint().apply {
        color= Color.WHITE
        strokeWidth=3f
    }
    val path = Path()
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewWidth=w.toFloat()
        viewHeight=h.toFloat()
    }
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawOval(canvas)
    }

    private fun drawOval(canvas: Canvas?) {
        path.run {
            reset()
            moveTo(0f,viewHeight/2)
            addArc(0f,0f,viewWidth,viewHeight,-180f,90f)
            lineTo(0f,0f)
            lineTo(0f,viewHeight/2)
            close()
            canvas?.drawPath(path,paint)

            reset()
            moveTo(viewWidth/2,viewHeight)
            addArc(0f,0f,viewWidth,viewHeight,-270f,90f)
            lineTo(0f,viewHeight)
            lineTo(viewWidth/2,viewHeight)
            close()
            canvas?.drawPath(path,paint)

            reset()
            moveTo(viewWidth,viewHeight/2)
            addArc(0f,0f,viewWidth,viewHeight,0f,90f)
            lineTo(viewWidth,viewHeight)
            lineTo(viewWidth,viewHeight/2)
            close()
            canvas?.drawPath(path,paint)

            reset()
            moveTo(viewWidth/2,0f)
            addArc(0f,0f,viewWidth,viewHeight,-90f,90f)
            lineTo(viewWidth,0f)
            lineTo(viewWidth/2,0f)
            close()
            canvas?.drawPath(path,paint)
        }
    }
}