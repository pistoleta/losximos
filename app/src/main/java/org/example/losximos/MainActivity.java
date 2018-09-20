package org.example.losximos;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    MediaPlayer mp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((Button)findViewById(R.id.btnContinuar)).setBackgroundResource(R.drawable.boton_verde_anim);
        ((Button)findViewById(R.id.btnNuevoJuego)).setBackgroundResource(R.drawable.boton_verde_anim);




    }



    @Override protected void onResume(){
        super.onResume();
        Log.d("MAIN","onResume()");
        iniciarMusica();

        SharedPreferences preferencias = this.getSharedPreferences("ayuda", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        if(!preferencias.contains("firstRun")) {
            crearPreferenciasAyuda();
        }


        if(preferencias.contains("ayuda_bienvenida") && preferencias.getBoolean("ayuda_bienvenida",false)){
            ((Button)findViewById(R.id.btnContinuar)).setVisibility(View.VISIBLE);
        }

    }
    public void lanzaJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        startActivity(i);
    }

    @Override protected void onPause(){
        Log.d("MAIN","onPause()");
        mp.pause();
        super.onPause();

    }



    public void nuevoJuegoClick(View view) {
        SharedPreferences preferencias = this.getSharedPreferences("ayuda", Context.MODE_PRIVATE);
        if(preferencias.contains("ayuda_bienvenida") && preferencias.getBoolean("ayuda_bienvenida",false)){
            lanzarDialogoConfirmarNuevoJuego();
        }
        else
            lanzaJuego(null);

    }
    private void lanzarDialogoConfirmarNuevoJuego(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                builder.setTitle("¿Comenzar partida nueva?")
                .setMessage("Todos los datos de la partida actual se borrarán")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ((Aplicacion)getApplication()).getAlmacenEstado().eliminarTodo();
                        SharedPreferences preferencias = getSharedPreferences("ayuda", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencias.edit();
                        editor.clear().commit();
                        editor.apply();
                        crearPreferenciasAyuda();
                        lanzaJuego(null);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog dialogo = builder.create();
        dialogo.show();

    }

    private void crearPreferenciasAyuda(){
        SharedPreferences preferencias = this.getSharedPreferences("ayuda", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putBoolean("ayuda_bienvenida",false);
        editor.putBoolean("ayuda_primerMueble", false); // construye primer mueble (tipos de muebles)
        editor.putBoolean("ayuda_mueble1",false);   //coloca primer mueble (armario)
        editor.putBoolean("ayuda_mueble_id1", false );  //interactua con mueble (armario)
        editor.putBoolean("ayuda_mueble_id2", false);
        editor.putBoolean("ayuda_mueble_id3", false); //ayuda tareas
        editor.putBoolean("ayuda_mueble_id4",false);
        editor.putBoolean("ayuda_tareas",false);
        editor.putBoolean("ayuda_tareas2",false);
        editor.putBoolean("ayuda_explorar",false);
        editor.putBoolean("firstRun", false).commit();
        editor.apply();
    }

    private void iniciarMusica() {

        mp=MediaPlayer.create(this, R.raw.mainwav);
        mp.setLooping(true);
        mp.start();


    }
    public void lanzaAjustes(View view) {
    }
}
