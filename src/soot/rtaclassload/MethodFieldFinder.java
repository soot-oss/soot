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
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ArrayList;
import soot.SootResolver;
import soot.SootClass;
import soot.SootMethod;
import soot.SootField;
import soot.Scene;

public class MethodFieldFinder {

  public List<SootField> findField(String signature){
    FieldSignatureUtil util = new FieldSignatureUtil();
    util.parse(signature);

    String subsig = util.getQuotedSubSignature();
    String start_class = util.getDeclaringClass();

    LinkedList<String> queue = new LinkedList<String>();
    queue.add(start_class);

    List<SootField> ret = new ArrayList<SootField>();

    while(queue.isEmpty() == false){
      String class_name = queue.removeFirst();

      SootClass soot_class = Scene.v().getSootClass(class_name);

      if(soot_class.declaresField(subsig)){
        SootField soot_field = soot_class.getField(subsig);
        ret.add(soot_field);
      }

      addToQueue(soot_class, queue);
    }

    if(ret.isEmpty()){
      throw new RuntimeException("Cannot find field: "+signature+". Are you sure all dependencies have been added to the input jar?");
    } else {
      return ret;
    }
  }

  public SootMethod findMethod(String signature){
    MethodSignatureUtil util = new MethodSignatureUtil();
    util.parse(signature);
    util.quote();

    String subsig = util.getSubSignature();
    String start_class = util.getClassName();

    LinkedList<String> queue = new LinkedList<String>();
    queue.add(start_class);

    while(queue.isEmpty() == false){
      String class_name = queue.removeFirst();

      SootClass soot_class = Scene.v().getSootClass(class_name);

      if(soot_class.declaresMethod(subsig)){
        SootMethod soot_method = soot_class.getMethod(subsig);
        return soot_method;
      }

      addToQueue(soot_class, queue);
    }

    throw new RuntimeException("Cannot find method: "+signature+". Are you sure all dependencies have been added to the input jar?");
  }

  private void addToQueue(SootClass soot_class, List<String> queue){
    if(soot_class.hasSuperclass()){
      SootClass super_class = soot_class.getSuperclass();
      queue.add(super_class.getName());
    }

    if(soot_class.hasOuterClass()){
      SootClass outer_class = soot_class.getOuterClass();
      queue.add(outer_class.getName());
    }

    Iterator<SootClass> iter = soot_class.getInterfaces().iterator();
    while(iter.hasNext()){
      SootClass curr = iter.next();
      queue.add(curr.getName());
    }
  }
}
