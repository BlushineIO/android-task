package com.spiddekauga.android.ui;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Instantly auto-complete whenever the field is focus, even when nothing has been written.
 */
public class InstantAutoComplete extends AutoCompleteTextView {

public InstantAutoComplete(Context context) {
	this(context, null);
}

public InstantAutoComplete(Context context, AttributeSet attrs) {
	super(context, attrs);
}

public InstantAutoComplete(Context context, AttributeSet attrs, int defStyleAttr) {
	super(context, attrs, defStyleAttr);
}

public InstantAutoComplete(
		Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
	super(context, attrs, defStyleAttr, defStyleRes);
}

@Override
public boolean enoughToFilter() {
	return true;
}

@Override
public void onFilterComplete(int count) {
	if (isFocused()) {
		showDropDown();
	}
}

@Override
protected void onFocusChanged(boolean focused, int direction,
							  Rect previouslyFocusedRect) {
	super.onFocusChanged(focused, direction, previouslyFocusedRect);
	if (focused) {
		performFiltering(getText(), 0);
	}
}

}