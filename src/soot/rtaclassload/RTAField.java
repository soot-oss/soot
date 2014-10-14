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

import soot.coffi.field_info;
import soot.coffi.ConstantPoolReader;

public class RTAField {

  private enum RTAFieldLevel {
    HIERARCHY,
    SIGNATURES
  }

  private int name;
  private RTAType type;
  private int accessFlags;

  private RTAClass clazz;
  private field_info fieldInfo;
  private int index;

  private RTAFieldLevel rtaFieldLevel;

  public RTAField(RTAClass clazz, field_info fieldInfo, int index){
    this.clazz = clazz;
    this.fieldInfo = fieldInfo;
    this.index = index;

    rtaFieldLevel = RTAFieldLevel.HIERARCHY;
  }

  public int getName(){
    requireSignatures();
    return name;
  }

  public RTAType getType(){
    requireSignatures();
    return type;
  }

  public int getAccessFlags(){
    requireSignatures();
    return accessFlags;
  }

  private void requireSignatures(){
    if(rtaFieldLevel == RTAFieldLevel.HIERARCHY){
      String nameStr = ConstantPoolReader.v().get(fieldInfo.name_index, clazz.getConstantPool());
      String descString = ConstantPoolReader.v().get(fieldInfo.descriptor_index, clazz.getConstantPool());
      String typeString = DescriptorParser.v().parseDesc(descString);

      name = StringNumbers.v().addString(nameStr);
      type = RTAType.create(typeString);

      accessFlags = fieldInfo.access_flags;
      rtaFieldLevel = RTAFieldLevel.SIGNATURES;
    }
  }
}
