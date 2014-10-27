package org.oscim.ios.canvas;

import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.ios.Utils;
import org.robovm.apple.coregraphics.CGColor;
import org.robovm.apple.coregraphics.CGColorSpace;
import org.robovm.apple.coregraphics.CGLineCap;
import org.robovm.apple.coretext.CTFont;
import org.robovm.apple.foundation.NSString;
import org.robovm.apple.uikit.NSStringExtensions;
import org.robovm.apple.uikit.UIFont;
import org.robovm.apple.uikit.UIScreen;

public class IosPaint implements Paint {
		
	private float fontSize = 16;
	
	UIFont font;
	CTFont ctFont;
	FontStyle fontStyle;
	CGLineCap cap = CGLineCap.Round;
	
	int color;
	CGColor cgColor;
	
	float strokeWidth = 1;

	private Style style = Style.FILL;
	
	@Override
	public void setColor(int color) {
		this.color = color;
		
		CGColorSpace colorSpace = CGColorSpace.createDeviceRGB();
		cgColor = CGColor.create(colorSpace, new float[]{Color.rToFloat(color), Color.gToFloat(color), Color.bToFloat(color), Color.aToFloat(color)});
		colorSpace.release();
	}
	
	@Override
	public int getColor() {
		return color;
	}

	public CGColor getCGColor(){
		return cgColor;
	}
	
	public CTFont getCTFont(){
		return ctFont;
	}
	
	public CGLineCap getCGLineCap(){
		return cap;
	}

	public float getStrokeWidth(){
		return strokeWidth;
	}
	
	public Style getStyle(){
		return style;
	}
	
	public float getFontSize(){
		return fontSize;
	}
	
	@Override
	public void setStrokeCap(Cap cap) {
		switch (cap) {
			case BUTT:
				this.cap = CGLineCap.Butt;
				break;
			case ROUND:
				this.cap = CGLineCap.Round;
				break;
			case SQUARE:
				this.cap = CGLineCap.Square;
				break;
			default:
				break;
		}
	}

	@Override
	public void setStrokeWidth(float width) {
		strokeWidth = (float)( width/UIScreen.getMainScreen().getScale());
	}
	
	@Override
	public void setStyle(Style style) {
		this.style = style;
	}
	
	@Override
	public void setTextAlign(Align align) {
	}

	public UIFont getFont(){
		return font;
	}
	
	@Override
	public void setTextSize(float textSize) {
		fontSize = (float)(textSize/UIScreen.getMainScreen().getScale())*1.25f;
		try {
			switch (fontStyle) {
				case NORMAL:
					font = UIFont.getFont("ArialMT", textSize);
					break;
				case ITALIC:
					font = UIFont.getFont("Arial-ItalicMT", textSize);
					break;
				case BOLD:
					font = UIFont.getFont("Arial-BoldMT", textSize);
					break;
				case BOLD_ITALIC:
					font = UIFont.getFont("Arial-BoldItalicMT", textSize);
					break;
			}
			ctFont = CTFont.create(font.getFontName(), fontSize, null);
		} catch (Throwable e) {
			Utils.printThrowable(e);
		}
	}

	@Override
	public void setTypeface(FontFamily fontFamily, FontStyle fontStyle) {
		this.fontStyle = fontStyle;
	}

	@Override
	@SuppressWarnings("deprecation")
	public float measureText(String text) {
		NSString str = new NSString(text);
		float width = (float) NSStringExtensions.getSize(str, font).width();
		return width;
	}

	@Override
	public float getFontHeight() {
		float fontHeight = (float) font.getLineHeight();
		return fontHeight;
	}

	@Override
	public float getFontDescent() {
		float descender = -(float) font.getDescender();
		return descender;
	}

}
