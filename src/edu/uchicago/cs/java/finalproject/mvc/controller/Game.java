package edu.uchicago.cs.java.finalproject.mvc.controller;

import edu.uchicago.cs.java.finalproject.mvc.model.*;
import edu.uchicago.cs.java.finalproject.mvc.view.GamePanel;
import edu.uchicago.cs.java.finalproject.sounds.Sound;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

// ===============================================
// == This Game class is the CONTROLLER
// ===============================================
/**
 * two thread.  Runnable interface, keyListener: key release, key press
 */
public class Game implements Runnable, KeyListener, MouseMotionListener, MouseListener {

	// ===============================================
	// FIELDS
	// ===============================================

	public static final Dimension DIM = new Dimension(900, 700); //the dimension of the game.
	private GamePanel gmpPanel;
	public static Random R = new Random();
	public final static int ANI_DELAY = 45; // milliseconds between screen
											// updates (animation)
	private Thread thrAnim;
	private int nLevel = 1;
	private int nTick = 0;

	private boolean bMuted = true;
	

	private final int PAUSE = 80, // p key
			QUIT = 81, // q key
			LEFT = 37, // rotate left; left arrow
			RIGHT = 39, // rotate right; right arrow
			UP = 38, // thrust; up arrow
	        DOWN = 40, //down key
			START = 83, // s key
			FIRE = 32, // space key
			MUTE = 77, // m-key mute
	        SHIELD = 65, // a key arrow
	        NUM_ENTER = 10, // num_enter key; hyperSpace
	        GUIDED = 71, // g key
	        SPECIAL = 70; 	// fire special weapon cruise Missle;  F key

	private Clip clpThrust;
	private Clip clpMusicBackground;

	private static final int SPAWN_NEW_SHIP_FLOATER = 1200;
	private static final int SPAWN_NEW_GOODIE_FLOATER = 900;
	private static final int SPAWN_NEW_WEAPONFOE = 700;

	private GuidedMissle gm;



	// ===============================================
	// ==CONSTRUCTOR
	// ===============================================

	public Game() {

		gmpPanel = new GamePanel(DIM);
		// addKeyListener to this class.
		gmpPanel.addKeyListener(this);
		gmpPanel.addMouseListener(this);
		gmpPanel.addMouseMotionListener(this);
		clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
		clpMusicBackground = Sound.clipForLoopFactory("music-background.wav");
	

	}

