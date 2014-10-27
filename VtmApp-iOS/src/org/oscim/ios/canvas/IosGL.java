package org.oscim.ios.canvas;

import org.oscim.backend.GL20;
import org.oscim.backend.GLAdapter;

import com.badlogic.gdx.backends.iosrobovm.IOSGLES20;


public class IosGL extends IOSGLES20 implements GL20{

	public static void init(){
		GLAdapter.init(new IosGL());
		
	}
}
