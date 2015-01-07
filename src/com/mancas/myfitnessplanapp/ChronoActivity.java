package com.mancas.myfitnessplanapp;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mancas.counterdown.CountDownPausable;
import com.mancas.listeners.CountDownListener;
import com.mancas.steps.Step;

public class ChronoActivity extends Activity implements CountDownListener,
        OnClickListener, OnInitListener
{
    private ArrayList<Step> mSteps;
    private final ArrayList<Step> mValidSteps = new ArrayList<Step>();
    private int mCurrentStep = 0;
    private CountDownPausable mCounter;
    private int mRepetitions;
    private int mRepetitionsRemaining;
    private ImageButton mPlay;
    private ImageButton mStop;
    private ImageButton mPause;
    private TextView mChronoDisplay;
    private TextToSpeech mTTS;
    private ImageView mActivityType;

    private boolean isPaused = false;
    private static final long INTERVAL = 1000;
    private final int TTS_DATA_CHECK_CODE = 0;
    private final int DONE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);
        Intent mIntent = getIntent();
        mSteps = mIntent.getParcelableArrayListExtra(HomeActivity.STEPS);
        mRepetitions = mRepetitionsRemaining = mIntent.getIntExtra(
                HomeActivity.REPETITIONS, 1);

        validSteps();
        setupView();
        checkTTS();
    }

    private void checkTTS()
    {
        Intent mIntent = new Intent();
        mIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(mIntent, TTS_DATA_CHECK_CODE);
    }

    private void validSteps()
    {
        for (Step s : mSteps) {
            if (s.getTime() != 0) {
                mValidSteps.add(s);
            }
        }
    }

    private void setupView()
    {
        // Controls
        mPause = (ImageButton) findViewById(R.id.pause);
        mPlay = (ImageButton) findViewById(R.id.play);
        mStop = (ImageButton) findViewById(R.id.stop);
        mPause.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mStop.setOnClickListener(this);

        mChronoDisplay = (TextView) findViewById(R.id.chrono_display);
        mActivityType = (ImageView) findViewById(R.id.activity_img);
        setFirstStepTime();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == TTS_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTTS = new TextToSpeech(getApplicationContext(), this);
            } else {
                // Install TTS
                Intent mIntent = new Intent();
                mIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(mIntent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chrono, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinish()
    {
        if (mRepetitionsRemaining <= 0) {
            done();
            reset();
            return;
        }
        Log.d("CHRONO", "finish");
        nextStep();
    }

    @Override
    public void onPaused()
    {
    }

    @Override
    public void onResumed()
    {
    }

    @Override
    public void onTick(long millisUntilFinished)
    {
        setDisplay(millisUntilFinished);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
        case R.id.play:
            mPlay.setVisibility(View.GONE);
            mPause.setVisibility(View.VISIBLE);
            startPlan();
            break;
        case R.id.pause:
            mPlay.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.GONE);
            isPaused = true;
            mCounter.pause();
            break;
        case R.id.stop:
            mPlay.setVisibility(View.VISIBLE);
            mPause.setVisibility(View.GONE);
            if (mCounter != null) {
                mCounter.stop();
            }
            reset();
            break;
        }
    }

    private void startPlan()
    {
        if (isPaused) {
            mCounter.start();
            isPaused = false;
            return;
        }

        nextStep();
    }

    private void nextStep()
    {
        final Step mStep = mValidSteps.get(mCurrentStep);
        int type = mStep.getType();
        speach(type);
        setActivityImage(type);
        long mTotalTime = getTimeInMillis(mStep.getTime());
        setDisplay(mTotalTime);
        mCounter = new CountDownPausable(mTotalTime, INTERVAL, this);
        mCounter.start();

        // Prepare next step
        prepareStep();
    }

    private void prepareStep()
    {
        mCurrentStep++;
        if (mCurrentStep >= mValidSteps.size()) {
            mCurrentStep = 0;
            mRepetitionsRemaining--;
        }
    }

    private void setDisplay(long milliseconds)
    {
        mChronoDisplay.setText(""
                + DateUtils.formatElapsedTime(TimeUnit.MILLISECONDS
                        .toSeconds(milliseconds)));
    }

    private void setFirstStepTime()
    {
        setActivityImage(mValidSteps.get(0).getType());
        setDisplay(getTimeInMillis(mValidSteps.get(0).getTime()));
    }

    private void setActivityImage(int type)
    {
        switch (type) {
        case 0:
            mActivityType.setImageResource(R.drawable.runner);
            break;
        case 1:
            mActivityType.setImageResource(R.drawable.walker);
            break;
        }
    }

    private long getTimeInMillis(double time)
    {
        // Time is in minutes
        return (long) ((time * 60) * 1000);
    }

    private void done()
    {
        mChronoDisplay.setText("Finish! Well done!");
        mPlay.setVisibility(View.VISIBLE);
        mPause.setVisibility(View.GONE);
        speach(DONE);
    }

    private void reset()
    {
        mRepetitionsRemaining = mRepetitions;
        mCurrentStep = 0;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void speach(int type)
    {
        String textToSpeach;
        switch (type) {
        case 0:
            textToSpeach = getResources()
                    .getString(R.string.run_text_to_speech);
            break;
        case 1:
            textToSpeach = getResources().getString(
                    R.string.walk_text_to_speech);
            break;
        case 2:
            textToSpeach = getResources().getString(
                    R.string.done_text_to_speech);
            break;
        default:
            textToSpeach = "";
        }

        if (Build.VERSION.SDK_INT == 21) {
            mTTS.speak(textToSpeach, TextToSpeech.QUEUE_ADD, null, null);
            mTTS.speak(textToSpeach, TextToSpeech.QUEUE_ADD, null, null);
            mTTS.speak(textToSpeach, TextToSpeech.QUEUE_ADD, null, null);
        } else {
            mTTS.speak(textToSpeach, TextToSpeech.QUEUE_ADD, null);
            mTTS.speak(textToSpeach, TextToSpeech.QUEUE_ADD, null);
            mTTS.speak(textToSpeach, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    public void onInit(int status)
    {
        if (status == TextToSpeech.SUCCESS) {
            // mTTS.setLanguage(Locale.UK);
        }
    }
}
