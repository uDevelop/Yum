package ru.inventos.yum;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainMenuAdapter extends BaseAdapter {
	private View[] items; 
				
	public MainMenuAdapter(Context context, int names, int pictures) {
		LayoutInflater inflater = LayoutInflater.from(context);
		Resources res = context.getResources();
		String[] textArray = res.getStringArray(names);
		String[] picNames = res.getStringArray(pictures);
		int count = textArray.length;  
		items = new View[count];
		View v = null;
		TextView text = null;
		ImageView img = null;
		Class<R.drawable> dres = R.drawable.class;
        int imageId = 0;
		for(int i = 0; i <count; i++) {
			v = inflater.inflate(R.layout.main_menu_item, null);
			v.setTag(textArray[i]);
			text = (TextView) v.findViewById(R.id.main_menu_item_text);
			text.setText(textArray[i]);
			try {
	        	imageId= dres.getField(picNames[i]).getInt(null);
	        }
	        catch (Exception ex) {
	        	Log.e("MainMenuAdapter", ex.getMessage());
	        }
			img = (ImageView) v.findViewById(R.id.main_menu_item_image);
	        img.setImageResource(imageId);	
	        items[i] = v;
		}
		
	
	}
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = items[position];
		if (parent.getId() == R.id.main_menu_menulist) {
			Integer pos = (Integer) parent.getTag();
			if ((pos!= null) && (pos.intValue() == position)) {				
				view.setBackgroundResource(R.drawable.pic);
			}
			else {
				view.setBackgroundResource(0);
			}
		}		
        return view;
    }
	
	@Override
	  public int getCount() {
	    return items.length; 
	  }

	 
	@Override
	public Object getItem(int position) {
		return items[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
