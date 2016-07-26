package com.grupothal.safepath;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Created by JoseLHM on 04/06/2016.
 */
public class Constantes {
    public  static Marker miPosicion;
    private int DIMENSION_DESING_WIDTH= 640;
    private int DIMENSION_DESING_HEIGHT= 1136;
    private int WIDTH_PIXEL;
    private int HEIGHT_PIXEL;
    Context context;

    public Constantes(int w, int h, Context context1){
        this.WIDTH_PIXEL = w;
        this.HEIGHT_PIXEL = h;
        this.context = context1;
    }

    //****************************** Static Constantes *************************************
    //GOOGLE ANALITYCS
    // The following line should be changed to include the correct property id.
    public static String PROPERTY_ID = "UA-79249503-1";

    //InternetClass.java
    public static String  titleMessageInternet = "Conexion a Internet";
    public static String mesageSuccessInternet = "Tu Dispositivo tiene Conexion a Wifi.";
    public static String mesageFailInternet = "Tu Dispositivo no tiene Conexion a Internet.";
    //Register.java
    public static String listStringTitleRegister[] = {"NOMBRES","APELLIDOS","CORREO ELECTRÓNICO","CONTRASEÑA"};
    public static String listStringHitRegister[] = {"Nombres","Apellidos","Correo Electrónico","Contraseña"};
    public static String listIconsRegister[] = {"icons/avatar.png","icons/none.png","icons/envelope.png","icons/padlock.png"};
    public static int listWidthIconsRegister[] = {47,40,30,53};
    //AddZona.java
    public static List<String> list_colores = Arrays.asList("#FFE57F","#FFE57F", "#FFD180","#FFD180", "#EF9A9A");
    public static List<String> list_colores_center = Arrays.asList("#FFEC00","#FFEC00", "#FF920C","#FF920C", "#FF1D19");
    //Base de Datos URL
    public static String URL_BASE = "https://safepath-empresagaj.c9users.io";
    public static String LINK_BD_ZONA = "/api/zona";
    public static String LINK_BD_USUARIO = "/api/usuario";
    public static String LINK_BD_REGISTRO =  "/api/registro/";
    //GPSclass
    public static String GPS_DISABLED = "Tu GPS esta desactivado. ¿Te gustaria habilitarlo?";
    public static String GPS_SETTINGS = "Activar GPS";
    //IDs Claves
    public static String ID_SP = "sp";//safePath
    public static String ID_FB = "fb";//Facebook
    public static String ID_GO = "go";//Google
    //SharedPreferences
    public static String MY_PREFS_NAME = "miCuenta";
    //DISTANCIA RADIO TIERRA
    public static double RADIO_TIERRA = 6372.795477;
    //------------------------------- FUNCIONES --------------------------------------------
    public int getHeight(int value){
        return HEIGHT_PIXEL*value/DIMENSION_DESING_HEIGHT;
    }

    public int getWidth(int value){
        return WIDTH_PIXEL*value/DIMENSION_DESING_WIDTH;
    }

    public void setWidthPixel(int value){
        WIDTH_PIXEL=value;
    }

    public void setHeigthPixel(int value){
        HEIGHT_PIXEL=value;
    }

    public Bitmap escalarImagen(String path, int w, int h){
        AssetManager assetManager = context.getAssets();
        Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        try {
            InputStream is = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(is);
            bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bitmap;
    }
}
