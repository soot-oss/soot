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
public class CondAndTest2 {
    
    public static void main(String [] args){
        String s1 = "S1";   
        String s2 = "S2";   
        String s3 = "S3";

        CondAndTest2 cat = new CondAndTest2();

        boolean result = cat.isValid(s1, s2, s3);
    }

    public boolean isValid(String s1, String s2, String s3){

        boolean p1 = s1 == null ? true : false;
        return ((s1 == null) && (s2 == null) && (s3 == null)); 
    }
}
