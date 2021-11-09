package soot.jimple.toolkit.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Qidan He
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ContainerMultiTypeSample {
    Helper helper;
    int i;
    public void target() {
        //this.helpers.get("").handle();
        for(Helper helper: helpers)
            helper.handle();
        //helper.handle();
    }

    //Map<String, Helper> helpers;
    Set<Helper> helpers;
    public ContainerMultiTypeSample () {
        //this.helpers = new HashMap<>();
        //this.helpers.put("a", new AHelper());
        //this.helpers.put("b", new BHelper());
        this.helpers = new HashSet<>();
        this.helpers.add(new AHelper());
        //if(i > 0)
        //    helper = new AHelper();
        //else
        //    helper = new BHelper();
    }
}

interface Helper {
    public void handle();
}

class AHelper implements Helper {

    @Override
    public void handle() {
        System.out.println("wtf");
    }
}

class BHelper implements Helper {

    @Override
    public void handle() {
    }
}