package io.blushine.android.feedback;

import com.squareup.otto.Subscribe;

import java.util.List;

import io.blushine.cloudshine.feedbackApi.model.Feedback;
import io.blushine.utils.EventBus;

/**
 * Repository for handling feedbacks
 */
public class FeedbackRepo {
private static FeedbackRepo mInstance = null;
private FeedbackPrefsGateway mPrefsGateway = new FeedbackPrefsGateway();

/**
 * Enforces singleton pattern
 */
protected FeedbackRepo() {
	EventBus.getInstance().register(this);
}

/**
 * Get singleton instance
 * @return get instance
 */
public static FeedbackRepo getInstance() {
	if (mInstance == null) {
		mInstance = new FeedbackRepo();
	}
	return mInstance;
}

/**
 * Send a new feedback. Will store the menu_feedback locally until it has been successfully sent
 * @param feedback the feedback to send.
 */
void sendFeedback(Feedback feedback) {
	feedback.setSyncing(true);
	mPrefsGateway.addFeedback(feedback);
	FeedbackSendTask feedbackSendTask = new FeedbackSendTask();
	feedbackSendTask.execute(feedback);
}

/**
 * Called after sendFeedback server response
 * @param event the feedback response event
 */
@Subscribe
@SuppressWarnings("unused")
public void onFeedbackResponse(FeedbackResponseEvent event) {
	if (event.isSuccessful()) {
		List<Feedback> syncedFeedbacks = event.getFeedbacks();
		mPrefsGateway.removeFeedbacks(syncedFeedbacks);
	}
}

/**
 * Sync unsynced preferences
 */
public void syncUnsyncedFeedbacks() {
	List<Feedback> feedbacks = mPrefsGateway.getUnsyncedFeedbacks();

	if (!feedbacks.isEmpty()) {
		FeedbackSendTask feedbackSendTask = new FeedbackSendTask();
		Feedback[] feedbackArray = new Feedback[feedbacks.size()];
		feedbackArray = feedbacks.toArray(feedbackArray);
		feedbackSendTask.execute(feedbackArray);
	}
}

/**
 * Reset syncing feedbacks
 */
public void resetSyncingFeedbacks() {
	mPrefsGateway.resetSyncingFeedbacks();
}
}
