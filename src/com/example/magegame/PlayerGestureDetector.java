package com.example.magegame;
import android.util.Log;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

class PlayerGestureDetector implements OnGestureListener, OnTouchListener
{
	private Player player;
	private Controller mainController;
	private GestureDetector gestureScanner;
	public PlayerGestureDetector(Player setPlayer, Controller control)
	{
		player = setPlayer;
		mainController = control;
		gestureScanner = new GestureDetector(mainController.context,this);
		Log.e("game", "worked");
	}
	@Override
	public boolean onDown(MotionEvent e) {
		Log.e("game", "downed");
		return true;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY)
	{
		Log.e("game", "roll");
		player.rads = Math.atan2(velocityY, velocityX);
		player.rotation = player.rads * player.r2d;
		player.roll();
		return true;
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		Log.e("game", "tap");
		if(mainController.pointOnSquare(e.getX(), e.getY(), 15, 152, 75, 212))
        {
        	player.teleport(e.getX(), e.getY());
        } else if(mainController.pointOnSquare(e.getX(), e.getY(), 405, 87, 465, 147))
        {
        	player.burst();
        } else if(mainController.pointOnSquare(e.getX(), e.getY(), 405, 152, 465, 212))
        {
        	player.chargingSp = true;
        } else if(mainController.pointOnScreen(e.getX(), e.getY()) && player.teleporting)
        {
        	player.teleport((e.getX()-mainController.screenMinX)/mainController.screenDimensionMultiplier, (e.getY()-mainController.screenMinY)/mainController.screenDimensionMultiplier);
        } else
        {
        	player.rads = Math.atan2(e.getX()-player.getX(), e.getY()-player.getY());
			player.rotation = player.rads * player.r2d;
			player.releasePowerBall();
		}
		return true;
	}
	@Override
	public void onLongPress(MotionEvent e) {
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return true;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
    public boolean onTouch(View v, MotionEvent e) {
        switch (e.getAction()){
        case MotionEvent.ACTION_DOWN:
        	if(mainController.pointOnSquare(e.getX(), e.getY(), 15, 152, 75, 212))
            {
            	player.teleport(e.getX(), e.getY());
            } else if(mainController.pointOnSquare(e.getX(), e.getY(), 405, 87, 465, 147))
            {
            	player.burst();
            } else if(mainController.pointOnSquare(e.getX(), e.getY(), 405, 152, 465, 212))
            {
            	player.chargingSp = true;
            } else if(mainController.pointOnScreen(e.getX(), e.getY()) && player.teleporting)
            {
            	player.teleport((e.getX()-mainController.screenMinX)/mainController.screenDimensionMultiplier, (e.getY()-mainController.screenMinY)/mainController.screenDimensionMultiplier);
            } else
            {
            	player.touching = true;
            	player.touchX = (e.getX()-mainController.screenMinX)/mainController.screenDimensionMultiplier;
            	player.touchY = (e.getY()-mainController.screenMinY)/mainController.screenDimensionMultiplier;
            }
        break;
        case MotionEvent.ACTION_MOVE:
            if(player.touching)
            {
            	player.touchX = (e.getX()-mainController.screenMinX)/mainController.screenDimensionMultiplier;
            	player.touchY = (e.getY()-mainController.screenMinY)/mainController.screenDimensionMultiplier;
            }
        break;
        case MotionEvent.ACTION_UP:
        	player.touching = false;
        	player.chargingSp = false;
        break;
        }
        return true;
    }
}