package soot.lambdaMetaFactory;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019 Manuel Benz
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Code according to issue 1146: https://github.com/soot-oss/soot/issues/1146
 *
 * @author Manuel Benz at 2019-05-14
 */
public class Issue1146 {
  public Vertrag getVertrag(String vsnr) {
    List<String> myList = Arrays.asList("element1", "element2", "element3");
    myList.forEach(element -> System.out.println(element));
    return new Vertrag();
  }

  public Vertrag getVertrag2(String vsnr) throws BpmnError {
    Vertrag vertrag = null;
    return Optional.ofNullable(vertrag).orElseThrow(() -> {
      return new BpmnError("not found");
    });
  }

  private class Vertrag {
  }

  private class BpmnError extends Exception {
    public BpmnError(String msg) {
      super(msg);
    }
  }
}
