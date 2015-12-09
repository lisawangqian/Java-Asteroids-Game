package edu.uchicago.cs.java.finalproject.mvc.model;

import edu.uchicago.cs.java.finalproject.mvc.controller.Game;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A Weaponed foe in the team of FOE, which is spawned randomly. And once spawned, shoot FoeBullet for a period of time
 */
public class WeaponFoe extends Sprite{


    private int nSpin;

    public WeaponFoe() {

        super();
        setTeam(Team.FOE);
        ArrayList<Point> pntCs = new ArrayList<Point>();
        // top of ship
        pntCs.add(new Point(2, 5));
        pntCs.add(new Point(2, 2));
        pntCs.add(new Point(5, 2));
        pntCs.add(new Point(5, -2));
        pntCs.add(new Point(2, -2));
        pntCs.add(new Point(2, -5));
        pntCs.add(new Point(-2, -5));
        pntCs.add(new Point(-2, -2));
        pntCs.add(new Point(-5, -2));
        pntCs.add(new Point(-5, 2));
        pntCs.add(new Point(-2, 2));
        pntCs.add(new Point(-2, 5));


        assignPolarPoints(pntCs);

        setExpire(100);
        setRadius(40);
        setColor(Color.WHITE);


        int nX = Game.R.nextInt(8);
        int nY = Game.R.nextInt(8);
        int nS = Game.R.nextInt(10);

        //set random DeltaX
        if (nX % 2 == 0)
            setDeltaX(nX);
        else
            setDeltaX(-nX);

        //set rnadom DeltaY
        if (nY % 2 == 0)
            setDeltaX(nY);
        else
            setDeltaX(-nY);

        //set random spin
        if (nS % 2 == 0)
            setSpin(nS);
        else
            setSpin(-nS);

        //random point on the screen
        setCenter(new Point(Game.R.nextInt(Game.DIM.width - 50),
                Game.R.nextInt(Game.DIM.height -50)));

        //random orientation
        setOrientation(Game.R.nextInt(360));

    }

    public void move() {
        super.move();
        setOrientation(getOrientation() + getSpin());

        //adding expire functionality, and set meter with remaining time
        if (getExpire() == 0){
            Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
           }
        else {

            setExpire(getExpire() - 1);
            Cc.getInstance().getOpsList().enqueue(new FoeBullet(this), CollisionOp.Operation.ADD);

        }


    }

    public int getSpin() {
        return this.nSpin;
    }

    public void setSpin(int nSpin) {
        this.nSpin = nSpin;
    }




    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //fill this polygon (with whatever color it has)
        g.fillPolygon(getXcoords(), getYcoords(), dDegrees.length);
        //now draw a white border
        g.setColor(Color.WHITE);
        g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
    }

}
