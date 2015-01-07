package com.mancas.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;

import com.mancas.myfitnessplanapp.R;

public class StepEditText extends EditText
{
    private int mType;

    public StepEditText(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public StepEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    public StepEditText(Context context)
    {
        super(context);
    }

    private void init(Context context, AttributeSet attrs)
    {
        TypedArray mAttributes = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.StepEditText, 0, 0);
        mType = mAttributes.getInt(R.styleable.StepEditText_type, 0);
        mAttributes.recycle();
    }

    public int getType()
    {
        return mType;
    }

}
