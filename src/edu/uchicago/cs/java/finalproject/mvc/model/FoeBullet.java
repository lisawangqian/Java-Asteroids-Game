package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;
import java.util.ArrayList;

/**
 * A Weapon Foe will shoot these bullets for a period of time, in the team of FOE.
 */
public class FoeBullet extends Sprite {

    private final double FIRE_POWER = 25.0;



    public FoeBullet(WeaponFoe foe){

        super();
        setTeam(Team.FOE);

        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,3)); //top point

        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(0,-2));
        pntCs.add(new Point(-1,-1));

        assignPolarPoints(pntCs);

        //a bullet expires after 20 frames
        setExpire(25);
        setRadius(5);
        setColor(Color.white);


        //everything is relative to the falcon ship that fired the bullet
        setDeltaX( foe.getDeltaX() +
                Math.cos( Math.toRadians( foe.getOrientation() ) ) * FIRE_POWER );
        setDeltaY( foe.getDeltaY() +
                Math.sin( Math.toRadians( foe.getOrientation() ) ) * FIRE_POWER );
        setCenter( foe.getCenter() );

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(foe.getOrientation());


    }

    //implementing the expire functionality in the move method - added by Dmitriy
    public void move(){

        super.move();

        if (getExpire() == 0)
            Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        else
            setExpire(getExpire() - 1);

    }

}
