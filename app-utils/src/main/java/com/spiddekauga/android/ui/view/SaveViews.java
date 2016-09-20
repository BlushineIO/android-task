package com.spiddekauga.android.ui.view;

import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.EditText;

import java.lang.reflect.Field;

/**
 * Base class for saving and restoring views easily
 */
public abstract class SaveViews {
private static final String TAG = SaveViews.class.getSimpleName();

/**
 * Save all views
 * @param outState write views to this state
 */
public void save(Bundle outState) {
	Field[] fields = getClass().getDeclaredFields();

	for (Field field : fields) {
		save(field, outState);
	}
}

/**
 * Save the specified field
 * @param field the field to save
 * @param outState save the field to this bundle
 */
private void save(Field field, Bundle outState) {
	try {
		Object fieldInstance = field.get(this);

		if (fieldInstance != null) {
			String fieldName = field.getName();

			if (fieldInstance instanceof EditText) {
				save((EditText) fieldInstance, fieldName, outState);
			} else if (fieldInstance instanceof CheckBox) {
				save((CheckBox) fieldInstance, fieldName, outState);
			} else {
				Log.w(TAG, "save() — no saving method for " + fieldName + ", class: " + field.getType().getSimpleName());
			}
		}
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	}
}

/**
 * Save edit text field
 * @param editText the edit text to save
 * @param fieldName the variable name of the field
 * @param outState save the edit text to this bundle
 */
private void save(EditText editText, String fieldName, Bundle outState) {
	outState.putString(fieldName, editText.getText().toString());
}

/**
 * Save checkbox field
 * @param checkBox the checbox to save
 * @param fieldName the variable name of the field
 * @param outState save the edit text to this bundle
 */
private void save(CheckBox checkBox, String fieldName, Bundle outState) {
	outState.putBoolean(fieldName, checkBox.isChecked());
}

/**
 * Restore all views. The views should have been set before
 * @param savedInstanceState saved instance variables
 */
public void restore(Bundle savedInstanceState) {
	if (savedInstanceState != null) {
		Field[] fields = getClass().getDeclaredFields();

		for (Field field : fields) {
			restore(field, savedInstanceState);
		}
	}
}

/**
 * Restore the specified field
 * @param field the field to restore
 * @param savedInstanceState restore the field from this bundle
 */
private void restore(Field field, Bundle savedInstanceState) {
	try {
		Object fieldInstance = field.get(this);

		if (fieldInstance != null) {
			String fieldName = field.getName();

			if (fieldInstance instanceof EditText) {
				restore((EditText) fieldInstance, fieldName, savedInstanceState);
			} else if (fieldInstance instanceof CheckBox) {
				restore((CheckBox) fieldInstance, fieldName, savedInstanceState);
			} else {
				Log.w(TAG, "save() — no saving method for " + fieldName + ", class: " + field.getType().getSimpleName());
			}
		}
	} catch (IllegalAccessException e) {
		e.printStackTrace();
	}
}

/**
 * Restore the edit text field
 * @param editText the edit text to restore
 * @param fieldName variable name of the field
 * @param savedInstanceState bundle containing the saved instance
 */
private void restore(EditText editText, String fieldName, Bundle savedInstanceState) {
	editText.setText(savedInstanceState.getString(fieldName, ""));
}

/**
 * Restore the checkbox field
 * @param checkBox the checkbox to restore
 * @param fieldName variable name of the field
 * @param savedInstanceState bundle containing the saved instance
 */
private void restore(CheckBox checkBox, String fieldName, Bundle savedInstanceState) {
	checkBox.setChecked(savedInstanceState.getBoolean(fieldName, false));
}

}
