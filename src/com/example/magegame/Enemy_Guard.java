/*
 * Specific object behavior and ai for pikemen
 */
package com.example.magegame;
public final class Enemy_Guard extends Human
{
	private int runTimer = 0;
	private double xdif;
	private double ydif;
	private double movementX;
	private double movementY;
	private double moveRads;
	public Enemy_Guard(Controller creator, double setX, double setY)
	{
		super();
		mainController = creator;
		width = 30;
		height = 30;
		x = setX;
		y = setY;
		rotation = 0;
		speedCur = 4;
		visualImage = mainController.imageLibrary.swordsman_Image[0];
		setImageDimensions();
	}
	@
	Override
	protected void frameCall()
	{
		if(runTimer < 1)
		{
			currentFrame = 0;
			playing = false;
			if(mainController.getRandomInt(50) == 0)
			{
				runRandom();
			}
		} else
		{
			runTimer--;
			x+=Math.cos(rads)*4;
			y+=Math.sin(rads)*4;
		}
		for(int i = 0; i < mainController.guards.length; i++)
		{
			if(mainController.guards[i] != null&& mainController.guards[i].x != x)
			{
				xdif = x - mainController.guards[i].x;
				ydif = y - mainController.guards[i].y;
				if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < 400)
				{
					moveRads = Math.atan2(ydif, xdif);
					movementX = x - (Math.cos(moveRads) * 20) - mainController.guards[i].x;
					movementY = y - (Math.sin(moveRads) * 20) - mainController.guards[i].y;
					mainController.guards[i].x += movementX/2;
					mainController.guards[i].y += movementY/2;
					x -= movementX/2;
					y -= movementY/2;
				}
			}
		}
		super.frameCall();
		visualImage = mainController.imageLibrary.swordsman_Image[currentFrame];
	}
	protected void runRandom()
	{
		boolean canMove = false;
		rotation = mainController.getRandomInt(360);
		rads = rotation / r2d;
		if(mainController.checkObstructionsAll(x, y,rads, 100))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!mainController.checkObstructionsAll(x, y,rads, 100))
				{
					runPathChooseCounter = 180;
					canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!mainController.checkObstructionsAll(x, y,rads, 100))
					{
						runPathChooseCounter = 180;
						canMove = true;
					}
				}
			}
		} else
		{
			canMove = true;
		}
		if(canMove)
		{
			runTimer=20;
			playing = true;
		}
	}
}