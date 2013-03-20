package ru.inventos.yum;

import java.util.ArrayList;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

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
	
	public void getLunchList(MainListAdapter receiver, ArrayList<LunchItem> lunchItems) {
		NetworkStorage storage = new NetworkStorage(receiver, lunchItems, NetworkStorage.GET_LUNCH_LIST);
		storage.execute();
	}
	
		
	private class NetworkStorage extends AsyncTask<Void, Void, String> {
		final static byte GET_LUNCH_LIST = 0;
		private Object mDataReceiver;
		private Object mParams;
		private byte mOperation;
		
		
		public NetworkStorage(Object dataReceiver, Object params, byte operation) {			
			super();
			mDataReceiver = dataReceiver;
			mOperation = operation;
			mParams = params;
		}
		
		@Override
        protected String doInBackground(Void... params) {
			//TODO: pull from server		
			return "";           
        }
		
		@Override
        protected void onPostExecute(String result) {
			switch(mOperation) {
			case GET_LUNCH_LIST:
				fillLunchList((ArrayList<LunchItem>) mParams, result);
				((MainListAdapter) mDataReceiver).UpdateList(); 
				break;
			}			
		}
		
		private void fillLunchList(ArrayList<LunchItem> list, String dataString) {
			//TODO:
		}
		
		
	}
	
}
