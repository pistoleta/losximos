package org.example.losximos;

/**
 * Created by pistoleta on 3/12/17.
 */

public class Item {
    public static final int CAMISA = 1;
    public static final int PANTALON = 2;
    public static final int ARMA = 3 ;
    public static final int CUERPO = 4;

    private final int codigo;
    private final String nombre;
    private  int imgResource;
    private  int imgResource2;
    private  String imgResourceStr;
    private  String imgResource2Str;
    private final int tipo;

    private int nivel ;

    public Item(int codigo, String nombre, int imgResource, int tipo)
    {
        this.codigo=codigo;
        this.tipo= tipo;
        this.nombre = nombre;
        this.imgResource= imgResource;
    }
    //constructor para el arma
    public Item(int codigo, String nombre, int imgResource, int tipo,int nivel)
    {
        this.codigo=codigo;
        this.tipo= tipo;
        this.nombre = nombre;
        this.imgResource= imgResource;
        this.nivel = nivel;
    }
    // NUEVO constructor para el arma
    public Item(int codigo, int tipo, String nombre, String imgResource, String imgResource2,int nivel)
    {
        this.codigo=codigo;
        this.tipo= tipo;
        this.nombre = nombre;
        this.imgResourceStr= imgResource;
        this.imgResource2Str= imgResource2;
        this.imgResource=0; // ESTO LO PONGO PA Q CALLE
        this.imgResource2=0;
        this.nivel = nivel;
    }


    public int getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public int getImgResource() {
        return imgResource;
    }

    public int getTipo() {
        return tipo;
    }


    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public String getImgResourceStr() {
        return imgResourceStr;
    }

    public void setImgResourceStr(String imgResourceStr) {
        this.imgResourceStr = imgResourceStr;
    }

    public String getImgResource2Str() {
        return imgResource2Str;
    }

    public void setImgResource2Str(String imgResource2Str) {
        this.imgResource2Str = imgResource2Str;
    }

    public void setImgResource(int imgResource) {
        this.imgResource = imgResource;
    }

    public int getImgResource2() {
        return imgResource2;
    }

    public void setImgResource2(int imgResource2) {
        this.imgResource2 = imgResource2;
    }
}

