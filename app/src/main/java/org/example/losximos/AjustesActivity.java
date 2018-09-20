package org.example.losximos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AjustesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        ((Button)findViewById(R.id.btnContinuar)).setBackgroundResource(R.drawable.boton_verde_anim);
        ((Button)findViewById(R.id.btnSalir)).setBackgroundResource(R.drawable.boton_verde_anim);
    }





    public void continuarClick(View view) {
        this.finish();
    }

    public void salirClick(View view) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("opc", "salir");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void ajustesClick(View view) {
        finish();
    }
}
