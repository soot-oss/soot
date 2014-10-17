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
import java.util.Map;
import java.util.HashMap;
import org.apache.commons.collections4.trie.PatriciaTrie;

import soot.G;
import soot.Singletons;

public class StringNumbers {

  public static StringNumbers v() {
    return G.v().soot_rtaclassload_StringNumbers();
  }

  private List<String> arrayList;
  private PatriciaTrie<Integer> map;

  public StringNumbers(Singletons.Global g){
    arrayList = new ArrayList<String>();
    map = new PatriciaTrie<Integer>();
    addString("<default>");
    addString("byte");
    addString("boolean");
    addString("char");
    addString("short");
    addString("int");
    addString("long");
    addString("float");
    addString("double");
    addString("void");
  }

  public boolean contains(String str){
    return map.containsKey(str);
  }

  public String getString(int index){
    return arrayList.get(index);
  }

  public int addString(String str){
    if(contains(str)){
      return map.get(str);
    }
    int ret = arrayList.size();
    arrayList.add(str);
    map.put(str, ret);
    return ret;
  }
}
