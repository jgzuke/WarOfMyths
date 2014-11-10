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
		visualImage = mainController.imageLibrary.powerBall_Image[mainController.getPlayerType()][mainController.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.powerBall_Image[mainController.getPlayerType()][mainController.getRandomInt(5)];
		for(int i = 0; i < mainController.enemies.length; i++)
		{
			if(mainController.enemies[i] != null)
			{
				mainController.enemies[i].setLevels(mainController.enemies[i].getLevelCurrentPosition(), x, y, xForward, yForward);
				mainController.enemies[i].incrementLevelCurrentPosition();
				xDif = x - mainController.enemies[i].x;
				yDif = y - mainController.enemies[i].y;
				if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
				{
					if(mainController.player.humanType == 0)
					{
						mainController.enemies[i].getHit((int)(power * mainController.player.spMod*mainController.activity.worshipAres));
					}
					else
					{
						mainController.enemies[i].getHit(power*mainController.activity.worshipAres);
					}
					explode();
				}
			}
		}
		if(mainController.checkHitBack(x, y))
		{
			explode();
		}
	}
	public void explode()
	{
		if(mainController.player.humanType == 0)
		{
			mainController.createPowerBallPlayerAOE(x, y, (int)(power * mainController.player.spMod*mainController.activity.worshipAres));
		}
		else
		{
			mainController.createPowerBallPlayerAOE(x, y, power*mainController.activity.worshipAres);
		}
		deleted = true;
	}
}