package com.framgia.library.calendardayview.data;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

/**
 * Created by FRAMGIA\pham.van.khac on 11/07/2016.
 */
public interface IEvent extends ITimeDuration {

    String getText();

    @ColorRes
    int getTextColor();

    @DrawableRes
    int getBackground();
}
