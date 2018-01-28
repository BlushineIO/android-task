package com.spiddekauga.android.ui.list;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;
import com.spiddekauga.android.ui.ColorHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Add ability to remove items by swiping
 */
class SwipeRemoveFunctionality<T> implements ViewHolderFunctionality<T> {
private boolean mUndoFunctionality = false;
private String mRemovedMessage = AppActivity.getActivity().getResources().getString(R.string.removed);
private RemoveListener<T> mListener;
@ColorInt
private int mColor = ColorHelper.getColor(AppActivity.getActivity().getResources(), R.color.remove, null);
private AdvancedAdapter<T, ?> mAdapter;
private Map<T, Runnable> mPendingRemoves = new HashMap<>();
private Handler mHandler = new Handler();
private boolean mRemoving = false;

SwipeRemoveFunctionality(AdvancedAdapter<T, ?> adapter, RemoveListener<T> listener) {
	mAdapter = adapter;
	mListener = listener;
}

SwipeRemoveFunctionality(AdvancedAdapter<T, ?> adapter, RemoveListener<T> listener, boolean undoFunctionality) {
	mAdapter = adapter;
	mListener = listener;
	mUndoFunctionality = undoFunctionality;
}

SwipeRemoveFunctionality(AdvancedAdapter<T, ?> adapter, RemoveListener<T> listener, boolean undoFunctionality, String removedMessage) {
	mAdapter = adapter;
	mListener = listener;
	mUndoFunctionality = undoFunctionality;
	mRemovedMessage = removedMessage;
}

public UndoViewHolder onCreateViewHolder(ViewGroup parent) {
	View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_undo, parent, false);
	return new UndoViewHolder(itemView);
}

@Override
public int getViewType() {
	return AdvancedAdapter.ViewTypes.UNDO.ordinal();
}

@Override
public Class<UndoViewHolder> getViewHolderClass() {
	return UndoViewHolder.class;
}

@Override
public void onBindViewHolder(AdvancedAdapter<T, ?> adapter, RecyclerView.ViewHolder view, int position) {
	UndoViewHolder undoView = (UndoViewHolder) view;
	undoView.mRemovedTextView.setText(mRemovedMessage);
	
	if (mUndoFunctionality) {
		final T item = mAdapter.getItem(position);
		undoView.mUndoButton.setVisibility(View.VISIBLE);
		undoView.mUndoButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Runnable pendingRemovalRunnable = mPendingRemoves.get(item);
				if (pendingRemovalRunnable != null) {
					mPendingRemoves.remove(item);
					mHandler.removeCallbacks(pendingRemovalRunnable);
					mAdapter.removeItemViewHolder(item, SwipeRemoveFunctionality.this);
					int currentPos = mAdapter.getItemPosition(item);
					if (currentPos != -1) {
						mAdapter.notifyItemChanged(currentPos);
					}
				}
			}
		});
	} else {
		undoView.mUndoButton.setVisibility(View.GONE);
	}
}

@Override
public void applyFunctionality(AdvancedAdapter<T, ?> adapter, RecyclerView recyclerView) {
	// Item touch helper
	// Use custom item touch helper so we can swipe between View Pages
	android.support.v7.widget.helper.ItemTouchHelper itemTouchHelper;
	if (canSwipeBetweenPages(recyclerView)) {
		itemTouchHelper = new ItemTouchHelper(new ItemRemoveCallback());
	} else {
		itemTouchHelper = new android.support.v7.widget.helper.ItemTouchHelper(new ItemRemoveCallback());
	}
	itemTouchHelper.attachToRecyclerView(recyclerView);
	
	// Red background when erasing
	BackgroundDecoration backgroundDecoration = new BackgroundDecoration();
	recyclerView.addItemDecoration(backgroundDecoration);
	
}

/**
 * Check if we have multiple pages we can swipe between
 * @param recyclerView used to check if this view is a child of ViewPages
 * @return true if we have multiple pages and can swipe mode has been enabled
 */
private static boolean canSwipeBetweenPages(RecyclerView recyclerView) {
	ViewParent viewParent = recyclerView.getParent();
	while (viewParent != null) {
		if (viewParent instanceof ViewPager) {
			return true;
		}
		viewParent = viewParent.getParent();
	}
	return false;
}

/**
 * Undo ViewHolder that views the undo functionality
 */
private static class UndoViewHolder extends RecyclerView.ViewHolder {
	private TextView mRemovedTextView;
	private Button mUndoButton;
	
	UndoViewHolder(View itemView) {
		super(itemView);
		mRemovedTextView = (TextView) itemView.findViewById(R.id.remove_message);
		mUndoButton = (Button) itemView.findViewById(R.id.undo_button);
	}
}

/**
 * Callback when a list item has been swiped (and should be removed)
 * Draws the red background together with the trash can while the list item is being swiped
 */
