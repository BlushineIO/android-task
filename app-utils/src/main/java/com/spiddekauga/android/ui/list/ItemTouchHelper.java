package com.spiddekauga.android.ui.list;

import android.content.res.Resources;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Improved version of the ItemTouchHelper
 */
public class ItemTouchHelper extends android.support.v7.widget.helper.ItemTouchHelper {
private static final String TAG = ItemTouchHelper.class.getSimpleName();
private static final float CLOSE_TO_SCREEN_EDGE = 0.15f; // 15%
private static final int ACTIVE_POINTER_ID_NONE = -1;
private static final int DIRECTION_FLAG_COUNT = 8;
private static final int ACTION_MODE_IDLE_MASK = (1 << DIRECTION_FLAG_COUNT) - 1;
private static final int ACTION_MODE_SWIPE_MASK = ACTION_MODE_IDLE_MASK << DIRECTION_FLAG_COUNT;
private static final boolean DEBUG = false;
private RecyclerView mRecyclerView;
private Callback mCallback;
private OnItemTouchListener mOnItemTouchListener = new OnItemTouchListener();
private float mInitialTouchX;
private float mInitialTouchY;
private float mInitialScreenTouchX;
private float mInitialScreenTouchY;
private Field mfActionState;
private Field mfActivePointerId;
private Field mfDx;
private Field mfDy;
private Field mfRecyclerView;
private Field mfSwipeEscapeVelocity;
private Field mfMaxSwipeVelocity;
private Field mfSlop;
private Field mfRecoverAnimations;
private Field mfRecoverAnimationsmViewHolder;
private Field mfOverdrawChild;
private Field mfOverdrawChildPosition;
private Field mfSelected;
private Method mmReleaseVelocityTracker;
private Method mmInitGestureDetector;
private Method mmSelect;
private Method mmFindSwipedView;
private Method mmCallbackGetAbsoluteMovementFlags;


/**
 * Creates an ItemTouchHelper that will work with the given Callback.
 * <p>
 * You can attach ItemTouchHelper to a RecyclerView via
 * {@link #attachToRecyclerView(RecyclerView)}. Upon attaching, it will add an item decoration,
 * an onItemTouchListener and a Child attach / detach listener to the RecyclerView.
 * @param callback The Callback which controls the behavior of this touch helper.
 */
public ItemTouchHelper(Callback callback) {
	super(callback);
	mCallback = callback;
	initReflection();
}

private void initReflection() {
	try {
		mfActionState = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mActionState");
		mfActionState.setAccessible(true);
		mfActivePointerId = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mActivePointerId");
		mfActivePointerId.setAccessible(true);
		mfDx = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mDx");
		mfDx.setAccessible(true);
		mfDy = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mDy");
		mfDy.setAccessible(true);
		mfRecyclerView = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mRecyclerView");
		mfRecyclerView.setAccessible(true);
		mfSwipeEscapeVelocity = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mSwipeEscapeVelocity");
		mfSwipeEscapeVelocity.setAccessible(true);
		mfMaxSwipeVelocity = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mMaxSwipeVelocity");
		mfMaxSwipeVelocity.setAccessible(true);
		mfSlop = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mSlop");
		mfSlop.setAccessible(true);
		mfSelected = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mSelected");
		mfSelected.setAccessible(true);
		mfRecoverAnimations = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mRecoverAnimations");
		mfRecoverAnimations.setAccessible(true);
		mfRecoverAnimationsmViewHolder = Class.forName(android.support.v7.widget.helper.ItemTouchHelper.class.getName() + "$RecoverAnimation").getDeclaredField("mViewHolder");
		mfOverdrawChild = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mOverdrawChild");
		mfOverdrawChild.setAccessible(true);
		mfOverdrawChildPosition = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mOverdrawChildPosition");
		mfOverdrawChildPosition.setAccessible(true);
		mmReleaseVelocityTracker = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("releaseVelocityTracker");
		mmReleaseVelocityTracker.setAccessible(true);
		mmInitGestureDetector = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("initGestureDetector");
		mmInitGestureDetector.setAccessible(true);
		mmSelect = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("select", RecyclerView.ViewHolder.class, int.class);
		mmSelect.setAccessible(true);
		mmFindSwipedView = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("findSwipedView", MotionEvent.class);
		mmFindSwipedView.setAccessible(true);
		mmCallbackGetAbsoluteMovementFlags = Callback.class.getDeclaredMethod("getAbsoluteMovementFlags", RecyclerView.class, RecyclerView.ViewHolder.class);
		mmCallbackGetAbsoluteMovementFlags.setAccessible(true);
	} catch (NoSuchFieldException e) {
		Log.e(TAG, "initReflection() — No such field", e);
	} catch (NoSuchMethodException e) {
		Log.e(TAG, "initReflection() — No such method", e);
	} catch (ClassNotFoundException e) {
		Log.e(TAG, "initReflection() — Class not found", e);
	}
}

@Override
public void attachToRecyclerView(@Nullable RecyclerView recyclerView) {
	if (mRecyclerView == recyclerView) {
		return; // nothing to do
	}
	if (mRecyclerView != null) {
		destroyCallbacks();
	}
	mRecyclerView = recyclerView;
	setField(mfRecyclerView, recyclerView);
	if (mRecyclerView != null) {
		final Resources resources = recyclerView.getResources();
		setField(mfSwipeEscapeVelocity, resources
				.getDimension(R.dimen.item_touch_helper_swipe_escape_velocity));
		setField(mfMaxSwipeVelocity, resources
				.getDimension(R.dimen.item_touch_helper_swipe_escape_max_velocity));
		setupCallbacks();
	}
}

private void destroyCallbacks() {
	mRecyclerView.removeItemDecoration(this);
	mRecyclerView.removeOnItemTouchListener(mOnItemTouchListener);
	mRecyclerView.removeOnChildAttachStateChangeListener(this);
	// clean all attached
	List<?> recoverAnimations = getField(mfRecoverAnimations);
	final int recoverAnimSize = recoverAnimations.size();
	for (int i = recoverAnimSize - 1; i >= 0; i--) {
		final Object recoverAnimation = recoverAnimations.get(0);
		mCallback.clearView(mRecyclerView, (RecyclerView.ViewHolder) getField(mfRecoverAnimationsmViewHolder));
	}
	recoverAnimations.clear();
	setField(mfOverdrawChild, null);
	setField(mfOverdrawChildPosition, -1);
	callMethod(mmReleaseVelocityTracker);
}

private void setupCallbacks() {
	ViewConfiguration vc = ViewConfiguration.get(mRecyclerView.getContext());
	setField(mfSlop, vc.getScaledTouchSlop());
	mRecyclerView.addItemDecoration(this);
	mRecyclerView.addOnItemTouchListener(mOnItemTouchListener);
	mRecyclerView.addOnChildAttachStateChangeListener(this);
	callMethod(mmInitGestureDetector);
}

/**
 * Checks whether we should select a View for swiping.
 */
private boolean checkSelectForSwipe(int action, MotionEvent motionEvent, int pointerIndex) {
	if (getField(mfSelected) != null || action != MotionEvent.ACTION_MOVE
			|| (Integer) getField(mfActionState) == ACTION_STATE_DRAG || !mCallback.isItemViewSwipeEnabled()) {
		return false;
	}
	if (mRecyclerView.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING) {
		return false;
	}
	if (isInitialTouchCloseToScreenEdge()) {
		return false;
	}
	final RecyclerView.ViewHolder vh = callMethod(mmFindSwipedView, motionEvent);
	if (vh == null) {
		return false;
	}
	final int movementFlags = (int) callMethodStatic(mmCallbackGetAbsoluteMovementFlags, mCallback, mRecyclerView, vh);
	
	final int swipeFlags = (movementFlags & ACTION_MODE_SWIPE_MASK)
			>> (DIRECTION_FLAG_COUNT * ACTION_STATE_SWIPE);
	
	if (swipeFlags == 0) {
		return false;
	}
	
	// mDx and mDy are only set in allowed directions. We use custom x/y here instead of
	// updateDxDy to avoid swiping if user moves more in the other direction
	final float x = motionEvent.getX(pointerIndex);
	final float y = motionEvent.getY(pointerIndex);
	
	// Calculate the distance moved
	final float dx = x - mInitialTouchX;
	final float dy = y - mInitialTouchY;
	// swipe target is chose w/o applying flags so it does not really check if swiping in that
	// direction is allowed. This why here, we use mDx mDy to check slope value again.
	final float absDx = Math.abs(dx);
	final float absDy = Math.abs(dy);
	
	int slop = getField(mfSlop);
	if (absDx < slop && absDy < slop) {
		return false;
	}
	if (absDx > absDy) {
		if (dx < 0 && (swipeFlags & LEFT) == 0) {
			return false;
		}
		if (dx > 0 && (swipeFlags & RIGHT) == 0) {
			return false;
		}
	} else {
		if (dy < 0 && (swipeFlags & UP) == 0) {
			return false;
		}
		if (dy > 0 && (swipeFlags & DOWN) == 0) {
			return false;
		}
	}
	setField(mfDx, 0f);
	setField(mfDy, 0f);
	setField(mfActivePointerId, motionEvent.getPointerId(0));
	callMethod(mmSelect, vh, ACTION_STATE_SWIPE);
	return true;
}

@SuppressWarnings("unchecked")
private <ObjectType> ObjectType getField(Field field) {
	return getField(field, this);
}

/**
 * Check if initial touch is too close to the screen edge. TODO never works close to any screen
 * edge. Check for swipe direction of the item touch helper to check which screens to use.
 * @return true if the initial touch is too close to the screen; meaning we shouldn't start a swipe.
 * False if it isn't near the screen edge
 */
private boolean isInitialTouchCloseToScreenEdge() {
	Display display = AppActivity.getActivity().getWindowManager().getDefaultDisplay();
	Point displaySize = new Point();
	display.getSize(displaySize);
	
	float closeToEdgeWidth = displaySize.x * CLOSE_TO_SCREEN_EDGE;
	float closeToEdgeHeight = displaySize.y * CLOSE_TO_SCREEN_EDGE;
	
	// Left edge
	if (mInitialScreenTouchX < closeToEdgeWidth) {
		return true;
	}
	// Right edge
	else if (displaySize.x - mInitialScreenTouchX < closeToEdgeWidth) {
		return true;
	}
//	// Top edge
//	else if (mInitialScreenTouchY < closeToEdgeHeight) {
//		return true;
//	}
//	// Bottom edge
//	else if (displaySize.y - mInitialScreenTouchY < closeToEdgeHeight) {
//		return true;
//	}
	
	return false;
}

@SuppressWarnings("unchecked")
private <ObjectType> ObjectType callMethod(Method method, Object... arguments) {
	return (ObjectType) callMethodStatic(method, this, arguments);
}

@SuppressWarnings("unchecked")
private static Object callMethodStatic(Method method, Object object, Object... arguments) {
	try {
		return method.invoke(object, arguments);
	} catch (IllegalAccessException e) {
		Log.e(TAG, "callMethod() — Illegal access exception", e);
	} catch (InvocationTargetException e) {
		Log.e(TAG, "callMethod() — Invocation target exception", e);
	}
	return null;
}

private void setField(Field field, Object value) {
	try {
		field.set(this, value);
	} catch (IllegalAccessException e) {
		Log.e(TAG, "setField() — Illegal access", e);
	}
}

@SuppressWarnings("unchecked")
private static <ObjectType> ObjectType getField(Field field, Object object) {
	try {
		return (ObjectType) field.get(object);
	} catch (IllegalAccessException e) {
		Log.e(TAG, "getField() — Illegal access exception", e);
		throw new RuntimeException(e);
	}
}

private class OnItemTouchListener implements RecyclerView.OnItemTouchListener {
	private Field mfGestureDetector;
	private Field mfInitialTouchX;
	private Field mfInitialTouchY;
	private Field mfRecoverAnimationmX;
	private Field mfRecoverAnimationmY;
	private Field mfRecoverAnimationmActionState;
	private Field mfPendingCleanup;
	private Field mfSelectedFlags;
	private Field mfVelocityTracker;
	private Field mfScrollRunnable;
	private Method mmObtainVelocityTracker;
	private Method mmFindAnimation;
	private Method mmEndRecoverAnimation;
	private Method mmUpdateDxDy;
	private Method mmMoveIfNecessary;
	
