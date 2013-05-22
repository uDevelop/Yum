package ru.inventos.yum;

import ru.inventos.yum.interfaces.ImageReceiver;
import android.content.Context;

public class ImageStorage {
	private NetStorage mNetStorage;
	
	public ImageStorage(Context context) {
		mNetStorage = new NetStorage(context);
	}
	
	public void getImage(String name, ImageReceiver receiver) {
		mNetStorage.getImage(receiver, Consts.SERVER_ADDRESS + name);
	}

}
