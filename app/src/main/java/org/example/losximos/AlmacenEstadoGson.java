package org.example.losximos;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pistoleta on 4/12/17.
 */

public class AlmacenEstadoGson implements AlmacenInterfaz {

    private String string;
    private Gson gson = new Gson();
    private Type type = new TypeToken<EstadoJugador>() {}.getType();
    private Type typeMueble = new TypeToken<List<Mueble>>(){}.getType();
    private Context context;

    public AlmacenEstadoGson(Context context)
    {
        this.context = context;

    }
    @Override
    public void guardarEstadoJugador(EstadoJugador estadoJugador) {

        string = gson.toJson(estadoJugador,type);
        guardarString(string);
    }

    public void resetEstadoJugador(){
      EstadoJugador  estadoJugador = new EstadoJugador();
      guardarEstadoJugador(estadoJugador);
    }

    @Override
    public EstadoJugador obtenerEstadoJugador() {
        string = leerString();
        EstadoJugador estadoJugador;
        if(string == null || string.isEmpty())
        {
            estadoJugador = new EstadoJugador();
        }
        else
        {
            estadoJugador = gson.fromJson(string,type);
        }

        return estadoJugador;
    }

    @Override
    public void guardarMuebles(List<Grafico> lgraficos) {
        Log.d("ALMACEN", "guardarMuebles()");
        ArrayList<Mueble> lmuebles;
        lmuebles = new ArrayList<>();
        for(Grafico grafico : lgraficos){    /*Convierto de grafico a mueble para guardar en json*/
           // Mueble m = new Mueble(grafico.getIdRecursoDrawable(),grafico.getIdRecursoDrawAnimacion(),grafico.getCenX(),grafico.getCenY(),grafico.getNombre(),grafico.isPisable());
            Mueble m = new Mueble(grafico);
            lmuebles.add(m);

        }
        try {
            string = gson.toJson(lmuebles, typeMueble);
        }catch (Exception ex )
        {
            ex.printStackTrace();
        }
        guardarStringMuebles(string);
        Log.d("ALMACEN", "guardaJson:"+string);
    }

    @Override
    public List<Grafico> obtenerListaMuebles() {
        Log.d("ALMACEN", "listaMuebles()");
        string = leerStringMuebles();
        ArrayList<Grafico> graficos= new ArrayList<Grafico>();
        ArrayList<Mueble> muebles;
        if(string == null || string.isEmpty()){ // si la lista esta vacia

            //no hago nada

        }else{
            muebles = gson.fromJson(string,typeMueble);
            for(Mueble mueble : muebles)                /* muebleJson a gráfico*/
            {
                int idMueble = mueble.getId();
                int posX=mueble.getPosX();
                int posY=mueble.getPosY();

                Grafico g = ((Aplicacion)context).getMuebleById(idMueble); //a partir del idMueble de la bd obtengo todas sus caracteristicas necesarias
                g.setCenX(posX);
                g.setCenY(posY);
                graficos.add(g);
            }

        }

        return graficos;
    }

    @Override
    public void eliminarTodo()
    {
        context.deleteFile("estadojugador");
        context.deleteFile("estadomuebles");
    }
    private void guardarString(String string) {
        try{
            FileOutputStream f = context.openFileOutput("estadojugador", Context.MODE_PRIVATE);
            String texto = string;
            f.write(texto.getBytes());
            f.close();
        }catch(Exception e){
            Log.e("PROYECTOMIO", e.getMessage(),e);
        }
    }

    private String leerString() {
        String result ="";
        try{
            if (!fileExists(context,"estadojugador"))
                guardarString(""); //si no existe lo crea nuevo

            FileInputStream f = context.openFileInput("estadojugador");
            BufferedReader entrada = new BufferedReader(new InputStreamReader(f));

            int n = 0;
            String linea;
            do{
                linea = entrada.readLine();
                if(linea != null){
                    result += linea;
                }
            }while (linea != null);

            f.close();
        }catch(Exception e){
            Log.e("PROYECTOMIO", e.getMessage(),e);
        }
        return result;

    }

    private String leerStringMuebles() {
        String result ="";
        try{
            if (!fileExists(context,"estadomuebles"))
                guardarStringMuebles(""); //si no existe lo crea nuevo

            FileInputStream f = context.openFileInput("estadomuebles");
            BufferedReader entrada = new BufferedReader(new InputStreamReader(f));

            int n = 0;
            String linea;
            do{
                linea = entrada.readLine();
                if(linea != null){
                    result += linea;
                }
            }while (linea != null);

            f.close();
        }catch(Exception e){
            Log.e("leyendoMuebles", e.getMessage(),e);
        }
        return result;

    }
    private void guardarStringMuebles(String string) {
        try{
            FileOutputStream f = context.openFileOutput("estadomuebles", Context.MODE_PRIVATE);
            String texto = string;
            f.write(texto.getBytes());
            f.close();
        }catch(Exception e){
            Log.e("Guardando muebles", e.getMessage(),e);
        }
    }

    public boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }


}
