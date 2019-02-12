package com.solarexsoft.solarexhaloprogressview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

import androidx.annotation.Nullable;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:33/2019/2/12
 *    Desc:
 * </pre>
 */

public class SolarexHaloProgressView extends ImageView {
    public static final int READY = 1;
    public static final int PROGRESS = 2;
    public static final int FINISH = 3;

    private int status = PROGRESS;
    private float outRadius,innerRadius,round;

    private int progress;
    private float finishAnimValue = 0f;
    private Paint paint;
    private Paint textPaint;
    private float animatorValue = 0f;
    ValueAnimator anim;
    Path path;

    public SolarexHaloProgressView(Context context) {
        this(context, null);
    }

    public SolarexHaloProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SolarexHaloProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    private void init() {
        paint = new Paint();
        textPaint = new Paint();

        paint.setAntiAlias(true);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(Utils.dp2px(getContext(), 16f));
        textPaint.setColor(Utils.getColor(getContext(), R.color.grey));

        anim = ObjectAnimator.ofFloat(0f, 1f);
        anim.setDuration(700);
        anim.setRepeatCount(ValueAnimator.INFINITE);
        anim.setRepeatMode(ValueAnimator.REVERSE);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animatorValue = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });
        path = new Path();
        outRadius = Utils.dp2px(getContext(), 50f);
        innerRadius = Utils.dp2px(getContext(), 40f);
        round = outRadius - innerRadius;
        debugProgressStart();
    }

    private void debugProgressStart() {
        if (BuildConfig.DEBUG) {
            status = PROGRESS;
            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(this, "progress", 0, 100);
            progressAnimator.setDuration(3000);
            progressAnimator.setInterpolator(new BounceInterpolator());
            progressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    status = FINISH;
                    startFinishAnim();
                }
            });
            progressAnimator.start();
        }
    }

    private void startFinishAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(1200);
        valueAnimator.setInterpolator(new AccelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                finishAnimValue = (float) animation.getAnimatedValue();
                postInvalidateOnAnimation();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                debugProgressStart();
            }
        });
        valueAnimator.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        anim.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        path.reset();
        canvas.save();
        path.addRoundRect(new RectF(0f, 0f, getWidth(), getHeight()), round, round, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
        paint.setColor(Utils.getColor(getContext(), R.color.bantouming));
        canvas.save();
        canvas.translate(getWidth()/2f, getHeight()/2f);
        switch (status) {
            case PROGRESS:
                canvas.drawPaint(paint);
                int sc = canvas.saveLayer(-outRadius, -outRadius, outRadius, outRadius, paint, Canvas.ALL_SAVE_FLAG);
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.WHITE);
                paint.setShader(new RadialGradient(0f, 0f, outRadius, new int[]{Color.TRANSPARENT, Color.WHITE, Color.WHITE, Color.TRANSPARENT}, new float[]{0.1f, 0.4f, 0.8f, 1f}, Shader.TileMode.CLAMP));
                paint.setAlpha((int) (animatorValue*255));
                canvas.drawCircle(0f, 0f, innerRadius + round * animatorValue, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                paint.setShader(null);
                paint.setColor(Color.WHITE);
                canvas.drawCircle(0f, 0f, innerRadius, paint);
                paint.setXfermode(null);

                String text = progress + "%";
                canvas.drawText(text, 0-textPaint.measureText(text)/2f, (textPaint.descent() - textPaint.ascent())/2f, textPaint);
                canvas.restoreToCount(sc);
                break;
            case FINISH:
                int scc = canvas.saveLayer(-getWidth()/2f, -getHeight()/2f, getWidth()/2f, getHeight()/2f, paint, Canvas.ALL_SAVE_FLAG);
                canvas.drawPaint(paint);
                float maxRadius = (float) Math.sqrt(Math.pow(getWidth(), 2) + Math.pow(getHeight(), 2));
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                paint.setColor(Color.WHITE);
                canvas.drawCircle(0f, 0f, (outRadius + (maxRadius - outRadius) * finishAnimValue), paint);
                paint.setXfermode(null);
                canvas.restoreToCount(scc);
                break;
        }
        canvas.restore();
        canvas.restore();
    }
}
