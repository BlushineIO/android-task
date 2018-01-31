package io.blushine.android.ui.list;

/**
 * Called when an item wants to be edited
 */
public interface ClickListener<T> {
/**
 * Called when an item was edited
 * @param item the item that was edited
 */
void onClick(T item);
}
