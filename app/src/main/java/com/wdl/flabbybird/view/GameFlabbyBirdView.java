package com.wdl.flabbybird.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.wdl.flabbybird.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Create by: wdl at 2019/11/25 10:08
 */
public class GameFlabbyBirdView extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
    /**
     * 游戏状态
     */
    private enum Status
    {
        RUNNING,
        WAITING,
        STOP
    }

    private Status status = Status.WAITING;

    private static final int TOUCH_UP_DIS = -16;

    /**
     * 每次点击上升的距离
     */
    private final int mBirdUpDistance = dp2px(TOUCH_UP_DIS);
    /**
     * 下路距离
     */
    private final int mBirdDownDistance = dp2px(2);

    private int mTmpDis;

    /**
     * 线程开关
     */
    private boolean isRunning;
    /**
     * holder
     */
    private SurfaceHolder mHolder;
    /**
     * 绘制类-与Holder相绑定
     */
    private Canvas mCanvas;

    /**
     * 绘制线程
     */
    private Thread mThread;

    /**
     * VIEW宽
     */
    private int mPanelWidth;
    /**
     * VIEW高
     */
    private int mPanelHeight;
    /**
     * 绘制范围
     */
    private RectF mPanelRectF = new RectF();

    /**
     * 背景图
     */
    private Bitmap mBgMap;

    /**
     * 鸟
     */
    private Bird mBird;
    private Bitmap mBirdMap;

    /**
     * 地板
     */
    private Paint mPaint;
    private Floor mFloor;
    private Bitmap mFloorBg;

    private int mSpeedX;

    /**
     * 管道相关
     */
    private int mPipeWidth;
    private static final int PIPE_WIDTH = 70;
    private static final int PIPE_BETWEEN = 200;
    /**
     * 每隔300dp生成一个管道
     */
    private final int mPipeBetween = dp2px(PIPE_BETWEEN);
    /**
     * 记录移动的距离
     */
    private int mMoveDis = 0;
    private Bitmap mPipeTop;
    private Bitmap mPipeBottom;
    private RectF mPipeRectF;

    private List<Pipe> mPipes = new ArrayList<>();


    /**
     * 分数
     */
    private final int[] nums = new int[]{
            R.drawable.n0, R.drawable.n1, R.drawable.n2,
            R.drawable.n3, R.drawable.n4, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8,
            R.drawable.n9
    };
    private List<Bitmap> mScore;
    private int mInitScore = 0;

    private int mRemovedPipe = 0;
    /**
     * 单个数字的高度
     */
    private static final float SINGLE_NUM_HEIGHT = 1 / 15F;
    /**
     * 单个数字的高、度度
     */
    private int mNumHeight;
    private int mNumWidth;
    private RectF mNumRectF;


    public GameFlabbyBirdView(Context context)
    {
        this(context, null);
    }

    public GameFlabbyBirdView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
        initBitmaps();
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mSpeedX = dp2px(2);

    }

    /**
     * 初始化相关图片资源
     */
    private void initBitmaps()
    {
        mFloorBg = loadRes(R.drawable.floor_bg2);
        mBirdMap = loadRes(R.drawable.bird);
        mBgMap = loadRes(R.drawable.bg_panel);
        mPipeTop = loadRes(R.drawable.bg_pipe_top);
        mPipeBottom = loadRes(R.drawable.bg_pipe_bottom);

        mScore = new ArrayList<>(nums.length);
        for (int num : nums)
        {
            mScore.add(loadRes(num));
        }

    }


    /**
     * 初始化SurfaceView相关
     */
    private void init()
    {
        mHolder = getHolder();
        // 设置回调
        mHolder.addCallback(this);
        setZOrderOnTop(true);
        // 设置画布背景透明
        mHolder.setFormat(PixelFormat.TRANSLUCENT);
        // 获取焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 屏幕常亮
        setKeepScreenOn(true);
    }


    @Override
    public void run()
    {
        // 循环绘制
        while (isRunning)
        {
            final long startTime = System.currentTimeMillis();
            logic();
            draw();
            final long endTime = System.currentTimeMillis();

            // 判断绘制时间间隔
            try
            {
                if (endTime - startTime < 50)
                {
                    Thread.sleep(50 - (endTime - startTime));
                }

            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            switch (status)
            {
                case WAITING:
                    status = Status.RUNNING;
                    break;
                case RUNNING:
                    mTmpDis = mBirdUpDistance;
                    break;
            }
        }
        return true;
    }

    private void logic()
    {
        switch (status)
        {
            case RUNNING:

                mInitScore = 0;

                // 管道动态添加、移除
                logicPipe();
                // 小鸟移动
                logicBird();

                mInitScore += mRemovedPipe;
                for (Pipe pipe : mPipes)
                {
                    if (pipe.getX() + mPipeWidth < mBird.getX())
                    {
                        mInitScore++;
                    }
                }

                // 判断游戏是否结束
                checkOver();

                // 地板移动
                mFloor.setX(mFloor.getX() - mSpeedX);
                break;
            case STOP:
                // 鸟未落地 -> 落地
                if (mBird.getY() < mFloor.getY() - mBird.getBirdHeight())
                {
                    logicBird();
                } else
                {
                    mInitScore = 0;
                    mRemovedPipe = 0;
                    // 落地 -> 切换状态,重置pos
                    // 初始状态
                    status = Status.WAITING;
                    //初始位置
                    initPos();
                }
                break;
            default:
                break;
        }
    }

    private void initPos()
    {
        mPipes.clear();
        mTmpDis = 0;
        mBird.setY(mPanelHeight * 2 / 3);
    }

    /**
     * 循环遍历管道 - 判断游戏是否结束
     */
    private void checkOver()
    {
        // 碰到地板
        if (mBird.getY() > mFloor.getY() - mBird.getBirdHeight())
        {
            status = Status.STOP;
            return;
        }

        for (Pipe mPipe : mPipes)
        {
            // 穿过管道
            if (mPipe.getX() + mPipeWidth < mBird.getX())
            {
                continue;
            }

            if (mPipe.touchBird(mBird))
            {
                status = Status.STOP;
                break;
            }

        }
    }

    private void logicBird()
    {
        // 每次下落 重新设置高度
        mTmpDis += mBirdDownDistance;
        mBird.setY(mTmpDis + mBird.getY());
    }

    private void logicPipe()
    {
        // 超过屏幕 移除
        Iterator<Pipe> pipes = mPipes.iterator();
        while (pipes.hasNext())
        {
            Pipe mPipe = pipes.next();
            if (mPipe.getX() < -mPipeWidth)
            {
                pipes.remove();
                mRemovedPipe++;
            }
        }

        // 超过300dp动态的添加管道
        mMoveDis += mSpeedX;
        if (mMoveDis >= mPipeBetween)
        {
            Pipe mPipe = new Pipe(getWidth(), getHeight(), mPipeTop, mPipeBottom);

            mPipes.add(mPipe);
            mMoveDis = 0;
        }
        for (Pipe mPipe : mPipes)
        {
            // 管道移动
            mPipe.setX(mPipe.getX() - mSpeedX);
        }
    }

    /**
     * 绘制流程
     */
    private void draw()
    {
        try
        {
            // 获取canvas
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null)
            {
                // 背景
                drawPanelBg();
                // 小鸟
                drawBird();
                // 底部地板
                drawFloor();
                // 管道
                drawPipes();
                // 分数
                drawScore();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            if (mCanvas != null)
            {
                mHolder.unlockCanvasAndPost(mCanvas);
            }

        }
    }

    private void drawScore()
    {
        // 100
        String score = String.valueOf(mInitScore);
        mCanvas.save();
        mCanvas.translate(mPanelWidth / 2 - score.length() * mNumWidth / 2, mPanelHeight * 1.0F / 8);
        for (int i = 0; i < score.length(); i++)
        {
            int num = Integer.valueOf(score.substring(i, i + 1));
            mCanvas.drawBitmap(mScore.get(num), null, mNumRectF, null);
            mCanvas.translate(mNumWidth, 0);
        }
        mCanvas.restore();
    }

    private void drawPipes()
    {
        for (Pipe pipe : mPipes)
        {
            // pipe.setX(pipe.getX() - mSpeedX);
            pipe.draw(mCanvas, mPipeRectF);
        }
    }

    private void drawFloor()
    {
        mFloor.draw(mCanvas, mPaint);
    }

    private void drawBird()
    {
        mBird.draw(mCanvas);
    }

    private void drawPanelBg()
    {
        mCanvas.drawBitmap(mBgMap, null, mPanelRectF, null);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // 开启进程
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // 结束
        isRunning = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        this.mPanelWidth = w;
        this.mPanelHeight = h;
        this.mPanelRectF.set(0, 0, w, h);

        mBird = new Bird(getContext(), w, h, mBirdMap);
        mFloor = new Floor(w, h, mFloorBg);

        // pipe
        mPipeWidth = dp2px(PIPE_WIDTH);
        mPipeRectF = new RectF(0, 0, mPipeWidth, h);

        mPipes.add(new Pipe(w,h,mPipeTop,mPipeBottom));

        // 分数
        mNumHeight = (int) (h * SINGLE_NUM_HEIGHT);
        mNumWidth = (int) (mNumHeight * 1.0f / mScore.get(0).getHeight() * mScore.get(0).getWidth());
        mNumRectF = new RectF(0, 0, mNumWidth, mNumHeight);
    }

    private int dp2px(int dp)
    {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics()) + 0.5f);
    }

    private Bitmap loadRes(int resId)
    {
        return BitmapFactory.decodeResource(getResources(), resId);
    }
}
