package com.spiddekauga.android.validate;

import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

/**
 * Makes the field setRequired
 */
class ValidateRequiredText extends Validate<TextView> {
/**
 * Uses the default setRequired message
 */
ValidateRequiredText() {
	this(AppActivity.getActivity().getResources().getString(R.string.validate_required));
}

/**
 * @param errorMessage print this message if the validation fails
 */
ValidateRequiredText(String errorMessage) {
	super(errorMessage);
}

@Override
public boolean validate(TextView field) {
	return field != null && !field.getText().toString().isEmpty();
}
}
