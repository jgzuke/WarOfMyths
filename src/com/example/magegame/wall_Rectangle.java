package com.example.magegame;

public class Wall_Rectangle extends Wall
{
	private int x;
	private int y;
	private int oRX1;
	private int oRX2;
	private int oRY1;
	private int oRY2;
	private boolean hitPlayer;
	private double r2d = 180/Math.PI;
	public Wall_Rectangle(Controller creator, int ORX, int ORY, int wallWidth, int wallHeight, boolean HitPlayer, boolean Tall)
	{
		tall = Tall;
		oRX1 = ORX;
		oRY1 = ORY;
		oRX2 = ORX+wallWidth;
		oRY2 = ORY+wallHeight;
		hitPlayer = HitPlayer;
		x = (oRX1 + oRX2) / 2;
		y = (oRY1 + oRY2) / 2;
		mainController = creator;
		mainController.setORectX1Short(mainController.getCurrentRectangleShort(), oRX1);
		mainController.setORectX2Short(mainController.getCurrentRectangleShort(), oRX2);
		mainController.setORectY1Short(mainController.getCurrentRectangleShort(), oRY1);
		mainController.setORectY2Short(mainController.getCurrentRectangleShort(), oRY2);
		mainController.incrementCurrentRectangleShort();
		if(tall)
		{
			mainController.setORectX1(mainController.getCurrentRectangle(), oRX1);
			mainController.setORectX2(mainController.getCurrentRectangle(), oRX2);
			mainController.setORectY1(mainController.getCurrentRectangle(), oRY1);
			mainController.setORectY2(mainController.getCurrentRectangle(), oRY2);
			mainController.incrementCurrentRectangle();
		}
		oRX1 -= humanWidth;
		oRY1 -= humanWidth;
		oRX2 += humanWidth;
		oRY2 += humanWidth;
	}
        @ Override
        protected void frameCall()
	{
        	if(hitPlayer)
        	{
				if(mainController.player.x > oRX1 && mainController.player.x < oRX2 && mainController.player.y > oRY1 && mainController.player.y < oRY2)
				{
					if(!mainController.checkHitBackPass(mainController.player.x, mainController.player.y))
					{
						double holdX;
						double holdY;
						if(mainController.player.x > x)
						{
							holdX = Math.abs(mainController.player.x - oRX2);
						} else
						{
							holdX = Math.abs(mainController.player.x - oRX1);
						}
						if(mainController.player.y > y)
						{
							holdY = Math.abs(mainController.player.y - oRY2);
						} else
						{
							holdY = Math.abs(mainController.player.y - oRY1);
						}
						if((holdX) < (holdY))
						{
							if(mainController.player.x > x)
							{
								mainController.player.x = oRX2;
							}
							else
							{
								mainController.player.x = oRX1;
							}
						} else
						{
							if(mainController.player.y > y)
							{
								mainController.player.y = oRY2;
							}
							else
							{
								mainController.player.y = oRY1;
							}
						}
					}
				}
        	}
		for(int i = 0; i < mainController.enemies.length; i++)
		{
			if(mainController.enemies[i] != null)
			{
				if(mainController.enemies[i].x > oRX1 && mainController.enemies[i].x < oRX2 && mainController.enemies[i].y > oRY1 && mainController.enemies[i].y < oRY2)
				{
					if(!mainController.checkHitBackPass(mainController.enemies[i].x, mainController.enemies[i].y))
					{
						double holdX;
						double holdY;
						if(mainController.enemies[i].x > x)
						{
							holdX = Math.abs(mainController.enemies[i].x - oRX2);
						} else
						{
							holdX = Math.abs(mainController.enemies[i].x - oRX1);
						}
						if(mainController.enemies[i].y > y)
						{
							holdY = Math.abs(mainController.enemies[i].y - oRY2);
						} else
						{
							holdY = Math.abs(mainController.enemies[i].y - oRY1);
						}
						if((holdX) < (holdY))
						{
							if(mainController.enemies[i].x > x)
							{
								mainController.enemies[i].x = oRX2;
							}
							else
							{
								mainController.enemies[i].x = oRX1;
							}
						}
						else
						{
							if(mainController.enemies[i].y > y)
							{
								mainController.enemies[i].y = oRY2;
							}
							else
							{
								mainController.enemies[i].y = oRY1;
							}
						}
					}
				}
			}
		}
	}
}