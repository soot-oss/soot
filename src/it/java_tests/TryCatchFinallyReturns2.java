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
public class TryCatchFinallyReturns2 {

    public static void main(String [] args){
        TryCatchFinallyReturns2 t = new TryCatchFinallyReturns2();
        int y = t.run(3);
        System.out.println(y);
    }

    public int run(int i){
        int [] x = new int[]{i};
        try {
            if (x[0] == 4) {
                return 7;
            }
            else if (x[1] == 8){
                return 2;
            }
        
        }
        catch (ArrayIndexOutOfBoundsException e){
            try {
                if (x[0] == 4){
                    return 7;
                }
                else if (x[1] == 8){
                    return 2;
                }
            }
            catch (ArrayIndexOutOfBoundsException e1){
                return 17;
            }
            finally {
                System.out.println("hello");
                return 27;
            }
        }
        finally {
            System.out.println("Hello 2");
            return 28;
        }
    }
}
