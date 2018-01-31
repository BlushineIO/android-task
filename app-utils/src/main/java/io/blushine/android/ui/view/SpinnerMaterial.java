package io.blushine.android.ui.view;


import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Calendar;

import io.blushine.android.R;
import io.blushine.android.ui.ColorHelper;

/**
 * Material-like spinner
 */
public class SpinnerMaterial extends AppCompatAutoCompleteTextView implements AdapterView.OnItemClickListener {

private static final int MAX_CLICK_DURATION = 200;
private long startClickTime;
private boolean isPopup;
private int mPosition = ListView.INVALID_POSITION;

public SpinnerMaterial(Context context) {
	super(context);
	setOnItemClickListener(this);
}

public SpinnerMaterial(Context arg0, AttributeSet arg1) {
	super(arg0, arg1);
	setOnItemClickListener(this);
}

public SpinnerMaterial(Context arg0, AttributeSet arg1, int arg2) {
	super(arg0, arg1, arg2);
	setOnItemClickListener(this);
}

@Override
public boolean enoughToFilter() {
	return true;
}

@Override
protected void onFocusChanged(boolean focused, int direction,
							  Rect previouslyFocusedRect) {
	super.onFocusChanged(focused, direction, previouslyFocusedRect);
	if (focused) {
		performFiltering("", 0);
		InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getWindowToken(), 0);
		setKeyListener(null);
		dismissDropDown();
	} else {
		isPopup = false;
	}
}

@Override
public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
	mPosition = position;
	isPopup = false;
}

@Override
public void setCompoundDrawablesWithIntrinsicBounds(Drawable left, Drawable top, Drawable right, Drawable bottom) {
	Drawable dropdownIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_drop_down_18dp);
	int iconColor = ColorHelper.getColor(getResources(), R.color.icon_black, null);
	dropdownIcon.setColorFilter(iconColor, PorterDuff.Mode.SRC_IN);
	super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom);
}

@Override
public boolean onTouchEvent(MotionEvent event) {
	if (!isEnabled()) {
		return false;
	}

	switch (event.getAction()) {
	case MotionEvent.ACTION_DOWN: {
		startClickTime = Calendar.getInstance().getTimeInMillis();
		break;
	}
	case MotionEvent.ACTION_UP: {
		long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
		if (clickDuration < MAX_CLICK_DURATION) {
			if (isPopup) {
				dismissDropDown();
				isPopup = false;
			} else {
				requestFocus();
				showDropDown();
				isPopup = true;
			}
		}
	}
	}

	return super.onTouchEvent(event);
}

public int getPosition() {
	return mPosition;
}
}