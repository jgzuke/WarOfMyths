/*
 * Loads, stores and resizes all graphics
 */
package com.example.magegame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
public final class ImageLibrary
{
	protected boolean pikemanLoaded = false;
	protected boolean swordsmanLoaded = false;
	protected boolean axemanLoaded = false;
	protected boolean archerLoaded = false;
	protected boolean rogueLoaded = false;
	protected Bitmap[] player_Image = new Bitmap[51];
	protected Bitmap[] mage_Image = new Bitmap[51];
	protected Bitmap[] rogue_Image = new Bitmap[65];
	protected Bitmap[] pikeman_Image = new Bitmap[107]; 
	protected Bitmap[] swordsman_Image = new Bitmap[79];
	protected Bitmap[] axeman_Image = new Bitmap[79]; 
	protected Bitmap[] archer_Image = new Bitmap[116];
	protected Bitmap[] effects = new Bitmap[4];
	protected Bitmap target_Image;
	protected Bitmap next;
	protected Bitmap haskey;
	protected Bitmap exitFightPortal;
	protected Bitmap[] powerBall_ImagePlayer = new Bitmap[5];
	protected Bitmap powerBallAOE_ImagePlayer;
	protected Bitmap[] powerBall_ImageEnemy = new Bitmap[5];
	protected Bitmap powerBallAOE_ImageEnemy;
	protected Bitmap[] teleport_Image = new Bitmap[15];
	protected Bitmap[] warnings = new Bitmap[2];
	protected Bitmap[] powerUps = new Bitmap[8];
	protected Bitmap[] powerUpBigs = new Bitmap[4];
	protected Bitmap[] coins = new Bitmap[2];
	protected Bitmap arrow;
	protected Bitmap levelLocked;
	protected Bitmap currentLevel;
	protected Bitmap currentLevelTop;
	protected Bitmap directionsTutorial;
	protected Bitmap toTile;
	private String getting;
	protected Resources res;
	protected String packageName;
	private Controller control;
	protected BitmapFactory.Options opts;
	/*
	 * Loads all images needed for every game
	 */
	public ImageLibrary(Context contextSet, Controller controlSet)
	{
		opts = new BitmapFactory.Options();
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inTempStorage = new byte[16 * 1024];
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();
		control = controlSet;
		loadAllImages();
	}
	protected void loadAllImages()
	{
		exitFightPortal = loadImage("exitfightportal", 60, 60);
		levelLocked = loadImage("menu_levellocked", 30, 30);
		mage_Image = loadArray1D(31, "human_mage", 30, 30);
		warnings[0] = loadImage("warn0001", 147, 27);
		warnings[1] = loadImage("warn0002", 205, 27);
		player_Image = loadArray1D(31, "human_zack", 30, 30);
		powerUps = loadArray1D(8, "powerup", 30, 30);
		powerUpBigs = loadArray1D(5, "powerupbig", 50, 50);
		coins = loadArray1D(2, "menu_coin", 30, 30);
		target_Image = loadImage("human_target", 46, 50);
		teleport_Image = loadArray1D(9, "teleport", 60, 21);
		// TODO change to black ball
		powerBallAOE_ImageEnemy = loadImage("powerballaoe0005", 80, 80);
		powerBall_ImageEnemy = loadArray1D(5, "powerball0005_", 42, 18);
		next = loadImage("menu_nexttutorial", 80, 40);
		effects = loadArray1D(4, "effect", 60, 60);
		haskey = loadImage("haskey", 40, 40);
		arrow = loadImage("arrow", 17, 10);
		loadLevel(control.levelNum, control.levelWidth, control.levelHeight);
		//loadSpriteImages();
		changeArrayLoaded("swordsman", true);
	}
	protected void loadLevel(int levelNum, int width, int height)
	{
		if(currentLevel!= null)
		{
			currentLevel.recycle();
		}
		if(currentLevelTop!= null)
		{
			currentLevelTop.recycle();
		}
		if(toTile!= null)
		{
			toTile.recycle();
		}
		currentLevel = loadImage("level"+correctDigits(levelNum, 4), width, height);
		currentLevelTop = loadImage("leveltop"+correctDigits(levelNum, 4), width, height);
		if(levelNum == 20)
		{
			directionsTutorial = loadImage("menu_tutorial0001", 217, 235);
		}
		if(levelNum == 10||levelNum == 20)
		{
			toTile = loadImage("level_tile0001", 90, 90);
		}
		if(levelNum == 11)
		{
			toTile = loadImage("level_tile0002", 90, 90);
		}
		if(levelNum == 30||levelNum == 40||levelNum == 80)
		{
			toTile = loadImage("level_tile0003", 90, 90);
		}
		if(levelNum == 50||levelNum == 60||levelNum == 70||levelNum == 90||levelNum == 91)
		{
			toTile = loadImage("level_tile0004", 90, 90);
		}
	}
	protected void loadPlayerPowerBall()
	{
		powerBallAOE_ImagePlayer = loadImage("powerballaoe000"+Integer.toString(control.playerType+1), 80, 80);
		powerBall_ImagePlayer = loadArray1D(5, "powerball000"+Integer.toString(control.playerType+1)+"_", 42, 18);
	}
	protected void loadSpriteImages()
	{
		if(control != null)
		{
				switch(control.levelNum)
				{
				case 10:
					changeArrayLoaded("swordsman", true);
					break;
				case 30:
					changeArrayLoaded("swordsman", true);
					changeArrayLoaded("archer", true);
					break;
				case 40:
					changeArrayLoaded("axeman", true);
					changeArrayLoaded("pikeman", true);
					break;
				case 50:
					changeArrayLoaded("swordsman", true);
					changeArrayLoaded("pikeman", true);
					break;
				case 60:
					changeArrayLoaded("swordsman", true);
					changeArrayLoaded("pikeman", true);
					changeArrayLoaded("rogue", true);
					break;
				case 70:
					changeArrayLoaded("swordsman", true);
					changeArrayLoaded("archer", true);
					break;
				case 80:
					changeArrayLoaded("pikeman", true);
					changeArrayLoaded("archer", true);
					break;
				case 90:
					changeArrayLoaded("swordsman", true);
					break;
				default:
					changeArrayLoaded("swordsman", true);
					changeArrayLoaded("archer", true);
					changeArrayLoaded("pikeman", true);
					changeArrayLoaded("rogue", true);
					changeArrayLoaded("axeman", true);
					break;
				}
		}
	}
	protected void recycleImages()
	{
		if(currentLevel != null)
		{
			currentLevel.recycle();
			currentLevel = null;
		}
		if(currentLevelTop != null)
		{
			currentLevelTop.recycle();
			currentLevelTop = null;
		}
		recycleArray(31, mage_Image);
		recycleArray(31, player_Image);
		recycleArray(2, warnings);
		recycleArray(5, powerUpBigs);
		recycleArray(8, powerUps);
		recycleArray(4, effects);
		levelLocked.recycle();
		levelLocked = null;
		changeArrayLoaded("archer", false);
		changeArrayLoaded("pikeman", false);
		changeArrayLoaded("axeman", false);
		changeArrayLoaded("swordsman", false);
		changeArrayLoaded("rogue", false);
	}
	protected void recycleArray(int length, Bitmap[] array)
	{
		for(int i = 0; i < length; i++)
		{
			if(array[i] != null)
			{
				array[i].recycle();
				array[i] = null;
			}
		}
	}
	/*
	 * Loads or recycles a human animation array
	 * @param toChange Which human to either load or recycle
	 * @param loading Whether to load or not
	 */
	protected void changeArrayLoaded(String toChange, boolean loading)
	{
		if(loading)
		{
			if(toChange.equals("archer") && !archerLoaded)
			{
				archer_Image = loadArray1D(49, "human_archer", 80, 50);
				archerLoaded = true;
			}
			else if(toChange.equals("pikeman") && !pikemanLoaded)
			{
				pikeman_Image = loadArray1D(62, "human_pikeman", 110, 40);
				pikemanLoaded = true;
			}
			else if(toChange.equals("axeman") && !axemanLoaded)
			{
				axeman_Image = loadArray1D(55, "human_axeman", 80, 60);
				axemanLoaded = true;
			}
			else if(toChange.equals("swordsman") && !swordsmanLoaded)
			{
				swordsman_Image = loadArray1D(55, "human_swordsman", 110, 70);
				swordsmanLoaded = true;
			}
			else if(toChange.equals("rogue") && !rogueLoaded)
			{
				rogue_Image = loadArray1D(65, "human_rogue", 60, 40);
				rogueLoaded = true;
			}
		}
		else
		{
			if(toChange.equals("archer") && archerLoaded)
			{
				for(int i = 0; i < archer_Image.length; i++)
				{
					archer_Image[i].recycle();
					archer_Image[i] = null;
				}
				archerLoaded = false;
			}
			else if(toChange.equals("pikeman") && pikemanLoaded)
			{
				for(int i = 0; i < pikeman_Image.length; i++)
				{
					pikeman_Image[i].recycle();
					pikeman_Image[i] = null;
				}
				pikemanLoaded = false;
			}
			else if(toChange.equals("axeman") && axemanLoaded)
			{
				for(int i = 0; i < axeman_Image.length; i++)
				{
					axeman_Image[i].recycle();
					axeman_Image[i] = null;
				}
				axemanLoaded = false;
			}
			else if(toChange.equals("swordsman") && swordsmanLoaded)
			{
				for(int i = 0; i < swordsman_Image.length; i++)
				{
					swordsman_Image[i].recycle();
					swordsman_Image[i] = null;
				}
				swordsmanLoaded = false;
			}
			else if(toChange.equals("rogue") && rogueLoaded)
			{
				for(int i = 0; i < rogue_Image.length; i++)
				{
					rogue_Image[i].recycle();
					rogue_Image[i] = null;
				}
				rogueLoaded = false;
			}
		}
	}
	/*
	 * Loads and resizes array of images
	 * @param length Length of array to load
	 * @param start Starting string which precedes array index to match resource name
	 * @param width End width of image being loaded
	 * @param height End height of image being loaded
	 */
	protected Bitmap[] loadArray1D(int length, String start, int width, int height)
	{
		Bitmap[] newArray = new Bitmap[length];
		for(int i = 0; i < length; i++)
		{
			getting = start + correctDigits(i + 1, 4);
			newArray[i] = loadImage(getting, width, height);
		}
		return newArray;
	}
	/*
	 * Adds 0's before string to make it four digits long
	 * Animations done in flash which when exporting .png sequence end file name with four character number
	 * @return Returns four character version of number
	 */
	protected String correctDigits(int start, int digits)
	{
		String end = Integer.toString(start);
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
	/*
	 * Loads image of name given from resources and scales to specified width and height
	 * @return Returns bitmap loaded and resized
	 */
	protected Bitmap loadImage(String imageName, int width, int height)
	{
		Log.e("game", imageName);
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), width, height, false);
	}
}