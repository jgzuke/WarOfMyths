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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.Log;
public final class LoadingScreen extends AllViews
{
	private Resources res;
	private String packageName;
	private BitmapFactory.Options opts;
	private int currentFrame;
	private int percentLoaded = 0;
	public LoadingScreen(Context contextSet, StartActivity activitySet)
	{
		super(contextSet);
		activity = activitySet;
		screenMinX = activitySet.screenMinX;
		screenMinY = activitySet.screenMinY;
		screenDimensionMultiplier = activitySet.screenDimensionMultiplier;
		opts = new BitmapFactory.Options();
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inTempStorage = new byte[16 * 1024];
		opts.inSampleSize = 1;
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();
	}
	public void frameCall()
	{
		invalidate();
	}
	@ Override
	public void onDraw(Canvas g)
	{
			g.translate(screenMinX+(int)(80*screenDimensionMultiplier), screenMinY);
			if(currentFrame > 20 && percentLoaded >99)
			{
				activity.startMenu();
			}
			g.scale((float) screenDimensionMultiplier/4, (float) screenDimensionMultiplier/4);
			drawBitmap(loadFrame(), 0, 0, g);
			drawRect(0, 0, (int)(percentLoaded*4.8), 20, g);
			currentFrame ++;
	}
	/*
	 * Loads and resizes array of images
	 * @param length Length of array to load
	 * @param start Starting string which precedes array index to match resource name
	 * @param width End width of image being loaded
	 * @param height End height of image being loaded
	 */
	public Bitmap loadFrame()
	{
		int imageNumber = res.getIdentifier("menu_loading", "drawable", packageName);
		//int imageNumber = res.getIdentifier("loadsequence"+correctDigits(currentFrame + 1, 4), "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), 1280, 1280, false);
	}
	/*
	 * Adds 0's before string to make it four digits long
	 * Animations done in flash which when exporting .png sequence end file name with four character number
	 * @return Returns four character version of number
	 */
	private String correctDigits(int start, int digits)
	{
		String end = Integer.toString(start);
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
	public void incrementPercentLoaded(int percentLoaded)
	{
		if(percentLoaded > 0)
		{
			this.percentLoaded += percentLoaded;
			if(percentLoaded>100)
			{
				percentLoaded = 100;
			}
		}
	}
}