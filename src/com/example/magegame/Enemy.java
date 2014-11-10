package com.example.magegame;
abstract public class Enemy extends human
{
	public int runPathChooseCounter;
	public double runPathChooseRotationStore;
	public int runTimer = 0;
	public double danger[][] = new double[4][30];
	public double levelX[] = new double[30];
	public double levelY[] = new double[30];
	public double levelXForward[] = new double[30];
	public double levelYForward[] = new double[30];
	public int levelCurrentPosition = 0;
	private int dangerCheckCounter;
	private double pathedToHit[] = new double[30];
	public int pathedToHitLength = 0;
	public boolean LOS;
	public int checkLOSTimer = 1;
	public int curFrame = 1;
	public double distanceFound;
	public Enemy()
	{}
	public void clearArray(double[] array, int length)
	{
		for(int i = 0; i < length; i++)
		{
			array[i] = -11111;
		}
	}@
	Override
	public void frameCall()
	{
		curFrame++;
		checkLOSTimer--;		
                runTimer--;		
		super.frameCall();
		levelCurrentPosition = 0;
		clearArray(levelX, 30);
		clearArray(levelY, 30);
		clearArray(levelXForward, 30);
		clearArray(levelYForward, 30);
		clearArray(pathedToHit, 30);
		pathedToHitLength = 0;
		setImageDimensions();
	}	
	public boolean checkObstructions(double fromX, double fromY, double moveX, double moveY, double distance, double speed)
	{
		int checkObstructionsCounter = 0;
		boolean checkObstructionsObstructed = false;
		while(checkObstructionsCounter < distance)
		{
			checkObstructionsCounter += speed;
			fromX += moveX;
			fromY += moveY;
			checkHitBack(fromX, fromY);
			if(hitBack == true)
			{
				checkObstructionsObstructed = true;
				distance = 0;
			}
		}
		return checkObstructionsObstructed;
	}
	public void checkHitBack(double X, double Y)
	{
		hitBack = false;
		if(X < 97.5 || X > 382.5 || Y < 17.5 || Y > 302.5)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.obstaclesRectanglesX1.length; i++)
			{
				if(hitBack == false)
				{
					if(X > mainController.obstaclesRectanglesX1[i] && X < mainController.obstaclesRectanglesX2[i])
					{
						if(Y > mainController.obstaclesRectanglesY1[i] && Y < mainController.obstaclesRectanglesY2[i])
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.obstaclesCirclesX.length; i++)
			{
				if(hitBack == false)
				{
					if(Math.pow(X - mainController.obstaclesCirclesX[i], 2) + Math.pow(Y - mainController.obstaclesCirclesY[i], 2) < Math.pow(mainController.obstaclesCirclesRadius[i], 2))
					{
						hitBack = true;
					}
				}
			}
		}
	}
	public void runAway()
	{
		rads = Math.atan2(-(mainController.player.y - y), -(mainController.player.x - x));
		rotation = rads * r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
		{
			runPathChooseCounter = 0;
			runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
	}        
	public void run()
	{
		runTimer = 10;
		playing = true;
                if(currentFrame > 48)
                {
                    currentFrame = 0;
                }
	}
	public void checkLOS()
	{
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		if(!checkObstructions(x, y, Math.cos(rads) * 20, Math.sin(rads) * 20, checkDistance(mainController.player.x, mainController.player.y, x, y) - 20, 20))
		{
			LOS = true;
		}
		else
		{
			LOS = false;
		}
	}
	public void checkDanger()
	{           
		dangerCheckCounter = 0;
		while(dangerCheckCounter < levelCurrentPosition)
		{
			distanceFound = checkDistance(danger[0][dangerCheckCounter], danger[1][dangerCheckCounter], x, y);
			distanceFound = checkDistance((int) Math.abs(danger[0][dangerCheckCounter] + (danger[2][dangerCheckCounter] / 10 * distanceFound)), (int) Math.abs(danger[1][dangerCheckCounter] + (danger[3][dangerCheckCounter] / 10 * distanceFound)), x, y);
			if(distanceFound < 20)
			{
				distanceFound = checkDistance(danger[0][dangerCheckCounter], danger[1][dangerCheckCounter], x, y);
				if(!checkObstructions(danger[0][dangerCheckCounter], danger[1][dangerCheckCounter], danger[2][dangerCheckCounter] * 2, danger[3][dangerCheckCounter] * 2, distanceFound, 20))
				{
					pathedToHit[pathedToHitLength] = dangerCheckCounter;
					pathedToHitLength++;         
				}
			}
			dangerCheckCounter++;
		}
	}
	public double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	abstract public void frameReactionsDangerLOS();
	abstract public void frameReactionsDangerNoLOS();
	abstract public void frameReactionsNoDangerLOS();
	abstract public void frameReactionsNoDangerNoLOS();
}