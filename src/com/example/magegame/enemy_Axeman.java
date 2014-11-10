/*
 * Specific variable values for axemen
 */
package com.example.magegame;

import com.example.magegame.Controller;

public final class Enemy_Axeman extends Enemy_Shield
{
	public Enemy_Axeman(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = mainController.imageLibrary.axeman_Image[0];
		setImageDimensions();
		setHpMax(6500);
		hp = 6500;
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.axeman_Image[currentFrame];
	}
}