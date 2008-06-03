/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.tagkit;

public class KeyTag implements Tag {

    private int red;
    private int green;
    private int blue;
    private String key;
    private String analysisType;
    
    /*public KeyTag(int r, int g, int b, String k){
        this(r, g, b, k, "Unknown");
    }*/

    public KeyTag(int r, int g, int b, String k, String type){
        red = r;
        green = g;
        blue = b;
        key = k;
        analysisType = type;
    }

    public KeyTag(int color, String k, String type){
        this(color, k);
        analysisType = type;
    }

    public KeyTag(int color, String k){
        switch(color){
			case ColorTag.RED: {
				red = 255;
				green = 0;
				blue = 0;
				break;
			}
			case ColorTag.GREEN: {
				red = 45;
				green = 255;
				blue = 84;
				break;
			}
			case ColorTag.YELLOW: {
				red = 255;
				green = 248;
				blue = 35;
				break;
			}
			case ColorTag.BLUE: {
				red = 174;
				green = 210;
				blue = 255;
				break;
			}
			case ColorTag.ORANGE: {
				red = 255;
				green = 163;
				blue = 0;
				break;
			}
			case ColorTag.PURPLE: {
				red = 159;
				green = 34;
				blue = 193;
				break;
			}
			default: {
				red = 220;
				green = 220;
                blue = 220;
                break;
            }
                     
        }
        key = k;
    }

    public int red(){
        return red;
    }

    public int green(){
        return green;
    }

    public int blue() {
        return blue;
    }

    public String key(){
        return key;
    }

    public String analysisType(){
        return analysisType;
    }
    
    public String getName(){
        return "KeyTag";
    }

    public byte[] getValue() {
        byte[] v = new byte[4];
        return v;
    }
    
}   
