// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
// import ca.mcgill.sable.soot.sideEffect.*;

class LoopDetector {
   List seenList = new ArrayList();
   Map backbranches = new HashMap();
   List workQ = new ArrayList();
   int loopcount;


   void setLoopCountFor ( MethodNode me ) {
      loopcount = 0;
      seenList.clear();
      backbranches = new HashMap();
      //      backbranches.clear():

      try {
         SootMethod m = me.getMethod();
         //      System.out.println ( "TRYING LOOPS FOR : "+m.getSignature() );

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
                           me.ImportantInvokeExprs.add ( ( ( InvokeStmt ) tempstmt ).getInvokeExpr() );
                        }

                        else if ( tempstmt instanceof AssignStmt )
                        {
                           AssignStmt tempassignstmt = ( AssignStmt ) tempstmt;
                           if ( tempassignstmt.getRightOp() instanceof InvokeExpr )
                           {
                              int indexofinvstmt = tempcount;
                              if ( ( indexofinvstmt >= indexofsucc ) && ( indexofinvstmt < stmtcount ) )
                              {
                                 me.ImportantInvokeExprs.add ( tempassignstmt.getRightOp() );
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

      me.numloops = loopcount;
   }



}




