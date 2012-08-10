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
 
package soot.jimple.toolkits.scalar;
 
import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;

public class OriginalNamePacker extends BodyTransformer {

  private Map<String, List<Local>> m_baseNameToLocals;
  private Map<String, List<Type>> m_types;
  
  public OriginalNamePacker(soot.Singletons.Global g){

  }
  
  public static OriginalNamePacker v() { return G.v().soot_jimple_toolkits_scalar_OriginalNamePacker(); }

  protected void internalTransform(Body body, String phaseName, Map options)
  {
    System.out.println("running OriginalNamePacker: "+body.getMethod().getSignature());
    createBaseNameMap(body);
    calculateRemapping();
  }
  
  private void createBaseNameMap(Body body){
    m_baseNameToLocals = new HashMap<String, List<Local>>();
    
    Chain<Local> locals = body.getLocals();
    
    //build mapping for locals with a # (for instance iter#2)
    for(Local local : locals){
      int signIndex = local.getName().indexOf("#");
      
      String base_name;
                            
		  if(signIndex != -1){
			  base_name = local.getName().substring(0, signIndex);
			} else {
        base_name = local.getName();
      }
      if(m_baseNameToLocals.containsKey(base_name)){
			  List<Local> list = m_baseNameToLocals.get(base_name);
			  list.add(local);
			} else {
			  List<Local> list = new ArrayList<Local>();
			  list.add(local);
			  m_baseNameToLocals.put(base_name, list);
			}
    }
    
    //now there exists the un-numbered locals that we need to look at too (for instance iter)
    for(Local local : locals){
      String base_name = local.getName();
      if(m_baseNameToLocals.containsKey(base_name)){
        List<Local> list = m_baseNameToLocals.get(base_name);
			  list.add(local);
      }
    }
  }
  
  private void calculateRemapping(){
  
    m_types = new HashMap<String, List<Type>>();
    
    Iterator<String> iter = m_baseNameToLocals.keySet().iterator();
    while(iter.hasNext()){
      String base_name = iter.next();
      List<Local> locals = m_baseNameToLocals.get(base_name);
      for(Local local : locals){
        insertType(m_types, base_name, local.getType());
      }
      List<Type> types = m_types.get(base_name);
      packTypes(types);
      for(Local local : locals){
        if(types.size() != 1){
          Type local_type = local.getType();
          int index = types.indexOf(local_type);
          if(index == -1){
            index = findIntType(types);
          }
          String new_name = base_name + "_"+index;
          local.setName(new_name);
          local.setType(types.get(index));
        } else {
          local.setName(base_name);
          local.setType(types.get(0));
        }
      }
    }
  }
  
  private int findIntType(List<Type> types){
    int index = 0;
    for(Type type : types){
      if(type instanceof IntegerType){
        return index;
      }
      index++;
    }
    return -1;
  }
  
  private void packTypes(List<Type> types){
    Type largest_int_type = null;
    int largest_id = -1;
    for(int i = 0; i < types.size(); ++i){
      Type curr = types.get(i);
      if(curr instanceof IntegerType){
        int id = -1;
        if(curr instanceof BooleanType){
          id = 1;
        }
        if(curr instanceof ByteType){
          id = 2;
        }
        if(curr instanceof CharType){
          id = 3;
        }
        if(curr instanceof ShortType){
          id = 4;
        }
        if(curr instanceof IntType){
          id = 8;
        }
        if(id > largest_id){
          largest_id = id;
          largest_int_type = curr;
        }
        types.remove(curr);
        --i;
      }
    }
    if(largest_int_type != null){
      types.add(largest_int_type);
    }    
  }
  
  private void insertType(Map<String, List<Type>> type_map, String base_name, Type type){
    if(type_map.containsKey(base_name)){
      List<Type> types = type_map.get(base_name);
      if(types.contains(type) == false){
        types.add(type);
      }
    } else {
      List<Type> types = new ArrayList<Type>();
      types.add(type);
      type_map.put(base_name, types);
    }
  }
  
  private Local findOrCreate(Local lhs, String new_name, Chain<Local> locals){
    Iterator<Local> iter = locals.iterator();
    Local ret = null;
    while(iter.hasNext()){
      Local curr = iter.next();
      if(curr.getName().equals(new_name)){
        ret = curr;
        break;
      }
    }
    if(ret == null){
      ret = (Local) lhs.clone();
      ret.setName(new_name);
      locals.addLast(ret);
    }
    return ret;
  }
}
