package com.example.vyacheslav.doodlz;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.provider.MediaStore;
import android.support.v4.print.PrintHelper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class DoodleView extends View {

    private static final float TOUCH_TOLERANCE = 10;

    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;
    private final Paint mPaintScreen;
    private final Paint mPaintLine;

    private final Map<Integer, Path> mPathMap = new HashMap<>();
    private final Map<Integer, Point> mPreviousPointMap = new HashMap<>();

    public DoodleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaintScreen = new Paint();

        mPaintLine = new Paint();
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStyle(Paint.Style.STROKE);
        mPaintLine.setStrokeWidth(5);
        mPaintLine.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        mBitmapCanvas = new Canvas(mBitmap);
        mBitmap.eraseColor(Color.WHITE);
    }

    public void clear() {
        mPathMap.clear();
        mPreviousPointMap.clear();
        mBitmap.eraseColor(Color.WHITE);
        invalidate();
    }

    public void setDrawingColor(int color) {
        mPaintLine.setColor(color);
    }

    public int getDrawingColor() {
        return mPaintLine.getColor();
    }

    public void setLineWidth(int width) {
        mPaintLine.setStrokeWidth(width);
    }

    public int getLineWidth() {
        return (int) mPaintLine.getStrokeWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(mBitmap, 0, 0, mPaintScreen);

        for (Integer key : mPathMap.keySet()) {
            canvas.drawPath(mPathMap.get(key), mPaintLine);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex), event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchEnded(int lineID) {
        Path path = mPathMap.get(lineID);
        mBitmapCanvas.drawPath(path, mPaintLine);
        path.reset();
    }

    private void touchMoved(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            if (mPathMap.containsKey(pointerID)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = mPathMap.get(pointerID);
                Point point = mPreviousPointMap.get(pointerID);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(point.x, point.y, (newX + point.x) / 2, (newY + point.y) / 2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void touchStarted(float x, float y, int lineID) {
        Path path;
        Point point;

        if (mPathMap.containsKey(lineID)) {
            path = mPathMap.get(lineID);
            path.reset();
            point = mPreviousPointMap.get(lineID);
        } else {
            path = new Path();
            mPathMap.put(lineID, path);
            point = new Point();
            mPreviousPointMap.put(lineID, point);
        }
        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    public void saveImage() {
        final String name = "Doodlz" + System.currentTimeMillis() + ".jpg";

        String location = MediaStore.Images.Media.insertImage(
                getContext().getContentResolver(), mBitmap, name, "Doodlz Drawing");

        if (location != null) {
            Toast message = Toast.makeText(getContext(), R.string.message_saved, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        } else {
            Toast message = Toast.makeText(getContext(), R.string.message_error_saving, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        }
    }

    public void printImage() {
        if (PrintHelper.systemSupportsPrint()) {
            PrintHelper printHelper = new PrintHelper(getContext());

            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("Doodlz Image", mBitmap);
        } else {
            Toast message = Toast.makeText(getContext(), R.string.message_error_printing, Toast.LENGTH_SHORT);
            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
            message.show();
        }
    }
}
