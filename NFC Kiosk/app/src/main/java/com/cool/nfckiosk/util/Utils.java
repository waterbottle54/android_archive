package com.cool.nfckiosk.util;

import android.content.res.Resources;
import android.util.TypedValue;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class Utils {

    public static String getEmailFromUserId(String userId) {
        return String.format(Locale.getDefault(), "%s@nfckiosk.com", userId);
    }

    public static float dpToPixels(Resources resources, float dp) {
        return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.getDisplayMetrics());
    }

    public static int getRecyclerSpanCount(Resources resources, RecyclerView recyclerView, float itemWidthInDp) {
        return (int)(recyclerView.getWidth() / dpToPixels(resources, itemWidthInDp));
    }

}
