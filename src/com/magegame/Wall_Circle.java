/**
 * behavior for circular walls
 */
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
	/**
	 * sets variables and stores some in control object array
	 * @param creator control object
	 * @param OCX x value
	 * @param OCY y value
	 * @param OCR radius
	 * @param OCRatio ratio between x and y
	 * @param Tall whether or not the wall is tall enough to stop projectiles
	 */
	public Wall_Circle(Controller creator, int OCX, int OCY, int OCR, double OCRatio, boolean Tall)
	{
		tall = Tall;
		oCX = OCX;
		oCY = OCY;
		oCR = OCR;
		oCRatio = OCRatio;
		control = creator;
		if(Tall) control.setOCirc(OCX, OCY, OCR, 1);
		else control.setOCirc(OCX, OCY, OCR, 0);
		oCR += humanWidth;
		oCRS = Math.pow(oCR, 2);
	}
	/**
	 * checks whether wall hits player or enemies
	 */
	@ Override
	protected void frameCall()
	{
			xdif = oCX - control.player.x;
			ydif = oCY - control.player.y;
			rads = Math.atan2(ydif, xdif);
			if(Math.pow(xdif, 2) + Math.pow(ydif/oCRatio, 2) < oCRS)
			{
				if(!control.checkHitBackPass(control.player.x, control.player.y, true))
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
					if(!control.checkHitBackPass(control.enemies[i].x, control.enemies[i].y, true))
					{
						control.enemies[i].x = oCX - (Math.cos(rads) * oCR);
						control.enemies[i].y = oCY - (Math.sin(rads) * oCR);
					}
				}
			}
		}
	}
}