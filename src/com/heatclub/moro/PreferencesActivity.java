package com.heatclub.moro;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class PreferencesActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        addPreferencesFromResource(R.xml.preferences);
    }

	@Override
	protected void onDestroy()
	{
		// TODO: Implement this method
		super.onDestroy();		
	}
	 
}
