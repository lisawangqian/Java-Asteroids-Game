package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * GuidedMissile is special weapon of Ship in the team of FRIENDS. throwed and then  explore by key press and release event
 */
public class GuidedMissle extends Sprite {

    private final double FIRE_POWER = 5.0;
    final int DEGREE_STEP = 10;
    private boolean ifRelease = false;



    public GuidedMissle(Falcon fal) {

        super();
        setTeam(Team.FRIEND);
        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();


        pntCs.add(new Point(0, 5));
        pntCs.add(new Point(1, 3));
        pntCs.add(new Point(1, 0));
        pntCs.add(new Point(6, 0));
        pntCs.add(new Point(6, -1));
        pntCs.add(new Point(1, -1));
        pntCs.add(new Point(1, -2));

        pntCs.add(new Point(-1, -2));
        pntCs.add(new Point(-1, -1));
        pntCs.add(new Point(-6, -1));
        pntCs.add(new Point(-6, 0));
        pntCs.add(new Point(-1, 0));
        pntCs.add(new Point(-1, 3));
        assignPolarPoints(pntCs);






        setRadius(20);

        //everything is relative to the falcon ship that fired the bullet
        setDeltaX(fal.getDeltaX()
                + Math.cos(Math.toRadians(fal.getOrientation())) * FIRE_POWER);
        setDeltaY(fal.getDeltaY()
                + Math.sin(Math.toRadians(fal.getOrientation())) * FIRE_POWER);
        setCenter(fal.getCenter());

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(fal.getOrientation());
        setColor(Color.RED);

    }



    @Override
    public void move() {

        super.move();

        if (!getRelease()){
            setOrientation(getOrientation() - DEGREE_STEP);
            setDeltaX(getDeltaX() * 1.05);
            setDeltaY(getDeltaY() * 1.05);
        }
        else {
            Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        }



    }

    @Override
    public void draw(Graphics g){
        setColor(Color.GREEN);
        super.draw(g);


    }

    public void setRelease(boolean release){
        ifRelease = release;

    }

    public boolean getRelease(){
        return ifRelease;

    }




}
