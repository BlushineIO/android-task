package com.spiddekauga.android.info;

import android.content.res.Resources;
import android.support.annotation.StringRes;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.R;
import com.spiddekauga.android.task.ResponseEvent;
import com.spiddekauga.android.task.WebTask;
import com.spiddekauga.android.ui.ProgressBar;
import com.spiddekauga.android.ui.SnackbarHelper;

/**
 * Fetch some information from the Internet and display it in an {@link InfoActivity} or {@link
 * InfoFragment}.
 */
public abstract class InfoFetchTask extends WebTask<InfoFetchTask.Instantiate, Void, InfoFetchTask.HtmlResponseEvent> {
private String mDownloadText;
private String mTitle;

/**
 * Fetch information from the Internet and display it in an {@link InfoActivity} or {@link
 * InfoFragment}.
 * @param titleId toolbar's title text in the {@link InfoActivity} or {@link InfoFragment}
 * @param downloadTextId progress bar text to show
 */
protected InfoFetchTask(@StringRes int titleId, @StringRes int downloadTextId) {
	super(WaitTypes.CHECK_CONNECTION);
	Resources resources = AppActivity.getActivity().getResources();

	mTitle = resources.getString(titleId);
	mDownloadText = resources.getString(downloadTextId);
}

@Override
protected void onPreExecute() {
	ProgressBar.show(ProgressBar.Styles.SPINNER, null, mDownloadText);
}

@Override
protected HtmlResponseEvent newEvent(Instantiate... params) {
	if (params.length == 1) {
		return new HtmlResponseEvent(params[0]);
	}
	// Use Activity as default
	else {
		return new HtmlResponseEvent(Instantiate.ACTIVITY);
	}
}

@Override
protected void waitForUserLoggedIn() {
	// Does nothing
}

@Override
protected boolean isUserLoggedIn() {
	return false;
}

@Override
protected void doInBackground(HtmlResponseEvent event, Instantiate... params) {
	doInBackground(event);
}

protected abstract void doInBackground(HtmlResponseEvent event);

@Override
protected void onPostExecute(HtmlResponseEvent event) {
	if (event.isSuccessful()) {
		ProgressBar.hide();
		switch (event.getInstantiate()) {
		case ACTIVITY:
			InfoActivity.show(mTitle, event.getHtml());
			break;

		case FRAGMENT:
			InfoFragment infoFragment = new InfoFragment();
			infoFragment.setArguments(mTitle, event.getHtml());
			infoFragment.show();
			break;
		}
	} else {
		SnackbarHelper.showSnackbar(R.string.info_download_failed);
	}
}

/**
 * How we want to instantiate the info screen
 */
public enum Instantiate {
	ACTIVITY,
	FRAGMENT
}

protected static class HtmlResponseEvent extends ResponseEvent {
	private Instantiate mInstantiate;
	private String mHtml;

	/**
	 * Create an HTML response
	 * @param instantiate if we want to instantiate an info activity or fragment
	 */
	private HtmlResponseEvent(Instantiate instantiate) {
		mInstantiate = instantiate;
	}

	public Instantiate getInstantiate() {
		return mInstantiate;
	}

	public String getHtml() {
		return mHtml;
	}

	public void setHtml(String html) {
		mHtml = html;
	}
}
}
