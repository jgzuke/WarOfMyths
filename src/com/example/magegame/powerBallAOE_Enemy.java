package com.example.magegame;
public final class PowerBallAOE_Enemy extends PowerBallAOE
{
	public PowerBallAOE_Enemy(Controller creator, int X, int Y, double Power)
	{
		mainController = creator;
		x = X;
		y = Y;
		visualImage = mainController.imageLibrary.powerBallAOE_Image[0];
		width = 10;
		height = 10;
		timeToDeath = (int) Power / 15;
		alpha = (byte) 200;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		xDif = x - mainController.player.x;
		yDif = y - mainController.player.y;
		if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
		{			
			mainController.player.getHit(60);
		}
	}
}