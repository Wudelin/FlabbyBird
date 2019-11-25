package com.wdl.flabbybird.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.TypedValue;

/**
 * Create by: wdl at 2019/11/25 11:48
 * 鸟视图
 */
@SuppressWarnings("unused")
public class Bird
{
    /**
     * 高度为屏幕的2/3
     */
    private static final float HEIGHT_AT = 2 / 3F;
    /**
     * 鸟宽度
     */
    private static final float BIRD_WIDTH = 25;

    /**
     * 鸟的横纵坐标
     */
    private int x;
    private int y;

    /**
     * 鸟的宽高
     */
    private int mBirdWidth;
    private int mBirdHeight;
    /**
     * 鸟图片
     */
    private Bitmap mBirdMap;
    /**
     * 绘制区域
     */
    private RectF mBirdRectF = new RectF();

    public Bird(Context context, int mPanelWidth, int mPanelHeight, Bitmap mBirdMap)
    {
        this.mBirdMap = mBirdMap;
        // 坐标点
        this.x = mPanelWidth / 2 - mBirdMap.getWidth() / 2;
        this.y = (int) (mPanelHeight * HEIGHT_AT);

        // 根据鸟的宽度以及图片宽高计算出高度
        this.mBirdWidth = (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BIRD_WIDTH,
                context.getResources().getDisplayMetrics()) + 0.5f);
        this.mBirdHeight = mBirdWidth * this.mBirdMap.getHeight() / this.mBirdMap.getWidth();
    }

    public void draw(Canvas canvas)
    {
        mBirdRectF.set(x, y, x + mBirdWidth, y + mBirdHeight);
        if (canvas != null)
        {
            canvas.drawBitmap(mBirdMap, null, mBirdRectF, null);
        }

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

    public int getBirdWidth()
    {
        return mBirdWidth;
    }

    public void setBirdWidth(int mBirdWidth)
    {
        this.mBirdWidth = mBirdWidth;
    }

    public int getBirdHeight()
    {
        return mBirdHeight;
    }

    public void setBirdHeight(int mBirdHeight)
    {
        this.mBirdHeight = mBirdHeight;
    }
}
