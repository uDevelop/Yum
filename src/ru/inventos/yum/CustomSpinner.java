package ru.inventos.yum;

import org.holoeverywhere.widget.Spinner;
import org.holoeverywhere.widget.AdapterView.OnItemClickListener;

import android.content.Context;
import android.util.AttributeSet;

public class CustomSpinner extends Spinner {
	
	public CustomSpinner(Context context) {
		super(context);
	}
    
	public CustomSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	public CustomSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyle, int mode) {
		super(context, attrs, defStyle, mode);
	}

	public CustomSpinner(Context context, int mode) {
		super(context, mode);
	}
	
}
