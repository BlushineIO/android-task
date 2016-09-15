package com.spiddekauga.android;

import android.app.Fragment;

/**
 * Fired when onResume() is called in a AppFragment
 */
public class FragmentEvent {
private Fragment mFragment;
private FragmentTypes mFragmentType;
private EventTypes mEventType;

/**
 * Create the fragment
 * @param fragment the fragment that was resumed
 * @param eventType event type
 */
FragmentEvent(Fragment fragment, EventTypes eventType) {
	mFragment = fragment;
	mEventType = eventType;
	if (fragment instanceof AppFragment) {
		mFragmentType = FragmentTypes.APP;
	} else if (fragment instanceof AppPreferenceFragment) {
		mFragmentType = FragmentTypes.PREFERENCE;
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
public FragmentTypes getFragmentType() {
	return mFragmentType;
}

/**
 * @return which type of fragment event this is
 */
public EventTypes getEventType() {
	return mEventType;
}

/**
 * Which type of fragment that was resumed
 */
public enum FragmentTypes {
	APP,
	PREFERENCE,
}

/**
 * Type of fragment event
 */
public enum EventTypes {
	RESUME,
	PAUSE,
	STOP
}
}
