package com.pubbycrawl.tools.checkstyle.checks.metrics;

import javax.print.attribute.standard.Finishings;

import com.pubbycrawl.tools.checkstyle.api.AbstractCheck;

public class JavaNCSSCheck extends AbstractCheck {
	
	public static void main(String[] args) {
		JavaNCSSCheck mainClass = new JavaNCSSCheck();
		mainClass.finishTree();
	}
	
	public void finishTree() {
		//logging the values here
		log("1234", "Test");
	}

}
