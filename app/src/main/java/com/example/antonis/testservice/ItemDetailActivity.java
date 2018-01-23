package com.example.antonis.testservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 */
public class ItemDetailActivity extends AppCompatActivity implements RCDevice.RCDeviceMessageListener {
    private static final String TAG = "ItemDetailActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Sending text message", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                try {
                    Log.i(TAG, "Sending message");
                    RCDevice.sendMessage(getApplicationContext(), "Hello there!", null, ItemDetailActivity.this);
                } catch (RCException e) {
                    e.printStackTrace();
                }

/*
                Log.i(TAG, "Disconnecting");
                connection.disconnect();
*/
            }
        });

        FloatingActionButton fab_call = (FloatingActionButton) findViewById(R.id.fab_call);
        fab_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "Invoking call Activity", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent intent = new Intent(ItemDetailActivity.this, CallActivity.class);
                startActivity(intent);
            }
        });


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.item_detail_container, fragment)
                    .commit();
        }
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
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // The activity is about to be destroyed.
        Log.i(TAG, "%% onDestroy");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, ItemListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // RCDevice callbacks
    @Override
    public void onMessageSent(boolean status)
    {
        Log.i(TAG, "Message is sent");
    }
}
