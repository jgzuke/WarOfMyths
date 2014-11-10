package com.example.magegame;
public class wall_Circle extends wall
{
	private double Xdif;
	private double Ydif;
	private int OCX;
	private int OCY;
	private int OCR;
	private double OCRS;
	private double rads;
	public wall_Circle(Controller creator, int oCX, int oCY, int oCR)
	{
		OCX = oCX;
		OCY = oCY;
		OCR = oCR;
		OCRS = Math.pow(OCR, 2);
		mainController = creator;
		mainController.obstaclesCirclesX[mainController.currentCircle] = OCX;
		mainController.obstaclesCirclesY[mainController.currentCircle] = OCY;
		mainController.obstaclesCirclesRadius[mainController.currentCircle] = OCR;
		mainController.currentCircle++;
	}
	@ Override
	public void frameCall()
	{
		changing = true;
		Sprite hold = mainController.player;
		while(hold != null)
		{
			Xdif = OCX - hold.x;
			Ydif = OCY - hold.y;
			rads = Math.atan2(Ydif, Xdif);
			if(Math.pow(Xdif, 2) + Math.pow(Ydif, 2) < OCRS)
			{
				hold.x = OCX - (Math.cos(rads) * OCR);
				hold.y = OCY - (Math.sin(rads) * OCR);
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