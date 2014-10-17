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

import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import soot.Type;
import java.util.List;
import java.util.ArrayList;

public class RTAType implements Comparable<RTAType> {

  private static Map<Integer, Map<Integer, Map<Integer, RTAType>>> flyweightStorage;
  private static Set<String> primitiveTypes;

  static {
    flyweightStorage = new TreeMap<Integer, Map<Integer, Map<Integer, RTAType>>>();
    primitiveTypes = new TreeSet<String>();
    primitiveTypes.add("byte");
    primitiveTypes.add("boolean");
    primitiveTypes.add("char");
    primitiveTypes.add("short");
    primitiveTypes.add("int");
    primitiveTypes.add("long");
    primitiveTypes.add("float");
    primitiveTypes.add("double");
    primitiveTypes.add("void");
  }

  public static RTAType create(String typeString){
    RTAType ret = new RTAType(typeString);
    return ensureExists(ret);
  }

  private static RTAType ensureExists(RTAType type){
    Map<Integer, Map<Integer, RTAType>> packageMap;
    Map<Integer, RTAType> classMap;

    if(flyweightStorage.containsKey(type.getPackageNumber())){
      packageMap = flyweightStorage.get(type.getPackageNumber());
    } else {
      packageMap = new TreeMap<Integer, Map<Integer, RTAType>>();
      flyweightStorage.put(type.getPackageNumber(), packageMap);
    }

    if(packageMap.containsKey(type.getClassNumber())){
      classMap = packageMap.get(type.getClassNumber());
    } else {
      classMap = new TreeMap<Integer, RTAType>();
      packageMap.put(type.getClassNumber(), classMap);
    }

    if(classMap.containsKey(type.getNumDimensions())){
      return classMap.get(type.getNumDimensions());
    } else {
      classMap.put(type.getNumDimensions(), type);
      return type;
    }
  }

  private int packageNumber;
  private int classNumber;
  private int numDimensions;
  private RTAType superClass;
  private List<RTAType> subClasses;
  private RTAClass rtaClass;

  private RTAType(String typeString){
    String typeStrNonArray = typeString.replace("[", "");
    typeStrNonArray = typeStrNonArray.replace("]", "");

    if(primitiveTypes.contains(typeStrNonArray)){
      classNumber = StringNumbers.v().addString(typeStrNonArray);
      packageNumber = StringNumbers.v().addString("<default>");
    } else {
      String className = getClassName(typeStrNonArray);
      String packageName = getPackageName(typeStrNonArray);
      if(packageName.equals("")){
        packageName = "<default>";
      }
      classNumber = StringNumbers.v().addString(className);
      packageNumber = StringNumbers.v().addString(packageName);
    }

    String arrayPostFix = typeString.substring(typeStrNonArray.length());
    //each dimension has []
    numDimensions = arrayPostFix.length() / 2;

    subClasses = new ArrayList<RTAType>();
    rtaClass = null;
  }

  private RTAType(int packageNumber, int classNumber, int numDimensions)
  {
    this.packageNumber = packageNumber;
    this.classNumber = classNumber;
    this.numDimensions = numDimensions;

    subClasses = new ArrayList<RTAType>();
    rtaClass = null;
  }

  public void setRTAClass(RTAClass rtaClass){
    this.rtaClass = rtaClass;
  }

  public RTAClass getRTAClass(){
    return rtaClass;
  }

  public void setSuperClass(RTAType superClass){
    this.superClass = superClass;
  }

  public RTAType getSuperClass(){
    return superClass;
  }

  public void addSubClass(RTAType subClass){
    subClasses.add(subClass);
  }

  public List<RTAType> getSubClasses(){
    return subClasses;
  }

  public int getPackageNumber(){
    return packageNumber;
  }

  public int getClassNumber(){
    return classNumber;
  }

  public int getNumDimensions(){
    return numDimensions;
  }

  public RTAType getNonArray(){
    RTAType ret = new RTAType(packageNumber, classNumber, 0);
    return ensureExists(ret);
  }

  public boolean isArray(){
    if(numDimensions == 0){
      return false;
    } else {
      return true;
    }
  }

  public boolean isRefType(){
    String packageName = getPackageName();
    if(packageName.equals("<default>")){
      String className = StringNumbers.v().getString(classNumber);
      if(primitiveTypes.contains(className)){
        return false;
      } else {
        return true;
      }
    } else {
      return true;
    }
  }

  public String getPackageName(){
    return StringNumbers.v().getString(packageNumber);
  }

  public Type toSootType(){
    return StringToType.convert(toString());
  }

  private String getPackageName(String className) {
    String[] tokens = className.split("\\.");
    String ret = "";
    for(int i = 0; i < tokens.length - 1; ++i){
      ret += tokens[i];
      if(i < tokens.length - 2){
        ret += ".";
      }
    }
    return ret;
  }

  private String getClassName(String className){
    String[] tokens = className.split("\\.");
    return tokens[tokens.length-1];
  }

  @Override
  public int compareTo(RTAType other){
    int packageCompare = Integer.compare(packageNumber, other.packageNumber);
    if(packageCompare == 0){
      int classCompare = Integer.compare(classNumber, other.classNumber);
      if(classCompare == 0){
        return Integer.compare(numDimensions, other.numDimensions);
      } else {
        return classCompare;
      }
    } else {
      return packageCompare;
    }
  }

  @Override
  public String toString(){
    String packageName = getPackageName();
    String className = StringNumbers.v().getString(classNumber);
    String ret = "";
    if(packageName.equals("<default>")){
      ret = className;
    } else {
      ret = packageName + "." + className;
    }
    for(int i = 0; i < numDimensions; ++i){
      ret += "[]";
    }
    return ret;
  }
}
