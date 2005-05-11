/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot;

import soot.tagkit.*;
import soot.util.*;
import soot.xml.*;
import java.util.*;
import java.io.*;

public class XMLAttributesPrinter {

	
	private String inFilename;
	private String useFilename;
	private SootClass sootClass;
	private String outputDir;
    private ArrayList attributes;
    
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
        attributes = new ArrayList();
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
    
	public void printAttrs(SootClass c, soot.xml.TagCollector tc) {
	
        tc.collectKeyTags(c);
        Iterator fIt = c.getFields().iterator();
        while (fIt.hasNext()){
            SootField sf = (SootField)fIt.next();
            tc.collectFieldTags(sf);
        }
        Iterator mIt = c.getMethods().iterator();
        while (mIt.hasNext()){
            SootMethod sm = (SootMethod)mIt.next();
            tc.collectMethodTags(sm);
        }
        tc.printTags(writerOut);
        tc.printKeys(writerOut);
		finishFile();
	}
    
	public void printAttrs(SootClass c) {
	
        soot.xml.TagCollector tc = new soot.xml.TagCollector();
        tc.collectKeyTags(c);
        tc.collectTags(c);
        tc.printTags(writerOut);
        tc.printKeys(writerOut);
		finishFile();
	}

	 
	FileOutputStream streamOut = null;
	PrintWriter writerOut = null;
	
	private int getJavaLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			//G.v().out.println(t.getClass().toString());
			if (t instanceof SourceLnPosTag) {
				//G.v().out.println("t is LineNumberTag");
				return ((SourceLnPosTag)t).startLn();
			}
            else if (t instanceof LineNumberTag){
                return (new Integer(((LineNumberTag)t).toString())).intValue();
            }
		}
		return 0;
	}
	
	private int getJimpleLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			if (t instanceof JimpleLineNumberTag) {
				return ((JimpleLineNumberTag)t).getStartLineNumber();
			}
		}
		return 0;
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
		in = StringTools.replaceAll(in, "<", "&lt;");
		in = StringTools.replaceAll(in, ">", "&gt;");
		in = StringTools.replaceAll(in, "&", "&amp;");
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
