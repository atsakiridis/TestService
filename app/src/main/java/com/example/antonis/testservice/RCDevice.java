package com.example.antonis.testservice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

//public class RCDevice extends Service {
public class RCDevice {
    private static final String TAG = "RCDevice";
    //Context context;
    // Binder given to clients
    //private final IBinder myServiceBinder = new MyServiceBinder();
    public static String INTENT_ACTION_INITIALIZED = "com.example.antonis.testservice.CONNECTIVITY";
    public static String INTENT_ACTION_MESSAGE_SENT = "com.example.antonis.testservice.MESSAGE_SENT";
    // Adding custom category for grouped filtering doesn't help as you still need to provide an action
    //public static String INTENT_CATEGORY_RESTCOMM_ANDROID_SDK = "com.example.antonis.testservice.RESTCOMM_ANDROID_SDK";


    public RCDevice()
    {
    }

    /*
    public class MyServiceBinder extends Binder {
        public RCDevice getService()
        {
            // Return this instance of LocalService so clients can call public methods
            return RCDevice.this;
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

        return START_NOT_STICKY;
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
    public void onDestroy()
    {
        Log.i(TAG, "%% onDestroy");

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
    */

    /**
     * Validate params and register for push. If we have a synchronous error we return an exception
     *
     * Let's use static methods to make it super easy to use from separate activities with minimum boilerplate code
     *
     * @param parameters
     * @param deviceListener
     * @return
     * @throws RCException
     */
    public static void initialize(Context context, Map<String, Object> parameters, RCDeviceListener deviceListener) throws RCException
    {
        Log.i(TAG, "initialize()");

        // Validate and throw exception if validation issue

        // Save settings in cache

        // Perform asynchronous call to setup push

        // Let's send an unsolicited broadcast. TODO: think about whether it really makes sense to wrap connectivity events in SDK
        /*
        new Handler(context.getMainLooper()).postDelayed(
                () -> {
                    // Use broadcasts for interesting events so that we have more flexibility on events. One example that bit us
                    // with previous design is needing to be notified in non-call activities on when the call is over in the
                    // scenario of backgrounding. This was not possible previously and would be too messy to implement
                    Log.i(TAG, "Sending connectivity broadcast intent");
                    Intent intent = new Intent();
                    intent.setAction(INTENT_ACTION_INITIALIZED);
                    intent.putExtra("data","Connectivity Action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                , 5000);
         */

        new Handler(context.getMainLooper()).postDelayed(
                () -> {
                    Log.i(TAG, "Calling callback");
                    // notify that initialization is good if the user is still on the Activity from which they initialized
                    deviceListener.onInitialized(true);

                    // notify that initialization is good for users that have left the initialization Activity. TODO: But how do we make sure that we are still running
                    // when this async functionality is finished???
                    Intent intent = new Intent();
                    intent.setAction(INTENT_ACTION_INITIALIZED);
                    intent.putExtra("data","Initialized Action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                , 2000);
    }

    public static RCConnection connect(Context context, HashMap<String, Object> parameters, RCConnection.RCConnectionListener listener) throws RCException
    {
        // We need to asynchronously register signaling and when ready start the call

        Log.i(TAG, "connect()");

        RCConnection connection = new RCConnection(context, listener);

        return connection;
    }

    public static String sendMessage(Context context, String message, Map<String, String> parameters, RCDeviceMessageListener deviceListener) throws RCException
    {
        Log.i(TAG, "sendMessage()");

        new Handler(context.getMainLooper()).postDelayed(
                () -> {
                    Log.i(TAG, "Calling callback");
                    // notify that initialization is good if the user is still on the Activity from which they initialized
                    deviceListener.onMessageSent(true);

                    // notify that initialization is good for users that have left the initialization Activity. TODO: But how do we make sure that we are still running
                    // when this async functionality is finished???
                    Intent intent = new Intent();
                    intent.setAction(INTENT_ACTION_MESSAGE_SENT);
                    intent.putExtra("data","Connectivity Action");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                , 2000);

        return "message-id";
    }

    /**
     * Callbacks are for asynchronous responses to specific API calls, like initialize().
     * More general & unsolicited events, should go out with broadcasts, so that it's easier
     * to subscribe from any activity without having to bind to service
     */
    public interface RCDeviceListener {
        // Push notifications are registered; if status is ok, then user is free to use the App
        void onInitialized(boolean status);
    }

    // Let's split out Message listener from RCDevice, since 99% of the times there's a separate activity for each
    public interface RCDeviceMessageListener {
        void onMessageSent(boolean status);
    }

}
