/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrice Pominville
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

//  import org.xml.sax.*;
//  import org.xml.sax.helpers.*;
import soot.*;
import java.util.*;
import java.io.*;
import soot.coffi.*;


public class XMLParser {
    // XML disabled temporarily.
//      private static final String parserClass = "com.jclark.xml.sax.Driver";
//      private Parser parser;
//      private Scene cm = Scene.v();
//      boolean cachePhantomRefs;
//      boolean cacheLazyResolving;

//      private SootClass currentClass = null;
    
//      public XMLParser() throws Exception
//      {
//  	Scene.v().setPhantomRefs(true);
//  	parser = ParserFactory.makeParser(parserClass);
//      }

//      public static void main(String[] args) throws Exception
//      {
//  	XMLParser p = new XMLParser();
//  	p.parseJimple(args[0]);

//      }

//      public SootClass parseJimple(InputStream iStream) throws Exception
//      {
//  	InputSource iSource = new InputSource(iStream);
//  	parser.setDocumentHandler(new JimpleHandler());
	
//  	setXMLParsingEnv();
	

//  	try{
//  	    parser.parse(iSource);
//  	} catch (SAXParseException e) { 
//  	    System.err.println(e.getMessage()+" Id: " + e.getSystemId() +  " "+ e.getLineNumber() + ":" + e.getColumnNumber());
//  	}
		

//  	restoreEnv();
//  	return currentClass;
//      }


//      /* a call to setXMLParsingEnv should be followed by a call to restoreEnv */

//      private  void setXMLParsingEnv() 
//      {
//  	cachePhantomRefs = cm.allowsPhantomRefs();
//  	cacheLazyResolving = cm.allowsLazyResolving();
//  	cm.setPhantomRefs(true);
//  	cm.setLazyResolving(false);
//      }    
//      private  void restoreEnv() 
//      {
//  	cm.setPhantomRefs(cachePhantomRefs);
//  	cm.setLazyResolving(cacheLazyResolving);
//      }

    
//      public SootClass parseJimple(String file) throws Exception
//      {
//  	parser.setDocumentHandler(new JimpleHandler());
	
//  	setXMLParsingEnv();
//  	if(Main.isVerbose) 
//  	    System.out.print("[xml] Parsing XML file: " + file + "... ");

//  	try{
//  	    parser.parse(file);
//  	} catch (SAXParseException e) { 
//  	    System.out.println(e.getMessage()+" Id: " + e.getSystemId() +  " "+ e.getLineNumber() + ":" + e.getColumnNumber());
//  	}
//  	if(Main.isVerbose)
//  	    System.out.println("[xml] Parsing: Done.");

//  	restoreEnv();
//  	return currentClass;
//      }



//      class JimpleHandler extends HandlerBase {
	


//  	int classCount = 0, fieldCount = 0, methodCnt = 0;	
//  	SootMethod currentMethod;
//  	SootClass currentClass;
//  	List parameterTypes;

//  	public void startElement (String name, AttributeList atts)
//  	{
//  	    if(name.equals("class")) {
	    
//  		SootClass superClass;
		
//  		String className = atts.getValue(0);
//  		int modifiers = Integer.parseInt(atts.getValue(1));
//  		String superClassName = atts.getValue(2);


//  		currentClass = Scene.v().getSootClass(className); 
//  		superClass = Scene.v().getSootClass(superClassName);
		
//  		currentClass.setModifiers(modifiers);
//  		currentClass.setSuperclass(superClass);
//  		currentClass.setPhantom(false);
//  		currentClass.setApplicationClass(); // xxx is this correct??
//  		classCount++;
		
//  	    } else if(name.equals("field")) {
//  		String fieldName = atts.getValue(0);
//  		Type fieldType = getType(atts.getValue(1));
//  		int modifiers = Integer.parseInt(atts.getValue(2));
		
//  		SootField field = new SootField(fieldName, fieldType, modifiers);
//  		currentClass.addField(field);
		
//  		fieldCount++;
//  	    } else if(name.equals("method")) {
//  		// syntax: <method name="soot.Timer" returnType="void" modifiers="1">parameters* </method>
//  		String methodName = atts.getValue(0);
//  		Type returnType = getType(atts.getValue(1));
//  		int modifiers = Integer.parseInt(atts.getValue(2));
		
//  		parameterTypes = new ArrayList();
		
//  		currentMethod = new SootMethod(methodName,
//  					       parameterTypes, 
//  					       returnType,
//  					       modifiers);
//  		parameterTypes = currentMethod.getParameterTypes();
//  // xxx 		currentMethod.setSource(new JimpleMethodSource());

//  		methodCnt++;

//  		//xxx  exceptions ??
//  	    } else if(name.equals("parameter")) {	    
//  		parameterTypes.add(getType(atts.getValue(0)));
//  	    } else if(name.equals("interface")) {
//  		currentClass.addInterface(cm.getSootClass(atts.getValue(0)));
//  	    }
//  	}
    
//  	public void endElement (String name)
//  	{
//  	    if(name.equals("method")) {
//  		currentClass.addMethod(currentMethod);
//  	    } 
//  	}

//  	public void endDocument() 
//  	{
//  	    if(Main.isVerbose) {
//  		System.out.println("\n[xml] Parsed " + classCount + " classes.");
//  		System.out.println("[xml] Parsed " + fieldCount + " fields.");
//  		System.out.println("[xml] Parsed " + methodCnt + " methods.");
//  	    }	
//  	}


       
//      }    

//      private Type getType(String aFieldDescriptor) 
//      {
//  	return Util.jimpleTypeOfFieldDescriptor(Scene.v(), aFieldDescriptor);
//      }

//      /*
//      private SootClass getResolvedClass(String aClassName) 
//      {
//  	SootClass c;
//  	if((c = Scene.v().getSootClass(aClassName)) == null) {
//  	    c = new SootClass(aClassName);
//  	    Scene.v().addClass(c);		    		    
//  	}
//  	return c;
//      }
      
//      */  
}



