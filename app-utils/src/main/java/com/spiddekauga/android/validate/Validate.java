package com.spiddekauga.android.validate;

import android.widget.TextView;

/**
 * Base class for various validators
 */
public abstract class Validate {
private String mErrorMessage;

/**
 * @param errorMessage print this message if the validation fails
 */
protected Validate(String errorMessage) {
	mErrorMessage = errorMessage;
}

/**
 * Validate the text
 * @param textView the text view to validate
 * @param text the text within the textView to validate
 * @return true if validation was successful, false if an error occurred
 */
protected abstract boolean validate(TextView textView, String text);

/**
 * @return error message to print if the validation fails
 */
String getErrorMessage() {
	return mErrorMessage;
}
}
