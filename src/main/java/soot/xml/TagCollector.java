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

import soot.Body;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.spark.ondemand.genericutil.Predicate;
import soot.tagkit.Host;
import soot.tagkit.JimpleLineNumberTag;
import soot.tagkit.KeyTag;
import soot.tagkit.SourceFileTag;
import soot.tagkit.Tag;

public class TagCollector {

  private final ArrayList<Attribute> attributes;
  private final ArrayList<Key> keys;

  public TagCollector() {
    attributes = new ArrayList<Attribute>();
    keys = new ArrayList<Key>();
  }

  public boolean isEmpty() {
    return attributes.isEmpty() && keys.isEmpty();
  }

  /** Convenience function for <code>collectTags(sc, true)</code>. */
  public void collectTags(SootClass sc) {
    collectTags(sc, true);
  }

  /**
   * Collect tags from all fields and methods of <code>sc</code>. If <code>includeBodies</code> is true, then tags are also
   * collected from method bodies.
   *
   * @param sc
   *          The class from which to collect the tags.
   */
  public void collectTags(SootClass sc, boolean includeBodies) {
    // tag the class
    collectClassTags(sc);

    // tag fields
    for (SootField sf : sc.getFields()) {
      collectFieldTags(sf);
    }

    // tag methods
    for (SootMethod sm : sc.getMethods()) {
      collectMethodTags(sm);

      if (!includeBodies || !sm.hasActiveBody()) {
        continue;
      }
      Body b = sm.getActiveBody();
      collectBodyTags(b);
    }
  }

  public void collectKeyTags(SootClass sc) {
    for (Tag next : sc.getTags()) {
      if (next instanceof KeyTag) {
        KeyTag kt = (KeyTag) next;
        Key k = new Key(kt.red(), kt.green(), kt.blue(), kt.key());
        k.aType(kt.analysisType());
        keys.add(k);
      }
    }
  }

  public void printKeys(PrintWriter writerOut) {
    Iterator<Key> it = keys.iterator();
    while (it.hasNext()) {
      Key k = it.next();
      k.print(writerOut);
    }
  }

  private void addAttribute(Attribute a) {
    if (!a.isEmpty()) {
      attributes.add(a);
    }
  }

  private void collectHostTags(Host h) {
    Predicate<Tag> p = Predicate.truePred();
    collectHostTags(h, p);
  }

  private void collectHostTags(Host h, Predicate<Tag> include) {
    if (!h.getTags().isEmpty()) {
      Attribute a = new Attribute();
      for (Tag t : h.getTags()) {
        if (include.test(t)) {
          a.addTag(t);
        }
      }
      addAttribute(a);
    }
  }

  public void collectClassTags(SootClass sc) {
    // All classes are tagged with their source files which
    // is not worth outputing because it can be inferred from
    // other information (like the name of the XML file).
    Predicate<Tag> noSFTags = new Predicate<Tag>() {
      public boolean test(Tag t) {
        return !(t instanceof SourceFileTag);
      }
    };
    collectHostTags(sc, noSFTags);
  }

  public void collectFieldTags(SootField sf) {
    collectHostTags(sf);
  }

  public void collectMethodTags(SootMethod sm) {
    if (sm.hasActiveBody()) {
      collectHostTags(sm);
    }
  }

  public synchronized void collectBodyTags(Body b) {
    for (Unit u : b.getUnits()) {
      Attribute ua = new Attribute();
      JimpleLineNumberTag jlnt = null;
      for (Tag t : u.getTags()) {
        ua.addTag(t);
        if (t instanceof JimpleLineNumberTag) {
          jlnt = (JimpleLineNumberTag) t;
        }
        // System.out.println("adding unit tag: "+t);
      }
      addAttribute(ua);
      for (ValueBox vb : u.getUseAndDefBoxes()) {
        // PosColorAttribute attr = new PosColorAttribute();
        if (!vb.getTags().isEmpty()) {
          Attribute va = new Attribute();
          for (Tag t : vb.getTags()) {
            // System.out.println("adding vb tag: "+t);
            va.addTag(t);
            // System.out.println("vb: "+vb.getValue()+" tag: "+t);
            if (jlnt != null) {
              va.addTag(jlnt);
            }
          }
          // also here add line tags of the unit
          addAttribute(va);
          // System.out.println("added att: "+va);
        }
      }
    }
  }

  public void printTags(PrintWriter writerOut) {

    Iterator<Attribute> it = attributes.iterator();
    while (it.hasNext()) {
      Attribute a = it.next();
      // System.out.println("will print attr: "+a);
      a.print(writerOut);
    }
  }
}
