package com.example.proximitygesture;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class Help extends Activity {
	 
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.help);
				
		TextView tv = (TextView) findViewById(R.id.textView2);
		tv.setText("How to Wave:\n Please wave the hand from left or right side of the device"
		+ "closely in front of the proximity sensor, and back to the original position where "
		+ "you started from as shown in figure");


		TextView tv1 = (TextView) findViewById(R.id.textView1);
		tv1.setText("How to hold:\n Please Block the proximity sensor for a while and "
		+ "quickly release it after the few seconds, as shown in the figure");


		super.onCreate(savedInstanceState);
	}
	
}		