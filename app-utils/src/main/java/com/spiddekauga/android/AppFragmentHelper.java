package com.spiddekauga.android;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.spiddekauga.android.ui.ColorHelper;
import com.spiddekauga.android.ui.Fonts;
import com.spiddekauga.utils.EventBus;

/**
 * Various common helper methods for both {@link AppFragment} and {@link AppPreferenceFragment}.
 */
public class AppFragmentHelper {
private static final EventBus mEventBus = EventBus.getInstance();
private static AppFragmentHelper mCurrentFragment = null;
@ColorInt
private int mToolbarColor;
@ColorInt
private int mStatusbarColor;
private Fragment mFragment;


/**
 * Current fragment
 */
AppFragmentHelper(Fragment fragment) {
	mFragment = fragment;
	setToolbarColor(R.color.primary, R.color.primary_dark);
}

/**
 * Set the toolbar and statusbar colors. Only works before {@link AppFragment#onResume()} is
 * called.
 * @param toolbarColor color of the toolbar
 * @param statusbarColor color of the statusbar
 */
void setToolbarColor(@ColorRes int toolbarColor, @ColorRes int statusbarColor) {
	Resources resources = AppActivity.getActivity().getResources();
	mToolbarColor = ColorHelper.getColor(resources, toolbarColor, null);
	mStatusbarColor = ColorHelper.getColor(resources, statusbarColor, null);
}

/**
 * Get current active (and visible) fragment
 */
public static AppFragmentHelper getCurrentFragment() {
	if (mCurrentFragment != null && mCurrentFragment.getFragment().isVisible()) {
		return mCurrentFragment;
	} else {
		return null;
	}
}

/**
 * Get the fragment associated with this helper
 */
public Fragment getFragment() {
	return mFragment;
}

/**
 * Called when the fragment has resumed
 */
void onResume() {
	mCurrentFragment = this;
	mEventBus.post(new FragmentEvent(mFragment, FragmentEvent.EventTypes.RESUME));
}

/**
 * Called when the fragment has stopped
 */
void onStop() {
	if (mCurrentFragment == this) {
		mCurrentFragment = null;
	}
	hideKeyboard();
	mEventBus.post(new FragmentEvent(mFragment, FragmentEvent.EventTypes.STOP));
}

/**
 * Hide the keyboard
 */
public static void hideKeyboard() {
	View focus = AppActivity.getActivity().getCurrentFocus();
	if (focus instanceof EditText) {
		InputMethodManager inputMethodManager = (InputMethodManager) AppActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}

/**
 * Updates the Toolbar and statusbar colors and fixes the Toolbar font
 * @param view the view that was created
 * @param savedInstanceState saved variables
 */
void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	if (Build.VERSION.SDK_INT >= 21) {
		AppActivity.getActivity().getWindow().setStatusBarColor(mStatusbarColor);
	}
	Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	if (toolbar != null) {
		colorToolbar(toolbar);

		// Fix Fonts
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
			fixToolbarFonts(toolbar);
		}
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
 * Fix Toolbar font styles
 * @param toolbar the toolbar to fix the fonts on
 */
private void fixToolbarFonts(Toolbar toolbar) {
	for (int childIndex = 0; childIndex < toolbar.getChildCount(); childIndex++) {
		View view = toolbar.getChildAt(childIndex);

		// Title
		if (view instanceof TextView) {
			TextView textView = (TextView) view;
			if (textView.getText().equals(toolbar.getTitle())) {
				textView.setTypeface(Fonts.MEDIUM.getTypeface());
			}
		}
		// Menu buttons
		else if (view instanceof ActionMenuView) {
			ActionMenuView menuView = (ActionMenuView) view;
			for (int menuItemIndex = 0; menuItemIndex < menuView.getChildCount(); menuItemIndex++) {
				ActionMenuItemView itemView = (ActionMenuItemView) menuView.getChildAt(menuItemIndex);
				itemView.setTypeface(Fonts.MEDIUM.getTypeface());
			}
		}
	}
}

/**
 * Go back, i.e. dismiss the window. If the AppFragment has unsaved changes a discard messages will
 * be shown
 */
public void back() {
	hideKeyboard();
	if (mFragment instanceof AppFragment) {
		((AppFragment) mFragment).back();
	} else if (mFragment instanceof AppPreferenceFragment) {
		((AppPreferenceFragment) mFragment).back();
	}
}

/**
 * Dismiss the fragment associated with this helper
 */
public void dismiss() {
	FragmentManager fragmentManager = mFragment.getFragmentManager();
	if (fragmentManager.getBackStackEntryCount() > 1) {
		fragmentManager.popBackStackImmediate();
	} else {
		AppActivity.getActivity().supportFinishAfterTransition();
	}
}
}
