package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;

/**
 * Meter display the remaining life of a shield, shipFloater or ShieldFloater
 */
public class Meter {

    private Font fnt = new Font("SansSerif", Font.BOLD, 12);
    private int goodie = 0;
    private int shipFloater = 0;
    private int shield = 0;

    private static Meter instance = null;

    // Constructor made private - static Utility class only
    private Meter() {}


    public static Meter getInstance(){
        if (instance == null){
            instance = new Meter();
        }
        return instance;
    }


    public void draw(Graphics g) {

        g.setFont(fnt);
        if (getGoodie() != 0) {
            g.setColor(Color.YELLOW);
            g.drawString("Remaining Time of Shield Floater :  " + getGoodie(), 20, 45);
        }
        else{
            g.setColor(Color.YELLOW);
            g.drawString("No Shield Floater", 20, 45);
        }

        if (getShipFloater() != 0) {
            g.setColor(Color.BLUE);
            g.drawString("Remaining Time of Ship Floater :  " + getShipFloater(), 20, 60);
        }
        else{
            g.setColor(Color.BLUE);
            g.drawString("No Ship Floater", 20, 60);
        }
        if (getShield() != 0) {
            g.setColor(Color.CYAN);
            g.drawString("Remaining Time of Shield :  " + getShield(), 20, 30);
        }
        else{
            g.setColor(Color.CYAN);
            g.drawString("No Shield On", 20, 30);
        }



    }

    public void setGoodie(int time){
        goodie = time;
    }
    public int getGoodie(){
        return goodie;
    }

    public void setShipFloater(int time){
        shipFloater= time;
    }
    public int getShipFloater(){
        return shipFloater;
    }

    public void setShield (int time){
        shield = time ;
    }
    public int getShield(){
        return shield;
    }

}
