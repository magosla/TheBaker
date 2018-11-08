package com.naijaplanet.magosla.android.thebaker.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CustomFrameLayout extends FrameLayout {
    private OnLayoutChangedListener mLayoutChangedListener;
    @SuppressWarnings("unused")
    public interface OnLayoutChangedListener{
        void onLayout(boolean changed, int left, int top, int right, int bottom);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(mLayoutChangedListener != null){
            mLayoutChangedListener.onLayout(changed,left,top,right,bottom);
        }
    }

    public void setLayoutChangedListener(OnLayoutChangedListener listener){
        mLayoutChangedListener = listener;
    }

    public CustomFrameLayout(Context context) {
        super(context);
    }

    public CustomFrameLayout(Context context, AttributeSet attrSet) {
        super(context, attrSet);
    }
    public CustomFrameLayout(Context context, AttributeSet attrSet, int defStyleAttr) {
        super(context, attrSet, defStyleAttr);
    }

}
