/*
 * Loads, stores and resizes all graphics
 */
package com.example.magegame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
public final class ImageLibrary
{
	protected boolean pikemanLoaded = false;
	protected boolean swordsmanLoaded = false;
	protected boolean axemanLoaded = false;
	protected boolean archerLoaded = false;
	protected Bitmap[] player_Image = new Bitmap[59];
	protected Bitmap[] mage_Image = new Bitmap[59];
	protected Bitmap[] pikeman_Image = new Bitmap[159]; 
	protected Bitmap[] swordsman_Image = new Bitmap[95];
	protected Bitmap[] axeman_Image = new Bitmap[95]; 
	protected Bitmap[] archer_Image = new Bitmap[125];
	protected Bitmap[][] powerBall_Image = new Bitmap[4][5];
	protected Bitmap[] powerBallAOE_Image = new Bitmap[4];
	protected Bitmap[][] teleport_Image = new Bitmap[2][15];
	protected Bitmap[] warnings = new Bitmap[2];
	private String getting;
	protected Resources res;
	protected String packageName;
	private StartActivity activity;
	protected BitmapFactory.Options opts;
	/*
	 * Loads all images needed for every game
	 */
	public ImageLibrary(Context contextSet, StartActivity activitySet)
	{
		opts = new BitmapFactory.Options();
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inTempStorage = new byte[16 * 1024];
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();
		activity = activitySet;
		loadAllImages();
	}
	protected void loadAllImages()
	{
		teleport_Image[0] = loadArray1D(15, "teleportstart", 160, 160);
		teleport_Image[1] = loadArray1D(15, "teleportfinish", 160, 160);
		mage_Image = loadArray1D(59, "mage", 30, 30);
		warnings[0] = loadImage("warn0001", 147, 27);
		warnings[1] = loadImage("warn0002", 173, 74);
		player_Image = loadArray1D(59, "player", 30, 30);
		if(activity.control != null)
		{
			if(activity.control.gameRunning)
			{
				switch(activity.control.levelNum)
				{
				case 0:
					changeArrayLoaded("swordsman", true);
					break;
				case 1:
					changeArrayLoaded("swordsman", true);
					break;
				case 2:
					changeArrayLoaded("swordsman", true);
					break;
				case 3:
					changeArrayLoaded("swordsman", true);
					break;
				case 4:
					changeArrayLoaded("swordsman", true);
					break;
				case 5:
					changeArrayLoaded("swordsman", true);
					break;
				}
			}
		}
	}
	protected void recycleImages()
	{
		recycleArray(15, teleport_Image[0]);
		recycleArray(15, teleport_Image[1]);
		recycleArray(59, mage_Image);
		recycleArray(59, player_Image);
		recycleArray(2, warnings);
		changeArrayLoaded("archer", false);
		changeArrayLoaded("pikeman", false);
		changeArrayLoaded("axeman", false);
		changeArrayLoaded("swordsman", false);
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
				archer_Image = loadArray1D(125, "archer", 80, 50);
				archerLoaded = true;
			}
			else if(toChange.equals("pikeman") && !pikemanLoaded)
			{
				pikeman_Image = loadArray1D(159, "pikeman", 110, 40);
				pikemanLoaded = true;
			}
			else if(toChange.equals("axeman") && !axemanLoaded)
			{
				axeman_Image = loadArray1D(95, "axeman", 80, 60);
				axemanLoaded = true;
			}
			else if(toChange.equals("swordsman") && !swordsmanLoaded)
			{
				swordsman_Image = loadArray1D(95, "swordsman", 110, 70);
				swordsmanLoaded = true;
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
		opts.inSampleSize = 1;
		int imageNumber = res.getIdentifier(imageName, "drawable", packageName);
		return Bitmap.createScaledBitmap(BitmapFactory.decodeResource(res, imageNumber, opts), width, height, false);
	}
}