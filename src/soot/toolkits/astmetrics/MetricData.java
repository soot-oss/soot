/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.astmetrics;

/*
 * Information about a particular metric
 */
public class MetricData {
	String metricName;
	Object value;
	
	public MetricData(String name, Object val){
		metricName=name;
		value=val;
	}
	
	public String toString(){
		StringBuffer b = new StringBuffer();
		b.append("<Metric>\n");
		b.append("  <MetricName>" + metricName + "</MetricName>\n");
		b.append("  <Value>" + value.toString() + "</Value>\n");
		b.append("</Metric>\n");
		return b.toString();
	}
}
