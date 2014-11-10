package com.example.magegame;
public final class PowerBallAOE_Player extends PowerBallAOE
{
	public PowerBallAOE_Player(Controller creator, int X, int Y, double Power, boolean Shrinking)
	{
		super(creator, X, Y, Power, Shrinking);
		visualImage = mainController.imageLibrary.powerBallAOE_ImagePlayer;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(damaging)
		{
			for(int i = 0; i < mainController.enemies.length; i++)
			{
				if(mainController.enemies[i] != null)
				{
					xDif = x - mainController.enemies[i].x;
					yDif = y - mainController.enemies[i].y;
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
					{
						if(mainController.player.humanType == 0)
						{
							mainController.enemies[i].getHit((int)(60 * mainController.player.spMod*mainController.activity.wAres/10));
						}
						else
						{
							mainController.enemies[i].getHit((int)(60*mainController.activity.wAres/10));
						}
					}
				}
			}
		}
	}
	@ Override
	protected void explode(int power)
	{
		if(mainController.playerType==0)
		{
			mainController.activity.playEffect("burn");
		} else if(mainController.playerType==1)
		{
			mainController.activity.playEffect("water");
		} else if(mainController.playerType==2)
		{
			mainController.activity.playEffect("electric");
		} else
		{
			mainController.activity.playEffect("earth");
		}
		for(int i = 0; i<6; i++)
		{
			mainController.createPowerBallPlayerAOE(x-15+mainController.getRandomInt(30), y-15+mainController.getRandomInt(30), power);
		}
		/*int power = 130;
		double veloc = 2+(mainController.getDifficultyLevelMultiplier()*5);
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
			mainController.createPowerBallPlayer(rotation,xMove,yMove,power, x+(2*xMove), y+(2*yMove));
		}*/
	}
}