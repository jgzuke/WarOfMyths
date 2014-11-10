package com.example.magegame;
public final class powerBall_Enemy extends powerBall
{
	public powerBall_Enemy(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		mainController = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		XForward = Xforward;
		YForward = Yforward;
		visualImage = mainController.game.imageLibrary.powerBall_Image[mainController.EnemyType][mainController.randomGenerator.nextInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		if(mainController.player.rollTimer < 1)
		{
			xDif = x - mainController.player.x;
			yDif = y - mainController.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				mainController.createPowerBallEnemyAOE(x, y, power);
				mainController.player.getHit(power);
                                deleted = true;
                                if(mainController.randomGenerator.nextInt(3) == 0)
                                {
                                    mainController.player.rads = Math.atan2(YForward, XForward);
                                    mainController.player.rotation = mainController.player.rads * mainController.player.r2d + 180;
                                    mainController.player.roll();
                                    mainController.player.currentFrame = 1;
                                    mainController.player.xMoveRoll /= 3;
                                    mainController.player.yMoveRoll /= 3;
                                    mainController.player.abilityTimer_roll += 100;
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