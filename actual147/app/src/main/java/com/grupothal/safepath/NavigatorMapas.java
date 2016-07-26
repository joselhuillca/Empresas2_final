package com.grupothal.safepath;

import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class NavigatorMapas extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Constantes configuration;
    public List<Zona> list_zonas;
    private LinearLayout layoutMisCalificaciones;

    private LayoutInflater inflater;
    private RelativeLayout contenedor;

    private TextView nickname;
    private TextView email;
    private ImageView imgProfile;
    private View header;
    private NavigationView navigationView;

    private boolean isMapaGeneral = true;
    private MenuItem item_actualizar;
    private Menu menu_;

    //Google Analitics
    public static GoogleAnalytics analytics;
    public static Tracker tracker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.grupothal.safepath.R.layout.activity_navigator_mapas);
        Toolbar toolbar = (Toolbar) findViewById(com.grupothal.safepath.R.id.toolbar);
        setSupportActionBar(toolbar);

        //Vamos a la funcion Añadir Zona - addZona
        FloatingActionButton fab = (FloatingActionButton) findViewById(com.grupothal.safepath.R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addZona();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(com.grupothal.safepath.R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, com.grupothal.safepath.R.string.navigation_drawer_open, com.grupothal.safepath.R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(com.grupothal.safepath.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //My functions
        IniConfiguration();
        IniComponents();
        setMyProfile();
        funMiMapa();

        //Google Analytics
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

        tracker.setScreenName("Mi Mapa");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(com.grupothal.safepath.R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.grupothal.safepath.R.menu.navigator_mapas, menu);
        menu_ = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case com.grupothal.safepath.R.id.action_settings:
                return true;
            case com.grupothal.safepath.R.id.search:
                metodoSearch_rutas();
                return true;
            case com.grupothal.safepath.R.id.actualizar:
                if(isMapaGeneral){funMiMapa();}
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        item_actualizar = menu_.findItem(com.grupothal.safepath.R.id.actualizar);

        if (id == com.grupothal.safepath.R.id.nav_cuenta) {
            isMapaGeneral = false;
            item_actualizar.setVisible(isMapaGeneral);
            funMiCuenta();
        } else if (id == com.grupothal.safepath.R.id.nav_rutas) {
            isMapaGeneral = true;
            item_actualizar.setVisible(isMapaGeneral);
            funMiMapa();
        } else if (id == com.grupothal.safepath.R.id.nav_calificaciones) {
            isMapaGeneral = false;
            item_actualizar.setVisible(isMapaGeneral);
            funMisCalificaciones();
        } else if (id == com.grupothal.safepath.R.id.nav_about) {
            isMapaGeneral = false;
            item_actualizar.setVisible(isMapaGeneral);
            funcAbout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(com.grupothal.safepath.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //My FUNCTIONS....
    public void IniConfiguration(){
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        configuration = new Constantes(w,h, this);
    }
    //Inicializamos los componentes
    public void IniComponents()
    {
        header = navigationView.getHeaderView(0);
        nickname = (TextView) header.findViewById(com.grupothal.safepath.R.id.nicknameView);
        email = (TextView) header.findViewById(com.grupothal.safepath.R.id.text_email);
        imgProfile = (ImageView) header.findViewById(com.grupothal.safepath.R.id.imageView);

        contenedor = (RelativeLayout)findViewById(com.grupothal.safepath.R.id.contend_layout);
        inflater = LayoutInflater.from(this);
    }

    //Seteamos elnickname e imgProfile por uno por defecto, lo que nosotros queramos
    public void myDefaultProlife()
    {
        SharedPreferences miCuenta = getSharedPreferences(Constantes.MY_PREFS_NAME,this.MODE_PRIVATE);
        String name = miCuenta.getString("nombre", "no found");
        String email_ = "Bienvenido a SafePaths!!";//miCuenta.getString("clave","no found");
        nickname.setText(name);
        email.setText(email_);
        Drawable imgProfile_ = new BitmapDrawable(this.getResources(),configuration.escalarImagen("icons/userProfile.png",100,100));
        imgProfile.setBackground(imgProfile_);
    }

    public void setMyProfile()
    {
        try{
            if (Profile.getCurrentProfile() != null) {
                nickname.setText(Profile.getCurrentProfile().getName());
                imgProfile = (ImageView) header.findViewById(com.grupothal.safepath.R.id.imageView);
                Picasso.with(getApplicationContext())
                        .load("https://graph.facebook.com/" + Profile.getCurrentProfile().getId()+ "/picture?type=small")
                        .resize(configuration.getWidth(100),configuration.getWidth(100))
                        .into(imgProfile);
            }else{
                myDefaultProlife();

            }
        }catch (Exception e)
        {
            //error al cargar miprofile
        }
    }

    //Inicializamos mapa como primera vista
    public void funMiMapa()
    {
        try {
            this.setTitle("Mi Mapa");
            //Infamos otra vez el content_navigator_mapas que contiene un fragment
            contenedor.removeAllViews();
            inflater.inflate(com.grupothal.safepath.R.layout.content_navigator_mapas, contenedor, true);
            //Reemplazamos el Fragmente por el de MapaGeneral
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(com.grupothal.safepath.R.id.contend_frame, new MapaGeneral()).commit();
            hide_Btn_AddCalific(false);
        }catch (Exception e){
            //Error al pasar el activity MapaGeneral
        }
    }

    //Se abrira otro activity para añadir zona
    public void addZona()
    {
        try{
            /*//Infamos otra vez el content_navigator_mapas que contiene un fragment
            contenedor.removeAllViews();
            inflater.inflate(com.grupothal.safepath.R.layout.content_navigator_mapas, contenedor, true);
            //Reemplazamos el Fragmente por el de addZona
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(com.grupothal.safepath.R.id.contend_frame,new AddZona()).commit();
            hide_Btn_AddCalific(true);*/
            Intent intent = new Intent(this,AddZona.class);
            startActivity(intent);
            overridePendingTransition(com.grupothal.safepath.R.anim.my_fade_in, com.grupothal.safepath.R.anim.my_fade_out);
        }catch (Exception e){
            //Error al pasar el activity AddZona
            Log.d("AddZona","Error addZona");
        }
    }
    //Ocultamos el boton
    public void hide_Btn_AddCalific(boolean hide)
    {
        FloatingActionButton fab = (FloatingActionButton)findViewById(com.grupothal.safepath.R.id.fab);
        if(hide){
            fab.setVisibility(View.INVISIBLE);
        }else{
            fab.setVisibility(View.VISIBLE);
        }
    }

    //Funcion About Safe Path
    public void funcAbout()
    {
        try{
            this.setTitle("Acerca de Safe Paths");
            contenedor.removeAllViews();
            inflater.inflate(com.grupothal.safepath.R.layout.activity_about, contenedor, true);
            hide_Btn_AddCalific(true);
        }catch (Exception e){
            //Error al pasar al actvity Registrarse
        }
    }

    public void funMisCalificaciones() {
        try {
            this.setTitle("Mis Calificaciones");
            contenedor.removeAllViews();
            View myView_miscalificaciones = inflater.inflate(R.layout.activity_mis_calificaciones, contenedor, true);
            layoutMisCalificaciones = (LinearLayout)myView_miscalificaciones.findViewById(R.id.layoutMisCalificaciones);
            layoutMisCalificaciones.setGravity(Gravity.CENTER_HORIZONTAL);
            layoutMisCalificaciones.setPadding(configuration.getWidth(10),configuration.getHeight(10),configuration.getWidth(10),configuration.getHeight(10));
            load_zones();

            hide_Btn_AddCalific(true);
        }catch (Exception e){
            //Error al pasar al actvity miscalificaciones
        }
    }

    //Funcion Mi cuenta
    public  void funMiCuenta()
    {
        try{
            this.setTitle("Mi Cuenta");
            contenedor.removeAllViews();
            View myView = inflater.inflate(com.grupothal.safepath.R.layout.activity_mi_cuenta, contenedor, true);

            LinearLayout layoutMiCuenta = (LinearLayout)myView.findViewById(R.id.layoutMiCuenta);
            Drawable dr = new BitmapDrawable(configuration.escalarImagen("icons/micuenta.png", configuration.getWidth(768), configuration.getHeight(1024)));
            layoutMiCuenta.setBackground(dr);

            LinearLayout layoutMiCuentaInterna = (LinearLayout)myView.findViewById(R.id.layoutMiCuentaInterna);
            layoutMiCuentaInterna.setPadding(0,configuration.getHeight(180),0,0);

            //Imagen del Perfil de la Cuenta
            ImageView perfilImg = (ImageView)myView.findViewById(com.grupothal.safepath.R.id.imgProfileLarge);
            if (Profile.getCurrentProfile() != null) {
                Picasso.with(getApplicationContext())
                        .load("https://graph.facebook.com/" + Profile.getCurrentProfile().getId()+ "/picture?type=small")
                        .resize(configuration.getWidth(150),configuration.getWidth(150))
                        .into(perfilImg);
            }

            SharedPreferences miCuenta = getSharedPreferences(Constantes.MY_PREFS_NAME,this.MODE_PRIVATE);
            String name = miCuenta.getString("nombre", "Your nickName");
            String email_ = miCuenta.getString("email","Your Email");
            String apellido = miCuenta.getString("apellido","Your lastName");

            EditText yourname = (EditText)myView.findViewById(R.id.nicknameView);
            yourname.setText(name);

            EditText lastname = (EditText)myView.findViewById(R.id.lastNameView);
            lastname.setText(apellido);

            EditText emailview = (EditText)myView.findViewById(R.id.emailView);
            emailview.setText(email_);

            hide_Btn_AddCalific(true);
        }catch (Exception e){
            //Error al pasar al actvity Registrarse
        }
    }

    //
    public void metodoSearch_rutas()
    {
        try{
            Intent intent = new Intent(this,MapaRutas.class);
            startActivity(intent);
            overridePendingTransition(com.grupothal.safepath.R.anim.my_fade_in, com.grupothal.safepath.R.anim.my_fade_out);
            //finish(); //Sirve para cerrar definitivamente el activity actual al pasar a otro
        }catch (Exception e){
            //Error al pasar al actvity Registrarse
        }
    }

    //Cargar Zonas Para Mis Calificaciones ---------------------------------------------------------

    //Cargar las Zonas de la Base de Datos
    public  void load_zones()
    {
        try{
            list_zonas = new ArrayList<Zona>();
            list_zonas.clear();
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(Constantes.URL_BASE + Constantes.LINK_BD_ZONA, null, getzonas());
        }catch (Exception e) {
            //
        }

    }
    //Mostrar mensajes al añadir una zona
    private AsyncHttpResponseHandler getzonas() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(NavigatorMapas.this);
                pDialog.setMessage("Descargando data ...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(NavigatorMapas.this, "Error al descargar Mis Calificaciones!", Toast.LENGTH_LONG).show();
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

                    SharedPreferences miCuenta = getSharedPreferences(Constantes.MY_PREFS_NAME,NavigatorMapas.this.MODE_PRIVATE);
                    String clave = miCuenta.getString("clave", "");
                    String email_ = miCuenta.getString("email","");
                    if(z.getIdFacebook().equals("")){
                        //hay clave en idextra
                        if(z.getIdExtra().equals(email_)){
                            list_zonas.add(z);
                        }
                    }else{//hay idFacebook
                        if(z.getIdFacebook().equals(clave)){
                            list_zonas.add(z);
                        }
                    }


                }
                Toast.makeText(NavigatorMapas.this, "Mis Calificaciones!", Toast.LENGTH_LONG).show();
                int tam = list_zonas.size();
                if(tam==0){
                    TextView mensaje = new TextView(NavigatorMapas.this);
                    mensaje.setText("No tienes Zonas Calificadas!");
                    mensaje.setTextSize(configuration.getWidth(16));
                    layoutMisCalificaciones.addView(mensaje);
                }else {
                    /*TextView mensaje = new TextView(NavigatorMapas.this);
                    mensaje.setText("Si tienes Zonas Calificadas!");
                    mensaje.setTextSize(configuration.getWidth(16));
                    layoutMisCalificaciones.addView(mensaje);*/

                    for(int i=0;i<tam;i++){
                        final LinearLayout linearTmp_general = new LinearLayout(NavigatorMapas.this);
                        linearTmp_general.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        linearTmp_general.setOrientation(LinearLayout.VERTICAL);

                        TextView lat = new TextView(NavigatorMapas.this);
                        lat.setText(String.format("Latitud: %f",list_zonas.get(i).getLat()));
                        lat.setTextSize(configuration.getWidth(16));
                        linearTmp_general.addView(lat);

                        TextView lng = new TextView(NavigatorMapas.this);
                        lng.setText(String.format("Longitud: %f",list_zonas.get(i).getLng()));
                        lng.setTextSize(configuration.getWidth(16));
                        linearTmp_general.addView(lng);

                        TextView descrip = new TextView(NavigatorMapas.this);
                        descrip.setText(String.format("Descripcion: %s",list_zonas.get(i).getDescripcion()));
                        descrip.setTextSize(configuration.getWidth(16));
                        linearTmp_general.addView(descrip);

                        TextView niv = new TextView(NavigatorMapas.this);
                        niv.setText(String.format("Nivel: %d",list_zonas.get(i).getNivel()));
                        niv.setTextSize(configuration.getWidth(16));
                        linearTmp_general.addView(niv);

                        layoutMisCalificaciones.addView(linearTmp_general);

                        //Separador
                        final LinearLayout linearTmp_separador = new LinearLayout(NavigatorMapas.this);
                        linearTmp_separador.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, configuration.getHeight(5)));
                        linearTmp_separador.setOrientation(LinearLayout.VERTICAL);
                        linearTmp_separador.setBackgroundColor(Color.parseColor("#9E9E9E"));

                        layoutMisCalificaciones.addView(linearTmp_separador);
                    }

                }
                //notificZonaP();
            }
        };
    }


}
