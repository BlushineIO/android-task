package com.spiddekauga.android.preference;

import android.content.Context;
import android.os.Parcelable;
import android.preference.Preference;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.util.Time;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

/**
 * A preference, which allows to choose a time via two {@link de.mrapp.android.preference.view.NumberPicker}
 * widgets. The chosen time will only be persisted if confirmed by the user.
 */
public class TimePreference extends Preference implements TimePickerDialog.OnTimeSetListener {
private TimePickerDialog mTimePicker;
private Time mTime;

public TimePreference(@NonNull Context context) {
	super(context);
	initialize(null, 0, 0);
}

/**
 * Initializes the preference.
 * @param attributeSet The attribute set, the attributes should be obtained from, as an instance of
 * the type {@link AttributeSet} or null, if no attributes should be obtained
 * @param defaultStyle The default style to apply to this preference. If 0, no style will be applied
 * (beyond what is included in the theme). This may either be an attribute resource, whose value
 * will be retrieved from the current theme, or an explicit style resource
 * @param defaultStyleResource A resource identifier of a style resource that supplies default
 * values for the preference, used only if the default style is 0 or can not be found in the theme.
 * Can be 0 to not look for defaults
 */
private void initialize(@Nullable AttributeSet attributeSet, @AttrRes final int defaultStyle, @StyleRes final int defaultStyleResource) {
//	setOnPreferenceClickListener(this);
}

public TimePreference(@NonNull Context context, @Nullable AttributeSet attributeSet) {
	super(context, attributeSet);
	initialize(attributeSet, 0, 0);
	
}

public TimePreference(@NonNull Context context, @Nullable AttributeSet attributeSet, @AttrRes int defaultStyle) {
	super(context, attributeSet, defaultStyle);
	initialize(attributeSet, defaultStyle, 0);
}

/**
 * Obtains all attributes from a specific attribute set.
 * @param attributeSet The attribute set, the attributes should be obtained from, as an instance of
 * the type {@link AttributeSet} or null, if no attributes should be obtained
 * @param defaultStyle The default style to apply to this preference. If 0, no style will be applied
 * (beyond what is included in the theme). This may either be an attribute resource, whose value
 * will be retrieved from the current theme, or an explicit style resource
 * @param defaultStyleResource A resource identifier of a style resource that supplies default
 * values for the preference, used only if the default style is 0 or can not be found in the theme.
 * Can be 0 to not look for defaults
 */
private void obtainStyledAttributes(@Nullable final AttributeSet attributeSet, @AttrRes final int defaultStyle, @StyleRes final int defaultStyleResource) {
	
}

@Override
public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
	second = 0;
	mTime = new Time(hourOfDay, minute, second);
	
	int minutes = minute + hourOfDay * 60;
	int seconds = second + minutes * 60;
	persistInt(seconds);
	notifyChanged();
	updateSummary();
}

private void updateSummary() {
	setSummary(mTime.toString());
}

@Override
protected void onClick() {
	mTimePicker = TimePickerDialog.newInstance(this, mTime.getHour(), mTime.getMinute(), mTime.getSecond(), DateFormat.is24HourFormat(AppActivity.getActivity()));
	mTimePicker.vibrate(false);
	mTimePicker.show(AppActivity.getActivity().getFragmentManager(), TimePreference.class.getSimpleName());
}

@Override
protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
	super.onSetInitialValue(restorePersistedValue, defaultValue);
	int defaultInt = defaultValue instanceof Integer ? (int) defaultValue : 0;
	int seconds = getPersistedInt(defaultInt);
	mTime = new Time(seconds);
	updateSummary();
}

@Override
protected Parcelable onSaveInstanceState() {
	Parcelable superState = super.onSaveInstanceState();
	return new Time(superState, mTime);
}

@Override
protected void onRestoreInstanceState(Parcelable state) {
	if (state != null && state instanceof Time) {
		mTime = (Time) state;
		super.onRestoreInstanceState(mTime.getSuperState());
	} else {
		super.onRestoreInstanceState(state);
	}
}
	
}
