package io.blushine.android.validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for validating fields and displaying an error if the field doesn't pass a validation.
 * By itself this class does nothing, you have to add validators through {@link
 * #addValidation(Validate)}.
 * @param <Field> the field to validate
 */
public abstract class Validator<Field> {
protected Field mField = null;
private boolean mValid = false;
private List<Validate<Field>> mValidates = new ArrayList<>();

/**
 * Create empty validator
 */
protected Validator() {
}

/**
 * Create a new validator
 * @param field the field to validate
 */
protected Validator(Field field) {
	mField = field;
}

/**
 * Validate the text validator
 * @return true if all validation passed, false if one or more failed
 */
public boolean validate() {
	mValid = true;

	for (Validate<Field> validate : mValidates) {
		if (!validate.validate(mField)) {
			showError(validate.getErrorMessage());
			mValid = false;
			break;
		}
	}

	if (mValid) {
		clearError();
	}

	return mValid;
}

/**
 * Show the error message
 * @param errorMessage the error message to show
 */
protected abstract void showError(String errorMessage);

/**
 * Clear error
 */
public abstract void clearError();

/**
 * @return true if all validation passed
 */
public boolean isValid() {
	return mValid;
}

/**
 * Make the validator valid. Only use this method if you override {@link #validate()}.
 * @param valid if the validator is valid or not
 */
protected void setValid(boolean valid) {
	mValid = valid;
}

/**
 * Invalidate this field, i.e. it should be re-validated again.
 */
public void invalidate() {
	mValid = false;
}

/**
 * Add custom validation
 * @param validate the validator to be called
 */
protected void addValidation(Validate<Field> validate) {
	mValidates.add(validate);
}

protected static class Builder<ValidatorType extends Validator, BuilderType extends Builder> {
	protected ValidatorType mValidator;

	protected Builder(ValidatorType validator) {
		mValidator = validator;
	}

	/**
	 * Add custom validation
	 * @param validate the validator to be called
	 */
	@SuppressWarnings("unchecked")
	public BuilderType addValidation(Validate<?> validate) {
		mValidator.addValidation(validate);
		return (BuilderType) this;
	}

	/**
	 * Builds the the validator
	 */
	public ValidatorType build() {
		return mValidator;
	}
}
}
