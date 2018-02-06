/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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







package soot.coffi;

import java.util.ArrayList;
import java.util.List;

import soot.Scene;
import soot.Type;
import soot.Value;
import soot.jimple.Jimple;

/** A constant pool entry of type CONSTANT_Methodref
 * @see cp_info
 * @author Clark Verbrugge
 */
class CONSTANT_Methodref_info extends cp_info implements ICONSTANT_Methodref_info {
   /** Constant pool index of a CONSTANT_Class object.
    * @see CONSTANT_Class_info
    */
   public int class_index;
   /** Constant pool index of a CONSTANT_NameAndType object.
    * @see CONSTANT_NameAndType_info
    */
   public int name_and_type_index;
   /** Returns the size of this cp_info object.
    * @return number of bytes occupied by this object.
    * @see cp_info#size
    */
   public int size() { return 5; }
   /** Returns a String representation of this entry.
    * @param constant_pool constant pool of ClassFile.
    * @return String representation of this entry.
    * @see cp_info#toString
    */
   public String toString(cp_info constant_pool[]) {
      CONSTANT_Class_info cc = (CONSTANT_Class_info)(constant_pool[class_index]);
      CONSTANT_NameAndType_info cn = (CONSTANT_NameAndType_info)(constant_pool[name_and_type_index]);
      return cc.toString(constant_pool) + "." + cn.toString(constant_pool);
   }
   /** Returns a String description of what kind of entry this is.
    * @return the String "methodref".
    * @see cp_info#typeName
    */
   public String typeName() { return "methodref"; }
   /** Compares this entry with another cp_info object (which may reside
    * in a different constant pool).
    * @param constant_pool constant pool of ClassFile for this.
    * @param cp constant pool entry to compare against.
    * @param cp_constant_pool constant pool of ClassFile for cp.
    * @return a value <0, 0, or >0 indicating whether this is smaller,
    * the same or larger than cp.
    * @see cp_info#compareTo
    */
   public int compareTo(cp_info constant_pool[],cp_info cp,cp_info cp_constant_pool[]) {
      int i;
      if (tag!=cp.tag) return tag-cp.tag;
      CONSTANT_Methodref_info cu = (CONSTANT_Methodref_info)cp;
      i = constant_pool[class_index].
         compareTo(constant_pool,cp_constant_pool[cu.class_index],cp_constant_pool);
      if (i!=0) return i;
      return constant_pool[name_and_type_index].
         compareTo(constant_pool,cp_constant_pool[cu.name_and_type_index],
                   cp_constant_pool);
   }
   
	public Value createJimpleConstantValue(cp_info[] constant_pool) {
		CONSTANT_Class_info cc = (CONSTANT_Class_info) (constant_pool[class_index]);
		CONSTANT_NameAndType_info cn = (CONSTANT_NameAndType_info) (constant_pool[name_and_type_index]);
		String className = cc.toString(constant_pool);
		String nameAndType = cn.toString(constant_pool);
		String name = nameAndType.substring(0, nameAndType.indexOf(":"));
		String typeName = nameAndType.substring(nameAndType.indexOf(":") + 1);

		List parameterTypes;
		Type returnType;

		// Generate parameters & returnType & parameterTypes
		{
			Type[] types = Util.v().jimpleTypesOfFieldOrMethodDescriptor(
					typeName);
			parameterTypes = new ArrayList();
			for (int k = 0; k < types.length - 1; k++) {
				parameterTypes.add(types[k]);
			}
			returnType = types[types.length - 1];
		}

	    return Jimple.v().newStaticInvokeExpr(Scene.v().makeMethodRef(Scene.v().getSootClass(className), name, parameterTypes, returnType, true));
	}

	public int getClassIndex() {
		return class_index;
	}
	public int getNameAndTypeIndex() {
		return name_and_type_index;
	}

}
