/*
 * 
 */
package com.example.magegame;
public final class PowerBall_Enemy extends PowerBall
{
	public PowerBall_Enemy(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		mainController = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		xForward = Xforward;
		yForward = Yforward;
		visualImage = mainController.imageLibrary.powerBall_Image[mainController.getEnemyType()][mainController.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.powerBall_Image[mainController.getEnemyType()][mainController.getRandomInt(5)];
		if(mainController.player.getRollTimer() < 1)
		{
			xDif = x - mainController.player.x;
			yDif = y - mainController.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				mainController.createPowerBallEnemyAOE(x, y, power);
				if(mainController.enemy.humanType == 0)
				{
					mainController.player.getHit((int)(power*(1+mainController.enemy.getSp())));
				} else
				{
					mainController.player.getHit(power);
				}
				mainController.player.lowerSp((mainController.player.getSp()/2));
                                deleted = true;
                                if(mainController.getRandomInt(3) == 0)
                                {
                                    mainController.player.rads = Math.atan2(yForward, xForward);
                                    mainController.player.stun();
                                }
			}
		}
		checkHitBack(x, y);
		if(hitBack == true)
		{
			mainController.createPowerBallEnemyAOE(x, y, power);
			deleted = true;
		}
	}
}