	/**
	 * Initializes reflection methods and fields
	 */
	OnItemTouchListener() {
		try {
			mfGestureDetector = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mGestureDetector");
			mfGestureDetector.setAccessible(true);
			mfInitialTouchX = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mInitialTouchX");
			mfInitialTouchX.setAccessible(true);
			mfInitialTouchY = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mInitialTouchY");
			mfInitialTouchY.setAccessible(true);
			mfRecoverAnimationmX = Class.forName(android.support.v7.widget.helper.ItemTouchHelper.class.getName() + "$RecoverAnimation").getDeclaredField("mX");
			mfRecoverAnimationmX.setAccessible(true);
			mfRecoverAnimationmY = Class.forName(android.support.v7.widget.helper.ItemTouchHelper.class.getName() + "$RecoverAnimation").getDeclaredField("mY");
			mfRecoverAnimationmY.setAccessible(true);
			mfRecoverAnimationmActionState = Class.forName(android.support.v7.widget.helper.ItemTouchHelper.class.getName() + "$RecoverAnimation").getDeclaredField("mActionState");
			mfRecoverAnimationmActionState.setAccessible(true);
			mfPendingCleanup = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mPendingCleanup");
			mfPendingCleanup.setAccessible(true);
			mfSelectedFlags = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mSelectedFlags");
			mfSelectedFlags.setAccessible(true);
			mfVelocityTracker = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mVelocityTracker");
			mfVelocityTracker.setAccessible(true);
			mfScrollRunnable = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredField("mScrollRunnable");
			mfScrollRunnable.setAccessible(true);
			mmObtainVelocityTracker = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("obtainVelocityTracker");
			mmObtainVelocityTracker.setAccessible(true);
			mmFindAnimation = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("findAnimation", MotionEvent.class);
			mmFindAnimation.setAccessible(true);
			mmEndRecoverAnimation = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("endRecoverAnimation", RecyclerView.ViewHolder.class, boolean.class);
			mmEndRecoverAnimation.setAccessible(true);
			mmUpdateDxDy = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("updateDxDy", MotionEvent.class, int.class, int.class);
			mmUpdateDxDy.setAccessible(true);
			mmMoveIfNecessary = android.support.v7.widget.helper.ItemTouchHelper.class.getDeclaredMethod("moveIfNecessary", RecyclerView.ViewHolder.class);
			mmMoveIfNecessary.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Log.e(TAG, "OnItemTouchListener() — No such field", e);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "OnItemTouchListener() — No such method", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "OnItemTouchListener() — Class not found", e);
		}
	}
	
