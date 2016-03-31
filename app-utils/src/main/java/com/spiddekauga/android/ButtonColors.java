package com.spiddekauga.android;

import android.support.annotation.ColorRes;

/**
 * Different button colors
 */
public enum ButtonColors {
	ACCEPT(R.color.accept_selectable),
	CANCEL(R.color.cancel_selectable),
	DEFAULT(R.color.default_selectable),
	PRIMARY(R.color.primary_selectable),;

private int mColorResId;

private ButtonColors(@ColorRes int id) {
	mColorResId = id;
}

@ColorRes
int getColorResId() {
	return mColorResId;
}
}
