package com.example.antonis.testservice;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by antonis on 1/22/18.
 */

/**
 * Important: RCConnection might live even though it's hosting activity is dead (like when we hit back from the Call screen but call continues)
 * that means that we need to notify any other interested activity in our App on the status of the ongoing call. Interesting events are:
 * - Call hung up (either from remote side or through Notification drawer which can be visible over any App activity)
 * - Call's audio got muted or unmuted
 */
public class RCConnection {
    private static final String TAG = "RCConnection";
    RCConnectionListener listener;
    Context context;
    public static String INTENT_ACTION_CONNECTION_DISCONNECTED = "com.example.antonis.testservice.INTENT_ACTION_CONNECTION_DISCONNECTED";

    public RCConnection(Context context, RCConnectionListener listener)
    {
        this.listener = listener;
        this.context = context;

        new Handler(context.getMainLooper()).postDelayed(
                () -> {
                    Log.i(TAG, "Calling callback");
                    // notify that initialization is good
                    listener.onConnected(true);
                }
                , 1000);
    }

    public void disconnect()
    {
        Log.i(TAG, "disconnect()");

        new Handler(context.getMainLooper()).postDelayed(
                () -> {
                    Log.i(TAG, "Notifying of disconnected");
                    // notify that initialization is good
                    listener.onDisconnected(true);

                    Intent intent = new Intent();
                    intent.setAction(INTENT_ACTION_CONNECTION_DISCONNECTED);
                    intent.putExtra("data","Connection Disconnected");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                , 4000);
    }

    public interface RCConnectionListener {
        void onConnected(boolean status);
        void onDisconnected(boolean status);
    }
}
