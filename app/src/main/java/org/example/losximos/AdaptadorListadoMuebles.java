package org.example.losximos;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by pistoleta on 7/12/17.
 */

class AdaptadorListadoMuebles extends RecyclerView.Adapter<AdaptadorListadoMuebles.ViewHolder> {

    private LayoutInflater inflador;
    private List<Grafico> lista;
    protected View.OnClickListener onClickListener;
    private EstadoJugador estadoJugador;
    ColorMatrix matrix;
    ColorMatrixColorFilter filter;

    public AdaptadorListadoMuebles(Context context, List<Grafico> lista) {
        this.lista = lista;
        inflador = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        estadoJugador = ((Aplicacion)context.getApplicationContext()).getEstadoJugador();

       /* matrix = new ColorMatrix();
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);*/

    }

    @Override
    public AdaptadorListadoMuebles.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.mueble_lista, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(AdaptadorListadoMuebles.ViewHolder holder, int i) {


        holder.nomMueble.setText(lista.get(i).getNombre());
        holder.imgMueble.setImageResource(lista.get(i).getIdRecursoDrawable());
        holder.precio.setText(Integer.toString(lista.get(i).getPrecio())+"€");


        if(lista.get(i).isInteractivo()){
           holder.imgRueda.setVisibility(View.VISIBLE);
        }
        else
            holder.imgRueda.setVisibility(View.GONE);
       // holder.imgMueble.setImageDrawable(lista.get(i).getDrawable());

        if(lista.get(i).getPrecio()>estadoJugador.getMonedas())
        {
            holder.imgMueble.setAlpha((float)0.4);
        }
        else{
            holder.imgMueble.setAlpha((float)1);
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

    public TextView nomMueble, precio;
    public ImageView imgMueble,imgRueda;


    ViewHolder(View itemView) {
        super(itemView);
        nomMueble = (TextView) itemView.findViewById(R.id.txtNomMueble);
        precio = (TextView) itemView.findViewById(R.id.txtPrecio);
        imgMueble = (ImageView) itemView.findViewById(R.id.imgMueble);
        imgRueda = (ImageView)itemView.findViewById(R.id.imgRueda);
    }

}
}
