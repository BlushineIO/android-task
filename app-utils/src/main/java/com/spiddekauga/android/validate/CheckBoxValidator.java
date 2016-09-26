package com.spiddekauga.android.validate;

import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;
import com.spiddekauga.android.ui.ColorHelper;

/**
 * Use this class to validate checkboxes.
 */
public class CheckBoxValidator extends Validator<CheckBox> {
private static final ColorStateList mErrorTint = ColorHelper.getColorStateList(AppActivity.getActivity().getResources(), R.color.default_error_color, null);
@ColorInt
private static final int ERROR_COLOR = ColorHelper.getColor(AppActivity.getActivity().getResources(), R.color.default_error_color, null);
private TextView mErrorView = null;
private ColorStateList mDefaultTint;
private ColorFilter mDefaultFilter;
private Drawable mButtonDrawable;
private Paint mErrorFilter;

/**
 * Create a new validator. Uses the default error message
 * @param checkBox the field to validate
 */
private CheckBoxValidator(CheckBox checkBox) {
	super(checkBox);
	mButtonDrawable = mField.getButtonDrawable();
	if (mButtonDrawable != null) {
		mDefaultFilter = mButtonDrawable.getColorFilter();
	}

//	mDefaultTint = CompoundButtonCompat.getButtonTintList(checkBox);
}

@Override
protected void showError(String errorMessage) {
	// Change checkbox color
	if (mButtonDrawable != null) {
		mButtonDrawable.setColorFilter(ERROR_COLOR, PorterDuff.Mode.SRC_ATOP);
	}

	// Set error message
	if (mErrorView != null && !errorMessage.isEmpty()) {
		mErrorView.setText(errorMessage);
		mErrorView.setVisibility(View.VISIBLE);
	}
}

@Override
public void clearError() {
	// Change checkbox color
	if (mButtonDrawable != null) {
		mButtonDrawable.setColorFilter(mDefaultFilter);
	}

	// Remove error view
	if (mErrorView != null) {
		mErrorView.setVisibility(View.GONE);
	}
}

private void setValidateOnClick() {
	mField.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			validate();
		}
	});
}

/**
 * Builder class for checkbox validator
 */
public static class Builder extends Validator.Builder<CheckBoxValidator, Builder> {
	private boolean mChecked = true;
	private String mErrorMessage = null;

	/**
	 * Builder with the setRequired arguments.
	 * @param checkBox the field to validate
	 */
	public Builder(CheckBox checkBox) {
		super(new CheckBoxValidator(checkBox));
	}

	/**
	 * Set if the checkbox should be checked or unchecked to be valid
	 * @param checked set to true if the checkbox should be checked to be valid. Default: true
	 */
	public Builder setValidState(boolean checked) {
		mChecked = checked;
		return this;
	}

	/**
	 * Set the error view to display an optional error message. If no error message has been set
	 * through {@link #setErrorMessage(String)} a default message will be used.
	 * @param errorView the view to display an error message in
	 */
	public Builder setErrorView(TextView errorView) {
		mValidator.mErrorView = errorView;
		return this;
	}

	/**
	 * Set the error message. To display the error message you also have to set an error view
	 * through {@link #setErrorView(TextView)}.
	 * @param errorMessage the error message to be shown if the checkbox isn't checked/unchecked
	 */
	public Builder setErrorMessage(String errorMessage) {
		mErrorMessage = errorMessage;
		return this;
	}

	/**
	 * Set if the checkbox should be validated when it's been clicked. Note that only one {@link
	 * android.view.View.OnClickListener} can be attached to a checkbox. Thus if you want it to be
	 * validated when you click the checkbox and have a custom {@link android.view.View.OnClickListener}
	 * you can accomplish this by calling {@link Validator#validate()} in your {@link
	 * android.view.View.OnClickListener}
	 */
	public Builder setValidateOnClick() {
		mValidator.setValidateOnClick();
		return this;
	}

	public CheckBoxValidator build() {
		mValidator.addValidation(new ValidateCheckbox(mChecked, mErrorMessage));
		return super.build();
	}
}
}