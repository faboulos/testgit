package com.kerawa.app.utilities;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by etinge mabian on 04/04/16.
 */
public class ImageAdapterGrid extends ImageView{
    public ImageAdapterGrid(Context context) {
        super(context);
    }

    public ImageAdapterGrid(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageAdapterGrid(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
