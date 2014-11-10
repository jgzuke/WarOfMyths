/*
 * All enemies, sets reaction methods, contains checks and mathematical functions
 * @param danger holds powerBalls headed towards object and their coordinates velocity etc
 */
package com.magegame;

import android.util.Log;

abstract public class Enemy extends Human
{
	protected boolean rogue = false;
	private int runTimer = 0;
	protected int worth = 3;
	protected double lastPlayerX;
	protected double lastPlayerY;
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
	public Enemy(Controller creator)
	{
		control = creator;
		danger[0] = levelX;
		danger[1] = levelY;
		danger[2] = levelXForward;
		danger[3] = levelYForward;
		speedCur = 1.5 + (Math.pow(control.getDifficultyLevelMultiplier(), 0.7)*2.5);
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
			control.player.x += movementX*(weight/added);
			control.player.y += movementY*(weight/added);
			x -= movementX*(control.player.weight/added);
			y -= movementY*(control.player.weight/added);
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
	protected void getHit(int damage)
	{
		if(!deleted)
		{
			if(control.player.powerUpTimer>0 && control.player.powerID == 4)
			{
				damage *= 1.5*Math.pow((double)control.activity.wApollo/10, 0.7);
			}
			super.getHit(damage);
			control.player.sp += damage*0.00003;
			if(deleted)
			{
				control.player.sp += 0.15;
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
				control.createPowerBallEnemyAOE(x, y, 140, false);
				for(int i = 0; i < worth; i ++)
				{
					double rads = control.getRandomDouble()*6.28;
					if(worth-i>20)
					{
						control.createCoin20(x+Math.cos(rads)*17, y+Math.sin(rads)*17);
						i+=19;
					} else if(worth-i>5)
					{
						control.createCoin5(x+Math.cos(rads)*17, y+Math.sin(rads)*17);
						i+=4;
					} else
					{
						control.createCoin1(x+Math.cos(rads)*17, y+Math.sin(rads)*17);
					}
				}
				control.activity.playEffect("burst");
			}
		}
	}
	/*
	 * Rotates to run away from player 
	 */
	protected void runAway()
	{
		rads = Math.atan2(-(control.player.y - y), -(control.player.x - x));
		rotation = rads * r2d;
		int distance = (int)checkDistance(x, y, control.getPlayerX(), control.getPlayerY());
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
	/*
	 * Runs in direction object is rotated for 10 frames
	 */
	protected void run()
	{
		runTimer = 10;
		playing = true;
        currentFrame = 0;
	}
	/*
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
				if(!control.checkObstructionsPointTall((float)danger[0][dangerCheckCounter], (float)danger[1][dangerCheckCounter], (float)x, (float)y))
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
	abstract protected int getType();
	abstract protected void stun(int time);
	abstract protected int getRollTimer();
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
	/*protected void runToward(double towardsX, double towardsY)
	{
		runTowardRecurse(x, y, towardsX, towardsY);
		setRunTimer(4);
		playing = true;
		if(currentFrame > 20)
		{
			currentFrame = 0;
		}
	}
	protected boolean runTowardRecurse(double X, double Y, double towardsX, double towardsY)
	{
		boolean isClear = false;
		rads = Math.atan2(towardsY - Y, towardsX - X);
		rotation = rads * r2d;
		if(control.checkObstructionsAll(X, Y, rads, 25))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 15;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(X, Y,rads, 25))
				{
					if(runTowardRecurse(X+(Math.cos(rads)*25), X+(Math.sin(rads)*25), towardsX, towardsY))
					{
						runPathChooseCounter = 180;
						isClear = true;
					}
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(X, Y,rads, 25))
					{
						if(runTowardRecurse(X+(Math.cos(rads)*25), X+(Math.sin(rads)*25), towardsX, towardsY))
						{
							runPathChooseCounter = 180;
							isClear = true;
						}
					}
				}
			}
		} else
		{
			if(!(checkDistance(towardsX, towardsY, X, Y)<25))
			{
				if(runTowardRecurse(X+(Math.cos(rads)*25), X+(Math.sin(rads)*25), towardsX, towardsY))
				{
					isClear = true;
				}
			}
		}
		return isClear;
	}*/
	
	
	
	
	/*
	 * Runs random direction for 25 or if not enough space 10 frames
	 */
	protected void runRandom()
	{
		boolean canMove = false;
		rotation = control.getRandomInt(360);
		rads = rotation / r2d;
		if(control.checkObstructionsAll(x, y,rads, 100))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y,rads, 100))
				{
					runPathChooseCounter = 180;
					canMove = true;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(x, y,rads, 100))
					{
						runPathChooseCounter = 180;
						canMove = true;
					}
				}
			}
		}
		if(control.checkObstructionsAll(x, y,rads, 40))
		{
			int runPathChooseCounter = 0;
			double runPathChooseRotationStore = rotation;
			while(runPathChooseCounter < 180)
			{
				runPathChooseCounter += 10;
				rotation = runPathChooseRotationStore + runPathChooseCounter;
				rads = rotation / r2d;
				if(!control.checkObstructionsAll(x, y,rads, 40))
				{
					runPathChooseCounter = 180;
				}
				else
				{
					rotation = runPathChooseRotationStore - runPathChooseCounter;
					rads = rotation / r2d;
					if(!control.checkObstructionsAll(x, y,rads, 40))
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