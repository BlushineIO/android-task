package com.spiddekauga.android.validate;

import android.view.View;

/**
 * Common view validator mechanics for {@link android.view.View}
 */
public abstract class ViewValidator<ViewField extends View> extends Validator<ViewField> implements View.OnFocusChangeListener {
private boolean mValidateOnLoseFocus = true;

/**
 * Create a new validator
 * @param viewField the field to validate
 */
protected ViewValidator(ViewField viewField) {
	super(viewField);
}

@Override
public final void onFocusChange(View view, boolean hasFocus) {
	if (!hasFocus) {
		invalidate();
		if (mValidateOnLoseFocus) {
			validate();
		}
	}
}

/**
 * Set if the TextView should be validated when the TextView looses focus. Default is true.
 * @param validateOnLoseFocus set to true if the TextView should be validated when the TextView
 * looses focus
 */
protected void setValidateOnLoseFocus(boolean validateOnLoseFocus) {
	mValidateOnLoseFocus = validateOnLoseFocus;

	if (validateOnLoseFocus) {
		mField.setOnFocusChangeListener(this);
	} else if (mField.getOnFocusChangeListener() == this) {
		mField.setOnFocusChangeListener(null);
	}
}

protected static class Builder<ValidatorType extends ViewValidator, BuilderType extends Builder> extends Validator.Builder<ValidatorType, BuilderType> {
	protected Builder(ValidatorType validator) {
		super(validator);
	}

	/**
	 * Set if the TextView should be validated when the TextView looses focus. Default is true.
	 * @param validateOnLoseFocus set to true if the TextView should be validated when the TextView
	 * looses focus this
	 */
	@SuppressWarnings("unchecked")
	public BuilderType setValidateOnLoseFocus(boolean validateOnLoseFocus) {
		mValidator.setValidateOnLoseFocus(validateOnLoseFocus);
		return (BuilderType) this;
	}
}
}
