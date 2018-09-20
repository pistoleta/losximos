package org.example.losximos;

/**
 * Created by pistoleta on 7/12/17.
 */

public class Mueble {
    private int id,posX,posY;


    public Mueble(Grafico g)
    {
        this.id=g.getId();
        this.posX=g.getCenX();
        this.posY=g.getCenY();

    }


    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }


    public int getId() {
        return id;
    }
}
