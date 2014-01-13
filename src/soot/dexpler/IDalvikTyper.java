// 
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for 
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>. 
//

package soot.dexpler;

import soot.Body;
import soot.Type;
import soot.ValueBox;

public interface IDalvikTyper {
  
  public static boolean ENABLE_DVKTYPER = false;
  public static boolean DEBUG = false;
  
  public abstract void setType(ValueBox v, Type type, boolean isUse);
  //public abstract void setObjectType(ValueBox v);
  public abstract void addConstraint(ValueBox box1, ValueBox box2);
  //public abstract void addStrongConstraint(ValueBox vb, Type t);
  abstract void assignType(Body b);
  //public static IDalvikTyper getDvkTyper(); 
  //public Stmt captureAssign(JAssignStmt stmt, int current);
}
