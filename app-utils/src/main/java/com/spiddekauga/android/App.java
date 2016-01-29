package com.spiddekauga.android;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Base class for Android Applications
 */
public class App extends AppCompatActivity {
static private Activity mActivity;

/**
 * @return the context for this app
 */
public static Activity getActivity() {
	return mActivity;
}

@Override
protected void onResume() {
	super.onResume();
	mActivity = this;
}
}
