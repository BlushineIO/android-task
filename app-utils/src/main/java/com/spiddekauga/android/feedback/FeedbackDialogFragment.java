package com.spiddekauga.android.feedback;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.BuildConfig;
import com.spiddekauga.android.R;
import com.spiddekauga.android.ui.ColorHelper;
import com.spiddekauga.android.ui.SnackbarHelper;
import com.spiddekauga.android.validate.TextValidator;
import com.spiddekauga.android.validate.TextValidatorGroup;
import com.spiddekauga.android.validate.Validate;
import com.spiddekauga.cloudshine.feedbackApi.model.Feedback;
import com.spiddekauga.utils.Strings;

/**
 * Displays a dialog for sending feedback to Spiddekauga's mail
 */
public class FeedbackDialogFragment extends DialogFragment {
private static final String TITLE_KEY = "title";
private static final String MESSAGE_KEY = "message";
private static final String EXCEPTION_KEY = "exception";
private static final String NAME_KEY = "name";
private static final String EMAIL_KEY = "email";
private CheckBox mBugReport = null;
private EditText mTitle = null;
private EditText mMessage = null;
private Toolbar mToolbar = null;
private String mException = null;
private String mName = null;
private String mEmail = null;
private String mCancelText = null;
private String mSuccessText = null;
private TextValidatorGroup mTextValidatorGroup = new TextValidatorGroup();

public FeedbackDialogFragment() {
	setStyle(DialogFragment.STYLE_NORMAL, R.style.Material_Dialog_Fullscreen);
}

/**
 * Set the exception
 * @param exception the exception as a string
 */
public void setException(String exception) {
	mException = exception;
}

/**
 * Set the exception, will convert the exception to a string
 * @param e the exception that was thrown
 */
public void setException(Throwable e) {
	mException = Strings.exceptionToString(e);
}

/**
 * Set user information
 * @param name the user's real name (or username)
 * @param email email of the user
 */
public void setUserInfo(String name, String email) {
	mName = name;
	mEmail = email;
}

@Nullable
@Override
public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_feedback, container);

	mBugReport = (CheckBox) view.findViewById(R.id.bug_checkbox);
	mBugReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switchFeedbackType(isChecked ? FeedbackTypes.BUG_REPORT : FeedbackTypes.FEEDBACK);
		}
	});

	mTitle = (EditText) view.findViewById(R.id.title_edit);
	TextValidator titleValidator = new TextValidator(mTitle);
	titleValidator.addValidation(new RequiredWhenNotException());
	mTextValidatorGroup.add(titleValidator);

	mMessage = (EditText) view.findViewById(R.id.message_edit);
	TextValidator messageValidator = new TextValidator(mMessage);
	messageValidator.addValidation(new RequiredWhenNotException());
	mTextValidatorGroup.add(messageValidator);

	// Set dialog title
	mToolbar = (Toolbar) view.findViewById(R.id.feedback_toolbar);
	mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			back();
		}
	});
	mToolbar.inflateMenu(R.menu.menu_feedback);
	mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
		@Override
		public boolean onMenuItemClick(MenuItem item) {
			if (mTextValidatorGroup.validate()) {
				sendFeedback();
			}
			return true;
		}
	});


	// Color everything black
	if (isBlack()) {
		Resources resources = AppActivity.getActivity().getResources();
		@ColorInt int color = ColorHelper.getColor(resources, R.color.icon, null);
		mToolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);

		MenuItem sendMenuItem = mToolbar.getMenu().findItem(R.id.action_send);
		sendMenuItem.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
	}
	setCancelable(false);
	getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
				back();
				return true;
			}
			return false;
		}
	});

	// Set previous values
	if (savedInstanceState != null) {
		String title = savedInstanceState.getString(TITLE_KEY);
		if (title != null) {
			mTitle.setText(title);
		}

		String message = savedInstanceState.getString(MESSAGE_KEY);
		if (message != null) {
			mMessage.setText(message);
		}

		mException = savedInstanceState.getString(EXCEPTION_KEY);
		mName = savedInstanceState.getString(NAME_KEY);
		mEmail = savedInstanceState.getString(EMAIL_KEY);
	}

	if (mException != null) {
		mBugReport.setChecked(true);
		mBugReport.setEnabled(false);
		switchFeedbackType(FeedbackTypes.BUG_REPORT);
	} else {
		switchFeedbackType(FeedbackTypes.FEEDBACK);
	}

	return view;
}

