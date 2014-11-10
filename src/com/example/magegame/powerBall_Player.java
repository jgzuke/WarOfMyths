package com.example.magegame;
public final class PowerBall_Player extends PowerBall
{
	protected PowerBall_Player(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		mainController = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = mainController.imageLibrary.powerBall_ImagePlayer[mainController.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(mainController.checkHitBack(x, y) || mainController.checkHitBack(x-(xForward/2), y-(yForward/2)) && !deleted)
		{
			explodeBack();
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
		}
		for(int i = 0; i < mainController.enemies.length; i++)
		{
			if(mainController.enemies[i] != null && !deleted && mainController.enemies[i].getRollTimer() < 1)
			{
				mainController.enemies[i].setLevels(mainController.enemies[i].getLevelCurrentPosition(), x, y, xForward, yForward);
				mainController.enemies[i].incrementLevelCurrentPosition();
				xDif = x - mainController.enemies[i].x;
				yDif = y - mainController.enemies[i].y;
				if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 400)
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
					if(mainController.player.humanType == 0)
					{
						mainController.enemies[i].getHit((int)(power * mainController.player.spMod*mainController.activity.wAres/10));
					}
					else
					{
						mainController.enemies[i].getHit((int)(power*mainController.activity.wAres/10));
					}
					explode();
				}
			}
		}
	}
	public void explodeBack()
	{
		x -= xForward;
		y -= yForward;
		mainController.createPowerBallPlayerAOE(x, y, 10);
		deleted = true;
	}
	public void explode()
	{
		mainController.createPowerBallPlayerAOE(x, y, power);
		deleted = true;
	}
}