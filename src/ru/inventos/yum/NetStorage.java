package ru.inventos.yum;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
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
		NetworkStorage storage = new NetworkStorage(receiver, NetworkStorage.GET_LUNCH_LIST);
		storage.execute();
	}
	
		
	private class NetworkStorage extends AsyncTask<Void, Void, String> {
		final static byte GET_LUNCH_LIST = 0;
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
		
		
		public NetworkStorage(Object dataReceiver, byte operation) {			
			super();
			mDataReceiver = dataReceiver;
			mOperation = operation;			
		}
		
		@Override
        protected String doInBackground(Void... params) {
			//TODO: pull from server
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
		
		@Override
        protected void onPostExecute(String result) {
			switch(mOperation) {
			case GET_LUNCH_LIST:
				LunchItem[] list = getLunchList(result);
				((MainListAdapter) mDataReceiver).UpdateList(list); 
				break;
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
		
		
	}
	
}
