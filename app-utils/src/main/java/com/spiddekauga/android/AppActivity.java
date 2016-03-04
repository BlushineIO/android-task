package com.spiddekauga.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Base class for Android Activities
 */
public abstract class AppActivity extends AppCompatActivity {
static private Activity mActivity;

/**
 * Exit the application
 */
public static void exit() {
	if (mActivity != null) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity.startActivity(intent);
	}
}

/**
 * @return the context for this app
 */
public static Activity getActivity() {
	return mActivity;
}

/**
 * Switch to the specified activity
 * @param activityClass the activity to switch to
 */
public static void switchTo(Class<? extends Activity> activityClass) {
	if (mActivity != null) {
		Intent intent = new Intent(mActivity, activityClass);
		mActivity.startActivity(intent);
	}
}

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mActivity = this;
}

@Override
protected void onResume() {
	super.onResume();
	mActivity = this;
}
}
