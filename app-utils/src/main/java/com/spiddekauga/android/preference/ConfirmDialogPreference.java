package com.spiddekauga.android.preference;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import de.mrapp.android.dialog.MaterialDialog;
import de.mrapp.android.preference.AbstractDialogPreference;

/**
 * A confirm dialog preference screen
 */
public class ConfirmDialogPreference extends AbstractDialogPreference {
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
protected boolean needInputMethod() {
	return false;
}

@Override
protected void onPrepareDialog(@NonNull MaterialDialog.Builder dialogBuilder) {
	// Does nothing
}

@Override
protected void onDialogClosed(boolean positiveResult) {
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
