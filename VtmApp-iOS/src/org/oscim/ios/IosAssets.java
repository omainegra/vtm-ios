package org.oscim.ios;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.oscim.backend.AssetAdapter;
import org.oscim.gdx.GdxAssets;
import org.robovm.apple.foundation.Foundation;
import org.robovm.apple.foundation.NSBundle;

public class IosAssets extends AssetAdapter{

	private String path = "";

	public IosAssets(String path){
		
		this.path = NSBundle.getMainBundle().getBundlePath() + "/" + path;
		Foundation.log("IosAssets.init: path =  " + this.path);
	}
	
	@Override
	protected InputStream openFileAsStream(String file) {
		try {
			Foundation.log("IosAssets.openFileAsStream: path =  " + path + "" + file);
			return new FileInputStream(new File(path + "" + file));
		} catch (Throwable e) {
			Utils.printThrowable(e);
			return null;
		}
	}

	public static void init(String path) {
		AssetAdapter.init(new IosAssets(path));
	}
	
	public static void init() {
		AssetAdapter.init(new IosAssets(""));
	}
}
