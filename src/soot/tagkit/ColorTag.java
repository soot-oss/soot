/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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


package soot.tagkit;

public class ColorTag implements Tag
{
    /* it is a value representing red. */
    private int red;
    /* it is a value representing green. */
    private int green;
    /* it is a value representing blue. */
    private int blue;
    
    public ColorTag(int r, int g, int b)
    {
		red = r;
		green = g;
		blue = b;
    }

	public int getRed(){
		return red;
	}

	public int getGreen(){
		return green;
	}

	public int getBlue(){
		return blue;
	}
    
    public String getName()
    {
		return "ColorTag";
    }

    public byte[] getValue()
    {
	byte[] v = new byte[2];
	return v;
    }

    public String toString()
    {
   	return ""+red+" "+green+" "+blue;
    }

}
