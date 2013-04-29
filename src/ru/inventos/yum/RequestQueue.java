package ru.inventos.yum;

import java.util.ArrayList;

import android.os.AsyncTask;

public class RequestQueue {
	private ArrayList<NetStorage.NetworkStorage> mQueue;
	
	public RequestQueue() {
		mQueue = new ArrayList<NetStorage.NetworkStorage>();
	}
	
	public void add(NetStorage.NetworkStorage request) {
		mQueue.add(request);
		if (mQueue.size() == 1) {
			request.execute();
		}
	}
	
	public void setFinished(NetStorage.NetworkStorage request) {
		if (mQueue.get(0).equals(request)) { 
			mQueue.remove(0);
			if (mQueue.size() > 0) {
				NetStorage.NetworkStorage newRequest = mQueue.get(0);
				newRequest.execute();
			}
		}
	}

}
