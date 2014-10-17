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

import soot.Scene;
import soot.SootClass;
import soot.SootField;
import java.util.Iterator;
import soot.Type;
import soot.Modifier;
import java.util.List;

public class FieldSignatureUtil {

  private String m_declaringClass;
  private String m_type;
  private String m_name;

  public FieldSignatureUtil(){
  }

  public void parse(FieldSignature fieldSignature){
    parse(fieldSignature.toString());
  }

  public void parse(String field_sig){
    String[] tokens0 = field_sig.split(":");
    m_declaringClass = tokens0[0].substring(1);
    String[] tokens1 = tokens0[1].substring(1).split(" ");
    m_type = tokens1[0];
    m_name = tokens1[1].substring(0, tokens1[1].length()-1);
  }

  private void quoteStrings(){
    m_declaringClass = Scene.v().quotedNameOf(m_declaringClass);
    m_name = Scene.v().quotedNameOf(m_name);
  }

  public String getSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append("<");
    ret.append(m_declaringClass);
    ret.append(": ");
    ret.append(getSubSignature());
    ret.append(">");
    return ret.toString();
  }

  public String getSubSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append(m_type);
    ret.append(" ");
    ret.append(m_name);
    return ret.toString();
  }

  public String getQuotedSubSignature(){
    StringBuilder ret = new StringBuilder();
    ret.append(m_type);
    ret.append(" ");
    ret.append(Scene.v().quotedNameOf(m_name));
    return ret.toString();
  }

  public void setDeclaringClass(String value){
    m_declaringClass = value;
  }

  public String getDeclaringClass(){
    return m_declaringClass;
  }

  public void setType(String value){
    m_type = value;
  }

  public String getType(){
    return m_type;
  }

  public void setName(String value){
    m_name = value;
  }

  public String getName(){
    return m_name;
  }

  public SootField getSootField(){
    MethodFieldFinder finder = new MethodFieldFinder();
    List<SootField> fields = finder.findField(getSignature());
    return fields.get(0);
  }

  @Override
  public String toString(){
    return getSignature();
  }
}
