package com.example.proximitygesture;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class MainActivity extends PreferenceActivity implements OnPreferenceChangeListener{

	SharedPreferences mpref;
	private static final int ADMIN_INTENT = 15;
	private DevicePolicyManager mDevicePolicyManager; 
    private ComponentName mComponentName;
	static CheckBoxPreference cbp;
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.main_screen);
		findPreference("actions").setIntent(new Intent(getApplicationContext(), Actions_Activity.class));
	    findPreference("options").setIntent(new Intent(getApplicationContext(), Settings.class));
	    findPreference("howtouse").setIntent(new Intent(getApplicationContext(), Help.class));
	    cbp = (CheckBoxPreference)getPreferenceManager().findPreference("devadmin");
	    mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);  
        mComponentName = new ComponentName(getApplicationContext(), SensorService$deviceAdminReceiver.class); 
		
        cbp.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				boolean myval = (Boolean)newValue;
				if(myval)
				{
					 Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			         intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Administrator Policy");
			         startActivityForResult(intent, ADMIN_INTENT);
				}
				else
				{
					mDevicePolicyManager.removeActiveAdmin(mComponentName);  
		            Toast.makeText(getApplicationContext(), "Admin registration removed", Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Registered As Administrator", Toast.LENGTH_SHORT).show();
            }else{
            	cbp.setChecked(false);
                Toast.makeText(getApplicationContext(), "Denied Device Administrator Permission ", Toast.LENGTH_SHORT).show();
            }
        }
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override
	protected void onRestart()
	{
		mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(mpref.getBoolean("service", true))
		{
			startService(new Intent(getBaseContext(),SensorService.class));
		}
		super.onRestart();
	}
	
	@Override
	protected void onResume()
	{
		mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		if(mpref.getBoolean("service", true))
		{
			startService(new Intent(getBaseContext(),SensorService.class));
		}
		super.onResume();	
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		return false;
	}

}
