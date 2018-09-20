package org.example.losximos;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;

/**
 * Created by pistoleta on 29/11/17.
 */

public class Grafico  {
    private  boolean interactivo;
    private  String tipo;
    private  int id;
    private Drawable drawable;      //imagen a dibujar
    private Drawable drawCamisa;
    private Drawable drawPantalon;
    private Drawable drawAnimacion;
    private int idRecursoDrawAnimacion;
    private int idRecursoDrawable;
    private int precio;
    private int cenX, cenY;         //posicion centro del grafico
    private int ancho, alto;        //dimensiones imagen
    private int xAnterior, yAnterior;//posicion anterior
    private View view;              //usada en view.invalidate()
    private int radioInval;
    private boolean visible;
    private String nombre;
    public boolean animando=false;
    public boolean seleccionado=false;
    public boolean colapsando=false;
    private boolean pisable=false;


    /*Graficos simples (gui)*/
    public Grafico(View view, Drawable drawable) {
        this.view = view;
        this.drawable = drawable;
        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioInval = (int)Math.hypot(ancho/2,alto/2);
        visible = true;
    }

    /*para mueble con o sin animacion*/
     public Grafico(Drawable drawable, Drawable drawAnimacion,int cenX, int cenY, String nombre, boolean pisable)
     {
         this.drawable= drawable;
         this.drawAnimacion = drawAnimacion;
         this.cenX= cenX;
         this.cenY= cenY;
         this.nombre=nombre;
         ancho = drawable.getIntrinsicWidth();
         alto = drawable.getIntrinsicHeight();
         radioInval = (int)Math.hypot(ancho/2,alto/2);
         visible = true;
         this.pisable=pisable;
     }

    /*para mueble con o sin animacion DESDDE SQLITE*/
    public Grafico(int id, String nombre, String tipo, boolean interactivo, Drawable drawable, Drawable drawAnimacion, boolean pisable, String tarea, int precio)
    {
        this.id=id;
        this.nombre=nombre;
        this.tipo=tipo;
        this.interactivo=interactivo;
        this.drawable= drawable;
        this.drawAnimacion = drawAnimacion;
        this.pisable=pisable;
        this.precio = precio;
        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioInval = (int)Math.hypot(ancho/2,alto/2);
        visible = true;

    }

    public Grafico clon(Context context){
        Grafico g = new Grafico(id,nombre,tipo,interactivo,drawable,drawAnimacion,pisable,"",precio);
        g.idRecursoDrawable=idRecursoDrawable;
        g.idRecursoDrawAnimacion = idRecursoDrawAnimacion;
        g.drawable =(ContextCompat.getDrawable(context,idRecursoDrawable));
        if(idRecursoDrawAnimacion!=0)
            g.drawAnimacion = (ContextCompat.getDrawable(context,idRecursoDrawAnimacion));
        return g;
    }
    /* Jugador */
    public Grafico(View view, Drawable drawable, Drawable drawCamisa, Drawable drawPantalon) {
        this.view = view;
        this.drawable = drawable;
        ancho = drawable.getIntrinsicWidth();
        alto = drawable.getIntrinsicHeight();
        radioInval = (int)Math.hypot(ancho/2,alto/2);
        visible = true;
        this.drawCamisa=drawCamisa;
        this.drawPantalon=drawPantalon;
    }


    public void dibujaGrafico(Canvas canvas) {

             //cojo el centro
             int x = cenX - ancho / 2;
             int y = cenY - alto / 2;

             //guarda matriz transformacion del canvas

            if(!animando) {
                drawable.draw(canvas);                   //se dibuja el drawable en el canvas
                drawable.setBounds(x, y, x + ancho, y + alto); //limites donde se situara el drawable
            }
            else {
                drawAnimacion.draw(canvas);
                drawAnimacion.setBounds(x, y, x + ancho, y + alto); //limites donde se situara el drawable
            }

             view.invalidate(cenX - radioInval, cenY - radioInval, cenX + radioInval, cenY + radioInval);  //informamos a la vista q tiene q ser redibujada
             // view.invalidate(xAnterior , yAnterior , xAnterior , yAnterior ); // redibujar cuadrado invalidate mas grande q el de setbounds al rotar el asteroide

             xAnterior = cenX;
             yAnterior = cenY;



    }

