package org.example.losximos;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MueblesActivity extends Activity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AdaptadorListadoMuebles adaptador;
    private List<Grafico> listaMueblesCompleta;
    private List<Grafico> listaMueblesInteractivos;
    private List<Grafico> listaMuebles;
    private List<Grafico> listaElectro;
    private List<Grafico> listaBanyo;
    private List<Grafico> listaDeco;
    private List<Grafico> listaPared;
    private List<Grafico> listaSuelo;
    private Button btnInteractivos;
    private Button btnMuebles;
    private Button btnElectrodom;
    private Button btnBanyo;
    private Button btnDeco;
    private Button btnPared;
    private Button btnSuelo;
    EstadoJugador estadoJugador;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muebles);
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        listaMueblesCompleta =  ((Aplicacion)getApplicationContext()).getMuebleListCompleta();
        inicializarListas();
        inicializarBotones();
        apagarBotones();
        this.setTitle("Todos los muebles");
        adaptador = new AdaptadorListadoMuebles(this,listaMueblesCompleta);
        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);



        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                Log.d("TAG", "Mueble seleccionadooor "+pos);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaMueblesCompleta.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaMueblesCompleta.get(pos).getId());
                    returnIntent.putExtra("precio",listaMueblesCompleta.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });

    }

    private void inicializarListas() {

        listaMueblesInteractivos = new ArrayList<>();
        listaMuebles = new ArrayList<>();
        listaElectro = new ArrayList<>();
        listaBanyo = new ArrayList<>();
        listaDeco = new ArrayList<>();
        listaPared = new ArrayList<>();
        listaSuelo = new ArrayList<>();
        for(int i =0 ; i < listaMueblesCompleta.size() ; i++) {
            if (listaMueblesCompleta.get(i).isInteractivo())
                listaMueblesInteractivos.add(listaMueblesCompleta.get(i));
            else if (listaMueblesCompleta.get(i).getTipo().equals("mueble"))
                listaMuebles.add(listaMueblesCompleta.get(i));
            else if (listaMueblesCompleta.get(i).getTipo().equals("electrodomestico"))
                listaElectro.add(listaMueblesCompleta.get(i));
            else if (listaMueblesCompleta.get(i).getTipo().equals("banyo"))
                listaBanyo.add(listaMueblesCompleta.get(i));
            else if (listaMueblesCompleta.get(i).getTipo().equals("deco"))
                listaDeco.add(listaMueblesCompleta.get(i));
            else if (listaMueblesCompleta.get(i).getTipo().equals("paredes"))
                listaPared.add(listaMueblesCompleta.get(i));
            else if (listaMueblesCompleta.get(i).getTipo().equals("suelo"))
                listaSuelo.add(listaMueblesCompleta.get(i));
        }
    }

    private void inicializarBotones() {
        btnInteractivos = (Button)findViewById(R.id.btnInteractivos);
        btnMuebles=(Button)findViewById(R.id.btnMuebles);
        btnElectrodom=(Button)findViewById(R.id.btnElectrodomesticos);
        btnBanyo=(Button)findViewById(R.id.btnBanyo);
        btnDeco = (Button)findViewById(R.id.btnDeco);
        btnPared=(Button)findViewById(R.id.btnParedes);
        btnSuelo=(Button)findViewById(R.id.btnSuelo);
    }

    public void filtrarInteractivos(View view){
        this.setTitle("Objetos interactivos");
        apagarBotones();
        btnInteractivos.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaMueblesInteractivos);

        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaMueblesInteractivos.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaMueblesInteractivos.get(pos).getId());
                    returnIntent.putExtra("precio",listaMueblesInteractivos.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void filtrarTodos(View view){
        this.setTitle("Todo");
        adaptador = new AdaptadorListadoMuebles(this,listaMueblesCompleta);

        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                //String s = listamuebles.getpo
                Log.d("TAG", "Mueble seleccionado "+pos);
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id", listaMueblesCompleta.get(pos).getId());
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });
    }


    public void filtrarMuebles(View view) {
        this.setTitle("Muebles");
        apagarBotones();
        btnMuebles.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaMuebles);

        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaMuebles.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaMuebles.get(pos).getId());
                    returnIntent.putExtra("precio",listaMuebles.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void filtrarElectrodomesticos(View view) {
        this.setTitle("Electrodomésticos");
        apagarBotones();
        btnElectrodom.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaElectro);

        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaElectro.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaElectro.get(pos).getId());
                    returnIntent.putExtra("precio",listaElectro.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void filtrarBanyo(View view) {
        this.setTitle("Baño");
        apagarBotones();
        btnBanyo.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaBanyo);
        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaBanyo.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaBanyo.get(pos).getId());
                    returnIntent.putExtra("precio",listaBanyo.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void filtrarDecoracion(View view) {
        this.setTitle("Decoración");
        apagarBotones();
        btnDeco.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaDeco);
        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaDeco.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaDeco.get(pos).getId());
                    returnIntent.putExtra("precio",listaDeco.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void filtrarParedes(View view) {
        this.setTitle("Puertas y paredes");
        apagarBotones();
        btnPared.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaPared);

        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaPared.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaPared.get(pos).getId());
                    returnIntent.putExtra("precio",listaPared.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }

    public void filtrarSuelo(View view) {
        this.setTitle("Suelo y alfombras");
        apagarBotones();
        btnSuelo.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde
        adaptador = new AdaptadorListadoMuebles(this,listaSuelo);

        recyclerView.setAdapter(adaptador);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);

        adaptador.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                if(listaSuelo.get(pos).getPrecio() <= estadoJugador.getMonedas()) {
                    Log.d("TAG", "Mueble seleccionado " + pos);
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("id", listaSuelo.get(pos).getId());
                    returnIntent.putExtra("precio",listaSuelo.get(pos).getPrecio());
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }
            }
        });
    }
    private void apagarBotones() {
        btnInteractivos.getBackground().clearColorFilter();
        btnMuebles.getBackground().clearColorFilter();
        btnElectrodom.getBackground().clearColorFilter();
        btnBanyo.getBackground().clearColorFilter();
        btnDeco.getBackground().clearColorFilter();
        btnPared.getBackground().clearColorFilter();
        btnSuelo.getBackground().clearColorFilter();
    }
}
