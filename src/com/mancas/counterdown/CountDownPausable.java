package com.mancas.counterdown;

import android.os.CountDownTimer;

import com.mancas.listeners.CountDownListener;

public class CountDownPausable
{
    private final long mTotalTime;
    private long mMillisRemaining;
    private final long mCountDownInterval;

    private CountDownTimer mCountDown;
    private CountDownListener mListener;

    private boolean isPaused = true;

    public CountDownPausable(long totalTime, long interval)
    {
        mTotalTime = mMillisRemaining = totalTime;
        mCountDownInterval = interval;
    }

    public CountDownPausable(long totalTime, long interval,
            CountDownListener listener)
    {
        mTotalTime = mMillisRemaining = totalTime;
        mCountDownInterval = interval;
        mListener = listener;
    }

    public synchronized final void start()
    {
        if (isPaused) {
            createCountDown();
            mCountDown.start();
            isPaused = false;
        }
    }

    public void pause()
    {
        if (!isPaused) {
            isPaused = true;
            mCountDown.cancel();
        }
    }

    public void stop()
    {
        mCountDown.cancel();
    }

    public boolean isPaused()
    {
        return isPaused;
    }

    private void createCountDown()
    {
        mCountDown = new CountDownTimer(mMillisRemaining, mCountDownInterval) {

            @Override
            public void onTick(long millisUntilFinished)
            {
                mMillisRemaining = millisUntilFinished;
                if (mListener != null) {
                    mListener.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish()
            {
                if (mListener != null) {
                    mListener.onFinish();
                }
            }
        };
    }
}
