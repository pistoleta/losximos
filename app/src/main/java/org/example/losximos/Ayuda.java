package org.example.losximos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v7.app.AlertDialog;

/**
 * Created by pistoleta on 25/12/17.
 */

public class Ayuda {

    Context context;
    SharedPreferences preferencias;
    SharedPreferences.Editor editor;
    SoundPool soundPool;
    int soundIdDing;

    public Ayuda(Context context){
        this.context=context;
        this.preferencias = context.getSharedPreferences("ayuda", Context.MODE_PRIVATE);
        this.soundPool = new SoundPool(1,AudioManager.STREAM_MUSIC, 0);
        soundIdDing = soundPool.load(context, R.raw.ding, 0);
    }

   /* public boolean compruebaYlanza(String key){
        return(preferencias.getBoolean(key,false));
    }*/
    public boolean compruebaYlanza(String key){
        if(!preferencias.contains(key)) return false;
        if(!preferencias.getBoolean(key,false)) {
            lanzarDialogo(key);
            return true;
        }
        return false;
    }

    public void set(String key, boolean valor){
        editor = preferencias.edit();
        editor.putBoolean(key, valor);
        editor.apply();
    }

    public void lanzarDialogo(final String codAyuda) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);


        int idResStr = context.getResources().getIdentifier(codAyuda, "string",  context.getPackageName());
        builder.setMessage(context.getResources().getString(idResStr))
                .setTitle("Info")
                .setIcon(context.getResources().getDrawable(R.drawable.info))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        set(codAyuda,true);
                        dialog.cancel();
                    }
                });

        AlertDialog alert= builder.create();
        alert.show();
        soundPool.play(soundIdDing, 1, 1, 3, 0, 1);
    }
}
