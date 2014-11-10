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
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.Random;
public final class Controller extends AllViews
{
	protected Enemy[] enemies = new Enemy[30];
	private PowerBall[] powerBalls = new PowerBall[30];
	private PowerBallAOE[] powerBallAOEs = new PowerBallAOE[30];
	private Graphic_Teleport[] graphic_Teleport = new Graphic_Teleport[30];
	private Wall_Rectangle[] walls = new Wall_Rectangle[30];
	private Wall_Circle[] wallCircles = new Wall_Circle[30];
	private Rect aoeRect = new Rect();
	private int wallWidth = 10;
	private boolean gameEnded = false;
	private int currentCircle = 0;
	private int currentRectangle = 0;
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator;
	private int playerColour;
	private int difficultyLevel;
	private double difficultyLevelMultiplier;
	private int playerType;
	protected int levelNum;
	private int warningTimer;
	private int warningType;
	private int[] oRectX1;
	private int[] oRectX2;
	private int[] oRectY1;
	private int[] oRectY2;
	private int[] oCircX;
	private int[] oCircY;
	private int[] oCircRadius;
	private Bitmap background;
	private PlayerGestureDetector detect;
	private double spChangeForType;
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected int xShiftLevel;
	protected int yShiftLevel;
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
		player = new Player(this);
		detect.setPlayer(player);
	}
	protected void primeFighting()
	{
		player.resetVariables();
		enemies = new Enemy[30];
		powerBalls = new PowerBall[30];
		powerBallAOEs = new PowerBallAOE[30];
		graphic_Teleport = new Graphic_Teleport[30];
		walls = new Wall_Rectangle[30];
		wallCircles = new Wall_Circle[30];
		gameEnded = false;
		currentCircle = 0;
		currentRectangle = 0;
		// TODO change to black ball
		imageLibrary.powerBallAOE_Image[0] = imageLibrary.loadImage("powerballaoe0001", 80, 80);
		imageLibrary.powerBall_Image[0] = imageLibrary.loadArray1D(5, "powerball0001_", 35, 15);
	}
	protected void startFighting(int playerTypeSet, int levelNumSet, int difficultyLevelSet)
	{
		playerType = playerTypeSet;
		player.humanType = playerType;
		difficultyLevel = difficultyLevelSet;
		levelNum = levelNumSet;
		difficultyLevelMultiplier = 20 / (double)(difficultyLevel + 10);
		switch(playerType)
		{
		case 0:
			imageLibrary.powerBallAOE_Image[0] = imageLibrary.loadImage("powerballaoe0001", 80, 80);
			imageLibrary.powerBall_Image[0] = imageLibrary.loadArray1D(5, "powerball0001_", 35, 15);
			player.spChangeForType = activity.wApollo;
			playerColour = Color.rgb(255, 0, 0);
			break;
		case 1:
			imageLibrary.powerBallAOE_Image[1] = imageLibrary.loadImage("powerballaoe0002", 80, 80);
			imageLibrary.powerBall_Image[1] = imageLibrary.loadArray1D(5, "powerball0002_", 35, 15);
			player.spChangeForType = activity.wPoseidon;
			playerColour = Color.rgb(0, 0, 255);
			break;
		case 2:
			imageLibrary.powerBallAOE_Image[2] = imageLibrary.loadImage("powerballaoe0003", 80, 80);
			imageLibrary.powerBall_Image[2] = imageLibrary.loadArray1D(5, "powerball0003_", 35, 15);
			player.spChangeForType = activity.wZues;
			playerColour = Color.rgb(170, 119, 221);
			break;
		case 3:
			imageLibrary.powerBallAOE_Image[3] = imageLibrary.loadImage("powerballaoe0004", 80, 80);
			imageLibrary.powerBall_Image[3] = imageLibrary.loadArray1D(5, "powerball0004_", 35, 15);
			player.spChangeForType = activity.wHades;
			playerColour = Color.rgb(102, 51, 0);
			break;
		}
		loadLevel();
		detect.getSide();
		background = drawStart();
	}
	/*
	 * Loads the level the user picked and initializes teleport and wall array variables
	 */
	protected Wall_Rectangle makeWall_Rectangle(int x, int y, int width, int height)
	{
		Wall_Rectangle wall1 = new Wall_Rectangle(this, x-2, y-2, width+4, height+4);
		return wall1;
	}
	protected Wall_Circle makeWall_Circle(int x, int y, int rad)
	{
		Wall_Circle wall1 = new Wall_Circle(this, x, y, rad+2);
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
	}
	protected void loadLevel()
	{
		if(levelNum == 0)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(8);
			createWallCircleValueArrays(0);
			levelWidth = 500;
			levelHeight = 500;
			enemies[4] = new Enemy_Mage(this, 105, 250);
			player.x = 325;
			player.y = 250;
			enemies[0] = new Enemy_Swordsman(this, 85, 85);
			enemies[1] = new Enemy_Swordsman(this, 415, 85);
			enemies[2] = new Enemy_Swordsman(this, 85, 415);
			enemies[3] = new Enemy_Swordsman(this, 415, 415);
			walls[0] = makeWall_Rectangle(50, 50, 400, 10);
			walls[1] = makeWall_Rectangle(50, 440, 400, 10);
			walls[2] = makeWall_Rectangle(110, 110, 115, 10);
			walls[3] = makeWall_Rectangle(275, 110, 115, 10);
			walls[4] = makeWall_Rectangle(110, 380, 115, 10);
			walls[5] = makeWall_Rectangle(275, 380, 115, 10);
			walls[6] = makeWall_Rectangle(110, 110, 10, 280);
			walls[7] = makeWall_Rectangle(380, 110, 10, 280);
		}
		if(levelNum == 1)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(1);
			createWallCircleValueArrays(0);
			levelWidth = 500;
			levelHeight = 500;
			walls[0] = makeWall_Rectangle(150, 150, 100, 100);
			enemies[3] = new Enemy_Swordsman(this, 370, 290);
			/*imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(6);
			createWallCircleValueArrays(1);
			levelWidth = 500;
			levelHeight = 500;
			enemies[4] = new Enemy_Mage(this, 110, 160);
			enemies[0] = new Enemy_Swordsman(this, 110, 30);
			enemies[1] = new Enemy_Swordsman(this, 370, 30);
			enemies[2] = new Enemy_Swordsman(this, 110, 290);
			enemies[3] = new Enemy_Swordsman(this, 370, 290);
			walls[0] = makeWall_Rectangle(130, 220, 50, 60);
			walls[1] = makeWall_Rectangle(260, 350, 50, 60);
			walls[2] = makeWall_Rectangle(130, 220, 260, 270);
			walls[3] = makeWall_Rectangle(260, 350, 260, 270);
			walls[4] = makeWall_Rectangle(130, 140, 50, 270);
			walls[5] = makeWall_Rectangle(340, 350, 50, 270);
			wallCircles[6] = makeWall_Circle(240, 160, 60);
			*/
		}
		if(levelNum == 2)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(6);
			createWallCircleValueArrays(0);
			enemies[4] = new Enemy_Mage(this, 110, 160);
			enemies[0] = new Enemy_Swordsman(this, 160, 110);
			enemies[1] = new Enemy_Swordsman(this, 320, 110);
			enemies[2] = new Enemy_Swordsman(this, 160, 210);
			enemies[3] = new Enemy_Swordsman(this, 320, 210);
			walls[0] = makeWall_Rectangle(130, 140, 50, 60);
			walls[1] = makeWall_Rectangle(340, 350, 50, 60);
			walls[2] = makeWall_Rectangle(130, 140, 180, 270);
			walls[3] = makeWall_Rectangle(340, 350, 180, 270);
			walls[4] = makeWall_Rectangle(130, 350, 130, 140);
			walls[5] = makeWall_Rectangle(130, 350, 180, 190);
		}
		if(levelNum == 3)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(4);
			createWallCircleValueArrays(0);
			enemies[4] = new Enemy_Mage(this, 110, 160);
			enemies[0] = new Enemy_Swordsman(this, 190, 30);
			enemies[1] = new Enemy_Swordsman(this, 290, 30);
			enemies[2] = new Enemy_Swordsman(this, 190, 290);
			enemies[3] = new Enemy_Swordsman(this, 290, 290);
			walls[0] = makeWall_Rectangle(185, 195, 50, 270);
			walls[1] = makeWall_Rectangle(235, 245, -10, 140);
			walls[2] = makeWall_Rectangle(235, 245, 180, 330);
			walls[3] = makeWall_Rectangle(285, 295, 50, 270);
		}
		if(levelNum == 4)
		{
			imageLibrary.changeArrayLoaded("swordsman", true);
			createWallRectangleValueArrays(4);
			createWallCircleValueArrays(0);
			enemies[4] = new Enemy_Mage(this, 110, 160);
			enemies[0] = new Enemy_Swordsman(this, 215, 30);
			enemies[1] = new Enemy_Swordsman(this, 265, 30);
			enemies[2] = new Enemy_Swordsman(this, 215, 290);
			enemies[3] = new Enemy_Swordsman(this, 265, 290);
			walls[0] = makeWall_Rectangle(130, 350, 130, 140);
			walls[1] = makeWall_Rectangle(235, 245, -10, 140);
			walls[2] = makeWall_Rectangle(235, 245, 180, 330);
			walls[3] = makeWall_Rectangle(130, 350, 180, 190);
		}
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
		drawRect(400-fix, 116, 400-fix + (70 * player.getHp() / player.getHpMax()), 132, g);
		paint.setColor(Color.GREEN);
		drawRect(400-fix, 163, 400-fix + (int)(70 * player.getSp()), 179, g);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect(400-fix, 116, 470-fix, 132, g);
		drawRect(400-fix, 163, 470-fix, 179, g);
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
						alive = true;
						if(enemies[i].getX() < 10) enemies[i].setX(10);
						if(enemies[i].getX() > levelWidth-10) enemies[i].setX(levelWidth-10);
						if(enemies[i].getY() < 10) enemies[i].setY(10);
						if(enemies[i].getY() > levelHeight-10) enemies[i].setY(levelHeight-10);
					}
				}
			}
		}
		if(!alive)
		{
			if(levelNum == activity.levelBeaten)
			{
				activity.levelBeaten++;
			}
			activity.startMenu(false);
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
		invalidate();
	}
	 /*
	 * draws background, player screens, level etc (TODO generate bitmap to draw instead)
	 */
	protected Bitmap drawStart()
	{
		Bitmap drawTo = Bitmap.createBitmap(480, 320, Bitmap.Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(playerColour);
		drawRect(0, 0, 480, 10, g);
		drawRect(0, 310, 480, 320, g);
		drawRect(0, 0, 90, 320, g);
		drawRect(390, 0, 480, 320, g);
		if(activity.stickOnRight)
		{
			drawBitmap(imageLibrary.loadImage("fullscreen0001", 480, 320), 0, 0, g);
			drawBitmap(imageLibrary.loadImage("symbol000" + Integer.toString(playerType+1), 80, 80), 395, 10, g);
		} else
		{
			drawBitmap(imageLibrary.loadImage("fullscreen0002", 480, 320), 0, 0, g);
			drawBitmap(imageLibrary.loadImage("symbol000" + Integer.toString(playerType+1), 80, 80), 5, 10, g);
		}
		return drawTo;
	}
	protected Bitmap drawLevel()
	{
		Bitmap drawTo = Bitmap.createBitmap(levelWidth, levelHeight, Bitmap.Config.ARGB_8888);
		Canvas g = new Canvas(drawTo);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		drawRect(0, 0, levelWidth, levelHeight, g);
		paint.setColor(Color.GRAY);
		for(int i = 0; i < oRectX1.length; i++)
		{
			drawRect(oRectX1[i], oRectY1[i], oRectX2[i], oRectY2[i], g);
		}
		for(int i = 0; i < oCircX.length; i++)
		{
			drawCircle(oCircX[i], oCircY[i], oCircRadius[i], g);
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
				drawBitmap(graphic_Teleport[i].getVisualImage(), (int)(graphic_Teleport[i].getX() - (graphic_Teleport[i].getImageWidth() / 2)), (int)(graphic_Teleport[i].getY() - (graphic_Teleport[i].getImageWidth() / 2)), g);
			}
		}
		drawHealthBars(g);
		return drawTo;
	}
	/*
	 * Draws objects and calls other drawing functions (background and stats)
	 * @see android.view.View#onDraw(android.graphics.Canvas)
	 */
	@ Override
	protected void onDraw(Canvas g)
	{
		g.translate(screenMinX, screenMinY);
		g.scale((float) screenDimensionMultiplier, (float) screenDimensionMultiplier);
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
	}
	protected void createPowerBallEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Enemy ballEnemy = new PowerBall_Enemy(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballEnemy;
	}
	protected void createPowerBallPlayer(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Player ballPlayer = new PowerBall_Player(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballPlayer;
	}
	protected void createPowerBallEnemyAOE(double x, double y, double power)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	protected void createPowerBallPlayerAOE(double x, double y, double power)
	{
		PowerBallAOE_Player ballAOEPlayer = new PowerBallAOE_Player(this, (int) x, (int) y, power);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
	}
	protected void teleportStart(double x, double y)
	{
		Graphic_Teleport teleportStart = new Graphic_Teleport(this, x, y, true);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportStart;
	}
	protected void teleportFinish(double x, double y)
	{
		Graphic_Teleport teleportFinish = new Graphic_Teleport(this, x, y, false);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportFinish;
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
	
	
	protected boolean checkObstructionsPoint(double x1, double y1, double x2, double y2)
	{	
		boolean hitBack = false;
		double m1 = (y2-y1)/(x2-x1);
		double b1 = y1-(m1*x1);
		double circM;
		double circB;
		double tempX;
		double tempY;
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
					if(Math.sqrt(Math.pow(tempX-oCircX[i],2)+Math.pow(tempY-oCircY[i],2))<oCircRadius[i])
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
		return hitBack;
	}
	
	protected boolean checkObstructions(double x1, double y1, double rads, int distance)
	{
		double x2 = x1 + (Math.cos(rads) * distance);
		double y2 = y1 + (Math.sin(rads) * distance);
		return checkObstructionsPoint(x1, y1, x2, y2);
	}
	protected boolean checkObstructionsPointBackup(double x1, double y1, double x2, double y2)
	{
		if(x1>x2)
		{
			double temp = x1;
			x1 = x2;
			x2 = temp;
			temp = y1;
			y1 = y2;
			y2 = temp;
		}
		double m1 = (y2-y1)/(x2-x1);
		double xChecked = x1;
		double yChecked = y1;
		boolean checkObstructionsObstructed = false;
		double xMove = Math.sqrt(4/(1+m1));
		double yMove = m1*xMove;
		if(m1>1)
		{
			yMove = 3;
			xMove = yMove/m1;
		} else
		{
			xMove = 3;
			yMove = m1*xMove;
		}
		while(xChecked < x2)
		{			
			if(checkHitBack(xChecked, yChecked))
			{
				checkObstructionsObstructed = true;
				xChecked = x2;
			}
			xChecked += xMove;
			yChecked += yMove;
		}
		return checkObstructionsObstructed;
		
		/*if(x1>x2)
		{
			double temp = x1;
			x1 = x2;
			x2 = temp;
			temp = y1;
			y1 = y2;
			y2 = temp;
		}
		boolean hitBack = false;
		double m1 = (y2-y1)/(x2-x1);
		double b1 = y1-(m1*x1);
		double circM;
		double circB;
		double tempX;
		double tempY;
		if(x1 < 0 || x1 > levelWidth || y1 < 0 || y1 > levelHeight)
		{
			hitBack = true;
		}
		if(x2 < 0 || x2 > levelWidth || y2 < 0 || y2 > levelHeight)
		{
			hitBack = true;
		}
			for(int i = 0; i < currentRectangle; i++)
			{
				if(!hitBack)
				{
					//Right and left Checks
					if(x1 < oRectX1[i] && oRectX1[i] < x2)
					{
						tempY = (m1*oRectX1[i])+b1;
						if((y1 < tempY && tempY < y2) || (y2 > tempY && tempY > y2))
						{
							hitBack = true;
						}
					}
					if(x1 < oRectX2[i] && oRectX2[i] < x2)
					{
						tempY = (m1*oRectX2[i])+b1;
						if((y1 < tempY && tempY < y2) || (y2 > tempY && tempY > y2))
						{
							hitBack = true;
						}
					}
					//Top and Bottom checks
					if((y1 < oRectY1[i] && oRectY1[i] < y2) || (y1 > oRectY1[i] && oRectY1[i] > y2))
					{
						tempX = (m1*oRectY1[i])+b1;
						if(x1 < tempX && tempX < x2)
						{
							hitBack = true;
						}
					}
					if((y1 < oRectY2[i] && oRectY2[i] < y2) || (y1 > oRectY2[i] && oRectY2[i] > y2))
					{
						tempX = (m1*oRectY2[i])+b1;
						if(x1 < tempX && tempX < x2)
						{
							hitBack = true;
						}
					}
				}
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
						if(Math.sqrt(Math.pow(tempX-oCircX[i],2)+Math.pow(tempY-oCircY[i],2))<oCircRadius[i])
						{
							hitBack = true;
						}
					}
				}
		}
		return hitBack;*/
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
					if(Math.pow(X - oCircX[i], 2) + Math.pow(Y - oCircY[i], 2) < Math.pow(oCircRadius[i], 2))
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
}