package com.spiddekauga.android.task;

import android.os.AsyncTask;

import com.spiddekauga.utils.EventBus;

/**
 * Base class for tasks that should send an event once the execution finished
 */
abstract class EventTask<Params, Progress> extends AsyncTask<Params, Progress, Object> {
private static final EventBus mEventBus = EventBus.getInstance();

@Override
protected void onPostExecute(Object event) {
	if (event != null) {
		mEventBus.post(event);
	}
}
}
