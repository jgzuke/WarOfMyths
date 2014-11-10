package com.example.magegame;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PlayerGestureDetector implements OnTouchListener {
	private Player player;
	private Controller main;
	private double screenDimensionMultiplier;
	private int screenMinX;
	private int screenMinY;
	private int trackingId;
	private int actionMask;
	private double xTouch;
	private double yTouch;
	private int buttonShiftX;
	public PlayerGestureDetector(Controller mainSet)
	{
		main = mainSet;
		screenDimensionMultiplier = mainSet.screenDimensionMultiplier;
		screenMinX = mainSet.screenMinX;
		screenMinY = mainSet.screenMinY;
		getSide();
	}
	protected void getSide()
	{
		if(main.activity.stickOnRight)
		{
			buttonShiftX = 0;
		} else
		{
			buttonShiftX = 390;
		}
	}
	protected void setPlayer(Player playerSet)
	{
		player = playerSet;
	}
	@Override
    public boolean onTouch(View v, MotionEvent e) {
			actionMask = e.getActionMasked();
		        switch (actionMask){
		        case MotionEvent.ACTION_DOWN:
		        	clickDown(e.getX(), e.getY(), e.getPointerId(0), true);
		        break;
		        case MotionEvent.ACTION_MOVE:
		            if(player.touching)
		            {
		            	player.touchX = visualX(e.getX(trackingId))-(427-buttonShiftX);
		            	player.touchY = visualY(e.getY(trackingId))-267;
		            }
		        break;
		        case MotionEvent.ACTION_UP:
		        	player.touching = false;
		        break;
		        case MotionEvent.ACTION_POINTER_UP:
		        	if(e.getPointerId(e.getActionIndex()) == trackingId)
		        	{
		        		player.touching = false;
		        	}
		        break;
		        case MotionEvent.ACTION_POINTER_DOWN:
		        	clickDown(e.getX(e.getActionIndex()), e.getY(e.getActionIndex()), e.getPointerId(e.getActionIndex()), false);
		        break;
		        }
		        return true;
    }
	protected void clickDown(float x, float y, int ID, boolean firstPointer)
	{
		if(main.gamePaused)
		{
			if(main.currentPause.equals("paused"))
			{
				clickDownPaused(x, y);
			} else if(main.currentPause.equals("options"))
			{
				clickDownOptions(x, y);
			} else if(main.currentPause.equals("startfight"))
			{
				clickDownStartFight(x, y);
			} else if(main.currentPause.equals("buy"))
			{
				clickDownBuy(x, y);
			}
		} else
		{
			if(main.levelNum == 0)
			{
				if(!clickDownNotPausedMenu(x, y))
				{
					clickDownNotPaused(x, y, ID, firstPointer);
				}
			} else if(main.levelNum == 1)
			{
				if(!clickDownNotPausedTutorial(x, y))
				{
					clickDownNotPaused(x, y, ID, firstPointer);
				}
			} else
			{
				clickDownNotPaused(x, y, ID, firstPointer);
			}
		}
	}
	protected void clickDownBuy(float x, float y)
	{
		if(main.pointOnSquare(x, y, 66, 231, 214, 276)&&main.activity.canBuyGame(main.buyingItem))
		{
			main.activity.buyGame(main.buyingItem);
			main.activity.playEffect("pageflip");
			main.gamePaused = false;
		} else if(main.pointOnSquare(x, y, 266, 231, 414, 276)&&main.activity.canBuyReal(main.buyingItem))
		{
			main.activity.buyReal(main.buyingItem);
			main.activity.playEffect("pageflip");
			main.gamePaused = false;
		} else if(main.pointOnSquare(x, y, 430, 0, 480, 50))
		{
			main.gamePaused = false;
		}
	}
	protected void clickDownStartFight(float x, float y)
	{
		if(main.pointOnSquare(x, y, 430, 0, 480, 50))
		{
			main.gamePaused = false;
		}
		if(main.pointOnSquare(x, y, 180, 171, 300, 219))
		{
			main.activity.startFight(main.startingLevel);
			main.invalidate();
		}
		if(main.startingLevel != 1)
		{
			if(main.pointOnSquare(x, y, 30, 104, 130, 144))
			{			
				main.changeDifficulty(10);
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 130, 29, 230, 69))
			{
				main.changeDifficulty(6);
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 250, 29, 350, 69))
			{
				main.changeDifficulty(3);
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 350, 104, 450, 144))
			{
				main.changeDifficulty(0);
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 16, 234, 116, 304))
			{
				main.drainHp = !main.drainHp;
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 132, 234, 232, 304))
			{
				main.lowerHp = !main.lowerHp;
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 248, 234, 348, 304))
			{
				main.limitSpells = !main.limitSpells;
				main.invalidate();
			} else if(main.pointOnSquare(x, y, 364, 234, 464, 304))
			{
				main.enemyRegen = !main.enemyRegen;
				main.invalidate();
			}
		}
	}
	protected void clickDownOptions(float x, float y)
	{
		if(main.pointOnSquare(x, y, 249, 174, 269, 194))
		{
			main.activity.shootTapScreen = false;
			main.activity.playEffect("pageflip");
			main.invalidate();
		}
		if(main.pointOnSquare(x, y, 417, 174, 437, 194))
		{
			main.activity.shootTapScreen = true;
			main.activity.playEffect("pageflip");
			main.invalidate();
		}
		if(main.pointOnSquare(x, y, 236, 200, 256, 220))
		{
			main.activity.shootTapDirectional = true;
			main.activity.playEffect("pageflip");
			main.invalidate();
		}
		if(main.pointOnSquare(x, y, 450, 200, 470, 220))
		{
			main.activity.shootTapDirectional = false;
			main.activity.playEffect("pageflip");
			main.invalidate();
		}
		if(main.pointOnSquare(x, y, 227, 147, 247, 167))
		{
			main.activity.stickOnRight = true;
			main.activity.playEffect("pageflip");
			main.changePlayOptions();
		}
		if(main.pointOnSquare(x, y, 338, 147, 358, 167))
		{
			main.activity.stickOnRight = false;
			main.activity.playEffect("pageflip");
			main.changePlayOptions();
		}
		if(main.pointOnSquare(x, y, 430, 0, 480, 50))
		{
			main.currentPause = "paused";
			main.invalidate();
		}
	}
	protected void clickDownPaused(float x, float y)
	{
		boolean clicked = true;
		if(main.pointOnSquare(x, y, 430, 0, 480, 50))
        {
			main.gamePaused = false;
        } else if(main.pointOnSquare(x, y, 262, 95, 414, 157))
        {
        	main.activity.startMenu(false);
        } else if(main.pointOnSquare(x, y, 245, 181, 414, 243))
        {
        	main.currentPause = "options";
			main.invalidate();
        } else if(main.pointOnCircle(x, y, 60, 60, 35) && main.activity.pHeal>0)
        {
        	player.getPowerUp(1);
        	main.activity.pHeal--;
        } else if(main.pointOnCircle(x, y, 160, 60, 35) && main.activity.pCool>0)
        {
        	player.getPowerUp(2);
        	main.activity.pCool--;
        } else if(main.pointOnCircle(x, y, 60, 160, 35) && main.activity.pWater>0)
        {
        	if(main.playerType!=1)
        	{
        		player.getPowerUp(3);
        		main.activity.pWater--;
        	} else
        	{
        		main.alreadyWorshipping();
        	}
        } else if(main.pointOnCircle(x, y, 160, 160, 35) && main.activity.pEarth>0)
        {
        	if(main.playerType!=3)
        	{
	        	player.getPowerUp(4);
	        	main.activity.pEarth--;
        	} else
        	{
        		main.alreadyWorshipping();
        	}
        } else if(main.pointOnCircle(x, y, 60, 260, 35) && main.activity.pAir>0)
        {
        	if(main.playerType!=2)
        	{
	        	player.getPowerUp(5);
	        	main.activity.pAir--;
        	} else
        	{
        		main.alreadyWorshipping();
        	}
        } else if(main.pointOnCircle(x, y, 160, 260, 35) && main.activity.pFire>0)
        {
        	if(main.playerType!=0)
        	{
	        	player.getPowerUp(6);
	        	main.activity.pFire--;
        	} else
        	{
        		main.alreadyWorshipping();
        	}
        } else
        {
        	clicked = false;
        }
		if(clicked)
		{
			main.invalidate();
		}
	}
	protected boolean clickDownNotPausedMenu(float setX, float setY)
	{		
		boolean hitButton = false;
		if(main.pointOnScreen(setX, setY))
		{
			hitButton = true;
			double x = screenX(setX);
			double y = screenY(setY);
			if(getDistance(x, y, 20, 625)<15)
	        {
		        main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Posiedon";
				main.invalidate();
	        } else if(getDistance(x, y, 475, 625)<15)
	        {
				main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Zues";
				main.invalidate();
	        } else if(getDistance(x, y, 20, 420)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Apollo";
				main.invalidate();
	        } else if(getDistance(x, y, 475, 420)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Hades";
				main.invalidate();
	        } else if(getDistance(x, y, 174, 420)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Hephaestus";
				main.invalidate();
	        } else if(getDistance(x, y, 326, 630)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Ares";
				main.invalidate();
	        } else if(getDistance(x, y, 174, 630)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Athena";
				main.invalidate();
	        } else if(getDistance(x, y, 326, 420)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Worship Hermes";
				main.invalidate();
	        } else if(getDistance(x, y, 99, 377)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Ambrosia";
				main.invalidate();
	        } else if(getDistance(x, y, 15, 322)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Cooldown";
				main.invalidate();
	        } else if(getDistance(x, y, 127, 334)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Posiedon's Shell";
				main.invalidate();
	        } else if(getDistance(x, y, 16, 263)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Hades' Helm";
				main.invalidate();
	        } else if(getDistance(x, y, 126, 234)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Zues's Armor";
				main.invalidate();
	        } else if(getDistance(x, y, 53, 376)<15)
	        {
	        	main.gamePaused = true;
				main.currentPause = "buy";
				main.buyingItem = "Apollo's Flame";
				main.invalidate();
	        } else
	        {
	        	hitButton = false;
	        }
		}
		return hitButton;
	}
	protected boolean clickDownNotPausedTutorial(float setX, float setY)
	{
		boolean hitButton = false;
		if(main.pointOnScreen(setX, setY))
		{
			hitButton = true;
			double x = visualX(setX);
			double y = visualY(setY);
			if(main.pointOnSquare(setX, setY, 200, 280, 280, 320))
	        {
				main.activity.playEffect("pageflip");
				main.currentTutorial ++;
				if(main.currentTutorial == 2)
				{
					main.enemies[0] = new Enemy_Target(main, 145, 150, 0, false);
					main.enemies[1] = new Enemy_Target(main, 355, 150, 180, false);
					main.enemies[2] = new Enemy_Target(main, 250, 25, 90, false);
					main.enemies[3] = new Enemy_Target(main, 250, 275, -90, false);
				}
				if(main.currentTutorial == 3)
				{
					main.enemies[0] = new Enemy_Target(main, 52, 150, 0, false);
					main.enemies[1] = new Enemy_Target(main, 415, 25, 90, true);
					main.enemies[2] = new Enemy_Target(main, 435, 275, -90, true);
					main.enemies[3] = null;
				}
				if(main.currentTutorial == 4)
				{
					main.enemies[0] = null;
					main.enemies[1] = null;
					main.enemies[2] = null;
					main.enemies[3] = null;
				}
				if(main.currentTutorial == 7)
				{
					if(main.activity.levelBeaten == 0)
		        	{
						main.activity.levelBeaten++;
		        	}
					main.activity.startMenu(false);
				}
				main.imageLibrary.directionsTutorial = main.imageLibrary.loadImage("menu_tutorial000"+Integer.toString(main.currentTutorial), 217, 235);
				main.invalidate();
	        } else
	        {
	        	hitButton = false;
	        }
		}
		return hitButton;
	}
	protected void clickDownNotPaused(float x, float y, int ID, boolean firstPointer)
	{
		if(main.pointOnSquare(x, y, 430, 0, 480, 50)&&main.activity.stickOnRight)
        {
        	main.gamePaused = true;
        	main.currentPause = "paused";
        	main.invalidate();
        } else if(main.pointOnSquare(x, y, 0, 0, 50, 50)&&!main.activity.stickOnRight)
        {
        	main.gamePaused = true;
        	main.currentPause = "paused";
        	main.invalidate();
        } else if(main.pointOnSquare(x, y, buttonShiftX+12, 82, buttonShiftX+82, 152))
        {
        	player.teleport(visualX(x), visualY(y));
        } else if(main.pointOnSquare(x, y, buttonShiftX+12, 12, buttonShiftX+82, 82))
        {
        	player.burst();
        }else if(main.pointOnSquare(x, y, buttonShiftX+12, 152, buttonShiftX+82, 222))
        {
        	player.roll();
        } else if(main.pointOnCircle(x, y, 53+(buttonShiftX*0.95897), 267, 60) && !main.activity.shootTapScreen)
        {
        	if(main.activity.shootTapDirectional)
        	{
        		double temp1 = player.rads;
            	player.rads = Math.atan2(visualY(y)-267, visualX(x)-(53+(buttonShiftX*0.95897)));
        		player.releasePowerBall();
        		player.rads = temp1;
        				
        	} else
        	{
        		player.releasePowerBall();
        	}
        } else if(main.pointOnScreen(x, y))
        {
        	if(player.teleporting)
        	{
            	player.teleport(screenX(x), screenY(y));
        	} else if(main.activity.shootTapScreen)
        	{
        		if(main.activity.shootTapDirectional)
            	{
        			double temp1 = player.rads;
        			player.rads = Math.atan2(screenY(y)-player.y, screenX(x)-player.x);
            		player.releasePowerBall();
            		player.rads = temp1;
            	} else
            	{
            		player.releasePowerBall();
            	}
        	}
        } else if(main.pointOnCircle(x, y, 427-(buttonShiftX*0.95897), 267, 60))
        {
        	player.touching = true;
        	player.touchX = visualX(x)-(427-(buttonShiftX*0.95897));
        	player.touchY = visualY(y)-267;
        	trackingId = ID;
        }
	}
	protected double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(visualX(x1)-visualX(x2), 2) + Math.pow(visualY(y1)-visualY(y2), 2));
	}
	protected double screenX(double x)
	{
		return (visualX(x)-90)-main.xShiftLevel;
	}
	protected double screenY(double y)
	{
		return (visualX(y)+10)-main.yShiftLevel;
	}
	protected double visualX(double x)
	{
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	protected double visualY(double y)
	{
		return ((y-screenMinY)/screenDimensionMultiplier);
	}
}