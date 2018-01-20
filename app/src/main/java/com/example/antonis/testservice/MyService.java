package com.example.antonis.testservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.Map;

public class MyService extends Service {
    private static final String TAG = "MyService";
    // Binder given to clients
    private final IBinder myServiceBinder = new MyServiceBinder();
    public static String INTENT_ACTION_CONNECTIVITY = "com.example.antonis.testservice.CONNECTIVITY";


    public MyService()
    {
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class MyServiceBinder extends Binder {
        public MyService getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }

    /**
     * Internal service callback; not meant for application use
     */
    @Override
    public void onCreate()
    {
        // Only runs once, when service is created
        Log.i(TAG, "%% onCreate");

    }

    /**
     * Internal service callback; not meant for application use
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // Runs whenever the user calls startService()
        Log.i(TAG, "%% onStartCommand");

        return START_NOT_STICKY;
    }

    /**
     * Internal service callback; not meant for application use
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        Log.i(TAG, "%% onBind");

        return myServiceBinder;
    }


    /**
     * Internal service callback; not meant for application use
     */
    @Override
    public void onRebind(Intent intent)
    {
        Log.i(TAG, "%% onRebind");

    }

    /**
     * Internal service callback; not meant for application use
     */
    @Override
    public void onDestroy()
    {
        Log.i(TAG, "%% onDestroy");

    }

    /**
     * Internal service callback; not meant for application use
     */
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

    /**
     * Validate params and register for push. If we have a synchronous error we return an exception
     * @param parameters
     * @param deviceListener
     * @return
     * @throws MyServiceException
     */
    public void initialize(Map<String, Object> parameters, MyServiceListener deviceListener) throws MyServiceException
    {
        Log.i(TAG, "initialize()");
        new Handler(getMainLooper()).postDelayed(
                () -> {
                    // Use broadcasts for interesting events so that we have more flexibility on events. One example that bit us
                    // with previous design is needing to be notified in non-call activities on when the call is over in the
                    // scenario of backgrounding. This was not possible previously and would be too messy to implement
                    Log.i(TAG, "Sending connectivity broadcast intent");
                    Intent intent = new Intent();
                    intent.setAction(INTENT_ACTION_CONNECTIVITY);
                    intent.putExtra("data","Connectivity Action");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
                , 5000);

        new Handler(getMainLooper()).postDelayed(
                () -> {
                    Log.i(TAG, "Calling calback");
                    // notify that initialization is good
                    deviceListener.onInitialized(true);
                }
                , 1000);
    }

    /**
     * Callbacks are for asynchronous responses to specific API calls, like initialize().
     * General events, like connectivity updates, etc should go out with broadcasts, so that it's easier
     * to subscribe from any activity without having to bind to service
     */
    public interface MyServiceListener {
        void onInitialized(boolean status);
    }
}