private class ItemRemoveCallback extends ItemTouchHelper.Callback {
	private static final int UNDO_DURATION = 3000; // 3sec
	private final int mTrashMargin = (int) AppActivity.getActivity().getResources().getDimension(R.dimen.margin);
	private Drawable mBackground = new ColorDrawable(mColor);
	private Drawable mTrash = ContextCompat.getDrawable(AppActivity.getActivity(), R.drawable.ic_delete_24dp);
	
	@Override
	public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
		// Don't allow undo view holders to be swiped
		if (viewHolder instanceof UndoViewHolder) {
			return 0;
		} else {
			int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
			return makeMovementFlags(0, swipeFlags);
		}
	}
	
	@Override
	public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
		return false;
	}
	
	@Override
	public boolean isItemViewSwipeEnabled() {
		return true;
	}
	
	@Override
	public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
		int swipedPosition = viewHolder.getAdapterPosition();
		final T item = mAdapter.getItem(swipedPosition);
		
		// Ability to undo
		if (mUndoFunctionality) {
			mAdapter.setItemViewHolder(item, SwipeRemoveFunctionality.this);
			Runnable pendingRemovalRunnable = new Runnable() {
				@Override
				public void run() {
					onRemove(item);
				}
			};
			mPendingRemoves.put(item, pendingRemovalRunnable);
			mAdapter.notifyItemChanged(swipedPosition);
			mHandler.postDelayed(pendingRemovalRunnable, UNDO_DURATION);
		}
		// No undo
		else {
			onRemove(item);
		}
	}
	
	@Override
	public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
		View itemView = viewHolder.itemView;
		
		// not sure why, but this method get's called for viewholder that are already swiped away
		if (viewHolder.getAdapterPosition() == -1) {
			// not interested in those
			return;
		}
		
		// draw red background
		// To the right
		if (dX < 0) {
			mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
		}
		// To the left
		else {
			mBackground.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + (int) dX, itemView.getBottom());
		}
		
		mBackground.draw(c);
		
		// draw trash mark
		int itemHeight = itemView.getBottom() - itemView.getTop();
		int intrinsicWidth = mTrash.getIntrinsicWidth();
		int intrinsicHeight = mTrash.getIntrinsicWidth();
		int trashTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
		int trashBottom = trashTop + intrinsicHeight;
		int trashLeft = Integer.MIN_VALUE;
		int trashRight = Integer.MIN_VALUE;
		
		// Right
		if (dX < 0) {
			trashLeft = itemView.getRight() - mTrashMargin - intrinsicWidth;
			trashRight = itemView.getRight() - mTrashMargin;
		}
		// Left
		else if (dX > 0) {
			trashLeft = itemView.getLeft() + mTrashMargin;
			trashRight = itemView.getLeft() + mTrashMargin + intrinsicWidth;
		}
		mTrash.setBounds(trashLeft, trashTop, trashRight, trashBottom);
		mTrash.draw(c);
		
		super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
	}
	
	private void onRemove(T item) {
		mRemoving = true;
		mAdapter.remove(item);
		mPendingRemoves.remove(item);
		if (mListener != null) {
			mListener.onRemoved(item);
		}
	}
}

/**
 * Draws the red background during the remove animation. I.e. after the list item has been removed
 */
private class BackgroundDecoration extends RecyclerView.ItemDecoration {
	private Drawable mBackground = new ColorDrawable(mColor);
	
	@Override
	public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
		// only if animation is in progress
		if (mRemoving && parent.getItemAnimator().isRunning()) {
			
			// some items might be animating down and some items might be animating up to close the gap left by the removed item
			// this is not exclusive, both movement can be happening at the same time
			// to reproduce this leave just enough items so the first one and the last one would be just a little off screen
			// then remove one from the middle
			
			// find first child with translationY > 0
			// and last one with translationY < 0
			// we're after a rect that is not covered in recycler-view views at this point in time
			View lastViewComingDown = null;
			View firstViewComingUp = null;
			
			// this is fixed
			int left = 0;
			int right = parent.getWidth();
			
			// this we need to find out
			int top = 0;
			int bottom = 0;
			
			// find relevant translating views
			int childCount = parent.getLayoutManager().getChildCount();
			for (int i = 0; i < childCount; i++) {
				View child = parent.getLayoutManager().getChildAt(i);
				if (child.getTranslationY() < 0) {
					// view is coming down
					lastViewComingDown = child;
				} else if (child.getTranslationY() > 0) {
					// view is coming up
					if (firstViewComingUp == null) {
						firstViewComingUp = child;
					}
				}
			}
			
			if (lastViewComingDown != null && firstViewComingUp != null) {
				// views are coming down AND going up to fill the void
				top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
				bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
			} else if (lastViewComingDown != null) {
				// views are going down to fill the void
				top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
				bottom = lastViewComingDown.getBottom();
			} else if (firstViewComingUp != null) {
				// views are coming up to fill the void
				top = firstViewComingUp.getTop();
				bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
			}
			
			mBackground.setBounds(left, top, right, bottom);
			mBackground.draw(c);
		} else {
			mRemoving = false;
		}
		
		
		super.onDraw(c, parent, state);
	}
}
}
