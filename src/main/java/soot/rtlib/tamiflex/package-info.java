/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
 
/**
 * This package contains classes that may be emitted into a program during code generation. If Soot cannot find a class on
 * the soot-classpath then it automatically loads the class from Soot's own JAR file but <i>only</i> if this class is in this
 * package. This is to avoid accidental mix-ups between the classes of the application being analyzed and Soot's own classes.
 *
 * To add a class, use, for example, the following:
 *
 * <pre>
 * // before calling soot.Main.main
 * Scene.v().addBasicClass(SootSig.class.getName(), SootClass.BODIES);
 * // then at some point
 * Scene.v().getSootClass(SootSig.class.getName()).setApplicationClass();
 * </pre>
 *
 * This will cause Soot to emit the class SootSig along with the analyzed program.
 */
package soot.rtlib.tamiflex;
