package org.example.losximos;

import java.util.List;

/**
 * Created by pistoleta on 4/12/17.
 */

public interface AlmacenInterfaz {
    public void guardarEstadoJugador(EstadoJugador estadoJugador);
    public EstadoJugador obtenerEstadoJugador();

    public void guardarMuebles(List<Grafico> mueble);
    public List<Grafico> obtenerListaMuebles();

    public void eliminarTodo();
}
