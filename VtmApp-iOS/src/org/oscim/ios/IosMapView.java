package org.oscim.ios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.TileGridLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Layers;
import org.oscim.map.Map;
import org.oscim.renderer.MapRenderer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.robovm.apple.coreanimation.CADisplayLink;
import org.robovm.apple.coregraphics.CGPoint;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSRunLoop;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.glkit.GLKView;
import org.robovm.apple.glkit.GLKViewDelegate;
import org.robovm.apple.glkit.GLKViewDrawableColorFormat;
import org.robovm.apple.glkit.GLKViewDrawableStencilFormat;
import org.robovm.apple.opengles.EAGLContext;
import org.robovm.apple.opengles.EAGLRenderingAPI;
import org.robovm.apple.uikit.UIEvent;
import org.robovm.apple.uikit.UITapGestureRecognizer;
import org.robovm.objc.Selector;
import org.robovm.objc.annotation.Method;
import org.robovm.rt.bro.annotation.Pointer;

public class IosMapView extends GLKView implements GLKViewDelegate{

	protected IosMap mMap;
	protected MapRenderer mMapRenderer;
	
	private VectorTileLayer mMapLayer;

	private Timer timer = new Timer();
	
	private EAGLContext context;

	private UITapGestureRecognizer tapRecognizer, doubleTapRecognizer;
	
	private CADisplayLink displayLink;
	
	private List<Runnable> runnables = new ArrayList<>();
	private List<Runnable> executedRunnables = new ArrayList<>();
		
	private void init(){
		Foundation.log("IosMapView.init");
		
		mMap = new IosMap(this);
		mMapRenderer = new MapRenderer(mMap);
		
		context = new EAGLContext(EAGLRenderingAPI.OpenGLES2);
		
		setDelegate(this);
		setContext(context);
		setMultipleTouchEnabled(true);
		setDrawableStencilFormat(GLKViewDrawableStencilFormat._8);
		setDrawableColorFormat(GLKViewDrawableColorFormat.RGBA8888);
		setEnableSetNeedsDisplay(false);
		
		Selector onTouchSelector = Selector.register("onTouch");
		Selector onMutitouchSelector = Selector.register("onMutiTouch");
		 
		tapRecognizer = new UITapGestureRecognizer(this, onTouchSelector);
		addGestureRecognizer(tapRecognizer);
		
		doubleTapRecognizer = new UITapGestureRecognizer(this, onMutitouchSelector);
		doubleTapRecognizer.setNumberOfTapsRequired(2);
		addGestureRecognizer(doubleTapRecognizer);
		
		Selector onFrame = Selector.register("onFrame");
		displayLink = CADisplayLink.create(this, onFrame);
		displayLink.addToRunLoop(NSRunLoop.getCurrent(), new NSString("kCFRunLoopDefaultMode"));
	}
	
	long lastFrame;
	float frameRate = 30;
	
	float deltha = 20;
	long tapPressedTime;
	float tapPressedX, tapPressedY;
		
	@Method
	public void onFrame(){
		synchronized (runnables) {
			executedRunnables.clear();
			executedRunnables.addAll(runnables);
			runnables.clear();
		}
		
		for (int i = 0; i < executedRunnables.size(); i++) {
			try {
				executedRunnables.get(i).run();
			} catch (Throwable e) {
				Utils.printThrowable(e);
			}
		}
		
		mMap.updateLayersExternal();
		display();
	}
	
	@Method
	public void onTouch(){
		CGPoint p = tapRecognizer.getLocationInView(this);
		
		//Foundation.log("IosMapView.onTouch, x = " + p.x() + ", y = " + p.y());
		MotionEvent event = new IosMotionEvent(MotionEvent.ACTION_UP, p.x(), p.y());
		mMap.handleGesture(Gesture.TAP, event);
		
		tapPressedX = (float) p.x();
		tapPressedY = (float) p.y();
		tapPressedTime = System.currentTimeMillis();
	}
	
	@Method
	public void onMutiTouch(){
		CGPoint p = doubleTapRecognizer.getLocationInView(this);
		
		//Foundation.log("IosMapView.onMutitouch, x = " + p.x() + ", y = " + p.y());
		
		MotionEvent event = new IosMotionEvent(MotionEvent.ACTION_UP, p.x(), p.y());
		mMap.handleGesture(Gesture.DOUBLE_TAP, event);
		mMap.input.fire(null, event);
	}
	
