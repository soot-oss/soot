/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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

import soot.jimple.*;
import java.util.*;

public class SootField extends AbstractHost implements ClassMember, EquivTo
{
    String name;
    Type type;
    int modifiers;

    boolean isDeclared = false;
    SootClass declaringClass;
    boolean isPhantom = false;
    
    public SootField(String name, Type type, int modifiers)
    {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    public SootField(String name, Type type)
    {
        this.name = name;
        this.type = type;
        this.modifiers = 0;
    }

    public boolean equivTo(Object o)
    {
        if (o instanceof SootField)
        {
            SootField sf = (SootField)o;
            return type.equals(sf.type) && modifiers == sf.modifiers &&
                name.equals(sf.name);
        }
        return false;
    }

    public int equivHashCode()
    {
        return type.hashCode() * 101 + modifiers * 17 + name.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public String getSignature()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<" + getDeclaringClass().getName() + ": ");
        buffer.append(getType() + " " + getName() + ">");

        return buffer.toString();

    }
  
    public String getJimpleStyleSignature()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<" + getDeclaringClass().getName() + ": ");
	Type t = getType();

	if(Jimple.isJavaKeywordType(t))
	  buffer.append("." + t  + " " + getName() + ">");
	else
	  buffer.append(getType() + " " + getName() + ">");

        return buffer.toString();

    }
    
    public String getSubSignature()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getType() + " " + getName());
        return buffer.toString();
    }
    
    public SootClass getDeclaringClass() throws NotDeclaredException
    {
        if(!isDeclared)
            throw new NotDeclaredException();

        return declaringClass;
    }

    public boolean isPhantom()
    {
        return isPhantom;
    }
    
    public void setPhantom(boolean value)
    {
        isPhantom = value;
    }

    public boolean isDeclared()
    {
        return isDeclared;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type t)
    {
        this.type = t;
    }

    public boolean isPublic()
    {
        return Modifier.isPublic(this.getModifiers());
    }

    public boolean isProtected()
    {
        return Modifier.isProtected(this.getModifiers());
    }

    public boolean isPrivate()
    {
        return Modifier.isPrivate(this.getModifiers());
    }

    public void setModifiers(int modifiers)
    {
        if (!declaringClass.isApplicationClass())
            throw new RuntimeException("Cannot set modifiers of a field from a non-app class!");
            
        this.modifiers = modifiers;
    }

    public int getModifiers()
    {
        return modifiers;
    }

    public String toString()
    {
        return getSignature();
    }


  private String getJimpleStyleDeclaration()
  {
    StringBuffer buf = new StringBuffer();
    StringTokenizer st = new StringTokenizer(Modifier.toString(modifiers));
    while(st.hasMoreTokens()){
      buf.append("." + st.nextToken()+ " ");      
    }
    if(Jimple.isJavaKeywordType(type))
      buf.append(".");

    buf.append(type);

    buf.append(" " + name);
    return buf.toString();
  }



  private String getOriginalStyleDeclaration()
  {
         String qualifiers = Modifier.toString(modifiers) + " " + type.toString();
        qualifiers = qualifiers.trim();

        if(qualifiers.equals(""))
            return name;
        else
            return qualifiers + " " + name + "";

  }


  public String getDeclaration()
    {
      int outputMode = Scene.v().getOutputMode();
      if(outputMode == Scene.v().OUTPUT_JIMPLE)
	return getJimpleStyleDeclaration();
      else
	return getOriginalStyleDeclaration();
    }


    public String getXML() 
    {
	return XMLManager.getXML(this);
    }
}






