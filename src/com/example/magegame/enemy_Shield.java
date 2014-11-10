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
		if(currentFrame == 54)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		
		if(currentFrame == 46)
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
		if(currentFrame > 46) damage /= 8;
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
				currentFrame = 47;
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
			currentFrame = 47;
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
		if(distanceFound < 40)
		{
			attacking = true;
			playing = true;
			if(mainController.getRandomDouble() > 0.65)
			{
				currentFrame = 47;
			} else
			{
				currentFrame = 21;
			}
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
		if(currentFrame == 28)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 35, y + Math.sin(rads) * 35, mainController.player.x, mainController.player.y);
			if(distanceFound < 25)
			{
				mainController.player.getHit((int)(300*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("sword1");
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 40)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 25, y + Math.sin(rads) * 25, mainController.player.x, mainController.player.y);
			if(distanceFound < 25)
			{
				mainController.player.getHit((int)(450*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("sword2");
				if(mainController.getRandomInt(3) == 0)
				{
					mainController.player.rads = Math.atan2(mainController.player.y-y, mainController.player.x-x);
					mainController.player.stun();
				}
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 50)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 25, y + Math.sin(rads) * 25, mainController.player.x, mainController.player.y);
			if(distanceFound < 25)
			{
				mainController.player.getHit((int)(100*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("earth");
				mainController.player.rads = Math.atan2(mainController.player.y-y, mainController.player.x-x);
				mainController.player.stun();
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
	}
}