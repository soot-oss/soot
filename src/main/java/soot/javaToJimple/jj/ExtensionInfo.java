package soot.javaToJimple.jj;

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

import java.io.File;
import java.util.HashMap;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.frontend.Job;
import polyglot.frontend.Source;
import polyglot.main.Options;
import polyglot.types.TypeSystem;

import soot.javaToJimple.jj.ast.JjNodeFactory_c;
import soot.javaToJimple.jj.types.JjTypeSystem_c;

/**
 * Extension information for jj extension.
 */
public class ExtensionInfo extends polyglot.ext.jl.ExtensionInfo {
  static {
    // force Topics to load
    new Topics();
  }

  public String defaultFileExtension() {
    return "jj";
  }

  public String compilerName() {
    return "jjc";
  }

  /*
   * public Parser parser(Reader reader, FileSource source, ErrorQueue eq) { Lexer lexer = new Lexer_c(reader, source.name(),
   * eq); Grm grm = new Grm(lexer, ts, nf, eq); return new CupParser(grm, source, eq); }
   */

  protected NodeFactory createNodeFactory() {
    return new JjNodeFactory_c();
  }

  protected TypeSystem createTypeSystem() {
    return new JjTypeSystem_c();
  }

  public List passes(Job job) {
    List passes = super.passes(job);
    // TODO: add passes as needed by your compiler
    return passes;
  }

  private HashMap<Source, Job> sourceJobMap;

  public HashMap<Source, Job> sourceJobMap() {
    return sourceJobMap;
  }

  public void sourceJobMap(HashMap<Source, Job> map) {
    sourceJobMap = map;
  }

  /**
   * Appends the soot classpath to the default system classpath.
   */
  protected Options createOptions() {
    return new Options(this) {

      /**
       * Appends the soot classpath to the default system classpath.
       */
      public String constructFullClasspath() {
        String cp = super.constructFullClasspath();
        cp += File.pathSeparator + soot.options.Options.v().soot_classpath();
        return cp;
      }

    };
  }
}
