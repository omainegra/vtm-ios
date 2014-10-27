package org.oscim.ios.canvas;

import java.io.IOException;
import java.io.InputStream;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Paint;
import org.oscim.ios.Utils;

public class IosGraphics extends CanvasAdapter {

	public static void init() {
		CanvasAdapter.init(new IosGraphics());
	}

	@Override
	protected Bitmap decodeBitmapImpl(InputStream inputStream) {
		try {
			return new IosBitmap(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}catch (Throwable e) {
			Utils.printThrowable(e);
		}
		
		return null;
	}

	@Override
	protected Bitmap loadBitmapAssetImpl(String fileName) {
		try {
			return new IosBitmap(fileName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Throwable e) {
			Utils.printThrowable(e);
		}
		return null;
		
	}

	@Override
	protected Bitmap newBitmapImpl(int width, int height, int format) {
		return new IosBitmap(width, height, format);
	}

	@Override
	protected Canvas newCanvasImpl() {
		return new IosCanvas();
	}

	@Override
	protected Paint newPaintImpl() {
		return new IosPaint();
	}

}
