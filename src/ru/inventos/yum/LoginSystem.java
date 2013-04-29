package ru.inventos.yum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class LoginSystem implements LoginReceiver {
	public final static byte STATUS_OK = 0;
	public final static byte STATUS_FAIL = 1;
	public final static byte STATUS_EMPTY_DATA = 2;
	private final static String FILE_NAME = "data";
	private NetStorage mNetStorage;
	private String mFolder;
	private File mFile;
	private EmailPassword mEmailPass;
	private EmailPassword mCurEmailPass;
	private LoginReceiver mStatusReceiver;
	
	
	public LoginSystem(Context context) {
		mNetStorage = new NetStorage(context);
		mFolder = Environment.getDataDirectory() + "/data/" + context.getPackageName();
		mFile = new File(mFolder + '/' + FILE_NAME);
		mEmailPass = fromFile();
		mCurEmailPass = new EmailPassword();
	}
	
	public void autoLogin(LoginReceiver receiver) {
		mStatusReceiver = receiver;
		if (mEmailPass != null) {
			mNetStorage.login(this, mEmailPass.email, mEmailPass.password);
		}
		else {
			receiver.receiveLoginStatus(STATUS_EMPTY_DATA);
		}		
	}
	
	public void login(LoginReceiver receiver, String email, String password) {
		mStatusReceiver = receiver;
		mCurEmailPass.email = email;
		mCurEmailPass.password = password;
		mNetStorage.login(this, email, password);
	}		
	
	public void logout() {
		mFile.delete();
		mEmailPass = null;
		mNetStorage.terminateSession();		
	}
	
	private void toFile(String email, String password) {
		File dir = new File(mFolder);
        if (!dir.exists()) {	
        	dir.mkdir();
        }
        mFile.delete();
        FileWriter writer = null;
        try {
        	mFile.createNewFile();  
        	writer = new FileWriter(mFile);
           	writer.write(password+'\n');
           	writer.write(email);
           	writer.close();
        }
        catch (IOException ex) {
        	Log.e("LoginSystem", ex.getMessage());
        }    
	}
	
	private EmailPassword fromFile() {
		EmailPassword result = new EmailPassword();
		Scanner scanner = null;
		try {
			scanner = new Scanner(mFile);
			result.password = scanner.next();
			result.email = scanner.next();
			scanner.close();			
		}
		catch (FileNotFoundException ex) {
			Log.e("LoginSystem", ex.getMessage());
			return null;
		}
		return result;		
	}
	
	@Override
	public void receiveLoginStatus(byte status) {
		if (mEmailPass != null) {
			if (status == STATUS_FAIL) {
				logout();
			}			
		}
		else {
			if (status == STATUS_OK) {
				toFile(mCurEmailPass.email, mCurEmailPass.password);
			}
		}
		mStatusReceiver.receiveLoginStatus(status);		
	}
}
