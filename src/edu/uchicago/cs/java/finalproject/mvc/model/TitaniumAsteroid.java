package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;

/**
 * TitaniumAsteroid inheriance from Asteroid with additional featured of changed color (weakened until Killed)
 */
public class TitaniumAsteroid extends Asteroid {
    private int weakness = 0;
    public TitaniumAsteroid(int nSize) {
        super(nSize);
        setNewColor();
        setDeltaX(super.getDeltaX()/3);
        setDeltaY(super.getDeltaY()/3);

    }
    //TitaniumAsteroid was weakened(color change) each time was hit and finally die.
    public void setNewColor(){
        if (getWeakness() == 0){
            setColor(Color.MAGENTA);
        }
        else if (getWeakness() == 1){
            setColor(Color.red);
        }
        else if (getWeakness() == 2){
            setColor(Color.orange);}
        else{
            setWeakness(0);
        }

    }
    public void setWeakness(int nPara){weakness = nPara;}

    public int getWeakness(){return weakness;}

}
