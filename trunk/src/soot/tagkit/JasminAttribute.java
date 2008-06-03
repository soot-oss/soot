/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville and Feng Qian
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.tagkit;

import java.util.*;



/**
 *  This class  must be extended  by Attributes that can 
 *  be emitted in Jasmin. The attributes must format their data
 *  in Base64 and if Unit references they may contain must be emitted as
 *  labels embedded and
 *  escaped in the attribute's Base64 data stream at the location where the value
 *  of their pc is to occur. For example:
<pre> 
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

</pre>
 * 
 */


public abstract class JasminAttribute implements Attribute
{
    abstract public byte[] decode(String attr, Hashtable labelToPc);
    
    abstract public String getJasminValue(Map instToLabel);
}
