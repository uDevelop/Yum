package ru.inventos.yum;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import org.holoeverywhere.widget.Spinner;

public class FeedbackActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);	 
		fillThemes(R.array.feedback_themes);
	}
	
	private void fillThemes(int themes) {
		Spinner spinner = (Spinner) findViewById(R.id.feedback_theme);
		Resources res = this.getResources();
		String[] data = res.getStringArray(themes);
		ArrayAdapter adapter = new ArrayAdapter<String>(this, 
				org.holoeverywhere.R.layout.simple_spinner_item, data);
		spinner.setAdapter(adapter);		
	}
	

}
