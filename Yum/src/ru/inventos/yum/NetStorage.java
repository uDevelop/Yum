package ru.inventos.yum;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.json.JSONArray;
import org.json.JSONObject;

import ru.inventos.yum.interfaces.DeliveryPriceReceiver;
import ru.inventos.yum.interfaces.ImageReceiver;
import ru.inventos.yum.interfaces.LoginReceiver;
import ru.inventos.yum.interfaces.LunchListReceiver;
import ru.inventos.yum.interfaces.OrderReceiver;
import ru.inventos.yum.interfaces.OrderStatusReceiver;
import ru.inventos.yum.interfaces.ServerStatusReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class NetStorage {
	private final static String CLASS_TAG = "NetStorage";
	private final static String AUTH_FILE_NAME = "data";
	private final static String AUTH_COOKIE_NAME = "_order_lunch_session";
	private final static String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	private final static String LOGIN_EMAIL = "/users/sign_in.json?user[email]=";
	private final static String LOGIN_PASSWORD = "user[password]=";
	private final static String LOGIN_STATUS_OK = "{\"success\":true";
	private final static String TERMINATE_SESSION_REQUEST = "/users/sign_out.json";
	private final static String GET_LUNCH_LIST_REQUEST = "/orders/get_index.json";
	private final static String GET_ORDERS_REQUEST = "/users/2.json";
	private final static String MAKE_ORDER_REQUEST = "/orders.json";
	private final static String MAKE_ORDER_ITEM_ID = "lunch[choose][]=";
	private final static String MAKE_ORDER_ITEM_COUNT = "lunch[counts][";
	private final static String MAKE_ORDER_TIME = "order[expected_time]=t";
	private final static String STATUS_OK = "{\"status\":\"ok\"}";
	private final static String GET_SERVER_STATUS_REQUEST = "/orders/get_block_status.json";
	private final static String SERVER_STATUS_READY = "{\"blocked\":false}";
	private final static String GET_DELIVERY_COST_REQUEST = "/orders/get_delivery_cost.json";
	private final static String SEND_FEEDBACK_REQUEST = "/feedbacks.json?";
	private final static String SEND_FEEDBACK_REQUEST_TITLE = "reason=";
	private final static String SEND_FEEDBACK_REQUEST_BODY = "&text=";
	private final static String AUTOLOGIN = "/users/get_login_status.json";
	private Context mContext;
	private SimpleDateFormat mFormatter;
	private static RequestQueue sQueue;
	private Toast mToast;
	private Toast sendFeedbackFail;
	private Toast sendFeedbackOk;
	private static BasicCookieStore sCookie; 
	private String mFolderName;
		
	public NetStorage(Context context) {
		mContext = context;
		mFormatter = new SimpleDateFormat(DATE_FORMAT);
		mFolderName = Environment.getDataDirectory() + "/data/" + context.getPackageName();
		if (sQueue == null) {
			sQueue = new RequestQueue();
		}
		if (sCookie == null) {
			sCookie = new BasicCookieStore(); 
			loadAuthToCookie(sCookie, mFolderName);
		}
		mToast = Toast.makeText(context, R.string.connection_fail, Consts.TOASTS_SHOW_DURATION);
		sendFeedbackFail = Toast.makeText(mContext, R.string.feedback_send_fail, 
					Consts.TOASTS_SHOW_DURATION);
		sendFeedbackOk = Toast.makeText(mContext, R.string.feedback_send_ok, 
				Consts.TOASTS_SHOW_DURATION);
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
	
	private final static void loadAuthToCookie(CookieStore cookieStore, String folderName) {
		File file = new File(folderName + '/' + AUTH_FILE_NAME);
		if (file.exists()) {
			FileReader reader = null;		
			try {
				reader = new FileReader(file);
				StringBuilder buf = new StringBuilder();
				int c = reader.read();
				while (c != -1) {
					buf.append((char) c);
					c = reader.read();
				}
				String token = buf.toString();
				BasicClientCookie auth = new BasicClientCookie(AUTH_COOKIE_NAME, token);
				auth.setDomain(Consts.SERVER_DOMAIN);
				auth.setPath("/");
				cookieStore.addCookie(auth);				
			}
			catch (FileNotFoundException ex) {
				Log.w(CLASS_TAG, ex.getMessage());
			}
			catch (IOException ex) {
				Log.w(CLASS_TAG, ex.getMessage());
			}
			finally { 
				try {
					reader.close();
				}
				catch (IOException ex){
					Log.w(CLASS_TAG, ex.getMessage());
				}
			}				
		}
	}
	
	private final static void saveAuthToFile(CookieStore cookieStore, String folderName) {
		List<Cookie> cookies = cookieStore.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(AUTH_COOKIE_NAME)) {
				File dir = new File(folderName); 
		        if (!dir.exists()) {	
		        	dir.mkdir();
		        }
		        File file = new File(folderName + '/' + AUTH_FILE_NAME);
		        file.delete();
		        FileWriter writer = null;
		        try {
		        	file.createNewFile();  
		        	writer = new FileWriter(file);
		           	writer.write(cookie.getValue());
		           	writer.flush();		           	
		        }
		        catch (IOException ex) {
		        	Log.e(CLASS_TAG, ex.getMessage());
		        }
		        finally {
		        	try {
						writer.close();
					}
					catch (IOException ex){
						Log.w(CLASS_TAG, ex.getMessage());
					}
		        }
		        return;
			}
		}
	}
	
	private void dropAuth(CookieStore cookieStore) {
		cookieStore.clear();
	}
	
	
	public void getLunchList(LunchListReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, Consts.SERVER_ADDRESS 
						+ GET_LUNCH_LIST_REQUEST, NetworkStorage.GET_LUNCH_LIST);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
	}
	
	public void getImage(ImageReceiver receiver, String url) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, url, NetworkStorage.GET_LUNCH_IMAGE);
			sQueue.add(storage);			
		}
		else {			
			mToast.show();
		}			
	}
	
	public void getOrders(OrderReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, Consts.SERVER_ADDRESS 
					+ GET_ORDERS_REQUEST, NetworkStorage.GET_ORDERS);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
	}
	
	public void sendFeedback(String title, String body) {
		if (isConnected()) {
			String trueTitle = title.replace(' ', '+');
			String trueBody = body.replace(' ', '+');
			String request = Consts.SERVER_ADDRESS + SEND_FEEDBACK_REQUEST 
						+ SEND_FEEDBACK_REQUEST_TITLE + trueTitle 
						+  SEND_FEEDBACK_REQUEST_BODY + trueBody;
			NetworkStorage storage = new NetworkStorage(sCookie, null, request, NetworkStorage.SEND_FEEDBACK);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
		
	}
	
	public void makeOrder(OrderStatusReceiver receiver, CartItem[] items) {
		if (isConnected()) {
			String request = Consts.SERVER_ADDRESS + MAKE_ORDER_REQUEST + '?';
			for (CartItem item: items) {
				request = request + MAKE_ORDER_ITEM_ID + Integer.toString(item.id) + '&';				
			}
			for (CartItem item: items) {
				request = request + MAKE_ORDER_ITEM_COUNT + Integer.toString(item.id) + "]="
						+ Integer.toString(item.count) + '&';				
			}
			request = request.substring(0, request.length() - 1);
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, request, NetworkStorage.BUY_LUNCHES);
			sQueue.add(storage);
		}
		else {			
			receiver.receiveStatus(Consts.CHECKOUT_STATUS_NETWORK_FAIL);
		}					
	}
	
	public void getDeliveryPrice(DeliveryPriceReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, Consts.SERVER_ADDRESS 
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
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, request, NetworkStorage.TRY_LOGIN);
			sQueue.add(storage);
		}
		else {
			receiver.receiveLoginStatus(Consts.LOGIN_STATUS_EMPTY_DATA);
			mToast.show();
		}		
	}
	
	public void autoLogin(LoginReceiver receiver) {
		if (isConnected()) {
			String request = Consts.SERVER_ADDRESS + AUTOLOGIN;
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, request, NetworkStorage.AUTOLOGIN);
			sQueue.add(storage);
		}
		else {
			receiver.receiveLoginStatus(Consts.LOGIN_STATUS_EMPTY_DATA);
			mToast.show();
		}		
	}
	
	public void terminateSession() {
		dropAuth(sCookie);
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(sCookie, null, Consts.SERVER_ADDRESS
						+TERMINATE_SESSION_REQUEST, NetworkStorage.TERMINATE_SESSION);
			sQueue.add(storage);
		}
		else {			
			mToast.show();
		}		
	}
	
	public void getServerStatus(ServerStatusReceiver receiver) {
		if (isConnected()) {
			NetworkStorage storage = new NetworkStorage(sCookie, receiver, Consts.SERVER_ADDRESS
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
		final static byte AUTOLOGIN = 9;
		private final static String LUNCHES = "lunches";
		private final static String ID = "id";
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
		private final static String STATUS_CANCEL = "cancelled";
		private final static String USER = "user";
		private final static String DELIVERY_COST = "delivery_cost";
		private final static String FREE_SUM = "free_delivery";
		
		
		private Object mDataReceiver;
		private byte mOperation;
		private Object mParams;
		private BasicHttpContext mHttpContext;
		
		
		public NetworkStorage(BasicCookieStore cookie, Object dataReceiver, Object params, byte operation) {			
			super();
			mDataReceiver = dataReceiver;
			mOperation = operation;
			mParams = params;
			mHttpContext = new BasicHttpContext(); 
			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, cookie);	
			
		}
		
		@Override
        protected Object doInBackground(Void... params) {
			HttpClient client = new DefaultHttpClient();  
			String str = (String) mParams;
			if (mOperation == TRY_LOGIN || mOperation == BUY_LUNCHES || mOperation == SEND_FEEDBACK) {				
				HttpPost request = new HttpPost(str);
				try {
					HttpResponse response = client.execute(request, mHttpContext);
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
					client.execute(request, mHttpContext).getEntity().getContent();
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
							|| mOperation == GET_SERVER_STATUS || mOperation == GET_DELIVERY_PRICE 
							|| mOperation == AUTOLOGIN) {				
				HttpGet request = new HttpGet(str); 
				try {
					HttpResponse response = client.execute(request, mHttpContext);
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
			case SEND_FEEDBACK:
        		if (result != null && ((String) result).equalsIgnoreCase(STATUS_OK)) {
        			sendFeedbackOk.show();        			
        		}
        		else {
        			sendFeedbackFail.show();
        		}
        		break;
			case AUTOLOGIN:
        		returnAutoLoginStatus((String) result);
        		break;
			}        	
		}
		
		private void returnLoginStatus(String status) {
			if (status != null) {
				String str = status.substring(0, 15);
				if (str.equalsIgnoreCase(LOGIN_STATUS_OK)) {
					saveAuthToFile(sCookie, mFolderName);
					((LoginReceiver) mDataReceiver).receiveLoginStatus(Consts.LOGIN_STATUS_OK);
				}
				else {
					((LoginReceiver) mDataReceiver).receiveLoginStatus(Consts.LOGIN_STATUS_FAIL);
				}
			}
			else {
				((LoginReceiver) mDataReceiver).receiveLoginStatus(Consts.LOGIN_STATUS_EMPTY_DATA);
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
			if (input != null && input.equalsIgnoreCase(STATUS_OK)) { 
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
				((ServerStatusReceiver) mDataReceiver).receiveServerStatus(false); 
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
		
		private void returnAutoLoginStatus(String status) { 
			if (status != null) {				
				if (status.equals(STATUS_OK)) {
					((LoginReceiver) mDataReceiver).receiveLoginStatus(Consts.LOGIN_STATUS_OK);
				}
				else {
					((LoginReceiver) mDataReceiver).receiveLoginStatus(Consts.LOGIN_STATUS_AUTOLOGIN_FAIL);					
				}
			}
			else {
				((LoginReceiver) mDataReceiver).receiveLoginStatus(Consts.LOGIN_STATUS_EMPTY_DATA);
				mToast.show();
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
					item.id = lunch.getInt(ID);
					item.name = lunch.getString(NAME);
					item.description = lunch.getString(DESCRIPTION);
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
