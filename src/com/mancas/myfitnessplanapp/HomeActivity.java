package com.mancas.myfitnessplanapp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.mancas.customviews.StepEditText;
import com.mancas.steps.Step;

public class HomeActivity extends Activity implements OnClickListener
{
    public static final String STEPS = "STEPS";
    public static final String REPETITIONS = "REPETITIONS";
    private final ArrayList<Step> mSteps = new ArrayList<Step>();
    private Button mStart;
    private int mRepetitions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mStart = (Button) findViewById(R.id.start_plan);
        mStart.setOnClickListener(this);
    }

    public void getSteps()
    {
        StepEditText mEditText = (StepEditText) findViewById(R.id.run1);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.walk1);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.run2);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.walk2);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.run3);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.walk3);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.run4);
        addStep(mEditText);
        mEditText = (StepEditText) findViewById(R.id.walk4);
        addStep(mEditText);

        final EditText mRepetitionField = (EditText) findViewById(R.id.repetition);
        mRepetitions = mRepetitionField.getText().toString().isEmpty() ? 1
                : Integer.parseInt(mRepetitionField.getText().toString());
    }

    private void addStep(StepEditText editText)
    {
        double time = editText.getText().toString().isEmpty() ? 0.0 : Double
                .parseDouble(editText.getText().toString());
        mSteps.add(new Step(editText.getType(), time));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
        case R.id.start_plan:
            getSteps();
            for (Step s : mSteps) {
                Log.d("HOME", "type ->" + s.getType());
                Log.d("HOME", "time ->" + s.getTime());
            }
            if (!planHasErrors()) {
                Intent mIntent = new Intent(HomeActivity.this,
                        ChronoActivity.class);
                Bundle mBundle = new Bundle();
                mBundle.putParcelableArrayList(STEPS, mSteps);
                mBundle.putInt(REPETITIONS, mRepetitions);
                mIntent.putExtras(mBundle);
                startActivity(mIntent);
            } else {
                if (mSteps.size() > 0) {
                    StepEditText mEditText = (StepEditText) findViewById(R.id.run1);
                    mEditText.setError(getResources().getString(
                            R.string.plan_error));
                }
            }
            break;
        }
    }

    private boolean planHasErrors()
    {
        double mTotalTime = 0.0;
        for (Step s : mSteps) {
            mTotalTime += s.getTime();
        }
        return mTotalTime > 0.0 ? false : true;
    }
}
