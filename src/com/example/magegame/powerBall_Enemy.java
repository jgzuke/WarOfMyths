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
		visualImage = mainController.imageLibrary.powerBall_Image[0][mainController.getRandomInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.powerBall_Image[0][mainController.getRandomInt(5)];
		if(mainController.player.getRollTimer() < 1)
		{
			xDif = x - mainController.player.x;
			yDif = y - mainController.player.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				mainController.player.getHit(power);
				mainController.createPowerBallEnemyAOE(x, y, power);
				deleted = true;
				if(mainController.getRandomInt(3) == 0)
				{
					mainController.player.rads = Math.atan2(yForward, xForward);
					mainController.player.stun();
				}
			}
		}
		if(mainController.checkHitBack(x, y))
		{
			power /= 2;
			x -= xForward;
			y -= yForward;
			mainController.createPowerBallEnemyAOE(x, y, power);
			deleted = true;
		}
	}
}