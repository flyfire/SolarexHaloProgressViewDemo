package com.solarexsoft.solarexhaloprogressview;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

/**
 * <pre>
 *    Author: houruhou
 *    CreatAt: 11:34/2019/2/12
 *    Desc:
 * </pre>
 */

public class Utils {
    public static int getColor(Context context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }
    public static int dp2px(Context context, float dp) {
        return (int)(context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }
}
