package ru.inventos.yum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

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
	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private Context mContext;
	private SimpleDateFormat mFormatter;
	
	public NetStorage(Context context) {
		mContext = context;
		mFormatter = new SimpleDateFormat(DATE_FORMAT);
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
	
	public void getOrders(OrderReceiver receiver) {
		NetworkStorage storage = new NetworkStorage(receiver, null, NetworkStorage.GET_ORDERS);
		storage.execute();
	}
	
	public void sendFeedback(String title, String body) {
		Feedback feedback = new Feedback();
		feedback.body = body;
		feedback.title = title;
		NetworkStorage storage = new NetworkStorage(null, feedback, NetworkStorage.SEND_FEEDBACK);
		storage.execute();
	}
	
	public void makeOrder(OrderStatusReceiver receiver, CartItem[] items, String time) {
		//TODO:
		String buyRequest = null;
		NetworkStorage storage = new NetworkStorage(receiver, buyRequest, NetworkStorage.BUY_LUNCHES);
		storage.execute();		
	}
	
	public void getDeliveryPrice(DeliveryPriceReceiver receiver) {
		NetworkStorage storage = new NetworkStorage(receiver, null, NetworkStorage.GET_DELIVERY_PRICE);
		storage.execute();
	}
	
	public void login(LoginReceiver receiver, String email, String password) {
		NetworkStorage storage = new NetworkStorage(receiver, null, NetworkStorage.TRY_LOGIN);
		storage.execute();
	}
	
	public void terminateSession() {
		NetworkStorage storage = new NetworkStorage(null, null, NetworkStorage.TERMINATE_SESSION);
		storage.execute();
	}
	
	private static class Feedback {
		String title;
		String body;
	}
	
		
	private class NetworkStorage extends AsyncTask<Void, Void, Object> {
		final static byte GET_LUNCH_LIST = 0;
		final static byte GET_LUNCH_IMAGE = 1;
		final static byte GET_ORDERS = 2;
		final static byte SEND_FEEDBACK = 3;
		final static byte BUY_LUNCHES = 4;
		final static byte GET_DELIVERY_PRICE = 5;
		final static byte TRY_LOGIN = 6;
		final static byte TERMINATE_SESSION = 7;
		private final static String LUNCHES = "lunches";
		private final static String NAME = "name";
		private final static String PRICE = "cost";
		private final static String DESCRIPTION = "descr";
		private final static String WEIGHT = "weight";
		private final static String LUNCH_TYPE = "lunch_type";
		private final static String LUNCH_TYPE_NAME = "name";	
		private final static String IMAGE = "image_for_api";
		private final static String ORDERS = "orders";
		private final static String TIME = "create_date";
		private final static String COST = "cost";
		private final static String STATUS = "status";
		private final static String STATUS_COMPLETE = "complete";
		private final static String STATUS_ACTIVE = "active";
		private final static String STATUS_CANCEL = "cancel";
		private final static String USER = "user";
		
		
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
			case GET_ORDERS:
				return getOrderList();
			case SEND_FEEDBACK:
				Feedback feedback = (Feedback) mParams; 
				Log.w(feedback.title, feedback.body);
			case BUY_LUNCHES:
				try {
					Thread.sleep(3000);
				}
				finally {
					return null;
				}
			case GET_DELIVERY_PRICE:
				try {
					Thread.sleep(3000);
				}
				finally {
					return null;
				}
			case TRY_LOGIN:
				try {
					Thread.sleep(3000);
				}
				finally {
					return null;
				}				
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
			case GET_ORDERS:
        		OrderItem[] orders = getOrders((String) result);
        		((OrderReceiver) mDataReceiver).receiveOrders(orders);			  
        		break;
			case BUY_LUNCHES:
        		((OrderStatusReceiver) mDataReceiver).receiveStatus(Math.random() > 0.5f);			  
        		break;
			case GET_DELIVERY_PRICE:
        		((DeliveryPriceReceiver) mDataReceiver).receiveDeliveryPrice(100f, 250f);			  
        		break;
			case TRY_LOGIN:
        		((LoginReceiver) mDataReceiver).receiveLoginStatus(LoginSystem.STATUS_OK);			  
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
		
		private String getOrderList() {
			try {
				InputStream input = mContext.getAssets().open("test_orders");
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
				JSONArray lunches = root.getJSONArray(LUNCHES); 
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
		
		private  OrderItem[] getOrders(String dataString) {
			OrderItem[] result = null;
			try {
				JSONObject root = new JSONObject(dataString);
				JSONObject user = root.getJSONObject(USER);
				JSONArray orders = user.getJSONArray(ORDERS); 
				int count = orders.length();
				if (count > 0) {
					result = new OrderItem[count];
					JSONObject order = null;
					OrderItem item = null;
					String status= null;
					String time;
					for(int i = 0; i < count; i++) {
						order = orders.getJSONObject(i);
						item = new OrderItem();
						time = order.getString(TIME);
						item.time = mFormatter.parse(time.substring(0, 22) + time.substring(23)); 
						item.cost = (float) order.getDouble(COST);
						status = order.getString(STATUS);  
						if (status.equals(STATUS_COMPLETE)) {
							item.status = Consts.ORDER_STATUS_COMPLETE;
						}
						else if (status.equals(STATUS_ACTIVE)) {
							item.status = Consts.ORDER_STATUS_ACTIVE;
						}
						else if (status.equals(STATUS_CANCEL)) {
							item.status = Consts.ORDER_STATUS_CANCEL;
						}
						result[i] = item;
					}
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
