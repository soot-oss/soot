package ca.mcgill.sable.soot.editors;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * @author jeshaw
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JimpleWordDetector implements IWordDetector {

  public boolean isWordStart(char c) {
    return Character.isJavaIdentifierStart(c);
  }
  public boolean isWordPart(char c) {
 	return Character.isJavaIdentifierPart(c);
  }
}
