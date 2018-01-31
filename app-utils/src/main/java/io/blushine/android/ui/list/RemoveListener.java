package io.blushine.android.ui.list;

/**
 * Listens to when an item has been removed
 */
public interface RemoveListener<T> {
/**
 * Called when an item has been removed
 * @param item the item that was removed
 */
void onRemoved(T item);
}
