package com.grupothal.safepath;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.location.LocationListener;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Jason on 23/06/2016.
 */
public class GPSclass {
    public Context context;
    private LocationManager locationManager;
    private Location currentLocation;

    public GPSclass(Context context) {
        this.context = context;
        Constantes.miPosicion = null;
        currentLocation = getLastKnownLocation();
        if (currentLocation != null) {
            Log.e("Ultima Localización: ", currentLocation.toString());
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;}

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, changeLocation());
    }


    public Location getPosition(){
        return  currentLocation;
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location mejorPosicion = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;}
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) continue;
            if (mejorPosicion == null || l.getAccuracy() < mejorPosicion.getAccuracy()) {
                mejorPosicion = l;
            }
        }
        return mejorPosicion;
    }

    private LocationListener changeLocation(){

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("onLocationChanged", location.toString());
                currentLocation = location;
                if(Constantes.miPosicion  != null)
                    Constantes.miPosicion.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Log.e("onStatusChanged", "Cambio el estado: " + provider + "Estado: " + status);
            }
            @Override
            public void onProviderEnabled(String provider) {
                //Log.e("onProviderEnabled", "Proveedor Habilitado: " + provider);
            }
            @Override
            public void onProviderDisabled(String provider) {
                //Log.e("onProviderDisabled", "Proveedor Desabilitado: " + provider);
            }
        };

    }
}
