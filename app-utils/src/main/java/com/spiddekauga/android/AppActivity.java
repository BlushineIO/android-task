package com.spiddekauga.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Base class for Android Activities
 */
public abstract class AppActivity extends AppCompatActivity {
static private AppCompatActivity mActivity;

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
public static AppCompatActivity getActivity() {
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

/**
 * Set the title of the action bar (if it exists)
 * @param title title of the action bar
 */
public static void setTitle(String title) {
	ActionBar actionBar = mActivity.getSupportActionBar();
	if (actionBar != null) {
		actionBar.setTitle(title);
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
