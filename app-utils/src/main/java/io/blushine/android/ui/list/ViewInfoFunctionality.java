package io.blushine.android.ui.list;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * View more information about the specified item by clicking on it
 */
class ViewInfoFunctionality<T> implements PostBindFunctionality<T> {
private ViewInfoListener<T> mListener;

public ViewInfoFunctionality(ViewInfoListener<T> listener) {
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
		public void onClick(View v) {
			T item = adapter.getItem(position);
			mListener.onViewInfo(item);
		}
	});
}
}
