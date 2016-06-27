package com.spiddekauga.android.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Edit an item by long clicking on it
 */
class EditFunctionality<T> implements PostBindFunctionality<T> {
private EditListener<T> mListener;

public EditFunctionality(EditListener<T> listener) {
	if (listener == null) {
		throw new IllegalArgumentException("listener is null");
	}

	mListener = listener;
}

@Override
public void applyFunctionality(AdvancedAdapter<T, ?> adapter, RecyclerView recyclerView) {
	// Does nothing
}

@Override
public void onPostBind(final AdvancedAdapter<T, ?> adapter, RecyclerView.ViewHolder viewHolder, final int position) {
	viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			T item = adapter.getItem(position);
			mListener.onEdit(item);
			return true;
		}
	});
}
}
