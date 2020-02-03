package com.pubbycrawl.tools.checkstyle.api;

public class AbstractCheck {
	
	public final void log(String key, String value) {		
		System.out.println("The string value is " + value + "The string key is " + key);		
	}

}
