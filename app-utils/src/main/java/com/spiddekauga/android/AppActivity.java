package com.spiddekauga.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.spiddekauga.android.feedback.FeedbackRepo;

/**
 * Base class for Android Activities
 */
public abstract class AppActivity extends AppCompatActivity {
static private AppActivity mActivity = null;
private static boolean mFirstTime = true;

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
public static AppActivity getActivity() {
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
 * Set the title of the toolbar (if it exists)
 * @param title title of the toolbar
 */
public static void setTitle(String title) {
	ActionBar actionBar = mActivity.getSupportActionBar();
	if (actionBar != null) {
		actionBar.setTitle(title);
	}
}

/**
 * Return the root view, also known as content view
 * @return the root view, also known as content view
 */
public static View getRootView() {
	View contentView = mActivity.findViewById(android.R.id.content);
	return ((ViewGroup) contentView).getChildAt(0);
}

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mActivity = this;

	if (mFirstTime) {
		mFirstTime = false;
		onFirstTime();
	}
}

/**
 * Called first time the application is started
 */
protected void onFirstTime() {
	FeedbackRepo feedbackRepo = FeedbackRepo.getInstance();
	feedbackRepo.resetSyncingFeedbacks();
	feedbackRepo.syncUnsyncedFeedbacks();
}

@Override
public void onBackPressed() {
	AppFragmentHelper visibleFragment = AppFragmentHelper.getHelper();
	if (visibleFragment != null) {
		visibleFragment.back();
	} else {
		super.onBackPressed();
	}
}

@Override
protected void onResume() {
	super.onResume();
	mActivity = this;
}
}
