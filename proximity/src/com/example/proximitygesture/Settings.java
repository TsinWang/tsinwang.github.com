package com.example.proximitygesture;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements OnPreferenceChangeListener{
	
	SharedPreferences mSharedPrefs;
	CheckBoxPreference checkbox;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings_layout);
		findPreference("testing").setIntent(new Intent(getApplicationContext(), SensorTesting.class));
		checkbox = (CheckBoxPreference)getPreferenceManager().findPreference("service");
		checkbox.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override	
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				boolean myval = (Boolean)newValue;
				if (myval)
				{
					startService(new Intent(getBaseContext(),SensorService.class));
					Toast.makeText(Settings.this, "Sensor Activated!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					stopService(new Intent(getBaseContext(),SensorService.class));
					Toast.makeText(Settings.this, "Sensor Stopped!", Toast.LENGTH_SHORT).show();
				}

				return true;
			}
		});
		
		Context context = getApplicationContext();
		if(AppWidgetProvider.isServiceRunning(context))
		{
			checkbox.setChecked(true);
		}
		else
		{
			checkbox.setChecked(false);
		}
		
	}

	/*public boolean isServiceRunning() {
		ActivityManager manager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.example.proximitygesture.SensorService"
					.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}*/
	
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}
}
