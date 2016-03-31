package com.spiddekauga.android;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.widget.Button;

/**
 * Helper methods for buttons
 */
public class ButtonHelper {
/**
 * Set the color of the button (only effects API v22 or older)
 * @param button the button to set the color of
 * @param color the color of the button
 */
@TargetApi(22)
@SuppressWarnings("deprecated")
public static void setColor(Button button, ButtonColors color) {
	if (Build.VERSION.SDK_INT <= 22) {
		ColorStateList colors = AppActivity.getActivity().getResources().getColorStateList(color.getColorResId());
		ViewCompat.setBackgroundTintList(button, colors);
	}
}
}
