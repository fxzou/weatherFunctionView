package cn.izouxiang.myview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;


/**
 * Created by zouxiang on 2016/4/17.
 */
public class FunctionView extends View {
    private Paint mHeightLinePaint;
    private Paint mLowLinePaint;
    private Paint mHeightCirclePaint;
    private Paint mLowCirclePaint;
    private Paint mTextPaint;
    private float mRadius;
    private float mLineStrokeWidth;
    private float mCircleStrokeWidth;
    private float mTextSize;
    private int mHeightLineColor;
    private int mLowLineColor;
    private int mHeightCircleColor;
    private int mLowCircleColor;
    private int mTextColor;
    private List<Element> elements;
    private float max;
    private float min;
    private int scaleX;
    private int scaleY;
    private int height;
    private int width;
    private boolean drawFlag;
    private String TAG = "TEST";
    private int BOTTOM = 0;
    private int TOP = 1;
    private float animationScale;
    private TypedArray ta;
    private ObjectAnimator mAnimator;


    public FunctionView(Context context) {
        super(context);
        initView();
        Log.d(TAG, "FunctionView: 1");
    }

    public FunctionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ta = context.obtainStyledAttributes(attrs,R.styleable.FunctionView);
        initView();
        Log.d(TAG, "FunctionView: 2");
    }

    public FunctionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        Log.d(TAG, "FunctionView: 3");
    }

    /**
     * 开启动画
     */

    public void startAnimation(){
        startAnimation(1000);
    }

    public void startAnimation(long duration){
        if(mAnimator == null) {
            mAnimator = ObjectAnimator.ofFloat(this, "animationScale", 0, 1);
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    FunctionView.this.setLayerType(View.LAYER_TYPE_NONE, null);
                }
            });
        }
        mAnimator.setDuration(duration);
        FunctionView.this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        Log.d(TAG, "onSizeChanged: ...");
        super.onSizeChanged(w, h, oldw, oldh);
        height = getHeight();
        width = getWidth();
        drawFlag = true;
        measureScale();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw: ...");
        super.onDraw(canvas);
        if (elements == null || elements.size() < 2) {
            return;
        }
        for (int i = 0; i < elements.size(); i++) {
            Element elem1 = elements.get(i);
            // 如果是最后一个点，点1点2为同一个点；
            if (elements.size() - 1 == i) {
                float x = (float) (0.1 * width + i * scaleX);
                float y = (float) (0.2 * height + (max - elem1.height) * scaleY) * animationScale;
                drawFunction(
                        canvas,
                        TOP,
                        elem1.height,
                        x,
                        y,
                        x,
                        y
                        );
                y = (float) (0.2 * height + (max - elem1.low) * scaleY) * animationScale;
                drawFunction(
                        canvas,
                        BOTTOM,
                        elem1.low,
                        x,
                        y,
                        x,
                        y
                );

            } else { //否则第二个点为下一个点
                Element elem2 = elements.get(i + 1);
                drawFunction(
                        canvas,
                        TOP,
                        elem1.height,
                        (float) (0.1 * width + i * scaleX),
                        (float) (0.2 * height + (max - elem1.height) * scaleY) * animationScale,
                        (float) (0.1 * width + (i + 1) * scaleX),
                        (float) (0.2 * height + (max - elem2.height) * scaleY) * animationScale
                );
                drawFunction(
                        canvas,
                        BOTTOM,
                        elem1.low,
                        (float) (0.1 * width + i * scaleX),
                        (float) (0.2 * height + (max - elem1.low) * scaleY) * animationScale,
                        (float) (0.1 * width + (i + 1) * scaleX),
                        (float) (0.2 * height + (max - elem2.low) * scaleY) * animationScale
                );
            }
            String title = elem1.title;
            if(!TextUtils.isEmpty(title)){
                Rect bounds = new Rect();
                mTextPaint.getTextBounds(title,0,title.length(),bounds);
                canvas.drawText(
                        elem1.title,
                        (float) (0.1 * width + i * scaleX) - bounds.width() / 2,
                        (float) (0.9 * height),
                        mTextPaint
                        );
            }
        }

    }

    /**
     * 测量比例
     */

    private void measureScale() {
        if (elements == null || elements.size() < 2) {
            return;
        }
        if (!drawFlag) {
            return;
        }
        Log.d(TAG, "measureScale: ...");
        max = elements.get(0).height;
        min = elements.get(0).low;
        for (Element element : elements) {
            if (element.height > max) {
                max = element.height;
            }
            if (element.low < min) {
                min = element.low;
            }
        }
        if (max == min) {
            throw new RuntimeException("elements max value can't equal min value ");
        }
        scaleX = (int) ((width * 0.8) / (elements.size() - 1));
        scaleY = (int) ((height * 0.5) / (max - min));

    }

    //画两个点之间的连接线，并且以第一个点为圆心画圆
    private void drawFunction(Canvas canvas, int drawType, float value, float x1, float y1, float x2, float y2) {
        Paint circlePaint = null;
        Paint linePaint = null;
        String text = String.valueOf(value);
        float textX,textY;
        if (drawType == TOP) {
            circlePaint = mHeightCirclePaint;
            linePaint = mHeightLinePaint;
            textX = x1 - mRadius;
            textY = y1 - mRadius * 2;

        } else if (drawType == BOTTOM) {
            circlePaint = mLowCirclePaint;
            linePaint = mLowLinePaint;
            Rect bounds = new Rect();
            mTextPaint.getTextBounds(text, 0, text.length(), bounds);
            textX = x1 - mRadius;
            textY = y1 + bounds.height() + mRadius * 2;
        } else {
            throw new RuntimeException("drawType can't resolve");
        }
        if(x1 != x2 || y1 != y2) {
            float scale = (float) (mRadius / Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)));
            Path path = new Path();
            path.moveTo(x1 + (x2 - x1) * scale, y1 + (y2 - y1) * scale);
            path.lineTo(x2 + (x1 - x2) * scale, y2 + (y1 - y2) * scale);
            canvas.drawPath(path, linePaint);
        }
        //canvas.drawLine(x1+(x2-x1)*scale,y1+(y2-y1)*scale,x2+(x1-x2)*scale,y2+(y1-y2)*scale,mLinePaint);
        canvas.drawCircle(x1, y1, mRadius, circlePaint);
        canvas.drawText(text,textX,textY,mTextPaint);
    }

    /**
     * 初始化View
     */
    public void initView() {
        Log.d(TAG, "initView: ...");
        animationScale = 1l;
        drawFlag = false;
        if(null == ta) {
            mRadius = 10f;
            mLineStrokeWidth = 8f;
            mCircleStrokeWidth = 8f;
            mHeightLineColor = Color.RED;
            mHeightCircleColor = Color.GREEN;
            mLowLineColor = Color.RED;
            mLowCircleColor = Color.GREEN;
            mTextSize = 16f;
            mTextColor = Color.YELLOW;
        }else{
            mRadius = ta.getFloat(R.styleable.FunctionView_radius,10f);
            mLineStrokeWidth = ta.getFloat(R.styleable.FunctionView_lineStrokeWidth,8f);
            mCircleStrokeWidth = ta.getFloat(R.styleable.FunctionView_circleStrokeWidth,8f);
            mHeightLineColor = ta.getColor(R.styleable.FunctionView_heightLineColor,Color.RED);
            mHeightCircleColor = ta.getColor(R.styleable.FunctionView_heightCircleColor,Color.GREEN);
            mLowLineColor = ta.getColor(R.styleable.FunctionView_lowLineColor,Color.RED);;
            mLowCircleColor = ta.getColor(R.styleable.FunctionView_lowCircleColor,Color.GREEN);
            mTextSize = ta.getFloat(R.styleable.FunctionView_textSize,16f);
            mTextColor = ta.getColor(R.styleable.FunctionView_textColor,Color.YELLOW);
        }
        mHeightLinePaint = new Paint();
        mHeightLinePaint.setStyle(Paint.Style.STROKE);
        mHeightCirclePaint = new Paint();
        mHeightCirclePaint.setStyle(Paint.Style.STROKE);
        mLowLinePaint = new Paint();
        mLowLinePaint.setStyle(Paint.Style.STROKE);
        mLowCirclePaint = new Paint();
        mLowCirclePaint.setStyle(Paint.Style.STROKE);
        mHeightLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLowLinePaint.setStrokeWidth(mLineStrokeWidth);
        mHeightCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mLowCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mHeightLinePaint.setColor(mHeightLineColor);
        mHeightCirclePaint.setColor(mHeightCircleColor);
        mLowLinePaint.setColor(mLowLineColor);
        mLowCirclePaint.setColor(mLowCircleColor);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
    }

    /**
     * 元素类
     */
    public static class Element {
        public final float height;
        public final float low;
        public final String title;

        public Element(@NonNull float height, @NonNull float low,String title) {
            this.height = height;
            this.low = low;
            this.title = title;
        }
    }

    /* set get 方法*/
    public float getRadius() {
        return mRadius;
    }

    public float getLineStrokeWidth() {
        return mLineStrokeWidth;
    }

    public float getCircleStrokeWidth() {
        return mCircleStrokeWidth;
    }

    public List<Element> getElements() {
        return elements;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public int getLowCircleColor() {
        return mLowCircleColor;
    }

    public int getHeightCircleColor() {
        return mHeightCircleColor;
    }

    public int getLowLineColor() {
        return mLowLineColor;
    }

    public int getHeightLineColor() {
        return mHeightLineColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public Paint getTextPaint() {
        return mTextPaint;
    }

    public Paint getLowCirclePaint() {
        return mLowCirclePaint;
    }

    public Paint getHeightCirclePaint() {
        return mHeightCirclePaint;
    }

    public Paint getLowLinePaint() {
        return mLowLinePaint;
    }

    public Paint getHeightLinePaint() {
        return mHeightLinePaint;
    }

    public float getAnimationScale() {
        return animationScale;
    }

    public void setRadius(float mRadius) {
        this.mRadius = mRadius;
        invalidate();
    }

    public void setLineStrokeWidth(float mLineStrokeWidth) {
        this.mLineStrokeWidth = mLineStrokeWidth;
        mHeightLinePaint.setStrokeWidth(mLineStrokeWidth);
        mLowLinePaint.setStrokeWidth(mLineStrokeWidth);
        invalidate();
    }

    public void setCircleStrokeWidth(float mCircleStrokeWidth) {
        this.mCircleStrokeWidth = mCircleStrokeWidth;
        mHeightCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        mLowCirclePaint.setStrokeWidth(mCircleStrokeWidth);
        invalidate();
    }

    public void setTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        mTextPaint.setTextSize(mTextSize);
        invalidate();
    }

    public void setHeightLineColor(@ColorInt int mHeightLineColor) {
        this.mHeightLineColor = mHeightLineColor;
        mHeightLinePaint.setColor(mHeightLineColor);
        invalidate();
    }

    public void setLowLineColor(@ColorInt int mLowLineColor) {
        this.mLowLineColor = mLowLineColor;
        mLowLinePaint.setColor(mLowLineColor);
        invalidate();
    }

    public void setHeightCircleColor(@ColorInt int mHeightCircleColor) {
        this.mHeightLineColor = mHeightCircleColor;
        mHeightCirclePaint.setColor(mHeightCircleColor);
        invalidate();
    }

    public void setLowCircleColor(@ColorInt int mLowCircleColor) {
        this.mLowCircleColor = mLowCircleColor;
        mLowCirclePaint.setColor(mLowCircleColor);
        invalidate();
    }

    public void setTextColor(@ColorInt int mTextColor) {
        this.mTextColor = mTextColor;
        mTextPaint.setColor(mTextColor);
        invalidate();
    }

    public void setElements(List<Element> elements) {
        Log.d(TAG, "setElements: ...");
        if (elements == null || elements.size() < 2) {
            throw new RuntimeException("elements size must >= 2");
        }
        this.elements = elements;
        measureScale();
        invalidate();
    }

    public void setAnimationScale(float animationScale) {
        this.animationScale = animationScale;
        invalidate();
    }

    public void setTextTypeface(Typeface typeface){
        mTextPaint.setTypeface(typeface);
        invalidate();
    }
}
