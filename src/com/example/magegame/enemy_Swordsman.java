package com.example.magegame;
public final class enemy_Swordsman extends enemy_Shield
{
	public enemy_Swordsman(Controller creator, double setX, double setY)
	{
                super(creator, setX, setY);
		visualImage = mainController.game.imageLibrary.swordsman_Image[0];
		setImageDimensions();
                HpMax = 6500;
                Hp = HpMax;
	}@
	Override
	public void frameCall()
	{                       
		super.frameCall();
                visualImage = mainController.game.imageLibrary.swordsman_Image[currentFrame];
	}
}