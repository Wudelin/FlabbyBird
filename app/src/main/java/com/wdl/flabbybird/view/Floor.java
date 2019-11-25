package com.wdl.flabbybird.view;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

/**
 * Create by: wdl at 2019/11/25 14:29
 */
public class Floor
{
    /**
     * 高度位于屏幕高度的4/5处
     */
    private static final float FLOOR_POS = 4 / 5F;
    /**
     * 移动移动的宽高
     */
    private int mFloorWidth;
    private int mFloorHeight;

    private int x;
    private int y;

    /**
     * 面板宽高
     */
    private int mPanelWidth;
    private int mPanelHeight;

    private RectF mFloorRectF = new RectF();

    /**
     * 填充
     */
    private BitmapShader mFloorBitmapShader;

    public Floor(int mPanelWidth, int mPanelHeight, Bitmap mFloorBg)
    {
        this.mPanelWidth = mPanelWidth;
        this.mPanelHeight = mPanelHeight;
        this.y = (int) (mPanelHeight * FLOOR_POS);
        this.mFloorBitmapShader = new BitmapShader(mFloorBg, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
    }

    public void draw(Canvas canvas, Paint mPaint)
    {
        if (-x > mPanelWidth)
        {
            x = x % mPanelWidth;
        }
        canvas.save();
        canvas.translate(x, y);
        mPaint.setShader(mFloorBitmapShader);
        canvas.drawRect(x, 0, -x + mPanelWidth, mPanelHeight - y, mPaint);
        canvas.restore();
        mPaint.setShader(null);
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }
}
