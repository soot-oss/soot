package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.Hashtable;
import java.util.Map;

import soot.Unit;

/**
 * This class must be extended by Attributes that can be emitted in Jasmin. The attributes must format their data in Base64
 * and if Unit references they may contain must be emitted as labels embedded and escaped in the attribute's Base64 data
 * stream at the location where the value of their pc is to occur. For example:
 *
 * <pre>
 *
    aload_1
    iload_2
    label2:
    iaload
 label3:
    iastore
    iinc 2 1
    label0:
    iload_2
    aload_0
    arraylength
 label4:
   if_icmplt label1
   return
 .code_attribute ArrayCheckAttribute "%label2%Aw==%label3%Ag==%label4%Ag=="
 *
 * </pre>
 *
 */

public abstract class JasminAttribute implements Attribute {
  abstract public byte[] decode(String attr, Hashtable<String, Integer> labelToPc);

  abstract public String getJasminValue(Map<Unit, String> instToLabel);
}
