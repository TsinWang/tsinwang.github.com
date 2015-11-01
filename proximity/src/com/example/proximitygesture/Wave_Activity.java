package com.example.proximitygesture;


import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class Wave_Activity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener{
	
	
	private ListPreference List1;
	private ListPreference List2;
	private ListPreference List3;
	private ListPreference List4;
	private ListPreference List5;
	private ListPreference List6;
	private ListPreference List7;
	private ListPreference List8;
	private static final int ADMIN_INTENT = 15;
	private DevicePolicyManager mDevicePolicyManager; 
    private ComponentName mComponentName;
	private SharedPreferences mpref;
    
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.wave_prefs);
		mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);  
        mComponentName = new ComponentName(getApplicationContext(), SensorService$deviceAdminReceiver.class);
        mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		List1 = (ListPreference)getPreferenceManager().findPreference("one");
		List2 = (ListPreference)getPreferenceManager().findPreference("two");
		List3 = (ListPreference)getPreferenceManager().findPreference("three");
		List4 = (ListPreference)getPreferenceManager().findPreference("four");

		List5 = (ListPreference)getPreferenceManager().findPreference("one_2");
		List6 = (ListPreference)getPreferenceManager().findPreference("two_2");
		List7 = (ListPreference)getPreferenceManager().findPreference("three_2");
		List8 = (ListPreference)getPreferenceManager().findPreference("four_2");
		
		List1.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("app"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectApp.class);
					startActivityForResult(Launch, 1);
				}
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 1);
				}
				if(newValue.equals("screen off"))
				{
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			         intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Administrator Policy");
			         startActivityForResult(intent, ADMIN_INTENT);
				}
				return true;
			}
		});
		
		List2.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("app"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectApp.class);
					startActivityForResult(Launch, 2);
				}
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 2);
				}
				if(newValue.equals("screen off"))
				{
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			         intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Administrator Policy");
			         startActivityForResult(intent, ADMIN_INTENT);
				}
				return true;
			}
		});
		
		List3.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("app"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectApp.class);
					startActivityForResult(Launch, 3);
				}
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 3);
				}
				if(newValue.equals("screen off"))
				{
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			         intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Administrator Policy");
			         startActivityForResult(intent, ADMIN_INTENT);
				}
				return true;
			}
		});
		
		List4.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("app"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectApp.class);
					startActivityForResult(Launch, 4);
				}
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 4);
				}
				if(newValue.equals("screen off"))
				{
					Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			         intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
			         intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"Administrator Policy");
			         startActivityForResult(intent, ADMIN_INTENT);
				}
				return true;
			}
		});
		
		List5.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 5);
				}
				return true;
			}
		});
		
		List6.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 6);
				}
				return true;
			}
		});
		
		List7.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 7);
				}
				return true;
			}
		});
		
		List8.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {	
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				if(newValue.equals("contact"))
				{
					Intent Launch = new Intent(getApplicationContext(),SelectContact.class);
					startActivityForResult(Launch, 8);
				}
				return true;
			}
		});
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADMIN_INTENT) {
            if (resultCode == RESULT_OK) {
                MainActivity.cbp.setChecked(true);
            	Toast.makeText(getApplicationContext(), "Registered As Administrator", Toast.LENGTH_SHORT).show();
            }else{
            	MainActivity.cbp.setChecked(false);
                Toast.makeText(getApplicationContext(), "Denied Device Administrator Permission ", Toast.LENGTH_SHORT).show();
            }
        }
        if(resultCode ==  2 && requestCode == 1)
		{
			String packagename = data.getStringExtra("PackageInfo");
			Log.e("Wave Class","Wave Act "+packagename);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavepack1", packagename).commit();
		}
        if(resultCode ==  2 && requestCode == 2)
		{
			String packagename = data.getStringExtra("PackageInfo");
			Log.e("Wave Class","Wave Act "+packagename);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavepack2", packagename).commit();
		}
        if(resultCode ==  2 && requestCode == 3)
		{
			String packagename = data.getStringExtra("PackageInfo");
			Log.e("Wave Class","Wave Act "+packagename);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavepack3", packagename).commit();
		}
        if(resultCode ==  2 && requestCode == 4)
		{
			String packagename = data.getStringExtra("PackageInfo");
			Log.e("Wave Class","Wave Act "+packagename);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavepack4", packagename).commit();
		}
        if(resultCode ==  1 && requestCode == 1)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone1", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 2)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone2", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 3)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone3", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 4)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone4", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 5)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone5", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 6)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone6", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 7)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone7", phoneNum).commit();
		}
        if(resultCode ==  1 && requestCode == 8)
		{
			String phoneNum = data.getStringExtra("PhoneNum");
			Log.e("Wave Class","Phone Num "+phoneNum);
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplication());
			mpref.edit().putString("wavephone8", phoneNum).commit();
		}
    }
	
	
	
	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		
		return true;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		
		return true;
	}
	
	
}

