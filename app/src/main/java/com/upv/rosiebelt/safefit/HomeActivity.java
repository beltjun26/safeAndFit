package com.upv.rosiebelt.safefit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.upv.rosiebelt.safefit.fragments.ActivityFragment;
import com.upv.rosiebelt.safefit.fragments.DirectionsFragment;
import com.upv.rosiebelt.safefit.fragments.HomeFragment;
import com.upv.rosiebelt.safefit.sql.DBUser;
import com.upv.rosiebelt.safefit.utility.BackgroundDetectedActivitiesService;
import com.upv.rosiebelt.safefit.utility.BottomNavigationViewHelper;
import com.upv.rosiebelt.safefit.utility.Constants;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment homefragment;
    BroadcastReceiver broadcastReceiver;
    NavigationView navigationView;
    DBUser dbuser;
    public DrawerLayout drawerLayout;
    public String currentModetext = "Loading";
    public String currentConfidence = "Loading";
    public int currentIcon = R.drawable.icon_still;
    private  BackgroundDetectedActivitiesService backgroundDetectedActivitiesService;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundDetectedActivitiesService.LocalBinder binder  = (BackgroundDetectedActivitiesService.LocalBinder) iBinder;
            backgroundDetectedActivitiesService = binder.getServiceInstant();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        dbuser = new DBUser(HomeActivity.this);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Cursor cursor = dbuser.getData(new String[]{DBUser.UserEntry.COLUMN_NAME_SEX, DBUser.UserEntry.COLUMN_NAME_FULLNAME, DBUser.UserEntry.COLUMN_NAME_EMAIL});

        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.header_fullname)).setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_FULLNAME)));
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.header_email)).setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_EMAIL)));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homefragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_frame, homefragment, "Home Fragment");
        transaction.addToBackStack(null);
        transaction.commit();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)){
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence",0);
                    handleUserActivity(type, confidence);
                }
            }
        };
        startTracking();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    homefragment = new HomeFragment();
                    transaction.replace(R.id.home_frame, homefragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_statistics:
//                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_directions:
                    fragment = new DirectionsFragment();
                    transaction.replace(R.id.home_frame, fragment);
                    transaction.commit();
                    return true;
                case R.id.navigation_activity:
                    fragment = new ActivityFragment();
                    transaction.replace(R.id.home_frame, fragment);
                    transaction.commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent intent =new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("USER_ID", getIntent().getIntExtra("USER_ID", -1));
            startActivity(intent);
        } else if (id == R.id.nav_med_record) {
            startActivity(new Intent(HomeActivity.this, MedicalRecordActivity.class));
        }  else if (id == R.id.nav_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(HomeActivity.this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
        stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mBound){
            handleUserActivity(backgroundDetectedActivitiesService.getRecentActivity(), backgroundDetectedActivitiesService.getRecentConfidence());
        }
        Cursor cursor = dbuser.getData(new String[]{DBUser.UserEntry.COLUMN_NAME_SEX, DBUser.UserEntry.COLUMN_NAME_FULLNAME, DBUser.UserEntry.COLUMN_NAME_EMAIL});
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.header_fullname)).setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_FULLNAME)));
        ((TextView)navigationView.getHeaderView(0).findViewById(R.id.header_email)).setText(cursor.getString(cursor.getColumnIndex(DBUser.UserEntry.COLUMN_NAME_EMAIL)));
        LocalBroadcastManager.getInstance(HomeActivity.this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));


    }

    public void startTracking(){
        Intent intent = new Intent(HomeActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopTracking(){
        Intent intent = new Intent(HomeActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
    }
    private void handleUserActivity(int type, int confidence){
        Log.v("Testing Output", Boolean.toString(homefragment == null));
        if(homefragment !=null && homefragment.getView() != null){
            TextView mode_text = (TextView) findViewById(R.id.mode_text);
            String label = mode_text.getText().toString();
            TextView text_confidence = (TextView) findViewById(R.id.confidence);
            ImageView imageView = (ImageView) findViewById(R.id.mode_image);
            int icon =R.drawable.unknown;
            switch (type) {
                case DetectedActivity.IN_VEHICLE:
                    label = getString(R.string.vehicle_activity);
                    icon = R.drawable.icon_car;
                    break;
                case DetectedActivity.ON_BICYCLE:
                    label = getString(R.string.bicycle_activity);
                    icon = R.drawable.ic_directions_bike_black;
                    break;
                case DetectedActivity.ON_FOOT:
                    label = getString(R.string.onfoot_activity);
                    icon = R.drawable.ic_directions_walk_black_24dp;
                    break;
                case DetectedActivity.RUNNING:
                    label = getString(R.string.running_activity);
                    icon = R.drawable.ic_directions_run_black_24dp;
                    break;
                case DetectedActivity.STILL:
                    icon = R.drawable.icon_still;
                    label = "No movement";
                    break;
                case DetectedActivity.TILTING:
                    label = "Tilting";
                    icon = R.drawable.icon_still;
                    break;
                case DetectedActivity.WALKING:
                    icon = R.drawable.ic_directions_walk_black_24dp;
                    label = "Walking";
                    break;
                case DetectedActivity.UNKNOWN:
                    label = "unknown";
                    break;
            }
            if (confidence > Constants.CONFIDENCE) {
                currentIcon = icon;
                currentConfidence = confidence+"%";
                currentModetext = label;
                imageView.setImageResource(currentIcon);
                mode_text.setText(String.valueOf(currentModetext));
                text_confidence.setText(currentConfidence);
            }
        }
    }

}
