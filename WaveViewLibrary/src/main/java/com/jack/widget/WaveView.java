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

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;

/**
 * 乐动，线条默认为白色
 *
 * @author chengqian
 * Created on 2020/11/16
 * @attr R.styleable.WaveView_waveColor 线条颜色<br/>
 * @attr R.styleable.WaveView_waveCount 线条数量<br/>
 * @attr R.styleable.WaveView_waveWidth 线条宽度<br/>
 * @attr R.styleable.WaveView_waveMargin 相邻线条之间的间距<br/>
 * @attr R.styleable.WaveView_waveAnimDuration 线条动画执行时长<br/>
 * @attr R.styleable.WaveView_waveAnimDelay 相邻线条之间动画延时<br/>
 * @attr R.styleable.WaveView_waveMinRatio 线条长度的最小显示比例，有效取值为(0,1]<br/>
 * @attr R.styleable.WaveView_waveGravity 枚举类型，具体参考 {@link Gravity} 说明
 */
public class WaveView extends View {

    private Paint mPaint;

    private int mWaveCount = 3;

    /**
     * 线条长度的变化比例
     */
    private float[] mRatios;

    /**
     * 动画执行一次的时长
     */
    private int mAnimDuration = 240;

    /**
     * 相邻两个线条动画执行的间隔
     */
    private int mAnimDelay = 100;

    /**
     * 最小高度
     */
    private final int MIN_HEIGHT = dp2px(3);

    /**
     * 默认高度
     */
    private final int DEFAULT_HEIGHT = dp2px(10);

    private int mWaveWidth = dp2px(1f);

    private int mWaveMargin = dp2px(1f);

    private ValueAnimator[] mValueAnimators;

    private int mWaveColor = Color.WHITE;

    /**
     * 线条长度比例的最小值
     */
    private float mWaveMinRatio = 0.3f;

    /**
     * 线条相对画布的位置
     *
     * @see Gravity#BOTTOM
     * @see Gravity#CENTER
     */
    private Gravity mWaveGravity = Gravity.CENTER;

    /**
     * 控件最终的高度
     */
    private int mHeight;

    public WaveView(Context context) {
        super(context);
        initPaint();
        initAnim();
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(mWaveColor);
        mPaint.setStrokeWidth(8);
        mPaint.setStyle(Paint.Style.FILL);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mWaveColor = typedArray.getColor(R.styleable.WaveView_waveColor, mWaveColor);

        int count = typedArray.getInt(R.styleable.WaveView_waveCount, mWaveCount);
        mWaveCount = count <= 0 ? mWaveCount : count;

        int waveWidth = typedArray.getDimensionPixelSize(R.styleable.WaveView_waveWidth, mWaveWidth);
        // 不关心线条很细的情况
        mWaveWidth = waveWidth <= 0 ? mWaveWidth : waveWidth;

        int waveMargin = typedArray.getDimensionPixelSize(R.styleable.WaveView_waveMargin, mWaveMargin);
        // 不关心线条间距很小的情况
        mWaveMargin = waveMargin <= 0 ? mWaveMargin : waveMargin;

        int duration = typedArray.getInteger(R.styleable.WaveView_waveAnimDuration, mAnimDuration);
        // 不关心因为动画执行时长导致的实际效果
        mAnimDuration = duration <= 0 ? mAnimDuration : duration;

        int delay = typedArray.getInteger(R.styleable.WaveView_waveAnimDelay, mAnimDelay);
        // 不关心因为动画延时导致的实际效果
        mAnimDelay = delay <= 0 ? mAnimDelay : delay;

        int gravity = typedArray.getInt(R.styleable.WaveView_waveGravity, -1);
        if (gravity != -1) {
            mWaveGravity = gravity == 0 ? Gravity.BOTTOM : Gravity.CENTER;
        }

        float minRatio = typedArray.getFloat(R.styleable.WaveView_waveMinRatio, mWaveMinRatio);
        // 过滤掉线条显示比例无效的数值
        if (minRatio > 0 && minRatio <= 1) {
            mWaveMinRatio = minRatio;
        }
        typedArray.recycle();

        initPaint();
        initAnim();
    }

