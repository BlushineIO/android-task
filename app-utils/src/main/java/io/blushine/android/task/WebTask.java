package io.blushine.android.task;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.ContextCompat;

import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import io.blushine.android.AppActivity;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Base class for all tasks requiring an Internet connection
 */
public abstract class WebTask<Params, Progress, Event extends ResponseEvent> extends EventTask<Params, Progress, Event> {
private static final int RETRIES = 5;
private static final long SLEEP_TIME = 10 * 1000; // IN MS
private static OkHttpClient mOkHttpClient = new OkHttpClient();
private WaitTypes mWaitType;

/**
 * @param waitType what to check and maybe wait for before calling {@link
 * #doInBackground(ResponseEvent, Object[])}
 */
protected WebTask(WaitTypes waitType) {
	mWaitType = waitType;
	
	CookieHandler cookieHandler = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
	CookieHandler.setDefault(cookieHandler);
	CookieJar cookieJar = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(AppActivity.getActivity()));
	
	mOkHttpClient = new OkHttpClient.Builder()
			.cookieJar(cookieJar)
			.build();
}

/**
 * Do a new HTTP call
 * @param request HTTP request
 * @return An HTTP Response, null if failed
 */
public static Response call(Request request) {
	try {
		return mOkHttpClient.newCall(request).execute();
	} catch (IOException e) {
		return null;
	}
}

protected void setWaitType(WaitTypes waitType) {
	mWaitType = waitType;
}

@Override
@SafeVarargs
protected final Event doInBackground(Params... params) {
	Event event = newEvent(params);
	
	switch (mWaitType) {
	case WAIT_CONNECTION:
		waitForConnection();
		break;
	
	case WAIT_USER_LOGGED_IN:
		waitForUserLoggedIn();
		break;
	
	case CHECK_CONNECTION:
		if (!isConnected()) {
			event.setStatus(ResponseEvent.ResponseStatuses.FAILED_NO_INTERNET);
			return event;
		}
		break;
	
	case CHECK_USER_LOGGED_IN:
		if (!isUserLoggedIn()) {
			event.setStatus(ResponseEvent.ResponseStatuses.FAILED_USER_LOGGED_OUT);
			return event;
		}
		break;
	
	case ONLY_RETRY:
		// Does nothing
		break;
	}
	
	// Retry
	int retriesLeft = RETRIES;
	while (retriesLeft > 0 && !event.isSuccessful()) {
		doInBackground(event, params);
		retriesLeft -= 1;
	}
	
	return event;
}

protected abstract Event newEvent(Params... params);

/**
 * Wait for an Internet connection
 */
private void waitForConnection() {
	while (!isConnected()) {
		sleep();
	}
}

/**
 * Wait for user is online
 */
protected abstract void waitForUserLoggedIn();

/**
 * Check if we have an Internet connection
 * @return true if we're connected to the Internet
 */
protected boolean isConnected() {
	ConnectivityManager connectivityManager = (ConnectivityManager) AppActivity.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
	if (ContextCompat.checkSelfPermission(AppActivity.getActivity(), Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	} else {
		return true;
	}
}

/**
 * Check if the user is online
 * @return true if the user is logged in
 */
protected abstract boolean isUserLoggedIn();

@SuppressWarnings({"unchecked", "varargs"})
protected abstract void doInBackground(Event event, Params... params);

/**
 * Sleep while waiting for a connection or until the user is logged in
 */
protected void sleep() {
	try {
		Thread.sleep(SLEEP_TIME);
	} catch (InterruptedException e) {
		// Does nothing
	}
}

protected enum WaitTypes {
	/** Check once after a connection */
	CHECK_CONNECTION,
	/** Check once if the user is online */
	CHECK_USER_LOGGED_IN,
	/**
	 * Wait until the user is online (including a connection) before calling {@link
	 * #doInBackground(ResponseEvent, Object[])}
	 */
	WAIT_USER_LOGGED_IN,
	/**
	 * Wait we have an Internet connection before calling {@link #doInBackground(ResponseEvent,
	 * Object[])}
	 */
	WAIT_CONNECTION,
	/**
	 * Don't check connection, just retry.
	 */
	ONLY_RETRY,
}
}
