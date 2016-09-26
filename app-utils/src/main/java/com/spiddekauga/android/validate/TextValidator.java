package com.spiddekauga.android.validate;

import android.content.res.Resources;
import android.support.design.widget.CheckableImageButton;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

/**
 * Use this class to validate text fields.
 */
public class TextValidator extends Validator<TextView> implements TextWatcher {
private TextInputLayout mTextInputLayout = null;

/**
 * Create a new text validator
 * @param textView the text to validate
 */
protected TextValidator(TextView textView) {
	super(textView);

	ViewParent parent = textView.getParent().getParent();
	if (parent instanceof TextInputLayout) {
		mTextInputLayout = (TextInputLayout) parent;

		// Fix bottom padding on passwords image
		FrameLayout frameLayout = (FrameLayout) mTextInputLayout.getChildAt(0);
		if (frameLayout.getChildCount() == 2) {
			frameLayout.setClipChildren(true);
			Resources resources = AppActivity.getActivity().getResources();
			int height = resources.getDimensionPixelSize(R.dimen.edit_inner_height);
			CheckableImageButton image = (CheckableImageButton) frameLayout.getChildAt(1);
			FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) image.getLayoutParams();
			layoutParams.height = height;
			image.setLayoutParams(layoutParams);
		}
	}
}

@Override
protected void showError(String errorMessage) {
	if (mTextInputLayout != null) {
		mTextInputLayout.setError(errorMessage);
	} else {
		mField.setError(errorMessage);
	}
}

@Override
public void clearError() {
	if (mTextInputLayout != null) {
		mTextInputLayout.setError(null);
		mTextInputLayout.setErrorEnabled(false);
	} else {
		mField.setError(null);
	}
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
	// Does nothing
}

@Override
public final void afterTextChanged(Editable s) {
	validate();
}

/**
 * Builder for building {@link TextValidator}
 */
public static class Builder extends Validator.Builder<TextValidator, Builder> {
	private boolean mValidateOnTextChange = true;

	/**
	 * Required parameters for creating a text validator
	 * @param textView the text view to validate
	 */
	public Builder(TextView textView) {
		super(new TextValidator(textView));
	}

	/**
	 * Won't validate after each text change
	 */
	public Builder setSkipValidateOnTextChange() {
		mValidateOnTextChange = false;
		return this;
	}

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

	/**
	 * Set the minimum length of the text.
	 * @param minLength minimum length of the text.
	 */
	public Builder setMinLength(int minLength) {
		addValidation(new ValidateMinLengthText(minLength, null));
		return this;
	}

	/**
	 * Set the minimum length of the text.
	 * @param minLength minimum length of the text.
	 * @param errorMessage the error message to display. If the message has an '#' it will be
	 * replaced with minLength.
	 */
	public Builder setMinLength(int minLength, String errorMessage) {
		addValidation(new ValidateMinLengthText(minLength, errorMessage));
		return this;
	}

	@Override
	public TextValidator build() {
		if (mValidateOnTextChange) {
			mValidator.setValidateOnTextChange();
		}
		return super.build();
	}
}
}
