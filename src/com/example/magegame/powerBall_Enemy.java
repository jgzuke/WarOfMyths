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
		visualImage = mainController.imageLibrary.powerBall_ImageEnemy[mainController.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(mainController.checkHitBack(x, y) && !deleted)
		{
			x -= xForward;
			y -= yForward;
			mainController.createPowerBallEnemyAOE(x, y, 10);
			deleted = true;
			mainController.activity.playEffect("electric");
		}
		if(mainController.player.currentFrame < 22 && !deleted)
		{
			xDif = x - mainController.player.x;
			yDif = y - mainController.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				mainController.player.getHit(power);
				mainController.createPowerBallEnemyAOE(x, y, power);
				deleted = true;
				mainController.activity.playEffect("electric");
				if(mainController.getRandomDouble()*(0.5+mainController.getDifficultyLevelMultiplier()) > 1.2)
				{
					mainController.player.rads = Math.atan2(yForward, xForward);
					mainController.player.stun();
				}
			}
		}
	}
}