package com.grupothal.safepath;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by JoseLHM on 05/06/2016.
 */
public class InternetClass {

    public Context context;

    public  InternetClass(Context ctx)
    {
        this.context = ctx;
    }
    //No es necesario dar una alerta de estas conectado a internet,XD
    protected Boolean estaConectado(){
        if(conectadoWifi()){
            //showAlertDialog(context, Constantes.titleMessageInternet, Constantes.mesageSuccessInternet, true);
            return true;
        }else{
            if(conectadoRedMovil()){
                //showAlertDialog(context, Constantes.titleMessageInternet,Constantes.mesageSuccessInternet, true);
                return true;
            }else{
                showAlertDialog(context, Constantes.titleMessageInternet,Constantes.mesageFailInternet, false);
                return false;
            }
        }
    }

    protected Boolean conectadoRedMovil(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    //Ver si el dispositivo esta conectado a wi-fi
    public boolean conectadoWifi(){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
    //Mostrar Alerta de si esta conectado A Wi-Fi
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setIcon((status) ? com.grupothal.safepath.R.drawable.success : com.grupothal.safepath.R.drawable.fail);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
