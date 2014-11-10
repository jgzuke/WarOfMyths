package com.example.magegame;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.Surface;

public final class PlayerHandleMove implements SensorEventListener, OnGestureListener
{	
	private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Controller mainController;
    private GestureDetector gestureScanner;
	public PlayerHandleMove(Controller controller)
	{
		//mainController = controller;
		//mSensorManager = (SensorManager) mainController.context.getSystemService(Context.SENSOR_SERVICE);
		//mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		//mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	    //gestureScanner = new GestureDetector(mainController.context, this);
	}	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
	    if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
	        return;
	     
	    switch (mainController.player.mDisplay.getRotation()) {
	    case Surface.ROTATION_0:
	    	mainController.player.mSensorX = event.values[0];
	    	mainController.player.mSensorY = event.values[1];
	        break;
	    case Surface.ROTATION_90:
	    	mainController.player.mSensorX = -event.values[1];
	    	mainController.player.mSensorY = event.values[0];
	        break;
	    case Surface.ROTATION_180:
	    	mainController.player.mSensorX = -event.values[0];
	    	mainController.player.mSensorY = -event.values[1];
	        break;
	    case Surface.ROTATION_270:
	    	mainController.player.mSensorX = event.values[1];
	    	mainController.player.mSensorY = -event.values[0];
	        break;
	    }
	}
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,	float velocityY)
	{
		mainController.player.rads = Math.atan2(velocityY, velocityX);
		mainController.player.rotation = mainController.player.rads * mainController.player.r2d;
		mainController.player.roll();
		return false;
	}
	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
}