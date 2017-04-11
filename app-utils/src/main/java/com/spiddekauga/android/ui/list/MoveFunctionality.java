package com.spiddekauga.android.ui.list;

import android.support.annotation.IdRes;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Add drag and drop functionality to the {@link AdvancedAdapter}.
 */
class MoveFunctionality<T> implements PostBindFunctionality<T> {
private static final String TAG = MoveFunctionality.class.getSimpleName();
private static final int INVALID_MOVE_BUTTON = -1;
private MoveListener<T> mListener;
private android.support.v7.widget.helper.ItemTouchHelper mItemTouchHelper;
@IdRes private int mMoveButtonId;

/**
 * Long press to start drag and drop
 * @param listener listens to when an item has been moved
 */
MoveFunctionality(MoveListener<T> listener) {
	this(listener, INVALID_MOVE_BUTTON);
}

/**
 * Press the specified button id to start dragging
 * @param listener listens to when an item has been moved
 * @param moveButtonId resource id of the button to start moving the item
 */
MoveFunctionality(MoveListener<T> listener, @IdRes int moveButtonId) {
	mListener = listener;
	mMoveButtonId = moveButtonId;
}

@Override
public void applyFunctionality(AdvancedAdapter<T, ?> adapter, RecyclerView recyclerView) {
	MoveCallback moveCallback = new MoveCallback(adapter);
	mItemTouchHelper = new android.support.v7.widget.helper.ItemTouchHelper(moveCallback);
	mItemTouchHelper.attachToRecyclerView(recyclerView);
}


@Override
public void onPostBind(AdvancedAdapter<T, ?> adapter, final RecyclerView.ViewHolder viewHolder, int position) {
	// Start dragging when we press the move button
	if (mMoveButtonId != INVALID_MOVE_BUTTON) {
		View button = viewHolder.itemView.findViewById(mMoveButtonId);
		if (button != null) {
			button.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
						mItemTouchHelper.startDrag(viewHolder);
					}
					return false;
				}
			});
		} else {
			Log.w(TAG, "onPostBind() — Couldn't find button to start moving list item");
		}
	}
}

private class MoveCallback extends ItemTouchHelper.Callback {
	private AdvancedAdapter<T, ?> mAdapter;
	
	MoveCallback(AdvancedAdapter<T, ?> advancedAdapter) {
		mAdapter = advancedAdapter;
	}
	
	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
		return makeMovementFlags(dragFlags, 0);
	}
	
	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		T item = mAdapter.getItem(viewHolder.getAdapterPosition());
		int fromPosition = viewHolder.getAdapterPosition();
		int toPosition = target.getAdapterPosition();
		mAdapter.move(fromPosition, toPosition);
		mListener.onMove(item, fromPosition, toPosition);
		return true;
	}
	
	@Override
	public boolean isLongPressDragEnabled() {
		return mMoveButtonId == INVALID_MOVE_BUTTON;
	}
	
	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		// Does nothing
	}
}
}
