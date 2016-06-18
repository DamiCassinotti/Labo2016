package com.fiuba.dami.labo2016;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class Joystick extends View {

    JoyStickListener listener;
    Paint paint;
    float width;
    float height;
    float centerX;
    float centerY;
    float min;
    float posX;
    float posY;
    float radius;
    float buttonRadius;
    double power = -1;
    double angle = -1;
    RectF temp;

    //Background Color
    int padColor;

    //Stick Color
    int buttonColor;

    //Keeps joystick in last position
    boolean stayPut;

    //Button Size percentage of the minimum(width, height)
    int percentage = 25;

    //Background Bitmap
    Bitmap padBGBitmap = null;

    //Button Bitmap
    Bitmap buttonBitmap = null;

    public interface JoyStickListener {
        void onMove(Joystick joyStick, double angle, double power);
    }

    public Joystick(Context context) {
        super(context);
        init(context, null);
    }

    public Joystick(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);

        temp = new RectF();

        padColor = Color.WHITE;
        buttonColor = Color.RED;

//        if (attrs != null) {
//            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.JoyStick);
//            if (typedArray != null) {
//                padColor = Color.WHITE;
//                buttonColor = Color.RED;
//                stayPut = false;
//                percentage = 25;
//                if (percentage > 50) percentage = 50;
//                if (percentage < 25) percentage = 25;
//
//                int padResId = -1;
//                int buttonResId = -1;
//
//                if (padResId > 0) {
//                    padBGBitmap = BitmapFactory.decodeResource(getResources(), padResId);
//                }
//                if (buttonResId > 0) {
//                    buttonBitmap = BitmapFactory.decodeResource(getResources(), buttonResId);
//                }
//
//                typedArray.recycle();
//            }
//        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        centerX = width/2;
        centerY = height/2;
        min = Math.min(width, height);
        posX = centerX;
        posY = centerY;
        buttonRadius = (min / 2f * (percentage/100f));
        radius = (min / 2f * ((100f-percentage)/100f));
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if (canvas == null) return;
        if (padBGBitmap == null) {
            paint.setColor(padColor);
            canvas.drawCircle(centerX, centerY, radius, paint);
        } else {
            temp.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
            canvas.drawBitmap(padBGBitmap, null, temp, paint);
        }
        if (buttonBitmap == null) {
            paint.setColor(buttonColor);
            canvas.drawCircle(posX, posY, buttonRadius, paint);
        } else {
            temp.set(posX - buttonRadius, posY - buttonRadius, posX + buttonRadius, posY + buttonRadius);
            canvas.drawBitmap(buttonBitmap, null, temp, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                posX = event.getX();
                posY = event.getY();
                float abs = (float) Math.sqrt((posX - centerX) * (posX - centerX)
                        + (posY - centerY) * (posY - centerY));
                if (abs > radius) {
                    posX = ((posX - centerX) * radius / abs + centerX);
                    posY = ((posY - centerY) * radius / abs + centerY);
                }

                angle = Math.toDegrees(Math.atan2(centerY - posY, centerX - posX));

                power = (100 * Math.sqrt((posX - centerX)
                        * (posX - centerX) + (posY - centerY)
                        * (posY - centerY)) / radius);

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (!stayPut) {
                    posX = centerX;
                    posY = centerY;
                    angle = 0;
                    power = 0;
                    invalidate();
                }
                break;
        }

        if (listener != null) {
            listener.onMove(this, angle, power);
        }
        return true;
    }

    public void setPadColor(int padColor) {
        this.padColor = padColor;
    }

    public void setButtonColor(int buttonColor) {
        this.buttonColor = buttonColor;
    }

    public void setListener(JoyStickListener listener) {
        this.listener = listener;
    }

    public double getPower() {
        return power;
    }

    public double getAngle() {
        return angle;
    }

    public double getAngleDegrees() {
        return Math.toDegrees(angle);
    }

    public void enableStayPut(boolean enable) {
        this.stayPut = enable;
    }

    //size of button is a percentage of the minimum(width, height)
    //percentage must be between 25 - 50
    public void setButtonRadiusScale(int scale) {
        percentage = scale;
        if (percentage > 50) percentage = 50;
        if (percentage < 25) percentage = 25;
    }

    public void setPadBackground(int resId) {
        this.padBGBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    public void setButtonDrawable(int resId) {
        this.buttonBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }
}
