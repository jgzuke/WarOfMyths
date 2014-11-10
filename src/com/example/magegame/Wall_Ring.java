package com.example.magegame;
public class Wall_Ring extends Wall
{
	private double xdif;
	private double ydif;
	private int oCX;
	private int oCY;
	private int oCRIn;
	private int oCROut;
	private double oCRSIn;
	private double oCRSOut;
	private double oCRSAve;
	private double rads;
	public Wall_Ring(Controller creator, int OCX, int OCY, int OCRIn, int OCROut)
	{
		oCX = OCX;
		oCY = OCY;
		oCRIn = OCRIn;
		oCROut = OCROut;
		mainController = creator;
		mainController.setORingX(mainController.getCurrentRing(), OCX);
		mainController.setORingY(mainController.getCurrentRing(), OCY);
		mainController.setORingInner(mainController.getCurrentRing(), oCRIn);
		mainController.setORingOuter(mainController.getCurrentRing(), oCROut);
		mainController.incrementCurrentRing();
		
		oCRIn -= humanWidth;
		oCROut += humanWidth;
		oCRSIn = Math.pow(oCRIn, 2);
		oCRSOut = Math.pow(oCROut, 2);
		oCRSAve = Math.pow((oCRIn+oCROut)/2, 2);
	}
	@ Override
	protected void frameCall()
	{
			double curX = mainController.player.x;
			double curY = mainController.player.y;
			xdif = oCX - curX;
			ydif = oCY - curY;
			rads = Math.atan2(ydif, xdif);
			double dist = Math.pow(xdif, 2) + Math.pow(ydif, 2);
			if(dist < oCRSOut&&dist>oCRSIn)
			{
				if(!mainController.checkHitBackPass(curX, curY))
				{
					if(dist<oCRSAve)
					{
						mainController.player.x = oCX - (Math.cos(rads) * oCRIn);
						mainController.player.y = oCY - (Math.sin(rads) * oCRIn);
					} else
					{
						mainController.player.x = oCX - (Math.cos(rads) * oCROut);
						mainController.player.y = oCY - (Math.sin(rads) * oCROut);
					}
				}
			}
		for(int i = 0; i < mainController.enemies.length; i++)
		{
			if(mainController.enemies[i] != null)
			{
				curX = mainController.enemies[i].x;
				curY = mainController.enemies[i].y;
				xdif = oCX - curX;
				ydif = oCY - curY;
				rads = Math.atan2(ydif, xdif);
				dist = Math.pow(xdif, 2) + Math.pow(ydif, 2);
				if(dist < oCRSOut&&dist>oCRSIn)
				{
					if(!mainController.checkHitBackPass(curX, curY))
					{
						if(dist<oCRSAve)
						{
							mainController.enemies[i].x = oCX - (Math.cos(rads) * oCRIn);
							mainController.enemies[i].y = oCY - (Math.sin(rads) * oCRIn);
						} else
						{
							mainController.enemies[i].x = oCX - (Math.cos(rads) * oCROut);
							mainController.enemies[i].y = oCY - (Math.sin(rads) * oCROut);
						}
					}
				}
			}
		}
	}
}