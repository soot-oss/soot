package soot;

import soot.tagkit.*;
import java.util.*;
import java.io.*;

public class XMLAttributesPrinter {

	private String inFilename;
	private String useFilename;
	private SootClass sootClass;
	private String outputDir;

	private void setOutputDir(String dir) {
		outputDir = dir;
	}

	private String getOutputDir() {
		return outputDir;
	}
	
	public XMLAttributesPrinter(String filename, String outputDir) {
		setInFilename(filename);
		setOutputDir(outputDir);
		initAttributesDir();
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
		  G.v().out.println(e1.getMessage());
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
			if (!sm.hasActiveBody()) {
				continue;
			}
			Iterator mTags = sm.getTags().iterator();
			int jimple_ln = -1;
			int java_ln = -1;				
			Vector attrs = new Vector();
			while (mTags.hasNext()){
				Tag t = (Tag)mTags.next();
				if (t instanceof LineNumberTag) {
					java_ln = (new Integer(((LineNumberTag)t).toString())).intValue();
				}
				else if (t instanceof JimpleLineNumberTag) {
					  jimple_ln = (new Integer(((JimpleLineNumberTag)t).toString())).intValue();
				}
				else if (t instanceof StringTag) {
					  
					  attrs.add(formatForXML(((StringTag)t).toString()));
				}
				else {
					if (!t.toString().equals("[Unknown]")){
				    	attrs.add(t.toString());
				  	}
					
				}
				
			}

			printAttribute(java_ln, jimple_ln, attrs);
			
			Body b = sm.getActiveBody();
			Iterator itUnits = b.getUnits().iterator();
			while (itUnits.hasNext()) {
				jimple_ln = -1;
				java_ln = -1;
				attrs = new Vector();
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
					  
					  attrs.add(formatForXML(((StringTag)t).toString()));
					}
					else {
					  if (!t.toString().equals("[Unknown]")){
					    attrs.add(t.toString());
					  }
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

	private void initAttributesDir() {
	
		StringBuffer sb = new StringBuffer();
		String attrDir = "attributes";
		
		sb.append(getOutputDir());
		sb.append(System.getProperty("file.separator"));
		sb.append(attrDir);
		
		File dir = new File(sb.toString());

		if (!dir.exists()) {
			try {
				dir.mkdirs();
			} 
			catch (SecurityException se) {
			        G.v().out.println("Unable to create " + attrDir);
		                //System.exit(0);
		        }
		}
				
	}

	private String  formatForXML(String in) {
		in = in.replaceAll("<", "&lt;");
		in = in.replaceAll(">", "&gt;");
		return in;
	}

	private void createUseFilename() {
		String tmp = getInFilename();
		//G.v().out.println("attribute file name: "+tmp);
		tmp = tmp.substring(0, tmp.lastIndexOf('.'));
		int slash = tmp.lastIndexOf(System.getProperty("file.separator"));
		if (slash != -1) {
			tmp = tmp.substring((slash+1), tmp.length()); 
		}
	
		StringBuffer sb = new StringBuffer();
		String attrDir = "attributes";
		sb.append(getOutputDir());
		sb.append(System.getProperty("file.separator"));
		sb.append(attrDir);
		sb.append(System.getProperty("file.separator"));  
		sb.append(tmp);
		sb.append(".xml");
		//tmp = sb.toString()+tmp+".xml";
		setUseFilename(sb.toString());
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
