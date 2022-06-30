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

  private ArrayList<ColorAttribute> colors;
  private ArrayList<StringAttribute> texts;
  private ArrayList<LinkAttribute> links;
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
    ArrayList<ColorAttribute> colors = this.colors;
    if (colors == null) {
      this.colors = colors = new ArrayList<ColorAttribute>();
    }
    colors.add(ca);
  }

  public void addText(StringAttribute s) {
    ArrayList<StringAttribute> texts = this.texts;
    if (texts == null) {
      this.texts = texts = new ArrayList<StringAttribute>();
    }
    texts.add(s);
  }

  public void addLink(LinkAttribute la) {
    ArrayList<LinkAttribute> links = this.links;
    if (links == null) {
      this.links = links = new ArrayList<LinkAttribute>();
    }
    links.add(la);
  }

  public int jimpleStartPos() {
    return this.jimpleStartPos;
  }

  public void jimpleStartPos(int x) {
    this.jimpleStartPos = x;
  }

  public int jimpleEndPos() {
    return this.jimpleEndPos;
  }

  public void jimpleEndPos(int x) {
    this.jimpleEndPos = x;
  }

  public int javaStartPos() {
    return this.javaStartPos;
  }

  public void javaStartPos(int x) {
    this.javaStartPos = x;
  }

  public int javaEndPos() {
    return this.javaEndPos;
  }

  public void javaEndPos(int x) {
    this.javaEndPos = x;
  }

  public int jimpleStartLn() {
    return this.jimpleStartLn;
  }

  public void jimpleStartLn(int x) {
    this.jimpleStartLn = x;
  }

  public int jimpleEndLn() {
    return this.jimpleEndLn;
  }

  public void jimpleEndLn(int x) {
    this.jimpleEndLn = x;
  }

  public int javaStartLn() {
    return this.javaStartLn;
  }

  public void javaStartLn(int x) {
    this.javaStartLn = x;
  }

  public int javaEndLn() {
    return this.javaEndLn;
  }

  public void javaEndLn(int x) {
    this.javaEndLn = x;
  }

  public boolean hasColor() {
    return this.colors != null;
  }

  public void addTag(Tag t) {
    if (t instanceof LineNumberTag) {
      int lnNum = ((LineNumberTag) t).getLineNumber();
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
      addLink(new LinkAttribute(lt.getInfo(), getJimpleLnOfHost(h), getJavaLnOfHost(h), lt.getClassName(),
          lt.getAnalysisType()));
    } else if (t instanceof StringTag) {
      StringTag st = (StringTag) t;
      addText(new StringAttribute(formatForXML(st.getInfo()), st.getAnalysisType()));
    } else if (t instanceof PositionTag) {
      PositionTag pt = (PositionTag) t;
      jimpleStartPos(pt.getStartOffset());
      jimpleEndPos(pt.getEndOffset());
    } else if (t instanceof ColorTag) {
      ColorTag ct = (ColorTag) t;
      addColor(new ColorAttribute(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground(), ct.getAnalysisType()));
    }
    /*
     * else if (t instanceof SourcePositionTag){ } else if (t instanceof SourceLineNumberTag){ }
     */
    else {
      addText(new StringAttribute(t.toString(), t.getName()));
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
    for (Tag t : h.getTags()) {
      if (t instanceof SourceLnPosTag) {
        return ((SourceLnPosTag) t).startLn();
      } else if (t instanceof LineNumberTag) {
        return ((LineNumberTag) t).getLineNumber();
      }
    }
    return 0;
  }

  private int getJimpleLnOfHost(Host h) {
    for (Tag t : h.getTags()) {
      if (t instanceof JimpleLineNumberTag) {
        return ((JimpleLineNumberTag) t).getStartLineNumber();
      }
    }
    return 0;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<srcPos sline=\"").append(javaStartLn());
    sb.append("\" eline=\"").append(javaEndLn());
    sb.append("\" spos=\"").append(javaStartPos());
    sb.append("\" epos=\"").append(javaEndPos()).append("\"/>");
    sb.append("<jmpPos sline=\"").append(jimpleStartLn());
    sb.append("\" eline=\"").append(jimpleEndLn());
    sb.append("\" spos=\"").append(jimpleStartPos());
    sb.append("\" epos=\"").append(jimpleEndPos()).append("\"/>");
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
      for (ColorAttribute ca : colors) {
        writerOut.println("<color r=\"" + ca.red() + "\" g=\"" + ca.green() + "\" b=\"" + ca.blue() + "\" fg=\"" + ca.fg()
            + "\" aType=\"" + ca.analysisType() + "\"/>");
      }
    }
    if (texts != null) {
      for (StringAttribute sa : texts) {
        writerOut.println("<text info=\"" + sa.info() + "\" aType=\"" + sa.analysisType() + "\"/>");
      }
    }
    if (links != null) {
      for (LinkAttribute la : links) {
        writerOut.println("<link label=\"" + formatForXML(la.info()) + "\" jmpLink=\"" + la.jimpleLink() + "\" srcLink=\""
            + la.javaLink() + "\" clssNm=\"" + la.className() + "\" aType=\"" + la.analysisType() + "\"/>");
      }
    }
    writerOut.println("</attribute>");
  }
}
