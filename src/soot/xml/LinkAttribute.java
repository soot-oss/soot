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

package soot.xml;

import soot.*;    

public class LinkAttribute {
    
    private String info;
    private int jimpleLink;
    private int javaLink;
    private String className;
    private boolean isJimpleLink;
    private boolean isJavaLink;
    private String analysisType;
    
    public LinkAttribute(String info, int jimpleLink, int javaLink, String className, String type){
        this.info = info;
        this.jimpleLink = jimpleLink;
        this.javaLink = javaLink;
        this.className = className; 
        isJimpleLink = true;
        isJavaLink = true;
        analysisType = type;
    }

    public String info(){
        return info;
    }

    public int jimpleLink(){
        return jimpleLink;
    }

    public int javaLink(){
        return javaLink;
    }

    public String className(){
        return className;
    }

    public boolean isJimpleLink(){
        return isJimpleLink;
    }

    public boolean isJavaLink(){
        return isJavaLink;
    }

    public String analysisType(){
        return analysisType;
    }
}   

