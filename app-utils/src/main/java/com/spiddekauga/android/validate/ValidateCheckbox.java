package com.spiddekauga.android.validate;

import android.widget.CheckBox;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

/**
 * Check if a checkbox is checked/unchecked
 */
public class ValidateCheckbox extends Validate<CheckBox> {
private static final String ERROR_CHECKED = AppActivity.getActivity().getResources().getString(R.string.validate_checkbox_not_checked);
private static final String ERROR_UNCHECKED = AppActivity.getActivity().getResources().getString(R.string.validate_checkbox_not_unchecked);
private boolean mShouldBeChecked;

/**
 * @param checked set to true if the checkbox should be checked to pass the validation
 * @param errorMessage the error message to show when the validation fails. If null a default
 * message will be used
 */
public ValidateCheckbox(boolean checked, String errorMessage) {
	super(getErrorMessage(checked, errorMessage));
	mShouldBeChecked = checked;
}

/**
 * Get the default error message if there is no error message
 * @param checked true if the checkbox should be checked to be valid
 * @param errorMessage the error message to show, if null will use default
 */
private static String getErrorMessage(boolean checked, String errorMessage) {
	if (errorMessage != null) {
		return errorMessage;
	} else {
		return checked ? ERROR_CHECKED : ERROR_UNCHECKED;
	}
}

@Override
public boolean validate(CheckBox checkBox) {
	return checkBox != null && checkBox.isChecked() == mShouldBeChecked;
}
}
