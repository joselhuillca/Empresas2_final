package com.grupothal.safepath;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements Validator.ValidationListener {

    @Email(message = "Email incorrecto")
    private EditText edt_email;
    @NotEmpty(message = "Escriba su Contraseña" )
    private EditText edt_passw;
    private Button btn_singIn;
    private InternetClass internetClass;
    private boolean isConnect; //Para saber si esta conectado a internet

    private LoginButton loginButton;
    private CallbackManager callbackManager;

    private Validator validator;

    private Constantes configuration;

    public List<Usuario> list_usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IniFacebookLogin();
        setContentView(com.grupothal.safepath.R.layout.activity_main);

        verificarSiYaIniciasteSesion();

        IniConfiguration();
        IniComponents();
        //Aneste de VerConnectionInternet se necesita IniComponents
        VerConnectionInternet();
        IniButomFacebook();
        DarEstiloCompo();
    }

    public void IniComponents() {
        //REMOVE TITLE AND FULLSCREEN enable
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //InternetClass Inicializando...
        internetClass = new InternetClass(this);
        //Inicializamos los datos del Layout
        edt_email = (EditText)findViewById(com.grupothal.safepath.R.id.edit_email);
        edt_passw = (EditText)findViewById(com.grupothal.safepath.R.id.edit_pass);
        btn_singIn = (Button)findViewById(com.grupothal.safepath.R.id.btn_iniSesion);

        list_usuarios = new ArrayList<Usuario>();

        validator = new Validator(this);
        validator.setValidationListener(this);

    }

    //Daremos estilo a los componentes del xml
    public void DarEstiloCompo()
    {
        //Colocamos una imagen de logo
        final ImageView imgLogo= (ImageView)findViewById(com.grupothal.safepath.R.id.logo_);
        imgLogo.setImageBitmap(configuration.escalarImagen("icons/logo.png", configuration.getWidth(300), configuration.getHeight(82)));

        //Tipo de fuente usado
        final Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueLight.ttf");
        final Typeface tf_bold = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeue.ttf");

        //Dar padding al mainlayout
        final LinearLayout mainLayout = (LinearLayout)findViewById(com.grupothal.safepath.R.id.mainLayout);
        //mainLayout.setPadding(configuration.getWidth(30), configuration.getHeight(135), configuration.getWidth(30), configuration.getHeight(30));
        mainLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        //mainLayout.setBackgroundColor(Color.parseColor("#404040"));
        //Drawable dr = new BitmapDrawable(configuration.escalarImagen("icons/fondoi.jpg", configuration.getWidth(768), configuration.getHeight(1024)));
        //mainLayout.setBackground(dr);
        //Dar padding al linear_feneral
        final LinearLayout linearLayout = (LinearLayout)findViewById(com.grupothal.safepath.R.id.linear_general);
        linearLayout.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        //linearLayout.setPadding(configuration.getWidth(50),configuration.getHeight(100),configuration.getWidth(50),configuration.getHeight(50));

        //Dar estilo al texto Iniciar Sesion
        final TextView textIniSesion = (TextView)findViewById(com.grupothal.safepath.R.id.text_inisesion);
        textIniSesion.setTextColor(Color.BLACK);
        textIniSesion.setGravity(Gravity.CENTER_HORIZONTAL);
        textIniSesion.setTypeface(tf_bold);
        textIniSesion.setPadding(0, 0, 0, configuration.getHeight(35));
        textIniSesion.setTextSize(configuration.getWidth(20));
        textIniSesion.setVisibility(View.INVISIBLE);//Ocultaremos este Texto, no es necesario mostrarlo
        //Dar estilo al email
        edt_email.setSingleLine(true);
        edt_email.setTypeface(tf);
        //edt_email.setText("test@gmail.com");
        edt_email.setTextSize(configuration.getWidth(20));
        edt_email.setTextColor(Color.BLACK);
        edt_email.setWidth(configuration.getWidth(400));
        edt_email.setHintTextColor(Color.parseColor("#BDBDBD"));
        //Dar estilo al password
        edt_passw.setSingleLine(true);
        edt_passw.setTypeface(tf);
        edt_passw.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //edt_passw.setText("test");
        edt_passw.setTextSize(configuration.getWidth(20));
        edt_passw.setTextColor(Color.BLACK);
        edt_passw.setWidth(configuration.getWidth(400));
        edt_passw.setHintTextColor(Color.parseColor("#BDBDBD"));
        //Dar estilo a"Tambien puedes iniciarcsesión usando:"
        final TextView text_infosesion = (TextView)findViewById(com.grupothal.safepath.R.id.text_tamb_inisesion);
        text_infosesion.setTextColor(Color.parseColor("#616161"));
        text_infosesion.setTypeface(tf);
        text_infosesion.setTextSize(configuration.getWidth(18));
        text_infosesion.setPadding(0, configuration.getHeight(35), 0, configuration.getHeight(35));
        text_infosesion.setGravity(Gravity.CENTER_HORIZONTAL);
        //Dar estilo a "Si no estas registrado,"
        final TextView text_registro1 = (TextView)findViewById(com.grupothal.safepath.R.id.text_sinoReg);
        text_registro1.setTextColor(Color.parseColor("#616161"));
        text_registro1.setTypeface(tf);
        text_registro1.setTextSize(configuration.getWidth(18));
        text_registro1.setPadding(0,configuration.getHeight(25),0,configuration.getHeight(25));
        text_registro1.setGravity(Gravity.RIGHT);
        //Dar estilo a "registrate aqui!"
        final TextView text_registro2 = (TextView)findViewById(com.grupothal.safepath.R.id.text_registr);
        text_registro2.setTextColor(Color.parseColor("#F44336"));
        text_registro2.setTypeface(tf);
        text_registro2.setTextSize(configuration.getWidth(18));
        text_registro2.setGravity(Gravity.LEFT);
        text_registro2.setPadding(0, configuration.getHeight(25), 0, configuration.getHeight(25));
        text_registro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Registrate gratis!", Toast.LENGTH_LONG).show();
                funRegister();
            }
        });

        //Dar estilo a los BOTONES
        btn_singIn.setTextColor(Color.WHITE);
        btn_singIn.setTextSize(configuration.getWidth(20));
        btn_singIn.setBackgroundColor(Color.parseColor("#E53935"));
        btn_singIn.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        btn_singIn.setPadding(configuration.getWidth(65),0, configuration.getWidth(65), 0);
        btn_singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funSingIn();
            }
        });
    }

    public void IniConfiguration(){
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        configuration = new Constantes(w,h, this);
    }

    //Sseteamos el parametro isconnect para verificar la conexion a internet
    public void VerConnectionInternet()
    {
        isConnect = internetClass.estaConectado();
    }

    //Funcion de Iniciar Sesion mediante la cuenta de Safe Path
    public  void funSingIn()
    {
        validator.validate();
    }

    //Ir a MapaGeneral una vez iniciado sesion
    public  void Ir_a_mapaGeneral()
    {
        //Te manda al NavigatorMapas
        Intent intent = new Intent(this,NavigatorMapas.class);
        startActivity(intent);
        overridePendingTransition(com.grupothal.safepath.R.anim.my_fade_in, com.grupothal.safepath.R.anim.my_fade_out);
        finish();

    }

    //Inicializamos el SDK de Facebook
    public void IniFacebookLogin(){
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();
    }

    //Iniciar Sesion con Facebok
    public void funSingIngFacebook(String name, String Clave)
    {
        edt_passw.setText(Constantes.ID_FB+Clave);
        if(verificarUsuario()){
            Ir_a_mapaGeneral();
        }else{
            //Registrarse
        }
    }

    //Damos funcionamiento al boton de facebook al hacerle click
    public void IniButomFacebook()
    {
        loginButton = (LoginButton) findViewById(com.grupothal.safepath.R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult loginResult) {
                Log.e("FACEBOOK", "DSDSDSD");
                ProfileTracker profileTracker = new ProfileTracker() {//Actualiza el Profile
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        this.stopTracking();
                        Profile.setCurrentProfile(currentProfile);

                        SharedPreferences sharedPreferences = getSharedPreferences(Constantes.MY_PREFS_NAME, MainActivity.this.MODE_PRIVATE);
                        SharedPreferences.Editor miCuenta = sharedPreferences.edit();
                        miCuenta.putString("email", "facebook");
                        miCuenta.putString("nombre", currentProfile.getFirstName());
                        miCuenta.putString("apellido", currentProfile.getLastName());
                        miCuenta.putString("clave", Constantes.ID_FB+currentProfile.getId());
                        miCuenta.commit();
                        Ir_a_mapaGeneral();
                    }
                };
                profileTracker.startTracking();
                // Log.d("Facebook", "Exitoooo!!.");
                //Ir_a_mapaGeneral();
            }

            @Override
            public void onCancel() {
                //info.setText("Login attempt canceled.");
                Log.d("Facebook", "Login attempt canceled.");
            }

            @Override
            public void onError(FacebookException e) {
                VerConnectionInternet();
            }
        });
    }

    //Verificamos si el usuario ya habia iniciado sesion anteriormente, si ese fuera el caso lo redireccionamos al activity MavigatorMapas
    public void verificarSiYaIniciasteSesion()
    {
        //Shared Preferences
        SharedPreferences miCuentaCrear = getSharedPreferences(Constantes.MY_PREFS_NAME,MainActivity.this.MODE_PRIVATE);
        String restoredText = miCuentaCrear.getString("nombre", null);
        //Si se inicio session abre el nuevo activity
        if (Profile.getCurrentProfile() != null || restoredText!=null) {

            Ir_a_mapaGeneral();
        }
    }

    //Pasamos a otro activity - A Registrarse
    public void funRegister(){
        try{
            Intent intent = new Intent(this,Registrarse.class);
            startActivity(intent);
            overridePendingTransition(com.grupothal.safepath.R.anim.my_fade_in, com.grupothal.safepath.R.anim.my_fade_out);
            //finish(); //Sirve para cerrar definitivamente el activity actual al pasar a otro
        }catch (Exception e){
            //Error al pasar al actvity Registrarse
        }
    }

    //***************VERIFICAr SI EL USUARIO EXISTE***********************
    //Cargar las Zonas de la Base de Datos
    public  void load_usuarios()
    {
        try{
            list_usuarios.clear();
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(Constantes.URL_BASE + Constantes.LINK_BD_REGISTRO+edt_email.getText(), null, getUsers());
        }catch (Exception e) {
            Toast.makeText(MainActivity.this, "Error ...", Toast.LENGTH_LONG).show();
        }
    }

    //Mostrar mensajes al añadir una zona
    private AsyncHttpResponseHandler getUsers() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Autenticando ...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error de Autenticación!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                String resultadoJson = new String(response);
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(resultadoJson);
                JsonArray arrayUsers = tradeElement.getAsJsonArray();
                int numUser = arrayUsers.size();
                for(int i=0;i<numUser;i++){
                    JsonElement obj = arrayUsers.get(i);
                    JsonObject json = obj.getAsJsonObject();
                    //JsonElement ele = json.get("_id");
                    Usuario z = new Usuario();
                    z.setEmail(json.get("email").getAsString());
                    z.setName_(json.get("nombre").getAsString());
                    z.setLastname(json.get("apellido").getAsString());
                    z.setPass(json.get("clave").getAsString());
                    list_usuarios.add(z);
                    //Toast.makeText(MainActivity.this,json.get("nombre").getAsString() , Toast.LENGTH_LONG).show();

                }
                if(verificarUsuario()){
                    SharedPreferences sharedPreferences = getSharedPreferences(Constantes.MY_PREFS_NAME,MainActivity.this.MODE_PRIVATE);
                    SharedPreferences.Editor miCuenta = sharedPreferences.edit();
                    miCuenta.putString("email",edt_email.getText().toString());
                    miCuenta.putString("nombre",list_usuarios.get(0).getName_());
                    miCuenta.putString("apellido",list_usuarios.get(0).getLastname());
                    miCuenta.putString("clave",list_usuarios.get(0).getPass());
                    miCuenta.commit();
                    Ir_a_mapaGeneral();
                }else {
                    Toast.makeText(MainActivity.this,"Registrese por favor!!", Toast.LENGTH_LONG).show();
                }


            }
        };
    }

    public boolean verificarUsuario(){
        int tam= list_usuarios.size();
        String pass = Constantes.ID_SP+edt_passw.getText().toString();
        for(int i=0;i<tam;i++){
            //Toast.makeText(MainActivity.this, list_usuarios.get(i).getPass(), Toast.LENGTH_LONG).show();
            if(list_usuarios.get(i).getEmail().equals(edt_email.getText().toString()) && list_usuarios.get(i).getPass().equals(pass) ){
                return  true;
            }
        }
        return false;
    }

    //TRABAJAMOS con la VALIDACION
    @Override
    public void onValidationSucceeded() {
        VerConnectionInternet();
        if(isConnect)
        {
            load_usuarios();
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors)
        {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            }
            else
            {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }
}
