package soot;

import soot.tagkit.*;
import java.util.*;
import java.io.*;

public class XMLAttributesPrinter {

	private String inFilename;
	private String useFilename;
	private SootClass sootClass;
	
	public XMLAttributesPrinter(String filename) {
		setInFilename(filename);
		createUseFilename();
		initFile();
	}

	private void initFile() {
		try {
		  streamOut = new FileOutputStream(getUseFilename());
		  writerOut = new PrintWriter(new OutputStreamWriter(streamOut));
		  writerOut.println("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		  writerOut.println("<attributes>");
		}
		catch(IOException e1) {
		  System.out.println(e1.getMessage());
		}
									
	}

	private void finishFile() {
		  writerOut.println("</attributes>");
		  writerOut.close();
	}
	public void printAttrs(SootClass c) {
		
		
		Iterator it = c.getMethods().iterator();
		while (it.hasNext()) {
			SootMethod sm = (SootMethod)it.next();
			Body b = sm.getActiveBody();
			Iterator itUnits = b.getUnits().iterator();
			while (itUnits.hasNext()) {
				int jimple_ln = -1;
				int java_ln = -1;
				Vector attrs = new Vector();
				Unit u = (Unit)itUnits.next();
				Iterator itTags = u.getTags().iterator();
				while (itTags.hasNext()) {
			        	Tag t = (Tag)itTags.next();
					if (t instanceof LineNumberTag) {
					  java_ln = (new Integer(((LineNumberTag)t).toString())).intValue();
					}
					else if (t instanceof JimpleLineNumberTag) {
					  jimple_ln = (new Integer(((JimpleLineNumberTag)t).toString())).intValue();
					}
					else if (t instanceof StringTag) {
					  attrs.add(((StringTag)t).toString());
					}
				}
				printAttribute(java_ln, jimple_ln, attrs);
				
			}
		}
		finishFile();
	}

	FileOutputStream streamOut = null;
	PrintWriter writerOut = null;

	private void printAttribute(int java_ln, int jimple_ln, Vector attrs) {

		Iterator it = attrs.iterator();
		while(it.hasNext()) {
			  writerOut.println("<attribute>");
			  writerOut.println("<java_ln>"+java_ln+"</java_ln>");
			  writerOut.println("<jimple_ln>"+jimple_ln+"</jimple_ln>");
			  writerOut.println("<text>"+(String)it.next()+"</text>");
			  writerOut.println("</attribute>");
		}
	}

	private void createUseFilename() {
		String tmp = getInFilename();
		tmp = tmp.substring(0, tmp.lastIndexOf('.'));
		tmp = "attributes"+"/"+tmp+".xml";
		setUseFilename(tmp);
	}

	private void setInFilename(String file) {
		useFilename = file;
	}

	private String getInFilename() {
		return useFilename;
	}

	private void setUseFilename(String file) {
		useFilename = file;
	}

	private String getUseFilename() {
		return useFilename;
	}
		
	
}
