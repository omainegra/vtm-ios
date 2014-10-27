package org.oscim.ios.canvas;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.oscim.backend.AssetAdapter;
import org.oscim.backend.GL20;
import org.oscim.backend.GLAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.ios.Utils;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGBitmapInfo;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGImage;
import org.robovm.apple.coregraphics.CGImageAlphaInfo;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSData;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIImage;
import org.robovm.rt.bro.ptr.BytePtr;

public class IosBitmap implements Bitmap {

	boolean disposable;
		
	CGBitmapContext context;
	ByteBuffer buffer;
	
	int width, height;
	
	private void createContext(int width, int height){
		this.width = width;
		this.height = height;
		
		CGColorSpace colorSpace = CGColorSpace.createDeviceRGB();
		context = CGBitmapContext.create(width, height, 8, 0, colorSpace, new CGBitmapInfo(CGImageAlphaInfo.PremultipliedLast.value() | CGBitmapInfo.ByteOrder32Big.value()));
		colorSpace.release();
		
		context.setAllowsAntialiasing(true);
		context.setAllowsFontSmoothing(true);
		context.setShouldAntialias(true);
		context.setShouldSmoothFonts(true);
		
		context.setAllowsFontSubpixelPositioning(true);
		context.setAllowsFontSubpixelQuantization(true);
		context.setShouldSubpixelPositionFonts(false);
		context.setShouldSubpixelQuantizeFonts(false);
		
//		context.translateCTM(0, height);
//		context.scaleCTM(1.0, -1.0);
		
		context.getTextMatrix().scale(1, -1);
		buffer = context.getData().as(BytePtr.class).asByteBuffer(width*height*4);
	}
		
	/** always argb8888 */
	public IosBitmap(int width, int height, int format) {
		Foundation.log("IosBitmap.init, width = " + width + ", height = " + height + ", threadName = " + Thread.currentThread().getName());
		try {
			createContext(width, height);
			disposable = true;
		} catch (Throwable e) {
			Utils.printThrowable(e);
		}
	}

	public IosBitmap(String fileName) throws IOException {
		this(AssetAdapter.readFileAsStream(fileName));
	}

	public IosBitmap(InputStream inputStream) throws IOException {
		try {
			NSData data = new NSData(Utils.toByteArray(inputStream));
			CGImage image = new UIImage(data).getCGImage();
			
			createContext((int) image.getWidth(), (int) image.getHeight());
			context.drawImage(new CGRect(0, 0, width, height), image);
			
			Foundation.log("IosBitmap.init, width = " + width + ", height = " + height);
		} catch (Throwable e) {
			Utils.printThrowable(e);
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public void recycle() {
		context.dispose();
	}

	@Override
	public int[] getPixels() {
		return null;
	}
	
	public CGBitmapContext getContext(){
		return context;
	}

	@Override
	public void eraseColor(int color) {
		context.clearRect(new CGRect(0, 0, getWidth(), getHeight()));
		
//		context.setFillColorWithColor(UIColor.white().getCGColor());
//		context.addRect(new CGRect(0, 0, getWidth(), getHeight()));
//		context.fillPath();
	}

	@Override
	public void uploadToTexture(boolean replace) {
		try {
			if (replace){
				GLAdapter.get().glTexSubImage2D(GL20.GL_TEXTURE_2D, 0, 0, 0, getWidth(), getHeight(), GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buffer);
			}
			else{
				GLAdapter.get().glTexImage2D(GL20.GL_TEXTURE_2D, 0, GL20.GL_RGBA, getWidth(), getHeight(), 0, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, buffer);
			}
		} catch (Throwable e) {
			Utils.printThrowable(e);
		} 
	}

	@Override
	public boolean isValid() {
		return true;
	}
	
	@Override
	public String toString(){
		return "[ " + getWidth() + ", " + getHeight() + "]";
	}
}
