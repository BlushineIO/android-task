package io.blushine.android.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

import io.blushine.android.AppActivity;

/**
 * Preference gateway for storing document sizes
 */
class DocumentChangePrefsGateway {
private static final String TAG = DocumentChangePrefsGateway.class.getSimpleName();
private static final String PREFERENCE_NAME = "document_change_information";
private final SharedPreferences mPreferences;


DocumentChangePrefsGateway() {
	mPreferences = AppActivity.getActivity().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
}

/**
 * Check if a document has been changed. And update the document information.
 * @param rawId document to check
 * @return true if the document has been changed since the last time
 */
boolean checkChangedAndUpdate(@RawRes int rawId) {
	Resources resources = AppActivity.getActivity().getResources();

	String documentFilename = resources.getResourceEntryName(rawId);
	if (documentFilename != null) {
		InputStream inputStream = resources.openRawResource(rawId);
		try {
			int fileSize = inputStream.available();
			inputStream.close();

			// Get file size
			int existingFileSize = getFileSize(documentFilename);

			// Update file size
			if (existingFileSize != fileSize) {
				setFileSize(documentFilename, fileSize);
			}

			// Was it changed?
			return fileSize != existingFileSize;

		} catch (IOException e) {
			Log.e(TAG, "Couldn't read available size", e);
		}
	}

	return false;
}

/**
 * Get existing file size
 * @param documentFilename name to get the existing file size of
 * @return file size of the existing document, -1 if no size has been set
 */
private int getFileSize(String documentFilename) {
	return mPreferences.getInt(documentFilename, -1);
}

/**
 * Update the file size
 * @param documentFilename name of the document to update
 * @param fileSize the new file size of the document
 */
private void setFileSize(String documentFilename, int fileSize) {
	SharedPreferences.Editor editor = mPreferences.edit();
	editor.putInt(documentFilename, fileSize);
	editor.apply();
}

}
