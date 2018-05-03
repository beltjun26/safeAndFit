package com.upv.rosiebelt.safefit.utility;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

/**
 * Created by root on 3/25/18.
 */

public class LocationTracker extends Service implements LocationListener {

    private final Context con;
    boolean isGPSon = false;
    boolean isNetworkEnabled = false;
    boolean isLocationEnabled = false;
    private static final long MIN_DISTANCE_TO_REQUEST_LOCATION = 1;
    private static final long MIN_TIME_FOR_UPDATES = 1000 * 1;
    Location location;
    double latitude, longtitude;
    LocationManager locationManager;

    public LocationTracker(Context context) {
        this.con = context;
        checkIfLocationAvailable();
    }

    private Location checkIfLocationAvailable() {
        try {
            locationManager = (LocationManager) con.getSystemService(Context.LOCATION_SERVICE);
            isGPSon = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGPSon) {
                isLocationEnabled = false;
                Toast.makeText(con, "No Location Provider is Available", Toast.LENGTH_SHORT).show();
            } else {
                isLocationEnabled = true;
                if (isGPSon) {
                    if (ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(con, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }else{
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATES, MIN_DISTANCE_TO_REQUEST_LOCATION, this);
                        if(locationManager != null){
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location!=null){
                                longtitude = location.getLongitude();
                                latitude = location.getLatitude();
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            Toast.makeText(con, "Error on check if location available", Toast.LENGTH_SHORT).show();
        }
        return location;
    }

    public void stopUsingLocation(){
        if(locationManager != null){
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    public double getLatitude(){
        if(location != null){
            return location.getLatitude();
        }
        return latitude;
    }

    public double getLongtitude(){
        if(location != null){
            return location.getLongitude();
        }
        return longtitude;
    }

    public boolean isLocationEnabled(){
        return this.isLocationEnabled;
    }

    public void askToOnLocation(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(con);
        dialog.setTitle("Settings");

        dialog.setMessage("Location is not Enabled. Do you want to go to settings to enable it?");
        dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                con.startActivity(intent);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        dialog.show();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
