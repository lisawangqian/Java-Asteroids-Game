package edu.uchicago.cs.java.finalproject.mvc.model;

import edu.uchicago.cs.java.finalproject.mvc.controller.Game;

import java.awt.*;
import java.util.ArrayList;


public class Falcon extends Sprite {

	// ==============================================================
	// FIELDS 
	// ==============================================================
	
	private final double THRUST = .45;

	final int DEGREE_STEP = 7;
	
	private boolean bShield = false;
	private boolean bFlame = false;
	private boolean bProtected; //for fade in and out
	
	private boolean bThrusting = false;
	private boolean bDecThrusting = false;
	private boolean bTurningRight = false;
	private boolean bTurningLeft = false;
	
	private int nShield=0;
			
	private final double[] FLAME = { 23 * Math.PI / 24 + Math.PI / 2,
			Math.PI + Math.PI / 2, 25 * Math.PI / 24 + Math.PI / 2 };

	private int[] nXFlames = new int[FLAME.length];
	private int[] nYFlames = new int[FLAME.length];

	private Point[] pntFlames = new Point[FLAME.length];

	public static final int EXPIRE = 250;
	private int mExpire = 0;
	private int weakness = 0;
	
	// ==============================================================
	// CONSTRUCTOR 
	// ==============================================================
	
	public Falcon() {
		super();
		setTeam(Team.FRIEND);
		ArrayList<Point> pntCs = new ArrayList<Point>();
		
		// Robert Alef's awesome falcon design
		pntCs.add(new Point(0,9));
		pntCs.add(new Point(-1, 6));
		pntCs.add(new Point(-1,3));
		pntCs.add(new Point(-4, 1));
		pntCs.add(new Point(4,1));
		pntCs.add(new Point(-4,1));

		pntCs.add(new Point(-4, -2));
		pntCs.add(new Point(-1, -2));
		pntCs.add(new Point(-1, -9));
		pntCs.add(new Point(-1, -2));
		pntCs.add(new Point(-4, -2));

		pntCs.add(new Point(-10, -8));
		pntCs.add(new Point(-5, -9));
		pntCs.add(new Point(-7, -11));
		pntCs.add(new Point(-4, -11));
		pntCs.add(new Point(-2, -9));
		pntCs.add(new Point(-2, -10));
		pntCs.add(new Point(-1, -10));
		pntCs.add(new Point(-1, -9));
		pntCs.add(new Point(1, -9));
		pntCs.add(new Point(1, -10));
		pntCs.add(new Point(2, -10));
		pntCs.add(new Point(2, -9));
		pntCs.add(new Point(4, -11));
		pntCs.add(new Point(7, -11));
		pntCs.add(new Point(5, -9));
		pntCs.add(new Point(10, -8));
		pntCs.add(new Point(4, -2));

		pntCs.add(new Point(1, -2));
		pntCs.add(new Point(1, -9));
		pntCs.add(new Point(1, -2));
		pntCs.add(new Point(4,-2));


		pntCs.add(new Point(4, 1));
		pntCs.add(new Point(1, 3));
		pntCs.add(new Point(1,6));
		pntCs.add(new Point(0,9));

		assignPolarPoints(pntCs);

		setColor(Color.WHITE);
		
		//put falcon in the middle.
		setCenter(new Point(Game.DIM.width / 2, Game.DIM.height / 2));
		
		//with random orientation
		setOrientation(Game.R.nextInt(360));
		
		//this is the size of the falcon
		setRadius(35);

		//these are falcon specific
		setProtected(true);
		setbShield(false);
		//initially provide 3 shields
		setShield(5);
		setWeakness(0);
		setFadeValue(0);
	}

	public Falcon(Falcon fal) {
		super();
		setTeam(Team.FRIEND);
		ArrayList<Point> pntCs = new ArrayList<Point>();

		// Robert Alef's awesome falcon design
		pntCs.add(new Point(0,9));
		pntCs.add(new Point(-1, 6));
		pntCs.add(new Point(-1,3));
		pntCs.add(new Point(-4, 1));
		pntCs.add(new Point(4,1));
		pntCs.add(new Point(-4,1));

		pntCs.add(new Point(-4, -2));
		pntCs.add(new Point(-1, -2));
		pntCs.add(new Point(-1, -9));
		pntCs.add(new Point(-1, -2));
		pntCs.add(new Point(-4, -2));

		pntCs.add(new Point(-10, -8));
		pntCs.add(new Point(-5, -9));
		pntCs.add(new Point(-7, -11));
		pntCs.add(new Point(-4, -11));
		pntCs.add(new Point(-2, -9));
		pntCs.add(new Point(-2, -10));
		pntCs.add(new Point(-1, -10));
		pntCs.add(new Point(-1, -9));
		pntCs.add(new Point(1, -9));
		pntCs.add(new Point(1, -10));
		pntCs.add(new Point(2, -10));
		pntCs.add(new Point(2, -9));
		pntCs.add(new Point(4, -11));
		pntCs.add(new Point(7, -11));
		pntCs.add(new Point(5, -9));
		pntCs.add(new Point(10, -8));
		pntCs.add(new Point(4, -2));

		pntCs.add(new Point(1, -2));
		pntCs.add(new Point(1, -9));
		pntCs.add(new Point(1, -2));
		pntCs.add(new Point(4,-2));


		pntCs.add(new Point(4, 1));
		pntCs.add(new Point(1, 3));
		pntCs.add(new Point(1,6));
		pntCs.add(new Point(0,9));

		assignPolarPoints(pntCs);

		setColor(Color.white);

		//put falcon in the middle.
		setCenter(new Point(Game.R.nextInt(Game.DIM.width), Game.R.nextInt(Game.DIM.height)));

		//with random orientation
		setOrientation(fal.getOrientation());

		//this is the size of the falcon
		setRadius(35);

		//these are falcon specific
		setProtected(true);
		setFadeValue(0);
		//set Shield
		setShield(fal.getShield());
		setbShield(fal.getbShield());
		setWeakness(fal.getWeakness());

	}


