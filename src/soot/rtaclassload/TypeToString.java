/* Soot - a J*va Optimization Framework
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

package soot.rtaclassload;

import soot.Type;
import soot.ArrayType;
import soot.RefType;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.ShortType;
import soot.IntType;
import soot.LongType;
import soot.FloatType;
import soot.DoubleType;
import soot.VoidType;

public class TypeToString {

  public static String convert(Type type){
    if(type instanceof ArrayType){
      ArrayType array_type = (ArrayType) type;
      String base_type = convert(array_type.baseType);
      for(int i = 0; i < array_type.numDimensions; ++i){
        base_type += "[]";
      }
      return base_type;
    } else if(type instanceof RefType){
      RefType ref_type = (RefType) type;
      return ref_type.getClassName();
    } else {
      if(type instanceof BooleanType){
        return "boolean";
      } else if(type instanceof ByteType){
        return "byte";
      } else if(type instanceof CharType){
        return "char";
      } else if(type instanceof ShortType){
        return "short";
      } else if(type instanceof IntType){
        return "int";
      } else if(type instanceof LongType){
        return "long";
      } else if(type instanceof FloatType){
        return "float";
      } else if(type instanceof DoubleType){
        return "double";
      } else if(type instanceof VoidType){
        return "void";
      } else {
        System.out.println("unknown type: "+type.toString());
        System.exit(0);
        return "";
      }
    }
  }
}
