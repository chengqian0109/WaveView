package com.jack.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 乐动
 *
 * @author chengqian
 * Created on 2020/11/16
 */
public class WaveView extends LinearLayout {

    private View[] mChildViews;

    private ValueAnimator[] mValueAnimators;

    /**
     * 子 view 的 scaleY 的变化值
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

    private int mChildWidth = dp2px(1f);

    private int mChildHeight = DEFAULT_HEIGHT;

    private int mChildMargin = dp2px(1.5f);

    private int mColor;

    public WaveView(Context context) {
        this(context, null);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.WaveView);
        mColor = typedArray.getColor(R.styleable.WaveView_waveColor, Color.WHITE);
        int count = typedArray.getInt(R.styleable.WaveView_waveCount, 3);
        int waveWidth = typedArray.getDimensionPixelSize(R.styleable.WaveView_waveWidth, mChildWidth);
        mChildWidth = Math.max(waveWidth, mChildWidth);
        int waveMargin = typedArray.getDimensionPixelSize(R.styleable.WaveView_waveMargin, mChildMargin);
        mChildMargin = Math.max(waveMargin, mChildMargin);
        mAnimDuration = typedArray.getInteger(R.styleable.WaveView_waveAnimDuration, 240);
        mAnimDelay = typedArray.getInteger(R.styleable.WaveView_waveAnimDelay, 100);
        Drawable drawable = typedArray.getDrawable(R.styleable.WaveView_waveBackground);
        if (drawable != null) {
            setBackground(drawable);
        } else {
            int bgColor = typedArray.getColor(R.styleable.WaveView_waveBackground, Color.TRANSPARENT);
            setBackgroundColor(bgColor);
        }
        typedArray.recycle();

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mChildViews = new View[count];
        mValueAnimators = new ValueAnimator[count];
        mFractions = new float[count];
        addChildren();
        initAnim();
    }

    private void addChildren() {
        for (int i = 0; i < mChildViews.length; i++) {
            View view = new View(getContext());
            view.setBackgroundColor(mColor);
            LayoutParams params = new LayoutParams(mChildWidth, mChildHeight);
            if (i != 0) {
                params.setMarginStart(mChildMargin);
            }
            mChildViews[i] = view;
            addView(view, params);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mChildViews.length; i++) {
            mChildViews[i].setScaleY(mFractions[i]);
        }
        invalidate();
    }

    /**
     * 初始化动画
     */
    public void initAnim() {
        for (int i = 0; i < mValueAnimators.length; i++) {
            ValueAnimator animator = ValueAnimator.ofFloat(0.2f, 1);
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

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dp2px(float dpValue) {
        float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        // 计算所需的宽度，忽略计算宽度很大，比方说屏幕宽度不足以完全显示控件的情况
        int widthSize = mChildViews.length * mChildWidth + (mChildViews.length - 1) * mChildMargin
                + getPaddingLeft() + getPaddingRight();
        if (heightMode == MeasureSpec.EXACTLY) {
            mChildHeight = heightSize - getPaddingBottom() - getPaddingTop();
            mChildHeight = Math.max(MIN_HEIGHT, mChildHeight);
        } else {
            mChildHeight = DEFAULT_HEIGHT;
        }
        heightSize = mChildHeight + getPaddingTop() + getPaddingBottom();
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        updateChildViewHeight();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void updateChildViewHeight() {
        for (View view : mChildViews) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = mChildHeight;
            view.setLayoutParams(layoutParams);
            view.invalidate();
        }
    }
}
