package com.example.winlowcustomer.modal;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

public class AnimatedTextView extends View {
    private Paint paint;
    private Path path;
    private PathMeasure pathMeasure;
    private float pathLength;
    private float animatedValue;
    private Paint textPaint;

    public AnimatedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5f);
        paint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(50f);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);

        path = new Path();
        path.moveTo(100, 300); // Start position
        path.quadTo(300, 100, 500, 300); // Curve path for writing effect

        pathMeasure = new PathMeasure(path, false);
        pathLength = pathMeasure.getLength();

        startAnimation();
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, pathLength);
        animator.setDuration(3000); // Animation duration
        animator.addUpdateListener(animation -> {
            animatedValue = (float) animation.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path drawnPath = new Path();
        pathMeasure.getSegment(0, animatedValue, drawnPath, true);
        canvas.drawPath(drawnPath, paint);

        // Get position on path for text animation
        float[] pos = new float[2];
        pathMeasure.getPosTan(animatedValue, pos, null);
        canvas.drawText("WinloSpices", pos[0], pos[1], textPaint);
    }
}

