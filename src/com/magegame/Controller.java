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
package com.magegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.Handler;
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
	protected int difficultyLevel = 10;
	private double difficultyLevelMultiplier;
	protected int playerType = 0;
	protected int levelNum = 10;
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
	protected PlayerGestureDetector detect;
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
	protected int moneyMultiplier = 0;
	protected int moneyMade = 0;
	private int savedEnemies = 0;
	private int[][] saveEnemyInformation = new int[30][5];
	public boolean hasKey = false;
	private int exitX = 0;
	private int exitY = 0;
	private int curXShift;
	private int curYShift;
	private int goldColor = Color.rgb(216, 200, 28);
	private int platinumColor = Color.rgb(196, 204, 204);
	protected DrawnSprite shootStick = new Graphic_shootStick();
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
		startFighting(10);
		frameCaller.run();
		check = imageLibrary.loadImage("menu_check", 20, 20);
		changePlayOptions();
		changePlayerType(activity.playerType);
	}
	protected void endFighting()
	{
		player.resetVariables();
		moneyMade = 0;
		hasKey = false;
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
		savedEnemies = 0;
		saveEnemyInformation = new int[30][5];
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
		difficultyLevelMultiplier = 15 / (double)(difficultyLevel + 5);
		activity.saveGame();
	}
	protected void changePlayOptions()
	{
		detect.getSide();
		if(activity.stickOnRight)
		{
			shootStick.x = 53;
			shootStick.y = 268;
		} else
		{
			shootStick.x = 426;
			shootStick.y = 268;
		}
		background = drawStart();
		invalidate();
		activity.saveGame();
	}
	protected void changePlayerType(int playerTypeSet)
	{
		playerType = playerTypeSet;
		player.humanType = playerType;
		imageLibrary.loadPlayerPowerBall();
		changePlayerType();
		background = drawStart();
		activity.saveGame();
	}
	protected void changePlayerType()
	{
		switch(playerType)
		{
		case 0:
			player.spChangeForType = (double)activity.wApollo / 10;
			shootStick.visualImage = imageLibrary.loadImage("shootfire", 65, 30);
			break;
		case 1:
			player.spChangeForType = (double)activity.wPoseidon / 10;
			shootStick.visualImage = imageLibrary.loadImage("shootwater", 65, 30);
			break;
		case 2:
			player.spChangeForType = (double)activity.wZues / 10;
			shootStick.visualImage = imageLibrary.loadImage("shootair", 65, 30);
			break;
		case 3:
			player.spChangeForType = (double)activity.wHades / 10;
			shootStick.visualImage = imageLibrary.loadImage("shootearth", 65, 30);
			break;
		}
	}
	/*
	 * Loads the level the user picked and initializes teleport and wall array variables
	 */
	protected Wall_Rectangle makeWall_Rectangle(int x, int y, int width, int height, boolean HitPlayer, boolean tall)
	{
		Wall_Rectangle wall1 = new Wall_Rectangle(this, x - 2, y - 2, width + 4, height + 4, HitPlayer, tall);
		return wall1;
	}
	protected Wall_Ring makeWall_Ring(int x, int y, int radIn, int radOut)
	{
		Wall_Ring wall1 = new Wall_Ring(this, x, y, radIn - 2, radOut + 2);
		return wall1;
	}
	protected void makeWall_Pass(int x, int y, int width, int height)
	{
		Wall_Pass wall1 = new Wall_Pass(this, x - 2, y - 2, width + 4, height + 4);
	}
	protected Wall_Circle makeWall_Circle(int x, int y, int rad, double ratio, boolean tall)
	{
		Wall_Circle wall1 = new Wall_Circle(this, x, y, rad + 2, ratio, tall);
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
		oCircRatio = new double[length];
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
		oCircRatioAll = new double[length];
	}
	protected void createWallRingValueArrays(int length)
	{
		oRingX = new int[length];
		oRingY = new int[length];
		oRingInner = new int[length];
		oRingOuter = new int[length];
	}
	protected void createWallPassageValueArrays(int length)
	{
		oPassageX1 = new int[length];
		oPassageX2 = new int[length];
		oPassageY1 = new int[length];
		oPassageY2 = new int[length];
	}
	protected void loadLevel()
	{
		if(levelNum == 10)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(16);
			createWallRectangleValueArraysAll(17);
			createWallCircleValueArraysAll(4);
			levelWidth = 500;
			levelHeight = 300;
			player.x = 150;
			player.y = 150;
			wallCircles[0] = makeWall_Circle(56, 189, 8, 1, false);
			wallCircles[1] = makeWall_Circle(58, 242, 8, 1, false);
			wallCircles[2] = makeWall_Circle(103, 273, 8, 1, false);
			wallCircles[3] = makeWall_Circle(162, 150, 5, 1, false);
			walls[0] = makeWall_Rectangle(-20, 0, 30, 40, true, true);
			walls[1] = makeWall_Rectangle(-20, 66, 30, 44, true, true);
			walls[2] = makeWall_Rectangle(-20, 137, 30, 300, true, true);
			walls[3] = makeWall_Rectangle(490, 0, 30, 40, true, true);
			walls[4] = makeWall_Rectangle(490, 66, 30, 44, true, true);
			walls[5] = makeWall_Rectangle(490, 137, 30, 44, true, true);
			walls[6] = makeWall_Rectangle(490, 212, 30, 44, true, true);
			walls[7] = makeWall_Rectangle(0, -20, 39, 30, true, true);
			walls[8] = makeWall_Rectangle(68, -20, 36, 30, true, true);
			walls[9] = makeWall_Rectangle(134, -20, 36, 30, true, true);
			walls[10] = makeWall_Rectangle(199, -20, 36, 30, true, true);
			walls[11] = makeWall_Rectangle(264, -20, 36, 30, true, true);
			walls[12] = makeWall_Rectangle(329, -20, 36, 30, true, true);
			walls[13] = makeWall_Rectangle(395, -20, 36, 30, true, true);
			walls[14] = makeWall_Rectangle(461, -20, 39, 30, true, true);
			walls[15] = makeWall_Rectangle(0, 290, 500, 10, true, true);			
			walls[16] = makeWall_Rectangle(8, 194, 36, 100, true, false);
		}
		if(levelNum == 20)
		{
			createWallRectangleValueArrays(6);
			createWallRectangleValueArraysAll(6);
			levelWidth = 515;
			levelHeight = 300;
			player.x = 100;
			player.y = 150;
			exitX = 16000;
			enemies[0] = new Enemy_Target(this, 282, 18, 90, false);
			enemies[1] = new Enemy_Target(this, 282, 284, -90, false);
			enemies[2] = new Enemy_Target(this, 344, 135, 180, false);
			enemies[3] = new Enemy_Target(this, 344, 165, 180, false);
			enemies[4] = new Enemy_Target(this, 423, 18, 90, true);
			enemies[5] = new Enemy_Target(this, 460, 18, 90, true);
			enemies[6] = new Enemy_Target(this, 404, 284, -90, true);
			enemies[7] = new Enemy_Target(this, 441, 284, -90, true);
			walls[0] = makeWall_Rectangle(210, -77, 15, 205, true, true);
			walls[1] = makeWall_Rectangle(210, 172, 15, 205, true, true);
			walls[2] = makeWall_Rectangle(355, -77, 15, 205, true, true);
			walls[3] = makeWall_Rectangle(355, 172, 15, 205, true, true);
			walls[4] = makeWall_Rectangle(503, -77, 15, 205, true, true);
			walls[5] = makeWall_Rectangle(503, 172, 15, 205, true, true);
		}
		if(levelNum == 30)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("archer", true);
			createWallRectangleValueArrays(5);
			createWallRectangleValueArraysAll(7);
			levelWidth = 400;
			levelHeight = 300;
			player.x = 376;
			player.y = 112;
			exitX = 27;
			exitY = 105;
			enemies[0] = new Enemy_Archer(this, 278, 35);
			enemies[1] = new Enemy_Swordsman(this, 215, 85);
			enemies[2] = new Enemy_Archer(this, 30, 192);
			enemies[3] = new Enemy_Swordsman(this, 50, 249);
			enemies[2].keyHolder = true;
			walls[0] = makeWall_Rectangle(-35, 8, 100, 70, true, true);
			walls[1] = makeWall_Rectangle(53, -9, 141, 153, true, true);
			walls[2] = makeWall_Rectangle(69, 125, 108, 59, true, true);
			
			walls[3] = makeWall_Rectangle(281, 67, 96, 1210, true, true);
			walls[4] = makeWall_Rectangle(270, 175, 1220, 1350, true, true);
			
			walls[5] = makeWall_Rectangle(86, 177, 13, 80, true, false);
			walls[6] = makeWall_Rectangle(86, 245, 56, 13, true, false);
		}
		if(levelNum == 40)
		{
			imageLibrary.changeArrayLoaded("archer", true);
			imageLibrary.changeArrayLoaded("pikeman", true);
			createWallRectangleValueArrays(9);
			createWallRectangleValueArraysAll(11);
			createWallRingValueArrays(2);
			createWallPassageValueArrays(3);
			levelWidth = 400;
			levelHeight = 400;
			player.x = 372;
			player.y = 338;
			exitX = 36;
			exitY = 111;
			enemies[0] = new Enemy_Pikeman(this, 208, 32);
			enemies[1] = new Enemy_Pikeman(this, 167, 96);
			enemies[2] = new Enemy_Pikeman(this, 181, 362);
			enemies[3] = new Enemy_Pikeman(this, 40, 251);
			enemies[4] = new Enemy_Archer(this, 341, 130);
			enemies[5] = new Enemy_Archer(this, 381, 104);
			enemies[6] = new Enemy_Mage(this, 200, 200);
			enemies[7] = new Enemy_Mage(this, 65, 200);
			enemies[7].keyHolder = true;
			makeWall_Pass(181, 131, 37, 137);
			makeWall_Pass(290, -21, 73, 70);
			makeWall_Pass(34, 347, 83, 79);
			wallRings[0] = makeWall_Ring(200, 25, 125, 145);
			wallRings[1] = makeWall_Ring(200, 375, 125, 145);
			walls[0] = makeWall_Rectangle(67, -52, 86, 86, true, false);
			walls[1] = makeWall_Rectangle(224, 320, 32, 196, true, false);
			walls[2] = makeWall_Rectangle(316, 49, 21, 19, true, true);
			walls[3] = makeWall_Rectangle(65, 326, 20, 20, true, true);
			
			walls[4] = makeWall_Rectangle(131, 144, 18, 110, true, true);
			walls[5] = makeWall_Rectangle(250, 145, 18, 108, true, true);
			walls[6] = makeWall_Rectangle(140, 144, 36, 19, true, true);
			walls[7] = makeWall_Rectangle(139, 236, 37, 19, true, true);
			walls[8] = makeWall_Rectangle(223, 144, 38, 19, true, true);
			walls[9] = makeWall_Rectangle(223, 234, 36, 19, true, true);
			walls[10] = makeWall_Rectangle(336, 248, 151, 24, true, true);
		}
		if(levelNum == 50)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("pikeman", true);
			createWallRectangleValueArrays(2);
			createWallRectangleValueArraysAll(8);
			createWallCircleValueArraysAll(1);
			levelWidth = 500;
			levelHeight = 300;
			player.x = 450;
			player.y = 250;
			exitX = 52;
			exitY = 150;
			enemies[0] = new Enemy_Pikeman(this, 22, 44);
			enemies[1] = new Enemy_Swordsman(this, 36, 202);
			enemies[2] = new Enemy_Pikeman(this, 192, 51);
			enemies[3] = new Enemy_Swordsman(this, 286, 48);
			enemies[4] = new Enemy_Mage(this, 460, 62);
			enemies[5] = new Enemy_Mage(this, 120, 48);
			enemies[5].keyHolder = true;
			walls[0] = makeWall_Rectangle(35, 58, 79, 31, true, false);
			walls[1] = makeWall_Rectangle(202, 59, 79, 31, true, false);
			walls[2] = makeWall_Rectangle(37, 217, 79, 31, true, false);
			walls[3] = makeWall_Rectangle(209, 220, 79, 31, true, false);
			walls[4] = makeWall_Rectangle(375, 14, 79, 31, true, false);
			walls[5] = makeWall_Rectangle(357, -13, 10, 207, true, true);
			walls[6] = makeWall_Rectangle(357, 225, 10, 85, true, true);
			walls[7] = makeWall_Rectangle(375, 185, 80, 10, true, false);
			wallCircles[0] = makeWall_Circle(180, 160, 15, 1, false);
		}
		if(levelNum == 60)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			imageLibrary.changeArrayLoaded("pikeman", true);
			imageLibrary.changeArrayLoaded("rogue", true);
			createWallRectangleValueArrays(17);
			createWallRectangleValueArraysAll(25);
			createWallCircleValueArraysAll(1);
			levelWidth = 450;
			levelHeight = 650;
			player.x = 452;
			player.y = 265;
			exitX = 400;
			exitY = 600;
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
			enemies[10].keyHolder = true;
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
		if(levelNum == 70)
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
			exitX = 200;
			exitY = 550;
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
			enemies[3].keyHolder = true;
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
		if(levelNum == 80)
		{
			imageLibrary.changeArrayLoaded("pikeman", true);
			imageLibrary.changeArrayLoaded("archer", true);
			createWallRectangleValueArrays(8);
			createWallRectangleValueArraysAll(8);
			createWallRingValueArrays(1);
			createWallPassageValueArrays(2);
			levelWidth = 400;
			levelHeight = 500;
			player.x = 379;
			player.y = 250;
			exitX = 200;
			exitY = 250;
			enemies[0] = new Enemy_Pikeman(this, 199, 61);
			enemies[1] = new Enemy_Pikeman(this, 187, 187);
			enemies[2] = new Enemy_Pikeman(this, 191, 327);
			enemies[3] = new Enemy_Pikeman(this, 191, 449);
			enemies[4] = new Enemy_Mage(this, 182, 257);
			enemies[5] = new Enemy_Archer(this, 111, 256);
			enemies[6] = new Enemy_Archer(this, 264, 240);
			enemies[4].keyHolder = true;
			makeWall_Pass(168, 73, 62, 98);
			makeWall_Pass(168, 358, 62, 98);
			wallRings[0] = makeWall_Ring(197, 253, 130, 150);
			walls[0] = makeWall_Rectangle(118, -152, 20, 282, true, true);
			walls[1] = makeWall_Rectangle(263, 61, 19, 71, true, true);
			walls[2] = makeWall_Rectangle(118, 375, 19, 283, true, true);
			walls[3] = makeWall_Rectangle(262, 375, 19, 68, true, true);
			walls[4] = makeWall_Rectangle(127, 109, 30, 19, true, true);
			walls[5] = makeWall_Rectangle(243, 109, 29, 19, true, true);
			walls[6] = makeWall_Rectangle(129, 378, 28, 19, true, true);
			walls[7] = makeWall_Rectangle(242, 378, 27, 19, true, true);
		}
		if(levelNum == 90)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(6);
			createWallRectangleValueArraysAll(6);
			levelWidth = 400;
			levelHeight = 410;
			player.x = 379;
			player.y = 250;
			exitX = -1000;
			exitY = -1000;
			enemies[0] = new Enemy_Swordsman(this, 379, 250);
			walls[0] = makeWall_Rectangle(43, -48, 10, 240, true, true);
			walls[1] = makeWall_Rectangle(43, 223, 10, 240, true, true);
			walls[2] = makeWall_Rectangle(346, -48, 10, 240, true, true);
			walls[3] = makeWall_Rectangle(346, 223, 10, 240, true, true);
			walls[4] = makeWall_Rectangle(88, 43, 10, 322, true, true);
			walls[5] = makeWall_Rectangle(302, 43, 10, 322, true, true);
			int[] toSave0 = {
				1, 50, 50, 0, 0
			};
			int[] toSave1 = {
				1, 50, 50, 0, 0
			};
			int[] toSave2 = {
				1, 50, 50, 0, 1
			};
			int[] toSave3 = {
				1, 50, 50, 0, 0
			};
			saveEnemyInformation[0] = toSave0;
			saveEnemyInformation[1] = toSave1;
			saveEnemyInformation[2] = toSave2;
			saveEnemyInformation[3] = toSave3;
			savedEnemies = 4;
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	protected void loadLevelSection(int level)
	{
		levelNum = level;
		if(levelNum>29)
		{
			int tempEnemies = savedEnemies;
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
			createWallRectangleValueArrays(6);
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
			exitY = 200;
			walls[0] = makeWall_Rectangle(43, 45, 10, 320, true, true);
			walls[1] = makeWall_Rectangle(346, 45, 10, 320, true, true);
			walls[2] = makeWall_Rectangle(0, 145, 45, 120, true, true);
			walls[3] = makeWall_Rectangle(355, 145, 45, 120, true, true);
		}
		if(levelNum == 21)
		{
			
			createWallRectangleValueArrays(7);
			createWallRectangleValueArraysAll(7);
			player.x = 25;
			enemies[0] = new Enemy_Target(this, 653-506, 125, 180, false);
			enemies[1] = new Enemy_Target(this, 653-506, 176, 180, false);
			enemies[2] = new Enemy_Target(this, 670-506, 150, 180, false);
			enemies[3] = new Enemy_Target(this, 1014-506, 150, 180, false);
			walls[0] = makeWall_Rectangle(503-506, -100, 15, 500, true, true);
			walls[1] = makeWall_Rectangle(665-506, -77, 15, 205, true, true);
			walls[2] = makeWall_Rectangle(665-506, 172, 15, 205, true, true);
			walls[3] = makeWall_Rectangle(825-506, -77, 15, 205, true, true);
			walls[4] = makeWall_Rectangle(825-506, 172, 15, 205, true, true);
			walls[5] = makeWall_Rectangle(1014-506, -77, 15, 205, true, true);
			walls[6] = makeWall_Rectangle(1014-506, 172, 15, 205, true, true);
		}
		if(levelNum == 22)
		{
			createWallRectangleValueArrays(5);
			createWallRectangleValueArraysAll(5);
			levelWidth = 632;
			player.x = 25;
			exitX = 582;
			exitY = 150;
			enemies[0] = new Enemy_Target(this, 1204-1017, 150, 180, false);
			enemies[1] = new Enemy_Target(this, 1400-1017, 150, 180, false);
			enemies[2] = new Enemy_Target(this, 1522-1017, 150, 180, false);
			enemies[2].keyHolder=true;
			walls[0] = makeWall_Rectangle(1014-1017, -100, 15, 500, true, true);
			walls[1] = makeWall_Rectangle(1198-1017, -77, 15, 205, true, true);
			walls[2] = makeWall_Rectangle(1198-1017, 172, 15, 205, true, true);
			walls[3] = makeWall_Rectangle(1393-1017, -77, 15, 205, true, true);
			walls[4] = makeWall_Rectangle(1393-1017, 172, 15, 205, true, true);
		}
		imageLibrary.loadLevel(levelNum, levelWidth, levelHeight);
	}
	private void createEnemy(int[] info, int index)
	{
		switch(info[0])
		{
		case 1:
			enemies[index] = new Enemy_Swordsman(this, info[1], info[2]);
			break;
		case 2:
			enemies[index] = new Enemy_Pikeman(this, info[1], info[2]);
			break;
		case 3:
			enemies[index] = new Enemy_Axeman(this, info[1], info[2]);
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
		if(info[3] != 0)
		{
			enemies[index].hp = info[3];
		}
		if(info[4] == 1)
		{
			enemies[index].keyHolder = true;
		}
	}
	private void endFightSection(int[][] enemyData, int tempEnemies)
	{
		enemies = new Enemy[30];
		powerBalls = new PowerBall[30];
		powerBallAOEs = new PowerBallAOE[30];
		powerUps = new PowerUp[30];
		graphic_Teleport = new Graphic_Teleport[30];
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
		for(int i = 0; i < tempEnemies; i++)
		{
			createEnemy(enemyData[i], i);
		}
	}
	private void endFightSection()
	{
		enemies = new Enemy[30];
		powerBalls = new PowerBall[30];
		powerBallAOEs = new PowerBallAOE[30];
		powerUps = new PowerUp[30];
		graphic_Teleport = new Graphic_Teleport[30];
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
	protected int fixXBoundsHpBar(int minX, int maxX)
	{
		int offset = 0;
		if(minX < 90)
		{
			offset = 90 - minX;
		}
		else if(maxX > 390)
		{
			offset = 390 - maxX;
		}
		return offset;
	}
	protected int fixYBoundsHpBar(int minY, int maxY)
	{
		int offset = 0;
		if(minY < 10)
		{
			offset = 10 - minY;
		}
		else if(maxY > 310)
		{
			offset = 310 - maxY;
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
				if(!enemies[i].rogue || enemies[i].currentFrame != 49)
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
		drawRect(400 - fix, 148, 400 - fix + (70 * player.getHp() / (int)(700 * activity.wHephaestus)), 164, g);
		paint.setColor(Color.GREEN);
		drawRect(400 - fix, 192, 400 - fix + (int)(70 * player.getSp()), 208, g);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect(400 - fix, 148, 470 - fix, 164, g);
		drawRect(400 - fix, 192, 470 - fix, 208, g);
		paint.setTextSize(12);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(Color.WHITE);
		drawText(Integer.toString(player.getHp()), 435 - fix, 160, g);
		drawText(Integer.toString((int)(3500 * player.getSp())), 435 - fix, 204, g);
		//drawText(Integer.toString(activity.gameCurrency), 435 - fix, 205, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.YELLOW);
		drawRect(12 + fix, 68, 12 + fix + (int)((66 * player.getAbilityTimer_burst()) / 500), 78, g);
		drawRect(12 + fix, 200, 12 + fix + (int)((66 * player.getAbilityTimer_roll()) / 120), 210, g);
		drawRect(12 + fix, 300, 12 + fix + (int)((66 * player.getAbilityTimer_powerBall()) / 90), 310, g);
		//drawRect(12 + fix, 134, 12 + fix + (int)((66 * player.getAbilityTimer_teleport()) / 350), 144, g);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawRect(12 + fix, 68, 78 + fix, 78, g);
		drawRect(12 + fix, 300, 78 + fix, 310, g);
		drawRect(12 + fix, 200, 78 + fix, 210, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GRAY);
		paint.setAlpha(151);
		if(player.teleporting || player.rollTimer > 0)
		{
			drawRect(12 + fix, 33, 78 + fix, 99, g);
			drawRect(12 + fix, 118, 78 + fix, 184, g);
			//drawRect(12 + fix, 78, 78 + fix, 144, g);
		}
		else
		{
			if(player.getAbilityTimer_burst() < 400)
			{
				drawRect(12 + fix, 33, 78 + fix, 99, g);
			}
			if(player.getAbilityTimer_roll() < 50)
			{
				drawRect(12 + fix, 118, 78 + fix, 184, g);
			}
			if(player.getAbilityTimer_teleport() < 250)
			{
				//drawRect(12 + fix, 78, 78 + fix, 144, g);
			}
		}
		drawBitmapRotated(shootStick, g);
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
				} else
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
					enemies[i].levelCurrentPosition = 0;
					enemies[i].pathedToHitLength = 0;
					if(enemyInView(enemies[i].x, enemies[i].y))
					{
						enemies[i].frameCall();
						if(enemies[i] != null)
						{
							if(enemies[i].getX() < 10) enemies[i].setX(10);
							if(enemies[i].getX() > levelWidth - 10) enemies[i].setX(levelWidth - 10);
							if(enemies[i].getY() < 10) enemies[i].setY(10);
							if(enemies[i].getY() > levelHeight - 10) enemies[i].setY(levelHeight - 10);
						}
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
			if(!player.isDeleted())
			{
				player.frameCall();
				if(!player.isTeleporting())
				{
					if(player.getX() < 10) player.setX(10);
					if(player.getX() > levelWidth - 10) player.setX(levelWidth - 10);
					if(player.getY() < 10) player.setY(10);
					if(player.getY() > levelHeight - 10) player.setY(levelHeight - 10);
				}
			}
		}
		if(activity.gameRunning)
		{
			if(levelNum == 10)
			{
				if(imageLibrary.directionsTutorial == null && distSquared(player.getX(), player.getY(), 162, 150) < 400)
				{
					imageLibrary.directionsTutorial = imageLibrary.loadImage("menu_directions", 200, 180);
				}
				else if(imageLibrary.directionsTutorial != null && distSquared(player.getX(), player.getY(), 162, 150) > 400)
				{
					imageLibrary.directionsTutorial.recycle();
					imageLibrary.directionsTutorial = null;
				}
				if(player.x < 15 && player.y > 113 && player.y < 134)
				{
					gamePaused = true;
					currentPause = "startfight";
					startingLevel = 0;
					currentTutorial = 1;
					invalidate();
					player.x += 15;
				}
				else if(player.x < 15 && player.y > 41 && player.y < 64)
				{
					if(activity.levelBeaten >= 1)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 1;
						invalidate();
						player.x += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 42 && player.x < 66)
				{
					if(activity.levelBeaten >= 2)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 2;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 107 && player.x < 131)
				{
					if(activity.levelBeaten >= 3)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 3;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 173 && player.x < 197)
				{
					if(activity.levelBeaten >= 4)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 4;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 237 && player.x < 260)
				{
					if(activity.levelBeaten >= 5)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 5;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 304 && player.x < 326)
				{
					if(activity.levelBeaten >= 6)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 6;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 369 && player.x < 392)
				{
					if(activity.levelBeaten >= 7)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 7;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.y < 15 && player.x > 433 && player.x < 457)
				{
					if(activity.levelBeaten >= 8)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 8;
						invalidate();
						player.y += 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.x > 485 && player.y > 42 && player.y < 62)
				{
					if(activity.levelBeaten >= 9)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 9;
						invalidate();
						player.x -= 15;
					}
					else
					{
						levelLocked();
					}
				}
				else if(player.x > 485 && player.y > 113 && player.y < 134)
				{
					if(activity.levelBeaten >= 10)
					{
						gamePaused = true;
						currentPause = "startfight";
						startingLevel = 10;
						invalidate();
						player.x -= 15;
					}
					else
					{
						levelLocked();
					}
				}
			}
			if(levelNum == 20||levelNum == 21)
			{
				if(player.x > 504 && player.y > 140 && player.y < 160)
				{
					loadLevelSection(levelNum+1);
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
			for(int i = 0; i < walls.length; i++)
			{
				if(walls[i] != null)
				{
					if(inView(walls[i].oRX1, walls[i].oRY1, walls[i].oRX2-walls[i].oRX1, walls[i].oRY2-walls[i].oRY1))
					{
						walls[i].frameCall();
					}
				}
			}
			for(int i = 0; i < wallCircles.length; i++)
			{
				if(wallCircles[i] != null)
				{
					int width = wallCircles[i].oCR;
					if(inView((int)wallCircles[i].oCX-(width/2), (int)wallCircles[i].oCY-(width/2), width, width))
					{
						wallCircles[i].frameCall();
					}
				}
			}
			for(int i = 0; i < wallRings.length; i++)
			{
				if(wallRings[i] != null)
				{
					int width = wallRings[i].oCROut;
					if(inView((int)wallRings[i].oCX-(width/2), (int)wallRings[i].oCY-(width/2), width, width))
					{
						wallRings[i].frameCall();
					}
				}
			}
			invalidate();
		}
	}
	protected double distSquared(double x1, double y1, double x2, double y2)
	{
		return Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
	}
	protected Bitmap drawStart()
	{
		paint.setAlpha(255);
		Bitmap drawTo = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		if(activity.stickOnRight)
		{
			drawBitmap(imageLibrary.loadImage("screen000"+Integer.toString(playerType+5), 480, 320), 0, 0, g);
		}
		else
		{
			drawBitmap(imageLibrary.loadImage("screen000"+Integer.toString(playerType+1), 480, 320), 0, 0, g);
		}
		return drawTo;
	}
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
				drawBitmapLevel(imageLibrary.toTile, w, h, g);
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
		if(levelNum == 10)
		{
			if(imageLibrary.directionsTutorial!= null) drawBitmapLevel(imageLibrary.directionsTutorial, 45, 65, g);
			if(activity.levelBeaten < 1) drawBitmapLevel(imageLibrary.levelLocked, 12, 33, g);
			if(activity.levelBeaten < 2) drawBitmapLevel(imageLibrary.levelLocked, 39, 10, g);
			if(activity.levelBeaten < 3) drawBitmapLevel(imageLibrary.levelLocked, 104, 10, g);
			if(activity.levelBeaten < 4) drawBitmapLevel(imageLibrary.levelLocked, 170, 10, g);
			if(activity.levelBeaten < 5) drawBitmapLevel(imageLibrary.levelLocked, 235, 10, g);
			if(activity.levelBeaten < 6) drawBitmapLevel(imageLibrary.levelLocked, 300, 10, g);
			if(activity.levelBeaten < 7) drawBitmapLevel(imageLibrary.levelLocked, 365, 10, g);
			if(activity.levelBeaten < 8) drawBitmapLevel(imageLibrary.levelLocked, 431, 10, g);
			if(activity.levelBeaten < 9) drawBitmapLevel(imageLibrary.levelLocked, 459, 33, g);
			if(activity.levelBeaten < 10) drawBitmapLevel(imageLibrary.levelLocked, 459, 103, g);
			paint.setAlpha(255);
		}
		if(player != null)
		{
			drawBitmapRotatedLevel(player, g);
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
		for(int i = 0; i < powerBalls.length; i++)
		{
			if(powerBalls[i] != null)
			{
				drawBitmapRotatedLevel(powerBalls[i], g);
			}
		}
		for(int i = 0; i < powerBallAOEs.length; i++)
		{
			if(powerBallAOEs[i] != null)
			{
				aoeRect.top = (int)(powerBallAOEs[i].getY() - (powerBallAOEs[i].getHeight() / 2.5));
				aoeRect.bottom = (int)(powerBallAOEs[i].getY() + (powerBallAOEs[i].getHeight() / 2.5));
				aoeRect.left = (int)(powerBallAOEs[i].getX() - (powerBallAOEs[i].getWidth() / 2.5));
				aoeRect.right = (int)(powerBallAOEs[i].getX() + (powerBallAOEs[i].getWidth() / 2.5));
				paint.setAlpha(powerBallAOEs[i].getAlpha());
				drawBitmapRectLevel(powerBallAOEs[i].getVisualImage(), aoeRect, g);
			}
		}
		paint.setAlpha(255);
		for(int i = 0; i < graphic_Teleport.length; i++)
		{
			if(graphic_Teleport[i] != null)
			{
				drawBitmapRotatedLevel(graphic_Teleport[i], g);
			}
		}
		for(int i = 0; i < powerUps.length; i++)
		{
			if(powerUps[i] != null)
			{
				drawBitmapLevel(powerUps[i].getVisualImage(), (int) powerUps[i].getX() - 15, (int) powerUps[i].getY() - 15, g);
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
	private boolean inView(int lowx, int lowy, int width, int height)
	{
		lowx += curXShift;
		int highx = lowx + width;
		lowy += curYShift;
		int highy = lowy + height;
		return !(lowx > 300 || highx < 0 || lowy > 300 || highy < 0);
	}
	protected boolean enemyInView(double x, double y)
	{
		return !(x+curXShift > 380 || x+curXShift < -80 || y+curYShift > 380 || y+curYShift < -80);
	}
	/*
	 * Draws objects and calls other drawing functions (background and stats)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
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
			drawRect(30, 104, 130, 144, g);
		}
		if(difficultyLevel == 6)
		{
			moneyMultiplier = 7;
			drawRect(130, 29, 230, 69, g);
		}
		if(difficultyLevel == 3)
		{
			moneyMultiplier = 12;
			drawRect(250, 29, 350, 69, g);
		}
		if(difficultyLevel == 0)
		{
			moneyMultiplier = 20;
			drawRect(350, 104, 450, 144, g);
		}
		moneyMultiplier = (int)((double)moneyMultiplier*getLevelWinningsMultiplier(startingLevel));
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
		paint.setAlpha(255);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(40);
		drawText("Level " + Integer.toString(startingLevel), 240, 113, g);
		paint.setTextSize(25);
		drawText("Gold Multiplier: " + Integer.toString(moneyMultiplier), 240, 153, g);
	}
	protected double getLevelWinningsMultiplier(int level)
	{
		switch(level)
		{
		case 0:
			return 0;
		case 1:
			return 1.05;
		case 2:
			return 1.1;
		case 3:
			return 1.25;
		case 4:
			return 1.6;
		case 5:
			return 1.65;
		case 6:
			return 1.5;
		case 7:
			return 1.65;
		default:
			return 1;
		}
	}
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
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 30, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 30, g);
		
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.buy("Ambrosia", 9999999, false)), 147, 147, g);
		drawText(Integer.toString(activity.buy("Cooldown", 9999999, false)), 357, 147, g);
		drawText(Integer.toString(activity.buy("Apollo's Flame", 9999999, false)), 147, 220, g);
		drawText(Integer.toString(activity.buy("Hades' Helm", 9999999, false)), 357, 220, g);
		drawText(Integer.toString(activity.buy("Zues's Armor", 9999999, false)), 147, 294, g);
		drawText(Integer.toString(activity.buy("Posiedon's Shell", 9999999, false)), 357, 294, g);
		
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
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 30, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 30, g);
		
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.buy("Worship Apollo", 9999999, false)), 98, 147, g);
		drawText(Integer.toString(activity.buy("Worship Posiedon", 9999999, false)), 248, 147, g);
		drawText(Integer.toString(activity.buy("Worship Zues", 9999999, false)), 398, 147, g);
		drawText(Integer.toString(activity.buy("Worship Hades", 9999999, false)), 98, 220, g);
		drawText(Integer.toString(activity.buy("Worship Ares", 9999999, false)), 248, 220, g);
		drawText(Integer.toString(activity.buy("Worship Athena", 9999999, false)), 398, 220, g);
		drawText(Integer.toString(activity.buy("Worship Hermes", 9999999, false)), 98, 294, g);
		drawText(Integer.toString(activity.buy("Worship Hephaestus", 9999999, false)), 248, 294, g);
		drawText(Integer.toString(activity.buy("Worship Hera", 9999999, false)), 398, 294, g);
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
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		String[] describe = activity.getItemDescribe(buyingItem);
		drawText(describe[0], 75, 173, g);
		drawText(describe[1], 75, 203, g);
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 30, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 30, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(30);
		paint.setColor(goldColor);
		paint.setTextAlign(Align.CENTER);
		drawText(Integer.toString(activity.buy(buyingItem, 9999999, false)), 260, 272, g);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyReal(buyingItem))
		{
			drawRect(150, 240, 330, 282, g);
		}
	}
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
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 30, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 30, g);
		
		paint.setTextSize(25);
		paint.setTextAlign(Align.CENTER);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.buy("Worship Apollo", 9999999, false)), 98, 147, g);
		drawText(Integer.toString(activity.buy("Worship Posiedon", 9999999, false)), 248, 147, g);
		drawText(Integer.toString(activity.buy("Worship Zues", 9999999, false)), 398, 147, g);
		drawText(Integer.toString(activity.buy("Worship Hades", 9999999, false)), 98, 220, g);
		drawText(Integer.toString(activity.buy("Worship Ares", 9999999, false)), 248, 220, g);
		drawText(Integer.toString(activity.buy("Worship Athena", 9999999, false)), 398, 220, g);
		drawText(Integer.toString(activity.buy("Worship Hermes", 9999999, false)), 98, 294, g);
		drawText(Integer.toString(activity.buy("Worship Hephaestus", 9999999, false)), 248, 294, g);
		drawText(Integer.toString(activity.buy("Worship Hera", 9999999, false)), 398, 294, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyReal("Worship Apollo")) drawRect(30, 119, 150, 154, g);
		if(!activity.canBuyReal("Worship Posiedon")) drawRect(180, 119, 300, 154, g);
		if(!activity.canBuyReal("Worship Zues")) drawRect(330, 119, 450, 154, g);
		if(!activity.canBuyReal("Worship Hades")) drawRect(30, 194, 150, 229, g);
		if(!activity.canBuyReal("Worship Ares")) drawRect(180, 194, 300, 229, g);
		if(!activity.canBuyReal("Worship Athena")) drawRect(330, 194, 450, 229, g);
		if(!activity.canBuyReal("Worship Hermes")) drawRect(30, 268, 150, 303, g);
		if(!activity.canBuyReal("Worship Hephaestus")) drawRect(180, 268, 300, 303, g);
		if(!activity.canBuyReal("Worship Hera")) drawRect(330, 268, 450, 303, g);
	}
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
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		String[] describe = activity.getItemDescribe(buyingItem);
		drawText(describe[0], 75, 173, g);
		drawText(describe[1], 75, 203, g);
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 30, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 30, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setTextSize(30);
		paint.setColor(goldColor);
		paint.setTextAlign(Align.CENTER);
		drawText(Integer.toString(activity.buy(buyingItem, 9999999, false)), 260, 272, g);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(!activity.canBuyGame(buyingItem))
		{
			drawRect(150, 240, 330, 282, g);
		}
	}
	protected void drawChooseGod(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_choosegod", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setAlpha(120);
		if(playerType == 2)
		{
			drawRect(16, 192, 116, 292, g);
		}
		if(playerType == 1)
		{
			drawRect(132, 192, 232, 292, g);
		}
		if(playerType == 3)
		{
			drawRect(248, 192, 348, 292, g);
		}
		if(playerType == 0)
		{
			drawRect(364, 192, 464, 292, g);
		}
		paint.setAlpha(255);
	}
	protected void drawPaused(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_paused", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.BLACK);
		paint.setTextSize(40);
		paint.setTextAlign(Align.LEFT);
		drawText(Integer.toString(activity.pHeal), 48, 73, g);
		drawText(Integer.toString(activity.pCool), 148, 73, g);
		drawText(Integer.toString(activity.pWater), 48, 173, g);
		drawText(Integer.toString(activity.pEarth), 148, 173, g);
		drawText(Integer.toString(activity.pAir), 48, 273, g);
		drawText(Integer.toString(activity.pFire), 148, 273, g);
		paint.setAlpha(151);
		if(activity.pHeal == 0)
		{
			drawCircle(60, 60, 37, g);
		}
		if(activity.pCool == 0)
		{
			drawCircle(160, 60, 37, g);
		}
		if(activity.pWater == 0 || playerType == 1)
		{
			drawCircle(60, 160, 37, g);
		}
		if(activity.pEarth == 0 || playerType == 3)
		{
			drawCircle(160, 160, 37, g);
		}
		if(activity.pAir == 0 || playerType == 2)
		{
			drawCircle(60, 260, 37, g);
		}
		if(activity.pFire == 0 || playerType == 0)
		{
			drawCircle(160, 260, 37, g);
		}
	}
	protected void drawBehindPause(Canvas g)
	{
		drawNotPaused(g);
		if(!activity.stickOnRight)
		{
			drawBitmap(imageLibrary.loadImage("menu_pauseback0001", 480, 320), 0, 0, g);
		}
		else
		{
			drawBitmap(imageLibrary.loadImage("menu_pauseback0002", 480, 320), 0, 0, g);
		}
	}
	protected void drawOptions(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_options", 480, 320), 0, 0, g);
		paint.setColor(Color.BLACK);
		paint.setAlpha(127);
		drawCircle(264 + (int)(Math.pow(activity.volumeMusic * 16129, 0.3333333333333333333333)), 103, 15, g);
		drawCircle(264 + (int)(Math.pow(activity.volumeEffect * 16129, 0.3333333333333333333333)), 130, 15, g);
		float systemVolume = activity.audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		systemVolume = (float)(systemVolume * activity.volumeMusic / 127);
		activity.backMusic.setVolume(systemVolume, systemVolume);
		if(activity.stickOnRight)
		{
			drawBitmap(check, 227, 147, g);
		}
		else
		{
			drawBitmap(check, 338, 147, g);
		}
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
	}
	protected void drawLost(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_lost", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		paint.setColor(Color.BLACK);
		drawText(Integer.toString(activity.gameCurrency) + " (+" + Integer.toString(moneyMade) + ")", 153, 146, g);
		drawText(Integer.toString(activity.realCurrency), 153, 193, g);
	}
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
		paint.setColor(platinumColor);
		drawText(Integer.toString(activity.realCurrency), 185, 30, g);
		paint.setColor(goldColor);
		drawText(Integer.toString(activity.gameCurrency), 320, 30, g);
	}
	protected void drawWon(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_won", 480, 320), 0, 0, g);
		paint.setTextSize(20);
		paint.setTextAlign(Align.LEFT);
		paint.setColor(Color.BLACK);
		drawText(Integer.toString(activity.gameCurrency) + " (+" + Integer.toString(moneyMade) + ")", 153, 146, g);
		drawText(Integer.toString(activity.realCurrency), 153, 193, g);
	}@
	Override
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
				}
				else if(currentPause.equals("options"))
				{
					drawOptions(g);
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
				else if(currentPause.equals("chooseGod"))
				{
					drawChooseGod(g);
				} else if(currentPause.equals("worship"))
				{
					drawWorship(g);
				} else if(currentPause.equals("blessing"))
				{
					drawBlessing(g);
				} else if(currentPause.equals("buycash"))
				{
					drawBuyCash(g);
				} else if(currentPause.equals("buyitemcash"))
				{
					drawBuyItemCash(g);
				} else if(currentPause.equals("buypremium"))
				{
					drawBuyPremium(g);
				}
			}
			else
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
		if(player.powerUpTimer > 0)
		{
			if(activity.stickOnRight)
			{
				drawBitmap(imageLibrary.powerUpBigs[player.powerID - 1], 410, 25, g);
			}
			else
			{
				drawBitmap(imageLibrary.powerUpBigs[player.powerID - 1], 20, 25, g);
			}
		}
		if(hasKey)
		{
			if(activity.stickOnRight)
			{
				drawBitmap(imageLibrary.powerUpBigs[4], 410, 25, g);
			}
			else
			{
				drawBitmap(imageLibrary.powerUpBigs[4], 20, 25, g);
			}
		}
		if(levelNum == 10 || levelNum == 11)
		{
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			paint.setTextAlign(Align.CENTER);
			paint.setAlpha(180);
			if(!activity.stickOnRight)
			{
				drawRect(402, 12, 468, 210, g);
				paint.setAlpha(255);
				drawBitmap(imageLibrary.coins[0], 420, 30, g);
				drawBitmap(imageLibrary.coins[1], 420, 130, g);
				paint.setTextSize(20);
				paint.setColor(platinumColor);
				drawText(Integer.toString(activity.realCurrency), 435, 90, g);
				paint.setColor(goldColor);
				drawText(Integer.toString(activity.gameCurrency), 435, 190, g);
			}
			else
			{
				drawRect(12, 12, 78, 210, g);
				paint.setAlpha(255);
				drawBitmap(imageLibrary.coins[0], 30, 30, g);
				drawBitmap(imageLibrary.coins[1], 30, 130, g);
				paint.setTextSize(20);
				paint.setColor(platinumColor);
				drawText(Integer.toString(activity.realCurrency), 45, 70, g);
				paint.setColor(goldColor);
				drawText(Integer.toString(activity.gameCurrency), 45, 170, g);
			}
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
		PowerUp powerUp = new PowerUp(this, X, Y, 0);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	protected void createCoin1(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 7);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	protected void createCoin5(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 9);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	protected void createCoin20(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 10);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	protected void createKey(double X, double Y)
	{
		PowerUp powerUp = new PowerUp(this, X, Y, 8);
		powerUps[lowestPositionEmpty(powerUps)] = powerUp;
	}
	protected void createPowerBallPlayer(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Player ballPlayer = new PowerBall_Player(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballPlayer;
	}
	protected void createPowerBallEnemyAOE(double x, double y, double power, boolean damaging)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power, true);
		if(!damaging) ballAOEEnemy.damaging = false;
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
	protected double getDistance(double x1, double y1, double x2, int y2)
	{
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	protected boolean checkObstructionsTall(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPointTall((float) x1, (float) y1, (float) x2, (float) y2);
	}
	protected boolean checkObstructionsAll(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPointAll((float) x1, (float) y1, (float) x2, (float) y2);
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
	protected double visualY(double y)
	{
		return((y - screenMinY) / screenDimensionMultiplier);
	}
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
	protected int getDifficultyLevel()
	{
		return difficultyLevel;
	}
	protected double getDifficultyLevelMultiplier()
	{
		return difficultyLevelMultiplier;
	}
	protected int getRandomInt(int i)
	{
		return randomGenerator.nextInt(i);
	}
	protected double getRandomDouble()
	{
		return randomGenerator.nextDouble();
	}
	protected int getPlayerType()
	{
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
	protected int getLevelNum()
	{
		return levelNum;
	}
	protected int getORectX1(int i)
	{
		return oRectX1[i];
	}
	protected int getORectX2(int i)
	{
		return oRectX2[i];
	}
	protected int getORectY1(int i)
	{
		return oRectY1[i];
	}
	protected int getORectY2(int i)
	{
		return oRectY2[i];
	}
	protected int getOCircX(int i)
	{
		return oCircX[i];
	}
	protected int getOCircY(int i)
	{
		return oCircY[i];
	}
	protected int getOCircRadius(int i)
	{
		return oCircRadius[i];
	}
	protected int getCurrentCircle()
	{
		return currentCircle;
	}
	protected int getCurrentRectangle()
	{
		return currentRectangle;
	}
	protected int getCurrentCircleAll()
	{
		return currentCircleAll;
	}
	protected int getCurrentRectangleAll()
	{
		return currentRectangleAll;
	}
	protected int getCurrentRing()
	{
		return currentRing;
	}
	protected int getCurrentPassage()
	{
		return currentPassage;
	}
	protected boolean getGameEnded()
	{
		return gameEnded;
	}
	protected void incrementCurrentRectangle()
	{
		currentRectangle++;
	}
	protected void incrementCurrentCircle()
	{
		currentCircle++;
	}
	protected void incrementCurrentRectangleAll()
	{
		currentRectangleAll++;
	}
	protected void incrementCurrentCircleAll()
	{
		currentCircleAll++;
	}
	protected void incrementCurrentRing()
	{
		currentRing++;
	}
	protected void incrementCurrentPassage()
	{
		currentPassage++;
	}
	protected void setORectX1All(int i, int oRectX1)
	{
		this.oRectX1All[i] = oRectX1;
	}
	protected void setORectX2All(int i, int oRectX2)
	{
		this.oRectX2All[i] = oRectX2;
	}
	protected void setORectY1All(int i, int oRectY1)
	{
		this.oRectY1All[i] = oRectY1;
	}
	protected void setOPassageY2(int i, int oRectY2)
	{
		this.oPassageY2[i] = oRectY2;
	}
	protected void setOPassageX1(int i, int oRectX1)
	{
		this.oPassageX1[i] = oRectX1;
	}
	protected void setOPassageX2(int i, int oRectX2)
	{
		this.oPassageX2[i] = oRectX2;
	}
	protected void setOPassageY1(int i, int oRectY1)
	{
		this.oPassageY1[i] = oRectY1;
	}
	protected void setORectY2All(int i, int oRectY2)
	{
		this.oRectY2All[i] = oRectY2;
	}
	protected void setOCircXAll(int i, int oCircX)
	{
		this.oCircXAll[i] = oCircX;
	}
	protected void setOCircYAll(int i, int oCircY)
	{
		this.oCircYAll[i] = oCircY;
	}
	protected void setOCircRadiusAll(int i, int oCircRadius)
	{
		this.oCircRadiusAll[i] = oCircRadius;
	}
	protected void setOCircRatioAll(int i, double oCircRatio)
	{
		this.oCircRatioAll[i] = oCircRatio;
	}
	protected void setORingX(int i, int oCircX)
	{
		this.oRingX[i] = oCircX;
	}
	protected void setORingY(int i, int oCircY)
	{
		this.oRingY[i] = oCircY;
	}
	protected void setORingInner(int i, int oRingInner)
	{
		this.oRingInner[i] = oRingInner;
	}
	protected void setORingOuter(int i, int oRingOuter)
	{
		this.oRingOuter[i] = oRingOuter;
	}
	protected void setORectX1(int i, int oRectX1)
	{
		this.oRectX1[i] = oRectX1;
	}
	protected void setORectX2(int i, int oRectX2)
	{
		this.oRectX2[i] = oRectX2;
	}
	protected void setORectY1(int i, int oRectY1)
	{
		this.oRectY1[i] = oRectY1;
	}
	protected void setORectY2(int i, int oRectY2)
	{
		this.oRectY2[i] = oRectY2;
	}
	protected void setOCircX(int i, int oCircX)
	{
		this.oCircX[i] = oCircX;
	}
	protected void setOCircY(int i, int oCircY)
	{
		this.oCircY[i] = oCircY;
	}
	protected void setOCircRadius(int i, int oCircRadius)
	{
		this.oCircRadius[i] = oCircRadius;
	}
	protected void setOCircRatio(int i, double oCircRatio)
	{
		this.oCircRatio[i] = oCircRatio;
	}
	/*
	 * Replaces canvas.drawRect(int, int, int, int, Paint) and auto scales
	 */
	protected void drawRect(int x, int y, int x2, int y2, Canvas g)
	{
		g.drawRect(x, y, x2, y2, paint);
	}
	/*
	 * Replaces canvas.drawCircle(int, int, int paint) and auto scales
	 */
	protected void drawCircle(int x, int y, int radius, Canvas g)
	{
		g.drawCircle(x, y, radius, paint);
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, int, int, paint) and auto scales
	 */
	protected void drawBitmap(Bitmap picture, int x, int y, Canvas g)
	{
		g.drawBitmap(picture, x, y, paint);
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawBitmapRotated(DrawnSprite sprite, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-sprite.getVisualImage().getWidth() / 2, -sprite.getVisualImage().getHeight() / 2);
		rotateImages.postRotate((float) sprite.getRotation());
		rotateImages.postTranslate((float) sprite.getX(), (float) sprite.getY());
		g.drawBitmap(sprite.getVisualImage(), rotateImages, paint);
		sprite = null;
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	protected void drawBitmapRect(Bitmap picture, Rect rectangle, Canvas g)
	{
		g.drawBitmap(picture, null, rectangle, paint);
	}
	protected void drawBitmapLevel(Bitmap picture, int x, int y, Canvas g)
	{
		if(inView(x, y, picture.getWidth(), picture.getHeight())) g.drawBitmap(picture, x, y, paint);
	}
	/*
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
			rotateImages.postRotate((float) sprite.getRotation());
			rotateImages.postTranslate((float) sprite.getX(), (float) sprite.getY());
			g.drawBitmap(sprite.getVisualImage(), rotateImages, paint);
			sprite = null;
		}
	}
	/*
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	protected void drawBitmapRectLevel(Bitmap picture, Rect rectangle, Canvas g)
	{
		if(inView(rectangle.left, rectangle.top, rectangle.bottom - rectangle.top, rectangle.right - rectangle.left))
		{
			g.drawBitmap(picture, null, rectangle, paint);
		}
	}
	/*
	 * Replaces canvas.drawText(String, int, int, Paint) and auto scales
	 */
	protected void drawText(String text, int x, int y, Canvas g)
	{
		// TODO
		g.drawText(text, x, y, paint);
	}
}