package com.spiddekauga.android.validate;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Use this class to validate text fields. By itself this class does nothing, you have to call
 * {@link #required()} or add your own validators through {@link #addValidation(Validate)}.
 * The TextField by default calls validate() whenever the text field is changed and when focus is
 * changed from the TextField
 */
public class TextValidator implements TextWatcher, View.OnFocusChangeListener {
protected final TextView mTextView;
private List<Validate> mValidates = new ArrayList<>();
private boolean mValid = false;
private boolean mNeedsValidation = true;
private boolean mValidateOnTextChange = true;
private boolean mValidateOnLoseFocus = true;

/**
 * Create a new text validator
 * @param textView the text to validate
 */
public TextValidator(TextView textView) {
	mTextView = textView;
	mTextView.addTextChangedListener(this);
	mTextView.setOnFocusChangeListener(this);
}

/**
 * @return true if the Validator re-validates the TextView when the TextView looses focus
 */
public boolean isValidateOnLoseFocus() {
	return mValidateOnLoseFocus;
}

/**
 * Set if the TextView should be validated when the TextView looses focus. Default is true.
 * @param validateOnLoseFocus set to true if the TextView should be validated when the TextView looses focus
 */
public void setValidateOnLoseFocus(boolean validateOnLoseFocus) {
	mValidateOnLoseFocus = validateOnLoseFocus;
}

/**
 * @return true if the Validator re-validates the TextView after each text change
 */
public boolean isValidateOnTextChange() {
	return mValidateOnTextChange;
}

/**
 * Set if the TextView should be validated after each change. Default is true.
 * @param validateOnTextChange set to true if the TextView should be validated after each change
 */
public void setValidateOnTextChange(boolean validateOnTextChange) {
	mValidateOnTextChange = validateOnTextChange;
}

@Override
public final void beforeTextChanged(CharSequence s, int start, int count, int after) {
	// Does nothing
}

@Override
public final void onTextChanged(CharSequence s, int start, int before, int count) {
	if (mValidateOnTextChange) {
		validate();
	}
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
 * Set the text to be required (non-empty). Uses default_selectable error message
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

@Override
public final void onFocusChange(View view, boolean hasFocus) {
	if (!hasFocus) {
		mValid = false;
		if (mValidateOnLoseFocus) {
			validate();
		}
	}
}
}
