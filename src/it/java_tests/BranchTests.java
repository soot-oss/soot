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
public class BranchTests {

    public static void main (String [] args) {
        BranchTests bt = new BranchTests();
        bt.runContinues();
        bt.runBreaks();
    }

    public void runBreaks() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            StringBuffer word = new StringBuffer();
            while (true) {
                String in = (String)br.readLine();
                if (in.equals( "done")) break;
                word.append(in);
            }
            System.out.println(word);

            int i = 0;
            outer: while(i < 5){
                int j = 0;
                inner: while (j < 3) {
                    String in = (String)br.readLine();
                    if (in.equals("outer")) break outer;
                    if (in.equals("inner")) break inner;
                    System.out.println(j);
                    j++;
                }
                System.out.println(i);
                i++;
                
            }

            
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    
        
    }
    
    public void runContinues() {
        
    }
}
