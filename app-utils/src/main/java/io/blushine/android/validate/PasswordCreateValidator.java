package io.blushine.android.validate;

import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import io.blushine.android.AppActivity;
import io.blushine.android.R;

/**
 * Validates passwords when creating them. Will have a confirm password validation
 */
public class PasswordCreateValidator extends ValidatorGroup {
private static final int MIN_LENGTH_DEFAULT = AppActivity.getActivity().getResources().getInteger(R.integer.password_length_min_default);
private TextValidator mPasswordValidator;
private TextValidator mConfirmPasswordValidator;
private TextView mPasswordView;
private TextView mConfirmPasswordView;
private boolean mConfirmHasValidated = false;
private int mMinLength = MIN_LENGTH_DEFAULT;
private String mMinLengthErrorMessage = null;

/**
 * Create a new validator
 * @param passwordView the view that contains the password
 * @param confirmPasswordView view that should confirm that the passwords match
 */
protected PasswordCreateValidator(TextView passwordView, TextView confirmPasswordView) {
	mPasswordView = passwordView;
	mConfirmPasswordView = confirmPasswordView;

	createValidators();
}

/**
 * Create inner validators for password and confirm password
 */
private void createValidators() {
	Resources resources = AppActivity.getActivity().getResources();

	mPasswordValidator = new TextValidator.Builder(mPasswordView)
			.setRequired()
			.setMinLength(mMinLength, mMinLengthErrorMessage)
			.build();

	mConfirmPasswordValidator = new TextValidator.Builder(mConfirmPasswordView)
			.addValidation(new Validate<TextView>(resources.getString(R.string.validate_password_mismatch)) {
				@Override
				public boolean validate(TextView editText) {
					mConfirmHasValidated = true;

					// Skip when both fields are empty
					if (mPasswordView.getText().toString().isEmpty() && mConfirmPasswordView.getText().toString().isEmpty()) {
						return true;
					} else {
						return mPasswordView.getText().toString().equals(editText.getText().toString());
					}
				}
			})
			.setRequired()
			.build();


	// Listen to password changes for validate confirm password field
	mPasswordView.addTextChangedListener(new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		@Override
		public void afterTextChanged(Editable s) {
			// Only listen to changes if the confirm password field isn't empty
			if (!mConfirmPasswordView.getText().toString().isEmpty()) {
				mConfirmPasswordValidator.validate();
			} else if (mConfirmHasValidated) {
				mConfirmPasswordValidator.validate();
			}
		}
	});

	add(mPasswordValidator);
	add(mConfirmPasswordValidator);
}

/**
 * Builder for building {@link PasswordCreateValidator}
 */
public static class Builder extends Validator.Builder<PasswordCreateValidator, Builder> {
	/**
	 * Required fields for a password validator
	 * @param passwordView the view that contains the password
	 * @param confirmPasswordView view that should confirm that the passwords match
	 */
	public Builder(TextView passwordView, TextView confirmPasswordView) {
		super(new PasswordCreateValidator(passwordView, confirmPasswordView));
	}

	/**
	 * Set the minimum length of the password. Default is password_length_min_default. Uses the
	 * default error message.
	 * @param minLength minimum length of the password. Default is password_length_min_default
	 */
	public Builder setMinLength(int minLength) {
		mValidator.mMinLength = minLength;
		return this;
	}


	/**
	 * Set the minimum length of the password. Default is password_length_min_default
	 * @param minLength minimum length of the password. Default is password_length_min_default
	 * @param errorMessage the error message to display. If the message has an '#' it will be
	 * replaced with minLength.
	 */
	public Builder setMinLength(int minLength, String errorMessage) {
		mValidator.mMinLength = minLength;
		mValidator.mMinLengthErrorMessage = errorMessage;
		return this;
	}
}
}
