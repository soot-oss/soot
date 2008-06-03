/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jennifer Lhotak
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

package ca.mcgill.sable.soot.editors;

import java.util.*;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;

public class JimpleScanner extends RuleBasedScanner {

  private static String[] keywords= {
	  	"ignored",
		"abstract",
		"final",
		"native",
		"public",
		"protected",
		"private",
		"static",
		"synchronized",
		"transient",
		"volatile",
		"class",
		"interface",
		"void",
		"boolean",
		"byte",
		"short",
		"char",
		"int",
		"long",
		"float",
		"double",
		"null_type",
		"unknown",
		"extends",
		"implements",
		"breakpoint",
		"case",
		"catch",
		"cmp",
		"cmpg",
		"cmpl",
		"default",
		"entermonitor",
		"exitmonitor",
		"goto",
		"if",
		"instanceof",
		"interfaceinvoke",
		"lengthof",
  		"lookupswitch",
  		"neg",
  		"new",
  		"newarray",
  		"newmultiarray",
  		"nop",
  		"ret",
  		"return",
  		"specialinvoke",
  		"staticinvoke",
  		"tableswitch",
  		"throw",
  		"throws",
  		"virtualinvoke",
  		"null",
		"from",
		"to",
		"with"
        };
        

  public JimpleScanner(ColorManager manager) {
  	 
	List rules = new ArrayList();
   
    IToken string = new Token(new TextAttribute(manager.getColor(IJimpleColorConstants.JIMPLE_STRING)));
    IToken def= new Token(new TextAttribute(manager.getColor(IJimpleColorConstants.JIMPLE_DEFAULT)));
    IToken key= new Token(new TextAttribute(manager.getColor(IJimpleColorConstants.JIMPLE_KEYWORD)));
   
    rules.add(new SingleLineRule("\"", "\"", string, '\\'));
    rules.add(new SingleLineRule("'", "'", string, '\\')); 
    
    WordRule wordRule= new WordRule(new JimpleWordDetector(), def);
	

    for (int i=0; i<keywords.length; i++) 
         wordRule.addWord(keywords[i], key);
       
	rules.add(wordRule);


	// Add generic whitespace rule.
	rules.add(new WhitespaceRule(new JimpleWhitespaceDetector()));

	IRule[] result = new IRule[rules.size()];
	rules.toArray(result);
	setRules(result);
    }
}
