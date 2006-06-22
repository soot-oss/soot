/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */





package soot;
import soot.*;

import soot.util.*;
import java.util.*;

/**
 *   A class that models Java's reference types. RefTypes are parametrized by a class name.
 *   Two RefType are equal iff they are parametrized by the same class name as a String.
 */

public class RefType extends RefLikeType implements Comparable
{
    public RefType( Singletons.Global g ) { className = ""; }
    public static RefType v() { return G.v().soot_RefType(); }

    /** the class name that parametrizes this RefType */
    private String className;
    public String getClassName() { return className; }
    private SootClass sootClass;
    private AnySubType anySubType;

    private RefType(String className)
    {
        if( className.startsWith("[") ) throw new RuntimeException("Attempt to create RefType whose name starts with [");
        if( className.indexOf("/") >= 0 ) throw new RuntimeException("Attempt to create RefType containing a /");
        if( className.indexOf(";") >= 0 ) throw new RuntimeException("Attempt to create RefType containing a ;");
        this.className = className;
    }

    /** 
     *  Create a RefType for a class. 
     *  @param className The name of the class used to parametrize the created RefType.
     *  @return a RefType for the given class name.
     */
    public static RefType v(String className)
    {
        RefType ret = Scene.v().getRefType( className );
        if( ret == null ) {
            ret = new RefType(className);
            Scene.v().addRefType( ret );
        }
        return ret;
    }

    public int compareTo(Object o) throws ClassCastException
    {
        RefType t = (RefType)o;
        return this.toString().compareTo(t.toString());
    }
        
    /** 
     *  Create a RefType for a class. 
     *  @param c A SootClass for which to create a RefType.
     *  @return a RefType for the given SootClass..
     */
    public static RefType v(SootClass c)
    {
        return v(c.getName());
    }
    
     /** 
      *  Get the SootClass object corresponding to this RefType.
      *  @return the corresponding SootClass
      */    
    public SootClass getSootClass()
    {
        if( sootClass == null ) {
            //System.out.println( "wrning: "+this+" has no sootclass" );
            sootClass = SootResolver.v().makeClassRef(className);
        }
        return sootClass;
    }

    public boolean hasSootClass() {
        return sootClass != null;
    }
    
    public void setClassName( String className )
    {
        this.className = className;
    }

     /** 
      *  Set the SootClass object corresponding to this RefType.
      *  @param sootClass The SootClass corresponding to this RefType.
      */    
    public void setSootClass( SootClass sootClass )
    {
        this.sootClass = sootClass;
    }

    /** 
     *  2 RefTypes are considered equal if they are parametrized by the same class name String.
     *  @param t an object to test for equality.
     *  @ return true if t is a RefType parametrized by the same name as this.
     */
    public boolean equals(Object t)
    {
        return ((t instanceof RefType) && className.equals(((RefType) t).className));
    }

    public String toString()
    {
        return className;
    }

    public int hashCode()
    {
        return className.hashCode();
    }

    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseRefType(this);
    }


    /** Returns the least common superclass of this type and other. */
    public Type merge(Type other, Scene cm)
    {
        if(other.equals(UnknownType.v()) || this.equals(other))
            return this;
        
        if(! (other instanceof RefType))
            throw new RuntimeException("illegal type merge: "
                                       + this + " and " + other);


        {
            // Return least common superclass
            
            SootClass thisClass = cm.getSootClass(((RefType) this).className);
            SootClass otherClass = cm.getSootClass(((RefType) other).className);
            SootClass javalangObject = cm.getSootClass("java.lang.Object");

            LinkedList thisHierarchy = new LinkedList();
            LinkedList otherHierarchy = new LinkedList();

            // Build thisHierarchy
            {
                SootClass SootClass = thisClass;

                for(;;)
                {
                    thisHierarchy.addFirst(SootClass);

                    if(SootClass == javalangObject)
                        break;

                    SootClass = SootClass.getSuperclass();
                }
            }

            // Build otherHierarchy
            {
                SootClass SootClass = otherClass;

                for(;;)
                {
                    otherHierarchy.addFirst(SootClass);

                    if(SootClass == javalangObject)
                        break;

                    SootClass = SootClass.getSuperclass();
                }
            }

            // Find least common superclass
            {
                SootClass commonClass = null;

                while(!otherHierarchy.isEmpty() && !thisHierarchy.isEmpty() &&
                    otherHierarchy.getFirst() == thisHierarchy.getFirst())
                {
                    commonClass = (SootClass) otherHierarchy.removeFirst();
                    thisHierarchy.removeFirst();
                }

                return RefType.v(commonClass.getName());
            }
        }
        
    }

    public Type getArrayElementType() {
	if( className.equals( "java.lang.Object" )
	    || className.equals( "java.io.Serializable" )
	    || className.equals( "java.lang.Cloneable" ) ) {
	    return RefType.v( "java.lang.Object" );
	}
	throw new RuntimeException( "Attempt to get array base type of a non-array" );

    }

    public AnySubType getAnySubType() { return anySubType; }
    public void setAnySubType( AnySubType anySubType ) {
        this.anySubType = anySubType;
    }
}
