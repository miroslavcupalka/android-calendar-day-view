package com.framgia.library.calendardayview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.framgia.library.calendardayview.data.IEvent;
import com.framgia.library.calendardayview.data.IPopup;
import com.framgia.library.calendardayview.data.ITimeDuration;
import com.framgia.library.calendardayview.decoration.CdvDecoration;
import com.framgia.library.calendardayview.decoration.CdvDecorationDefault;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by FRAMGIA\pham.van.khac on 07/07/2016.
 */
public class CalendarDayView extends FrameLayout {

    private int mDayHeight = 0;

    private int mEventMarginLeft = 0;

    private int mHourWidth = 120;

    private int mTimeHeight = 120;

    private int mSeparateHourHeight = 0;

    private int mVerticalBorderHeight = 76;

    private int mStartHour = 0;

    private int mEndHour = 24;

    private int numberOfColumns = 1;

    private int eventWidth = 0;

    private LinearLayout mLayoutDayView;

    private FrameLayout mLayoutEvent;

    private FrameLayout mLayoutPopup;

    private CdvDecoration mDecoration;

    private List<? extends IEvent> mEvents;

    private List<? extends IEvent> currentTimeEvents;

    private List<? extends IPopup> mPopups;

    private ArrayList<Rect> rectArrayList = new ArrayList<Rect>();

    private int currentTimeIndicatorPosition;

    private Calendar currentTime;

    public CalendarDayView(Context context) {
        super(context);
        init(null);
    }

