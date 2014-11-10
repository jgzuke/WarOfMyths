/*
 * Specific variable values for axemen
 */
package com.magegame;

import com.magegame.Controller;

public final class Enemy_Axeman extends Enemy_Shield
{
	public Enemy_Axeman(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = control.imageLibrary.axeman_Image[0];
		setImageDimensions();
		hp = (int)(5500 * control.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		visualImage = control.imageLibrary.axeman_Image[currentFrame];
	}
	@Override
	protected int getType()
	{
		return 3;
	}
}