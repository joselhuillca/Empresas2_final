package com.grupothal.safepath;

import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

public class MiCuenta extends AppCompatActivity {

    private Constantes configuration;
    private TextView nickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.grupothal.safepath.R.layout.activity_mi_cuenta);
        setTitle("Mi Cuenta");

        IniConfiguration();
        IniComponentes();
    }

    //Inicializar componetes
    public void IniComponentes(){
        //seteamos el imageProfileLarge por default
        ImageView imgProfileLarge = (ImageView)findViewById(com.grupothal.safepath.R.id.imgProfileLarge);
        Drawable imgProf = new BitmapDrawable(this.getResources(),configuration.escalarImagen("icons/userProfile.png",250,250));
        imgProfileLarge.setBackground(imgProf);

        nickname = (TextView)findViewById(com.grupothal.safepath.R.id.nicknameView);

        SharedPreferences miCuenta = getSharedPreferences(Constantes.MY_PREFS_NAME,this.MODE_PRIVATE);
        String name = miCuenta.getString("nombre", "no found");
        nickname.setText(name);
    }

    public void IniConfiguration(){
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        configuration = new Constantes(w,h, this);
    }
}
