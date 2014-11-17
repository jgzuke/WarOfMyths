
/** Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
 * @param DifficultyLevel Chosen difficulty setting which dictates enemy reaction time and DifficultyLevelMultiplier
 * @param DifficultyLevelMultiplier Function of DifficultyLevel which changes enemy health, mana, speed
 * @param EnemyType Mage type of enemy
 * @param PlayerType Mage type of player
 * @param LevelNum Level chosen to fight on
 * @param player Player object that has health etc and generates movement handler
 * @param enemy Enemy object with health etc and ai
 * @param enemies Array of all enemies currently on screen excluding main mage enemy
 * @param proj_Trackers Array of all enemy or player proj_Trackers
 * @param proj_Trackers Array of all enemy or player Proj_Tracker explosions
 * @param spGraphicEnemy Handles the changing of main enemy's sp
 * @param spGraphicPlayer Handles the changing of player's sp
 * @param oRectX1 Array of all walls left x value
 * @param oRectX2 Array of all walls right x value
 * @param oRectY1 Array of all walls top y value
 * @param oRectY2 Array of all walls bottom x value
 * @param oCircX Array of all pillars middle x value
 * @param oCircY Array of all pillars middle y value
 * @param oCircRadius Array of all pillars radius
 * @param currentCircle Current index of oCircX to write to
 * @param currentRectangle Current index of oRectX1 to write to
 * @param teleportSpots Array of levels four teleport spots x and y for enemy mage
 * @param game Game object holding imageLibrary
 * @param context Main activity context for returns
 * @param aoeRect Rectangle to draw sized bitmaps
 * @param mHandler Timer for frameCaller
 * @param handleMovement Handles players movement attacks etc
 * @param screenMinX Start of game on screen horizontally
 * @param screenMinY Start of game on screen vertically
 * @param screenDimensionMultiplier
 * @param frameCaller Calls objects and controllers frameCalls
 */
package com.magegame;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
public final class Controller extends View
{
	protected int screenMinX;
	protected int screenMinY;
	protected double screenDimensionMultiplier;
	protected Paint paint = new Paint();
	protected Matrix rotateImages = new Matrix();
	protected StartActivity activity;
	protected int currentTutorial = 1;
	protected ArrayList<int[]> saveEnemyInformation = new ArrayList<int[]>();
	protected ArrayList<Enemy> enemies = new ArrayList<Enemy>();
	protected ArrayList<Structure> structures = new ArrayList<Structure>();
	protected ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();
	protected ArrayList<Proj_Tracker> proj_Trackers = new ArrayList<Proj_Tracker>();
	protected ArrayList<Proj_Tracker_AOE> proj_Tracker_AOEs = new ArrayList<Proj_Tracker_AOE>();
	
