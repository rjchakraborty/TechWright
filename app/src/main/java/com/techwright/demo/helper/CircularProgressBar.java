package com.techwright.demo.helper;

/**
 * Created by RJ Chakraborty on 23-07-2019.
 */

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StyleRes;

import com.techwright.demo.R;


/**
 * Circular progress bar
 */
public class CircularProgressBar extends View {
    private static final float DEFAULT_MAXIMUM = 100f;
    private static final float DEFAULT_PROGRESS = 0f;
    private static final float DEFAULT_FOREGROUND_STROKE_WIDTH_DP = 3f;
    private static final float DEFAULT_BACKGROUND_STROKE_WIDTH_DP = 1f;
    private static final float DEFAULT_START_ANGLE = 270f;
    private static final float DEFAULT_INDETERMINATE_MINIMUM_ANGLE = 60f;
    private static final int DEFAULT_FOREGROUND_STROKE_COLOR = Color.BLUE;
    private static final int DEFAULT_BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final int DEFAULT_PROGRESS_ANIMATION_DURATION = 100;
    private static final int DEFAULT_INDETERMINATE_ROTATION_ANIMATION_DURATION = 1200;
    private static final int DEFAULT_INDETERMINATE_SWEEP_ANIMATION_DURATION = 600;
    private static final boolean DEFAULT_ANIMATE_PROGRESS = true;
    private static final boolean DEFAULT_DRAW_BACKGROUND_STROKE = true;
    private static final boolean DEFAULT_INDETERMINATE = false;
    private final Runnable mSweepRestartAction = new SweepRestartAction();
    private final RectF mDrawRect = new RectF();
    private final ValueAnimator mProgressAnimator = new ValueAnimator();
    private final ValueAnimator mIndeterminateStartAnimator = new ValueAnimator();
    private final ValueAnimator mIndeterminateSweepAnimator = new ValueAnimator();
    private final Paint mForegroundStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBackgroundStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mMaximum;
    private float mProgress;
    private float mStartAngle;
    private float mIndeterminateStartAngle;
    private float mIndeterminateSweepAngle;
    private float mIndeterminateOffsetAngle;
    private float mIndeterminateMinimumAngle;
    private boolean mIndeterminate;
    private boolean mAnimateProgress;
    private boolean mDrawBackgroundStroke;
    private boolean mIndeterminateGrowMode;
    private boolean mVisible;

    public CircularProgressBar(@NonNull Context context) {
        super(context);
        initialize(context, null, 0, 0);
    }

