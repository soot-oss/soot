/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.coffi;
import soot.*;
import soot.util.*;

class Main
{
    public static void main(String[] args) throws RuntimeException
    {
        if(args.length == 0)
        {
            G.v().out.println("Usage: java soot.coffi.Main class1 class2 ...");
            System.exit(0);
        }

        for(int i = 0; i < args.length; i++)
            printClassInfo(args[i]);
    }

    public static void printClassInfo(String name)
    {
        ClassFile coffiClass = new ClassFile(name);
        long totalLocals;
        long totalInstructions;
        long totalCodeSize;

        // Load classFile
        {
            boolean success = coffiClass.loadClassFile();

            if(!success)
            throw new RuntimeException("Couldn't load class file for " + name);
        }

        // Get statistics
            totalLocals = 0;
            totalInstructions = 0;
            totalCodeSize = 0;

            for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info method = coffiClass.methods[i];
                Code_attribute code_attribute = method.locate_code_attribute();

                long numLocals = 0;
                long numInstructions = 0;
                long codeSize = 0;

                if(code_attribute != null)
                {
                    new CFG(method);

                    method.cfg.reconstructInstructions();

                    numLocals += code_attribute.max_locals;
                    codeSize += code_attribute.code_length;

                    Instruction ins = method.instructions;

                    while(ins != null)
                    {
                        numInstructions++;

                        ins = ins.next;
                    }
                }

                totalLocals += numLocals;
                totalInstructions += numInstructions;
                totalCodeSize += codeSize;
            }

        // Print info
            G.v().out.println(name + ": " + totalLocals + " locals  " + totalInstructions +
                " bytecode instructions  " + totalCodeSize + " bytes of code");
    }
}
