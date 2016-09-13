package com.spiddekauga.android;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.spiddekauga.utils.EventBus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import de.mrapp.android.dialog.MaterialDialog;

/**
 * Base class for fullscreen fragments
 */
public abstract class AppFragment extends Fragment {
private static final String TAG = AppFragment.class.getSimpleName();
private static final EventBus mEventBus = EventBus.getInstance();
@StringRes
int mBackMessage;
@StringRes
int mBackPositiveActionText = R.string.discard;
private AppFragmentHelper mFragmentHelper = new AppFragmentHelper();

/**
 * Go to the specified fragment. If the fragment exists in the back stack it will pop to this
 * fragment. Otherwise it will create a new fragment
 * @param fragmentClass the fragment class to goto to (or create)
 */
public static void gotoFragment(Class<? extends AppFragment> fragmentClass) {
	if (existsInBackStack(fragmentClass)) {
		AppActivity.getActivity().getSupportFragmentManager().popBackStackImmediate(fragmentClass.getSimpleName(), 0);
	} else {
		try {
			Constructor<? extends AppFragment> constructor = fragmentClass.getConstructor();
			AppFragment appFragment = constructor.newInstance();
			appFragment.show();
		} catch (NoSuchMethodException | IllegalAccessException | java.lang.InstantiationException | InvocationTargetException e) {
			Log.e(TAG, "newFirstScreenInstance() - Failed to find Constructor", e);
		}
	}
}

/**
 * Checks if a fragment class exists in the back stack
 * @param fragmentClass the fragment class to check if it exists in the back stack
 * @return true if the fragmentClass exists in the back stack
 */
public static boolean existsInBackStack(Class<? extends Fragment> fragmentClass) {
	FragmentManager fragmentManager = AppActivity.getActivity().getSupportFragmentManager();
	for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
		FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(0);
		if (fragmentClass.getSimpleName().equals(backStackEntry.getName())) {
			return true;
		}
	}
	return false;
}

/**
 * Show the current fragment
 */
public void show() {
	AppActivity activity = AppActivity.getActivity();
	FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(android.R.id.content, this);
	fragmentTransaction.addToBackStack(getClass().getSimpleName());
	fragmentTransaction.commit();
}

static Fragment getVisibleFragment() {
	FragmentManager fragmentManager = AppActivity.getActivity().getSupportFragmentManager();
	List<Fragment> fragments = fragmentManager.getFragments();
	if (fragments != null) {
		for (Fragment fragment : fragments) {
			if (fragment != null && fragment.isVisible()) {
				return fragment;
			}
		}
	}
	return null;
}

/**
 * Set the toolbar and statusbar colors. Only works before {@link #onResume()} is called.
 * @param toolbarColor color of the toolbar
 * @param statusbarColor color of the statusbar
 */
protected void setToolbarColor(@ColorRes int toolbarColor, @ColorRes int statusbarColor) {
	mFragmentHelper.setToolbarColor(toolbarColor, statusbarColor);
}

/**
 * Set the message when back is pressed. This will be displayed in a small dialog
 * @param message the message to display in a small dialog
 */
protected void setBackMessage(@StringRes int message) {
	mBackMessage = message;
}

/**
 * Set the message when back is pressed. This will be displayed in a small dialog
 * @param message the message to display in a small dialog
 * @param positiveActionText the positive action button text of the dialog. By default this is
 * R.string.discard
 */
protected void setBackMessage(@StringRes int message, @StringRes int positiveActionText) {
	mBackMessage = message;
	mBackPositiveActionText = positiveActionText;
}

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	mFragmentHelper.onViewCreated(view, savedInstanceState);
}

@Override
public void onResume() {
	super.onResume();
	mEventBus.post(new FragmentResumeEvent(this));
}

@Override
public void onStop() {
	super.onStop();

	// Always hide the keyboard
	hideKeyboard();
}

/**
 * Hide the keyboard
 */
protected void hideKeyboard() {
	View focus = getActivity().getCurrentFocus();
	if (focus instanceof EditText) {
		InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}

/**
 * Display back dialog discard message if something has been changed in the fragment. If nothing has
 * been changed it simply dismisses the window.
 */
public void back() {
	hideKeyboard();
	if (isChanged()) {
		MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getContext());
		dialogBuilder.setMessage(mBackMessage);
		dialogBuilder.setPositiveButton(mBackPositiveActionText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		dialogBuilder.create().show();
	} else {
		dismiss();
	}
}

/**
 * Override this to check if values have been changed
 * @return true if values have been changed/edited
 */
protected boolean isChanged() {
	return false;
}

/**
 * Dismiss this window.
 */
public void dismiss() {
	// Never pop the first fragment in back stack as the activity will be left empty then
	FragmentManager fragmentManager = getFragmentManager();
	if (fragmentManager.getBackStackEntryCount() > 1) {
		fragmentManager.popBackStackImmediate();
	} else {
		AppActivity.getActivity().supportFinishAfterTransition();
	}
}

}