	private ArrayList<Wall_Rectangle> wallRects = new ArrayList<Wall_Rectangle>();
	private ArrayList<Wall_Ring> wallRings = new ArrayList<Wall_Ring>();
	private ArrayList<Wall_Circle> wallCircles = new ArrayList<Wall_Circle>();
	private ArrayList<int[]> wallPassageValues = new ArrayList<int[]>(); // int[] is x1, x2, y1, y2
	private ArrayList<int[]> wallRingValues = new ArrayList<int[]>(); // int[] is x, y, radiusInner, radiusOuter, tall or not
	private ArrayList<int[]> wallRectValues = new ArrayList<int[]>(); // int[] is x1, x2, y1, y2, tall or not
	private ArrayList<int[]> wallCircleValues = new ArrayList<int[]>(); // int[] is x, y, radius, tall or not
	private Rect aoeRect = new Rect();
	private boolean gameEnded = false;
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator = new Random();
	protected int difficultyLevel = 10;
	private double difficultyLevelMultiplier;
	protected int levelNum = 10;
	private Bitmap background;
	protected Bitmap tempPicture;
	protected Bitmap tempPictureLock;
	protected PlayerGestureDetector detect;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected int xShiftLevel;
	protected int yShiftLevel;
	private Handler mHandler = new Handler();
	private Bitmap check;
	protected String buyingItem;
	protected int startingLevel;
	protected boolean drainHp = false;
	protected boolean lowerHp = false;
	protected boolean limitSpells = false;
	protected boolean enemyRegen = false;
	protected double moneyMultiplier = 0;
	protected double moneyMade = 0;
	public boolean hasKey = false;
	private int exitX = 0;
	private int exitY = 0;
	private int curXShift;
	private int curYShift;
	private int goldColor = Color.rgb(216, 200, 28);
	private int platinumColor = Color.rgb(196, 204, 204);
	private int healthColor = Color.rgb(150, 0, 0);
	private int specialColor = Color.rgb(0, 0, 150);
	private int cooldownColor = Color.rgb(190, 190, 0);
	private int highlightChoiceColor = Color.BLUE;
	protected DrawnSprite shootStick = new Graphic_shootStick();
	protected int playerHit=0;
	protected int playerBursted = 0;
	Typeface magicMedieval; 
	protected Runnable frameCaller = new Runnable()
	{
		/**
		 * calls most objects 'frameCall' method (walls enemies etc)
		 */
		public void run()
		{
			if(activity.gameRunning) // if game is running call framecalls
			{
				frameCall();
				mHandler.postDelayed(this, 50);
			}
		}
	};	
	/** 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public Controller(Context startSet, StartActivity activitySet)
	{
		super(startSet);
		activity = activitySet;
		context = startSet;
		imageLibrary = new ImageLibrary(startSet, this); // creates image library
		screenMinX = activitySet.screenMinX;
		screenMinY = activitySet.screenMinY;
		screenDimensionMultiplier = activitySet.screenDimensionMultiplier;
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setFilterBitmap(true);
		setBackgroundColor(Color.BLACK);
		setKeepScreenOn(true); // so screen doesnt shut off when game is left inactive
		player = new Player(this); // creates player object
		detect = new PlayerGestureDetector(this); // creates gesture detector object
		setOnTouchListener(detect);
		detect.setPlayer(player);
		changeDifficulty(10);
		shootStick.visualImage = imageLibrary.loadImage("icon_shoot", 70, 35);
		changePlayOptions();
	}
	/**
	 * starts a new round of fighting, sets difficulty and loads level
	 * @param levelNumSet level to start
	 */
	protected void startFighting(int levelNumSet)
	{
		endFighting();
		levelNum = levelNumSet;
		difficultyLevelMultiplier *= 0.5+(Math.pow(getLevelWinningsMultiplier((int)(levelNum/10)+2), 0.2)/2); // set difficulty for level
		loadLevel(); // create enemies walls etc.
	}
	/**
	 * changes difficulty of level
	 * @param difficultyLevelSet new difficulty to set
	 */
	protected void changeDifficulty(int difficultyLevelSet)
	{
		difficultyLevel = difficultyLevelSet;
		difficultyLevelMultiplier = 15 / (double)(difficultyLevel + 5); // set difficulty multiplier
		activity.saveGame(); // saves game state in case of interuption
	}
	/**
	 * changes look of background when options are changed
	 */
	protected void changePlayOptions()
	{
		shootStick.x = 426;
		shootStick.y = 268;
		background = drawStart(); // redraws play screen
		invalidate();
		activity.saveGame(); // saves game state in case of interuption
	}
	/**
	 * creates a rectangle wall object
	 * @param x x position
	 * @param y y position
	 * @param width wall width
	 * @param height wall height
	 * @param HitPlayer whether wall interacts with player
	 * @param tall whether wall is tall enough to block projectiles
	 * @return wall object
	 */
	protected void makeWall_Rectangle(int x, int y, int width, int height, boolean HitPlayer, boolean tall)
	{
		wallRects.add(new Wall_Rectangle(this, x - 2, y - 2, width + 4, height + 4, HitPlayer, tall));
	}
	/**
	 * creates a ring wall object
	 * @param x x position
	 * @param y y position
	 * @param radIn inner radius
	 * @param radOut outer radius
	 * @return wall object
	 */
	protected void makeWall_Ring(int x, int y, int radIn, int radOut, boolean tall)
	{
		wallRings.add(new Wall_Ring(this, x, y, radIn - 2, radOut + 2, tall));
	}
	/**
	 * creates a passage through a ring
	 * @param x x position
	 * @param y y position
	 * @param width passage width
	 * @param height passage height
	 */
	protected void makeWall_Pass(int x, int y, int width, int height, boolean fullPass)
	{
		new Wall_Pass(this, x - 2, y - 2, width + 4, height + 4, fullPass);
	}
	/**
	 * creates circular wall object
	 * @param x x position
	 * @param y y position
	 * @param rad radius
	 * @param ratio ratio between width and height
	 * @param tall whether object is tall enough to block projectiles
	 * @return wall object
	 */
	protected void makeWall_Circle(int x, int y, int rad, double ratio, boolean tall)
	{
		wallCircles.add(new Wall_Circle(this, x, y, rad + 2, ratio, tall));
	}
	/**
	 * ends a round of fighting and resets variables
	 */
	protected void endFighting()
	{
		player.resetVariables(); // resets players variables
		moneyMade = 0;
		hasKey = false;
		clearObjectArrays();
		
		//TODO clean arrays
		
		clearWallArrays();
		
		gameEnded = false;
		imageLibrary.currentLevelTop = null;
		activity.saveGame(); // saves game in case phone shuts down etc.
	}
	private void clearObjectArrays()
	{
		saveEnemyInformation.clear();
		enemies.clear();
		structures.clear();
		powerUps.clear();
		proj_Trackers.clear();
		proj_Tracker_AOEs.clear();
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void loadLevel()
	{
		clearWallArrays();
		if(levelNum == 10)
		{			// *******EXAMPLE FOR COMMENTS FOR LOADLEVEL SECTION
			levelWidth = 450; // height of level
			levelHeight = 300; // width of level
			player.x = 30; // player start x
			player.y = 30; // player start y
			exitX = 35;
			exitY = 165;
			makeEnemy(1, 269, 86);
			makeEnemy(1, 358, 140, true);
			makeEnemy(1, 365, 204);
			makeEnemy(2, 146, 61);
			makeEnemy(2, 327, 231);
			makeWall_Rectangle(78, 122, 41, 24, true, false);
			makeWall_Rectangle(63, -20, 31, 142, true, true);
			makeWall_Rectangle(73, 238, 47, 62, true, true);
			makeWall_Rectangle(94, -19, 25, 152, true, false);
			makeWall_Rectangle(252, 269, 234, 62, true, true);
			makeWall_Rectangle(412, 82, 74, 250, true, true);
			makeWall_Rectangle(382, 133, 30, 83, true, false);
			makeWall_Circle(330, 297, 47, 1, false);
			makeWall_Rectangle(217, -15, 109, 81, true, false);
			makeWall_Rectangle(179, -32, 38, 63, true, true);
			makeWall_Rectangle(318, -41, 66, 63, true, true);
		}
		if(levelNum == 20)
		{
			levelWidth = 300; // height of level
			levelHeight = 660; // width of level
			player.x = 20; // player start x
			player.y = 640; // player start y
			exitX = 227;
			exitY = 610;
			makeEnemy(1, 54, 377);
			makeEnemy(1, 150, 100, true);
			makeEnemy(1, 35, 484);
			makeEnemy(1, 227, 333);
			makeEnemy(1, 35, 114);
			makeEnemy(1, 262, 110);
			makeEnemy(2, 150, 315);
			makeEnemy(2, 99, 195);
			makeEnemy(2, 213, 195);
			makeWall_Rectangle(105, 279, 15, 120, true, true);
			makeWall_Rectangle(180, 280, 15, 120, true, true);
			makeWall_Rectangle(-22, 565, 143, 16, true, true);
			makeWall_Rectangle(105, 461, 15, 120, true, true);
			makeWall_Rectangle(180, 565, 143, 16, true, true);
			makeWall_Rectangle(180, 460, 15, 120, true, true);
			makeWall_Rectangle(263, 571, 61, 76, true, true);
			makeWall_Rectangle(-9, 572, 47, 33, true, true);
			makeWall_Rectangle(262, 321, 47, 60, true, true);
			makeWall_Rectangle(262, 400, 47, 60, true, true);
			makeWall_Rectangle(262, 481, 47, 60, true, true);
			makeWall_Rectangle(-7, 253, 102, 41, true, true);
			makeWall_Rectangle(207, 253, 102, 42, true, true);
			makeWall_Rectangle(-55, -45, 425, 84, true, true);
			makeWall_Circle(225, 38, 56, 1, false);
			makeWall_Circle(74, 38, 56, 1, false);
			makeWall_Rectangle(216, 224, 89, 54, true, false);
			makeWall_Rectangle(241, 200, 29, 77, true, false);
			makeWall_Rectangle(10, 224, 79, 54, true, false);
			makeWall_Rectangle(35, 200, 29, 77, true, false);
			makeWall_Rectangle(0, 284, 111, 72, true, false);
			makeWall_Rectangle(0, 502, 114, 101, true, false);
			makeWall_Rectangle(-7, 585, 46, 35, true, false);
			makeWall_Rectangle(235, 637, 71, 35, true, false);
			makeWall_Rectangle(72, 637, 73, 43, true, false);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	private void clearWallArrays()
	{
		wallPassageValues.clear();
		wallRingValues.clear();
		wallRectValues.clear();
		wallCircleValues.clear();
		wallRects.clear();
		wallRings.clear();
		wallCircles.clear();
		//TODO
	}
	/**
	 * loads a new section of the current level
	 * @param level id of new section to load
	 */
	protected void loadLevelSection(int level)
	{
		Log.e("worked", "worked");
		clearWallArrays();
		levelNum = level;
		for(int i = 0; i < powerUps.size(); i++)
		{
			if(powerUps.get(i) != null) player.getPowerUp(powerUps.get(i).ID);
		}			 // READS IN AND CREATES ENEMIES IN NEW SECTION, SAVES ENEMIES IN OLD SECTION
			ArrayList<int[]> tempSave = (ArrayList<int[]>)saveEnemyInformation.clone();
			int j = 0;
			for(int i = 0; i < saveEnemyInformation.size(); i++)
			{
				if(!enemies.get(i).deleted)
				{
					saveEnemyInformation.get(j)[0] = enemies.get(i).enemyType;
					saveEnemyInformation.get(j)[1] = (int) enemies.get(i).x;
					saveEnemyInformation.get(j)[2] = (int) enemies.get(i).y;
					saveEnemyInformation.get(j)[3] = enemies.get(i).hp;
					if(enemies.get(i).keyHolder)
					{
						saveEnemyInformation.get(j)[4] = 1;
					}
					else
					{
						saveEnemyInformation.get(j)[4] = 0;
					}
					j++;
				}
			}
			endFightSection(tempSave);
		if(levelNum == 21)
		{
			levelWidth = 555;
			player.x = 25; 						// same format as loading levels
			makeEnemy(2, 99, 195);
			makeWall_Rectangle(503 - 506, -100, 15, 500, true, true);
			makeWall_Rectangle(665 - 506, -77, 15, 205, true, true);
			makeWall_Rectangle(665 - 506, 172, 15, 205, true, true);
			makeWall_Rectangle(825 - 506+40, -77, 15, 205, true, true);
			makeWall_Rectangle(825 - 506+40, 172, 15, 205, true, true);
			makeWall_Rectangle(1014 - 506+40, -77, 15, 205, true, true);
			makeWall_Rectangle(1014 - 506+40, 172, 15, 205, true, true);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	/**
	 * creates an enemy based off of saved info
	 * @param info array of stored values
	 * @param index which spot in enemy array to populate
	 */
	private void createEnemy(int[] info)
	{
		makeEnemy(info[0], info[1], info[2]);
		if(info[3] != 0) // if enemy has set health change it, otherwise leave as starting health
		{
			enemies.get(enemies.size()-1).hp = info[3];
		}
		if(info[4] == 1)
		{
			enemies.get(enemies.size()-1).keyHolder = true; // if saved enemy has key
		}
	}
	protected void makeEnemy(int type, int x, int y, boolean key)
	{
		makeEnemy(type, x, y);
		enemies.get(enemies.size()-1).keyHolder=true;
	}
	protected void makeEnemy(int type, int x, int y)
	{
		if(type==1)
		{
			enemies.add(new Enemy_Default(this, x, y, 2000, 9, //x, y, hp, worth 
					true, false, false, false, false, type)); //gun, sheild, hide, sword, sick
		} else if(type==2)
		{
			enemies.add(new Enemy_Default(this, x, y, 2000, 9, //x, y, hp, worth 
				false, true, false, true, false, type)); //gun, sheild, hide, sword, sick
		}
	}
	/**
	 * end a section of a fight, stored enemies in current states
	 * @param enemyData enemies to create
	 * @param tempEnemies number of enemies to create
	 */
	private void endFightSection(ArrayList<int[]> enemyData)
	{
		endFightSection();
		for(int i = 0; i < enemyData.size(); i++)
		{
			createEnemy(enemyData.get(i)); // CREATES SAVED ENEMIES
		}
	}
	/**
	 * ends a fight section with no saved enemies
	 */
	private void endFightSection()
	{
		clearObjectArrays();
		clearWallArrays();
	}
	/**
	 * fixes hp bar so it is on screen
	 * @param minX small x value of bar
	 * @param maxX large x value of bar
	 * @return offset so bar is on screen
	 */
	protected int fixXBoundsHpBar(int minX, int maxX)
	{
		int offset = 0;
		if(minX < 90) // IF TOO FAR LEFT FIX
		{
			offset = 90 - minX;
		}
		else if(maxX > 390) // IF TOO FAR RIGHT FIX
		{
			offset = 390 - maxX;
		}
		return offset;
	}
	/**
	 * fixes hp bar so it is on screen
	 * @param minY small y value of bar
	 * @param maxY large y value of bar
	 * @return offset so bar is on screen
	 */
	protected int fixYBoundsHpBar(int minY, int maxY)
	{
		int offset = 0;
		if(minY < 10) // IF TOO UP LEFT FIX
		{
			offset = 10 - minY;
		}
		else if(maxY > 310) // IF TOO FAR DOWN FIX
		{
			offset = 310 - maxY;
		}
		return offset;
	}
	/**
	 * draws all enemy health bars
	 * @param g canvas to draw to
	 */
	protected void drawHealthBars(Canvas g)
	{
		int minX;
		int maxX;
		int minY;
		int maxY;
		//int offset;
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
					minX = (int) enemies.get(i).x - 20;
					maxX = (int) enemies.get(i).x + 20;
					minY = (int) enemies.get(i).y - 30;
					maxY = (int) enemies.get(i).y - 20;
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					drawRect(minX, minY, minX + (40 * enemies.get(i).getHp() / enemies.get(i).getHpMax()), maxY, g);
					paint.setColor(Color.BLACK);
					paint.setStyle(Paint.Style.STROKE);
					drawRect(minX, minY, maxX, maxY, g);
			}
		}
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null)
			{
				minX = (int) structures.get(i).x - 20;
				maxX = (int) structures.get(i).x + 20;
				minY = (int) structures.get(i).y - 30;
				maxY = (int) structures.get(i).y - 20;
				paint.setColor(Color.BLUE);
				paint.setStyle(Paint.Style.FILL);
				drawRect(minX, minY, minX + (40 * structures.get(i).hp / structures.get(i).hpMax), maxY, g);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				drawRect(minX, minY, maxX, maxY, g);
			}
		}
	}
	/**
	 * Draws hp, mp, sp, and cooldown bars for player and enemies
	 * @param g canvas to draw to
	 */
	protected void drawContestantStats(Canvas g)
	{
		int fix = 390;
		paint.setAlpha(255);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		//drawRect(395, 240, 475, 316, g);
		//drawRect(5, 240, 85, 316, g);
		paint.setColor(healthColor);
		drawRect(400 - fix, 148, 400 - fix + (70 * player.getHp() / player.hpMax), 164, g);
		paint.setColor(specialColor);
		drawRect(400 - fix, 192, 400 - fix + (int)(140*player.getSp()/3), 208, g);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect(400 - fix, 148, 470 - fix, 164, g);
		drawRect(400 - fix, 192, 470 - fix, 208, g);
		paint.setTextSize(12);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.WHITE);
		drawText(Integer.toString(player.getHp()), 435 - fix, 158, g);
		drawText(Integer.toString((int)(2000*player.getSp())), 435 - fix, 202, g);
		//drawText(Integer.toString(activity.gameCurrency), 435 - fix, 205, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(cooldownColor);
		if(player.transformed==0)
		{
			drawRect(12 + fix, 101, 12 + fix + (int)((66 * player.getAbilityTimer_burst()) / 500), 111, g);
			drawRect(12 + fix, 205, 12 + fix + (int)((66 * player.getAbilityTimer_roll()) / 120), 215, g);
			drawRect(12 + fix, 300, 12 + fix + (int)((66 * player.getAbilityTimer_Proj_Tracker()) / (91+(activity.bReserve*20))), 310, g);
		} else if(player.transformed==1)
		{
			drawRect(12 + fix, 101, 12 + fix + (int)((66 * player.abilityTimerTransformed_pound) / 120), 111, g);
			drawRect(12 + fix, 205, 12 + fix + (int)((66 * player.abilityTimerTransformed_hit) / 20), 215, g);
			drawRect(90, 300, 90 + (int)((500 - player.transformedTimer) *3/5), 310, g);
		} else
		{
			drawRect(12 + fix, 101, 12 + fix + (int)((66 * player.abilityTimerTransformed_pound) / 120), 111, g);
			drawRect(12 + fix, 205, 12 + fix + (int)((66 * player.abilityTimerTransformed_hit) / 20), 215, g);
			drawRect(90, 300, 90 + (int)((500 - player.transformedTimer) *3/5), 310, g);
		}
		//drawRect(12 + fix, 134, 12 + fix + (int)((66 * player.getAbilityTimer_teleport()) / 350), 144, g);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawRect(12 + fix, 101, 78 + fix, 111, g);
		if(player.transformed==0) drawRect(12 + fix, 300, 78 + fix, 310, g);
		drawRect(12 + fix, 205, 78 + fix, 215, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(151);
			if(player.transformed==0)
			{
				if(player.getAbilityTimer_burst() < 300)
				{
					drawRect(12 + fix, 45, 78 + fix, 111, g);
				}
				if(player.getAbilityTimer_roll() < 40)
				{
					drawRect(12 + fix, 149, 78 + fix, 215, g);
				}
			} else if(player.transformed==0)
			{
				if(player.abilityTimerTransformed_pound < 100)
				{
					drawRect(12 + fix, 45, 78 + fix, 111, g);
				}
				if(player.abilityTimerTransformed_hit < 15)
				{
					drawRect(12 + fix, 149, 78 + fix, 215, g);
				}
			} else
			{
				if(player.abilityTimerTransformed_pound < 100)
				{
					drawRect(12 + fix, 45, 78 + fix, 111, g);
				}
				if(player.abilityTimerTransformed_hit < 15)
				{
					drawRect(12 + fix, 149, 78 + fix, 215, g);
				}
			}
		paint.setAlpha(255);
		if(player.transformed==0) drawBitmapRotated(shootStick, g);
	}
	/**
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	protected void frameCall()
	{
		playerHit++;
		playerBursted++;
		for(int i = 0; i < proj_Trackers.size(); i++)
		{
			if(proj_Trackers.get(i) != null)
			{
				if(proj_Trackers.get(i).deleted)
				{
					proj_Trackers.remove(i);
				}
				else
				{
					proj_Trackers.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < proj_Tracker_AOEs.size(); i++)
		{
			if(proj_Tracker_AOEs.get(i) != null)
			{
				if(proj_Tracker_AOEs.get(i).deleted)
				{
					proj_Tracker_AOEs.remove(i);
				}
				else
				{
					proj_Tracker_AOEs.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < powerUps.size(); i++)
		{
			if(powerUps.get(i) != null)
			{
				if(powerUps.get(i).deleted)
				{
					powerUps.remove(i);
				}
				else
				{
					powerUps.get(i).frameCall();
				}
			}
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				if(enemies.get(i).deleted)
				{
					enemies.remove(i);
				}
				else
				{
					enemies.get(i).levelCurrentPosition = 0;
					enemies.get(i).pathedToHitLength = 0;
					if(enemyInView(enemies.get(i).x, enemies.get(i).y))
					{
						enemies.get(i).frameCall();
						if(enemies.get(i) != null)
						{
							if(enemies.get(i).x < 10) enemies.get(i).x = 10;
							if(enemies.get(i).x > levelWidth - 10) enemies.get(i).x = (levelWidth - 10);
							if(enemies.get(i).y < 10) enemies.get(i).y = 10;
							if(enemies.get(i).y > levelHeight - 10) enemies.get(i).y = (levelHeight - 10);
						}
					}
				}
			}
		}
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null)
			{
				if(structures.get(i).deleted)
				{
					structures.remove(i);
				}
				else
				{
					if(enemyInView(structures.get(i).x, structures.get(i).y))
					{
						structures.get(i).frameCall();
					}
				}
			}
		}
		if(hasKey && getDistance(player.x, player.y, exitX, exitY) < 30)
		{
			activity.winFight();
		}
		else
		{
			if(!player.deleted)
			{
				player.frameCall();
					if(player.x < 10) player.x = (10);
					if(player.x > levelWidth - 10) player.x = (levelWidth - 10);
					if(player.y < 10) player.y = (10);
					if(player.y > levelHeight - 10) player.y = (levelHeight - 10);
			}
		}
		if(activity.gameRunning)
		{
			if(levelNum == 20)
			{
				if(player.x > 619 && player.y > 140 && player.y < 160)
				{
					loadLevelSection(21);
				}
			}
			if(levelNum == 21)
			{
				if(player.x > 544 && player.y > 140 && player.y < 160)
				{
					loadLevelSection(22);
				}
			}
			for(int i = 0; i < wallRects.size(); i++)
			{
					wallRects.get(i).frameCall();
			}
			for(int i = 0; i < wallCircles.size(); i++)
			{
					wallCircles.get(i).frameCall();
			}
			for(int i = 0; i < wallRings.size(); i++)
			{
					wallRings.get(i).frameCall();
			}
			invalidate();
		}
		activity.resetVolume();
	}
	protected boolean playerOnSquare(double x1, double y1, double width, double height)
	{
		double x2 = x1+width;
		double y2 = y1+height;
		return (player.x<x2&&player.x>x1&&player.y<y2&&player.y>y1);
	}
	/**
	 * returns distance squared between two objects
	 * @param x1 first x position
	 * @param y1 first y position
	 * @param x2 second x position
	 * @param y2 second y position
	 * @return distance between points squared
	 */
	protected double distSquared(double x1, double y1, double x2, double y2)
	{
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	/**
	 * draws background of play screen
	 * @return bitmap of play screen
	 */
	protected Bitmap drawStart()
	{
		paint.setAlpha(255);
		Bitmap drawTo = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		drawBitmap(imageLibrary.loadImage("menu_screen", 480, 320), 0, 0, g);
		return drawTo;
	}
	/**
	 * draws the level and objects in it
	 * @return bitmap of level and objects
	 */
	protected Bitmap drawLevel()
	{
		Bitmap drawTo = Bitmap.createBitmap(levelWidth, levelHeight, Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		int w = 0;
		int h = 0;
		while(w < levelWidth)
		{
			while(h < levelHeight)
			{
				drawBitmapLevel(imageLibrary.backDrop, w, h, g);
				h += 90;
			}
			w += 90;
			h = 0;
		}
		drawBitmap(imageLibrary.currentLevel, 0, 0, g);
		drawBitmapLevel(imageLibrary.exitFightPortal, exitX - 30, exitY - 30, g);
		for(int i = 0; i < structures.size(); i++)
		{
			if(structures.get(i) != null)
			{
				drawBitmapLevel(imageLibrary.structure_Spawn, (int)structures.get(i).x-structures.get(i).width, (int)structures.get(i).y-structures.get(i).height, g);
			}
		}
		if(player != null)
		{
			drawBitmap(imageLibrary.isPlayer, (int)player.x-imageLibrary.isPlayerWidth, (int)player.y-imageLibrary.isPlayerWidth, g);
			drawBitmapRotatedLevel(player, g);
			if(player.transformedTimer>1&&player.transformedTimer<500)
			{
				int frame = player.transformedTimer;
				while(frame>9)
				{
					frame -= 10;
				}
				drawBitmapLevel(imageLibrary.trans[frame], (int)player.x - 60, (int)player.y - 60, g);
			}
		}
		for(int i = 0; i < enemies.size(); i++)
		{
			if(enemies.get(i) != null)
			{
				if(enemies.get(i).keyHolder)
				{
					drawBitmapLevel(imageLibrary.haskey, (int) enemies.get(i).x - 20, (int) enemies.get(i).y - 20, g);
				}
				drawBitmapRotatedLevel(enemies.get(i), g);
			}
		}
		for(int i = 0; i < proj_Trackers.size(); i++)
		{
			if(proj_Trackers.get(i) != null)
			{
				drawBitmapRotatedLevel(proj_Trackers.get(i), g);
			}
		}
		for(int i = 0; i < proj_Tracker_AOEs.size(); i++)
		{
			if(proj_Tracker_AOEs.get(i) != null)
			{
				aoeRect.top = (int)(proj_Tracker_AOEs.get(i).y - (proj_Tracker_AOEs.get(i).getHeight() / 2.5));
				aoeRect.bottom = (int)(proj_Tracker_AOEs.get(i).y + (proj_Tracker_AOEs.get(i).getHeight() / 2.5));
				aoeRect.left = (int)(proj_Tracker_AOEs.get(i).x - (proj_Tracker_AOEs.get(i).getWidth() / 2.5));
				aoeRect.right = (int)(proj_Tracker_AOEs.get(i).x + (proj_Tracker_AOEs.get(i).getWidth() / 2.5));
				paint.setAlpha(proj_Tracker_AOEs.get(i).getAlpha());
				drawBitmapRectLevel(proj_Tracker_AOEs.get(i).getVisualImage(), aoeRect, g);
			}
		}
		paint.setAlpha(255);
		for(int i = 0; i < powerUps.size(); i++)
		{
			if(powerUps.get(i) != null)
			{
				drawBitmapLevel(powerUps.get(i).getVisualImage(), (int) powerUps.get(i).x - 15, (int) powerUps.get(i).y - 15, g);
			}
		}
		if(imageLibrary.currentLevelTop != null)
		{
			drawBitmapLevel(imageLibrary.currentLevelTop, 0, 0, g);
		}
		if(player.powerUpTimer > 0)
		{
			drawBitmapLevel(imageLibrary.effects[player.powerID - 1], (int) player.x - 30, (int) player.y - 30, g);
		}
		drawHealthBars(g);
		return drawTo;
	}
	/**
	 * checks whether object is in view
	 * @param lowx objects low x
	 * @param lowy objects low y
	 * @param width objects width
	 * @param height objects height
	 * @return whether object is in view
	 */
	private boolean inView(int lowx, int lowy, int width, int height)
	{
		lowx += curXShift;
		int highx = lowx + width;
		lowy += curYShift;
		int highy = lowy + height;
		return !(lowx > 300 || highx < 0 || lowy > 300 || highy < 0);
	}
	/**
	 * checks whether enemy is in view
	 * @param x enemy x
	 * @param y enemy y
	 * @return whether enemy is in view
	 */
	protected boolean enemyInView(double x, double y)
	{
		return !(x + curXShift > 400 || x + curXShift < -100 || y + curYShift > 400 || y + curYShift < -100);
	}
	protected double getLevelWinningsMultiplier(int level)
	{
		if(level==0)
		{
			return 0;
		} else
		{
			return 1 + ((double)(level-1)/(double)10);
		}
	}
	protected String getLevelName(int level)
	{
		switch(level)
		{
		case 0:
			return "Tutorial";
		case 1:
			return "Broken Sanctuary";
		case 2:
			return "Beyond the Gate";
		case 3:
			return "Mouldy Tavern";
		case 4:
			return "The Chambers";
		case 5:
			return "Orientation";
		case 6:
			return "The Outpost";
		case 7:
			return "War Preparation";
		case 8:
			return "The Labyrinth";
		case 9:
			return "Back to Town";
		case 10:
			return "Temple of Fire";
		case 11:
			return "Temple of Ice";
		case 12:
			return "Temple of Earth";
		case 13:
			return "Goblin Nest";
		case 14:
			return "The Hordes Return";
		default:
			return "Default";
		}
	}
	@Override
	protected void onDraw(Canvas g)
	{
		if(activity.gameRunning)
		{
			g.translate(screenMinX, screenMinY);
			g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
			drawNotPaused(g);
		}
	}
	/**
	 * draw normal unpaused screen
	 * @param g canvas to draw to
	 */
	private void drawNotPaused(Canvas g)
	{
		paint.setTextAlign(Align.LEFT);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		drawRect(90, 10, 390, 310, g);
			xShiftLevel = 150 - (int) player.x;
			yShiftLevel = 150 - (int) player.y;
			if(player.x < 150)
			{
				xShiftLevel = 0;
			}
			if(player.y < 150)
			{
				yShiftLevel = 0;
			}
			if(player.x > levelWidth - 150)
			{
				xShiftLevel = 300 - levelWidth;
			}
			if(player.y > levelHeight - 150)
			{
				yShiftLevel = 300 - levelHeight;
			}
		curXShift = xShiftLevel;
		curYShift = yShiftLevel;
		drawBitmap(drawLevel(), xShiftLevel + 90, yShiftLevel + 10, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		drawRect(-1000, -1000, 0, 1320, g);
		drawRect(480, -1000, 1480, 1320, g);
		drawRect(-1000, -1000, 1480, 0, g);
		drawRect(-1000, 320, 1480, 1320, g);
		if(playerBursted<6)
		{
			paint.setColor(Color.WHITE);
			paint.setStyle(Paint.Style.FILL);
			paint.setAlpha(255-(30*playerBursted));
			drawRect(90, 10, 390, 310, g);
		}
		if(playerHit<6)
		{
			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL);
			paint.setAlpha(100-(15*playerHit));
			drawRect(90, 10, 390, 310, g);
		}
		paint.setAlpha(255);
		paint.setColor(Color.GREEN);
		paint.setAlpha(255);
		drawBitmap(background, 0, 0, g);
		if(player.transformedTimer>1&&player.transformedTimer<500)
		{
			drawBitmap(imageLibrary.transattack, 402, 149, g);
		}
		drawContestantStats(g);
		paint.setStyle(Paint.Style.STROKE);
		if(player.powerUpTimer > 0)
		{
			drawBitmap(imageLibrary.powerUpBigs[player.powerID - 1], 10, 25, g);
		}
		if(hasKey)
		{
			drawBitmap(imageLibrary.powerUpBigs[4], 10, 25, g);
		}
	}
	/**
	 * creates an enemy power ball
	 * @param rotation rotation of Proj_Tracker
	 * @param xVel horizontal velocity of ball
	 * @param yVel vertical velocity of ball
	 * @param power power of ball
	 * @param x x position
	 * @param y y position
	 */
	protected void createProj_TrackerEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		proj_Trackers.add(new Proj_Tracker_Enemy(this, (int) (x+xVel*2), (int) (y+yVel*2), power, xVel, yVel, rotation));
	}
	/**
	 * creates an enemy crossbow bolt
	 * @param rotation rotation of bolt
	 * @param xVel horizontal velocity of bolt
	 * @param yVel vertical velocity of bolt
	 * @param power power of bolt
	 * @param x x position
	 * @param y y position
	 */
	protected void createCrossbowBolt(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		proj_Trackers.add(new CrossbowBolt(this, (int) (x+xVel), (int) (y+yVel), power, xVel, yVel, rotation));
	}
	/**
	 * creates a consumable the player can pick up
	 * @param X x position
	 * @param Y y position
	 * @param ID 0:random power, 1-6:power, 7:coin1, 8:key, 9:coin5, 10:coin20
	 */
	protected void createConsumable(double X, double Y, int ID) // 0: random powerup or
	{															// 1-6:powerups 7:coin1
		powerUps.add(new PowerUp(this, X, Y, 7));				// 9: coin5, 10:coin20, 8:key 
	}
	/**
	 * creates a player power ball
	 * @param rotation rotation of bolt
	 * @param xVel horizontal velocity of bolt
	 * @param yVel vertical velocity of bolt
	 * @param power power of bolt
	 * @param x x position
	 * @param y y position
	 */
	protected void createProj_TrackerPlayer(double rotation, double Vel, int power, double x, double y)
	{
		proj_Trackers.add(new Proj_Tracker_Player(this, (int)x, (int)y, power, Vel, rotation));
	}
	/**
	 * creates an emeny AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 * @param damaging whether it damages player
	 */
	protected void createProj_TrackerEnemyAOE(double x, double y, double power, boolean damaging)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Enemy(this, (int) x, (int) y, power, true));
		if(!damaging) proj_Tracker_AOEs.get(proj_Tracker_AOEs.size()-1).damaging = false;
	}
	/**
	 * creates a player AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerAOE(double x, double y, double power, boolean damaging)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Player(this, (int) x, (int) y, power, true));
		if(!damaging) proj_Tracker_AOEs.get(proj_Tracker_AOEs.size()-1).damaging = false;
	}
	/**
	 * creates an enemy burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerEnemyBurst(double x, double y, double power)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Enemy(this, (int) x, (int) y, power, false));
	}
	/**
	 * creates a player burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerBurst(double x, double y, double power)
	{
		proj_Tracker_AOEs.add(new Proj_Tracker_AOE_Player(this, (int) x, (int) y, power, false));
	}
	/**
	 * Starts warning label
	 * @param warning
	 */
	protected void startWarningImediate(String warning)
	{
		AlertDialog.Builder bld = new AlertDialog.Builder(context);
		bld.setMessage(warning);
		bld.setNeutralButton("OK", null);
		bld.create().show();
	}
	/**
	 * checks whether a projectile could travel between two points
	 * @param x1 first x
	 * @param y1 first y
	 * @param x2 second x
	 * @param y2 second y
	 * @return whether it could travel between points
	 */
	protected boolean checkObstructionsPoint(float x1, float y1, float x2, float y2, boolean objectOnGround)
	{
		boolean hitBack = false;
		float m1 = (y2 - y1) / (x2 - x1);
		float b1 = y1 - (m1 * x1);
		float circM;
		float circB;
		float tempX;
		float tempY;
		if(x1 < 0 || x1 > levelWidth || y1 < 0 || y1 > levelHeight)
		{
			hitBack = true;
		}
		if(x2 < 0 || x2 > levelWidth || y2 < 0 || y2 > levelHeight)
		{
			hitBack = true;
		}
		for(int i = 0; i < wallCircleValues.size(); i++)
		{
			int [] values = wallCircleValues.get(i);
			if(values[3]==1||objectOnGround) // OBJECT IS TALL OR OBJECT ON GROUND
			{
				if(!hitBack)
				{
					circM = -(1 / m1);
					circB = values[1] - (circM * values[0]);
					tempX = (circB - b1) / (m1 - circM);
					if(x1 < tempX && tempX < x2)
					{
						tempY = (circM * tempX) + circB;
						if(Math.sqrt(Math.pow(tempX - values[0], 2) + Math.pow((tempY - values[1]), 2)) < values[2])
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(x1 > x2)
		{
			tempX = x1;
			x1 = x2;
			x2 = tempX;
		}
		if(y1 > y2)
		{
			tempY = y1;
			y1 = y2;
			y2 = tempY;
		}
		for(int i = 0; i < wallRectValues.size(); i++)
		{
			int [] values = wallRectValues.get(i);
			if(values[4]==1||objectOnGround) // OBJECT IS TALL
			{
				if(!hitBack)
				{
					//Right and left Checks
					if(x1 < values[0] && values[0] < x2)
					{
						tempY = (m1 * values[0]) + b1;
						if(values[2] < tempY && tempY < values[3])
						{
							hitBack = true;
						}
					}
					if(x1 < values[1] && values[1] < x2)
					{
						tempY = (m1 * values[1]) + b1;
						if(values[2] < tempY && tempY < values[3])
						{
							hitBack = true;
						}
					}
					//Top and Bottom checks
					if(y1 < values[2] && values[2] < y2)
					{
						tempX = (values[2] - b1) / m1;
						if(values[0] < tempX && tempX < values[1])
						{
							hitBack = true;
						}
					}
					if(y1 < values[3] && values[3] < y2)
					{
						tempX = (values[2] - b1) / m1;
						if(values[0] < tempX && tempX < values[1])
						{
							hitBack = true;
						}
					}
				}
			}
		}
		for(int i = 0; i < wallRingValues.size(); i++)
		{
			int [] values = wallRingValues.get(i);
			if(values[4]==1||objectOnGround) // OBJECT IS TALL
			{
				if(!hitBack)
				{
					double a = Math.pow(m1, 2) + 1;
					double b = 2 * ((m1 * b1) - (m1 * values[1]) - (values[0]));
					double c = Math.pow(b1, 2) + (Math.pow(values[1], 2)) - (2 * b1 * values[1]) + Math.pow(values[0], 2) - Math.pow(140, 2);
					double temp = (-4 * a * c) + Math.pow(b, 2);
					if(temp >= 0)
					{
						double change = Math.sqrt(temp);
						double pointX1 = (-b + change) / (2 * a);
						double pointX2 = (-b - change) / (2 * a);
						if(x1 < pointX1 && pointX1 < x2)
						{
							double pointY = (m1 * pointX1) + b1;
							if(!checkHitBackPass(pointX1, pointY, objectOnGround))
							{
								hitBack = true;
							}
						}
						if(x1 < pointX2 && pointX2 < x2)
						{
							double pointY = (m1 * pointX2) + b1;
							if(!checkHitBackPass(pointX2, pointY, objectOnGround))
							{
								hitBack = true;
							}
						}
					}
				}
			}
		}
		return hitBack;
	}
	/**
	 * returns distance between two points
	 *  @param x1 first x
	 * @param y1 first y
	 * @param x2 second x
	 * @param y2 second y
	 * @return distance between points
	 */
	protected double getDistance(double x1, double y1, double x2, int y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	/**
	 * checks whether a projectile could travel along a given line
	 * @param x1 start x
	 * @param y1 start y
	 * @param rads direction to travel
	 * @param distance distance to travel
	 * @return whether it could travel along the given line
	 */
	protected boolean checkObstructions(double x1, double y1, double rads, int distance, boolean objectOnGround)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPoint((float) x1, (float) y1, (float) x2, (float) y2, objectOnGround);
	}
	/**
	 * checks whether a given point hits any obstacles
	 * @param X x point
	 * @param Y y point
	 * @return whether it hits
	 */
	protected boolean checkHitBack(double X, double Y, boolean objectOnGround)
	{
		boolean hitBack = false;
		if(X < 0 || X > levelWidth || Y < 0 || Y > levelHeight)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < wallRectValues.size(); i++)
			{
				int [] values = wallRectValues.get(i);
				if(values[4]==1||objectOnGround) // OBJECT IS TALL
				{
					if(hitBack == false)
					{
						if(X > values[0] && X < values[1])
						{
							if(Y > values[2] && Y < values[3])
							{
								hitBack = true;
							}
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < wallCircleValues.size(); i++)
			{
				int [] values = wallCircleValues.get(i);
				if(values[3]==1||objectOnGround) // OBJECT IS TALL
				{
					if(hitBack == false)
					{
						if(Math.pow(X - values[1], 2) + Math.pow((Y - values[2]), 2) < Math.pow(values[3], 2))
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < wallRingValues.size(); i++)
			{
				int [] values = wallRingValues.get(i);
				if(values[4]==1||objectOnGround) // OBJECT IS TALL
				{
					if(hitBack == false)
					{
						double dist = Math.pow(X - values[0], 2) + Math.pow((Y - values[1]), 2);
						if(dist < Math.pow(values[3], 2) && dist > Math.pow(values[2], 2))
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack)
		{
			hitBack = !checkHitBackPass(X, Y, objectOnGround);
		}
		return hitBack;
	}
	/**
	 * checks whether a given point hits any passages
	 * @param X x point
	 * @param Y y point
	 * @return whether it hits
	 */
	protected boolean checkHitBackPass(double X, double Y, boolean objectOnGround)
	{
		boolean hitBack = false;
		for(int i = 0; i < wallPassageValues.size(); i++)
		{
			if(hitBack == false)
			{
				int [] values = wallRingValues.get(i); // valeus[4] is true when passage is for lower and upper area
				if(values[4]==1||!objectOnGround) // OBJECT IS TALL
				{
					if(X > values[0] && X < values[1])
					{
						if(Y > values[2] && Y < values[3])
						{
							hitBack = true;
						}
					}
				}
			}
		}
		return hitBack;
	}
	
	protected double visualX(double x)
	{
		return(x - screenMinX) / screenDimensionMultiplier;
	}
	/**
	 * converts value from click y point to where on the screen it would be
	 * @param y y value of click
	 * @return y position of click on screen
	 */
	protected double visualY(double y)
	{
		return((y - screenMinY) / screenDimensionMultiplier);
	}
	/**
	 * returns whether a point clicked is on  the screen
	 * @param x x position
	 * @param y y position
	 * @return whether it is on screen
	 */
	protected boolean pointOnScreen(double x, double y)
	{
		x = visualX(x);
		y = visualY(y);
		if(x > 90 && x < 390 && y > 10 && y < 310)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * returns whether a point is on a given square
	 * @param x x position
	 * @param y y position
	 * @param lowX left hand side of square
	 * @param lowY top of square
	 * @param highX right hand side of square
	 * @param highY bottom of square
	 * @return whether it is on square
	 */
	protected boolean pointOnSquare(double x, double y, double lowX, double lowY, double highX, double highY)
	{
		x = visualX(x);
		y = visualY(y);
		if(x > lowX && x < highX && y > lowY && y < highY)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * returns whether a point is on a given circle
	 * @param x x position
	 * @param y y position
	 * @param midX x position
	 * @param midY y position
	 * @param radius radius of circle
	 * @return whether it is on circle
	 */
	protected boolean pointOnCircle(double x, double y, double midX, double midY, double radius)
	{
		x = visualX(x);
		y = visualY(y);
		if(Math.sqrt(Math.pow(x - midX, 2) + Math.pow(y - midY, 2)) < radius)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	/**
	 * returns difficultyLevel
	 * @return difficultyLevel
	 */
	protected int getDifficultyLevel()
	{
		return difficultyLevel;
	}
	/**
	 * returns difficultyLevelMultiplier
	 * @return difficultyLevelMultiplier
	 */
	protected double getDifficultyLevelMultiplier()
	{
		return difficultyLevelMultiplier;
	}
	/**
	 * returns random integer between 0 and i-1
	 * @param i returns int between one less than this and 0
	 * @return random integer between 0 and i-1
	 */
	protected int getRandomInt(int i)
	{
		return randomGenerator.nextInt(i);
	}
	/**
	 * returns random double between 0 and 1
	 * @return random double between 0 and 1
	 */
	protected double getRandomDouble()
	{
		return randomGenerator.nextDouble();
	}
	/**
	 * returns levelNum
	 * @return levelNum
	 */
	protected int getLevelNum()
	{
		return levelNum;
	}
	/**
	 * returns whether game has ended
	 * @return whether game has ended
	 */
	protected boolean getGameEnded()
	{
		return gameEnded;
	}
	/**
	 * sets values for an index of all rectangle tall wall value arrays
	 * @param i index to set values to
	 * @param oRectX1 left x
	 * @param oRectX2 right x
	 * @param oRectY1 top y
	 * @param oRectY2 bottom y
	 */
	protected void setORect(int left, int right, int top, int bottom, int tall)
	{
		int [] vals = {left, right, top, bottom, tall};
		wallRectValues.add(vals);
	}
	/**
	 * sets values for an index of all passage wall value arrays
	 * @param i index to set values to
	 * @param oRectX1 left x
	 * @param oRectX2 right x
	 * @param oRectY1 top y
	 * @param oRectY2 bottom y
	 */
	protected void setOPassage(int left, int right, int top, int bottom, int fullyOpen)
	{
		int [] vals = {left, right, top, bottom, fullyOpen};
		wallPassageValues.add(vals);
	}
	/**
	 * sets values for an index of all tall circle wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oCircRadius radius
	 * @param oCircRatio ratio between width and height
	 */
	protected void setOCirc(int xVal, int yVal, int radiusVal, int tall)
	{
		int [] vals = {xVal, yVal, radiusVal, tall};
		wallCircleValues.add(vals);
	}
	/**
	 * sets values for an index of all ring wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oRingIn inner ring radius
	 * @param oRingOut outer ring radius
	 */
	protected void setORing(int xVal, int yVal, int radiusInVal, int radiusOutVal, int tall)
	{
		int [] vals = {xVal, yVal, radiusInVal, radiusOutVal, tall};
		wallRingValues.add(vals);
	}
	/**
	 * Replaces canvas.drawRect(int, int, int, int, Paint) and auto scales
	 */
	protected void drawRect(int x, int y, int x2, int y2, Canvas g)
	{
		g.drawRect(x, y, x2, y2, paint);
	}
	/**
	 * Replaces canvas.drawCircle(int, int, int paint) and auto scales
	 */
	protected void drawCircle(int x, int y, int radius, Canvas g)
	{
		g.drawCircle(x, y, radius, paint);
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, int, int, paint) and auto scales
	 */
	protected void drawBitmap(Bitmap picture, int x, int y, Canvas g)
	{
		g.drawBitmap(picture, x, y, paint);
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawBitmapRotated(DrawnSprite sprite, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-sprite.getVisualImage().getWidth() / 2, -sprite.getVisualImage().getHeight() / 2);
		rotateImages.postRotate((float) sprite.rotation);
		rotateImages.postTranslate((float) sprite.x, (float) sprite.y);
		g.drawBitmap(sprite.getVisualImage(), rotateImages, paint);
		sprite = null;
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	protected void drawBitmapRect(Bitmap picture, Rect rectangle, Canvas g)
	{
		g.drawBitmap(picture, null, rectangle, paint);
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and only draws object if it is in view
	 */
	protected void drawBitmapLevel(Bitmap picture, int x, int y, Canvas g)
	{
		if(inView(x, y, picture.getWidth(), picture.getHeight())) g.drawBitmap(picture, x, y, paint);
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawBitmapRotatedLevel(DrawnSprite sprite, Canvas g)
	{
		int width = sprite.getVisualImage().getWidth();
		int height = sprite.getVisualImage().getHeight();
		if(inView((int) sprite.x - (width / 2) - 10, (int) sprite.y - (height / 2) - 10, width + 20, height + 20))
		{
			rotateImages.reset();
			rotateImages.postTranslate(-width / 2, -height / 2);
			rotateImages.postRotate((float) sprite.rotation);
			rotateImages.postTranslate((float) sprite.x, (float) sprite.y);
			g.drawBitmap(sprite.getVisualImage(), rotateImages, paint);
			sprite = null;
		}
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	protected void drawBitmapRectLevel(Bitmap picture, Rect rectangle, Canvas g)
	{
		if(inView(rectangle.left, rectangle.top, rectangle.bottom - rectangle.top, rectangle.right - rectangle.left))
		{
			g.drawBitmap(picture, null, rectangle, paint);
		}
	}
	/**
	 * Replaces canvas.drawText(String, int, int, Paint) and auto scales
	 */
	protected void drawText(String text, int x, int y, Canvas g)
	{
		// TODO
		g.drawText(text, x, y, paint);
	}
}