package com.spiddekauga.android.util;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;

import com.spiddekauga.android.AppActivity;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Date;

import de.mrapp.android.preference.NumberPickerPreference;
import de.mrapp.android.util.view.AbstractSavedState;

/**
 * A data structure that saves the internal states of the {@link TimePickerDialog}.
 */
public class Time extends AbstractSavedState {
/**
 * A creator, which allows to create instances of the class {@link
 * Time} from parcels.
 */
public static final Creator<Time> CREATOR =
		new Creator<Time>() {
			
			@Override
			public Time createFromParcel(final Parcel in) {
				return new Time(in);
			}
			
			@Override
			public Time[] newArray(final int size) {
				return new Time[size];
			}
		};

private int mHour;
private int mMinute;
private int mSecond;

/**
 * Create a new time
 * @param hour the hour
 * @param minute the minute
 * @param second the second
 */
public Time(int hour, int minute, int second) {
	super((Parcelable) null);
	mHour = hour;
	mMinute = minute;
	mSecond = second;
}

/**
 * Create a new time. Will calculate the hour, minute, and second from the seconds parameter
 * @param seconds total amount of seconds
 */
public Time(int seconds) {
	super((Parcelable) null);
	int minutes = seconds / 60;
	mHour = minutes / 60;
	mMinute = minutes - mHour * 60;
	mSecond = seconds - minutes * 60;
}

/**
 * Creates a new data structure, which allows to store the internal state of an {@link
 * NumberPickerPreference}. This constructor is called by derived classes when saving their
 * states.
 * @param superState The state of the superclass of this view, as an instance of the type {@link
 * Parcelable}. The state may not be null
 * @param time the time to save
 */
public Time(@NonNull final Parcelable superState, Time time) {
	super(superState);
	mHour = time.mHour;
	mMinute = time.mMinute;
	mSecond = time.mSecond;
}

/**
 * Creates a new data structure, which allows to store the internal state of an {@link
 * NumberPickerPreference}. This constructor is used when reading from a parcel. It reads
 * the state of the superclass.
 * @param source The parcel to read read from as a instance of the class {@link Parcel}. The parcel
 * may not be null
 */
private Time(@NonNull final Parcel source) {
	super(source);
	mHour = source.readInt();
	mMinute = source.readInt();
	mSecond = source.readInt();
}

public int getHour() {
	return mHour;
}

public int getMinute() {
	return mMinute;
}

public int getSecond() {
	return mSecond;
}

@Override
public final void writeToParcel(final Parcel destination, final int flags) {
	super.writeToParcel(destination, flags);
	destination.writeInt(mHour);
	destination.writeInt(mMinute);
	destination.writeInt(mSecond);
}

@Override
public int hashCode() {
	int result = mHour;
	result = 31 * result + mMinute;
	result = 31 * result + mSecond;
	return result;
}

@Override
public boolean equals(Object o) {
	if (this == o) {
		return true;
	}
	if (o == null || getClass() != o.getClass()) {
		return false;
	}
	
	Time time = (Time) o;
	
	if (mHour != time.mHour) {
		return false;
	}
	if (mMinute != time.mMinute) {
		return false;
	}
	return mSecond == time.mSecond;
	
}

/**
 * Converts the time to the correct format for the device
 * @return time string in the correct format
 */
@Override
public String toString() {
	java.text.DateFormat dateFormat = DateFormat.getTimeFormat(AppActivity.getActivity());
	return dateFormat.format(toDate());
}

public Date toDate() {
	int minutes = mMinute + (mHour - 1) * 60;
	int seconds = mSecond + minutes * 60;
	long milliseconds = seconds * 1000;
	return new Date(milliseconds);
}
}
