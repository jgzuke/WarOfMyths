
/** Controls running of battle, calls objects frameCalls, draws and handles all objects, edge hit detection
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
	protected int difficultyLevel = 10;
	private double difficultyLevelMultiplier;
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
	private PowerBall[] powerBalls = new PowerBall[30];
	private PowerBallAOE[] powerBallAOEs = new PowerBallAOE[30];
	private Wall_Rectangle[] walls = new Wall_Rectangle[30];
	private Wall_Circle[] wallCircles = new Wall_Circle[30];
	private Wall_Ring[] wallRings = new Wall_Ring[30];
	private Rect aoeRect = new Rect();
	protected Player player;
	protected Context context;
	protected ImageLibrary imageLibrary;
	private Random randomGenerator = new Random();
	protected int playerType = 0;
	private int curXShift;
	private int curYShift;
	private int warningTimer;
	private String warningText = "";
	protected int levelWidth = 300;
	protected int levelHeight = 300;
	protected int xShiftLevel;
	protected int yShiftLevel;
	private int wallsMade = 0;
	private int wallCirclesMade = 0;
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
	private int[] checkedX = {0};
	private int[] checkedY = {0};
	private int currentCircle = 0;
	private int currentRectangle = 0;
	private int currentCircleAll = 0;
	private int currentRectangleAll = 0;
	private int currentRing = 0;
	private int currentRingAll = 0;
	private int currentPassage = 0;
	private int[][] exits = new int[1][4];
	private Bitmap background;
	protected Bitmap tempPicture;
	protected Bitmap tempPictureLock;
	protected PlayerGestureDetector detect;
	protected boolean gamePaused = false;
	private Handler mHandler = new Handler();
	protected String currentPause;
	private Bitmap check;
	protected String buyingItem;
	protected boolean drainHp = false;
	protected boolean lowerHp = false;
	protected boolean limitSpells = false;
	protected boolean enemyRegen = false;
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
	protected int numEnemies = 0;
	protected int[][] horizontals = new int[6][5];
	protected int[][] verticals = new int[5][6];
	protected int[][] tileType = new int[6][6];
	protected int horSpots = 0;
	protected int verSpots = 0;
	protected int levelsTravelled = 0;
	private Bitmap backgroundImageTop = Bitmap.createBitmap(300, 300, Config.ARGB_8888);
	private Bitmap backgroundImageBottom = Bitmap.createBitmap(300, 300, Config.ARGB_8888);
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
		changeDifficulty(10);
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
		changePlayerType(0);
		frameCaller.run();
		check = imageLibrary.loadImage("menu_check", 20, 20);
		changePlayOptions();
		changePlayerType(activity.playerType);
		restartGame();
	}
	/**
	 * ends a round of fighting and resets variables
	 */
	protected void restartGame()
	{
		changeDifficulty(10);
		levelsTravelled = 0;
		player.resetVariables(); // resets players variables
		resetLevel();
		playerHit = -11;
		activity.saveGame(); // saves game in case phone shuts down etc.
	}
	/**
	 * ends a round of fighting and resets variables
	 */
	protected void resetLevel()
	{
		enemies = new Enemy[30];
		structures = new Structure[30];
		powerBalls = new PowerBall[30];
		powerBallAOEs = new PowerBallAOE[30];
		powerUps = new PowerUp[30];
		powerUps = new PowerUp[30];
		numEnemies = 0;
		for(int i = 0; i < enemies.length; i++) enemies[i]=null;
		generateLevel();
	}
	protected int getRandomSpotX()
	{
		return (getRandomInt(horSpots)*50)+25;
	}
	protected int getRandomSpotY()
	{
		return (getRandomInt(verSpots)*50)+25;
	}
	protected int enemyX()
	{
		int pos = (getRandomInt(horSpots)*50)+25;
		while(Math.abs(pos-player.x)<90)
		{
			pos = (getRandomInt(horSpots)*50)+25;
		}
		return pos;
	}
	protected int enemyY()
	{
		int pos = (getRandomInt(verSpots)*50)+25;
		while(Math.abs(pos-player.y)<90)
		{
			pos = (getRandomInt(verSpots)*50)+25;
		}
		return pos;
	}
	private boolean wallBuiltH(int x, int y)
	{
		if(x < 0 || x > horSpots-1 || y < 0 || y > verSpots-2)
		{
			return true;
		} else if(horizontals[x][y]==1||horizontals[x][y]==2)
		{
			return true;
		} else
		{
			return false;
		}
	}
	private boolean wallBuiltV(int x, int y)
	{
		if(x < 0 || x > horSpots-2 || y < 0 || y > verSpots-1)
		{
			return true;
		} else if(verticals[x][y]==1||verticals[x][y]==2)
		{
			return true;
		} else
		{
			return false;
		}
	}
	private boolean wallOpenH(int x, int y)
	{
		if(x < 0 || x > horSpots-1 || y < 0 || y > verSpots-2)
		{
			return false;
		} else if(horizontals[x][y]==1)
		{
			return false;
		} else
		{
			return true;
		}
	}
	private boolean wallOpenV(int x, int y)
	{
		if(x < 0 || x > horSpots-2 || y < 0 || y > verSpots-1)
		{
			return false;
		} else if(verticals[x][y]==1)
		{
			return false;
		} else
		{
			return true;
		}
	}
	private void growSides(int x, int y, boolean max, boolean growing)
	{
		byte openSides = 4;
		boolean[] sides = new boolean[4]; // top, bottom, left, right
		sides[0] = (wallBuiltH(x, y-1));
		sides[1] = (wallBuiltH(x, y));
		sides[2] = (wallBuiltV(x-1, y));
		sides[3] = (wallBuiltV(x, y));
		for(int i = 0; i < 4; i ++) if(sides[i]) openSides--;
		int toBuild = 0;
		if(openSides>0)
		{
			if(max)
			{
				toBuild = openSides-1;
			} else
			{
				toBuild = openSides-1;//getRandomInt(openSides);
				int alternate = getRandomInt(openSides);
				if(alternate > toBuild) toBuild = alternate;
			}
		}
		while(toBuild>0)
		{
			int test = getRandomInt(4);
			if(sides[test]==false)
			{
				switch(test)
				{
					case 0:
						if(horizontals[x][y-1]==0)
						{
							horizontals[x][y-1]=1;
							toBuild--;
						}
						break;
					case 1:
						if(horizontals[x][y]==0)
						{
							horizontals[x][y]=1;
							toBuild--;
						}
						break;
					case 2:
						if(verticals[x-1][y]==0)
						{
							verticals[x-1][y]=1;
							toBuild--;
						}
						break;
					case 3:
						if(verticals[x][y]==0)
						{
							verticals[x][y]=1;
							toBuild--;
						}
						break;
				}
			}
		}
		for(int i = 0; i < 4; i ++)
		{
			if(sides[i]==false)
			{
				switch(i)
				{
					case 0: if(horizontals[x][y-1]!=1) horizontals[x][y-1]=2;
						break;
					case 1: if(horizontals[x][y]!=1) horizontals[x][y]=2;
						break;
					case 2: if(verticals[x-1][y]!=1) verticals[x-1][y]=2;
						break;
					case 3: if(verticals[x][y]!=1) verticals[x][y]=2;
						break;
				}
			}
		}
		if(growing)
		{
			for(int i = 0; i < 4; i ++) if(!sides[i])
			{
				switch(i)
				{
					case 0: growSides(x,y-1, false, true);
					case 1: growSides(x,y+1, false, true);
					case 2: growSides(x-1,y, false, true);
					case 3: growSides(x+1,y, false, true);
				}
			}		
		}
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawWallRotated(Bitmap section, int rotation, int x, int y, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-section.getWidth() / 2, -section.getHeight() / 2);
		rotateImages.postRotate((float) rotation);
		rotateImages.postTranslate((float) x, (float) y);
		g.drawBitmap(section, rotateImages, paint);
		section = null;
	}
	private boolean checkPath(int x, int y, boolean first)
	{
		if(first)
		{
			checkedX = new int[1];
			checkedY = new int[1];
			checkedX[0]=x;
			checkedY[0]=y;
		} else
		{
			for(int i = 0; i < checkedX.length; i++)
			{
				if(x==checkedX[i]&&y==checkedY[i]) return false;
			}
			int[]newCheckedX = new int[checkedX.length+1];
			int[]newCheckedY = new int[checkedX.length+1];
			for(int i = 0; i < checkedX.length; i++)
			{
				newCheckedX[i]=checkedX[i];
				newCheckedY[i]=checkedY[i];
			}
			newCheckedX[checkedX.length]=x;
			newCheckedY[checkedX.length]=y;
			checkedX = newCheckedX;
			checkedY = newCheckedY;
		}
		boolean onExit = false;
		for(int i = 0; i < exits.length; i++)
		{
			if(x==exits[i][2]&&y==exits[i][3])
			{
				onExit = true;
			}
		}
		if(onExit)
		{
			return true;
		} else		
		{
			boolean[] sides = new boolean[4]; // top, bottom, left, right
			sides[0] = (wallOpenH(x, y-1));
			sides[1] = (wallOpenH(x, y));
			sides[2] = (wallOpenV(x-1, y));
			sides[3] = (wallOpenV(x, y));
			return( sides[0]&&checkPath(x, y-1, false) || 
					sides[1]&&checkPath(x, y+1, false) || 
					sides[2]&&checkPath(x-1, y, false) || 
					sides[3]&&checkPath(x+1, y, false));
		}		
	}
	private void buildSides()
	{
		Canvas g = new Canvas(backgroundImageTop);
		for(int i = 0; i < horSpots; i++)
		{
			for(int j = 0; j < verSpots-1; j++)
			{
				if(horizontals[i][j]==1)
				{
					drawWallRotated(imageLibrary.wallStraight[0], 0, 25+(50*i), 50+(50*j), g);
					walls[wallsMade] = makeWall_Rectangle(-10+(50*i), 40+(50*j), 70, 20, true, true);
					wallsMade++;
				}
				if(horizontals[i][j]==2)
				{
					//g.drawCircle(25+(50*i), 50+(50*j), 5, paint);
				}
			}
		}
		for(int i = 0; i < horSpots-1; i++)
		{
			for(int j = 0; j < verSpots; j++)
			{
				if(verticals[i][j]==1)
				{
					drawWallRotated(imageLibrary.wallStraight[0], 90, 50+(50*i), 25+(50*j), g);
					walls[wallsMade] = makeWall_Rectangle(40+(50*i), -10+(50*j), 20, 70, true, true);
					wallsMade++;
				}
				if(verticals[i][j]==2)
				{
					//g.drawCircle(50+(50*i), 25+(50*j), 5, paint);
				}
			}
		}
		for(int i = 0; i < horSpots-1; i++)
		{
			for(int j = 0; j < verSpots-1; j++)
			{
				int numAttached = 0;
				if(verticals[i][j]==1) numAttached++;
				if(verticals[i][j+1]==1) numAttached++;
				if(horizontals[i][j]==1) numAttached++;
				if(horizontals[i+1][j]==1) numAttached++;
				if(numAttached == 1 || numAttached == 2) drawBitmapLevel(imageLibrary.wallPoint[0], 38+(50*i), 38+(50*j), g);
			}
		}
		for(int i = 0; i < horSpots; i++)
		{
			drawWallRotated(imageLibrary.wallStraight[0], 0, 25+(50*i), 0, g);
			drawWallRotated(imageLibrary.wallStraight[0], 0, 25+(50*i), verSpots*50, g);
		}
		for(int i = 0; i < verSpots; i++)
		{
			drawWallRotated(imageLibrary.wallStraight[0], 90, 0, 25+(50*i), g);
			drawWallRotated(imageLibrary.wallStraight[0], 90, horSpots*50, 25+(50*i), g);
		}
	}
	private int returnSmaller(int x, int y)
	{
		if(x<y) return x;
		else return y;
	}
	private void buildObject(int x, int y, int rotation, String name, Canvas g)
	{
		if(name == "pillar")
		{
			drawWallRotated(imageLibrary.objectPillar[0], rotation, 25+(50*x), 25+(50*y), g);
			wallCircles[wallCirclesMade] = makeWall_Circle(25+(50*x), 25+(50*y), 15, 1, true);
			wallCirclesMade++;
		}
		if(name == "table")
		{
			drawWallRotated(imageLibrary.objectTable[0], rotation, 25+(50*x), 25+(50*y), g);
			if(rotation==0)
			{
				walls[wallsMade] = makeWall_Rectangle((50*x)-13, (50*y), 75, 50, true, true);
			} else
			{
				walls[wallsMade] = makeWall_Rectangle((50*x), (50*y)-13, 50, 75, true, true);
			}
			wallsMade++;
		}
	}
	private void buildRoom()
	{
		Canvas g = new Canvas(backgroundImageTop);
		Canvas h = new Canvas(backgroundImageBottom);
		int roomWidth = getRandomInt(horSpots-3)+2;
		int roomHeight = getRandomInt(verSpots-4)+2;
		if(Math.random()>0.5)
		{
			roomWidth = getRandomInt(horSpots-4)+2;
			roomHeight = getRandomInt(verSpots-3)+2;
		}
		Log.e("game", "width"+Integer.toString(roomWidth));
		Log.e("game", "height"+Integer.toString(roomHeight));
		int topmost = getRandomInt(verSpots-roomHeight+1);
		int leftmost = getRandomInt(horSpots-roomWidth+1);
		boolean spotTaken = false;
		for(int i = leftmost; i < leftmost+roomWidth; i++)
		{
			for(int j = topmost; j < topmost+roomHeight; j++)
			{
				if(tileType[i][j]==1) spotTaken = true;
			}
		}
		if(!spotTaken)
		{
			for(int i = leftmost; i < leftmost+roomWidth; i++)
			{
				for(int j = topmost; j < topmost+roomHeight; j++)
				{
					tileType[i][j]=1;
					int overlayLevel = returnSmaller(4, returnSmaller(returnSmaller(Math.abs(leftmost-i), Math.abs(leftmost+roomWidth-i-1)), returnSmaller(Math.abs(topmost-j), Math.abs(topmost+roomHeight-j-1))));
					drawBitmapLevel(imageLibrary.levelOverlays[(overlayLevel*3)+getRandomInt(3)], (50*i), (50*j), h);
				}
			}
			for(int i = leftmost; i < leftmost+roomWidth; i++)
			{
				for(int j = topmost; j < topmost+roomHeight-1; j++)
				{
					horizontals[i][j]=2;
				}
			}
			for(int i = leftmost; i < leftmost+roomWidth-1; i++)
			{
				for(int j = topmost; j < topmost+roomHeight; j++)
				{
					verticals[i][j]=2;
				}
			}
			if(roomWidth>2&&roomHeight>2)
			{
				if(Math.random()>0.5)
				{
					for(int i = 0; i < 1+((roomWidth-2)*(roomHeight-2)/4); i++)
					{
						int X = getRandomInt(roomWidth-2)+leftmost+1;
						int Y = getRandomInt(roomHeight-2)+topmost+1;
						if(tileType[X][Y]==1)
						{
							tileType[X][Y]=3;
							int Rot = 0;
							if(roomHeight>roomWidth) Rot=90;
							buildObject(X, Y, Rot, "table", g);
						}
					}
				} else
				{
					if(roomWidth>4||roomHeight>4)
					{
						for(int i = leftmost+1; i<leftmost+roomWidth-1;i+=2)
						{
							for(int j = topmost+1; j<topmost+roomHeight-1;j+=2)
							{
								buildObject(i, j, 0, "pillar", g);
							}
						}
					}
				}
			}
		}
	}
	/**
	 * loads a new level, creates walls enemies etc.
	 */
	protected void generateLevel()
	{
//Log.e("game","1");
		wallsMade = 0;
		wallCirclesMade = 0;
		wallCircles = new Wall_Circle[30];
		wallRings = new Wall_Ring[30];
		currentCircle = 0;
		currentRectangle = 0;
		currentCircleAll = 0;
		currentRectangleAll = 0;
		currentRing = 0;
		currentRingAll = 0;
		currentPassage = 0;
		if(levelsTravelled>0)
		{
			horSpots = 6+getRandomInt(levelsTravelled);
			verSpots = 6+getRandomInt(levelsTravelled);	
		} else
		{
			horSpots = 6;
			verSpots = 6;
		}
		createWallRectangleValueArrays((horSpots*verSpots)+20);
		createWallRectangleValueArraysAll((horSpots*verSpots)+20);
		createWallCircleValueArrays(40);
		createWallCircleValueArraysAll(40);
		walls = new Wall_Rectangle[(horSpots*verSpots)+20];
		horizontals = new int[horSpots][verSpots-1];
		verticals = new int[horSpots-1][verSpots];
		tileType = new int[horSpots][verSpots];
		levelWidth = 50*horSpots; // height of level
		levelHeight = 50*verSpots; // width of level
		backgroundImageTop = Bitmap.createBitmap(levelWidth, levelHeight, Config.ARGB_8888);
		backgroundImageBottom = Bitmap.createBitmap(levelWidth, levelHeight, Config.ARGB_8888);
		exits = new int[Math.round(horSpots*verSpots/15)][4];
		//Log.e("game","2");
		for(int i = 0; i < getRandomInt(3)+(levelsTravelled/2); i++)
		{
			buildRoom();
		}
		//Log.e("game","3");
		int playerX = getRandomInt(horSpots-2)+1;
		int playerY = getRandomInt(verSpots-2)+1;
		while(tileType[playerX][playerY]!=0)
		{
			playerX = getRandomInt(horSpots-2)+1;
			playerY = getRandomInt(verSpots-2)+1;
		}
		for(int i = 0; i < exits.length; i++)
		{
			boolean toClose = true;
			int counter = 0;
			while(toClose)
			{
				counter++;
				exits[i][2] = getRandomInt(horSpots);
				exits[i][3] = getRandomInt(verSpots);
				toClose = false;
				if(Math.abs(exits[i][2]-playerX)+Math.abs(exits[i][3]-playerY)<4||tileType[exits[i][2]][exits[i][3]]!=0)
				{
					toClose = true;
				} else
				{
					for(int j = 0; j < i; j++)
					{
						if(Math.abs(exits[i][2]-exits[j][2])+Math.abs(exits[i][3]-exits[j][3])<4)
						{
							toClose = true;
						}
					}
				}
				if(toClose&&counter>40)
				{
					if(i>0)
					{
						exits[i][2] = exits[i-1][2];
						exits[i][3] = exits[i-1][3];
					} else
					{
						exits[i][2] = getRandomInt(horSpots);
						exits[i][3] = getRandomInt(verSpots);
					}
					toClose = false;
				}
			}
			exits[i][0] = (exits[i][2]*50)+25;
			exits[i][1]	= (exits[i][3]*50)+25;
		}
		//Log.e("game","6");
		player.x = (playerX*50)+25;
		player.y = (playerY*50)+25;
		//Log.e("game","7");
		if(playerX>0) verticals[playerX-1][playerY]=1;
		if(playerX<horSpots-1) verticals[playerX][playerY]=1;
		if(playerY>0) horizontals[playerX][playerY-1]=1;
		if(playerY<verSpots-1) horizontals[playerX][playerY]=1;
		for(int i = 0; i < horSpots-1; i++)
		{
			if(Math.random()>0.7) verticals[i][0]=2;
			if(Math.random()>0.7) verticals[i][verSpots-1]=2;
		}
		for(int i = 0; i < verSpots-1; i++)
		{
			if(Math.random()>0.7) horizontals[0][i]=2;
			if(Math.random()>0.7) horizontals[horSpots-1][i]=2;
		}
		//Log.e("game","8");
		if(Math.random()>0.5)
		{
			if(playerX>horSpots/2)
			{
				verticals[playerX-1][playerY]=2;
				verticals[playerX-2][playerY]=1;
				growSides(playerX-1, playerY, false, true);
			} else
			{
				verticals[playerX][playerY]=2;
				verticals[playerX+1][playerY]=1;
				growSides(playerX+1, playerY, false, true);
			}
		} else
		{
			if(playerY>verSpots/2)
			{
				horizontals[playerX][playerY-1]=2;
				horizontals[playerX][playerY-2]=1;
				growSides(playerX, playerY-1, false, true);
			} else
			{
				horizontals[playerX][playerY]=2;
				horizontals[playerX][playerY+1]=1;
				growSides(playerX, playerY+1, false, true);
			}
		}
		//Log.e("game","9");
		for(int i = 0; i < 20; i++) growSides(getRandomInt(horSpots),getRandomInt(verSpots),false, true);
		buildSides();
		//Log.e("game","10");
		imageLibrary.loadLevel();
		//Log.e("game","101");
		if(!checkPath(playerX, playerY, true))
		{
			generateLevel();
		} else
		{
			//Log.e("game","11");
			if(true)//decide section type
			{
				//Log.e("game","12");
				imageLibrary.changeArrayLoaded("shield", "orc_swordsman");
				imageLibrary.changeArrayLoaded("rogue", "goblin_rogue");
				imageLibrary.changeArrayLoaded("archer", "orc_archer");
				imageLibrary.changeArrayLoaded("pikeman", "orc_pikeman");
				imageLibrary.changeArrayLoaded("mage", "goblin_mage");
				for(int i = 0; i < horSpots; i++)
				{
					for(int j = 0; j< verSpots; j++)
					{
						if(tileType[i][j]==0&&!(i==playerX&&j==playerY))
						{
							boolean spotTaken = false;
							for(int k = 0; k < exits.length; k++)
							{
								if(exits[k][2]==i&&exits[k][3]==j)
								{
									spotTaken = true;
								}
							}
							if(!spotTaken&&getRandomInt(4)==0)
							{
								double enemytype = Math.random();
								if(enemytype<0.1)
								{
									enemies[numEnemies] = new Enemy_Mage(this, (i*50)+getRandomInt(50), (j*50)+getRandomInt(50));
									numEnemies++;
								} else if(enemytype<0.2)
								{
									enemies[numEnemies] = new Enemy_Rogue(this, (i*50)+getRandomInt(50), (j*50)+getRandomInt(50));
									numEnemies++;
								} else if(enemytype<0.4)
								{
									enemies[numEnemies] = new Enemy_Archer(this, (i*50)+getRandomInt(50), (j*50)+getRandomInt(50));
									numEnemies++;
								} else if(enemytype<0.7)
								{
									enemies[numEnemies] = new Enemy_Pikeman(this, (i*50)+getRandomInt(50), (j*50)+getRandomInt(50));
									numEnemies++;
								} else
								{
									enemies[numEnemies] = new Enemy_Shield(this, (i*50)+getRandomInt(50), (j*50)+getRandomInt(50));
									numEnemies++;
								}
							}
						}
					}
				}
			}
			//Log.e("game","13");
		}
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
				drawBitmapLevel(imageLibrary.toTile, w, h, g);
				h += 50;
			}
			w += 50;
			h = 0;
		}
		drawBitmapLevel(backgroundImageBottom, 0, 0, g);
		for(int i = 0; i < exits.length; i++)
		{
			drawBitmapLevel(imageLibrary.exitFightPortal, exits[i][0] - 15, exits[i][1] - 15, g);
		}
		for(int i = 0; i < structures.length; i++)
		{
			if(structures[i] != null)
			{
				drawBitmapLevel(imageLibrary.structure_Spawn, (int)structures[i].x-structures[i].width, (int)structures[i].y-structures[i].height, g);
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
		for(int i = 0; i < enemies.length; i++)
		{
			if(enemies[i] != null)
			{
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
				aoeRect.top = (int)(powerBallAOEs[i].y - (powerBallAOEs[i].getHeight() / 2.5));
				aoeRect.bottom = (int)(powerBallAOEs[i].y + (powerBallAOEs[i].getHeight() / 2.5));
				aoeRect.left = (int)(powerBallAOEs[i].x - (powerBallAOEs[i].getWidth() / 2.5));
				aoeRect.right = (int)(powerBallAOEs[i].x + (powerBallAOEs[i].getWidth() / 2.5));
				paint.setAlpha(powerBallAOEs[i].getAlpha());
				drawBitmapRectLevel(powerBallAOEs[i].getVisualImage(), aoeRect, g);
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
		if(player.powerUpTimer > 0)
		{
			drawBitmapLevel(imageLibrary.effects[player.powerID - 1], (int) player.x - 30, (int) player.y - 30, g);
		}
		drawBitmapLevel(backgroundImageTop, 0, 0, g);
		drawHealthBars(g);
		return drawTo;
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
		if(activity.stickOnRight)
		{
			drawBitmap(imageLibrary.loadImage("screen000" + Integer.toString(playerType + 5), 480, 320), 0, 0, g);
		}
		else
		{
			drawBitmap(imageLibrary.loadImage("screen000" + Integer.toString(playerType + 1), 480, 320), 0, 0, g);
		}
		return drawTo;
	}
	/**
	 * Sets deleted objects to null to be gc'd and tests player and enemy hitting arena bounds
	 */
	protected void frameCall()
	{
		playerHit++;
		playerBursted++;
		for(int i = 0; i < powerBalls.length; i++)
		{
			if(powerBalls[i] != null)
			{
				if(powerBalls[i].deleted)
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
				if(powerBallAOEs[i].deleted)
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
					structures[i].frameCall();
				}
			}
		}
		player.frameCall();
		if(player.x < 10) player.x = (10);
		if(player.x > levelWidth - 10) player.x = (levelWidth - 10);
		if(player.y < 10) player.y = (10);
		if(player.y > levelHeight - 10) player.y = (levelHeight - 10);
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
		for(int i = 0; i < exits.length; i++)
		{
			if(getDistance(player.x, player.y, exits[i][0], exits[i][1])<20)
			{
				levelsTravelled ++;
				if(difficultyLevel>0)
				{
					changeDifficulty(difficultyLevel-1);
				}
				resetLevel();
			}
		}
		invalidate();
		activity.resetVolume();
	}
	/**
	 * changes visuals and behavior when player changes type
	 * @param playerTypeSet new player type
	 */
	protected void changePlayerType(int playerTypeSet)
	{
		playerType = playerTypeSet;
		player.humanType = playerType;
		imageLibrary.loadPlayerPowerBall(); // loads powerball image
		changePlayerType(); // loads images etc.
		background = drawStart(); // redaws player screen
		activity.saveGame(); // saves game state in case of interuption
	}
	/**
	 * changes look of background when options are changed
	 */
	protected void changePlayOptions()
	{
		detect.getSide(); // changes which side joystick is on
		if(activity.stickOnRight)
		{
			shootStick.x = 53;
			shootStick.y = 268;
		}
		else
		{
			shootStick.x = 426;
			shootStick.y = 268;
		}
		background = drawStart(); // redraws play screen
		invalidate();
		activity.saveGame(); // saves game state in case of interuption
	}
	/**
	 * changes player behavior when player changes type
	 */
	protected void changePlayerType()
	{
		switch(playerType)
		{
		case 0://fire
			player.spChangeForType = (double) activity.wApollo / 10*0.8;
			shootStick.visualImage = imageLibrary.loadImage("shootfire", 70, 35);
			break;
		case 1://water
			player.spChangeForType = (double) activity.wPoseidon / 10*1.5;
			shootStick.visualImage = imageLibrary.loadImage("shootwater", 70, 35);
			break;
		case 2://electric
			player.spChangeForType = (double) activity.wZues / 10*1.1;
			shootStick.visualImage = imageLibrary.loadImage("shootelectric", 70, 35);
			break;
		case 3://earth
			player.spChangeForType = (double) activity.wHades / 10*1.3;
			shootStick.visualImage = imageLibrary.loadImage("shootearth", 70, 35);
			break;
		}
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
		case 2:
			enemies[index] = new Enemy_Pikeman(this, info[1], info[2]); // creates pikeman in alternate level section
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
		if(activity.stickOnRight) fix = 0;
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
			drawRect(12 + fix, 300, 12 + fix + (int)((66 * player.getAbilityTimer_powerBall()) / (91+(activity.bReserve*20))), 310, g);
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
	 * draw choose deity screen
	 * @param g canvas to draw to
	 */
	protected void drawChooseGod(Canvas g)
	{
		drawBehindPause(g);
		drawBitmap(imageLibrary.loadImage("menu_choosegod", 480, 320), 0, 0, g);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(8);
		paint.setColor(highlightChoiceColor);
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
		paint.setStrokeWidth(0);
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
		if(!activity.stickOnRight)
		{
			drawBitmap(imageLibrary.loadImage("menu_pauseback0001", 480, 320), 0, 0, g);
		}
		else
		{
			drawBitmap(imageLibrary.loadImage("menu_pauseback0002", 480, 320), 0, 0, g);
		}
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
				else if(currentPause.equals("buy"))
				{
					drawBuy(g);
				}
				else if(currentPause.equals("chooseGod"))
				{
					drawChooseGod(g);
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
			paint.setColor(godColors[player.humanType]);
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
			if(activity.stickOnRight)
			{
				drawBitmap(imageLibrary.powerUpBigs[player.powerID - 1], 400, 25, g);
			}
			else
			{
				drawBitmap(imageLibrary.powerUpBigs[player.powerID - 1], 10, 25, g);
			}
		}
	}
	/**
	 * creates an enemy power ball
	 * @param rotation rotation of powerball
	 * @param xVel horizontal velocity of ball
	 * @param yVel vertical velocity of ball
	 * @param power power of ball
	 * @param x x position
	 * @param y y position
	 */
	protected void createPowerBallEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		PowerBall_Enemy ballEnemy = new PowerBall_Enemy(this, (int) (x+xVel*2), (int) (y+yVel*2), power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballEnemy;
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
		powerBalls[lowestPositionEmpty(powerBalls)] = boltEnemy;
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
	protected void createPowerBallPlayer(double rotation, double Vel, int power, double x, double y)
	{
		PowerBall_Player ballPlayer = new PowerBall_Player(this, (int)x, (int)y, power, Vel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballPlayer;
	}
	/**
	 * creates an emeny AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 * @param damaging whether it damages player
	 */
	protected void createPowerBallEnemyAOE(double x, double y, double power, boolean damaging)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power, true);
		if(!damaging) ballAOEEnemy.damaging = false;
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	/**
	 * creates a player AOE explosion
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createPowerBallPlayerAOE(double x, double y, double power, boolean damaging)
	{
		PowerBallAOE_Player ballAOEPlayer = new PowerBallAOE_Player(this, (int) x, (int) y, power, true);
		if(!damaging) ballAOEPlayer.damaging = false;
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
	}
	/**
	 * creates an enemy burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createPowerBallEnemyBurst(double x, double y, double power)
	{
		PowerBallAOE_Enemy ballAOEEnemy = new PowerBallAOE_Enemy(this, (int) x, (int) y, power, false);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	/**
	 * creates a player burst
	 * @param x x position
	 * @param y y position
	 * @param power power of explosion
	 */
	protected void createPowerBallPlayerBurst(double x, double y, double power)
	{
		PowerBallAOE_Player ballAOEPlayer = new PowerBallAOE_Player(this, (int) x, (int) y, power, false);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
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
		if(m1>50)m1=50;
		if(m1<-50)m1=-50;
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
		g.drawBitmap(picture, x, y, paint);
	}
	/**
	 * Replaces canvas.drawBitmap(Bitmap, Matrix, Paint) and auto scales and rotates image based on drawnSprite values
	 */
	protected void drawBitmapRotatedLevel(DrawnSprite sprite, Canvas g)
	{
		int width = sprite.getVisualImage().getWidth();
		int height = sprite.getVisualImage().getHeight();
			rotateImages.reset();
			rotateImages.postTranslate(-width / 2, -height / 2);
			rotateImages.postRotate((float) sprite.rotation);
			rotateImages.postTranslate((float) sprite.x, (float) sprite.y);
			g.drawBitmap(sprite.getVisualImage(), rotateImages, paint);
			sprite = null;
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
	 * Replaces canvas.drawBitmap(Bitmap, Rect, Rect, Paint) and auto scales
	 */
	protected void drawBitmapRectLevel(Bitmap picture, Rect rectangle, Canvas g)
	{
			g.drawBitmap(picture, null, rectangle, paint);
	}
	/**
	 * Replaces canvas.drawText(String, int, int, Paint) and auto scales
	 */
	protected void drawText(String text, int x, int y, Canvas g)
	{
		// TODO
		g.drawText(text, x, y, paint);
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
}