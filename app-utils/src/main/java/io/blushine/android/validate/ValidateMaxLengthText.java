package io.blushine.android.validate;

import android.widget.TextView;

/**
 * Validate if the text view has at max N characters
 */
class ValidateMaxLengthText extends Validate<TextView> {
private final int mMaxLength;

/**
 * @param maxLength minimum number of characters the text view should have
 */
ValidateMaxLengthText(int maxLength) {
	super("");
	mMaxLength = maxLength;
}

@Override
public boolean validate(TextView textView) {
	return textView.getText().toString().length() <= mMaxLength;
}
}
