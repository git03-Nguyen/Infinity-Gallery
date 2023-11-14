package edu.team08.infinitegallery.singlephoto;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

public class ZoomViewPager extends ViewPager {
    public ZoomViewPager(Context context, AttributeSet attributes){
        super(context, attributes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        ZoomImageView view = ((ViewPagerAdapter) getAdapter()).getImageView();
        if(view != null) return (!view.isZooming() && super.onTouchEvent(ev));

        return super.onTouchEvent(ev);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ZoomImageView view = ((ViewPagerAdapter) getAdapter()).getImageView();
        if(view != null) return (!view.isZooming() && super.onInterceptTouchEvent(ev));

        return super.onInterceptTouchEvent(ev);
    }
}