    /*Solo jugador*/
    public void dibujaGraficoJugador(Canvas canvas) {
        //cojo el centro
        int x =  cenX - ancho / 2;
        int y =  cenY - alto / 2;
        drawable.setBounds(x, y, x + ancho, y + alto); //limites donde se situara el drawable
        //guarda matriz transformacion del canvas

        drawable.draw(canvas);                   //se dibuja el drawable en el canvas
        if(drawCamisa!=null) {
            drawCamisa.setBounds(x, y, x + ancho, y + alto);
            drawCamisa.draw(canvas);
        }
        if(drawPantalon!=null) {
            drawPantalon.setBounds(x, y, x + ancho, y + alto);
            drawPantalon.draw(canvas);
        }
        view.invalidate(cenX-radioInval, cenY-radioInval, cenX+radioInval, cenY+radioInval);  //informamos a la vista q tiene q ser redibujada
        // view.invalidate(xAnterior , yAnterior , xAnterior , yAnterior ); // redibujar cuadrado invalidate mas grande q el de setbounds al rotar el asteroide

        xAnterior = cenX;
        yAnterior = cenY;



    }

    public void desplazar(int xDest, int yDest) {
        xAnterior=cenX;
        yAnterior=cenY;

        if(cenX<xDest) {
            cenX++;

        }
        else if(cenX>xDest) {
            cenX--;

        }
        if(cenY<yDest) {
            cenY++;

        }
        else if(cenY>yDest) {
            cenY--;
        }

    }

/*Getters y setters*/
    public Drawable getDrawCamisa() {
        return drawCamisa;
    }

    public void setDrawCamisa(Drawable drawCamisa) {
        this.drawCamisa = drawCamisa;
    }

    public Drawable getDrawPantalon() {
        return drawPantalon;
    }

    public void setDrawPantalon(Drawable drawPantalon) {
        this.drawPantalon = drawPantalon;
    }


    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getCenX() {
        return cenX;
    }

    public void setCenX(int cenX) {
        this.cenX = cenX;
    }

    public int getCenY() {
        return cenY;
    }

    public void setCenY(int cenY) {
        this.cenY = cenY;
    }

    public int getAncho() {
        return ancho;
    }

    public void setAncho(int ancho) {
        this.ancho = ancho;
    }

    public int getAlto() {
        return alto;
    }

    public void setAlto(int alto) {
        this.alto = alto;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isPisable() {
        return pisable;
    }

    public void setPisable(boolean pisable) {
        this.pisable = pisable;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public Drawable getDrawAnimacion() {
        return drawAnimacion;
    }

    public void setDrawAnimacion(Drawable drawAnimacion) {
        this.drawAnimacion = drawAnimacion;
    }

    public int getIdRecursoDrawAnimacion() {
        return idRecursoDrawAnimacion;
    }

    public void setIdRecursoDrawAnimacion(int idRecursoDrawAnimacion) {
        this.idRecursoDrawAnimacion = idRecursoDrawAnimacion;
    }

    public int getIdRecursoDrawable() {
        return idRecursoDrawable;
    }

    public void setIdRecursoDrawable(int idRecursoDrawable) {
        this.idRecursoDrawable = idRecursoDrawable;
    }

    public boolean isInteractivo() {
        return interactivo;
    }

    public String getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public boolean haSidoPulsado(int x, int y)
    {
        if (!animando)
            return this.getDrawable().getBounds().contains((int) x, (int) y);
        else
            return this.getDrawAnimacion().getBounds().contains((int) x, (int) y);
    }



    public int getTope()
    {
       return cenY-(alto/2);
    }
    public int getBase()
    {
        return cenY+(alto/2);
    }

    /*Muebles*/
    public void seleccionar() {
        this.getDrawable().mutate();//impide que se modifiquen todas las instancias de ese drawable
        this.getDrawable().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
        this.getDrawable().setAlpha(100);
        this.seleccionado=true;
        this.colapsando=false;
    }
    public void seleccionarRojo(){
        this.getDrawable().setColorFilter(0xffff0000, PorterDuff.Mode.MULTIPLY);
        this.colapsando=true;
        this.seleccionado=false;
    }
    public void deseleccionar(){
        this.getDrawable().clearColorFilter();
        this.getDrawable().setAlpha(255);
        this.seleccionado=false;
        this.colapsando=true;

    }
}
