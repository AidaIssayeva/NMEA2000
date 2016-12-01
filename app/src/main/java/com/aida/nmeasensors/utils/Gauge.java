package com.aida.nmeasensors.utils;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.aida.nmeasensors.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AIssayeva on 10/28/16.
 */

public class Gauge extends View {


    private static final int DEFAULT_LONG_POINTER_SIZE = 1;

    private Paint mPaint;
    private float mStrokeWidth;
    private int mStrokeColor;
    private RectF mRect;
    private String mStrokeCap;
    private int mStartAngle;
    private int mSweepAngle;
    private int mStartValue;
    private int mEndValue;
    private int mValue;
    private double mPointAngle;
    private int mPoint;
    private int mPointSize;
    private int mPointStartColor;
    private int mPointEndColor;
    private int mDividerColor;
    private int mDividerSize;
    private int mDividerStepAngle;
    private int mDividersCount;
    private boolean mDividerDrawFirst;
    private boolean mDividerDrawLast;
    String mSensorName;
       double     mSensorValue;


    public static final double DEFAULT_MAX_SPEED = 100.0;
    public static final double DEFAULT_MAJOR_TICK_STEP = 20.0;
    public static final int DEFAULT_MINOR_TICKS = 1;
    public static final int DEFAULT_LABEL_TEXT_SIZE_DP = 12;

    private double maxSpeed = DEFAULT_MAX_SPEED;
    private double speed = 0;
    private int defaultColor = Color.rgb(180, 180, 180);
    private double majorTickStep = DEFAULT_MAJOR_TICK_STEP;
    private int minorTicks = DEFAULT_MINOR_TICKS;
    private LabelConverter labelConverter;

    private List<ColoredRange> ranges = new ArrayList<ColoredRange>();

    private Paint backgroundPaint;
    private Paint backgroundInnerPaint;
    private Paint maskPaint;
    private Paint needlePaint;
    private Paint ticksPaint;
    private Paint txtPaint;
    private Paint colorLinePaint;
    private int labelTextSize;

    private Bitmap mMask;


    public Gauge(Context context) {
        super(context);
        float density = getResources().getDisplayMetrics().density;
        setLabelTextSize(Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density));
        init();
    }
    public Gauge(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomGauge, 0, 0);

//        // stroke style
//        setStrokeWidth(a.getDimension(R.styleable.CustomGauge_gaugeStrokeWidth, 10));
//        setStrokeColor(a.getColor(R.styleable.CustomGauge_gaugeStrokeColor, ContextCompat.getColor(context, android.R.color.darker_gray)));
//        setStrokeCap(a.getString(R.styleable.CustomGauge_gaugeStrokeCap));

        //setSensor Name and Value
        setmSensorName(a.getString(R.styleable.CustomGauge_gaugeSensorName));
        setmSensorValue(a.getInt(R.styleable.CustomGauge_gaugeSensorValue,0));

        // angle start and sweep (opposite direction 0, 270, 180, 90)
        setStartAngle(a.getInt(R.styleable.CustomGauge_gaugeStartAngle, 0));
        setSweepAngle(a.getInt(R.styleable.CustomGauge_gaugeSweepAngle, 360));

//        // scale (from mStartValue to mEndValue)
//        setStartValue(a.getInt(R.styleable.CustomGauge_gaugeStartValue, 0));
//        setEndValue(a.getInt(R.styleable.CustomGauge_gaugeEndValue, 1000));
//
//        // pointer size and color
//        setPointSize(a.getInt(R.styleable.CustomGauge_gaugePointSize, 0));
//        setPointStartColor(a.getColor(R.styleable.CustomGauge_gaugePointStartColor, ContextCompat.getColor(context, android.R.color.white)));
//        setPointEndColor(a.getColor(R.styleable.CustomGauge_gaugePointEndColor, ContextCompat.getColor(context, android.R.color.white)));
//
//        // divider options
//        int dividerSize = a.getInt(R.styleable.CustomGauge_gaugeDividerSize, 0);
//        setDividerColor(a.getColor(R.styleable.CustomGauge_gaugeDividerColor, ContextCompat.getColor(context, android.R.color.white)));
//        int dividerStep = a.getInt(R.styleable.CustomGauge_gaugeDividerStep, 0);
//        setDividerDrawFirst(a.getBoolean(R.styleable.CustomGauge_gaugeDividerDrawFirst, true));
//        setDividerDrawLast(a.getBoolean(R.styleable.CustomGauge_gaugeDividerDrawLast, true));

