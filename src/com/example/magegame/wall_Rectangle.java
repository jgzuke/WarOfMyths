package com.example.magegame;
public class Wall_Rectangle extends Wall
{
	private int x;
	private int y;
	private int width;
	private int height;
	private int oRX1;
	private int oRX2;
	private int oRY1;
	private int oRY2;
	public Wall_Rectangle(Controller creator, int ORX1, int ORX2, int ORY1, int ORY2)
	{
		oRX1 = ORX1;
		oRX2 = ORX2;
		oRY1 = ORY1;
		oRY2 = ORY2;
		width = Math.abs(oRX1 - oRX2) - 15;
		height = Math.abs(oRY1 - oRY2) - 15;
		x = (ORX1 + ORX2) / 2;
		y = (ORY1 + ORY2) / 2;
		mainController = creator;
		mainController.setObstaclesRectanglesX1(mainController.getCurrentRectangle(), ORX1);
		mainController.setObstaclesRectanglesX2(mainController.getCurrentRectangle(), ORX2);
		mainController.setObstaclesRectanglesY1(mainController.getCurrentRectangle(), ORY1);
		mainController.setObstaclesRectanglesY2(mainController.getCurrentRectangle(), ORY2);
		mainController.incrementCurrentRectangle();
	}
        @ Override
	public void frameCall()
	{
		changing = true;
		Sprite hold = mainController.player;
		while(hold != null)
		{
			if(hold.x > oRX1 && hold.x < oRX2 && hold.y > oRY1 && hold.y < oRY2)
			{
				double holdX = Math.abs(hold.x - x);
				double holdY = Math.abs(hold.y - y);
				if((holdX / width) > (holdY / height))
				{
					if(hold.x > x)
					{
						hold.x = oRX2;
					}
					else
					{
						hold.x = oRX1;
					}
				}
				else
				{
					if(hold.y > y)
					{
						hold.y = oRY2;
					}
					else
					{
						hold.y = oRY1;
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