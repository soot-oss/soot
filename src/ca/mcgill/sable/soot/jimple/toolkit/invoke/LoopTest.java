// package ca.mcgill.sable.soot.virtualCalls;
package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
// import ca.mcgill.sable.soot.sideEffect.*;

class LoopTest {
   List seenList = new ArrayList();
   Map backbranches = new HashMap();
   List workQ = new ArrayList();
   int loopcount;
   void setLoopCountFor ( SootMethod m ) {
      //  public List dfsCast( StmtGraph g, Stmt s,  CastExpr e, Vector vector, Vector svector )

      //    public void setLoopCountFor ( SootMethod m ) {

      loopcount = 0;
      seenList.clear();
      backbranches = new HashMap();
      //      backbranches.clear():

      try {
         // SootMethod m = me.getMethod();

         System.out.println ( "TRYING LOOPS FOR : "+m.getSignature() );
         JimpleBody jb = Jimplifier.getJimpleBody ( m );
         //  JimpleBody jb = (JimpleBody) new BuildAndStoreBody(Jimple.v(), new StoredBody(ClassFile.v())).resolveFor(m);

         StmtList stmtList = jb.getStmtList();
         CompleteStmtGraph g = new CompleteStmtGraph(stmtList);
         Iterator stmtit = stmtList.iterator();
         int stmtcount = 0;
         while ( stmtit.hasNext() )
         {
            Stmt s = ( Stmt ) stmtit.next();
            if ( s instanceof IfStmt )
            {
               //       System.out.println ( "IF STMT "+s );

               List list = g.getSuccsOf( s );
               Iterator listit = list.iterator();
               while ( listit.hasNext() )
               {
                  Stmt succ = ( Stmt ) listit.next();
                  //       System.out.println ( "SUCC "+succ );

                  int indexofsucc = stmtList.indexOf ( succ );
                  if ( indexofsucc <= stmtcount )
                  {
                     //     System.out.println ( "INDEXOF "+ stmtList.indexOf ( succ ) );

                     //     System.out.println ( "STMTCOUNT "+ stmtcount );

                     loopcount++;
                     Iterator stmtIterator = stmtList.iterator();
                     int tempcount = 0;
                     while ( stmtIterator.hasNext() )
                     {
                        Stmt tempstmt = ( Stmt ) stmtIterator.next();
                        if ( tempstmt instanceof InvokeStmt )
                        {
                           int indexofinvstmt = tempcount;
                           if ( ( indexofinvstmt >= indexofsucc ) && ( indexofinvstmt < stmtcount ) )
                           //           me.ImportantInvokeExprs.add ( ( ( InvokeStmt ) tempstmt ).getInvokeExpr() );

                           System.out.println ( "IMPORTANT "+tempstmt );
                        }

                        else if ( tempstmt instanceof AssignStmt )
                        {
                           AssignStmt tempassignstmt = ( AssignStmt ) tempstmt;
                           if ( tempassignstmt.getRightOp() instanceof InvokeExpr )
                           {
                              int indexofinvstmt = tempcount;
                              if ( ( indexofinvstmt >= indexofsucc ) && ( indexofinvstmt < stmtcount ) )
                              {
                                 // me.ImportantInvokeExprs.add ( tempassignstmt.getRightOp() );

                                 System.out.println ( "IMPORTANT "+tempstmt );
                              }

                           }

                        }

                        tempcount++;
                     }

                  }

               }

            }

            stmtcount++;
         }

      }
      catch ( java.lang.RuntimeException e ) {}

      // me.numloops = loopcount;

   }


   /*

      public void setLoopCountFor ( MethodNode me ) {

    //  public List dfsCast( StmtGraph g, Stmt s,  CastExpr e, Vector vector, Vector svector )

    //   public void setLoopCountFor ( SootMethod m ) {

         loopcount = 0;
          
         seenList.clear();

         backbranches = new HashMap();

   //      backbranches.clear():

         SootMethod m = me.getMethod();

         System.out.println ( "TRYING LOOPS FOR : "+m.getSignature() );

         JimpleBody jb = Jimplifier.getJimpleBody ( m );

   //    JimpleBody jb = (JimpleBody) new BuildAndStoreBody(Jimple.v(), new StoredBody(ClassFile.v())).resolveFor(m);

         StmtList stmtList = jb.getStmtList();

         CompleteStmtGraph g = new CompleteStmtGraph(stmtList);

         List heads = g.getHeads();

         Iterator headsit = heads.iterator();

         while ( headsit.hasNext() )
         {

          Stmt s = ( Stmt ) headsit.next();

       //   workQ.add ( s );

          dfs ( s, g );

         }

         // if ( workQ.size() > 0 )
         // bfs ( ( Stmt ) workQ.get ( 0 ), g );

         me.numloops = loopcount;

         System.out.println ( m.getSignature() + " LOOP COUNT " + loopcount );

        }




          public void dfs ( Stmt s, CompleteStmtGraph g  ) {

           List tails = g.getTails();

           seenList.add ( s );

           List list = g.getSuccsOf( s );

           Iterator listit = list.iterator();

           while ( listit.hasNext() )
           {
      
            Stmt succ = ( Stmt ) listit.next();

            if ( seenList.contains ( succ ) )
            {       
         
             if ( ( ( ( Stmt ) backbranches.get( succ ) ) == null ) ) 
             {

   //          System.out.println ( s +" AND "+succ );

             loopcount++;

             backbranches.put ( succ, s );

             }

            }
            else if ( ! tails.contains ( succ ) )
            {

             dfs ( succ, g );

             // workQ.add ( succ );

            }
                      
           }

           // workQ.remove(0);

           // if ( workQ.size() > 0 )
           // bfs ( ( Stmt ) workQ.get ( 0 ), g ); 

           seenList.remove ( s );

          }

   */
   static void main ( String[] args ) {
      LoopTest l = new LoopTest();
      SootClassManager cm = new SootClassManager();
      for ( int i=0; i<args.length; i++ )
      {
         SootClass sClass = cm.getClass(args[i]);
         Iterator methodIt = sClass.getMethods().iterator();
         while(methodIt.hasNext())
         {
            SootMethod m = (SootMethod) methodIt.next();
            l.setLoopCountFor ( m );
            System.out.println ( m.getSignature() + " LOOP COUNT " + l.loopcount );
         }

      }

   }


}




