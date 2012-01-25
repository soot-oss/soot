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
import soot.jbco.util.Rand;
import soot.jimple.*;
import soot.util.*;

/**
 * @author Michael Batchelder
 * 
 * Created on 31-May-2006
 */
public class CollectConstants extends SceneTransformer implements IJbcoTransform {

  int updatedConstants = 0;
  int constants = 0;
  
  public void outputSummary() {
    out.println(constants + " constants found");
    out.println(updatedConstants + " static fields created");
  }

  public static String dependancies[] = new String[] { "wjtp.jbco_cc" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "wjtp.jbco_cc";
  
  public String getName() {
    return name;
  }
  
  public static HashMap<Constant, SootField> constantsToFields = new HashMap<Constant, SootField>();
  public static HashMap<Type,List<Constant>> typesToValues = new HashMap<Type,List<Constant>>();

  public static SootField field = null;  
  
  protected void internalTransform(String phaseName, Map options)
  {
    Scene scene = G.v().soot_Scene();
    
    if (output)
      out.println("Collecting Constant Data");
    
    soot.jbco.util.BodyBuilder.retrieveAllNames();
   
    Chain appClasses = scene.getApplicationClasses();
    Iterator it = appClasses.iterator();
    while (it.hasNext()) {
      SootClass cl = (SootClass)it.next();
      Object meths[] = cl.getMethods().toArray();
      for (Object element : meths) {
        SootMethod m = (SootMethod)element;
        if (!m.hasActiveBody() || m.getName().indexOf("<clinit>")>=0)
          continue;
        Body body = m.getActiveBody();
        Iterator iter = body.getUseBoxes().iterator();
        while (iter.hasNext()) {
          Value v = ((ValueBox) iter.next()).getValue();
          if (v instanceof Constant) {
            Constant c = (Constant) v;
            Type t = c.getType();
            List<Constant> values = typesToValues.get(t);
            if (values == null) {
              values = new ArrayList<Constant>();
              typesToValues.put(t, values);
            }
            
            boolean found = false;
            Iterator<Constant> vit = values.iterator();
            while (vit.hasNext()) {
              if (vit.next().equals(c)) {
                found = true;
                break;
              }
            }
            
            if (!found) {
              constants++;
              values.add(c);
            }
          }
        }
      }
    }
    
    int count = 0;
    String name = "newConstantJbcoName";
    Object classes[] = appClasses.toArray();
    it = typesToValues.keySet().iterator();
    while (it.hasNext()) {
      Type t = (Type) it.next();
      if (t instanceof NullType)  continue; //t = RefType.v("java.lang.Object");
      Iterator cit = typesToValues.get(t).iterator();
      while (cit.hasNext()) {
        Constant c = (Constant) cit.next();
        
        name += "_";
        SootClass rand = null;
        do {
          rand = (SootClass)classes[Rand.getInt(classes.length)];
        } while (rand.isInterface());
        
        SootField newf = new SootField(FieldRenamer.getNewName(), t, Modifier.STATIC
              ^ Modifier.PUBLIC);
        rand.addField(newf);
        FieldRenamer.sootFieldsRenamed.add(newf);
        FieldRenamer.addOldAndNewName(name, newf.getName());
        constantsToFields.put(c, newf);
        addInitializingValue(rand, newf, c);
        FieldRenamer.addOldAndNewName("addedConstant" + count++, newf.getName());
      }
    }
    
    updatedConstants += count;
  }
  
  private void addInitializingValue(SootClass clas, SootField f, Constant con) {
    if (con instanceof NullConstant) {return;}
    else if (con instanceof IntConstant)
      {if (((IntConstant)con).value == 0) return;}
    else if (con instanceof LongConstant)
      {if (((LongConstant)con).value == 0) return;}
    else if (con instanceof StringConstant)
      {if (((StringConstant)con).value == null) return;}
    else if (con instanceof DoubleConstant)
      {if (((DoubleConstant)con).value == 0) return;}
    else if (con instanceof FloatConstant)
      {if (((FloatConstant)con).value == 0) return;}    
    
    Body b = null;
    boolean newInit = false;
    if (!clas.declaresMethodByName("<clinit>")) {
      SootMethod m = new SootMethod("<clinit>", new ArrayList(), VoidType.v());
      clas.addMethod(m);
      b = Jimple.v().newBody(m);
      m.setActiveBody(b);
      newInit = true;
    } else {
      SootMethod m = clas.getMethodByName("<clinit>");
      if(!m.hasActiveBody()) {
          b = Jimple.v().newBody(m);
          m.setActiveBody(b);
          newInit = true;
      } else {
    	  b = m.getActiveBody();
      }
    }

    PatchingChain units = b.getUnits();
    
    units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(f.makeRef()),con));
    if (newInit)
      units.addLast(Jimple.v().newReturnVoidStmt());
  }
}