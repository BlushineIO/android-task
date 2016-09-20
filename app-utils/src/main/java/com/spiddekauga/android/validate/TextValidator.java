package com.spiddekauga.android.validate;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Use this class to validate text fields.
 */
public class TextValidator extends ViewValidator<TextView> implements TextWatcher {

/**
 * Create a new text validator
 * @param textView the text to validate
 */
private TextValidator(TextView textView) {
	super(textView);
}

@Override
protected void showError(String errorMessage) {
	mField.setError(errorMessage);
}

@Override
public void clearError() {
	mField.setError(null);
}

/**
 * Will validate after each new or removed character is typed.
 */
protected void setValidateOnTextChange() {
	mField.addTextChangedListener(this);
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
	invalidate();
}

/**
 * Builder for building {@link TextValidator}
 */
public static class Builder extends ViewValidator.Builder<TextValidator, Builder> {
	/**
	 * Required parameters for creating a text validator
	 * @param textView the text view to validate
	 */
	public Builder(TextView textView) {
		super(new TextValidator(textView));
		setValidateOnLoseFocus(true);
	}

//	/**
//	 * Will validate after each new or removed character is typed.
//	 */
//	public Builder setValidateOnTextChange() {
//		mValidator.setValidateOnTextChange();
//
//		return this;
//	}

	/**
	 * Set the text field as setRequired
	 */
	public Builder setRequired() {
		addValidation(new ValidateRequiredText());
		return this;
	}

	/**
	 * Set the text to be setRequired (non-empty)
	 * @param errorMessage what to display if it's empty
	 */
	public Builder setRequired(String errorMessage) {
		addValidation(new ValidateRequiredText(errorMessage));
		return this;
	}
}
}
