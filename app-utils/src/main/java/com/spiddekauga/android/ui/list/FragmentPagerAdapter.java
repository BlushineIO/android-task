package com.spiddekauga.android.ui.list;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.NonNull;
import android.util.SparseArray;

/**
 * Improved Fragment Pager adapter that can return an instance of the fragment
 */
public abstract class FragmentPagerAdapter<FragmentType extends Fragment> extends android.support.v13.app.FragmentPagerAdapter {
private SparseArray<FragmentType> mFragments = new SparseArray<>();

protected FragmentPagerAdapter(@NonNull FragmentManager fragmentManager) {
	super(fragmentManager);
}

/**
 * Get the instance of the item at position. Creates a new instance if none exists
 * @param position position of item to get
 * @return Fragment instance of the item at position.
 */
@Override
public FragmentType getItem(int position) {
	FragmentType fragment = mFragments.get(position);
	if (fragment == null) {
		fragment = instantiateItem(position);
		mFragments.append(position, fragment);
	}
	return fragment;
}

/**
 * Create a new instance of the item at the specified position
 * @return new fragment instance for this position
 */
protected abstract FragmentType instantiateItem(int position);
}
