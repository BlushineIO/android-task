package io.blushine.android;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Constructor;

import io.blushine.android.ui.ColorHelper;
import io.blushine.android.ui.Fonts;
import io.blushine.utils.EventBus;

/**
 * Various common helper methods for both {@link AppFragment} and {@link AppPreferenceFragment}.
 */
public class AppFragmentHelper {
private static final String TAG = AppFragmentHelper.class.getSimpleName();
private static final EventBus mEventBus = EventBus.getInstance();
private static AppFragmentHelper mCurrentHelper = null;
@ColorRes
private int mToolbarColorId;
@ColorInt
private int mToolbarColor = -1;
@ColorRes
private int mStatusbarColorId;
@ColorInt
private int mStatusbarColor = -1;
private Fragment mFragment;


/**
 * Current fragment
 */
AppFragmentHelper(Fragment fragment) {
	mFragment = fragment;
	setToolbarColor(R.color.primary, R.color.primary_dark);
}

/**
 * Set the toolbar and statusbar colors. Colors are applied in {@link #onViewRestored(View, Bundle)}
 * so call this method before then.
 * @param toolbarColor color of the toolbar
 * @param statusbarColor color of the statusbar
 */
void setToolbarColor(@ColorRes int toolbarColor, @ColorRes int statusbarColor) {
	mToolbarColorId = toolbarColor;
	mStatusbarColorId = statusbarColor;
}

/**
 * Go to the specified fragment. If the fragment exists in the back stack it will pop to this
 * fragment. Otherwise it will create a new fragment
 * @param fragmentClass the fragment class to goto to (or create)
 */
public static void gotoFragment(Class<? extends AppFragment> fragmentClass) {
	if (existsInBackStack(fragmentClass)) {
		AppActivity.getActivity().getFragmentManager().popBackStackImmediate(fragmentClass.getSimpleName(), 0);
	} else {
		try {
			Constructor<? extends AppFragment> constructor = fragmentClass.getConstructor();
			AppFragment appFragment = constructor.newInstance();
			appFragment.show();
		} catch (Exception e) {
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
	FragmentManager fragmentManager = AppActivity.getActivity().getFragmentManager();
	for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
		FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(0);
		if (fragmentClass.getSimpleName().equals(backStackEntry.getName())) {
			return true;
		}
	}
	return false;
}

/**
 * @return the current active fragment helper, null if none is active
 */
public static AppFragmentHelper getHelper() {
	if (mCurrentHelper != null && getFragment() != null) {
		return mCurrentHelper;
	} else {
		return null;
	}
}

/**
 * @return current active fragment, null if none is active
 */
public static Fragment getFragment() {
	if (mCurrentHelper != null) {
		return mCurrentHelper.mFragment;
	} else {
		return null;
	}
}

/**
 * Focus an edittext and show the keyboard
 * @param editText the field to focus and show the keyboard for
 */
public static void focusEditText(EditText editText) {
	editText.requestFocus();
	InputMethodManager inputMethodManager = (InputMethodManager) AppActivity.getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
}

/**
 * Called when the fragment has resumed
 */
void onResume() {
	Log.d(TAG, "onResume() — " + mFragment.getClass().getSimpleName());
	mCurrentHelper = this;
	mEventBus.post(new FragmentEvent(mFragment, FragmentEvent.EventTypes.RESUME));
}

/**
 * Called when the fragment has paused
 */
void onPause() {
	Log.d(TAG, "onPause() — " + mFragment.getClass().getSimpleName());
	if (mCurrentHelper == this) {
		mCurrentHelper = null;
	}
	hideKeyboard();
	mEventBus.post(new FragmentEvent(mFragment, FragmentEvent.EventTypes.PAUSE));
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
 * Called when the fragment has stopped
 */
void onStop() {
	Log.d(TAG, "onStop() — " + mFragment.getClass().getSimpleName());
	mEventBus.post(new FragmentEvent(mFragment, FragmentEvent.EventTypes.STOP));
}

/**
 * Called when the fragment view is destroyed
 */
void onDestroyView() {
	Log.d(TAG, "onDestroyView() — " + mFragment.getClass().getSimpleName());
}

/**
 * Called when the fragment is destroyed
 */
void onDestroy() {
	Log.d(TAG, "onDestroy() — " + mFragment.getClass().getSimpleName());
	mFragment = null;
}

/**
 * Updates the Toolbar and statusbar colors and fixes the Toolbar font
 * @param view the view that was created
 * @param savedInstanceState saved variables
 */
void onViewRestored(View view, @Nullable Bundle savedInstanceState) {
	updateColorsFromResource();
	
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
 * Fetch and set the toolbar and statusbar colors from the resource file
 */
private void updateColorsFromResource() {
	Resources resources = AppActivity.getActivity().getResources();
	mToolbarColor = ColorHelper.getColor(resources, mToolbarColorId, null);
	mStatusbarColor = ColorHelper.getColor(resources, mStatusbarColorId, null);
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
		fragmentManager.popBackStack();
	} else {
		AppActivity.getActivity().supportFinishAfterTransition();
	}
}
}
