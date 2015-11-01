package com.example.proximitygesture;


import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class SensorTesting extends Activity implements SensorEventListener{

	TextView Proximity,Timedifference,Distance,TimeValue;
	private Sensor mSensor;
	private SensorManager mSensorManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sensor_testing);
		Proximity = (TextView) findViewById(R.id.textView1);
		Timedifference = (TextView) findViewById(R.id.textView2);
		Distance = (TextView) findViewById(R.id.textView3);
		TimeValue = (TextView) findViewById(R.id.textView4);
		
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
		mSensorManager.registerListener(SensorTesting.this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL); 
	}

	private long start_time,end_time,time_diff;
	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
		float a = event.values[0];
		
		if (a < 1.0F || a < mSensor.getMaximumRange())
		{
			end_time = System.currentTimeMillis();
			time_diff = end_time - start_time;
			TimeValue.setText(String.valueOf(time_diff));
			Distance.setText(R.string.prox_close);
		}
		else
		{
			start_time = System.currentTimeMillis();
			time_diff = start_time - end_time;
			TimeValue.setText(String.valueOf(time_diff));
			Distance.setText(R.string.prox_far);
		}
		
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		mSensorManager.unregisterListener(this, mSensor);
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
	
}