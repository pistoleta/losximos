package org.example.losximos;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import static org.example.losximos.Juego.ayuda;


/**
 * Created by pistoleta on 29/11/17.
 */

public class VistaJuego extends View  implements SensorEventListener{



    /*tareas*/
    private static final int NINGUNA = 0;
    private static final int ESTUDIAR = 1;
    private static final int PESAS = 2;
    private static final int EXPLORAR = 3;
    private static final int DORMIR = 4;
    private static final int TRABAJAR = 5;

    private static final String[] tareaNombre = new String[]{"","Estudiar","Hacer pesas", "Explorar","Dormir","Trabajar"};
    private  int inicioBarraTope;
    private  int inicioBarra;
    private  Paint paintBarra;
    private final int REDUCCION_TIEMPO = 30; //30 veces menos tiempo para tarea


    private int tareaEnCurso  ;
    public int idMuebleTareaEnCurso =0;
    private long comienzoTarea;
    private long finTarea;


    private List<Grafico> lgraficosInsertados;
    public static Grafico jugador;
    Grafico energia;
    Grafico rayo;
    Grafico moneda;
    Grafico reloj;

    Drawable drawableJugador;
    Drawable drawCamisa;
    Drawable drawPantalon;

    private int drawableJugadorCamina;
    private int drawableJugadorPesas;
    private int drawableEscritorio;
    private int drawableCama;



    Drawable drawRayo;
    Drawable drawEnergia;
    Drawable drawMoneda;
    Drawable drawReloj;

    Context context;
    //coordenadas del último evento.
    private float mX = 0, mY = 0;
    private boolean singleTap = false;
    private String tiempoRestante;
    private Path mPath;
    private Paint pincel;
    private TextView tvMonedas;
    private AlertDialog alertDialogFin;
    private Activity actividadPadre;
    private boolean caminando=false;


    private long ultimoProceso;
    private long PERIODO_PROCESO = 5;
    private long PERIODO_MUESTREO_RELOJ =1000;

    private HiloCaminar hiloCaminar;
    private Thread hiloComprobarColision;

    private Application aplicacion;
    public boolean modoEdicion = false;
    public int idMuebleMover=-1;
    private String TAG ="MiProyecto";
    public int idMuebleInsertar = -1;
    private int anchoPantalla;
    private int altoPantalla;
    private Vector<Punto> ruta;
    private int yAnt;
    private int xAnt;
    private int indiceRuta=0;
    Paint pincelRuta;
    private Paint paintTexto;
    public MiTarea miTarea;
    public boolean modoInsercion = false;
    private int clicX;
    private int clicY;
    private int offsetX;
    private int offsetY;
    private float ultimaX;
    private float ultimaY;
    private long ultimaComprobacionColision;
    private AlertDialog dialogElimMueble;


    /*audio*/
    SoundPool soundPool;
    private int soundIdLogro;
    private int soundIdMueble;
    private int soundIdPago;

    private MediaPlayer mp;
    private int soundIdTap;
    private Rect rectCentro;


    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        Log.d(TAG, "VistaJuego()");

        this.context = context;
        aplicacion = (Aplicacion)context.getApplicationContext();

        cargarJugador();
        cargaGUI();
        cargaSonidos();

