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
		setHpMax(6500);
		hp = 6500;
	}@
	Override
	public void frameCall()
	{
		super.frameCall();
		visualImage = mainController.imageLibrary.swordsman_Image[currentFrame];
	}
}