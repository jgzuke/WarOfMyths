package com.example.magegame;
public class Wall_Circle extends Wall
{
	private double xdif;
	private double ydif;
	private int oCX;
	private int oCY;
	private int oCR;
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
		mainController = creator;
		mainController.setOCircXAll(mainController.getCurrentCircleAll(), OCX);
		mainController.setOCircYAll(mainController.getCurrentCircleAll(), OCY);
		mainController.setOCircRadiusAll(mainController.getCurrentCircleAll(), OCR);
		mainController.setOCircRatioAll(mainController.getCurrentCircleAll(), OCRatio);
		mainController.incrementCurrentCircleAll();
		if(tall)
		{
			mainController.setOCircX(mainController.getCurrentCircle(), OCX);
			mainController.setOCircY(mainController.getCurrentCircle(), OCY);
			mainController.setOCircRadius(mainController.getCurrentCircle(), OCR);
			mainController.setOCircRatio(mainController.getCurrentCircle(), OCRatio);
			mainController.incrementCurrentCircle();
		}
		oCR += humanWidth;
		oCRS = Math.pow(oCR, 2);
	}
	@ Override
	protected void frameCall()
	{
			xdif = oCX - mainController.player.x;
			ydif = oCY - mainController.player.y;
			rads = Math.atan2(ydif, xdif);
			if(Math.pow(xdif, 2) + Math.pow(ydif/oCRatio, 2) < oCRS)
			{
				if(!mainController.checkHitBackPass(mainController.player.x, mainController.player.y))
				{
					mainController.player.x = oCX - (Math.cos(rads) * oCR);
					mainController.player.y = oCY - (Math.sin(rads) * oCR);
				}
			}
		for(int i = 0; i < mainController.enemies.length; i++)
		{
			if(mainController.enemies[i] != null)
			{
				xdif = oCX - mainController.enemies[i].x;
				ydif = oCY - mainController.enemies[i].y;
				rads = Math.atan2(ydif, xdif);
				if(Math.pow(xdif, 2) + Math.pow(ydif/oCRatio, 2) < oCRS)
				{
					if(!mainController.checkHitBackPass(mainController.enemies[i].x, mainController.enemies[i].y))
					{
						mainController.enemies[i].x = oCX - (Math.cos(rads) * oCR);
						mainController.enemies[i].y = oCY - (Math.sin(rads) * oCR);
					}
				}
			}
		}
	}
}