/**
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds Proj_Trackers headed towards object and their coordinates velocity etc
 */
package com.magegame;

abstract public class Enemy extends Human
{
	protected boolean rogue = false;
	private int runTimer = 0;
	protected int worth = 3;
	protected double lastPlayerX;
	protected double lastPlayerY;
	protected boolean sick = false;
	protected boolean checkedPlayerLast = true;
	protected double danger[][] = new double[4][30];
	private double levelX[] = new double[30];
	private double levelY[] = new double[30];
	private double levelXForward[] = new double[30];
	private double levelYForward[] = new double[30];
	protected int levelCurrentPosition = 0;
	protected int pathedToHitLength = 0;
	protected boolean LOS;
	private int checkLOSTimer = 1;
	protected double distanceFound;
	private int dangerCheckCounter;
	protected boolean keyHolder = false;
	private double pathedToHit[] = new double[30];
	protected int radius = 20;
	protected double pXVelocity=0;
	protected double pYVelocity=0;
	private double pXSpot=0;
	private double pYSpot=0;
	/**
	 * sets danger arrays, speed and control object
	 * @param creator control object
	 */
	public Enemy(Controller creator)
	{
		control = creator;
		danger[0] = levelX;
		danger[1] = levelY;
		danger[2] = levelXForward;
		danger[3] = levelYForward;
		speedCur = 1.5 + (Math.pow(control.getDifficultyLevelMultiplier(), 0.4)*2.6);
		speedCur *= 1.2;
	}
	/**
	 * clears desired array
	 * @param array array to clear
	 * @param length length of array to clear
	 */
	protected void clearArray(double[] array, int length)
	{
		for(int i = 0; i < length; i++)
		{
			array[i] = -11111;
		}
	}
	/**
	 * Clears danger arrays, sets current dimensions, and counts timers
	 */
	@
	Override
	protected void frameCall()
	{
		if(sick)
		{
			hp -= 20;
			getHit(0);
		}
		pXVelocity = control.player.x-pXSpot;
		pYVelocity = control.player.y-pYSpot;
		pXSpot = control.player.x;
		pYSpot = control.player.y;
		if(control.enemyRegen)
		{
			hp += 40;
		}
		hp += 4;
		checkLOSTimer--;		
                runTimer--;		
		super.frameCall();
		clearArray(levelX, 30);
		clearArray(levelY, 30);
		clearArray(levelXForward, 30);
		clearArray(levelYForward, 30);
		clearArray(pathedToHit, 30);
		setImageDimensions();
		double xdif = x - control.player.x;
		double ydif = y - control.player.y;
		double movementX;
		double movementY;
		double moveRads;
		if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < Math.pow(radius, 2))
		{
			moveRads = Math.atan2(ydif, xdif);
			movementX = x - (Math.cos(moveRads) * radius) - control.player.x;
			movementY = y - (Math.sin(moveRads) * radius) - control.player.y;
			double added = weight+control.player.weight;
			if(control.player.rollTimer>0)
			{
				control.player.x += movementX*(weight/added)/2;
				control.player.y += movementY*(weight/added)/2;
				x -= movementX*(control.player.weight/added)/3;
				y -= movementY*(control.player.weight/added)/3;
			} else
			{
				control.player.x += movementX*(weight/added);
				control.player.y += movementY*(weight/added);
				x -= movementX*(control.player.weight/added);
				y -= movementY*(control.player.weight/added);
			}
		}
		for(int i = 0; i < control.enemies.length; i++)
		{
			if(control.enemies[i] != null&& control.enemies[i].x != x)
			{
				xdif = x - control.enemies[i].x;
				ydif = y - control.enemies[i].y;
				if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < Math.pow(radius, 2))
				{
					moveRads = Math.atan2(ydif, xdif);
					movementX = x - (Math.cos(moveRads) * radius) - control.enemies[i].x;
					movementY = y - (Math.sin(moveRads) * radius) - control.enemies[i].y;
					double added = weight+control.enemies[i].weight;
					control.enemies[i].x += movementX*(weight/added);
					control.enemies[i].y += movementY*(weight/added);
					x -= movementX*(control.enemies[i].weight/added);
					y -= movementY*(control.enemies[i].weight/added);
				}
			}
		}
	}
	/**
	 * Takes a sent amount of damage, modifies based on shields etc.
	 * if health below 0 kills enemy
	 * @param damage amount of damage to take
	 */
	protected void getHit(double damage)
	{
		if(!deleted)
		{
			damage /= 1.2;
			if(control.player.powerUpTimer>0 && control.player.powerID == 4)
			{
				damage *= 1.5*Math.pow((double)control.activity.wApollo/10, 0.7);
			}
			super.getHit(damage);
			control.player.abilityTimer_burst += damage*control.activity.bReplentish/30;
			control.player.abilityTimer_roll += damage*control.activity.bReplentish/50;
			control.player.abilityTimerTransformed_pound += damage*control.activity.bReplentish/50;
			control.player.abilityTimer_Proj_Tracker += damage*control.activity.bReplentish/100;
			control.player.sp += damage*0.00003;
			if(deleted)
			{
				control.player.sp += 0.15;
				control.createProj_TrackerEnemyAOE(x, y, 140, false);
				if(!sick)
				{
					if(keyHolder)
					{
						control.createKey(x, y);
					} else
					{
						if(control.getRandomDouble()>0.7)
						{
							control.createPowerUp(x, y);
						}
					}
					for(int i = 0; i < worth; i ++)
					{
						double rads = control.getRandomDouble()*6.28;
						if(worth-i>20)
						{
							control.createCoin20(x+Math.cos(rads)*12, y+Math.sin(rads)*12);
							i+=19;
						} else if(worth-i>5)
						{
							control.createCoin5(x+Math.cos(rads)*12, y+Math.sin(rads)*12);
							i+=4;
						} else
						{
							control.createCoin1(x+Math.cos(rads)*12, y+Math.sin(rads)*12);
						}
					}
				}
				control.activity.playEffect("burst");
			}
		}
	}
	/**
	 * Rotates to run away from player 
	 */
	protected void runAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		int distance = (int)checkDistance(x, y, control.player.x,  control.player.y);
		if(control.checkObstructionsAll(x, y, rads, distance))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y, rads, 40))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(x, y,  rads, 40))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
	}        
	/**
	 * Runs in direction object is rotated for 10 frames
	 */
	protected void run()
	{
		runTimer = 10;
		playing = true;
        currentFrame = 0;
	}
	/**
	 * Checks whether object can 'see' player
	 */
	protected void checkLOS()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		if(!control.checkObstructionsPointTall((float)x, (float)y, (float)control.player.x, (float)control.player.y))
		{
			LOS = true;
		}
		else
		{
			LOS = false;
		}
	}
	/**
	 * Checks whether any Proj_Trackers are headed for object
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
				if(!control.checkObstructionsPointTall((float)danger[0][dangerCheckCounter], (float)danger[1][dangerCheckCounter], (float)x, (float)y))
				{
					pathedToHit[pathedToHitLength] = dangerCheckCounter;
					pathedToHitLength++;         
				}
			}
			dangerCheckCounter++;
		}
	}
	/**
	 * Checks distance between two points
	 * @return Returns distance
	 */
	protected double checkDistance(double fromX, double fromY, double toX, double toY)
	{
		return Math.sqrt((Math.pow(fromX - toX, 2)) + (Math.pow(fromY - toY, 2)));
	}
	/**
	 * returns last distance found
	 * @return distanceFound
	 */
	protected double getDistanceFound() {
		return distanceFound;
	}
	/**
	 * sets distanceFound
	 * @param distanceFound sets to distanceFound
	 */
	protected void setDistanceFound(double distanceFound) {
		this.distanceFound = distanceFound;
	}
	/**
	 * reaction when in danger with LOS
	 */
	abstract protected void frameReactionsDangerLOS();
	/**
	 * reaction when in danger with no LOS
	 */
	abstract protected void frameReactionsDangerNoLOS();
	/**
	 * reaction when in no danger with LOS
	 */
	abstract protected void frameReactionsNoDangerLOS();
	/**
	 * returns type
	 * @return type of enemy
	 */
	abstract protected int getType();
	/**
	 * stuns enemy
	 * @param time time to stun enemy for
	 */
	abstract protected void stun(int time);
	/**
	 * returns roll timer
	 * @return roll timer
	 */
	abstract protected int getRollTimer();
	/**
	 * sets run timer
	 * @param runTimer sets to run timer
	 */
	protected void setRunTimer(int runTimer) {
		this.runTimer = runTimer;
	}
	/**
	 * reaction when in no danger with no LOS
	 */
	abstract protected void frameReactionsNoDangerNoLOS();
	/**
	 * returns levelCurrentPosition
	 * @return levelCurrentPosition
	 */
	protected int getLevelCurrentPosition() {
		return levelCurrentPosition;
	}
	/**
	 * returns pathedToHitLength
	 * @return pathedToHitLength
	 */
	protected int getPathedToHitLength() {
		return pathedToHitLength;
	}
	/**
	 * sets a certain index in danger arrays
	 * @param i index to set
	 * @param levelX x position of danger
	 * @param levelY y position of danger
	 * @param levelXForward x velocity of danger
	 * @param levelYForward y velocity of danger
	 */
	protected void setLevels(int i, double levelX, double levelY, double levelXForward, double levelYForward) {
		this.levelX[i] = levelX;
		this.levelY[i] = levelY;
		this.levelXForward[i] = levelXForward;
		this.levelYForward[i] = levelYForward;
	}
	/**
	 * Runs towards player, if you cant, run randomly
	 */
	protected void runTowardPlayer()
	{
		//TODO
		double dX = control.player.x;
		double dY = control.player.y;
		if(Math.pow(dX-x, 2)+Math.pow(dY-y, 2)<Math.pow(dX+pXVelocity-x, 2)+Math.pow(dY+pYVelocity-y, 2))
		{
			double timeToHit = (checkDistance(x, y, dX, dY))/speedCur;
			dX += (pXVelocity*timeToHit);
			dY += (pYVelocity*timeToHit);
		}
		rads = Math.atan2(dY - y, dX - x);
		rotation = rads * r2d;
		if(control.checkObstructionsAll(x, y, rads, 32))
		{
			if(!runTowardDistanceGood(dX, dY, 20))
			{
				if(!runTowardDistanceGood(dX, dY, 40))
				{
					if(!runTowardDistanceGood(dX, dY, 60))
					{
						if(!runAroundCorner(dX, dY))
						{
							runRandom();
						}
					}
				}
			}
		} else
		{
			setRunTimer(8);
		}
		playing = true;
	}
	/**
	 * runs towards a set x and y
	 * @param towardsX destination x value
	 * @param towardsY destination y value
	 * @param distance distance to run
	 * @return whether it is possible or not to run here
	 */
	protected boolean runTowardDistanceGood(double towardsX, double towardsY, int distance)
	{
		int runPathChooseCounter = 0;
		double runPathChooseRotationStore = rotation;
		boolean goodMove = false;
		while(runPathChooseCounter < 180)
		{
			runPathChooseCounter += 10;
			rotation = runPathChooseRotationStore + runPathChooseCounter;
			rads = rotation / r2d;
			if(!control.checkObstructionsAll(x, y,rads, distance))
			{
				if(!control.checkObstructionsPointAll((float)towardsX, (float)towardsY, (float)(x+Math.cos(rads)*distance), (float)(y+Math.sin(rads)*distance)))
				{
					runPathChooseCounter = 180;
					goodMove = true;
				}
			}
			else
			{
				rotation = runPathChooseRotationStore - runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y,rads, distance))
				{
					if(!control.checkObstructionsPointAll((float)towardsX, (float)towardsY, (float)(x+Math.cos(rads)*distance), (float)(y+Math.sin(rads)*distance)))
					{
						runPathChooseCounter = 180;
						goodMove = true;
					}
				}
			}
		}
		setRunTimer(distance/4);
		return goodMove;
	}
	/**
	 * check whether you can run around a corner to player
	 * @param towardsX destination x value
	 * @param towardsY destination y value
	 * @return whether it is possible to get to player going around the corner
	 */
	protected boolean runAroundCorner(double towardsX, double towardsY)
	{
		int runPathChooseCounter = 0;
		double runPathChooseRotationStore = rotation;
		boolean goodMove = false;
		while(runPathChooseCounter < 180)
		{
			runPathChooseCounter += 10;
			rotation = runPathChooseRotationStore + runPathChooseCounter;
			rads = rotation / r2d;
			if(!control.checkObstructionsAll(x, y,rads, 80))
			{
				double newX = (x+Math.cos(rads)*80);
				double newY = (y+Math.sin(rads)*80);
				if(ranAroundCorner(towardsX, towardsY, newX, newY))
				{
					runPathChooseCounter = 180;
					goodMove = true;
				}
			} else
			{
				rotation = runPathChooseRotationStore - runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y,rads, 80))
				{
					float newX = (float)(x+Math.cos(rads)*80);
					float newY = (float)(y+Math.sin(rads)*80);
					if(ranAroundCorner(towardsX, towardsY, newX, newY))
					{
						runPathChooseCounter = 180;
						goodMove = true;
					}
				}
			}
		}
		setRunTimer(15);
		return goodMove;
	}
	/**
	 * part of runAroundCorner
	 * @param towardsX destination x value
	 * @param towardsY destination y value
	 * @param newX enemies x after first segment of movement
	 * @param newY enemies y after first segment of movement
	 * @return whether you can run to player from here
	 */
	protected boolean ranAroundCorner(double towardsX, double towardsY, double newX, double newY)
	{
		int runPathChooseCounter = 0;
		double runPathChooseRotationStore = rotation;
		boolean goodMove = false;
		double testRads;
		double testRotation;
		while(runPathChooseCounter < 180)
		{
			runPathChooseCounter += 10;
			testRotation = runPathChooseRotationStore + runPathChooseCounter;
			testRads = testRotation / r2d;
			if(!control.checkObstructionsAll(newX, newY,testRads, 80))
			{
				float endX = (float)(newX+Math.cos(testRads)*80);
				float endY = (float)(newY+Math.sin(testRads)*80);
				if(!control.checkObstructionsPointAll((float)towardsX, (float)towardsY, endX, endY))
				{
					runPathChooseCounter = 180;
					goodMove = true;
				}
			}
			else
			{
				testRotation = runPathChooseRotationStore - runPathChooseCounter;
				testRads = testRotation / r2d;
				if(!control.checkObstructionsAll(newX, newY,testRads, 80))
				{
					float endX = (float)(newX+Math.cos(testRads)*80);
					float endY = (float)(newY+Math.sin(testRads)*80);
					if(!control.checkObstructionsPointAll((float)towardsX, (float)towardsY, endX, endY))
					{
						runPathChooseCounter = 180;
						goodMove = true;
					}
				}
			}
		}
		return goodMove;
	}	
	/**
	 * Runs random direction for 25 or if not enough space 10 frames
	 */
	protected void runRandom()
	{
		boolean canMove = false;
		rotation = control.getRandomInt(360);
		rads = rotation / r2d;
		if(control.checkObstructionsAll(x, y,rads, (int)(speedCur*20)))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y,rads, (int)(speedCur*20)))
				{
					runPathChooseCounter = 180;
					canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(x, y,rads, (int)(speedCur*20)))
					{
						runPathChooseCounter = 180;
						canMove = true;
					}
				}
			}
		}
		if(control.checkObstructionsAll(x, y,rads, (int)(speedCur*10)))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y,rads, (int)(speedCur*10)))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(x, y,rads, (int)(speedCur*10)))
					{
						runPathChooseCounter = 180;
					}
				}
			}
		}
		if(canMove)
		{
			setRunTimer(20);
		}
		else
		{
			setRunTimer(10);
		}
		playing = true;
	}
	protected void baseHp() {
		hp *= Math.pow(control.getDifficultyLevelMultiplier(), ((double)hp/10000));
	}
	/**
	 * adds 1 to levelCurrentPosition
	 */
	protected void incrementLevelCurrentPosition()
	{
		levelCurrentPosition ++;
	}
	/**
	 * returns checkLOSTimer
	 * @return checkLOSTimer
	 */
	protected int getCheckLOSTimer() {
		return checkLOSTimer;
	}
	/**
	 * resets checkLOSTimer to 10
	 */
	protected void resetCheckLOSTimer() {
		checkLOSTimer = 10;
	}
	/**
	 * returns runTimer
	 * @return runTimer
	 */
	protected int getRunTimer() {
		return runTimer;
	}
	/**
	 * returns last known player x
	 * @return last known player x
	 */
	protected double getLastPlayerX() {
		return lastPlayerX;
	}
	/**
	 * returns last known player y
	 * @return last known player y
	 */
	protected double getLastPlayerY() {
		return lastPlayerY;
	}
	/**
	 * sets whether you have checked where you saw the player last for his existence
	 * @param checkedPlayerLast have you checked where you saw him
	 */
	protected void setCheckedPlayerLast(boolean checkedPlayerLast) {
		this.checkedPlayerLast = checkedPlayerLast;
	}
	/**
	 * returns whether players last whereabouts have been investigated
	 * @return whether players last whereabouts have been investigated
	 */
	protected boolean isCheckedPlayerLast() {
		return checkedPlayerLast;
	}
}