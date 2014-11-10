/*
 * Specific variable values for swordsmen
 */
package com.magegame;
public final class Enemy_Swordsman extends Enemy_Shield
{
	public Enemy_Swordsman(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = control.imageLibrary.swordsman_Image[0];
		setImageDimensions();
		hp = (int)(6500 * control.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}
	@ Override
	protected void frameCall()
	{
		super.frameCall();
		visualImage = control.imageLibrary.swordsman_Image[currentFrame];
	}
	@Override
	protected int getType()
	{
		return 1;
	}
}