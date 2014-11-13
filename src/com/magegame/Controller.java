
/** Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
 * @param DifficultyLevel Chosen difficulty setting which dictates enemy reaction time and DifficultyLevelMultiplier
 * @param DifficultyLevelMultiplier Function of DifficultyLevel which changes enemy health, mana, speed
 * @param EnemyType Mage type of enemy
 * @param PlayerType Mage type of player
 * @param LevelNum Level chosen to fight on
 * @param player Player object that has health etc and generates movement handler
 * @param enemy Enemy object with health etc and ai
 * @param enemies Array of all enemies currently on screen excluding main mage enemy
 * @param Proj_Trackers Array of all enemy or player Proj_Trackers
 * @param Proj_Tracker_AOEs Array of all enemy or player Proj_Tracker explosions
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
	protected Enemy[] enemies = new Enemy[30];
	protected Structure[] structures = new Structure[10];
	private PowerUp[] powerUps = new PowerUp[30];
	private Proj_Tracker[] Proj_Trackers = new Proj_Tracker[30];
	private Proj_Tracker_AOE[] Proj_Tracker_AOEs = new Proj_Tracker_AOE[30];
	private Wall_Rectangle[] walls = new Wall_Rectangle[30];
	private Wall_Circle[] wallCircles = new Wall_Circle[30];
	private Wall_Ring[] wallRings = new Wall_Ring[30];
	private Rect aoeRect = new Rect();
	private boolean gameEnded = false;
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator = new Random();
	protected int difficultyLevel = 10;
	private double difficultyLevelMultiplier;
	protected int levelNum = 10;
	private int warningTimer;
	private String warningText = "";
	private int[] oPassageX1;
	private int[] oPassageX2;
	private int[] oPassageY1;
	private int[] oPassageY2;
	private int[] oRingX;
	private int[] oRingY;
	private int[] oRingInner;
	private int[] oRingOuter;
	private int[] oRingXAll;
	private int[] oRingYAll;
	private int[] oRingInnerAll;
	private int[] oRingOuterAll;
	private int[] oRectX1;
	private int[] oRectX2;
	private int[] oRectY1;
	private int[] oRectY2;
	private int[] oCircX;
	private int[] oCircY;
	private int[] oCircRadius;
	private double[] oCircRatio;
	private int[] oRectX1All;
	private int[] oRectX2All;
	private int[] oRectY1All;
	private int[] oRectY2All;
	private int[] oCircXAll;
	private int[] oCircYAll;
	private int[] oCircRadiusAll;
	private double[] oCircRatioAll;
	private int currentCircle = 0;
	private int currentRectangle = 0;
	private int currentCircleAll = 0;
	private int currentRectangleAll = 0;
	private int currentRing = 0;
	private int currentRingAll = 0;
	private int currentPassage = 0;
	private Bitmap background;
	protected Bitmap tempPicture;
	protected Bitmap tempPictureLock;
	protected PlayerGestureDetector detect;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected int xShiftLevel;
	protected int yShiftLevel;
	protected boolean gamePaused = false;
	private Handler mHandler = new Handler();
	protected String currentPause;
	private Bitmap check;
	protected String buyingItem;
	protected int startingLevel;
	protected boolean drainHp = false;
	protected boolean lowerHp = false;
	protected boolean limitSpells = false;
	protected boolean enemyRegen = false;
	protected double moneyMultiplier = 0;
	protected double moneyMade = 0;
	private int savedEnemies = 0;
	private int[][] saveEnemyInformation = new int[30][5];
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
	private int[] godColors = {Color.rgb(150, 98, 50), Color.rgb(52, 84, 125), Color.rgb(202, 189, 105), Color.rgb(101, 42, 10)};
	protected DrawnSprite shootStick = new Graphic_shootStick();
	private long timeLast;
	private byte times;
	protected int playerHit=0;
	protected int playerBursted = 0;
	Typeface magicMedieval; 
	private Runnable frameCaller = new Runnable()
	{
		/**
		 * calls most objects 'frameCall' method (walls enemies etc)
		 */
		public void run()
		{
			if(!gamePaused && activity.gameRunning) // if game is running call framecalls
			{
				frameCall();
			}
			times++;
			if(times == 10)
			{
				timeLast = System.nanoTime();
				times = 0;
			}
			mHandler.postDelayed(this, 50);
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
		magicMedieval = Typeface.createFromAsset(context.getAssets(),"fonts/MagicMedieval.ttf"); 
		imageLibrary = new ImageLibrary(startSet, this); // creates image library
		screenMinX = activitySet.screenMinX;
		screenMinY = activitySet.screenMinY;
		screenDimensionMultiplier = activitySet.screenDimensionMultiplier;
		paint.setAntiAlias(false);
		paint.setDither(false);
		paint.setTypeface(magicMedieval);
		if(activity.highGraphics)
		{
			//paint.setAntiAlias(true);
			paint.setFilterBitmap(true);
			//paint.setDither(true);
		} else
		{
			//paint.setAntiAlias(false);
			paint.setFilterBitmap(false);
			//paint.setDither(false);
		}
		setBackgroundColor(Color.WHITE);
		setKeepScreenOn(true); // so screen doesnt shut off when game is left inactive
		player = new Player(this); // creates player object
		detect = new PlayerGestureDetector(this); // creates gesture detector object
		setOnTouchListener(detect);
		detect.setPlayer(player);
		changeDifficulty(10);
		startFighting(10);
		frameCaller.run();
		check = imageLibrary.loadImage("menu_check", 20, 20);
		changePlayOptions();
		shootStick.visualImage = imageLibrary.loadImage("icon_shoot", 70, 35);
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
	protected Wall_Rectangle makeWall_Rectangle(int x, int y, int width, int height, boolean HitPlayer, boolean tall)
	{
		Wall_Rectangle wall1 = new Wall_Rectangle(this, x - 2, y - 2, width + 4, height + 4, HitPlayer, tall);
		return wall1;
	}
	/**
	 * creates a ring wall object
	 * @param x x position
	 * @param y y position
	 * @param radIn inner radius
	 * @param radOut outer radius
	 * @return wall object
	 */
	protected Wall_Ring makeWall_Ring(int x, int y, int radIn, int radOut, boolean tall)
	{
		Wall_Ring wall1 = new Wall_Ring(this, x, y, radIn - 2, radOut + 2, tall);
		return wall1;
	}
	/**
	 * creates a passage through a ring
	 * @param x x position
	 * @param y y position
	 * @param width passage width
	 * @param height passage height
	 */
	protected void makeWall_Pass(int x, int y, int width, int height)
	{
		new Wall_Pass(this, x - 2, y - 2, width + 4, height + 4);
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
	protected Wall_Circle makeWall_Circle(int x, int y, int rad, double ratio, boolean tall)
	{
		Wall_Circle wall1 = new Wall_Circle(this, x, y, rad + 2, ratio, tall);
		return wall1;
	}
	/**
	 * creates an empty array of rectangular wall variables
	 * @param length desired length of arrays
	 */
	protected void createWallRectangleValueArrays(int length)
	{
		oRectX1 = new int[length];
		oRectX2 = new int[length];
		oRectY1 = new int[length];
		oRectY2 = new int[length];
	}
	/**
	 * creates an empty array of circular wall variables
	 * @param length desired length of arrays
	 */
	protected void createWallCircleValueArrays(int length)
	{
		oCircX = new int[length];
		oCircY = new int[length];
		oCircRadius = new int[length];
		oCircRatio = new double[length];
	}
	/**
	 * creates an empty array of tall rectangular wall variables
	 * @param length desired length of arrays
	 */
	protected void createWallRectangleValueArraysAll(int length)
	{
		oRectX1All = new int[length];
		oRectX2All = new int[length];
		oRectY1All = new int[length];
		oRectY2All = new int[length];
	}
	/**
	 * creates an empty array of tall circular wall variables
	 * @param length desired length of arrays
	 */
	protected void createWallCircleValueArraysAll(int length)
	{
		oCircXAll = new int[length];
		oCircYAll = new int[length];
		oCircRadiusAll = new int[length];
		oCircRatioAll = new double[length];
	}
	/**
	 * creates an empty array of ringular wall variables
	 * @param length desired length of arrays
	 */
	protected void createWallRingValueArrays(int length)
	{
		oRingX = new int[length];
		oRingY = new int[length];
		oRingInner = new int[length];
		oRingOuter = new int[length];
	}
	/**
	 * creates an empty array of ringular wall variables
	 * @param length desired length of arrays
	 */
	protected void createWallRingValueArraysAll(int length)
	{
		oRingXAll = new int[length];
		oRingYAll = new int[length];
		oRingInnerAll = new int[length];
		oRingOuterAll = new int[length];
	}
	/**
	 * creates an empty array of passage variables
	 * @param length desired length of arrays
	 */
	protected void createWallPassageValueArrays(int length)
	{
		oPassageX1 = new int[length];
		oPassageX2 = new int[length];
		oPassageY1 = new int[length];
		oPassageY2 = new int[length];
	}
	/**
	 * ends a round of fighting and resets variables
	 */
	protected void endFighting()
	{
		player.resetVariables(); // resets players variables
		moneyMade = 0;
		hasKey = false;
		enemies = new Enemy[30];
		structures = new Structure[30];
		Proj_Trackers = new Proj_Tracker[30];
		Proj_Tracker_AOEs = new Proj_Tracker_AOE[30];
		powerUps = new PowerUp[30];
		walls = new Wall_Rectangle[30];
		wallCircles = new Wall_Circle[30];
		wallRings = new Wall_Ring[30];
		powerUps = new PowerUp[30];
		gameEnded = false;
		currentCircle = 0;
		currentRectangle = 0;
		currentCircleAll = 0;
		currentRectangleAll = 0;
		currentRing = 0;
		currentRingAll = 0;
		currentPassage = 0;
		imageLibrary.currentLevelTop = null;
		savedEnemies = 0;
		saveEnemyInformation = new int[30][5];
		activity.saveGame(); // saves game in case phone shuts down etc.
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void loadLevel()
	{
		if(levelNum == 10)
		{			// *******EXAMPLE FOR COMMENTS FOR LOADLEVEL SECTION
			createWallRectangleValueArraysAll(6); //creates array to hold tall rectangular walls
			createWallCircleValueArraysAll(2); //creates array to hold tall circular walls
			levelWidth = 300; // height of level
			levelHeight = 300; // width of level
			player.x = 150; // player start x
			player.y = 150; // player start y
			wallCircles[0] = makeWall_Circle(106, 271, 19, 1, false);
			wallCircles[1] = makeWall_Circle(59, 244, 19, 1, false);
			walls[0] = makeWall_Rectangle(0, 0, 10, 300, true, false);
			walls[1] = makeWall_Rectangle(0, 0, 110, 10, true, false);
			walls[2] = makeWall_Rectangle(290, 0, 10, 300, true, false);
			walls[3] = makeWall_Rectangle(0, 290, 300, 10, true, false);
			walls[4] = makeWall_Rectangle(190, 0, 110, 10, true, false);
			walls[5] = makeWall_Rectangle(-185, 199, 227, 210, true, false);
		}
		if(levelNum == 20)
		{
			createWallRectangleValueArrays(6);
			createWallRectangleValueArraysAll(6);
			levelWidth = 630;
			levelHeight = 300;
			player.x = 100;
			player.y = 150;
			exitX = 16000;
			enemies[0] = new Enemy_Target(this, 332, 18, 90, false); // CREATES ENEMIES
			enemies[1] = new Enemy_Target(this, 332, 284, -90, false);
			enemies[2] = new Enemy_Target(this, 444, 135, 180, false);
			enemies[3] = new Enemy_Target(this, 444, 165, 180, false);
			enemies[4] = new Enemy_Target(this, 531, 18, 90, true);
			enemies[5] = new Enemy_Target(this, 568, 18, 90, true);
			enemies[6] = new Enemy_Target(this, 512, 284, -90, true);
			enemies[7] = new Enemy_Target(this, 549, 284, -90, true);
			walls[0] = makeWall_Rectangle(210, -77, 15, 205, true, true);
			walls[1] = makeWall_Rectangle(210, 172, 15, 205, true, true);
			walls[2] = makeWall_Rectangle(455, -77, 15, 205, true, true);
			walls[3] = makeWall_Rectangle(455, 172, 15, 205, true, true);
			walls[4] = makeWall_Rectangle(618, -77, 15, 205, true, true);
			walls[5] = makeWall_Rectangle(618, 172, 15, 205, true, true);
		}
		if(levelNum == 30)
		{
			createWallRectangleValueArrays(5);
			createWallRectangleValueArraysAll(7);
			levelWidth = 480;
			levelHeight = 310;
			player.x = 436;
			player.y = 112;
			exitX = 40; // exit x coordinate
			exitY = 112; // exit y coordinate
			enemies[0] = new Enemy_Shield(this, 245, 85);
			enemies[1] = new Enemy_Archer(this, 60, 192);
			enemies[2] = new Enemy_Shield(this, 80, 249);
			enemies[1].keyHolder = true;			// THIS ENEMY HOLDS KEY TO NEXT LEVEL
			walls[0] = makeWall_Rectangle(-5, 8, 100, 70, true, true);
			walls[1] = makeWall_Rectangle(83, -9, 141, 153, true, true);
			walls[2] = makeWall_Rectangle(99, 125, 108, 59, true, true);
			walls[3] = makeWall_Rectangle(311, 67, 96, 1210, true, true);
			walls[4] = makeWall_Rectangle(300, 175, 1220, 1350, true, true);
			walls[5] = makeWall_Rectangle(116, 177, 13, 80, true, false);
			walls[6] = makeWall_Rectangle(116, 245, 56, 13, true, false);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	/**
	 * loads a new section of the current level
	 * @param level id of new section to load
	 */
	protected void loadLevelSection(int level)
	{
		levelNum = level;
		for(int i = 0; i < powerUps.length; i++)
		{
			if(powerUps[i] != null) player.getPowerUp(powerUps[i].ID);
		}
		if(levelNum > 29)
		{
			int tempEnemies = savedEnemies; // READS IN AND CREATES ENEMIES IN NEW SECTION, SAVES ENEMIES IN OLD SECTION
			int[][] tempSave = new int[tempEnemies][5];
			for(int i = 0; i < tempEnemies; i++)
			{
				System.arraycopy(saveEnemyInformation[i], 0, tempSave[i], 0, 5);
			}
			savedEnemies = 0;
			for(int i = 0; i < 30; i++)
			{
				if(enemies[i] != null)
				{
					if(!enemies[i].deleted)
					{
						saveEnemyInformation[savedEnemies][0] = enemies[i].getType();
						saveEnemyInformation[savedEnemies][1] = (int) enemies[i].x;
						saveEnemyInformation[savedEnemies][2] = (int) enemies[i].y;
						saveEnemyInformation[savedEnemies][3] = enemies[i].hp;
						if(enemies[i].keyHolder)
						{
							saveEnemyInformation[savedEnemies][4] = 1;
						}
						else
						{
							saveEnemyInformation[savedEnemies][4] = 0;
						}
						savedEnemies++;
					}
				}
			}
			endFightSection(tempSave, tempEnemies);
		} else
		{
			endFightSection();
		}
		if(levelNum == 90)
		{
			createWallRectangleValueArrays(6); // SAME FORMAT AS LOADING LEVELS
			createWallRectangleValueArraysAll(6);
			exitX = -1000;
			exitY = -1000;
			walls[0] = makeWall_Rectangle(43, -48, 10, 240, true, true);
			walls[1] = makeWall_Rectangle(43, 223, 10, 240, true, true);
			walls[2] = makeWall_Rectangle(346, -48, 10, 240, true, true);
			walls[3] = makeWall_Rectangle(346, 223, 10, 240, true, true);
			walls[4] = makeWall_Rectangle(88, 43, 10, 322, true, true);
			walls[5] = makeWall_Rectangle(302, 43, 10, 322, true, true);
		}
		if(levelNum == 91)
		{
			createWallRectangleValueArrays(4);
			createWallRectangleValueArraysAll(4);
			exitX = 200;
			exitY = 230;
			walls[0] = makeWall_Rectangle(43, 45, 10, 320, true, true);
			walls[1] = makeWall_Rectangle(346, 45, 10, 320, true, true);
			walls[2] = makeWall_Rectangle(0, 145, 45, 120, true, true);
			walls[3] = makeWall_Rectangle(355, 145, 45, 120, true, true);
		}
		if(levelNum == 100)
		{
			createWallRectangleValueArrays(17); // SAME FORMAT AS LOADING LEVELS
			createWallRectangleValueArraysAll(17);
			exitX = -1000;
			exitY = -1000;
			walls[0] = makeWall_Rectangle(49-3, 231, 16, 226, true, true);
			walls[1] = makeWall_Rectangle(-150, 175-3, 260, 16, true, true);
			walls[2] = makeWall_Rectangle(104-3, 179, 16, 51, true, true);
			walls[3] = makeWall_Rectangle(107, 224-3, 95, 16, true, true);
			walls[4] = makeWall_Rectangle(197-3, 229, 16, 56, true, true);
			walls[5] = makeWall_Rectangle(105, 280-3, 232, 16, true, true);
			walls[6] = makeWall_Rectangle(330-3, 229, 16, 56, true, true);
			walls[7] = makeWall_Rectangle(334, 224-3, 123, 16, true, true);
			walls[8] = makeWall_Rectangle(108, 121-3, 228, 16, true, true);
			walls[9] = makeWall_Rectangle(330-3, 124, 16, 59, true, true);
			walls[10] = makeWall_Rectangle(156-3, 126, 16, 54, true, true);
			walls[11] = makeWall_Rectangle(197-3, 126, 16, 54, true, true);
			walls[12] = makeWall_Rectangle(160, 174-3, 43, 16, true, true);
			walls[13] = makeWall_Rectangle(49-3, 49, 16, 131, true, true);
			walls[14] = makeWall_Rectangle(105-3, -79, 16, 206, true, true);
			walls[15] = makeWall_Rectangle(258-3, -140, 16, 206, true, true);
			walls[16] = makeWall_Rectangle(163, 60-3, 101, 16, true, true);
		}
		if(levelNum == 101)
		{
			createWallRectangleValueArrays(9); // SAME FORMAT AS LOADING LEVELS
			createWallRectangleValueArraysAll(9);
			exitX = 40;
			exitY = 300;
			walls[0] = makeWall_Rectangle(-73, 224-3, 187, 16, true, true);
			walls[1] = makeWall_Rectangle(161, 280-3, 126, 16, true, true);
			walls[2] = makeWall_Rectangle(330-3, 169, 16, 272, true, true);
			walls[3] = makeWall_Rectangle(330-3, 64, 16, 63, true, true);
			walls[4] = makeWall_Rectangle(49-3, -46, 16, 226, true, true);
			walls[5] = makeWall_Rectangle(157-3, -106, 16, 391, true, true);
			walls[6] = makeWall_Rectangle(165, 60-3, 172, 16, true, true);
			walls[7] = makeWall_Rectangle(162, 224-3, 172, 16, true, true);
			walls[8] = makeWall_Rectangle(103-3, 284, 16, 135, true, true);

		}
		if(levelNum == 150)
		{
			createWallRectangleValueArraysAll(19);
			createWallRectangleValueArrays(8);
			levelWidth = 360;
			levelHeight = 360;
			player.x +=199;
			player.y +=50;
			exitX = 15000;
			exitY = 90000;
			walls[0] = makeWall_Rectangle(49, -31, 61, 130, true, true);
			walls[1] = makeWall_Rectangle(-41, 137, 134, 61, true, true);
			walls[2] = makeWall_Rectangle(320, 19, 167, 206, true, true);
			walls[3] = makeWall_Rectangle(280, 95, 198, 85, true, true);
			walls[4] = makeWall_Rectangle(309, 212, 173, 180, true, true);
			walls[5] = makeWall_Rectangle(260, 247, 173, 180, true, true);
			walls[6] = makeWall_Rectangle(243, 106, 225, 12, true, true);
			walls[7] = makeWall_Rectangle(243, 158, 225, 12, true, true);
			
			walls[8] = makeWall_Rectangle(-21, 284, 69, 16, true, false);
			walls[9] = makeWall_Rectangle(-21, 248, 69, 16, true, false);
			walls[10] = makeWall_Rectangle(35, 253, 16, 42, true, false);
			walls[11] = makeWall_Rectangle(185, 37, 42, 16, true, false);
			walls[12] = makeWall_Rectangle(215, -20, 16, 69, true, false);
			walls[13] = makeWall_Rectangle(180, -20, 16, 69, true, false);
			walls[14] = makeWall_Rectangle(148, 310, 42, 16, true, false);
			walls[15] = makeWall_Rectangle(179, 315, 16, 69, true, false);
			walls[16] = makeWall_Rectangle(144, 315, 16, 69, true, false);
			
			walls[17] = makeWall_Rectangle(224, 321, 180, 61, true, false);
			walls[18] = makeWall_Rectangle(302, -38, 36, 91, true, false);
		}
		if(levelNum == 151)
		{
			createWallRectangleValueArrays(13);
			createWallRectangleValueArraysAll(13);
			createWallCircleValueArrays(2);
			levelWidth = 300;
			levelHeight = 305;
			player.x -=199;
			player.y -=50;
			exitX = 150;
			exitY = 275;
			walls[0] = makeWall_Rectangle(10, -12, 109, 58, true, true);
			walls[1] = makeWall_Rectangle(-29, 5, 109, 58, true, true);
			walls[2] = makeWall_Rectangle(-29, 117, 109, 58, true, true);
			walls[3] = makeWall_Rectangle(0, 264, 109, 58, true, true);
			walls[4] = makeWall_Rectangle(196, 266, 109, 58, true, true);
			walls[5] = makeWall_Rectangle(185, -12, 109, 58, true, true);
			walls[6] = makeWall_Rectangle(220, 37, 109, 121, true, true);
			walls[7] = makeWall_Rectangle(183, 131, 109, 29, true, true);
			walls[8] = makeWall_Rectangle(198, 146, 109, 53, true, true);
			walls[9] = makeWall_Rectangle(241, 175, 109, 121, true, true);
			walls[10] = makeWall_Rectangle(-39, 171, 109, 121, true, true);
			walls[11] = makeWall_Rectangle(-8, 132, 129, 27, true, true);
			walls[12] = makeWall_Rectangle(3, 146, 109, 53, true, true);
			wallCircles[0] = makeWall_Circle(237, 203, 32, 1, false);
			wallCircles[1] = makeWall_Circle(83, 257, 26, 1, false);
		}
		if(levelNum == 21)
		{
			createWallRectangleValueArrays(7);
			createWallRectangleValueArraysAll(7);
			levelWidth = 555;
			player.x = 25;
			enemies[0] = new Enemy_Target(this, 653 - 506, 125, 180, false);
			enemies[1] = new Enemy_Target(this, 653 - 506, 176, 180, false);
			enemies[2] = new Enemy_Target(this, 670 - 506, 150, 180, false);
			enemies[3] = new Enemy_Target(this, 358, 150, 180, false);
			enemies[4] = new Enemy_Target(this, 1014 - 506+40, 150, 180, false);
			walls[0] = makeWall_Rectangle(503 - 506, -100, 15, 500, true, true);
			walls[1] = makeWall_Rectangle(665 - 506, -77, 15, 205, true, true);
			walls[2] = makeWall_Rectangle(665 - 506, 172, 15, 205, true, true);
			walls[3] = makeWall_Rectangle(825 - 506+40, -77, 15, 205, true, true);
			walls[4] = makeWall_Rectangle(825 - 506+40, 172, 15, 205, true, true);
			walls[5] = makeWall_Rectangle(1014 - 506+40, -77, 15, 205, true, true);
			walls[6] = makeWall_Rectangle(1014 - 506+40, 172, 15, 205, true, true);
		}
		if(levelNum == 22)
		{
			createWallRectangleValueArrays(5);
			createWallRectangleValueArraysAll(5);
			levelWidth = 632;
			player.x = 25;
			exitX = 582;
			exitY = 150;
			enemies[0] = new Enemy_Target(this, 1204 - 1017, 150, 180, false);
			enemies[1] = new Enemy_Target(this, 1400 - 1017, 150, 180, false);
			enemies[2] = new Enemy_Target(this, 1522 - 1017, 150, 180, false);
			enemies[2].keyHolder = true;
			walls[0] = makeWall_Rectangle(1014 - 1017, -100, 15, 500, true, true);
			walls[1] = makeWall_Rectangle(1198 - 1017, -77, 15, 205, true, true);
			walls[2] = makeWall_Rectangle(1198 - 1017, 172, 15, 205, true, true);
			walls[3] = makeWall_Rectangle(1393 - 1017, -77, 15, 205, true, true);
			walls[4] = makeWall_Rectangle(1393 - 1017, 172, 15, 205, true, true);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	/**
	 * creates an enemy based off of saved info
	 * @param info array of stored values
	 * @param index which spot in enemy array to populate
	 */
	private void createEnemy(int[] info, int index)
	{
		switch(info[0])
		{
		case 1:
			enemies[index] = new Enemy_Shield(this, info[1], info[2]); // creates shield in alternate level section
			break;
		case 4:
			enemies[index] = new Enemy_Rogue(this, info[1], info[2]);
			break;
		case 5:
			enemies[index] = new Enemy_Archer(this, info[1], info[2]);
			break;
		case 6:
			enemies[index] = new Enemy_Mage(this, info[1], info[2]);
			break;
		}
		if(info[3] != 0) // if enemy has set health change it, otherwise leave as starting health
		{
			enemies[index].hp = info[3];
		}
		if(info[4] == 1)
		{
			enemies[index].keyHolder = true; // if saved enemy has key
		}
	}
	/**
	 * end a section of a fight, stored enemies in current states
	 * @param enemyData enemies to create
	 * @param tempEnemies number of enemies to create
	 */
	private void endFightSection(int[][] enemyData, int tempEnemies)
	{
		enemies = new Enemy[30];
		structures = new Structure[30];
		Proj_Trackers = new Proj_Tracker[30];
		Proj_Tracker_AOEs = new Proj_Tracker_AOE[30];
		powerUps = new PowerUp[30];
		walls = new Wall_Rectangle[30];
		wallCircles = new Wall_Circle[30];
		wallRings = new Wall_Ring[30];
		powerUps = new PowerUp[30];
		currentCircle = 0;
		currentRectangle = 0; // RESETS CURRENT WALL INDEXES OF ARRAYS
		currentCircleAll = 0;
		currentRectangleAll = 0;
		currentRing = 0;
		currentPassage = 0;
		for(int i = 0; i < tempEnemies; i++)
		{
			createEnemy(enemyData[i], i); // CREATES SAVED ENEMIES
		}
	}
	/**
	 * ends a fight section with no saved enemies
	 */
	private void endFightSection()
	{
		enemies = new Enemy[30]; // SAME AS OTHER endFightSection BUT DOESNT SPAWN ENEMIES
		structures = new Structure[30];
		Proj_Trackers = new Proj_Tracker[30];
		Proj_Tracker_AOEs = new Proj_Tracker_AOE[30];
		powerUps = new PowerUp[30];
		walls = new Wall_Rectangle[30];
		wallCircles = new Wall_Circle[30];
		wallRings = new Wall_Ring[30];
		powerUps = new PowerUp[30];
		currentCircle = 0;
		currentRectangle = 0;
		currentCircleAll = 0;
		currentRectangleAll = 0;
		currentRing = 0;
		currentPassage = 0;
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
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
				if(!enemies[i].rogue || enemies[i].currentFrame != 49)
				{
					minX = (int) enemies[i].x - 20;
					maxX = (int) enemies[i].x + 20;
					minY = (int) enemies[i].y - 30;
					maxY = (int) enemies[i].y - 20;
					/*offset = fixXBoundsHpBar(minX, maxX);
					minX += offset;
					maxX += offset;
					offset = fixYBoundsHpBar(minY, maxY);
					minY += offset;
					maxY += offset;
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					drawRect(minX, minY, minX + (40 * enemies[i].getHp() / enemies[i].getHpMax()), maxY, g);
					paint.setColor(Color.BLACK);
					paint.setStyle(Paint.Style.STROKE);
					drawRect(minX, minY, maxX, maxY, g);*/
					paint.setColor(Color.RED);
					paint.setStyle(Paint.Style.FILL);
					drawRect(minX, minY, minX + (40 * enemies[i].getHp() / enemies[i].getHpMax()), maxY, g);
					paint.setColor(Color.BLACK);
					paint.setStyle(Paint.Style.STROKE);
					drawRect(minX, minY, maxX, maxY, g);
				}
			}
		}
		for(int i = 0; i < structures.length; i++)
		{
			if(structures[i] != null)
			{
				minX = (int) structures[i].x - 20;
				maxX = (int) structures[i].x + 20;
				minY = (int) structures[i].y - 30;
				maxY = (int) structures[i].y - 20;
				paint.setColor(Color.BLUE);
				paint.setStyle(Paint.Style.FILL);
				drawRect(minX, minY, minX + (40 * structures[i].hp / structures[i].hpMax), maxY, g);
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
		if(player.transformed==0&&levelNum!=10) drawBitmapRotated(shootStick, g);
	}
	/**
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	protected void frameCall()
	{
		playerHit++;
		playerBursted++;
		for(int i = 0; i < Proj_Trackers.length; i++)
		{
			if(Proj_Trackers[i] != null)
			{
				if(Proj_Trackers[i].deleted)
				{
					Proj_Trackers[i] = null;
				}
				else
				{
					Proj_Trackers[i].frameCall();
				}
			}
		}
		for(int i = 0; i < Proj_Tracker_AOEs.length; i++)
		{
			if(Proj_Tracker_AOEs[i] != null)
			{
				if(Proj_Tracker_AOEs[i].deleted)
				{
					Proj_Tracker_AOEs[i] = null;
				}
				else
				{
					Proj_Tracker_AOEs[i].frameCall();
				}
			}
		}
		for(int i = 0; i < powerUps.length; i++)
		{
			if(powerUps[i] != null)
			{
				if(powerUps[i].deleted)
				{
					powerUps[i] = null;
				}
				else
				{
					powerUps[i].frameCall();
				}
			}
		}
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
				if(enemies[i].deleted)
				{
					enemies[i] = null;
				}
				else
				{
					enemies[i].levelCurrentPosition = 0;
					enemies[i].pathedToHitLength = 0;
					if(enemyInView(enemies[i].x, enemies[i].y))
					{
						enemies[i].frameCall();
						if(enemies[i] != null)
						{
							if(enemies[i].x < 10) enemies[i].x = 10;
							if(enemies[i].x > levelWidth - 10) enemies[i].x = (levelWidth - 10);
							if(enemies[i].y < 10) enemies[i].y = 10;
							if(enemies[i].y > levelHeight - 10) enemies[i].y = (levelHeight - 10);
						}
					}
				}
			}
		}
		for(int i = 0; i < structures.length; i++)
		{
			if(structures[i] != null)
			{
				if(structures[i].deleted)
				{
					structures[i] = null;
				}
				else
				{
					if(enemyInView(structures[i].x, structures[i].y))
					{
						structures[i].frameCall();
					}
				}
			}
		}
		if(hasKey && getDistance(player.x, player.y, exitX, exitY) < 30 && levelNum > 19)
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
			if(levelNum == 10)
			{
				if(imageLibrary.directionsTutorial != null && distSquared(player.x, player.y, 162, 150) > 400)
				{
					imageLibrary.directionsTutorial.recycle();
					imageLibrary.directionsTutorial = null;
				}
				if(player.x > 110 && player.x < 190 && player.y < 16)
				{
					gamePaused = true;
					currentPause = "chooseLevel";
					player.y += 15;
					invalidate();
				}
			}
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
			if(levelNum == 90)
			{
				if(player.x < 50 || player.x > 350)
				{
					if(player.y < 105 || player.y > 305)
					{
						loadLevelSection(91);
					}
				}
			}
			if(levelNum == 91)
			{
				if(player.x < 50 || player.x > 350)
				{
					if(player.y > 125 && player.y < 285)
					{
						loadLevelSection(90);
					}
				}
			}
			if(levelNum == 100)
			{
				if(playerOnSquare(184, 234, 10, 48)) loadLevelSection(101);
				if(playerOnSquare(338, 232, 48, 15)) loadLevelSection(101);
				if(playerOnSquare(228, 5, 31, 57)) loadLevelSection(101);
				if(playerOnSquare(4, 113, 48, 65)) loadLevelSection(101);
			}
			if(levelNum == 101)
			{
				if(playerOnSquare(338, 268, 48, 69)) loadLevelSection(100);
				if(playerOnSquare(3, 6, 48, 89)) loadLevelSection(100);
				if(playerOnSquare(166, 5, 42, 57)) loadLevelSection(100);
				if(playerOnSquare(166, 234, 12, 48)) loadLevelSection(100);
			}
			if(levelNum == 150)
			{
				if(playerOnSquare(262, 122, 17, 37)) loadLevelSection(151);
			}
			if(levelNum == 151)
			{
				if(playerOnSquare(40, 72, 17, 36)) loadLevelSection(150);
			}
			for(int i = 0; i < walls.length; i++)
			{
				if(walls[i] != null)
				{
					walls[i].frameCall();
				}
			}
			for(int i = 0; i < wallCircles.length; i++)
			{
				if(wallCircles[i] != null)
				{
					wallCircles[i].frameCall();
				}
			}
			for(int i = 0; i < wallRings.length; i++)
			{
				if(wallRings[i] != null)
				{
					wallRings[i].frameCall();
				}
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
		if(levelNum > 19)
		{
			drawBitmapLevel(imageLibrary.exitFightPortal, exitX - 30, exitY - 30, g);
		}
		for(int i = 0; i < structures.length; i++)
		{
			if(structures[i] != null)
			{
				drawBitmapLevel(imageLibrary.structure_Spawn, (int)structures[i].x-structures[i].width, (int)structures[i].y-structures[i].height, g);
			}
		}
		if(levelNum == 10)
		{
			//if(imageLibrary.directionsTutorial != null) drawBitmapLevel(imageLibrary.directionsTutorial, 45, 65, g);
			paint.setAlpha(255);
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
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
				if(enemies[i].keyHolder)
				{
					drawBitmapLevel(imageLibrary.haskey, (int) enemies[i].x - 20, (int) enemies[i].y - 20, g);
				}
				drawBitmapRotatedLevel(enemies[i], g);
			}
		}
		for(int i = 0; i < Proj_Trackers.length; i++)
		{
			if(Proj_Trackers[i] != null)
			{
				drawBitmapRotatedLevel(Proj_Trackers[i], g);
			}
		}
		for(int i = 0; i < Proj_Tracker_AOEs.length; i++)
		{
			if(Proj_Tracker_AOEs[i] != null)
			{
				aoeRect.top = (int)(Proj_Tracker_AOEs[i].y - (Proj_Tracker_AOEs[i].getHeight() / 2.5));
				aoeRect.bottom = (int)(Proj_Tracker_AOEs[i].y + (Proj_Tracker_AOEs[i].getHeight() / 2.5));
				aoeRect.left = (int)(Proj_Tracker_AOEs[i].x - (Proj_Tracker_AOEs[i].getWidth() / 2.5));
				aoeRect.right = (int)(Proj_Tracker_AOEs[i].x + (Proj_Tracker_AOEs[i].getWidth() / 2.5));
				paint.setAlpha(Proj_Tracker_AOEs[i].getAlpha());
				drawBitmapRectLevel(Proj_Tracker_AOEs[i].getVisualImage(), aoeRect, g);
			}
		}
		paint.setAlpha(255);
		for(int i = 0; i < powerUps.length; i++)
		{
			if(powerUps[i] != null)
			{
				drawBitmapLevel(powerUps[i].getVisualImage(), (int) powerUps[i].x - 15, (int) powerUps[i].y - 15, g);
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
	/**
	 * draw start fight screen
	 * @param g canvas to draw to
	 */
	protected void drawStartFight(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_startfight", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(difficultyLevel == 10)
		{
			moneyMultiplier = 5;
			drawRect(16, 126, 116, 166, g);
		}
		if(difficultyLevel == 6)
		{
			moneyMultiplier = 7;
			drawRect(16, 176, 116, 216, g);
		}
		if(difficultyLevel == 3)
		{
			moneyMultiplier = 12;
			drawRect(364, 126, 464, 166, g);
		}
		if(difficultyLevel == 0)
		{
			moneyMultiplier = 20;
			drawRect(364, 176, 464, 216, g);
		}
		moneyMultiplier *= getLevelWinningsMultiplier(startingLevel);
		if(drainHp)
		{
			moneyMultiplier *= 1.4;
			drawRect(16, 234, 116, 304, g);
		}
		if(lowerHp)
		{
			moneyMultiplier *= 1.4;
			drawRect(132, 234, 232, 304, g);
		}
		if(limitSpells)
		{
			moneyMultiplier *= 1.4;
			drawRect(248, 234, 348, 304, g);
		}
		if(enemyRegen)
		{
			moneyMultiplier *= 1.4;
			drawRect(364, 234, 464, 304, g);
		}
		moneyMultiplier *= 1+((double)activity.bExcess/8);
		paint.setAlpha(255);
		paint.setTextAlign(Align.CENTER);
		//TODO
		paint.setTextSize(75-(getLevelName(startingLevel).length()*2));
		drawText(getLevelName(startingLevel), 240, 90, g);
		paint.setTextSize(25);
		drawText("Gold Multiplier: " + Integer.toString((int)moneyMultiplier), 240, 153, g);
	}
	/**
	 * returns winning multiplier for level
	 * @param level level to return multiplier for
	 * @return multiplier for level
	 */
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
	/**
	 * draw buy powerups screen
	 * @param g canvas to draw to
	 */
	protected void drawBlessing(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_blessing", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		drawMoneyTop(g);
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.buy("Ambrosia", 9999999, false)), 147, 142, g);
		drawText(Integer.toString(activity.buy("Cooldown", 9999999, false)), 357, 142, g);
		drawText(Integer.toString(activity.buy("Apollo's Flame", 9999999, false)), 147, 215, g);
		drawText(Integer.toString(activity.buy("Hades' Helm", 9999999, false)), 357, 215, g);
		drawText(Integer.toString(activity.buy("Zues's Armor", 9999999, false)), 147, 289, g);
		drawText(Integer.toString(activity.buy("Posiedon's Shell", 9999999, false)), 357, 289, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyGame("Ambrosia")) drawRect(60, 119, 210, 154, g);
		if(!activity.canBuyGame("Cooldown")) drawRect(270, 119, 420, 154, g);
		if(!activity.canBuyGame("Apollo's Flame")) drawRect(60, 194, 210, 229, g);
		if(!activity.canBuyGame("Hades' Helm")) drawRect(270, 194, 420, 229, g);
		if(!activity.canBuyGame("Zues's Armor")) drawRect(60, 268, 210, 303, g);
		if(!activity.canBuyGame("Posiedon's Shell")) drawRect(270, 268, 420, 303, g);
	}
	/**
	 * draw buy powerups screen
	 * @param g canvas to draw to
	 */
	protected void drawBuyAll(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_buyall", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		drawMoneyTop(g);
	}
	protected void drawChooseLevel(Canvas g)
	{
		drawBehindPause(g);
		//TODO
		if(tempPicture == null)
		{
			tempPicture = imageLibrary.loadImage("menu_chooselevelback", 480, 320);
			tempPictureLock = imageLibrary.loadImage("icon_menu_levellocked", 50, 70);
		}
		for(int i = 1; i < 9; i++)
		{
			int yVal = (80*i)-60-(int)((double)360/250*(detect.chooseLevelSliderY-35));
			if(yVal<300&&yVal>-60)
			{
				drawBitmap(imageLibrary.loadImage("menu_chooselevel000"+Integer.toString(i), 400, 80), 20, yVal, g);
			}
			if(activity.levelBeaten < (2*i)-2)
			{
				drawBitmap(tempPictureLock, 95, yVal+5, g);
			}
			if(activity.levelBeaten < (2*i)-1)
			{
				drawBitmap(tempPictureLock, 295, yVal+5, g);
			}
		}
		drawBitmap(tempPicture, 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(420, detect.chooseLevelSliderY-17, 460, detect.chooseLevelSliderY+17, g);
	}
	/**
	 * draw buy worship screen
	 * @param g canvas to draw to
	 */
	protected void drawWorship(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_worship", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		drawMoneyTop(g);
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.buy("Worship Apollo", 9999999, false)), 98, 142, g);
		drawText(Integer.toString(activity.buy("Worship Posiedon", 9999999, false)), 248, 142, g);
		drawText(Integer.toString(activity.buy("Worship Zues", 9999999, false)), 398, 142, g);
		drawText(Integer.toString(activity.buy("Worship Hades", 9999999, false)), 98, 215, g);
		drawText(Integer.toString(activity.buy("Worship Ares", 9999999, false)), 248, 215, g);
		drawText(Integer.toString(activity.buy("Worship Athena", 9999999, false)), 398, 215, g);
		drawText(Integer.toString(activity.buy("Worship Hermes", 9999999, false)), 98, 289, g);
		drawText(Integer.toString(activity.buy("Worship Hephaestus", 9999999, false)), 248, 289, g);
		drawText(Integer.toString(activity.buy("Worship Hera", 9999999, false)), 398, 289, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyGame("Worship Apollo")) drawRect(30, 119, 150, 154, g);
		if(!activity.canBuyGame("Worship Posiedon")) drawRect(180, 119, 300, 154, g);
		if(!activity.canBuyGame("Worship Zues")) drawRect(330, 119, 450, 154, g);
		if(!activity.canBuyGame("Worship Hades")) drawRect(30, 194, 150, 229, g);
		if(!activity.canBuyGame("Worship Ares")) drawRect(180, 194, 300, 229, g);
		if(!activity.canBuyGame("Worship Athena")) drawRect(330, 194, 450, 229, g);
		if(!activity.canBuyGame("Worship Hermes")) drawRect(30, 268, 150, 303, g);
		if(!activity.canBuyGame("Worship Hephaestus")) drawRect(180, 268, 300, 303, g);
		if(!activity.canBuyGame("Worship Hera")) drawRect(330, 268, 450, 303, g);
	}
	/**
	 * draw buy skins screen
	 * @param g canvas to draw to
	 */
	protected void drawBuySkins(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_buyskins", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		drawMoneyTop(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.ownSkin1)
		{
			drawRect(0, 133, 160, 227, g);
			if(!activity.canBuyReal("skin1")) drawRect(0, 133, 160, 173, g);
		}
		if(!activity.ownSkin2)
		{
			if(!activity.canBuyReal("skin2")) drawRect(0, 227, 160, 267, g);
			drawRect(0, 227, 160, 320, g);
		}
		if(!activity.ownSkin3)
		{
			if(!activity.canBuyReal("skin3")) drawRect(160, 133, 320, 173, g);
			drawRect(160, 133, 320, 227, g);
		}
		if(!activity.ownSkin4)
		{
			if(!activity.canBuyReal("skin4")) drawRect(160, 227, 320, 267, g);
			drawRect(160, 227, 320, 320, g);
		}
		if(!activity.ownSkin5)
		{
			if(!activity.canBuyReal("skin5")) drawRect(320, 40, 480, 80, g);
			drawRect(320, 40, 480, 133, g);
		}
		if(!activity.ownSkin6)
		{
			if(!activity.canBuyReal("skin6")) drawRect(320, 133, 480, 173, g);
			drawRect(320, 133, 480, 227, g);
		}
		if(!activity.ownSkin7)
		{
			if(!activity.canBuyReal("skin7")) drawRect(320, 227, 480, 267, g);
			drawRect(320, 227, 480, 320, g);
		}
		
		
		
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(8);
		paint.setColor(highlightChoiceColor);
		paint.setAlpha(120);
		switch (activity.currentSkin)
		{
			case 0: 
				drawRect(0, 40, 160, 133, g);
				break;
			case 1: 
				drawRect(0, 133, 160, 227, g);
				break;
			case 2: 
				drawRect(0, 227, 160, 320, g);
				break;
			case 3: 
				drawRect(160, 133, 320, 227, g);
				break;
			case 4: 
				drawRect(160, 227, 320, 320, g);
				break;
			case 5: 
				drawRect(320, 40, 480, 133, g);
				break;
			case 6: 
				drawRect(320, 133, 480, 227, g);
				break;
			case 7: 
				drawRect(320, 227, 480, 320, g);
				break;
		}
		paint.setStrokeWidth(0);
		paint.setAlpha(255);
	}
	/**
	 * draw buy specific cash item screen
	 * @param g canvas to draw to
	 */
	protected void drawBuyItemCash(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_buyitemcash", 480, 320), 0, 0, g);
		paint.setTextSize(40);
		paint.setTextAlign(Align.CENTER);
		drawText(buyingItem, 240, 103, g);
		paint.setTextSize(28);
		paint.setTextAlign(Align.LEFT);
		String[] describe = activity.getItemDescribe(buyingItem);
		drawText(describe[0], 75, 163, g);
		drawText(describe[1], 75, 200, g);
		drawMoneyTop(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(38);
		paint.setColor(platinumColor);
		paint.setTextAlign(Align.CENTER);
		drawText(Integer.toString(activity.buy(buyingItem, 9999999, false)), 260, 268, g);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyReal(buyingItem))
		{
			drawRect(150, 240, 330, 282, g);
		}
	}
	/**
	 * draw buy cash items screen
	 * @param g canvas to draw to
	 */
	protected void drawBuyPremium(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_premium", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		drawMoneyTop(g);
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.buy("1000g", 9999999, false)), 98, 142, g);
		drawText(Integer.toString(activity.buy("8000g", 9999999, false)), 248, 142, g);
		drawText(Integer.toString(activity.buy("40000g", 9999999, false)), 398, 142, g);
		drawText(Integer.toString(activity.buy("Iron Golem", 9999999, false)), 98, 215, g);
		drawText(Integer.toString(activity.buy("Gold Golem", 9999999, false)), 248, 215, g);
		drawText(Integer.toString(activity.buy("Reserve", 9999999, false)), 398, 215, g);
		drawText(Integer.toString(activity.buy("Excess", 9999999, false)), 98, 289, g);
		drawText(Integer.toString(activity.buy("Replentish", 9999999, false)), 248, 289, g);
		drawText(Integer.toString(activity.buy("Trailing", 9999999, false)), 398, 289, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyReal("1000g")) drawRect(30, 119, 150, 154, g);
		if(!activity.canBuyReal("8000g")) drawRect(180, 119, 300, 154, g);
		if(!activity.canBuyReal("40000g")) drawRect(330, 119, 450, 154, g);
		if(!activity.canBuyReal("Iron Golem")) drawRect(30, 194, 150, 229, g);
		if(!activity.canBuyReal("Gold Golem")) drawRect(180, 194, 300, 229, g);
		if(!activity.canBuyReal("Reserve")) drawRect(330, 194, 450, 229, g);
		if(!activity.canBuyReal("Excess")) drawRect(30, 268, 150, 303, g);
		if(!activity.canBuyReal("Replentish")) drawRect(180, 268, 300, 303, g);
		if(!activity.canBuyReal("Trailing")) drawRect(330, 268, 450, 303, g);
	}
	/**
	 * draw buy specific item screen
	 * @param g canvas to draw to
	 */
	protected void drawBuy(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_buy", 480, 320), 0, 0, g);
		paint.setTextSize(40);
		paint.setTextAlign(Align.CENTER);
		drawText(buyingItem, 240, 103, g);
		paint.setTextSize(28);
		paint.setTextAlign(Align.LEFT);
		String[] describe = activity.getItemDescribe(buyingItem);
		drawText(describe[0], 62, 163, g);
		drawText(describe[1], 62, 200, g);
		paint.setTextSize(24);
		drawMoneyTop(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(38);
		paint.setColor(goldColor);
		paint.setTextAlign(Align.CENTER);
		drawText(Integer.toString(activity.buy(buyingItem, 9999999, false)), 260, 268, g);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyGame(buyingItem))
		{
			drawRect(150, 240, 330, 282, g);
		}
	}
	/**
	 * draw pause screen
	 * @param g canvas to draw to
	 */
	protected void drawPaused(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_paused", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(45);
		paint.setTextAlign(Align.CENTER);
		drawText(Integer.toString(activity.pHeal), 50, 70, g);
		drawText(Integer.toString(activity.pCool), 150, 70, g);
		drawText(Integer.toString(activity.pWater), 50, 170, g);
		drawText(Integer.toString(activity.pEarth), 150, 170, g);
		drawText(Integer.toString(activity.pAir), 50, 270, g);
		drawText(Integer.toString(activity.pFire), 150, 270, g);
		drawText(Integer.toString(activity.pGolem), 241, 120, g);
		drawText(Integer.toString(activity.pHammer), 241, 220, g);
		paint.setAlpha(151);
		if(activity.pHeal == 0)
		{
			drawCircle(60, 60, 37, g);
		}
		if(activity.pCool == 0)
		{
			drawCircle(160, 60, 37, g);
		}
		if(activity.pGolem == 0)
		{
			drawCircle(251, 110, 37, g);
		}
		if(activity.pHammer == 0)
		{
			drawCircle(251, 210, 37, g);
		}
	}
	/**
	 * draw screen behind pause or menus
	 * @param g canvas to draw to
	 */
	protected void drawBehindPause(Canvas g)
	{
		drawNotPaused(g);
		drawBitmap(imageLibrary.loadImage("menu_pauseback", 480, 320), 0, 0, g);
	}
	/**
	 * draw options screen
	 * @param g canvas to draw to
	 */
	protected void drawOptions(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_options", 480, 320), 0, 0, g);
		paint.setAlpha(255);
		paint.setColor(Color.GRAY);
		paint.setStyle(Style.FILL);
		drawCircle(264 + (int)(Math.pow(activity.volumeMusic * 16129, 0.3333333333333333333333)), 103, 15, g);
		drawCircle(264 + (int)(Math.pow(activity.volumeEffect * 16129, 0.3333333333333333333333)), 130, 15, g);
		float systemVolume = activity.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float)(systemVolume * activity.volumeMusic / 127);
		activity.backMusic.setVolume(systemVolume, systemVolume);
		drawBitmap(check, 338, 147, g);
		if(!activity.shootTapScreen)
		{
			drawBitmap(check, 249, 174, g);
		}
		else
		{
			drawBitmap(check, 417, 174, g);
		}
		if(activity.shootTapDirectional)
		{
			drawBitmap(check, 236, 200, g);
		}
		else
		{
			drawBitmap(check, 450, 200, g);
		}
		if(activity.holdShoot)
		{
			drawBitmap(check, 168, 228, g);
		}
		else
		{
			drawBitmap(check, 259, 228, g);
		}
		if(activity.highGraphics)
		{
			drawBitmap(check, 270, 257, g);
		}
		else
		{
			drawBitmap(check, 168, 257, g);
		}
	}
	/**
	 * draw death screen
	 * @param g canvas to draw to
	 */
	protected void drawLost(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_lost", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		paint.setColor(Color.BLACK);
		activity.gameCurrency -= (int)(moneyMade/2);
		drawText(Integer.toString(activity.gameCurrency) + " (+" + Integer.toString((int)moneyMade) + "-"+Integer.toString((int)(moneyMade/2))+")", 153, 146, g);
		drawText(Integer.toString(activity.realCurrency), 153, 193, g);
	}
	/**
	 * draw buy real money screen
	 * @param g canvas to draw to
	 */
	protected void drawBuyCash(Canvas g)
	{
		drawBehindPause(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(0, 0, 480, 40, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_buycash", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		drawMoneyTop(g);
	}
	protected void drawMoneyTop(Canvas g)
	{
		paint.setTextSize(24);
		paint.setTextAlign(Align.LEFT);
		paint.setColor(Color.BLACK);
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 26, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 26, g);
	}
	/**
	 * draw win fight screen
	 * @param g canvas to draw to
	 */
	protected void drawWon(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_won", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		paint.setColor(Color.BLACK);
		drawText(Integer.toString(activity.gameCurrency) + " (+" + Integer.toString((int)moneyMade) + ")", 153, 146, g);
		drawText(Integer.toString(activity.realCurrency), 153, 193, g);
	}
	/**
	 * chooses what to draw
	 * @param g canvas to draw to
	 */
	@Override
	protected void onDraw(Canvas g)
	{
		if(activity.gameRunning)
		{
			//paint.reset();
			g.translate(screenMinX, screenMinY);
			g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
			paint.setAlpha(255);
			if(gamePaused)
			{
				if(currentPause.equals("paused"))
				{
					drawPaused(g);
				}
				else if(currentPause.equals("options"))
				{
					drawOptions(g);
				}
				else if(currentPause.equals("buyall"))
				{
					drawBuyAll(g);
				}
				else if(currentPause.equals("startfight"))
				{
					drawStartFight(g);
				}
				else if(currentPause.equals("buy"))
				{
					drawBuy(g);
				}
				else if(currentPause.equals("won"))
				{
					drawWon(g);
				}
				else if(currentPause.equals("lost"))
				{
					drawLost(g);
				}
				else if(currentPause.equals("chooseLevel"))
				{
					drawChooseLevel(g);
				}
				else if(currentPause.equals("worship"))
				{
					drawWorship(g);
				}
				else if(currentPause.equals("blessing"))
				{
					drawBlessing(g);
				}
				else if(currentPause.equals("buycash"))
				{
					drawBuyCash(g);
				}
				else if(currentPause.equals("buyitemcash"))
				{
					drawBuyItemCash(g);
				}
				else if(currentPause.equals("buypremium"))
				{
					drawBuyPremium(g);
				}
				else if(currentPause.equals("buyskins"))
				{
					drawBuySkins(g);
				}
			}
			else
			{
				drawNotPaused(g);
			}
			paint.setAlpha(255);
			drawBitmap(imageLibrary.backButton, 0, 0, g);
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
		if(warningTimer > 0)
		{
			warningTimer--;
			paint.setColor(Color.BLACK);
			if(warningTimer<25)
			{
				paint.setAlpha((byte)(warningTimer * 7));
			} else
			{
				paint.setAlpha(255);
			}
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize(64-(warningText.length()*2));
			drawText(warningText, 240, 160, g);
		}
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
		if(levelNum == 10)
		{
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.CENTER);
			paint.setAlpha(180);
			drawRect(395, 5, 475, 315, g);
			paint.setAlpha(255);
			drawBitmap(imageLibrary.coins[0], 420, 30, g);
			drawBitmap(imageLibrary.coins[1], 420, 130, g);
			paint.setTextSize(20);
			paint.setColor(platinumColor);
			drawText(Integer.toString(activity.realCurrency), 435, 85, g);
			paint.setColor(goldColor);
			drawText(Integer.toString(activity.gameCurrency), 435, 185, g);
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
		Proj_Tracker_Enemy ballEnemy = new Proj_Tracker_Enemy(this, (int) (x+xVel*2), (int) (y+yVel*2), power, xVel, yVel, rotation);
		Proj_Trackers[lowestPositionEmpty(Proj_Trackers)] = ballEnemy;
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
		CrossbowBolt boltEnemy = new CrossbowBolt(this, (int) (x+xVel), (int) (y+yVel), power, xVel, yVel, rotation);
		Proj_Trackers[lowestPositionEmpty(Proj_Trackers)] = boltEnemy;
	}
	/**
	 * creates a powerup object the player can pick up
	 * @param X x position
	 * @param Y y position
	 */
	protected void createPowerUp(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 0);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	/**
	 * creates a small coin the player can pick up
	 * @param X x position
	 * @param Y y position
	 */
	protected void createCoin1(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 7);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	/**
	 * creates a medium coin the player can pick up
	 * @param X x position
	 * @param Y y position
	 */
	protected void createCoin5(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 9);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	/**
	 * creates a large coin the player can pick up
	 * @param X x position
	 * @param Y y position
	 */
	protected void createCoin20(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 10);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	/**
	 * creates a key the player can pick up
	 * @param X x position
	 * @param Y y position
	 */
	protected void createKey(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 8);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
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
		Proj_Tracker_Player ballPlayer = new Proj_Tracker_Player(this, (int)x, (int)y, power, Vel, rotation);
		Proj_Trackers[lowestPositionEmpty(Proj_Trackers)] = ballPlayer;
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
		Proj_Tracker_AOE_Enemy ballAOEEnemy = new Proj_Tracker_AOE_Enemy(this, (int) x, (int) y, power, true);
		if(!damaging) ballAOEEnemy.damaging = false;
		Proj_Tracker_AOEs[lowestPositionEmpty(Proj_Tracker_AOEs)] = ballAOEEnemy;
	}
	/**
	 * creates a player AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerAOE(double x, double y, double power, boolean damaging)
	{
		Proj_Tracker_AOE_Player ballAOEPlayer = new Proj_Tracker_AOE_Player(this, (int) x, (int) y, power, true);
		if(!damaging) ballAOEPlayer.damaging = false;
		Proj_Tracker_AOEs[lowestPositionEmpty(Proj_Tracker_AOEs)] = ballAOEPlayer;
	}
	/**
	 * creates an enemy burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerEnemyBurst(double x, double y, double power)
	{
		Proj_Tracker_AOE_Enemy ballAOEEnemy = new Proj_Tracker_AOE_Enemy(this, (int) x, (int) y, power, false);
		Proj_Tracker_AOEs[lowestPositionEmpty(Proj_Tracker_AOEs)] = ballAOEEnemy;
	}
	/**
	 * creates a player burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createProj_TrackerPlayerBurst(double x, double y, double power)
	{
		Proj_Tracker_AOE_Player ballAOEPlayer = new Proj_Tracker_AOE_Player(this, (int) x, (int) y, power, false);
		Proj_Tracker_AOEs[lowestPositionEmpty(Proj_Tracker_AOEs)] = ballAOEPlayer;
	}
	/**
	 * Tests an array to find lowest null index
	 * @ param array Array to check in for null indexes
	 * @ return Returns lowest index that equals null
	 */
	protected int lowestPositionEmpty(Sprite[] array)
	{
		int lowest = 0;
		for(int i = 0; i < 25; i++)
		{
			if(array[i] == null)
			{
				lowest = i;
				i = 30;
			}
		}
		return lowest;
	}
	/**
	 * Starts warning label
	 * @param warning
	 */
	protected void startWarning(String warning)
	{
		warningTimer = 50;
		warningText = warning;
		//TODO
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
	protected boolean checkObstructionsPointTall(float x1, float y1, float x2, float y2)
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
		for(int i = 0; i < currentCircle; i++)
		{
			if(!hitBack)
			{
				circM = -(1 / m1);
				circB = oCircY[i] - (circM * oCircX[i]);
				tempX = (circB - b1) / (m1 - circM);
				if(x1 < tempX && tempX < x2)
				{
					tempY = (circM * tempX) + circB;
					if(Math.sqrt(Math.pow(tempX - oCircX[i], 2) + Math.pow((tempY - oCircY[i]) / oCircRatio[i], 2)) < oCircRadius[i])
					{
						hitBack = true;
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
		for(int i = 0; i < currentRectangle; i++)
		{
			if(!hitBack)
			{
				//Right and left Checks
				if(x1 < oRectX1[i] && oRectX1[i] < x2)
				{
					tempY = (m1 * oRectX1[i]) + b1;
					if(oRectY1[i] < tempY && tempY < oRectY2[i])
					{
						hitBack = true;
					}
				}
				if(x1 < oRectX2[i] && oRectX2[i] < x2)
				{
					tempY = (m1 * oRectX2[i]) + b1;
					if(oRectY1[i] < tempY && tempY < oRectY2[i])
					{
						hitBack = true;
					}
				}
				//Top and Bottom checks
				if(y1 < oRectY1[i] && oRectY1[i] < y2)
				{
					tempX = (oRectY1[i] - b1) / m1;
					if(oRectX1[i] < tempX && tempX < oRectX2[i])
					{
						hitBack = true;
					}
				}
				if(y1 < oRectY2[i] && oRectY2[i] < y2)
				{
					tempX = (oRectY1[i] - b1) / m1;
					if(oRectX1[i] < tempX && tempX < oRectX2[i])
					{
						hitBack = true;
					}
				}
			}
		}
		for(int i = 0; i < currentRing; i++)
		{
			if(!hitBack)
			{
				double a = Math.pow(m1, 2) + 1;
				double b = 2 * ((m1 * b1) - (m1 * oRingY[i]) - (oRingX[i]));
				double c = Math.pow(b1, 2) + (Math.pow(oRingY[i], 2)) - (2 * b1 * oRingY[i]) + Math.pow(oRingX[i], 2) - Math.pow(140, 2);
				double temp = (-4 * a * c) + Math.pow(b, 2);
				if(temp >= 0)
				{
					double change = Math.sqrt(temp);
					double pointX1 = (-b + change) / (2 * a);
					double pointX2 = (-b - change) / (2 * a);
					if(x1 < pointX1 && pointX1 < x2)
					{
						double pointY = (m1 * pointX1) + b1;
						if(!checkHitBackPass(pointX1, pointY))
						{
							hitBack = true;
						}
					}
					if(x1 < pointX2 && pointX2 < x2)
					{
						double pointY = (m1 * pointX2) + b1;
						if(!checkHitBackPass(pointX2, pointY))
						{
							hitBack = true;
						}
					}
				}
			}
		}
		return hitBack;
	}
	/**
	 * checks whether a human could travel between two points
	 * @param x1 first x
	 * @param y1 first y
	 * @param x2 second x
	 * @param y2 second y
	 * @return whether the human could travel between points
	 */
	protected boolean checkObstructionsPointAll(float x1, float y1, float x2, float y2)
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
		for(int i = 0; i < currentCircleAll; i++)
		{
			if(!hitBack)
			{
				circM = -(1 / m1);
				circB = oCircYAll[i] - (circM * oCircXAll[i]);
				tempX = (circB - b1) / (m1 - circM);
				if(x1 < tempX && tempX < x2)
				{
					tempY = (circM * tempX) + circB;
					if(Math.sqrt(Math.pow(tempX - oCircXAll[i], 2) + Math.pow((tempY - oCircYAll[i]) / oCircRatioAll[i], 2)) < oCircRadiusAll[i])
					{
						if(!checkHitBackPass(tempX, tempY))
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
		for(int i = 0; i < currentRectangleAll; i++)
		{
			if(!hitBack)
			{
				//Right and left Checks
				if(x1 < oRectX1All[i] && oRectX1All[i] < x2)
				{
					tempY = (m1 * oRectX1All[i]) + b1;
					if(oRectY1All[i] < tempY && tempY < oRectY2All[i])
					{
						if(!checkHitBackPass(oRectX1All[i], tempY))
						{
							hitBack = true;
						}
					}
				}
				if(!hitBack)
				{
					if(x1 < oRectX2All[i] && oRectX2All[i] < x2)
					{
						tempY = (m1 * oRectX2All[i]) + b1;
						if(oRectY1All[i] < tempY && tempY < oRectY2All[i])
						{
							if(!checkHitBackPass(oRectX2All[i], tempY))
							{
								hitBack = true;
							}
						}
					}
				}
				//Top and Bottom checks
				if(!hitBack)
				{
					if(y1 < oRectY1All[i] && oRectY1All[i] < y2)
					{
						tempX = (oRectY1All[i] - b1) / m1;
						if(oRectX1All[i] < tempX && tempX < oRectX2All[i])
						{
							if(!checkHitBackPass(tempX, oRectY1All[i]))
							{
								hitBack = true;
							}
						}
					}
				}
				if(!hitBack)
				{
					if(y1 < oRectY2All[i] && oRectY2All[i] < y2)
					{
						tempX = (oRectY2All[i] - b1) / m1;
						if(oRectX1All[i] < tempX && tempX < oRectX2All[i])
						{
							if(!checkHitBackPass(tempX, oRectY2All[i]))
							{
								hitBack = true;
							}
						}
					}
				}
			}
		}
		for(int i = 0; i < currentRingAll; i++)
		{
			if(!hitBack)
			{
				double a = Math.pow(m1, 2) + 1;
				double b = 2 * ((m1 * b1) - (m1 * oRingYAll[i]) - (oRingXAll[i]));
				double c = Math.pow(b1, 2) + (Math.pow(oRingYAll[i], 2)) - (2 * b1 * oRingYAll[i]) + Math.pow(oRingXAll[i], 2) - Math.pow(140, 2);
				double temp = (-4 * a * c) + Math.pow(b, 2);
				if(temp >= 0)
				{
					double change = Math.sqrt(temp);
					double pointX1 = (-b + change) / (2 * a);
					double pointX2 = (-b - change) / (2 * a);
					if(x1 < pointX1 && pointX1 < x2)
					{
						double pointY = (m1 * pointX1) + b1;
						if(!checkHitBackPass(pointX1, pointY))
						{
							hitBack = true;
						}
					}
					if(x1 < pointX2 && pointX2 < x2)
					{
						double pointY = (m1 * pointX2) + b1;
						if(!checkHitBackPass(pointX2, pointY))
						{
							hitBack = true;
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
	protected boolean checkObstructionsTall(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPointTall((float) x1, (float) y1, (float) x2, (float) y2);
	}
	/**
	 * checks whether a human could travel along a given line
	 * @param x1 start x
	 * @param y1 start y
	 * @param rads direction to travel
	 * @param distance distance to travel
	 * @return whether the human could travel along the given line
	 */
	protected boolean checkObstructionsAll(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPointAll((float) x1, (float) y1, (float) x2, (float) y2);
	}
	/**
	 * checks whether a given point hits any obstacles
	 * @param X x point
	 * @param Y y point
	 * @return whether it hits
	 */
	protected boolean checkHitBack(double X, double Y)
	{
		boolean hitBack = false;
		if(X < 0 || X > levelWidth || Y < 0 || Y > levelHeight)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < currentRectangle; i++)
			{
				if(hitBack == false)
				{
					if(X > oRectX1[i] && X < oRectX2[i])
					{
						if(Y > oRectY1[i] && Y < oRectY2[i])
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < currentCircle; i++)
			{
				if(hitBack == false)
				{
					if(Math.pow(X - oCircX[i], 2) + Math.pow((Y - oCircY[i]) / oCircRatio[i], 2) < Math.pow(oCircRadius[i], 2))
					{
						hitBack = true;
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < currentRing; i++)
			{
				if(hitBack == false)
				{
					double dist = Math.pow(X - oRingX[i], 2) + Math.pow((Y - oRingY[i]), 2);
					if(dist < Math.pow(oRingOuter[i], 2) && dist > Math.pow(oRingInner[i], 2))
					{
						hitBack = true;
					}
				}
			}
		}
		if(hitBack)
		{
			hitBack = !checkHitBackPass(X, Y);
		}
		return hitBack;
	}
	/**
	 * checks whether a given point hits any passages
	 * @param X x point
	 * @param Y y point
	 * @return whether it hits
	 */
	protected boolean checkHitBackPass(double X, double Y)
	{
		boolean hitBack = false;
		for(int i = 0; i < currentPassage; i++)
		{
			if(hitBack == false)
			{
				if(X > oPassageX1[i] && X < oPassageX2[i])
				{
					if(Y > oPassageY1[i] && Y < oPassageY2[i])
					{
						hitBack = true;
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
	protected void setORectAll(int oRectX1, int oRectX2, int oRectY1, int oRectY2)
	{
		oRectX1All[currentRectangleAll] = oRectX1;
		oRectX2All[currentRectangleAll] = oRectX2;
		oRectY1All[currentRectangleAll] = oRectY1;
		oRectY2All[currentRectangleAll] = oRectY2;
		currentRectangleAll++;
	}
	/**
	 * sets values for an index of all passage wall value arrays
	 * @param i index to set values to
	 * @param oRectX1 left x
	 * @param oRectX2 right x
	 * @param oRectY1 top y
	 * @param oRectY2 bottom y
	 */
	protected void setOPassage(int oRectX1, int oRectX2, int oRectY1, int oRectY2)
	{
		oPassageX1[currentPassage] = oRectX1;
		oPassageX2[currentPassage] = oRectX2;
		oPassageY1[currentPassage] = oRectY1;
		oPassageY2[currentPassage] = oRectY2;
		currentPassage++;
	}
	/**
	 * sets values for an index of all tall circle wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oCircRadius radius
	 * @param oCircRatio ratio between width and height
	 */
	protected void setOCircAll(int oCircX, int oCircY, int oCircRadius, double oCircRatio)
	{
		oCircXAll[currentCircleAll] = oCircX;
		oCircYAll[currentCircleAll] = oCircY;
		oCircRadiusAll[currentCircleAll] = oCircRadius;
		oCircRatioAll[currentCircleAll] = oCircRatio;
		currentCircleAll++;
	}
	/**
	 * sets values for an index of all ring wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oRingIn inner ring radius
	 * @param oRingOut outer ring radius
	 */
	protected void setORing(int oCircX, int oCircY, int oRingIn, int oRingOut)
	{
		oRingX[currentRing] = oCircX;
		oRingY[currentRing] = oCircY;
		oRingInner[currentRing] = oRingIn;
		oRingOuter[currentRing] = oRingOut;
		currentRing++;
	}
	/**
	 * sets values for an index of all ring wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oRingIn inner ring radius
	 * @param oRingOut outer ring radius
	 */
	protected void setORingAll(int oCircX, int oCircY, int oRingIn, int oRingOut)
	{
		oRingXAll[currentRingAll] = oCircX;
		oRingYAll[currentRingAll] = oCircY;
		oRingInnerAll[currentRingAll] = oRingIn;
		oRingOuterAll[currentRingAll] = oRingOut;
		currentRingAll++;
	}
	/**
	 * sets values for an index of all rectangle wall value arrays
	 * @param i index to set values to
	 * @param oRectX1 left x
	 * @param oRectX2 right x
	 * @param oRectY1 top y
	 * @param oRectY2 bottom y
	 */
	protected void setORect(int oRectX1, int oRectX2, int oRectY1, int oRectY2)
	{
		this.oRectX1[currentRectangle] = oRectX1;
		this.oRectX2[currentRectangle] = oRectX2;
		this.oRectY1[currentRectangle] = oRectY1;
		this.oRectY2[currentRectangle] = oRectY2;
		currentRectangle++;
	}
	/**
	 * sets values for an index of all circle wall value arrays
	 * @param i index to set values to
	 * @param oCircX x position
	 * @param oCircY y position
	 * @param oCircRadius radius
	 * @param oCircRatio ratio between width and height
	 */
	protected void setOCirc(int oCircX, int oCircY, int oCircRadius, double oCircRatio)
	{
		this.oCircX[currentCircle] = oCircX;
		this.oCircY[currentCircle] = oCircY;
		this.oCircRadius[currentCircle] = oCircRadius;
		this.oCircRatio[currentCircle] = oCircRatio;
		currentCircle++;
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