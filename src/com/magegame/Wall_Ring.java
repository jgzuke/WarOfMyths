/**
 * behavior for ring walls
 */
package com.magegame;
public class Wall_Ring extends Wall
{
	private double xdif;
	private double ydif;
	protected int oCX;
	protected int oCY;
	private int oCRIn;
	protected int oCROut;
	private double oCRSIn;
	private double oCRSOut;
	private double oCRSAve;
	private double rads;
	/**
	 * sets variables and stores some in control object array
	 * @param creator control object
	 * @param OCX x value
	 * @param OCY y value
	 * @param OCRIn inner radius
	 * @param OCROut outer radius
	 */
	public Wall_Ring(Controller creator, int OCX, int OCY, int OCRIn, int OCROut)
	{
		oCX = OCX;
		oCY = OCY;
		oCRIn = OCRIn;
		oCROut = OCROut;
		control = creator;
		control.setORing(control.getCurrentRing(), OCX, OCY, oCRIn, oCROut);
		control.incrementCurrentRing();
		
		oCRIn -= humanWidth;
		oCROut += humanWidth;
		oCRSIn = Math.pow(oCRIn, 2);
		oCRSOut = Math.pow(oCROut, 2);
		oCRSAve = Math.pow((oCRIn+oCROut)/2, 2);
	}
	/**
	 * checks whether wall hits player or enemies
	 */
	@ Override
	protected void frameCall()
	{
			double curX = control.player.x;
			double curY = control.player.y;
			xdif = oCX - curX;
			ydif = oCY - curY;
			rads = Math.atan2(ydif, xdif);
			double dist = Math.pow(xdif, 2) + Math.pow(ydif, 2);
			if(dist < oCRSOut&&dist>oCRSIn)
			{
				if(!control.checkHitBackPass(curX, curY))
				{
					if(dist<oCRSAve)
					{
						control.player.x = oCX - (Math.cos(rads) * oCRIn);
						control.player.y = oCY - (Math.sin(rads) * oCRIn);
					} else
					{
						control.player.x = oCX - (Math.cos(rads) * oCROut);
						control.player.y = oCY - (Math.sin(rads) * oCROut);
					}
				}
			}
		for(int i = 0; i < control.enemies.length; i++)
		{
			if(control.enemies[i] != null)
			{
				curX = control.enemies[i].x;
				curY = control.enemies[i].y;
				xdif = oCX - curX;
				ydif = oCY - curY;
				rads = Math.atan2(ydif, xdif);
				dist = Math.pow(xdif, 2) + Math.pow(ydif, 2);
				if(dist < oCRSOut&&dist>oCRSIn)
				{
					if(!control.checkHitBackPass(curX, curY))
					{
						if(dist<oCRSAve)
						{
							control.enemies[i].x = oCX - (Math.cos(rads) * oCRIn);
							control.enemies[i].y = oCY - (Math.sin(rads) * oCRIn);
						} else
						{
							control.enemies[i].x = oCX - (Math.cos(rads) * oCROut);
							control.enemies[i].y = oCY - (Math.sin(rads) * oCROut);
						}
					}
				}
			}
		}
	}
}