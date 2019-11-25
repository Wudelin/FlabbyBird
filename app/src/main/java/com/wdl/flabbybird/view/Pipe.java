package com.wdl.flabbybird.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

import java.util.Random;

/**
 * Create by: wdl at 2019/11/25 15:01
 */
public class Pipe
{
    /**
     * 下下管道间的间距
     */
    private static final float PIPE_BETWEEN_DIS = 1 / 5F;
    /**
     * 管道最大高度
     */
    private static final float MAX_PIPE_HEIGHT = 2 / 5F;
    /**
     * 管道最小高度
     */
    private static final float MIN_PIPE_HEIGHT = 1 / 5F;
    /**
     * 上下管道margin
     */
    private int betweenMargin;
    /**
     * 横坐标
     */
    private int x;
    /**
     * 上管道高度
     */
    private int topHeight;

    /**
     * 图片资源
     */
    private Bitmap mTop;
    private Bitmap mBottom;

    private static Random mRandom = new Random();

    public Pipe(int mPanelWidth, int mPanelHeight, Bitmap mTop, Bitmap mBottom)
    {
        this.mTop = mTop;
        this.mBottom = mBottom;

        this.betweenMargin = (int) (mPanelHeight * PIPE_BETWEEN_DIS);
        /**
         * 默认最左边
         */
        this.x = mPanelWidth;

        randomHeight(mPanelHeight);
    }

    /**
     * 生成在范围内的随机高度
     *
     * @param mPanelHeight 屏幕高度
     */
    private void randomHeight(int mPanelHeight)
    {
        topHeight = mRandom.nextInt((int) (mPanelHeight * (MAX_PIPE_HEIGHT - MIN_PIPE_HEIGHT)));
        topHeight = (int) (topHeight + mPanelHeight * MIN_PIPE_HEIGHT);
    }

    /**
     * @param mCanvas Canvas
     * @param mRectF  RectF 此处代表一整个管道
     */
    public void draw(Canvas mCanvas, RectF mRectF)
    {
        mCanvas.save();
        // RectF 此处代表一整个管道 ， （上半管道）假如mRectF高度为100，需要绘制25，则需要向上移动75
        // 绘制上管道
        mCanvas.translate(x, -(mRectF.bottom - topHeight));
        mCanvas.drawBitmap(mTop, null, mRectF, null);
        // 绘制下管道
        mCanvas.translate(0, mRectF.bottom - topHeight + topHeight + betweenMargin);
        mCanvas.drawBitmap(mBottom, null, mRectF, null);

        mCanvas.restore();
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * 判断是否相撞
     *
     * @param mBird
     * @return
     */
    public boolean touchBird(Bird mBird)
    {
        // 在上下管道之间
        // y在上管道高度范围内
        // y在下管道范围内
        if (mBird.getX() + mBird.getBirdWidth() > x
                &&
                (mBird.getY() < topHeight
                        || mBird.getY() + mBird.getBirdHeight() > topHeight + betweenMargin))
        {
            return true;
        }

        return false;
    }
}
