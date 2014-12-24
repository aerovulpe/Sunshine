package me.aerovulpe.sunshine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by Aaron on 24/12/2014.
 */
public class CompassView extends View {

    private static final String LOG_TAG = CompassView.class.getSimpleName();
    private Paint circleBrush;
    private Paint indicatorBrush;
    private Paint textBrush;
    private float mDirection;
    private int mCenterX;
    private int mCenterY;
    private int mRimRadius;
    private int mFaceRadius;
    private int mIndicatorRadius;
    private Context mContext;

    public CompassView(Context context) {
        super(context);
        initTools();
        mContext = context;
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTools();
        mContext = context;
    }

    public CompassView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        initTools();
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        if (hSpecMode == MeasureSpec.EXACTLY) {
            myHeight = hSpecSize;
        } else if (hSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = wSpecSize;
        if (wSpecMode == MeasureSpec.EXACTLY) {
            myWidth = wSpecSize;
        } else if (wSpecMode == MeasureSpec.AT_MOST) {
            // Wrap Content
        }

        setMeasuredDimension(myWidth, myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.v(LOG_TAG, "OnDraw");


        drawCircleBg2(canvas);
        drawArrowIndicator(canvas);
        drawDirections(canvas);

    }

    private void initTools() {
        Log.v(LOG_TAG, "Init Tools");
        circleBrush = new Paint();
        indicatorBrush = new Paint();
        textBrush = new Paint();

        circleBrush.setAntiAlias(true);
        indicatorBrush.setAntiAlias(true);
        textBrush.setAntiAlias(true);

        mDirection = 330;
    }

    private void drawCircleBg(Canvas canvas) {

        Log.v(LOG_TAG, "Drawing the background circle.");
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height) {
            mRimRadius = height / 2;
        } else {
            mRimRadius = width / 2;
        }

        mCenterX = mRimRadius;
        mCenterY = mRimRadius;


        circleBrush.setStyle(Paint.Style.FILL_AND_STROKE);
        int[] colors = {0xFFB0B0B0, 0xFFFFFFFF, 0xFF000000};
        circleBrush.setShader(new RadialGradient(mCenterX, mCenterY, mRimRadius, colors, null, Shader.TileMode.MIRROR));
        canvas.drawCircle(mCenterX, mCenterY, mRimRadius, circleBrush);

        mFaceRadius = mRimRadius - 20;
        circleBrush.reset();
        int[] colors2 = {0xFF00A6F1, 0xFF0074CB, 0xFF0074CB, 0xFF0053B3};
        circleBrush.setShader(new RadialGradient(mCenterX, mCenterY, mFaceRadius, colors2, null, Shader.TileMode.MIRROR));
        canvas.drawCircle(mCenterX, mCenterY, mFaceRadius, circleBrush);

        circleBrush.reset();
        mIndicatorRadius = mFaceRadius / 10;

    }

    private void drawArrowIndicator(Canvas canvas) {

        Log.v(LOG_TAG, "Drawing Indicator");

        indicatorBrush.reset();
        indicatorBrush.setColor(Color.RED);
        indicatorBrush.setStyle(Paint.Style.FILL);

        Path arrow = new Path();
        arrow.addCircle(mCenterX, mCenterY, mIndicatorRadius, Path.Direction.CW);
        arrow.moveTo(mCenterX, mCenterY);
        arrow.lineTo(mCenterX - mIndicatorRadius, mCenterY);
        arrow.lineTo(mCenterX, mCenterY - (mIndicatorRadius * 6));
        arrow.lineTo(mCenterX + mIndicatorRadius, mCenterY);

        arrow.close();

        Matrix mMatrix = new Matrix();
        RectF bounds = new RectF();
        arrow.computeBounds(bounds, true);
        mMatrix.setRotate(mDirection, mCenterX, mCenterY);
        arrow.transform(mMatrix);

        canvas.drawPath(arrow, indicatorBrush);

    }

    private void drawDirections(Canvas canvas) {

        Log.v(LOG_TAG, "Drawing directions");

        textBrush.setTextSize(30.0f);
        textBrush.setTypeface(Typeface.DEFAULT_BOLD);
        textBrush.setColor(Color.WHITE);
        textBrush.setTextAlign(Paint.Align.CENTER);

        canvas.drawText("N", mCenterX, mCenterY - mFaceRadius + 26.0f, textBrush);
        canvas.drawText("W", mCenterX - mFaceRadius + 25.0f, mCenterY, textBrush);
        canvas.drawText("E", mCenterX + mFaceRadius - 25.0f, mCenterY, textBrush);
        canvas.drawText("S", mCenterX, mCenterY + mFaceRadius - 10.0f, textBrush);

    }


    public void updateDirection(float dir) {
        Log.v(LOG_TAG, "Updating direction");
        mDirection = dir;
        invalidate();

        AccessibilityManager accessibilityManager =
                (AccessibilityManager) mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);

        if (accessibilityManager.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }

    private void drawCircleBg2(Canvas canvas) {

        Log.v(LOG_TAG, "Drawing the background circle.");
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height) {
            mRimRadius = height / 2;
        } else {
            mRimRadius = width / 2;
        }

        mCenterX = mRimRadius;
        mCenterY = mRimRadius;


        circleBrush.setStyle(Paint.Style.STROKE);
        circleBrush.setColor(getResources().getColor(R.color.sunshine_blue));
        canvas.drawCircle(mCenterX, mCenterY, mRimRadius, circleBrush);

        mFaceRadius = mRimRadius - 20;
        circleBrush.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawCircle(mCenterX, mCenterY, mFaceRadius, circleBrush);

        circleBrush.reset();
        mIndicatorRadius = mFaceRadius / 10;

    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(String.valueOf(mDirection));
        return true;
    }
}
