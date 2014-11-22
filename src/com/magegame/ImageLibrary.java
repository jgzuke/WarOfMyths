/**
 * Loads, stores and resizes all graphics
 */
package com.magegame;
import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public final class ImageLibrary
{
	protected Bitmap[] player_Image = new Bitmap[32];
	protected Bitmap[] enemy_Image = new Bitmap[95];
	protected Bitmap structure_Spawn;
	protected Bitmap[] effects = new Bitmap[4];
	protected Bitmap isPlayer;
	protected int isPlayerWidth;
	protected Bitmap haskey;
	protected Bitmap exitFightPortal;
	protected Bitmap shotPlayer;
	protected Bitmap shotAOEPlayer;
	protected Bitmap shotEnemy;
	protected Bitmap shotAOEEnemy;
	protected Bitmap[] powerUps = new Bitmap[11];
	protected Bitmap[] powerUpBigs = new Bitmap[4];
	protected Bitmap[] coins = new Bitmap[2];
	protected Bitmap[] trans = new Bitmap[10];
	protected Bitmap currentLevel;
	protected Bitmap currentLevelTop;
	protected Bitmap directionsTutorial;
	protected Bitmap backDrop;
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
		player_Image = loadArray1D(32, "human_playerzack", 46, 50);
		isPlayerWidth = 26;
		/*switch(control.activity.currentSkin)
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
		}*/
		isPlayer = loadImage("icon_isplayer", 2*isPlayerWidth, 2*isPlayerWidth);
	}
	/**
	 * loads all required images for all games
	 */
	protected void loadAllImages()
	{
		exitFightPortal = loadImage("icon_exitfightportal", 60, 60);
		loadPlayerImage();
		powerUps = loadArray1D(11, "icon_powerup", 30, 30);
		powerUpBigs = loadArray1D(5, "icon_powerupbig", 70, 70);
		coins = loadArray1D(2, "icon_menu_coin", 30, 30);
		enemy_Image = loadArray1D(95, "human_enemy", 100, 70);//TODO change size

		shotAOEEnemy = loadImage("shotexplodeenemy", 80, 80);
		shotEnemy = loadImage("shotenemy", 40, 3);
		shotAOEPlayer = loadImage("shotexplodeplayer", 80, 80);
		shotPlayer = loadImage("shotplayer", 40, 3);
		effects = loadArray1D(4, "effect", 60, 60);
		haskey = loadImage("icon_haskey", 40, 40);
		isPlayer = loadImage("icon_isplayer", 40, 40);
		loadLevel(control.levelNum, control.levelWidth, control.levelHeight);
		backDrop = loadImage("level_back", 300, 300);
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
		if(backDrop!= null)
		{
			backDrop.recycle();
		}
		backDrop = loadImage("level_back", 300, 300);
		currentLevel = loadImage("level"+correctDigits(levelNum, 4), width, height);
		currentLevelTop = loadImage("leveltop"+correctDigits(levelNum, 4), width, height);
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
		recycleArray(player_Image);
		recycleArray(powerUpBigs);
		recycleArray(powerUps);
		recycleArray(effects);
	}
	/**
	 * recycles desired array of images
	 * @param length length of array
	 * @param array array to recycle
	 */
	protected void recycleArray(Bitmap[] array)
	{
		int length = array.length;
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