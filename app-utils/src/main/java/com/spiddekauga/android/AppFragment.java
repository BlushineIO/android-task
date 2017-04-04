package com.spiddekauga.android;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;
import java.util.Map;

import de.mrapp.android.dialog.MaterialDialog;

/**
 * Base class for fullscreen fragments
 */
public abstract class AppFragment extends Fragment {
@StringRes
int mBackMessage;
@StringRes
int mBackPositiveActionText = R.string.discard;
private AppFragmentHelper mFragmentHelper = new AppFragmentHelper(this);
private Map<String, View> mSaveViews = new HashMap<>();
private Map<String, Object> mArguments = new HashMap<>();
private Map<String, ArgumentRequired> mArgumentRequired = new HashMap<>();

public AppFragment() {
	onDeclareArguments();
}

/**
 * Called when argument should be declared
 */
protected void onDeclareArguments() {
	// Does nothing
}

/**
 * Declare arguments. If an argument is set as required and it's not available it will
 * generate an error.
 * @param argumentName name of the argument
 * @param required true if required, false if optional.
 */
protected void declareArgument(String argumentName, ArgumentRequired required) {
	mArgumentRequired.put(argumentName, required);
}

/**
 * Save the state of this view when this view is destroyed and restore it when it's created.
 * Also works for arguments. Be sure to call this method before {@link #onViewStateRestored(Bundle)}
 * is called.
 * @param view the view which state to save. Accepts: <ul> <li>{@link android.widget.EditText}</li>
 * </ul>
 * @param name name of the view to save it as
 * @throws IllegalArgumentException if the view class hasn't been implemented yet
 */
protected void addSaveView(View view, String name) {
	if (!(view instanceof EditText)) {
		throw new IllegalArgumentException(view.getClass().getName() + " not implemented");
	}
	
	mSaveViews.put(name, view);
}

/**
 * Show the current fragment
 */
public void show() {
	AppActivity activity = AppActivity.getActivity();
	FragmentTransaction fragmentTransaction = activity.getFragmentManager().beginTransaction();
	fragmentTransaction.replace(android.R.id.content, this);
	fragmentTransaction.addToBackStack(getClass().getSimpleName());
	fragmentTransaction.commit();
}

/**
 * Set the toolbar and statusbar colors. Colors are applied in {@link #onViewCreated(View, Bundle)}
 * so call this method before then.
 * @param toolbarColor color of the toolbar
 * @param statusbarColor color of the statusbar
 */
protected void setToolbarColor(@ColorRes int toolbarColor, @ColorRes int statusbarColor) {
	mFragmentHelper.setToolbarColor(toolbarColor, statusbarColor);
}

/**
 * Set the message when back is pressed. This will be displayed in a small dialog
 * @param message the message to display in a small dialog
 */
protected void setBackMessage(@StringRes int message) {
	mBackMessage = message;
}

/**
 * Set the message when back is pressed. This will be displayed in a small dialog
 * @param message the message to display in a small dialog
 * @param positiveActionText the positive action button text of the dialog. By default this is
 * R.string.discard
 */
protected void setBackMessage(@StringRes int message, @StringRes int positiveActionText) {
	mBackMessage = message;
	mBackPositiveActionText = positiveActionText;
}

/**
 * Display back dialog discard message if something has been changed in the fragment. If nothing has
 * been changed it simply dismisses the window.
 */
public void back() {
	if (isChanged()) {
		MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(getContext());
		dialogBuilder.setMessage(mBackMessage);
		dialogBuilder.setPositiveButton(mBackPositiveActionText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		dialogBuilder.show();
	} else {
		dismiss();
	}
}

/**
 * Automatically checks if any of the save views have been changed
 * @return true if values have been changed/edited
 */
protected boolean isChanged() {
	// Values
	for (Map.Entry<String, View> entry : mSaveViews.entrySet()) {
		String name = entry.getKey();
		View view = entry.getValue();
		Object originalValue = mArguments.get(name);
		
		if (view instanceof EditText) {
			String compareValue = "";
			if (originalValue != null) {
				compareValue = (String) originalValue;
			}
			
			if (!((EditText) view).getText().toString().equals(compareValue)) {
				return true;
			}
		}
	}
	
	return false;
}

public Context getContext() {
	if (Build.VERSION.SDK_INT >= 23) {
		return super.getContext();
	} else {
		return getActivity();
	}
}

@Override
public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	mFragmentHelper.onViewCreated(view, savedInstanceState);
}

@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	fetchArguments();
}

/**
 * Fetch Arguments
 */
private void fetchArguments() {
	Bundle arguments = getArguments();
	
	for (Map.Entry<String, ArgumentRequired> entry : mArgumentRequired.entrySet()) {
		String name = entry.getKey();
		ArgumentRequired required = entry.getValue();
		Object value = arguments.get(name);
		
		if (value != null) {
			mArguments.put(name, value);
		} else if (required == ArgumentRequired.REQUIRED) {
			throw new IllegalStateException("Required argument " + name + " not set!");
		}
	}
}

@Override
public void onViewStateRestored(Bundle savedInstanceState) {
	super.onViewStateRestored(savedInstanceState);
	
	for (Map.Entry<String, View> entry : mSaveViews.entrySet()) {
		String name = entry.getKey();
		View view = entry.getValue();
		
		if (view instanceof EditText) {
			String value = null;
			// Fetch value from saved instance
			if (savedInstanceState != null) {
				value = savedInstanceState.getString(name);
			}
			
			// Try with argument
			if (value == null) {
				value = getArgument(name);
			}
			
			if (value != null) {
				((EditText) view).setText(value);
			}
		}
	}
}

/**
 * Get the argument value
 * @param argumentName the name of the argument
 * @return original field value as string, an empty string if no original value was found
 */
@SuppressWarnings("unchecked")
protected <ReturnType> ReturnType getArgument(String argumentName) {
	Object value = mArguments.get(argumentName);
	if (value instanceof String) {
		return (ReturnType) value;
	}
	
	return null;
}

@Override
public void onResume() {
	super.onResume();
	mFragmentHelper.onResume();
}

@Override
public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	
	// Values
	for (Map.Entry<String, View> entry : mSaveViews.entrySet()) {
		String name = entry.getKey();
		View view = entry.getValue();
		
		if (view instanceof EditText) {
			outState.putString(name, ((EditText) view).getText().toString());
		}
	}
}

@Override
public void onPause() {
	super.onPause();
	mFragmentHelper.onPause();
}

@Override
public void onStop() {
	super.onStop();
	mFragmentHelper.onStop();
}

@Override
public void onDestroyView() {
	super.onDestroyView();
	mFragmentHelper.onDestroyView();
}

@Override
public void onDestroy() {
	super.onDestroy();
	mFragmentHelper.onDestroy();
}

/**
 * Dismiss this window.
 */
public void dismiss() {
	mFragmentHelper.dismiss();
}

/**
 * If an argument is required or not
 */
protected enum ArgumentRequired {
	REQUIRED,
	OPTIONAL,
}

public class BackOnClickListener implements View.OnClickListener {
	@Override
	public void onClick(View v) {
		back();
	}
}
}