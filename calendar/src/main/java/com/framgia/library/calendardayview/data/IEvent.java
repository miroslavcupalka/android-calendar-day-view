package com.framgia.library.calendardayview.data;

/**
 * Created by FRAMGIA\pham.van.khac on 11/07/2016.
 */
public interface IEvent extends ITimeDuration {

    String getName();

    int getID();

    Integer getServiceMatchId();

    Integer getRequestSessionId();

    int getColor();

    int getBorderColor();

    void setColorToBorderColor();
}
