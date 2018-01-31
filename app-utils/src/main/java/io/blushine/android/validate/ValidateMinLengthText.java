package io.blushine.android.validate;

import android.widget.TextView;

import io.blushine.android.AppActivity;
import io.blushine.android.R;

/**
 * Validate if the text view has at least N characters
 */
class ValidateMinLengthText extends Validate<TextView> {
private static final String ERROR_MESSAGE_DEFAULT = AppActivity.getActivity().getResources().getString(R.string.validate_min_length);
private final int mMinLength;

/**
 * @param minLength minimum number of characters the text view should have
 * @param errorMessage print this message if the validation fails. If null default error message is
 * used. If the character '#' is in the error message string this will be replace with minLength.
 */
ValidateMinLengthText(int minLength, String errorMessage) {
	super(getErrorMessage(minLength, errorMessage));
	mMinLength = minLength;
}

private static String getErrorMessage(int minLength, String errorMessage) {
	String messageToUse;
	if (errorMessage != null) {
		messageToUse = errorMessage;
	} else {
		messageToUse = ERROR_MESSAGE_DEFAULT;
	}
	return messageToUse.replace("#", Integer.toString(minLength));
}

@Override
public boolean validate(TextView textView) {
	return textView.getText().toString().length() >= mMinLength;
}
}
