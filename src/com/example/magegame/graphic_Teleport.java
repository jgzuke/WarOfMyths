package com.example.magegame;
public final class graphic_Teleport extends drawnSprite
{
	private boolean start;
	private int startInt = 1;
	public graphic_Teleport(Controller creator, double X, double Y, boolean Start)
	{
		width = 100;
		height = 100;
		mainController = creator;
		x = X;
		y = Y;
		start = Start;
		if(start) startInt = 0;
		visualImage = mainController.game.imageLibrary.teleport_Image[startInt][currentFrame];
	}@
	Override
	public void frameCall()
	{
		visualImage = mainController.game.imageLibrary.teleport_Image[startInt][currentFrame];
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