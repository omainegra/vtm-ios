package org.oscim.ios.canvas;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.backend.canvas.Paint.Style;
import org.oscim.ios.Utils;
import org.robovm.apple.corefoundation.CFMutableDictionary;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGColor;
import org.robovm.apple.coretext.CTFont;
import org.robovm.apple.coretext.CTLine;
import org.robovm.apple.coretext.CoreText;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.foundation.NSDictionary;
import org.robovm.apple.foundation.NSNumber;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIColor;

public class IosCanvas implements Canvas {

	IosBitmap bitmap;
	
	
	public IosCanvas() {
	}

	@Override
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = (IosBitmap) bitmap;
	}
	
	@Override
	public void drawText(String string, float x, float originalY, Paint p) {
		try {
			double y = bitmap.height - originalY;
			
			IosPaint paint = (IosPaint) p;
			CGBitmapContext context = bitmap.getContext();
				
			
			
			
//			NSAttributedStringAttributes strAttributes = new NSAttributedStringAttributes();
//			strAttributes.setFont(paint.getFont());
//			strAttributes.setStrokeColor(UIColor.red());
//			
//			NSAttributedString attrString = new NSAttributedString(string, strAttributes);
//			CTLine line = CTLine.create(attrString);
//			
			CFMutableDictionary dictionary = CFMutableDictionary.create();
			dictionary.put(new NSString(CoreText.Value__kCTFontAttributeName()), paint.getCTFont());	
			
			if (paint.getStyle() == Style.STROKE){
//				Foundation.log("drawText, style = STROKE, stroke = " + paint.getStrokeWidth());
				dictionary.put(new NSString(CoreText.Value__kCTLigatureAttributeName()), NSNumber.valueOf(2));
				dictionary.put(new NSString(CoreText.Value__kCTStrokeColorAttributeName()), paint.getCGColor());
				dictionary.put(new NSString(CoreText.Value__kCTStrokeWidthAttributeName()), NSNumber.valueOf(3));		
			}
			else{	
//				Foundation.log("drawText, style = FILL, stroke = " + paint.getStrokeWidth());
				dictionary.put(new NSString(CoreText.Value__kCTLigatureAttributeName()), NSNumber.valueOf(2));
				dictionary.put(new NSString(CoreText.Value__kCTForegroundColorAttributeName()), paint.getCGColor());
				dictionary.put(new NSString(CoreText.Value__kCTStrokeWidthAttributeName()), NSNumber.valueOf(0.0));
			}

			NSAttributedString attrString = new NSAttributedString(string, new NSAttributedStringAttributes(dictionary.as(NSDictionary.class)));
			CTLine line = CTLine.create(attrString);
			
			context.setTextPosition(x, y);
			context.setLineCap(paint.getCGLineCap());
			line.draw(context);
			line.release();
			
			//Foundation.log("IosCanvas.drawText in " + (System.currentTimeMillis() - time) + " millis, thread = " + Thread.currentThread().getName() + ", style = " + paint.getStyle() + ", cap = " + paint.getCGLineCap());
		} catch (Throwable e) {
			Utils.printThrowable(e);
		}
	}

