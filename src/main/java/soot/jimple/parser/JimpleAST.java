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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.HashMap;
import java.util.Set;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootResolver;
import soot.jimple.JimpleBody;
import soot.jimple.parser.lexer.Lexer;
import soot.jimple.parser.lexer.LexerException;
import soot.jimple.parser.node.Start;
import soot.jimple.parser.parser.Parser;
import soot.jimple.parser.parser.ParserException;

/**
 * This class encapsulates a JimpleAST instance and provides methods to act on it.
 */
public class JimpleAST {
  private Start mTree = null;
  private HashMap<SootMethod, JimpleBody> methodToParsedBodyMap = null;

  /**
   * Constructs a JimpleAST and generates its parse tree from the given InputStream.
   *
   * @param aJIS
   *          The InputStream to parse.
   */
  public JimpleAST(InputStream aJIS) throws ParserException, LexerException, IOException {
    Parser p = new Parser(new Lexer(new PushbackReader(new BufferedReader(new InputStreamReader(aJIS)), 1024)));
    mTree = p.parse();
  }

  /**
   * Reads an entire class from jimple, creates the Soot objects & returns it.
   */
  public SootClass createSootClass() {
    Walker w = new Walker(SootResolver.v());
    mTree.apply(w);
    return w.getSootClass();
  }

  /**
   * Applies a SkeletonExtractorWalker to the given SootClass, using the given Resolver to resolve the reference types it
   * contains. The given SootClass instance will be filled to contain a class skeleton: that is no Body instances will be
   * created for the class' methods.
   *
   * @param sc
   *          a SootClass to fill in.
   */
  public void getSkeleton(SootClass sc) {
    Walker w = new SkeletonExtractorWalker(SootResolver.v(), sc);
    mTree.apply(w);
  }

  /**
   * Returns a body corresponding to the parsed jimple for m. If necessary, applies the BodyExtractorWalker to initialize the
   * bodies map.
   *
   * @param m
   *          the method we want to get a body for.
   * @return the actual body for the given method.
   */
  public Body getBody(SootMethod m) {
    if (methodToParsedBodyMap == null) {
      synchronized (this) {
        if (methodToParsedBodyMap == null) {
          stashBodiesForClass(m.getDeclaringClass());
        }
      }
    }
    return methodToParsedBodyMap.get(m);
  }

  /**
   * Extracts the reference constant pool for this JimpleAST.
   *
   * @return the Set of RefTypes for the reference types contained this AST.
   */
  public Set<String> getCstPool() {
    CstPoolExtractor cpe = new CstPoolExtractor(mTree);
    return cpe.getCstPool();
  }

  /** Returns the SootResolver currently in use. */
  public SootResolver getResolver() {
    return SootResolver.v();
  }

  /*
   * Runs a Walker on the InputStream associated to this object. The SootClass which we want bodies for is passed as the
   * argument.
   */
  private void stashBodiesForClass(SootClass sc) {
    HashMap<SootMethod, JimpleBody> methodToBodyMap = new HashMap<SootMethod, JimpleBody>();

    Walker w = new BodyExtractorWalker(sc, SootResolver.v(), methodToBodyMap);

    boolean oldPhantomValue = Scene.v().getPhantomRefs();

    Scene.v().setPhantomRefs(true);
    mTree.apply(w);
    Scene.v().setPhantomRefs(oldPhantomValue);

    methodToParsedBodyMap = methodToBodyMap;
  }
} // Parse