    public CalendarDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CalendarDayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.view_day_calendar, this, true);

        mLayoutDayView = (LinearLayout) findViewById(R.id.dayview_container);
        mLayoutEvent = (FrameLayout) findViewById(R.id.event_container);
        mLayoutPopup = (FrameLayout) findViewById(R.id.popup_container);

        mDayHeight = getResources().getDimensionPixelSize(R.dimen.dayHeight);

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarDayView);
            try {
                mEventMarginLeft =
                    a.getDimensionPixelSize(R.styleable.CalendarDayView_eventMarginLeft,
                        mEventMarginLeft);
                mDayHeight =
                    a.getDimensionPixelSize(R.styleable.CalendarDayView_dayHeight, mDayHeight);
                mStartHour = a.getInt(R.styleable.CalendarDayView_startHour, mStartHour);
                mEndHour = a.getInt(R.styleable.CalendarDayView_endHour, mEndHour);
            } finally {
                a.recycle();
            }
        }

        mEvents = new ArrayList<>();
        mPopups = new ArrayList<>();
        currentTimeEvents = new ArrayList<>();
        mDecoration = new CdvDecorationDefault(getContext());

        refresh();
    }

    public void refresh() {
        drawDayViews();

        drawEvents();

        drawPopups();

    }

    private void drawDayViews() {
        mLayoutDayView.removeAllViews();
        DayView dayView = null;
        for (int i = mStartHour; i <= mEndHour; i++) {
            dayView = getDecoration().getDayView(i);
            mLayoutDayView.addView(dayView);
        }
        mHourWidth = (int) dayView.getHourTextWidth();
        mTimeHeight = (int) dayView.getHourTextHeight();
        mSeparateHourHeight = (int) dayView.getSeparateHeight();


    }

    private void drawEvents() {
        mLayoutEvent.removeAllViews();

        drawCurrentTimeIndicator();

        for (IEvent event : mEvents) {
        //Rect rect = getTimeBound(event, iTimeDurationList)
            Rect rect = getTimeBoundEvent(event);

            Log.d("RECT1", rect.flattenToString());

            // add event view
            EventView eventView =
                getDecoration().getEventView(event, rect, mTimeHeight, mSeparateHourHeight, eventWidth);
            if (eventView != null) {
                mLayoutEvent.addView(eventView, eventView.getLayoutParams());
            }
        }
    }

    private void drawPopups() {
        mLayoutPopup.removeAllViews();

        for (IPopup popup : mPopups) {
            Rect rect = getTimeBound(popup);

            // add popup views
            PopupView view =
                getDecoration().getPopupView(popup, rect, mTimeHeight, mSeparateHourHeight);
            if (popup != null) {
                mLayoutPopup.addView(view, view.getLayoutParams());
            }
        }
    }

    private void drawCurrentTimeIndicator() {

        Log.d("EVENTS", "Size of currentTimeEvents : " + currentTimeEvents.size());
        if (currentTimeEvents.size() == 1) {
            Rect rect = getTimeBound(currentTimeEvents.get(0));

            currentTimeIndicatorPosition = rect.top;

            Log.d("CALENDARSCROLL", currentTimeIndicatorPosition + " in initialization");

            rect.left = rect.left - 10;

        }

    }


    private Rect getTimeBound(ITimeDuration event) {
        Rect rect = new Rect();
            rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
            rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
            rect.left = mHourWidth + mEventMarginLeft;
            rect.right = getWidth();
            return rect;
    }

    private Rect getTimeBoundEvent(ITimeDuration event) {
        Log.d("RECT1", numberOfColumns + " no of columns");

        eventWidth = ((getWidth() - (mHourWidth + mEventMarginLeft))/numberOfColumns);

        Rect rect = new Rect();
        rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
        rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
        rect.left = mHourWidth + mEventMarginLeft;
        rect.right = (getWidth() - mHourWidth + mEventMarginLeft)/numberOfColumns;

        if (rectArrayList.size() == 0) {
            rectArrayList.add(rect);
            return rect;
        } else {
            Rect modifiedRect = placingEvent(rect, rectArrayList);
            Log.d("modified", modifiedRect + " modified rect");
            rectArrayList.add(modifiedRect);
            return modifiedRect;
        }

        return rect;
    }

    public Rect placingEvent (Rect rect, ArrayList<Rect> rectArrayList) {
        int moveCount = 0;
        for (Rect currentRect : rectArrayList) {
            //if moveCount equals the maximum 'moves' it should make, return rect.
                if (moveCount == numberOfColumns - 1 ) {
                    return rect;
                }
                if (rect.top < currentRect.bottom && currentRect.top < rect.bottom) {
                    moveCount++;
                    rect.left = rect.left + eventWidth;
                    rect.right = rect.right + eventWidth;
                }
                else {
                    return rect;
                }
            }
        return rect;
    }


    public int scrollToCurrentTime() {
        Log.d("CALENDARSCROLL", currentTimeIndicatorPosition + "");
//        mLayoutDayView.scrollBy(0, currentTimeIndicatorPosition - 200);
        return currentTimeIndicatorPosition -200;
    }

    public int getCurrentTime(){
        return currentTimeIndicatorPosition;
    }

    private int getPositionOfTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY) - mStartHour;
        int minute = calendar.get(Calendar.MINUTE);
        return hour * mDayHeight + minute * mDayHeight / 60;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
        refresh();
    }

    public void setEvents(List<? extends IEvent> events) {
        this.mEvents = events;
        refresh();
    }

    public void setPopups(List<? extends IPopup> popups) {
        this.mPopups = popups;
        refresh();
    }

    public void setLimitTime(int startHour, int endHour) {
        if (startHour >= endHour) {
            throw new IllegalArgumentException("start hour must before end hour");
        }
        mStartHour = startHour;
        mEndHour = endHour;
        refresh();
    }

    public void setCurrentTime(Calendar currentTime) {
        this.currentTime = currentTime;
        refresh();
    }

    public void setEventsCurrentTime(List<? extends IEvent> events) {
        this.currentTimeEvents = events;
        refresh();
    }
    /**
     * @param decorator decoration for draw event, popup, time
     */
    public void setDecorator(@NonNull CdvDecoration decorator) {
        this.mDecoration = decorator;
        refresh();
    }

    public int getEventLeftMargin(){
        return mEventMarginLeft + mHourWidth;
    }

    public CdvDecoration getDecoration() {
        return mDecoration;
    }
}
