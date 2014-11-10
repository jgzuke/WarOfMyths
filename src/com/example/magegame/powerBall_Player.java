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
	public void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.powerBall_Image[mainController.getPlayerType()][mainController.getRandomInt(5)];
		mainController.enemy.setLevels(mainController.enemy.getLevelCurrentPosition(), x, y, xForward, yForward);
		mainController.enemy.incrementLevelCurrentPosition();
		if(mainController.enemy.getRollTimer() < 1)
		{
			xDif = x - mainController.enemy.x;
			yDif = y - mainController.enemy.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				if(mainController.player.humanType == 0)
				{
					mainController.enemy.getHit((int)(power*(1+mainController.player.getSp())));
				} else
				{
					mainController.enemy.getHit(power);
				}
				mainController.enemy.lowerSp((mainController.enemy.getSp()/2));
				mainController.createPowerBallPlayerAOE(x, y, power);
				deleted = true;
                                if(mainController.getRandomInt(3) == 0)
                                {
                                    mainController.enemy.rads = Math.atan2(yForward, xForward);
                                    mainController.enemy.stun();
                                }
			}
		}
                for(int i = 0; i < mainController.enemies.length; i++)
                {
                    if(mainController.enemies[i] != null)
			{
                    	mainController.enemies[i].setLevels(mainController.enemy.getLevelCurrentPosition(), x, y, xForward, yForward);
                		mainController.enemies[i].incrementLevelCurrentPosition();
                            xDif = x - mainController.enemies[i].x;
                            yDif = y - mainController.enemies[i].y;
                            if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
                            {
                                    mainController.enemies[i].getHit(power);
                                    mainController.createPowerBallPlayerAOE(x, y, power);
                                    deleted = true;
                            }
                        }
                }
		checkHitBack(x, y);
		if(hitBack == true)
		{
			mainController.createPowerBallPlayerAOE(x, y, power);
			deleted = true;
		}
	}
}