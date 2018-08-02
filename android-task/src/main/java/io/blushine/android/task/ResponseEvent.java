package io.blushine.android.task;

/**
 * Response event from an HTTP request
 */
public abstract class ResponseEvent {
private ResponseStatuses mStatus = ResponseStatuses.FAILED_UNKNOWN;

/**
 * Set a response event with default_selectable value FAILED_UNKNOWN
 */
protected ResponseEvent() {
	// Does nothing
}

/**
 * Set a response event
 * @param status response status
 */
protected ResponseEvent(ResponseStatuses status) {
	mStatus = status;
}

/**
 * @return true if successful
 */
public boolean isSuccessful() {
	return mStatus == ResponseStatuses.SUCCESS;
}

/**
 * @return status of the response
 */
public ResponseStatuses getStatus() {
	return mStatus;
}

/**
 * Set the response status
 * @param status the response status
 */
public void setStatus(ResponseStatuses status) {
	mStatus = status;
}

/**
 * Different responses from the server
 */
public enum ResponseStatuses {
	/** Success */
	SUCCESS,
	/** Failed */
	FAILED,
	/** Failed because of no internet connection */
	FAILED_NO_INTERNET,
	/** Could not connect to the server */
	FAILED_SERVER_CONNECTION,
	/** Failed user not online */
	FAILED_USER_LOGGED_OUT,
	/** Unknown failure */
	FAILED_UNKNOWN,
}
}
