package com.elrain.bashim.util;

import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by denys.husher on 26.11.2015.
 */
public final class ScreenUtil {
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
