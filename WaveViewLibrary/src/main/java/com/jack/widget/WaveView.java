package com.jack.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 乐动，线条默认为白色，相关自定义属性如下：<br/>
 * WaveView_waveColor 线条颜色<br/>
 * WaveView_waveCount 线条数量<br/>
 * WaveView_waveWidth 线条宽度<br/>
 * WaveView_waveMargin 相邻线条之间的间距<br/>
 * WaveView_waveAnimDuration 线条动画执行时长<br/>
 * WaveView_waveAnimDelay 相邻线条之间动画延时
 *
 * @author chengqian
 * Created on 2020/11/16
 */
public class WaveView extends View {

    private Paint mPaint;

    private int mWaveCount;

    /**
     * 线条长度的变化比例
     */
    private float[] mFractions;

    /**
     * 动画执行一次的时长
     */
    private long mAnimDuration;

    /**
     * 相邻两个子 view 动画执行的间隔
     */
    private long mAnimDelay;

    /**
     * 最小高度
     */
    private final int MIN_HEIGHT = dp2px(5);

    /**
     * 默认高度
     */
    private final int DEFAULT_HEIGHT = dp2px(10);

    private int mWaveWidth = dp2px(1f);

    private int mWaveMargin = dp2px(1.5f);

    private ValueAnimator[] mValueAnimators;

    /**
     * 控件最终的高度
     */
    private int mHeight;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        int waveColor = typedArray.getColor(R.styleable.WaveView_waveColor, Color.WHITE);
        mWaveCount = typedArray.getInt(R.styleable.WaveView_waveCount, 3);
        int waveWidth = typedArray.getDimensionPixelSize(R.styleable.WaveView_waveWidth, mWaveWidth);
        mWaveWidth = Math.max(waveWidth, mWaveWidth);
        int waveMargin = typedArray.getDimensionPixelSize(R.styleable.WaveView_waveMargin, mWaveMargin);
        mWaveMargin = Math.max(waveMargin, mWaveMargin);
        mAnimDuration = typedArray.getInteger(R.styleable.WaveView_waveAnimDuration, 240);
        mAnimDelay = typedArray.getInteger(R.styleable.WaveView_waveAnimDelay, 100);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setColor(waveColor);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.FILL);

        mValueAnimators = new ValueAnimator[mWaveCount];
        mFractions = new float[mWaveCount];
        initAnim();
    }

    /**
     * 初始化动画
     */
    public void initAnim() {
        for (int i = 0; i < mValueAnimators.length; i++) {
            ValueAnimator animator = ValueAnimator.ofFloat(0.3f, 1);
            animator.setDuration(mAnimDuration);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            if (i != 0) {
                animator.setStartDelay(i * mAnimDelay);
            }
            animator.setRepeatMode(ValueAnimator.REVERSE);
            final int finalI = i;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mFractions[finalI] = (float) animation.getAnimatedValue();
                }
            });
            mValueAnimators[i] = animator;
            animator.start();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(0, mHeight / 2f);
        for (int i = 0; i < mWaveCount; i++) {
            canvas.drawRect(i * (mWaveWidth + mWaveMargin), -mFractions[i] * mHeight / 2,
                    i * (mWaveWidth + mWaveMargin) + mWaveWidth, mFractions[i] * mHeight / 2, mPaint);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 计算所需的宽度，忽略计算宽度很大，比方说屏幕宽度不足以完全显示控件的情况
        int widthSize = mWaveCount * mWaveWidth + (mWaveCount - 1) * mWaveMargin
                + getPaddingLeft() + getPaddingRight();
        int waveHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            waveHeight = heightSize - getPaddingBottom() - getPaddingTop();
            waveHeight = Math.max(MIN_HEIGHT, waveHeight);
        } else {
            waveHeight = DEFAULT_HEIGHT;
        }
        heightSize = waveHeight + getPaddingTop() + getPaddingBottom();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.VISIBLE) {
            startAnim();
        } else {
            stopAnim();
        }
    }

    @Override
    public void invalidate() {
        if (hasWindowFocus()) {
            super.invalidate();
            startAnim();
        } else {
            stopAnim();
        }
    }

    /**
     * 开启动画
     */
    public void startAnim() {
        if (mValueAnimators[0].isRunning()) {
            return;
        }
        for (ValueAnimator valueAnimator : mValueAnimators) {
            valueAnimator.start();
        }
    }

    /**
     * 停止动画
     */
    public void stopAnim() {
        if (mValueAnimators[0].isRunning()) {
            for (ValueAnimator valueAnimator : mValueAnimators) {
                valueAnimator.cancel();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            invalidate();
            startAnim();
        } else {
            stopAnim();
        }
    }
}
