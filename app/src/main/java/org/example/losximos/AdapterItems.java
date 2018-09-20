package org.example.losximos;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by pistoleta on 3/12/17.
 */

public class AdapterItems extends RecyclerView.Adapter<AdapterItems.ViewHolder> {
    private final Context context;
    private final List<Item> items;
    private final ColorMatrix matrix;
    private final ColorMatrixColorFilter filter;
    private final EstadoJugador estadoJugador;
    private final LayoutInflater inflador;
    private View.OnClickListener onClickListener;


    public AdapterItems(Context context, List<Item> items)
    {
        this.context=context;
        this.items=items;
        inflador = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        matrix = new ColorMatrix();
        matrix.setSaturation(0);
        filter = new ColorMatrixColorFilter(matrix);
        estadoJugador = ((Aplicacion)context.getApplicationContext()).getEstadoJugador();

    }

    @Override
    public AdapterItems.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.layout_item, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterItems.ViewHolder holder, int i) {

        holder.nombre.setText(items.get(i).getNombre());
        holder.imagen.setImageResource(items.get(i).getImgResource());
        //holder.precio.setText(Integer.toString(items.get(i).getNivel())+"€");

        if(items.get(i).getTipo()!=Item.CUERPO) {
            if (!estadoJugador.getListaIdsItems().contains(items.get(i).getCodigo())) {   //si no tengo el item muestro el precio
                holder.precio.setText(Integer.toString(items.get(i).getNivel()) + "€");//(precio)
                holder.precio.setAlpha((float) 0.5);
                holder.imagen.setAlpha((float) 0.5);
                holder.nombre.setAlpha((float) 0.5);

            } else {//lo tengo
                holder.nombre.setAlpha(1);
                holder.precio.setAlpha(1);
                holder.precio.setVisibility(View.INVISIBLE);
                holder.imagen.setImageResource(items.get(i).getImgResource());
                holder.imagen.setAlpha((float) 1);
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    @Override
    public long getItemId(int position) {
        return this.items.get(position).getCodigo();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre, precio;
        public ImageView imagen;


        ViewHolder(View itemView) {
            super(itemView);
            nombre = (TextView) itemView.findViewById(R.id.nomItem);
            precio = (TextView) itemView.findViewById(R.id.precioItem);
            imagen = (ImageView) itemView.findViewById(R.id.imgItem);

        }
    }
}
