package com.example.magegame;
import android.graphics.Bitmap;
abstract public class drawnSprite extends Sprite
{
	public int imageWidth, imageHeight;
	public Bitmap visualImage = null;
	public drawnSprite()
	{}
	public void setImageDimensions()
	{
		imageWidth = visualImage.getWidth();
		imageHeight = visualImage.getHeight();
	}
}