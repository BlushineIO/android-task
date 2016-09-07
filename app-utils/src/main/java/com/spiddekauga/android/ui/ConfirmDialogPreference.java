package com.spiddekauga.android.ui;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

/**
 * A confirm dialog preference screen
 */
public class ConfirmDialogPreference extends DialogPreference {
private Listener mListener = null;

public ConfirmDialogPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
	super(context, attrs, defStyleAttr, defStyleRes);
}

public ConfirmDialogPreference(Context context, AttributeSet attrs, int defStyleAttr) {
	super(context, attrs, defStyleAttr);
}

public ConfirmDialogPreference(Context context, AttributeSet attrs) {
	super(context, attrs);
}

public ConfirmDialogPreference(Context context) {
	super(context);
}

/**
 * Set listener when the dialog is closed
 * @param listener listen to dialog events
 */
public void setListener(Listener listener) {
	mListener = listener;
}

@Override
protected void onDialogClosed(boolean positiveResult) {
	super.onDialogClosed(positiveResult);
	if (mListener != null) {
		mListener.onDialogClosed(positiveResult);
	}
}

public interface Listener {
	/**
	 * Called when the dialog is closed
	 * @param positiveResult true if the user click the positive button
	 */
	void onDialogClosed(boolean positiveResult);
}
}
