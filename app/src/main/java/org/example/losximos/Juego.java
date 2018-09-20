package org.example.losximos;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by pistoleta on 29/11/17.
 */

public class Juego  extends Activity {

    public static final int REQ_ANYADE_MUEBLE = 0;
    private static final int REQ_AJUSTES = 1;
    private VistaJuego vistaJuego;
    final String TAG = "Juego";
    public static Ayuda ayuda;
    private Button btnMoverMueble;
    private Button btnCasa;
    private Button btnLupa;

    @Override
    public void onCreate(Bundle s){
        super.onCreate(s);
        ayuda = new Ayuda(this);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.juego);

        vistaJuego = (VistaJuego)findViewById(R.id.VistaJuego);
        vistaJuego.setAplicacion((Aplicacion)getApplication());
        vistaJuego.setPadre(this);

        btnMoverMueble = (Button)findViewById(R.id.btnTools);
        btnCasa =(Button)findViewById(R.id.btnCasa);
        btnLupa =(Button)findViewById(R.id.btnLupa);

    }


    @Override protected void onPause()
    {
        Log.d("Juego", "onPause()");
        //guardo en persistencia
        guardarJugador();
        canceloHilosEnEjecucion();

        eliminoMueblesPorColocar();
        guardarMuebles();
        if(vistaJuego.getMp()!=null && vistaJuego.getMp().isPlaying())
            vistaJuego.getMp().pause();
        //deselecciono botones laterales
        ((Button)findViewById(R.id.btnTools)).getBackground().clearColorFilter();
        ((Button)findViewById(R.id.btnCasa)).getBackground().clearColorFilter();

        super.onPause();
    }


    @Override protected void onResume()
    {
        super.onResume();
        Log.d("Juego", "onResume()");
        //cargo de persistencia
        ((Aplicacion)getApplication()).setEstadoJugador(((Aplicacion)getApplication()).getAlmacenEstado().obtenerEstadoJugador());
        ((Aplicacion)getApplication()).setMuebleList((((Aplicacion) getApplication()).getAlmacenEstado().obtenerListaMuebles()));

        //refresh jugador y muebles  NECESARIO?
        vistaJuego.cargarJugador();
        vistaJuego.cargarMuebles();
        cargarHabilidadesYvistaXimo();

        //si hay tareas en curso las cargo
        if (((Aplicacion)getApplication()).getEstadoJugador().getTareaEnCurso() != 0){
            vistaJuego.reanudarTarea();

        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Juego", "OnActivityResult() reqCode:"+requestCode+" resultCode:"+resultCode);
        EstadoJugador estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();

        //inserta mueble
        if (requestCode == REQ_ANYADE_MUEBLE && resultCode == Activity.RESULT_OK) {

            int idMueble = data.getIntExtra("id",0);
            int precio = data.getIntExtra("precio",0);
            estadoJugador.setMonedas(estadoJugador.getMonedas()-precio);
            guardarJugador();
            vistaJuego.setIdMuebleInsertar(idMueble);
            vistaJuego.modoInsercion=true;
            Button btnCasa= (Button)findViewById(R.id.btnCasa);
            btnCasa.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);

        }
        else if(requestCode ==REQ_AJUSTES && resultCode==Activity.RESULT_OK){
            String opc = data.getStringExtra("opc");
            if(opc.equals("salir")){
                this.finish();
            }
        }
    }//onActivityResult
    @Override protected void onDestroy()
    {
        Log.d("Juego", "onDestroy()");
        super.onDestroy();
    }

    private void canceloHilosEnEjecucion() {
        //Cancelar hilos si existen
        if(vistaJuego.miTarea!= null && !vistaJuego.miTarea.isCancelled())
            vistaJuego.miTarea.cancel(true);
        if(vistaJuego.getHiloComprobarColision()!= null && !vistaJuego.getHiloComprobarColision().isInterrupted())
            vistaJuego.getHiloComprobarColision().interrupt();
    }

    private void eliminoMueblesPorColocar() {
        //si se estaba insertando un mueble y no se ha terminado se elimina
        if(vistaJuego.idMuebleInsertar > -1)
            vistaJuego.getLgraficosInsertados().remove(vistaJuego.getLgraficosInsertados().size()-1);
        if(vistaJuego.idMuebleMover > -1)
            vistaJuego.getLgraficosInsertados().remove(vistaJuego.idMuebleMover);
    }

    public void guardarMuebles() {
        Log.d(TAG,"guardarMuebles()");
        ((Aplicacion)getApplication()).getAlmacenEstado().guardarMuebles(vistaJuego.getLgraficosInsertados());
    }

    private void guardarJugador(){
        ((Aplicacion)getApplication()).getAlmacenEstado().guardarEstadoJugador(((Aplicacion)getApplication()).getEstadoJugador());
    }

    public void guardarDatosTarea(int tarea, int segundos, long comienzo) {
        Log.d(TAG, "guardarDatosTarea()");
        /*guardo tarea en curso en estadoJugador*/
        ((Aplicacion)getApplication()).getEstadoJugador().setTareaEnCurso(tarea);
        ((Aplicacion)getApplication()).getEstadoJugador().setSegundosTarea(segundos);
        ((Aplicacion)getApplication()).getEstadoJugador().setComienzoTarea(comienzo);
        ((Aplicacion)getApplication()).getEstadoJugador().setIdGraficoTarea(vistaJuego.idMuebleTareaEnCurso);
        /*Guardo estado jugador en persistencia*/
        guardarJugador();
    }

    public void cargarHabilidadesYvistaXimo() {

        TextView txtInteligencia= (TextView)findViewById(R.id.txtIntel);
        txtInteligencia.setText("INT:"+((Aplicacion)getApplication()).getEstadoJugador().getInteligencia());
        TextView txtFuerza = (TextView)findViewById(R.id.txtFue);
        txtFuerza.setText("FUE:"+((Aplicacion)getApplication()).getEstadoJugador().getFuerza());
        TextView txtAgilidad= (TextView)findViewById(R.id.txtAgi);
        txtAgilidad.setText(("AGI:"+((Aplicacion)getApplication()).getEstadoJugador().getAgilidad()));

        //ROPA y Accesorios
        ImageView imgCamisa = (ImageView)findViewById(R.id.imgCamisa);
        ImageView imgPantalon=(ImageView)findViewById(R.id.imgPantalon);
        ImageView imgArma = (ImageView)findViewById(R.id.imgArma);
        ImageView imgCuerpo = (ImageView)findViewById(R.id.imgCuerpo);

        imgCamisa.setImageResource(((Aplicacion)getApplication()).getEstadoJugador().getCamisa());
        imgPantalon.setImageResource(((Aplicacion)getApplication()).getEstadoJugador().getPantalones());
        imgArma.setImageResource(((Aplicacion)getApplication()).getEstadoJugador().getArma());
        imgCuerpo.setImageResource((((Aplicacion)getApplication()).getEstadoJugador().getCuerpo()));


    }

    /*RELOJ TAREA*/
    public void actualizaRelojTarea(final String timeLeft){
        final TextView txtReloj= (TextView)findViewById(R.id.txtReloj);
        txtReloj.setText(timeLeft);
    }

    public void ocultarRelojTarea() {
        final TextView txtReloj= (TextView)findViewById(R.id.txtReloj);
        txtReloj.setVisibility(View.INVISIBLE);
    }

    public void mostrarRelojTarea() {
        final TextView txtReloj= (TextView)findViewById(R.id.txtReloj);
        txtReloj.setVisibility(View.VISIBLE);
    }

    /*BOTONES LATERALES*/

    public void goToWorkClic(View v){
        Log.d("MILOG", "CLIC goToWork");
        vistaJuego.botonTrabajarClick(v);
    }

    public void jobSearchClic(View v){
        Log.d("MILOG", "CLIC JobSearch");
        vistaJuego.botonBusquedaTrabajoClick(v);
    }

    public void estadoJugadorClick(View v) {
        Log.d("MILOG", "CLIC Jugador");
        Intent i = new Intent(this, EstadoJugadorActivity.class);
        startActivity(i);
    }

    public void casaClic(View view){
        if(vistaJuego.modoEdicion) return;

            if (vistaJuego.idMuebleInsertar == -1) {
                vistaJuego.activarModoInsercion();

            } else {
                vistaJuego.desactivarModoInsercion();
            }
    }

    public void lanzaAjustesClick(View v){
        Log.d("MILOG", "CLIC casa");
        Intent i = new Intent(this, AjustesActivity.class);
        startActivityForResult(i, Juego.REQ_AJUSTES);
    }

    public void seleccionarBoton(String boton){
        if(boton=="edicion")
            btnMoverMueble.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
        else if(boton =="insercion")
            btnCasa.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
        else if (boton=="lupa")
            btnLupa.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);
    }

    public void deseleccionarBoton(String boton){
        if(boton=="edicion")
            btnMoverMueble.getBackground().clearColorFilter();
        else if(boton =="insercion")
            btnCasa.getBackground().clearColorFilter();
        else if(boton =="lupa")
            btnLupa.getBackground().clearColorFilter();
    }

    public void mueveMuebleClic(View view) {
        Log.d("MILOG", "CLIC moverMueble");
        if(vistaJuego.modoInsercion) return;

        if(vistaJuego.modoEdicion)
            vistaJuego.desactivarModoEdicion();

        else
            vistaJuego.activarModoEdicion();
    }

    public void lupaClic(View view) {
        vistaJuego.botonLupaClick(view);
    }

}
