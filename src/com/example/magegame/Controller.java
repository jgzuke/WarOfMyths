//Zatanna 
//Tifa Lockheart
//Nariko
//Elena Fisher
//Madison Paige
//resident evil Ada
//Sophitia Alexandra
//



//@author (classes and interfaces only, required)
//@version (classes and interfaces only, required. See footnote 1)
//@param (methods and constructors only)
//@return (methods only)
//@exception (@throws is a synonym added in Javadoc 1.2)
//@see
//@since
//@serial (or @serialField or @serialData)
//@deprecated (see How and When To Deprecate APIs)
/* Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
 * @param DifficultyLevel Chosen difficulty setting which dictates enemy reaction time and DifficultyLevelMultiplier
 * @param DifficultyLevelMultiplier Function of DifficultyLevel which changes enemy health, mana, speed
 * @param EnemyType Mage type of enemy
 * @param PlayerType Mage type of player
 * @param LevelNum Level chosen to fight on
 * @param player Player object that has health etc and generates movement handler
 * @param enemy Enemy object with health etc and ai
 * @param enemies Array of all enemies currently on screen excluding main mage enemy
 * @param powerBalls Array of all enemy or player powerBalls
 * @param powerBallAOEs Array of all enemy or player powerBall explosions
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
package com.example.magegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;

import java.util.Random;
public final class Controller extends AllViews
{
	protected int currentTutorial = 1;
	protected Enemy[] enemies = new Enemy[30];
	private PowerUp[] powerUps = new PowerUp[30];
	private PowerBall[] powerBalls = new PowerBall[30];
	private PowerBallAOE[] powerBallAOEs = new PowerBallAOE[30];
	private Wall_Rectangle[] walls = new Wall_Rectangle[30];
	private Graphic_Teleport[] graphic_Teleport = new Graphic_Teleport[30];
	private Wall_Circle[] wallCircles = new Wall_Circle[30];
	private Wall_Ring[] wallRings = new Wall_Ring[30];
	private Wall_Pass[] wallPasses = new Wall_Pass[30];
	private Rect aoeRect = new Rect();
	private int wallWidth = 10;
	private boolean gameEnded = false;
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator = new Random();
	private int difficultyLevel = 10;
	private double difficultyLevelMultiplier;
	protected int playerType = 0;
	protected int levelNum = 0;
	private int warningTimer;
	private int warningType;
	private int[] oPassageX1;
	private int[] oPassageX2;
	private int[] oPassageY1;
	private int[] oPassageY2;
	private int[] oRingX;
	private int[] oRingY;
	private int[] oRingInner;
	private int[] oRingOuter;
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
	private int currentPassage = 0;
	private Bitmap background;
	private PlayerGestureDetector detect;
	private double spChangeForType;
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
	protected int winnings = 0;
	private Runnable frameCaller = new Runnable()
	{
		public void run()
		{
				if(!gamePaused && activity.gameRunning)
				{
					frameCall();
				}
			mHandler.postDelayed(this, 50);
		}
	};
	/* 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public Controller(Context startSet, StartActivity activitySet)
	{
		super(startSet);
		activity = activitySet;
		context = startSet;
		imageLibrary = new ImageLibrary(startSet, this);
		screenMinX = activitySet.screenMinX;
		screenMinY = activitySet.screenMinY;
		screenDimensionMultiplier = activitySet.screenDimensionMultiplier;
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
    	setBackgroundColor(Color.WHITE);
        setKeepScreenOn(true);
        player = new Player(this);
        detect = new PlayerGestureDetector(this);
		setOnTouchListener(detect);
		detect.setPlayer(player);
		changePlayerType(0);
		changeDifficulty(10);
		startFighting(0);
        frameCaller.run();
        check = imageLibrary.loadImage("menu_check", 20, 20);
		changePlayOptions();
    }
	protected void endFighting()
	{
		player.resetVariables();
		enemies = new Enemy[30];
		powerBalls = new PowerBall[30];
		powerBallAOEs = new PowerBallAOE[30];
		powerUps = new PowerUp[30];
		graphic_Teleport = new Graphic_Teleport[30];
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
		currentPassage = 0;
		imageLibrary.currentLevelTop = null;
		activity.saveGame();
	}
	protected void startFighting(int levelNumSet)
	{
		endFighting();
		levelNum = levelNumSet;
		loadLevel();
	}
	protected void changeDifficulty(int difficultyLevelSet)
	{
		difficultyLevel = difficultyLevelSet;
		difficultyLevelMultiplier = 17 / (double)(difficultyLevel + 7);
	}
	protected void changePlayOptions()
	{
		detect.getSide();
		background = drawStart();
		invalidate();
	}
	protected void changePlayerType(int playerTypeSet)
	{
		playerType = playerTypeSet;
		player.humanType = playerType;
		imageLibrary.loadPlayerPowerBall();
		switch(playerType)
		{
		case 0:
			player.spChangeForType = activity.wApollo/10;
			break;
		case 1:
			player.spChangeForType = activity.wPoseidon/10;
			break;
		case 2:
			player.spChangeForType = activity.wZues/10;
			break;
		case 3:
			player.spChangeForType = activity.wHades/10;
			break;
		}
		background = drawStart();
	}
	/*
	 * Loads the level the user picked and initializes teleport and wall array variables
	 */
	protected Wall_Rectangle makeWall_Rectangle(int x, int y, int width, int height, boolean HitPlayer, boolean tall)
	{
		Wall_Rectangle wall1 = new Wall_Rectangle(this, x-2, y-2, width+4, height+4, HitPlayer, tall);
		return wall1;
	}
	protected Wall_Ring makeWall_Ring(int x, int y, int radIn, int radOut)
	{
		Wall_Ring wall1 = new Wall_Ring(this, x, y, radIn-2, radOut+2);
		return wall1;
	}
	protected void makeWall_Pass(int x, int y, int width, int height)
	{
		Wall_Pass wall1 = new Wall_Pass(this, x-2, y-2, width+4, height+4);
	}
	protected Wall_Circle makeWall_Circle(int x, int y, int rad, double ratio, boolean tall)
	{
		Wall_Circle wall1 = new Wall_Circle(this, x, y, rad+2, ratio, tall);
		return wall1;
	}
	protected void createWallRectangleValueArrays(int length)
	{
		oRectX1 = new int[length];
		oRectX2 = new int[length];
		oRectY1 = new int[length];
		oRectY2 = new int[length];
	}
	protected void createWallCircleValueArrays(int length)
	{
		oCircX = new int[length];
		oCircY = new int[length];
		oCircRadius = new int[length];
		oCircRatio =  new double[length];
	}
	protected void createWallRectangleValueArraysAll(int length)
	{
		oRectX1All = new int[length];
		oRectX2All = new int[length];
		oRectY1All = new int[length];
		oRectY2All = new int[length];
	}
	protected void createWallCircleValueArraysAll(int length)
	{
		oCircXAll = new int[length];
		oCircYAll = new int[length];
		oCircRadiusAll = new int[length];
		oCircRatioAll =  new double[length];
	}
	protected void createWallRingValueArrays(int length)
	{
		oRingX = new int[length];
		oRingY = new int[length];
		oRingInner = new int[length];
		oRingOuter =  new int[length];
	}
	protected void createWallPassageValueArrays(int length)
	{
		oPassageX1 = new int[length];
		oPassageX2 = new int[length];
		oPassageY1 = new int[length];
		oPassageY2 =  new int[length];
	}
	protected void loadLevel()
	{
		if(levelNum == 0)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(23);
			createWallCircleValueArrays(20);
			createWallRectangleValueArraysAll(26);
			createWallCircleValueArraysAll(28);
			levelWidth = 500;
			levelHeight = 650;
			player.x = 250;
			player.y = 280;
			enemies[0] = new Enemy_Swordsman(this, 375, 191);
			enemies[1] = new Enemy_Swordsman(this, 482, 191);
			
			walls[0] = makeWall_Rectangle(0, 0, 10, 40, true, true);
			walls[1] = makeWall_Rectangle(0, 66, 10, 44, true, true);
			walls[2] = makeWall_Rectangle(0, 137, 10, 40, true, true);
			walls[3] = makeWall_Rectangle(490, 0, 10, 40, true, true);
			walls[4] = makeWall_Rectangle(490, 66, 10, 44, true, true);
			walls[5] = makeWall_Rectangle(490, 137, 10, 40, true, true);
			
			walls[6] = makeWall_Rectangle(0, 0, 39, 10, true, true);
			walls[7] = makeWall_Rectangle(68, 0, 36, 10, true, true);
			walls[8] = makeWall_Rectangle(134, 0, 36, 10, true, true);
			walls[9] = makeWall_Rectangle(199, 0, 36, 10, true, true);
			walls[10] = makeWall_Rectangle(264, 0, 36, 10, true, true);
			walls[11] = makeWall_Rectangle(329, 0, 36, 10, true, true);
			walls[12] = makeWall_Rectangle(395, 0, 36, 10, true, true);
			walls[13] = makeWall_Rectangle(461, 0, 39, 10, true, true);
			
			walls[14] = makeWall_Rectangle(0, 163, 230, 10, true, true);
			walls[15] = makeWall_Rectangle(270, 163, 230, 10, true, true);
			walls[16] = makeWall_Rectangle(0, 385, 219, 10, true, true);
			walls[17] = makeWall_Rectangle(281, 385, 219, 10, true, true);
			
			walls[18] = makeWall_Rectangle(141, 172, 10, 90, true, true);
			walls[19] = makeWall_Rectangle(141, 295, 10, 90, true, true);
			walls[20] = makeWall_Rectangle(347, 172, 10, 90, true, true);
			walls[21] = makeWall_Rectangle(347, 295, 10, 90, true, true);
			walls[22] = makeWall_Rectangle(347, 255, 10, 47, false, true);
			
			walls[23] = makeWall_Rectangle(0, 165, 148, 47, true, false);//storage
			walls[24] = makeWall_Rectangle(351, 165, 98, 36, true, false);//barrels
			walls[25] = makeWall_Rectangle(62, 251, 27, 72, true, false);//barrels
			
			wallCircles[0] = makeWall_Circle(475, 420, 15, 1, true);//hades
			wallCircles[1] = makeWall_Circle(20, 420, 15, 1, true);//apollo
			wallCircles[2] = makeWall_Circle(20, 625, 15, 1, true);//posiedon
			wallCircles[3] = makeWall_Circle(475, 625, 15, 1, true);//zues
			
			wallCircles[4] = makeWall_Circle(423, 466, 8, 1, true);//same again
			wallCircles[5] = makeWall_Circle(68, 466, 8, 1, true);
			wallCircles[6] = makeWall_Circle(68, 580, 8, 1, true);
			wallCircles[7] = makeWall_Circle(423, 580, 8, 1, true);
			
			wallCircles[8] = makeWall_Circle(174, 420, 15, 1, true);//heph
			wallCircles[9] = makeWall_Circle(174, 630, 15, 1, true);//athena
			wallCircles[10] = makeWall_Circle(326, 420, 15, 1, true);//hermes
			wallCircles[11] = makeWall_Circle(326, 630, 15, 1, true);//ares
			
			wallCircles[12] = makeWall_Circle(195, 219, 8, 1, true);
			wallCircles[13] = makeWall_Circle(318, 224, 8, 1, true);
			wallCircles[14] = makeWall_Circle(306, 373, 8, 1, true);
			wallCircles[15] = makeWall_Circle(371, 129, 8, 1, true);
			wallCircles[16] = makeWall_Circle(473, 87, 8, 1, true);
			wallCircles[17] = makeWall_Circle(281, 20, 8, 1, true);
			wallCircles[18] = makeWall_Circle(151, 35, 8, 1, true);
			wallCircles[19] = makeWall_Circle(24, 91, 8, 1, true);
			
			wallCircles[20] = makeWall_Circle(99, 377, 6, 1, false);//ambrosia
			wallCircles[21] = makeWall_Circle(15, 322, 6, 1, false);//cooldown
			wallCircles[22] = makeWall_Circle(127, 334, 6, 1, false);//posiedon
			wallCircles[23] = makeWall_Circle(16, 263, 6, 1, false);//hades
			wallCircles[24] = makeWall_Circle(126, 234, 6, 1, false);//zues
			wallCircles[25] = makeWall_Circle(53, 376, 6, 1, false);//apollo
			
			wallCircles[26] = makeWall_Circle(406, 212, 6, 1, false);
			wallCircles[27] = makeWall_Circle(488, 321, 6, 1, false);
		}
		if(levelNum == 1)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(3);
			createWallCircleValueArrays(0);
			createWallRectangleValueArraysAll(3);
			createWallCircleValueArraysAll(0);
			levelWidth = 500;
			levelHeight = 300;
			player.x = 250;
			player.y = 150;
			walls[0] = makeWall_Rectangle(110, 0, 10, 300, true, true);
			walls[1] = makeWall_Rectangle(380, 0, 10, 125, true, true);
			walls[2] = makeWall_Rectangle(380, 175, 10, 125, true, true);
		}
		if(levelNum == 2)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("archer", true);
			createWallRectangleValueArrays(5);
			createWallCircleValueArrays(3);
			createWallRectangleValueArraysAll(7);
			createWallCircleValueArraysAll(3);
			levelWidth = 500;
			levelHeight = 300;
			player.x = 476;
			player.y = 112;
			enemies[0] = new Enemy_Archer(this, 338, 35);
			enemies[1] = new Enemy_Swordsman(this, 269, 85);
			enemies[2] = new Enemy_Archer(this, 30, 192);
			enemies[3] = new Enemy_Swordsman(this, 61, 249);
			walls[0] = makeWall_Rectangle(15, 8, 100, 70, true, true);
			walls[1] = makeWall_Rectangle(103, -9, 141, 153, true, true);
			walls[2] = makeWall_Rectangle(119, 125, 108, 59, true, true);
			
			walls[3] = makeWall_Rectangle(356, 67, 96, 121, true, true);
			walls[4] = makeWall_Rectangle(345, 175, 122, 135, true, true);
			
			walls[5] = makeWall_Rectangle(136, 177, 13, 80, true, false);
			walls[6] = makeWall_Rectangle(136, 245, 56, 13, true, false);
			wallCircles[0] = makeWall_Circle(108, 178, 15, 1, true);
			wallCircles[1] = makeWall_Circle(335, 221, 15, 1, true);
			wallCircles[2] = makeWall_Circle(460, 71, 15, 1, true);
		}
		if(levelNum == 3)
		{
			imageLibrary.changeArrayLoaded("axeman", true);
			imageLibrary.changeArrayLoaded("pikeman", true);
			createWallRectangleValueArrays(0);
			createWallCircleValueArrays(7);
			createWallRectangleValueArraysAll(2);
			createWallCircleValueArraysAll(7);
			levelWidth = 400;
			levelHeight = 400;
			player.x = 370;
			player.y = 200;
			enemies[0] = new Enemy_Pikeman(this, 244, 239);
			enemies[1] = new Enemy_Pikeman(this, 91, 114);
			enemies[2] = new Enemy_Pikeman(this, 139, 180);
			enemies[3] = new Enemy_Axeman(this, 309, 184);
			enemies[4] = new Enemy_Axeman(this, 103, 180);
			enemies[5] = new Enemy_Axeman(this, 166, 114);
			walls[0] = makeWall_Rectangle(80, 127, 79, 33, true, false);
			walls[1] = makeWall_Rectangle(263, 179, 30, 78, true, false);
			wallCircles[0] = makeWall_Circle(148, 69, 15, 1, true);
			wallCircles[1] = makeWall_Circle(252, 74, 15, 1, true);
			wallCircles[2] = makeWall_Circle(35, 200, 15, 1, true);
			wallCircles[3] = makeWall_Circle(108, 357, 15, 1, true);
			wallCircles[4] = makeWall_Circle(230, 370, 15, 1, true);
			wallCircles[5] = makeWall_Circle(374, 203, 15, 1, true);
			wallCircles[6] = makeWall_Circle(202, 201, 8, 1, false);
		}
		if(levelNum == 4)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("pikeman", true);
			createWallRectangleValueArrays(2);
			createWallCircleValueArrays(0);
			createWallRectangleValueArraysAll(9);
			createWallCircleValueArraysAll(1);
			levelWidth = 500;
			levelHeight = 300;
			player.x = 452;
			player.y = 265;
			enemies[0] = new Enemy_Pikeman(this, 22, 44);
			enemies[1] = new Enemy_Swordsman(this, 36, 202);
			enemies[2] = new Enemy_Pikeman(this, 192, 51);
			enemies[3] = new Enemy_Swordsman(this, 286, 48);
			enemies[4] = new Enemy_Mage(this, 460, 62);
			enemies[5] = new Enemy_Mage(this, 120, 48);
			walls[0] = makeWall_Rectangle(35, 58, 79, 31, true, false);
			walls[1] = makeWall_Rectangle(202, 59, 79, 31, true, false);
			walls[2] = makeWall_Rectangle(37, 217, 79, 31, true, false);
			walls[3] = makeWall_Rectangle(209, 220, 79, 31, true, false);
			walls[4] = makeWall_Rectangle(375, 14, 79, 31, true, false);
			walls[5] = makeWall_Rectangle(419, 92, 90, 65, true, false);
			walls[6] = makeWall_Rectangle(357, -13, 10, 207, true, true);
			walls[7] = makeWall_Rectangle(357, 225, 10, 85, true, true);
			walls[8] = makeWall_Rectangle(375, 185, 153, 10, true, false);
			wallCircles[0] = makeWall_Circle(180, 160, 15, 1, false);
		}
		if(levelNum == 5)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("pikeman", true);
			imageLibrary.changeArrayLoaded("rogue", true);
			createWallRectangleValueArrays(17);
			createWallCircleValueArrays(0);
			createWallRectangleValueArraysAll(25);
			createWallCircleValueArraysAll(1);
			levelWidth = 450;
			levelHeight = 650;
			player.x = 452;
			player.y = 265;
			enemies[0] = new Enemy_Pikeman(this, 207, 578);
			enemies[1] = new Enemy_Pikeman(this, 311, 549);
			enemies[2] = new Enemy_Pikeman(this, 397, 573);
			enemies[3] = new Enemy_Swordsman(this, 175, 219);
			enemies[4] = new Enemy_Swordsman(this, 281, 222);
			enemies[5] = new Enemy_Swordsman(this, 267, 313);
			enemies[6] = new Enemy_Swordsman(this, 362, 133);
			enemies[7] = new Enemy_Swordsman(this, 355, 70);
			enemies[8] = new Enemy_Mage(this, 250, 136);
			enemies[9] = new Enemy_Mage(this, 177, 96);
			enemies[10] = new Enemy_Mage(this, 61, 38);
			enemies[11] = new Enemy_Rogue(this, 72, 536);
			enemies[12] = new Enemy_Rogue(this, 17, 91);
			wallCircles[0] = makeWall_Circle(116, 33, 15, 1, false);
			walls[0] = makeWall_Rectangle(-92, -116, 138, 192, true, false);
			walls[1] = makeWall_Rectangle(158, -98, 119, 175, true, false);
			walls[2] = makeWall_Rectangle(212, -128, 22, 296, true, true);
			walls[3] = makeWall_Rectangle(221, -124, 92, 241, true, false);
			walls[4] = makeWall_Rectangle(392, -131, 192, 252, true, true);
			walls[5] = makeWall_Rectangle(-49, 106, 105, 14, true, true);
			walls[6] = makeWall_Rectangle(-27, 112, 40, 232, true, true);
			walls[7] = makeWall_Rectangle(42, 161, 14, 182, true, true);
			walls[8] = makeWall_Rectangle(50, 157, 350, 20, true, true);
			walls[9] = makeWall_Rectangle(394, 162, 14, 182, true, true);
			walls[10] = makeWall_Rectangle(97, 249, 80, 33, true, false);
			walls[11] = makeWall_Rectangle(99, 364, 78, 32, true, false);
			walls[12] = makeWall_Rectangle(272, 368, 79, 32, true, false);
			walls[13] = makeWall_Rectangle(-31, 469, 41, 49, true, true);
			walls[14] = makeWall_Rectangle(-44, 512, 137, 11, true, true);
			walls[15] = makeWall_Rectangle(88, 519, 11, 92, true, true);
			walls[16] = makeWall_Rectangle(60, 552, 34, 62, true, false);
			walls[17] = makeWall_Rectangle(46, 468, 441, 11, true, true);
			walls[18] = makeWall_Rectangle(140, 473, 346, 19, true, true);
			walls[19] = makeWall_Rectangle(275, 249, 77, 33, true, false);
			walls[20] = makeWall_Rectangle(134, 474, 11, 139, true, true);
			walls[21] = makeWall_Rectangle(437, 111, 41, 232, true, true);
			walls[22] = makeWall_Rectangle(50, 167, 40, 48, true, true);
			walls[23] = makeWall_Rectangle(360, 172, 40, 40, true, true);
			walls[24] = makeWall_Rectangle(108, 120, 115, 48, true, false);
		}
		if(levelNum == 6)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("archer", true);
			createWallRectangleValueArrays(8);
			createWallRectangleValueArraysAll(10);
			createWallCircleValueArraysAll(9);
			levelWidth = 400;
			levelHeight = 600;
			player.x = 350;
			player.y = 300;
			enemies[0] = new Enemy_Archer(this, 150, 25);
			enemies[1] = new Enemy_Archer(this, 221, 54);
			enemies[2] = new Enemy_Archer(this, 283, 24);
			enemies[3] = new Enemy_Archer(this, 258, 253);
			enemies[4] = new Enemy_Mage(this, 146, 304);
			enemies[5] = new Enemy_Mage(this, 194, 559);
			enemies[6] = new Enemy_Swordsman(this, 165, 143);
			enemies[7] = new Enemy_Swordsman(this, 202, 420);
			enemies[8] = new Enemy_Swordsman(this, 64, 569);
			enemies[9] = new Enemy_Swordsman(this, 318, 565);
			
			walls[0] = makeWall_Rectangle(-62, -112, 79, 186, true, false);
			walls[1] = makeWall_Rectangle(381, -143, 62, 221, true, false);
			walls[2] = makeWall_Rectangle(43, 96, 10, 190, true, true);
			walls[3] = makeWall_Rectangle(43, 317, 10, 186, true, true);
			walls[4] = makeWall_Rectangle(88, 137, 10, 322, true, true);
			walls[5] = makeWall_Rectangle(302, 137, 10, 322, true, true);
			walls[6] = makeWall_Rectangle(346, 98, 10, 185, true, true);
			walls[7] = makeWall_Rectangle(346, 316, 10, 186, true, true);
			walls[8] = makeWall_Rectangle(49, 497, 303, 10, true, true);
			walls[9] = makeWall_Rectangle(49, 92, 303, 10, true, true);
			wallCircles[0] = makeWall_Circle(200, 14, 9, 1, false);
			wallCircles[1] = makeWall_Circle(237, 11, 9, 1, false);
			wallCircles[2] = makeWall_Circle(212, 85, 9, 1, false);
			wallCircles[3] = makeWall_Circle(239, 83, 9, 1, false);
			wallCircles[4] = makeWall_Circle(157, 221, 14, 1, false);
			wallCircles[5] = makeWall_Circle(258, 182, 14, 1, false);
			wallCircles[6] = makeWall_Circle(218, 293, 14, 1, false);
			wallCircles[7] = makeWall_Circle(147, 404, 14, 1, false);
			wallCircles[8] = makeWall_Circle(254, 403, 14, 1, false);
		}
		if(levelNum == 7)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(4);
			createWallRectangleValueArraysAll(4);
			createWallRingValueArrays(1);
			createWallPassageValueArrays(2);
			levelWidth = 400;
			levelHeight = 500;
			player.x = 379;
			player.y = 250;
			enemies[0] = new Enemy_Swordsman(this, 379, 250);
			makeWall_Pass(168, 73, 62, 98);
			makeWall_Pass(168, 358, 62, 98);
			wallRings[0] = makeWall_Ring(197, 253, 130, 150);
			walls[0] = makeWall_Rectangle(118, -152, 20, 282, true, true);
			walls[1] = makeWall_Rectangle(263, 61, 19, 71, true, true);
			walls[2] = makeWall_Rectangle(118, 375, 19, 283, true, true);
			walls[3] = makeWall_Rectangle(262, 375, 19, 68, true, true);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	protected int fixXBoundsHpBar(int minX, int maxX)
	{
		int offset = 0;
		if(minX<90)
		{
			offset = 90-minX;
		} else if(maxX>390)
		{
			offset = 390-maxX;
		}
		return offset;
	}
	protected int fixYBoundsHpBar(int minY, int maxY)
	{
		int offset = 0;
		if(minY<10)
		{
			offset = 10-minY;
		} else if(maxY>310)
		{
			offset = 310-maxY;
		}
		return offset;
	}
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
				if(!enemies[i].rogue || enemies[i].currentFrame!=49)
				{
					minX = (int) enemies[i].getX() - 20;
					maxX = (int) enemies[i].getX() + 20;
					minY = (int) enemies[i].getY() - 30;
					maxY = (int) enemies[i].getY() - 20;
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
	}
	/*
	 * Draws hp, mp, sp, and cooldown bars for player and enemies
	 */
	protected void drawContestantStats(Canvas g)
	{
		int fix = 390;
		if(activity.stickOnRight) fix = 0;
		paint.setAlpha(255);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		//drawRect(395, 240, 475, 316, g);
		//drawRect(5, 240, 85, 316, g);
		paint.setColor(Color.RED);
		drawRect(400-fix, 116, 400-fix + (70 * player.getHp() / (int)(700 * activity.wHephaestus)), 132, g);
		paint.setColor(Color.GREEN);
		drawRect(400-fix, 163, 400-fix + (int)(70 * player.getSp()), 179, g);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect(400-fix, 116, 470-fix, 132, g);
		drawRect(400-fix, 163, 470-fix, 179, g);
		paint.setTextSize(12);
		drawText(Integer.toString(player.getHp()), 417-fix, 129, g);
		drawText(Integer.toString((int)(3500*player.getSp())), 417-fix, 176, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.YELLOW);
		drawRect(12+fix, 68, 12+fix + (int)((66 * player.getAbilityTimer_burst()) / 500), 78, g);
		drawRect(12+fix, 200, 12+fix + (int)((66 * player.getAbilityTimer_roll()) / 120), 210, g);
		drawRect(12+fix, 300, 12+fix + (int)((66 * player.getAbilityTimer_powerBall()) / 40), 310, g);
		drawRect(12+fix, 134, 12+fix + (int)((66 * player.getAbilityTimer_teleport()) / 350), 144, g);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawRect(12+fix, 68, 78+fix, 78, g);
		drawRect(12+fix, 200, 78+fix, 210, g);
		drawRect(12+fix, 300, 78+fix, 310, g);
		drawRect(12+fix, 134, 78+fix, 144, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		paint.setAlpha(151);
		if(player.teleporting || player.rollTimer>0)
		{
			drawRect(12+fix, 12, 78+fix, 78, g);
			drawRect(12+fix, 144, 78+fix, 210, g);
			drawRect(12+fix, 78, 78+fix, 144, g);
		} else
		{
			if(player.getAbilityTimer_burst() < 400)
			{
				drawRect(12+fix, 12, 78+fix, 78, g);
			}
			if(player.getAbilityTimer_roll() < 50)
			{
				drawRect(12+fix, 144, 78+fix, 210, g);
			}
			if(player.getAbilityTimer_teleport() < 250)
			{
				drawRect(12+fix, 78, 78+fix, 144, g);
			}
		}
		paint.setAlpha(255);
	}
	/*
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	protected void frameCall()
	{
		for(int i = 0; i < graphic_Teleport.length; i++)
		{
			if(graphic_Teleport[i] != null)
			{
				if(graphic_Teleport[i].isDeleted())
				{
					graphic_Teleport[i] = null;
				}
				else
				{
					graphic_Teleport[i].frameCall();
				}
			}
		}
		for(int i = 0; i < powerBalls.length; i++)
		{
			if(powerBalls[i] != null)
			{
				if(powerBalls[i].isDeleted())
				{
					powerBalls[i] = null;
				}
				else
				{
					powerBalls[i].frameCall();
				}
			}
		}
		for(int i = 0; i < powerBallAOEs.length; i++)
		{
			if(powerBallAOEs[i] != null)
			{
				if(powerBallAOEs[i].isDeleted())
				{
					powerBallAOEs[i] = null;
				}
				else
				{
					powerBallAOEs[i].frameCall();
				}
			}
		}
		for(int i = 0; i < powerUps.length; i++)
		{
			if(powerUps[i] != null)
			{
				if(powerUps[i].isDeleted())
				{
					powerUps[i] = null;
				}
				else
				{
					powerUps[i].frameCall();
				}
			}
		}
		boolean alive = false;
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
				if(enemies[i].isDeleted())
				{
					enemies[i] = null;
				}
				else
				{
					enemies[i].frameCall();
					if(enemies[i] != null)
					{
						if(!enemies[i].rogue) alive = true;
						if(enemies[i].getX() < 10) enemies[i].setX(10);
						if(enemies[i].getX() > levelWidth-10) enemies[i].setX(levelWidth-10);
						if(enemies[i].getY() < 10) enemies[i].setY(10);
						if(enemies[i].getY() > levelHeight-10) enemies[i].setY(levelHeight-10);
					}
				}
			}
		}
		if(!alive&& levelNum > 1)
		{
			if(levelNum == activity.levelBeaten)
			{
				activity.levelBeaten++;
			}
			activity.startMenu(true);
		} else
		{
			if(!player.isDeleted())
			{
				player.frameCall();
				if(!player.isTeleporting())
				{
					if(player.getX() < 10) player.setX(10);
					if(player.getX() > levelWidth-10) player.setX(levelWidth-10);
					if(player.getY() < 10) player.setY(10);
					if(player.getY() > levelHeight-10) player.setY(levelHeight-10);
				}
			}
		}
		if(activity.gameRunning)
		{
			if(levelNum == 0)
			{
				if(distSquared(player.getX(), player.getY(), 68, 466)<800)
				{
					if(playerType != 0)changePlayerType(0);
				} else if(distSquared(player.getX(), player.getY(), 68, 580)<800)
				{
					if(playerType != 1)changePlayerType(1);
				} else if(distSquared(player.getX(), player.getY(), 423, 580)<800)
				{
					if(playerType != 2)changePlayerType(2);
				} else if(distSquared(player.getX(), player.getY(), 423, 466)<800)
				{
					if(playerType != 3)changePlayerType(3);
				}
				if(imageLibrary.directionsTutorial==null&&distSquared(player.getX(), player.getY(), 250, 280)<400)
				{
					imageLibrary.directionsTutorial = imageLibrary.loadImage("menu_directions", 235, 140);
				} else if(imageLibrary.directionsTutorial!=null&&distSquared(player.getX(), player.getY(), 250, 280)>400)
				{
					imageLibrary.directionsTutorial.recycle();
					imageLibrary.directionsTutorial = null;
				}
				if(player.x<15&& player.y>113 && player.y<134)
		        {
					gamePaused = true;
					currentPause = "startfight";
					startingLevel = 1;
					currentTutorial = 1;
					invalidate();
					player.x += 15;
		        } else if(player.x<15 && player.y>41 && player.y<64)
		        {
		        	if(activity.levelBeaten >= 1)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 2;
						invalidate();
						player.x += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>42 && player.x<66)
		        {
		        	if(activity.levelBeaten >= 2)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 3;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>107 && player.x<131)
		        {
		        	if(activity.levelBeaten >= 3)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 4;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>173 && player.x<197)
		        {
		        	if(activity.levelBeaten >= 4)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 5;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>237 && player.x<260)
		        {
		        	if(activity.levelBeaten >= 5)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 6;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>304 && player.x<326)
		        {
		        	if(activity.levelBeaten >= 6)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 7;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>369 && player.x<392)
		        {
		        	if(activity.levelBeaten >= 7)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 8;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.y<15 && player.x>433 && player.x<457)
		        {
		        	if(activity.levelBeaten >= 8)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 9;
						invalidate();
						player.y += 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.x>485 && player.y>42 && player.y<62)
		        {
		        	if(activity.levelBeaten >= 9)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 10;
						invalidate();
						player.x -= 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        } else if(player.x>485 && player.y>113 && player.y<134)
		        {
		        	if(activity.levelBeaten >= 10)
		        	{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 11;
						invalidate();
						player.x -= 15;
		        	} else
		        	{
		        		levelLocked();
		        	}
		        }
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
	}
	protected double distSquared(double x1, double y1, double x2, double y2)
	{
		return Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2);
	}
	protected Bitmap drawStart()
	{
		Bitmap drawTo = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		if(activity.stickOnRight)
		{
			drawBitmap(imageLibrary.loadImage("screen000"+Integer.toString(playerType+5), 480, 320), 0, 0, g);
		} else
		{
			drawBitmap(imageLibrary.loadImage("screen000"+Integer.toString(playerType+1), 480, 320), 0, 0, g);
		}
		return drawTo;
	}
	protected Bitmap drawLevel()
	{
		Bitmap drawTo = Bitmap.createBitmap(imageLibrary.currentLevel);
		Canvas g = new Canvas(drawTo);
		if(levelNum == 0 && imageLibrary.directionsTutorial != null)
		{
			drawBitmap(imageLibrary.directionsTutorial, 133, 211, g);
		}
		if(levelNum == 1)
		{
			drawBitmap(imageLibrary.directionsTutorial, 145, 29, g);
		}
		if(player != null)
		{
			drawBitmapRotated(player, g);
		}
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
				drawBitmapRotated(enemies[i], g);
			}
		}
		for(int i = 0; i < powerBalls.length; i++)
		{
			if(powerBalls[i] != null)
			{
				drawBitmapRotated(powerBalls[i], g);
			}
		}
		for(int i = 0; i < powerBallAOEs.length; i++)
		{
			if(powerBallAOEs[i] != null)
			{
				aoeRect.top = (int)(powerBallAOEs[i].getY() - (powerBallAOEs[i].getHeight() / 2));
				aoeRect.bottom = (int)(powerBallAOEs[i].getY() - (powerBallAOEs[i].getHeight() / 2)) + (int)powerBallAOEs[i].getHeight();
				aoeRect.left = (int)(powerBallAOEs[i].getX() - (powerBallAOEs[i].getWidth() / 2));
				aoeRect.right = (int)(powerBallAOEs[i].getX() - (powerBallAOEs[i].getWidth() / 2)) + (int)powerBallAOEs[i].getWidth();
				paint.setAlpha(powerBallAOEs[i].getAlpha());
				drawBitmapRect(powerBallAOEs[i].getVisualImage(), aoeRect, g);
			}
		}
		paint.setAlpha(255);
		for(int i = 0; i < graphic_Teleport.length; i++)
		{
			if(graphic_Teleport[i] != null)
			{
				drawBitmapRotated(graphic_Teleport[i], g);
			}
		}
		for(int i = 0; i < powerUps.length; i++)
		{
			if(powerUps[i] != null)
			{
				drawBitmap(powerUps[i].getVisualImage(), (int)powerUps[i].getX() - 15, (int)powerUps[i].getY() - 15, g);
			}
		}
		if(imageLibrary.currentLevelTop != null)
		{
			drawBitmap(imageLibrary.currentLevelTop, 0, 0, g);
		}
		if(player.powerUpTimer>0)
		{
			drawBitmap(imageLibrary.effects[player.powerID-1], (int)player.x-30, (int)player.y-30, g);
		}
		drawHealthBars(g);
		return drawTo;
	}
	/*
	 * Draws objects and calls other drawing functions (background and stats)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	protected void drawStartFight(Canvas g)
	{
		drawNotPaused(g);
		drawBitmap(imageLibrary.loadImage("menu_startfight", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(difficultyLevel == 10)
		{
			winnings = 5;
			drawRect(30, 104, 130, 144, g);
		}
		if(difficultyLevel == 6)
		{
			winnings = 10;
			drawRect(130, 29, 230, 69, g);
		}
		if(difficultyLevel == 3)
		{
			winnings = 20;
			drawRect(250, 29, 350, 69, g);
		}
		if(difficultyLevel == 0)
		{
			winnings = 50;
			drawRect(350, 104, 450, 144, g);
		}
		winnings = (int)(winnings*getLevelWinningsMultiplier(startingLevel));
		if(drainHp)
		{
			winnings *= 2;
			drawRect(16, 234, 116, 304, g);
		}
		if(lowerHp)
		{
			winnings *= 2;
			drawRect(132, 234, 232, 304, g);
		}
		if(limitSpells)
		{
			winnings *= 2;
			drawRect(248, 234, 348, 304, g);
		}	
		if(enemyRegen)
		{
			winnings *= 2;
			drawRect(364, 234, 464, 304, g);
		}	
		paint.setAlpha(255);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(40);
		drawText("Level "+Integer.toString(startingLevel), 240, 113, g);
		paint.setTextSize(25);
		drawText("Winnings: "+Integer.toString(winnings), 240, 153, g);
	}
	private double getLevelWinningsMultiplier(int level)
	{
		switch(level)
		{
			case 1:
				return 0;
			case 2:
				return 1.1;
			case 3:
				return 1.3;
			case 4:
				return 1.5;
			case 5:
				return 2.2;
			default:
				return 1;
		}
	}
	protected void drawBuy(Canvas g)
	{
		drawNotPaused(g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(100);
		drawRect(90, 10, 390, 50, g);
		paint.setAlpha(255);
		drawBitmap(imageLibrary.loadImage("menu_buy", 480, 320), 0, 0, g);
		paint.setTextSize(40);
		paint.setTextAlign(Align.CENTER);
		drawText(buyingItem, 240, 103, g);
		paint.setTextSize(25);
		drawText("Cost: "+Integer.toString(activity.buy(buyingItem, 9999999, false)), 240, 143, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		String[] describe = activity.getItemDescribe(buyingItem);
		drawText(describe[0], 75, 173, g);
		drawText(describe[1], 75, 203, g);
		paint.setColor(Color.LTGRAY);
		drawText(Integer.toString(activity.realCurrency), 135, 40, g);
		paint.setColor(Color.YELLOW);
		drawText(Integer.toString(activity.gameCurrency), 270, 40, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyGame(buyingItem))
		{
			drawRect(66, 231, 214, 276, g);
		}
		if(!activity.canBuyReal(buyingItem))
		{
			drawRect(266, 231, 414, 276, g);
		}
	}
	protected void drawPaused(Canvas g)
	{
		drawNotPaused(g);
		drawBitmap(imageLibrary.loadImage("menu_paused", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(40);
		drawText(Integer.toString(activity.pHeal), 48, 73, g);
		drawText(Integer.toString(activity.pCool), 148, 73, g);
		drawText(Integer.toString(activity.pWater), 48, 173, g);
		drawText(Integer.toString(activity.pEarth), 148, 173, g);
		drawText(Integer.toString(activity.pAir), 48, 273, g);
		drawText(Integer.toString(activity.pFire), 148, 273, g);
		paint.setAlpha(151);
		if(activity.pHeal==0)
		{
			drawCircle(60, 60, 37, g);
		}
		if(activity.pCool==0)
		{
			drawCircle(160, 60, 37, g);
		}
		if(activity.pWater==0||playerType == 1)
		{
			drawCircle(60, 160, 37, g);
		}
		if(activity.pEarth==0||playerType == 3)
		{
			drawCircle(160, 160, 37, g);
		}
		if(activity.pAir==0||playerType == 2)
		{
			drawCircle(60, 260, 37, g);
		}
		if(activity.pFire==0||playerType == 0)
		{
			drawCircle(160, 260, 37, g);
		}
	}
	protected void drawOptions(Canvas g)
	{
		drawNotPaused(g);
		drawBitmap(imageLibrary.loadImage("menu_options", 480, 320), 0, 0, g);
		if(activity.stickOnRight)
		{
			drawBitmap(check, 227, 147, g);
		} else
		{
			drawBitmap(check, 338, 147, g);
		}
		if(!activity.shootTapScreen)
		{
			drawBitmap(check, 249, 174, g);
		} else
		{
			drawBitmap(check, 417, 174, g);
		}
		if(activity.shootTapDirectional)
		{
			drawBitmap(check, 236, 200, g);
		} else
		{
			drawBitmap(check, 450, 200, g);
		}
	}
	@ Override
	protected void onDraw(Canvas g)
	{
		if(activity.gameRunning)
		{
			g.translate(screenMinX, screenMinY);
			g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
			paint.setAlpha(255);
			if(gamePaused)
			{
				if(currentPause.equals("paused"))
				{
					drawPaused(g);
				} else if(currentPause.equals("options"))
				{
					drawOptions(g);
				} else if(currentPause.equals("startfight"))
				{
					drawStartFight(g);
				} else if(currentPause.equals("buy"))
				{
					drawBuy(g);
				}
			} else
			{
				drawNotPaused(g);
			}
		}
	}
	private void drawNotPaused(Canvas g)
	{
		paint.setTextAlign(Align.LEFT);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		drawRect(90, 10, 390, 310, g);
		if(!player.teleporting)
		{
			xShiftLevel = 150 - (int)player.x;
			yShiftLevel = 150 - (int)player.y;
			if(player.x < 150)
			{
				xShiftLevel = 0;
			}
			if(player.y < 150)
			{
				yShiftLevel = 0;
			}
			if(player.x > levelWidth-150)
			{
				xShiftLevel = 300-levelWidth;
			}
			if(player.y > levelHeight-150)
			{
				yShiftLevel = 300-levelHeight;
			}
		}
		drawBitmap(drawLevel(), xShiftLevel+90, yShiftLevel+10, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		drawRect(-1000, -1000, 0, 1320, g);
		drawRect(480, -1000, 1480, 1320, g);
		drawRect(-1000, -1000, 1480, 0, g);
		drawRect(-1000, 320, 1480, 1320, g);
		paint.setColor(Color.GREEN);
		if(warningTimer > 0)
		{
			warningTimer--;
			paint.setAlpha((byte)(warningTimer * 7));
			drawBitmap(imageLibrary.warnings[warningType], 240 - (imageLibrary.warnings[warningType].getWidth() / 2), 160 - (imageLibrary.warnings[warningType].getHeight() / 2), g);
		}
		paint.setAlpha(255);
		drawBitmap(background, 0, 0, g);
		drawContestantStats(g);
		paint.setStyle(Paint.Style.STROKE);
		if(player.powerUpTimer>0)
		{
			if(activity.stickOnRight)
			{
				drawBitmap(imageLibrary.powerUpBigs[player.powerID-1], 410, 25, g);
			} else
			{
				drawBitmap(imageLibrary.powerUpBigs[player.powerID-1], 20, 25, g);
			}
		}
		if(levelNum == 0)
		{
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.LEFT);
			paint.setAlpha(120);
			drawRect(90, 10, 390, 50, g);
			paint.setAlpha(255);
			drawBitmap(imageLibrary.coins[0], 95, 15, g);
			drawBitmap(imageLibrary.coins[1], 230, 15, g);
			paint.setTextSize(20);
			paint.setColor(Color.LTGRAY);
			drawText(Integer.toString(activity.realCurrency), 135, 40, g);
			paint.setColor(Color.YELLOW);
			drawText(Integer.toString(activity.gameCurrency), 270, 40, g);
		}
		if(levelNum == 1)
		{
			drawBitmap(imageLibrary.next, 200, 280, g);
		}
	}
	protected void createPowerBallEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Enemy ballEnemy = new PowerBall_Enemy(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballEnemy;
	}
	protected void createCrossbowBolt(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		CrossbowBolt boltEnemy = new CrossbowBolt(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = boltEnemy;
	}
	protected void createPowerUp(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	protected void createPowerBallPlayer(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Player ballPlayer = new PowerBall_Player(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballPlayer;
	}
	protected void createPowerBallEnemyAOE(double x, double y, double power)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power, true);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	protected void createPowerBallPlayerAOE(double x, double y, double power)
	{
		PowerBallAOE_Player ballAOEPlayer = new PowerBallAOE_Player(this, (int) x, (int) y, power, true);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
	}
	protected void createPowerBallEnemyBurst(double x, double y, double power)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power, false);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	protected void createPowerBallPlayerBurst(double x, double y, double power)
	{
		PowerBallAOE_Player ballAOEPlayer = new PowerBallAOE_Player(this, (int) x, (int) y, power, false);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
	}
	protected void createTeleport(double x, double y, double rotation)
	{
		Graphic_Teleport teleportStart = new Graphic_Teleport(this, x, y, rotation);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportStart;
	}
	/*
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
	/*
	 * Starts 'not enough mana' warning
	 */
	protected void notEnoughMana()
	{
		warningTimer = 30;
		warningType = 1;
	}
	/*
	 * Starts 'cooldown' warning
	 */
	protected void coolDown()
	{
		warningTimer = 30;
		warningType = 0;
	}
	protected void levelLocked()
	{
		warningTimer = 30;
		warningType = 1;
	}
	protected void alreadyWorshipping()
	{
		//warningTimer = 30;
		//warningType = 1;
	}
	/*
	 * Checks for a clear line starting at set coordinates and going for a set distance at set velocity
	 * @param fromX Starting x coordinate
	 * @param fromY Starting y coordinate
	 * @param moveX X velocity
	 * @param moveY Y velocity
	 * @param distance Interval at which to check whether line is clear
	 * @param distance Distance to extend line
	 * @return Returns whether line is clear
	 */
	
	
	protected boolean checkObstructionsPointTall(float x1, float y1, float x2, float y2)
	{	
		boolean hitBack = false;
		float m1 = (y2-y1)/(x2-x1);
		float b1 = y1-(m1*x1);
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
				circM = -(1/m1);
				circB = oCircY[i]-(circM*oCircX[i]);
				tempX = (circB-b1)/(m1-circM);
				if(x1 < tempX && tempX < x2)
				{
					tempY = (circM * tempX)+ circB;
					if(Math.sqrt(Math.pow(tempX-oCircX[i],2)+Math.pow((tempY-oCircY[i])/oCircRatio[i],2))<oCircRadius[i])
					{
						hitBack = true;
					}
				}
			}
		}
		if(x1>x2)
		{
			tempX = x1;
			x1 = x2;
			x2 = tempX;
		}
		if(y1>y2)
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
						tempY = (m1*oRectX1[i])+b1;
						if(oRectY1[i] < tempY && tempY < oRectY2[i])
						{
							hitBack = true;
						}
					}
					if(x1 < oRectX2[i] && oRectX2[i] < x2)
					{
						tempY = (m1*oRectX2[i])+b1;
						if(oRectY1[i] < tempY && tempY < oRectY2[i])
						{
							hitBack = true;
						}
					}
					//Top and Bottom checks
					if(y1 < oRectY1[i] && oRectY1[i] < y2)
					{
						tempX = (oRectY1[i]-b1)/m1;
						if(oRectX1[i] < tempX && tempX < oRectX2[i])
						{
							hitBack = true;
						}
					}
					if(y1 < oRectY2[i] && oRectY2[i] < y2)
					{
						tempX = (oRectY1[i]-b1)/m1;
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
					double a = Math.pow(m1, 2)+1;
					double b = 2*((m1*b1)-(m1*oRingY[i])-(oRingX[i]));
					double c = Math.pow(b1, 2)+(Math.pow(oRingY[i], 2))-(2*b1*oRingY[i])+Math.pow(oRingX[i], 2)-Math.pow(140, 2);
					double temp = (-4*a*c)+Math.pow(b, 2);
					if(temp>=0)
					{
						double change = Math.sqrt(temp);
						double pointX1 = (-b+change)/(2*a);
						double pointX2 = (-b-change)/(2*a);
						if(x1 < pointX1 && pointX1 < x2)
						{
							double pointY = (m1 * pointX1)+ b1;
							if(!checkHitBackPass(pointX1, pointY))
							{
								hitBack = true;
							}
						}
						if(x1 < pointX2 && pointX2 < x2)
						{
							double pointY = (m1 * pointX2)+ b1;
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
	protected boolean checkObstructionsPointAll(float x1, float y1, float x2, float y2)
	{	
		boolean hitBack = false;
		float m1 = (y2-y1)/(x2-x1);
		float b1 = y1-(m1*x1);
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
				circM = -(1/m1);
				circB = oCircYAll[i]-(circM*oCircXAll[i]);
				tempX = (circB-b1)/(m1-circM);
				if(x1 < tempX && tempX < x2)
				{
					tempY = (circM * tempX)+ circB;
					if(Math.sqrt(Math.pow(tempX-oCircXAll[i],2)+Math.pow((tempY-oCircYAll[i])/oCircRatioAll[i],2))<oCircRadiusAll[i])
					{
						if(!checkHitBackPass(tempX, tempY))
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(x1>x2)
		{
			tempX = x1;
			x1 = x2;
			x2 = tempX;
		}
		if(y1>y2)
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
						tempY = (m1*oRectX1All[i])+b1;
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
							tempY = (m1*oRectX2All[i])+b1;
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
							tempX = (oRectY1All[i]-b1)/m1;
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
							tempX = (oRectY2All[i]-b1)/m1;
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
		return hitBack;
	}
	protected boolean checkObstructionsTall(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPointTall((float)x1, (float)y1, (float)x2, (float)y2);
	}
	protected boolean checkObstructionsAll(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPointAll((float)x1, (float)y1, (float)x2, (float)y2);
	}
	
	protected boolean checkHitBack(double X, double Y)
	{
		boolean hitBack = false;
		if(X < 0 || X > levelWidth || Y < 0 || Y > levelHeight)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < getCurrentRectangle(); i++)
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
			for(int i = 0; i < getCurrentCircle(); i++)
			{
				if(hitBack == false)
				{
					if(Math.pow(X - oCircX[i], 2) + Math.pow((Y - oCircY[i])/oCircRatio[i], 2) < Math.pow(oCircRadius[i], 2))
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
					if(dist < Math.pow(oRingOuter[i], 2)&&dist > Math.pow(oRingInner[i], 2))
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
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	protected double visualY(double y)
	{
		return ((y-screenMinY)/screenDimensionMultiplier);
	}
	protected boolean pointOnScreen(double x, double y)
	{
		x = visualX(x);
		y = visualY(y);
		if(x > 90 && x < 390 && y > 10 && y < 310)
        {
			return true;
        } else
        {
        	return false;
        }
	}
	protected boolean pointOnSquare(double x, double y, double lowX, double lowY, double highX, double highY)
	{
		x = visualX(x);
		y = visualY(y);
		if(x > lowX && x < highX && y > lowY && y < highY)
        {
			return true;
        } else
        {
        	return false;
        }
	}
	protected boolean pointOnCircle(double x, double y, double midX, double midY, double radius)
	{
		x = visualX(x);
		y = visualY(y);
		if(Math.sqrt(Math.pow(x-midX, 2) + Math.pow(y-midY, 2)) < radius)
        {
			return true;
        } else
        {
        	return false;
        }
	}
	protected int getDifficultyLevel() {
		return difficultyLevel;
	}
	protected double getDifficultyLevelMultiplier() {
		return difficultyLevelMultiplier;
	}
	protected int getRandomInt(int i) {
		return randomGenerator.nextInt(i);
	}
	protected double getRandomDouble() {
		return randomGenerator.nextDouble();
	}
	protected int getPlayerType() {
		return playerType;
	}
	protected double getPlayerX()
	{
		return player.getX();
	}
	protected double getPlayerY()
	{
		return player.getY();
	}
	protected int getLevelNum() {
		return levelNum;
	}
	protected int getORectX1(int i) {
		return oRectX1[i];
	}
	protected int getORectX2(int i) {
		return oRectX2[i];
	}
	protected int getORectY1(int i) {
		return oRectY1[i];
	}
	protected int getORectY2(int i) {
		return oRectY2[i];
	}
	protected int getOCircX(int i) {
		return oCircX[i];
	}
	protected int getOCircY(int i) {
		return oCircY[i];
	}
	protected int getOCircRadius(int i) {
		return oCircRadius[i];
	}
	protected int getCurrentCircle() {
		return currentCircle;
	}
	protected int getCurrentRectangle() {
		return currentRectangle;
	}
	protected int getCurrentCircleAll() {
		return currentCircleAll;
	}
	protected int getCurrentRectangleAll() {
		return currentRectangleAll;
	}
	protected int getCurrentRing() {
		return currentRing;
	}
	protected int getCurrentPassage() {
		return currentPassage;
	}
	protected boolean getGameEnded() {
		return gameEnded;
	}
	protected void incrementCurrentRectangle()
	{
		currentRectangle ++;
	}
	protected void incrementCurrentCircle()
	{
		currentCircle ++;
	}
	protected void incrementCurrentRectangleAll()
	{
		currentRectangleAll ++;
	}
	protected void incrementCurrentCircleAll()
	{
		currentCircleAll ++;
	}
	protected void incrementCurrentRing()
	{
		currentRing ++;
	}
	protected void incrementCurrentPassage()
	{
		currentPassage ++;
	}
	protected void setORectX1All(int i, int oRectX1) {
		this.oRectX1All[i] = oRectX1;
	}
	protected void setORectX2All(int i, int oRectX2) {
		this.oRectX2All[i] = oRectX2;
	}
	protected void setORectY1All(int i, int oRectY1) {
		this.oRectY1All[i] = oRectY1;
	}
	protected void setOPassageY2(int i, int oRectY2) {
		this.oPassageY2[i] = oRectY2;
	}
	protected void setOPassageX1(int i, int oRectX1) {
		this.oPassageX1[i] = oRectX1;
	}
	protected void setOPassageX2(int i, int oRectX2) {
		this.oPassageX2[i] = oRectX2;
	}
	protected void setOPassageY1(int i, int oRectY1) {
		this.oPassageY1[i] = oRectY1;
	}
	protected void setORectY2All(int i, int oRectY2) {
		this.oRectY2All[i] = oRectY2;
	}
	protected void setOCircXAll(int i, int oCircX) {
		this.oCircXAll[i] = oCircX;
	}
	protected void setOCircYAll(int i, int oCircY) {
		this.oCircYAll[i] = oCircY;
	}
	protected void setOCircRadiusAll(int i, int oCircRadius) {
		this.oCircRadiusAll[i] = oCircRadius;
	}
	protected void setOCircRatioAll(int i, double oCircRatio) {
		this.oCircRatioAll[i] = oCircRatio;
	}
	
	protected void setORingX(int i, int oCircX) {
		this.oRingX[i] = oCircX;
	}
	protected void setORingY(int i, int oCircY) {
		this.oRingY[i] = oCircY;
	}
	protected void setORingInner(int i, int oRingInner) {
		this.oRingInner[i] = oRingInner;
	}
	protected void setORingOuter(int i, int oRingOuter) {
		this.oRingOuter[i] = oRingOuter;
	}
	
	protected void setORectX1(int i, int oRectX1) {
		this.oRectX1[i] = oRectX1;
	}
	protected void setORectX2(int i, int oRectX2) {
		this.oRectX2[i] = oRectX2;
	}
	protected void setORectY1(int i, int oRectY1) {
		this.oRectY1[i] = oRectY1;
	}
	protected void setORectY2(int i, int oRectY2) {
		this.oRectY2[i] = oRectY2;
	}
	protected void setOCircX(int i, int oCircX) {
		this.oCircX[i] = oCircX;
	}
	protected void setOCircY(int i, int oCircY) {
		this.oCircY[i] = oCircY;
	}
	protected void setOCircRadius(int i, int oCircRadius) {
		this.oCircRadius[i] = oCircRadius;
	}
	protected void setOCircRatio(int i, double oCircRatio) {
		this.oCircRatio[i] = oCircRatio;
	}
}