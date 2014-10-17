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
import soot.ByteType;
import soot.BooleanType;
import soot.CharType;
import soot.ShortType;
import soot.IntType;
import soot.LongType;
import soot.FloatType;
import soot.DoubleType;
import soot.VoidType;
import soot.ArrayType;
import soot.RefType;
import soot.Scene;

public class StringToType {

  public static Type convert(String input){
    //convert JNI types here

    String input_no_array = input.replace("[", "");
    input_no_array = input_no_array.replace("]", "");

    Type base_type;
    if(input_no_array.equals("byte")){
      base_type = ByteType.v();
    } else if(input_no_array.equals("boolean")){
      base_type = BooleanType.v();
    } else if(input_no_array.equals("char")){
      base_type = CharType.v();
    } else if(input_no_array.equals("short")){
      base_type = ShortType.v();
    } else if(input_no_array.equals("int")){
      base_type = IntType.v();
    } else if(input_no_array.equals("long")){
      base_type = LongType.v();
    } else if(input_no_array.equals("float")){
      base_type = FloatType.v();
    } else if(input_no_array.equals("double")){
      base_type = DoubleType.v();
    } else if(input_no_array.equals("void")){
      base_type = VoidType.v();
    } else {
      base_type = RefType.v(input_no_array);
    }

    String array_post_fix = input.substring(input_no_array.length());
    //each dimension has []
    int num_dimensions = array_post_fix.length() / 2;
    if(num_dimensions > 0){
      return ArrayType.v(base_type, num_dimensions);
    } else {
      return base_type;
    }
  }

  public static boolean isArrayType(String input){
    String input_no_array = input.replace("[", "");
    input_no_array = input_no_array.replace("]", "");

    String array_post_fix = input.substring(input_no_array.length());
    //each dimension has []
    int num_dimensions = array_post_fix.length() / 2;
    if(num_dimensions > 0){
      return true;
    }
    return false;
  }

  public static String getBaseType(String input){
    String input_no_array = input.replace("[", "");
    input_no_array = input_no_array.replace("]", "");
    return input_no_array;
  }

  public static boolean isRefType(String input){
    String input_no_array = input.replace("[", "");
    input_no_array = input_no_array.replace("]", "");

    String array_post_fix = input.substring(input_no_array.length());
    //each dimension has []
    int num_dimensions = array_post_fix.length() / 2;
    if(num_dimensions > 0){
      return true;
    }

    if(input_no_array.equals("byte")){
      return false;
    } else if(input_no_array.equals("boolean")){
      return false;
    } else if(input_no_array.equals("char")){
      return false;
    } else if(input_no_array.equals("short")){
      return false;
    } else if(input_no_array.equals("int")){
      return false;
    } else if(input_no_array.equals("long")){
      return false;
    } else if(input_no_array.equals("float")){
      return false;
    } else if(input_no_array.equals("double")){
      return false;
    } else if(input_no_array.equals("void")){
      return false;
    } else {
      return true;
    }
  }
}
