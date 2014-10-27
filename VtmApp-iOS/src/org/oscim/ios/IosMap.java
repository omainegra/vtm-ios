package org.oscim.ios;

import org.oscim.map.Map;
import org.robovm.apple.foundation.Foundation;

public class IosMap extends Map {
	
	IosMapView mapView;
	
	private volatile boolean needsRedraw = true;
			
	public IosMap(IosMapView mapView){
		this.mapView = mapView;
	}
	
	@Override
	public void updateMap(boolean redraw) {
		needsRedraw |= redraw;
	}

	@Override
	public void render() {
		updateMap(true);
	}

	@Override
	public boolean post(Runnable action) {
		mapView.postRunnable(action);
		return true;
	}

	@Override
	public boolean postDelayed(Runnable action, long delay) {
		mapView.postDelayedRunnable(action, delay);
		return true;
	}

	@Override
	public int getWidth() {
		return (int) mapView.getBounds().getWidth();
	}

	@Override
	public int getHeight() {
		return (int) mapView.getBounds().getHeight();
	}
		
	public boolean needsRedraw(){
		if (!needsRedraw) return false;
		
		needsRedraw = false;
		return true;
	}
		
	public void updateLayersExternal() {
		updateLayers();
	}
}
