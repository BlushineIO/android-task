package com.spiddekauga.android.validate;

import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

/**
 * @author Matteus Magnusson <matteus.magnusson@spiddekauga.com>
 */
class ValidateRequired extends Validate {
/**
 * Uses the default message
 */
ValidateRequired() {
	this(AppActivity.getActivity().getResources().getString(R.string.validate_error_required));
}

/**
 * @param errorMessage print this message if the validation fails
 */
ValidateRequired(String errorMessage) {
	super(errorMessage);
}

@Override
protected boolean validate(TextView textView, String text) {
	return text != null && !text.isEmpty();
}
}
