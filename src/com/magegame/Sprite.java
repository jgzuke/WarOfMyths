/**
 * behavior for all sprites
 */
package com.magegame;

abstract public class Sprite
{
	protected double width;
	protected double height;
	protected int currentFrame = 0;
	protected double x;
	protected double y;
	protected double rotation;
	protected Controller control;
	protected boolean deleted = false;
	public Sprite()
	{}
	/**
	 * called every frame, performs desired actions
	 */
	abstract protected void frameCall();
}