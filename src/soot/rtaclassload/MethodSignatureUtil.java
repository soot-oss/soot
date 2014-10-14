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

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Set;
import soot.Scene;
import soot.options.Options;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.Type;

public class MethodSignatureUtil {

  private String m_className;
  private String m_returnType;
  private String m_methodName;
  private List<String> m_params;

  public MethodSignatureUtil(){
  }

  public MethodSignatureUtil(String method_signature) {
    parse(method_signature);
  }

  public void parse(MethodSignature signature){
    parse(signature.toString());
  }

  public void parseSubSignature(String method_sub_sig){
    String[] tokens1 = method_sub_sig.split("\\(");
    String front = tokens1[0].trim();
    String back = tokens1[1].trim();

    String[] tokens2 = front.split(" ");
    m_returnType = tokens2[0].trim();
    m_methodName = tokens2[1].trim();

    String params = back.substring(0, back.length()-1);
    String[] param_tokens = params.split(",");
    m_params = new ArrayList<String>();

    for(String param : param_tokens){
      String curr = param.trim();
      if(curr.equals("") == false){
        m_params.add(curr);
      }
    }
  }

  public void parse(String signature){
    String[] tokens0 = signature.split(":");
    String cls = tokens0[0].trim();
    m_className = cls.substring(1);

    String method_sub_sig = tokens0[1].trim();
    method_sub_sig = method_sub_sig.substring(0, method_sub_sig.length()-1);
    parseSubSignature(method_sub_sig);
  }

  public void quote(){
    Set<String> reserved_names = Scene.v().getReservedNames();
    m_methodName = quoteString(m_methodName, reserved_names);
  }

  private String quoteString(String value, Set<String> reserved_names){
    if(reserved_names.contains(value)){
      value = "'" + value + "'";
    }
    return value;
  }

  private void quoteStrings(){
    m_className = Scene.v().quotedNameOf(m_className);
    m_methodName = Scene.v().quotedNameOf(m_methodName);
  }

  private void print(){
    System.out.println("return_type: ["+m_returnType+"]");
    System.out.println("class_name: ["+m_className+"]");
    System.out.println("method_name: ["+m_methodName+"]");
    System.out.print("args: [");
    for(String arg : m_params){
      System.out.println("  {"+arg+"}");
    }
    System.out.println("]");
  }

  public String getClassName(){
    return m_className;
  }

  public void setClassName(String class_name){
    m_className = class_name;
  }

  public String getReturnType(){
    return m_returnType;
  }

  public void setReturnType(String return_type){
    m_returnType = return_type;
  }

  public String getMethodName(){
    return m_methodName;
  }

  public void setMethodName(String method_name){
    m_methodName = method_name;
  }

  public List<String> getParameterTypes(){
    return m_params;
  }

  public List<Type> getParameterTypesTyped(){
    List<Type> ret = new ArrayList<Type>();
    StringToType converter = new StringToType();
    for(String param_str : m_params){
      ret.add(converter.convert(param_str));
    }
    return ret;
  }

  public void setParameterTypes(List<String> params){
    m_params = params;
  }

  public String getSubSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append(m_returnType);
    ret.append(" ");
    ret.append(m_methodName);
    ret.append("(");
    for(int i = 0; i < m_params.size(); ++i){
      ret.append(m_params.get(i));
      if(i < m_params.size() - 1){
        ret.append(",");
      }
    }
    ret.append(")");
    return ret.toString();
  }

  public String getCovarientSubSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append(m_methodName);
    ret.append("(");
    for(int i = 0; i < m_params.size(); ++i){
      ret.append(m_params.get(i));
      if(i < m_params.size() - 1){
        ret.append(",");
      }
    }
    ret.append(")");
    return ret.toString();
  }

  public boolean covarientEqual(String rhs_signature){
    MethodSignatureUtil rhs_util = new MethodSignatureUtil();
    rhs_util.parse(rhs_signature);

    if(getMethodName().equals(rhs_util.getMethodName()) == false){
      return false;
    }
    List<String> lhs_types = getParameterTypes();
    List<String> rhs_types = rhs_util.getParameterTypes();
    if(lhs_types.size() != rhs_types.size()){
      return false;
    }
    for(int i = 0; i < lhs_types.size(); ++i){
      String lhs_type = lhs_types.get(i);
      String rhs_type = rhs_types.get(i);
      if(lhs_type.equals(rhs_type) == false){
        return false;
      }
    }
    return true;
  }

  public SootMethod getSootMethod(){
    MethodFieldFinder finder = new MethodFieldFinder();
    return finder.findMethod(getSignature());
  }

  public String getSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append("<");
    ret.append(m_className);
    ret.append(": ");
    ret.append(getSubSignature());
    ret.append(">");
    return ret.toString();
  }

  @Override
  public String toString(){
    return getSignature();
  }

  public static void main(String[] args){
    String sig = "<java.util.LinkedList: java.util.LinkedList$Entry addBefore(java.lang.Object,java.util.LinkedList$Entry)>";
    MethodSignatureUtil util = new MethodSignatureUtil();
    util.parse(sig);
    util.print();
  }
}
