package com.deange.marathonapp.ui.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.deange.marathonapp.R;

/**
 * Shimmer
 * User: romainpiel
 * Date: 06/03/2014
 * Time: 10:19
 */
public class ShimmerTextView
        extends TextView
        implements Animator.AnimatorListener {

    private static final int DEFAULT_REFLECTION_COLOR = 0xFFAAAAAA;

    // center position of the gradient
    private float gradientX;
    private ShapeDrawable.ShaderFactory linearGradientFactory;
    private int reflectionColor;

    // true when animating
    private boolean isShimmering;

    // true after first global layout
    private boolean isSetUp;

    // callback called after first global layout
    private AnimationSetupCallback callback;

    public interface AnimationSetupCallback {
        void onSetupAnimation(ShimmerTextView shimmerTextView);
    }

    public ShimmerTextView(Context context) {
        this(context, null);
    }

    public ShimmerTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShimmerTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public float getMaskX() {
        return gradientX;
    }

    public void setMaskX(float maskX) {
        gradientX = maskX;
        invalidate();
    }

    public boolean isShimmering() {
        return isShimmering;
    }

    protected void setShimmering(boolean isShimmering) {
        this.isShimmering = isShimmering;
    }

    public boolean isSetUp() {
        return isSetUp;
    }

    public void setSetUp(boolean isSetUp) {
        this.isSetUp = isSetUp;
    }

    public void setAnimationSetupCallback(AnimationSetupCallback callback) {
        this.callback = callback;
    }

    private void init(Context context, AttributeSet attributeSet) {

        reflectionColor = DEFAULT_REFLECTION_COLOR;

        if (attributeSet != null) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.ShimmerTextView, 0, 0);
            if (a != null) {
                try {
                    reflectionColor = a.getColor(R.styleable.ShimmerTextView_reflectionColor, DEFAULT_REFLECTION_COLOR);
                } catch (Exception e) {
                    Log.e("ShimmerTextView", "Error while creating the view:", e);
                } finally {
                    a.recycle();
                }
            }
        }

        linearGradientFactory = new LinearGradientFactory();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                isSetUp = true;

                if (callback != null) {
                    callback.onSetupAnimation(ShimmerTextView.this);
                }

                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // only draw the shader gradient over the text while animating
        if (isShimmering) {
            getPaint().setShader(linearGradientFactory.resize(getWidth(), getHeight()));
        } else {
            getPaint().setShader(null);
        }

        super.onDraw(canvas);
    }

    public void animate(final Animator.AnimatorListener animatorListener, final int durationMillis) {
        setShimmering(true);

        final Runnable animate = new Runnable() {
            @Override
            public void run() {
                ObjectAnimator animator = ObjectAnimator.ofFloat(ShimmerTextView.this, "maskX", 0, getWidth());
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setDuration(durationMillis);
                animator.setStartDelay(0);
                animator.addListener(ShimmerTextView.this);
                if (animatorListener != null) {
                    animator.addListener(animatorListener);
                }
                animator.start();
            }
        };

        if (!isSetUp()) {
            setAnimationSetupCallback(new ShimmerTextView.AnimationSetupCallback() {
                @Override
                public void onSetupAnimation(final ShimmerTextView shimmerTextView) {
                    animate.run();
                }
            });
        } else {
            animate.run();
        }
    }

    @Override
    public void onAnimationStart(Animator animation) {
        // Nothing to do here
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        setShimmering(false);
        postInvalidate();
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        setShimmering(false);
        postInvalidate();
    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        // Nothing to do here
    }

    /**
     * Factory class creating LinearGradient shaders. The shader is based on current value of gradientX
     */
    private class LinearGradientFactory extends ShapeDrawable.ShaderFactory {

        @Override
        public Shader resize(int width, int height) {

            // our linear gradient's width is 3 times bigger than the view's width
            // it is divided in 4 parts:
            // - text color
            // - gradient text color - reflection color
            // - gradient reflection color - text color
            // - text color
            // addition of two central parts width = view width

            float delta = gradientX / (float) getWidth();
            int textColor = getCurrentTextColor();

            return new LinearGradient(-width, 0, 2 * width, 0,
                    new int[]{
                            textColor,
                            textColor,
                            reflectionColor,
                            textColor,
                            textColor
                    },
                    new float[]{
                            0,
                            delta - 1f / 3f,
                            delta,
                            delta + 1f / 3f,
                            1
                    },
                    Shader.TileMode.CLAMP);
        }
    }
}