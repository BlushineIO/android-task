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
private static final String NAME_EDIT_KEY = "name_edit";
private static final String EMAIL_EDIT_KEY = "email_edit";
private static final String TITLE_EDIT_KEY = "title_edit";
private static final String MESSAGE_EDIT_KEY = "message_edit";
private static final String EXCEPTION_KEY = "exception";
private static final String NAME_KEY = "name";
private static final String EMAIL_KEY = "email";
private CheckBox mBugReport = null;
private EditText mTitleEdit = null;
private EditText mMessageEdit = null;
private EditText mNameEdit = null;
private EditText mEmailEdit = null;
private Toolbar mToolbar = null;
private String mException = null;
private String mPresetName = null;
private String mPresetEmail = null;
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
	mPresetName = name;
	mPresetEmail = email;
}

@Nullable
@Override
public View onCreateViewImpl(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
	View view = inflater.inflate(R.layout.fragment_feedback, container, false);
	
	mBugReport = (CheckBox) view.findViewById(R.id.bug_checkbox);
	mBugReport.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			switchFeedbackType(isChecked ? FeedbackTypes.BUG_REPORT : FeedbackTypes.FEEDBACK);
		}
	});
	
	// Name
	mNameEdit = (EditText) view.findViewById(R.id.name_edit);
	if (mPresetName == null) {
		mValidatorGroup.add(new TextValidator.Builder(mNameEdit)
				.addValidation(new RequiredWhenNotException())
				.build()
		);
	}
	// Hide name as this has already been set
	else {
		mNameEdit.setVisibility(View.GONE);
		View nameDivider = view.findViewById(R.id.name_divider);
		nameDivider.setVisibility(View.GONE);
	}
	
	// Email
	mEmailEdit = (EditText) view.findViewById(R.id.email_edit);
	if (mPresetEmail == null) {
		mValidatorGroup.add(new TextValidator.Builder(mEmailEdit)
				.addValidation(new RequiredWhenNotException())
				.build()
		);
	}
	// Hide email as this has already been set
	else {
		mEmailEdit.setVisibility(View.GONE);
		View emailDivider = view.findViewById(R.id.email_divider);
		emailDivider.setVisibility(View.GONE);
	}
	
	// Title
	mTitleEdit = (EditText) view.findViewById(R.id.title_edit);
	mValidatorGroup.add(new TextValidator
			.Builder(mTitleEdit)
			.addValidation(new RequiredWhenNotException())
			.build()
	);
	
	// Message
	mMessageEdit = (EditText) view.findViewById(R.id.message_edit);
	mValidatorGroup.add(new TextValidator
			.Builder(mMessageEdit)
			.addValidation(new RequiredWhenNotException())
			.build()
	);
	
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
		String name = savedInstanceState.getString(NAME_EDIT_KEY);
		if (name != null) {
			mNameEdit.setText(name);
		}
		
		String email = savedInstanceState.getString(EMAIL_EDIT_KEY);
		if (email != null) {
			mEmailEdit.setText(email);
		}
		
		String title = savedInstanceState.getString(TITLE_EDIT_KEY);
		if (title != null) {
			mTitleEdit.setText(title);
		}
		
		String message = savedInstanceState.getString(MESSAGE_EDIT_KEY);
		if (message != null) {
			mMessageEdit.setText(message);
		}
		
		mException = savedInstanceState.getString(EXCEPTION_KEY);
		mPresetName = savedInstanceState.getString(NAME_KEY);
		mPresetEmail = savedInstanceState.getString(EMAIL_KEY);
	}
	
	// Set exception and force bug report
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
		mNameEdit.setHint(R.string.feedback_name_hint);
		mEmailEdit.setHint(R.string.feedback_email_hint);
		mTitleEdit.setHint(R.string.feedback_title_hint);
		mMessageEdit.setHint(resources.getString(R.string.feedback_message_hint));
		setBackMessage(R.string.feedback_cancel);
		mSuccessText = R.string.feedback_sent;
		break;
	case BUG_REPORT:
		mToolbar.setTitle(resources.getString(R.string.bug_report_header));
		mNameEdit.setHint(R.string.feedback_name_optional_hint);
		mEmailEdit.setHint(R.string.feedback_email_optional_hint);
		mTitleEdit.setHint(R.string.feedback_title_optional_hint);
		mMessageEdit.setHint(resources.getString(R.string.bug_report_message_hint));
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
	
	feedback.setTitle(mTitleEdit.getText().toString());
	feedback.setMessage(mMessageEdit.getText().toString());
	feedback.setException(mException);
	feedback.setBugReport(mBugReport.isChecked());
	if (mPresetName != null) {
		feedback.setName(mPresetName);
	} else {
		feedback.setName(mNameEdit.getText().toString());
	}
	if (mPresetEmail != null) {
		feedback.setEmail(mPresetEmail);
	} else {
		feedback.setEmail(mEmailEdit.getText().toString());
	}
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

protected boolean isChanged() {
	return !mTitleEdit.getText().toString().isEmpty() || !mMessageEdit.getText().toString().isEmpty();
}

@Override
public void onSaveInstanceState(Bundle outState) {
	super.onSaveInstanceState(outState);
	
	outState.putString(NAME_EDIT_KEY, mNameEdit.getText().toString());
	outState.putString(EMAIL_EDIT_KEY, mEmailEdit.getText().toString());
	outState.putString(TITLE_EDIT_KEY, mTitleEdit.getText().toString());
	outState.putString(MESSAGE_EDIT_KEY, mMessageEdit.getText().toString());
	
	if (mException != null) {
		outState.putString(EXCEPTION_KEY, mException);
	}
	if (mPresetName != null) {
		outState.putString(NAME_KEY, mPresetName);
	}
	if (mPresetEmail != null) {
		outState.putString(EMAIL_KEY, mPresetEmail);
	}
}

private enum FeedbackTypes {
	FEEDBACK,
	BUG_REPORT
}

private class RequiredWhenNotException extends Validate<TextView> {
	private RequiredWhenNotException() {
		super(AppActivity.getActivity().getResources().getString(R.string.validate_required_no_hint));
	}
	
	@Override
	public boolean validate(TextView field) {
		return mException != null || !field.getText().toString().isEmpty();
	}
}
}
