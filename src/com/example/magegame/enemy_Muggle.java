/*
 * All enemies but main mage, defines some variables and starts ai reactions
 * @param lastPlayerX Last X coordinate the player was seen at
 * @param lastPlayerY Last Y coordinate the player was seen at
 */
package com.example.magegame;
abstract public class Enemy_Muggle extends Enemy
{
	protected boolean attacking = false;
	protected double lastPlayerX;
	protected double lastPlayerY;
	private boolean checkedPlayerLast = false;
	public Enemy_Muggle(Controller creator, double setX, double setY)
	{
		super();
		mainController = creator;
		width = 30;
		height = 30;
		x = setX;
		y = setY;
		rotation = 0;
		lastPlayerX = x;
		lastPlayerY = y;
		speedCur = 3 + (mainController.getRandomDouble()*1);
	}
	/*
	 * Calls correct ai method, sets correct los and in danger states
	 * @see com.example.magegame.Enemy#frameCall()
	 */
	@
	Override
	public void frameCall()
	{
		if(!attacking && getRunTimer() < 1)
		{
			if(getCheckLOSTimer() < 1)
			{
				checkLOS();
				if(LOS)
				{
					lastPlayerX = mainController.player.x;
					lastPlayerY = mainController.player.y;
					checkedPlayerLast = false;
				}
				resetCheckLOSTimer();
			}
			checkDanger();
			if(getPathedToHitLength() > 0)
			{
				if(LOS)
				{
					frameReactionsDangerLOS();
				}
				else
				{
					frameReactionsDangerNoLOS();
				}
			}
			else
			{
				if(LOS == true)
				{
					frameReactionsNoDangerLOS();
				}
				else
				{
					frameReactionsNoDangerNoLOS();
				}
			}
		}
		else
		{
			if(getRunTimer() < 1)
			{
				attacking();
			}
			else
			{
				x += Math.cos(rads) * speedCur;
				y += Math.sin(rads) * speedCur;
			}
		}
		super.frameCall();
	}
	/*
	 * Runs towards a specific x, y point for four frames
	 */
	public void runToward(double towardsX, double towardsY)
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
	public void runRandom()
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
			setRunTimer(25);
		}
		else
		{
			setRunTimer(10);
		}
		playing = true;
		if(currentFrame > 48)
		{
			currentFrame = 0;
		}
	}
	/*
	 * How object acts during an attack
	 */
	abstract public void attacking();
	public double getLastPlayerX() {
		return lastPlayerX;
	}
	public double getLastPlayerY() {
		return lastPlayerY;
	}
	public void setCheckedPlayerLast(boolean checkedPlayerLast) {
		this.checkedPlayerLast = checkedPlayerLast;
	}
	public boolean isCheckedPlayerLast() {
		return checkedPlayerLast;
	}
	@Override
	public void getHit(int damage)
	{
		super.getHit(damage);
		if(deleted)
		{
			mainController.createPowerBallEnemy(0, 10, 0, 170, x, y);
			mainController.createPowerBallEnemy(45, 7, 7, 170, x, y);
			mainController.createPowerBallEnemy(90, 0, 10, 170, x, y);
			mainController.createPowerBallEnemy(135, -7, 7, 170, x, y);
			mainController.createPowerBallEnemy(180, -10, 0, 170, x, y);
			mainController.createPowerBallEnemy(225, -7, -7, 170, x, y);
			mainController.createPowerBallEnemy(270, 0, -10, 170, x, y);
			mainController.createPowerBallEnemy(315, 7, -7, 170, x, y);
		}
	}
}