package com.spiddekauga.android;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.spiddekauga.android.ui.ColorHelper;

import java.util.List;

/**
 * Base class for fullscreen dialog fragments
 */
public abstract class AppFragment extends Fragment {
@StringRes
int mBackMessage;
@StringRes
int mBackPositiveActionText = R.string.discard;
@ColorInt
int mToolbarColor;
@ColorInt
int mStatusbarColor;

protected AppFragment() {
	setToolbarColor(R.color.primary, R.color.primary_dark);
}

/**
 * Set the toolbar and statusbar colors. Only works before {@link #onResume()} is called.
 * @param toolbarColor color of the toolbar
 * @param statusbarColor color of the statusbar
 */
protected void setToolbarColor(@ColorRes int toolbarColor, @ColorRes int statusbarColor) {
	Resources resources = AppActivity.getActivity().getResources();
	mToolbarColor = ColorHelper.getColor(resources, toolbarColor, null);
	mStatusbarColor = ColorHelper.getColor(resources, statusbarColor, null);
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
	getActivity().getWindow().setStatusBarColor(mStatusbarColor);
	Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	if (toolbar != null) {
		colorToolbar(toolbar);
	}
}

/**
 * Set the correct color of the toolbar and its icons
 * @param toolbar the toolbar to color
 */
private void colorToolbar(Toolbar toolbar) {
	toolbar.setBackgroundColor(mToolbarColor);

	Resources resources = AppActivity.getActivity().getResources();
	@ColorInt int iconColor = ColorHelper.getColor(resources, R.color.icon_toolbar, null);

	// Navigation icon
	Drawable navIcon = toolbar.getNavigationIcon();
	if (navIcon != null) {
		navIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
	}

	// Menu items
	Menu menu = toolbar.getMenu();
	if (menu != null) {
		for (int i = 0; i < menu.size(); i++) {
			MenuItem menuItem = menu.getItem(i);

			// Icon
			Drawable icon = menuItem.getIcon();
			if (icon != null) {
				icon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
			}
		}
	}
}

/**
 * Display the back message. Called when going back
 */
public void back() {
	if (isChanged()) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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

public void dismiss() {
	getFragmentManager().popBackStackImmediate();
}

/**
 * Requires that all activities has a fragment container with id fragment_container.
 */
public void show() {
	AppActivity activity = AppActivity.getActivity();
	FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.fragment_container, this);
	Fragment currentFragment = getVisibleFragment();
	if (currentFragment != null) {
		fragmentTransaction.addToBackStack(currentFragment.getClass().getSimpleName());
	}
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
}