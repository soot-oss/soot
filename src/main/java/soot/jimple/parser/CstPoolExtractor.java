package soot.jimple.parser;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrice Pominville
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

import java.util.HashSet;
import java.util.Set;

import soot.Scene;
import soot.jimple.parser.analysis.DepthFirstAdapter;
import soot.jimple.parser.node.AFullIdentClassName;
import soot.jimple.parser.node.AFullIdentNonvoidType;
import soot.jimple.parser.node.AIdentClassName;
import soot.jimple.parser.node.AIdentNonvoidType;
import soot.jimple.parser.node.AQuotedClassName;
import soot.jimple.parser.node.AQuotedNonvoidType;
import soot.jimple.parser.node.Start;
import soot.util.StringTools;

/**
 * Walks a jimple AST, extracting all the contained reference type names.
 */

class CstPoolExtractor {

  private Set<String> mRefTypes = null;
  private Start mParseTree;

  public CstPoolExtractor(Start parseTree) {
    mParseTree = parseTree;
  }

  public Set<String> getCstPool() {
    if (mRefTypes == null) {
      mRefTypes = new HashSet<String>();
      CstPoolExtractorWalker walker = new CstPoolExtractorWalker();
      mParseTree.apply(walker);
      mParseTree = null; // allow garbage collection
    }
    return mRefTypes;
  }

  private class CstPoolExtractorWalker extends DepthFirstAdapter {
    CstPoolExtractorWalker() {
    }

    public void inStart(Start node) {
      defaultIn(node);
    }

    public void outAQuotedClassName(AQuotedClassName node) {
      String tokenString = node.getQuotedName().getText();
      tokenString = tokenString.substring(1, tokenString.length() - 1);
      tokenString = StringTools.getUnEscapedStringOf(tokenString);

      mRefTypes.add(tokenString);

    }

    public void outAIdentClassName(AIdentClassName node) {
      String tokenString = node.getIdentifier().getText();
      tokenString = StringTools.getUnEscapedStringOf(tokenString);

      mRefTypes.add(tokenString);
    }

    public void outAFullIdentClassName(AFullIdentClassName node) {
      String tokenString = node.getFullIdentifier().getText();
      tokenString = Scene.v().unescapeName(tokenString);
      tokenString = StringTools.getUnEscapedStringOf(tokenString);

      mRefTypes.add(tokenString);
    }

    public void outAQuotedNonvoidType(AQuotedNonvoidType node) {
      String tokenString = node.getQuotedName().getText();
      tokenString = tokenString.substring(1, tokenString.length() - 1);
      tokenString = StringTools.getUnEscapedStringOf(tokenString);

      mRefTypes.add(tokenString);
    }

    public void outAFullIdentNonvoidType(AFullIdentNonvoidType node) {
      String tokenString = node.getFullIdentifier().getText();
      tokenString = Scene.v().unescapeName(tokenString);
      tokenString = StringTools.getUnEscapedStringOf(tokenString);

      mRefTypes.add(tokenString);
    }

    public void outAIdentNonvoidType(AIdentNonvoidType node) {
      String tokenString = node.getIdentifier().getText();
      tokenString = StringTools.getUnEscapedStringOf(tokenString);

      mRefTypes.add(tokenString);
    }
  }
}
