package com.example.magegame;
public final class SpGraphic extends Sprite
{
	private double velocity = 0;
	private double displacement = 50;
	private double r2d = 180 / Math.PI;
	private boolean player;
	public SpGraphic(Controller creator, double X, double Y, boolean setPlayer)
	{
		mainController = creator;
		player = setPlayer;
		if(player)
		{
			x = mainController.player.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.player.y + Math.sin(rotation / r2d) * displacement;
		}
		else
		{
			x = mainController.enemy.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.enemy.y + Math.sin(rotation / r2d) * displacement;
		}
		width = 10;
		rotation = mainController.getRandomInt(360);
	}@
	Override
	public void frameCall()
	{
		currentFrame++;
		if(currentFrame == 15)
		{
			deleted = true;
		}
		velocity += 0.4;
		displacement -= velocity;
		rotation += 10;
		if(player)
		{
			x = mainController.player.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.player.y + Math.sin(rotation / r2d) * displacement;
		}
		else
		{
			x = mainController.enemy.x + Math.cos(rotation / r2d) * displacement;
			y = mainController.enemy.y + Math.sin(rotation / r2d) * displacement;
		}
		width -= 0.5;
	}
}