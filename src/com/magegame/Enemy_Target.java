/**
 * Target behavior and variables
 */
package com.magegame;
public final class Enemy_Target extends Enemy_Muggle
{
	protected int timer = 10;
	protected boolean shooting;
	/**
	 * Sets all variables for target
	 * @param creator control object
	 * @param setX starting x position
	 * @param setY starting y position
	 * @param Rotation rotation of target
	 * @param Shooting whether target is shooting
	 */
	public Enemy_Target(Controller creator, double setX, double setY, double Rotation, boolean Shooting)
	{
		super(creator, setX, setY);
		visualImage = control.imageLibrary.target_Image;
		setImageDimensions();
		hp = (int)(3000);
		setHpMax(hp);
		rotation = Rotation;
		shooting = Shooting;
		worth = 1;
		weight = 10000;
		radius = 25;
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
				control.createPowerBallEnemy(rotation, Math.cos(rads) * 10, Math.sin(rads) * 10, 130, x, y);
				timer = 20;
			}
		}
		super.frameCall();
	}

	@Override
	protected void attacking() {}

	@Override
	protected void frameReactionsDangerLOS() {}

	@Override
	protected void frameReactionsDangerNoLOS() {}

	@Override
	protected void frameReactionsNoDangerLOS() {}

	@Override
	protected void frameReactionsNoDangerNoLOS() {}
	@Override
	protected int getType() {
		return 0;
	}
}