    public CircularProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0, 0);
    }

    public CircularProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public CircularProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,
                               int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Indeterminate mode
     */
    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    /**
     * Indeterminate mode, disabled by default
     */
    public void setIndeterminate(boolean indeterminate) {
        stopIndeterminateAnimations();
        mIndeterminate = indeterminate;
        invalidate();
        if (mVisible && indeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Get current progress value for non-indeterminate mode
     */
    public float getProgress() {
        return mProgress;
    }

    /**
     * Set current progress value for non-indeterminate mode
     */
    public void setProgress(float progress) {
        if (mIndeterminate) {
            mProgress = progress;
        } else {
            stopProgressAnimation();
            if (mVisible && mAnimateProgress) {
                setProgressAnimated(progress);
            } else {
                setProgressInternal(progress);
            }
        }
    }

    /**
     * Maximum progress for non-indeterminate mode
     */
    public float getMaximum() {
        return mMaximum;
    }

    /**
     * Maximum progress for non-indeterminate mode
     */
    public void setMaximum(float maximum) {
        mMaximum = maximum;
        invalidate();
    }

    /**
     * Start angle for non-indeterminate mode, between -360 and 360 degrees
     */
    public void setStartAngle(@FloatRange(from = -360f, to = 360f) float angle) {
        checkStartAngle(angle);
        mStartAngle = angle;
        invalidate();
    }

    /**
     * Whether to animate progress for non-indeterminate mode
     */
    public void setAnimateProgress(boolean animate) {
        mAnimateProgress = animate;
    }

    /**
     * Progress animation duration for non-indeterminate mode (in milliseconds)
     */
    public void setProgressAnimationDuration(@IntRange(from = 0) long duration) {
        checkAnimationDuration(duration);
        if (mVisible) {
            if (mProgressAnimator.isRunning()) {
                mProgressAnimator.end();
            }
        }
        mProgressAnimator.setDuration(duration);
    }

    /**
     * Minimum angle for indeterminate mode, between 0 and 180 degrees
     */
    public void setIndeterminateMinimumAngle(@FloatRange(from = 0f, to = 180f) float angle) {
        checkIndeterminateMinimumAngle(angle);
        stopIndeterminateAnimations();
        mIndeterminateMinimumAngle = angle;
        mIndeterminateSweepAnimator.setFloatValues(360f - angle * 2f);
        invalidate();
        if (mVisible && mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Rotation animation duration for indeterminate mode (in milliseconds)
     */
    public void setIndeterminateRotationAnimationDuration(@IntRange(from = 0) long duration) {
        checkAnimationDuration(duration);
        stopIndeterminateAnimations();
        mIndeterminateStartAnimator.setDuration(duration);
        invalidate();
        if (mVisible && mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Sweep animation duration for indeterminate mode (in milliseconds)
     */
    public void setIndeterminateSweepAnimationDuration(@IntRange(from = 0) long duration) {
        checkAnimationDuration(duration);
        stopIndeterminateAnimations();
        mIndeterminateSweepAnimator.setDuration(duration);
        invalidate();
        if (mVisible && mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    /**
     * Foreground stroke color
     */
    public void setForegroundStrokeColor(@ColorInt int color) {
        mForegroundStrokePaint.setColor(color);
        invalidate();
    }

    /**
     * Foreground stroke width (in pixels)
     */
    public void setForegroundStrokeWidth(@FloatRange(from = 0f, to = Float.MAX_VALUE) float width) {
        checkWidth(width);
        mForegroundStrokePaint.setStrokeWidth(width);
        invalidateDrawRect();
        invalidate();
    }

    /**
     * Background stroke color
     */
    public void setBackgroundStrokeColor(@ColorInt int color) {
        mForegroundStrokePaint.setColor(color);
        invalidate();
    }

    /**
     * Background stroke width (in pixels)
     */
    public void setBackgroundStrokeWidth(@FloatRange(from = 0f, to = Float.MAX_VALUE) float width) {
        checkWidth(width);
        mBackgroundStrokePaint.setStrokeWidth(width);
        invalidateDrawRect();
        invalidate();
    }

    /**
     * Whether to draw background stroke
     */
    public void setDrawBackgroundStroke(boolean draw) {
        mDrawBackgroundStroke = draw;
        invalidateDrawRect();
        invalidate();
    }

    @Override
    public void onVisibilityAggregated(boolean visible) {
        super.onVisibilityAggregated(visible);
        mVisible = visible;
        if (mIndeterminate) {
            if (visible) {
                startIndeterminateAnimations();
            } else {
                stopIndeterminateAnimations();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDrawBackgroundStroke) {
            canvas.drawOval(mDrawRect, mBackgroundStrokePaint);
        }
        float start;
        float sweep;
        if (mIndeterminate) {
            float startAngle = mIndeterminateStartAngle;
            float sweepAngle = mIndeterminateSweepAngle;
            float offsetAngle = mIndeterminateOffsetAngle;
            float minimumAngle = mIndeterminateMinimumAngle;
            if (mIndeterminateGrowMode) {
                start = startAngle - offsetAngle;
                sweep = sweepAngle + minimumAngle;
            } else {
                start = startAngle + sweepAngle - offsetAngle;
                sweep = 360f - sweepAngle - minimumAngle;
            }
        } else {
            float maximum = mMaximum;
            float progress = mProgress;
            start = mStartAngle;
            if (Math.abs(progress) < Math.abs(maximum)) {
                sweep = progress / maximum * 360f;
            } else {
                sweep = 360f;
            }
        }
        canvas.drawArc(mDrawRect, start, sweep, false, mForegroundStrokePaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
        invalidateDrawRect(width, height);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        invalidateDrawRect(width, height);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mVisible = true;
        if (mIndeterminate) {
            startIndeterminateAnimations();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mVisible = false;
        stopIndeterminateAnimations();
        stopProgressAnimation();
    }

    private void initialize(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int defStyleAttr,
                            @StyleRes int defStyleRes) {
        mForegroundStrokePaint.setStyle(Paint.Style.STROKE);
        mBackgroundStrokePaint.setStyle(Paint.Style.STROKE);
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        if (attributeSet == null) {
            mMaximum = DEFAULT_MAXIMUM;
            mProgress = DEFAULT_PROGRESS;
            mStartAngle = DEFAULT_START_ANGLE;
            mIndeterminateMinimumAngle = DEFAULT_INDETERMINATE_MINIMUM_ANGLE;
            mProgressAnimator.setDuration(DEFAULT_PROGRESS_ANIMATION_DURATION);
            mIndeterminate = DEFAULT_INDETERMINATE;
            mAnimateProgress = DEFAULT_ANIMATE_PROGRESS;
            mDrawBackgroundStroke = DEFAULT_DRAW_BACKGROUND_STROKE;
            mForegroundStrokePaint.setColor(DEFAULT_FOREGROUND_STROKE_COLOR);
            mForegroundStrokePaint
                    .setStrokeWidth(Math.round(DEFAULT_FOREGROUND_STROKE_WIDTH_DP * displayMetrics.density));
            mBackgroundStrokePaint.setColor(DEFAULT_BACKGROUND_STROKE_COLOR);
            mBackgroundStrokePaint
                    .setStrokeWidth(Math.round(DEFAULT_BACKGROUND_STROKE_WIDTH_DP * displayMetrics.density));
            mIndeterminateStartAnimator.setDuration(DEFAULT_INDETERMINATE_ROTATION_ANIMATION_DURATION);
            mIndeterminateSweepAnimator.setDuration(DEFAULT_INDETERMINATE_SWEEP_ANIMATION_DURATION);
        } else {
            TypedArray attributes = null;
            try {
                attributes = context.getTheme()
                        .obtainStyledAttributes(attributeSet, R.styleable.CircularProgressBar, defStyleAttr,
                                defStyleRes);
                mMaximum = attributes.getFloat(R.styleable.CircularProgressBar_maximum, DEFAULT_MAXIMUM);
                mProgress = attributes.getFloat(R.styleable.CircularProgressBar_progress, DEFAULT_PROGRESS);
                float startAngle = attributes.getFloat(R.styleable.CircularProgressBar_startAngle, DEFAULT_START_ANGLE);
                checkStartAngle(startAngle);
                mStartAngle = startAngle;
                float minimumAngle = attributes.getFloat(R.styleable.CircularProgressBar_indeterminateMinimumAngle,
                        DEFAULT_INDETERMINATE_MINIMUM_ANGLE);
                checkIndeterminateMinimumAngle(minimumAngle);
                mIndeterminateMinimumAngle = minimumAngle;
                long progressDuration = attributes.getInteger(R.styleable.CircularProgressBar_progressAnimationDuration,
                        DEFAULT_PROGRESS_ANIMATION_DURATION);
                checkAnimationDuration(progressDuration);
                mProgressAnimator.setDuration(progressDuration);
                long rotationDuration = attributes
                        .getInteger(R.styleable.CircularProgressBar_indeterminateRotationAnimationDuration,
                                DEFAULT_INDETERMINATE_ROTATION_ANIMATION_DURATION);
                checkAnimationDuration(rotationDuration);
                mIndeterminateStartAnimator.setDuration(rotationDuration);
                long sweepDuration = attributes
                        .getInteger(R.styleable.CircularProgressBar_indeterminateSweepAnimationDuration,
                                DEFAULT_INDETERMINATE_SWEEP_ANIMATION_DURATION);
                checkAnimationDuration(sweepDuration);
                mIndeterminateSweepAnimator.setDuration(sweepDuration);
                mForegroundStrokePaint.setColor(attributes
                        .getColor(R.styleable.CircularProgressBar_foregroundStrokeColor,
                                DEFAULT_FOREGROUND_STROKE_COLOR));
                mBackgroundStrokePaint.setColor(attributes
                        .getColor(R.styleable.CircularProgressBar_backgroundStrokeColor,
                                DEFAULT_BACKGROUND_STROKE_COLOR));
                float foregroundWidth = attributes.getDimension(R.styleable.CircularProgressBar_foregroundStrokeWidth,
                        Math.round(DEFAULT_FOREGROUND_STROKE_WIDTH_DP * displayMetrics.density));
                checkWidth(foregroundWidth);
                mForegroundStrokePaint.setStrokeWidth(foregroundWidth);
                float backgroundWidth = attributes.getDimension(R.styleable.CircularProgressBar_backgroundStrokeWidth,
                        Math.round(DEFAULT_BACKGROUND_STROKE_WIDTH_DP * displayMetrics.density));
                checkWidth(backgroundWidth);
                mBackgroundStrokePaint.setStrokeWidth(backgroundWidth);
                mAnimateProgress = attributes
                        .getBoolean(R.styleable.CircularProgressBar_animateProgress, DEFAULT_ANIMATE_PROGRESS);
                mDrawBackgroundStroke = attributes.getBoolean(R.styleable.CircularProgressBar_drawBackgroundStroke,
                        DEFAULT_DRAW_BACKGROUND_STROKE);
                mIndeterminate =
                        attributes.getBoolean(R.styleable.CircularProgressBar_indeterminate, DEFAULT_INDETERMINATE);
            } finally {
                if (attributes != null) {
                    attributes.recycle();
                }
            }
        }
        mProgressAnimator.setInterpolator(new DecelerateInterpolator());
        mProgressAnimator.addUpdateListener(new ProgressUpdateListener());
        mIndeterminateStartAnimator.setFloatValues(360f);
        mIndeterminateStartAnimator.setRepeatMode(ValueAnimator.RESTART);
        mIndeterminateStartAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mIndeterminateStartAnimator.setInterpolator(new LinearInterpolator());
        mIndeterminateStartAnimator.addUpdateListener(new StartUpdateListener());
        mIndeterminateSweepAnimator.setFloatValues(360f - mIndeterminateMinimumAngle * 2f);
        mIndeterminateSweepAnimator.setInterpolator(new DecelerateInterpolator());
        mIndeterminateSweepAnimator.addUpdateListener(new SweepUpdateListener());
        mIndeterminateSweepAnimator.addListener(new SweepAnimatorListener());
    }

    private void invalidateDrawRect() {
        int width = getWidth();
        int height = getHeight();
        if (width > 0 && height > 0) {
            invalidateDrawRect(width, height);
        }
    }

    private void invalidateDrawRect(int width, int height) {
        float thickness;
        if (mDrawBackgroundStroke) {
            thickness = Math.max(mForegroundStrokePaint.getStrokeWidth(), mBackgroundStrokePaint.getStrokeWidth());
        } else {
            thickness = mForegroundStrokePaint.getStrokeWidth();
        }
        if (width > height) {
            float offset = (width - height) / 2f;
            mDrawRect.set(offset + thickness / 2f + 1f, thickness / 2f + 1f, width - offset - thickness / 2f - 1f,
                    height - thickness / 2f - 1f);
        } else if (width < height) {
            float offset = (height - width) / 2f;
            mDrawRect.set(thickness / 2f + 1f, offset + thickness / 2f + 1f, width - thickness / 2f - 1f,
                    height - offset - thickness / 2f - 1f);
        } else {
            mDrawRect.set(thickness / 2f + 1f, thickness / 2f + 1f, width - thickness / 2f - 1f,
                    height - thickness / 2f - 1f);
        }
    }

    private void setProgressInternal(float progress) {
        mProgress = progress;
        invalidate();
    }

    private void setProgressAnimated(float progress) {
        mProgressAnimator.setFloatValues(mProgress, progress);
        mProgressAnimator.start();
    }

    private void stopProgressAnimation() {
        if (mProgressAnimator.isRunning()) {
            mProgressAnimator.cancel();
        }
    }

    private void stopIndeterminateAnimations() {
        if (mIndeterminateStartAnimator.isRunning()) {
            mIndeterminateStartAnimator.cancel();
        }
        if (mIndeterminateSweepAnimator.isRunning()) {
            mIndeterminateSweepAnimator.cancel();
        }
    }

    private void startIndeterminateAnimations() {
        if (!mIndeterminateStartAnimator.isRunning()) {
            mIndeterminateStartAnimator.start();
        }
        if (!mIndeterminateSweepAnimator.isRunning()) {
            mIndeterminateSweepAnimator.start();
        }
    }

    private void checkStartAngle(float angle) {
        if (angle < -360f || angle > 360f) {
            throw new IllegalArgumentException("Start angle value should be between -360 and 360 degrees (inclusive)");
        }
    }

    private void checkIndeterminateMinimumAngle(float angle) {
        if (angle < 0f || angle > 180f) {
            throw new IllegalArgumentException(
                    "Indeterminate minimum angle value should be between 0 and 180 degrees (inclusive)");
        }
    }

    private void checkAnimationDuration(long duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Animation duration can't be negative");
        }
    }

    private void checkWidth(float width) {
        if (width < 0f) {
            throw new IllegalArgumentException("Width can't be negative");
        }
    }

    private final class ProgressUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            setProgressInternal((float) animation.getAnimatedValue());
        }
    }

    private final class StartUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mIndeterminateStartAngle = (float) animation.getAnimatedValue();
            invalidate();
        }
    }

    private final class SweepUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mIndeterminateSweepAngle = (float) animation.getAnimatedValue();
        }
    }

    private final class SweepAnimatorListener implements ValueAnimator.AnimatorListener {
        private boolean mCancelled;

        @Override
        public void onAnimationStart(Animator animation) {
            mCancelled = false;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (!mCancelled) {
                post(mSweepRestartAction);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mCancelled = true;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
            // Do nothing
        }
    }

    private final class SweepRestartAction implements Runnable {
        @Override
        public void run() {
            mIndeterminateGrowMode = !mIndeterminateGrowMode;
            if (mIndeterminateGrowMode) {
                mIndeterminateOffsetAngle = (mIndeterminateOffsetAngle + mIndeterminateMinimumAngle * 2f) % 360f;
            }
            if (mIndeterminateSweepAnimator.isRunning()) {
                mIndeterminateSweepAnimator.cancel();
            }
            if (mVisible) {
                mIndeterminateSweepAnimator.start();
            }
        }
    }
}