        ayuda.compruebaYlanza("ayuda_bienvenida");


    }



    @Override protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter)
    {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        Log.d(TAG, "onSizeChanged()");

        this.anchoPantalla=ancho;
        this.altoPantalla=alto;
        
        /*SITUAMOS OBJETOS AL INICIO*/

        //posicionamos el jugador en el centro de la vista
        jugador.setCenX(ancho/2);
        jugador.setCenY(alto/2);

        rectCentro = new Rect(jugador.getCenX()-jugador.getAncho()/2, jugador.getCenY()-jugador.getAlto()/2,
                jugador.getCenX()+jugador.getAncho()/2, jugador.getCenY()+jugador.getAlto()/2);

        /*Barra energia*/
        rayo.setCenX(0+rayo.getAncho()/2);
        rayo.setCenY(0+rayo.getAlto()/2);
        energia.setAncho(210);
        energia.setCenX(0+rayo.getAncho()+energia.getAncho()/2);
        energia.setCenY(0+rayo.getAlto()/2);
        moneda.setCenX(ancho-moneda.getAncho()*2);
        moneda.setCenY(0+moneda.getAlto()/2);
        inicioBarra = (rayo.getAncho())+5;
        inicioBarraTope= (rayo.getCenY()-10);
        reloj.setCenX(ancho-(reloj.getAncho()/2));
        reloj.setCenY(alto-(reloj.getAlto()/2));


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*muebles*/
        for(int i = 0; i< lgraficosInsertados.size(); i++){
            lgraficosInsertados.get(i).dibujaGrafico(canvas);
        }

        if(jugador.isVisible())
            jugador.dibujaGraficoJugador(canvas);

      /*  if(ruta!=null) {
            for (Punto p : ruta) {
                canvas.drawPoint(p.x, p.y, pincelRuta);
            }
        }*/

        /*misc*/
        MostrarBarraEnergia(canvas);    //!!!!!!!!!!
        energia.dibujaGrafico(canvas);
        rayo.dibujaGrafico(canvas);
        moneda.dibujaGrafico(canvas);
        if(tareaEnCurso != NINGUNA)
            reloj.dibujaGrafico(canvas);

    }




    class Punto{
        private int x,y;

        public Punto(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }
    }
    /*caminar*/
    class HiloCaminar extends Thread{

        public HiloCaminar()
        {
            Log.d(TAG, "HiloCaminar()");
        }
        @Override public void run()
        {


            caminando= true;
            Log.d(TAG, "HiloCaminar -> run()");
            while(caminando && !hiloCaminar.isInterrupted()) {
                ActualizarPosicion();
            }

            stopCaminar();
        }
    }

    class HiloComprobarColision extends Thread{

        private int idMueble;
        public HiloComprobarColision(int idMueble){Log.d(TAG,"HiloComprobarColision()");
        this.idMueble=idMueble;}
        @Override public void run()
        {
            
            Log.d(TAG, "HiloComprobarColision -> run()");
            while(!hiloComprobarColision.isInterrupted()) {
                comprobarColisionHilo(idMueble);
            }

        }
    }


    private void CaminarHacia(int x, int y) {
        Log.d(TAG, "CaminarHacia()");
        if(hiloCaminar!= null && hiloCaminar.isAlive()) {
            hiloCaminar.interrupt();
            caminando=false;  //detiene el anterior hilo (la anterior trayectoria)
        }

        crearRutaD(x,y);

        hiloCaminar = new HiloCaminar();
        hiloCaminar.start();

        actividadPadre.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startCaminar();
            }
        });

    }

    private void ActualizarPosicion() {
        // Log.d(TAG, "ActualizaPosicion() ");
        long ahora = System.currentTimeMillis();
        //verificamos si ya ha pasado PERIODO_PROCESO desde la última vez que se ejecutó (ultimoProceso).
        if (ultimoProceso + PERIODO_PROCESO > ahora)
        {
            return; // Salir si el período de proceso no se ha cumplido.
        }
        ultimoProceso = ahora;

        if(ruta.size()>indiceRuta)
        jugador.desplazar(ruta.get(indiceRuta).getX(), ruta.get(indiceRuta).getY());

        indiceRuta++;
        if(indiceRuta==ruta.size()) {
            caminando = false;
            indiceRuta=0;
        }
        else
            caminando=true;

    }

    private boolean crearRutaA(int xFin,int yFin)
    {
        final int DERECHA =0;
        final int IZQUIERDA =1;
        final int ARRIBA = 2;
        final int ABAJO =3;
        int ultimaDireccion=-1;

        indiceRuta=0;
        ruta = new Vector<Punto>();
        Punto ini= new Punto(jugador.getCenX(),jugador.getCenY());
        Punto fin   = new Punto(xFin,yFin);
        boolean llegado = false;

        Punto p= new Punto(ini.x, ini.y);
        int x=p.getX();
        int y=p.getY();

        while(!llegado) {

            if(x<fin.getX() && ultimaDireccion!=IZQUIERDA)
            {//derecha
                boolean obstaculo=false;
                while(!obstaculo && x!=xFin) {
                    x++;
                    ultimaDireccion = DERECHA;


                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde la parte izquierda

                            //1 paso atras
                            x--;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenY();
                            int base = g.getBase();
                            int tope = g.getTope() - jugador.getAlto()/2;

                            if (y > centro)//voy por abajo
                            {
                                ultimaDireccion = ABAJO;
                                for (int i = y; i < (base + 1); i++) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            } else {//voy por arriba
                                ultimaDireccion = ARRIBA;
                                for (int i = y; i > (tope - 1); i--) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            }

                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst

            }
            else if(x>fin.getX() && ultimaDireccion!=DERECHA){//izquierda
                boolean obstaculo=false;

                while(!obstaculo && x!=xFin) {
                    x--;
                    ultimaDireccion=IZQUIERDA;

                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde la parte izquierda

                            //1 paso atras
                            x++;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenY();
                            int base = g.getBase();
                            int tope = g.getTope() - jugador.getAlto()/2;

                            if (y > centro)//voy por abajo
                            {
                                ultimaDireccion = ABAJO;
                                for (int i = y; i < (base + 1); i++) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            } else {//voy por arriba
                                ultimaDireccion = ARRIBA;
                                for (int i = y; i > (tope - 1); i--) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            }


                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst

            }
            else if(y<fin.getY() && ultimaDireccion!=ARRIBA){//si estoy mas arriba...
                boolean obstaculo=false;


                 while(!obstaculo && y!= yFin) {

                     y++;
                     ultimaDireccion=ABAJO;
                     for (Grafico g : lgraficosInsertados) {
                         if (g.getDrawable().getBounds().contains(x, y)) {
                             obstaculo = true;
                             //he chocado desde arriba

                             //1 paso atras
                             y--;
                             ruta.add(new Punto(x, y));

                             int alto = g.getAlto();
                             int centroY = g.getCenY();
                             int centroX = g.getCenX();
                             int base = g.getBase() + jugador.getAlto()/2;
                             int tope = g.getTope() - jugador.getAlto()/2;
                             int dcha = (g.getCenX() + g.getAncho() / 2) + 50;
                             int izq = (g.getCenX() - g.getAncho() / 2) - 50;

                             if (x > centroX)//voy por dcha
                             {
                                 ultimaDireccion = DERECHA;
                                 for (int i = x; i < dcha; i++) {
                                     ruta.add(new Punto(i, y));
                                     x = i;
                                 }
                             } else {//voy por izq
                                 ultimaDireccion = IZQUIERDA;
                                 for (int i = x; i > izq; i--) {
                                     ruta.add(new Punto(i, y));
                                     x = i;
                                 }
                             }
                         }//fin if
                     }//fin for
                     if (!obstaculo) ruta.add(new Punto(x, y));
                 }//fin while obst
            }
            else if(y>fin.getY() && ultimaDireccion!=ABAJO){ //si estoy mas abajo que la meta...
                boolean obstaculo=false;
                while(!obstaculo && y!=yFin) {
                    y--;    //paso arriba
                    ultimaDireccion = ARRIBA;


                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde abajo

                            //1 paso atras
                            y++;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenX();
                            int base = g.getBase() + 50;
                            int tope = g.getTope() - 50;
                            int dcha = (g.getCenX() + g.getAncho() / 2) + 50;
                            int izq = (g.getCenX() - g.getAncho() / 2) - 50;

                            if (x > centro)//voy por dcha
                            {
                                ultimaDireccion = DERECHA;
                                for (int i = x; i < dcha; i++) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            } else {//voy por izq
                                ultimaDireccion = IZQUIERDA;
                                for (int i = x; i > izq; i--) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            }
                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//while obst
            }


            p=  ruta.get(ruta.size()-1);
            if(p.getX()==fin.getX() && p.getY()==fin.getY())
                llegado=true;

            if(ruta.size()>1000){
                llegado=true;
                Log.d(TAG,"RUTAS>1000!!!!!!!!!!!!!!");
                return false;
            }
        }

        return true;

    }
    private boolean crearRutaB(int xFin,int yFin)
    {
        final int DERECHA =0;
        final int IZQUIERDA =1;
        final int ARRIBA = 2;
        final int ABAJO =3;
        int ultimaDireccion=-1;

        indiceRuta=0;
        ruta = new Vector<Punto>();
        Punto ini= new Punto(jugador.getCenX(),jugador.getCenY());
        Punto fin   = new Punto(xFin,yFin);
        boolean llegado = false;

        Punto p= new Punto(ini.x, ini.y);
        int x=p.getX();
        int y=p.getY();

        while(!llegado) {

            if(y<fin.getY() && ultimaDireccion!=ARRIBA){//si estoy mas arriba...
                boolean obstaculo=false;


                while(!obstaculo && y!= yFin) {

                    y++;
                    ultimaDireccion=ABAJO;
                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde arriba

                            //1 paso atras
                            y--;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centroY = g.getCenY();
                            int centroX = g.getCenX();
                            int base = g.getBase() + jugador.getAlto()/2;
                            int tope = g.getTope() - jugador.getAlto()/2;
                            int dcha = (g.getCenX() + g.getAncho() / 2) + 50;
                            int izq = (g.getCenX() - g.getAncho() / 2) - 50;

                            if (x > centroX)//voy por dcha
                            {
                                ultimaDireccion = DERECHA;
                                for (int i = x; i < dcha; i++) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            } else {//voy por izq
                                ultimaDireccion = IZQUIERDA;
                                for (int i = x; i > izq; i--) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            }
                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst
            }
            else if(y>fin.getY() && ultimaDireccion!=ABAJO){ //si estoy mas abajo que la meta...
                boolean obstaculo=false;
                while(!obstaculo && y!=yFin) {
                    y--;    //paso arriba
                    ultimaDireccion = ARRIBA;


                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde abajo

                            //1 paso atras
                            y++;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenX();
                            int base = g.getBase() + 50;
                            int tope = g.getTope() - 50;
                            int dcha = (g.getCenX() + g.getAncho() / 2) + 50;
                            int izq = (g.getCenX() - g.getAncho() / 2) - 50;

                            if (x > centro)//voy por dcha
                            {
                                ultimaDireccion = DERECHA;
                                for (int i = x; i < dcha; i++) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            } else {//voy por izq
                                ultimaDireccion = IZQUIERDA;
                                for (int i = x; i > izq; i--) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            }
                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//while obst
            }

           else if(x<fin.getX() && ultimaDireccion!=IZQUIERDA)
            {//derecha
                boolean obstaculo=false;
                while(!obstaculo && x!=xFin) {
                    x++;
                    ultimaDireccion = DERECHA;


                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde la parte izquierda

                            //1 paso atras
                            x--;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenY();
                            int base = g.getBase();
                            int tope = g.getTope() - jugador.getAlto()/2;

                            if (y > centro)//voy por abajo
                            {
                                ultimaDireccion = ABAJO;
                                for (int i = y; i < (base + 1); i++) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            } else {//voy por arriba
                                ultimaDireccion = ARRIBA;
                                for (int i = y; i > (tope - 1); i--) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            }

                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst

            }
            else if(x>fin.getX() && ultimaDireccion!=DERECHA){//izquierda
                boolean obstaculo=false;

                while(!obstaculo && x!=xFin) {
                    x--;
                    ultimaDireccion=IZQUIERDA;

                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde la parte izquierda

                            //1 paso atras
                            x++;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenY();
                            int base = g.getBase();
                            int tope = g.getTope() - jugador.getAlto()/2;

                            if (y > centro)//voy por abajo
                            {
                                ultimaDireccion = ABAJO;
                                for (int i = y; i < (base + 1); i++) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            } else {//voy por arriba
                                ultimaDireccion = ARRIBA;
                                for (int i = y; i > (tope - 1); i--) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            }


                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst

            }



            p=  ruta.get(ruta.size()-1);
            if(p.getX()==fin.getX() && p.getY()==fin.getY())
                llegado=true;

            if(ruta.size()>1000){
                llegado=true;
                Log.d(TAG,"RUTAS>1000!!!!!!!!!!!!!!");
                return false;
            }
        }

        return true;

    }
    private boolean crearRutaC(int xFin,int yFin)
    {
        final int DERECHA =0;
        final int IZQUIERDA =1;
        final int ARRIBA = 2;
        final int ABAJO =3;
        int ultimaDireccion=-1;

        indiceRuta=0;
        ruta = new Vector<Punto>();
        Punto ini= new Punto(jugador.getCenX(),jugador.getCenY());
        Punto fin   = new Punto(xFin,yFin);
        boolean llegado = false;

        Punto p= new Punto(ini.x, ini.y);
        int x=p.getX();
        int y=p.getY();

        while(!llegado) {

            if(y<fin.getY() && ultimaDireccion!=ARRIBA){//si estoy mas arriba...
                boolean obstaculo=false;


                while(!obstaculo && y!= yFin) {

                    y++;
                    ultimaDireccion=ABAJO;
                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde arriba

                            //1 paso atras
                            y--;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centroY = g.getCenY();
                            int centroX = g.getCenX();
                            int base = g.getBase() + jugador.getAlto()/2;
                            int tope = g.getTope() - jugador.getAlto()/2;
                            int dcha = (g.getCenX() + g.getAncho() / 2) + 50;
                            int izq = (g.getCenX() - g.getAncho() / 2) - 50;

                            if (x > centroX)//voy por dcha
                            {
                                ultimaDireccion = DERECHA;
                                for (int i = x; i < dcha; i++) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            } else {//voy por izq
                                ultimaDireccion = IZQUIERDA;
                                for (int i = x; i > izq; i--) {
                                    ruta.add(new Punto(i, y));
                                    x = i;
                                }
                            }
                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst
            }
            else if(y>fin.getY() && ultimaDireccion!=ABAJO){ //si estoy mas abajo que la meta...
                boolean obstaculo=false;
                boolean colisionArriba =false;
                while(!obstaculo && y!=yFin) {
                    y--;    //paso arriba
                    ultimaDireccion = ARRIBA;


                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde abajo
                            colisionArriba=true;

                            String nom=g.getNombre();
                            //1 paso atras
                          /*  y++;
                            ruta.add(new Punto(x, y));*/

                            int alto = g.getAlto();
                            int centro = g.getCenX();
                            int base = g.getBase() + 50;
                            int tope = g.getTope() - 50;
                            int dcha = (g.getCenX() + g.getAncho() / 2) + 50;
                            int izq = (g.getCenX() - g.getAncho() / 2) - 50;

                            if (x > centro)//voy por dcha
                            {
                                ultimaDireccion = DERECHA;
                                while(colisionArriba)
                                {


                                    x++;
                                    ruta.add(new Punto(x,y));
                                    if(choca(x,y+1)==null){
                                        colisionArriba=false;
                                    }
                                }
                            } else {//voy por izq
                                ultimaDireccion = IZQUIERDA; //ir a la izquierda hasta que arriba no haya colision

                                while(colisionArriba)
                                {   x--;
                                    ruta.add(new Punto(x,y));
                                    if(choca(x,y+1)==null){
                                        colisionArriba=false;
                                    }
                                }

                            }
                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//while obst
            }

            else if(x<fin.getX() && ultimaDireccion!=IZQUIERDA)
            {//derecha
                boolean obstaculo=false;
                while(!obstaculo && x!=xFin) {
                    x++;
                    ultimaDireccion = DERECHA;


                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde la parte izquierda

                            //1 paso atras
                            x--;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenY();
                            int base = g.getBase();
                            int tope = g.getTope() - jugador.getAlto()/2;

                            if (y > centro)//voy por abajo
                            {
                                ultimaDireccion = ABAJO;
                                for (int i = y; i < (base + 1); i++) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            } else {//voy por arriba
                                ultimaDireccion = ARRIBA;
                                for (int i = y; i > (tope - 1); i--) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            }

                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst

            }
            else if(x>fin.getX() && ultimaDireccion!=DERECHA){//izquierda
                boolean obstaculo=false;

                while(!obstaculo && x!=xFin) {
                    x--;
                    ultimaDireccion=IZQUIERDA;

                    for (Grafico g : lgraficosInsertados) {
                        if (g.getDrawable().getBounds().contains(x, y)) {
                            obstaculo = true;
                            //he chocado desde la parte izquierda

                            //1 paso atras
                            x++;
                            ruta.add(new Punto(x, y));

                            int alto = g.getAlto();
                            int centro = g.getCenY();
                            int base = g.getBase();
                            int tope = g.getTope() - jugador.getAlto()/2;

                            if (y > centro)//voy por abajo
                            {
                                ultimaDireccion = ABAJO;
                                for (int i = y; i < (base + 1); i++) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            } else {//voy por arriba
                                ultimaDireccion = ARRIBA;
                                for (int i = y; i > (tope - 1); i--) {
                                    ruta.add(new Punto(x, i));
                                    y = i;
                                }
                            }


                        }//fin if
                    }//fin for
                    if (!obstaculo) ruta.add(new Punto(x, y));
                }//fin while obst

            }



            p=  ruta.get(ruta.size()-1);
            if(p.getX()==fin.getX() && p.getY()==fin.getY())
                llegado=true;

            if(ruta.size()>1000){
                llegado=true;
                Log.d(TAG,"RUTAS>1000!!!!!!!!!!!!!!");
                return false;
            }
        }

        return true;

    }
    private boolean crearRutaD(int xFin, int yFin){
        Log.d("RUTA", "CreoRutaD()");
         int DERECHA =0;
         int IZQUIERDA =1;
         int ARRIBA = 2;
         int ABAJO =3;
        int ultimaDireccion=-1;
        int direccion=-1;
        int direccionY=-1;

        indiceRuta=0;
        ruta = new Vector<Punto>();
        Punto ini= new Punto(jugador.getCenX(),jugador.getCenY());
        Punto fin   = new Punto(xFin,yFin);


        Punto p= new Punto(ini.x, ini.y);
        int x=p.getX();
        int y=p.getY();

        if (x < xFin) {
            direccion=DERECHA; //0
            Log.d("RUTA", "Direccion derecha");
        }
        else if(x>xFin){
            direccion=IZQUIERDA; //1
            Log.d("RUTA", "Direccion izquierda");
        }
        else if(y>yFin){
            direccion=ARRIBA;   //2
            Log.d("RUTA", "Direccion arriba");
        }else if(y<yFin){
            direccion=ABAJO;    //3
            Log.d("RUTA", "Direccion abajo");
        }

        if(x < xFin) {
            Log.d("RUTA", "Ruta hacia derecha");
            boolean hayHueco=true;
            boolean llegado = false;
            while (hayHueco && !llegado) {
                int vHuecos[] = hueco(x + 1, y);
                if (vHuecos[DERECHA] == 0) {
                    x++;
                    ruta.add(new Punto(x, y));
                } else {
                    hayHueco = false;
                }
                if(x==xFin)
                    llegado=true;
            }
        }
        if(x > xFin){
            Log.d("RUTA", "Ruta hacia izquierda");
            boolean hayHueco=true;
            boolean llegado=false;
            while (hayHueco && !llegado) {
                int vHuecos[] = hueco(x - 1, y);
                if (vHuecos[IZQUIERDA] == 0) {
                    x--;
                    ruta.add(new Punto(x, y));

                } else {
                    hayHueco = false;
                }
                if(x==xFin)
                    llegado=true;
            }
        }
        if(y > yFin){
            Log.d("RUTA", "Ruta hacia arriba");
            boolean hayHueco=true;
            boolean llegado = false;
            while (hayHueco&& !llegado) {
                int vHuecos[] = hueco(x , y-1);
                if (vHuecos[ARRIBA] == 0) {
                    y--;
                    ruta.add(new Punto(x, y));
                } else {
                    hayHueco = false;
                }
                if(y==yFin)
                    llegado=true;
            }
        }
        if(y < yFin){
            Log.d("RUTA", "Ruta hacia abajo");
            boolean hayHueco=true;
            boolean llegado = false;
            while (hayHueco && !llegado) {
                int vHuecos[] = hueco(x , y+1);
                if (vHuecos[ABAJO] == 0) {
                    y++;
                    ruta.add(new Punto(x, y));
                } else {
                    hayHueco = false;
                    ruta.add(new Punto(x,y-jugador.getAlto()/2));
                }
                if(y==yFin)
                    llegado=true;
            }
        }

    /*    p=  ruta.get(ruta.size()-1);
        if(p.getX()==fin.getX() && p.getY()==fin.getY())
            llegado=true;*/
        return true;
    }

    private int[] hueco(int x,int y)
    {
        int[] vHuecos= new int[]{1,1,1,1};

        if(choca(x+1,y)==null){
            //hueco dcha
            vHuecos[0]=0;
        }
        if(choca(x-1,y)==null){
            //hueco izq
            vHuecos[1]=0;
        }
        if(choca(x,y-1)==null){
            //hueco arriba
            vHuecos[2]=0;
        }
        if(choca(x,y+1)==null){
            //hueco abajo
            vHuecos[3]=0;
        }
        return vHuecos;
    }
    private Grafico choca(int x,int y){
        Grafico gcolision=null;
        for(Grafico g: lgraficosInsertados) {
            if(!g.isPisable()) {
                if (g.getDrawable().getBounds().contains(x, y+50))// le sumo 50 para que el jugador siempre vaya un trozo por arriba del mueble y no solape
                    gcolision = g;
            }
        }
        return gcolision;
    }




    /*tarea*/

    class MiTarea extends AsyncTask<Integer,String,Void>{
        private Context contexto;
        @Override
        protected void onPreExecute(){
            ((Juego)actividadPadre).mostrarRelojTarea();
            Log.d("PREEX", "comienzoTarea "+comienzoTarea);
            Log.d("PREEX", "finTarea "+finTarea);
            Log.d("PREEX", "diferencia"+(finTarea-comienzoTarea));
        }
        @Override
        protected Void doInBackground(Integer... n){

            while(finTarea>0 && tareaEnCurso!=NINGUNA && !miTarea.isCancelled()) {
                long ahora = System.currentTimeMillis();
                long progreso = 0;
                long porcent = 0;
                long diferencia = 0;

                if (ultimoProceso + PERIODO_MUESTREO_RELOJ < ahora)
                {

                    ahora = System.currentTimeMillis();
                    progreso = finTarea-ahora;
                    diferencia = finTarea - comienzoTarea;
                    porcent = (progreso * 100)/diferencia;
                    Log.d("ASYNC", "ahora "+ahora);
                    Log.d("ASYNC", "progreso "+progreso);
                    Log.d("ASYNC", "porcent "+porcent);

                    porcent = 100 - porcent;

                    long millis = (finTarea-ahora);

                    int hoursInt = (int) (millis/(1000 * 60 * 60));
                    int minsInt = (int) (millis/(1000*60)) % 60;
                    int secInt = (int) (millis / 1000) % 60;

                    tiempoRestante = (hoursInt < 10  ? "0"+hoursInt : hoursInt)  +":"
                                    +(minsInt < 10  ? "0"+minsInt : minsInt) +":"
                                    +(secInt < 10  ? "0"+secInt : secInt);

                    publishProgress(tiempoRestante, Long.toString(porcent));


                    if(ahora>finTarea) return null;
                    ultimoProceso = ahora;
                }
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(String... tiempoRestante){
            Log.d("PROGRESS","tiempoRes0 "+tiempoRestante[0]+ " -tiempoRes1 "+tiempoRestante[1]);
            ((Juego)actividadPadre).actualizaRelojTarea(tiempoRestante[0]);
            actualizaPorcentajeTarea(tiempoRestante[1]);
            Log.d("ASYNC", "onProgressUpdate()");
        }
        @Override
        protected void onPostExecute(Void v){
            Log.d("ASYNC", "onPostExecute()");
            ((Juego)actividadPadre).ocultarRelojTarea();
            if(tareaEnCurso!=NINGUNA) {//si no la he cancelado/acelerado yo
                finalizarTarea(false);
                soundPool.play(soundIdLogro, 1, 1, 3, 0, 1);
            }
        }

    }

    public void actualizaPorcentajeTarea(String porcentajeString)
    {
        Log.d("ACTUALIZAPORC", "porcentajeString "+porcentajeString);
        long porcentaje = Long.parseLong(porcentajeString);
        if(porcentaje < 6){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock5);
        }else if(porcentaje < 10){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock5);
        }else if(porcentaje < 15){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock10);
        }else if(porcentaje<20){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock15);
        }else if(porcentaje<25){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock20);
        }else if(porcentaje<30){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock25);
        }else if(porcentaje<35){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock30);
        }else if(porcentaje<40){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock35);
        }else if(porcentaje<45){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock40);
        }else if(porcentaje<50){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock45);
        }else if(porcentaje<55){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock50);
        }else if(porcentaje<60){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock55);
        }else if(porcentaje<65){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock60);
        }else if(porcentaje<70){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock65);
        }else if(porcentaje<75){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock70);
        }else if(porcentaje<80){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock75);
        }else if(porcentaje<85){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock80);
        }else if(porcentaje<90){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock85);
        }else if(porcentaje<95){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock90);
        }else if(porcentaje<99){
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock95);
        }else if(porcentaje < 101) {
            drawReloj = ContextCompat.getDrawable(context, R.drawable.clock100);
        }
        reloj.setDrawable(drawReloj);

    }

    public void reanudarTarea() {
        Log.d(TAG, "reanudarTarea()");
        EstadoJugador estadoJugador = (((Aplicacion)aplicacion)).getEstadoJugador();
        int tareaAReanudar      =   estadoJugador.getTareaEnCurso();
        int segundosARealizar    =   estadoJugador.getSegundosTarea();
        long cuandoComenzo      =   estadoJugador.getComienzoTarea();
        idMuebleTareaEnCurso      =   estadoJugador.getIdGraficoTarea();

        empezarTarea(tareaAReanudar,segundosARealizar,cuandoComenzo);


    }

    private void empezarTarea(int tarea, int segundos,long comienzo) {
        Log.d(TAG, "empezarTarea()");


        //Comprobar si tengo suficiente energia
        int energiaActual = ((Aplicacion)aplicacion).getEstadoJugador().getEnergia();
        int aRestar = 0;

        if(tarea==ESTUDIAR) {
            aRestar = ( (segundos) * 20 ) / 3600;
        }
        else if(tarea==PESAS){
            aRestar = ( (segundos) * 50 ) / 3600;
        }else if(tarea==EXPLORAR) {
            aRestar = ((segundos) * 40) / 3600;
        }else if(tarea==TRABAJAR){
            aRestar = ((segundos) * 50) /3600;
        }

        if(aRestar<=energiaActual) {//si tengo suficiente energia

            segundos = segundos / REDUCCION_TIEMPO; // !!!!MODIFICADOR DE TIEMPO
            comienzoTarea = comienzo;
            finTarea = comienzoTarea + (segundos * 1000);
            tareaEnCurso = tarea;

            miTarea = (MiTarea) new MiTarea().execute();  //ejecuto asynctask para control del reloj

            jugador.setVisible(false);

            if (tarea != EXPLORAR && tarea!= TRABAJAR)
                startAnimacionMueble();
        }
        else{   //no tengo suficiente energia
            Toast.makeText(context,"No tienes suficiente energía para "+ tareaNombre[tarea]+ ", "+segundos/60+" minutos. \n Deberías descansar.", Toast.LENGTH_LONG).show();
        }
    }

    private void finalizarTarea(boolean forzado) {
        Log.d(TAG, "finalizarTarea()");
        long segundosEfectivosTarea=0;


        if(forzado)
            segundosEfectivosTarea = (long) ((System.currentTimeMillis() - comienzoTarea)/1000.0);
        else {
            segundosEfectivosTarea = (long) ((finTarea - comienzoTarea) / 1000.0);
        }

        //vuelvo a poner los segundos como estaban para asignar puntuacion
        segundosEfectivosTarea = segundosEfectivosTarea * REDUCCION_TIEMPO;//MODIFICADOR!!!!!
        //relojes a 0
        finTarea=0;
        comienzoTarea=0;

        if(alertDialogFin!=null && alertDialogFin.isShowing()) //si hay algun cuadro de dialogo abierto lo cierro
            alertDialogFin.cancel();

        if(tareaEnCurso!=EXPLORAR && tareaEnCurso!=TRABAJAR)
            stopAnimacionMueble();

        if(tareaEnCurso==EXPLORAR){
            obtenerResultadosTareaExplorar(segundosEfectivosTarea);
        }
        else if(tareaEnCurso==TRABAJAR){
            sumarSueldo(segundosEfectivosTarea);
        }

        SumarExperiencia(tareaEnCurso,segundosEfectivosTarea);
        RestarEnergia(tareaEnCurso,segundosEfectivosTarea);

        ((Juego)actividadPadre).cargarHabilidadesYvistaXimo(); //actualizo panel derecha

        tareaEnCurso = NINGUNA;
        //quito la tarea de estadoJugador
        idMuebleTareaEnCurso=0;                       //!!!!!!!!!!! peligroso
        ((Juego)actividadPadre).guardarDatosTarea(NINGUNA,0,0);  //actualizo estado jugador
        stopCaminar(); //muestra al jugador
    }

    private void obtenerResultadosTareaExplorar(long segundosEfectivosTarea) {

        int intel = ((Aplicacion)aplicacion).getEstadoJugador().getInteligencia();
        Random gen = new Random();
        int probEncuentra = gen.nextInt(50);
        boolean encuentraAlgo = probEncuentra < (intel * 0.9) + (segundosEfectivosTarea/60)*0.60 ;
        Log.d("probab", "probencuentra: "+probEncuentra+ " intel: "+(intel * 0.9)+ "seg: "+((segundosEfectivosTarea/60)*0.60));
        gen = new Random();
        int probQueEncuentra = gen.nextInt(100);

        Log.d("probab", "probQueEncuentra: "+probQueEncuentra);
        if(encuentraAlgo){
            Log.d("RES", "encuentraAlgo");
            if(probQueEncuentra<50){ //bebida energetica
                obtenerBebidaEnergetica(segundosEfectivosTarea);
            }
            else if(probQueEncuentra<101){//monedas
                obtenerMonedas(segundosEfectivosTarea);

            }

          /*  else if(probQueEncuentra<=100){//otros
                Toast.makeText(context, "No has encontrado nada",Toast.LENGTH_LONG).show();
            }*/
        }
        else{
            Log.d("RES", "no encuentraAlgo");
            if(intel<=5){
                Toast.makeText(context, "Tu jugador no ha sabido salir del portal de casa, considera hacerlo más inteligente.",Toast.LENGTH_LONG).show();
            }
            else
                Toast.makeText(context, "No has encontrado nada, prueba a explorar durante más tiempo o hacer a tu jugador más inteligente.",Toast.LENGTH_LONG).show();

        }
    }

    private void obtenerBebidaEnergetica(long segundosExplorando) {

        Random gen = new Random();
        int prob = gen.nextInt(100);
        if(prob<10){
            Toast.makeText(context, "Has encontrado super bebida energética XXL (+100 energía)",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia() + 100);


        }
        else if(prob<50){
            Toast.makeText(context, "Has encontrado bebida energética pequeña (+25 energía)",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia() + 25);
        }
        else if(prob<75){
            Toast.makeText(context, "Has encontrado bebida energética mediana (+50 energía)",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia() + 50);
        }
        else {
            Toast.makeText(context, "Has encontrado bebida energética grande (+75 energía)",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia() + 75);
        }
    }

    private void obtenerMonedas(long segundosExplorando) {
        Random gen = new Random();
        int prob = gen.nextInt(100);
        if(prob<10){
            Toast.makeText(context, "Has encontrado 100 monedas",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setMonedas(((Aplicacion)aplicacion).getEstadoJugador().getMonedas() + 100);
        }
        else if(prob<50){
            Toast.makeText(context, "Has encontrado 25 monedas",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setMonedas(((Aplicacion)aplicacion).getEstadoJugador().getMonedas() + 25);
        }
        else if(prob<75){
            Toast.makeText(context, "Has encontrado 50 monedas",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setMonedas(((Aplicacion)aplicacion).getEstadoJugador().getMonedas() + 50);
        }
        else {
            Toast.makeText(context, "Has encontrado 75 monedas",Toast.LENGTH_LONG).show();
            ((Aplicacion)aplicacion).getEstadoJugador().setMonedas(((Aplicacion)aplicacion).getEstadoJugador().getMonedas() + 75);
        }
    }


    private void RestarEnergia(int tareaRealizada, long segundosTarea) {
        Log.d(TAG, "RestarEnergia()");
        if(tareaRealizada==ESTUDIAR)
        {//1h - 20energia
            float aRestar = ( segundosTarea * 20 ) / 3600;
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia()- (int)aRestar);

        }
        else if(tareaRealizada==PESAS)
        {//1h - 50 energia
            float aRestar = ( segundosTarea * 50 ) / 3600;
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia()- (int)aRestar);
        }
        else if(tareaRealizada==EXPLORAR)
        {//1h - 40 energia
            float aRestar = ( segundosTarea * 40 ) / 3600;
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia()- (int)aRestar);
        }
        else if(tareaRealizada==DORMIR)
        { //1 + 25 energia
            float aSumar = ( segundosTarea * 25 ) / 3600;
            ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia()+ (int)aSumar);
        }
    }

    private void SumarExperiencia(int tareaRealizada, long segundosTarea) {
        Log.d(TAG, "SumarExperiencia()");

        if(tareaRealizada==ESTUDIAR)
        {//1h + 4 inteligencia
            float aSumar = (segundosTarea * 6) /3600;

            ((Aplicacion)aplicacion).getEstadoJugador().setInteligencia(((Aplicacion)aplicacion).getEstadoJugador().getInteligencia()+ (int)aSumar);
            Toast.makeText(context, "Finalizaste "+ segundosTarea/60+"min. de estudio. \n Has aumentado "+ (int)aSumar +" ptos de INTELIGENCIA", Toast.LENGTH_LONG).show();
        }
        else if(tareaRealizada==PESAS)
        { //1h +4 fuerza
            float aSumar = (segundosTarea * 6) /3600;

            ((Aplicacion)aplicacion).getEstadoJugador().setFuerza(((Aplicacion)aplicacion).getEstadoJugador().getFuerza()+ (int)aSumar);
            Toast.makeText(context, "Finalizaste "+ segundosTarea/60+"min. de pesas. \n Has aumentado "+ (int)aSumar +" ptos de FUERZA", Toast.LENGTH_LONG).show();
        }
    }

    private void sumarSueldo(long segundosTarea){
        EstadoJugador estadoJugador = (((Aplicacion)aplicacion)).getEstadoJugador();
        int idTrabajo = estadoJugador.getIdTrabajo();
        String[] arraySueldos = getResources().getStringArray(R.array.arrayTrabajosSueldoHora);
        int monedasHora = Integer.parseInt(arraySueldos[idTrabajo]);

        int paga = (int) ((segundosTarea / 3600) * monedasHora);
        if(paga >= 1){
            estadoJugador.setMonedas(estadoJugador.getMonedas()+paga);
            Toast.makeText(context, "Has trabajado "+ segundosTarea/3600+" horas. \n Tu paga es: "+ paga +" MONEDAS", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context, "Debes trabajar al menos 1 hora para recibir tu paga", Toast.LENGTH_LONG).show();
        }

    }

    public void lanzarDialogoBuscarTrabajo(){
        Log.d(TAG, "lanzarDialogoBuscarTrabajo()");
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("Elige un trabajo")
                ;

        final FrameLayout frameView = new FrameLayout(context);
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_dialog_trabajos, frameView);


        TableLayout tableLayout = ((TableLayout)dialoglayout.findViewById(R.id.tablaTrabajos));
        ArrayAdapter<String> adapter;

        final String[] arrayTrabajosNombres = getResources().getStringArray(R.array.arrayTrabajosNombres);
        String[] arrayTrabajosINTEL = getResources().getStringArray(R.array.arrayTrabajosRequisitosINTEL);
        String[] arraySueldos = getResources().getStringArray(R.array.arrayTrabajosSueldoHora);

        final EstadoJugador estadoJugador = (((Aplicacion)aplicacion)).getEstadoJugador();

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,(float)1.0);
        tableLayout.setColumnStretchable(0,true);
        tableLayout.setColumnStretchable(1,true);
        TableRow.LayoutParams rowParamsTrabajo = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,(float)1.0);
        TableRow.LayoutParams rowParamsInt = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,(float)1.0);
        TableRow.LayoutParams rowParamsSalario = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,(float)1.0);
        TableRow.LayoutParams rowParamsAccion = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,(float)1.0);


        View.OnClickListener miListenerClicTrabajo = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d(TAG, "clic en trabajo id: "+v.getId());
                new AlertDialog.Builder(context)
                        .setTitle("Confirmar")
                        .setMessage("¿Quieres trabajar de "+arrayTrabajosNombres[v.getId()]+" ?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Sí", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                estadoJugador.setIdTrabajo(v.getId());
                                alertDialog.dismiss();
                            }})
                        .setNegativeButton("No", null).show();

            }
        };

        TableRow th = new TableRow(context);
        th.setLayoutParams(rowParams);
        th.setGravity(Gravity.CENTER_VERTICAL);
        th.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView tvCab = new TextView(context);
        tvCab.setText("Trabajo");
        tvCab.setTypeface(tvCab.getTypeface(), Typeface.BOLD);
        tvCab.setGravity(Gravity.CENTER_HORIZONTAL);
        tvCab.setLayoutParams(rowParamsTrabajo);
        th.addView(tvCab,0);

        tvCab = new TextView(context);
        tvCab.setText("INT");
        tvCab.setTypeface(tvCab.getTypeface(), Typeface.BOLD);
        tvCab.setGravity(Gravity.LEFT);
        tvCab.setLayoutParams(rowParamsInt);
        th.addView(tvCab,1);

        tvCab = new TextView(context);
        tvCab.setText("$");
        tvCab.setGravity(Gravity.CENTER_HORIZONTAL);
        tvCab.setTypeface(tvCab.getTypeface(), Typeface.BOLD);
        tvCab.setLayoutParams(rowParamsSalario);
        th.addView(tvCab,2);

        tvCab = new TextView(context);
        tvCab.setText("-");
        tvCab.setGravity(Gravity.CENTER_HORIZONTAL);
        tvCab.setTypeface(tvCab.getTypeface(), Typeface.BOLD);
        tvCab.setLayoutParams(rowParamsAccion);
        th.addView(tvCab,3);
        tableLayout.addView(th);

        for(int i=0; i<arrayTrabajosNombres.length; i++) {

            TableRow tableRow = new TableRow(context);
            tableRow.setLayoutParams(tableLayout.getLayoutParams());// TableLayout is the parent view
            tableRow.setGravity(Gravity.CENTER_VERTICAL);

            //create a text view
            TextView tvTrabajo = new TextView(context);
            tvTrabajo.setText(arrayTrabajosNombres[i]);
            tvTrabajo.setLayoutParams(rowParams);
            tableRow.addView(tvTrabajo,0);

            //create a text view
            TextView tvIntel = new TextView(context);
            tvIntel.setText(arrayTrabajosINTEL[i]);
            tvIntel.setLayoutParams(rowParams);
            tableRow.addView(tvIntel,1);

            //create a text view
            TextView tvSalario = new TextView(context);
            tvSalario.setText(arraySueldos[i]+"/h");
            tvSalario.setLayoutParams(rowParams);
            tvSalario.setGravity(Gravity.RIGHT);
            tableRow.addView(tvSalario,2);

            Log.d(TAG, "trabajo: "+arrayTrabajosNombres[i]);

            ImageButton btTrabajo = new ImageButton(context);
            //btTrabajo.setBackgroundColor(Color.alpha(100));
            btTrabajo.setLayoutParams(rowParams);

            if(estadoJugador.getIdTrabajo() == i){
                btTrabajo.setImageDrawable(getResources().getDrawable(R.drawable.ico_selected));
                btTrabajo.setEnabled(false);
            }
            else {

                if (estadoJugador.getInteligencia() >= Integer.parseInt(arrayTrabajosINTEL[i])) {
                    btTrabajo.setImageDrawable(getResources().getDrawable(R.drawable.ico_available));
                    btTrabajo.setId(i);
                    btTrabajo.setOnClickListener(miListenerClicTrabajo);

                } else {
                    btTrabajo.setImageDrawable(getResources().getDrawable(R.drawable.ico_not));
                    btTrabajo.setEnabled(false);
                }
            }


            tableRow.addView(btTrabajo,3);
            tableLayout.addView(tableRow);
        }

        alertDialog.show();


        Button btnOk = (Button)dialoglayout.findViewById(R.id.btnOk);

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                alertDialog.dismiss();
            }
        });


    }

    protected void lanzarDialogoComenzarTarea(final int tareaARealizar) {
        Log.d(TAG, "lanzarDialogoComenzarTarea()");
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("¿"+tareaNombre[tareaARealizar]+"?")
                .setMessage("Indica el tiempo en min. que quieres dedicarle");

        final FrameLayout frameView = new FrameLayout(context);
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_dialog, frameView);
        alertDialog.show();


        Button btnOk = (Button)dialoglayout.findViewById(R.id.btnOk);
        Button btnCancel = (Button)dialoglayout.findViewById(R.id.btnCancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Spinner spinnerTiempo = (Spinner)dialoglayout.findViewById(R.id.spinnerMinutos);
                //int minutos = (int)spinnerTiempo.getSelectedItem();
                int minutos = Integer.parseInt(spinnerTiempo.getSelectedItem().toString());
                long comienzo = System.currentTimeMillis();
                empezarTarea(tareaARealizar,minutos*60,comienzo);
                ((Juego)actividadPadre).guardarDatosTarea(tareaARealizar,minutos*60,comienzo);
               alertDialog.cancel();
               ayuda.compruebaYlanza("ayuda_tareas2");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                alertDialog.cancel();
            }
        });

        ayuda.compruebaYlanza("ayuda_tareas");
    }

    private void lanzarDialogoFinalizarTarea() {
        Log.d(TAG, "lanzarDialogoFinalizarTarea()");
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle("¿Terminar?")
                .setMessage("Terminar "+tareaNombre[tareaEnCurso]);

        final FrameLayout frameView = new FrameLayout(context);
        builder.setView(frameView);

        alertDialogFin = builder.create();
        LayoutInflater inflater = alertDialogFin.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.finalizar_tarea_dialog, frameView);
        tvMonedas =(TextView)dialoglayout.findViewById(R.id.numMonedas);


        Button btnOk = (Button)dialoglayout.findViewById(R.id.btnOk);
        Button btnCancel = (Button)dialoglayout.findViewById(R.id.btnCancel);
        Button btnAcel = (Button)dialoglayout.findViewById(R.id.btnAcel);
        FrameLayout fragLayout = (FrameLayout)dialoglayout.findViewById(R.id.frameBotonAcel);

        long segundosRestantesTarea = (long) ((finTarea - System.currentTimeMillis())/1000.0) * REDUCCION_TIEMPO;
        int minutosRestantes = (int)segundosRestantesTarea/60;
        final int monedasNecesarias = minutosRestantes/3;
        int monedasDisponibles = ((Aplicacion)aplicacion).getEstadoJugador().getMonedas();

        if(tareaEnCurso!=TRABAJAR) {
            if (monedasDisponibles >= monedasNecesarias) {
                tvMonedas.setText("Puedes terminar acelerar la finalización de la tarea por " + monedasNecesarias + " monedas");
                fragLayout.setVisibility(VISIBLE);
            } else {
                tvMonedas.setText("No tienes suficientes monedas para acelerar la tarea. \n ¡Busca un trabajo!");
                fragLayout.setVisibility(INVISIBLE);
            }
        }
        else{//no dejamos que se pague por terminara de trabajar
            fragLayout.setVisibility(INVISIBLE);
        }

        alertDialogFin.show();

        btnOk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                alertDialogFin.cancel();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v)
            {
                finalizarTarea(true);
                alertDialogFin.cancel();
            }
        });

        if(monedasDisponibles-monedasNecesarias>=0) {
            btnAcel.setEnabled(true);
            btnAcel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    ((Aplicacion)aplicacion).getEstadoJugador().setMonedas(((Aplicacion)aplicacion).getEstadoJugador().getMonedas()-monedasNecesarias);
                    soundPool.play(soundIdPago, 1, 1, 3, 0, 1);
                    finalizarTarea(false);
                    alertDialogFin.cancel();
                }
            });
        }
        else{
            btnAcel.setEnabled(false);
        }
    }

    private void lanzarDialogoBorrarMueble(final int idMuebleBorrar){
        final AlertDialog.Builder builder = new AlertDialog.Builder(actividadPadre);
        builder.setMessage("¿Eliminar mueble?")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        if(idMuebleBorrar != idMuebleTareaEnCurso) {
                            desactivarModoEdicion();
                            lgraficosInsertados.remove(idMuebleBorrar);
                        }
                        else
                            Toast.makeText(context,"No se puede borrar un mueble que esté siendo usado", Toast.LENGTH_SHORT).show();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dialogElimMueble = builder.create();
        dialogElimMueble.show();

    }

    private void lanzarDialogoItem(String titulo, String detalle,String nomObjeto, int idObjeto){
        Log.d(TAG, "lanzarDialogoLogro()");
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(titulo)
                .setMessage(detalle)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });



        final FrameLayout frameView = new FrameLayout(context);
        builder.setView(frameView);

        final AlertDialog alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        final View dialoglayout = inflater.inflate(R.layout.custom_dialog_logro, frameView);
        alertDialog.show();

        // Get screen width and height in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        actividadPadre.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // The absolute width of the available display size in pixels.
        int displayWidth = displayMetrics.widthPixels;
        // The absolute height of the available display size in pixels.
        int displayHeight = displayMetrics.heightPixels;

        // Initialize a new window manager layout parameters
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();

        // Copy the alert dialog window attributes to new layout parameter instance
        layoutParams.copyFrom(alertDialog.getWindow().getAttributes());

        // Set the alert dialog window width and height
        // Set alert dialog width equal to screen width 90%
        // int dialogWindowWidth = (int) (displayWidth * 0.9f);
        // Set alert dialog height equal to screen height 90%
        // int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set alert dialog width equal to screen width 50%
        int dialogWindowWidth = (int) (displayWidth * 0.6f);
        // Set alert dialog height equal to screen height 70%
        int dialogWindowHeight = (int) (displayHeight * 0.9f);

        // Set the width and height for the layout parameters
        // This will bet the width and height of alert dialog
        layoutParams.width = dialogWindowWidth;
        layoutParams.height = dialogWindowHeight;

        // Apply the newly created layout parameters to the alert dialog window
         alertDialog.getWindow().setAttributes(layoutParams);


        ImageView imgItem=(ImageView)dialoglayout.findViewById(R.id.imgItem) ;
        imgItem.setImageResource(idObjeto);
        TextView txtNom =(TextView)dialoglayout.findViewById(R.id.txtNomItem);
        txtNom.setText(nomObjeto);




    }

