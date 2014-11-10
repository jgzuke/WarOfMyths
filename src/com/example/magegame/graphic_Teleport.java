/*
 * Handles visuals for player or enemy teleportation
 */
package com.example.magegame;
public final class Graphic_Teleport extends DrawnSprite
{
	private boolean start;
	private int startInt = 1;
	public Graphic_Teleport(Controller creator, double X, double Y, boolean Start)
	{
		width = 100;
		height = 100;
		mainController = creator;
		x = X;
		y = Y;
		start = Start;
		if(start) startInt = 0;
		visualImage = mainController.imageLibrary.teleport_Image[startInt][currentFrame];
	}@
	Override
	protected void frameCall()
	{
		visualImage = mainController.imageLibrary.teleport_Image[startInt][currentFrame];
		setImageDimensions();
		width *= 3;
		height = width;
		currentFrame++;
		if(currentFrame == 15)
		{
			deleted = true;
		}
	}
}