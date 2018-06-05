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
import java.io.*;
import java.util.*;

public class FileReaderTest {

    public static void main(String [] args) {
    
        for (int i = 0; i < args.length; i++) {
        
            try {
                BufferedReader br = new BufferedReader(new FileReader(args[i]));

                StringTokenizer st = new StringTokenizer(br.readLine());
                
                myMethod(StringToInt(st.nextToken()), StringToInt(st.nextToken()), st.nextToken());
            
                String line;
                while ((line = br.readLine()) != "done") {
                    StringTokenizer st2 = new StringTokenizer(line);
                    myOtherMethod(StringToInt(st2.nextToken()), StringToInt(st2.nextToken()));
                }
                
            }
            catch(IOException e) {
                System.out.println("Error: "+e);
            }
                
        }
    }

    private static void myOtherMethod(int i, int j) {
        System.out.println(i+j);
    }
    
    private static void myMethod(int i, int j, String s) {
        System.out.println(i+j+s);
    }
    
    private static int StringToInt(String s) {
    
        return (new Integer(s)).intValue();
    }
}
