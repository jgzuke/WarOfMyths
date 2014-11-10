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
			buttonShiftX = 10;
		} else
		{
			buttonShiftX = 400;
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
		            	player.touchX = visualX(e.getX(trackingId))-(435-buttonShiftX);
		            	player.touchY = visualY(e.getY(trackingId))-275;
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
		if(main.pointOnSquare(x, y, 420, 0, 480, 60))
        {
        	main.activity.startMenu();
        } else if(main.pointOnSquare(x, y, buttonShiftX, 85, buttonShiftX+70, 155))
        {
        	player.teleport(visualX(x), visualY(y));
        } else if(main.pointOnSquare(x, y, buttonShiftX, 10, buttonShiftX+70, 80))
        {
        	player.burst();
        }else if(main.pointOnSquare(x, y, buttonShiftX, 160, buttonShiftX+70, 230))
        {
        	player.roll();
        } else if(main.pointOnSquare(x, y, buttonShiftX-5, 235, buttonShiftX+75, 315) && !main.activity.shootTapScreen)
        {
        	if(main.activity.shootTapDirectional)
        	{
            	player.rads = Math.atan2(visualY(y)-275, visualX(x)-(35+buttonShiftX));
        		player.rotation = player.rads*player.r2d;
        		if(firstPointer)
        		{
	        		player.touchX = visualX(x)-275;
	            	player.touchY = visualY(y)-(35+buttonShiftX);
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
        } else if(main.pointOnCircle(x, y, 435-buttonShiftX, 275, 50))
        {
        	player.touching = true;
        	player.touchX = visualX(x)-(435-buttonShiftX);
        	player.touchY = visualY(y)-275;
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