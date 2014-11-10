/**
 * Loads, stores and resizes all graphics
 */
package com.magegame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public final class ImageLibrary
{
	protected boolean pikemanLoaded = false;
	protected boolean shieldLoaded = false;
	protected boolean archerLoaded = false;
	protected boolean rogueLoaded = false;
	protected boolean mageLoaded = false;
	protected boolean clericLoaded = false;
	protected Bitmap[] player_Image = new Bitmap[51];
	protected Bitmap[] mage_Image = new Bitmap[51];
	protected Bitmap[] cleric_Image = new Bitmap[51];
	protected Bitmap[] rogue_Image = new Bitmap[65];
	protected Bitmap[] pikeman_Image = new Bitmap[107]; 
	protected Bitmap[] shield_Image = new Bitmap[79];
	protected Bitmap[] archer_Image = new Bitmap[116];
	protected Bitmap structure_Spawn;
	protected Bitmap[] effects = new Bitmap[4];
	protected Bitmap target_Image;
	protected Bitmap isPlayer;
	protected int isPlayerWidth;
	protected Bitmap haskey;
	protected Bitmap exitFightPortal;
	protected Bitmap backButton;
	protected Bitmap[] powerBall_ImagePlayer = new Bitmap[5];
	protected Bitmap powerBallAOE_ImagePlayer;
	protected Bitmap[] powerBall_ImageEnemy = new Bitmap[5];
	protected Bitmap powerBallAOE_ImageEnemy;
	protected Bitmap[] powerUps = new Bitmap[9];
	protected Bitmap[] powerUpBigs = new Bitmap[4];
	protected Bitmap[] coins = new Bitmap[2];
	protected Bitmap[] trans = new Bitmap[10];
	protected Bitmap arrow;
	protected Bitmap currentLevel;
	protected Bitmap currentLevelTop;
	protected Bitmap directionsTutorial;
	protected Bitmap toTile;
	private String getting;
	protected Resources res;
	protected String packageName;
	private Controller control;
	protected BitmapFactory.Options opts;
	protected Bitmap transattack;
	protected int scrollPosition = 0;
	/**
	 * loads in images and optimizes settings for loading
	 * @param contextSet start activity for getting resources etc
	 * @param controlSet control object
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
	/**
	 * loads players current animation
	 */
	protected void loadPlayerImage()
	{
		switch(control.activity.currentSkin)
		{
			case 0: player_Image = loadArray1D(31, "human_playerzack", 35, 40);
					isPlayerWidth = 26;
				break;
			case 1: player_Image = loadArray1D(31, "human_playergolden", 35, 40);
					isPlayerWidth = 26;
				break;
			case 2: player_Image = loadArray1D(31, "human_playerleather", 35, 40);
					isPlayerWidth = 26;
				break;
			case 3: player_Image = loadArray1D(31, "human_playerbarbarian", 35, 40);
					isPlayerWidth = 26;
				break;
			case 4: player_Image = loadArray1D(31, "human_playercleric", 35, 40);
					isPlayerWidth = 26;
				break;
			case 5: player_Image = loadArray1D(31, "human_playerent", 80, 76);
					isPlayerWidth = 43;
				break;
			case 6: player_Image = loadArray1D(31, "human_playergargoyle", 60, 69);
					isPlayerWidth = 38;
				break;
			case 7: player_Image = loadArray1D(31, "human_playerdragon", 35, 40);
					isPlayerWidth = 26;
				break;
			default: player_Image = loadArray1D(31, "human_playerzack", 35, 40);
					isPlayerWidth = 26;
				break;
		}
		isPlayer = loadImage("isplayer", 2*isPlayerWidth, 2*isPlayerWidth);
	}
	/**
	 * load transformation background
	 */
	protected void loadTrans()
	{
		String temp;
		if(control.player.transformed==1)
		{
			transattack = loadImage("attacksword", 66, 66);
		} else
		{
			transattack = loadImage("attackhammer", 66, 66);
		}
		switch(control.playerType)
		{
			case 0: temp = "fire";
				break;
			case 1: temp = "water";
				break;
			case 2: temp = "electric";
				break;
			case 3: temp = "earth";
				break;
			default: temp = "fire";
				break;
		}
		trans = loadArray1D(10, "human_trans"+temp, 119, 119);
	}
	/**
	 * loads all required images for all games
	 */
	protected void loadAllImages()
	{
		exitFightPortal = loadImage("exitfightportal", 60, 60);
		backButton = loadImage("exitfight", 40, 40);
		loadPlayerImage();
		powerUps = loadArray1D(10, "powerup", 30, 30);
		powerUpBigs = loadArray1D(5, "powerupbig", 70, 70);
		coins = loadArray1D(2, "menu_coin", 30, 30);
		target_Image = loadImage("human_target", 48, 53);
		// TODO change to black ball
		powerBallAOE_ImageEnemy = loadImage("powerballaoe0005", 80, 80);
		powerBall_ImageEnemy = loadArray1D(5, "powerball0005_", 42, 18);
		effects = loadArray1D(4, "effect", 60, 60);
		haskey = loadImage("haskey", 40, 40);
		arrow = loadImage("arrow", 30, 10);
		loadLevel(control.levelNum, control.levelWidth, control.levelHeight);
		//loadSpriteImages();
		changeArrayLoaded("shield", "human_swordsman");
	}
	/**
	 * loads level image layers and background image
	 * @param levelNum level to load
	 * @param width width of level
	 * @param height height of level
	 */
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
		if(levelNum == 10||levelNum == 20||levelNum == 21||levelNum == 22)
		{
			toTile = loadImage("level_tile0001", 90, 90);
		}
		if(levelNum == 11)
		{
			toTile = loadImage("level_tile0002", 90, 90);
		}
		if(levelNum == 30||levelNum == 40)
		{
			toTile = loadImage("level_tile0003", 90, 90);
		}
		if(levelNum == 60||levelNum == 70||levelNum == 90||levelNum == 91||levelNum == 100||levelNum == 101)
		{
			toTile = loadImage("level_tile0004", 90, 90);
		}
		if(levelNum == 110)
		{
			toTile = loadImage("level_tile0005", 90, 90);
		}
		if(levelNum == 120)
		{
			toTile = loadImage("level_tile0006", 90, 90);
		}
		if(levelNum == 130)
		{
			toTile = loadImage("level_tile0007", 90, 90);
		}
		if(levelNum == 140||levelNum == 80)
		{
			toTile = loadImage("level_tile0008", 90, 90);
		}
		if(levelNum == 150||levelNum == 160)
		{
			toTile = loadImage("level_tile0009", 90, 90);
		}
		if(levelNum == 151)
		{
			toTile = loadImage("level_tile0010", 90, 90);
		}
		if(levelNum == 50)
		{
			toTile = loadImage("level_tile0011", 90, 90);
		}
	}
	/**
	 * loads powerball of players type
	 */
	protected void loadPlayerPowerBall()
	{
		powerBallAOE_ImagePlayer = loadImage("powerballaoe000"+Integer.toString(control.playerType+1), 80, 80);
		powerBall_ImagePlayer = loadArray1D(5, "powerball000"+Integer.toString(control.playerType+1)+"_", 42, 18);
	}
	/**
	 * loads enemies in each level if app is exited and reentered
	 */
	protected void loadSpriteImages()
	{
		if(control != null)
		{
				switch(control.levelNum)
				{
				case 10:
					changeArrayLoaded("shield", "human_swordsman");
					break;
				case 30:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("archer", "human_archer");
					break;
				case 40:
					changeArrayLoaded("archer", "human_archer");
					changeArrayLoaded("pikeman", "human_pikeman");
					break;
				case 50:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("pikeman", "human_pikeman");
					break;
				case 60:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("pikeman", "human_pikeman");
					changeArrayLoaded("rogue", "human_rogue");
					break;
				case 70:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("archer", "human_archer");
					break;
				case 80:
					changeArrayLoaded("pikeman", "human_pikeman");
					changeArrayLoaded("archer", "human_archer");
					break;
				case 90:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("archer", "human_archer");
					break;
				case 100:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("archer", "human_archer");
					break;
				case 110:
					changeArrayLoaded("pikeman", "human_pikeman");
					changeArrayLoaded("archer", "human_archer");
					changeArrayLoaded("rogue", "human_rogue");
					break;
				case 120:
					changeArrayLoaded("pikeman", "human_pikeman");
					changeArrayLoaded("archer", "human_archer");
					break;
				case 130:
					changeArrayLoaded("archer", "human_archer");
					changeArrayLoaded("shield", "human_axeman");
					break;
				default:
					changeArrayLoaded("shield", "human_swordsman");
					changeArrayLoaded("archer", "human_archer");
					changeArrayLoaded("pikeman", "human_pikeman");
					changeArrayLoaded("rogue", "human_rogue");
					break;
				}
		}
	}
	/**
	 * recycles images to save memory
	 */
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
		recycleArray(31, cleric_Image);
		recycleArray(31, player_Image);
		recycleArray(5, powerUpBigs);
		recycleArray(10, powerUps);
		recycleArray(4, effects);
		changeArrayLoaded("archer");
		changeArrayLoaded("pikeman");
		changeArrayLoaded("shield");
		changeArrayLoaded("rogue");
		changeArrayLoaded("mage");
	}
	/**
	 * recycles desired array of images
	 * @param length length of array
	 * @param array array to recycle
	 */
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
	/**
	 * Loads or recycles a human animation array
	 * @param toChange Which human to either load or recycle
	 * @param loading Whether to load or not
	 */
	protected void changeArrayLoaded(String toChange)
	{
			if(toChange.equals("archer") && archerLoaded)
			{
				for(int i = 0; i < archer_Image.length; i++)
				{
					if(archer_Image[i] != null)
					{
						archer_Image[i].recycle();
						archer_Image[i] = null;
					}
				}
				archerLoaded = false;
			}
			else if(toChange.equals("pikeman") && pikemanLoaded)
			{
				for(int i = 0; i < pikeman_Image.length; i++)
				{
					if(pikeman_Image[i] != null)
					{
						pikeman_Image[i].recycle();
						pikeman_Image[i] = null;
					}
				}
				pikemanLoaded = false;
			}
			else if(toChange.equals("axeman") && shieldLoaded)
			{
				for(int i = 0; i < shield_Image.length; i++)
				{
					if(shield_Image[i] != null)
					{
						shield_Image[i].recycle();
						shield_Image[i] = null;
					}
				}
				shieldLoaded = false;
			}
			else if(toChange.equals("swordsman") && shieldLoaded)
			{
				for(int i = 0; i < shield_Image.length; i++)
				{
					if(shield_Image[i] != null)
					{
						shield_Image[i].recycle();
						shield_Image[i] = null;
					}
				}
				shieldLoaded = false;
			}
			else if(toChange.equals("rogue") && rogueLoaded)
			{
				for(int i = 0; i < rogue_Image.length; i++)
				{
					if(rogue_Image[i] != null)
					{
						rogue_Image[i].recycle();
						rogue_Image[i] = null;
					}
				}
				rogueLoaded = false;
			}
			else if(toChange.equals("mage") && mageLoaded)
			{
				for(int i = 0; i < mage_Image.length; i++)
				{
					if(mage_Image[i] != null)
					{
						mage_Image[i].recycle();
						mage_Image[i] = null;
					}
				}
				mageLoaded = false;
			}
			else if(toChange.equals("cleric") && clericLoaded)
			{
				for(int i = 0; i < cleric_Image.length; i++)
				{
					if(cleric_Image[i] != null)
					{
						cleric_Image[i].recycle();
						cleric_Image[i] = null;
					}
				}
				clericLoaded = false;
			}
	}
	/**
	 * Loads or recycles a human animation array
	 * @param toChange Which human to either load or recycle
	 * @param loading Whether to load or not
	 */
	protected void changeArrayLoaded(String toChange, String nameToLoad)
	{
			if(toChange.equals("archer"))
			{
				archer_Image = loadArray1D(49, nameToLoad, 80, 50);
				archerLoaded = true;
			}
			else if(toChange.equals("pikeman"))
			{
				pikeman_Image = loadArray1D(62, nameToLoad, 110, 40);
				pikemanLoaded = true;
			}
			else if(toChange.equals("shield"))
			{
				shield_Image = loadArray1D(55, nameToLoad, 110, 70);
				shieldLoaded = true;
			}
			else if(toChange.equals("rogue"))
			{
				rogue_Image = loadArray1D(65, nameToLoad, 60, 40);
				rogueLoaded = true;
			}
			else if(toChange.equals("mage"))
			{
				mage_Image = loadArray1D(31, nameToLoad, 30, 34);
				mageLoaded = true;
			}
			else if(toChange.equals("cleric"))
			{
				cleric_Image = loadArray1D(25, nameToLoad, 39, 34);
				clericLoaded = true;
			}
	}
	/**
	 * Loads or recycles a human animation array
	 * @param toChange Which human to either load or recycle
	 * @param loading Whether to load or not
	 */
	protected void changeArrayLoaded(String toChange, String nameToLoad, int width, int height)
	{
			if(toChange.equals("archer"))
			{
				archer_Image = loadArray1D(49, nameToLoad, width, height);
				archerLoaded = true;
			}
			else if(toChange.equals("pikeman"))
			{
				pikeman_Image = loadArray1D(62, nameToLoad, width, height);
				pikemanLoaded = true;
			}
			else if(toChange.equals("shield"))
			{
				shield_Image = loadArray1D(55, nameToLoad, width, height);
				shieldLoaded = true;
			}
			else if(toChange.equals("rogue"))
			{
				rogue_Image = loadArray1D(65, nameToLoad, width, height);
				rogueLoaded = true;
			}
			else if(toChange.equals("mage"))
			{
				mage_Image = loadArray1D(31, nameToLoad, width, height);
				mageLoaded = true;
			}
			else if(toChange.equals("cleric"))
			{
				cleric_Image = loadArray1D(25, nameToLoad, width, height);
				clericLoaded = true;
			}
	}
	/**
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
	/**
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
	/**
	 * Loads image of name given from resources and scales to specified width and height
	 * @return Returns bitmap loaded and resized
	 */
	protected Bitmap loadImage(String imageName, int width, int height)
	{
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), width, height, false);
	}
}