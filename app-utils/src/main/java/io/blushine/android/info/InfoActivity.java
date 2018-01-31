package io.blushine.android.info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

import io.blushine.android.AppActivity;

/**
 * Display some text information in a fragment. If you can, preferably use {@link InfoFragment}
 * directly instead as this requires less resources.
 */
public class InfoActivity extends AppActivity {

/**
 * Create and show an instance of this class
 * @param titleId toolbar title id of this info fragment
 * @param rawIds the text file(s) to concatenate and show
 */
public static void show(@StringRes int titleId, @RawRes int... rawIds) {
	show(titleId, "", rawIds);
}

/**
 * Create and show an instance of this class
 * @param titleId toolbar title id of this info fragment
 * @param separator separator between the text files
 * @param rawIds the text file(s) to concatenate and show
 */
public static void show(@StringRes int titleId, String separator, @RawRes int... rawIds) {
	Bundle extras = InfoFragment.createArguments(titleId, separator, rawIds);
	show(extras);
}

/**
 * Create and show an instance of this class
 * @param extras contains the toolbar title and the text to display
 */
private static void show(Bundle extras) {
	Activity activity = getActivity();
	Intent intent = new Intent(activity, InfoActivity.class);
	intent.putExtras(extras);
	activity.startActivity(intent);
}

/**
 * Create and show an instance of this class
 * @param title the toolbar title of this info fragment
 * @param text the text to display in the fragment
 */
public static void show(String title, String text) {
	Bundle extras = InfoFragment.createArguments(title, text);
	show(extras);
}

@Override
public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	InfoFragment infoFragment = new InfoFragment();
	infoFragment.setArguments(getIntent().getExtras());
	infoFragment.show();
}
}
