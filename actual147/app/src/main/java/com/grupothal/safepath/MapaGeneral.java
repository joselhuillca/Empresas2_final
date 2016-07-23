package com.grupothal.safepath;

import android.app.Fragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MapaGeneral extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    public List<Zona> list_zonas;
    private LatLng locationDefault;

    private GPSclass gps;

    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;

    MediaPlayer alarma_notification;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstancestate)
    {
        return  inflater.inflate(com.grupothal.safepath.R.layout.activity_mapa_general,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(com.grupothal.safepath.R.id.mapB);
        fragment.getMapAsync(this);

        list_zonas = new ArrayList<Zona>();
        locationDefault = new LatLng(-16.411141,-71.540515);

        gps = new GPSclass(getActivity());


        alarma_notification = MediaPlayer.create(getActivity(), com.grupothal.safepath.R.raw.videoplayback);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //gps.terminarLocalizacion();
        list_zonas.clear();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            int zoom_ = 10;
            mMap = googleMap;
            //cargamos zonas y cambiamos locationDefault
            load_zones();
            //getLocation();
            //Animaciones para el marker
            verificarGPS();
            Constantes.miPosicion  = mMap.addMarker(new MarkerOptions().position(locationDefault).title("Tu estas aqui!").icon(BitmapDescriptorFactory.fromAsset("icons/inicio.png")));

            Constantes.miPosicion.showInfoWindow();

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDefault, zoom_));

            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom_ + 5), 2000, null);
            //notificZonaP();

        }catch (Exception e){
            //Problemas al cargar el mapa
        }
    }


    //MIS FUNCIONES

    //Si activa el GPS deberia de haber un boton de actualizar para mostrar su ubicacion
    public void verificarGPS()
    {

        Location lo = gps.getPosition();
        if(lo != null){

            locationDefault = new LatLng(lo.getLatitude(), lo.getLongitude());}

    }

    //Cargar las Zonas de la Base de Datos
    public  void load_zones()
    {
        try{
            list_zonas.clear();
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(Constantes.URL_BASE + Constantes.LINK_BD_ZONA, null,getzonas());
        }catch (Exception e) {
            //
        }

    }

    //Dibujar las zonas en el mapa
    public void drawZonas(){
        try {
            for(int i=0;i<list_zonas.size();i++){
                int nivel = list_zonas.get(i).getNivel()-1;
                if(nivel>=5){nivel=4;}
                final LatLng posicionZona= new LatLng(list_zonas.get(i).getLat(), list_zonas.get(i).getLng());
                //Añadimos las zonas
                mMap.addCircle(new CircleOptions()
                        .center(posicionZona)
                        .radius(list_zonas.get(i).getRadio())
                        .strokeColor(Color.parseColor(Constantes.list_colores.get(nivel)))
                        .fillColor(Color.parseColor(Constantes.list_colores_center.get(nivel))));
                //Log.d("Lat:",String.valueOf(list_zonas.get(i).getLat()));
                //Añadir los marcadores para las zonas
                mMap.addMarker(new MarkerOptions()
                        .position(posicionZona)
                        .title(list_zonas.get(i).getDescripcion())
                        .icon(BitmapDescriptorFactory.fromResource(icon_nivel_zona(nivel))));
            }
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
    }

    //Icono Adecuado segun el nivel de zona
    public int icon_nivel_zona(int nivel)
    {
        int icon_nivel = 0;
        if(nivel==0 || nivel==1){
            icon_nivel = com.grupothal.safepath.R.drawable.icon_niv_bajo;
        }else{
            if(nivel==3 || nivel==2){
                icon_nivel = com.grupothal.safepath.R.drawable.icon_niv_medio;
            }else{
                icon_nivel = com.grupothal.safepath.R.drawable.icon_niv_alto;
            }
        }
        return  icon_nivel;
    }

    //Mostrar mensajes al añadir una zona
    private AsyncHttpResponseHandler getzonas() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(getActivity());
                pDialog.setMessage("Descargando data ...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(getActivity(), "Error al descargar zonas!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                String resultadoJson = new String(response);
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(resultadoJson);
                JsonArray arrayZonas = tradeElement.getAsJsonArray();
                int numZonas = arrayZonas.size();
                for(int i=0;i<numZonas;i++){
                    JsonElement obj = arrayZonas.get(i);
                    JsonObject json = obj.getAsJsonObject();
                    //JsonElement ele = json.get("_id");
                    Zona z = new Zona();
                    //z.set_id(json.get("_id").getAsString());
                    z.setIdFacebook(json.get("idFacebook").getAsString());
                    z.setIdGooglePlus(json.get("idGooglePlus").getAsString());
                    z.setIdExtra(json.get("idExtra").getAsString());
                    z.setLat(json.get("lat").getAsDouble());
                    z.setLng(json.get("lng").getAsDouble());
                    z.setRadio(json.get("radio").getAsInt());
                    if(json.get("descripcion")!=null){
                        z.setDescripcion(json.get("descripcion").getAsString());
                    }
                    else{z.setDescripcion("");}
                    z.setNivel(json.get("nivel").getAsInt());
                    list_zonas.add(z);

                }
                drawZonas();
                Toast.makeText(getActivity(), "Zonas Cargadas...!", Toast.LENGTH_LONG).show();
                //notificZonaP();
            }
        };
    }

    //Verificar si estas en una zona peligrosa
    public void notificZonaP()
    {
        double dist = 100;
        Log.d("list_zonas",String.valueOf(list_zonas.size()));

        for(int i=0;i<list_zonas.size();i++){
            Location lo = gps.getPosition();
            LatLng actalPos = new LatLng(lo.getLatitude(),lo.getLongitude());
            LatLng posicionLatLng = new LatLng(list_zonas.get(i).lat,list_zonas.get(i).lng);
            double distancia = getDistanciaMetros(actalPos,posicionLatLng);
            Log.d("Distancias", String.valueOf(distancia));
            if(distancia<dist){
                showNotification();
            }
        }
    }

    // Ejemplo de Notificacion
    public void showNotification(){

        //Alarma de la Notificacion
        play_alarm();

        //Builder es el que proporciona una manera conveniente diferentes campos de una notificación y generar vistas de contenido
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());
        builder.setSmallIcon(com.grupothal.safepath.R.drawable.icono);
        builder.setContentTitle("Safe Paths V1");
        builder.setContentText("Te estás aproximando a una zona peligrosa");
        //Intent sirve para invocar componentes, ahora solo llamara al mismo layout
        Intent intent= new Intent(getActivity(),NavigatorMapas.class);
        //Es para que funcione en versiones anteriores
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        //PendingIntent será el que contenga la configuración que necesitamos para que cuando pulsemos la notificación en este caso abrirá el mainActivity
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //NotificationManager es capaz de lanzar el Intent que nosotros hemos configurado
        NotificationManager NM= (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);
        NM.notify(0,builder.build());
    }

    public void play_alarm()
    {
        Thread t=new Thread()
        {
            public void run()
            {
                try {
                    alarma_notification.start();
                    sleep(2000);
                    alarma_notification.stop();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    private double getDistanciaMetros(LatLng markerA, LatLng markerB){
        double latA =  markerA.latitude * Math.PI / 180;
        double lonA =  markerA.longitude * Math.PI / 180;
        double latB =  markerB.latitude * Math.PI / 180;
        double lonB =  markerB.longitude * Math.PI / 180;
        double distancia = Constantes.RADIO_TIERRA * Math.acos(Math.sin(latA) * Math.sin(latB)
                + Math.cos(latA) * Math.cos(latB) * Math.cos(lonA - lonB)) *  1000;

        return Math.floor(distancia * 1000) / 1000;
    }
}
