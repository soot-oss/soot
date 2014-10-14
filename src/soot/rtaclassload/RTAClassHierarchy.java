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

import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Collections;

public class RTAClassHierarchy {

  private Set<RTAType> allTypes;
  private Set<RTAType> refTypes;
  private Set<RTAType> interfaceTypes;
  private Map<RTAType, Set<RTAType>> interfaceChildren;
  private int classNumber;
  private int interfaceCount;
  private Set<RTAType> topoVisited;
  private Set<RTAType> bfsVisited;
  private List<NumberedType> numberedTypes;
  private Map<RTAType, NumberedType> rtaToNumberedTypeMap;

  public RTAClassHierarchy(){
    allTypes = new TreeSet<RTAType>();
    refTypes = new TreeSet<RTAType>();
    interfaceTypes = new TreeSet<RTAType>();
    interfaceChildren = new TreeMap<RTAType, Set<RTAType>>();
    topoVisited = new TreeSet<RTAType>();
    bfsVisited = new TreeSet<RTAType>();
    numberedTypes = new ArrayList<NumberedType>();
    rtaToNumberedTypeMap = new TreeMap<RTAType, NumberedType>();
  }

  public void addTypes(Set<RTAType> types){
    for(RTAType type : types){
      addType(type);
    }
  }

  public void addType(RTAType type){
    allTypes.add(type.getNonArray());
  }

  public void addTypes(RTAType[] types){
    for(RTAType type : types){
      addType(type);
    }
  }

  public int size(){
    return allTypes.size();
  }

  public Set<RTAType> getAllTypes(){
    return allTypes;
  }

  public List<NumberedType> numberClasses(){
    RTAType object = RTAType.create("java.lang.Object");
    findRefTypes();
    orderInterfaceTypes();
    topoVisitInterfaces(object);
    bfsVisitConcrete();

    Collections.sort(numberedTypes);
    for(NumberedType numberedType : numberedTypes){
      rtaToNumberedTypeMap.put(numberedType.getType(), numberedType);
    }
    return numberedTypes;
  }

  public NumberedType getNumberedType(RTAType rtaType){
    return rtaToNumberedTypeMap.get(rtaType);
  }

  private void findRefTypes(){
    for(RTAType type : allTypes){
      if(type.isRefType()){
        refTypes.add(type);
      }
    }
  }

  private void orderInterfaceTypes(){
    for(RTAType type : refTypes){
      RTAClass rtaClass = RTAClassLoader.v().getRTAClass(type);
      if(rtaClass.isInterface()){
        interfaceTypes.add(type);
        RTAType[] interfaceParents = rtaClass.getInterfaces();
        for(RTAType interfaceParent : interfaceParents){
          addInterfaceHierarchy(type, interfaceParent);
        }
      }
    }
    interfaceChildren.put(RTAType.create("java.lang.Object"), interfaceTypes);
    interfaceCount = interfaceTypes.size();
    classNumber = interfaceCount + 1;
  }

  private void addInterfaceHierarchy(RTAType child, RTAType parent){
    Set<RTAType> children;
    if(interfaceChildren.containsKey(parent)){
      children = interfaceChildren.get(parent);
    } else {
      children = new TreeSet<RTAType>();
      interfaceChildren.put(parent, children);
    }
    children.add(child);
  }

  private Set<RTAType> getInterfaceChildren(RTAType rtaType){
    if(interfaceChildren.containsKey(rtaType)){
      return interfaceChildren.get(rtaType);
    } else {
      return new TreeSet<RTAType>();
    }
  }

  private void topoVisitInterfaces(RTAType rtaType){
    rtaType = rtaType.getNonArray();
    if(topoVisited.contains(rtaType)){
      return;
    }
    topoVisited.add(rtaType);

    Set<RTAType> children = getInterfaceChildren(rtaType);
    for(RTAType child : children){
      child = child.getNonArray();
      if(refTypes.contains(child)){
        RTAClass rtaClass = RTAClassLoader.v().getRTAClass(child);
        if(rtaClass.isInterface()){
          topoVisitInterfaces(child);
        }
      }
    }

    NumberedType numberedType = new NumberedType(interfaceCount, rtaType);
    numberedTypes.add(numberedType);
    --interfaceCount;
  }

  private void bfsVisitConcrete(){
    RTAType object = RTAType.create("java.lang.Object");
    LinkedList<RTAType> queue = new LinkedList<RTAType>();
    queue.add(object);
    while(!queue.isEmpty()){
      RTAType item = queue.removeFirst();
      item = item.getNonArray();
      if(bfsVisited.contains(item)){
        continue;
      }
      bfsVisited.add(item);

      if(refTypes.contains(item)){
        RTAClass rtaClass = RTAClassLoader.v().getRTAClass(item);
        if(rtaClass.isInterface() == false){
          if(item != object){            //notice RTATypes are flyweights
            NumberedType numberedType = new NumberedType(classNumber, item);
            numberedTypes.add(numberedType);
            ++classNumber;
          }
        }
      }

      List<RTAType> children = RTAClassLoader.v().getSubClasses(item);
      for(RTAType child : children){
        child = child.getNonArray();
        if(refTypes.contains(child)){
          queue.add(child);
        }
      }
    }
  }
}
