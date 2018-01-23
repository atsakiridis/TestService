package com.example.antonis.testservice;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class CallActivity extends AppCompatActivity implements RCConnection.RCConnectionListener {
    private static final String TAG = "CallActivity";
    RCConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.i(TAG, "Disconnecting");
                connection.disconnect();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try {
            Log.i(TAG, "Connecting");
            connection = RCDevice.connect(getApplicationContext(), null, this);
        } catch (RCException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnected(boolean status)
    {
        Log.i(TAG, "Connection is connected");
    }

    @Override
    public void onDisconnected(boolean status)
    {
        Log.i(TAG, "Connection is disconnected");
        finish();
    }
}
