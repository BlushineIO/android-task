package com.spiddekauga.android;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Matteus Magnusson <matteus.magnusson@spiddekauga.com>
 */
public class App extends AppCompatActivity {
static private Context mContext;

/**
 * @return the context for this app
 */
public static Context getContext() {
	return mContext;
}

/**
 * Set the context
 * @param context the active context
 */
protected static void setContext(Context context) {
	mContext = context;
}
}
