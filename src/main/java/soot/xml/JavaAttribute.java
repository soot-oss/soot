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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import soot.tagkit.ColorTag;
import soot.tagkit.Host;
import soot.tagkit.JimpleLineNumberTag;
import soot.tagkit.LineNumberTag;
import soot.tagkit.LinkTag;
import soot.tagkit.PositionTag;
import soot.tagkit.SourceLnPosTag;
import soot.tagkit.SourcePositionTag;
import soot.tagkit.StringTag;
import soot.tagkit.Tag;

public class JavaAttribute {

    private int startLn;
    private ArrayList<Tag> tags;
    private ArrayList<PosColorAttribute> vbAttrs;
    public PrintWriter writerOut;
    
    public JavaAttribute(){
    }
    
    public int startLn(){
        return startLn;
    }


    public void startLn(int x){
        startLn = x;
    }


    public ArrayList<Tag> tags(){
        return tags;
    }

    public ArrayList<PosColorAttribute> vbAttrs(){
        return vbAttrs;
    }
    
    public void addTag(Tag t){
        if (tags == null){
            tags = new ArrayList<Tag>();
        }
        tags.add(t);
    }

    public void addVbAttr(PosColorAttribute vbAttr){
        if (vbAttrs == null) {
            vbAttrs = new ArrayList<PosColorAttribute>();
        }
        vbAttrs.add(vbAttr);
    }

    public boolean hasColorTag(){
        if (tags != null){
            Iterator<Tag> it = tags.iterator();
            while (it.hasNext()){
                Tag t = it.next();
                if (t instanceof ColorTag) return true;
            }
        }
        if (vbAttrs != null){
            Iterator<PosColorAttribute> vbIt = vbAttrs.iterator();
            while (vbIt.hasNext()){
                PosColorAttribute t = vbIt.next();
                if (t.hasColor()) return true;
            }
        }
        return false;
    }
	
    private void printAttributeTag(Tag t) {
		if (t instanceof LineNumberTag) {
            int lnNum = (new Integer(((LineNumberTag)t).toString())).intValue();
			printJavaLnAttr(lnNum, lnNum);
		}
		else if (t instanceof JimpleLineNumberTag) {
            JimpleLineNumberTag jlnTag = (JimpleLineNumberTag)t;
			printJimpleLnAttr(jlnTag.getStartLineNumber(), jlnTag.getEndLineNumber());
		}
		/*else if (t instanceof SourceLineNumberTag) {
            SourceLineNumberTag jlnTag = (SourceLineNumberTag)t; 
			printJavaLnAttr(jlnTag.getStartLineNumber(), jlnTag.getEndLineNumber());
		}*/
		else if (t instanceof LinkTag) {
			LinkTag lt = (LinkTag)t;
			Host h = lt.getLink();
			printLinkAttr(formatForXML(lt.toString()), getJimpleLnOfHost(h), getJavaLnOfHost(h), lt.getClassName());

		}
		else if (t instanceof StringTag) {
			printTextAttr(formatForXML(((StringTag)t).toString()));
		}
		else if (t instanceof SourcePositionTag){
			SourcePositionTag pt = (SourcePositionTag)t;
			printSourcePositionAttr(pt.getStartOffset(), pt.getEndOffset());
		}
        else if (t instanceof PositionTag){
			PositionTag pt = (PositionTag)t;
			printJimplePositionAttr(pt.getStartOffset(), pt.getEndOffset());
		}
		else if (t instanceof ColorTag){
			ColorTag ct = (ColorTag)t;
			printColorAttr(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground());
		}
		else {
			printTextAttr(t.toString());
		}
    }
	
    private void printJavaLnAttr(int start_ln, int end_ln){
		writerOut.println("<javaStartLn>"+start_ln+"</javaStartLn>");
		writerOut.println("<javaEndLn>"+end_ln+"</javaEndLn>");
	}


