package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Soot contributors
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

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the {@code NestMembers} class-file attribute introduced by JEP 181 (Nest-Based Access Control). It records the
 * classes that are authorized to be members of the nest hosted by the annotated class.
 */
public class NestMembersTag implements Tag {

  public static final String NAME = "NestMembersTag";

  private final Set<String> members = Collections.newSetFromMap(new ConcurrentHashMap<>());

  /**
   * Adds a nest member.
   * 
   * @param member
   *          the internal name of the nest member
   * @return true if the member was added successfully, otherwise false
   */
  public boolean addNestMember(String member) {
    return this.members.add(member);
  }

  /**
   * Returns all nest members.
   * 
   * @return all nest members
   */
  public Set<String> getNestMembers() {
    return this.members;
  }

  @Override
  public String toString() {
    return "Nest members: " + members;
  }

  @Override
  public String getName() {
    return NAME;
  }
}
