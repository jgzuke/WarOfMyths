package com.magegame;
public final class PowerBallAOE_Enemy extends PowerBallAOE
{
	public PowerBallAOE_Enemy(Controller creator, int X, int Y, double Power, boolean Shrinking)
	{
		super(creator, X, Y, Power, Shrinking);
		visualImage = control.imageLibrary.powerBallAOE_ImageEnemy;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(damaging)
		{
			xDif = x - control.player.x;
			yDif = y - control.player.y;
			if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
			{			
				control.player.getHit(60);
			}
		}
	}
	@ Override
	protected void explode(int power)
	{
		control.activity.playEffect("electric");
		for(int i = 0; i<6; i++)
		{
			control.createPowerBallEnemyAOE(x-15+control.getRandomInt(30), y-15+control.getRandomInt(30), power, true);
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
			control.createPowerBallEnemy(rotation,xMove,yMove,power, x+(2*xMove), y+(2*yMove));
		}*/
	}
}