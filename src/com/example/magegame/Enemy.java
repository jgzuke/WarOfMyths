/*
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds powerBalls headed towards object and their coordinates velocity etc
 */
package com.example.magegame;
abstract public class Enemy extends Human
{
	private int runTimer = 0;
	protected double lastPlayerX;
	protected double lastPlayerY;
	protected boolean checkedPlayerLast = true;
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
	protected void clearArray(double[] array, int length)
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
	protected void frameCall()
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
	protected boolean checkObstructions(double fromX, double fromY, double moveX, double moveY, double distance, double speed)
	{
		int checkObstructionsCounter = 0;
		boolean checkObstructionsObstructed = false;
		while(checkObstructionsCounter < distance)
		{
			checkObstructionsCounter += speed;
			fromX += moveX;
			fromY += moveY;
			if(mainController.checkHitBack(fromX, fromY))
			{
				checkObstructionsObstructed = true;
				distance = 0;
			}
		}
		return checkObstructionsObstructed;
	}
	protected void getHit(int damage)
	{
		if(!deleted)
		{
			super.getHit(damage);
			mainController.player.sp += damage*0.00005;
			if(deleted)
			{
				mainController.player.sp += 0.25;
				int power = 150;
				mainController.createPowerBallEnemy(0, 10, 0, power, x, y);
				mainController.createPowerBallEnemy(45, 7, 7, power, x, y);
				mainController.createPowerBallEnemy(90, 0, 10, power, x, y);
				mainController.createPowerBallEnemy(135, -7, 7, power, x, y);
				mainController.createPowerBallEnemy(180, -10, 0, power, x, y);
				mainController.createPowerBallEnemy(225, -7, -7, power, x, y);
				mainController.createPowerBallEnemy(270, 0, -10, power, x, y);
				mainController.createPowerBallEnemy(315, 7, -7, power, x, y);
			}
		}
	}
	/*
	 * Rotates to run away from player 
	 */
	protected void runAway()
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
	protected void run()
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
	protected void checkLOS()
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
	protected void checkDanger()
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
	protected double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	protected double getDistanceFound() {
		return distanceFound;
	}
	protected void setDistanceFound(double distanceFound) {
		this.distanceFound = distanceFound;
	}
	abstract protected void frameReactionsDangerLOS();
	abstract protected void frameReactionsDangerNoLOS();
	abstract protected void frameReactionsNoDangerLOS();
	abstract protected void stun(int time);
	protected void setRunTimer(int runTimer) {
		this.runTimer = runTimer;
	}
	abstract protected void frameReactionsNoDangerNoLOS();
	protected int getLevelCurrentPosition() {
		return levelCurrentPosition;
	}
	protected int getPathedToHitLength() {
		return pathedToHitLength;
	}
	protected void setLevels(int i, double levelX, double levelY, double levelXForward, double levelYForward) {
		this.levelX[i] = levelX;
		this.levelY[i] = levelY;
		this.levelXForward[i] = levelXForward;
		this.levelYForward[i] = levelYForward;
	}
	/*
	 * Runs towards a specific x, y point for four frames
	 */
	protected void runToward(double towardsX, double towardsY)
	{
		rads = Math.atan2(towardsY - y, towardsX - x);
		rotation = rads * r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 16, 4))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 16, 4))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 16, 4))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
		setRunTimer(4);
		playing = true;
		if(currentFrame > 48)
		{
			currentFrame = 0;
		}
	}
	/*
	 * Runs random direction for 25 or if not enough space 10 frames
	 */
	protected void runRandom()
	{
		boolean canMove = false;
		rotation = mainController.getRandomInt(360);
		rads = rotation / r2d;
		if(checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 100, 4))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 100, 4))
				{
					runPathChooseCounter = 180;
					canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!checkObstructions(x, y, Math.cos(rads) * 4, Math.sin(rads) * 4, 100, 4))
					{
						runPathChooseCounter = 180;
						canMove = true;
					}
				}
			}
		}
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
		if(canMove)
		{
			setRunTimer(16);
		}
		else
		{
			setRunTimer(7);
		}
		playing = true;
		if(currentFrame > 48)
		{
			currentFrame = 0;
		}
	}
	protected void incrementLevelCurrentPosition()
	{
		levelCurrentPosition ++;
	}
	protected int getCheckLOSTimer() {
		return checkLOSTimer;
	}
	protected void resetCheckLOSTimer() {
		checkLOSTimer = 10;
	}
	protected int getRunTimer() {
		return runTimer;
	}
	protected double getLastPlayerX() {
		return lastPlayerX;
	}
	protected double getLastPlayerY() {
		return lastPlayerY;
	}
	protected void setCheckedPlayerLast(boolean checkedPlayerLast) {
		this.checkedPlayerLast = checkedPlayerLast;
	}
	protected boolean isCheckedPlayerLast() {
		return checkedPlayerLast;
	}
}