package soot.xml;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
import soot.tagkit.StringTag;
import soot.tagkit.Tag;

public class Attribute {

  // private ColorAttribute color;
  private ArrayList<ColorAttribute> colors;
  private int jimpleStartPos;
  private int jimpleEndPos;
  private int javaStartPos;
  private int javaEndPos;
  private int javaStartLn;
  private int javaEndLn;
  private int jimpleStartLn;
  private int jimpleEndLn;

  public ArrayList<ColorAttribute> colors() {
    return colors;
  }

  public void addColor(ColorAttribute ca) {
    if (colors == null) {
      colors = new ArrayList<ColorAttribute>();
    }
    colors.add(ca);
  }

  /*
   * public ColorAttribute color(){ return color; }
   *
   * public void color(ColorAttribute c){ color = c; }
   */

  public int jimpleStartPos() {
    return jimpleStartPos;
  }

  public void jimpleStartPos(int x) {
    jimpleStartPos = x;
  }

  public int jimpleEndPos() {
    return jimpleEndPos;
  }

  public void jimpleEndPos(int x) {
    jimpleEndPos = x;
  }

  public int javaStartPos() {
    return javaStartPos;
  }

  public void javaStartPos(int x) {
    javaStartPos = x;
  }

  public int javaEndPos() {
    return javaEndPos;
  }

  public void javaEndPos(int x) {
    javaEndPos = x;
  }

  public int jimpleStartLn() {
    return jimpleStartLn;
  }

  public void jimpleStartLn(int x) {
    jimpleStartLn = x;
  }

  public int jimpleEndLn() {
    return jimpleEndLn;
  }

  public void jimpleEndLn(int x) {
    jimpleEndLn = x;
  }

  public int javaStartLn() {
    return javaStartLn;
  }

  public void javaStartLn(int x) {
    javaStartLn = x;
  }

  public int javaEndLn() {
    return javaEndLn;
  }

  public void javaEndLn(int x) {
    javaEndLn = x;
  }

  public boolean hasColor() {
    if (colors != null) {
      return true;
    } else {
      return false;
    }
  }

  ArrayList<StringAttribute> texts;
  ArrayList<LinkAttribute> links;

  public void addText(StringAttribute s) {
    if (texts == null) {
      texts = new ArrayList<StringAttribute>();
    }
    texts.add(s);
  }

  public void addLink(LinkAttribute la) {
    if (links == null) {
      links = new ArrayList<LinkAttribute>();
    }
    links.add(la);
  }

  public void addTag(Tag t) {
    if (t instanceof LineNumberTag) {
      int lnNum = (new Integer(((LineNumberTag) t).toString())).intValue();
      javaStartLn(lnNum);
      javaEndLn(lnNum);
    } else if (t instanceof JimpleLineNumberTag) {
      JimpleLineNumberTag jlnTag = (JimpleLineNumberTag) t;
      jimpleStartLn(jlnTag.getStartLineNumber());
      jimpleEndLn(jlnTag.getEndLineNumber());
    } else if (t instanceof SourceLnPosTag) {
      SourceLnPosTag jlnTag = (SourceLnPosTag) t;
      javaStartLn(jlnTag.startLn());
      javaEndLn(jlnTag.endLn());
      javaStartPos(jlnTag.startPos());
      javaEndPos(jlnTag.endPos());
    } else if (t instanceof LinkTag) {
      LinkTag lt = (LinkTag) t;
      Host h = lt.getLink();
      LinkAttribute link = new LinkAttribute(lt.getInfo(), getJimpleLnOfHost(h), getJavaLnOfHost(h), lt.getClassName(),
          lt.getAnalysisType());
      addLink(link);

    } else if (t instanceof StringTag) {
      StringTag st = (StringTag) t;
      StringAttribute string = new StringAttribute(formatForXML(st.getInfo()), st.getAnalysisType());
      addText(string);
    } else if (t instanceof PositionTag) {
      PositionTag pt = (PositionTag) t;
      jimpleStartPos(pt.getStartOffset());
      jimpleEndPos(pt.getEndOffset());
    } else if (t instanceof ColorTag) {
      ColorTag ct = (ColorTag) t;
      ColorAttribute ca
          = new ColorAttribute(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground(), ct.getAnalysisType());
      // color(ca);
      addColor(ca);
    }
    /*
     * else if (t instanceof SourcePositionTag){ } else if (t instanceof SourceLineNumberTag){ }
     */
    else {
      // System.out.println("t is: "+t.getClass());
      StringAttribute sa = new StringAttribute(t.toString(), t.getName());
      addText(sa);
    }

  }

