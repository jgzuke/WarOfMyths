package com.example.magegame;
public final class powerBall_Player extends powerBall
{
	public powerBall_Player(Controller creator, int X, int Y, int Power, double Xforward, double Yforward, double Rotation)
	{
		mainController = creator;
		x = X;
		y = Y;
		realX = x;
		realY = y;
		XForward = Xforward;
		YForward = Yforward;
		visualImage = mainController.game.imageLibrary.powerBall_Image[mainController.PlayerType][mainController.randomGenerator.nextInt(5)];
		setImageDimensions();
		power = Power;
		rotation = Rotation;
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		mainController.enemy.levelX[mainController.enemy.levelCurrentPosition] = x;
		mainController.enemy.levelY[mainController.enemy.levelCurrentPosition] = y;
		mainController.enemy.levelXForward[mainController.enemy.levelCurrentPosition] = XForward;
		mainController.enemy.levelYForward[mainController.enemy.levelCurrentPosition] = YForward;
		mainController.enemy.levelCurrentPosition++;                
		if(mainController.enemy.rollTimer < 1)
		{
			xDif = x - mainController.enemy.x;
			yDif = y - mainController.enemy.y;
			if(Math.pow(xDif, 2) + Math.pow(yDif, 2) < 100)
			{
				mainController.enemy.getHit(power);
				mainController.createPowerBallPlayerAOE(x, y, power);
				deleted = true;
                                if(mainController.randomGenerator.nextInt(3) == 0)
                                {
                                    mainController.enemy.rads = Math.atan2(YForward, XForward);
                                    mainController.enemy.rotation = mainController.enemy.rads * mainController.enemy.r2d + 180;
                                    mainController.enemy.roll();                                    
                                    mainController.enemy.currentFrame = 1;
                                    mainController.enemy.xMoveRoll /= 3;
                                    mainController.enemy.yMoveRoll /= 3;                                    
                                    mainController.enemy.reactTimer = 0;
                                }
			}
		}
                for(int i = 0; i < mainController.enemies.length; i++)
                {
                    if(mainController.enemies[i] != null)
			{
                            mainController.enemies[i].levelX[mainController.enemies[i].levelCurrentPosition] = x;
                            mainController.enemies[i].levelY[mainController.enemies[i].levelCurrentPosition] = y;
                            mainController.enemies[i].levelXForward[mainController.enemies[i].levelCurrentPosition] = XForward;
                            mainController.enemies[i].levelYForward[mainController.enemies[i].levelCurrentPosition] = YForward;
                            mainController.enemies[i].levelCurrentPosition++;
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