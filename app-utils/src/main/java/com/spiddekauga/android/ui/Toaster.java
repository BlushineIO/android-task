package com.spiddekauga.android.ui;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

/**
 * Creates toasts for the UI
 */
public class Toaster {
private static final Duration DURATION_DEFAULT = Duration.SHORT;

/**
 * Display text toast message for a {@link Duration.SHORT} amount of time
 * @param message the message to display
 */
public static void show(String message) {
	show(message, DURATION_DEFAULT);
}

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
	Activity activity = AppActivity.getActivity();
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
 * Display a toast message with the specified icon for a short amount of time
 * @param message the message to display
 * @parma icon what icon to use in from of the message. Set to {@link Icons.TEXT} to only display text
 */
public static void show(String message, Icons icon) {
	show(message, DURATION_DEFAULT, icon);
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
