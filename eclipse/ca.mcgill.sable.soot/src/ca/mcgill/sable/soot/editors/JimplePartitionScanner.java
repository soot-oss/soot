package ca.mcgill.sable.soot.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.*;

public class JimplePartitionScanner extends RuleBasedPartitionScanner {
	public final static String JIMPLE_STRING = "__jimple_string";
    public final static String SKIP = "__skip";
	
	public JimplePartitionScanner() {

		List rules = new ArrayList();

		IToken string = new Token(JIMPLE_STRING);
		IToken skip = new Token(SKIP);

		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", skip, '\\'));

		IPredicateRule[] result= new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}
