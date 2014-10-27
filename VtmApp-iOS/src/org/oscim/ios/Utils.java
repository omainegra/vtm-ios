package org.oscim.ios;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;

import org.robovm.apple.foundation.Foundation;

public class Utils {

	public static void printThrowable(Throwable e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		
		try {
			sw.close();
		} catch (IOException e1) {}
		
		Foundation.log(sw.toString());
	}
	
	public static byte [] toByteArray(InputStream in) throws IOException{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte [] buff = new byte[8192];
		
		while (in.read(buff) > 0){
			out.write(buff);
		}
		
		out.close();
		return out.toByteArray();
	}
	
	public static void startStackTracesWatcher(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true){
						Thread.sleep(10000);
						
						java.util.Map<Thread, StackTraceElement[]> stacks = Thread.getAllStackTraces();
						
						for (Thread thread: stacks.keySet()){
							StackTraceElement[] stack = stacks.get(thread);
							if (stack != null){
								String s = "\n[StackTrace of " + thread.getName() + "]\n";
								
								for (StackTraceElement elem: stack){
									s += "    " + elem.toString() + "\n";
								}
								
								Foundation.log(s);
							}
						}
					}
				} catch (Throwable e) {
					Utils.printThrowable(e);
				}
			}
		}, "Stacktraces Watcher").start();
	}
	
	public static void startStackTracesWatcher(final String threadName){
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true){
						Thread.sleep(10000);
						
						ThreadGroup group = Thread.currentThread().getThreadGroup();
						while (group.getParent() != null) group = group.getParent();
						
						if (group != null){
							Thread [] threads = new Thread[1024];
									
							group.enumerate(threads);
							
							for (Thread thread: threads){
								if (thread != null && thread.getName().equalsIgnoreCase(threadName)){
									String s = "\n[StackTrace of " + thread.getName() + "]\n";
									
									for (StackTraceElement elem: thread.getStackTrace()){
										s += "    " + elem.toString() + "\n";
									}
									
									Foundation.log(s);
									break;
								}
							}
						}
					}
				} catch (Throwable e) {
					Utils.printThrowable(e);
				}
			}
		}, "Stacktraces Watcher").start();
	}
}
