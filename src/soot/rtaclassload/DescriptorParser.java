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

public class DescriptorParser {

  private static DescriptorParser m_instance;

  public static DescriptorParser v(){
    if(m_instance == null){
      m_instance = new DescriptorParser();
    }
    return m_instance;
  }

  private DescriptorParser(){
  }

  public String parseDesc(String str){
    List<String> ret_list = new ArrayList<String>();
    int index = 0;
    while(index < str.length()){
      int array_count = 0;
      while(str.charAt(index) == '['){
        array_count++;
        ++index;
      }
      String param = "";
      if(str.charAt(index) == 'B'){
        param = "byte";
        ++index;
      } else if(str.charAt(index) == 'C'){
        param = "char";
        ++index;
      } else if(str.charAt(index) == 'D'){
        param = "double";
        ++index;
      } else if(str.charAt(index) == 'D'){
        param = "double";
        ++index;
      } else if(str.charAt(index) == 'F'){
        param = "float";
        ++index;
      } else if(str.charAt(index) == 'I'){
        param = "int";
        ++index;
      } else if(str.charAt(index) == 'J'){
        param = "long";
        ++index;
      } else if(str.charAt(index) == 'S'){
        param = "short";
        ++index;
      } else if(str.charAt(index) == 'Z'){
        param = "boolean";
        ++index;
      } else if(str.charAt(index) == 'V'){
        param = "void";
        ++index;
      } else if(str.charAt(index) == 'L'){
        ++index;
        param = "";
        while(str.charAt(index) != ';'){
          param += str.charAt(index);
          ++index;
        }
        ++index;
        param = param.replace('/','.');
      }
      for(int i = 0; i < array_count; ++i){
        param += "[]";
      }
      ret_list.add(param);
    }
    String ret = "";
    for(int i = 0; i < ret_list.size(); ++i){
      ret += ret_list.get(i);
      if(i < ret_list.size() - 1){
        ret += ",";
      }
    }
    return ret;
  }

  public String parseMethodDesc_return(String s) {
    int j;
    j = s.lastIndexOf(')');
    if (j>=0) {
      return parseDesc(s.substring(j+1));
    }
    return parseDesc(s);
  }

  public String[] parseMethodDesc_params(String s) {
    int i,j;
    i = s.indexOf('(');
    if (i>=0) {
      j = s.indexOf(')',i+1);
      //empty params
      if(j == i + 1){
        return new String[0];
      }
      if (j>=0) {
        String desc = parseDesc(s.substring(i+1,j));
        String[] tokens = desc.split(",");
        return tokens;
      }
    }
    throw new RuntimeException("invalid method descriptor");
  }
}
