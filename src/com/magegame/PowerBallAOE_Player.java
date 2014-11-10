/**
 * behavior for enemy power ball explosion
 */
package com.magegame;
public final class PowerBallAOE_Player extends PowerBallAOE
{
	/**
	 * sets image
	 * @param creator control object
	 * @param X starting x coordinate
	 * @param Y starting y coordinate
	 * @param Power power or size to start at
	 * @param Shrinking whether it is shrinking or growing
	 */
	public PowerBallAOE_Player(Controller creator, int X, int Y, double Power, boolean Shrinking)
	{
		super(creator, X, Y, Power, Shrinking);
		visualImage = control.imageLibrary.powerBallAOE_ImagePlayer;
	}
	/**
	 * checks whether it damages player
	 */
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		if(damaging)
		{
			for(int i = 0; i < control.enemies.length; i++)
			{
				if(control.enemies[i] != null)
				{
					xDif = x - control.enemies[i].x;
					yDif = y - control.enemies[i].y;
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
					{
						double damage = 60*Math.pow((double)control.activity.wAres/10, 0.5);
						if(control.player.humanType == 0)
						{
							damage*= Math.pow(control.player.spMod, 0.7);
						}
						control.enemies[i].getHit((int)damage);
					}
				}
			}
		}
	}
	/**
	 * if is is shrinking, explode creates more growing ones at certain points in time
	 */
	@ Override
	protected void explode(int power)
	{
		control.activity.playPlayerEffect();
		for(int i = 0; i<6; i++)
		{
			control.createPowerBallPlayerAOE(x-15+control.getRandomInt(30), y-15+control.getRandomInt(30), power);
		}
		/*int power = 130;
		double veloc = 2+(control.getDifficultyLevelMultiplier()*5);
		double radians;
		double xMove;
		double yMove;
		rotation-=20;
		for(int i = 0; i < 4; i++)
		{
			rotation+=5;
			radians=rotation/r2d;
			xMove = Math.cos(radians)*veloc;
			yMove = Math.sin(radians)*veloc;
			control.createPowerBallPlayer(rotation,xMove,yMove,power, x+(2*xMove), y+(2*yMove));
		}*/
	}
}