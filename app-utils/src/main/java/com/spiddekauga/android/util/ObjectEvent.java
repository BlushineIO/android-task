package com.spiddekauga.android.util;

/**
 * Base class for object events. Used for add, edit, and remove actions.
 */
public abstract class ObjectEvent {
private final Actions mAction;

protected ObjectEvent(Actions action) {
	mAction = action;
}

/**
 * Get the action to take on the object event
 * @return the action to take on the object event
 */
public Actions getAction() {
	return mAction;
}

/**
 * The different actions
 */
public enum Actions {
	ADD,
	EDIT,
	REMOVE,
}
}
