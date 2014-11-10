/*
 * Specific object behavior and ai for pikemen
 */
package com.example.magegame;
public final class Enemy_Pikeman extends Enemy_Muggle
{
	public Enemy_Pikeman(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = mainController.imageLibrary.pikeman_Image[0];
		setImageDimensions();
		hp = (int)(4000 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}@
	Override
	protected void frameCall()
	{
		if(currentFrame == 61)
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
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, mainController.player.x, mainController.player.y);
		if(distanceFound < 40)
		{
			currentFrame = 21;
			attacking = true;
			playing = true;
		}
		else
		{
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
		if(currentFrame == 29)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit((int)(700*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("sword1");
				if(mainController.getRandomDouble()*(0.5+mainController.getDifficultyLevelMultiplier()) > 1.2)
				{
					mainController.player.rads = Math.atan2(mainController.player.y-y, mainController.player.x-x);
					mainController.player.stun();
				}
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 48)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit((int)(400*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("sword2");
				if(mainController.getRandomDouble()*(0.5+mainController.getDifficultyLevelMultiplier()) > 1.2)
				{
					mainController.player.rads = Math.atan2(mainController.player.y-y, mainController.player.x-x);
					mainController.player.stun();
				}
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 53)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit((int)(400*mainController.getDifficultyLevelMultiplier()));
				mainController.activity.playEffect("sword2");
				if(mainController.getRandomDouble()*(0.5+mainController.getDifficultyLevelMultiplier()) > 1.2)
				{
					mainController.player.rads = Math.atan2(mainController.player.y-y, mainController.player.x-x);
					mainController.player.stun();
				}
			} else
			{
				mainController.activity.playEffect("swordmiss");
			}
		}
	}
}