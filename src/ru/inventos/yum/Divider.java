package ru.inventos.yum;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Divider extends View {
	
	private int mColor;	
	
	public Divider(Context context) {
		super(context);	
		mColor = Color.BLACK;
	}
	
	public Divider(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.Divider);
        mColor = arr.getColor(R.styleable.Divider_fill_color, Color.BLACK);		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(mColor);
		canvas.drawRect(canvas.getClipBounds(), paint);
		invalidate();
	}

}
