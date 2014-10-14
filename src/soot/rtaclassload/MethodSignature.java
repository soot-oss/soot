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

import java.util.List;
import java.util.ArrayList;

public class MethodSignature implements Comparable<MethodSignature> {

  private RTAType className;
  private int methodName;
  private RTAType returnType;
  private RTAType[] params;

  public MethodSignature(RTAType className, int methodName, RTAType returnType,
    RTAType[] params){

    this.className = className;
    this.methodName = methodName;
    this.returnType = returnType;
    this.params = params;
  }

  public MethodSignature(String className, String methodName, String returnType,
      String[] params){

    this.className = RTAType.create(className);
    this.methodName = StringNumbers.v().addString(methodName);
    this.returnType = RTAType.create(returnType);
    this.params = new RTAType[params.length];
    for(int i = 0; i < params.length; ++i){
      this.params[i] = RTAType.create(params[i]);
    }
  }

  public MethodSignature(String signature){
    MethodSignatureUtil util = new MethodSignatureUtil();
    util.parse(signature);
    parse(util);
  }

  public MethodSignature(MethodSignatureUtil util){
    parse(util);
  }

  private void parse(MethodSignatureUtil util){
    className = RTAType.create(util.getClassName());
    methodName = StringNumbers.v().addString(util.getMethodName());
    returnType = RTAType.create(util.getReturnType());

    List<String> utilParams = util.getParameterTypes();
    params = new RTAType[utilParams.size()];
    for(int i = 0; i < utilParams.size(); ++i){
      params[i] = RTAType.create(utilParams.get(i));
    }
  }

  public void setClassName(RTAType className){
    this.className = className;
  }

  public RTAType getClassName(){
    return className;
  }

  public int getMethodName(){
    return methodName;
  }

  public RTAType getReturnType(){
    return returnType;
  }

  public RTAType[] getParameterTypes(){
    return params;
  }

  public boolean covarientMatch(MethodSignature other){
    if(methodName != other.methodName){
      return false;
    }
    if(params.length != other.params.length){
      return false;
    }
    for(int i = 0; i < params.length; ++i){
      RTAType lhs = params[i];
      RTAType rhs = other.params[i];
      if(lhs != rhs){
        return false;
      }
    }
    return true;
  }

  public boolean subsigMatch(MethodSignature other){
    if(returnType != other.returnType){
      return false;
    }
    return covarientMatch(other);
  }

  @Override
  public String toString(){
    StringBuilder ret = new StringBuilder();
    ret.append("<");
    ret.append(className.toString());
    ret.append(": ");
    ret.append(getSubSignatureString());
    ret.append(">");
    return ret.toString();
  }

  public String getSubSignatureString(){
    StringBuilder ret = new StringBuilder();
    ret.append(returnType.toString());
    ret.append(" ");
    ret.append(StringNumbers.v().getString(methodName));
    ret.append("(");
    for(int i = 0; i < params.length; ++i){
      ret.append(params[i].toString());
      if(i < params.length - 1){
        ret.append(",");
      }
    }
    ret.append(")");
    return ret.toString();
  }

  @Override
  public boolean equals(Object other){
    if(other instanceof MethodSignature == false){
      return false;
    }
    MethodSignature rhs = (MethodSignature) other;
    if(className != rhs.className){
      return false;
    }
    if(methodName != rhs.methodName){
      return false;
    }
    if(returnType != rhs.returnType){
      return false;
    }
    if(params.length != rhs.params.length){
      return false;
    }
    for(int i = 0; i < params.length; ++i){
      RTAType lhs_param = params[i];
      RTAType rhs_param = rhs.params[i];
      if(lhs_param != rhs_param){
        return false;
      }
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + this.className.hashCode();
    hash = 23 * hash + this.methodName;
    hash = 23 * hash + this.returnType.hashCode();
    hash = 23 * hash + (this.params != null ? this.params.hashCode() : 0);
    return hash;
  }

  @Override
  public int compareTo(MethodSignature other){
    int classCompare = className.compareTo(other.className);
    if(classCompare == 0){
      int methodCompare = Integer.valueOf(methodName).compareTo(Integer.valueOf(other.methodName));
      if(methodCompare == 0){
        int returnCompare = returnType.compareTo(other.returnType);
        if(returnCompare == 0){
          int paramLengthCompare = Integer.valueOf(params.length).compareTo(Integer.valueOf(other.params.length));
          if(paramLengthCompare == 0){
            for(int i = 0; i < params.length; ++i){
              RTAType param = params[i];
              RTAType otherParam = other.params[i];
              int paramCompare = param.compareTo(otherParam);
              if(paramCompare != 0){
                return paramCompare;
              }
            }
            return 0;
          } else {
            return paramLengthCompare;
          }
        } else {
          return returnCompare;
        }
      } else {
        return methodCompare;
      }
    } else {
      return classCompare;
    }
  }
}
