package com.mapas.sdm.seminariomapas;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by miquel on 9/03/15.
 */
public class MyListener implements LocationListener {

    private Context context;

    private EditText etLongitude;
    private EditText etLatitude;

    public MyListener(Context context) {
        this.context = context;
        etLongitude = (EditText) ((Activity) context).findViewById(R.id.etLongitude);
        etLatitude = (EditText) ((Activity) context).findViewById(R.id.etLatitude);

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(context, "Location changed", Toast.LENGTH_SHORT).show();
        etLongitude.setText(Double.toString(location.getLongitude()));
        etLatitude.setText(Double.toString(location.getLatitude()));

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
