/*
, * Main enemy ai, cooldowns stats etc
 * @param reactionTimeRating how many frames the enemy takes to react to certain scenarios
 * @param playerAreaProtected whether or not the player is in a mostly enclosed area
 * @param enemyAreaProtected whether or not the main enemy is in a mostly enclosed area
 */
package com.magegame;

public final class Enemy_Archer extends Enemy_Muggle
{
	private double projectileVelocity;
	public Enemy_Archer(Controller creator, int setX, int setY)
	{
		super(creator, setX, setY);
		visualImage = control.imageLibrary.archer_Image[0];
		setImageDimensions();
		hp = (int)(3000 * control.getDifficultyLevelMultiplier());
		setHpMax(hp);
		worth = 4;
		weight = 1;
	}
	/*
	 * Replenishes stats, counts down timers, and checks los etc
	 * @see com.example.magegame.Enemy#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		visualImage = control.imageLibrary.archer_Image[currentFrame];
		if(currentFrame == 48)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		super.frameCall();
	}
	@
	Override
	protected void frameReactionsDangerLOS()
	{
		frameReactionsNoDangerLOS();
	}
	@Override
	protected void frameReactionsDangerNoLOS()
	{
		frameReactionsNoDangerNoLOS();
	}
	@	Override
	protected void frameReactionsNoDangerLOS()
	{
		rads = Math.atan2((control.player.y - y), (control.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, control.player.x, control.player.y);
		if(distanceFound < 200)
		{
			attacking = true;
			playing = true;
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			currentFrame = 21;
		} else
		{
			runTowardPlayer();
		}
	}
	@Override
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
	}
	/*
	 * Releases stored powerBall towards player
	 */
	protected void shoot()
	{
			projectileVelocity = 10*(0.4+control.getDifficultyLevelMultiplier());
			double timeToHit = (checkDistance(x, y, control.player.x, control.player.y))/projectileVelocity;
			timeToHit *= (control.getRandomDouble()*0.7)+0.4;
			double newPX;
			double newPY;
			if(control.player.isTeleporting())
			{
				newPX = control.player.getXSave();
				newPY = control.player.getYSave();
			}
			else
			{
				newPX = control.player.x+(pXVelocity*timeToHit);
				newPY = control.player.y+(pYVelocity*timeToHit);
			}
			double xDif = newPX-x;
			double yDif = newPY-y;
			rads = Math.atan2(yDif, xDif);
			rotation = rads * r2d;
			control.createCrossbowBolt(rotation, Math.cos(rads) * projectileVelocity, Math.sin(rads) * projectileVelocity, 130, x, y);
			control.activity.playEffect("arrowrelease");
	}
	
	@Override
	protected void stun(int time) {
		
	}
	@Override
	protected void attacking()
	{
		if(currentFrame == 38)
		{
			shoot();
		}
	}
	@Override
	protected int getType()
	{
		return 5;
	}
}