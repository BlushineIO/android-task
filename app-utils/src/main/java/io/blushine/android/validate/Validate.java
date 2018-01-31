package io.blushine.android.validate;

/**
 * Base class for validating that a field contains a valid value
 * @param <Field> field class to validate
 */
public abstract class Validate<Field> {
private String mErrorMessage;

/**
 * @param errorMessage print this message if the validation fails
 */
protected Validate(String errorMessage) {
	mErrorMessage = errorMessage;
}

/**
 * Validate the field
 * @param field the field to validate
 * @return true if validation was successful, false if an error occurred
 */
public abstract boolean validate(Field field);

/**
 * @return error message to print if the validation fails
 */
public String getErrorMessage() {
	return mErrorMessage;
}
}
