package com.ecg.roboticslab.ecg;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.UUID;
import android.os.Handler;


public class graph extends ActionBarActivity {
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    Thread workerThread;
    byte[] readBuffer;
    volatile boolean stopWorker;
    String address;
    InputStream mmInputStream;
    byte[] packetBytes;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    TextView textView;
    Button button;
    GraphView ecg_graph;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    int bytesAvailable;
    LineGraphSeries<DataPointInterface> series;
    int x=0;
    Scanner myscanner;
    boolean start_loop = true;

    ////////////// data string
    String snaptext;
    Paint paint;
    private BluetoothDevice positive;

    /// graph view stuff



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_graph);
        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS);
        new ConnectBT().execute();
        ecg_graph= (GraphView)findViewById(R.id.graph);
        textView=(TextView)findViewById(R.id.text);
        button = (Button) findViewById(R.id.button2);
        ecg_graph.getViewport().setScrollable(true);
        ecg_graph.getViewport().setScalable(true);
        series=new LineGraphSeries<>();
        ecg_graph.addSeries(series);
        Viewport viewport=ecg_graph.getViewport();
        viewport.setScrollable(true);
        viewport.setYAxisBoundsManual(false);//changed it to false on 10/18/2015
        viewport.setMinY(0);
        viewport.setMaxY(1023);

        ecg_graph.getGridLabelRenderer();



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_graph, menu);
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

    ////////////////////////// code for button
    public void start_function(View view) {

        beginListenForData();
    }


    public void stopit(View view) {
        start_loop = false;
    }
///////////////////////////////////////////////////////////////////// async task

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(graph.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try {
                if (btSocket == null || !isBtConnected) {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    positive = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = positive.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            } catch (IOException e) {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                msg("Connected.");
                isBtConnected = true;
                try {
                    mmInputStream = btSocket.getInputStream();
                    mmOutputStream = btSocket.getOutputStream();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
            progress.dismiss();
        }

        private void msg(String s) {
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////// Blutooth reieve

    public void beginListenForData()
    {

        final Handler handler=new Handler();
        stopWorker=false;
        readBuffer=new byte[1024];
        workerThread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bytesAvailable = mmInputStream.available();
                    if(bytesAvailable >0 )
                    packetBytes = new byte[bytesAvailable];
                    mmInputStream.read(packetBytes);
                } catch (IOException e) {
                    e.printStackTrace();

                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                            String s = new String(packetBytes);
                            myscanner = new Scanner(s);
                           start();
                        }
                    });

                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

       });
         workerThread.start();
     }

public void start() {
    final Handler handler = new Handler();
    new Thread(new Runnable() {
        @Override
        public void run() {
            for (int i = 0; i < 3500; i++) {
                if (!start_loop)
                {
                    break;
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Graph_plot();
                    }
                });
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }).start();
}

            private void Graph_plot()  {



                    series.appendData(new DataPoint(x++, myscanner.nextDouble()), true, 1000);
            }


 }





