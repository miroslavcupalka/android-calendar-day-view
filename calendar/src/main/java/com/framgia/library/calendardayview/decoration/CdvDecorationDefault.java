package com.framgia.library.calendardayview.decoration;

import android.content.Context;
import android.graphics.Rect;

import com.framgia.library.calendardayview.DayView;
import com.framgia.library.calendardayview.EventView;
import com.framgia.library.calendardayview.PopupView;
import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.data.IPopup;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FRAMGIA\pham.van.khac on 22/07/2016.
 */
public class CdvDecorationDefault implements CdvDecoration {

    protected Context mContext;

    protected EventView.OnEventClickListener mEventClickListener;

    protected EventView.OnEventSwipeListener mEventSwipeListener;

    protected PopupView.OnEventPopupClickListener mPopupClickListener;

    public CdvDecorationDefault(Context context) {
        this.mContext = context;
    }

    @Override
    public EventView getEventView(IEvent event, Rect eventBound, int hourHeight,
            int separateHeight) {
        EventView eventView = new EventView(mContext);
        eventView.setEvent(event);
        eventView.setPosition(eventBound, -hourHeight, hourHeight - separateHeight * 2);
        eventView.setOnEventClickListener(mEventClickListener);
        eventView.setOnEventSwipeListener(mEventSwipeListener);
        return eventView;
    }

    @Override
    public PopupView getPopupView(IPopup popup, Rect eventBound, int hourHeight, int seperateH) {
        PopupView view = new PopupView(mContext);
        view.setOnPopupClickListener(mPopupClickListener);
        view.setPopup(popup);
        view.setPosition(eventBound, -hourHeight / 4 + seperateH, hourHeight / 2 - seperateH * 2);
        return view;
    }

    @Override
    public EventView getCurrentTimeIndicator(Rect eventBound, int hourHeight, int seperateHeightTime, int eventWidth) {
        EventView eventView = new EventView(mContext);
        eventView.setPosition(eventBound, -hourHeight, hourHeight - seperateHeightTime * 2);
        eventView.setOnEventClickListener(mEventClickListener);
        eventView.setOnEventSwipeListener(mEventSwipeListener);
        return eventView;
    }

    @Override
    public DayView getDayView(int hour) {
        DayView dayView = new DayView(mContext);
        dayView.setText(timeToFormattedTime(hour));
        return dayView;
    }

    public void setOnEventClickListener(EventView.OnEventClickListener listener) {
        this.mEventClickListener = listener;
    }

    public void setOnEventSwipeListener(EventView.OnEventSwipeListener listener) {
        this.mEventSwipeListener = listener;
    }

    public void setOnPopupClickListener(PopupView.OnEventPopupClickListener listener) {
        this.mPopupClickListener = listener;
    }

    private String timeToFormattedTime (int hour) {
        String _24HourTime = String.format("%1$2s:00", hour);
        DateFormat timeFormat = new SimpleDateFormat(android.text.format.DateFormat.getBestDateTimePattern(Locale.getDefault(), "jj:mm"), Locale.getDefault());
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeFormat.format(_24HourDt);
    }
}
