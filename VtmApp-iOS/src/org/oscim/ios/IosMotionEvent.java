package org.oscim.ios;

import org.oscim.event.MotionEvent;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UITouch;
import org.robovm.apple.uikit.UIView;

public class IosMotionEvent extends MotionEvent{

	UIEvent event;
	
	int type;
		
	int numTouches = 1;
	float [] touchX = new float[10];
	float [] touchY = new float[10];
	
	public IosMotionEvent(int type, UIEvent e) {
		this.type = type;
		this.event = e;
		
		numTouches = e.getAllTouches().size();
		int i = 0;
		
		for (UITouch touch: e.getAllTouches()){
			CGPoint loc = touch.getLocation(touch.getView());
			
			touchX[i] = (float) loc.x();
			touchY[i] = (float) loc.y();
			
			i++;
		}
	}
	
	public IosMotionEvent(int type, double x, double y) {
		this.type = type;
		numTouches = 1;
		touchX[0] = (float) x;
		touchY[0] = (float) y;
	}
	
	@Override
	public long getTime() {
		return (long) (event.getTimestamp() * 1000000000);
	}

	@Override
	public int getAction() {
		return type;
	}

	@Override
	public float getX() {
		return touchX[0];
	}

	@Override
	public float getY() {
		return touchY[0];
	}

	@Override
	public float getX(int idx) {
		return touchX[idx];
	}

	@Override
	public float getY(int idx) {
		return touchY[idx];
	}

	@Override
	public int getPointerCount() {
		return numTouches;
	}
}
