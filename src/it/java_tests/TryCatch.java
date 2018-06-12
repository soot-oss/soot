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

public class TryCatch {

    int x;
    
    public static void main(String [] args) {
        TryCatch tc = new TryCatch();
        tc.run();
        tc.x = 8;
    }

    private void run(){
        try {
            InputStreamReader isr = new InputStreamReader(System.in);
            //BufferedReader br = new BufferedReader(isr);
            //while (true){
            //String temp = (String)br.readLine();
            //    if (temp.equals("done")) break;
            //    System.out.println(temp);
            //}
        }
        /*catch(IOException e) {
            //System.out.println(e.getMessage());
            System.out.println("Error");
        }*/
        catch(Exception e2) {
            System.out.println(e2.getMessage());
        }
        finally {
            System.out.println("Smile");
        }
    }
}
