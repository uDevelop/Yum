package ru.inventos.yum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class NetStorage {
	private Context mContext;
	
	public NetStorage(Context context) {
		mContext = context;
	}
	
	public boolean isConnected() {
		ConnectivityManager cm = 
				(ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public void getLunchList(MainListAdapter receiver) {
		NetworkStorage storage = new NetworkStorage(receiver, null, NetworkStorage.GET_LUNCH_LIST);
		storage.execute();
	}
	
	public void getImage(ImageReceiver receiver, String url) {
		NetworkStorage storage = new NetworkStorage(receiver, url, NetworkStorage.GET_LUNCH_IMAGE);
		storage.execute();
	}
	
		
	private class NetworkStorage extends AsyncTask<Void, Void, Object> {
		final static byte GET_LUNCH_LIST = 0;
		final static byte GET_LUNCH_IMAGE = 1;
		private final static String LUNCHES = "lunches";
		private final static String NAME = "name";
		private final static String PRICE = "cost";
		private final static String DESCRIPTION = "descr";
		private final static String WEIGHT = "weight";
		private final static String LUNCH_TYPE = "lunch_type";
		private final static String LUNCH_TYPE_NAME = "name";	
		private final static String IMAGE = "image_for_api";
		private Object mDataReceiver;
		private byte mOperation;
		private Object mParams;
		
		
		public NetworkStorage(Object dataReceiver, Object params, byte operation) {			
			super();
			mDataReceiver = dataReceiver;
			mOperation = operation;
			mParams = params;
		}
		
		@Override
        protected Object doInBackground(Void... params) {
			switch (mOperation) {
			case GET_LUNCH_IMAGE:
				return getBitmapByUrl((String) mParams);
			case GET_LUNCH_LIST:
				return getTestList();			
			default:
				return null;
			}			           
        }
		
		@Override
        protected void onPostExecute(Object result) {
			switch(mOperation) {
			case GET_LUNCH_LIST:
				LunchItem[] list = getLunchList((String) result);
				((MainListAdapter) mDataReceiver).UpdateList(list); 
				break;
			case GET_LUNCH_IMAGE:
				Bitmap bmp = (Bitmap) result;
				((ImageReceiver) mDataReceiver).receiveImage(bmp);  
				break;
			}	
		}
		
		private String getTestList() {
			try {
				InputStream input = mContext.getAssets().open("test_data");
				BufferedReader reader = null;		
				reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
				StringBuffer buf = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buf.append(line);
				}
				return buf.toString();
			}
			catch(Exception ex) {
				Log.e("NetStorage", ex.getMessage());
				return null;
			}	
		}
		
		private  LunchItem[] getLunchList(String dataString) {
			LunchItem[] result = null;
			try {
				JSONObject root = new JSONObject(dataString);
				JSONArray lunches = root.getJSONArray("lunches");
				int count = lunches.length();
				result = new LunchItem[count];
				JSONObject lunch = null;
				LunchItem item = null;
				for(int i = 0; i < count; i++) {
					lunch = lunches.getJSONObject(i);
					item = new LunchItem();
					item.id = i + 1;
					item.name = lunch.getString(NAME);
					item.description = lunch.getString(DESCRIPTION);
					//item.count = (int) Math.round(lunch.getDouble(COUNT));
					item.count = 5;
					item.price = (float) lunch.getDouble(PRICE);
					item.weight = (int) Math.round(lunch.getDouble(WEIGHT));
					item.image = lunch.getString(IMAGE);
					item.category = lunch.getJSONObject(LUNCH_TYPE).getString(LUNCH_TYPE_NAME);					 
					result[i] = item;
				}
			}
			catch(Exception ex) {
				Log.e("NetStorage", ex.getMessage());
				return null;
			}						   	
			return result;
		}
		
		private Bitmap getBitmapByUrl(String url) {
			InputStream input = null;
			Bitmap image  = null;
			try {
				URL u = new URL(url);
				u.openConnection(); 
				input = u.openStream();
				image = BitmapFactory.decodeStream(input);	
				input.close();				
			} 
			catch (MalformedURLException ex) {
				Log.e("NetStorage", ex.getMessage());
			} 
			catch (IOException ex) {
				Log.e("NetStorage", ex.getMessage());
			}			
			return image; 
		}
		
		
	}
	
}
