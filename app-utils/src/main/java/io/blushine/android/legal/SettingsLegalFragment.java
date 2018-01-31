package io.blushine.android.legal;

import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.support.v7.app.ActionBar;

import io.blushine.android.AppActivity;
import io.blushine.android.AppPreferenceFragment;
import io.blushine.android.R;
import io.blushine.android.info.InfoActivity;

/**
 * Legal settings fragment
 */
public class SettingsLegalFragment extends AppPreferenceFragment {
@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	addPreferencesFromResource(R.xml.settings_legal);

	Resources resources = AppActivity.getActivity().getResources();

	// Privacy Policy
	Preference privacyPrefs = findPreference(resources.getString(R.string.legal_privacy_policy_key));
	if (privacyPrefs != null) {
		privacyPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				InfoActivity.show(R.string.legal_privacy_policy_title, R.raw.privacy_policy);
				return true;
			}
		});
	}

	// Open source
	Preference openSourcePrefs = findPreference(resources.getString(R.string.legal_open_source_key));
	if (openSourcePrefs != null) {
		openSourcePrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				InfoActivity.show(R.string.legal_open_source_title,
						"\n\n",
						R.raw.open_source_legal_app,
						R.raw.open_source_legal_lib);
				return true;
			}
		});
	}
}

@Override
public void onResume() {
	super.onResume();
	ActionBar actionBar = AppActivity.getActivity().getSupportActionBar();
	if (actionBar != null) {
		actionBar.setTitle(R.string.legal_title);
	}
}
}
