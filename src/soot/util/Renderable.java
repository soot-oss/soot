

package soot.util;

import java.io.*;

public interface Renderable {
  public void render(OutputStream device, int indent) throws IOException;
}
