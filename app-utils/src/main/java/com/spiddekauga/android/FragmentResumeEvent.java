package com.spiddekauga.android;

/**
 * Fired when onResume() is called in a AppFragment
 */
public class FragmentResumeEvent {
private AppFragment mAppFragment;
private AppPreferenceFragment mPreferenceFragment;
private FragmentTypes mType;

/**
 * Create AppFragment type
 * @param fragment the fragment that was resumed
 */
FragmentResumeEvent(AppFragment fragment) {
	mAppFragment = fragment;
	mType = FragmentTypes.APP;
}

/**
 * Create AppPreferenceFragment type
 * @param fragment the preference fragment that was resumed
 */
FragmentResumeEvent(AppPreferenceFragment fragment) {
	mPreferenceFragment = fragment;
	mType = FragmentTypes.PREFERENCE;
}

/**
 * Get the fragment that was resumed
 * @return fragment that was resumed
 */
public Object getFragment() {
	switch (mType) {
	case APP:
		return mAppFragment;
	case PREFERENCE:
		return mPreferenceFragment;
	}

	return null;
}

/**
 * Get the {@link AppFragment} that was resumed
 * @return the {@link AppFragment} that was resumed
 */
public AppFragment getAppFragment() {
	return mAppFragment;
}

/**
 * Get the {@link AppPreferenceFragment} that was resumed
 * @return the {@link AppPreferenceFragment} that was resumed
 */
public AppPreferenceFragment getPreferenceFragment() {
	return mPreferenceFragment;
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
