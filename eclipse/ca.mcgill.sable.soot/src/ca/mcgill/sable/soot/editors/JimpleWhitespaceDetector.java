package ca.mcgill.sable.soot.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;


public class JimpleWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
