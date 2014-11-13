/**
 * AI and variables for archers
 */
package com.magegame;

public final class Enemy_Cleric extends Enemy_Muggle
{
	private double projectileVelocity;
	/**
	 * Sets health, worth, and image
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 */
	public Enemy_Cleric(Controller creator, int setX, int setY)
	{
		super(creator, setX, setY); //sets x, y and creator
		visualImage = control.imageLibrary.archer_Image[0];
		setImageDimensions();
		hp = 2000;
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
		if(currentFrame == 25)
		{
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
		frameReactionsNoDangerNoLOS();
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
	private int pickEnemy()
	{
		int numEnemies = control.enemies.size();
		//Sort to find closest enemy in sight needing hp and heal
				// if can see player shoot
				//if no enemies run randomly
		double[] distances = new double[numEnemies];
		for(int i = 0; i < numEnemies; i++)
		{
			if(control.enemies.get(i) != null)
			{
				double xDif = x - control.enemies.get(i).x;
				double yDif = y - control.enemies.get(i).y;
				distances[i] = Math.pow(xDif, 2) + Math.pow(yDif, 2);
			}
		}
		boolean finished = false;
		int lowest = 0;
		while(!finished)
		{
			for(int j = 0; j < numEnemies; j++)
			{
				if(distances[j]<distances[lowest])  lowest = j;
			}
			if(!control.checkObstructionsPoint((float)x, (float)y, (float)control.enemies.get(lowest).y, (float)control.enemies.get(lowest).y, true))
			{
				finished = true;
			} else
			{
				distances[lowest] = 999999;
			}
			if(distances[lowest] == 999999)
			{
				lowest = -1;
				finished = true;
			}
		}
		return lowest+1;
	}
}