package com.mancas.listeners;

public interface CountDownListener
{
    public void onFinish();

    public void onPaused();

    public void onResumed();

    public void onTick(long millisUntilFinished);
}
