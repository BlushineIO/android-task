package com.spiddekauga.android.ui;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.spiddekauga.android.AppActivity;

/**
 * Some helper methods for creating simple snackbars
 */
public class SnackbarHelper {
@Snackbar.Duration
private static final int DURATION_SHORT = Snackbar.LENGTH_SHORT;
@Snackbar.Duration
private static final int DURATION_MEDIUM = Snackbar.LENGTH_LONG;
private static final int LENGTH_MEDIUM_CHARACTERS = 0;
@Snackbar.Duration
private static final int DURATION_LONG = 6000;
private static final int LENGTH_LONG_CHARACTERS = 20;

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param stringId id of the message to show
 */
public static void showSnackbar(@StringRes int stringId) {
	showSnackbar(getString(stringId));
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param message the message to show
 */
public static void showSnackbar(String message) {
	showSnackbar(message, null, null);
}

private static String getString(@StringRes int stringId) {
	return AppActivity.getActivity().getResources().getString(stringId);
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message and an action
 * @param message the message to show
 * @param actionTitle button title
 * @param action the action to take
 */
public static void showSnackbar(String message, String actionTitle, View.OnClickListener action) {
	View rootView = AppActivity.getRootView();
	int duration = calculateDuration(message);
	Snackbar snackbar = Snackbar.make(rootView, message, duration);
	if (actionTitle != null && action != null) {
		snackbar.setAction(actionTitle, action);
	}
	snackbar.show();
}

private static int calculateDuration(String message) {
	if (message.length() > LENGTH_LONG_CHARACTERS) {
		return DURATION_LONG;
	} else if (message.length() > LENGTH_MEDIUM_CHARACTERS) {
		return DURATION_MEDIUM;
	} else {
		return DURATION_SHORT;
	}
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param stringId id of the message to show
 * @param actionTitleId button title as a string resource id
 * @param action the action to take
 */
public static void showSnackbar(@StringRes int stringId, @StringRes int actionTitleId, View.OnClickListener action) {
	showSnackbar(getString(stringId), getString(actionTitleId), action);
}
}
