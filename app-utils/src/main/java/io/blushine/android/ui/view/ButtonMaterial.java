package io.blushine.android.ui.view;

import android.content.Context;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

import io.blushine.android.ui.Fonts;

/**
 * Material button with correct font style 'sans-serif-medium' that works on all Android versions,
 * including pre-Lollipop
 */
public class ButtonMaterial extends AppCompatButton {
public ButtonMaterial(Context context) {
	super(context);
	init();
}

private void init() {
	setTypeface(Fonts.MEDIUM.getTypeface());
}

public ButtonMaterial(Context context, AttributeSet attrs) {
	super(context, attrs);
	init();
}

public ButtonMaterial(Context context, AttributeSet attrs, int defStyleAttr) {
	super(context, attrs, defStyleAttr);
	init();
}
}