//	@Override
//	public void drawText(String string, float x, float originalY, Paint p) {
//		try {
//			long time = System.currentTimeMillis();
//			
//			double y = bitmap.height - originalY;
//			
//			IosPaint paint = (IosPaint) p;
//			CGBitmapContext context = bitmap.getContext();
//			
//			int intColor = paint.getColor();
//			Style style = paint.getStyle();
//			
////			float lineWidth = paint.getStrokeWidth();
////
//			double r = Color.rToFloat(intColor);
//			double g = Color.gToFloat(intColor);
//			double b = Color.bToFloat(intColor);
//			double a = Color.aToFloat(intColor);
////			
////			CGColor color = CGColor.create(CGColorSpace.createDeviceRGB(), new double[]{r, g, b, a});
////			
////			Random r = new Random();
////			for (int i = 0; i < bitmap.width; i += bitmap.width/8){
////				for (int j = 0; j < bitmap.height; i += bitmap.height/8){
////					UIColor c = new UIColor(r.nextDouble(), r.nextDouble(), r.nextDouble(), 1.0);
////					
////					context.addEllipseInRect(new CGRect(i, j, bitmap.width/8, bitmap.height/8));
////					context.setFillColorWithColor(c.getCGColor());
////					context.fillPath();	
////				}
////			}
//			
////			context.addEllipseInRect(new CGRect(x, originalY, 5, 5));
////			context.setFillColorWithColor(UIColor.blue().getCGColor());
////			context.fillPath();
////						
////			context.addEllipseInRect(new CGRect(x, y, 5, 5));
////			context.setFillColorWithColor(UIColor.red().getCGColor());
////			context.fillPath();	
////			
////			context.selectFont(paint.font.getFontName(), paint.getFontSize(), CGTextEncoding.MacRoman);
//			
////			context.saveGState();
//			
//			
//			CTFont font = paint.getCTFont();
//						
//			CFMutableDictionary dictionary = CFMutableDictionary.create();
//			dictionary.put(new NSString(CoreText.Value__kCTFontAttributeName()), font);
//			
//			if (paint.getStyle() == style.FILL){
//				dictionary.put(new NSString(CoreText.Value__kCTForegroundColorAttributeName()), new UIColor(r, g, b, a));
//			}
//			else{
//				dictionary.put(new NSString(CoreText.Value__kCTForegroundColorAttributeName()), new UIColor(r, g, b, a));
//			}
////			dictionary.put(new NSString(CoreText.Value__kCTStrokeColorAttributeName()), UIColor.red().getCGColor());
////			dictionary.put(new NSString(CoreText.Value__kCTStrokeWidthAttributeName()), NSNumber.valueOf(5));
//
//			NSAttributedString attrString = new NSAttributedString(string, dictionary.as(NSDictionary.class));
//			CTLine line = CTLine.create(attrString);
//			
//			context.setTextPosition(x, y);
//			line.draw(context);
//			line.release();
//			
////			attrString.release();
////			context.restoreGState();
//			
////			NSMutableDictionary<NSString, NSObject> params = new NSMutableDictionary<>();
////			params.put(new NSString("NSFont"), paint.getFont());
////			params.put(new NSString("NSColor"), UIColor.red());
////			params.put(new NSString("NSStrokeColor"), UIColor.green());
////			params.put(new NSString("NSStrokeWidth"), NSNumber.valueOf(4));
////			
////			UIGraphics.pushContext(context);
////			NSStringExtensions.draw(new NSString(string), new CGPoint(x, originalY), params);
//			
//			//UIGraphics.popContext();
//			
////			Foundation.log("IosCanvas.drawText, attrString = " + attrString);
//			
//			//if (style == Style.FILL){
//				//context.setLineWidth(2);
////				context.setTextDrawingMode(CGTextDrawingMode.Fill);
////				context.setFillColorWithColor(new UIColor(r, g, b, a).getCGColor());
//			//}
//			//else{
//			//	context.setLineWidth(0.25);
//			//	context.setTextDrawingMode(CGTextDrawingMode.Stroke);
//			//	context.setStrokeColorWithColor(new UIColor(Color.rToFloat(intColor), Color.gToFloat(intColor), Color.bToFloat(intColor), Color.aToFloat(intColor)).getCGColor());
//			//}
//				
////			context.selectFont(paint.font.getFontName(), paint.getFontSize(), CGTextEncoding.MacRoman);	
////			context.setTextDrawingMode(CGTextDrawingMode.Fill);
////			context.setFillColorWithColor(new UIColor(r, g, b, a).getCGColor());
////			context.showTextAtPoint(x, y, string);
//						
//			Foundation.log("IosCanvas.drawText in " + (System.currentTimeMillis() - time) + " millis, thread = " + Thread.currentThread().getName());
//		} catch (Throwable e) {
//			Utils.printThrowable(e);
//		}
//	}

	@Override
	public void drawBitmap(Bitmap bitmap, float x, float y) {
		Foundation.log("IosCanvas.drawBitmap"); 
	}
}
