package com.spiddekauga.android.ui.list;

/**
 * Called when an item wants to be edited
 */
public interface EditListener<T> {
/**
 * Called when an item was edited
 * @param item the item that was edited
 */
void onEdit(T item);
}
