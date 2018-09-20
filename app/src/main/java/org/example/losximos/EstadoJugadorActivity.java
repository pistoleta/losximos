package org.example.losximos;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;



public class EstadoJugadorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vista_estado_jugador);

        TextView txtIntel = (TextView)findViewById(R.id.txtInteligencia);
        txtIntel.setText(Integer.toString(((Aplicacion)getApplication()).getEstadoJugador().getInteligencia()));

        TextView txtEnergia=(TextView)findViewById(R.id.txtEnergia);
        txtEnergia.setText(Integer.toString(((Aplicacion)getApplication()).getEstadoJugador().getEnergia()));

        TextView txtFuerza =(TextView)findViewById(R.id.txtFuerza);
        txtFuerza.setText(Integer.toString(((Aplicacion)getApplication()).getEstadoJugador().getFuerza()));
    }
}
