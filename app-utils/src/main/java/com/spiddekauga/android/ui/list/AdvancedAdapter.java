package com.spiddekauga.android.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
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
private Map<Class<?>, AdapterFunctionality<T>> mFunctionalities = new HashMap<>();
private List<PostBindFunctionality<T>> mPostBindFunctionalities = new ArrayList<>();
private Map<Class<?>, ViewHolderFunctionality<T>> mFunctionalityByViewHolder = new HashMap<>();
private Map<Integer, ViewHolderFunctionality<T>> mFunctionalityByViewType = new HashMap<>();
private Map<T, ViewHolderFunctionality<T>> mItemViewHolder = new HashMap<>();

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
public void addFunctionality(AdapterFunctionality<T> functionality) {
	// Not added before
	if (!mFunctionalities.containsKey(functionality.getClass())) {
		mFunctionalities.put(functionality.getClass(), functionality);

		if (functionality instanceof ViewHolderFunctionality) {
			ViewHolderFunctionality viewHolderFunctionality = (ViewHolderFunctionality<T>) functionality;
			mFunctionalityByViewType.put(viewHolderFunctionality.getViewType(), viewHolderFunctionality);
			mFunctionalityByViewHolder.put(viewHolderFunctionality.getViewHolderClass(), viewHolderFunctionality);
		}

		if (functionality instanceof PostBindFunctionality) {
			mPostBindFunctionalities.add((PostBindFunctionality<T>) functionality);
		}

		// Apply functionality to RecyclerViews
		for (RecyclerView recyclerView : mRecyclerViews) {
			functionality.applyFunctionality(this, recyclerView);
		}
	}
}

/**
 * Notify item changed
 * @param item the item that was changed
 */
public void notifyItemChanged(T item) {
	int position = getItemPosition(item);
	notifyItemChanged(position);
}

/**
 * Get item position
 * @param item the item to get the position of
 * @return position of the item, -1 if not found
 */
public int getItemPosition(T item) {
	return mItems.indexOf(item);
}

/**
 * Add ability to edit an item
 * @param listener listen to when an item wants to be edited
 */
public void addEditFunctionality(EditListener<T> listener) {
	addFunctionality(new EditFunctionality<T>(listener));
}

/**
 * Add ability to view more information about an item
 * @param listener listen to when an item wants to be viewed
 */
public void addViewInfoFunctionality(ViewInfoListener<T> listener) {
	addFunctionality(new ViewInfoFunctionality<T>(listener));
}

/**
 * Set the {@link ViewHolderFunctionality} to create and manage the {@link
 * android.support.v7.widget.RecyclerView.ViewHolder} for the specified item. If another view holder
 * has been set, the new specified view holder will be used instead
 * @param item the item to use a custom {@link android.support.v7.widget.RecyclerView.ViewHolder}.
 * @param viewHolderFunctionality the instance which handles the item
 * @see #removeItemViewHolder(Object, ViewHolderFunctionality) to remove it
 */
public void setItemViewHolder(T item, ViewHolderFunctionality viewHolderFunctionality) {
	mItemViewHolder.put(item, viewHolderFunctionality);
}

/**
 * Remove the {@link ViewHolderFunctionality} from managing the {@link
 * android.support.v7.widget.RecyclerView.ViewHolder} for the item. Does nothing if
 * viewHolderFunctionality isn't managing the item.
 * @param item the item to remove the custom use of a {@link android.support.v7.widget.RecyclerView.ViewHolder}.
 * @param viewHolderFunctionality the instance which handles the item
 * @see #setItemViewHolder(Object, ViewHolderFunctionality)  to add the functionality
 */
public void removeItemViewHolder(T item, ViewHolderFunctionality viewHolderFunctionality) {
	ViewHolderFunctionality overridingViewHolder = mItemViewHolder.get(item);

	// Only remove if it's the same view holder
	if (overridingViewHolder == viewHolderFunctionality) {
		mItemViewHolder.remove(item);
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
 * Add item(s) to the end of the list
 * @param items the items to add
 */
public void addItem(T... items) {
	int prevSize = mItems.size();
	List<T> convertList = Arrays.asList(items);
	mItems.addAll(convertList);
	notifyItemRangeInserted(prevSize, items.length);
}

/**
 * Add an item at the specified position
 * @param position the position to add the item
 * @param item the item to add at the specified position
 */
public void addItem(int position, T item) {
	mItems.add(position, item);
	notifyItemInserted(position);
}

/**
 * Remove the specified item from the list
 * @param item the item to remove
 */
public void remove(T item) {
	int index = mItems.indexOf(item);
	if (index >= 0) {
		remove(index);
	}
}

/**
 * Remove the item at the specified location from the list
 * @param itemIndex index of the item to remove
 */
public void remove(int itemIndex) {
	T item = mItems.remove(itemIndex);
	notifyItemRemoved(itemIndex);

	if (item != null) {
		mItemViewHolder.remove(item);
	}
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

protected abstract VH onCreateView(ViewGroup parent, int viewType);

@Override
@SuppressWarnings("unchecked")
public final void onBindViewHolder(RecyclerView.ViewHolder view, final int position) {
	ViewHolderFunctionality functionality = mFunctionalityByViewHolder.get(view.getClass());
	if (functionality != null) {
		functionality.onBindViewHolder(this, view, position);
	} else {
		onBindView((VH) view, position);

		// Call post bind functionalities
		for (PostBindFunctionality<T> postBindFunctionality : mPostBindFunctionalities) {
			postBindFunctionality.onPostBind(this, view, position);
		}
	}
}

/**
 * Be sure to override this method if you want to use more than 1 view type. View types should start
 * at 10.
 * @param position item position
 * @return view type identifier
 */
@Override
public int getItemViewType(int position) {
	T item = getItem(position);
	ViewHolderFunctionality itemViewHolder = mItemViewHolder.get(item);
	if (itemViewHolder != null) {
		return itemViewHolder.getViewType();
	} else {
		return VIEW_TYPES_ALLOCATED;
	}
}

/**
 * Get the item of the specified position
 * @param position get the item on this position
 * @return item
 */
public T getItem(int position) {
	return mItems.get(position);
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

protected abstract void onBindView(VH view, int position);

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
