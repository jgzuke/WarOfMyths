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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
public final class MenuRunner extends AllViews implements OnTouchListener
{
	private Resources res;
	private String packageName;
	private BitmapFactory.Options opts;
	protected String currentScreen = "main";
	private int playerType;
	private int level;
	public MenuRunner(Context contextSet, StartActivity activitySet)
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
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();
		setOnTouchListener(this);
	}
	@ Override
	public void onDraw(Canvas g)
	{
			g.translate(screenMinX, screenMinY);
			g.scale((float) screenDimensionMultiplier/4, (float) screenDimensionMultiplier/4);
			drawBitmap(loadImage("menu_"+currentScreen), 0, 0, g);
	}
	private Bitmap loadImage(String imageName)
	{
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), 1920, 1280, false);
	}
	@Override
	public boolean onTouch(View v, MotionEvent e)
	{
		if(e.getAction() == MotionEvent.ACTION_DOWN)
		{
			double x = realX(e.getX());
			double y = realY(e.getY());
			if(currentScreen.equals("main"))
			{
				if(pointOnSquare(x, y, 146.4, 71.25, 333.7, 169.35))
				{
					currentScreen = "fightdetails1";
				}
			} else if(currentScreen.equals("fightdetails1"))
			{
				if(pointOnCircle(x, y, 95, 225, 65))
				{
					currentScreen = "fightdetails2";
					playerType = 0;
				} else if(pointOnCircle(x, y, 190, 95, 65))
				{
					currentScreen = "fightdetails2";
					playerType = 1;
				} else if(pointOnCircle(x, y, 290, 225, 65))
				{
					currentScreen = "fightdetails2";
					playerType = 2;
				} else if(pointOnCircle(x, y, 385, 95, 65))
				{
					currentScreen = "fightdetails2";
					playerType = 3;
				}
			} else if(currentScreen.equals("fightdetails2"))
			{
				if(y > 35 && y < 155)
				{
					if(x > 115 && x < 235)
					{
						level = 0;
						currentScreen = "fightdetails3";
					}
					if(x > 245 && x < 365)
					{
						level = 1;
						currentScreen = "fightdetails3";
					}
				}
				if(y > 165 && y < 285)
				{
					if(x > 50 && x < 170)
					{
						level = 2;
						currentScreen = "fightdetails3";
					}
					if(x > 180 && x < 300)
					{
						level = 3;
						currentScreen = "fightdetails3";
					}
					if(x > 310 && x < 430)
					{
						level = 4;
						currentScreen = "fightdetails3";
					}
				}
			} else if(currentScreen.equals("fightdetails3"))
			{
				if(x>20 && x<460 && y>120 && y<200)
				{
						currentScreen = "loading";
						invalidate();
						if(x < 60)
						{
							activity.startFight(playerType, level, 16);
						}
						else if(x < 100)
						{
							activity.startFight(playerType, level, 13);
						}
						else if(x < 140)
						{
							activity.startFight(playerType, level, 11);
						}
						else if(x < 180)
						{
							activity.startFight(playerType, level, 9);
						}
						else if(x < 220)
						{
							activity.startFight(playerType, level, 8);
						}
						else if(x < 260)
						{
							activity.startFight(playerType, level, 6);
						}
						else if(x < 300)
						{
							activity.startFight(playerType, level, 5);
						}
						else if(x < 340)
						{
							activity.startFight(playerType, level, 3);
						}
						else if(x < 380)
						{
							activity.startFight(playerType, level, 2);
						}
						else if(x < 420)
						{
							activity.startFight(playerType, level, 1);
						} else
						{
							activity.startFight(playerType, level, 0);
						}
				}
			} else if(currentScreen.equals("fightdetails3"))
			{
				
			} else if(currentScreen.equals("fightdetails3"))
			{
				
			}
			invalidate();
		}
		return true;
	}
	public double realX(double x)
	{
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	public double realY(double y)
	{
		return (y-screenMinX)/screenDimensionMultiplier;
	}
	public boolean pointOnSquare(double x, double y, double lowX, double lowY, double highX, double highY)
	{
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
		if(Math.sqrt(Math.pow(x-midX, 2) + Math.pow(y-midY, 2)) < radius)
        {
			return true;
        } else
        {
        	return false;
        }
	}
}