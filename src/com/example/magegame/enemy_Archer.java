/*
, * Main enemy ai, cooldowns stats etc
 * @param reactionTimeRating how many frames the enemy takes to react to certain scenarios
 * @param playerAreaProtected whether or not the player is in a mostly enclosed area
 * @param enemyAreaProtected whether or not the main enemy is in a mostly enclosed area
 */
package com.example.magegame;

public final class Enemy_Archer extends Enemy_Muggle
{
	private double projectileVelocity;
	private double pXSpot=0;
	private double pYSpot=0;
	private double pXVelocity=0;
	private double pYVelocity=0;
	public Enemy_Archer(Controller creator, int setX, int setY)
	{
		super(creator, setX, setY);
		visualImage = mainController.imageLibrary.archer_Image[0];
		setImageDimensions();
		hp = (int)(3000 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}
	/*
	 * Replenishes stats, counts down timers, and checks los etc
	 * @see com.example.magegame.Enemy#frameCall()
	 */
	@
	Override
	protected void frameCall()
	{
		pXVelocity = mainController.player.x-pXSpot;
		pYVelocity = mainController.player.y-pYSpot;
		pXSpot = mainController.player.x;
		pYSpot = mainController.player.y;
		visualImage = mainController.imageLibrary.archer_Image[currentFrame];
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
		rads = Math.atan2((mainController.player.y - y), (mainController.player.x - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, mainController.player.x, mainController.player.y);
		if(distanceFound < 200)
		{
			attacking = true;
			playing = true;
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			currentFrame = 21;
		} else
		{
			runToward(lastPlayerX, lastPlayerY);
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
	}
	/*
	 * Releases stored powerBall towards player
	 */
	protected void shoot()
	{
			projectileVelocity = 10*(0.4+mainController.getDifficultyLevelMultiplier());
			double timeToHit = (checkDistance(x, y, mainController.player.x, mainController.player.y))/projectileVelocity;
			timeToHit *= (mainController.getRandomDouble()*0.7)+0.4;
			double newPX;
			double newPY;
			if(mainController.player.isTeleporting())
			{
				newPX = mainController.player.getXSave();
				newPY = mainController.player.getYSave();
			}
			else
			{
				newPX = mainController.player.x+(pXVelocity*timeToHit);
				newPY = mainController.player.y+(pYVelocity*timeToHit);
			}
			double xDif = newPX-x;
			double yDif = newPY-y;
			rads = Math.atan2(yDif, xDif);
			rotation = rads * r2d;
			mainController.createCrossbowBolt(rotation, Math.cos(rads) * projectileVelocity, Math.sin(rads) * projectileVelocity, 130, x, y);
			mainController.activity.playEffect("arrowrelease");
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