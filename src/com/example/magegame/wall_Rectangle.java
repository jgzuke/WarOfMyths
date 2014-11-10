package com.example.magegame;
public class wall_Rectangle extends wall
{
	private int x;
	private int y;
	private int width;
	private int height;
	private int ORX1;
	private int ORX2;
	private int ORY1;
	private int ORY2;
	public wall_Rectangle(Controller creator, int oRX1, int oRX2, int oRY1, int oRY2)
	{
		ORX1 = oRX1;
		ORX2 = oRX2;
		ORY1 = oRY1;
		ORY2 = oRY2;
		width = Math.abs(oRX1 - oRX2) - 15;
		height = Math.abs(oRY1 - oRY2) - 15;
		x = (ORX1 + ORX2) / 2;
		y = (ORY1 + ORY2) / 2;
		mainController = creator;
		mainController.obstaclesRectanglesX1[mainController.currentRectangle] = ORX1;
		mainController.obstaclesRectanglesX2[mainController.currentRectangle] = ORX2;
		mainController.obstaclesRectanglesY1[mainController.currentRectangle] = ORY1;
		mainController.obstaclesRectanglesY2[mainController.currentRectangle] = ORY2;
		mainController.currentRectangle++;
	}
        @ Override
	public void frameCall()
	{
		changing = true;
		Sprite hold = mainController.player;
		while(hold != null)
		{
			if(hold.x > ORX1 && hold.x < ORX2 && hold.y > ORY1 && hold.y < ORY2)
			{
				double holdX = Math.abs(hold.x - x);
				double holdY = Math.abs(hold.y - y);
				if((holdX / width) > (holdY / height))
				{
					if(hold.x > x)
					{
						hold.x = ORX2;
					}
					else
					{
						hold.x = ORX1;
					}
				}
				else
				{
					if(hold.y > y)
					{
						hold.y = ORY2;
					}
					else
					{
						hold.y = ORY1;
					}
				}
			}
			if(changing)
			{
				hold = mainController.enemy;
				changing = false;
			}
			else
			{
				hold = null;
			}
		}
	}
}