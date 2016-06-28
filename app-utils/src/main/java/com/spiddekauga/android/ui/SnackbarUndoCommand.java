package com.spiddekauga.android.ui;

import android.support.annotation.StringRes;
import android.view.View;

import com.spiddekauga.android.R;
import com.spiddekauga.utils.command.UndoCommand;

/**
 * Undo command for a snackbar. This command should preferable
 */
public abstract class SnackbarUndoCommand implements UndoCommand, View.OnClickListener {

@Override
public void onClick(View view) {
	undo();
}

protected void showSnackbar(@StringRes int resId) {
	SnackbarHelper.showSnackbar(resId, R.string.undo, this);
}
}
