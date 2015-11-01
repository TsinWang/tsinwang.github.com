package com.example.proximitygesture;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Actions_Activity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.actions_layout);
		findPreference("wave").setIntent(new Intent(getApplicationContext(), Wave_Activity.class));
	    findPreference("hold").setIntent(new Intent(getApplicationContext(), Hold_Activity.class));
		
	}
	
}
