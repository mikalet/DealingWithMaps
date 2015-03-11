package com.mapas.sdm.seminariomapas;

import android.app.Dialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private ArrayList<City> cities;

    private LocationManager manager;
    private MyListener listener;

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest request;

    private HashMap<Marker,City> marker_cities =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec=tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("tab1");
        tabs.addTab(spec);

        spec=tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("tab2");
        tabs.addTab(spec);

        tabs.setCurrentTab(0);

        /** Using Location Manager**/

        // Si quereis utilizar LocationManager descomentad la siguiente línea
        // y comentad las utilizadas para los GPlay Services

        //setLocationManager();

        /** Using Location Services**/
        createLocationRequest();
        setLocationServices();

        // Inicializamos el vector de ciudades

        setCities();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check the availability of the Google Play Services

        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(available, this, 0);
            if (dialog != null) {
                 MyErrorDialog errorDialog = new MyErrorDialog();
                 errorDialog.setDialog(dialog);
                 errorDialog.show(getSupportFragmentManager(), "errorDialog");
            }
        }

        // Instantiate the MapFragment

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {

               setUpMarkers();
                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                        // Animamos la camara hacia la posición del mapa donde hemos pulsado
                        // Existen muchas posibilidades a la hora de mover la camara
                        // jugando con las opciones que nos ofrece CamaraPosition
                        // os recomiendo que le echéis un vistazo a las opciones en

                        /**
                         *
                         *       https://developers.google.com/maps/documentation/android/views
                         *
                         * **/
                    CameraPosition pos = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(5)
                            .build();
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(pos), 3000, null);

                    }
                });


                mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {

                        // Añadimos un marker con el icono por defecto en el punto del mapa
                        // donde realizamos una pulsación prolongada

                        // Probad atributos como "flat", "rotation" o "draggable" para ver como se comportan

                        mMap.addMarker(
                                new MarkerOptions()
                                        .position(latLng)
                                        .title("new position at"+latLng.toString())
                        );
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        // Cambiamos el icono a mostrar cuando pulsamos sobre el marker

                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        // Si el return se establece a false (por defecto)
                        // Cuando pulsamos sobre el marker, se realizan las opciones definidas
                        // en este método, y a continuación su comportamiento habitual:
                        // centrar la vista en el marker, y mostrar su infowindow
                        // Si se devuelve "true", SOLO se ejecuta el comportamiento definido
                        // en este método
                        return false;
                    }
                });


                mMap.setInfoWindowAdapter(new CustomAdapter(this));

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Modificamos el tipo de mapa a partir de las opciones del menu

        int id = item.getItemId();

        switch(id){
            case R.id.mnone:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.mnormal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mhybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.msatellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mterrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    private void setCities(){
        if(cities==null)
            cities = new ArrayList<City>();
        else
            cities.clear();

        cities.add(new City("Barcelona",41.38792,2.169919,R.mipmap.ic_bcn));
        cities.add(new City("Madrid",40.41669,-3.700346,R.mipmap.ic_madrid));
        cities.add(new City("Valencia", 39.47024, -0.3768049, R.mipmap.ic_vlc));
        cities.add(new City("Granada",37.17649,-3.597929,R.mipmap.ic_granada));

    }


    // Método para utilizar LocationManager

    public void setLocationManager(){
        listener = new MyListener(this);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ToggleButton toggleLocation = (ToggleButton) findViewById(R.id.tbGeolocation);
        toggleLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (((ToggleButton) v).isChecked()) {
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
                    } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
                    }
                } else {
                    manager.removeUpdates(listener);
                }
            }
        });
    }


    // Definimos las características de las peticiones que vamos a realizar a los Location Services

    protected void createLocationRequest() {
        request = new LocationRequest();
        request.setInterval(10000);
        request.setFastestInterval(5000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Creamos un GoogleApiClient indicando el tipo de Service que vamos a utilizar
    // y donde están implementados los listeners que requiere para su funcionamiento

    public void setLocationServices(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        ToggleButton toggleLocation = (ToggleButton) findViewById(R.id.tbGeolocation);
        toggleLocation.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (((ToggleButton) v).isChecked()) {
                   mGoogleApiClient.connect();
                } else {
                   mGoogleApiClient.disconnect();
                }
            }
        });
    }

    @Override
    public void onConnected(Bundle bundle) {
        if(mGoogleApiClient.isConnected()) {

            // Asociamos el cliente a los Location Services para solicitar
            // actualizaciones de ubicación

            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, request, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(mGoogleApiClient.isConnected()) {

            // Desasociamos el cliente a los Location Services para solicitar
            // actualizaciones de ubicación

            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location changed", Toast.LENGTH_SHORT).show();
        ((EditText) findViewById(R.id.etLongitude)).setText(Double.toString(location.getLongitude()));
        ((EditText) findViewById(R.id.etLatitude)).setText(Double.toString(location.getLatitude()));
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    mGoogleApiClient, this);
        }
    }

    public HashMap<Marker, City> getMarkerCities(){
        return marker_cities;
    }

    private void setUpMarkers(){
        if(marker_cities==null){
            marker_cities = new HashMap<Marker,City>();
        }

        // Creamos un Marker para cada ciudad, y los relacionamos mediante un HashMap
        // para luego poder acceder a la información desde el InfoWindowAdapter que nos
        // hemos definido "CustomAdapter"

        for(City c: cities) {

            Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(c.toLatLng())
                            .title(c.getName())
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_pin))
            );

            marker_cities.put(marker,c);
        }
    }

}
