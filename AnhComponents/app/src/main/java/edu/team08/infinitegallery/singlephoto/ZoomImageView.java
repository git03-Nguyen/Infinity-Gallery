package edu.team08.infinitegallery.singlephoto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class ZoomImageView extends ImageView {
    private Matrix transfromMatrix;
    Context context;

    // We can be in one of these 3 states
    private static final float EPSILON = 0.001F;
    private static final int NONE = 0;
    private static final int DRAGGING = 1;
    private static final int ZOOMING = 2;
    int currentState = NONE;

    // Remember some things for zooming
    float minScale = 1f;
    float maxScale = 3f;
    static float currentScale = 1f;
    ScaleGestureDetector mScaleDetector;
    PointF startPoint = new PointF();
    PointF lastPoint = new PointF();

    float[] matrixVal;
    int viewWidth, viewHeight;
    int oldMeasuredWidth, oldMeasuredHeight;
    protected float origWidth, origHeight;
    static final int CLICK = 3;

    public ZoomImageView(Context context) {
        super(context);
        currentScale = 1f;
        initiate(context);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        currentScale = 1f;
        initiate(context);
    }
    public boolean isZooming() {
        return (!(Math.abs(currentScale - 1) <= EPSILON));
    }
    public void setMaxZoom(float x) {
        maxScale = x;
    }
    private void initiate(Context context) {
        super.setClickable(true);
        this.context = context;
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        transfromMatrix = new Matrix();
        matrixVal = new float[9];
        setImageMatrix(transfromMatrix);
        setScaleType(ScaleType.MATRIX);

        setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                PointF currPoint = new PointF(event.getX(), event.getY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentState = DRAGGING;
                        lastPoint.set(currPoint);
                        startPoint.set(lastPoint);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (currentState == DRAGGING) {
                            float deltaX = currPoint.x - lastPoint.x;
                            float deltaY = currPoint.y - lastPoint.y;
                            float fixTransX = getFixDragTrans(deltaX, viewWidth, origWidth * currentScale);
                            float fixTransY = getFixDragTrans(deltaY, viewHeight, origHeight * currentScale);

                            transfromMatrix.postTranslate(fixTransX, fixTransY);
                            lastPoint.set(currPoint.x, currPoint.y);
                            fixTrans();
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        currentState = NONE;
                        int dx = (int) Math.abs(currPoint.x - startPoint.x);
                        int dy = (int) Math.abs(currPoint.y - startPoint.y);
                        if (dx < CLICK && dy < CLICK) performClick();
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        currentState = NONE;
                        break;
                }

                setImageMatrix(transfromMatrix);
                invalidate();
                return true; // indicate event was handled
            }
        });
    }



    void fixTrans() {
        transfromMatrix.getValues(matrixVal);
        float transX = matrixVal[Matrix.MTRANS_X];
        float transY = matrixVal[Matrix.MTRANS_Y];

        float fixTransX = getFixTrans(transX, viewWidth, origWidth * currentScale);
        float fixTransY = getFixTrans(transY, viewHeight, origHeight * currentScale);

        if (fixTransX != 0 || fixTransY != 0) transfromMatrix.postTranslate(fixTransX, fixTransY);
    }

    float getFixTrans(float trans, float viewSize, float contentSize) {
        float minTrans, maxTrans;

        if (contentSize <= viewSize) {
            minTrans = 0;
            maxTrans = viewSize - contentSize;
        } else {
            maxTrans = 0;
            minTrans = viewSize - contentSize;
        }

        if (trans > maxTrans)  return  maxTrans - trans;
        if (trans < minTrans)  return  minTrans - trans;
        return 0;
    }

    float getFixDragTrans(float delta, float viewSize, float contentSize) {
        if (contentSize <= viewSize) return 0;

        return delta;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Rescale image on rotate
        if (oldMeasuredHeight == viewWidth && oldMeasuredHeight == viewHeight
                || viewWidth == 0 || viewHeight == 0) return;

        oldMeasuredHeight = viewHeight;
        oldMeasuredWidth = viewWidth;

        if (currentScale == 1) {
            // Fit to screen.
            float scale;

            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0
                    || drawable.getIntrinsicHeight() == 0) return;

            int bmWidth = drawable.getIntrinsicWidth();
            int bmHeight = drawable.getIntrinsicHeight();

            float scaleX = (float) viewWidth / (float) bmWidth;
            float scaleY = (float) viewHeight / (float) bmHeight;
            scale = Math.min(scaleX, scaleY);
            transfromMatrix.setScale(scale, scale);

            //Center the image
            float redundantYSpace = (float) viewHeight - (scale * (float) bmHeight);
            float redundantXSpace = (float) viewWidth - (scale * (float) bmWidth);
            redundantYSpace /= (float) 2;
            redundantXSpace /= (float) 2;

            transfromMatrix.postTranslate(redundantXSpace, redundantYSpace);

            origWidth = viewWidth - 2 * redundantXSpace;
            origHeight = viewHeight - 2 * redundantYSpace;

            setImageMatrix(transfromMatrix);
        }

        fixTrans();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            currentState = ZOOMING;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float coefficient = detector.getScaleFactor();
            currentScale *= coefficient;

            float origScale = currentScale;
            if (currentScale > maxScale) {
                currentScale = maxScale; coefficient = maxScale / origScale;
            } else if (currentScale < minScale) {
                currentScale = minScale; coefficient = minScale / origScale;
            }

            if (origWidth * currentScale <= viewWidth || origHeight * currentScale <= viewHeight)
                transfromMatrix.postScale(coefficient, coefficient, viewWidth / 2, viewHeight / 2);
            else transfromMatrix.postScale(coefficient, coefficient, detector.getFocusX(), detector.getFocusY());

            fixTrans();
            return true;
        }

    }
}