/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

import soot.*;

public class ColorTag implements Tag
{
    /* it is a value representing red. */
    private int red;
    /* it is a value representing green. */
    private int green;
    /* it is a value representing blue. */
    private int blue;
    /* for highlighting foreground of text default is to
     * higlight background */
    private boolean foreground = false;
    
    public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int YELLOW = 2;
	public static final int BLUE = 3;
	public static final int ORANGE = 4;
	public static final int PURPLE = 5;
	
	public ColorTag(int r, int g, int b, boolean fg)
    {
		red = r;
		green = g;
		blue = b;
        foreground = fg;
    }
    
	public ColorTag(int r, int g, int b)
    {
		red = r;
		green = g;
		blue = b;
    }

    public ColorTag(int color){
        this(color, false);
    }
    
	public ColorTag(int color, boolean fg){
		//G.v().out.println("color: "+color);
		switch (color) {
			case RED: {
				red = 255;
				green = 0;
				blue = 0;
				break;
			}
			case GREEN: {
				red = 45;
				green = 255;
				blue = 84;
				break;
			}
			case YELLOW: {
				red = 255;
				green = 248;
				blue = 35;
				break;
			}
			case BLUE: {
				red = 174;
				green = 210;
				blue = 255;
				break;
			}
			case ORANGE: {
				red = 255;
				green = 163;
				blue = 0;
				break;
			}
			case PURPLE: {
				red = 255;
				green = 210;
				blue = 255;
				break;
			}
			default: {
				red = 220;
				green = 220;
				blue = 220;
				break;
			}
		}
        foreground = fg;
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
   
    public boolean isForeground(){
        return foreground;
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
