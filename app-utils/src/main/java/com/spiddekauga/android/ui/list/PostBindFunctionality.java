package com.spiddekauga.android.ui.list;

import android.support.v7.widget.RecyclerView;

/**
 * Called after the view has been bound (only for original list items)
 */
public interface PostBindFunctionality<T> extends AdapterFunctionality<T> {
/**
 * Called after the view has been bound, but only for normal list item and not for other
 * functionalities' list items
 * @param adapter the adapter that was bound
 * @param viewHolder the view holder to bind
 * @param position the position of the item that was bound
 */
void onPostBind(AdvancedAdapter<T, ?> adapter, RecyclerView.ViewHolder viewHolder, int position);
}
