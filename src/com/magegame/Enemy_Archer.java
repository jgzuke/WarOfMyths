/**
 * AI and variables for archers
 */
package com.magegame;

public final class Enemy_Archer extends Enemy_Muggle
{
	private double projectileVelocity;
	/**
	 * Sets health, worth, and image
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 */
	public Enemy_Archer(Controller creator, int setX, int setY)
	{
		super(creator, setX, setY); //sets x, y and creator
		visualImage = control.imageLibrary.archer_Image[0];
		setImageDimensions();
		hp = 2300;
		baseHp();
		setHpMax(hp);
		worth = 4;
		weight = 1;
	}
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
	@ Override
	protected void frameReactionsDangerLOS()
	{
		frameReactionsNoDangerLOS();
	}
	@ Override
	protected void frameReactionsDangerNoLOS()
	{
		frameReactionsNoDangerNoLOS();
	}
	@ Override
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
			if(!control.checkObstructionsPointAll((float)x, (float)y, (float)lastPlayerX, (float)lastPlayerY))
			{
				runTowardPlayer();
			} else
			{
				attacking = true;
				playing = true;
				rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
				rotation = rads * r2d;
				currentFrame = 21;
			}
		}
	}
	@Override
	protected void frameReactionsNoDangerNoLOS()
	{		
		distanceFound = checkDistance(x, y, lastPlayerX, lastPlayerY); // lastPlayerX and Y are the last seen coordinates
		if(isCheckedPlayerLast() || distanceFound < 10)
		{
			currentFrame = 0;
			playing = false;
			if(control.getRandomInt(10) == 0) // around ten frames of pause between random wandering
			{
				runRandom();
			}
			setCheckedPlayerLast(true); // has checked where player was last seen
		}
		else
		{
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x)); // move towards last seen coordinates
			rotation = rads * r2d;
			runTowardPlayer();
		}
	}
	/**
	 * Releases arrow towards player
	 */
	protected void shoot()
	{
			projectileVelocity = 10*(0.4+control.getDifficultyLevelMultiplier());
			double timeToHit = (checkDistance(x, y, control.player.x, control.player.y))/projectileVelocity;
			timeToHit *= (control.getRandomDouble()*0.7)+0.4;
			double newPX;
			double newPY;												//SHOOTS AHEAD OF PLAYER BASED ON VELOCITY LAST FRAME
				newPX = control.player.x+(pXVelocity*timeToHit);
				newPY = control.player.y+(pYVelocity*timeToHit);
			double xDif = newPX-x;
			double yDif = newPY-y;
			rads = Math.atan2(yDif, xDif); // ROTATES TOWARDS PLAYER
			rotation = rads * r2d;
			control.createCrossbowBolt(rotation, Math.cos(rads) * projectileVelocity, Math.sin(rads) * projectileVelocity, 130, x, y);
			control.activity.playEffect("arrowrelease");
	}
	@Override
	protected void stun(int time) {
		// archers cannnot be stunned
	}
	@Override
	protected void attacking()
	{
		if(currentFrame == 38) // at the right point in animation fire arrow
		{
			shoot();
		}
	}
	@Override
	protected int getType()
	{
		return 5; // 5 is the enemy type of archers
	}
}