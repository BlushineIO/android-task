package io.blushine.android.validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of validators
 */
public class ValidatorGroup extends Validator {
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
	setValid(true);

	for (Validator<?> validator : mValidators) {
		if (!validator.validate()) {
			setValid(false);
		}
	}

	return isValid();
}

@Override
protected void showError(String errorMessage) {
	// Does nothing
}

/**
 * Clear all errors
 */
@Override
public void clearError() {
	for (Validator<?> validator : mValidators) {
		validator.clearError();
	}
}
}
