package edu.uchicago.cs.java.finalproject.mvc.model;

import edu.uchicago.cs.java.finalproject.mvc.controller.Game;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Explosion in team of Friend. Cause cruiseMissile, guidedMissle, and weaponedFoe explosion when hitting a foe.
 */
public class Explosion implements Movable {
    public static final int EXPIRE = 80;
    private int mExpire;
    private int mRadiux;
    private Point mCenter;

    public Explosion(Movable movFriend) {
        this.mExpire = EXPIRE;
        this.mRadiux = 1;
        this.mCenter = movFriend.getCenter();

    }


    public void move() {
        if (getExpire() == 0)
            Cc.getInstance().getOpsList().enqueue(this, CollisionOp.Operation.REMOVE);
        else {
            if (getExpire() > EXPIRE / 2) {
                setRadius(getRadius() + 3);
            }
            else {
                setRadius(getRadius() - 3);
            }
            setExpire(getExpire() - 1);
        }
    }
    @Override
    public void draw(Graphics g) {
        g.setColor(new Color(Game.R.nextInt(256), Game.R.nextInt(256),
                Game.R.nextInt(256)));
        g.fillOval(getCenter().x -getRadius()/2, getCenter().y-getRadius()/2, getRadius(), getRadius());
        g.fillOval(getCenter().x -getRadius()/2 + 30, getCenter().y-getRadius()/2 + 30, getRadius(), getRadius());
        g.fillOval(getCenter().x -getRadius()/2 + 30, getCenter().y-getRadius()/2 - 30, getRadius(), getRadius());
        g.fillOval(getCenter().x -getRadius()/2 - 30, getCenter().y-getRadius()/2 + 30, getRadius(), getRadius());
        g.fillOval(getCenter().x -getRadius()/2 - 30, getCenter().y-getRadius()/2 - 30, getRadius(), getRadius());
    }
    //methods to satisfy the interface Movable
    @Override
    public Point getCenter() {
        return mCenter;
    }
    @Override
    public int getRadius() {
        return mRadiux;
    }
    @Override
    public Team getTeam() {
        return Team.FRIEND;
    }

    //other getters/setters
    public void setRadius(int radiux) {
        mRadiux = radiux;
    }
    public int getExpire() {
        return mExpire;
    }
    public void setExpire(int expire) {
        mExpire = expire;
    }

}






