/* Soot - a J*va Optimization Framework
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

package soot.rtaclassload;

public class BytecodeFile {

  private String jarFilename;
  private String filename;
  private byte[] contents;

  public BytecodeFile(String jarFilename, String filename, byte[] contents){
    this.jarFilename = jarFilename;
    this.filename = filename;
    this.contents = contents;
  }

  public String getJarFilename(){
    return jarFilename;
  }

  public String getFileName(){
    return filename;
  }

  public byte[] getContents(){
    return contents;
  }
}
