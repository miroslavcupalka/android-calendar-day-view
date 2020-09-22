package com.framgia.sample.calendardayview;

import androidx.annotation.DrawableRes;

import com.framgia.library.calendardayview.data.IEvent;
import java.util.Calendar;

/**
 * Created by FRAMGIA\pham.van.khac on 07/07/2016.
 */
public class Event implements IEvent {

    private long mId;
    private Calendar mStartTime;
    private Calendar mEndTime;
    private String mName;
    private String mLocation;
    private int mBackground;

    public Event() {

    }

    public Event(long mId, Calendar mStartTime, Calendar mEndTime, String mName, String mLocation,
            int mBackground) {
        this.mId = mId;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mName = mName;
        this.mLocation = mLocation;
        this.mBackground = mBackground;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public Calendar getStartTime() {
        return mStartTime;
    }

    public void setStartTime(Calendar startTime) {
        this.mStartTime = startTime;
    }

    public Calendar getEndTime() {
        return mEndTime;
    }

    public void setEndTime(Calendar endTime) {
        this.mEndTime = endTime;
    }

    public String getText() {
        return mName;
    }

    @Override
    public int getTextColor() {
        return android.R.color.black;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        this.mLocation = location;
    }

    public int getBackground() {
        return mBackground;
    }

    public void setBackground(@DrawableRes int background) {
        this.mBackground = background;
    }
}
