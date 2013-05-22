package ru.inventos.yum.adapters;

import ru.inventos.yum.R;
import ru.inventos.yum.R.drawable;
import ru.inventos.yum.R.id;
import ru.inventos.yum.R.layout;
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
	private Holder[] holders;
				
	public MainMenuAdapter(Context context, int names, int pictures) {
		LayoutInflater inflater = LayoutInflater.from(context);
		Resources res = context.getResources();
		String[] textArray = res.getStringArray(names);
		String[] picNames = res.getStringArray(pictures);
		int count = textArray.length;  
		items = new View[count];
		holders = new Holder[count];
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
	        Holder holder = new Holder();
	        holder.picture = img;
	        holder.background = (ImageView) v.findViewById(R.id.main_menu_item_selected_background);
	        holder.background2 = (ImageView) v.findViewById(R.id.main_menu_item_selected_background2);	        
	        items[i] = v;
	        holders[i] = holder;
		}
		
	
	}
	
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		View view = items[position];
		if (parent.getId() == R.id.main_menu_menulist) {
			Integer pos = (Integer) parent.getTag();
			Holder holder = holders[position];
			if ((pos!= null) && (pos.intValue() == position)) {	
				holder.background.setVisibility(View.VISIBLE);
				holder.background2.setVisibility(View.VISIBLE);
				holder.picture.setSelected(true);				
			}
			else {
				holder.background.setVisibility(View.INVISIBLE);
				holder.background2.setVisibility(View.INVISIBLE);
				holder.picture.setSelected(false);
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
	
	class Holder {
		ImageView background;
		ImageView background2;
		ImageView picture;
	}
}