/*Visuales*/
    /*Carga visuales y FX*/
    public void cargarJugador() {
        Log.d(TAG, "cargarJugador()");
        //CARGAR JUGADOR aspecto, habilidades
        ImageView playerAnimado=new ImageView(context);
        playerAnimado.setBackgroundResource(((Aplicacion)aplicacion).getEstadoJugador().getCuerpo());
        drawableJugador = playerAnimado.getBackground();

        drawableJugadorCamina = (((Aplicacion)aplicacion).getEstadoJugador().getDrawCamina());
        drawableJugadorPesas = (((Aplicacion)aplicacion).getEstadoJugador().getDrawPesas());
        drawableEscritorio = (((Aplicacion)aplicacion).getEstadoJugador().getDrawEscritorio());
        drawableCama = (((Aplicacion)aplicacion).getEstadoJugador().getDrawCama());

        drawCamisa = ContextCompat.getDrawable(context, ((Aplicacion)aplicacion).getEstadoJugador().getCamisa());
        drawPantalon = ContextCompat.getDrawable(context,((Aplicacion)aplicacion).getEstadoJugador().getPantalones());
        jugador = new Grafico(this, drawableJugador,drawCamisa, drawPantalon);

        //posicionamos el jugador en el centro de la vista
        jugador.setCenX((int)this.getWidth()/2);
        jugador.setCenY((int)this.getHeight()/2);

        idMuebleTareaEnCurso = (((Aplicacion)aplicacion).getEstadoJugador().getIdGraficoTarea());
        if(caminando)
            startCaminar();
    }

    public void cargarMuebles() {
        Log.d(TAG, "cargarMuebles()");
        lgraficosInsertados = new ArrayList<Grafico>();
        List<Grafico> muebleList=((Aplicacion)aplicacion).getMuebleList();
        //CARGAR MOBILIARIO
        for(int i=0;i<muebleList.size();i++)
        {
            muebleList.get(i).setView(this);
            lgraficosInsertados.add(muebleList.get(i));
        }

        if(idMuebleInsertar >= 0) {
            insertarMueble(idMuebleInsertar);
        }
    }

    private void cargaGUI() {
        Log.d(TAG, "cargaGUI()");
        //CARGARMISCELANEA
        drawEnergia = ContextCompat.getDrawable(context, R.drawable.barra_energia);
        energia = new Grafico(this, drawEnergia);

        drawRayo = ContextCompat.getDrawable(context, R.drawable.rayo);
        rayo = new Grafico (this, drawRayo);

        drawMoneda = ContextCompat.getDrawable(context, R.drawable.moneda);
        moneda = new Grafico (this,drawMoneda);

        drawReloj = ContextCompat.getDrawable(context, R.drawable.clock5);
        reloj = new Grafico(this,drawReloj);

        /*Barra energia*/
        paintBarra= new Paint();
        paintBarra.setStrokeWidth(20);
        paintBarra.setColor(Color.GREEN);
        paintBarra.setStyle(Paint.Style.FILL);
        paintTexto= new Paint();
        paintTexto.setColor(Color.BLACK);
        paintTexto.setTextSize(30);
    }

    private void cargaSonidos() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundIdLogro = soundPool.load(context, R.raw.logro1, 0);
        soundIdMueble = soundPool.load(context, R.raw.mueble, 0);
        soundIdPago =  soundPool.load(context, R.raw.pago, 0);


    }

    public void insertarMueble(int id) {

        ayuda.compruebaYlanza("ayuda_mueble1");
        soundPool.play(soundIdMueble, 1, 1, 3, 0, 1);

        Grafico muebleAInsertar = ((Aplicacion)aplicacion).getMuebleById(id); //clon
        muebleAInsertar.setView(this);

        lgraficosInsertados.add(muebleAInsertar);
        ((Juego)actividadPadre).guardarMuebles();
        modoInsercion=true;

        idMuebleInsertar = lgraficosInsertados.size()-1; //cojo el ultimo insertado

        //lo pongo en el centro (d momento)
        lgraficosInsertados.get(idMuebleInsertar).setCenX(anchoPantalla/2);
        lgraficosInsertados.get(idMuebleInsertar).setCenY(altoPantalla/2);
        //translucido
       // lgraficosInsertados.get(idMuebleInsertar).getDrawable().setAlpha(150);
        lgraficosInsertados.get(idMuebleInsertar).seleccionar();


        hiloComprobarColision = new HiloComprobarColision(idMuebleInsertar);
        hiloComprobarColision.start();

        Log.d(TAG, "insertarMueble() /n idMueble:"+idMuebleInsertar+"/m lgraficosInsertados size: "+ lgraficosInsertados.size());

    }

    /*modif visuales y FX*/
    private void startCaminar() {
        Log.d(TAG, "startCaminar()");
        ImageView playerAnimado=new ImageView(context);
        playerAnimado.setBackgroundResource(drawableJugadorCamina);
        drawableJugador = playerAnimado.getBackground();
        int cenX=jugador.getCenX();
        int cenY=jugador.getCenY();

        jugador = new Grafico(this, drawableJugador,drawCamisa, drawPantalon);
        jugador.setCenX(cenX);
        jugador.setCenY(cenY);

        drawableJugador.setCallback(new Drawable.Callback() {
            @Override
            public void unscheduleDrawable(Drawable who, Runnable what) {
                jugador.getView().removeCallbacks(what);
            }
            @Override
            public void scheduleDrawable(Drawable who, Runnable what, long when) {
                jugador.getView().postDelayed(what, when- SystemClock.uptimeMillis());
            }
            @Override
            public void invalidateDrawable(Drawable who) {
               jugador.getView().postInvalidate();
            }
        });

        if(!((AnimationDrawable)drawableJugador).isRunning())
            ((AnimationDrawable)drawableJugador).start();

        ayuda.compruebaYlanza("ayuda_primerMueble");
    }

    private void stopCaminar(){
        Log.d(TAG, "stopCaminar()");
        drawableJugador = getResources().getDrawable(((Aplicacion)aplicacion).getEstadoJugador().getCuerpo());
        int cenX=jugador.getCenX();
        int cenY=jugador.getCenY();
        jugador = new Grafico(this, drawableJugador,drawCamisa, drawPantalon);
        jugador.setCenX(cenX);
        jugador.setCenY(cenY);

    }

    private void startAnimacionMueble() {
        startSonidoAnimacion();
        Log.d(TAG, "startAnimacionMueble()");
             /*cambios en los graficos*/
         if(idMuebleTareaEnCurso >-1) {
             ImageView imageView = new ImageView(context);

             switch (lgraficosInsertados.get(idMuebleTareaEnCurso).getId())
             {
                 case 2:     imageView.setBackgroundResource(drawableJugadorPesas);
                     break;
                 case 3:    imageView.setBackgroundResource(drawableEscritorio);
                    break;
                 case 4:    imageView.setBackgroundResource(drawableCama);
                    break;
                    default:imageView.setBackgroundResource(lgraficosInsertados.get(idMuebleTareaEnCurso).getIdRecursoDrawAnimacion());
             }

             Drawable drawAnimacion = imageView.getBackground();
             lgraficosInsertados.get(idMuebleTareaEnCurso).setDrawAnimacion(drawAnimacion);

             //int resourceMuebleEstatico = lgraficosInsertados.get(idMuebleTareaEnCurso).getIdRecursoDrawable();
             int cenX = lgraficosInsertados.get(idMuebleTareaEnCurso).getCenX();
             int cenY = lgraficosInsertados.get(idMuebleTareaEnCurso).getCenY();
             jugador.setVisible(false);

             lgraficosInsertados.get(idMuebleTareaEnCurso).animando=true;

             //lgraficosInsertados.get(idMuebleTareaEnCurso).setIdRecursoDrawable(resourceMuebleEstatico);

             lgraficosInsertados.get(idMuebleTareaEnCurso).setCenX(cenX);
             lgraficosInsertados.get(idMuebleTareaEnCurso).setCenY(cenY);


             drawAnimacion.setCallback(new Drawable.Callback() {
                 int nveces= 0;
                 @Override
                 public void unscheduleDrawable(Drawable who, Runnable what) {
                     lgraficosInsertados.get(idMuebleTareaEnCurso).getView().removeCallbacks(what);
                 }
                 @Override
                 public void scheduleDrawable(Drawable who, Runnable what, long when) {
                     lgraficosInsertados.get(idMuebleTareaEnCurso).getView().postDelayed(what, when- SystemClock.uptimeMillis());
                 }

                 @Override
                 public void invalidateDrawable(Drawable who) {
                     lgraficosInsertados.get(idMuebleTareaEnCurso).getView().postInvalidate();
                 }
             });

             if(lgraficosInsertados.get(idMuebleTareaEnCurso).getId()==2 || lgraficosInsertados.get(idMuebleTareaEnCurso).getId()==3)
             ((AnimationDrawable)drawAnimacion).start();
         }
    }

    private void startSonidoAnimacion() {
        mp = new MediaPlayer();

        if(tareaEnCurso==ESTUDIAR) {
            mp= MediaPlayer.create(context, R.raw.keyboardwav);
        }
        else if(tareaEnCurso==PESAS){
            mp=MediaPlayer.create(context, R.raw.pesas);
        }
        else if(tareaEnCurso==DORMIR){
            mp= MediaPlayer.create(context,R.raw.sleep);
        }
        mp.setLooping(true);
        mp.start();
    }

    private void stopAnimacionMueble() {
        Log.d(TAG, "stopAnimacionMueble()");
        lgraficosInsertados.get(idMuebleTareaEnCurso).animando=false;
        if(lgraficosInsertados.get(idMuebleTareaEnCurso).getId()==2 || lgraficosInsertados.get(idMuebleTareaEnCurso).getId()==3)
            ((AnimationDrawable) lgraficosInsertados.get(idMuebleTareaEnCurso).getDrawAnimacion()).stop();
        stopSonidoAnimacion();

    }

    private void stopSonidoAnimacion() {
        //soundPool.stop(soundIdTeclado);

            //   soundPool.play(soundWav, 1, 1, 3, 1, 1);
            if(mp != null && mp.isPlaying())
            mp.stop();
    }

    private void MostrarBarraEnergia(Canvas canvas) {

        canvas.drawRect(inicioBarra , inicioBarraTope,inicioBarra + ((Aplicacion)aplicacion).getEstadoJugador().getEnergia()*2,inicioBarraTope + 16,paintBarra);
        canvas.drawText(Integer.toString(((Aplicacion)aplicacion).getEstadoJugador().getEnergia()),energia.getAncho()+rayo.getAncho(),inicioBarraTope+20,paintTexto);
        canvas.drawText(Integer.toString(((Aplicacion)aplicacion).getEstadoJugador().getMonedas()), moneda.getCenX()+moneda.getAncho()/2,  moneda.getCenY()+moneda.getAlto()/4,paintTexto);
    }

