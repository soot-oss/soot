package soot;

import java.util.*;
import java.io.*;

public class TagManager 
{

   
    public static void printReportFor(PrintWriter aOut) 
    {
	//	Set set = new HashSet();
	//set.addAll(aTagNames);
	
       	printContentsOfHost("<<scene>>", "", Scene.v(), aOut);
	
	Iterator it = Scene.v().getApplicationClasses().iterator();
	while(it.hasNext()) {
	    SootClass cl = (SootClass) it.next();
	    
	    

	    printContentsOfHost("<" + cl.getName() +">", "    ", cl, aOut);
	    Iterator methodIt = cl.getMethods().iterator();
	    while(methodIt.hasNext()) {
		SootMethod mtd = (SootMethod) methodIt.next();				
		//printContentsOfHost(mtd.toString(), "        ", mtd, aOut);
	    }
	    
	    

	}
	
    }




    public static void printContentsOfHost(String aSignature, String aIndent, Host aHost, PrintWriter aOut)
    {
	aOut.println(aIndent + aSignature);
	
	Iterator it = aHost.getTags().iterator();
	while(it.hasNext()) {
	    aOut.println(aIndent + "    " + it.next());
	}

	aOut.println("");
    }


    public static void sumTagsUpMethods(String aTagName, SootClass aClass)
    {
	long sum = 0;
	aClass.destroyTag(aTagName);
	
	Iterator it = aClass.getMethods().iterator();
	while(it.hasNext()) {
	    SootMethod method = (SootMethod) it.next();
	    sum += ((Long) method.getTag(aTagName).getValue()).longValue();	    
	}
	
	aClass.newTag(aTagName, new Long(sum));	
    }

    public static void sumTagsUp(String aTagName, Scene aScene)
    {
	long sum = 0;
	aScene.destroyTag(aTagName);

	
	Iterator it = aScene.getApplicationClasses().iterator();
	while(it.hasNext()) {
	    SootClass c =  (SootClass) it.next();
	    TagManager.sumTagsUpMethods(aTagName, c);
	    sum += ((Long)c.getTag(aTagName).getValue()).longValue();
	}
	
	aScene.newTag(aTagName, new Long(sum));	
    }




}


    
