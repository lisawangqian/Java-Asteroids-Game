package edu.uchicago.cs.java.finalproject.mvc.model;

import java.awt.*;

/**
 * In the team of Debris. When collision happens, debris is generated for foe and friend members
 */
public class Debris implements Movable {
    private static final int EXPIRE = 100;
    private int mExpire;
    private Point mCenter;
    private Color mColor;
    private int n;
    public Debris(Point point, Color color, int nPara) {
        mCenter = point;
        mExpire = EXPIRE;
        mColor = color;
        n = nPara;
    }

    @Override
    public void move() {
        if (mExpire ==0){
            Cc.getInstance().getOpsList().enqueue(this,
                    CollisionOp.Operation.REMOVE);
        }

        mExpire--;

    }

    @Override
    public void draw(Graphics g) {
        g.setColor(mColor);
        g.fillOval(mCenter.x, mCenter.y, mExpire/n, mExpire/n);
        g.fillOval(mCenter.x - 20, mCenter.y - 20, mExpire/n, mExpire/n);
        g.fillOval(mCenter.x - 20, mCenter.y + 20, mExpire/n, mExpire/n);
        g.fillOval(mCenter.x + 20, mCenter.y - 20, mExpire/n, mExpire/n);
        g.fillOval(mCenter.x + 20, mCenter.y + 20, mExpire/n, mExpire/n);

    }

    @Override
    public Point getCenter() {
        return mCenter;
    }

    @Override
    public int getRadius() {
        return mExpire;
    }

    @Override
    public Team getTeam() {
        return Team.DEBRIS;
    }
}