/**
 * Update the texts to use depending on which type of feedback it is
 * @param feedbackType the type of feedback it is
 */
private void switchFeedbackType(FeedbackTypes feedbackType) {
	Resources resources = AppActivity.getActivity().getResources();
	switch (feedbackType) {
	case FEEDBACK:
		mToolbar.setTitle(resources.getString(R.string.feedback_header));
		mMessage.setHint(resources.getString(R.string.feedback_message_hint));
		mCancelText = resources.getString(R.string.feedback_cancel);
		mSuccessText = resources.getString(R.string.feedback_sent);
		break;
	case BUG_REPORT:
		mToolbar.setTitle(resources.getString(R.string.bug_report_header));
		mMessage.setHint(resources.getString(R.string.bug_report_message_hint));
		mCancelText = resources.getString(R.string.bug_report_cancel);
		mSuccessText = resources.getString(R.string.bug_report_sent);
		break;
	}
}

private void back() {
	// Prompt user if they want to discard the message
	if (!isEmpty()) {
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
		dialogBuilder.setMessage(mCancelText);
		dialogBuilder.setPositiveButton(R.string.discard, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dismiss();
			}
		});
		dialogBuilder.setNegativeButton(R.string.cancel, null);
		dialogBuilder.create().show();
	} else {
		dismiss();
	}
}

private void sendFeedback() {
	FeedbackRepo feedbackRepo = FeedbackRepo.getInstance();
	Feedback feedback = createFeedback();
	feedbackRepo.sendFeedback(feedback);
	
	SnackbarHelper.showSnackbar(mSuccessText);
	dismiss();
}

/**
 * Check if we should tint everything black
 * @return true if we want to tint everything black
 */
private boolean isBlack() {
	Resources resources = AppActivity.getActivity().getResources();
	int titleColor = ColorHelper.getColor(resources, R.color.toolbar_title, null);
	int black = ColorHelper.getColor(resources, R.color.text_color_black_primary, null);
	return titleColor == black;
}

private boolean isEmpty() {
	return mTitle.getText().toString().isEmpty() && mMessage.getText().toString().isEmpty();
}

private Feedback createFeedback() {
	Feedback feedback = new Feedback();
	
	feedback.setTitle(mTitle.getText().toString());
	feedback.setMessage(mMessage.getText().toString());
	feedback.setException(mException);
	feedback.setBugReport(mBugReport.isChecked());
	feedback.setName(mName);
	feedback.setEmail(mEmail);
	feedback.setDeviceInfo(getDeviceInfo());
	feedback.setAppVersion(BuildConfig.VERSION_NAME);
	
	int appStringId = AppActivity.getActivity().getApplicationInfo().labelRes;
	feedback.setAppName(AppActivity.getActivity().getResources().getString(appStringId));

	return feedback;
}

private String getDeviceInfo() {
	return "OS Version: " + System.getProperty("os.version") + "\n" +
			"API Level: " + Build.VERSION.SDK_INT + "\n" +
			"Device: " + Build.DEVICE + "\n" +
			"Model: " + Build.MODEL + "\n" +
			"Product: " + Build.PRODUCT;
}

@Override
public void onResume() {
	WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
	
	params.width = WindowManager.LayoutParams.MATCH_PARENT;
	params.height = WindowManager.LayoutParams.MATCH_PARENT;
	
	getDialog().getWindow().setAttributes(params);
	
	super.onResume();
}

@Override
public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	
	outState.putString(TITLE_KEY, mTitle.getText().toString());
	outState.putString(MESSAGE_KEY, mMessage.getText().toString());
	
	if (mException != null) {
		outState.putString(EXCEPTION_KEY, mException);
	}
	if (mName != null) {
		outState.putString(NAME_KEY, mName);
	}
	if (mEmail != null) {
		outState.putString(EMAIL_KEY, mEmail);
	}
}

private enum FeedbackTypes {
	FEEDBACK,
	BUG_REPORT
}

private class RequiredWhenNotException extends Validate {
	private RequiredWhenNotException() {
		super(AppActivity.getActivity().getResources().getString(R.string.validate_error_required));
	}

	@Override
	protected boolean validate(TextView textView, String text) {
		return mException != null || !text.isEmpty();
	}
}
}
