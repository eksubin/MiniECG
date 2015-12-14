package com.ecg.roboticslab.ecg;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


public class menu extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void call_devicelist(View arg0) {
        Animation doalpha = AnimationUtils.loadAnimation(this, R.anim.anim_button);
        arg0.startAnimation(doalpha);
        Intent call_device = new Intent(getApplicationContext(),DeviceList.class);
        startActivity(call_device);
    }

    public void call_about(View arg0) {
        Animation doalpha2 = AnimationUtils.loadAnimation(this,R.anim.anim_button);
        arg0.startAnimation(doalpha2);
        Intent call_about_page = new Intent(getApplicationContext(),about_page.class);
        startActivity(call_about_page);
    }

    public void know_more(View arg0) {
        Animation doalpha3 = AnimationUtils.loadAnimation(this,R.anim.anim_button);
        arg0.startAnimation(doalpha3);
        Intent call_know_more = new Intent(getApplicationContext(),know_more.class);
        startActivity(call_know_more);
    }
}
