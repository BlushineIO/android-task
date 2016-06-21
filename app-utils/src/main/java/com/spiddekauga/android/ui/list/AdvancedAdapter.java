package com.spiddekauga.android.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An advanced adapter for a RecyclerView
 * @param <T> what type of items to store inside the adapetr
 * @param <VH> ViewHolder type for T's items
 */
public abstract class AdvancedAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
private static final int VIEW_TYPES_ALLOCATED = 10;
private List<T> mItems = new ArrayList<>();
private List<RecyclerView> mRecyclerViews = new ArrayList<>();
private Map<Class<?>, AdapterFunctionality> mFunctionalities = new HashMap<>();
private Map<Class<?>, ViewHolderFunctionality> mFunctionalityByViewHolder = new HashMap<>();
private Map<Integer, ViewHolderFunctionality> mFunctionalityByViewType = new HashMap<>();

/**
 * Add ability to remove rows from the adapter by swiping. Doesn't enable undo functionality.
 * @param listener listen to when an item has been removed
 */
public void addSwipeRemoveFunctionality(RemoveListener<T> listener) {
	addFunctionality(new SwipeRemoveFunctionality<>(this, listener));
}

/**
 * Adds an adapter functionality
 * @param functionality the functionality to add
 */
public void addFunctionality(AdapterFunctionality functionality) {
	// Not added before
	if (!mFunctionalities.containsKey(functionality.getClass())) {
		mFunctionalities.put(functionality.getClass(), functionality);

		if (functionality instanceof ViewHolderFunctionality) {
			ViewHolderFunctionality viewHolderFunctionality = (ViewHolderFunctionality) functionality;
			mFunctionalityByViewType.put(viewHolderFunctionality.getViewType(), viewHolderFunctionality);
			mFunctionalityByViewHolder.put(viewHolderFunctionality.getViewHolderClass(), viewHolderFunctionality);
		}

		// Apply functionality to RecyclerViews
		for (RecyclerView recyclerView : mRecyclerViews) {
			functionality.applyFunctionality(this, recyclerView);
		}
	}
}

/**
 * Add ability to remove rows from the adapter by swiping.
 * @param listener listen to when an item has been removed
 * @param undoFunctionality set to true to enable undo functionality. Lets the user undo the remove
 * action for some time.
 */
public void addSwipeRemoveFunctionality(RemoveListener<T> listener, boolean undoFunctionality) {
	addFunctionality(new SwipeRemoveFunctionality<>(this, listener, undoFunctionality));
}

/**
 * Add ability to remove rows from the adapter by swiping
 * @param listener listen to when an item has been removed
 * @param undoFunctionality set to true to enable undo functionality. Lets the user unde the remove
 * action for some time.
 * @param removedMessage display this message instead of the default message when the item has been
 * removed.
 */
public void addSwipeRemoveFunctionality(RemoveListener<T> listener, boolean undoFunctionality, String removedMessage) {
	addFunctionality(new SwipeRemoveFunctionality<>(this, listener, undoFunctionality, removedMessage));
}

/**
 * Get the item of the specified position
 * @param position get the item on this position
 * @return item
 */
public T getItem(int position) {
	return mItems.get(position);
}

/**
 * Clear all items
 */
public void clearItems() {
	mItems.clear();
}

/**
 * Get the current list for modifying purposes
 * @return list of items
 */
protected List<T> getItems() {
	return mItems;
}

/**
 * Set items
 * @param items the items to use
 */
public void setItems(List<T> items) {
	mItems.clear();
	mItems.addAll(items);
}

/**
 * Remove the specified item from the list
 * @param item the item to remove
 */
public void remove(T item) {
	int index = mItems.indexOf(item);
	if (index > 0) {
		remove(index);
	}
}

/**
 * Remove the item at the specified location from the list
 * @param itemIndex index of the item to remove
 */
public void remove(int itemIndex) {
	mItems.remove(itemIndex);
	notifyItemRemoved(itemIndex);
}

@Override
public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
	ViewHolderFunctionality functionality = mFunctionalityByViewType.get(viewType);
	if (functionality != null) {
		return functionality.onCreateViewHolder(parent);
	} else {
		return onCreateView(parent, viewType);
	}
}

@Override
@SuppressWarnings("unchecked")
public final void onBindViewHolder(RecyclerView.ViewHolder view, final int position) {
	ViewHolderFunctionality functionality = mFunctionalityByViewHolder.get(view.getClass());
	if (functionality != null) {
		functionality.onBindViewHolder(this, view, position);
	} else {
		onBindView((VH) view, position);
	}
}

protected abstract void onBindView(VH view, int position);

/**
 * Be sure to override this method if you want to use more than 1 view type. View types should start
 * at 10.
 * @param position item position
 * @return view type identifier
 */
@Override
public int getItemViewType(int position) {
	// TODO
	return VIEW_TYPES_ALLOCATED;
}

@Override
public int getItemCount() {
	return mItems.size();
}

@Override
public void onAttachedToRecyclerView(RecyclerView recyclerView) {
	mRecyclerViews.add(recyclerView);

	// Apply previously added functionalities
	for (AdapterFunctionality functionality : mFunctionalities.values()) {
		functionality.applyFunctionality(this, recyclerView);
	}
}

@Override
public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
	mRecyclerViews.remove(recyclerView);
}

protected abstract VH onCreateView(ViewGroup parent, int viewType);

@SuppressWarnings("unchecked")
private <F extends AdapterFunctionality> F getFunctionality(Class<F> clazz) {
	return (F) mFunctionalities.get(clazz);
}

/**
 * Maixmum 10 view types !!!
 */
enum ViewTypes {
	UNDO,
}
}