//        // calculating one point sweep
//        mPointAngle = ((double) Math.abs(mSweepAngle) / (mEndValue - mStartValue));
//
//        // calculating divider step
//        if (dividerSize > 0) {
//            mDividerSize = mSweepAngle / (Math.abs(mEndValue - mStartValue) / dividerSize);
//            mDividersCount = 100 / dividerStep;
//            mDividerStepAngle = mSweepAngle / mDividersCount;
//        }
        float density = getResources().getDisplayMetrics().density;
        setMaxSpeed(a.getFloat(R.styleable.CustomGauge_maxSpeed, (float) DEFAULT_MAX_SPEED));
        setSpeed(a.getFloat(R.styleable.CustomGauge_speed, 0));
        setLabelTextSize(a.getDimensionPixelSize(R.styleable.CustomGauge_labelTextSize, Math.round(DEFAULT_LABEL_TEXT_SIZE_DP * density)));

        a.recycle();
        init();
    }

    private void init() {
        //main Paint
        mPaint = new Paint();
        mPaint.setColor(mStrokeColor);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setAntiAlias(true);
        if (!TextUtils.isEmpty(mStrokeCap)) {
            if (mStrokeCap.equals("BUTT"))
                mPaint.setStrokeCap(Paint.Cap.BUTT);
            else if (mStrokeCap.equals("ROUND"))
                mPaint.setStrokeCap(Paint.Cap.ROUND);
        } else
            mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setStyle(Paint.Style.STROKE);
        mRect = new RectF();

        mValue = mStartValue;
        mPoint = mStartAngle;

        txtPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        txtPaint.setColor(Color.WHITE);
        txtPaint.setTextSize(labelTextSize);
        txtPaint.setTextAlign(Paint.Align.CENTER);



        maskPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        maskPaint.setDither(true);

        ticksPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ticksPaint.setStrokeWidth(3.0f);
        ticksPaint.setStyle(Paint.Style.STROKE);
        ticksPaint.setColor(defaultColor);

        colorLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        colorLinePaint.setStyle(Paint.Style.STROKE);
        colorLinePaint.setStrokeWidth(5);
        colorLinePaint.setColor(defaultColor);

        needlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        needlePaint.setStrokeWidth(5);
        needlePaint.setStyle(Paint.Style.STROKE);
        needlePaint.setColor(Color.argb(200, 255, 0, 0));
    }

    public void setLabelTextSize(int labelTextSize) {
        this.labelTextSize = labelTextSize;
        if (txtPaint != null) {
            txtPaint.setTextSize(labelTextSize);
            invalidate();
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float padding = getStrokeWidth();
        float size = getWidth()<getHeight() ? getWidth() : getHeight();
        float width = size - (2*padding);
        float height = size - (2*padding);
//        float radius = (width > height ? width/2 : height/2);
        float radius = (width < height ? width/2 : height/2);



        float rectLeft = (getWidth() - (2*padding))/2 - radius + padding;
        float rectTop = (getHeight() - (2*padding))/2 - radius + padding;
        float rectRight = (getWidth() - (2*padding))/2 - radius + padding + width;
        float rectBottom = (getHeight() - (2*padding))/2 - radius + padding + height;

        mRect.set(rectLeft, rectTop, rectRight, rectBottom);

        mPaint.setColor(mStrokeColor);
        mPaint.setShader(null);
        canvas.drawArc(mRect, mStartAngle, mSweepAngle, false, mPaint);
        mPaint.setColor(mPointStartColor);
        mPaint.setShader(new LinearGradient(getWidth(), getHeight(), 0, 0, mPointEndColor, mPointStartColor, Shader.TileMode.CLAMP));
        if (mPointSize>0) {//if size of pointer is defined
            if (mPoint > mStartAngle + mPointSize/2) {
                canvas.drawArc(mRect, mPoint - mPointSize/2, mPointSize, false, mPaint);
            }
            else { //to avoid excedding start/zero point
                canvas.drawArc(mRect, mPoint, mPointSize, false, mPaint);
            }
        }
        else { //draw from start point to value point (long pointer)
            if (mValue==mStartValue) //use non-zero default value for start point (to avoid lack of pointer for start/zero value)
                canvas.drawArc(mRect, mStartAngle, DEFAULT_LONG_POINTER_SIZE, false, mPaint);
            else
                canvas.drawArc(mRect, mStartAngle, mPoint - mStartAngle, false, mPaint);
        }

        if (mDividerSize > 0) {
            mPaint.setColor(mDividerColor);
            mPaint.setShader(null);
            int i = mDividerDrawFirst ? 0 : 1;
            int max = mDividerDrawLast ? mDividersCount + 1 : mDividersCount;
            for (; i < max; i++) {
                canvas.drawArc(mRect, mStartAngle + i* mDividerStepAngle, mDividerSize, false, mPaint);
            }
        }

        // Draw Ticks and colored arc
        drawTicks(canvas);
        // Draw Needle
        drawNeedle(canvas);

    }
    private void drawNeedle(Canvas canvas) {
        RectF oval = getOval(canvas, 1);
        float radius = oval.width()*0.35f + 10;
        RectF smallOval = getOval(canvas, 0.2f);

        float angle = 10 + (float) (getSpeed()/ getMaxSpeed()*160);
        canvas.drawLine(
                (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * smallOval.width()*0.5f),
                (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * smallOval.width()*0.5f),
                (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * (radius)),
                (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * (radius)),
                needlePaint
        );


        canvas.drawArc(smallOval, 180, 180, true, backgroundPaint);
    }

    private void drawTicks(Canvas canvas) {
        float availableAngle = 160;
        float majorStep = (float) (majorTickStep/ maxSpeed *availableAngle);
        float minorStep = majorStep / (1 + minorTicks);

        float majorTicksLength = 30;
        float minorTicksLength = majorTicksLength/2;

        RectF oval = getOval(canvas, 1);
        float radius = oval.width()*0.35f;

        float currentAngle = 10;
        double curProgress = 0;
        while (currentAngle <= 170) {

            canvas.drawLine(
                    (float) (oval.centerX() + Math.cos((180-currentAngle)/180*Math.PI)*(radius-majorTicksLength/2)),
                    (float) (oval.centerY() - Math.sin(currentAngle/180*Math.PI)*(radius-majorTicksLength/2)),
                    (float) (oval.centerX() + Math.cos((180-currentAngle)/180*Math.PI)*(radius+majorTicksLength/2)),
                    (float) (oval.centerY() - Math.sin(currentAngle/180*Math.PI)*(radius+majorTicksLength/2)),
                    ticksPaint
            );

            for (int i=1; i<=minorTicks; i++) {
                float angle = currentAngle + i*minorStep;
                if (angle >= 170 + minorStep/2) {
                    break;
                }
                canvas.drawLine(
                        (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * radius),
                        (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * radius),
                        (float) (oval.centerX() + Math.cos((180 - angle) / 180 * Math.PI) * (radius + minorTicksLength)),
                        (float) (oval.centerY() - Math.sin(angle / 180 * Math.PI) * (radius + minorTicksLength)),
                        ticksPaint
                );
            }

            if (labelConverter != null) {

                canvas.save();
                canvas.rotate(180 + currentAngle, oval.centerX(), oval.centerY());
                float txtX = oval.centerX() + radius + majorTicksLength/2 + 8;
                float txtY = oval.centerY();
                canvas.rotate(+90, txtX, txtY);
                canvas.drawText(labelConverter.getLabelFor(curProgress, maxSpeed), txtX, txtY, txtPaint);
                canvas.restore();
            }

            currentAngle += majorStep;
            curProgress += majorTickStep;
        }

        RectF smallOval = getOval(canvas, 0.7f);
        colorLinePaint.setColor(defaultColor);
        canvas.drawArc(smallOval, 185, 170, false, colorLinePaint);

        for (ColoredRange range: ranges) {
            colorLinePaint.setColor(range.getColor());
            canvas.drawArc(smallOval, (float) (190 + range.getBegin()/ maxSpeed *160), (float) ((range.getEnd() - range.getBegin())/ maxSpeed *160), false, colorLinePaint);
        }
    }
    private RectF getOval(Canvas canvas, float factor) {
        RectF oval;
        final int canvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        final int canvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        if (canvasHeight*2 >= canvasWidth) {
            oval = new RectF(0, 0, canvasWidth*factor, canvasWidth*factor);
        } else {
            oval = new RectF(0, 0, canvasHeight*2*factor, canvasHeight*2*factor);
        }

        oval.offset((canvasWidth-oval.width())/2 + getPaddingLeft(), (canvasHeight*2-oval.height())/2 + getPaddingTop());

        return oval;
    }
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        if (maxSpeed <= 0)
            throw new IllegalArgumentException("Non-positive value specified as max speed.");
        this.maxSpeed = maxSpeed;
        invalidate();
    }
    public double getSpeed() {
        return speed;
    }

    @TargetApi(11)
    public ValueAnimator setSpeed(double progress, long duration, long startDelay) {
        if (progress <= 0)
            throw new IllegalArgumentException("Non-positive value specified as a speed.");

        if (progress > maxSpeed)
            progress = maxSpeed;

        ValueAnimator va = ValueAnimator.ofObject(new TypeEvaluator<Double>() {
            @Override
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                return startValue + fraction*(endValue-startValue);
            }
        }, Double.valueOf(getSpeed()), Double.valueOf(progress));

        va.setDuration(duration);
        va.setStartDelay(startDelay);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Double value = (Double) animation.getAnimatedValue();
                if (value != null)
                    setSpeed(value);
            }
        });
        va.start();
        return va;
    }

    @TargetApi(11)
    public ValueAnimator setSpeed(double progress, boolean animate) {
        return setSpeed(progress, 1500, 200);
    }

    public void setSpeed(double speed) {
        if (speed < 0)
            throw new IllegalArgumentException("Non-positive value specified as a speed.");
        if (speed > maxSpeed)
            speed = maxSpeed;
        this.speed = speed;
        invalidate();
    }

    public void setValue(int value) {
        mValue = value;
        mPoint = (int) (mStartAngle + (mValue-mStartValue) * mPointAngle);
        invalidate();
    }

    public int getValue() {
        return mValue;
    }

    @SuppressWarnings("unused")
    public float getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        mStrokeWidth = strokeWidth;
    }

    @SuppressWarnings("unused")
    public int getStrokeColor() {
        return mStrokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        mStrokeColor = strokeColor;
    }

    @SuppressWarnings("unused")
    public String getStrokeCap() {
        return mStrokeCap;
    }

    public void setStrokeCap(String strokeCap) {
        mStrokeCap = strokeCap;
    }

    @SuppressWarnings("unused")
    public int getStartAngle() {
        return mStartAngle;
    }

    public void setStartAngle(int startAngle) {
        mStartAngle = startAngle;
    }

    @SuppressWarnings("unused")
    public int getSweepAngle() {
        return mSweepAngle;
    }

    public void setSweepAngle(int sweepAngle) {
        mSweepAngle = sweepAngle;
    }

    @SuppressWarnings("unused")
    public int getStartValue() {
        return mStartValue;
    }

    public void setStartValue(int startValue) {
        mStartValue = startValue;
    }

    @SuppressWarnings("unused")
    public int getEndValue() {
        return mEndValue;
    }

    public void setEndValue(int endValue) {
        mEndValue = endValue;
    }

    @SuppressWarnings("unused")
    public int getPointSize() {
        return mPointSize;
    }
    @SuppressWarnings("unused")
    public String getmSensorName() {
        return mSensorName;
    }
    @SuppressWarnings("unused")
    public double getmSensorValue() {
        return mSensorValue;
    }

    public void setmSensorName(String mSensorName) {
        this.mSensorName = mSensorName;
    }

    public void setmSensorValue(double mSensorValue) {
        this.mSensorValue = mSensorValue;
    }

    public void setPointSize(int pointSize) {
        mPointSize = pointSize;
    }

    @SuppressWarnings("unused")
    public int getPointStartColor() {
        return mPointStartColor;
    }

    public void setPointStartColor(int pointStartColor) {
        mPointStartColor = pointStartColor;
    }

    @SuppressWarnings("unused")
    public int getPointEndColor() {
        return mPointEndColor;
    }

    public void setPointEndColor(int pointEndColor) {
        mPointEndColor = pointEndColor;
    }

    @SuppressWarnings("unused")
    public int getDividerColor() {
        return mDividerColor;
    }

    public void setDividerColor(int dividerColor) {
        mDividerColor = dividerColor;
    }

    @SuppressWarnings("unused")
    public boolean isDividerDrawFirst() {
        return mDividerDrawFirst;
    }

    public void setDividerDrawFirst(boolean dividerDrawFirst) {
        mDividerDrawFirst = dividerDrawFirst;
    }

    @SuppressWarnings("unused")
    public boolean isDividerDrawLast() {
        return mDividerDrawLast;
    }

    public void setDividerDrawLast(boolean dividerDrawLast) {
        mDividerDrawLast = dividerDrawLast;
    }
    public static interface LabelConverter {

        String getLabelFor(double progress, double maxProgress);

    }

    public static class ColoredRange {

        private int color;
        private double begin;
        private double end;

        public ColoredRange(int color, double begin, double end) {
            this.color = color;
            this.begin = begin;
            this.end = end;
        }

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public double getBegin() {
            return begin;
        }

        public void setBegin(double begin) {
            this.begin = begin;
        }

        public double getEnd() {
            return end;
        }

        public void setEnd(double end) {
            this.end = end;
        }
    }


}
