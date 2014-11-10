package com.magegame;
public class Wall_Circle extends Wall
{
	private double xdif;
	private double ydif;
	protected int oCX;
	protected int oCY;
	protected int oCR;
	private double oCRatio;
	private double oCRS;
	private double rads;
	public Wall_Circle(Controller creator, int OCX, int OCY, int OCR, double OCRatio, boolean Tall)
	{
		tall = Tall;
		oCX = OCX;
		oCY = OCY;
		oCR = OCR;
		oCRatio = OCRatio;
		control = creator;
		control.setOCircXAll(control.getCurrentCircleAll(), OCX);
		control.setOCircYAll(control.getCurrentCircleAll(), OCY);
		control.setOCircRadiusAll(control.getCurrentCircleAll(), OCR);
		control.setOCircRatioAll(control.getCurrentCircleAll(), OCRatio);
		control.incrementCurrentCircleAll();
		if(tall)
		{
			control.setOCircX(control.getCurrentCircle(), OCX);
			control.setOCircY(control.getCurrentCircle(), OCY);
			control.setOCircRadius(control.getCurrentCircle(), OCR);
			control.setOCircRatio(control.getCurrentCircle(), OCRatio);
			control.incrementCurrentCircle();
		}
		oCR += humanWidth;
		oCRS = Math.pow(oCR, 2);
	}
	@ Override
	protected void frameCall()
	{
			xdif = oCX - control.player.x;
			ydif = oCY - control.player.y;
			rads = Math.atan2(ydif, xdif);
			if(Math.pow(xdif, 2) + Math.pow(ydif/oCRatio, 2) < oCRS)
			{
				if(!control.checkHitBackPass(control.player.x, control.player.y))
				{
					control.player.x = oCX - (Math.cos(rads) * oCR);
					control.player.y = oCY - (Math.sin(rads) * oCR);
				}
			}
		for(int i = 0; i < control.enemies.length; i++)
		{
			if(control.enemies[i] != null)
			{
				xdif = oCX - control.enemies[i].x;
				ydif = oCY - control.enemies[i].y;
				rads = Math.atan2(ydif, xdif);
				if(Math.pow(xdif, 2) + Math.pow(ydif/oCRatio, 2) < oCRS)
				{
					if(!control.checkHitBackPass(control.enemies[i].x, control.enemies[i].y))
					{
						control.enemies[i].x = oCX - (Math.cos(rads) * oCR);
						control.enemies[i].y = oCY - (Math.sin(rads) * oCR);
					}
				}
			}
		}
	}
}