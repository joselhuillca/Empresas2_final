package com.grupothal.safepath;

/**
 * Created by JoseLHM on 06/06/2016.
 */
public class Zona {

    //{"_id":"506","idFacebook":152456,"idGooglePlus":"","idExtra":"","descripcion":"","lat":-16.4141,"lng":-71.515,"radio":30,"nivel":3,"__v":0},
    public String _id;
    public String idFacebook;
    public String idGooglePlus;
    public String idExtra;
    public String descripcion;
    public double lat;
    public double lng;
    public int radio;
    public int nivel;
    public int __v;


    public Zona(){}

    public  Zona(double lt,double lg,int r,String info, int niv)
    {
        this.lat  =lt;
        this.lng = lg;
        this.radio =r;
        this.descripcion = info;
        this.nivel = niv;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getIdFacebook() {
        return idFacebook;
    }

    public void setIdFacebook(String idFacebook) {
        this.idFacebook = idFacebook;
    }

    public String getIdGooglePlus() {
        return idGooglePlus;
    }

    public void setIdGooglePlus(String idGooglePlus) {
        this.idGooglePlus = idGooglePlus;
    }

    public String getIdExtra() {
        return idExtra;
    }

    public void setIdExtra(String idExtra) {
        this.idExtra = idExtra;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getRadio() {
        return radio;
    }

    public void setRadio(int radio) {
        this.radio = radio;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }
}