	@Override
	public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent event) {
		GestureDetectorCompat gestureDetector = getField(mfGestureDetector);
		gestureDetector.onTouchEvent(event);
		if (DEBUG) {
			Log.d(TAG, "intercept: x:" + event.getX() + ",y:" + event.getY() + ", " + event);
		}
		final int action = MotionEventCompat.getActionMasked(event);
		int activePointerId = getField(mfActivePointerId);
		if (action == MotionEvent.ACTION_DOWN) {
			setField(mfActivePointerId, event.getPointerId(0));
			mInitialTouchX = event.getX();
			mInitialTouchY = event.getY();
			setField(mfInitialTouchX, mInitialTouchX);
			setField(mfInitialTouchY, mInitialTouchY);
			
			// Screen touch location
			mInitialScreenTouchX = event.getRawX();
			mInitialScreenTouchY = event.getRawY();
			
			callMethod(mmObtainVelocityTracker);
			RecyclerView.ViewHolder selected = getField(mfSelected);
			if (selected == null) {
				final Object animation = callMethod(mmFindAnimation, event);
				if (animation != null) {
					mInitialTouchX -= (float) getField(mfRecoverAnimationmX, animation);
					mInitialTouchY -= (float) getField(mfRecoverAnimationmY, animation);
					setField(mfInitialTouchX, mInitialTouchX);
					setField(mfInitialTouchY, mInitialTouchY);
					RecyclerView.ViewHolder animationViewHolder = getField(mfRecoverAnimationsmViewHolder, animation);
					callMethod(mmEndRecoverAnimation, animationViewHolder, true);
					final List<View> pendingCleanup = getField(mfPendingCleanup);
					if (pendingCleanup.remove(animationViewHolder.itemView)) {
						mCallback.clearView(mRecyclerView, animationViewHolder);
					}
					int animationActionState = getField(mfRecoverAnimationmActionState);
					callMethod(mmSelect, animationViewHolder, animationActionState);
					int selectedFlags = getField(mfSelectedFlags);
					callMethod(mmUpdateDxDy, event, selectedFlags, 0);
				}
			}
		} else if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
			setField(mfActivePointerId, ACTIVE_POINTER_ID_NONE);
			callMethod(mmSelect, null, ACTION_STATE_IDLE);
		} else if (activePointerId != ACTIVE_POINTER_ID_NONE) {
			// in a non scroll orientation, if distance change is above threshold, we
			// can select the item
			final int index = event.findPointerIndex(activePointerId);
			if (DEBUG) {
				Log.d(TAG, "pointer index " + index);
			}
			if (index >= 0) {
				checkSelectForSwipe(action, event, index);
			}
		}
		VelocityTracker velocityTracker = getField(mfVelocityTracker);
		if (velocityTracker != null) {
			velocityTracker.addMovement(event);
		}
		return getField(mfSelected) != null;
	}
	
	@Override
	public void onTouchEvent(RecyclerView recyclerView, MotionEvent event) {
		GestureDetectorCompat gestureDetector = getField(mfGestureDetector);
		gestureDetector.onTouchEvent(event);
		if (DEBUG) {
			Log.d(TAG,
					"on touch: x:" + mInitialTouchX + ",y:" + mInitialTouchY + ", :" + event);
		}
		VelocityTracker velocityTracker = getField(mfVelocityTracker);
		if (velocityTracker != null) {
			velocityTracker.addMovement(event);
		}
		if ((Integer) getField(mfActivePointerId) == ACTIVE_POINTER_ID_NONE) {
			return;
		}
		final int action = MotionEventCompat.getActionMasked(event);
		final int activePointerIndex = event.findPointerIndex((Integer) getField(mfActivePointerId));
		if (activePointerIndex >= 0) {
			checkSelectForSwipe(action, event, activePointerIndex);
		}
		RecyclerView.ViewHolder viewHolder = getField(mfSelected);
		if (viewHolder == null) {
			return;
		}
		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			// Find the index of the active pointer and fetch its position
			if (activePointerIndex >= 0) {
				callMethod(mmUpdateDxDy, event, getField(mfSelectedFlags), activePointerIndex);
				callMethod(mmMoveIfNecessary, viewHolder);
				Runnable scrollRunnable = getField(mfScrollRunnable);
				mRecyclerView.removeCallbacks(scrollRunnable);
				scrollRunnable.run();
				mRecyclerView.invalidate();
			}
			break;
		}
		case MotionEvent.ACTION_CANCEL:
			if (velocityTracker != null) {
				velocityTracker.clear();
			}
			// fall through
		case MotionEvent.ACTION_UP:
			callMethod(mmSelect, null, ACTION_STATE_IDLE);
			setField(mfActivePointerId, ACTIVE_POINTER_ID_NONE);
			break;
		case MotionEvent.ACTION_POINTER_UP: {
			final int pointerIndex = MotionEventCompat.getActionIndex(event);
			final int pointerId = event.getPointerId(pointerIndex);
			if (pointerId == (Integer) getField(mfActivePointerId)) {
				// This was our active pointer going up. Choose a new
				// active pointer and adjust accordingly.
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				setField(mfActivePointerId, event.getPointerId(newPointerIndex));
				callMethod(mmUpdateDxDy, event, getField(mfSelectedFlags), pointerIndex);
			}
			break;
		}
		}
	}
	
	@Override
	public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		if (!disallowIntercept) {
			return;
		}
		callMethod(mmSelect, null, ACTION_STATE_IDLE);
	}
}
}
