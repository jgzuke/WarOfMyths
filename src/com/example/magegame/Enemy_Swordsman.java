/*
 * Specific variable values for swordsmen
 */
package com.example.magegame;
public final class Enemy_Swordsman extends Enemy_Shield
{
	public Enemy_Swordsman(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
		visualImage = mainController.imageLibrary.swordsman_Image[0];
		setImageDimensions();
		hp = (int)(6500 * mainController.getDifficultyLevelMultiplier());
		setHpMax(hp);
	}@
	Override
	protected void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.swordsman_Image[currentFrame];
	}
}