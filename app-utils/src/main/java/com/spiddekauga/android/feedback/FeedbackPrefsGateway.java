package com.spiddekauga.android.feedback;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.api.client.util.DateTime;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.spiddekauga.android.AppActivity;
import com.spiddekauga.cloudshine.feedbackApi.model.Feedback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gateway for storing Feedback in preferences
 */
class FeedbackPrefsGateway {
private static final String FEEDBACKS_FIELD = "feedbacks";
private final SharedPreferences mPreferences;
private final Gson mGson = new Gson();

FeedbackPrefsGateway() {
	mPreferences = PreferenceManager.getDefaultSharedPreferences(AppActivity.getActivity());
}

/**
 * Add a menu_feedback
 * @param feedback the menu_feedback to add
 */
void addFeedback(Feedback feedback) {
	List<Feedback> allFeedbacks = getAllFeedbacks();
	allFeedbacks.add(feedback);
	updateFeedacks(allFeedbacks);
}

private List<Feedback> getAllFeedbacks() {
	String jsonFeedback = mPreferences.getString(FEEDBACKS_FIELD, null);
	if (jsonFeedback == null) {
		return new ArrayList<>();
	} else {
		Type type = new TypeToken<List<Feedback>>() {
		}.getType();
		return mGson.fromJson(jsonFeedback, type);
	}
}

private void updateFeedacks(List<Feedback> feedbacks) {
	String jsonFeedback = mGson.toJson(feedbacks);
	SharedPreferences.Editor editor = mPreferences.edit();
	editor.putString(FEEDBACKS_FIELD, jsonFeedback);
	editor.apply();
}

/**
 * Remove all specified feedbacks.
 * @param toBeRemoved the feedbacks.
 */
void removeFeedbacks(List<Feedback> toBeRemoved) {
	Map<DateTime, Feedback> allFeedbacks = new HashMap<>();
	for (Feedback feedback : getAllFeedbacks()) {
		allFeedbacks.put(feedback.getDate(), feedback);
	}

	for (Feedback removeFeedback : toBeRemoved) {
		allFeedbacks.remove(removeFeedback.getDate());
	}

	List<Feedback> updatedFeedbacks = new ArrayList<>();
	updatedFeedbacks.addAll(allFeedbacks.values());
	updateFeedacks(updatedFeedbacks);
}

/**
 * Get all feedbacks that haven't been sent
 * @return all feedbacks that haven't been sent or aren't sending at the moment
 */
List<Feedback> getUnsyncedFeedbacks() {
	List<Feedback> unsynced = new ArrayList<>();
	List<Feedback> allFeedbacks = getAllFeedbacks();

	for (Feedback feedback : allFeedbacks) {
		if (!feedback.getSyncing()) {
			unsynced.add(feedback);
		}
	}

	return unsynced;
}

/**
 * Set all feedbacks as not syncing
 */
void resetSyncingFeedbacks() {
	List<Feedback> feedbacks = getAllFeedbacks();

	if (!feedbacks.isEmpty()) {
		for (Feedback feedback : feedbacks) {
			feedback.setSyncing(false);
		}

		updateFeedacks(feedbacks);
	}
}
}
