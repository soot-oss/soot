/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import java.util.*;
import javax.swing.*;


/** Represents a tag; these get attached to implementations of Host.
 */
public abstract  class CodeAttribute implements Tag
{
    public byte[] pc = new byte[2];
    
    public byte[] getPc()
    {
	return pc;
    }
    
    public void setPc(byte[] aPc)
    {
	pc = aPc;
    }

    public int getPcAsInt()
    {	
	int lower = pc[1];
	int upper = pc[0];

	if(lower < 0 ) lower += 255;
	if(upper < 0) upper += 255;
	int pc = (upper<<8) + lower;

	return pc;
    }

    public void setPc(int aPc)
    {
	pc =  convertPcToByteArray(aPc);
    }

    public void setPc(byte a, byte b)
    {
	pc[0] = a;
	pc[1] = b;
    }    

    public static byte[] convertPcToByteArray(int pcValue)
    {
	byte[] pc = new byte[2];
	pc[1] = (byte)( pcValue & 0x000000ff);
	pc[0] = (byte) (pcValue >> 8);
	return pc;
    }
}