	// ==============================================================
	// METHODS 
	// ==============================================================

	public void move() {
		super.move();
		if (bThrusting) {
			bFlame = true;
			double dAdjustX = Math.cos(Math.toRadians(getOrientation()))
					* THRUST;
			double dAdjustY = Math.sin(Math.toRadians(getOrientation()))
					* THRUST;
			setDeltaX(getDeltaX() + dAdjustX);
			setDeltaY(getDeltaY() + dAdjustY);
		}
        // if not thrusting and press down arrow, stop the ship
		if (!bThrusting && bDecThrusting) {
			bFlame = false;

			setDeltaX(0);

			setDeltaY(0);



		}

		if (bTurningLeft) {

			if (getOrientation() <= 0 && bTurningLeft) {
				setOrientation(360);
			}
			setOrientation(getOrientation() - DEGREE_STEP);
		} 
		if (bTurningRight) {
			if (getOrientation() >= 360 && bTurningRight) {
				setOrientation(0);
			}
			setOrientation(getOrientation() + DEGREE_STEP);
		}


		//implementing the fadeInOut functionality - added by Dmitriy
		if (getProtected()) {
			setFadeValue(getFadeValue() + 3);
		}
		if (getFadeValue() == 255) {
			setProtected(false);
		}

		//set meter with remaining time
		if (getExpire() ==0){
			setbShield(false);
			Meter.getInstance().setShield(0);
		}
		else{
			setExpire(getExpire()-1);
			Meter.getInstance().setShield(getExpire());
		}





	} //end move

	public void rotateLeft() {
		bTurningLeft = true;
	}

	public void rotateRight() {
		bTurningRight = true;
	}

	public void stopRotating() {
		bTurningRight = false;
		bTurningLeft = false;
	}

	public void thrustOn() {
		bThrusting = true;
	}

	public void thrustDown() {
		bDecThrusting = true;
	}

	public void thrustOff() {
		bThrusting = false;
		bDecThrusting = false;
		bFlame = false;

	}


	private int adjustColor(int nCol, int nAdj) {
		if (nCol - nAdj <= 0) {
			return 0;
		} else {
			return nCol - nAdj;
		}
	}

	public void draw(Graphics g) {

		//does the fading at the beginning or after hyperspace
		Color colShip;

		if (getFadeValue() == 255) {
			colShip = Color.white;
		} else {
			colShip = new Color(adjustColor(getFadeValue(), 200), adjustColor(
					getFadeValue(), 175), getFadeValue());
		}

//		//shield on
		if (getbShield()) {
			if (getWeakness()== 0){
				g.setColor(Color.blue);}
			else if (getWeakness()== 1){
				g.setColor(Color.CYAN);}
			else if (getWeakness()== 2){
				g.setColor(Color.GREEN);}
			else {
				g.setColor(Color.white);}

			g.drawOval(getCenter().x - getRadius(),
					getCenter().y - getRadius(), getRadius() * 2, getRadius() * 2);
		} //end if shield

		//thrusting
		if (bFlame) {
			g.setColor(colShip);
			//the flame
			for (int nC = 0; nC < FLAME.length; nC++) {
				if (nC % 2 != 0) //odd
				{
					pntFlames[nC] = new Point((int) (getCenter().x + 2
							* getRadius()
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])), (int) (getCenter().y - 2
							* getRadius()
							* Math.cos(Math.toRadians(getOrientation())
									+ FLAME[nC])));

				} else //even
				{
					pntFlames[nC] = new Point((int) (getCenter().x + getRadius()
							* 1.1
							* Math.sin(Math.toRadians(getOrientation())
									+ FLAME[nC])),
							(int) (getCenter().y - getRadius()
									* 1.1
									* Math.cos(Math.toRadians(getOrientation())
											+ FLAME[nC])));

				} //end even/odd else

			} //end for loop

			for (int nC = 0; nC < FLAME.length; nC++) {
				nXFlames[nC] = pntFlames[nC].x;
				nYFlames[nC] = pntFlames[nC].y;

			} //end assign flame points

			//g.setColor( Color.white );
			g.fillPolygon(nXFlames, nYFlames, FLAME.length);

		} //end if flame

			drawShipWithColor(g, colShip);

	} //end draw()

	public void drawShipWithColor(Graphics g, Color col) {
		super.draw(g);
		g.setColor(col);
		g.drawPolygon(getXcoords(), getYcoords(), dDegrees.length);
	}


	public void setProtected(boolean bParam) {
		if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}

	public void setProtected(boolean bParam, int n) {
		if (bParam && n % 3 == 0) {
			setFadeValue(n);
		} else if (bParam) {
			setFadeValue(0);
		}
		bProtected = bParam;
	}	

	public boolean getProtected() {return bProtected;}

	//set remaining shield number
	public void setShield(int n) {nShield = n;}
	//get shield number
	public int getShield() {return nShield;}

	//if shield is ON, set expire time(timed shield for a period)
	public void setbShield(boolean ifShield) {
		bShield = ifShield && getShield()>0;
	    if (bShield){
		setExpire(EXPIRE);
		setWeakness(0);}
	}
	public boolean getbShield() {return bShield;}

	public int getExpire() {
		return mExpire;
	}
	public void setExpire(int expire) {
		mExpire = expire;
	}

	public void setWeakness(int nPara){weakness = nPara;}

	public int getWeakness(){return weakness;}


} //end class