  private String formatForXML(String in) {
    in = in.replaceAll("<", "&lt;");
    in = in.replaceAll(">", "&gt;");
    in = in.replaceAll("&", "&amp;");
    in = in.replaceAll("\"", "&quot;");
    return in;
  }

  private int getJavaLnOfHost(Host h) {
    Iterator<Tag> it = h.getTags().iterator();
    while (it.hasNext()) {
      Tag t = it.next();
      if (t instanceof SourceLnPosTag) {
        return ((SourceLnPosTag) t).startLn();
      } else if (t instanceof LineNumberTag) {
        return (new Integer(((LineNumberTag) t).toString())).intValue();
      }
    }
    return 0;
  }

  private int getJimpleLnOfHost(Host h) {
    Iterator<Tag> it = h.getTags().iterator();
    while (it.hasNext()) {
      Tag t = it.next();
      if (t instanceof JimpleLineNumberTag) {
        return ((JimpleLineNumberTag) t).getStartLineNumber();
      }
    }
    return 0;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("<srcPos sline=\"" + javaStartLn() + "\" eline=\"" + javaEndLn() + "\" spos=\"" + javaStartPos() + "\" epos=\""
        + javaEndPos() + "\"/>");
    sb.append("<jmpPos sline=\"" + jimpleStartLn() + "\" eline=\"" + jimpleEndLn() + "\" spos=\"" + jimpleStartPos()
        + "\" epos=\"" + jimpleEndPos() + "\"/>");
    return sb.toString();
  }

  public boolean isEmpty() {
    return colors == null && texts == null && links == null;
  }

  public void print(PrintWriter writerOut) {
    if (isEmpty()) {
      // System.out.println("no data found for: ");
      // System.out.println("<srcPos sline=\""+javaStartLn()+"\" eline=\""+javaEndLn()+"\" spos=\""+javaStartPos()+"\"
      // epos=\""+javaEndPos()+"\"/>");
      // System.out.println("<jmpPos sline=\""+jimpleStartLn()+"\" eline=\""+jimpleEndLn()+"\" spos=\""+jimpleStartPos()+"\"
      // epos=\""+jimpleEndPos()+"\"/>");
      return;
    }
    writerOut.println("<attribute>");
    writerOut.println("<srcPos sline=\"" + javaStartLn() + "\" eline=\"" + javaEndLn() + "\" spos=\"" + javaStartPos()
        + "\" epos=\"" + javaEndPos() + "\"/>");
    writerOut.println("<jmpPos sline=\"" + jimpleStartLn() + "\" eline=\"" + jimpleEndLn() + "\" spos=\"" + jimpleStartPos()
        + "\" epos=\"" + jimpleEndPos() + "\"/>");
    if (colors != null) {
      Iterator<ColorAttribute> cIt = colors.iterator();
      while (cIt.hasNext()) {
        ColorAttribute ca = cIt.next();
        writerOut.println("<color r=\"" + ca.red() + "\" g=\"" + ca.green() + "\" b=\"" + ca.blue() + "\" fg=\"" + ca.fg()
            + "\" aType=\"" + ca.analysisType() + "\"/>");
      }
    }
    if (texts != null) {
      Iterator<StringAttribute> textsIt = texts.iterator();
      while (textsIt.hasNext()) {
        StringAttribute sa = textsIt.next();
        writerOut.println("<text info=\"" + sa.info() + "\" aType=\"" + sa.analysisType() + "\"/>");
      }
    }
    if (links != null) {
      Iterator<LinkAttribute> linksIt = links.iterator();
      while (linksIt.hasNext()) {
        LinkAttribute la = linksIt.next();
        writerOut.println("<link label=\"" + formatForXML(la.info()) + "\" jmpLink=\"" + la.jimpleLink() + "\" srcLink=\""
            + la.javaLink() + "\" clssNm=\"" + la.className() + "\" aType=\"" + la.analysisType() + "\"/>");
      }
    }
    writerOut.println("</attribute>");
  }
}
