package org.example.losximos;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ArmarioActivity extends Activity {

    ImageView imgCamisa;
    ImageView imgPantalon;
    ImageView imgArma;
    private Application aplicacion;
    private List<Item> todosItemsRopa = new ArrayList<Item>();
    private List<Item> todosItemsArmas = new ArrayList<Item>();
    private List<Item> itemsCuerpo;

    private int nivelArmaJugador;
    private Button btnRopa;
    private Button btnArmas;
    private Button btnCuerpo;

    private EstadoJugador estadoJugador;
    final Context context = this;
    private RecyclerView recyclerView;
    private AdapterItems itemsAdapter;
    private GridLayoutManager layoutManager;
    private ImageView imgCuerpo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armario);
        aplicacion = (Aplicacion)getApplication();

        nivelArmaJugador = ((Aplicacion)aplicacion).getEstadoJugador().getNivelArmaDescubierta();
        todosItemsRopa = ((Aplicacion)aplicacion).getRopaList();
        todosItemsArmas= ((Aplicacion)aplicacion).getArmasList();
        itemsCuerpo = ((Aplicacion)aplicacion).getCuerposList();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);

        imgCuerpo = (ImageView)findViewById(R.id.imgCuerpo);
        imgCamisa = (ImageView)findViewById(R.id.imgCamisa);
        imgPantalon=(ImageView)findViewById(R.id.imgPantalon);
        imgArma=    (ImageView)findViewById(R.id.imgArma);

        imgCuerpo.setImageResource(((Aplicacion)getApplication()).getEstadoJugador().getCuerpo());
        imgCuerpo.setTag(((Aplicacion)aplicacion).getEstadoJugador().getCuerpo());

        imgCamisa.setImageResource(((Aplicacion)aplicacion).getEstadoJugador().getCamisa());
        imgCamisa.setTag(((Aplicacion)aplicacion).getEstadoJugador().getCamisa());

        imgPantalon.setImageResource(((Aplicacion)aplicacion).getEstadoJugador().getPantalones());
        imgPantalon.setTag((((Aplicacion)aplicacion)).getEstadoJugador().getPantalones());

        imgArma.setImageResource(((Aplicacion)aplicacion).getEstadoJugador().getArma());
        imgArma.setTag(((Aplicacion)aplicacion).getEstadoJugador().getArma());

        inicializarBotones();
        apagarBotones();

        filtraRopaClick(null);
    }

    private void inicializarBotones() {
        btnRopa = (Button)findViewById(R.id.btnFiltraRopa);
        btnArmas=(Button)findViewById(R.id.btnFiltraArmas);
        btnCuerpo = (Button)findViewById(R.id.btnFiltraCuerpos);
    }

    private void apagarBotones() {
        btnRopa.getBackground().clearColorFilter();
        btnArmas.getBackground().clearColorFilter();
        btnCuerpo.getBackground().clearColorFilter();
    }

    public void filtraCuerposClick(View view){
        this.setTitle("Cuerpo");
        apagarBotones();
        btnCuerpo.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY); //pngo boton en verde

        itemsAdapter = new AdapterItems(this, itemsCuerpo);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);
        Log.d("ARMARIOACTV", "todosItmsCuerpo size: " + itemsCuerpo.size());

        recyclerView.setAdapter(itemsAdapter);
        itemsAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                final Item item = itemsCuerpo.get(pos);
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();
                imgCuerpo.setImageResource(item.getImgResource());
                imgCuerpo.setTag(item.getImgResource());
            }
        });
    }

    public void filtraArmasClick(View view){

        this.setTitle("Armas");
        apagarBotones();
        btnArmas.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY); //pngo boton en verde

        itemsAdapter = new AdapterItems(this, todosItemsArmas);
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);
        Log.d("ARMARIOACTV", "todosItemsArmas size: " + todosItemsArmas.size());

        recyclerView.setAdapter(itemsAdapter);
        itemsAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                final Item item = todosItemsArmas.get(pos);



                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();

                if(!estadoJugador.getListaIdsItems().contains(item.getCodigo()))
                {//si no lo tengo
                    if(estadoJugador.getMonedas() >= item.getNivel())
                    {//si tengo sufi dinero => comprar
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("Comprar");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("¿Comprar item?")
                                .setCancelable(false)
                                .setPositiveButton("Sí",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        estadoJugador.getListaIdsItems().add(item.getCodigo());                 //añado item a mi lista de items
                                        estadoJugador.setMonedas(estadoJugador.getMonedas()-item.getNivel()); //resto el precio
                                        ((Aplicacion)getApplication()).getAlmacenEstado().guardarEstadoJugador(((Aplicacion)getApplication()).getEstadoJugador());
                                        filtraArmasClick(null);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    else{//sino sufi dinero
                        /* nada*/
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("Comprar");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("No tienes suficiente dinero para comprar esto.")
                                .setCancelable(false)
                                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                else{// si lo tengo me lo pruebo
                    if(item.getTipo()== Item.ARMA){
                        imgArma.setImageResource(item.getImgResource());
                        imgArma.setTag(item.getImgResource());
                    }
                }

            }
        });
    }

    public void filtraRopaClick(View view){
        this.setTitle("Ropa");
        apagarBotones();
        btnRopa.getBackground().setColorFilter(0xff00ff00,PorterDuff.Mode.MULTIPLY); //pngo boton en verde

        itemsAdapter = new AdapterItems(this, todosItemsRopa);
        Log.d("ARMARIOACTV", "todosItemsRopa size: " + todosItemsRopa.size());
        layoutManager = new GridLayoutManager(this, 4,1,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(itemsAdapter);
        itemsAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                int pos = recyclerView.getChildAdapterPosition(v);
                final Item item = todosItemsRopa.get(pos);
                Log.d(TAG, "clic en item id: "+item.getCodigo());
                estadoJugador = ((Aplicacion)getApplicationContext()).getEstadoJugador();

                if(!estadoJugador.getListaIdsItems().contains(item.getCodigo()))
                {//si no lo tengo
                    if(estadoJugador.getMonedas() >= item.getNivel())
                    {//si tengo sufi dinero => comprar
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("Comprar");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("¿Comprar item?")
                                .setCancelable(false)
                                .setPositiveButton("Sí",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        estadoJugador.getListaIdsItems().add(item.getCodigo());                 //añado item a mi lista de items
                                        estadoJugador.setMonedas(estadoJugador.getMonedas()-item.getNivel()); //resto el precio
                                        ((Aplicacion)getApplication()).getAlmacenEstado().guardarEstadoJugador(((Aplicacion)getApplication()).getEstadoJugador());
                                        filtraRopaClick(null);
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                    else{//sino sufi dinero
                        /* nada*/
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                context);
                        alertDialogBuilder.setTitle("Comprar");
                        // set dialog message
                        alertDialogBuilder
                                .setMessage("No tienes suficiente dinero para comprar esto.")
                                .setCancelable(false)
                                .setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
                else{// si lo tengo me lo pruebo
                    if (item.getTipo() == Item.CAMISA) { //me pruebo la camisa
                        imgCamisa.setImageResource(item.getImgResource());
                        imgCamisa.setTag(item.getImgResource());
                    } else if (item.getTipo() == Item.PANTALON) {//me pruebo el pantalon
                        imgPantalon.setImageResource(item.getImgResource());
                        imgPantalon.setTag(item.getImgResource());
                    }
                }

            }
        });
    }

    public void guardarCambiosClick(View view)
    {
       //VistaJuego.estadoJugador.setCuerpo();
        ((Aplicacion)aplicacion).getEstadoJugador().setCamisa((int)imgCamisa.getTag());
        ((Aplicacion)aplicacion).getEstadoJugador().setPantalones((int)imgPantalon.getTag());
        ((Aplicacion)aplicacion).getEstadoJugador().setArma((int)imgArma.getTag());
        ((Aplicacion)aplicacion).getEstadoJugador().setCuerpo((int)imgCuerpo.getTag());
        String nombreRecurso = getResources().getResourceEntryName((int)imgCuerpo.getTag());
        Log.d(TAG, "nombrerecurso "+nombreRecurso);
        String prefijoPlayer = nombreRecurso.substring(0,3);
        Log.d(TAG, "prefijoplayer "+prefijoPlayer);
        int idDrawableCaminar= getResources().getIdentifier(prefijoPlayer+"_camina_frente_drawable", "drawable",  getApplicationContext().getPackageName());
        Log.d(TAG, "idDrawabbleCaminar "+idDrawableCaminar);
        ((Aplicacion)aplicacion).getEstadoJugador().setDrawCamina(idDrawableCaminar);
        int idDrawablePesas= getResources().getIdentifier(prefijoPlayer+"_pesas_anim", "drawable",  getApplicationContext().getPackageName());
        ((Aplicacion)aplicacion).getEstadoJugador().setDrawPesas(idDrawablePesas);
        int idDrawableCama =  getResources().getIdentifier(prefijoPlayer+"_dormir", "drawable",  getApplicationContext().getPackageName());
        ((Aplicacion)aplicacion).getEstadoJugador().setDrawCama(idDrawableCama);
        int idEscritorio =  getResources().getIdentifier(prefijoPlayer+"_escritorio_anim", "drawable",  getApplicationContext().getPackageName());
        ((Aplicacion)aplicacion).getEstadoJugador().setDrawEscritorio(idEscritorio);
        ((Aplicacion)getApplication()).getAlmacenEstado().guardarEstadoJugador(((Aplicacion)getApplication()).getEstadoJugador());
        this.finish();
    }
}
