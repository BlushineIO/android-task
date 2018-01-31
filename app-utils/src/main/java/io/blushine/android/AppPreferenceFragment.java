package io.blushine.android;

import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Base class for settings fragments
 */
public abstract class AppPreferenceFragment extends PreferenceFragment {
private AppFragmentHelper mFragmentHelper = new AppFragmentHelper(this);

@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	View view = super.onCreateView(inflater, container, savedInstanceState);

	// Materialize pre-lollipop methods
	if (view != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
		materialize(view);
	}

	return view;
}

@Override
public void onViewCreated(View view, Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	mFragmentHelper.onViewRestored(view.getRootView(), savedInstanceState);
}

@Override
public void onStop() {
	super.onStop();
	mFragmentHelper.onStop();
}

/**
 * Make the preferences use our own Material-looking lists
 * @param view the view that should be created
 */
private void materialize(View view) {
	// Remove list padding
	View listView = view.findViewById(android.R.id.list);
	listView.setPadding(0, 0, 0, 0);

	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
		PreferenceScreen preferenceScreen = getPreferenceScreen();

		for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
			Preference preference = preferenceScreen.getPreference(i);
			materialize(preference);
		}
	}
}

/**
 * Make the preference Material-looking
 * @param preference the preference to use a material looking list-style
 */
private void materialize(Preference preference) {
	// Category
	if (preference instanceof PreferenceCategory) {
		if (preference.getLayoutResource() != R.layout.list_item_category) {
			preference.setLayoutResource(R.layout.list_item_category);

			PreferenceCategory category = (PreferenceCategory) preference;
			for (int i = 0; i < category.getPreferenceCount(); i++) {
				Preference innerPreference = category.getPreference(i);
				materialize(innerPreference);
			}
		}
	}
	// Screen
	else if (preference instanceof PreferenceScreen) {
		if (preference.getLayoutResource() != R.layout.list_item) {
			preference.setLayoutResource(R.layout.list_item);
		}
	}
	// Basic preference
	else {
		if (preference.getLayoutResource() != R.layout.list_item_widget) {
			preference.setLayoutResource(R.layout.list_item_widget);
		}
	}
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
	mFragmentHelper.onResume();
}

@Override
public void onPause() {
	super.onPause();
	mFragmentHelper.onPause();
}

/**
 * Dismiss this window.
 */
public void back() {
	dismiss();
}

/**
 * Dismiss this window.
 */
public void dismiss() {
	mFragmentHelper.dismiss();
}
}
