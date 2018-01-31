package io.blushine.android;

import android.support.annotation.MenuRes;
import android.support.v7.widget.Toolbar;
import android.view.View;

import io.blushine.android.validate.ValidatorGroup;

/**
 * Base class for all dialog fragments
 */
public abstract class DialogFragment extends AppFragment implements Toolbar.OnMenuItemClickListener {
protected final ValidatorGroup mValidatorGroup = new ValidatorGroup();

/**
 * Validate all text fields
 * @return true if all text fields are valid
 */
protected boolean validateTextFields() {
	return mValidatorGroup.validate();
}

/**
 * Initialize the toolbar
 * @param view a view that contains the toolbar
 */
protected void initToolbar(View view) {
	Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
	toolbar.setTitle(getTitle());
	toolbar.inflateMenu(getMenu());
	toolbar.setNavigationOnClickListener(new BackOnClickListener());
	toolbar.setOnMenuItemClickListener(this);
}

/**
 * @return title of this dialog
 */
protected abstract String getTitle();

/**
 * @return menu to inflate the toolbar with
 */
@MenuRes
protected abstract int getMenu();

@Override
public void onResume() {
	super.onResume();
	mValidatorGroup.clearError();
}
}
