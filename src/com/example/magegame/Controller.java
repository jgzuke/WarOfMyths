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
 * @param obstaclesRectanglesX1 Array of all walls left x value
 * @param obstaclesRectanglesX2 Array of all walls right x value
 * @param obstaclesRectanglesY1 Array of all walls top y value
 * @param obstaclesRectanglesY2 Array of all walls bottom x value
 * @param obstaclesCirclesX Array of all pillars middle x value
 * @param obstaclesCirclesY Array of all pillars middle y value
 * @param obstaclesCirclesRadius Array of all pillars radius
 * @param currentCircle Current index of obstaclesCirclesX to write to
 * @param currentRectangle Current index of obstaclesRectanglesX1 to write to
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
import android.graphics.Rect;
import android.util.Log;

import java.util.Random;
public final class Controller extends AllViews
{
	protected Enemy_Muggle[] enemies = new Enemy_Muggle[30];
	private PowerBall[] powerBalls = new PowerBall[30];
	private SpGraphic[] spGraphic = new SpGraphic[30];
	private PowerBallAOE[] powerBallAOEs = new PowerBallAOE[30];
	private Graphic_Teleport[] graphic_Teleport = new Graphic_Teleport[30];
	private Wall_Rectangle[] walls = new Wall_Rectangle[30];
	private Wall_Circle[] wallCircles = new Wall_Circle[30];
	private Rect aoeRect = new Rect();
	private int wallWidth = 10;
	private boolean gameEnded = false;
	private int currentCircle = 0;
	private int currentRectangle = 0;
	private int[][] teleportSpots = new int[2][4];
	protected Player player;
	protected Enemy_Mage enemy;
	protected Context context;
	protected SpControl spGraphicEnemy;
	protected SpControl spGraphicPlayer;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator;
	private int playerColour;
	private int enemyColour;
	private int difficultyLevel;
	private double difficultyLevelMultiplier;
	private int enemyType;
	private int playerType;
	private int levelNum;
	private int warningTimer;
	private int warningType;
	private int[] obstaclesRectanglesX1;
	private int[] obstaclesRectanglesX2;
	private int[] obstaclesRectanglesY1;
	private int[] obstaclesRectanglesY2;
	private int[] obstaclesCirclesX;
	private int[] obstaclesCirclesY;
	private int[] obstaclesCirclesRadius;
	private Bitmap background;
	private PlayerGestureDetector detect;
	/* 
	 * Initializes all undecided variables, loads level, creates player and enemy objects, and starts frameCaller
	 */
	public Controller(Context startSet, StartActivity activitySet, ImageLibrary imageLibrarySet)
	{
		super(startSet);
		activity = activitySet;
		imageLibrary = imageLibrarySet;
		screenMinX = activitySet.screenMinX;
		screenMinY = activitySet.screenMinY;
		screenDimensionMultiplier = activitySet.screenDimensionMultiplier;
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		context = startSet;
		randomGenerator = new Random();
		detect = new PlayerGestureDetector(this);
		setOnTouchListener(detect);
		spGraphicEnemy = new SpControl(this, false);
		spGraphicPlayer = new SpControl(this, true);
		imageLibrary.changeArrayLoaded("swordsman", true);
		for(int i = 0; i < 4; i++)
		{
			enemies[i] = new Enemy_Swordsman(this, teleportSpots[0][i], teleportSpots[1][i]);
		}
	}
	public void primeFighting()
	{
		enemies = new Enemy_Muggle[30];
		powerBalls = new PowerBall[30];
		spGraphic = new SpGraphic[30];
		powerBallAOEs = new PowerBallAOE[30];
		graphic_Teleport = new Graphic_Teleport[30];
		walls = new Wall_Rectangle[30];
		wallCircles = new Wall_Circle[30];
		teleportSpots = new int[2][4];
		aoeRect = new Rect();
		wallWidth = 10;
		gameEnded = false;
		currentCircle = 0;
		currentRectangle = 0;
		enemyType = randomGenerator.nextInt(4);
		player = new Player(this);
		detect.setPlayer(player);
		enemy = new Enemy_Mage(this);
		switch(enemyType)
		{
		case 0:
			imageLibrary.powerBallAOE_Image[0] = imageLibrary.loadImage("powerballaoe0001", 80, 80);
			imageLibrary.powerBall_Image[0] = imageLibrary.loadArray1D(5, "powerball0001_", 35, 15);
			enemyColour = Color.rgb(255, 0, 0);
			break;
		case 1:
			imageLibrary.powerBallAOE_Image[1] = imageLibrary.loadImage("powerballaoe0002", 80, 80);
			imageLibrary.powerBall_Image[1] = imageLibrary.loadArray1D(5, "powerball0002_", 35, 15);
			enemyColour = Color.rgb(0, 0, 255);
			enemy.setMp(2250);
			enemy.setMpMax(4500);
			break;
		case 2:
			imageLibrary.powerBallAOE_Image[2] = imageLibrary.loadImage("powerballaoe0003", 80, 80);
			imageLibrary.powerBall_Image[2] = imageLibrary.loadArray1D(5, "powerball0003_", 35, 15);
			enemyColour = Color.rgb(170, 119, 221);
			enemy.setSpeedCur(4);
			break;
		case 3:
			imageLibrary.powerBallAOE_Image[3] = imageLibrary.loadImage("powerballaoe0004", 80, 80);
			imageLibrary.powerBall_Image[3] = imageLibrary.loadArray1D(5, "powerball0004_", 35, 15);
			enemyColour = Color.rgb(102, 51, 0);
			enemy.setHp(9000);
			enemy.setHpMax(9000);
			break;
		}
	}
	public void startFighting(int playerTypeSet, int levelNumSet, int difficultyLevelSet)
	{
		playerType = playerTypeSet;
		difficultyLevel = difficultyLevelSet;
		levelNum = levelNumSet;
		difficultyLevelMultiplier = 20 / (double)(difficultyLevel + 10);
		switch(playerType)
		{
		case 0:
			imageLibrary.powerBallAOE_Image[0] = imageLibrary.loadImage("powerballaoe0001", 80, 80);
			imageLibrary.powerBall_Image[0] = imageLibrary.loadArray1D(5, "powerball0001_", 35, 15);
			playerColour = Color.rgb(255, 0, 0);
			break;
		case 1:
			imageLibrary.powerBallAOE_Image[1] = imageLibrary.loadImage("powerballaoe0002", 80, 80);
			imageLibrary.powerBall_Image[1] = imageLibrary.loadArray1D(5, "powerball0002_", 35, 15);
			playerColour = Color.rgb(0, 0, 255);
			player.setMp(2250);
			player.setMpMax(4500);
			break;
		case 2:
			imageLibrary.powerBallAOE_Image[2] = imageLibrary.loadImage("powerballaoe0003", 80, 80);
			imageLibrary.powerBall_Image[2] = imageLibrary.loadArray1D(5, "powerball0003_", 35, 15);
			playerColour = Color.rgb(170, 119, 221);
			player.setSpeedCur(4);
			break;
		case 3:
			imageLibrary.powerBallAOE_Image[3] = imageLibrary.loadImage("powerballaoe0004", 80, 80);
			imageLibrary.powerBall_Image[3] = imageLibrary.loadArray1D(5, "powerball0004_", 35, 15);
			playerColour = Color.rgb(102, 51, 0);
			player.setHp(9000);
			player.setHpMax(9000);
			break;
		}
		loadLevel();
		background = drawStart();
	}
	/*
	 * Loads the level the user picked and initializes teleport and wall array variables
	 */
	public Wall_Rectangle makeWall_Rectangle(int x1, int x2, int y1, int y2)
	{
		Wall_Rectangle wall1 = new Wall_Rectangle(this, x1 - wallWidth, x2 + wallWidth, y1 - wallWidth, y2 + wallWidth);
		return wall1;
	}
	public Wall_Circle makeWall_Circle(int x, int y, int rad)
	{
		Wall_Circle wall1 = new Wall_Circle(this, x, y, rad + wallWidth);
		return wall1;
	}
	public void createWallRectangleValueArrays(int length)
	{
		obstaclesRectanglesX1 = new int[length];
		obstaclesRectanglesX2 = new int[length];
		obstaclesRectanglesY1 = new int[length];
		obstaclesRectanglesY2 = new int[length];
	}
	public void createWallCircleValueArrays(int length)
	{
		obstaclesCirclesX = new int[length];
		obstaclesCirclesY = new int[length];
		obstaclesCirclesRadius = new int[length];
	}
	public void loadLevel()
	{
		if(levelNum == 0)
		{
			createWallRectangleValueArrays(6);
			createWallCircleValueArrays(0);
			teleportSpots[0][0] = 110;
			teleportSpots[0][1] = 370;
			teleportSpots[0][2] = 110;
			teleportSpots[0][3] = 370;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			walls[0] = makeWall_Rectangle(90, 220, 50, 60);
			walls[1] = makeWall_Rectangle(260, 390, 50, 60);
			walls[2] = makeWall_Rectangle(90, 220, 260, 270);
			walls[3] = makeWall_Rectangle(260, 390, 260, 270);
			walls[4] = makeWall_Rectangle(130, 140, 100, 220);
			walls[5] = makeWall_Rectangle(340, 350, 100, 220);
		}
		if(levelNum == 1)
		{
			createWallRectangleValueArrays(6);
			createWallCircleValueArrays(1);
			teleportSpots[0][0] = 110;
			teleportSpots[0][1] = 370;
			teleportSpots[0][2] = 110;
			teleportSpots[0][3] = 370;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			walls[0] = makeWall_Rectangle(130, 220, 50, 60);
			walls[1] = makeWall_Rectangle(260, 350, 50, 60);
			walls[2] = makeWall_Rectangle(130, 220, 260, 270);
			walls[3] = makeWall_Rectangle(260, 350, 260, 270);
			walls[4] = makeWall_Rectangle(130, 140, 50, 270);
			walls[5] = makeWall_Rectangle(340, 350, 50, 270);
			wallCircles[6] = makeWall_Circle(240, 160, 60);
		}
		if(levelNum == 2)
		{
			createWallRectangleValueArrays(6);
			createWallCircleValueArrays(0);
			teleportSpots[0][0] = 160;
			teleportSpots[0][1] = 320;
			teleportSpots[0][2] = 160;
			teleportSpots[0][3] = 320;
			teleportSpots[1][0] = 110;
			teleportSpots[1][1] = 110;
			teleportSpots[1][2] = 210;
			teleportSpots[1][3] = 210;
			walls[0] = makeWall_Rectangle(130, 140, 50, 60);
			walls[1] = makeWall_Rectangle(340, 350, 50, 60);
			walls[2] = makeWall_Rectangle(130, 140, 180, 270);
			walls[3] = makeWall_Rectangle(340, 350, 180, 270);
			walls[4] = makeWall_Rectangle(130, 350, 130, 140);
			walls[5] = makeWall_Rectangle(130, 350, 180, 190);
		}
		if(levelNum == 3)
		{
			createWallRectangleValueArrays(4);
			createWallCircleValueArrays(0);
			teleportSpots[0][0] = 190;
			teleportSpots[0][1] = 290;
			teleportSpots[0][2] = 190;
			teleportSpots[0][3] = 290;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			walls[0] = makeWall_Rectangle(185, 195, 50, 270);
			walls[1] = makeWall_Rectangle(235, 245, 10, 140);
			walls[2] = makeWall_Rectangle(235, 245, 180, 310);
			walls[3] = makeWall_Rectangle(285, 295, 50, 270);
		}
		if(levelNum == 4)
		{
			createWallRectangleValueArrays(4);
			createWallCircleValueArrays(0);
			teleportSpots[0][0] = 215;
			teleportSpots[0][1] = 265;
			teleportSpots[0][2] = 215;
			teleportSpots[0][3] = 265;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			walls[0] = makeWall_Rectangle(130, 350, 130, 140);
			walls[1] = makeWall_Rectangle(235, 245, 10, 140);
			walls[2] = makeWall_Rectangle(235, 245, 180, 310);
			walls[3] = makeWall_Rectangle(130, 350, 180, 190);
		}
	}
	/*
	 * Draws hp, mp, sp, and cooldown bars for player and enemies
	 */
	public void drawContestantStats(Canvas g)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		//drawRect(395, 240, 475, 316, g);
		//drawRect(5, 240, 85, 316, g);
		paint.setColor(Color.RED);
		drawRect(400, 116, 400 + (70 * player.getHp() / player.getHpMax()), 132, g);
		paint.setColor(Color.BLUE);
		drawRect(400, 157, 400 + (70 * player.getMp() / player.getMpMax()), 173, g);
		paint.setColor(Color.GREEN);
		drawRect(400, 203, 400 + (int)(70 * player.getSp()), 219, g);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect(400, 116, 470, 132, g);
		drawRect(400, 157, 470, 173, g);
		drawRect(400, 203, 470, 219, g);
		Log.e("game", Double.toString(player.getSp()));
		drawText(Integer.toString(player.getHp()), 417, 129, g);
		drawText(Integer.toString(player.getMp()), 417, 170, g);
		drawText(Integer.toString((int)(3500*player.getSp())), 417, 216, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.YELLOW);
		drawRect(10, 70, 10 + (int)((70 * player.getAbilityTimer_burst()) / 400), 80, g);
		drawRect(10, 223, 10 + (int)((70 * player.getAbilityTimer_roll()) / 350), 233, g);
		drawRect(10, 300, 10 + (int)((70 * player.getAbilityTimer_teleport()) / 500), 310, g);
		drawRect(190, 295, 190 + (int)((100 * player.getAbilityTimer_powerBall()) / 90), 310, g);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawRect(10, 70, 80, 80, g);
		drawRect(10, 223, 80, 233, g);
		drawRect(10, 300, 80, 310, g);
		drawRect(190, 295, 290, 310, g);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
		drawRect((int) enemy.getX() - 20, (int) enemy.getY() - 30, (int) enemy.getX() - 20 + (40 * enemy.getHp() / enemy.getHpMax()), (int) enemy.getY() - 20, g);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect((int) enemy.getX() - 20, (int) enemy.getY() - 30, (int) enemy.getX() + 20, (int) enemy.getY() - 20, g);
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
				paint.setColor(Color.RED);
				paint.setStyle(Paint.Style.FILL);
				drawRect((int) enemies[i].getX() - 20, (int) enemies[i].getY() - 30, (int) enemies[i].getX() - 20 + (40 * enemies[i].getHp() / enemies[i].getHpMax()), (int) enemies[i].getY() - 20, g);
				paint.setColor(Color.BLACK);
				paint.setStyle(Paint.Style.STROKE);
				drawRect((int) enemies[i].getX() - 20, (int) enemies[i].getY() - 30, (int) enemies[i].getX() + 20, (int) enemies[i].getY() - 20, g);
			}
		}
	}
	/*
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	public void frameCall()
	{
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
		for(int i = 0; i < spGraphic.length; i++)
		{
			if(spGraphic[i] != null)
			{
				if(spGraphic[i].isDeleted())
				{
					spGraphic[i] = null;
				}
				else
				{
					spGraphic[i].frameCall();
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
					enemies[i].frameCall();
					if(enemies[i].getX() < 100) enemies[i].setX(100);
					if(enemies[i].getX() > 380) enemies[i].setX(380);
					if(enemies[i].getY() < 20) enemies[i].setY(20);
					if(enemies[i].getY() > 300) enemies[i].setY(300);
				}
			}
		}
		if(!player.isDeleted())
		{
			player.frameCall();
			if(!player.isTeleporting())
			{
				if(player.getX() < 100) player.setX(100);
				if(player.getX() > 380) player.setX(380);
				if(player.getY() < 20) player.setY(20);
				if(player.getY() > 300) player.setY(300);
			}
		}
		else
		{
			player = null;
		}
		if(!enemy.isDeleted())
		{
			enemy.frameCall();
			if(enemy.getX() < 100) enemy.setX(100);
			if(enemy.getX() > 380) enemy.setX(380);
			if(enemy.getY() < 20) enemy.setY(20);
			if(enemy.getY() > 300) enemy.setY(300);
		}
		else
		{
			enemy = null;
		}
		spGraphicPlayer.frameCall();
		spGraphicEnemy.frameCall();
		invalidate();
	}
	 /*
	 * draws background, player screens, level etc (TODO generate bitmap to draw instead)
	 */
	public Bitmap drawStart()
	{
		Bitmap drawTo = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_8888);
		Canvas toReturn = new Canvas(drawTo);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(playerColour);
		drawRect(0, 0, 480, 320, toReturn);
		paint.setColor(Color.WHITE);
		drawRect(90, 10, 390, 310, toReturn);
		drawBitmap(imageLibrary.loadImage("symbol000" + Integer.toString(playerType+1), 80, 80), 395, 10, toReturn);
		drawBitmap(imageLibrary.loadImage("fullscreen0001", 90, 320), 0, 0, toReturn);
		drawBitmap(imageLibrary.loadImage("fullscreen0002", 90, 320), 390, 0, toReturn);
		paint.setColor(Color.GRAY);
		for(int i = 0; i < obstaclesRectanglesX1.length; i++)
		{
			drawRect(obstaclesRectanglesX1[i] + wallWidth, obstaclesRectanglesY1[i] + wallWidth, obstaclesRectanglesX2[i] - wallWidth, obstaclesRectanglesY2[i] - wallWidth, toReturn);
		}
		for(int i = 0; i < obstaclesCirclesX.length; i++)
		{
			drawCircle(obstaclesCirclesX[i], obstaclesCirclesY[i], obstaclesCirclesRadius[i] - wallWidth, toReturn);
		}
		return drawTo;
	}
	/*
	 * Draws objects and calls other drawing functions (background and stats)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@ Override
	public void onDraw(Canvas g)
	{
		g.translate(screenMinX, screenMinY);
		g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
		drawBitmap(background, 0, 0, g);
		drawContestantStats(g);
		if(player != null)
		{
			drawBitmapRotated(player, g);
		}
		if(enemy != null)
		{
			drawBitmapRotated(enemy, g);
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
				drawBitmap(graphic_Teleport[i].getVisualImage(), (int)(graphic_Teleport[i].getX() - (graphic_Teleport[i].getImageWidth() / 2)), (int)(graphic_Teleport[i].getY() - (graphic_Teleport[i].getImageWidth() / 2)), g);
			}
		}
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GREEN);
		for(int i = 0; i < spGraphic.length; i++)
		{
			if(spGraphic[i] != null)
			{
				drawCircle((int)(spGraphic[i].getX() - (spGraphic[i].getWidth() / 2)), (int)(spGraphic[i].getY() - (spGraphic[i].getWidth() / 2)), (int)spGraphic[i].getWidth(), g);
			}
		}
		if(warningTimer > 0)
		{
			warningTimer--;
			paint.setAlpha((byte)(warningTimer * 7));
			drawBitmap(imageLibrary.warnings[warningType], 240 - (imageLibrary.warnings[warningType].getWidth() / 2), 160 - (imageLibrary.warnings[warningType].getHeight() / 2), g);
		}
		paint.setAlpha(255);
	}
	public void createPowerBallEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Enemy ballEnemy = new PowerBall_Enemy(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballEnemy;
	}
	public void createPowerBallPlayer(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Player ballPlayer = new PowerBall_Player(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballPlayer;
	}
	public void createPowerBallEnemyAOE(double x, double y, double power)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	public void createPowerBallPlayerAOE(double x, double y, double power)
	{
		PowerBallAOE_Player ballAOEPlayer = new PowerBallAOE_Player(this, (int) x, (int) y, power);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
	}
	public void teleportStart(double x, double y)
	{
		Graphic_Teleport teleportStart = new Graphic_Teleport(this, x, y, true);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportStart;
	}
	public void teleportFinish(double x, double y)
	{
		Graphic_Teleport teleportFinish = new Graphic_Teleport(this, x, y, false);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportFinish;
	}
	/*
	 * Tests an array to find lowest null index
	 * @ param array Array to check in for null indexes
	 * @ return Returns lowest index that equals null
	 */
	public int lowestPositionEmpty(Sprite[] array)
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
	public void notEnoughMana()
	{
		warningTimer = 30;
		warningType = 1;
	}
	/*
	 * Starts 'cooldown' warning
	 */
	public void coolDown()
	{
		warningTimer = 30;
		warningType = 0;
	}
	public boolean pointOnScreen(double x, double y)
	{
		x = (x-screenMinX)/screenDimensionMultiplier;
		y = (y-screenMinY)/screenDimensionMultiplier;
		if(x > 90 && x < 390 && y > 10 && y < 310)
        {
			return true;
        } else
        {
        	return false;
        }
	}
	public boolean pointOnSquare(double x, double y, double lowX, double lowY, double highX, double highY)
	{
		x = (x-screenMinX)/screenDimensionMultiplier;
		y = (y-screenMinY)/screenDimensionMultiplier;
		if(x > lowX && x < highX && y > lowY && y < highY)
        {
			return true;
        } else
        {
        	return false;
        }
	}
	public boolean pointOnCircle(double x, double y, double midX, double midY, double radius)
	{
		x = (x-screenMinX)/screenDimensionMultiplier;
		y = (y-screenMinY)/screenDimensionMultiplier;
		if(Math.sqrt(Math.pow(x-midX, 2) + Math.pow(y-midY, 2)) < radius)
        {
			return true;
        } else
        {
        	return false;
        }
	}
	public int getDifficultyLevel() {
		return difficultyLevel;
	}
	public double getDifficultyLevelMultiplier() {
		return difficultyLevelMultiplier;
	}
	public int getRandomInt(int i) {
		return randomGenerator.nextInt(i);
	}
	public double getRandomDouble() {
		return randomGenerator.nextDouble();
	}
	public int getEnemyType() {
		return enemyType;
	}
	public int getPlayerType() {
		return playerType;
	}
	public double getPlayerX()
	{
		return player.getX();
	}
	public double getPlayerY()
	{
		return player.getY();
	}
	public int getLevelNum() {
		return levelNum;
	}
	public SpGraphic getSpGraphic(int i) {
		return spGraphic[i];
	}
	public int getObstaclesRectanglesX1(int i) {
		return obstaclesRectanglesX1[i];
	}
	public int getObstaclesRectanglesX2(int i) {
		return obstaclesRectanglesX2[i];
	}
	public int getObstaclesRectanglesY1(int i) {
		return obstaclesRectanglesY1[i];
	}
	public int getObstaclesRectanglesY2(int i) {
		return obstaclesRectanglesY2[i];
	}
	public int getObstaclesCirclesX(int i) {
		return obstaclesCirclesX[i];
	}
	public int getObstaclesCirclesY(int i) {
		return obstaclesCirclesY[i];
	}
	public void setSpGraphic(int i, SpGraphic spGraphic) {
		this.spGraphic[i] = spGraphic;
	}
	public int getObstaclesCirclesRadius(int i) {
		return obstaclesCirclesRadius[i];
	}
	public int getCurrentCircle() {
		return currentCircle;
	}
	public int getCurrentRectangle() {
		return currentRectangle;
	}
	public int getTeleportSpots(int i, int j) {
		return teleportSpots[i][j];
	}
	public boolean getGameEnded() {
		return gameEnded;
	}
	public SpGraphic[] getSpGraphic() {
		return spGraphic;
	}
	public void incrementCurrentRectangle()
	{
		currentRectangle ++;
	}
	public void incrementCurrentCircle()
	{
		currentCircle ++;
	}
	public void setObstaclesRectanglesX1(int i, int obstaclesRectanglesX1) {
		this.obstaclesRectanglesX1[i] = obstaclesRectanglesX1;
	}
	public void setObstaclesRectanglesX2(int i, int obstaclesRectanglesX2) {
		this.obstaclesRectanglesX2[i] = obstaclesRectanglesX2;
	}
	public void setObstaclesRectanglesY1(int i, int obstaclesRectanglesY1) {
		this.obstaclesRectanglesY1[i] = obstaclesRectanglesY1;
	}
	public void setObstaclesRectanglesY2(int i, int obstaclesRectanglesY2) {
		this.obstaclesRectanglesY2[i] = obstaclesRectanglesY2;
	}
	public void setObstaclesCirclesX(int i, int obstaclesCirclesX) {
		this.obstaclesCirclesX[i] = obstaclesCirclesX;
	}
	public void setObstaclesCirclesY(int i, int obstaclesCirclesY) {
		this.obstaclesCirclesY[i] = obstaclesCirclesY;
	}
	public void setObstaclesCirclesRadius(int i, int obstaclesCirclesRadius) {
		this.obstaclesCirclesRadius[i] = obstaclesCirclesRadius;
	}
}