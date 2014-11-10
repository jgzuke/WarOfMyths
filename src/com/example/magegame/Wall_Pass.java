package com.example.magegame;

public class Wall_Pass extends Wall
{
	private int oRX1;
	private int oRX2;
	private int oRY1;
	private int oRY2;
	private boolean hitPlayer;
	public Wall_Pass(Controller creator, int ORX, int ORY, int wallWidth, int wallHeight)
	{
		oRX1 = ORX;
		oRY1 = ORY;
		oRX2 = ORX+wallWidth;
		oRY2 = ORY+wallHeight;
		creator.setOPassageX1(creator.getCurrentCurrentPassage(), oRX1);
		creator.setOPassageX2(creator.getCurrentCurrentPassage(), oRX2);
		creator.setOPassageY1(creator.getCurrentCurrentPassage(), oRY1);
		creator.setOPassageY2(creator.getCurrentCurrentPassage(), oRY2);
		creator.incrementCurrentPassage();
	}
	@Override
	protected void frameCall() {
		// TODO Auto-generated method stub
		
	}
}