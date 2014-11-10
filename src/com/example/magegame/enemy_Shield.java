/*
 * Object behavior and ai for enemies with sheilds
 */
package com.example.magegame;
abstract public class Enemy_Shield extends Enemy_Muggle
{
	public Enemy_Shield(Controller creator, double setX, double setY)
	{
		super(creator, setX, setY);
	}@
	Override
	public void frameCall()
	{
		if(currentFrame == 109)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		if(currentFrame == 96)
		{
			currentFrame = 0;
			playing = false;
			attacking = false;
		}
		super.frameCall();
	}@
	Override
	public void getHit(int damage)
	{
		if(97 < currentFrame && currentFrame < 110) damage /= 8;
		super.getHit(damage);
	}@
	Override
	public void frameReactionsDangerLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 100)
		{
			rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
			rotation = rads * r2d;
			currentFrame = 98;
			attacking = true;
			playing = true;
		}
		else
		{
			frameReactionsNoDangerLOS();
		}
	}@
	Override
	public void frameReactionsDangerNoLOS()
	{
		distanceFound = checkDistance(danger[0][0], danger[1][0], x, y);
		if(distanceFound < 100)
		{
			rads = Math.atan2((danger[1][0] - y), (danger[0][0] - x));
			rotation = rads * r2d;
			currentFrame = 98;
			attacking = true;
			playing = true;
		}
		else
		{
			frameReactionsNoDangerNoLOS();
		}
	}@
	Override
	public void frameReactionsNoDangerLOS()
	{
		rads = Math.atan2((mainController.getPlayerY() - y), (mainController.getPlayerX() - x));
		rotation = rads * r2d;
		distanceFound = checkDistance(x, y, mainController.getPlayerX(), mainController.getPlayerY());
		if(distanceFound < 20)
		{
			currentFrame = 49;
			attacking = true;
			playing = true;
		}
		else
		{
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			runToward(lastPlayerX, lastPlayerY);
		}
	}@
	Override
	public void frameReactionsNoDangerNoLOS()
	{
		distanceFound = checkDistance(x, y, lastPlayerX, lastPlayerY);
		if(isCheckedPlayerLast() || distanceFound < 10)
		{
			hp += 5;
			currentFrame = 0;
			playing = false;
			if(mainController.getRandomInt(20) == 0)
			{
				runRandom();
			}
			setCheckedPlayerLast(true);
		}
		else
		{
			rads = Math.atan2((lastPlayerY - y), (lastPlayerX - x));
			rotation = rads * r2d;
			runToward(lastPlayerX, lastPlayerY);
		}
	}@
	Override
	public void attacking()
	{
		if(currentFrame == 73)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 20, y + Math.sin(rads) * 20, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit(600);
				mainController.activity.playEffect(R.raw.sword1);
			}
		}
		if(currentFrame == 87)
		{
			distanceFound = checkDistance(x + Math.cos(rads) * 20, y + Math.sin(rads) * 20, mainController.player.x, mainController.player.y);
			if(distanceFound < 30)
			{
				mainController.player.getHit(400);
				mainController.activity.playEffect(R.raw.sword2);
			}
		}
	}
}