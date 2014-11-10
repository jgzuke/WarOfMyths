package com.magegame;

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
		control = creator;
		control.setORectX1All(control.getCurrentRectangleAll(), oRX1);
		control.setORectX2All(control.getCurrentRectangleAll(), oRX2);
		control.setORectY1All(control.getCurrentRectangleAll(), oRY1);
		control.setORectY2All(control.getCurrentRectangleAll(), oRY2);
		control.incrementCurrentRectangleAll();
		if(tall)
		{
			control.setORectX1(control.getCurrentRectangle(), oRX1);
			control.setORectX2(control.getCurrentRectangle(), oRX2);
			control.setORectY1(control.getCurrentRectangle(), oRY1);
			control.setORectY2(control.getCurrentRectangle(), oRY2);
			control.incrementCurrentRectangle();
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
				if(control.player.x > oRX1 && control.player.x < oRX2 && control.player.y > oRY1 && control.player.y < oRY2)
				{
						double holdX;
						double holdY;
						if(control.player.x > x)
						{
							holdX = Math.abs(control.player.x - oRX2);
						} else
						{
							holdX = Math.abs(control.player.x - oRX1);
						}
						if(control.player.y > y)
						{
							holdY = Math.abs(control.player.y - oRY2);
						} else
						{
							holdY = Math.abs(control.player.y - oRY1);
						}
						if((holdX) < (holdY))
						{
							if(control.player.x > x)
							{
								control.player.x = oRX2;
							}
							else
							{
								control.player.x = oRX1;
							}
						} else
						{
							if(control.player.y > y)
							{
								control.player.y = oRY2;
							}
							else
							{
								control.player.y = oRY1;
							}
						}
				}
        	}
		for(int i = 0; i < control.enemies.length; i++)
		{
			if(control.enemies[i] != null)
			{
				if(control.enemies[i].x > oRX1 && control.enemies[i].x < oRX2 && control.enemies[i].y > oRY1 && control.enemies[i].y < oRY2)
				{
					if(!control.checkHitBackPass(control.enemies[i].x, control.enemies[i].y))
					{
						double holdX;
						double holdY;
						if(control.enemies[i].x > x)
						{
							holdX = Math.abs(control.enemies[i].x - oRX2);
						} else
						{
							holdX = Math.abs(control.enemies[i].x - oRX1);
						}
						if(control.enemies[i].y > y)
						{
							holdY = Math.abs(control.enemies[i].y - oRY2);
						} else
						{
							holdY = Math.abs(control.enemies[i].y - oRY1);
						}
						if((holdX) < (holdY))
						{
							if(control.enemies[i].x > x)
							{
								control.enemies[i].x = oRX2;
							}
							else
							{
								control.enemies[i].x = oRX1;
							}
						}
						else
						{
							if(control.enemies[i].y > y)
							{
								control.enemies[i].y = oRY2;
							}
							else
							{
								control.enemies[i].y = oRY1;
							}
						}
					}
				}
			}
		}
	}
}