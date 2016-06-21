package com.spiddekauga.android.ui;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatButton;
import android.widget.Button;

import com.spiddekauga.android.AppActivity;

/**
 * Helper methods for buttons
 */
public class ButtonHelper {
/**
 * Set the color of the button (only effects API v20 or before)
 * @param button the button to set the color of
 * @param color the color of the button
 */
@TargetApi(21)
@SuppressWarnings("deprecation")
public static void setColor(Button button, ButtonColors color) {
	if (Build.VERSION.SDK_INT <= 21) {
		ColorStateList colors = AppActivity.getActivity().getResources().getColorStateList(color.getColorResId());

		if (Build.VERSION.SDK_INT >= 20 && button instanceof AppCompatButton) {
			((AppCompatButton) button).setSupportBackgroundTintList(colors);
		} else {
			ViewCompat.setBackgroundTintList(button, colors);
		}
	}
}
}
