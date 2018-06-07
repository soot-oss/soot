package soot.dava;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem (nomair.naeem@mail.mcgill.ca)
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

//import java.util.*;
//import soot.util.*;
//import soot.dava.toolkits.base.finders.*;

public class CorruptASTException extends DecompilationException {
  public CorruptASTException(String message) {
    super();
    System.out.println("The Abstract Syntax Tree is corrupt");
    System.out.println(message);
    report();
  }
}