	// ===============================================
	// ==METHODS
	// ===============================================

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() { // uses the Event dispatch thread from Java 5 (refactored)
					public void run() {
						try {
							Game game = new Game(); // construct itself
							game.fireUpAnimThread();

						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}

	private void fireUpAnimThread() { // called initially
		if (thrAnim == null) {
			thrAnim = new Thread(this); // pass the thread a runnable object (this)
			thrAnim.start();
		}
	}

	// implements runnable - must have run method
	public void run() {

		// lower this thread's priority; let the "main" aka 'Event Dispatch'
		// thread do what it needs to do first
		thrAnim.setPriority(Thread.MIN_PRIORITY);

		// and get the current time
		long lStartTime = System.currentTimeMillis();

		// this thread animates the scene
		while (Thread.currentThread() == thrAnim) {
			tick();
			// spawn power-up ship floater
			spawnNewShipFloater();
			// spawn power-up shield floater
			spawnNewGoodie();
			// spawn weaponed Foe
			spawnWeaponFoe();

			gmpPanel.update(gmpPanel.getGraphics()); // update takes the graphics context we must
														// surround the sleep() in a try/catch block
														// this simply controls delay time between 
														// the frames of the animation

			//this might be a good place to check for collisions
			checkCollisions();
			//this might be a god place to check if the level is clear (no more foes)
			//if the level is clear then spawn some big asteroids -- the number of asteroids 
			//should increase with the level. 
			checkNewLevel();

			try {
				// The total amount of time is guaranteed to be at least ANI_DELAY long.  If processing (update) 
				// between frames takes longer than ANI_DELAY, then the difference between lStartTime - 
				// System.currentTimeMillis() will be negative, then zero will be the sleep time
				lStartTime += ANI_DELAY;
				Thread.sleep(Math.max(0,
						lStartTime - System.currentTimeMillis()));
			} catch (InterruptedException e) {
				// just skip this frame -- no big deal
				continue;
			}
		} // end while
	} // end run



	private void checkCollisions() {

		

		Point pntFriendCenter, pntFoeCenter;
		int nFriendRadiux, nFoeRadiux;

		for (Movable movFriend : Cc.getInstance().getMovFriends()) {
			for (Movable movFoe : Cc.getInstance().getMovFoes()) {

				pntFriendCenter = movFriend.getCenter();
				pntFoeCenter = movFoe.getCenter();
				nFriendRadiux = movFriend.getRadius();
				nFoeRadiux = movFoe.getRadius();

				//detect collision
				if (pntFriendCenter.distance(pntFoeCenter) < (nFriendRadiux + nFoeRadiux)) {

					//falcon
					if ((movFriend instanceof Falcon)) {
						//if falcon is not on shield and not on protected, it is killed
						if (!Cc.getInstance().getFalcon().getProtected() && !Cc.getInstance().getFalcon().getbShield()) {
							int shieldNum = ((Falcon) movFriend).getShield();
							Cc.getInstance().getOpsList().enqueue(movFriend, CollisionOp.Operation.REMOVE);
							Cc.getInstance().getOpsList().enqueue(new Debris(movFriend.getCenter(), Color.GREEN, 3),
									CollisionOp.Operation.ADD);

							Cc.getInstance().spawnFalcon(false);
							if (Cc.getInstance().getNumFalcons() != 0) {
								Cc.getInstance().getFalcon().setShield(shieldNum);
							}

						}
						//if falcon in on shield, weaken the shield by changing its color
						//if falcon shield is destroyed at this time point, set it protected for a little time
						if (Cc.getInstance().getFalcon().getbShield()){
							if (Cc.getInstance().getFalcon().getWeakness() == 0){
								Cc.getInstance().getFalcon().setWeakness(1);
							}
							else if (Cc.getInstance().getFalcon().getWeakness() == 1){
								Cc.getInstance().getFalcon().setWeakness(2);
							}
							else if (Cc.getInstance().getFalcon().getWeakness() == 2){
								Cc.getInstance().getFalcon().setWeakness(3);
							}
							else {
								Cc.getInstance().getFalcon().setProtected(true);
								Cc.getInstance().getFalcon().setbShield(false);
								Cc.getInstance().getFalcon().setExpire(0);
								Meter.getInstance().setShield(0);

							}
						}
					}

					//not the falcon
					else {

						Cc.getInstance().getOpsList().enqueue(movFriend, CollisionOp.Operation.REMOVE);
						// if not the falcon(bullet or weapon), increase the score according to different foe
						if ((movFoe instanceof WeaponFoe)) {
							Cc.getInstance().setScore(Cc.getInstance().getScore() + 500);
							spawnExplosion(movFriend);
						}
						else if ((movFriend instanceof Bomb)) {
							Cc.getInstance().setScore(Cc.getInstance().getScore() + 200);
						}

						else if ((movFriend instanceof Cruise)) {
							Cc.getInstance().setScore(Cc.getInstance().getScore() + 150);
							// spawn an explosion if collision caused by cruiseMissle
							spawnExplosion(movFriend);
						}
						else {
							Cc.getInstance().setScore(Cc.getInstance().getScore() + 100);
						}
					}//end else

					//Weaken the foe if it is a titaniumAsteroid until it can be killed(spawn new smaller asteroid
					if (movFoe instanceof TitaniumAsteroid) {
						if (((TitaniumAsteroid) movFoe).getWeakness() < 2){
							((TitaniumAsteroid) movFoe).setWeakness(((TitaniumAsteroid) movFoe).getWeakness() + 1);
							((TitaniumAsteroid) movFoe).setNewColor();

						}
						else{
							killFoe(movFoe);
							Cc.getInstance().getOpsList().enqueue(new Debris(movFoe.getCenter(), Color.GRAY, 5),
									CollisionOp.Operation.ADD);
							Sound.playSound("kapow.wav");

						}

					}
					//kill the foe and if asteroid, then spawn new asteroids
					else if (movFoe instanceof Asteroid) {
						killFoe(movFoe);
						Cc.getInstance().getOpsList().enqueue(new Debris(movFoe.getCenter(), Color.GRAY, 5),
								CollisionOp.Operation.ADD);
						Sound.playSound("kapow.wav");
					}
					else if (movFoe instanceof WeaponFoe) {
						killFoe(movFoe);
						Cc.getInstance().getOpsList().enqueue(new Debris(movFoe.getCenter(), Color.GRAY, 3),
								CollisionOp.Operation.ADD);

					}


				}//end if
			}//end inner for
		}//end outer for


		//check for collisions between falcon and floaters
		if (Cc.getInstance().getFalcon() != null){
			Point pntFalCenter = Cc.getInstance().getFalcon().getCenter();
			int nFalRadiux = Cc.getInstance().getFalcon().getRadius();
			Point pntFloaterCenter;
			int nFloaterRadiux;
			
			for (Movable movFloater : Cc.getInstance().getMovFloaters()) {
				pntFloaterCenter = movFloater.getCenter();
				nFloaterRadiux = movFloater.getRadius();
	
				//detect collision
				if (pntFalCenter.distance(pntFloaterCenter) < (nFalRadiux + nFloaterRadiux)) {
					if (movFloater instanceof NewShipFloater){
                    //add one more falcon to the available number of ships
						Meter.getInstance().setShipFloater(0);
						Cc.getInstance().setNumFalcons(Cc.getInstance().getNumFalcons() + 1);
						Cc.getInstance().getOpsList().enqueue(movFloater, CollisionOp.Operation.REMOVE);
						Sound.playSound("pacman_eatghost.wav"); }
					if (movFloater instanceof Goodie){
						//add one more shield to the available number of shields
						Meter.getInstance().setGoodie(0);
						Cc.getInstance().getFalcon().setShield(Cc.getInstance().getFalcon().getShield() + 1);
						Cc.getInstance().getOpsList().enqueue(movFloater, CollisionOp.Operation.REMOVE);
						Sound.playSound("pacman_eatghost.wav"); }
	
					}//end if
			}//end inner for
		}//end if not null
		


		//we are dequeuing the opsList and performing operations in serial to avoid mutating the movable arraylists while iterating them above
		while(!Cc.getInstance().getOpsList().isEmpty()){
			CollisionOp cop =  Cc.getInstance().getOpsList().dequeue();
			Movable mov = cop.getMovable();
			CollisionOp.Operation operation = cop.getOperation();

			switch (mov.getTeam()){
				case FOE:
					if (operation == CollisionOp.Operation.ADD){
						Cc.getInstance().getMovFoes().add(mov);
					} else {
						Cc.getInstance().getMovFoes().remove(mov);
					}

					break;
				case FRIEND:
					if (operation == CollisionOp.Operation.ADD){
						Cc.getInstance().getMovFriends().add(mov);
					} else {
						Cc.getInstance().getMovFriends().remove(mov);
					}
					break;

				case FLOATER:
					if (operation == CollisionOp.Operation.ADD){
						Cc.getInstance().getMovFloaters().add(mov);
					} else {
						Cc.getInstance().getMovFloaters().remove(mov);
					}
					break;

				case DEBRIS:
					if (operation == CollisionOp.Operation.ADD){
						Cc.getInstance().getMovDebris().add(mov);
					} else {
						Cc.getInstance().getMovDebris().remove(mov);
					}
					break;


			}

		}
		//a request to the JVM is made every frame to garbage collect, however, the JVM will choose when and how to do this
		System.gc();
		
	}//end meth



	private void killFoe(Movable movFoe) {
		
		if (movFoe instanceof Asteroid){

			//we know this is an Asteroid, so we can cast without threat of ClassCastException
			Asteroid astExploded = (Asteroid)movFoe;
			//big asteroid 
			if(astExploded.getSize() == 0){
				//spawn two medium Asteroids
				Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

			} 
			//medium size aseroid exploded
			else if(astExploded.getSize() == 1){
				//spawn three small Asteroids
				Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);
				Cc.getInstance().getOpsList().enqueue(new Asteroid(astExploded), CollisionOp.Operation.ADD);

			}

		} 

		//remove the original Foe
		Cc.getInstance().getOpsList().enqueue(movFoe, CollisionOp.Operation.REMOVE);

	}

	//some methods for timing events in the game,
	//such as the appearance of UFOs, floaters (power-ups), etc. 
	public void tick() {
		if (nTick == Integer.MAX_VALUE)
			nTick = 0;
		else
			nTick++;
	}

	public int getTick() {
		return nTick;
	}

	private void spawnNewShipFloater() {
		//make the appearance of power-up dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_SHIP_FLOATER - nLevel * 7) == 0) {
			//Cc.getInstance().getMovFloaters().enqueue(new NewShipFloater());
			Cc.getInstance().getOpsList().enqueue(new NewShipFloater(), CollisionOp.Operation.ADD);
		}
	}

