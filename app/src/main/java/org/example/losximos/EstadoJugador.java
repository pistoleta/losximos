package org.example.losximos;
import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pistoleta on 4/12/17.
 */

public class EstadoJugador {



    /* misc */
    private int cuerpo;
    private int drawCamina;
    private int drawPesas;
    private int drawCama;
    private int drawEscritorio;
    private int pelo;
    private int camisa;
    private int pantalones;
    private int arma;
    private int zapatos;
    private String nombre;

    /*skills*/

    private int inteligencia;
    private int fuerza;
    private int agilidad;

    /*partida*/
    private int energia;
    private int monedas;
    private int tareaEnCurso;
    private int segundosTarea;
    private long comienzoTarea;
    private int idGraficoTarea;
    private int nivelArmaDescubierta;
    private int idTrabajo;
    private List<Integer> listaIdsItems;


    public EstadoJugador() {
        this.cuerpo= R.drawable.pl1_frente1;
        this.drawCamina = R.drawable.pl1_camina_frente_drawable;
        this.drawPesas = R.drawable.pl1_pesas_anim;
        this.drawEscritorio = R.drawable.pl1_escritorio_anim;
        this.drawCama = R.drawable.pl1_dormir;
        this.inteligencia= 0;
        this.energia = 100;
        this.agilidad =0;
        this.arma = R.drawable.vacio;
        this.camisa = R.drawable.vacio;
        this.fuerza = 0;
        this.monedas = 100;
        this.nombre = "Juan" ;
        this.pantalones = R.drawable.vacio;
        this.pelo = 0;
        this.tareaEnCurso = 0;
        this.idGraficoTarea =0;
        this.nivelArmaDescubierta=0;
        this.idTrabajo = -1;
        this.listaIdsItems = new ArrayList<>();
    }

    public int getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(int cuerpo) {
        this.cuerpo = cuerpo;
    }

    public int getDrawCamina() {
        return drawCamina;
    }

    public void setDrawCamina(int drawCamina) {
        this.drawCamina = drawCamina;
    }

    public int getDrawPesas() {
        return drawPesas;
    }

    public void setDrawPesas(int drawPesas) {
        this.drawPesas = drawPesas;
    }

    public int getDrawCama() {
        return drawCama;
    }

    public void setDrawCama(int drawCama) {
        this.drawCama = drawCama;
    }

    public int getDrawEscritorio() {
        return drawEscritorio;
    }

    public void setDrawEscritorio(int drawEscritorio) {
        this.drawEscritorio = drawEscritorio;
    }

    public int getPelo() {
        return pelo;
    }

    public void setPelo(int pelo) {
        this.pelo = pelo;
    }

    public int getCamisa() {
        return camisa;
    }

    public void setCamisa(int camisa) {
        this.camisa = camisa;
    }

    public int getPantalones() {
        return pantalones;
    }

    public void setPantalones(int pantalones) {
        this.pantalones = pantalones;
    }

    public int getArma() {
        return arma;
    }

    public void setArma(int arma) {
        this.arma = arma;
    }

    public int getZapatos() {
        return zapatos;
    }

    public void setZapatos(int zapatos) {
        this.zapatos = zapatos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEnergia() {
        return energia;
    }

    public void setEnergia(int energia) {
        this.energia = energia;
    }

    public int getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(int inteligencia) {
        this.inteligencia = inteligencia;
    }

    public int getFuerza() {
        return fuerza;
    }

    public void setFuerza(int fuerza) {
        this.fuerza = fuerza;
    }

    public int getMonedas() {
        return monedas;
    }

    public void setMonedas(int monedas) {
        this.monedas = monedas;
    }

    public int getAgilidad() {
        return agilidad;
    }

    public void setAgilidad(int agilidad) {
        this.agilidad = agilidad;
    }

    public int getTareaEnCurso() {
        return tareaEnCurso;
    }

    public void setTareaEnCurso(int tareaEnCurso) {
        this.tareaEnCurso = tareaEnCurso;
    }

    public int getIdTrabajo() {
        return idTrabajo;
    }

    public void setIdTrabajo(int idTrabajo) {
        this.idTrabajo = idTrabajo;
    }

    public long getComienzoTarea() {
        return comienzoTarea;
    }

    public void setComienzoTarea(long comienzoTarea) {
        this.comienzoTarea = comienzoTarea;
    }

    public void setIdGraficoTarea(int idGraficoTarea) {
        this.idGraficoTarea = idGraficoTarea;
    }

    public int getIdGraficoTarea() {
        return idGraficoTarea;
    }

    public int getNivelArmaDescubierta() {
        return nivelArmaDescubierta;
    }

    public void setNivelArmaDescubierta(int nivelArmaDescubierta) {
        this.nivelArmaDescubierta = nivelArmaDescubierta;
    }

    public List<Integer> getListaIdsItems() {
        return listaIdsItems;
    }

    public int getSegundosTarea() {
        return segundosTarea;
    }

    public void setSegundosTarea(int segundosTarea) {
        this.segundosTarea = segundosTarea;
    }
}
