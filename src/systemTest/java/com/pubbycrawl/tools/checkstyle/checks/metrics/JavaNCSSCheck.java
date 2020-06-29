package com.pubbycrawl.tools.checkstyle.checks.metrics;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
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

import javax.print.attribute.standard.Finishings;

import com.pubbycrawl.tools.checkstyle.api.AbstractCheck;

/**
 * @author Pavan Gurkhi Bhimesh created on 29.06.20
 */

public class JavaNCSSCheck extends AbstractCheck {
	
	public static void main(String[] args) {
		JavaNCSSCheck mainClass = new JavaNCSSCheck();
		mainClass.finishTree();
	}
	
	public void finishTree() {
		//logging the values here
		log("1234", "Test");
	}

}
