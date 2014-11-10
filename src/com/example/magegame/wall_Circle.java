package com.example.magegame;
public class Wall_Circle extends Wall
{
	private double xdif;
	private double ydif;
	private int oCX;
	private int oCY;
	private int oCR;
	private double oCRS;
	private double rads;
	public Wall_Circle(Controller creator, int OCX, int OCY, int OCR)
	{
		oCX = OCX;
		oCY = OCY;
		oCR = OCR;
		oCRS = Math.pow(oCR, 2);
		mainController = creator;
		mainController.setObstaclesCirclesX(mainController.getCurrentCircle(), OCX);
		mainController.setObstaclesCirclesY(mainController.getCurrentCircle(), OCY);
		mainController.setObstaclesCirclesRadius(mainController.getCurrentCircle(), OCR);
		mainController.incrementCurrentCircle();
	}
	@ Override
	public void frameCall()
	{
		changing = true;
		Sprite hold = mainController.player;
		while(hold != null)
		{
			xdif = oCX - hold.x;
			ydif = oCY - hold.y;
			rads = Math.atan2(ydif, xdif);
			if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < oCRS)
			{
				hold.x = oCX - (Math.cos(rads) * oCR);
				hold.y = oCY - (Math.sin(rads) * oCR);
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