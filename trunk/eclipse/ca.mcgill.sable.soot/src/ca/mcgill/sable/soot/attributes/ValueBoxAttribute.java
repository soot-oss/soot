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


package ca.mcgill.sable.soot.attributes;

import org.eclipse.swt.graphics.RGB;


public class ValueBoxAttribute {

	private int startOffset;
	private int endOffset;
    private int javaOffsetStart;
    private int javaOffsetEnd;
    
	private int red;
	private int green;
	private int blue;
	
	public RGB getRGBColor(){
		return new RGB(getRed(), getGreen(), getBlue());
	}
	
	/**
	 * @return
	 */
	public int getBlue() {
		return blue;
	}

	/**
	 * @return
	 */
	public int getEndOffset() {
		return endOffset;
	}

	/**
	 * @return
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * @return
	 */
	public int getRed() {
		return red;
	}

	/**
	 * @return
	 */
	public int getStartOffset() {
		return startOffset;
	}

	/**
	 * @param i
	 */
	public void setBlue(int i) {
		blue = i;
	}

	/**
	 * @param i
	 */
	public void setEndOffset(int i) {
		endOffset = i;
	}

	/**
	 * @param i
	 */
	public void setGreen(int i) {
		green = i;
	}

	/**
	 * @param i
	 */
	public void setRed(int i) {
		red = i;
	}

	/**
	 * @param i
	 */
	public void setStartOffset(int i) {
		startOffset = i;
	}

    /**
     * @return
     */
    public int getJavaOffsetEnd() {
        return javaOffsetEnd;
    }

    /**
     * @return
     */
    public int getJavaOffsetStart() {
        return javaOffsetStart;
    }

    /**
     * @param i
     */
    public void setJavaOffsetEnd(int i) {
        javaOffsetEnd = i;
    }

    /**
     * @param i
     */
    public void setJavaOffsetStart(int i) {
        javaOffsetStart = i;
    }

}
