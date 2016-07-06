package com.spiddekauga.android.feedback;

import com.spiddekauga.android.task.ResponseEvent;
import com.spiddekauga.cloudshine.feedbackApi.model.Feedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Event Response after sending feedback to the server
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
