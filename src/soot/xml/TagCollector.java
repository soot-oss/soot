package soot.xml;

import soot.*;
import soot.tagkit.*;
import java.util.*;
import java.io.*;

public class TagCollector {

    private HashMap javaTags;
    
    public TagCollector(){
        javaTags = new HashMap();
    }

    public void collectTags(SootClass sc){
	
        int javaLn = 0;
        
        // tag fields
        Iterator fit = sc.getFields().iterator();
		while (fit.hasNext()){
            SootField sf = (SootField)fit.next();
            Iterator fTags = sf.getTags().iterator();
            javaLn = getJavaLnOfHost(sf);
            JavaAttribute ja;
            if (javaTags.containsKey(new Integer(javaLn))){
                ja = (JavaAttribute)javaTags.get(new Integer(javaLn));
            }
            else {
                ja = new JavaAttribute();
            }
            while (fTags.hasNext()){
                Tag t = (Tag)fTags.next();
                ja.addTag(t);
            }
            javaTags.put(new Integer(javaLn), ja);
        }
        
        // tag methods
        Iterator it = sc.getMethods().iterator();
		while (it.hasNext()) {
			SootMethod sm = (SootMethod)it.next();
			if (!sm.hasActiveBody()) {
				continue;
			}
			if (!sm.getTags().isEmpty()){
				Iterator mTags = sm.getTags().iterator();
                javaLn = getJavaLnOfHost(sm);
                JavaAttribute ja;
                if (javaTags.containsKey(new Integer(javaLn))){
                    ja = (JavaAttribute)javaTags.get(new Integer(javaLn));
                }
                else {
                    ja = new JavaAttribute();
                }
				while (mTags.hasNext()){
					Tag t = (Tag)mTags.next();
					ja.addTag(t);
				}
                javaTags.put(new Integer(javaLn), ja);
			}
			
			Body b = sm.getActiveBody();
			Iterator itUnits = b.getUnits().iterator();
			while (itUnits.hasNext()) {
				Unit u = (Unit)itUnits.next();
				Iterator itTags = u.getTags().iterator();
                javaLn = getJavaLnOfHost(u);
                JavaAttribute ja;
                if (javaTags.containsKey(new Integer(javaLn))){
                    ja = (JavaAttribute)javaTags.get(new Integer(javaLn));
                }
                else {
                    ja = new JavaAttribute();
                }
				while (itTags.hasNext()) {
			   		Tag t = (Tag)itTags.next();
				    ja.addTag(t);
                }
                javaTags.put(new Integer(javaLn), ja);
				Iterator valBoxIt = u.getUseAndDefBoxes().iterator();
				while (valBoxIt.hasNext()){
					ValueBox vb = (ValueBox)valBoxIt.next();
                    PosColorAttribute attr = new PosColorAttribute();
					if (!vb.getTags().isEmpty()){
						Iterator tagsIt = vb.getTags().iterator(); 
                        javaLn = getJavaLnOfHost(u);
                        JavaAttribute vja;
                        if (javaTags.containsKey(new Integer(javaLn))){
                            vja = (JavaAttribute)javaTags.get(new Integer(javaLn));
                        }
                        else {
                            vja = new JavaAttribute();
                        }
						while (tagsIt.hasNext()) {
							Tag t = (Tag)tagsIt.next();
						    handleVbTag(attr, t);
                        }
                        vja.addVbAttr(attr);
                        javaTags.put(new Integer(javaLn), vja);
                    }
                    
                }
            }
        }
    }

    private void handleVbTag(PosColorAttribute attr, Tag t){
		if (t instanceof LineNumberTag) {
            int lnNum = (new Integer(((LineNumberTag)t).toString())).intValue();
            attr.javaStartLn(lnNum);
            attr.javaEndLn(lnNum);
		}
		else if (t instanceof JimpleLineNumberTag) {
            JimpleLineNumberTag jlnTag = (JimpleLineNumberTag)t;
		    attr.jimpleStartLn(jlnTag.getStartLineNumber());
            attr.jimpleEndLn(jlnTag.getEndLineNumber());
		}
		else if (t instanceof SourceLineNumberTag) {
            SourceLineNumberTag jlnTag = (SourceLineNumberTag)t; 
			attr.javaStartLn(jlnTag.getStartLineNumber());
            attr.javaEndLn(jlnTag.getEndLineNumber());
		}
		else if (t instanceof SourcePositionTag){
			SourcePositionTag pt = (SourcePositionTag)t;
            attr.javaStartPos(pt.getStartOffset());
            attr.javaEndPos(pt.getEndOffset());
		}
        else if (t instanceof PositionTag){
			PositionTag pt = (PositionTag)t;
            attr.jimpleStartPos(pt.getStartOffset());
            attr.jimpleEndPos(pt.getEndOffset());
		}
		else if (t instanceof ColorTag){
			ColorTag ct = (ColorTag)t;
            ColorAttribute ca = new ColorAttribute(ct.getRed(), ct.getGreen(), ct.getBlue(), ct.isForeground());
            attr.color(ca);
		}
								
	}
	
    private int getJavaLnOfHost(Host h){
		Iterator it = h.getTags().iterator();
		while (it.hasNext()){
			Tag t = (Tag)it.next();
			if (t instanceof SourceLineNumberTag) {
				return ((SourceLineNumberTag)t).getStartLineNumber();
			}
            else if (t instanceof LineNumberTag){
                return (new Integer(((LineNumberTag)t).toString())).intValue();
            }
		}
		return 0;
	}

    public void printTags(PrintWriter writerOut){
        Iterator keysIt = javaTags.keySet().iterator();
        while (keysIt.hasNext()){
            JavaAttribute ja = (JavaAttribute)javaTags.get(keysIt.next());
            if (ja.hasColorTag()) {
                writerOut.println("<attribute>");
                ja.printAllTags(writerOut);
                writerOut.println("</attribute>");
            }
            else if (ja.hasInfoTag()){
                writerOut.println("<attribute>");
                ja.printInfoTags(writerOut);
                writerOut.println("</attribute>");
            }
            else {
                // don't print anything!
            }
        }
    }
}
