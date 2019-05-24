package com.ebanswers.cmdlib.timer;

import android.util.Log;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @Author Snail
 * Date 2019/5/22
 * Email yuesnail@gmail.com
 */
public class TimerTask {
    private final static String TAG = "TimerTask";
    /**
     * 总时间，秒为单位
     */
    private long mTotalTime;
    /**
     * 时间间隔
     */
    private long mDuring;
    /**
     * 类型
     */
    private String mType;
    private volatile Future timerFuture;
    private volatile Runnable timerRunnable;
    private volatile TimerTaskListener mTimerTaskListener;
    private volatile boolean isRun = false;
    private volatile boolean isStop = true;

    public TimerTask(String mType, long mDuring, TimerTaskListener mTimerTaskListener) {
        this.mType = mType;
        this.mDuring = mDuring;
        this.mTimerTaskListener = mTimerTaskListener;
    }

    /**
     * 开始计时
     */
    public synchronized void start(int totalTime) {
        Log.d(TAG, "start: ");
        this.mTotalTime = totalTime;
        if (isStop) {
            isStop = false;
            resume();
        }
    }

    /**
     * 结束倒计时
     */
    public synchronized void stop() {
        Log.d(TAG, "stop: ");
        if (!isStop) {
            isRun = false;
            mTotalTime = 0;
            isStop = true;
            if (null != timerFuture) {
                timerFuture.cancel(true);
                timerFuture = null;
            }
            if (null != mTimerTaskListener) {
                mTimerTaskListener.onFinish(mType);
            }
        }
    }

    /**
     * 暂停倒计时
     */
    public synchronized void pause() {
        Log.d(TAG, "pause: ");
        if (isRun) {
            isRun = false;
            if (null != timerFuture) {
                timerFuture.cancel(true);
                timerFuture = null;
            }
            if (null != mTimerTaskListener) {
                mTimerTaskListener.onPause(mTotalTime, mType);
            }
        }
    }

    /**
     * 唤醒倒计时
     */
    public synchronized void resume() {
        Log.d(TAG, "resume: ");
        if (!isRun) {
            isRun = true;
            timerWork();
        }
    }


    /**
     * 使用线程池实现计时器
     */
    public void timerWork() {
        if (null != timerFuture) {
            timerFuture.cancel(true);
            timerFuture = null;
        }
        timerFuture = Executors.newScheduledThreadPool(1)
                .scheduleAtFixedRate(timerRunnable, 0, 1, TimeUnit.SECONDS);

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (mTotalTime == 0) {
                    Log.d(TAG, "run: time = " + mTotalTime);
                    stop();
                    return;
                }
                if (mTotalTime > 0) {
                    mTotalTime--;
                    Log.d(TAG, "run: time = " + mTotalTime + ",  type = " + mType);
                }
                if (mTimerTaskListener != null) {
                    mTimerTaskListener.onTick(mTotalTime, mType);
                }
            }
        };
        if (mTimerTaskListener != null) {
            mTimerTaskListener.onStart(mTotalTime, mType);
        }
    }

    /**
     * 是否在运行
     *
     * @return
     */
    public boolean isRun() {
        return isRun;
    }

    /**
     * 是否处于暂停状态
     * @return
     */
    public boolean isPause() {
        return !isRun && !isStop;
    }

    /**
     * 是否计时完成
     * @return
     */
    public boolean isFinish() {
        return isStop;
    }

    public interface TimerTaskListener {
        void onStart(long totalTime, String type);

        void onTick(long countdownTime, String type);

        void onFinish(String type);

        void onPause(long countdownTime, String type);
    }
}