	private void spawnNewGoodie() {
		//make the appearance of power-up dependent upon ticks and levels, more frequent than ship pow-up floater
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_GOODIE_FLOATER - nLevel * 8) == 0) {
			Cc.getInstance().getOpsList().enqueue(new Goodie(), CollisionOp.Operation.ADD);
		}
	}

	private void spawnWeaponFoe(){
		//make the appearance of weaponedFoe dependent upon ticks and levels
		//the higher the level the more frequent the appearance
		if (nTick % (SPAWN_NEW_WEAPONFOE - nLevel * 10) == 0) {
			Cc.getInstance().getOpsList().enqueue(new WeaponFoe(), CollisionOp.Operation.ADD);
		}
	}

	// Called when user presses 's'
	private void startGame() {
		Cc.getInstance().clearAll();
		Cc.getInstance().initGame();
		Cc.getInstance().setLevel(0);
		Cc.getInstance().setPlaying(true);
		Cc.getInstance().setPaused(false);

		if (!bMuted){
		   clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);}
	}

	//Method to spawn an Explosion
	private void spawnExplosion(Movable movFriend) {
		Cc.getInstance().getOpsList().enqueue(new Explosion(movFriend), CollisionOp.Operation.ADD);
		Sound.playSound("explosion.wav");
	}

	//this method spawns new asteroids
	private void spawnAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			Cc.getInstance().getOpsList().enqueue(new Asteroid(0), CollisionOp.Operation.ADD);

		}
	}

	//this method spawns new TitaniumAsteroids
	private void spawnTitaniumAsteroids(int nNum) {
		for (int nC = 0; nC < nNum; nC++) {
			//Asteroids with size of zero are big
			Cc.getInstance().getOpsList().enqueue(new TitaniumAsteroid(0), CollisionOp.Operation.ADD);

		}
	}
	
	
	private boolean isLevelClear(){
		//if there are no more Asteroids/TitaniumAsteroid on the screen
		boolean bAsteroidFree = true;
		for (Movable movFoe : Cc.getInstance().getMovFoes()) {
			if (movFoe instanceof Asteroid | movFoe instanceof TitaniumAsteroid | movFoe instanceof WeaponFoe){
				bAsteroidFree = false;
				break;
			}
		}
		
		return bAsteroidFree;

		
	}
	
	private void checkNewLevel(){
		
		if (isLevelClear() ){
			if (Cc.getInstance().getFalcon() !=null)
				Cc.getInstance().getFalcon().setProtected(true);
			
			spawnAsteroids(Cc.getInstance().getLevel() + 2);
			spawnTitaniumAsteroids(Cc.getInstance().getLevel() + 2);
			Cc.getInstance().setLevel(Cc.getInstance().getLevel() + 1);

		}
	}




	// Varargs for stopping looping-music-clips
	private static void stopLoopingSounds(Clip... clpClips) {
		for (Clip clp : clpClips) {
			clp.stop();
		}
	}

	// ===============================================
	// KEYLISTENER METHODS
	// ===============================================

	@Override
	public void keyPressed(KeyEvent e) {
		Falcon fal = Cc.getInstance().getFalcon();
		int nKey = e.getKeyCode();
		// System.out.println(nKey);

		if (nKey == START && !Cc.getInstance().isPlaying())
			startGame();

		if (fal != null) {
			switch (nKey) {
				case PAUSE:
					Cc.getInstance().setPaused(!Cc.getInstance().isPaused());
					if (Cc.getInstance().isPaused())
						stopLoopingSounds(clpMusicBackground, clpThrust);
					else
						clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
					break;
				case QUIT:
					System.exit(0);
					break;
				case UP:
					fal.thrustOn();
					if (!Cc.getInstance().isPaused()) {
						clpThrust.loop(Clip.LOOP_CONTINUOUSLY);
					}
					break;
				case DOWN:
					fal.thrustDown();
					break;
				case LEFT:
					fal.rotateLeft();
					break;
				case RIGHT:
					fal.rotateRight();
					break;
				case GUIDED:
					gm = new GuidedMissle(fal);
					Cc.getInstance().getOpsList().enqueue(gm, CollisionOp.Operation.ADD);
					gm.setRelease(false);
					Sound.playSound("laser.wav");
					break;

				// possible future use
				// case KILL:
				// case SHIELD:
				// case NUM_ENTER:

				default:
					break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		Falcon fal = Cc.getInstance().getFalcon();
		int nKey = e.getKeyCode();
		 //System.out.println(nKey);

		if (fal != null) {
			switch (nKey) {
				case FIRE:
					Cc.getInstance().getOpsList().enqueue(new Bullet(fal), CollisionOp.Operation.ADD);
					Sound.playSound("laser.wav");
					break;
				//special is a special weapon, current it fires the cruise missile and explore when hit a foe.
				case SPECIAL:
					Cc.getInstance().getOpsList().enqueue(new Cruise(fal), CollisionOp.Operation.ADD);
					Sound.playSound("laser.wav");
					break;
				//guided is a special weapon, press key to throw it and release key cause it explore
				case GUIDED:
					gm.setRelease(true);
					spawnExplosion(gm);
					break;
				//hyperspace with a little time on protection
				case NUM_ENTER:
					Cc.getInstance().changeFalcon(fal);
					Sound.playSound("shipspawn.wav");
					break;
				//shield on
				case SHIELD:
					if (fal.getShield() > 0) {
						fal.setbShield(true);
						Sound.playSound("shieldup.wav");
						if (fal.getbShield()) {
							fal.setShield(fal.getShield() - 1);
						}
					}
					break;
				case LEFT:
					fal.stopRotating();
					break;
				case RIGHT:
					fal.stopRotating();
					break;
				case UP:
					fal.thrustOff();
					clpThrust.stop();
					break;
				case DOWN:
					fal.thrustOff();
					break;
				case MUTE:
					if (!bMuted) {
						stopLoopingSounds(clpMusicBackground);
						bMuted = !bMuted;
					} else {
						clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
						bMuted = !bMuted;
					}
					break;


				default:
					break;
			}
		}
	}

	@Override
	// Just need it b/c of KeyListener implementation
	public void keyTyped(KeyEvent e) {



	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	//once clicked throw a bomb
	public void mousePressed(MouseEvent e) {

		Cc.getInstance().getOpsList()
				.enqueue(new Bomb(e), CollisionOp.Operation.ADD);
		Sound.playSound("explosion.wav");

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	//moved mouse position was drawn in gmpPanel.
	public void mouseMoved(MouseEvent e) {
		gmpPanel.setMouseEvent(e);

	}
}


