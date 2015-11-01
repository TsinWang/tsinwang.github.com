package com.example.proximitygesture;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


@SuppressWarnings("deprecation")
public class SensorService extends Service implements SensorEventListener{
	
	private PowerManager pm;
	private PackageManager pkgmgr;
	private AudioManager mad;
	private Sensor mSensor;
	private SensorManager mSensorManager;
	private SharedPreferences mpref;
	private String val;
	private Camera camera;
	private boolean flashon = false;
	private DevicePolicyManager mDevicePolicyManager; 
    private ComponentName mComponentName;
    private MediaPlayer mp;
    private TelephonyManager tm;
    private Vibrator vb;
    Handler hand = new Handler();
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		mDevicePolicyManager = (DevicePolicyManager)getSystemService(Context.DEVICE_POLICY_SERVICE);
		vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mComponentName = new ComponentName(this, SensorService$deviceAdminReceiver.class);
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mad = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mSensorManager.registerListener(SensorService.this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		camera = Camera.open();
		mp = MediaPlayer.create(this, R.raw.swoosh);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
	}

	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mSensorManager.unregisterListener(SensorService.this, mSensor);
		if (camera != null) {
			   camera.release();
			  }
	}
	
	private final IBinder mBinder = new LocalBinder();

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public class LocalBinder extends Binder {
		public SensorService getService() {
			return SensorService.this;
		}
	}

	private long start_time,end_time,difference,hold_start=0L;
	@SuppressLint("UseSparseArrays")
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int waveminduration = Integer.parseInt(mpref.getString("wave_min_duration", "5"));
		int waveduration = Integer.parseInt(mpref.getString("waveduration", "800"));
		int first_hold = Integer.parseInt(mpref.getString("first_hold","2000"));
		int next_hold = Integer.parseInt(mpref.getString("next_hold","1000"));
		boolean sound = mpref.getBoolean("sound feedback", true);
		float a = event.values[0];
		int hold2 = first_hold+next_hold;
		int hold3 = first_hold+next_hold+next_hold;
		int hold4 = first_hold+next_hold+next_hold+next_hold;
		int hold5 = first_hold+next_hold+next_hold+next_hold+next_hold;
			

		if(a > 0)
		{
			start_time = System.currentTimeMillis();
			difference = (start_time - end_time);
			hold_start = 0L;
			Log.e("Difference","Time Far: "+difference);
			if(sound)
			{
				mp.start();
			}
			if(difference < waveminduration)
			{	
				Toast.makeText(getBaseContext(), "Accidental Triggering", Toast.LENGTH_SHORT).show();
			}
			if (difference > waveminduration && difference <= waveduration) 
			{
				Log.e("gesture", "wave");
				onWaveGesture();
			}
			if (difference > first_hold && difference < hold2) 
			{
				Log.e("gesture", "hold1");
				onHoldOne();
			}
			else if (difference > hold2 && difference < hold3) 
			{
				Log.e("gesture", "hold2");
				onHoldTwo();
			}
			else if (difference > hold3 && difference < hold4) 
			{
				Log.e("gesture", "hold3");
				onHoldThree();
			}
			else if (difference > hold4 && difference < hold5) 
			{
				Log.e("gesture", "hold4");
				onHoldFour();
			}
		}
		else
		{	
			end_time = System.currentTimeMillis();
			difference = (end_time - start_time);
			Log.e("Difference","Time Near : "+difference);	
				if(difference < waveminduration)
				{	
					Toast.makeText(getBaseContext(), "Accidental Triggering", Toast.LENGTH_SHORT).show();
				}
				if(sound)
				{
						mp.start();
				}
				if(hold_start == 0)
				{
				hold_start = SystemClock.uptimeMillis();
				hand.removeCallbacks(run);
				hand.postDelayed(run, first_hold);
				}
				
				
		}
	}
	
	private Runnable run = new Runnable() {
		@Override
		public void run() {
			int first_hold = Integer.parseInt(mpref.getString("first_hold","2000"));
			int next_hold = Integer.parseInt(mpref.getString("next_hold","1000"));
			int hold2 = first_hold+next_hold;
			int hold3 = first_hold+next_hold+next_hold;
			int hold4 = first_hold+next_hold+next_hold+next_hold;
			int hold5 = first_hold+next_hold+next_hold+next_hold+next_hold;
			boolean vibration = mpref.getBoolean("feedback", true);
			final long start = hold_start;
			long millis = SystemClock.uptimeMillis() - start;	
            Log.e("Millis",""+millis);
            if(vibration)
			{
            	if(millis > first_hold && millis < hold2)
            	{
            		hand.removeCallbacks(run);
            		vb.vibrate(150L);
            		millis = 0;
            	} else 
            	if(millis > hold2 && millis < hold3)
            	{	
            		hand.removeCallbacks(run);
            		vb.vibrate(150L);
            		millis = 0;
            	} else
            	if(millis > hold3 && millis < hold4)
            	{
            		hand.removeCallbacks(run);
            		vb.vibrate(150L);
            		millis = 0;
            	} else
            	if(millis > hold4 && millis < hold5)
            	{	
            		hand.removeCallbacks(run);
            		vb.vibrate(150L);
            		millis = 0;
            	}
			}
            hand.postDelayed(this,next_hold);
		}
	};
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	private int waveCount = 0;
	static final int MAXWAVES = 4;
	private void onWaveGesture() {
		waveCount++;
		
		if(waveCount == 1) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				  @Override
				  public void run() {
				    //Do something after 1000ms
					  finishWaveGesture();
				  }
				}, 760*(MAXWAVES - 1));
		}
		if(waveCount == 2) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				  @Override
				  public void run() {
				    //Do something after 1000ms
					  finishWaveGesture();
				  }
				}, 760*(MAXWAVES - 1));
		}
		if(waveCount == 3) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				  @Override
				  public void run() {
				    //Do something after 1000ms
					  finishWaveGesture();
				  }
				}, 760*(MAXWAVES - 1));
		}
		if(waveCount == 4) {
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				  @Override
				  public void run() {
				    //Do something after 1000ms
					  finishWaveGesture();
				  }
				}, 760*(MAXWAVES - 1));
		}
	}

	private void finishWaveGesture() {
		if(pm.isScreenOn())
		{
		switch(waveCount) {
		case 1:
			Log.e("wave", "Single wave");
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("one", "Nothing Selected");
			Log.d("Info","Wave 1:" +val);
			
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchWaveApp1();
			}
			if (val.equals("contact"))
			{
				launchWaveContact1();
			}
			break;
		case 2:
			Log.e("wave", "Double wave");
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("two", "Nothing Selected");
			Log.d("Info","Wave 2:" +val);
			
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchWaveApp2();
			}
			if (val.equals("contact"))
			{
				launchWaveContact2();
			}
			break;
		case 3:
			Log.e("wave", "Triple wave");
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("three", "Nothing Selected");
			Log.d("Info","Wave 3 :" +val);
			
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchWaveApp3();
			}
			if (val.equals("contact"))
			{
				launchWaveContact3();
			}
			break;
		case 4:
			Log.e("wave", "Quadruple wave");
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("four", "Nothing Selected");
			Log.d("Info","Wave 4:" +val);
			
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchWaveApp4();
			}
			if (val.equals("contact"))
			{
				launchWaveContact4();
			}
			break;
		}
		waveCount = 0;
		}
		else
		{
			switch(waveCount) {
			case 1:
				Log.e("wave", "Single wave");
				mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				val = mpref.getString("one_2", "Nothing Selected");
				Log.d("Info","Off Wave 1 :" +val);
				
				if (val.equals("led"))
				{	
					Context con = this;
					pkgmgr = con.getPackageManager();
					if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
					{
						Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
						return;
					}
					else
					{	
						final Parameters p = camera.getParameters();
						flashlight(p);
					}
				}
				if(val.equals("play/pause"))
				{
					playPause();
				}
				if(val.equals("play next"))
				{
					nextSong();
				}
				if(val.equals("play previous"))
				{
					prevSong();
				}
				if(val.equals("wakeup"))
				{
					wakeup();
				}
				if (val.equals("contact"))
				{
					launchWaveContact5();
				}
				break;
			case 2:
				Log.e("wave", "Double  wave");
				mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				val = mpref.getString("two_2", "Nothing Selected");
				Log.d("Info","Off Wave 2 :" +val);
				
				if (val.equals("led"))
				{	
					Context con = this;
					pkgmgr = con.getPackageManager();
					if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
					{
						Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
						return;
					}
					else
					{	
						final Parameters p = camera.getParameters();
						flashlight(p);
					}
				}
				if(val.equals("play/pause"))
				{
					playPause();
				}
				if(val.equals("play next"))
				{
					nextSong();
				}
				if(val.equals("play previous"))
				{
					prevSong();
				}
				if(val.equals("wakeup"))
				{
					wakeup();
				}
				if (val.equals("contact"))
				{
					launchWaveContact6();
				}
				break;
			case 3:
				Log.e("wave", "Triple wave");
				mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				val = mpref.getString("three_2", "Nothing Selected");
				Log.d("Info","Off Wave 3 :" +val);
				
				if (val.equals("led"))
				{	
					Context con = this;
					pkgmgr = con.getPackageManager();
					if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
					{
						Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
						return;
					}
					else
					{	
						final Parameters p = camera.getParameters();
						flashlight(p);
					}
				}
				if(val.equals("play/pause"))
				{
					playPause();
				}
				if(val.equals("play next"))
				{
					nextSong();
				}
				if(val.equals("play previous"))
				{
					prevSong();
				}
				if(val.equals("wakeup"))
				{
					wakeup();
				}
				if (val.equals("contact"))
				{
					launchWaveContact7();
				}
				break;
			case 4:
				Log.e("wave", "Four waves");
				mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
				val = mpref.getString("four_2", "Nothing Selected");
				Log.d("Info","Off Wave 4 :" +val);
				
				if (val.equals("led"))
				{	
					Context con = this;
					pkgmgr = con.getPackageManager();
					if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
					{
						Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
						return;
					}
					else
					{	
						final Parameters p = camera.getParameters();
						flashlight(p);
					}
				}
				if(val.equals("play/pause"))
				{
					playPause();
				}
				if(val.equals("play next"))
				{
					nextSong();
				}
				if(val.equals("play previous"))
				{
					prevSong();
				}
				if(val.equals("wakeup"))
				{
					wakeup();
				}
				if (val.equals("contact"))
				{
					launchWaveContact8();
				}
				break;
			}
			waveCount = 0;
		}
	}	
	
	private void onHoldOne() {
		if(pm.isScreenOn())
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold1", "Nothing Selected");
			Log.d("Info","Hold 1 :" +val);
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchHoldApp1();
			}
			if (val.equals("contact"))
			{
				launchHoldContact1();
			}
		}
		else
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold1_2", "Nothing Selected");
			Log.d("Info","Off Hold 1 :" +val);
			
			if (val.equals("led"))
			{	
				Context con = this;
				pkgmgr = con.getPackageManager();
				if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
				{
					Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{	
					final Parameters p = camera.getParameters();
					flashlight(p);
				}
			}
			if(val.equals("play/pause"))
			{
				playPause();
			}
			if(val.equals("play next"))
			{
				nextSong();
			}
			if(val.equals("play previous"))
			{
				prevSong();
			}
			if(val.equals("wakeup"))
			{
				wakeup();
			}
			if (val.equals("contact"))
			{
				launchHoldContact5();
			}
		}
	}
	
	private void onHoldTwo()
	{
		if(pm.isScreenOn())
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold2", "Nothing Selected");
			Log.d("Info","Hold 2 :" +val);
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchHoldApp2();
			}
			if (val.equals("contact"))
			{
				launchHoldContact2();
			}
		}
		else
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold2_2", "Nothing Selected");
			Log.d("Info","Off Hold 2 :" +val);
			
			if (val.equals("led"))
			{	
				Context con = this;
				pkgmgr = con.getPackageManager();
				if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
				{
					Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{	
					final Parameters p = camera.getParameters();
					flashlight(p);
				}
			}
			if(val.equals("play/pause"))
			{
				playPause();
			}
			if(val.equals("play next"))
			{
				nextSong();
			}
			if(val.equals("play previous"))
			{
				prevSong();
			}
			if(val.equals("wakeup"))
			{
				wakeup();
			}
			if (val.equals("contact"))
			{
				launchHoldContact6();
			}
		}
	}
	private void onHoldThree()
	{
		if(pm.isScreenOn())
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold3", "Nothing Selected");
			Log.d("Info","Hold 3 :" +val);
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchHoldApp3();
			}
			if (val.equals("contact"))
			{
				launchHoldContact3();
			}
		}
		else
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold3_2", "Nothing Selected");
			Log.d("Info","Off Hold 3 :" +val);
			
			if (val.equals("led"))
			{	
				Context con = this;
				pkgmgr = con.getPackageManager();
				if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
				{
					Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{	
					final Parameters p = camera.getParameters();
					flashlight(p);
				}
			}
			if(val.equals("play/pause"))
			{
				playPause();
			}
			if(val.equals("play next"))
			{
				nextSong();
			}
			if(val.equals("play previous"))
			{
				prevSong();
			}
			if(val.equals("wakeup"))
			{
				wakeup();
			}
			if (val.equals("contact"))
			{
				launchHoldContact7();
			}
		}
	}
	private void onHoldFour()
	{
		if(pm.isScreenOn())
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold4", "Nothing Selected");
			Log.d("Info","Hold 4 :" +val);
			if (val.equals("play/pause"))
			{
				playPause();
			}
			if (val.equals("screen off"))
			{
				lockscreen();
			}
			if (val.equals("play next"))
			{
				nextSong();
			}
			if (val.equals("play previous"))
			{
				prevSong();
			}
			if (val.equals("auto rotation"))
			{
				autoRotation();
			}
			if (val.equals("accept"))
			{
				Context context = getApplicationContext();
				answerCall(context);
			}
			if (val.equals("app"))
			{
				launchHoldApp4();
			}
			if (val.equals("contact"))
			{
				launchHoldContact4();
			}
		}
		else
		{
			mpref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			val = mpref.getString("hold4_2", "Nothing Selected");
			Log.d("Info","Off Hold 4 :" +val);
			
			if (val.equals("led"))
			{	
				Context con = this;
				pkgmgr = con.getPackageManager();
				if(!pkgmgr.hasSystemFeature(PackageManager.FEATURE_CAMERA))
				{
					Toast.makeText(getBaseContext(), "Device has no camera flash", Toast.LENGTH_SHORT).show();
					return;
				}
				else
				{	
					final Parameters p = camera.getParameters();
					flashlight(p);
				}
			}
			if(val.equals("play/pause"))
			{
				playPause();
			}
			if(val.equals("play next"))
			{
				nextSong();
			}
			if(val.equals("play previous"))
			{
				prevSong();
			}
			if(val.equals("wakeup"))
			{
				wakeup();
			}
			if (val.equals("contact"))
			{
				launchHoldContact8();
			}
		}
	}
	
	private void nextSong() {
		if (mad.isMusicActive()) {
			long eventtime = SystemClock.uptimeMillis();

			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent downEvent = new KeyEvent(eventtime, eventtime,
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
			sendOrderedBroadcast(downIntent, null);

			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent upEvent = new KeyEvent(eventtime, eventtime,
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_NEXT, 0);
			upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
			sendOrderedBroadcast(upIntent, null);
			Toast.makeText(getBaseContext(), "Next Song", Toast.LENGTH_SHORT).show();
		}
	}

	private void prevSong() {
		if (mad.isMusicActive()) {
			long eventtime = SystemClock.uptimeMillis();

			Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent downEvent = new KeyEvent(eventtime, eventtime,
					KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
			downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
			sendOrderedBroadcast(downIntent, null);

			Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
			KeyEvent upEvent = new KeyEvent(eventtime, eventtime,
					KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PREVIOUS, 0);
			upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
			sendOrderedBroadcast(upIntent, null);
			Toast.makeText(getBaseContext(), "Previous Song", Toast.LENGTH_SHORT).show();
		}
	}

	private void playPause() {
		long eventtime = SystemClock.uptimeMillis();

		Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		KeyEvent downEvent = new KeyEvent(eventtime, eventtime,
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
		downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
		sendOrderedBroadcast(downIntent, null);

		Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
		KeyEvent upEvent = new KeyEvent(eventtime, eventtime,
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, 0);
		upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
		sendOrderedBroadcast(upIntent, null);
		Toast.makeText(getBaseContext(), "Play/Pause", Toast.LENGTH_SHORT).show();
	}
	
	private void flashlight(Parameters p)
	{	
			if(flashon)
			{
				Log.d("Info", "Flash/LED is Off!"); 
				p.setFlashMode(Parameters.FLASH_MODE_OFF);
				camera.setParameters(p);
				camera.stopPreview();
				flashon = false;
			}
			else
			{
				Log.d("Info", "Flash/LED is On!");
				p.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(p);
				camera.startPreview();
				flashon = true;
			}
				
	}
	
	private void lockscreen()
	{
		boolean isAdmin = mDevicePolicyManager.isAdminActive(mComponentName);  
        if (isAdmin) {  
            mDevicePolicyManager.lockNow();  
        }else{
            Toast.makeText(getApplicationContext(), "Not Registered as Admin", Toast.LENGTH_SHORT).show();
        }
		
	}
	
	@SuppressWarnings("unused")
	@SuppressLint("Wakelock")
	private void wakeup()
	{
		WakeLock fullWakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "FULL WAKE LOCK");
	    WakeLock partialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "PARTIAL WAKE LOCK");
	    fullWakeLock.acquire();

	    KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
	    KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
	    keyguardLock.disableKeyguard();
	}
	
	private void autoRotation()
	{
		if  (android.provider.Settings.System.getInt(getContentResolver(),android.provider.Settings.System.ACCELEROMETER_ROTATION, 0) == 1){
		    android.provider.Settings.System.putInt(getContentResolver(),android.provider.Settings.System.ACCELEROMETER_ROTATION, 0);
		    Toast.makeText(this, "Rotation OFF", Toast.LENGTH_SHORT).show();
		    }
		else{
		    android.provider.Settings.System.putInt(getContentResolver(), android.provider.Settings.System.ACCELEROMETER_ROTATION, 1);
		    Toast.makeText(this, "Rotation ON", Toast.LENGTH_SHORT).show();
		    }
	}
	
	private void launchWaveApp1()
	{
		String wavepack1 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavepack1", "");
		Log.e("Sensor Service","WavePack1 :"+wavepack1);		
		try{
		Intent wavepack_1 = getPackageManager().getLaunchIntentForPackage(wavepack1);
			if (null != wavepack1) {
         	  startActivity(wavepack_1);
			}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }		
	}
	
	private void launchWaveApp2()
	{
		String wavepack2 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavepack2", "");
		Log.e("Sensor Service","WavePack2 :"+wavepack2);
		try{
			Intent wavepack_2 = getPackageManager().getLaunchIntentForPackage(wavepack2);
			if (null != wavepack2) {
	         	  startActivity(wavepack_2);
				}
			}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
		
	}
	
	private void launchWaveApp3()
	{
		String wavepack3 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavepack3", "");
		Log.e("Sensor Service","WavePack3 :"+wavepack3);
		try{
			Intent wavepack_3 = getPackageManager().getLaunchIntentForPackage(wavepack3);
			if (null != wavepack3) {
	         	  startActivity(wavepack_3);
				}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
	}
	
	private void launchWaveApp4()
	{
		String wavepack4 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavepack4", "");
		Log.e("Sensor Service","WavePack4 :"+wavepack4);
		try{
			Intent wavepack_4 = getPackageManager().getLaunchIntentForPackage(wavepack4);
			if (null != wavepack4) {
	         	  startActivity(wavepack_4);
				}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
	}
	
	private void launchHoldApp1()
	{
		String holdpack1 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdpack1", "");
		Log.e("Sensor Service","HoldPack1 :"+holdpack1);
		try{
			Intent holdpack_1 = getPackageManager().getLaunchIntentForPackage(holdpack1);
			if (null != holdpack1) {
	         	  startActivity(holdpack_1);
				}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
	}
	
	private void launchHoldApp2()
	{
		String holdpack2 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdpack2", "");
		Log.e("Sensor Service","HoldPack2 :"+holdpack2);
		try{
			Intent holdpack_2 = getPackageManager().getLaunchIntentForPackage(holdpack2);
			if (null != holdpack2) {
	         	  startActivity(holdpack_2);
				}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
	}
	
	private void launchHoldApp3()
	{
		String holdpack3 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdpack3", "");
		Log.e("Sensor Service","HoldPack3 :"+holdpack3);
		try{
			Intent holdpack_3 = getPackageManager().getLaunchIntentForPackage(holdpack3);
			if (null != holdpack3) {
	         	  startActivity(holdpack_3);
				}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
	}
	
	private void launchHoldApp4()
	{
		String holdpack4 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdpack4", "");
		Log.e("Sensor Service","HoldPack4 :"+holdpack4);
		try{
			Intent holdpack_4 = getPackageManager().getLaunchIntentForPackage(holdpack4);
			if (null != holdpack4) {
	         	  startActivity(holdpack_4);
				}
		}
		catch (ActivityNotFoundException e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
	}
	
	private void launchWaveContact1()
	{
		String wavephone1 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone1", "");
		Log.e("Sensor Service","WavePhone1 :"+wavephone1);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone1));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact2()
	{
		String wavephone2 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone2", "");
		Log.e("Sensor Service","WavePhone2 :"+wavephone2);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone2));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact3()
	{
		String wavephone3 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone3", "");
		Log.e("Sensor Service","WavePhone3 :"+wavephone3);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone3));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact4()
	{
		String wavephone4 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone4", "");
		Log.e("Sensor Service","WavePhone4 :"+wavephone4);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone4));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact5()
	{
		String wavephone5 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone5", "");
		Log.e("Sensor Service","WavePhone5 :"+wavephone5);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone5));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact6()
	{
		String wavephone6 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone6", "");
		Log.e("Sensor Service","WavePhone6 :"+wavephone6);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone6));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact7()
	{
		String wavephone7 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone7", "");
		Log.e("Sensor Service","WavePhone7 :"+wavephone7);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone7));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchWaveContact8()
	{
		String wavephone8 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("wavephone8", "");
		Log.e("Sensor Service","WavePhone8 :"+wavephone8);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + wavephone8));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void launchHoldContact1()
	{
		String holdphone1 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone1", "");
		Log.e("Sensor Service","HoldPhone1 :"+holdphone1);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone1));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact2()
	{
		String holdphone2 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone2", "");
		Log.e("Sensor Service","HoldPhone8 :"+holdphone2);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone2));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact3()
	{
		String holdphone3 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone3", "");
		Log.e("Sensor Service","HoldPhone3 :"+holdphone3);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone3));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact4()
	{
		String holdphone4 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone4", "");
		Log.e("Sensor Service","HoldPhone4 :"+holdphone4);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone4));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact5()
	{
		String holdphone5 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone5", "");
		Log.e("Sensor Service","HoldPhone5 :"+holdphone5);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone5));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact6()
	{
		String holdphone6 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone6", "");
		Log.e("Sensor Service","HoldPhone6 :"+holdphone6);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone6));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact7()
	{
		String holdphone7 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone7", "");
		Log.e("Sensor Service","HoldPhone7 :"+holdphone7);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone7));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	private void launchHoldContact8()
	{
		String holdphone8 = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("holdphone8", "");
		Log.e("Sensor Service","HoldPhone8 :"+holdphone8);
		try{
			Intent intent = new Intent(Intent.ACTION_CALL);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setData(Uri.parse("tel:" + holdphone8));
			startActivity(intent);
		}
		catch (Exception e) {
            Toast.makeText(this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }

	}
	
	private void answerCall(Context context)
	{
		int a = tm.getCallState();
		Log.e("","Call "+a);
		boolean spk = mpref.getBoolean("speaker", false);
		int currVolume = 0;
		AudioManager mad = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		if(a == 1)
		{	

		    Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		    buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, 
		      new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		    context.sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
		    if(spk)	
	    	{
		    mad.setMode(AudioManager.ROUTE_SPEAKER);
		    currVolume = mad.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
		    if(!mad.isSpeakerphoneOn()) {
                mad.setMode(AudioManager.MODE_IN_CALL);
                mad.setSpeakerphoneOn(true);
                mad.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                mad.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),AudioManager.STREAM_VOICE_CALL);
            	}
	    	}

		}
	}
}

