
package soot.util;

import java.io.*;

public class DotGraphCommand implements Renderable{
  String command;

  public DotGraphCommand(String cmd) {
    this.command = cmd;
  }

  public void render(OutputStream out, int indent) throws IOException {
    DotGraphUtility.renderLine(out, command, indent);
  }
}
