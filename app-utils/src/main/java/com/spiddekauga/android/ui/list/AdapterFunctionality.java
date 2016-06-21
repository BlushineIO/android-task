package com.spiddekauga.android.ui.list;

import android.support.v7.widget.RecyclerView;

/**
 * Base class for adapter functionality
 */
public abstract class AdapterFunctionality {

protected abstract void applyFunctionality(AdvancedAdapter<?, ?> adapter, RecyclerView recyclerView);
}
