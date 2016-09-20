package com.spiddekauga.android.validate;

import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Use this class to validate checkboxes.
 */
public class CheckboxValidator extends ViewValidator<CheckBox> {
private TextView mErrorView;
private boolean mValidateOnClick = false;

/**
 * Create a new validator. Uses the default error message
 * @param checkBox the field to validate
 * @param errorView the view to show the error in
 */
private CheckboxValidator(CheckBox checkBox, TextView errorView) {
	super(checkBox);
	mErrorView = errorView;
}

@Override
protected void showError(String errorMessage) {
	mErrorView.setError(errorMessage);
}

@Override
public void clearError() {
	mErrorView.setError(null);
}

/**
 * Builder class for checkbox validator
 */
public static class Builder extends ViewValidator.Builder<CheckboxValidator, Builder> {
	private boolean mChecked = true;

	/**
	 * Builder with the setRequired arguments.
	 * @param checkBox the field to validate
	 * @param errorView the view to show the error in
	 */
	public Builder(CheckBox checkBox, TextView errorView) {
		super(new CheckboxValidator(checkBox, errorView));
	}

	/**
	 * Set if the checkbox should be checked or unchecked to be valid
	 * @param checked set to true if the checkbox should be checked to be valid. Default: true
	 */
	public Builder setValidState(boolean checked) {
		mChecked = checked;
		return this;
	}

	public CheckboxValidator build() {
		mValidator.addValidation(new ValidateCheckbox(mChecked));
		return super.build();
	}
}
}