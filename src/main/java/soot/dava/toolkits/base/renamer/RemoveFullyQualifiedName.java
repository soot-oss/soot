package soot.dava.toolkits.base.renamer;

import java.util.Iterator;

import soot.ArrayType;
import soot.Type;
import soot.util.IterableSet;

public class RemoveFullyQualifiedName {

	public static boolean containsMultiple(Iterator it, String qualifiedName,Type t){
		/*
		 * The fully qualified name might contain [] in the end if the type is an ArrayType
		 */		
		if(t != null){
			if(t instanceof ArrayType){
				if(qualifiedName.indexOf('[')>=0)
					qualifiedName = qualifiedName.substring(0,qualifiedName.indexOf('['));

			}
		}
		//get last name
		String className = getClassName(qualifiedName);
		
		int count=0;
		while(it.hasNext()){
			String tempName = getClassName( (String)it.next());
			if(tempName.equals(className))
				count++;
		}
		if(count>1)
			return true;
		return false;
	}
	
	/*
	 * Method finds the last . and returns the className after that
	 * if no dot is found (shouldnt happen) then the name is simply returned back
	 */
	public static String getClassName(String qualifiedName){
		if(qualifiedName.lastIndexOf('.')>-1)
			return qualifiedName.substring(qualifiedName.lastIndexOf('.')+1);
		
		return qualifiedName;
	}
	
	public static String getReducedName(IterableSet importList, String qualifiedName,Type t){
		//if two explicit imports dont import the same class we can remove explicit qualification
		if(!containsMultiple(importList.iterator(),qualifiedName,t))
			return getClassName(qualifiedName);
		return qualifiedName;
	}
}
