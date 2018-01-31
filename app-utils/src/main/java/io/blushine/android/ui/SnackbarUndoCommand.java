package io.blushine.android.ui;

import android.support.annotation.StringRes;
import android.view.View;

import io.blushine.android.R;
import io.blushine.utils.command.UndoCommand;

/**
 * Undo command for a snackbar. This command should preferable
 */
public abstract class SnackbarUndoCommand implements UndoCommand, View.OnClickListener {

@Override
public void onClick(View view) {
	undo();
}

protected void showSnackbar(@StringRes int resId) {
	SnackbarHelper.showSnackbar(resId);
}

protected void showSnackbarWithUndo(@StringRes int resId) {
	SnackbarHelper.showSnackbar(resId, R.string.undo, this);
}
}
