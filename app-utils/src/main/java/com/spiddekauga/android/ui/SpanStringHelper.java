package com.spiddekauga.android.ui;

import android.support.annotation.StringRes;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ClickableSpan;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.BuildConfig;
import com.spiddekauga.utils.Strings;

/**
 * Creates a clickable SpannableString from a resource. Automatically determines where the clickable
 * part from the resource. The resource should contain the clickable part between ||clickable text||
 * i.e. between the pipes.
 */
public class SpanStringHelper {
private static final String DELIMITER = "\\|\\|";

/**
 * Creates a clickable SpannableString from a resource. Automatically determines where the clickable
 * part from the resource. The resource should contain the clickable part between ||clickable text||
 * i.e. between the pipes.
 * @param stringId the string to create the spannable text from
 * @param clickableListeners array of all clickable spans listeners, should match the number of
 * clickable lists
 */
public static SpannableString createSpannableString(@StringRes int stringId, ClickableSpan... clickableListeners) {
	String text = AppActivity.getActivity().getResources().getString(stringId);
	String[] spans = text.split(DELIMITER, -1);
	
	// Check so listeners matches the spans
	// It should be clickableListener * 2 + 1
	if (BuildConfig.DEBUG) {
		if (spans.length != (clickableListeners.length * 2) + 1) {
			throw new IllegalArgumentException("Text \"" + text + "\" does not contain " + clickableListeners.length + " clickable parts");
		}
	}
	
	String displayText = Strings.merge(spans);
	SpannableString spannableString = new SpannableString(displayText);

	// Bind each clickable state
	int totalLength = 0;
	for (int i = 0; i < spans.length - 1; i += 2) {
		int beginIndex = totalLength + spans[i].length();
		int length = spans[i + 1].length();
		int endIndex = beginIndex + length;

		spannableString.setSpan(clickableListeners[i / 2], beginIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		totalLength = endIndex;
	}

	return spannableString;
}
}
