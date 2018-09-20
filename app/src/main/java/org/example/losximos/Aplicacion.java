package org.example.losximos;

import android.app.Application;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pistoleta on 4/12/17.
 */

public class Aplicacion extends Application {
   private AlmacenEstadoGson almacenEstado;
   private EstadoJugador estadoJugador;
   private List<Grafico> muebleList;
   private List<Grafico> muebleListCompleta;

   private AlmacenSQLite almacenSQLite;
    private List<Item> armasList;
    private List<Item> ropaList;
    private List<Item> cuerposList;

    // Called when the application is starting, before any other application objects have been created.
    // Overriding this method is totally optional!
    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
        almacenEstado= new AlmacenEstadoGson(this);

        estadoJugador= almacenEstado.obtenerEstadoJugador();

        almacenSQLite = new AlmacenSQLite(this);
        armasList= almacenSQLite.listaArmas();
        Log.d("APLICACION", "listaArmas: "+armasList.size());
        ropaList = almacenSQLite.listaRopa();
        Log.d("APLICACION", "listaRopa: "+ropaList.size());
        muebleListCompleta=almacenSQLite.listaMuebles();//<-- lista muebles catalogo BD
        muebleList = almacenEstado.obtenerListaMuebles();//<-- lista muebles INSERTADOS a la habitacion
        cuerposList = creaItemsCuerpos();

    }

    public List<Item> getArmasList() {
        return armasList;
    }

    public List<Item> getRopaList() {
        return ropaList;
    }

    public List<Item> creaItemsCuerpos(){
        List<Item> listaCuerpos = new ArrayList<Item>();
        for(int i=1; i<9; i++){
            int idResource= getApplicationContext().getResources().getIdentifier("pl"+i+"_frente", "drawable",  getApplicationContext().getPackageName());
            Item cuerpo = new Item(i, "cuerpo "+i, idResource ,Item.CUERPO );
            listaCuerpos.add(cuerpo);
        }
        return listaCuerpos;
    }

    public Grafico getMuebleById(int idMueble){
        for(int i = 0; i < muebleListCompleta.size() ;i++){
            if(muebleListCompleta.get(i).getId()==idMueble) {
                return muebleListCompleta.get(i).clon(this);
            }
        }
        return null;
    }

    public List<Item> getCuerposList() {
        return cuerposList;
    }

    public AlmacenEstadoGson getAlmacenEstado() {
        return almacenEstado;
    }

    public EstadoJugador getEstadoJugador() {
        return estadoJugador;
    }

    public void setEstadoJugador(EstadoJugador estadoJugador) {
        this.estadoJugador = estadoJugador;
    }

    public List<Grafico> getMuebleList() {
        return muebleList;
    }

    public List<Grafico> getMuebleListCompleta() {
        return muebleListCompleta;
    }
    public void setMuebleList(List<Grafico> muebleList) {
        this.muebleList = muebleList;
    }

    // Called by the system when the device configuration changes while your component is running.
    // Overriding this method is totally optional!
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // This is called when the overall system is running low on memory,
    // and would like actively running processes to tighten their belts.
    // Overriding this method is totally optional!
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


}