	private void printJimpleLnAttr(int start_ln, int end_ln){
		writerOut.println("<jimpleStartLn>"+start_ln+"</jimpleStartLn>");
		writerOut.println("<jimpleEndLn>"+end_ln+"</jimpleEndLn>");
	}

	private void printTextAttr(String text){
		writerOut.println("<text>"+text+"</text>");
	}
	
	private void printLinkAttr(String label, int jimpleLink, int javaLink, String className){
		writerOut.println("<link_attribute>");
		writerOut.println("<link_label>"+label+"</link_label>");
		writerOut.println("<jimple_link>"+jimpleLink+"</jimple_link>");
		writerOut.println("<java_link>"+javaLink+"</java_link>");
		writerOut.println("<className>"+className+"</className>");
		writerOut.println("</link_attribute>");
	}
    
	private void startPrintValBoxAttr(){
		writerOut.println("<value_box_attribute>");
	}

	private void printSourcePositionAttr(int start, int end){
		writerOut.println("<javaStartPos>"+start+"</javaStartPos>");
		writerOut.println("<javaEndPos>"+end+"</javaEndPos>");
	}
    
	private void printJimplePositionAttr(int start, int end){
		writerOut.println("<jimpleStartPos>"+start+"</jimpleStartPos>");
		writerOut.println("<jimpleEndPos>"+end+"</jimpleEndPos>");
	}
	
	private void printColorAttr(int r, int g, int b, boolean fg){
		writerOut.println("<red>"+r+"</red>");
		writerOut.println("<green>"+g+"</green>");
		writerOut.println("<blue>"+b+"</blue>");
        if (fg) {
		    writerOut.println("<fg>1</fg>");
        }
        else {
		    writerOut.println("<fg>0</fg>");    
        }
	}
	
    private void endPrintValBoxAttr(){
		writerOut.println("</value_box_attribute>");
	}

    // prints all tags
	public void printAllTags(PrintWriter writer){
        this.writerOut = writer;
        if (tags != null) {
            Iterator<Tag> it = tags.iterator();
            while (it.hasNext()){
                printAttributeTag(it.next());
            }
        }
        if (vbAttrs != null) {
            Iterator<PosColorAttribute> vbIt = vbAttrs.iterator();
            while (vbIt.hasNext()){
                PosColorAttribute attr = vbIt.next();
                if (attr.hasColor()){
                    startPrintValBoxAttr();
                    printSourcePositionAttr(attr.javaStartPos(), attr.javaEndPos());
                    printJimplePositionAttr(attr.jimpleStartPos(), attr.jimpleEndPos());
                    //printColorAttr(attr.color().red(), attr.color().green(), attr.color().blue(), attr.color().fg());
                    endPrintValBoxAttr();
                }
            }
        }
    }

    // prints only tags related to strings and links (no pos tags)
    public void printInfoTags(PrintWriter writer){
        this.writerOut = writer;
        Iterator<Tag> it = tags.iterator();
        while (it.hasNext()){
            printAttributeTag(it.next());
        }
    }
    
    private String  formatForXML(String in) {
        in = in.replaceAll( "<", "&lt;");
        in = in.replaceAll(">", "&gt;");
        in = in.replaceAll("&", "&amp;");
        return in;
    }
	
    private int getJavaLnOfHost(Host h){
		Iterator<Tag> it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = it.next();
			//G.v().out.println(t.getClass().toString());
			if (t instanceof SourceLnPosTag) {
				//G.v().out.println("t is LineNumberTag");
				return ((SourceLnPosTag)t).startLn();
			}
            else if (t instanceof LineNumberTag){
                return (new Integer(((LineNumberTag)t).toString())).intValue();
            }
		}
		return 0;
	}
	
	private int getJimpleLnOfHost(Host h){
		Iterator<Tag> it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = it.next();
			if (t instanceof JimpleLineNumberTag) {
				return ((JimpleLineNumberTag)t).getStartLineNumber();
			}
		}
		return 0;
    }    
}   
