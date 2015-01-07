package com.mancas.steps;

import android.os.Parcel;
import android.os.Parcelable;

public class Step implements Parcelable
{
    private final int mType;
    private final double mTime;

    public Step(int type, double time)
    {
        mType = type;
        mTime = time;
    }

    public double getTime()
    {
        return mTime;
    }

    public int getType()
    {
        return mType;
    }

    public String getTextToSpeech()
    {
        /*
         * Resources mResources = mContext.getResources(); switch (mType) { case
         * RUN_TYPE: return mResources.getString(R.string.run_text_to_speech);
         * case WALK_TYPE: return
         * mResources.getString(R.string.walk_text_to_speech); }
         */

        return "";
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeInt(mType);
        dest.writeDouble(mTime);
    }

    public static final Parcelable.Creator<Step> CREATOR = new Creator<Step>() {

        @Override
        public Step createFromParcel(Parcel source)
        {
            return new Step(source.readInt(), source.readDouble());
        }

        @Override
        public Step[] newArray(int size)
        {
            return new Step[size];
        }
    };
}
