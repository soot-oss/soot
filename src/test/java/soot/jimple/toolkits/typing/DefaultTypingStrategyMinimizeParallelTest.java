package soot.jimple.toolkits.typing;

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

import java.util.List;

import soot.jimple.toolkits.typing.fast.BytecodeHierarchy;
import soot.jimple.toolkits.typing.fast.DefaultTypingStrategy;
import soot.jimple.toolkits.typing.fast.ITyping;

/**
 * JUnit-Tests for the {@link DefaultTypingStrategy#minimizeParallel(List, soot.jimple.toolkits.typing.fast.IHierarchy)}
 * method.
 * 
 * For each test we generate a simple synthetic class hierarchy and some {@link Typing}s we minimize afterwards and check the
 * result. The test are the same as in {@link DefaultTypingStrategyMinimizeSequentialTest}.
 * 
 * @author Jan Peter Stotz
 */
public class DefaultTypingStrategyMinimizeParallelTest extends DefaultTypingStrategyMinimizeSequentialTest {

  @Override
  protected void executeMinimize(List<ITyping> typingList) {
    new DefaultTypingStrategy().minimizeParallel(typingList, new BytecodeHierarchy());
  }

}
