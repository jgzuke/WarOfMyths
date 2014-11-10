package com.example.magegame;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;
import java.util.Random;
public final class Controller extends View
{	
	private int playerColour;
	private int enemyColour;
	private double screenDimensionMultiplier;
	private int screenMinX;
	private int screenMinY;
	public int DifficultyLevel;
	public double DifficultyLevelMultiplier;
	public int EnemyType;
	public int PlayerType;
	private int wallWidth = 10;
	public int LevelNum;
	public Player player;
	public enemy_Mage enemy;
	public enemy_Muggle[] enemies = new enemy_Muggle[30];
	private powerBall[] powerBalls = new powerBall[30];
	private powerBallAOE[] powerBallAOEs = new powerBallAOE[30];
	private graphic_Teleport[] graphic_Teleport = new graphic_Teleport[30];
	public SpGraphic[] spGraphic = new SpGraphic[30];
        public wall[] walls = new wall[30];
	public SpControl spGraphicEnemy;
	public SpControl spGraphicPlayer;
	public int[] obstaclesRectanglesX1;
	public int[] obstaclesRectanglesX2;
	public int[] obstaclesRectanglesY1;
	public int[] obstaclesRectanglesY2;
	public int[] obstaclesCirclesX;
	public int[] obstaclesCirclesY;
	public int[] obstaclesCirclesRadius = new int[0];
	public int currentCircle = 0;
	public int currentRectangle = 0;
	public int[][] teleportSpots = new int[2][4];
	public Random randomGenerator;
	private int warningTimer;
	private int warningType;
	public Game game;
	public Context context;
        public boolean gameEnded = false;
        public Paint paint = new Paint();
        private Matrix rotateImages = new Matrix();
        private Rect aoeRect = new Rect();  
        private Handler mHandler = new Handler();
        private PlayerHandleMove handleMovement;
        private Runnable frameCaller = new Runnable()
        {
        	public void run()
        	{
        		frameCall();
        		mHandler.postDelayed(this, 50);
        	}
        };
	public Controller(Context startSet, Game gameSet, int PlayerTypeSet, int DifficultyLevelSet, int LevelNumSet, double screenDimensionMultiplierSet, int screenMinXSet, int screenMinYSet)
	{		
		super(startSet);
		screenDimensionMultiplier = screenDimensionMultiplierSet;
		screenMinX = screenMinXSet;
		screenMinY = screenMinYSet;
		game = gameSet;	
    	PlayerType = PlayerTypeSet;		
    	DifficultyLevel = DifficultyLevelSet;
    	LevelNum = LevelNumSet;
    	DifficultyLevelMultiplier = 20 / (double)(DifficultyLevel + 10);
		context = startSet;
		paint.setColor(Color.BLACK);		
		randomGenerator = new Random();
		EnemyType = randomGenerator.nextInt(4);
		spGraphicEnemy = new SpControl(this, false);
		spGraphicPlayer = new SpControl(this, true);
		player = new Player(this);
		player.isPlayer = true;
		//handleMovement = new PlayerHandleMove(this);
		enemy = new enemy_Mage(this);
		switch(EnemyType)
		{
		case 0:
			game.imageLibrary.powerBallAOE_Image[0] = game.imageLibrary.loadImage("powerballaoe0001");
			game.imageLibrary.powerBall_Image[0] = game.imageLibrary.loadArray1D(4, "powerball0001_");
			enemyColour = Color.rgb(255,0,0);
			break;
		case 1:
			game.imageLibrary.powerBallAOE_Image[1] = game.imageLibrary.loadImage("powerballaoe0002");
			game.imageLibrary.powerBall_Image[1] = game.imageLibrary.loadArray1D(4, "powerball0002_");
			enemyColour = Color.rgb(0,0,255);
			enemy.Mp = 2250;
			enemy.MpMax = 4500;
			break;
		case 2:
			game.imageLibrary.powerBallAOE_Image[2] = game.imageLibrary.loadImage("powerballaoe0003");
			game.imageLibrary.powerBall_Image[2] = game.imageLibrary.loadArray1D(4, "powerball0003_");
			enemyColour = Color.rgb(170,119,221);
			enemy.speedCur = 4;
			break;
		case 3:
			game.imageLibrary.powerBallAOE_Image[3] = game.imageLibrary.loadImage("powerballaoe0004");
			game.imageLibrary.powerBall_Image[3] = game.imageLibrary.loadArray1D(4, "powerball0004_");
			enemyColour = Color.rgb(102,51,0);
			enemy.Hp = 9000;
			enemy.HpMax = 9000;
			break;
		}
		switch(PlayerType)
		{
		case 0:
			game.imageLibrary.powerBallAOE_Image[0] = game.imageLibrary.loadImage("powerballaoe0001");
			game.imageLibrary.powerBall_Image[0] = game.imageLibrary.loadArray1D(4, "powerball0001_");
			playerColour = Color.rgb(255,0,0);
			break;
		case 1:
			game.imageLibrary.powerBallAOE_Image[1] = game.imageLibrary.loadImage("powerballaoe0002");
			game.imageLibrary.powerBall_Image[1] = game.imageLibrary.loadArray1D(4, "powerball0002_");
			playerColour = Color.rgb(0,0,255);
			player.Mp = 2250;
			player.MpMax = 4500;
			break;
		case 2:
			game.imageLibrary.powerBallAOE_Image[2] = game.imageLibrary.loadImage("powerballaoe0003");
			game.imageLibrary.powerBall_Image[2] = game.imageLibrary.loadArray1D(4, "powerball0003_");
			playerColour = Color.rgb(170,119,221);
			player.speedCur = 4;
			break;
		case 3:
			game.imageLibrary.powerBallAOE_Image[3] = game.imageLibrary.loadImage("powerballaoe0004");
			game.imageLibrary.powerBall_Image[3] = game.imageLibrary.loadArray1D(4, "powerball0004_");
			playerColour = Color.rgb(102,51,0);
			player.Hp = 9000;
			player.HpMax = 9000;
			break;
		}
		loadLevel();
		//game.imageLibrary.changeArrayLoaded("swordsman", true);
                for(int i = 0; i < 4; i ++)
                {
                    //enemies[i] = new enemy_Swordsman(this, teleportSpots[0][i], teleportSpots[1][i]);
                }
                frameCaller.run();
	}
	public void loadLevel()
	{
		if(LevelNum == 0)
		{
			obstaclesRectanglesX1 = new int[6];
			obstaclesRectanglesX2 = new int[6];
			obstaclesRectanglesY1 = new int[6];
			obstaclesRectanglesY2 = new int[6];
			obstaclesCirclesX = new int[0];
			obstaclesCirclesY = new int[0];
			obstaclesCirclesRadius = new int[0];
			teleportSpots[0][0] = 110;
			teleportSpots[0][1] = 370;
			teleportSpots[0][2] = 110;
			teleportSpots[0][3] = 370;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			wall_Rectangle wall1 = new wall_Rectangle(this, 90 - wallWidth, 220 + wallWidth, 50 - wallWidth, 60 + wallWidth);
                        walls[0] = wall1;
			wall_Rectangle wall2 = new wall_Rectangle(this, 260 - wallWidth, 390 + wallWidth, 50 - wallWidth, 60 + wallWidth);
                        walls[1] = wall2;
			wall_Rectangle wall3 = new wall_Rectangle(this, 90 - wallWidth, 220 + wallWidth, 260 - wallWidth, 270 + wallWidth);
                        walls[2] = wall3;
			wall_Rectangle wall4 = new wall_Rectangle(this, 260 - wallWidth, 390 + wallWidth, 260 - wallWidth, 270 + wallWidth);
                        walls[3] = wall4;
			wall_Rectangle wall5 = new wall_Rectangle(this, 130 - wallWidth, 140 + wallWidth, 100 - wallWidth, 220 + wallWidth);
                        walls[4] = wall5;
			wall_Rectangle wall6 = new wall_Rectangle(this, 340 - wallWidth, 350 + wallWidth, 100 - wallWidth, 220 + wallWidth);
                        walls[5] = wall6;
		}
		if(LevelNum == 1)
		{
			obstaclesRectanglesX1 = new int[6];
			obstaclesRectanglesX2 = new int[6];
			obstaclesRectanglesY1 = new int[6];
			obstaclesRectanglesY2 = new int[6];
			obstaclesCirclesX = new int[1];
			obstaclesCirclesY = new int[1];
			obstaclesCirclesRadius = new int[1];
			teleportSpots[0][0] = 110;
			teleportSpots[0][1] = 370;
			teleportSpots[0][2] = 110;
			teleportSpots[0][3] = 370;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			wall_Rectangle wall1 = new wall_Rectangle(this, 130 - wallWidth, 220 + wallWidth, 50 - wallWidth, 60 + wallWidth);
                        walls[0] = wall1;
			wall_Rectangle wall2 = new wall_Rectangle(this, 260 - wallWidth, 350 + wallWidth, 50 - wallWidth, 60 + wallWidth);
                        walls[1] = wall2;
			wall_Rectangle wall3 = new wall_Rectangle(this, 130 - wallWidth, 220 + wallWidth, 260 - wallWidth, 270 + wallWidth);
                        walls[2] = wall3;
			wall_Rectangle wall4 = new wall_Rectangle(this, 260 - wallWidth, 350 + wallWidth, 260 - wallWidth, 270 + wallWidth);
                        walls[3] = wall4;
			wall_Rectangle wall5 = new wall_Rectangle(this, 130 - wallWidth, 140 + wallWidth, 50 - wallWidth, 270 + wallWidth);
                        walls[4] = wall5;
			wall_Rectangle wall6 = new wall_Rectangle(this, 340 - wallWidth, 350 + wallWidth, 50 - wallWidth, 270 + wallWidth);
                        walls[5] = wall6;
			wall_Circle wall7 = new wall_Circle(this, 240, 160, 60);
                        walls[6] = wall7;
		}
		if(LevelNum == 2)
		{
			obstaclesRectanglesX1 = new int[6];
			obstaclesRectanglesX2 = new int[6];
			obstaclesRectanglesY1 = new int[6];
			obstaclesRectanglesY2 = new int[6];
			obstaclesCirclesX = new int[0];
			obstaclesCirclesY = new int[0];
			obstaclesCirclesRadius = new int[0];
			teleportSpots[0][0] = 160;
			teleportSpots[0][1] = 320;
			teleportSpots[0][2] = 160;
			teleportSpots[0][3] = 320;
			teleportSpots[1][0] = 110;
			teleportSpots[1][1] = 110;
			teleportSpots[1][2] = 210;
			teleportSpots[1][3] = 210;
			wall_Rectangle wall1 = new wall_Rectangle(this, 130 - wallWidth, 140 + wallWidth, 50 - wallWidth, 140 + wallWidth);
                        walls[0] = wall1;
			wall_Rectangle wall2 = new wall_Rectangle(this, 340 - wallWidth, 350 + wallWidth, 50 - wallWidth, 140 + wallWidth);
                        walls[1] = wall2;
			wall_Rectangle wall3 = new wall_Rectangle(this, 130 - wallWidth, 140 + wallWidth, 180 - wallWidth, 270 + wallWidth);
                        walls[2] = wall3;
			wall_Rectangle wall4 = new wall_Rectangle(this, 340 - wallWidth, 350 + wallWidth, 180 - wallWidth, 270 + wallWidth);
                        walls[3] = wall4;
			wall_Rectangle wall5 = new wall_Rectangle(this, 130 - wallWidth, 350 + wallWidth, 130 - wallWidth, 140 + wallWidth);
                        walls[4] = wall5;
			wall_Rectangle wall6 = new wall_Rectangle(this, 130 - wallWidth, 350 + wallWidth, 180 - wallWidth, 190 + wallWidth);
                        walls[5] = wall6;
		}
		if(LevelNum == 3)
		{
			obstaclesRectanglesX1 = new int[4];
			obstaclesRectanglesX2 = new int[4];
			obstaclesRectanglesY1 = new int[4];
			obstaclesRectanglesY2 = new int[4];
			obstaclesCirclesX = new int[0];
			obstaclesCirclesY = new int[0];
			obstaclesCirclesRadius = new int[0];
			teleportSpots[0][0] = 190;
			teleportSpots[0][1] = 290;
			teleportSpots[0][2] = 190;
			teleportSpots[0][3] = 290;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			wall_Rectangle wall1 = new wall_Rectangle(this, 185 - wallWidth, 195 + wallWidth, 50 - wallWidth, 270 + wallWidth);
                        walls[0] = wall1;
			wall_Rectangle wall2 = new wall_Rectangle(this, 235 - wallWidth, 245 + wallWidth, 10 - wallWidth, 140 + wallWidth);
                        walls[1] = wall2;
			wall_Rectangle wall3 = new wall_Rectangle(this, 235 - wallWidth, 245 + wallWidth, 180 - wallWidth, 310 + wallWidth);
                        walls[2] = wall3;
			wall_Rectangle wall4 = new wall_Rectangle(this, 285 - wallWidth, 295 + wallWidth, 50 - wallWidth, 270 + wallWidth);
                        walls[3] = wall4;
		}
		if(LevelNum == 4)
		{
			obstaclesRectanglesX1 = new int[4];
			obstaclesRectanglesX2 = new int[4];
			obstaclesRectanglesY1 = new int[4];
			obstaclesRectanglesY2 = new int[4];
			obstaclesCirclesX = new int[0];
			obstaclesCirclesY = new int[0];
			obstaclesCirclesRadius = new int[0];
			teleportSpots[0][0] = 215;
			teleportSpots[0][1] = 265;
			teleportSpots[0][2] = 215;
			teleportSpots[0][3] = 265;
			teleportSpots[1][0] = 30;
			teleportSpots[1][1] = 30;
			teleportSpots[1][2] = 290;
			teleportSpots[1][3] = 290;
			wall_Rectangle wall1 = new wall_Rectangle(this, 130 - wallWidth, 350 + wallWidth, 130 - wallWidth, 140 + wallWidth);
                        walls[0] = wall1;
			wall_Rectangle wall2 = new wall_Rectangle(this, 235 - wallWidth, 245 + wallWidth, 10 - wallWidth, 140 + wallWidth);
                        walls[1] = wall2;
			wall_Rectangle wall3 = new wall_Rectangle(this, 235 - wallWidth, 245 + wallWidth, 180 - wallWidth, 310 + wallWidth);
                        walls[2] = wall3;
			wall_Rectangle wall4 = new wall_Rectangle(this, 130 - wallWidth, 350 + wallWidth, 180 - wallWidth, 190 + wallWidth);
                        walls[3] = wall4;
		}
	}
	public void drawContestantStats(Canvas g)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.WHITE);
		drawRect(395, 240, 475, 316, g);
		drawRect(5, 240, 85, 316, g);
		paint.setColor(Color.RED);
		drawRect(405, 250, 405+(60 * player.Hp / player.HpMax), 266, g);		
		drawRect(15, 250, 15+(60 * enemy.Hp / enemy.HpMax), 266, g);
		paint.setColor(Color.BLUE);
		drawRect(405, 270, 405+(60 * player.Mp / player.MpMax), 286, g);		
		drawRect(15, 270, 15+(60 * enemy.Mp / enemy.MpMax), 286, g);
		paint.setColor(Color.GREEN);		
		drawRect(405, 290, 405+(60 * player.Sp / player.SpMax), 306, g);		
		drawRect(15, 290, 15+(60 * enemy.Sp / enemy.SpMax), 306, g);		
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		drawRect(405, 250, 465, 266, g);
		drawRect(405, 270, 465, 286, g);
		drawRect(405, 290, 465, 306, g);
		drawRect(15, 250, 75, 266, g);
		drawRect(15, 270, 75, 286, g);
		drawRect(15, 290, 75, 306, g);
		
		drawText(Integer.toString(player.Hp), 420, 263, g);
		drawText(Integer.toString(player.Mp), 420, 283, g);
		drawText(Integer.toString(player.Sp), 420, 303, g);
		drawText(Integer.toString(enemy.Hp), 30, 263, g);
		drawText(Integer.toString(enemy.Mp), 30, 283, g);
		drawText(Integer.toString(enemy.Sp), 30, 303, g);
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.YELLOW);
		drawRect(15, 137, 15+((60 * player.abilityTimer_roll) / 400), 147, g);
		drawRect(15, 202, 15+((60 * player.abilityTimer_teleport) / 350), 212, g);
		drawRect(405, 137, 405+((60 * player.abilityTimer_burst) / 500), 147, g);
		drawRect(190, 295, 190+((100 * player.abilityTimer_powerBall) / 90), 310, g);
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(Color.BLACK);
		drawRect(15, 137, 75, 147, g);
		drawRect(15, 202, 75, 212, g);
		drawRect(405, 137, 465, 147, g);
		drawRect(190, 295, 290, 310, g);
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL);
                drawRect((int)enemy.x - 20, (int)enemy.y - 30, (int)enemy.x - 20 + (40 * enemy.Hp/enemy.HpMax), (int)enemy.y - 20, g);   
                paint.setColor(Color.BLACK);
                paint.setStyle(Paint.Style.STROKE);
                drawRect((int)enemy.x - 20, (int)enemy.y - 30, (int)enemy.x + 20, (int)enemy.y - 20, g);
                for(int i = 0; i < enemies.length; i++)
                {
                    if(enemies[i] != null)
			{
                    	paint.setColor(Color.RED);
                    	paint.setStyle(Paint.Style.FILL);
                            drawRect((int)enemies[i].x - 20, (int)enemies[i].y - 30, (int)enemies[i].x - 20+(40 * enemies[i].Hp/enemies[i].HpMax), (int)enemies[i].y - 20, g);   
                            paint.setColor(Color.BLACK);
                            paint.setStyle(Paint.Style.STROKE);
                            drawRect((int)enemies[i].x - 20, (int)enemies[i].y - 30, (int)enemies[i].x + 20, (int)enemies[i].y - 20, g);
                        }
                }
	}
	public void frameCall()
	{
                for(int i = 0; i < walls.length; i++)
		{
			if(walls[i] != null)
			{				
                            walls[i].frameCall();
			}
		}
		for(int i = 0; i < graphic_Teleport.length; i++)
		{
			if(graphic_Teleport[i] != null)
			{
				if(graphic_Teleport[i].deleted)
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
		for(int i = 0; i < spGraphic.length; i++)
		{
			if(spGraphic[i] != null)
			{
				if(spGraphic[i].deleted)
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
				if(enemies[i].deleted)
				{
					enemies[i] = null;
				}
				else
				{
					enemies[i].frameCall();
                                        if(enemies[i].x < 100) enemies[i].x = 100;
                                        if(enemies[i].x > 380) enemies[i].x = 380;
                                        if(enemies[i].y < 20) enemies[i].y = 20;
                                        if(enemies[i].y > 300) enemies[i].y = 300;
				}
			}
		}
		if(!player.deleted)
		{
			player.frameCall();
			if(!player.teleporting)
			{
				if(player.x < 100) player.x = 100;
				if(player.x > 380) player.x = 380;
				if(player.y < 20) player.y = 20;
				if(player.y > 300) player.y = 300;
			}
		}
		else
		{
			player = null;
		}
		if(!enemy.deleted)
		{
			enemy.frameCall();
			if(enemy.x < 100) enemy.x = 100;
			if(enemy.x > 380) enemy.x = 380;
			if(enemy.y < 20) enemy.y = 20;
			if(enemy.y > 300) enemy.y = 300;
		}
		else
		{
			enemy = null;
		}
		if(!spGraphicPlayer.deleted)
		{
			spGraphicPlayer.frameCall();
		}
		if(!spGraphicEnemy.deleted)
		{
			spGraphicEnemy.frameCall();
		}
		invalidate();
	}
	public void drawRect(int x, int y, int x2, int y2, Canvas g)
	{
		x *= screenDimensionMultiplier;
		y *= screenDimensionMultiplier;
		x += screenMinX;
		y += screenMinY;
		x2 *= screenDimensionMultiplier;
		y2 *= screenDimensionMultiplier;
		x2 += screenMinX;
		y2 += screenMinY;
		g.drawRect(x, y, x2, y2, paint);
	}
	public void drawCircle(int x, int y, int radius, Canvas g)
	{
		x *= screenDimensionMultiplier;
		y *= screenDimensionMultiplier;
		x += screenMinX;
		y += screenMinY;
		g.drawCircle(x, y, radius, paint);
	}
	public void drawBitmap(Bitmap picture, int x, int y, Canvas g)
	{
		x *= screenDimensionMultiplier;
		y *= screenDimensionMultiplier;
		x += screenMinX;
		y += screenMinY;
		g.drawBitmap(picture, x, y, paint);
	}
	public void drawBitmapRotated(drawnSprite sprite, Canvas g)
	{
		rotateImages.reset();
		rotateImages.postTranslate(-sprite.visualImage.getWidth() / 2, -sprite.visualImage.getHeight() / 2);
		rotateImages.postRotate((float)sprite.rotation);
		rotateImages.postTranslate((float)(sprite.x * screenDimensionMultiplier) + screenMinX, (float)(sprite.y * screenDimensionMultiplier) + screenMinY);
		g.drawBitmap(sprite.visualImage, rotateImages, paint);
		sprite = null;
	}
	public void drawBitmapRect(Bitmap picture, Rect rectangle, Canvas g)
	{
		rectangle.top *= screenDimensionMultiplier;
		rectangle.top += screenMinY;
		rectangle.bottom *= screenDimensionMultiplier;
		rectangle.bottom += screenMinY;
		rectangle.right *= screenDimensionMultiplier;
		rectangle.right += screenMinX;
		rectangle.left *= screenDimensionMultiplier;
		rectangle.left += screenMinX;
		g.drawBitmap(picture, null, rectangle, paint);
	}
	public void drawText(String text, int x, int y, Canvas g)
	{
		x *= screenDimensionMultiplier;
		y *= screenDimensionMultiplier;
		x += screenMinX;
		y += screenMinY;
		g.drawText(text, x, y, paint);
	}
	public void drawStart(Canvas g)
	{
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(playerColour);
		drawRect(240, 0, 480, 320, g);
		paint.setColor(enemyColour);
		drawRect(0, 0, 240, 320, g);
		paint.setColor(Color.WHITE);
		drawRect(90, 10, 390, 310, g);
		drawBitmap(game.imageLibrary.fullScreen1, 15, 87, g);
		drawBitmap(game.imageLibrary.fullScreen2, 405, 87, g);
		paint.setColor(Color.GRAY);
		for(int i = 0; i < obstaclesRectanglesX1.length; i++)
		{
			drawRect(obstaclesRectanglesX1[i]+wallWidth, obstaclesRectanglesY1[i]+wallWidth, obstaclesRectanglesX2[i]-wallWidth, obstaclesRectanglesY2[i]-wallWidth, g);
		}
		for(int i = 0; i < obstaclesCirclesX.length; i++)
		{
			drawCircle(obstaclesCirclesX[i], obstaclesCirclesY[i], obstaclesCirclesRadius[i]-wallWidth, g);
		}
	}
	@Override
	public void onDraw(Canvas g)
	{		
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
				aoeRect.top = (int)(powerBallAOEs[i].y - (powerBallAOEs[i].height / 2));
				aoeRect.bottom = (int)(powerBallAOEs[i].y - (powerBallAOEs[i].height / 2)) + powerBallAOEs[i].height;
				aoeRect.left = (int)(powerBallAOEs[i].x - (powerBallAOEs[i].width / 2));
				aoeRect.right = (int)(powerBallAOEs[i].x - (powerBallAOEs[i].width / 2)) + powerBallAOEs[i].width;
				paint.setAlpha(powerBallAOEs[i].alpha);
				drawBitmapRect(powerBallAOEs[i].visualImage, aoeRect, g);
			}
		}
		paint.setAlpha(255);
		for(int i = 0; i < graphic_Teleport.length; i++)
		{
			if(graphic_Teleport[i] != null)
			{
				drawBitmap(graphic_Teleport[i].visualImage, (int)(graphic_Teleport[i].x - (graphic_Teleport[i].imageWidth / 2)), (int)(graphic_Teleport[i].y - (graphic_Teleport[i].imageHeight / 2)), g);
			}
		}
		paint.setStyle(Paint.Style.FILL);
		paint.setColor(Color.GREEN);
		for(int i = 0; i < spGraphic.length; i++)
		{
			if(spGraphic[i] != null)
			{
				drawCircle((int)(spGraphic[i].x - (spGraphic[i].width / 2)), (int)(spGraphic[i].y - (spGraphic[i].width / 2)), spGraphic[i].width, g);
			}
		}
		if(warningTimer > 0)
		{
			warningTimer--;
			paint.setAlpha((byte)(warningTimer * 7));
			drawBitmap(game.imageLibrary.warnings[warningType], 240 - (game.imageLibrary.warnings[warningType].getWidth() / 2), 160 - (game.imageLibrary.warnings[warningType].getHeight() / 2), g);
		}
		paint.setAlpha(255);
	}
	public void createPowerBallEnemy(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		powerBall_Enemy ballEnemy = new powerBall_Enemy(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballEnemy;
	}
	public void createPowerBallPlayer(double rotation, double xVel, double yVel, int power, double x, double y)
	{
		powerBall_Player ballPlayer = new powerBall_Player(this, (int) x, (int) y, power, xVel, yVel, rotation);
		powerBalls[lowestPositionEmpty(powerBalls)] = ballPlayer;
	}
	public void createPowerBallEnemyAOE(double x, double y, double power)
	{
		powerBallAOE_Enemy ballAOEEnemy = new powerBallAOE_Enemy(this, (int) x, (int) y, power);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEEnemy;
	}
	public void createPowerBallPlayerAOE(double x, double y, double power)
	{
		powerBallAOE_Player ballAOEPlayer = new powerBallAOE_Player(this, (int) x, (int) y, power);
		powerBallAOEs[lowestPositionEmpty(powerBallAOEs)] = ballAOEPlayer;
	}
	public void teleportStart(double x, double y)
	{
		graphic_Teleport teleportStart = new graphic_Teleport(this, x, y, true);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportStart;
	}
	public void teleportFinish(double x, double y)
	{
		graphic_Teleport teleportFinish = new graphic_Teleport(this, x, y, false);
		graphic_Teleport[lowestPositionEmpty(graphic_Teleport)] = teleportFinish;
	}
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
	public void notEnoughMana()
	{
		warningTimer = 30;
		warningType = 0;
	}
	public void coolDown()
	{
		warningTimer = 30;
		warningType = 1;
	}	
}