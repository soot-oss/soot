/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jbco.gui;
import java.io.*;
import javax.swing.*;
public class RunnerThread implements Runnable {

  public boolean stopRun = false;
  private JBCOViewer viewer = null;
  private String[] cmdarray = null;
  
  public RunnerThread(String[] argv, JBCOViewer jv) {
    cmdarray = argv;
    viewer = jv;
  }
  
  public void run() {
    synchronized (viewer.newFileMenuItem) {
      viewer.newFileMenuItem.setEnabled(false);
    }
    synchronized (viewer.openFileMenuItem) {
      viewer.openFileMenuItem.setEnabled(true);
    }
    try {
      Process p = Runtime.getRuntime().exec(cmdarray);
      BufferedReader br_in = new BufferedReader(new InputStreamReader(p.getInputStream()));
      
      String line_in = "";
      try {
        while ((line_in = br_in.readLine()) != null) {
          if (stopRun) {
            p.destroy();
            synchronized(viewer.TextAreaOutput) {  
              viewer.TextAreaOutput.append("\n\n*** Execution STOPPED ***");
              viewer.TextAreaOutput.setCaretPosition(viewer.TextAreaOutput.getDocument().getLength() );
            }
            break;
          }
          synchronized(viewer.TextAreaOutput) {
            boolean autoScroll = false;
            JScrollBar vbar = viewer.jScrollPane1.getVerticalScrollBar();
            synchronized (vbar) {
              autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
            }
            viewer.TextAreaOutput.append("\n"+line_in);
            if (autoScroll) 
              viewer.TextAreaOutput.setCaretPosition(viewer.TextAreaOutput.getDocument().getLength() );
          }
        }
      } catch (Exception exc) {
        throw exc;
      } finally {
        br_in.close();
      }
    } catch (Exception exc) {
      synchronized(viewer.TextAreaOutput) {
        viewer.TextAreaOutput.append("\n\n"+exc.toString());
        viewer.TextAreaOutput.setCaretPosition(viewer.TextAreaOutput.getDocument().getLength() );
      }
    }
    synchronized (viewer.newFileMenuItem) {
      viewer.newFileMenuItem.setEnabled(true);
    }
    synchronized (viewer.openFileMenuItem) {
      viewer.openFileMenuItem.setEnabled(false);
    }
  }

}
