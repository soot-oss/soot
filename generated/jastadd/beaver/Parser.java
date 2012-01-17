/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver;

import java.io.IOException;

/**
 * Almost complete implementation of a LALR parser. Two components that it lacks to parse a concrete
 * grammar -- rule actions and parsing tables -- are provided by a generated subclass.
 */
public abstract class Parser
{
	static public class Exception extends java.lang.Exception
	{
		Exception(String msg)
		{
			super(msg);
		}
	}
	
	/**
	 * This class "lists" reportable events that might happen during parsing.
	 */
	static public class Events
	{
		public void scannerError(Scanner.Exception e)
		{
			System.err.print("Scanner Error:");
			if (e.line > 0)
			{
				System.err.print(e.line);
				System.err.print(',');
				System.err.print(e.column);
				System.err.print(':');
			}
			System.err.print(' ');
			System.err.println(e.getMessage());
		}
		public void syntaxError(Symbol token)
		{
			System.err.print(':');
			System.err.print(Symbol.getLine(token.start));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.start));
			System.err.print('-');
			System.err.print(Symbol.getLine(token.end));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.end));
			System.err.print(": Syntax Error: unexpected token ");
			if (token.value != null)
			{
				System.err.print('"');
				System.err.print(token.value);
				System.err.println('"');
			}
			else
			{
				System.err.print('#');
				System.err.println(token.id);
			}
		}
		public void unexpectedTokenRemoved(Symbol token)
		{
			System.err.print(':');
			System.err.print(Symbol.getLine(token.start));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.start));
			System.err.print('-');
			System.err.print(Symbol.getLine(token.end));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.end));
			System.err.print(": Recovered: removed unexpected token ");
			if (token.value != null)
			{
				System.err.print('"');
				System.err.print(token.value);
				System.err.println('"');
			}
			else
			{
				System.err.print('#');
				System.err.println(token.id);
			}
		}
		public void missingTokenInserted(Symbol token)
		{
			System.err.print(':');
			System.err.print(Symbol.getLine(token.start));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.start));
			System.err.print('-');
			System.err.print(Symbol.getLine(token.end));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.end));
			System.err.print(": Recovered: inserted missing token ");
			if (token.value != null)
			{
				System.err.print('"');
				System.err.print(token.value);
				System.err.println('"');
			}
			else
			{
				System.err.print('#');
				System.err.println(token.id);
			}
		}
		public void misspelledTokenReplaced(Symbol token)
		{
			System.err.print(':');
			System.err.print(Symbol.getLine(token.start));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.start));
			System.err.print('-');
			System.err.print(Symbol.getLine(token.end));
			System.err.print(',');
			System.err.print(Symbol.getColumn(token.end));
			System.err.print(": Recovered: replaced unexpected token with ");
			if (token.value != null)
			{
				System.err.print('"');
				System.err.print(token.value);
				System.err.println('"');
			}
			else
			{
				System.err.print('#');
				System.err.println(token.id);
			}
		}
		public void errorPhraseRemoved(Symbol error)
		{
			System.err.print(':');
			System.err.print(Symbol.getLine(error.start));
			System.err.print(',');
			System.err.print(Symbol.getColumn(error.start));
			System.err.print('-');
			System.err.print(Symbol.getLine(error.end));
			System.err.print(',');
			System.err.print(Symbol.getColumn(error.end));
			System.err.println(": Recovered: removed error phrase");
		}
	}
	
	/**
	 * This class wrapps a Scanner and provides a token "accumulator" for a parsing simulation.
	 * <p>If a source that is being parsed does not have syntax errors this wrapper only adds 
	 * one indirection while it delivers the next token. However when parser needs to recover
	 * from a syntax error this wrapper accumulates tokens shifted by a forward parsing simulation
	 * and later feeds them to the recovered parser.
	 */
	public class TokenStream
	{
		private Scanner  scanner;
		private Symbol[] buffer;
		private int      n_marked;
		private int      n_read;
		private int      n_written;
		
		public TokenStream(Scanner scanner)
		{
			this.scanner = scanner;
		}

        public TokenStream(Scanner scanner, Symbol first_symbol)
        {
            this(scanner);
            alloc(1);
            buffer[0] = first_symbol;
            n_written++;
        }
        
		public Symbol nextToken() throws IOException
		{
			if (buffer != null)
			{				
				if (n_read < n_written)
					return buffer[n_read++];
				
				if (n_written < n_marked)
				{
					n_read++;
					return buffer[n_written++] = readToken();
				}
				buffer = null;
			}
			return readToken();
		}

		/**
		 * Prepare a stream to accumulate tokens.
		 * 
		 * @param size number of shifted tokens to accumulate
		 */
		public void alloc(int size)
		{
			buffer = new Symbol[(n_marked = size) + 1];
			n_read = n_written = 0;
		}
		
		/**
		 * Prepare accumulated tokens to be reread by a next simulation run
		 * or by a recovered parser.
		 */
		public void rewind()
		{
			n_read = 0;
		}
        
		/**
		 * Insert two tokens at the beginning of a stream.
		 * 
		 * @param t0 first token to be inserted
		 * @param t1 second token to be inserted
		 */
		public void insert(Symbol t0, Symbol t1)
		{
		    if (buffer.length - n_written < 2)
		        throw new IllegalStateException ("not enough space in the buffer");
			System.arraycopy(buffer, 0, buffer, 2, n_written);
			buffer[0] = t0;
			buffer[1] = t1;
			n_written += 2;
		}
		
		/**
		 * Removes a token from the accumulator.
		 * 
		 * @param i index of a token in the accumulator.
		 * @return removed token
		 */
		public Symbol remove(int i)
		{
			Symbol token = buffer[i];
			int last = n_written - 1;
			while (i < last)
			{
				buffer[i] = buffer[++i];
			}
			n_written = last;
			return token;
		}

		/**
		 * Checks whether a simulation filled the token accumulator. 
		 * 
		 * @return true if accumulator is full
		 */
		boolean isFull()
		{
			return n_read == n_marked;
		}
		
		/**
		 * Reads next recognized token from the scanner. If scanner fails to recognize a token and
		 * throws an exception it will be reported via Parser.scannerError().
		 * <p>It is expected that scanner is capable of returning at least an EOF token after the
		 * exception.</p>
		 * 
		 * @return next recognized token
		 * @throws IOException
		 *             as thrown by a scanner
		 */
		private Symbol readToken() throws IOException
		{
			while (true)
			{
				try
				{
					return scanner.nextToken();
				}
				catch (Scanner.Exception e)
				{
					report.scannerError(e);
				}
			}
		}
	}

	/**
	 * Simulator is a stripped (of action code) version of a parser that will try to parse ahead
	 * token stream after a syntax error. The simulation is considered successful if 3 tokens were
	 * shifted successfully. If during simulation this parser enconters an error it drops the first
	 * token it tried to use and restarts the simulated parsing.
	 * <p>
	 * Note: Without a special "error" rule present in a grammar, which a parser will try to shift
	 * at the beginning of an error recovery, simulation continues without removing anything from
	 * the original states stack. This often will lead to cases when no parsing ahead will recover
	 * the parser from a syntax error.
	 * </p>
	 */
	public class Simulator
	{
		private short[] states;
		private int top, min_top;

		public boolean parse(TokenStream in) throws IOException
		{
			initStack();
			do {
				Symbol token = in.nextToken();
				while (true)
				{
					short act = tables.findParserAction(states[top], token.id);
					if (act > 0)
					{
						shift(act);
						break;
					}
					else if (act == accept_action_id)
					{
						return true;
					}
					else if (act < 0)
					{
						short nt_id = reduce(~act);

						act = tables.findNextState(states[top], nt_id);
						if (act > 0)
							shift(act);
						else
							return act == accept_action_id;
					}
					else // act == 0, i.e. this is an error
					{
						return false;
					}
				}
			}
			while (!in.isFull());
			return true;
		}

		private void initStack() throws IOException
		{
			if (states == null || states.length < Parser.this.states.length)
			{
				states = new short[Parser.this.states.length];
				min_top = 0;
			}
			System.arraycopy(Parser.this.states, min_top, states, min_top, (top = Parser.this.top) + 1);
		}

		private void increaseStackCapacity()
		{
			short[] new_states = new short[states.length * 2];
			System.arraycopy(states, 0, new_states, 0, states.length);
			states = new_states;
		}

		private void shift(short state)
		{
			if (++top == states.length)
				increaseStackCapacity();
			states[top] = state;
		}

		private short reduce(int rule_id)
		{
			int rule_info = tables.rule_infos[rule_id];
			int rhs_size = rule_info & 0xFFFF;
			top -= rhs_size;
			min_top = Math.min(min_top, top);
			return (short) (rule_info >>> 16);
		}
	}

	/** The automaton tables. */
	protected final ParsingTables tables;

	/** Cached ID of the ACCEPT action. */
	protected final short accept_action_id;

	/** The parser's stack. */
	protected short[] states;

	/** Index of the stack's top element, i.e. it's = -1 when the stack is empty; */
	protected int top;

	/** The stack of shifted symbols. */
	protected Symbol[] _symbols;

	/** Parsing events notification "gateway" */
	protected Events report;
	

	protected Parser(ParsingTables tables)
	{
		this.tables = tables;
		this.accept_action_id = (short) ~tables.rule_infos.length;
		this.states = new short[256];
	}

    /**
     * Parses a source and returns a semantic value of the accepted nonterminal
     * 
     * @param source of tokens - a Scanner
     * @return semantic value of the accepted nonterminal
     */
	public Object parse(Scanner source) throws IOException, Parser.Exception
	{
		init();
		return parse(new TokenStream(source));
	}
    
    /**
     * Parses a source and returns a semantic value of the accepted nonterminal.
     * Before parsing starts injects alternative goal marker into the source to
     * indicate that an alternative goal should be matched.
     * 
     * @param source of tokens - a Scanner
     * @param alt_goal_marker_id ID of a token like symbol that will be used as a marker
     * @return semantic value of the accepted nonterminal
     */
    public Object parse(Scanner source, short alt_goal_marker_id) throws IOException, Parser.Exception
    {
        init();
        TokenStream in = new TokenStream(source, new Symbol(alt_goal_marker_id));
        return parse(in);
    }
    
    private Object parse(TokenStream in) throws IOException, Parser.Exception
    {
        while (true)
        {
            Symbol token = in.nextToken();
            while (true)
            {
                short act = tables.findParserAction(states[top], token.id);
                if (act > 0)
                {
                    shift(token, act);
                    break;
                }
                else if (act == accept_action_id)
                {
                    Symbol goal = _symbols[top];
                    _symbols = null; // drop this stack to prevent loitering
                    return goal.value;
                }
                else if (act < 0)
                {
                    Symbol nt = reduce(~act);
                    act = tables.findNextState(states[top], nt.id);
                    if (act > 0)
                    {
                        shift(nt, act);
                    }
                    else if (act == accept_action_id)
                    {
                        _symbols = null; // no loitering
                        return nt.value;
                    }
                    else
                    {
                        throw new IllegalStateException("Cannot shift a nonterminal");
                    }
                }
                else // act == 0, i.e. this is an error
                {
                    report.syntaxError(token);
                    recoverFromError(token, in);
                    break; // because error recovery altered token stream - parser needs to refetch the next token
                }
            }
        }
    }

	/**
	 * Invoke actual reduce action routine.
	 * Method must be implemented by a generated parser
	 * 
	 * @param rule_num ID of a reduce action routine to invoke
	 * @param offset to the symbol before first action routine argument
	 * @return reduced nonterminal
	 */
	protected abstract Symbol invokeReduceAction(int rule_num, int offset);

	/**
	 * Performs stacks and, if not initialized yet, reduce actions array initialization.
	 */
	private void init()
	{
		if (report == null) report = new Events();
		
		_symbols = new Symbol[states.length];
		top = 0; // i.e. it's not empty
		_symbols[top] = new Symbol("none"); // need a symbol here for a default reduce on the very first erroneous token  
		states[top] = 1; // initial/first state
	}

	/**
	 * Increases the stack capacity if it has no room for new entries.
	 */
	private void increaseStackCapacity()
	{
		short[] new_states = new short[states.length * 2];
		System.arraycopy(states, 0, new_states, 0, states.length);
		states = new_states;

		Symbol[] new_stack = new Symbol[states.length];
		System.arraycopy(_symbols, 0, new_stack, 0, _symbols.length);
		_symbols = new_stack;
	}

	/**
	 * Shift a symbol to stack and go to a new state 
	 * 
	 * @param sym
	 *            symbol that will be shifted
	 * @param goto_state
	 *            to switch to
	 */
	private void shift(Symbol sym, short goto_state)
	{
		if (++top == states.length)
			increaseStackCapacity();
		_symbols[top] = sym;
		states[top] = goto_state;
	}

	/**
	 * Perform a reduce action.
	 * 
	 * @param rule_id
	 *            Number of the production by which to reduce
	 * @return nonterminal created by a reduction
	 */
	private Symbol reduce(int rule_id)
	{
		int rule_info = tables.rule_infos[rule_id];
		int rhs_size = rule_info & 0xFFFF;

		top -= rhs_size;
		Symbol lhs_sym = invokeReduceAction(rule_id, top);
		lhs_sym.id = (short) (rule_info >>> 16);
		if (rhs_size == 0)
		{
			lhs_sym.start = lhs_sym.end = _symbols[top].end;
		}
		else
		{
			lhs_sym.start = _symbols[top + 1].start;
			lhs_sym.end = _symbols[top + rhs_size].end;
		}
		return lhs_sym;
	}

    /**
     * Implements parsing error recovery. Tries several simple approches first, like deleting "bad" token
     * or replacing the latter with one of the expected in his state (if possible). If simple methods did
     * not work tries to do error phrase recovery.
     * 
     * It is expected that normally descendand parsers do not need to alter this method. In same cases though
     * they may want to override it if they need a different error recovery strategy. 
     * 
     * @param token a lookahead terminal symbol that messed parsing 
     * @param in token stream
     * @throws IOException propagated from a scanner if it has issues with the source
     * @throws Parser.Exception if Parser cannot recover
     */
	protected void recoverFromError(Symbol token, TokenStream in) throws IOException, Parser.Exception
	{
		if (token.id == 0) // end of input
			throw new Parser.Exception("Cannot recover from the syntax error");
		
		Simulator sim = new Simulator();
		in.alloc(3);
		short current_state = states[top];
		if (!tables.compressed) // then we can try "insert missing" and "replace unexpected" recoveries
		{
			short first_term_id = tables.findFirstTerminal(current_state);
			if (first_term_id >= 0)
			{
				Symbol term = new Symbol(first_term_id, _symbols[top].end, token.start);
				in.insert(term, token); // insert expected terminal before unexpected one
				in.rewind();
				if (sim.parse(in))
				{
					in.rewind();
					report.missingTokenInserted(term);
					return;
				}
				
				int offset = tables.actn_offsets[current_state];
				
				for (short term_id = (short) (first_term_id + 1); term_id < tables.n_term; term_id++)
				{
					int index = offset + term_id;
					if (index >= tables.lookaheads.length)
						break;
					if (tables.lookaheads[index] == term_id)
					{
						term.id = term_id;
						in.rewind();
						if (sim.parse(in))
						{
							in.rewind();
							report.missingTokenInserted(term);
							return;
						}
					}
				}
				in.remove(1); // unexpected token, i.e. alter stream as if we replaced 
				              // the unexpected token to an expected terminal
				term.start = token.start;
				term.end = token.end;
				
				for (short term_id = first_term_id; term_id < tables.n_term; term_id++)
				{
					int index = offset + term_id;
					if (index >= tables.lookaheads.length)
						break;
					if (tables.lookaheads[index] == term_id)
					{
						term.id = term_id;
						in.rewind();
						if (sim.parse(in))
						{
							in.rewind();
							report.misspelledTokenReplaced(term);
							return;
						}
					}
				}
				in.remove(0); // simple recoveries failed - remove all stream changes 
			}
		}
		// finally try parsing without unexpected token (as if it was "deleted")
        if (sim.parse(in)) 
        {
            in.rewind();
            report.unexpectedTokenRemoved(token);
            return;
        }
		
		// Simple recoveries failed or are not applicable. Next step is an error phrase recovery.
		/*
		 * Find a state where parser can shift "error" symbol. Discard already reduced (and shifted)
		 * productions, which are part of a phrase where unexpected terminal is found. (Note that if
		 * "error" symbol was not used by a grammar, in the end the entire input becomes an error phrase,
		 * and ... parser won't recover from it :)
		 */
		Symbol first_sym = token, last_sym = token;
		short goto_state;
		while ((goto_state = tables.findNextState(states[top], tables.error_symbol_id)) <= 0)
		{
			// parser cannot shift "error" in this state, so use the top symbol
			// as the leftmost symbol of an error phrase
			first_sym = _symbols[top];
			// and go to the previous state
			if (--top < 0)
				throw new Parser.Exception("Cannot recover from the syntax error");
		}
		Symbol error = new Symbol(tables.error_symbol_id, first_sym.start, last_sym.end); // the end is temporary
		shift(error, goto_state);

		in.rewind();
		while (!sim.parse(in))
		{
			last_sym = in.remove(0);
			if (last_sym.id == 0) // EOF
				throw new Parser.Exception("Cannot recover from the syntax error");
			in.rewind();
		}
		error.end = last_sym.end;
		in.rewind();
		report.errorPhraseRemoved(error);
	}
}
