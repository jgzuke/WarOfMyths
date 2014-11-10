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
					if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
					{
						mainController.enemies[i].getHit(60);
					}
				}
			}
		}
	}
	@ Override
	protected void explode()
	{
		int power = 130;
		double pV = mainController.player.projectileSpeed;
		double oV = 0.7*pV;
		mainController.createPowerBallPlayer(0,   pV,  0,  power, x+(2*pV), y);
		mainController.createPowerBallPlayer(45,  oV,  oV, power, x+(2*oV), y+(2*oV));
		mainController.createPowerBallPlayer(90,  0,   pV, power, x, y+(2*pV));
		mainController.createPowerBallPlayer(135,-oV,  oV, power, x-(2*oV), y+(2*oV));
		mainController.createPowerBallPlayer(180,-pV,  0,  power, x-(2*pV), y);
		mainController.createPowerBallPlayer(225, -oV,-oV, power, x-(2*oV), y-(2*oV));
		mainController.createPowerBallPlayer(270, 0,  -pV, power, x, y-(2*pV));
		mainController.createPowerBallPlayer(315, oV, -oV, power, x+(2*oV), y-(2*oV));
	}
}