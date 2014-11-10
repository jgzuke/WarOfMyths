package com.example.magegame;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class PlayerGestureDetector implements OnTouchListener {
	private Player player;
	private Controller main;
	private long milliSecondsLastPress;
	private double startFlingX;
	private double startFlingY;
	private double screenDimensionMultiplier;
	private int screenMinX;
	private int screenMinY;
	public PlayerGestureDetector(Controller mainSet)
	{
		main = mainSet;
		screenDimensionMultiplier = mainSet.screenDimensionMultiplier;
		screenMinX = mainSet.screenMinX;
		screenMinY = mainSet.screenMinY;
	}
	public void setPlayer(Player playerSet)
	{
		player = playerSet;
	}
	@Override
    public boolean onTouch(View v, MotionEvent e) {
		if(main.gameRunning)
		{
		        switch (e.getAction()){
		        case MotionEvent.ACTION_DOWN:
		        	/*if(System.currentTimeMillis()-milliSecondsLastPress < 200)
		        	{
		        		// TODO double click
		        	} else */if(main.pointOnSquare(e.getX(), e.getY(), 420, 0, 480, 60))
		            {
		            	main.activity.startMenu();
		            } else if(main.pointOnSquare(e.getX(), e.getY(), 10, 240, 80, 310))
		            {
		            	player.teleport(visualX(e.getX()), visualY(e.getY()));
		            } else if(main.pointOnSquare(e.getX(), e.getY(), 10, 10, 80, 80))
		            {
		            	player.burst();
		            } else if(main.pointOnSquare(e.getX(), e.getY(), 10, 86, 80, 157))
		            {
		            	player.chargingSp = true;
		            } else if(main.pointOnSquare(e.getX(), e.getY(), 10, 163, 80, 233))
		            {
		            	player.roll();
		            } else if(main.pointOnScreen(e.getX(), e.getY()))
		            {
		            	if(player.teleporting)
		            	{
			            	player.teleport(visualX(e.getX()), visualY(e.getY()));
		            	} else
		            	{
		            		//startFlingX = e.getX();
			            	//startFlingY = e.getY();
		            		player.rads = Math.atan2(visualY(e.getY())-player.y, visualX(e.getX())-player.x);
			        		player.rotation = player.rads*player.r2d;
			        		player.touchX = visualX(e.getX())-player.x;
			            	player.touchY = visualY(e.getY())-player.y;
			        		player.releasePowerBall();
		            	}
		            } else if(main.pointOnCircle(e.getX(), e.getY(), 437, 267, 50))
		            {
		            	player.touching = true;
		            	player.touchX = visualX(e.getX())-437;
		            	player.touchY = visualY(e.getY())-267;
		            } else
		            {
		            	startFlingX = e.getX();
		            	startFlingY = e.getY();
		            }
		        	milliSecondsLastPress = System.currentTimeMillis();
		        break;
		        case MotionEvent.ACTION_MOVE:
		            if(player.touching)
		            {
		            	player.touchX = visualX(e.getX())-437;
		            	player.touchY = visualY(e.getY())-267;
		            }
		        break;
		        case MotionEvent.ACTION_UP:
		        	/*if(System.currentTimeMillis()-milliSecondsLastPress < 200)
		        	{
		        		Log.e("game", Long.toString(System.currentTimeMillis()-milliSecondsLastPress));
			        	if(getDistance(e.getX(), e.getY(), startFlingX, startFlingY) > 80)
			        	{
			        		player.rads = Math.atan2(visualY(e.getY())-player.y, visualX(e.getX())-player.x);
			        		player.rotation = player.rads*player.r2d;
			        		player.roll();
			        	} else
			        	{
			        		player.rads = Math.atan2(visualY(e.getY())-player.y, visualX(e.getX())-player.x);
			        		player.rotation = player.rads*player.r2d;
			        		player.touchX = visualX(e.getX())-player.x;
			            	player.touchY = visualY(e.getY())-player.y;
			        		player.releasePowerBall();
			        	}
		        	}*/
		        	player.touching = false;
		        	player.chargingSp = false;
		        break;
		        }
		        return true;
		} else
		{
			return false;
		}
    }
	public double getDistance(double x1, double y1, double x2, double y2)
	{
		return Math.sqrt(Math.pow(visualX(x1)-visualX(x2), 2) + Math.pow(visualY(y1)-visualY(y2), 2));
	}
	public double visualX(double x)
	{
		return (x-screenMinX)/screenDimensionMultiplier;
	}
	public double visualY(double y)
	{
		return (y-screenMinY)/screenDimensionMultiplier;
	}
}