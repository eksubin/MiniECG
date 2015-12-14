package com.ecg.roboticslab.ecg;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;



public class Page_1 extends Activity
{



     int value;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_page_1);
        new Thread(new BarUpdate()).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page_1, menu);
        Intent intent=new Intent(this, menu.class);
        startActivity(intent);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id==R.id.action_settings)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private class BarUpdate implements Runnable
    {
        @Override
        public void run()
        {

            for (int i = 0; i <= 100; i++)
            {
                try
                {
                    Thread.sleep(50);
                    value = i;
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                if(value == 100)
                {
                    Intent j = new Intent(getApplicationContext(), menu.class);
                    startActivity(j);
                    finish();

                }
            }
        }
    }
}


