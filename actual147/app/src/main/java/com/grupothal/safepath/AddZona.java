package com.grupothal.safepath;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class AddZona extends AppCompatActivity {

    private GoogleMap mMap;

    public Constantes configuration;

    public Button btn_bajo;
    public Button btn_medio;
    public Button btn_alto;
    private Button btn_confirmar;

    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private int calificacion;
    private TextView descripcion;
    private SeekBar seekBar_radio;
    private String descriptionTxt;
    private Circle my_zone;

    private LatLng locationDefault;
    private GPSclass gps;

    private MapView mapView;

    private Marker my_marker;

    //Google Analitics
    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.grupothal.safepath.R.layout.activity_add_zona);

        this.setTitle("Añade una Zona");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        iniciarConfig();
        //Iniciar Mapa
        //MapFragment fragment = (MapFragment)findViewById(R.id.mapCalification);
        //fragment.getMapAsync(this);
        //Inicializamos Componentes
        calificacion = 0;
        locationDefault = new LatLng(-16.411141,-71.540515);
        gps = new GPSclass(this);
        initData(savedInstanceState);
        Ini_seekBar_radio();
        Ini_botones_calificar();
        Ini_addDescription_pop();

        /*
        descripcion_text = new EditText(getActivity());
        descripcion_text.setText("");*/
        Toast.makeText(this, "Puedes mover el marcador presionando sobre el un pequeño tiempo!!", Toast.LENGTH_LONG).show();

        Ini_GoogleAnalitics();
    }

    public void Ini_GoogleAnalitics()
    {
        analytics = GoogleAnalytics.getInstance(this);
        analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker(Constantes.PROPERTY_ID);
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        tracker.setScreenName("Añadir Rutas");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    public void iniciarConfig(){
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        configuration = new Constantes(w,h, this);
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
    //Inicializa
    private void initData(Bundle savedInstanceState){
        try{
            mapView = (MapView) findViewById(R.id.mi_mapa_addZona);
            mapView.onCreate(savedInstanceState);

            mMap = mapView.getMap();
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //seteamos la latitud y longitud que se mostrara en el mapa

            //LatLng lat_lng = new LatLng(location.getLatitude(),location.getLongitude());
            //El marcador y mi zona estaran juntos
            verificarGPS();

            my_marker = mMap.addMarker(new MarkerOptions()
                    .position(locationDefault)
                    .title("Zona ha añadir")
                            //.icon(BitmapDescriptorFactory.fromAsset("icons/inicio.png"))
                    .draggable(true));
            my_marker.showInfoWindow();

            my_zone = mMap.addCircle(new CircleOptions()
                    .center(locationDefault)
                    .radius(10)
                    .strokeColor(Color.parseColor("#9CCC65"))
                    .fillColor(Color.parseColor("#33691E")));

            int zoom_ = 13;
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDefault, zoom_));

            // Zoom out to zoom level 13, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom_ + 3), 2000, null);


            //AL  Arrastrar el marcador se ejecuta esta funcion
            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

                @Override
                public void onMarkerDragStart(Marker marker) {
                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    marker.setSnippet(marker.getPosition().toString());
                    my_zone.setCenter(marker.getPosition());
                }

                @Override
                public void onMarkerDrag(Marker marker) {
                }

            });
        }catch (Exception e){
            //error al cargar el mapa
        }
    }

    //-----------------------------------  FUNCIONES DE CALIFICACION ---------------------------------------------
    //Si activa el GPS deberia de haber un boton de actualizar para mostrar su ubicacion
    public void verificarGPS()
    {
        Location lo = gps.getPosition();
        if(lo != null){
            //Toast.makeText(getActivity(), lo.toString(), Toast.LENGTH_LONG).show();
            locationDefault = new LatLng(lo.getLatitude(), lo.getLongitude());}
    }

    //Inicializamos los botones para calificar
    public void Ini_botones_calificar()
    {
        btn_bajo = (Button)findViewById(com.grupothal.safepath.R.id.btn_cal_bajo);
        btn_medio = (Button)findViewById(com.grupothal.safepath.R.id.btn_cal_medio);
        btn_alto = (Button)findViewById(com.grupothal.safepath.R.id.btn_cal_alto);

        btn_bajo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_calif_Bajo();
            }
        });
        btn_medio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_calif_Medio();
            }
        });
        btn_alto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_calif_Alto();
            }
        });

        btn_confirmar = (Button)findViewById(com.grupothal.safepath.R.id.btn_confirmarAddZona);
        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_qualification();
            }
        });
    }
    //Boton calificacion tipo bajo
    public void btn_calif_Bajo()
    {
        calificacion = 1;

        btn_bajo.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_bajo_sel)));
        btn_medio.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_medio)));
        btn_alto.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_alto)));

        btn_bajo.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_bajo)));
        btn_medio.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_medio_sel)));
        btn_alto.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_alto_sel)));
        //Cambiamos el color de mi zona
        my_zone.setFillColor(Color.parseColor(configuration.list_colores_center.get(0)));
        my_zone.setStrokeColor(Color.parseColor(configuration.list_colores.get(0)));

        my_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_niv_bajo));
    }
    //Boton calificacion tipo Medio
    public void btn_calif_Medio()
    {
        calificacion = 3;

        btn_bajo.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_bajo)));
        btn_medio.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_medio_sel)));
        btn_alto.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_alto)));

        btn_bajo.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_bajo_sel)));
        btn_medio.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_medio)));
        btn_alto.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_alto_sel)));
        //Cambiamos el color de mi zona
        my_zone.setFillColor(Color.parseColor(configuration.list_colores_center.get(2)));
        my_zone.setStrokeColor(Color.parseColor(configuration.list_colores.get(2)));

        my_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_niv_medio));
    }
    //Boton calificacion tipo Alto
    public void btn_calif_Alto()
    {
        calificacion = 5;

        btn_bajo.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_bajo)));
        btn_medio.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_medio)));
        btn_alto.setBackgroundColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_alto_sel)));

        btn_bajo.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_bajo_sel)));
        btn_medio.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_medio_sel)));
        btn_alto.setTextColor(Color.parseColor(getString(com.grupothal.safepath.R.string.color_alto)));
        //Cambiamos el color de mi zona
        my_zone.setFillColor(Color.parseColor(configuration.list_colores_center.get(4)));
        my_zone.setStrokeColor(Color.parseColor(configuration.list_colores.get(4)));

        my_marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_niv_alto));
    }

    public void add_qualification()
    {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Adding Zone")
                .build());

        SharedPreferences miCuentaCrear = this.getSharedPreferences(Constantes.MY_PREFS_NAME, this.MODE_PRIVATE);
        String clave_ = miCuentaCrear.getString("clave", null);
        Log.d("CLAVE", clave_);

        RequestParams params = new RequestParams();
        String idface = "1526487456";
        String idGoogle = "";
        String idExtra = "";

        if(clave_!=null){
            String id_clave = clave_.substring(0,2);
            if(id_clave.equals(Constantes.ID_FB)){
                idface = clave_;
            }else{
                idface="";
                idExtra = miCuentaCrear.getString("email",null);
            }
        }

        String descript = descriptionTxt;//descripcion_text.getText().toString();
        double lat = my_marker.getPosition().latitude;
        double lng = my_marker.getPosition().longitude;
        double radio = my_zone.getRadius();
        //calificacion->nivel
        if(calificacion==0){
            Toast.makeText(this, "Califique la zona!!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            params.put("idFacebook", idface);
            params.put("idGooglePlus",idGoogle);
            params.put("idExtra", idExtra);
            params.put("descripcion", descript);
            params.put("lat", lat);
            params.put("lng",lng);
            params.put("radio",radio);
            params.put("nivel",calificacion);

        } catch (Exception  e) {
            //Error al colocar los parametros
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constantes.URL_BASE + Constantes.LINK_BD_ZONA, params, registerZone());

    }
    //Mostrar mensajes al añadir una zona
    private AsyncHttpResponseHandler registerZone() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(AddZona.this);
                pDialog.setMessage("Añadiendo Zona ...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(AddZona.this, "Error al añadir zona!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                btn_confirmar.setText("AÑADIR OTRA ZONA");
                Toast.makeText(AddZona.this, "Listo!", Toast.LENGTH_LONG).show();
            }
        };
    }

    //Inicializamos la funcion de añadir descripcion - pop up
    public void Ini_addDescription_pop()
    {
        descripcion = (TextView)findViewById(com.grupothal.safepath.R.id.pop_addDescription);
        descripcion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tracker.send(new HitBuilders.EventBuilder()
                        .setCategory("Action")
                        .setAction("Adding Description")
                        .build());
                //startActivity(new Intent(getActivity(), PopAddQualification.class));
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddZona.this);

                final EditText descripcion_text = new EditText(AddZona.this);
                descripcion_text.setHint("Escribe una descripción ...");
                descripcion_text.setHeight(configuration.getHeight(200));
                descripcion_text.setGravity(Gravity.TOP);

                alertDialogBuilder.setTitle("Añadir una descripción:");

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(descripcion_text);

                // set dialog message
                alertDialogBuilder.setCancelable(false)
                        .setNegativeButton("CANCELAR",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        descriptionTxt = "";
                                        dialog.cancel();
                                    }
                                })
                        .setPositiveButton("ACEPTAR", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                descriptionTxt = descripcion_text.getText().toString();
                                descripcion.setTextColor(Color.parseColor("#1B5E20"));
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        });
    }



    //Iniializamos el seekbar para el radio de la zona
    public void Ini_seekBar_radio()
    {
        seekBar_radio = (SeekBar)findViewById(com.grupothal.safepath.R.id.seekBar_radio);
        seekBar_radio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                my_zone.setRadius((double) progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
