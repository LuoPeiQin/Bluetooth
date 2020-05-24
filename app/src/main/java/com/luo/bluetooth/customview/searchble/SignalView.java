package com.luo.bluetooth.customview.searchble;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.luo.bluetooth.R;


/**
 * 强度指示器，等级0-4
 * Created by LPQ on 2017/1/17.
 */

public class SignalView extends View {

    private Paint mPaint;
    private int mWidth, mHeight;
    private float cx, cy, radius;
    private int mNoIntensityColor, mIntensityColor;
    private int mIntensity;

    public SignalView(Context context) {
        this(context, null);
    }

    public SignalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SignalView, defStyleAttr, 0);
        mIntensityColor = typedArray.getColor(R.styleable.SignalView_intensityColor, 0xff6cb1f2);
        mNoIntensityColor = typedArray.getColor(R.styleable.SignalView_noIntensityColor, 0xffbdc3c7);
        typedArray.recycle();
        init();
    }

    private void init(){
//        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    /**
     * 设置强度
     * */
    public void setIntensity(int intensity){
        mIntensity = intensity;
        if (intensity>4)
            mIntensity = 4;
        if (intensity<0)
            mIntensity = 0;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        cx = mWidth/2.f;
        cy = mHeight*0.7f;
        if (mWidth>mHeight){
            radius = mHeight/12.f;
        }else {
            radius = mWidth/12.f;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mIntensity>0?mIntensityColor:mNoIntensityColor);
        canvas.drawCircle(cx, cy, radius, mPaint);
        RectF rectF = new RectF(cx-2.5f*radius, cy-2.f*radius, cx+2.5f*radius, cy+4*radius);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(radius*0.8f);
        mPaint.setColor(mIntensity>1?mIntensityColor:mNoIntensityColor);
        canvas.drawArc(rectF, 220, 100, false, mPaint);
        float offest = 1.5f*radius;
        for (int i=2;i<4;i++){
            mPaint.setColor(mIntensity>i?mIntensityColor:mNoIntensityColor);
            rectF.left -= offest;
            rectF.top -= offest;
            rectF.right += offest;
            canvas.drawArc(rectF, 220, 100, false, mPaint);
        }
    }
}
