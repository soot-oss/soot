// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
class Timer
{
   private long duration;
   private long startTime;
   Timer()
   {
      duration = 0;
   }


   void start()
   {
      startTime = System.currentTimeMillis();
   }


   void end()
   {
      duration += System.currentTimeMillis() - startTime;
   }


   long getTime()
   {
      return duration;
   }


}




