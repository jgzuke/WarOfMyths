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
		hp = (int)(5500 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.axeman_Image[currentFrame];
	}
	@Override
	protected int getType()
	{
		return 3;
	}
}