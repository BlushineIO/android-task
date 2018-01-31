package io.blushine.android.ui.list;

/**
 * Called when we want to show more information about a certain item
 */
public interface ViewInfoListener<T> {
/**
 * Called when we want to view more information about a certain item
 * @param item the item we want to view more information about
 */
void onViewInfo(T item);
}
