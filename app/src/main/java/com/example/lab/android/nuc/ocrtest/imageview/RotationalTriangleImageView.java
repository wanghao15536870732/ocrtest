package com.example.lab.android.nuc.ocrtest.imageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import static java.lang.Math.sqrt;

@SuppressLint("AppCompatCustomView")
public class RotationalTriangleImageView extends ImageView {
    private float parameter;
    private RotationalTriangleDrawable drawable;

    public RotationalTriangleImageView(Context context) {
        super(context);
        init();
    }

    public RotationalTriangleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RotationalTriangleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        drawable = new RotationalTriangleDrawable();
        this.setImageDrawable(drawable);

    }

    public void start() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "parameter", 0, 1);
        animator.setDuration(400);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                drawable.setFlag(!drawable.getFlag());
                drawable.setParameter(0);

            }
        });
        animator.start();

    }

    public float getParameter() {
        return drawable.getParameter();
    }

    public void setParameter(float parameter) {
        this.parameter = parameter;
        drawable.setParameter(parameter);
    }

    private class RotationalTriangleDrawable extends Drawable {

        private boolean flag = true;//由三角形向平行线变化时，即当前是三角形，为true；

        private float rotation;
        private float rotation2;
        private float rotation3;
        private float parameter;

        private Paint alphaPaint;
        private int alpha;
        private float seniLength;
        private Paint paint;

        @Override
        public void draw(Canvas canvas) {
            initPaint();


            if (flag) {
                alpha = 255 - (int) (parameter * 255);//其中的一条杠消失；

            } else {
                alpha = (int) (parameter * 255);//消失的那条杠出现；

            }
            alphaPaint.setAlpha(alpha);


            if (flag) {
                rotation = parameter * 180;
                rotation2 = 60 - parameter * 60;
                rotation3 = 60 + parameter * 30;
                seniLength=24-24*parameter;

            } else {
                rotation = 180 + parameter * 180;
                rotation2 = parameter * 60;
                rotation3 = 90 - parameter * 30;
                seniLength=24*parameter;

            }

            canvas.save();
            //1杠，永远显示，起到参照的作用；
            canvas.translate(30, 30);
            canvas.rotate(rotation);
            canvas.drawLine(-(float) (8 * sqrt(3)), 24, -(float) (8 * sqrt(3)), -24, paint);

            //2
            canvas.save();
            canvas.rotate(rotation2);
            canvas.drawLine((float) (8 * sqrt(3)), 24, (float) (8 * sqrt(3)), -24, paint);

            canvas.restore();

            //3
            canvas.save();
            canvas.rotate(-rotation3);
            canvas.drawLine((float) (8 * sqrt(3)), seniLength, (float) (8 * sqrt(3)), -seniLength, alphaPaint);

            canvas.restore();


            canvas.restore();


        }

        private void initPaint() {
            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor( Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6);

            alphaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            alphaPaint.setColor(Color.WHITE);
            alphaPaint.setStyle(Paint.Style.STROKE);
            alphaPaint.setStrokeWidth(6);

        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter cf) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        public void setRotation(float rotation) {
            this.rotation = rotation;
        }

        public void setParameter(float parameter) {
            this.parameter = parameter;
            invalidateSelf();
        }

        public float getParameter() {
            return parameter;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        public boolean getFlag() {
            return flag;
        }
    }

}
