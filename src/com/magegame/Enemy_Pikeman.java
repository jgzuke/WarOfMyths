/**
 * Specific object behavior and ai for pikemen
 */
package com.magegame;
public final class Enemy_Pikeman extends Enemy_Muggle
{
	/**
	 * Sets health, weight, and image
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 */
	public Enemy_Pikeman(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = control.imageLibrary.pikeman_Image[0];
		setImageDimensions();
		hp = (int)(4500);//  * control.getDifficultyLevelMultiplier());
		setHpMax(hp);
		weight = 1.5;
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
		visualImage = control.imageLibrary.pikeman_Image[currentFrame];
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
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, control.player.x, control.player.y);
		if(distanceFound < 40)
		{
			currentFrame = 21;
			attacking = true;
			playing = true;
		}
		else
		{
			runTowardPlayer();
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
			if(control.getRandomInt(10) == 0)
			{
				runRandom();
			}
			setCheckedPlayerLast(true);
		}
		else
		{
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			runTowardPlayer();
		}
	}@
	Override
	protected void attacking()
	{
		if(currentFrame == 29)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, control.player.x, control.player.y);
			if(distanceFound < 30)
			{
				control.player.getHit((int)(700*control.getDifficultyLevelMultiplier()));
				control.activity.playEffect("sword1");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.2)
				{
					control.player.rads = Math.atan2(control.player.y-y, control.player.x-x);
					control.player.stun();
				}
			} else
			{
				control.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 48)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, control.player.x, control.player.y);
			if(distanceFound < 30)
			{
				control.player.getHit((int)(400*control.getDifficultyLevelMultiplier()));
				control.activity.playEffect("sword2");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.2)
				{
					control.player.rads = Math.atan2(control.player.y-y, control.player.x-x);
					control.player.stun();
				}
			} else
			{
				control.activity.playEffect("swordmiss");
			}
		}
		if(currentFrame == 53)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 30, y + Math.sin(rads) * 30, control.player.x, control.player.y);
			if(distanceFound < 30)
			{
				control.player.getHit((int)(400*control.getDifficultyLevelMultiplier()));
				control.activity.playEffect("sword2");
				if(control.getRandomDouble()*(0.5+control.getDifficultyLevelMultiplier()) > 1.2)
				{
					control.player.rads = Math.atan2(control.player.y-y, control.player.x-x);
					control.player.stun();
				}
			} else
			{
				control.activity.playEffect("swordmiss");
			}
		}
	}
	@Override
	protected int getType()
	{
		return 2;
	}
}