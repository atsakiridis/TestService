package com.example.antonis.testservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import java.util.Date;

/**
 * Created by antonis on 1/23/18.
 */

public class StartedService extends Service {
    private static final String TAG = "StartedService";
    //Context context;
    // Binder given to clients
    private final IBinder myServiceBinder = new MyServiceBinder();
    public static String INTENT_ACTION_CONNECTIVITY = "com.example.antonis.testservice.CONNECTIVITY";
    private Handler timerHandler;
    // Adding custom category for grouped filtering doesn't help as you still need to provide an action
    //public static String INTENT_CATEGORY_RESTCOMM_ANDROID_SDK = "com.example.antonis.testservice.RESTCOMM_ANDROID_SDK";


    public StartedService()
    {
    }

    public class MyServiceBinder extends Binder {
        public StartedService getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return StartedService.this;
        }
    }

    @Override
    public void onCreate()
    {
        // Only runs once, when service is created
        Log.i(TAG, "%% onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Runs whenever the user calls startService()
        Log.i(TAG, "%% onStartCommand");

        timerHandler = new Handler(getMainLooper());
        startTimer();

        return START_NOT_STICKY;
    }

    void startTimer()
    {
        timerHandler.removeCallbacksAndMessages(null);
        // schedule a registration update after 'registrationRefresh' seconds
        Runnable timerRunnable = new Runnable() {
            @Override
            public void run()
            {
                Log.i(TAG, "Timer on Started service went off: " + System.currentTimeMillis());
                startTimer();
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    @Override
    public void onDestroy()
    {
        Log.i(TAG, "%% onDestroy");

    }


    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i(TAG, "%% onBind");

        return myServiceBinder;
    }


    @Override
    public void onRebind(Intent intent)
    {
        Log.i(TAG, "%% onRebind");

    }

    @Override
    public boolean onUnbind(Intent intent)
    {
        Log.i(TAG, "%%  onUnbind");

        // We need to return true so that the service's onRebind(Intent) method is called later when new clients bind to it
        // Reason this is important is to make sure isServiceAttached is always consistent and up to date. Consider this case:
        // 1. User starts call and hits Home button while call is ongoing. At that point call activity is stopped and service
        //   is unbound (and hence isServiceAttached is set to false). Notice though that it's still running as a foreground service,
        //   since the call is still live)
        // 2. User taps on the App launcher and resumes the call by being navigated to the call screen
        // 3. In the call activity code we are binding to the service, but because onUnbind() returned false previously, no onBind() or onRebind()
        //   is called and hence isServiceAttached remains false, even though we are attached to it and this messes up the service state.
        return true;
    }
}