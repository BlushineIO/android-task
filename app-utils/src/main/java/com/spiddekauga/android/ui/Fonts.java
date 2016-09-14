package com.spiddekauga.android.ui;

import android.graphics.Typeface;
import android.os.Build;

import com.spiddekauga.android.AppActivity;

/**
 * Access to all available fonts, and easily cached
 */
public enum Fonts {
	LIGHT(null, "sans-serif-light", Typeface.NORMAL, 16),
	LIGHT_ITALIC(null, "sans-serif-light", Typeface.ITALIC, 16),
	LIGHT_BOLD(null, "sans-serif-light", Typeface.BOLD, 16),
	LIGHT_BOLD_ITALIC(null, "sans-serif-light", Typeface.BOLD_ITALIC, 16),
	REGULAR(null, "sans-serif", Typeface.NORMAL, 16),
	REGULAR_ITALIC(null, "sans-serif", Typeface.ITALIC, 16),
	REGULAR_BOLD(null, "sans-serif", Typeface.BOLD, 16),
	REGULAR_BOLD_ITALIC(null, "sans-serif", Typeface.BOLD_ITALIC, 16),
	MEDIUM("Roboto-Medium.ttf", "sans-serif-medium", Typeface.NORMAL, 21),
	CONDENSED(null, "sans-serif-condensed", Typeface.NORMAL, 16),
	CONDENSED_ITALIC(null, "sans-serif-condensed", Typeface.ITALIC, 16),
	CONDENSED_BOLD(null, "sans-serif-condensed", Typeface.BOLD, 16),
	CONDENSED_BOLD_ITALIC(null, "sans-serif-condensed", Typeface.BOLD_ITALIC, 16),;

// -------------------------------------------------------------
// Don't forget to edit the attrs.xml file, when changing this!
// --------------------------------------------------------------

private static final String FONT_DIR = "fonts/";
private String mAssetName;
private String mFamilyName;
private int mFamilyStyle;
private int mFromApi;
private Typeface mTypeface = null;

/**
 * @param assetName filename of the font in the asset folder, can be null
 * @param familyName which family name the font has inside android , can be null
 * @param familyStyle which family style the font uses
 * @param fromApi from which API version the familyName is available
 */
Fonts(String assetName, String familyName, int familyStyle, int fromApi) {
	mAssetName = FONT_DIR + assetName;
	mFamilyName = familyName;
	mFamilyStyle = familyStyle;
	mFromApi = fromApi;
}

/**
 * Get the font from the index (ordinal) value
 * @param index the index of the font, null if not found
 */
public static Fonts fromIndex(int index) {
	Fonts[] fonts = values();
	if (0 <= index && index < fonts.length) {
		return fonts[index];
	} else {
		return null;
	}
}

/**
 * Returns a cached font if it exists, otherwise it will open the font
 * @return typeface font
 */
public Typeface getTypeface() {
	// Load typeface
	if (mTypeface == null) {

		// Try to get from the internal font-family first
		if (Build.VERSION.SDK_INT >= mFromApi) {
			mTypeface = Typeface.create(mFamilyName, mFamilyStyle);
		}
		// Load asset
		else {
			mTypeface = Typeface.createFromAsset(AppActivity.getActivity().getAssets(), mAssetName);
		}
	}

	return mTypeface;
}

}
