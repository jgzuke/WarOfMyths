/**
 * behavior for enemy power ball explosion
 */
package com.magegame;
public final class Proj_Tracker_AOE_Player extends Proj_Tracker_AOE
{
	/**
	 * sets image
	 * @param creator control object
	 * @param X starting x coordinate
	 * @param Y starting y coordinate
	 * @param Power power or size to start at
	 * @param Shrinking whether it is shrinking or growing
	 */
	public Proj_Tracker_AOE_Player(Controller creator, int X, int Y, double Power, boolean Shrinking)
	{
		super(creator, X, Y, Power, Shrinking);
		visualImage = control.imageLibrary.shotAOEPlayer;
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
			for(int i = 0; i < control.enemies.size(); i++)
			{
				if(control.enemies.get(i) != null)
				{
					xDif = x - control.enemies.get(i).x;
					yDif = y - control.enemies.get(i).y;
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
					{
						double damage = 60*Math.pow((double)control.activity.wAres/10, 0.5);
						control.enemies.get(i).getHit((int)damage);
					}
				}
			}
			for(int i = 0; i < control.structures.size(); i++)
			{
				if(control.structures.get(i) != null)
				{
					xDif = x - control.structures.get(i).x;
					yDif = y - control.structures.get(i).y;
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
					{
						double damage = 60*Math.pow((double)control.activity.wAres/10, 0.5);
						control.structures.get(i).getHit((int)damage);
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
		for(int i = 0; i<9; i++)
		{
			control.createProj_TrackerPlayerAOE(x-15+control.getRandomInt(30), y-15+control.getRandomInt(30), power+22, true);
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
			control.createProj_TrackerPlayer(rotation,xMove,yMove,power, x+(2*xMove), y+(2*yMove));
		}*/
	}
}