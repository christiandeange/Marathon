/**
 * Copyright 2013 Romain Guy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deange.marathonapp.ui.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.deange.marathonapp.R;
import com.deange.marathonapp.utils.SvgHelper;

import java.util.ArrayList;
import java.util.List;

public class LogoView extends View {
    private static final String LOG_TAG = "IntroView";

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final SvgHelper mSvg = new SvgHelper(mPaint);
    private final int mSvgResource = R.raw.marathon_svg;

    private final Object mSvgLock = new Object();
    private List<SvgHelper.SvgPath> mPaths = new ArrayList<SvgHelper.SvgPath>(0);
    private Thread mLoader;

    private float mPhase;
    private int mDuration;
    private float mFadeFactor;

    private ObjectAnimator mSvgAnimator;

    public LogoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LogoView, defStyle, 0);
        try {
            if (a != null) {
                mPaint.setStrokeWidth(a.getFloat(R.styleable.LogoView_strokeWidth, 5.0f));
                mPaint.setColor(a.getColor(R.styleable.LogoView_strokeColor, Color.WHITE));
                mPhase = a.getFloat(R.styleable.LogoView_phase, 0.0f);
                mDuration = a.getInt(R.styleable.LogoView_duration, 1500);
                mFadeFactor = a.getFloat(R.styleable.LogoView_fadeFactor, 10.0f);
            }
        } finally {
            if (a != null) a.recycle();
        }

        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.STROKE);

        mSvgAnimator = ObjectAnimator.ofFloat(this, "phase", mPhase, 0.0f).setDuration(mDuration);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        synchronized (mSvgLock) {
            canvas.save();
            canvas.translate(getPaddingLeft(), getPaddingTop() - getPaddingBottom());
            for (SvgHelper.SvgPath svgPath : mPaths) {
                // We use the fade factor to speed up the alpha animation
                int alpha = (int) (Math.min((1.0f - mPhase) * mFadeFactor, 1.0f) * 255.0f);
                svgPath.paint.setAlpha(alpha);

                canvas.drawPath(svgPath.path, svgPath.paint);
            }
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(final int w, final int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mLoader != null) {
            try {
                mLoader.join();
            } catch (InterruptedException e) {
                Log.e(LOG_TAG, "Unexpected error", e);
            }
        }

        mLoader = new Thread(new Runnable() {
            @Override
            public void run() {
                mSvg.load(getContext(), mSvgResource);
                synchronized (mSvgLock) {
                    mPaths = mSvg.getPathsForViewport(
                            w - getPaddingLeft() - getPaddingRight(),
                            h - getPaddingTop() - getPaddingBottom());
                    updatePathsPhaseLocked();
                }
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (mSvgAnimator.isRunning()) mSvgAnimator.cancel();
                        mSvgAnimator.start();
                    }
                });
            }
        }, "SVG Loader");
        mLoader.start();
    }

    private void updatePathsPhaseLocked() {
        for (SvgHelper.SvgPath svgPath : mPaths) {
            svgPath.paint.setPathEffect(createPathEffect(svgPath.length, mPhase, 0.0f));
        }
    }

    @SuppressWarnings("unused")
    public float getPhase() {
        // USED BY ANIMATION FRAMEWORK
        return mPhase;
    }

    @SuppressWarnings("unused")
    public void setPhase(float phase) {
        // USED BY ANIMATION FRAMEWORK
        mPhase = phase;
        synchronized (mSvgLock) {
            updatePathsPhaseLocked();
        }
        invalidate();
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[] { pathLength, pathLength },
                Math.max(phase * pathLength, offset));
    }
}
