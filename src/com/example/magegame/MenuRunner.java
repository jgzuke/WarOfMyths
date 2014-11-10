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
import android.graphics.Color;
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
	protected Bitmap now = null;
	private Bitmap check;
	private boolean drawn = false;
	private byte size = 2;
	private int fixYClickOffset = -20;
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
		now = loadImage(currentScreen);
		check = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, res.getIdentifier("menu_check", "drawable", packageName), opts), 20*size, 20*size, false);
		setOnTouchListener(this);
	}
	protected void frameCall()
	{
		
		/*if(drawn)
		{
			if(currentScreen.equals("fightdetails1"))
			{
				now = loadImage("fightdetails2");
			} else if(currentScreen.equals("fightdetails2"))
			{
				now = loadImage("fightdetails3");
			} else if(currentScreen.equals("tutorial0001"))
			{
				now = loadImage("tutorial0002");
			} else if(currentScreen.startsWith("tutorial"))
			{
				String number = currentScreen.substring(8);
				for(int i = 0; i < 10; i++)
				{
					if(number.startsWith("0"))
					{
						number = number.substring(1);
					}
				}
				int numberSave = Integer.parseInt(number)+1;
				if(numberSave < 23)
				{
					number = Integer.toString(numberSave+1);
					for(int i = 0; i < 10; i++)
					{
						if(number.length() < 4)
						{
							number = "0" + number;
						}
					}
					now = loadImage("tutorial"+number);
				} else
				{
					now = loadImage("main");
				}
			}
			drawn = false;
		}*/
	}
	@ Override
	protected void onDraw(Canvas g)
	{
			g.translate(screenMinX, screenMinY);
			g.scale((float) screenDimensionMultiplier/size, (float) screenDimensionMultiplier/size);
			drawBitmap(now, 0, 0, g);
			drawn = true;
			if(currentScreen.equals("fightdetails2"))
			{
				paint.setColor(Color.GRAY);
				paint.setAlpha(151);
				if(activity.levelBeaten < 1)
				{
					drawRect(245*size, 35*size, 365*size, 155*size, g);
				}
				if(activity.levelBeaten < 2)
				{
					drawRect(50*size, 165*size, 170*size, 285*size, g);
				}
				if(activity.levelBeaten < 3)
				{
					drawRect(180*size, 165*size, 300*size, 285*size, g);
				}
				if(activity.levelBeaten < 4)
				{
					drawRect(310*size, 165*size, 430*size, 285*size, g);
				}
			}
			if(currentScreen.equals("options"))
			{
				if(activity.stickOnRight)
				{
					drawBitmap(check, 263*size, 147*size, g);
				} else
				{
					drawBitmap(check, 192*size, 147*size, g);
				}
				if(activity.shootTapScreen)
				{
					drawBitmap(check, 319*size, 227*size, g);
				} else
				{
					drawBitmap(check, 205*size, 227*size, g);
				}
				if(activity.shootTapDirectional)
				{
					drawBitmap(check, 195*size, 255*size, g);
				} else
				{
					drawBitmap(check, 338*size, 255*size, g);
				}
			} else
			{
				now = null;
			}
	}
	protected Bitmap loadImage(String imageName)
	{
		int imageNumber = res.getIdentifier("menu_"+imageName, "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), 480*size, 320*size, false);
	}
	protected void changeScreen(String newScreen)
	{
		currentScreen = newScreen;
		activity.playEffect(R.raw.pageflip);
		if(now == null)
		{
			now = loadImage(currentScreen);
		}
		invalidate();
	}
	@Override
	public boolean onTouch(View v, MotionEvent e)
	{
		if(e.getAction() == MotionEvent.ACTION_DOWN)
		{
			double x = realX(e.getX());
			double y = realY(e.getY());
			if(currentScreen.startsWith("tutorial"))
			{
				if(currentScreen.equals("tutorial0023") || pointOnSquare(x, y, 400, 275, 480, 320))
				{
					now = null;
					changeScreen("main");
				} else
				{
					String number = currentScreen.substring(8);
					for(int i = 0; i < 10; i++)
					{
						if(number.startsWith("0"))
						{
							number = number.substring(1);
						}
					}
					int numberSave = Integer.parseInt(number)+1;
					number = Integer.toString(numberSave);
					for(int i = 0; i < 10; i++)
					{
						if(number.length() < 4)
						{
							number = "0" + number;
						}
					}
					changeScreen("tutorial" + number);
				}
			} else if(currentScreen.equals("main"))
			{
				if(pointOnSquare(x, y, 146, 41, 334, 139))
				{
					changeScreen("fightdetails1");
				}
				if(pointOnSquare(x, y, 10, 150, 140, 187))
				{
					changeScreen("tutorial0001");
				}
				if(pointOnSquare(x, y, 340, 107, 470, 144))
				{
					changeScreen("options");
				}
			} else if(currentScreen.equals("fightdetails1"))
			{
				if(pointOnCircle(x, y, 95, 225, 65))
				{
					changeScreen("fightdetails2");
					playerType = 0;
				} else if(pointOnCircle(x, y, 190, 95, 65))
				{
					changeScreen("fightdetails2");
					playerType = 1;
				} else if(pointOnCircle(x, y, 290, 225, 65))
				{
					changeScreen("fightdetails2");
					playerType = 2;
				} else if(pointOnCircle(x, y, 385, 95, 65))
				{
					changeScreen("fightdetails2");
					playerType = 3;
				}
			} else if(currentScreen.equals("fightdetails2"))
			{
				if(y > 35 && y < 155)
				{
					if(x > 115 && x < 235)
					{
						level = 0;
						changeScreen("fightdetails3");
					}
					if(x > 245 && x < 365 && activity.levelBeaten > 0)
					{
						level = 1;
						changeScreen("fightdetails3");
					}
				}
				if(y > 165 && y < 285 && activity.levelBeaten > 1)
				{
					if(x > 50 && x < 170)
					{
						level = 2;
						changeScreen("fightdetails3");
					}
					if(x > 180 && x < 300 && activity.levelBeaten > 2)
					{
						level = 3;
						changeScreen("fightdetails3");
					}
					if(x > 310 && x < 430 && activity.levelBeaten > 3)
					{
						level = 4;
						changeScreen("fightdetails3");
					}
				}
			} else if(currentScreen.equals("fightdetails3"))
			{
				if(x>20 && x<460 && y>120 && y<200)
				{
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
			} else if(currentScreen.equals("options"))
			{
				if(pointOnSquare(x, y, 205, 227, 225, 247))
				{
					activity.shootTapScreen = false;
					activity.playEffect(R.raw.pageflip);
					invalidate();
				}
				if(pointOnSquare(x, y, 319, 227, 339, 247))
				{
					activity.shootTapScreen = true;
					activity.playEffect(R.raw.pageflip);
					invalidate();
				}
				if(pointOnSquare(x, y, 195, 255, 215, 275))
				{
					activity.shootTapDirectional = true;
					activity.playEffect(R.raw.pageflip);
					invalidate();
				}
				if(pointOnSquare(x, y, 338, 255, 358, 275))
				{
					activity.shootTapDirectional = false;
					activity.playEffect(R.raw.pageflip);
					invalidate();
				}
				if(pointOnSquare(x, y, 263, 147, 283, 167))
				{
					activity.stickOnRight = true;
					activity.playEffect(R.raw.pageflip);
					invalidate();
				}
				if(pointOnSquare(x, y, 192, 147, 212, 167))
				{
					activity.stickOnRight = false;
					activity.playEffect(R.raw.pageflip);
					invalidate();
				}
				if(pointOnSquare(x, y, 11, 11, 126, 55))
				{
					now = null;
					changeScreen("main");
				}
			} else if(currentScreen.equals("fightdetails3"))
			{
				
			}
		}
		return true;
	}
	protected double realX(double x)
	{
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	protected double realY(double y)
	{
		return ((y-screenMinX)/screenDimensionMultiplier);
	}
	protected boolean pointOnSquare(double x, double y, double lowX, double lowY, double highX, double highY)
	{
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
		if(Math.sqrt(Math.pow(x-midX, 2) + Math.pow(y-midY, 2)) < radius)
        {
			return true;
        } else
        {
        	return false;
        }
	}
}