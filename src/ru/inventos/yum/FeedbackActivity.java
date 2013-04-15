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
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
					R.array.feedback_themes, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);		
	}
	

}
