package xyz.twinone.droneremote;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Luuk W. (Twinone).
 */
public class JoyStickView extends View implements View.OnTouchListener {


    private Listener mListener;

    private static final float SIZE = 0.5f;
    private static final int WIDTH = 10;
    private Paint mPaint;

    private float mStartX = -1;
    private float mStartY = -1;

    private boolean mResetX = true;
    private boolean mResetY = true;
    private boolean mInvertX = false;
    private boolean mInvertY = false;

    public JoyStickView(Context context) {
        super(context);
        init();
    }

    public JoyStickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public JoyStickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.parseColor("#ff00ff"));
        mPaint.setStrokeWidth(WIDTH);
        mPaint.setStyle(Paint.Style.STROKE);

        setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int w = getWidth();
        int h = getHeight();

        if (mStartX == -1) {
            mStartX = w / 2;
        }
        if (mStartY == -1) {
            mStartY = w / 2;
        }

        canvas.save();
        canvas.drawCircle(mStartX, mStartY, (int) (getWidth() / 2 * SIZE), mPaint);

        canvas.drawCircle(w / 2, h / 2, getWidth() / 2 - WIDTH / 2, mPaint);

        canvas.restore();

        invalidate();
    }


    @Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return true;
            case MotionEvent.ACTION_MOVE:

                update(e.getX(), e.getY());

                return true;

            case MotionEvent.ACTION_UP:
                Float xx = mResetX ? (float) getWidth() / 2 : null;
                Float yy = mResetY ? (float) getWidth() / 2 : null;
                update(xx, yy);
                return true;
        }

        return false;
    }

    public void setResetX(boolean resetX) {
        mResetX = resetX;
    }

    public void setInvertX(boolean invertX) {
        mInvertX = invertX;
    }

    public void setResetY(boolean resetY) {
        mResetY = resetY;
    }

    public void setInvertY(boolean invertY) {
        mInvertY = invertY;
    }

    private void update(Float x, Float y) {
        if (x != null) mStartX = x;
        if (y != null) mStartY = y;

        float xx = clamp(mStartX / getWidth());
        float yy = clamp(mStartY / getWidth());
        if (mInvertX) xx = 1 - xx;
        if (mInvertY) yy = 1 - yy;
        if (mListener != null)
            mListener.onUpdate(this, xx, yy);
    }

    private float clamp(float x) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return x;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    public interface Listener {
        /**
         * Called when an update event has ocurred. 0 <= x, y <= 1
         *
         * @param x
         * @param y
         */
        void onUpdate(JoyStickView v, float x, float y);
    }
}
