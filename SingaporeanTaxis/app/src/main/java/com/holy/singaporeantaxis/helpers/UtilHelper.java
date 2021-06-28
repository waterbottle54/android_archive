package com.holy.singaporeantaxis.helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.holy.singaporeantaxis.R;

import java.util.Arrays;
import java.util.List;

public class UtilHelper {

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int resId) {

        Drawable vectorDrawable = ContextCompat.getDrawable(context, resId);
        if (vectorDrawable == null) {
            return null;
        }

        vectorDrawable.setBounds(
                0,
                0,
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());

        Bitmap bitmap = Bitmap.createBitmap(
                vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);

        // This source code from link below:

        // https://stackoverflow.com/a/45564994
    }

    public static Uri resourceToUri(Context context, int resID) {

        Resources resources = context.getResources();
        return new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(resID))
                .appendPath(resources.getResourceTypeName(resID))
                .appendPath(resources.getResourceEntryName(resID))
                .build();

        // This source code from link below:

        // https://stackoverflow.com/a/38340580
    }

    public static double fareInSingaporeanDollars(double distanceInMeters) {

        double fare = 3.90;
        double distanceUnder10Km = 0;
        double distanceOver10Km = 0;

        // Calculate fare per 400m above 1km-10km
        if (distanceInMeters > 1000) {
            if (distanceInMeters < 10000) {
                distanceUnder10Km = distanceInMeters - 1000;
            } else {
                distanceUnder10Km = 9000;
            }
        }

        while (distanceUnder10Km > 0) {
            fare += 0.22;
            distanceUnder10Km -= 400;
        }

        // Calculate fare per 400m above 10km
        if (distanceInMeters > 10000) {
            distanceOver10Km = distanceInMeters - 10000;
        }

        while (distanceOver10Km > 0) {
            fare += 0.22;
            distanceOver10Km -= 350;
        }

        return fare;
    }

    public static PolylineOptions getStandardPolylineOptions() {

        final PatternItem DOT = new Dot();
        final PatternItem GAP = new Gap(10);
        final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

        return new PolylineOptions()
                .color(0xFF3333AA)
                .startCap(new RoundCap())
                .endCap(new RoundCap())
                .pattern(PATTERN_POLYLINE_DOTTED);
    }

}
