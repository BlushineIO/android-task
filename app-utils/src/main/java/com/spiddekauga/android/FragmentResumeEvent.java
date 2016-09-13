package com.spiddekauga.android;

import android.app.Fragment;

/**
 * Fired when onResume() is called in a AppFragment
 */
public class FragmentResumeEvent {
private Fragment mFragment;
private FragmentTypes mType;

/**
 * Create the fragment
 * @param fragment the fragment that was resumed
 */
FragmentResumeEvent(Fragment fragment) {
	mFragment = fragment;
	if (fragment instanceof AppFragment) {
		mType = FragmentTypes.APP;
	} else if (fragment instanceof AppPreferenceFragment) {
		mType = FragmentTypes.PREFERENCE;
	}
}

/**
 * Get the fragment that was resumed
 * @return fragment that was resumed
 */
@SuppressWarnings("unchecked")
public <FragmentType> FragmentType getFragment() {
	return (FragmentType) mFragment;
}

/**
 * @return which type of fragment that was resumed
 */
public FragmentTypes getType() {
	return mType;
}

/**
 * Which type of fragment that was resumed
 */
public enum FragmentTypes {
	APP,
	PREFERENCE,
}
}
