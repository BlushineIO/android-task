package com.spiddekauga.android.validate;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Matteus Magnusson <matteus.magnusson@spiddekauga.com>
 */
public class TextValidator implements TextWatcher {
protected final TextView mTextView;
private List<Validate> mValidates = new ArrayList<>();
private boolean mValid = false;

/**
 * Create a new text validator
 * @param textView the text to validate
 */
public TextValidator(TextView textView) {
	mTextView = textView;
	mTextView.addTextChangedListener(this);
}

@Override
public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
	// Does nothing
}

@Override
public final void onTextChanged(CharSequence s, int start, int before, int count) {
	validate();
}

@Override
public final void afterTextChanged(Editable s) {
	mValid = false;
}

/**
 * Validate the text validator
 * @return true if all validation passed, false if one or more failed
 */
public boolean validate() {
	mValid = true;

	for (Validate validate : mValidates) {
		if (!validate.validate(mTextView, mTextView.getText().toString())) {
			mTextView.setError(validate.getErrorMessage());
			mValid = false;
			break;
		}
	}

	return mValid;
}

/**
 * @return true if all validation passed
 */
public boolean isValid() {
	return mValid;
}

/**
 * Set the text to be required (non-empty). Uses default error message
 */
public void required() {
	addValidation(new ValidateRequired());
}

/**
 * Add custom validation
 * @param validate the validator to be called
 */
public void addValidation(Validate validate) {
	if (validate != null) {
		mValidates.add(validate);
	}
}

/**
 * Set the text to be required (non-empty)
 * @param errorMessage what to display if it's empty
 */
public void required(String errorMessage) {
	addValidation(new ValidateRequired(errorMessage));
}
}
