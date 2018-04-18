
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
