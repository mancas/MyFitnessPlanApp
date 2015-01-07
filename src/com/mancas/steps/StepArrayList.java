package com.mancas.steps;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class StepArrayList extends ArrayList<Step> implements Parcelable
{
    private static final long serialVersionUID = 1L;
    private final ArrayList<Step> mSteps = new ArrayList<Step>();

    public StepArrayList()
    {
    }

    public static final Parcelable.Creator<StepArrayList> CREATOR = new Creator<StepArrayList>() {

        @Override
        public StepArrayList createFromParcel(Parcel source)
        {
            // Restore the parcel array
            StepArrayList mRestored = new StepArrayList();
            int size = source.readInt();

            for (int i = 0; i < size; i++) {
                Step s = new Step(source.readInt(), source.readDouble());
                mRestored.add(s);
            }

            return mRestored;
        }

        @Override
        public StepArrayList[] newArray(int size)
        {
            return new StepArrayList[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        // Write size first
        dest.writeInt(mSteps.size());

        for (Step s : mSteps) {
            dest.writeInt(s.getType());
            dest.writeDouble(s.getTime());
        }
    }

}
