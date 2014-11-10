package com.example.magegame;

public class Wall_Rectangle extends Wall
{
	private int x;
	private int y;
	protected int oRX1;
	protected int oRX2;
	protected int oRY1;
	protected int oRY2;
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
		mainController.setORectX1All(mainController.getCurrentRectangleAll(), oRX1);
		mainController.setORectX2All(mainController.getCurrentRectangleAll(), oRX2);
		mainController.setORectY1All(mainController.getCurrentRectangleAll(), oRY1);
		mainController.setORectY2All(mainController.getCurrentRectangleAll(), oRY2);
		mainController.incrementCurrentRectangleAll();
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
		for(int i = 0; i < mainController.guards.length; i++)
		{
			if(mainController.guards[i] != null)
			{
				if(mainController.guards[i].x > oRX1 && mainController.guards[i].x < oRX2 && mainController.guards[i].y > oRY1 && mainController.guards[i].y < oRY2)
				{
					if(!mainController.checkHitBackPass(mainController.guards[i].x, mainController.guards[i].y))
					{
						double holdX;
						double holdY;
						if(mainController.guards[i].x > x)
						{
							holdX = Math.abs(mainController.guards[i].x - oRX2);
						} else
						{
							holdX = Math.abs(mainController.guards[i].x - oRX1);
						}
						if(mainController.guards[i].y > y)
						{
							holdY = Math.abs(mainController.guards[i].y - oRY2);
						} else
						{
							holdY = Math.abs(mainController.guards[i].y - oRY1);
						}
						if((holdX) < (holdY))
						{
							if(mainController.guards[i].x > x)
							{
								mainController.guards[i].x = oRX2;
							}
							else
							{
								mainController.guards[i].x = oRX1;
							}
						}
						else
						{
							if(mainController.guards[i].y > y)
							{
								mainController.guards[i].y = oRY2;
							}
							else
							{
								mainController.guards[i].y = oRY1;
							}
						}
					}
				}
			}
		}
	}
}