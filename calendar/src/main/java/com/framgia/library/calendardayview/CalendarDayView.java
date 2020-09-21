package com.framgia.library.calendardayview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import androidx.annotation.NonNull;
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

    private int borderWidth = 10;

    private LinearLayout mLayoutDayView;

    private FrameLayout mLayoutEvent;

    private FrameLayout mLayoutPopup;

    private CdvDecoration mDecoration;

    private List<? extends IEvent> mEvents;

    private List<? extends IEvent> currentTimeEvents;

    private List<? extends IPopup> mPopups;

    private ArrayList<Rect> rectArrayList = new ArrayList<Rect>();

    private ArrayList<Rect> borderArrayList = new ArrayList<Rect>();

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
        rectArrayList = new ArrayList<>();
        currentTimeEvents = new ArrayList<>();
        mDecoration = new CdvDecorationDefault(getContext());

        Log.d("FOREVENT", "day view init");

        refresh();
    }

    public void refresh() {
        drawDayViews();

        drawEvents();

        drawPopups();

        drawCurrentTimeIndicator();
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
        ArrayList<Rect> ToBeModifiedRectArrayList = new ArrayList<Rect>();

        for (IEvent event : mEvents) {
            ToBeModifiedRectArrayList.add(getTimeBoundEvent(event));
        }

//            Rect borderRect = getBorderRect(event);

//            IEvent newBorderEvent = event;
//            newBorderEvent.setColorToBorderColor();

        ArrayList<Rect> modifiedRectArrayList = new ArrayList<>();
        modifiedRectArrayList = useAllWidth(ToBeModifiedRectArrayList);
            // add event view

//        Log.d("SIZEARRAY", modifiedRectArrayList.size() + " size of array");
//        Log.d("MODRECT", modifiedRectArrayList.toString());
        Log.d("MODRECT","before mod : " + ToBeModifiedRectArrayList.toString());
        Log.d("MODRECT", eventWidth + " eventWidth");

        Log.d("FOREVENT", modifiedRectArrayList.toString());

        Log.d("FOREVENT", modifiedRectArrayList.size() + " size of modified array");

        for (int i = 0; i < modifiedRectArrayList.size(); i++) {
            Log.d("MODRECT", "Rect " + i + " : " + ToBeModifiedRectArrayList.get(i).left + " left");
            Log.d("MODRECT", "Rect " + i + " : " + ToBeModifiedRectArrayList.get(i).right + " right");

            int dynamicEventWidth = modifiedRectArrayList.get(i).right - modifiedRectArrayList.get(i).left;

//            EventView eventView =
//                    getDecoration().getEventView(mEvents.get(i), ToBeModifiedRectArrayList.get(i), mTimeHeight, mSeparateHourHeight, eventWidth);

            EventView eventView =
                    getDecoration().getEventView(mEvents.get(i), modifiedRectArrayList.get(i), mTimeHeight, mSeparateHourHeight, dynamicEventWidth);

            Log.d("MODRECT", "Rect " + i + " : " + ToBeModifiedRectArrayList.get(i).right + " right");
//            EventView borderView =
//                    getDecoration().getEventView(newBorderEvent, borderRect, mTimeHeight, mSeparateHourHeight, eventWidth);
            if (eventView != null) {
                mLayoutEvent.addView(eventView, eventView.getLayoutParams());
//                mLayoutEvent.addView(borderView, eventView.getLayoutParams());
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

    private Rect getTimeBoundEvent(IEvent event) {
        Log.d("RECT1", numberOfColumns + " no of columns");

        eventWidth = ((getWidth() - (mHourWidth + mEventMarginLeft)) / numberOfColumns);

        Log.d("modified", eventWidth + " event width");

        Rect rect = new Rect();
        rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
        rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
        rect.left = mHourWidth + mEventMarginLeft;
        rect.right = rect.left + eventWidth ;

        Log.d("modified", rect + " OG rect");

        if (rectArrayList.size() == 0) {
            rectArrayList.add(rect);
            return rect;
        } else {
            Rect modifiedRect = placingEvent(rect, rectArrayList);
            Log.d("modified", modifiedRect + " modified rect");
            rectArrayList.add(modifiedRect);
            return modifiedRect;
        }

    }

    private Rect getBorderRect(IEvent event) {
//        eventWidth = ((getWidth() - (mHourWidth + mEventMarginLeft))/numberOfColumns) - borderWidth;

        Rect rect = new Rect();
        rect.top = getPositionOfTime(event.getStartTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
        rect.bottom = getPositionOfTime(event.getEndTime()) + mTimeHeight / 2 + mSeparateHourHeight + mVerticalBorderHeight;
        rect.left = mHourWidth + mEventMarginLeft;
        rect.right = mHourWidth + mEventMarginLeft + borderWidth;

        if (borderArrayList.size() == 0) {
            borderArrayList.add(rect);
            return rect;
        } else {
            Rect modifiedRect = placingEvent(rect, borderArrayList);
            Log.d("modified", modifiedRect + " modified rect");
            borderArrayList.add(modifiedRect);
            return modifiedRect;
        }

    }

    public Rect placingEvent(Rect rect, ArrayList<Rect> rectArrayList) {
        int moveCount = 0;
        for (Rect currentRect : rectArrayList) {
            //if moveCount equals the maximum 'moves' it should make, return rect.
            if (moveCount == numberOfColumns - 1) {
                return rect;
            }
            if ((rect.left == currentRect.left && currentRect.right == rect.right) && (rect.top < currentRect.bottom && currentRect.top < rect.bottom)) {
                moveCount++;
                rect.left = rect.left + eventWidth;
                rect.right = rect.right + eventWidth;
            }
        }
        return rect;
    }

    //condition to check if rect.right is overlapping with right limit
    public ArrayList<Rect> useAllWidth(ArrayList<Rect> arrangedRectList) {

        if (arrangedRectList.size() == 1 ) {
            arrangedRectList.get(0).right = getWidth();
            return arrangedRectList;
        }

        ArrayList<Rect> modifiedRectList = arrangedRectList;
        ArrayList<Rect> currentRectList = arrangedRectList;

        int totalLeftMargin =  mHourWidth + mEventMarginLeft;

    for(int i = 0; i < currentRectList.size(); i++) {
        int maxExtension = 0;
        int collisionCount = 0;
        for (int j = 0; j < modifiedRectList.size(); j++)
            {
                //check if it is compared to the same rect
                if (currentRectList.get(i).contains(modifiedRectList.get(j))) {
                    Log.d("SAMERECT", "same rect");
                    continue;
                }

                //breaks loop when rect is not supposed to be extended
                    if (currentRectList.get(i).left == totalLeftMargin && currentRectList.get(i).right == modifiedRectList.get(j).left
//                            ||
//                            currentRectList.get(i).top <= modifiedRectList.get(j).bottom && modifiedRectList.get(j).top <= currentRectList.get(i).bottom && currentRectList.get(i).right ==  modifiedRectList.get(i).left
                            ||
                            currentRectList.get(i).right + eventWidth > getWidth()
                            ) {
                        Log.d("SAMERECT", "break within condition");
                        maxExtension = currentRectList.get(i).right;
//                        break;
                            }


                    if (currentRectList.get(i).right >= modifiedRectList.get(j).left) {
                        if (currentRectList.get(i).top <= modifiedRectList.get(j).bottom && modifiedRectList.get(j).top <= currentRectList.get(i).bottom) {
                            if (currentRectList.get(i).right == modifiedRectList.get(j).left) {
                                maxExtension = currentRectList.get(i).right;
                                break;
                            } else {
                                maxExtension = getWidth();
                            }
                    } else {
                        maxExtension = getWidth();
                    }
                } else {
                        if (currentRectList.get(i).top <= modifiedRectList.get(j).bottom && modifiedRectList.get(j).top <= currentRectList.get(i).bottom) {
                            maxExtension = modifiedRectList.get(j).left;
                        } else {
                            maxExtension = getWidth();
                            break;
                        }

                }

            }
//            if (maxExtension > 0 && collisionCount == 0) {
//                if (maxExtension == getWidth()) {
//                    currentRectList.get(i).right = getWidth();
//                } else {
                    currentRectList.get(i).right = maxExtension;
//                }
//            }


    }

    return currentRectList;
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
