package com.spiddekauga.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all fragments. If you want to make a fullscreen fragment, use {@link AppFragment
 * instead}. This class is mostly used for fragments inside fragments.
 */
public abstract class Fragment extends android.app.Fragment {
protected View mView;
private Map<String, Object> mArguments = new HashMap<>();
private Map<String, AppFragment.ArgumentRequired> mArgumentRequired = new HashMap<>();
private boolean mCreatedNewView = false;

public Fragment() {
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
protected void declareArgument(String argumentName, AppFragment.ArgumentRequired required) {
	mArgumentRequired.put(argumentName, required);
}

@Nullable
@Override
public final View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
	if (mView == null) {
		mView = onCreateViewImpl(inflater, container, savedInstanceState);
		mCreatedNewView = true;
	} else {
		mCreatedNewView = false;
	}
	
	return mView;
}

/**
 * Called to have the fragment instantiate its user interface view.
 * This is optional, and non-graphical fragments can return null (which
 * is the default implementation).  This will be called between
 * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
 * <p>
 * <p>If you return a View from here, you will later be called in
 * {@link #onDestroyView} when the view is being released.
 * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
 * @param container If non-null, this is the parent view that the fragment's UI should be attached
 * to.  The fragment should not add the view itself, but this can be used to generate the
 * LayoutParams of the view.
 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
 * saved state as given here.
 * @return Return the View for the fragment's UI, or null.
 */
public abstract View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState);

@Override
public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
	super.onViewCreated(view, savedInstanceState);
	
	if (mCreatedNewView) {
		onViewCreatedImpl(view, savedInstanceState);
	}
}

@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	fetchArguments();
	onArgumentsSet();
}

/**
 * Set Arguments
 */
private void fetchArguments() {
	Bundle arguments = getArguments();
	
	for (Map.Entry<String, AppFragment.ArgumentRequired> entry : mArgumentRequired.entrySet()) {
		String name = entry.getKey();
		AppFragment.ArgumentRequired required = entry.getValue();
		Object value = arguments.get(name);
		
		if (value != null) {
			mArguments.put(name, value);
		} else if (required == AppFragment.ArgumentRequired.REQUIRED) {
			throw new IllegalStateException("Required argument " + name + " not set!");
		}
	}
}

/**
 * Called when the arguments have been set
 */
protected void onArgumentsSet() {
	// Does nothing
}

/**
 * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
 * has returned, but before any saved state has been restored in to the view.
 * This gives subclasses a chance to initialize themselves once
 * they know their view hierarchy has been completely created.  The fragment's
 * view hierarchy is not however attached to its parent at this point.
 * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
 * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
 * saved state as given here.
 */
public void onViewCreatedImpl(View view, @Nullable Bundle savedInstanceState) {
}

/**
 * Get the argument value
 * @param argumentName the name of the argument
 * @return argument value in the specified format
 */
@SuppressWarnings("unchecked")
protected <ReturnType> ReturnType getArgument(String argumentName) {
	return (ReturnType) mArguments.get(argumentName);
}

/**
 * If an argument is required or not
 */
protected enum ArgumentRequired {
	REQUIRED,
	OPTIONAL,
}
}
