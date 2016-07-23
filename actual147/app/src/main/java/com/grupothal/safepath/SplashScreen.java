package com.grupothal.safepath;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by JoseLHM on 03/06/2016.
 */
public class SplashScreen extends AppCompatActivity {

    private Constantes configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.grupothal.safepath.R.layout.splash);

        //printHashKey();
        IniConfiguration();
        IniComponents();
        IniAnimations();

    }

    public void IniConfiguration(){
        DisplayMetrics displayMetrics = getBaseContext().getResources().getDisplayMetrics();
        int w = displayMetrics.widthPixels;
        int h = displayMetrics.heightPixels;
        configuration = new Constantes(w,h, this);
    }

    public void IniComponents()
    {
        //REMOVE TITLE AND FULLSCREEN enable
        this.getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Seteamos el fondo del realive Layout
        final RelativeLayout layoutSplash = (RelativeLayout)findViewById(com.grupothal.safepath.R.id.layoutSplash);
        Drawable fondo = new BitmapDrawable(this.getResources(),configuration.escalarImagen("Splash/fondo_splash.jpg",768,1024));
        layoutSplash.setBackground(fondo);


    }

    public void IniAnimations()
    {
        //Inicializamos la imagen loading.png -- la imagen esta en drawable
        final ImageView imgSplash = (ImageView)findViewById(com.grupothal.safepath.R.id.imageSplash);

        //Inicializamos los animations
        final Animation animLoad = AnimationUtils.loadAnimation(getBaseContext(), com.grupothal.safepath.R.anim.rotate);
        final Animation animFade = AnimationUtils.loadAnimation(getBaseContext(), com.grupothal.safepath.R.anim.my_fade_out);
        //animamos la imagen loading.png
        imgSplash.startAnimation(animLoad);
        //Cuando termine la animacion nos vamos al MainActivity.class
        animLoad.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Hacemos que la imagen loading.png se desvanesca
                imgSplash.startAnimation(animFade);
                finish();
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                //Hacemos que el Activity se desvanesza
                overridePendingTransition(com.grupothal.safepath.R.anim.my_fade_in, com.grupothal.safepath.R.anim.my_fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    //Obtener el HashKey para poder usar el API de Facebook
    public void printHashKey()
    {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.joselhm.safepath_droid",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
