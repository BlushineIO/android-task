package com.spiddekauga.android.validate;

import android.widget.CheckBox;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

/**
 * Check if a checkbox is checked/unchecked
 */
public class ValidateCheckbox extends Validate<CheckBox> {
private static final String ERROR_CHECKED = AppActivity.getActivity().getResources().getString(R.string.validate_error_checked);
private static final String ERROR_UNCHECKED = AppActivity.getActivity().getResources().getString(R.string.validate_error_unchecked);
private boolean mShouldBeChecked;

/**
 * Uses the default setRequired message
 * @param checked set to true if the checkbox should be checked to pass the validation
 */
public ValidateCheckbox(boolean checked) {
	this(checked, null);
}

/**
 * @param checked set to true if the checkbox should be checked to pass the validation
 * @param errorMessage print this message if the validation fails
 */
public ValidateCheckbox(boolean checked, String errorMessage) {
	super(errorMessage != null ? errorMessage : getDefaultErrorMessage(checked));
	mShouldBeChecked = checked;
}

public static String getDefaultErrorMessage(boolean checked) {
	return checked ? ERROR_CHECKED : ERROR_UNCHECKED;
}

@Override
public boolean validate(CheckBox checkBox) {
	return checkBox != null && checkBox.isChecked() == mShouldBeChecked;
}
}
