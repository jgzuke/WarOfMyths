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
			if(Math.sqrt(Math.pow(xDif, 2) + Math.pow(yDif, 2)) < widthDone+15)
			{			
				mainController.player.getHit(60);
			}
		}
	}
	@ Override
	protected void explode(int power)
	{
		mainController.activity.playEffect("electric");
		for(int i = 0; i<6; i++)
		{
			mainController.createPowerBallEnemyAOE(x-15+mainController.getRandomInt(30), y-15+mainController.getRandomInt(30), power);
		}
		/*int power = 130;
		double veloc = 2+(mainController.getDifficultyLevelMultiplier()*5);
		double radians;
		double xMove;
		double yMove;
		rotation-=20;
		for(int i = 0; i < 4; i++)
		{
			rotation+=5;
			radians=rotation/r2d;
			xMove = Math.cos(radians)*veloc;
			yMove = Math.sin(radians)*veloc;
			mainController.createPowerBallEnemy(rotation,xMove,yMove,power, x+(2*xMove), y+(2*yMove));
		}*/
	}
}