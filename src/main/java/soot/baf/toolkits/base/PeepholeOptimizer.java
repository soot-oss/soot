package soot.baf.toolkits.base;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;

/**
 * Driver class to run peepholes on the Baf IR. The peepholes applied must implement the Peephole interface. Peepholes are
 * loaded dynamically by the soot runtime; the runtime reads the file peephole.dat, in order to determine which peepholes to
 * apply.
 *
 * @see Peephole
 * @see ExamplePeephole
 */

public class PeepholeOptimizer extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(PeepholeOptimizer.class);

  public PeepholeOptimizer(Singletons.Global g) {
  }

  public static PeepholeOptimizer v() {
    return G.v().soot_baf_toolkits_base_PeepholeOptimizer();
  }

  private final String packageName = "soot.baf.toolkits.base";
  private static boolean peepholesLoaded = false;
  private static final Object loaderLock = new Object();

  private final Map<String, Class<?>> peepholeMap = new HashMap<String, Class<?>>();

  /** The method that drives the optimizations. */
  /* This is the public interface to PeepholeOptimizer */

  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (!peepholesLoaded) {
      synchronized (loaderLock) {
        if (!peepholesLoaded) {
          peepholesLoaded = true;

          InputStream peepholeListingStream = null;
          peepholeListingStream = PeepholeOptimizer.class.getResourceAsStream("/peephole.dat");
          if (peepholeListingStream == null) {
            throw new RuntimeException("could not open file peephole.dat!");
          }
          BufferedReader reader = new BufferedReader(new InputStreamReader(peepholeListingStream));

          String line = null;
          List<String> peepholes = new LinkedList<String>();
          try {
            line = reader.readLine();
            while (line != null) {
              if (line.length() > 0) {
                if (!(line.charAt(0) == '#')) {
                  peepholes.add(line);
                }
              }
              line = reader.readLine();
            }
          } catch (IOException e) {
            throw new RuntimeException(
                "IO error occured while reading file:  " + line + System.getProperty("line.separator") + e);
          }

          try {
            reader.close();
            peepholeListingStream.close();
          } catch (IOException e) {
            logger.debug(e.getMessage(), e);
          }

          for (String peepholeName : peepholes) {
            Class<?> peepholeClass;
            if ((peepholeClass = peepholeMap.get(peepholeName)) == null) {
              try {
                peepholeClass = Class.forName(packageName + "." + peepholeName);
              } catch (ClassNotFoundException e) {
                throw new RuntimeException(e.toString());
              }
              peepholeMap.put(peepholeName, peepholeClass);
            }
          }
        }
      }
    }

    boolean changed = true;
    while (changed) {
      changed = false;

      for (String peepholeName : peepholeMap.keySet()) {
        boolean peepholeWorked = true;
        while (peepholeWorked) {
          peepholeWorked = false;
          Peephole p = null;

          try {
            p = (Peephole) peepholeMap.get(peepholeName).newInstance();
          } catch (IllegalAccessException e) {
            throw new RuntimeException(e.toString());
          } catch (InstantiationException e) {
            throw new RuntimeException(e.toString());
          }

          if (p.apply(body)) {
            peepholeWorked = true;
            changed = true;
          }
        }
      }
    }
  }
}
