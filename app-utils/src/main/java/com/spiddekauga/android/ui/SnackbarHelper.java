package com.spiddekauga.android.ui;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.spiddekauga.android.AppActivity;

/**
 * Some helper methods for creating simple snackbars
 */
public class SnackbarHelper {
/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param message the message to show
 */
public static void showSnackbar(String message) {
	View rootView = AppActivity.getRootView();
	Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param stringId id of the message to show
 */
public static void showSnackbar(@StringRes int stringId) {
	View rootView = AppActivity.getRootView();
	Snackbar.make(rootView, stringId, Snackbar.LENGTH_LONG).show();
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message and an action
 * @param message the message to show
 * @param actionTitle button title
 * @param action the action to take
 */
public static void showSnackbar(String message, String actionTitle, View.OnClickListener action) {
	View rootView = AppActivity.getRootView();
	Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG);
	snackbar.setAction(actionTitle, action);
	snackbar.show();
}

/**
 * Create a simple {@link android.support.design.widget.Snackbar} with a message
 * @param stringId id of the message to show
 * @param actionTitleId button title as a string resource id
 * @param action the action to take
 */
public static void showSnackbar(@StringRes int stringId, @StringRes int actionTitleId, View.OnClickListener action) {
	View rootView = AppActivity.getRootView();
	Snackbar snackbar = Snackbar.make(rootView, stringId, Snackbar.LENGTH_LONG);
	snackbar.setAction(actionTitleId, action);
	snackbar.show();
}
}
