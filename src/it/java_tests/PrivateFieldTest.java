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
public class PrivateFieldTest {
    private int a = 9;
    private int b = 9;
    private int c = 9;
    private int d = 9;
    private int e = 9;
    private int f = 9;
    private int g = 9;
    private int h = 9;
    private int i = 9;
    private int j = 9;
    private int k = 9;
    private int l = 9;
    private int m = 9;
    private int n = 9;
    private int o = 9;
    private int p = 9;
    private int q = 9;
    private int r = 9;
    private int s = 9;
    
    public static void main(String [] args){
        PrivateFieldTest u = new PrivateFieldTest();
        u.run();
    }

    public void run(){
        Inner i = new Inner();
        i.run();
    }
    
    public class Inner {
        
        public void run(){
            a += 9;
            b += 0;
            c += 5;
            d -= 8;
            e += 4;
            f *= 3;
            g -= 2;
            h += 44;
            i += h;
            j -= g;
            h++;
            k--;
            l++;
            System.out.println(l);
        }
    }
}
