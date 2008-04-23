/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * This file is part of Beaver Parser Generator.                       *
 * Copyright (C) 2003,2004 Alexander Demenchuk <alder@softanvil.com>.  *
 * All rights reserved.                                                *
 * See the file "LICENSE" for the terms and conditions for copying,    *
 * distribution and modification of Beaver.                            *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package beaver;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;

/**
 * Parsing Tables
 */
public final class ParsingTables
{
	/** A table with all actions */
	private final short[] actions;

	/**
	 * A table containing the lookahead for each entry in "actions" table.
	 * Used to detect "collisions".
	 */
	final short[] lookaheads;

	/**
	 * For each state, the offset into "actions" table that is used to find action for a terminal
	 * that has been fetched from the scanner.
	 */
	final int[] actn_offsets;

	/**
	 * For each state, the offset into "actions" table that is used to find a next parser's state
	 * using a nonterminal that has been created by a reduced production.
	 */
	private final int[] goto_offsets;

	/** Default action for each state */
	private final short[] default_actions;

	/**
	 * A table with encoded production information.
	 * <p/>
	 * Each slot in this table is a "structure":
	 * <pre>
	 *   short lhs_symbol_id ; // Symbol on the left-hand side of the production
	 *   short rhs_length    ; // Number of right-hand side symbols in the production
	 * </pre>
	 * where lhs_symbol_id uses high 16 bit of this structure, and rhs_length - lower 16 bits
	 */
	final int[] rule_infos;

	/** ID of the "error" nonterminal */
	final short error_symbol_id;

	/** Indicates whether action tables were compressed. */
	final boolean compressed;
	
	/** Number of terminal symbols. */
	final int n_term;

	public ParsingTables(Class impl_class)
	{
		this(getSpecAsResourceStream(impl_class));
	}
	
	/**
	 * Ensures that parser tables are loaded.
	 *
	 * @param impl_class class of the instance of the Parser
	 */
	public ParsingTables(String spec)
	{
		this(new ByteArrayInputStream(decode(spec)));
	}
	
	private ParsingTables(InputStream in)
	{
		try
		{
			DataInputStream data = new DataInputStream(new InflaterInputStream(in));
			try
			{
				int len = data.readInt();
				actions = new short[len];
				for (int i = 0; i < len; i++)
				{
					actions[i] = data.readShort();
				}
				lookaheads = new short[len];
				for (int i = 0; i < len; i++)
				{
					lookaheads[i] = data.readShort();
				}
				
				len = data.readInt();
				actn_offsets = new int[len];
				for (int i = 0; i < len; i++)
				{
					actn_offsets[i] = data.readInt();
				}
				goto_offsets = new int[len];
				for (int i = 0; i < len; i++)
				{
					goto_offsets[i] = data.readInt();
				}
				
				len = data.readInt();
				compressed = len != 0;
				if (compressed)
				{
					default_actions = new short[len];
					for (int i = 0; i < len; i++)
					{
						default_actions[i] = data.readShort();
					}
				}
				else
				{
					default_actions = null;
				}
				
				int min_nt_id = Integer.MAX_VALUE;
				len = data.readInt();
				rule_infos = new int[len];
				for (int i = 0; i < len; i++)
				{
					rule_infos[i] = data.readInt();
					min_nt_id = Math.min(min_nt_id, rule_infos[i] >>> 16);
				}
				n_term = min_nt_id;
				
				error_symbol_id = data.readShort();
			}
			finally
			{
				data.close();
			}
		}
		catch (IOException e)
		{
			throw new IllegalStateException("cannot initialize parser tables: " + e.getMessage());
		}
	}
	
	/**
	 * Scans lookaheads expected in a given state for a terminal symbol.
	 * Used in error recovery when an unexpected terminal is replaced with one that is expected.
	 * 
	 * @param state in which error occured
	 * @return ID of the expected terminal symbol or -1 if there is none
	 */
	final short findFirstTerminal(int state)
	{
		int offset = actn_offsets[state];
		for (short term_id = offset < 0 ? (short) -offset : 0; term_id < n_term; term_id++)
		{
			int index = offset + term_id;
			if (index >= lookaheads.length)
				break;
			if (lookaheads[index] == term_id)
				return term_id;
		}
		return -1;
	}

	/**
	 * Find the appropriate action for a parser in a given state with a specified terminal look-ahead.
	 *
	 * @param state     of a parser
	 * @param lookahead
	 * @return parser action
	 */
	final short findParserAction(int state, short lookahead)
	{
		int index = actn_offsets[state];
		if (index != UNUSED_OFFSET)
		{
			index += lookahead;
			if (0 <= index && index < actions.length && lookaheads[index] == lookahead)
			{
				return actions[index];
			}
		}
		return compressed ? default_actions[state] : 0;
	}

	/**
	 * Find the appropriate action for a parser in a given state with a specified nonterminal look-ahead.
	 * In this case the only possible outcomes are either a state to shift to or an accept action.
	 *
	 * @param state     of a parser
	 * @param lookahead
	 * @return parser action
	 */
	final short findNextState(int state, short lookahead)
	{
		int index = goto_offsets[state];
		if (index != UNUSED_OFFSET)
		{
			index += lookahead;
			if (0 <= index && index < actions.length && lookaheads[index] == lookahead)
			{
				return actions[index];
			}
		}
		return compressed ? default_actions[state] : 0;
	}

	static final int UNUSED_OFFSET = Integer.MIN_VALUE;
	
	static byte[] decode(String spec)
	{
		char[] chars = spec.toCharArray();
		if (chars.length % 4 != 0)
			throw new IllegalArgumentException("corrupted encoding");
		int len = chars.length / 4 * 3;
		byte[] bytes = new byte[chars[chars.length - 1] == '=' ? chars[chars.length - 2] == '=' ? len - 2 : len - 1 : len];
		
		len -= 3;
		int ci = 0, bi = 0;
		while (bi < len)
		{
			int acc = decode(chars[ci++]) << 18 | decode(chars[ci++]) << 12 | decode(chars[ci++]) << 6 | decode(chars[ci++]);
			bytes[bi++] = (byte) (acc >> 16);
			bytes[bi++] = (byte) (acc >> 8 & 0xFF);
			bytes[bi++] = (byte) (acc & 0xFF);
		}
		int acc = decode(chars[ci++]) << 18 | decode(chars[ci++]) << 12 | decode(chars[ci++]) << 6 | decode(chars[ci++]);
		bytes[bi++] = (byte) (acc >> 16);
		if (bi < bytes.length)
		{
			bytes[bi++] = (byte) (acc >> 8 & 0xFF);
			if (bi < bytes.length)
			{
				bytes[bi++] = (byte) (acc & 0xFF);
			}
		}
		return bytes;
	}
	
	static int decode(char c)
	{
		if (c <= '9')
		{
			if (c >= '0')
				return c - '0';
			if (c == '#')
				return 62;
			if (c == '$')
				return 63;
		}
		else if (c <= 'Z')
		{
			if (c >= 'A')
				return c - 'A' + 10;
			if (c == '=')
				return 0;
		}
		else if ('a' <= c && c <= 'z')
			return c - 'a' + 36;
		throw new IllegalStateException("illegal encoding character '" + c + "'");
	}
	
	static InputStream getSpecAsResourceStream(Class impl_class)
	{
		String name = impl_class.getName();
		name = name.substring(name.lastIndexOf('.') + 1) + ".spec";
		InputStream spec_stream = impl_class.getResourceAsStream(name);
		if (spec_stream == null)
			throw new IllegalStateException("parser specification not found");
		return spec_stream;
	}
}