	public IosMapView(){
		init();
	}
	
	public IosMapView(CGRect rect){
		super(rect);
		init();
	}
	
	public Map getMap(){
		return mMap;
	}
	
	protected void createLayers() {
		mMap.layers().add(new TileGridLayer(mMap));
	}
	
	protected void initDefaultLayers(TileSource tileSource, boolean tileGrid, boolean labels,
	        boolean buildings) {
		Layers layers = mMap.layers();
		
		if (tileSource != null) {
			mMapLayer = mMap.setBaseMap(tileSource);
			mMap.setTheme(VtmThemes.DEFAULT);

			if (buildings)
				layers.add(new BuildingLayer(mMap, mMapLayer));

			if (labels)
				layers.add(new LabelLayer(mMap, mMapLayer));
		}

		if (tileGrid)
			layers.add(new TileGridLayer(mMap));
	}
	
	public void postRunnable(Runnable action) {
		synchronized (runnables) {
			runnables.add(action);
		}
	}

	public void postDelayedRunnable(final Runnable action, long delay) {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				postRunnable(action);
			}
		}, delay);
	}

	public void onRotate(){
		int w = (int) getBounds().getWidth();
		int h = (int) getBounds().getHeight();
		mMapRenderer.onSurfaceChanged(w, h);
	}
	
	private boolean created = false;
	
	@Override
	public void draw(GLKView view, CGRect rect) {
		try {
			int w = (int) rect.getWidth();
			int h = (int) rect.getHeight();
			
			if (!created){
				mMap.viewport().setScreenSize(w, h);
				mMapRenderer.onSurfaceCreated();
				mMapRenderer.onSurfaceChanged(w, h);
				
				createLayers();
				
				created = true;
			}
//			mMapRenderer.onDrawFrame();
			if (mMap.needsRedraw()){
				//mMap.updateLayersExternal();
				mMapRenderer.onDrawFrame();
			}
			
		} catch (Throwable e) {
			Utils.printThrowable(e);
		}
	}
		
	@Method(selector = "touchesBegan:withEvent:")
	public void touchesBegan (@Pointer long touches, final UIEvent nativeEvent) {		
		int action = nativeEvent.getAllTouches().size() > 1? MotionEvent.ACTION_POINTER_DOWN: MotionEvent.ACTION_DOWN;
		IosMotionEvent event = new IosMotionEvent(action, nativeEvent);
		
		if (System.currentTimeMillis() - tapPressedTime < 250 && nativeEvent.getAllTouches().size() == 1){
			event.type = MotionEvent.ACTION_MOVE;
			mMap.handleGesture(Gesture.DOUBLE_TAP, event);
		}
		else
			mMap.input.fire(null, event);
	}

	@Method(selector = "touchesCancelled:withEvent:")
	public void touchesCancelled (@Pointer long touches, UIEvent nativeEvent) {
		IosMotionEvent event = new IosMotionEvent(MotionEvent.ACTION_CANCEL, nativeEvent);
		mMap.input.fire(null, event);
	}

	@Method(selector = "touchesEnded:withEvent:")
	public void touchesEnded (@Pointer long touches, UIEvent event) {
		String s = String.format("touchesEnded, time = " + new Date());
		//Foundation.log(s);
		
		int action = event.getAllTouches().size() > 1? MotionEvent.ACTION_POINTER_UP: MotionEvent.ACTION_UP;
		
		//mMap.handleGesture(Gesture.PRESS, new IosMotionEvent(this, MotionEvent.ACTION_UP, event));
		mMap.input.fire(null, new IosMotionEvent(action, event));
	}

	@Method(selector = "touchesMoved:withEvent:")
	public void touchesMoved (@Pointer long touches, UIEvent event) {
		String s = String.format("touchesMoved, time = " + new Date());
		//Foundation.log(s);
		
		//mMap.handleGesture(Gesture.PRESS, new IosMotionEvent(this, MotionEvent.ACTION_MOVE, event));
		mMap.input.fire(null, new IosMotionEvent(MotionEvent.ACTION_MOVE, event));
	}
}
