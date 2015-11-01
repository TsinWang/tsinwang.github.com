package com.example.proximitygesture;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.text.format.Time;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

public class AppWidgetProvider extends android.appwidget.AppWidgetProvider
{

	public Context	con;
	static String	tag_action	= "cn.com.widget.click";
	private Intent	it;

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		// TODO Auto-generated method stub

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.myappwidget);

		Intent intent = new Intent("cn.com.widget.click");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		remoteViews.setOnClickPendingIntent(R.id.layout, pendingIntent);
		appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);

		super.onUpdate(context, appWidgetManager, appWidgetIds);

	}

	public static boolean isServiceRunning(Context context)
	{
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
		{
			if ("com.example.proximitygesture.SensorService".equals(service.service.getClassName()))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub

		super.onReceive(context, intent);
		if (intent.getAction().equals(tag_action))
		{
			boolean bl1 = isServiceRunning(context);
			if (bl1 == true)
			{
				Intent it = new Intent(context, SensorService.class);
				Toast.makeText(context, "Service Stop", 1).show();
				context.stopService(it);
			}
			else
			{
				Intent it = new Intent(context, SensorService.class);
				Toast.makeText(context, "Service Start", 1).show();
				context.startService(it);
			}
		}

	}
}
