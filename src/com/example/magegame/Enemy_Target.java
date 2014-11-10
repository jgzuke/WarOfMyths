/*
 * Specific object behavior and ai for pikemen
 */
package com.example.magegame;
public final class Enemy_Target extends Enemy_Muggle
{
	protected int timer = 10;
	protected boolean shooting;
	public Enemy_Target(Controller creator, double setX, double setY, double Rotation, boolean Shooting)
	{
		super(creator, setX, setY);
		visualImage = mainController.imageLibrary.target_Image;
		setImageDimensions();
		hp = (int)(4000);
		setHpMax(hp);
		rotation = Rotation;
		shooting = Shooting;
	}
	@Override
	protected void frameCall()
	{
		if(shooting)
		{
			timer--;
			if(timer == 0)
			{
				rads = rotation / r2d;
				mainController.createPowerBallEnemy(rotation, Math.cos(rads) * 10, Math.sin(rads) * 10, 130, x, y);
				timer = 20;
			}
		}
		super.frameCall();
	}

	@Override
	protected void attacking() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void frameReactionsDangerLOS() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void frameReactionsDangerNoLOS() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void frameReactionsNoDangerLOS() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void frameReactionsNoDangerNoLOS() {
		// TODO Auto-generated method stub
		
	}
}