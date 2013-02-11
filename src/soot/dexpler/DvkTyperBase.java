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

import soot.Type;
import soot.ValueBox;

public abstract class DvkTyperBase {
  public static boolean ENABLE_DVKTYPER = false;
  public abstract void setType(ValueBox v, Type type);
  public abstract void setObjectType(ValueBox v);
  public abstract void setConstraint(ValueBox box1, ValueBox box2);
  abstract void assignType();
  public static DvkTyperBase getDvkTyper() {
    // TODO Auto-generated method stub
    return null;
  }
}
