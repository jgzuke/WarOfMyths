package com.example.magegame;
public final class enemy_Axeman extends enemy_Shield
{
	public enemy_Axeman(Controller creator, double setX, double setY)
	{
                super(creator, setX, setY);
		visualImage = mainController.game.imageLibrary.axeman_Image[0];
		setImageDimensions();
                HpMax = 6500;
                Hp = HpMax;
	}@
	Override
	public void frameCall()
	{       
		super.frameCall();
                visualImage = mainController.game.imageLibrary.axeman_Image[currentFrame];
	}
}