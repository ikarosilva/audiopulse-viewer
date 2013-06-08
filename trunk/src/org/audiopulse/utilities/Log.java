package org.audiopulse.utilities;

public class Log {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public static void v(String tag, String msg){
		System.out.println(tag + ":\t" + msg);
	}
	
	public static void w(String tag, String msg){
		System.out.println(tag + ":\t" + msg);
	}
	
	public static void e(String tag, String msg){
		System.err.println(tag + ":\t" + msg);
	}
}
