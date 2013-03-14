package ru.inventos.yum;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Devider extends View {
	
	private int mColor;	
	
	public Devider(Context context) {
		super(context);	
		mColor = this.getResources().getColor(R.color.black);
	}
	
	public Devider(Context context, AttributeSet attrs) {
		super(context, attrs);
		mColor = this.getResources().getColor(R.color.black);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(mColor);
		canvas.drawRect(canvas.getClipBounds(), paint);
		invalidate();
	}

}
