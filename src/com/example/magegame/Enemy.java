/*
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds powerBalls headed towards object and their coordinates velocity etc
 */
package com.example.magegame;
abstract public class Enemy extends Human
{
	private int runTimer = 0;
	protected double danger[][] = new double[4][30];
	private double levelX[] = new double[30];
	private double levelY[] = new double[30];
	private double levelXForward[] = new double[30];
	private double levelYForward[] = new double[30];
	private int levelCurrentPosition = 0;
	private int pathedToHitLength = 0;
	protected boolean LOS;
	private int checkLOSTimer = 1;
	protected double distanceFound;
	private int dangerCheckCounter;
	private double pathedToHit[] = new double[30];
	public Enemy()
	{
		danger[0] = levelX;
		danger[1] = levelY;
		danger[2] = levelXForward;
		danger[3] = levelYForward;
	}
	public void clearArray(double[] array, int length)
	{
		for(int i = 0; i < length; i++)
		{
			array[i] = -11111;
		}
	}
	/*
	 * Clears danger arrays, sets current dimensions, and counts timers
	 * @see com.example.magegame.human#frameCall()
	 */
	@
	Override
	public void frameCall()
	{
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
	/*
	 * Checks for a clear line starting at set coordinates and going for a set distance at set velocity
	 * @param fromX Starting x coordinate
	 * @param fromY Starting y coordinate
	 * @param moveX X velocity
	 * @param moveY Y velocity
	 * @param distance Interval at which to check whether line is clear
	 * @param distance Distance to extend line
	 * @return Returns whether line is clear
	 */
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
	/*
	 * Checks whether a point hits any obstructions
	 */
	public void checkHitBack(double X, double Y)
	{
		
		hitBack = false;
		if(X < 97.5 || X > 382.5 || Y < 17.5 || Y > 302.5)
		{
			hitBack = true;
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.getCurrentRectangle(); i++)
			{
				if(hitBack == false)
				{
					if(X > mainController.getObstaclesRectanglesX1(i) && X < mainController.getObstaclesRectanglesX2(i))
					{
						if(Y > mainController.getObstaclesRectanglesY1(i) && Y < mainController.getObstaclesRectanglesY2(i))
						{
							hitBack = true;
						}
					}
				}
			}
		}
		if(hitBack == false)
		{
			for(int i = 0; i < mainController.getCurrentCircle(); i++)
			{
				if(hitBack == false)
				{
					if(Math.pow(X - mainController.getObstaclesCirclesX(i), 2) + Math.pow(Y - mainController.getObstaclesCirclesY(i), 2) < Math.pow(mainController.getObstaclesCirclesRadius(i), 2))
					{
						hitBack = true;
					}
				}
			}
		}
	}
	/*
	 * Rotates to run away from player 
	 */
	public void runAway()
	{
		rads = Math.atan2(-(mainController.player.y - y), -(mainController.player.x - x));
		rotation = rads * r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 40, 4))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
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
	/*
	 * Runs in direction object is rotated for 10 frames
	 */
	public void run()
	{
		runTimer = 10;
		playing = true;
                if(currentFrame > 48)
                {
                    currentFrame = 0;
                }
	}
	/*
	 * Checks whether object can 'see' player
	 */
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
	/*
	 * Checks whether any powerBalls are headed for object
	 */
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
	/*
	 * Checks distance between two points
	 * @return Returns distance
	 */
	public double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	public double getDistanceFound() {
		return distanceFound;
	}
	public void setDistanceFound(double distanceFound) {
		this.distanceFound = distanceFound;
	}
	abstract public void frameReactionsDangerLOS();
	abstract public void frameReactionsDangerNoLOS();
	abstract public void frameReactionsNoDangerLOS();
	public void setRunTimer(int runTimer) {
		this.runTimer = runTimer;
	}
	abstract public void frameReactionsNoDangerNoLOS();
	public int getLevelCurrentPosition() {
		return levelCurrentPosition;
	}
	public int getPathedToHitLength() {
		return pathedToHitLength;
	}
	public void setLevels(int i, double levelX, double levelY, double levelXForward, double levelYForward) {
		this.levelX[i] = levelX;
		this.levelY[i] = levelY;
		this.levelXForward[i] = levelXForward;
		this.levelYForward[i] = levelYForward;
	}
	public void incrementLevelCurrentPosition()
	{
		levelCurrentPosition ++;
	}
	public int getCheckLOSTimer() {
		return checkLOSTimer;
	}
	public void resetCheckLOSTimer() {
		checkLOSTimer = 10;
	}
	public int getRunTimer() {
		return runTimer;
	}
}