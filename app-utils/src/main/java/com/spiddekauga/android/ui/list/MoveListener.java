package com.spiddekauga.android.ui.list;

/**
 * Listens to when an item is moved in a list
 */
public interface MoveListener<T> {
/**
 * Called when an item has been moved in a list
 * @param item the item that was moved
 * @param fromPosition item moved from this position
 * @param toPosition item moved to this position
 */
void onMove(T item, int fromPosition, int toPosition);
}
