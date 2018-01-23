package com.example.antonis.testservice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.example.antonis.testservice.dummy.DummyContent;

import java.util.HashMap;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
//public class ItemListActivity extends AppCompatActivity implements RCDevice.MyServiceListener,
//        ServiceConnection {
public class ItemListActivity extends AppCompatActivity implements RCDevice.MyServiceListener {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;


    private static final String TAG = "ItemListActivity";
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(TAG, "%% onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        // The activity is about to become visible.
        Log.i(TAG, "%% onStart");

        // Register receiver to receive events from service
        broadcastReceiver = new MyBroadcastReceiver();

        // Doesn't seem to be a way to specify wildcard action; if no action is passed then sender must also pass no action
        IntentFilter filter = new IntentFilter(RCDevice.INTENT_ACTION_CONNECTIVITY);
        filter.addAction(RCConnection.INTENT_ACTION_CONNECTION_DISCONNECTED);
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

        HashMap<String, Object> params = new HashMap<>();
        // Important parameters:
        // - Intents through which we can wake specific Activities on user requests
        //   (like when answering a call we need to know which is the call handling activity so that we can wake it)
        // - Signaling parameters so that we can keep them around for when the call arrives. An issue here is
        //   how can we fully validate them? That is what happens if credentials passed are valid BUT don't work when
        //   we try on the server later that we receive the call
        // - Push parameters so that we can register right away
        //params.put();
        //RCDevice device = new RCDevice();
        try {
            Log.i(TAG, "Initializing RCDevice");
            RCDevice.initialize(getApplicationContext(), params, this);
        } catch (MyException e) {
            e.printStackTrace();
        }

        //bindService(new Intent(this, RCDevice.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // The activity has become visible (it is now "resumed").
        Log.i(TAG, "%% onResume");

    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i(TAG, "%% onPause");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
        Log.i(TAG, "%% onStop");

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.i(TAG, "%% onDestroy");
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView)
    {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));
    }

/*
    @Override
    public void onServiceConnected(ComponentName name, IBinder service)
    {
        Log.i(TAG, "%% onServiceConnected");
        // We've bound to LocalService, cast the IBinder and get LocalService instance
        RCDevice.MyServiceBinder binder = (RCDevice.MyServiceBinder) service;
        RCDevice device = binder.getService();

        try {
            HashMap<String, Object> params = new HashMap<>();
            device.initialize(params, this);
        }
        catch (MyException e) {

        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name)
    {

    }
*/

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.id);
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, ItemDetailActivity.class);
                    intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane)
        {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position)
        {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount()
        {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view)
            {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    /**
     * Receiver for getting broadcasts from Service
     */
    public class MyBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "MyBroadcastReceiver";
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received broadcast");
            StringBuilder sb = new StringBuilder();
            sb.append("Action: " + intent.getAction() + "\n");
            sb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = sb.toString();
            Log.d(TAG, log);
            Toast.makeText(context, log, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * RCDevice callbacks
     * @param status
     */
    @Override
    public void onInitialized(boolean status)
    {
        Log.i(TAG, "Service is initialized");

        // Done with initialization; don't need to keep the service running any longer
        //unbindService(this);
    }

}
