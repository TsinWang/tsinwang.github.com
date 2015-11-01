package com.example.proximitygesture;

import java.util.ArrayList;
import java.util.List;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
 
public class SelectApp extends ListActivity {
    private PackageManager packageManager = null;
    private List<ApplicationInfo> applist = null;
    private ApkAdapter listadaptor = null;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectapp);
 
        packageManager = getPackageManager();
 
        new LoadApplications().execute();
    }
 
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
 
        return true;
    }
 
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean result = true; 
        return result;
    }
 
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
 
        ApplicationInfo app = applist.get(position);
        String appname = app.name;
        String packageName = app.packageName;
        Intent intent = new Intent();
        intent.putExtra("PackageInfo", packageName);
        Log.e("Packname","Pack "+packageName);
        setResult(2, intent);
        finish();
 /*       try {
            Intent intent = packageManager
                    .getLaunchIntentForPackage(app.packageName);
 
            if (null != intent) {
                startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(SelectApp.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(SelectApp.this, e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }*/
    }
 
    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
        for (ApplicationInfo info : list) {
            try {
                if (null != packageManager.getLaunchIntentForPackage(info.packageName)) {
                    applist.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
 
        return applist;
    }
    
    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;
 
        @Override
        protected Void doInBackground(Void... params) {
            applist = checkForLaunchIntent(packageManager.getInstalledApplications(PackageManager.GET_META_DATA));
            listadaptor = new ApkAdapter(SelectApp.this,
                    R.layout.applist_item, applist);
 
            return null;
        }
 
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
 
        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listadaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }
 
        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(SelectApp.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }
 
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}