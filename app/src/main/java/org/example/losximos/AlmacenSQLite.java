package org.example.losximos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pistoleta on 22/12/17.
 */

public class AlmacenSQLite extends SQLiteOpenHelper {
    private Context context;
    public AlmacenSQLite(Context context){
        super(context,"items",null,1);
        this.context=context;
    }

    public void eliminar(SQLiteDatabase db){
        db.execSQL("DROP DATABASE items");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            //Cargo script crear tabla mueble
            InputStream inputStream=   context.getAssets().open("mueble.sql");
            String sql = convertStreamToString(inputStream);
            inputStream.close();
            db.execSQL(sql);
            //cagro scripts inserts en mueble
            inputStream=   context.getAssets().open("datos_mueble.sql");
            sql = convertStreamToString(inputStream);
            inputStream.close();
            db.execSQL(sql);

            db.execSQL("CREATE TABLE IF NOT EXISTS 'item' (" +
                    "id INTEGER NOT NULL," +
                    "tipo INTEGER, " +
                    "nombre TEXT, " +
                    "resource1 TEXT," +
                    "resource2 TEXT," +
                    "nivel INTEGER," +
                    "PRIMARY KEY('id')" +
                    ");");

           /* db.execSQL("INSERT INTO 'item' VALUES (1,3,'cuchillo','itm_cuchillo_cocina',NULL,1)"
                    +",(2,3,'palo','itm_palo_madera',NULL,2)"
                    +",(3,3,'espada','itm_espada',NULL,3);");*/

            //cagro scripts inserts en item (items y ropa)
            inputStream=   context.getAssets().open("datos_item.sql");
            sql = convertStreamToString(inputStream);
            inputStream.close();
            db.execSQL(sql);


        }catch(Exception ex){
            ex.printStackTrace();
            Log.d("DB", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is,"UTF-8").useDelimiter("\\A");
        String res = s.hasNext() ? s.next() : "";
        res=res.replaceAll("\n","");
        res=res.replaceAll("\t","");
        return res;
    }

    public List<Item> listaArmas(){
       List<Item> listaItems = new ArrayList<Item>();
       SQLiteDatabase db = getReadableDatabase();
       Cursor cursor = db.rawQuery("SELECT id, tipo, nombre, resource1, resource2, nivel FROM item WHERE tipo = 3" , null);
        while (cursor.moveToNext()){
            Item item = new Item(cursor.getInt(0), cursor.getInt(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getInt(5));

            int id1= context.getResources().getIdentifier(item.getImgResourceStr(), "drawable",  context.getPackageName());
            item.setImgResource(id1);
            if(item.getImgResource2Str()!=null && item.getImgResource2Str()!="")
               item.setImgResource2(context.getResources().getIdentifier(item.getImgResource2Str(), "drawable",  context.getPackageName()));

            listaItems.add(item);
        }
        cursor.close();
        db.close();
        return listaItems;
    }

    public List<Item> listaRopa(){
        List<Item> listaItems = new ArrayList<Item>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, tipo, nombre, resource1, resource2, nivel FROM item WHERE tipo = 1 OR tipo = 2" , null);
        while (cursor.moveToNext()){
            Item item = new Item(cursor.getInt(0), cursor.getInt(1),
                    cursor.getString(2), cursor.getString(3),
                    cursor.getString(4), cursor.getInt(5));

            int id1= context.getResources().getIdentifier(item.getImgResourceStr(), "drawable",  context.getPackageName());
            item.setImgResource(id1);
            if(item.getImgResource2Str()!=null && item.getImgResource2Str()!="")
                item.setImgResource2(context.getResources().getIdentifier(item.getImgResource2Str(), "drawable",  context.getPackageName()));

            listaItems.add(item);
        }
        cursor.close();
        db.close();
        return listaItems;
    }

    public List<Grafico> listaMuebles(){
        List<Grafico> listaMuebles = new ArrayList<Grafico>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id,nombre,tipo,interactivo,drawable,drawAnim,pisable,tarea,precio FROM mueble" , null);
        int i =0;
        while (cursor.moveToNext()){

            String drawableAnimStr=cursor.getString(5);
            String drawableStr = cursor.getString(4);
            int pwp = cursor.getInt(8);
            Log.d("AlmSQL", "cargando mueble:"+ drawableStr +" : "+pwp);
            int resDrawableID = context.getResources().getIdentifier(drawableStr, "drawable",  context.getPackageName());
            Drawable drawable = ContextCompat.getDrawable(context,resDrawableID);
            int resDrawableAnimId=0;
            Drawable drawableAnim = null;

            if(drawableAnimStr!=null && !drawableAnimStr.equals(""))//si tiene algo en el drawableAnim
            {
                resDrawableAnimId = context.getResources().getIdentifier(drawableAnimStr, "drawable",  context.getPackageName());
                drawableAnim = ContextCompat.getDrawable(context,resDrawableID);
            }

//Puede que esta lista  este gastando mucha memoria
            Grafico g = new Grafico(cursor.getInt(0), cursor.getString(1), cursor.getString(2), (cursor.getInt(3) ==1),
                                    drawable,drawableAnim, (cursor.getInt(6) ==1), cursor.getString(7),cursor.getInt(8));

            //hace falta ponerle el id del recurso para utilizar el drawable en mas de una ocasion
            g.setIdRecursoDrawable(resDrawableID);
            g.setIdRecursoDrawAnimacion(resDrawableAnimId);
            listaMuebles.add(g);

        }

        cursor.close();
        db.close();
        return listaMuebles;
    }



}
