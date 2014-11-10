package com.example.magegame;
public class SpControl
{
	private int gainTimer = 15;
	public boolean gaining = false;
	private Controller mainController;
	private boolean player;
	public boolean deleted;
	public SpControl(Controller creator, boolean Player)
	{
		mainController = creator;
		player = Player;
	}
	public void frameCall()
	{
		gainTimer--;
		if(gaining)
		{
			if(gainTimer < 1)
			{
				drawSp(player);
				gainTimer = 8;
			}
		}
		gaining = false;
	}
	public void drawSp(boolean Player)
	{
		if(Player)
		{
			SpGraphic spGraphicDraw = new SpGraphic(mainController, mainController.player.x, mainController.player.y, true);
			mainController.spGraphic[mainController.lowestPositionEmpty(mainController.spGraphic)] = spGraphicDraw;
		}
		else
		{
			SpGraphic spGraphicDraw = new SpGraphic(mainController, mainController.enemy.x, mainController.enemy.y, false);
			mainController.spGraphic[mainController.lowestPositionEmpty(mainController.spGraphic)] = spGraphicDraw;
		}
	}
}