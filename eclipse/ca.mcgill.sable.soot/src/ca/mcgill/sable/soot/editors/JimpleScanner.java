package ca.mcgill.sable.soot.editors;

import java.util.*;
import org.eclipse.jface.text.rules.*;
import org.eclipse.jface.text.*;
import org.eclipse.jface.preference.*;



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
    //IToken key= new Token(new TextAttribute(manager.getColor(IJimpleColorConstants.JIMPLE_KEYWORD),manager.getColor(IJimpleColorConstants.JIMPLE_BACKGROUND),1));
   
    rules.add(new SingleLineRule("\"", "\"", string, '\\')); //$NON-NLS-2$ //$NON-NLS-1$
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
