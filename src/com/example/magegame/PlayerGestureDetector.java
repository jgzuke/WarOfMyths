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
		if(main.gameRunning)
		{
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
		} else
		{
			return false;
		}
    }
	protected void clickDown(float x, float y, int ID, boolean firstPointer)
	{
		if(main.gamePaused)
		{
			clickDownPaused(x, y, ID, firstPointer);
		} else
		{
			clickDownNotPaused(x, y, ID, firstPointer);
		}
	}
	protected void clickDownPaused(float x, float y, int ID, boolean firstPointer)
	{
		boolean clicked = true;
		if(main.pointOnSquare(x, y, 420, 0, 480, 60))
        {
			main.gamePaused = false;
        } else if(main.pointOnSquare(x, y, 245, 111, 432, 209))
        {
        	main.activity.startMenu(true);
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
        	player.getPowerUp(3);
        	main.activity.pWater--;
        } else if(main.pointOnCircle(x, y, 160, 160, 35) && main.activity.pEarth>0)
        {
        	player.getPowerUp(4);
        	main.activity.pEarth--;
        } else if(main.pointOnCircle(x, y, 60, 260, 35) && main.activity.pAir>0)
        {
        	player.getPowerUp(5);
        	main.activity.pAir--;
        } else if(main.pointOnCircle(x, y, 160, 260, 35) && main.activity.pFire>0)
        {
        	player.getPowerUp(6);
        	main.activity.pFire--;
        } else
        {
        	clicked = false;
        }
		if(clicked)
		{
			main.invalidate();
		}
	}
	protected void clickDownNotPaused(float x, float y, int ID, boolean firstPointer)
	{
		if(main.pointOnSquare(x, y, 420, 0, 480, 60))
        {
        	main.gamePaused = true;
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
            	player.rads = Math.atan2(visualY(y)-267, visualX(x)-(53+(buttonShiftX*0.95897)));
        		player.rotation = player.rads*player.r2d;
        		if(firstPointer)
        		{
	        		player.touchX = visualX(x)-267;
	            	player.touchY = visualY(y)-(53+(buttonShiftX*0.95897));
        		}
        		player.releasePowerBall();
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
	        		player.rads = Math.atan2(screenY(y)-player.y, screenX(x)-player.x);
	        		player.rotation = player.rads*player.r2d;
	        		if(firstPointer)
	        		{
		        		player.touchX = screenX(x)-player.x;
		            	player.touchY = screenY(y)-player.y;
	        		}
	        		player.releasePowerBall();
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