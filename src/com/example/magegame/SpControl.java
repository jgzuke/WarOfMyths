package com.example.magegame;
public class SpControl
{
	private int gainTimer = 15;
	private boolean gaining = false;
	private Controller mainController;
	private boolean player;
	public SpControl(Controller creator, boolean Player)
	{
		mainController = creator;
		player = Player;
	}
	public void frameCall()
	{
		gainTimer--;
		if(isGaining())
		{
			if(gainTimer < 1)
			{
				drawSp(player);
				gainTimer = 8;
			}
		}
		setGaining(false);
	}
	public void drawSp(boolean Player)
	{
		SpGraphic spGraphicDraw;
		if(Player)
		{
			spGraphicDraw = new SpGraphic(mainController, mainController.player.x, mainController.player.y, true);
		}
		else
		{
			spGraphicDraw = new SpGraphic(mainController, mainController.enemy.x, mainController.enemy.y, false);
		}
		mainController.setSpGraphic(mainController.lowestPositionEmpty(mainController.getSpGraphic()), spGraphicDraw);
	}
	public boolean isGaining() {
		return gaining;
	}
	public void setGaining(boolean gaining) {
		this.gaining = gaining;
	}
}