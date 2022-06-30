package soot.toolkits.astmetrics;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2006 Nomair A. Naeem
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

import java.util.ArrayList;
import java.util.Iterator;

/*
 * Should store general information about the class
 * Size? number of nodes?
 * Plus a list of MetricData objects
 */
public class ClassData {
  String className; // the name of the class whose data is being stored
  ArrayList<MetricData> metricData; // each element should be a MetricData

  public ClassData(String name) {
    className = name;
    metricData = new ArrayList<MetricData>();
  }

  public String getClassName() {
    return className;
  }

  /*
   * returns true if this className has the same name as the string sent as argument
   */
  public boolean classNameEquals(String className) {
    return (this.className.equals(className));
  }

  /*
   * Only add new metric if this is not already present Else dont add
   */
  public void addMetric(MetricData data) {
    Iterator<MetricData> it = metricData.iterator();
    while (it.hasNext()) {
      MetricData temp = it.next();
      if (temp.metricName.equals(data.metricName)) {
        // System.out.println("Not adding same metric again......"+temp.metricName);
        return;
      }
    }
    metricData.add(data);
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("<Class>\n");
    b.append("<ClassName>" + className + "</ClassName>\n");
    Iterator<MetricData> it = metricData.iterator();
    while (it.hasNext()) {
      b.append(it.next().toString());
    }
    b.append("</Class>");
    return b.toString();
  }
}
