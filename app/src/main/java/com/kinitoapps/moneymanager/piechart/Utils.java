package com.kinitoapps.moneymanager.piechart;

import android.graphics.Color;

/**
 * Created by HP INDIA on 03-Jan-18.
 */

public class Utils {

    public static int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }
}