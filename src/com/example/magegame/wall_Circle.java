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
	private int humanWidth = 10;
	public Wall_Circle(Controller creator, int OCX, int OCY, int OCR)
	{
		oCX = OCX;
		oCY = OCY;
		oCR = OCR;
		mainController = creator;
		mainController.setObstaclesCirclesX(mainController.getCurrentCircle(), OCX);
		mainController.setObstaclesCirclesY(mainController.getCurrentCircle(), OCY);
		mainController.setObstaclesCirclesRadius(mainController.getCurrentCircle(), OCR);
		mainController.incrementCurrentCircle();
		oCR += humanWidth;
		oCRS = Math.pow(oCR, 2);
	}
	@ Override
	protected void frameCall()
	{
			xdif = oCX - mainController.player.x;
			ydif = oCY - mainController.player.y;
			rads = Math.atan2(ydif, xdif);
			if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < oCRS)
			{
				mainController.player.x = oCX - (Math.cos(rads) * oCR);
				mainController.player.y = oCY - (Math.sin(rads) * oCR);
			}
		for(int i = 0; i < mainController.enemies.length; i++)
		{
			if(mainController.enemies[i] != null)
			{
				xdif = oCX - mainController.enemies[i].x;
				ydif = oCY - mainController.enemies[i].y;
				rads = Math.atan2(ydif, xdif);
				if(Math.pow(xdif, 2) + Math.pow(ydif, 2) < oCRS)
				{
					mainController.enemies[i].x = oCX - (Math.cos(rads) * oCR);
					mainController.enemies[i].y = oCY - (Math.sin(rads) * oCR);
				}
			}
		}
	}
}