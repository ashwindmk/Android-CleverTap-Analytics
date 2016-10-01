package com.example.ashwin.clevertapanalytics;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CleverTapAPI cleverTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(BuildConfig.DEBUG)
        {
            CleverTapAPI.changeCredentials("TEST-your clevertap account id", "TEST-your clevertap account token");
        }

        //Test credentials
        //Must be called before super.onCreate()
        /*CleverTapAPI.changeCredentials("TEST-98W-948-K74Z", "TEST-b4c-0bc");
        ActivityLifecycleCallback.register(getApplication());*/

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://android-firebase-test-a2516.firebaseio.com/makeup-app-list/apps/");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FirebaseRecyclerAdapter<App, AppViewHolder>(App.class, R.layout.app_list_row, AppViewHolder.class, ref) {
            @Override
            public void populateViewHolder(AppViewHolder avh, App app, final int position) {
                avh.setTitle(app.getTitle());
                avh.setSubTitle(app.getSubtitle());
                avh.setAppLogo(getApplicationContext(), app.getApplogo());
                avh.setAppImage(getApplicationContext(), app.getAppimage());
                avh.setCallToActionText(app.getCalltoactiontext());

                avh.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        App app = getItem(position);
                        Toast.makeText(getApplicationContext(), app.getTitle() + " ( " + app.getPackagename() +" ) is selected!", Toast.LENGTH_SHORT).show();

                        //handle events using CleverTap
                        HashMap<String, Object> appUpdate = new HashMap<>();
                        appUpdate.put("Name", app.getTitle());                         // Can be either Y or N
                        appUpdate.put("PackageName", app.getPackagename());
                        cleverTap.event.push("App Clicked", appUpdate);
                    }
                });
            }
        };

        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
