/*
 * Specific object behavior and ai for archers
 */
package com.example.magegame;
public final class Enemy_Archer extends Enemy_Muggle
{
	public Enemy_Archer(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = mainController.imageLibrary.pikeman_Image[0];
		setImageDimensions();
		hp = (int)(3000 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}@
	Override
	protected void frameCall()
	{
		if(currentFrame == 159)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		visualImage = mainController.imageLibrary.pikeman_Image[currentFrame];
		super.frameCall();
	}@
	Override
	protected void frameReactionsDangerLOS()
	{
		frameReactionsNoDangerLOS();
	}@
	Override
	protected void frameReactionsDangerNoLOS()
	{
		frameReactionsNoDangerNoLOS();
	}@
	Override
	protected void frameReactionsNoDangerLOS()
	{
		rads = Math.atan2((mainController.getPlayerY() - y), (mainController.getPlayerX() - x));
		rotation = rads * r2d;
		setDistanceFound(checkDistance(x, y, mainController.getPlayerX(), mainController.getPlayerY()));
		if(getDistanceFound() < 30)
		{
			runAway();
		}
		else if(getDistanceFound() < 120)
		{
			currentFrame = 49;
			attacking = true;
			playing = true;
		}
		else
		{
			runToward(getLastPlayerX(), getLastPlayerY());
		}
	}@
	Override
	protected void frameReactionsNoDangerNoLOS()
	{
		setDistanceFound(checkDistance(x, y, getLastPlayerX(), getLastPlayerY()));
		if(isCheckedPlayerLast() || getDistanceFound() < 10)
		{
			hp += 5;
			currentFrame = 0;
			playing = false;
			if(mainController.getRandomInt(10) == 0)
			{
				runRandom();
			}
			setCheckedPlayerLast(true);
		}
		else
		{
			rads = Math.atan2((getLastPlayerY() - y), (getLastPlayerX() - x));
			rotation = rads * r2d;
			runToward(getLastPlayerX(), getLastPlayerY());
		}
	}@
	Override
	protected void attacking()
	{
		if(currentFrame == 79)
		{
			setDistanceFound(checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.getPlayerX(), mainController.getPlayerY()));
			if(getDistanceFound() < 30)
			{
				mainController.player.getHit(700);
			}
		}
		if(currentFrame == 121)
		{
			setDistanceFound(checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.getPlayerX(), mainController.getPlayerY()));
			if(getDistanceFound() < 30)
			{
				mainController.player.getHit(400);
			}
		}
		if(currentFrame == 131)
		{
			setDistanceFound(checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.getPlayerX(), mainController.getPlayerY()));
			if(getDistanceFound() < 30)
			{
				mainController.player.getHit(400);
			}
		}
	}
}