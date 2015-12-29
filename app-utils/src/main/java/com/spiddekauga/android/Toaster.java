package com.spiddekauga.android;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Creates toasts for the UI
 */
public class Toaster {
/**
 * Display text toast message
 * @param message the message to display
 * @param duration how long the message should be displayed
 */
public static void show(String message, Duration duration) {
	show(message, duration, Icons.TEXT);
}

/**
 * Display a toast with the specified icon
 * @param message the message to display
 * @param duration how long the message should be displayed
 * @param icon what icon to use in front of the message. Set to {@link Icons.TEXT} to only display text
 */
public static void show(String message, Duration duration, Icons icon) {
	Activity activity = App.getActivity();
	LayoutInflater inflater = activity.getLayoutInflater();

	@IdRes int toastLayoutId = 0;
	@LayoutRes int toastLayout = 0;
	if (icon != null) {
		switch (icon) {
		case TEXT:
			toastLayoutId = R.id.toast_text_layout;
			toastLayout = R.layout.toast_text_layout;
			break;

		case WARNING:
			toastLayoutId = R.id.toast_warning_layout;
			toastLayout = R.layout.toast_warning_layout;
			break;
		}
	}


	View layout = inflater.inflate(toastLayout, (ViewGroup) activity.findViewById(toastLayoutId));

	TextView text = (TextView) layout.findViewById(R.id.toast_text);
	text.setText(message);

	Toast toast = new Toast(activity);

	switch (duration) {
	case LONG:
		toast.setDuration(Toast.LENGTH_LONG);
		break;

	case SHORT:
		toast.setDuration(Toast.LENGTH_SHORT);
		break;
	}

	toast.setView(layout);
	toast.show();
}

/**
 * The different toast icons
 */
public enum Icons {
	/** Display an warning icon in front of the text */
	WARNING,
	/** Only text */
	TEXT,
}

/**
 * Duration of the toasts
 */
public enum Duration {
	LONG,
	SHORT,
}
}
