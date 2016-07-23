package com.grupothal.safepath;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Checked;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class Registrarse extends AppCompatActivity implements Validator.ValidationListener {

    private Constantes configutation;
    private LinearLayout mainLayout;

    public List<Usuario> list_usuarios;
    private boolean emailExist = false;

    @NotEmpty(message = "Escriba su nombre" )
    public EditText edt_name;
    @NotEmpty(message = "Escriba su nombre" )
    public EditText edt_lastname;
    @Email(message = "Email incorrecto")
    public EditText edt_email;
    @Password(min = 3, message = "Password muy debil")
    public EditText edt_passw;
    @Checked(message = "Debes confirmar")
    public CheckBox aceptarCondicionesCheckBox;
    public Button btn_registrarse;
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Registrate");

        setContentView(com.grupothal.safepath.R.layout.activity_registrarse);
        IniConfig();
        IniMainLayout();
        IniComponents();
    }

    public void IniConfig(){
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        configutation = new Constantes(w,h, this);
    }

    //Inicializamos el mainLayout
    public void IniMainLayout(){
        mainLayout = (LinearLayout)findViewById(com.grupothal.safepath.R.id.layoutRegister);
        mainLayout.setPadding(configutation.getWidth(50), configutation.getHeight(35), configutation.getWidth(50), configutation.getHeight(35));
        mainLayout.setGravity(Gravity.CENTER_VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        //Drawable dr = new BitmapDrawable(configutation.escalarImagen("icons/fondoh.jpg", configutation.getWidth(768), configutation.getHeight(1024)));
        //mainLayout.setBackground(dr);
    }

    //Inicializamos los componetes
    public void IniComponents()
    {
        //Tipo de fuente a usar
        final Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/HelveticaNeue.ttf");

        edt_name = new EditText(this);
        edt_lastname = new EditText(this);
        edt_email = new EditText(this);
        edt_passw = new EditText(this);
        aceptarCondicionesCheckBox = new CheckBox(this);
        btn_registrarse = new Button(this);

        list_usuarios = new ArrayList<Usuario>();

        validator = new Validator(this);
        validator.setValidationListener(this);
        //Lo colocamos en un List
        List<EditText> listEdt= new ArrayList<EditText>();
        listEdt.add(edt_name);listEdt.add(edt_lastname);listEdt.add(edt_email);listEdt.add(edt_passw);

        //Ejemplo de la lista de strings:  {"Nombres","Apellidos","Correo Electrónico","Contraseña"};
        //Añadimos los componentes al mainLayout
        for(int i=0;i<Constantes.listStringTitleRegister.length;i++){
            /*TextView tmp = new TextView(this);
            tmp.setText(Constantes.listStringTitleRegister[i]);
            tmp.setTextColor(Color.BLACK);
            tmp.setTextSize(configutation.getHeight(18));*/
            final LinearLayout linearTmp = new LinearLayout(this);
            linearTmp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearTmp.setOrientation(LinearLayout.HORIZONTAL);
            linearTmp.setPadding(0, configutation.getHeight(12), 0, configutation.getHeight(12));

            final ImageView icono = new ImageView(this);
            icono.setImageBitmap(configutation.escalarImagen(Constantes.listIconsRegister[i], configutation.getWidth(40), configutation.getHeight(Constantes.listWidthIconsRegister[i])));


            listEdt.get(i).setHint(Constantes.listStringHitRegister[i]);
            listEdt.get(i).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            listEdt.get(i).setPadding(configutation.getWidth(5), 0, 0, configutation.getHeight(8));
            listEdt.get(i).setGravity(Gravity.CENTER_VERTICAL);
            listEdt.get(i).setSingleLine(true);
            listEdt.get(i).setTypeface(tf);

            linearTmp.addView(icono);
            linearTmp.addView(listEdt.get(i));

            //mainLayout.addView(tmp);
            //mainLayout.addView(listEdt.get(i));
            mainLayout.addView(linearTmp);
        }


        edt_passw.setTransformationMethod(PasswordTransformationMethod.getInstance());
        //Añadimos el check para confirmar condiones
        aceptarCondicionesCheckBox.setText("Aceptar Condiciones");
        mainLayout.addView(aceptarCondicionesCheckBox);

        //Colocamos el boton Registraese
        LinearLayout contentButton = new LinearLayout(this);
        contentButton.setPadding(0,configutation.getHeight(65),0,0);
        contentButton.setOrientation(LinearLayout.HORIZONTAL);
        contentButton.setGravity(Gravity.CENTER_HORIZONTAL);
        btn_registrarse.setTextColor(Color.WHITE);
        btn_registrarse.setText("   CREAR CUENTA    ");
        btn_registrarse.setTextSize(configutation.getHeight(20));
        btn_registrarse.setBackgroundColor(Color.parseColor("#E53935"));
        btn_registrarse.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        btn_registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funRegistrarse();
            }
        });

        contentButton.addView(btn_registrarse);

        mainLayout.addView(contentButton);
    }


    //Mostrar mensajes al añadir una zona
    private AsyncHttpResponseHandler registro() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(Registrarse.this);
                pDialog.setMessage("Registrandose ...");
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(Registrarse.this, "Error al Registrarse!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(Registrarse.this, "Se registro correctamente!", Toast.LENGTH_LONG).show();
                //Una ves registrado nos vamos alactivity principal
                Intent intent = new Intent(Registrarse.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(com.grupothal.safepath.R.anim.my_fade_in, com.grupothal.safepath.R.anim.my_fade_out);
                finish();
            }
        };
    }

    //Funcion Registrarse
    public void funRegistrarse(){
        validator.validate();
    }

    //TRABAJANDO CON VALIDATOR
    @Override
    public void onValidationSucceeded() {
        load_usuarios();
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

    //VERIFICAR USUARIO
    //Cargar las Zonas de la Base de Datos
    public  void load_usuarios()
    {
        try{
            list_usuarios.clear();
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(Constantes.URL_BASE + Constantes.LINK_BD_REGISTRO+edt_email.getText(), null, getUsers());
        }catch (Exception e) {
            //Toast.makeText(this, "Error ...", Toast.LENGTH_LONG).show();
        }
    }

    public boolean verificarUsuario(){
        int tam= list_usuarios.size();
        for(int i=0;i<tam;i++){
            //Toast.makeText(MainActivity.this, list_usuarios.get(i).getPass(), Toast.LENGTH_LONG).show();
            if(list_usuarios.get(i).getEmail().equals(edt_email.getText().toString()) ){
                return  true;
            }
        }
        return false;
    }

    //Mostrar mensajes al añadir una zona
    private AsyncHttpResponseHandler getUsers() {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //verificando usuario
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                Toast.makeText(Registrarse.this, "Error al Registrar!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
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
                    //z.setPass(json.get("clave").getAsString());
                    list_usuarios.add(z);
                    //Toast.makeText(MainActivity.this,json.get("nombre").getAsString() , Toast.LENGTH_LONG).show();

                }
                emailExist = verificarUsuario();
                if(emailExist){
                    Toast.makeText(Registrarse.this,"Este correo ya existe!", Toast.LENGTH_LONG).show();
                }else{//Registrar usuario
                    Toast.makeText(Registrarse.this, "Datos ingresados correctamente", Toast.LENGTH_SHORT).show();
                    RequestParams params = new RequestParams();
                    try {
                        params.put("nombre", edt_name.getText());
                        params.put("apellido", edt_lastname.getText());
                        params.put("email", edt_email.getText());
                        params.put("clave", Constantes.ID_SP + edt_passw.getText());

                    } catch (Exception e) {
                        //Error al colocar los parametros
                    }
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(Constantes.URL_BASE + Constantes.LINK_BD_USUARIO, params, registro());
                }
            }
        };
    }
}