    /**
     * 初始化动画
     */
    public void initAnim() {
        mValueAnimators = new ValueAnimator[mWaveCount];
        mRatios = new float[mWaveCount];
        for (int i = 0; i < mValueAnimators.length; i++) {
            // 设置线条长度的变化比例范围为 0.3~1.0
            ValueAnimator animator = ValueAnimator.ofFloat(mWaveMinRatio, 1);
            animator.setDuration(mAnimDuration);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setStartDelay(i * mAnimDelay);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            final int finalI = i;
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mRatios[finalI] = (float) animation.getAnimatedValue();
                }
            });
            mValueAnimators[i] = animator;
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
        if (mWaveGravity == Gravity.CENTER) {
            canvas.translate(0, mHeight / 2f);
            for (int i = 0; i < mWaveCount; i++) {
                canvas.drawRect(
                        i * (mWaveWidth + mWaveMargin),
                        -mRatios[i] * mHeight / 2,
                        i * (mWaveWidth + mWaveMargin) + mWaveWidth,
                        mRatios[i] * mHeight / 2, mPaint);
            }
        } else if (mWaveGravity == Gravity.BOTTOM) {
            canvas.translate(0, mHeight);
            for (int i = 0; i < mWaveCount; i++) {
                canvas.drawRect(
                        i * (mWaveWidth + mWaveMargin),
                        -mRatios[i] * mHeight,
                        i * (mWaveWidth + mWaveMargin) + mWaveWidth,
                        0, mPaint);
            }
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
    private void startAnim() {
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
    private void stopAnim() {
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

    /**
     * 设置线条颜色值
     *
     * @param color 颜色值
     */
    public void setWaveColor(@ColorInt int color) {
        mWaveColor = color;
        mPaint.setColor(mWaveColor);
    }

    /**
     * 设置线条颜色资源ID
     *
     * @param colorId 颜色资源ID
     */
    public void setWaveColorRes(@ColorRes int colorId) {
        mWaveColor = ContextCompat.getColor(getContext(), colorId);
    }

    /**
     * 设置线条数量
     *
     * @param waveCount 线条数量
     */
    public void setWaveCount(int waveCount) {
        if (waveCount <= 0) {
            return;
        }
        // 线条个数不允许二次赋值
        if (mValueAnimators[0].isRunning()) {
            return;
        }
        mWaveCount = waveCount;
        initAnim();
    }

    /**
     * 设置动画执行时长
     *
     * @param animDuration 动画时长
     */
    public void setAnimDuration(int animDuration) {
        if (animDuration <= 0) {
            return;
        }
        mAnimDuration = animDuration;
        updateAnimDuration();
    }

    /**
     * 更新动画时长
     */
    private void updateAnimDuration() {
        if (mValueAnimators != null && mValueAnimators.length > 0) {
            for (ValueAnimator animator : mValueAnimators) {
                animator.setDuration(mAnimDuration);
            }
        }
    }

    /**
     * 设置相邻线条动画延时
     *
     * @param animDelay 动画延时
     */
    public void setAnimDelay(int animDelay) {
        if (animDelay <= 0) {
            return;
        }
        mAnimDelay = animDelay;
        updateAnimDelay();
    }

    /**
     * 更新动画延时
     */
    private void updateAnimDelay() {
        if (mValueAnimators != null && mValueAnimators.length > 0) {
            for (int i = 0; i < mValueAnimators.length; i++) {
                mValueAnimators[i].setStartDelay(i * mAnimDelay);
            }
        }
    }

    /**
     * 设置线条间距
     *
     * @param pixels 线条间距：像素
     */
    public void setWaveMargin(@Px int pixels) {
        if (pixels <= 0) {
            return;
        }
        mWaveMargin = pixels;
    }

    /**
     * 设置线条间距
     *
     * @param dimensionId 资源ID
     */
    public void setWaveMarginRes(@DimenRes int dimensionId) {
        mWaveMargin = getResources().getDimensionPixelSize(dimensionId);
    }

    /**
     * 设置线条宽度像素值
     *
     * @param pixels 线条宽度：像素
     */
    public void setWaveWidth(@Px int pixels) {
        if (pixels <= 0) {
            return;
        }
        mWaveWidth = pixels;
    }

    /**
     * 设置线条宽度
     *
     * @param dpValue 线条宽度dp值
     */
    public void setWaveWidthDp(@Dimension int dpValue) {
        if (dpValue <= 0) {
            return;
        }
        mWaveWidth = dp2px(dpValue);
    }

    /**
     * 设置线条宽度
     *
     * @param dimensionId 线条宽度资源ID
     */
    public void setWaveWidthRes(@DimenRes int dimensionId) {
        mWaveWidth = getResources().getDimensionPixelSize(dimensionId);
    }

    /**
     * 设置线条绘制位置
     *
     * @param waveGravity Gravity.BOTTOM 和 Gravity.CENTER
     * @see Gravity#BOTTOM
     * @see Gravity#CENTER
     */
    public void setWaveGravity(Gravity waveGravity) {
        mWaveGravity = waveGravity;
    }

    /**
     * 设置线条长度最小显示比例
     *
     * @param waveMinRatio 线条长度最小显示比例
     */
    public void setWaveMinRatio(float waveMinRatio) {
        if (waveMinRatio <= 0 || waveMinRatio > 1) {
            return;
        }
        // 最小显示比例不允许二次赋值
        if (mValueAnimators[0].isRunning()) {
            return;
        }
        mWaveMinRatio = waveMinRatio;
    }

    public enum Gravity {
        /**
         * 画布坐标原点纵坐标在线条底部
         */
        BOTTOM,

        /**
         * 画布坐标原点纵坐标在线条中央
         */
        CENTER;
    }
}