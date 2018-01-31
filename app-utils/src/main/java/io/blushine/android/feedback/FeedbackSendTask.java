package io.blushine.android.feedback;

import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;

import io.blushine.android.AppActivity;
import io.blushine.android.R;
import io.blushine.android.task.ResponseEvent;
import io.blushine.android.task.WebTask;
import io.blushine.cloudshine.feedbackApi.FeedbackApi;
import io.blushine.cloudshine.feedbackApi.model.Feedback;
import io.blushine.cloudshine.feedbackApi.model.FeedbackResponse;

/**
 * Send menu_feedback task
 */
class FeedbackSendTask extends WebTask<Feedback, Void, FeedbackResponseEvent> {
private static final String TAG = FeedbackSendTask.class.getSimpleName();
private static final String CLOUDSHINE_ROOT_URL = AppActivity.getActivity().getResources().getString(R.string.cloudshine_root_url);
private static final String CLOUDSHINE_APP_NAME = AppActivity.getActivity().getResources().getString(R.string.cloudshine_app_name);
private static FeedbackApi mFeedbackApi = null;

FeedbackSendTask() {
	super(WaitTypes.WAIT_CONNECTION);
}

@Override
protected FeedbackResponseEvent newEvent(Feedback... feedbacks) {
	return new FeedbackResponseEvent(feedbacks);
}

@Override
protected void waitForUserLoggedIn() {
	// Does nothing
}

@Override
protected boolean isUserLoggedIn() {
	return true;
}

@Override
protected void doInBackground(FeedbackResponseEvent event, Feedback... feedbacks) {
	if (mFeedbackApi == null) {
		initApi();
	}

	event.setStatus(ResponseEvent.ResponseStatuses.SUCCESS);
	for (Feedback feedback : feedbacks) {
		try {
			FeedbackResponse feedbackResponse = mFeedbackApi.sendFeedback(feedback).execute();

			if (feedbackResponse.getState().equals("FAILED_SERVER_ERROR")) {
				event.setStatus(ResponseEvent.ResponseStatuses.FAILED);
				break;
			}
		} catch (IOException e) {
			Log.w(TAG, "doInBackground() - Couldn't send feedback", e);
			event.setStatus(ResponseEvent.ResponseStatuses.FAILED);
			break;
		}
	}
}

private void initApi() {
	FeedbackApi.Builder builder = new FeedbackApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null);
	builder.setRootUrl(CLOUDSHINE_ROOT_URL);
	builder.setApplicationName(CLOUDSHINE_APP_NAME);

	mFeedbackApi = builder.build();
}
}
