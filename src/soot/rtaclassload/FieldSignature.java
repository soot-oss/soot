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

public class FieldSignature implements Comparable<FieldSignature> {

  private RTAType declaringClass;
  private RTAType type;
  private int name;

  public FieldSignature(String field_ref){
    FieldSignatureUtil util = new FieldSignatureUtil();
    util.parse(field_ref);
    parse(util);
  }

  public void parse(FieldSignatureUtil util){
    declaringClass = RTAType.create(util.getDeclaringClass());
    type = RTAType.create(util.getType());
    name = StringNumbers.v().addString(util.getName());
  }

  public RTAType getDeclaringClass(){
    return declaringClass;
  }

  public RTAType getType(){
    return type;
  }

  public int getName(){
    return name;
  }

  @Override
  public int compareTo(FieldSignature other){
    int classCompare = declaringClass.compareTo(other.declaringClass);
    if(classCompare == 0){
      int typeCompare = type.compareTo(other.type);
      if(typeCompare == 0){
        return Integer.valueOf(name).compareTo(Integer.valueOf(other.name));
      } else {
        return typeCompare;
      }
    } else {
      return classCompare;
    }
  }

  @Override
  public String toString(){
    return getSignature();
  }

  public String getSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append("<");
    ret.append(declaringClass.toString());
    ret.append(": ");
    ret.append(getSubSignature());
    ret.append(">");
    return ret.toString();
  }

  public String getSubSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append(type.toString());
    ret.append(" ");
    ret.append(StringNumbers.v().getString(name));
    return ret.toString();
  }
}
