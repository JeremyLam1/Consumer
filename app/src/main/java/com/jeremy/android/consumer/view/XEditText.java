package com.jeremy.android.consumer.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;


/**
 * TODO: document your custom view class.
 */
public class XEditText extends AutoCompleteTextView {

    private DrawableLeftListener mLeftListener;
    private DrawableRightListener mRightListener;

    final int DRAWABLE_LEFT = 0;
    final int DRAWABLE_RIGHT = 2;


    public XEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public XEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XEditText(Context context) {
        super(context);
    }

    public void setDrawableLeftListener(DrawableLeftListener listener) {
        this.mLeftListener = listener;
    }

    public void setDrawableRightListener(DrawableRightListener listener) {
        this.mRightListener = listener;
    }

    public interface DrawableLeftListener {
        void onDrawableLeftClick(View view);
    }

    public interface DrawableRightListener {
        void onDrawableRightClick(View view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (mLeftListener != null) {
                    Drawable drawableLeft = getCompoundDrawables()[DRAWABLE_LEFT];
                    if (drawableLeft != null && event.getRawX() <= (getLeft() + drawableLeft.getBounds().width())) {
                        mLeftListener.onDrawableLeftClick(this);
                        event.setAction(MotionEvent.ACTION_CANCEL);
                        requestFocus();
                    }
                }
                Drawable drawableRight = getCompoundDrawables()[DRAWABLE_RIGHT];
                if (drawableRight != null && event.getRawX() >= (getRight() - drawableRight.getBounds().width())) {
                    setText("");
                    if (mRightListener != null) {
                        mRightListener.onDrawableRightClick(this);
                    }
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    requestFocus();
                }
                break;
        }

        return super.onTouchEvent(event);
    }
}
