package com.mapas.sdm.seminariomapas;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by miquel on 8/03/15.
 */
public class City {

    private String name;
    private double latitude;
    private double longitude;
    private int icon_id;

    public City(String name, double latitude, double longitude, int icon_id) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.icon_id = icon_id;
    }

    public City() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getIcon_id() {
        return icon_id;
    }

    public void setIcon_id(int icon_id) {
        this.icon_id = icon_id;
    }

    public LatLng toLatLng() {
        LatLng aux = new LatLng(latitude,longitude);
        return aux;
    }
}
