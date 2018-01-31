package io.blushine.android.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Edit an item by long clicking on it
 */
class ClickFunctionality<T> implements PostBindFunctionality<T> {
private ClickListener<T> mListener;

public ClickFunctionality(ClickListener<T> listener) {
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
	viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			T item = adapter.getItem(position);
			mListener.onClick(item);
		}
	});
}
}