/*Sensores*/

    @Override
    public void onSensorChanged(SensorEvent event) {

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}



/*MEJORA: CAMBIAR POSICIONES GRAFICOS DE int a float*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        final float x = event.getX();
        final float y = event.getY();


        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:


                        if(modoEdicion )
                        {//averiguar que mueble ha pulsado:

                            clicX = (int)x;
                            clicY = (int)y;
                            Log.d("Clic", "x: "+clicX+ ", y: "+clicY);
                            for(Grafico mueble: lgraficosInsertados) {
                                if (mueble.getDrawable().getBounds().contains((int) x, (int) y)) {
                                    if(idMuebleMover!=-1) {//si habia alguno seleccionado
                                        if(!lgraficosInsertados.get(idMuebleMover).colapsando) //si no esta en rojo(colapsando
                                            lgraficosInsertados.get(idMuebleMover).deseleccionar();
                                        else{

                                            return true; //si hay otro en rojo salgo y no hago nada
                                        }
                                    }
                                    idMuebleMover = lgraficosInsertados.indexOf(mueble);
                                    mueble.seleccionar();
                                    hiloComprobarColision = new HiloComprobarColision(idMuebleMover);
                                    hiloComprobarColision.start();
                                }
                            }
                            //calcular offset
                            if(idMuebleMover>-1) {
                                offsetX = clicX - lgraficosInsertados.get(idMuebleMover).getCenX();
                                offsetY = clicY - lgraficosInsertados.get(idMuebleMover).getCenY();
                            }
                        }else if(modoInsercion){
                            clicX = (int)x;
                            clicY = (int)y;
                            Log.d("Clic", "x: "+clicX+ ", y: "+clicY);

                            if(idMuebleInsertar>-1) {
                                offsetX = clicX - lgraficosInsertados.get(idMuebleInsertar).getCenX();
                                offsetY = clicY - lgraficosInsertados.get(idMuebleInsertar).getCenY();
                            }

                        }else{
                            singleTap = true;//cuando pulsamos precargamos el disparo, despues si se desliza ya lo anulamos
                        }

                break;
            case MotionEvent.ACTION_MOVE:

                if(Math.abs(ultimaX-x)<2 && Math.abs(ultimaY-y)<2) //control casero de sensibilidad
                   break;

                int nuevoX = (int)x-offsetX;
                int nuevoY = (int)y-offsetY;

                if(modoEdicion && idMuebleMover > -1) {

                    if(nuevoX>0 && nuevoX<anchoPantalla &&  nuevoY > 0 && nuevoY < altoPantalla ) {
                        lgraficosInsertados.get(idMuebleMover).setCenX(nuevoX);
                        lgraficosInsertados.get(idMuebleMover).setCenY(nuevoY);
                        Log.d(TAG,"x: "+nuevoX+" y:"+nuevoY);

                    }
                    else /*HA arrastrado fuera*/
                    {

                        /* lo devuelvo dentro*/
                        if(nuevoX<=0) {
                            lgraficosInsertados.get(idMuebleMover).setCenX(10);
                            nuevoX=10;
                        }
                        else if(nuevoX>=anchoPantalla){
                            lgraficosInsertados.get(idMuebleMover).setCenX(anchoPantalla-10);
                            nuevoX=anchoPantalla-10;}
                        if(nuevoY<=0){
                            lgraficosInsertados.get(idMuebleMover).setCenY(10);
                            nuevoY=10;}
                        else if(nuevoY >= altoPantalla){
                            lgraficosInsertados.get(idMuebleMover).setCenY(altoPantalla-10);
                            nuevoY=altoPantalla-10;}

                        if(dialogElimMueble==null || (dialogElimMueble!=null &&!dialogElimMueble.isShowing()))
                            lanzarDialogoBorrarMueble(idMuebleMover);

                    }
                }
                if(modoInsercion && idMuebleInsertar > -1){
                    Log.d(TAG,"idmuebleinsertar: "+idMuebleInsertar);
                    if(nuevoX>0 && nuevoX<anchoPantalla &&  nuevoY > 0 && nuevoY < altoPantalla ) {
                        lgraficosInsertados.get(idMuebleInsertar).setCenX(nuevoX);
                        lgraficosInsertados.get(idMuebleInsertar).setCenY(nuevoY);

                    }
                }
                ultimaX = x;
                ultimaY = y;
                break;

            case MotionEvent.ACTION_UP:


                if (singleTap)
                {

                        if (!modoEdicion && !modoInsercion) {
                                boolean clicEnMueble = false;

                                for (Grafico mueble : lgraficosInsertados) {
                                    if (mueble.haSidoPulsado((int) x, (int) y)) {

                                        if (mueble.getNombre().equals("escritorio")) {

                                            if (tareaEnCurso == NINGUNA) {
                                                lanzarDialogoComenzarTarea(ESTUDIAR);
                                                idMuebleTareaEnCurso = lgraficosInsertados.indexOf(mueble);
                                            } else if (tareaEnCurso == ESTUDIAR)
                                                lanzarDialogoFinalizarTarea();
                                            clicEnMueble = true;
                                        } else if (mueble.getNombre().equals("armario")) {
                                            if (tareaEnCurso == NINGUNA) {
                                                Log.d("MILOG", "CLIC Armario");
                                                Intent i = new Intent(context, ArmarioActivity.class);
                                                context.startActivity(i);
                                            }
                                            clicEnMueble = true;
                                        } else if (mueble.getNombre().equals("pesas")) {
                                            if (tareaEnCurso == NINGUNA) {
                                                lanzarDialogoComenzarTarea(PESAS);
                                                idMuebleTareaEnCurso = lgraficosInsertados.indexOf(mueble);
                                            } else if (tareaEnCurso == PESAS)
                                                lanzarDialogoFinalizarTarea();
                                            clicEnMueble = true;
                                        } else if (mueble.getNombre().equals("cama")) {
                                            if (tareaEnCurso == NINGUNA) {
                                                lanzarDialogoComenzarTarea(DORMIR);
                                                idMuebleTareaEnCurso = lgraficosInsertados.indexOf(mueble);
                                            } else if (tareaEnCurso == DORMIR)
                                                lanzarDialogoFinalizarTarea();
                                            clicEnMueble = true;
                                        }

                                    }
                                }
                                if(reloj.haSidoPulsado((int)x,(int)y)) {
                                    lanzarDialogoFinalizarTarea();
                                }
                                if(rayo.haSidoPulsado((int)x,(int)y)){
                                    ((Aplicacion)aplicacion).getEstadoJugador().setEnergia(((Aplicacion)aplicacion).getEstadoJugador().getEnergia()+10) ;
                                }
                                if(moneda.haSidoPulsado((int)x,(int)y)){
                                    ((Aplicacion)aplicacion).getEstadoJugador().setMonedas(((Aplicacion)aplicacion).getEstadoJugador().getMonedas()+10);
                                }
                                if (!clicEnMueble && tareaEnCurso == NINGUNA && !modoEdicion && !modoInsercion)
                                    CaminarHacia((int) x, (int) y);
                        }//!modoedicion
                }//singletap

                break;
        }
        mX = x;//guardamos posiciones actuales
        mY = y;

        return true;

    }


    private void comprobarColisionHilo(int idMueble) {
        long ahora = System.currentTimeMillis();

        if (ultimaComprobacionColision + 500 > ahora)
        {
            return ; // Salir si el período de proceso no se ha cumplido.
        }
        ultimaComprobacionColision=ahora;

        boolean colapsa=false;
        Log.d("HILOCOMP", "compruebo");
        for (int i = 0; i< lgraficosInsertados.size(); i++) {
            if(i!=idMueble) { // me salto el mueble que paso por parametro
                if(!lgraficosInsertados.get(i).isPisable()) {
                    if (lgraficosInsertados.get(idMueble).getDrawable().getBounds().intersect(lgraficosInsertados.get(i).getDrawable().getBounds()))
                        colapsa=true;
                }
            }
        }
        //colision con jugador
        if(jugador.getDrawable().getBounds().intersect(lgraficosInsertados.get(idMueble).getDrawable().getBounds()))
            colapsa=true;

        if(lgraficosInsertados.get(idMueble).getDrawable().getBounds().intersect(rectCentro))
            colapsa=true;

        if(colapsa)
            lgraficosInsertados.get(idMueble).seleccionarRojo();
        else
            getLgraficosInsertados().get(idMueble).seleccionar();
    }

    /*Comprueba colision de un mueble con el resto*/
    public boolean comprobarColision(int idMueble) {

        for (int i = 0; i< lgraficosInsertados.size(); i++) {
            if(i!=idMueble) { // me salto el mueble que paso por parametro
                if(!lgraficosInsertados.get(i).isPisable()) {
                    if (lgraficosInsertados.get(idMueble).getDrawable().getBounds().intersect(lgraficosInsertados.get(i).getDrawable().getBounds()))
                        return true;
                }
            }
        }
        //impido que coloque muebles en el centro
        if(lgraficosInsertados.get(idMueble).getDrawable().getBounds().intersect(rectCentro)){
            return true;
        }
        //colision con jugador
        if(jugador.getDrawable().getBounds().intersect(lgraficosInsertados.get(idMueble).getDrawable().getBounds()))
            return true;
        else
            return false;
    }


    public void activarModoEdicion(){
        Toast.makeText(actividadPadre, "Pulsa sobre un mueble para moverlo", Toast.LENGTH_LONG).show();
        modoEdicion = true;

        ((Juego)(actividadPadre)).seleccionarBoton("edicion");
    }

    public void desactivarModoEdicion(){
        if(hiloComprobarColision!=null && hiloComprobarColision.isAlive())
        hiloComprobarColision.interrupt();

        if(idMuebleMover != -1) { // si ha seleccionado algun mueble para mover

            if(!comprobarColision(idMuebleMover)) //si no solapa con ninguno...
               lgraficosInsertados.get(idMuebleMover).deseleccionar();
            else {
                Toast.makeText(actividadPadre, "No se puede colocar ahí",Toast.LENGTH_SHORT).show();
                return; //si solapa con alguno lo quito
            }
            idMuebleMover = -1;
        }

        modoEdicion=false;
        ((Juego)(actividadPadre)).guardarMuebles();
        ((Juego)(actividadPadre)).deseleccionarBoton("edicion");
    }
    public void activarModoInsercion(){
        Log.d("MILOG", "activar modo insercion");
        Intent i = new Intent(context, MueblesActivity.class);
        actividadPadre.startActivityForResult(i, Juego.REQ_ANYADE_MUEBLE);
    }
    public void desactivarModoInsercion(){
        if (!comprobarColision(idMuebleInsertar)) {
            ((Juego)actividadPadre).deseleccionarBoton("insercion");
            modoInsercion = false;
            lgraficosInsertados.get(idMuebleInsertar).deseleccionar();

            ayuda.compruebaYlanza("ayuda_mueble_id"+ lgraficosInsertados.get(idMuebleInsertar).getId());
            hiloComprobarColision.interrupt();
            idMuebleInsertar = -1;

            soundPool.play(soundIdMueble, 1, 1, 3, 0, 1);
            ((Juego)actividadPadre).guardarMuebles();

        } else {
            Toast.makeText(context, "No se puede colocar ahí", Toast.LENGTH_SHORT).show();
        }
    }


    /*viene de actv Juego*/
    public void botonLupaClick(View v) {
        if(!modoEdicion && !modoInsercion) {
            if (tareaEnCurso == NINGUNA) {
                lanzarDialogoComenzarTarea(EXPLORAR);
                ayuda.compruebaYlanza("ayuda_explorar");
            } else if (tareaEnCurso == EXPLORAR)
                lanzarDialogoFinalizarTarea();
        }
    }
    public void botonBusquedaTrabajoClick(View v){
        if(!modoEdicion && !modoInsercion) {
            if (tareaEnCurso == NINGUNA) {
                lanzarDialogoBuscarTrabajo();
                //ayuda.compruebaYlanza("ayuda_explorar");
            }
        }
    }
    public void botonTrabajarClick(View v){
        EstadoJugador estadoJugador = (((Aplicacion)aplicacion)).getEstadoJugador();
        if(estadoJugador.getIdTrabajo() > -1) {
            if (!modoEdicion && !modoInsercion) {
                if (tareaEnCurso == NINGUNA) {
                    lanzarDialogoComenzarTarea(TRABAJAR);
                    //ayuda.compruebaYlanza("ayuda_explorar");
                } else if (tareaEnCurso == TRABAJAR)
                    lanzarDialogoFinalizarTarea();
            }
        }
        else{
            new AlertDialog.Builder(context)
                    .setTitle("Ir a trabajar")
                    .setMessage("Primero debes conseguir un trabajo")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setNegativeButton("Ok", null).show();
        }
    }


/*Getters y Setters*/

    public List<Grafico> getLgraficosInsertados() {
        return lgraficosInsertados;
    }

    public void setIdMuebleInsertar(int idMuebleInsertar) {
        this.idMuebleInsertar = idMuebleInsertar;
    }

    public void setPadre(Activity actividadPadre) {
        Log.d(TAG, "setPadre()");
        this.actividadPadre=actividadPadre;
    }
    public void setAplicacion(Application aplicacion) {
        Log.d(TAG, "setAplicacion()");
        this.aplicacion = aplicacion;
    }

    public Thread getHiloComprobarColision() {
        return hiloComprobarColision;
    }

    public MediaPlayer getMp() {
        return mp;
    }
}
