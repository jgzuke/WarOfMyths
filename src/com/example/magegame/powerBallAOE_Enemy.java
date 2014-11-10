package com.example.magegame;
public final class PowerBallAOE_Enemy extends PowerBallAOE
{
	public PowerBallAOE_Enemy(Controller creator, int X, int Y, double Power, boolean Shrinking)
	{
		super(creator, X, Y, Power, Shrinking);
		visualImage = mainController.imageLibrary.powerBallAOE_ImageEnemy;
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		if(damaging)
		{
			xDif = x - mainController.player.x;
			yDif = y - mainController.player.y;
			if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone)
			{			
				mainController.player.getHit(60);
			}
		}
	}
	@ Override
	protected void explode()
	{
		int power = 120;
		double pV = 2+(mainController.getDifficultyLevelMultiplier()*5);
		double oV = 0.7*pV;
		mainController.createPowerBallEnemy(0,   pV,  0,  power, x+(2*pV), y);
		mainController.createPowerBallEnemy(45,  oV,  oV, power, x+(2*oV), y+(2*oV));
		mainController.createPowerBallEnemy(90,  0,   pV, power, x, y+(2*pV));
		mainController.createPowerBallEnemy(135,-oV,  oV, power, x-(2*oV), y+(2*oV));
		mainController.createPowerBallEnemy(180,-pV,  0,  power, x-(2*pV), y);
		mainController.createPowerBallEnemy(225, -oV,-oV, power, x-(2*oV), y-(2*oV));
		mainController.createPowerBallEnemy(270, 0,  -pV, power, x, y-(2*pV));
		mainController.createPowerBallEnemy(315, oV, -oV, power, x+(2*oV), y-(2*oV));
	}
}