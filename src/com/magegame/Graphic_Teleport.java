/*
 * Handles visuals for player or enemy teleportation
 */
package com.magegame;
public final class Graphic_Teleport extends DrawnSprite
{
	public Graphic_Teleport(Controller creator, double X, double Y, double Rotation)
	{
		rotation = Rotation;
		width = 100;
		height = 100;
		control = creator;
		x = X;
		y = Y;
		visualImage = control.imageLibrary.teleport_Image[currentFrame];
	}@
	Override
	protected void frameCall()
	{
		visualImage = control.imageLibrary.teleport_Image[currentFrame];
		setImageDimensions();
		currentFrame++;
		if(currentFrame == 9)
		{
			deleted = true;
		}
	}
}