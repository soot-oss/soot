
package soot.util;

public class DotGraphAttribute {
  String id;
  String value;

  public DotGraphAttribute(String id, String v){
    this.id = id;
    this.value = v;
  }
  
  public String toString(){
    StringBuffer line = new StringBuffer();
    line.append(this.id);
    line.append("=");
    line.append(this.value);
    return new String(line);
  }
}
