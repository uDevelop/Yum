package ru.inventos.yum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class NetStorage {
	private final static String CLASS_TAG = "NetStorage";
	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private final static String LOGIN_EMAIL = "/users/sign_in.json?user[email]=";
	private final static String LOGIN_PASSWORD = "user[password]=";
	private final static String LOGIN_STATUS_OK = "{\"success\":true";
	private final static String TERMINATE_SESSION_REQUEST = "/users/sign_out.json";
	private final static String GET_LUNCH_LIST_REQUEST = "/orders/get_index.json";
	private final static String GET_ORDERS_REQUEST = "/users/2.json";
	private final static String MAKE_ORDER_REQUEST = "/orders/create.json";
	private final static String MAKE_ORDER_ITEM_ID = "choose[]=";
	private final static String MAKE_ORDER_TIME = "order[expected_time]=";
	private final static String MAKE_ORDER_STATUS_OK = "{\"status\":\"ok\"}";
	private final static String GET_SERVER_STATUS_REQUEST = "/orders/get_block_status.json";
	private final static String SERVER_STATUS_READY = "{\"blocked\":false}";
	private final static String GET_DELIVERY_COST_REQUEST = "/orders/get_delivery_cost.json";
	private Context mContext;
	private SimpleDateFormat mFormatter;
	private static RequestQueue sQueue;
	private Toast mToast;
	
	public NetStorage(Context context) {
		mContext = context;
		mFormatter = new SimpleDateFormat(DATE_FORMAT);
		if (sQueue == null) {
			sQueue = new RequestQueue();
		}
		mToast = Toast.makeText(context, R.string.connection_fail, Consts.TOASTS_SHOW_DURATION);
	}
	
	private boolean isConnected() {
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
	
	public void getLunchList(LunchListReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(receiver, Consts.SERVER_ADDRESS 
						+ GET_LUNCH_LIST_REQUEST, NetworkStorage.GET_LUNCH_LIST);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
	}
	
	public void getImage(ImageReceiver receiver, String url) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(receiver, url, NetworkStorage.GET_LUNCH_IMAGE);
			sQueue.add(storage);			
		}
		else {			
			mToast.show();
		}			
	}
	
	public void getOrders(OrderReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(receiver, Consts.SERVER_ADDRESS 
					+ GET_ORDERS_REQUEST, NetworkStorage.GET_ORDERS);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
	}
	
	public void sendFeedback(String title, String body) {
		if (isConnected()) {
			//TODO:
			String request = Consts.SERVER_ADDRESS;
			NetworkStorage storage = new NetworkStorage(null, request, NetworkStorage.SEND_FEEDBACK);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
		
	}
	
	public void makeOrder(OrderStatusReceiver receiver, CartItem[] items, String time) {
		if (isConnected()) {
			String request = Consts.SERVER_ADDRESS + MAKE_ORDER_REQUEST + '?';
			for (CartItem item: items) {
				for (int i = 0; i < item.count; i++) {
					request = request + MAKE_ORDER_ITEM_ID + Integer.toString(item.id) + '&';
				}
			}
			String time2 = time.replace(' ', '+');
			request = request + MAKE_ORDER_TIME + time;
			NetworkStorage storage = new NetworkStorage(receiver, request, NetworkStorage.BUY_LUNCHES);
			sQueue.add(storage);
		}
		else {			
			receiver.receiveStatus(Consts.CHECKOUT_STATUS_NETWORK_FAIL);
		}					
	}
	
	public void getDeliveryPrice(DeliveryPriceReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(receiver, Consts.SERVER_ADDRESS 
						+ GET_DELIVERY_COST_REQUEST, NetworkStorage.GET_DELIVERY_PRICE);
			sQueue.add(storage);
		}
		else {
			receiver.receiveDeliveryPrice(-1, -1);
			mToast.show();
		}	
	}
	
	public void login(LoginReceiver receiver, String email, String password) {
		if (isConnected()) {
			String request = Consts.SERVER_ADDRESS + LOGIN_EMAIL + email 
						+ '&' + LOGIN_PASSWORD + password;
			NetworkStorage storage = new NetworkStorage(receiver, request, NetworkStorage.TRY_LOGIN);
			sQueue.add(storage);
		}
		else {
			receiver.receiveLoginStatus(LoginSystem.STATUS_EMPTY_DATA);
			mToast.show();
		}		
	}
	
	public void terminateSession() {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(null, Consts.SERVER_ADDRESS
						+TERMINATE_SESSION_REQUEST, NetworkStorage.TERMINATE_SESSION);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
	}
	
	public void getServerStatus(ServerStatusReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(receiver, Consts.SERVER_ADDRESS
						+ GET_SERVER_STATUS_REQUEST, NetworkStorage.GET_SERVER_STATUS);
			sQueue.add(storage);
		}
		else {			
			receiver.receiveServerStatus(false);
		}	
	}	
			
	public class NetworkStorage extends AsyncTask<Void, Void, Object> {
		final static byte GET_LUNCH_LIST = 0;
		final static byte GET_LUNCH_IMAGE = 1;
		final static byte GET_ORDERS = 2;
		final static byte SEND_FEEDBACK = 3;
		final static byte BUY_LUNCHES = 4;
		final static byte GET_DELIVERY_PRICE = 5;
		final static byte TRY_LOGIN = 6;
		final static byte TERMINATE_SESSION = 7;
		final static byte GET_SERVER_STATUS = 8;
		private final static String LUNCHES = "lunches";
		private final static String NAME = "name";
		private final static String PRICE = "cost";
		private final static String DESCRIPTION = "descr";
		private final static String WEIGHT = "weight";
		private final static String COUNT = "count";
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
		private final static String DELIVERY_COST = "delivery_cost";
		private final static String FREE_SUM = "free_delivery";
		
		
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
			HttpClient client = new DefaultHttpClient();
			String str = (String) mParams;
			if (mOperation == TRY_LOGIN || mOperation == BUY_LUNCHES) {				
				HttpPost request = new HttpPost(str); 
				try {
					HttpResponse response = client.execute(request);
					InputStream input = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					StringBuffer buf = new StringBuffer();
					String line = null;
					while ((line = reader.readLine()) != null) {
					    buf.append(line);
					}
					return buf.toString();
				} 
				catch (ClientProtocolException e) {
					Log.w(CLASS_TAG, e.getMessage());
					return null;				
				} 
				catch (IOException e) {
					Log.w(CLASS_TAG, e.getMessage());
					return null;	
				}				
			}
			else if (mOperation == TERMINATE_SESSION) {
				HttpDelete request = new HttpDelete(str); 
				try {
					client.execute(request).getEntity().getContent();
					return null;
				} 
				catch (ClientProtocolException e) {
					Log.w(CLASS_TAG, e.getMessage());
					return null;				
				} 
				catch (IOException e) {
					Log.w(CLASS_TAG, e.getMessage());
					return null;	
				}			
			}
			else if (mOperation == GET_LUNCH_LIST || mOperation == GET_ORDERS 
							|| mOperation == GET_SERVER_STATUS || mOperation == GET_DELIVERY_PRICE ) {				
				HttpGet request = new HttpGet(str); 
				try {
					HttpResponse response = client.execute(request);
					InputStream input = response.getEntity().getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(input));
					StringBuffer buf = new StringBuffer();
					String line = null;
					while ((line = reader.readLine()) != null) {
					    buf.append(line);
					}
					return buf.toString();
				} 
				catch (ClientProtocolException e) {
					Log.w(CLASS_TAG, e.getMessage());
					return null;				
				} 
				catch (IOException e) {
					Log.w(CLASS_TAG, e.getMessage());
					return null;	
				}				
			}
			else if (mOperation == GET_LUNCH_IMAGE) {
				return getBitmapByUrl((String) mParams);
			}				
			return null;
			
			
			
			/*switch (mOperation) {
			case 
			case GET_LUNCH_LIST:
				return "";
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
				
			case GET_SERVER_STATUS:
				try {
					Thread.sleep(1000);
				}
				finally {
					return null;
				}
			default:
				return null;
			}*/			           
        }
		
		@Override
        protected void onPostExecute(Object result) {
			sQueue.setFinished(this);
			switch(mOperation) {
			case GET_LUNCH_LIST:
				returnLunchList((String) result); 
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
        		returnOrderResult((String) result);			  
        		break;
			case GET_DELIVERY_PRICE:
        		returnDeliveryPrice((String) result);			  
        		break;
			case TRY_LOGIN:
				returnLoginStatus((String) result);
        		break;
			case GET_SERVER_STATUS:
        		returnServerStatus((String) result);			  
        		break;
			}        	
		}
		
		private void returnLoginStatus(String status) {
			if (status != null) {
				String str = status.substring(0, 15);
				if (str.equalsIgnoreCase(LOGIN_STATUS_OK)) {
					((LoginReceiver) mDataReceiver).receiveLoginStatus(LoginSystem.STATUS_OK);
				}
				else {
					((LoginReceiver) mDataReceiver).receiveLoginStatus(LoginSystem.STATUS_FAIL);
				}
			}
			else {
				((LoginReceiver) mDataReceiver).receiveLoginStatus(LoginSystem.STATUS_EMPTY_DATA);
				mToast.show();
			}			
		}
		
		private void returnLunchList(String input) {
			if (input != null) {
				LunchItem[] items = getLunchList(input);
				if (items != null) {
					((LunchListReceiver) mDataReceiver).receiveLunchList(items);
				}
			}
			else {
				mToast.show();
			}			
		}
		
		private void returnOrderResult(String input) {
			if (input.equalsIgnoreCase(MAKE_ORDER_STATUS_OK)) {
				((OrderStatusReceiver) mDataReceiver).receiveStatus(Consts.CHECKOUT_STATUS_OK);
			}
			else {
				((OrderStatusReceiver) mDataReceiver).receiveStatus(Consts.CHECKOUT_STATUS_ERROR);
			}
		}	
		
		private void returnServerStatus(String input) {
			if (input != null && input.equalsIgnoreCase(SERVER_STATUS_READY)) {
				((ServerStatusReceiver) mDataReceiver).receiveServerStatus(true);
			}
			else {
				((ServerStatusReceiver) mDataReceiver).receiveServerStatus(true); //поставить false
			}
		}
		
		private void returnDeliveryPrice(String input) {
			if (input != null) {
				try {
					JSONObject root = new JSONObject(input);
					int cost = root.getInt(DELIVERY_COST);
					int free_cost = root.getInt(FREE_SUM); 
					((DeliveryPriceReceiver) mDataReceiver).receiveDeliveryPrice(cost, free_cost);					
				}
				catch(Exception ex) {
					Log.e("NetStorage", ex.getMessage());
					((DeliveryPriceReceiver) mDataReceiver).receiveDeliveryPrice(-1, -1);
				}					
			}
			else {
				((DeliveryPriceReceiver) mDataReceiver).receiveDeliveryPrice(-1, -1);
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
					item.count = lunch.getInt(COUNT);
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
