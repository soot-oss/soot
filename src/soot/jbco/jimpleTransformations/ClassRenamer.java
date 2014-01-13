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

package soot.jbco.jimpleTransformations;

import java.util.*;

import soot.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;
/**
 * @author Michael Batchelder 
 * 
 * Created on 26-Jan-2006 
 */
public class ClassRenamer extends SceneTransformer  implements IJbcoTransform {

  public void outputSummary() {}
  
  public static String dependancies[] = new String[] { "wjtp.jbco_cr" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "wjtp.jbco_cr";
  
  public String getName() {
    return name;
  }
  
  private static final char stringChars[][] = { {'S','5','$'},
      {'l','1','I'},
      {'_'}
  };
  
  public static HashMap<String, String> oldToNewClassNames = new HashMap<String, String>();
  public static HashMap<String, SootClass> newNameToClass = new HashMap<String, SootClass>();
  
  protected void internalTransform(String phaseName, Map options)
  {
    if (output)
      G.v().out.println("Transforming Class Names...");
    
    soot.jbco.util.BodyBuilder.retrieveAllBodies();
    soot.jbco.util.BodyBuilder.retrieveAllNames();
    
    Scene scene = G.v().soot_Scene();
    // iterate through application classes, rename classes with junk
    for (SootClass c : scene.getApplicationClasses())
    {
      if (scene.getMainClass() == c || oldToNewClassNames.containsValue(c.getName()) || 
          soot.jbco.Main.getWeight(phaseName, c.getName()) == 0) {
        continue;
      }
      
      String oldName = c.getName();
      String newName = oldToNewClassNames.get(oldName);
      if (newName == null)
      {
        newName = getNewName(getNamePrefix(oldName));
        oldToNewClassNames.put(oldName, newName);
      }
      
      c.setName(newName);
      RefType crt = RefType.v(newName);
      crt.setSootClass(c);
      c.setRefType(crt);
      c.setResolvingLevel(SootClass.BODIES);
      // will this fix dangling classes?
      //scene.addRefType(c.getType());
      
      newNameToClass.put(newName,c);
      
      if (output) 
        out.println("\tRenaming "+oldName+ " to "+newName);
    }
    
    scene.releaseActiveHierarchy();
    scene.getActiveHierarchy();
    scene.setFastHierarchy(new FastHierarchy());
    
    if (output)
      out.println("\r\tUpdating bytecode class references");
    
    for (SootClass c : scene.getApplicationClasses())
    {
      for (SootMethod m : c.getMethods())
      {
        if (!m.isConcrete()) continue;
        
        if (output)
          out.println("\t\t"+m.getSignature());
        Body aBody = null;
        try {
          aBody = m.getActiveBody();
        } catch (Exception exc) {
          continue;
        }
        for (Unit u : aBody.getUnits())
        {
         Iterator<ValueBox> udbIt = u.getUseAndDefBoxes().iterator();
          while (udbIt.hasNext())
          {
            Value v = udbIt.next().getValue();
            if (v instanceof soot.jimple.Ref)
            {
              if (v.getType() instanceof soot.RefType)
              {
                RefType rt = (RefType)v.getType();
                
                if (!rt.getSootClass().isLibraryClass() && oldToNewClassNames.containsKey(rt.getClassName()))
                {
                  rt.setSootClass(newNameToClass.get(oldToNewClassNames.get(rt.getClassName())));
                  rt.setClassName(oldToNewClassNames.get(rt.getClassName()));
                }
              }
              else if (v.getType() instanceof ArrayType)
              {
                ArrayType at = (ArrayType)v.getType();
                if (at.baseType instanceof RefType)
                {
                  RefType rt = (RefType)at.baseType;
                  if (!rt.getSootClass().isLibraryClass() && oldToNewClassNames.containsKey(rt.getClassName()))
                    rt.setSootClass(newNameToClass.get(oldToNewClassNames.get(rt.getClassName())));
                }
              }
            }
          }
        }
      }
    }
    
    scene.releaseActiveHierarchy();
    scene.getActiveHierarchy();
    scene.setFastHierarchy(new FastHierarchy());
  } 
  
  /*
   * @return	String	newly generated junk name that DOES NOT exist yet
   */
  public static String getNewName(String prefix) 
  {
    int size = 5;
    int tries = 0;
    int index = Rand.getInt(stringChars.length);
    int length = stringChars[index].length;
    
    String result = null;
    char cNewName[] = new char[size];
    do {
      if (tries == size)
      {
        cNewName = new char[++size];
        tries = 0;
      }
      
      do {
        cNewName[0] = stringChars[index][Rand.getInt(length)];
	  } while (!Character.isJavaIdentifierStart(cNewName[0]));
	      
	  // generate random string
	  for (int i = 1; i < cNewName.length; i++){
	    int rand = Rand.getInt(length);
	    cNewName[i] = stringChars[index][rand];      
	  }
      result = prefix + String.copyValueOf(cNewName);
      tries++;
    } while (oldToNewClassNames.containsValue(result) || BodyBuilder.nameList.contains(result));
    
    BodyBuilder.nameList.add(result);
    
    return result;
  }
  
  public static String getNamePrefix(String fullName) 
  {
    int idx = fullName.lastIndexOf('.');
    if (idx >= 0)
      return fullName.substring(0,idx + 1);
    else 
      return "";
  }
}
