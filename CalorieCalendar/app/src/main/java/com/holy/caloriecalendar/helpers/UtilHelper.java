package com.holy.caloriecalendar.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;


public class UtilHelper {

    public static Uri resourceToUri(Context context, int resID) {

        // 리소스 ID (ex. R.drawable.image) 를 Uri 값으로 변환한다
        Resources resources = context.getResources();
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resID))
                .appendPath(resources.getResourceTypeName(resID))
                .appendPath(resources.getResourceEntryName(resID))
                .build();

        // 소스코드 출처 :
        // https://stackoverflow.com/a/38340580
    }

}
