package com.spiddekauga.android.validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of validators
 */
public class ValidatorGroup {
private List<Validator<?>> mValidators = new ArrayList<>();

/**
 * Add another text validator
 * @param validator the validator to add to this group
 */
public void add(Validator<?> validator) {
	if (validator != null) {
		mValidators.add(validator);
	}
}

/**
 * Validate the entire group
 * @return true if the entire group is valid
 */
public boolean validate() {
	boolean valid = true;

	for (Validator<?> validator : mValidators) {
		if (!validator.validate()) {
			valid = false;
		}
	}

	return valid;
}

/**
 * Clear all errors
 */
public void clearErrors() {
	for (Validator<?> validator : mValidators) {
		validator.clearError();
	}
}

/**
 * @return true if all text views are valid
 */
public boolean isValid() {
	for (Validator<?> validator : mValidators) {
		if (!validator.isValid()) {
			return false;
		}
	}

	return true;
}
}
