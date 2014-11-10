package com.example.magegame;
public final class SpGraphic extends Sprite
{
	private double velocity = 0;
	private double displacement = 50;
	private double r2d = 180 / Math.PI;
	private boolean Player;
	public int alpha = 0;
	public SpGraphic(Controller creator, double X, double Y, boolean setPlayer)
	{
		mainController = creator;
		Player = setPlayer;
		if(Player)
		{
			x = mainController.player.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.player.y + Math.sin(rotation / r2d) * displacement;
		}
		else
		{
			x = mainController.enemy.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.enemy.y + Math.sin(rotation / r2d) * displacement;
		}
		width = 20;
		rotation = mainController.randomGenerator.nextInt(360);
	}@
	Override
	public void frameCall()
	{
		currentFrame++;
		if(currentFrame == 15)
		{
			deleted = true;
		}
		alpha += 15;
		velocity += 0.4;
		displacement -= velocity;
		rotation += 10;
		if(Player)
		{
			x = mainController.player.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.player.y + Math.sin(rotation / r2d) * displacement;
		}
		else
		{
			x = mainController.enemy.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.enemy.y + Math.sin(rotation / r2d) * displacement;
		}
		width -= 1;
	}
}