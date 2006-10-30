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
import java.util.regex.Pattern;

import soot.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;
import soot.jimple.*;

/**
 * @author Michael Batchelder 
 * 
 * Created on 26-Jan-2006 
 */
public class FieldRenamer extends SceneTransformer implements IJbcoTransform {

  public void outputSummary() {}
  
  public static String dependancies[] = new String[] { "wjtp.jbco_fr" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "wjtp.jbco_fr";
  
  public String getName() {
    return name;
  }
  
  private static final char stringChars[][] = { {'S','5','$'},
		{'l','1','I'},
		{'_'}
	  };
  
  public static Vector  namesToNotRename = new Vector();
  public static Hashtable oldToNewFieldNames = new Hashtable();
  public static Hashtable opaquePreds1ByClass = new Hashtable();
  public static Hashtable opaquePreds2ByClass = new Hashtable();
  public static ArrayList sootFieldsRenamed = new ArrayList();
  public static SootField opaquePairs[][] = null;
  public static int	  handedOutPairs[] = null;
  public static int	  handedOutRunPairs[] = null;
  public static boolean rename_fields = false;
  
  RefType boolRef;
  
  protected void internalTransform(String phaseName, Map options)
  {
    Scene scene = G.v().soot_Scene();
    //Hierarchy hierarchy = scene.getActiveHierarchy();
    boolRef = scene.getRefType("java.lang.Boolean");
    
    if (output) {
      if (rename_fields)
        out.println("Transforming Field Names and Adding Opaque Predicates...");
      else
        out.println("Adding Opaques...");
    }
    
    soot.jbco.util.BodyBuilder.retrieveAllBodies();
    soot.jbco.util.BodyBuilder.retrieveAllNames();
    
    Iterator it = scene.getApplicationClasses().iterator();
    while (it.hasNext())
    {
      SootClass c = (SootClass)it.next();
      String cName = c.getName();
      if (cName.indexOf(".") >= 0)
        cName = cName.substring(cName.lastIndexOf(".") + 1, cName.length());
      oldToNewFieldNames.put(cName,cName);
      
      if (rename_fields) {
        if (output) out.println("\tClassName: "+cName);
	    // rename all the fields in the class
	    Iterator fIt = c.getFields().iterator();
	    while (fIt.hasNext()) {
          SootField f = (SootField)fIt.next();
          int weight = soot.jbco.Main.getWeight(phaseName, f.getName());
          if (weight > 0)
            renameField(cName,f);
        }
      }
      
      // skip interfaces - they can only hold final fields
      if (c.isInterface()) continue;
      
      // add one opaq predicate for true and one for false to each class 
      String bool = "opPred1";
      Type t = Rand.getInt() % 2 == 0 ? (Type)BooleanType.v() : (Type)boolRef;
      while (oldToNewFieldNames.containsKey(bool))
        bool += "_";
      SootField f = new SootField(bool, t, Modifier.PUBLIC | Modifier.STATIC);
      renameField(cName,f);
      opaquePreds1ByClass.put(c,f);
      c.addField(f);
      
      setBooleanTo(c,f,true);
      
      bool = "opPred2";
      t = t == BooleanType.v() ? (Type)boolRef : (Type)BooleanType.v();
      while (oldToNewFieldNames.containsKey(bool))
        bool += "_";
      f = new SootField(bool, t, Modifier.PUBLIC | Modifier.STATIC);
      renameField(cName,f);
      opaquePreds2ByClass.put(c,f);
      c.addField(f);
      
      if (t == boolRef)
        setBooleanTo(c,f,false);
    }
    
    buildOpaquePairings();
    
    if (!rename_fields)
      return;
    
    if (output) 
      out.println("\r\tUpdating field references in bytecode");
    
    it = scene.getApplicationClasses().iterator();
    while (it.hasNext())
    {
      SootClass c = (SootClass)it.next();
      Iterator mIt = c.getMethods().iterator();
      while (mIt.hasNext())
      {
        SootMethod m = (SootMethod)mIt.next();
        if (!m.isConcrete()) continue;
        
        if (!m.hasActiveBody())
          m.retrieveActiveBody();
        
        Iterator uIt = m.getActiveBody().getUnits().iterator();
        while (uIt.hasNext())
        {
          Iterator udbIt = ((Unit)uIt.next()).getUseAndDefBoxes().iterator();
          while (udbIt.hasNext())
          {
            Value v = ((ValueBox)udbIt.next()).getValue();
            if (v instanceof FieldRef)
            {
              FieldRef fr = (FieldRef)v;
              SootFieldRef sfr = fr.getFieldRef();
              if (sfr.declaringClass().isLibraryClass())
                continue;
              
              String oldName = sfr.name();
              String fullName = sfr.declaringClass().getName() + '.' + oldName;
              String newName = (String)oldToNewFieldNames.get(oldName);
              if (newName == null || namesToNotRename.contains(fullName)) 
                continue;
              
              if (newName.equals(oldName)) {
                System.out.println("Strange.. Should not find a field with the same old and new name.");
              }
              sfr = scene.makeFieldRef(sfr.declaringClass(), newName, sfr.type(), sfr.isStatic());
              fr.setFieldRef(sfr);
              try {
                sfr.resolve();
              } catch (Exception exc)
              {
                System.out.println("********ERROR Updating "+sfr.name()+" to "+newName);
                System.out.println("Fields of "+sfr.declaringClass().getName() + ": "+sfr.declaringClass().getFields());
                //System.out.println("Fields of "+_c.getName() + ": "+_c.getFields());
                System.out.println(exc);
                System.exit(1);
              }
            }
          }
        }
      }
    }
  } 
  
  protected void setBooleanTo(SootClass c, SootField f, boolean value) {
    
    if (!value && f.getType() instanceof IntegerType && Rand.getInt() % 2 > 0) 
      return;
    
    Body b = null;
    boolean newInit = false;
    if (!c.declaresMethodByName("<clinit>")) {
      SootMethod m = new SootMethod("<clinit>", new ArrayList(),VoidType.v());
      c.addMethod(m);
      b = Jimple.v().newBody(m);
      m.setActiveBody(b);
      newInit = true;
    } else {
      SootMethod m = c.getMethodByName("<clinit>");
      b = m.getActiveBody();
  	}
    
    PatchingChain units = b.getUnits();
    if (f.getType() instanceof IntegerType) {
	  units.addFirst(
	      Jimple.v().newAssignStmt(
	         Jimple.v().newStaticFieldRef(f.makeRef()), 
	         IntConstant.v(value ? 1 : 0)));
    } else {
      Local bool = Jimple.v().newLocal("boolLcl",boolRef);
      b.getLocals().add(bool);
      
      SootMethod boolInit = boolRef.getSootClass().getMethod("void <init>(boolean)");
      
      units.addFirst(Jimple.v().newAssignStmt(
          					Jimple.v().newStaticFieldRef(f.makeRef()),
          					bool));
      
      units.addFirst(Jimple.v().newInvokeStmt(
          Jimple.v().newSpecialInvokeExpr(bool, boolInit.makeRef(), IntConstant.v(value ? 1 : 0))));
      
      units.addFirst(Jimple.v().newAssignStmt(bool, 
	         Jimple.v().newNewExpr(boolRef)));
    }
    if (newInit)
      units.addLast(Jimple.v().newReturnVoidStmt());
  }
  
  protected void renameField(String cName, SootField f) {
    if (sootFieldsRenamed.contains(f)) 
      return;
    
    String newName = (String)oldToNewFieldNames.get(f.getName());
    if (newName == null)
    {
      newName = getNewName();
      oldToNewFieldNames.put(f.getName(), newName);
    }
   if (output)
      G.v().out.println("\t\tChanged " + f.getName() + " to " + newName);
    f.setName(newName);
    sootFieldsRenamed.add(f);
  }
  
  /*
   * @return	String	newly generated junk name that DOES NOT exist yet
   */
  public static String getNewName() 
  {
    int size = 3;
    int tries = 0;
    int index = Rand.getInt(stringChars.length);
    int length = stringChars[index].length;
    
    String result = null;
    char cNewName[] = new char[size];
    do {
      if (tries == 10)
      {
        cNewName = new char[++size];
        index = Rand.getInt(stringChars.length);
        length = stringChars[index].length;
        tries = 0;
      }
      
      if (size<12) { 
	      do {
            int rand = Rand.getInt(length);
	        cNewName[0] = stringChars[index][rand];
	      } while (!Character.isJavaIdentifierStart(cNewName[0]));
	      
	      // generate random string
	      for (int i = 1; i < cNewName.length; i++)
	      {
	        int rand = Rand.getInt(length);
	        cNewName[i] = stringChars[index][rand];      
	      }
	      result = String.copyValueOf(cNewName);
      } else {
        cNewName = new char[size-6];  // size will always be at least 8 here
        
        // generate more random string
        while (true) {
          for (int i = 0; i < cNewName.length; i++)
		    cNewName[i] = (char)Rand.getInt();
          result = String.copyValueOf(cNewName);
          if (isJavaIdentifier(result))
            break;
        }
      }
      tries++;
    } while (oldToNewFieldNames.containsValue(result) || BodyBuilder.nameList.contains(result));
    
    BodyBuilder.nameList.add(result);
    
    return result;
  }
  
  public static void addOldAndNewName(String oldn, String newn) {
    oldToNewFieldNames.put(oldn,newn);
  }
  
  public static boolean isJavaIdentifier(String s) {
    if (s == null || s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
        return false;
    }
    for (int i=1; i<s.length(); i++) {
        if (!Character.isJavaIdentifierPart(s.charAt(i))) {
            return false;
        }
    }
    return true;
  }
  
  public static SootField[] getRandomOpaques() 
  {
    if (handedOutPairs == null) {
      handedOutPairs = new int[opaquePairs.length];
    }
    
    int lowValue = 99999;
    ArrayList available = new ArrayList();
    for (int i = 0; i < handedOutPairs.length; i++)
      if (lowValue>handedOutPairs[i]) lowValue = handedOutPairs[i];
    for (int i = 0; i < handedOutPairs.length; i++)
      if (handedOutPairs[i] == lowValue) available.add(new Integer(i));
      
    Integer index = (Integer)available.get(Rand.getInt(available.size()));
    handedOutPairs[index.intValue()]++;
    
    return opaquePairs[index.intValue()];
  }
  
  public static int getRandomOpaquesForRunnable() 
  {
    if (handedOutRunPairs == null) {
      handedOutRunPairs = new int[opaquePairs.length];
    }
    
    int lowValue = 99999;
    ArrayList available = new ArrayList();
    for (int i = 0; i < handedOutRunPairs.length; i++)
      if (lowValue>handedOutRunPairs[i]) lowValue = handedOutRunPairs[i];
    if (lowValue>2) return -1;
    for (int i = 0; i < handedOutRunPairs.length; i++)
      if (handedOutRunPairs[i] == lowValue) available.add(new Integer(i));
      
    Integer index = (Integer)available.get(Rand.getInt(available.size()));
	   
    return index.intValue();
  }
  
  public static void updateOpaqueRunnableCount(int i) {
    handedOutRunPairs[i]++;
  }
  
  private void buildOpaquePairings() {
    Object fields1[] = opaquePreds1ByClass.values().toArray();
    Object fields2[] = opaquePreds2ByClass.values().toArray();
    
    int leng = fields1.length;
    
    if (leng>1) {
	    int i = leng * 2;
	    while (i>1) {
	      int rand1 = Rand.getInt(leng);
	      int rand2 = Rand.getInt(leng);
	      int rand3 = Rand.getInt(leng);
	      int rand4 = Rand.getInt(leng);
	      while (rand1 == rand2) 
	        rand2 = Rand.getInt(leng);
	      
	      while (rand3 == rand4) 
	        rand4 = Rand.getInt(leng);
	      
	      Object value = fields1[rand1];
	      fields1[rand1]  = fields1[rand2];
	      fields1[rand2]  = value;
	      value = fields2[rand3];
	      fields2[rand3]  = fields2[rand4];
	      fields2[rand4]  = value;
	      i--;
	    }
    }
    opaquePairs = new SootField[leng][2];
    for (int i = 0; i < leng; i++) {
      opaquePairs[i] = new SootField[]{(SootField)fields1[i], (SootField)fields2[i]};
    }
  }
}
