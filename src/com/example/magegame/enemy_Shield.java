/*
 * Object behavior and ai for enemies with sheilds
 */
package com.example.magegame;
abstract public class Enemy_Shield extends Enemy_Muggle
{
	public Enemy_Shield(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
	}@
	Override
	protected void frameCall()
	{
		if(currentFrame == 94)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		if(currentFrame == 84)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		super.frameCall();
	}@
	Override
	protected void getHit(int damage)
	{
		if(85 < currentFrame && currentFrame < 96) damage /= 8;
		super.getHit(damage);
	}@
	Override
	protected void frameReactionsDangerLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 100 && distanceFound > 60)
		{
			if(mainController.getRandomInt(3) == 0)
			{
				rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
				rotation = rads * r2d;
				currentFrame = 86;
				attacking = true;
				playing = true;
			}
		}
		else
		{
			frameReactionsNoDangerLOS();
		}
	}@
	Override
	protected void frameReactionsDangerNoLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 100)
		{
			rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
			rotation = rads * r2d;
			currentFrame = 86;
			attacking = true;
			playing = true;
		}
		else
		{
			frameReactionsNoDangerNoLOS();
		}
	}@
	Override
	protected void frameReactionsNoDangerLOS()
	{
		rads = Math.atan2((mainController.getPlayerY() - y), (mainController.getPlayerX() - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, mainController.getPlayerX(), mainController.getPlayerY());
		if(distanceFound < 20)
		{
			currentFrame = 49;
			attacking = true;
			playing = true;
		}
		else
		{
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			runToward(lastPlayerX, lastPlayerY);
		}
	}@
	Override
	protected void frameReactionsNoDangerNoLOS()
	{
		distanceFound = checkDistance(x, y, lastPlayerX, lastPlayerY);
		if(isCheckedPlayerLast() || distanceFound < 10)
		{
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
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			runToward(lastPlayerX, lastPlayerY);
		}
	}@
	Override
	protected void attacking()
	{
		if(currentFrame == 64)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 20, y + Math.sin(rads) * 20, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit((int)(600*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect(R.raw.sword1);
			} else
			{
				mainController.activity.playEffect(R.raw.sword3);
			}
		}
		if(currentFrame == 78)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 20, y + Math.sin(rads) * 20, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit((int)(400*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect(R.raw.sword2);
			} else
			{
				mainController.activity.playEffect(R.raw.sword3);
			}
		}
	}
}