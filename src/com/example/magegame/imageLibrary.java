package com.example.magegame;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
public final class imageLibrary
{
	public boolean pikemanLoaded = false;
	public boolean swordsmanLoaded = false;
	public boolean axemanLoaded = false;
	public boolean archerLoaded = false;
	public Bitmap[] player_Image = new Bitmap[59]; //		x
	public Bitmap[] mage_Image = new Bitmap[59]; //			x
    public Bitmap[] pikeman_Image = new Bitmap[159];//		x        
    public Bitmap[] swordsman_Image = new Bitmap[159];//	x        
    public Bitmap[] axeman_Image = new Bitmap[122]; //		x        
    public Bitmap[] archer_Image = new Bitmap[125];        
	public Bitmap[][] powerBall_Image = new Bitmap[4][5];//	x
	public Bitmap[] powerBallAOE_Image = new Bitmap[4];//	x
	public Bitmap[][] teleport_Image = new Bitmap[2][15];//	x
	public Bitmap[] warnings = new Bitmap[2];			//	x
	public Bitmap fullScreen1;//							x
	public Bitmap fullScreen2;//							x
	private String getting;
	public Resources res;
	public String packageName;
	private double screenDimensionMultiplier;
	public imageLibrary(Context contextSet, double screenDimensionMultiplierSet)
	{
		BitmapFactory.Options opts=new BitmapFactory.Options();
        opts.inDither=false;
        opts.inPurgeable=true;
        opts.inInputShareable=true;
        opts.inTempStorage=new byte[16 * 1024]; 
		screenDimensionMultiplier = screenDimensionMultiplierSet;
		packageName = contextSet.getPackageName();
		res = contextSet.getResources();		
		getting = "gainsp";
		teleport_Image[0] = loadArray1D(15, "teleportstart");
		teleport_Image[1] = loadArray1D(15, "teleportfinish");
        mage_Image = loadArray1D(59, "mage");
        warnings = loadArray1D(2, "warn");
		fullScreen1 = loadImage("fullscreen0001", 60, 154);
		fullScreen2 = loadImage("fullscreen0002", 60, 154);
		player_Image = loadArray1D(59, "player");
	}
	public void changeArrayLoaded(String toChange, boolean loading)
	{
		if(loading)
		{
			if(toChange.equals("archer"))
			{
				archer_Image = loadArray1D(125, "archer");
				archerLoaded = true;
			} else if(toChange.equals("pikeman"))
			{
				pikeman_Image = loadArray1D(159, "pikeman");
				pikemanLoaded = true;
			} else if(toChange.equals("axeman"))
			{
				axeman_Image = loadArray1D(109, "axeman");
				axemanLoaded = true;
			} else if(toChange.equals("swordsman"))
			{
				swordsman_Image = loadArray1D(109, "swordsman");
				swordsmanLoaded = true;
			}
		} else
		{
			if(toChange.equals("archer"))
			{
				for(int i = 0; i < archer_Image.length; i++)
				{
					archer_Image[i].recycle();
					archer_Image[i] = null;
				}
				archerLoaded = false;
			} else if(toChange.equals("pikeman"))
			{
				for(int i = 0; i < pikeman_Image.length; i++)
				{
					pikeman_Image[i].recycle();
					pikeman_Image[i] = null;
				}
				pikemanLoaded = false;
			} else if(toChange.equals("axeman"))
			{
				for(int i = 0; i < axeman_Image.length; i++)
				{
					axeman_Image[i].recycle();
					axeman_Image[i] = null;
				}
				axemanLoaded = false;
			} else if(toChange.equals("swordsman"))
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
	private Bitmap[][] loadArray2D(int length1, int length2, String start, int width, int height)
    {
        Bitmap[][] newArray = new Bitmap[length1][length2];
        for(int i = 0; i < length1; i++)
        {
        	newArray[i] = loadArray1D(length2, (start + correctDigits(i + 1, 4) + "_"), width, height);
        }
        return newArray;
    }
	private Bitmap[][] loadArray2D(int length1, int length2, String start)
    {
        Bitmap[][] newArray = new Bitmap[length1][length2];
        for(int i = 0; i < length1; i++)
        {
        	newArray[i] = loadArray1D(length2, (start + correctDigits(i + 1, 4) + "_"));
        }
        return newArray;
    }
	public Bitmap[] loadArray1D(int length, String start)
        {
            Bitmap[] newArray = new Bitmap[length];
            for(int i = 0; i < length; i++)
            {
				getting = start + correctDigits(i + 1, 4);
				newArray[i] = loadImage(getting);
            }
            return newArray;
        }
        private Bitmap[] loadArray1D(int length, String start, int width, int height)
        {
            Bitmap[] newArray = new Bitmap[length];
            for(int i = 0; i < length; i++)
            {
				getting = start + correctDigits(i + 1, 4);
				newArray[i] = loadImage(getting, width, height);
            }
            return newArray;
        }
	private String correctDigits(int start, int digits)
	{
		String end = Integer.toString(start);
		while(end.length() < digits)
		{
			end = "0" + end;
		}
		return end;
	}
	public Bitmap loadImage(String imageName)
	{
		Log.e("**********", imageName);
		int imageNumber = res.getIdentifier(imageName , "drawable", packageName);
		Bitmap start = BitmapFactory.decodeResource(res, imageNumber);
		int width = (int)(screenDimensionMultiplier*start.getWidth());
		int height = (int)(screenDimensionMultiplier*start.getHeight());
		Bitmap end = Bitmap.createScaledBitmap(start, width/4, height/4, false);
		start.recycle();
		return end;
	}
	private Bitmap loadImage(String imageName, int width, int height)
	{
		Log.e("**********", imageName);
		int imageNumber = res.getIdentifier(imageName , "drawable", packageName);
		Bitmap start = BitmapFactory.decodeResource(res, imageNumber);
		width = (int)(screenDimensionMultiplier*width);
		height = (int)(screenDimensionMultiplier*height);
		Bitmap end = Bitmap.createScaledBitmap(start, width, height, false);
		start.recycle();
		return end;
	}
}