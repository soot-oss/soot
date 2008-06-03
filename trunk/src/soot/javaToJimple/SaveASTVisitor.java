/* Soot - a J*va Optimization Framework
 * Copyright (C) 2004 Jennifer Lhotak
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

package soot.javaToJimple;

import java.util.*;

import polyglot.frontend.Job;
import polyglot.frontend.Source;

public class SaveASTVisitor extends polyglot.frontend.AbstractPass {

    private polyglot.frontend.Job job;
    private polyglot.frontend.ExtensionInfo extInfo;
    
    public SaveASTVisitor(polyglot.frontend.Pass.ID id, polyglot.frontend.Job job,  polyglot.frontend.ExtensionInfo extInfo){
        super(id);
        this.job = job;
        this.extInfo = extInfo;
    }

    public boolean run(){
        if (extInfo instanceof soot.javaToJimple.jj.ExtensionInfo){
            soot.javaToJimple.jj.ExtensionInfo jjInfo = (soot.javaToJimple.jj.ExtensionInfo)extInfo; 
            if (jjInfo.sourceJobMap() == null){
                jjInfo.sourceJobMap(new HashMap<Source, Job>());
            }
            jjInfo.sourceJobMap().put(job.source(), job);
            return true;
        }
        return false;
    }
}
