package com.spiddekauga.android.validate;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of text validators
 */
public class TextValidatorGroup {
private List<TextValidator> mTextValidators = new ArrayList<>();

/**
 * Add another text validator
 * @param textValidator the text validator to add to this group
 */
public void add(TextValidator textValidator) {
	if (textValidator != null) {
		mTextValidators.add(textValidator);
	}
}

/**
 * Validate the entire group
 * @return true if the entire group is valid
 */
public boolean validate() {
	boolean valid = true;

	for (TextValidator textValidator : mTextValidators) {
		if (!textValidator.validate()) {
			valid = false;
		}
	}

	return valid;
}

/**
 * @return true if all text views are valid
 */
public boolean isValid() {
	for (TextValidator textValidator : mTextValidators) {
		if (!textValidator.isValid()) {
			return false;
		}
	}

	return true;
}
}
