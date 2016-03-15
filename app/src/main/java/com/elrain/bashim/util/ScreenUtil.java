package com.elrain.bashim.util;

import android.content.Context;
import android.content.res.Configuration;

public final class ScreenUtil {
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
