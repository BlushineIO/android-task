package io.blushine.android.ui.list;

import android.content.Context;
import android.database.DataSetObserver;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import java.util.HashMap;
import java.util.Map;

import io.blushine.android.R;

/**
 * A list adapter that allows for list headers to be implemented.
 * If a section is empty the header is automatically hidden.
 * If you want to be able to add/remove data dynamically you have to check
 * the convertView manually.
 */
public class SectionAdapter<AdapterType extends Adapter> extends BaseAdapter {
private final static int VIEW_TYPE_HEADER_ID = 0;
private Map<String, AdapterType> mSections = new HashMap<>();
private ArrayAdapter<String> mHeaderAdapter;
private DataSetObserver mDataSetObserver = new DataSetObserver() {
	@Override
	public void onChanged() {
		notifyDataSetChanged();
	}

	@Override
	public void onInvalidated() {
		notifyDataSetInvalidated();
	}
};

/**
 * Creates a new section adapter with the default header layout
 * @param context activity
 * @see SectionAdapter(Context, int) to use a custom header layout
 */
public SectionAdapter(Context context) {
	this(context, R.layout.list_header);
}

/**
 * Creates a new section adapter with the specified header layout
 * @param context activity
 * @param headerLayout A TextView layout to use when creating headers.
 * @see SectionAdapter(Context) to use the default header layout
 */
public SectionAdapter(Context context, @LayoutRes int headerLayout) {
	mHeaderAdapter = new ArrayAdapter<>(context, headerLayout);
}

/**
 * Add a new section to the adapter
 * @param header header title
 * @param adapter adapter for the section
 * @throws IllegalArgumentException if header or adapter is null, or header is empty.
 */
public void addSection(String header, AdapterType adapter) {
	if (header == null) {
		throw new IllegalArgumentException("Header is null");
	} else if (header.isEmpty()) {
		throw new IllegalArgumentException("Header mustn't be empty");
	} else if (adapter == null) {
		throw new IllegalArgumentException("Adapter is null");
	}

	mHeaderAdapter.add(header);
	mSections.put(header, adapter);

	adapter.registerDataSetObserver(mDataSetObserver);

	notifyDataSetChanged();
}

/**
 * Check if a section exist
 * @param header check if a section with this header exists
 * @return true if it exists, false if it doesn't
 */
public boolean sectionExists(String header) {
	return mSections.containsKey(header);
}

/**
 * Get number of sections
 * @return number of sections
 */
public int getSectionCount() {
	return mHeaderAdapter.getCount();
}

@Override
public int getCount() {
	int count = 0;

	// Adapter sizes + 1 for each header
	for (Adapter adapter : mSections.values()) {
		int sectionCount = adapter.getCount() + 1;

		// Not empty
		if (sectionCount > 1) {
			count += sectionCount;
		}
	}

	return count;
}

@Override
public Object getItem(int position) {
	int innerPosition = position;

	for (int i = 0; i < mHeaderAdapter.getCount(); i++) {
		String header = mHeaderAdapter.getItem(i);
		AdapterType adapter = mSections.get(header);
		int sectionCount = adapter.getCount() + 1;

		// Empty section
		if (sectionCount == 1) {
			continue;
		}

		// Check if position is inside this section
		if (innerPosition == 0) {
			return header;
		} else if (innerPosition < sectionCount) {
			return adapter.getItem(innerPosition - 1);
		}

		innerPosition -= sectionCount;
	}
	return null;
}

@Override
public long getItemId(int position) {
	return position;
}

@Override
public View getView(int position, View convertView, ViewGroup parent) {
	int innerPosition = position;
	for (int i = 0; i < mHeaderAdapter.getCount(); i++) {
		String header = mHeaderAdapter.getItem(i);
		Adapter adapter = mSections.get(header);
		int sectionCount = adapter.getCount() + 1;

		// Empty section
		if (sectionCount == 1) {
			continue;
		}

		// Check if position is inside this section
		if (innerPosition == 0) {
			if (convertView != null && convertView.getId() != R.id.list_header) {
				convertView = null;
			}
			return mHeaderAdapter.getView(i, convertView, parent);
		} else if (innerPosition < sectionCount) {
			return adapter.getView(innerPosition - 1, convertView, parent);
		}

		innerPosition -= sectionCount;
	}
	return convertView;
}

/**
 * Get the adapter for the specified header
 * @param header header title to get the adapter from
 * @return adapter for the specified header, null if not found
 */
public AdapterType getSectionAdapter(String header) {
	return mSections.get(header);
}

@Override
public boolean areAllItemsEnabled() {
	return false;
}

@Override
public boolean isEnabled(int position) {
	return getItemViewType(position) != VIEW_TYPE_HEADER_ID;
}

@Override
public int getItemViewType(int position) {
	int innerPosition = position;
	int type = 1;
	for (int i = 0; i < mHeaderAdapter.getCount(); i++) {
		String header = mHeaderAdapter.getItem(i);
		Adapter adapter = mSections.get(header);
		int sectionCount = adapter.getCount() + 1;

		// Empty section
		if (sectionCount == 1) {
			type += adapter.getViewTypeCount();
			continue;
		}

		// Check if position is inside this section
		if (innerPosition == 0) {
			return VIEW_TYPE_HEADER_ID;
		} else if (innerPosition < sectionCount) {
			return type + adapter.getItemViewType(innerPosition - 1);
		}

		innerPosition -= sectionCount;
		type += adapter.getViewTypeCount();
	}
	return -1;
}

@Override
public int getViewTypeCount() {
	// Header adapter
	int count = mHeaderAdapter.getViewTypeCount();

	// Section adapters
	for (Adapter adapter : mSections.values()) {
		count += adapter.getViewTypeCount();
	}
	return count;
}
}
