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

public class ClassName {

  private int m_arrayCount;
  private String m_className;

  public void constantPoolParse(String value){

    if(value.startsWith("[")){
      m_className = DescriptorParser.v().parseDesc(value);
    } else {
      m_className = value.replace('/','.');
    }

    m_arrayCount = 0;
    while(m_className.contains("[")){
      ++m_arrayCount;
      m_className = m_className.substring(0, m_className.length()-2);
    }
  }

  public String getClassName(){
    return m_className;
  }

  public void setClassName(String class_name){
    m_className = class_name;
  }

  public int getArrayCount(){
    return m_arrayCount;
  }

  public void setArrayCount(int array_count){
    m_arrayCount = array_count;
  }

  public String toJavaString(){
    StringBuilder ret = new StringBuilder();
    ret.append(m_className);
    for(int i = 0; i < m_arrayCount; ++i){
      ret.append("[]");
    }
    return ret.toString();
  }

  public String toConstantPoolString(){
    StringBuilder ret = new StringBuilder();
    for(int i = 0; i < m_arrayCount; ++i){
      ret.append("[");
    }
    ret.append(m_className.replace(".", "/"));
    return ret.toString();
  }
}
