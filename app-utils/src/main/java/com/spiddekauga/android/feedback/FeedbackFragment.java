package com.spiddekauga.android.feedback;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.spiddekauga.android.AppActivity;
import com.spiddekauga.android.AppFragment;
import com.spiddekauga.android.BuildConfig;
import com.spiddekauga.android.R;
import com.spiddekauga.android.ui.SnackbarHelper;
import com.spiddekauga.android.validate.TextValidator;
import com.spiddekauga.android.validate.Validate;
import com.spiddekauga.android.validate.ValidatorGroup;
import com.spiddekauga.cloudshine.feedbackApi.model.Feedback;
import com.spiddekauga.utils.Strings;

/**
 * Displays a dialog for sending menu_feedback to Spiddekauga's mail
 */
public class FeedbackFragment extends AppFragment {
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
@StringRes
private int mSuccessText = -1;
private ValidatorGroup mValidatorGroup = new ValidatorGroup();

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
	View view = inflater.inflate(R.layout.fragment_feedback, container, false);

	mBugReport = (CheckBox) view.findViewById(R.id.bug_checkbox);
	mBugReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switchFeedbackType(isChecked ? FeedbackTypes.BUG_REPORT : FeedbackTypes.FEEDBACK);
		}
	});

	mTitle = (EditText) view.findViewById(R.id.title_edit);
	TextValidator titleValidator = new TextValidator
			.Builder(mTitle)
			.addValidation(new RequiredWhenNotException())
			.build();
	mValidatorGroup.add(titleValidator);

	mMessage = (EditText) view.findViewById(R.id.message_edit);
	TextValidator messageValidator = new TextValidator
			.Builder(mMessage)
			.addValidation(new RequiredWhenNotException())
			.build();
	mValidatorGroup.add(messageValidator);

	// Set dialog title
	mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
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
			if (mValidatorGroup.validate()) {
				sendFeedback();
			}
			return true;
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
 * Update the texts to use depending on which type of menu_feedback it is
 * @param feedbackType the type of menu_feedback it is
 */
private void switchFeedbackType(FeedbackTypes feedbackType) {
	Resources resources = AppActivity.getActivity().getResources();
	switch (feedbackType) {
	case FEEDBACK:
		mToolbar.setTitle(resources.getString(R.string.feedback_header));
		mMessage.setHint(resources.getString(R.string.feedback_message_hint));
		setBackMessage(R.string.feedback_cancel);
		mSuccessText = R.string.feedback_sent;
		break;
	case BUG_REPORT:
		mToolbar.setTitle(resources.getString(R.string.bug_report_header));
		mMessage.setHint(resources.getString(R.string.bug_report_message_hint));
		setBackMessage(R.string.bug_report_cancel);
		mSuccessText = R.string.bug_report_sent;
		break;
	}
}

private void sendFeedback() {
	FeedbackRepo feedbackRepo = FeedbackRepo.getInstance();
	Feedback feedback = createFeedback();
	feedbackRepo.sendFeedback(feedback);
	
	SnackbarHelper.showSnackbar(mSuccessText);
	dismiss();
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

protected boolean isChanged() {
	return !mTitle.getText().toString().isEmpty() || !mMessage.getText().toString().isEmpty();
}

private enum FeedbackTypes {
	FEEDBACK,
	BUG_REPORT
}

private class RequiredWhenNotException extends Validate<TextView> {
	private RequiredWhenNotException() {
		super(AppActivity.getActivity().getResources().getString(R.string.validate_required));
	}

	@Override
	public boolean validate(TextView field) {
		return mException != null || !field.getText().toString().isEmpty();
	}
}
}
