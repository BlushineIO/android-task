package io.blushine.android.feedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.blushine.android.task.ResponseEvent;
import io.blushine.cloudshine.feedbackApi.model.Feedback;

/**
 * Event Response after sending menu_feedback to the server
 */
public class FeedbackResponseEvent extends ResponseEvent {
private List<Feedback> mFeedbacks = new ArrayList<>();

FeedbackResponseEvent(Feedback... feedbacks) {
	mFeedbacks.addAll(Arrays.asList(feedbacks));
}

FeedbackResponseEvent(List<Feedback> feedbacks) {
	mFeedbacks.addAll(feedbacks);
}

public List<Feedback> getFeedbacks() {
	return mFeedbacks;
}
}
