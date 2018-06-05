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

public class Hello2
{  
	public static void main(String args[])
	{
		new Hello2().foo(1);
	}
	int c=0;
	int foo(final int i)
	{
		class LocalClass 
		{
			class InnerClass 
			{
				int run () 
				{
                    //System.out.println("in InnerClass run()");
					class LocalClassNeverUsed
					{
						int run () // this is never called
						{
							return i;
						}
                        
					}
					new LocalClassNeverUsed().run();
					return new LocalClass().run();
				}
			}
			int run () 
			{
                //System.out.println("c: "+c);
                //c--;
                //System.out.println("c: "+c);
                
				if (c--<0) /* prevent stack overflow*///{
				    //System.out.println("returning 0");
                    return 0;
                //}
				//System.out.println("will created InnerClass");
                InnerClass ic = new InnerClass();
				//System.out.println("created InnerClass");
                return ic.run();
				//return new InnerClass().run();
			}			
		};
        //System.out.println("returning from foo");
		return new LocalClass().run();
	}
}
