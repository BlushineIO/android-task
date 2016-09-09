package com.spiddekauga.android;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;

import com.spiddekauga.utils.EventBus;

/**
 * Base class for settings fragments
 */
public abstract class AppPreferenceFragment extends PreferenceFragment {
private static final EventBus mEventBus = EventBus.getInstance();
private AppFragmentHelper mFragmentHelper = new AppFragmentHelper();

@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	mFragmentHelper.onViewCreated(view.getRootView(), savedInstanceState);
}

/**
 * Requires that all activities has a fragment container with id fragment_container.
 */
public void show() {
	AppActivity activity = AppActivity.getActivity();
	FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
	fragmentTransaction.replace(R.id.fragment_container, this);
	fragmentTransaction.addToBackStack(getClass().getSimpleName());
	fragmentTransaction.commit();
}

@Override
public void onResume() {
	super.onResume();
	mEventBus.post(new FragmentResumeEvent(this));
}
}
