package ru.inventos.yum;

import ru.inventos.yum.adapters.MainListAdapter;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class SuggestionProvider extends ContentProvider {
	public static String AUTHORITY = "ru.inventos.yum.SuggestionProvider";
	private static final int SEARCH_SUGGEST = 0;
    private static final UriMatcher sURIMatcher = makeUriMatcher();
    private MainListAdapter mLunchAdapter;
    
    
    @Override
    public boolean onCreate() {    	
    	mLunchAdapter = new MainListAdapter();
    	return true;
    }
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, 	String[] selectionArgs,
    		String sortOrder) {
    	if (sURIMatcher.match(uri) == SEARCH_SUGGEST) {
    		return getSuggestions(selectionArgs[0]);
    	}
    	else {
    		throw new IllegalArgumentException("Unknown Uri: " + uri);
    	}
    }
    
    private Cursor getSuggestions(String query) {
        String lowQuery = query.toLowerCase();
        String[] columns = new String[] {BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1,
        			SearchManager.SUGGEST_COLUMN_INTENT_DATA};   
        MatrixCursor cursor = new MatrixCursor(columns);
        int ind = 0;
        String[] names = mLunchAdapter.getLunchNames(); 
        if (names != null) {  
        	for (String name : names) {
        		if (name.toLowerCase().matches(lowQuery + ".*")) {
        			cursor.addRow(new Object[] {ind, name, name});
        			ind++;
        		}
        	}
        }
        return cursor;
    }
       
    private static UriMatcher makeUriMatcher() {
        UriMatcher matcher =  new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        matcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
        return matcher;
    }
    
    
    @Override
    public String getType(Uri uri) {
    	switch (sURIMatcher.match(uri)) {
        	case SEARCH_SUGGEST:
            	return SearchManager.SUGGEST_MIME_TYPE;            
            default:
            	throw new IllegalArgumentException("Unknown URL " + uri);
        }	
    }
    
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
    	throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
    	throw new UnsupportedOperationException();
    }    
}

