package soot.jimple.parser.parser;

import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;
import ca.mcgill.sable.util.*;
import java.util.*;

import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;

public class Parser
{
    public final Analysis ignoredTokens = new AnalysisAdapter();

    protected Node node;

    private final Lexer lexer;
    private final ListIterator stack = new LinkedList().listIterator();
    private int last_shift;
    private int last_pos;
    private int last_line;
    private final TokenIndex converter = new TokenIndex();
    private final int[] action = new int[2];

    private final static int SHIFT = 0;
    private final static int REDUCE = 1;
    private final static int ACCEPT = 2;
    private final static int ERROR = 3;

    protected void filter() throws ParserException, LexerException, IOException
    {
    }

    public Parser(Lexer lexer)
    {
        this.lexer = lexer;

        if(actionTable == null)
        {
            try
            {
                DataInputStream s = new DataInputStream(
                    new BufferedInputStream(
                    Parser.class.getResourceAsStream("parser.dat")));

                // read actionTable
                int length = s.readInt();
                actionTable = new int[length][][];
                for(int i = 0; i < actionTable.length; i++)
                {
                    length = s.readInt();
                    actionTable[i] = new int[length][3];
                    for(int j = 0; j < actionTable[i].length; j++)
                    {
                        for(int k = 0; k < 3; k++)
                        {
                            actionTable[i][j][k] = s.readInt();
                        }
                    }
                }

                // read gotoTable
                length = s.readInt();
                gotoTable = new int[length][][];
                for(int i = 0; i < gotoTable.length; i++)
                {
                    length = s.readInt();
                    gotoTable[i] = new int[length][2];
                    for(int j = 0; j < gotoTable[i].length; j++)
                    {
                        for(int k = 0; k < 2; k++)
                        {
                            gotoTable[i][j][k] = s.readInt();
                        }
                    }
                }

                // read errorMessages
                length = s.readInt();
                errorMessages = new String[length];
                for(int i = 0; i < errorMessages.length; i++)
                {
                    length = s.readInt();
                    StringBuffer buffer = new StringBuffer();

                    for(int j = 0; j < length; j++)
                    {
                        buffer.append(s.readChar());
                    }
                    errorMessages[i] = buffer.toString();
                }

                // read errors
                length = s.readInt();
                errors = new int[length];
                for(int i = 0; i < errors.length; i++)
                {
                    errors[i] = s.readInt();
                }

                s.close();
            }
            catch(Exception e)
            {
                throw new RuntimeException("Unable to read parser.dat.");
            }
        }
    }

    private int goTo(int index)
    {
        int state = state();
        int low = 1;
        int high = gotoTable[index].length - 1;
        int value = gotoTable[index][0][1];

        while(low <= high)
        {
            int middle = (low + high) / 2;

            if(state < gotoTable[index][middle][0])
            {
                high = middle - 1;
            }
            else if(state > gotoTable[index][middle][0])
            {
                low = middle + 1;
            }
            else
            {
                value = gotoTable[index][middle][1];
                break;
            }
        }

        return value;
    }

    private void push(int state, Node node, boolean filter) throws ParserException, LexerException, IOException
    {
        this.node = node;

        if(filter)
        {
            filter();
        }

        if(!stack.hasNext())
        {
            stack.add(new State(state, this.node));
            stack.next();
            return;
        }

        State s = (State) stack.next();
        s.state = state;
        s.node = this.node;
    }

    private int state()
    {
        State s = (State) stack.previous();
        stack.next();
        return s.state;
    }

    private Node pop()
    {
        return (Node) ((State) stack.previous()).node;
    }

    private int index(Switchable token)
    {
        converter.index = -1;
        token.apply(converter);
        return converter.index;
    }

    public Start parse() throws ParserException, LexerException, IOException
    {
        push(0, null, false);

        List ign = null;
        while(true)
        {
            while(index(lexer.peek()) == -1)
            {
                if(ign == null)
                {
                    ign = new TypedLinkedList(NodeCast.instance);
                }

                ign.add(lexer.next());
            }

            if(ign != null)
            {
                ignoredTokens.setIn(lexer.peek(), ign);
                ign = null;
            }

            last_pos = lexer.peek().getPos();
            last_line = lexer.peek().getLine();

            int index = index(lexer.peek());
            action[0] = actionTable[state()][0][1];
            action[1] = actionTable[state()][0][2];

            int low = 1;
            int high = actionTable[state()].length - 1;

            while(low <= high)
            {
                int middle = (low + high) / 2;

                if(index < actionTable[state()][middle][0])
                {
                    high = middle - 1;
                }
                else if(index > actionTable[state()][middle][0])
                {
                    low = middle + 1;
                }
                else
                {
                    action[0] = actionTable[state()][middle][1];
                    action[1] = actionTable[state()][middle][2];
                    break;
                }
            }

            switch(action[0])
            {
                case SHIFT:
                    push(action[1], lexer.next(), true);
                    last_shift = action[1];
                    break;
                case REDUCE:
                    switch(action[1])
                    {
                    case 0: { Node node = new0(); push(goTo(0), node, true); } break;
                    case 1: { Node node = new1(); push(goTo(0), node, true); } break;
                    case 2: { Node node = new2(); push(goTo(52), node, false); } break;
                    case 3: { Node node = new3(); push(goTo(52), node, false); } break;
                    case 4: { Node node = new4(); push(goTo(0), node, true); } break;
                    case 5: { Node node = new5(); push(goTo(0), node, true); } break;
                    case 6: { Node node = new6(); push(goTo(0), node, true); } break;
                    case 7: { Node node = new7(); push(goTo(0), node, true); } break;
                    case 8: { Node node = new8(); push(goTo(0), node, true); } break;
                    case 9: { Node node = new9(); push(goTo(0), node, true); } break;
                    case 10: { Node node = new10(); push(goTo(1), node, true); } break;
                    case 11: { Node node = new11(); push(goTo(1), node, true); } break;
                    case 12: { Node node = new12(); push(goTo(1), node, true); } break;
                    case 13: { Node node = new13(); push(goTo(1), node, true); } break;
                    case 14: { Node node = new14(); push(goTo(1), node, true); } break;
                    case 15: { Node node = new15(); push(goTo(1), node, true); } break;
                    case 16: { Node node = new16(); push(goTo(1), node, true); } break;
                    case 17: { Node node = new17(); push(goTo(1), node, true); } break;
                    case 18: { Node node = new18(); push(goTo(1), node, true); } break;
                    case 19: { Node node = new19(); push(goTo(1), node, true); } break;
                    case 20: { Node node = new20(); push(goTo(2), node, true); } break;
                    case 21: { Node node = new21(); push(goTo(2), node, true); } break;
                    case 22: { Node node = new22(); push(goTo(3), node, true); } break;
                    case 23: { Node node = new23(); push(goTo(4), node, true); } break;
                    case 24: { Node node = new24(); push(goTo(5), node, true); } break;
                    case 25: { Node node = new25(); push(goTo(5), node, true); } break;
                    case 26: { Node node = new26(); push(goTo(53), node, false); } break;
                    case 27: { Node node = new27(); push(goTo(53), node, false); } break;
                    case 28: { Node node = new28(); push(goTo(6), node, true); } break;
                    case 29: { Node node = new29(); push(goTo(6), node, true); } break;
                    case 30: { Node node = new30(); push(goTo(7), node, true); } break;
                    case 31: { Node node = new31(); push(goTo(7), node, true); } break;
                    case 32: { Node node = new32(); push(goTo(8), node, true); } break;
                    case 33: { Node node = new33(); push(goTo(8), node, true); } break;
                    case 34: { Node node = new34(); push(goTo(8), node, true); } break;
                    case 35: { Node node = new35(); push(goTo(8), node, true); } break;
                    case 36: { Node node = new36(); push(goTo(8), node, true); } break;
                    case 37: { Node node = new37(); push(goTo(8), node, true); } break;
                    case 38: { Node node = new38(); push(goTo(8), node, true); } break;
                    case 39: { Node node = new39(); push(goTo(8), node, true); } break;
                    case 40: { Node node = new40(); push(goTo(8), node, true); } break;
                    case 41: { Node node = new41(); push(goTo(8), node, true); } break;
                    case 42: { Node node = new42(); push(goTo(9), node, true); } break;
                    case 43: { Node node = new43(); push(goTo(9), node, true); } break;
                    case 44: { Node node = new44(); push(goTo(10), node, true); } break;
                    case 45: { Node node = new45(); push(goTo(10), node, true); } break;
                    case 46: { Node node = new46(); push(goTo(11), node, true); } break;
                    case 47: { Node node = new47(); push(goTo(12), node, true); } break;
                    case 48: { Node node = new48(); push(goTo(13), node, true); } break;
                    case 49: { Node node = new49(); push(goTo(13), node, true); } break;
                    case 50: { Node node = new50(); push(goTo(13), node, true); } break;
                    case 51: { Node node = new51(); push(goTo(13), node, true); } break;
                    case 52: { Node node = new52(); push(goTo(13), node, true); } break;
                    case 53: { Node node = new53(); push(goTo(13), node, true); } break;
                    case 54: { Node node = new54(); push(goTo(13), node, true); } break;
                    case 55: { Node node = new55(); push(goTo(13), node, true); } break;
                    case 56: { Node node = new56(); push(goTo(14), node, true); } break;
                    case 57: { Node node = new57(); push(goTo(14), node, true); } break;
                    case 58: { Node node = new58(); push(goTo(14), node, true); } break;
                    case 59: { Node node = new59(); push(goTo(14), node, true); } break;
                    case 60: { Node node = new60(); push(goTo(14), node, true); } break;
                    case 61: { Node node = new61(); push(goTo(14), node, true); } break;
                    case 62: { Node node = new62(); push(goTo(14), node, true); } break;
                    case 63: { Node node = new63(); push(goTo(14), node, true); } break;
                    case 64: { Node node = new64(); push(goTo(14), node, true); } break;
                    case 65: { Node node = new65(); push(goTo(15), node, true); } break;
                    case 66: { Node node = new66(); push(goTo(15), node, true); } break;
                    case 67: { Node node = new67(); push(goTo(54), node, false); } break;
                    case 68: { Node node = new68(); push(goTo(54), node, false); } break;
                    case 69: { Node node = new69(); push(goTo(15), node, true); } break;
                    case 70: { Node node = new70(); push(goTo(15), node, true); } break;
                    case 71: { Node node = new71(); push(goTo(15), node, true); } break;
                    case 72: { Node node = new72(); push(goTo(15), node, true); } break;
                    case 73: { Node node = new73(); push(goTo(15), node, true); } break;
                    case 74: { Node node = new74(); push(goTo(15), node, true); } break;
                    case 75: { Node node = new75(); push(goTo(16), node, true); } break;
                    case 76: { Node node = new76(); push(goTo(17), node, true); } break;
                    case 77: { Node node = new77(); push(goTo(17), node, true); } break;
                    case 78: { Node node = new78(); push(goTo(17), node, true); } break;
                    case 79: { Node node = new79(); push(goTo(55), node, false); } break;
                    case 80: { Node node = new80(); push(goTo(55), node, false); } break;
                    case 81: { Node node = new81(); push(goTo(17), node, true); } break;
                    case 82: { Node node = new82(); push(goTo(56), node, false); } break;
                    case 83: { Node node = new83(); push(goTo(56), node, false); } break;
                    case 84: { Node node = new84(); push(goTo(17), node, true); } break;
                    case 85: { Node node = new85(); push(goTo(17), node, true); } break;
                    case 86: { Node node = new86(); push(goTo(57), node, false); } break;
                    case 87: { Node node = new87(); push(goTo(57), node, false); } break;
                    case 88: { Node node = new88(); push(goTo(17), node, true); } break;
                    case 89: { Node node = new89(); push(goTo(17), node, true); } break;
                    case 90: { Node node = new90(); push(goTo(17), node, true); } break;
                    case 91: { Node node = new91(); push(goTo(18), node, true); } break;
                    case 92: { Node node = new92(); push(goTo(19), node, true); } break;
                    case 93: { Node node = new93(); push(goTo(19), node, true); } break;
                    case 94: { Node node = new94(); push(goTo(20), node, true); } break;
                    case 95: { Node node = new95(); push(goTo(21), node, true); } break;
                    case 96: { Node node = new96(); push(goTo(21), node, true); } break;
                    case 97: { Node node = new97(); push(goTo(22), node, true); } break;
                    case 98: { Node node = new98(); push(goTo(22), node, true); } break;
                    case 99: { Node node = new99(); push(goTo(22), node, true); } break;
                    case 100: { Node node = new100(); push(goTo(22), node, true); } break;
                    case 101: { Node node = new101(); push(goTo(22), node, true); } break;
                    case 102: { Node node = new102(); push(goTo(58), node, false); } break;
                    case 103: { Node node = new103(); push(goTo(58), node, false); } break;
                    case 104: { Node node = new104(); push(goTo(22), node, true); } break;
                    case 105: { Node node = new105(); push(goTo(22), node, true); } break;
                    case 106: { Node node = new106(); push(goTo(22), node, true); } break;
                    case 107: { Node node = new107(); push(goTo(22), node, true); } break;
                    case 108: { Node node = new108(); push(goTo(22), node, true); } break;
                    case 109: { Node node = new109(); push(goTo(22), node, true); } break;
                    case 110: { Node node = new110(); push(goTo(22), node, true); } break;
                    case 111: { Node node = new111(); push(goTo(22), node, true); } break;
                    case 112: { Node node = new112(); push(goTo(22), node, true); } break;
                    case 113: { Node node = new113(); push(goTo(22), node, true); } break;
                    case 114: { Node node = new114(); push(goTo(22), node, true); } break;
                    case 115: { Node node = new115(); push(goTo(22), node, true); } break;
                    case 116: { Node node = new116(); push(goTo(23), node, true); } break;
                    case 117: { Node node = new117(); push(goTo(24), node, true); } break;
                    case 118: { Node node = new118(); push(goTo(24), node, true); } break;
                    case 119: { Node node = new119(); push(goTo(25), node, true); } break;
                    case 120: { Node node = new120(); push(goTo(26), node, true); } break;
                    case 121: { Node node = new121(); push(goTo(26), node, true); } break;
                    case 122: { Node node = new122(); push(goTo(26), node, true); } break;
                    case 123: { Node node = new123(); push(goTo(27), node, true); } break;
                    case 124: { Node node = new124(); push(goTo(28), node, true); } break;
                    case 125: { Node node = new125(); push(goTo(29), node, true); } break;
                    case 126: { Node node = new126(); push(goTo(29), node, true); } break;
                    case 127: { Node node = new127(); push(goTo(29), node, true); } break;
                    case 128: { Node node = new128(); push(goTo(29), node, true); } break;
                    case 129: { Node node = new129(); push(goTo(29), node, true); } break;
                    case 130: { Node node = new130(); push(goTo(29), node, true); } break;
                    case 131: { Node node = new131(); push(goTo(29), node, true); } break;
                    case 132: { Node node = new132(); push(goTo(29), node, true); } break;
                    case 133: { Node node = new133(); push(goTo(30), node, true); } break;
                    case 134: { Node node = new134(); push(goTo(30), node, true); } break;
                    case 135: { Node node = new135(); push(goTo(30), node, true); } break;
                    case 136: { Node node = new136(); push(goTo(59), node, false); } break;
                    case 137: { Node node = new137(); push(goTo(59), node, false); } break;
                    case 138: { Node node = new138(); push(goTo(31), node, true); } break;
                    case 139: { Node node = new139(); push(goTo(31), node, true); } break;
                    case 140: { Node node = new140(); push(goTo(32), node, true); } break;
                    case 141: { Node node = new141(); push(goTo(32), node, true); } break;
                    case 142: { Node node = new142(); push(goTo(33), node, true); } break;
                    case 143: { Node node = new143(); push(goTo(33), node, true); } break;
                    case 144: { Node node = new144(); push(goTo(34), node, true); } break;
                    case 145: { Node node = new145(); push(goTo(34), node, true); } break;
                    case 146: { Node node = new146(); push(goTo(34), node, true); } break;
                    case 147: { Node node = new147(); push(goTo(34), node, true); } break;
                    case 148: { Node node = new148(); push(goTo(35), node, true); } break;
                    case 149: { Node node = new149(); push(goTo(36), node, true); } break;
                    case 150: { Node node = new150(); push(goTo(37), node, true); } break;
                    case 151: { Node node = new151(); push(goTo(37), node, true); } break;
                    case 152: { Node node = new152(); push(goTo(37), node, true); } break;
                    case 153: { Node node = new153(); push(goTo(38), node, true); } break;
                    case 154: { Node node = new154(); push(goTo(38), node, true); } break;
                    case 155: { Node node = new155(); push(goTo(39), node, true); } break;
                    case 156: { Node node = new156(); push(goTo(39), node, true); } break;
                    case 157: { Node node = new157(); push(goTo(40), node, true); } break;
                    case 158: { Node node = new158(); push(goTo(41), node, true); } break;
                    case 159: { Node node = new159(); push(goTo(41), node, true); } break;
                    case 160: { Node node = new160(); push(goTo(42), node, true); } break;
                    case 161: { Node node = new161(); push(goTo(43), node, true); } break;
                    case 162: { Node node = new162(); push(goTo(44), node, true); } break;
                    case 163: { Node node = new163(); push(goTo(44), node, true); } break;
                    case 164: { Node node = new164(); push(goTo(45), node, true); } break;
                    case 165: { Node node = new165(); push(goTo(45), node, true); } break;
                    case 166: { Node node = new166(); push(goTo(46), node, true); } break;
                    case 167: { Node node = new167(); push(goTo(46), node, true); } break;
                    case 168: { Node node = new168(); push(goTo(46), node, true); } break;
                    case 169: { Node node = new169(); push(goTo(46), node, true); } break;
                    case 170: { Node node = new170(); push(goTo(46), node, true); } break;
                    case 171: { Node node = new171(); push(goTo(47), node, true); } break;
                    case 172: { Node node = new172(); push(goTo(47), node, true); } break;
                    case 173: { Node node = new173(); push(goTo(48), node, true); } break;
                    case 174: { Node node = new174(); push(goTo(48), node, true); } break;
                    case 175: { Node node = new175(); push(goTo(48), node, true); } break;
                    case 176: { Node node = new176(); push(goTo(48), node, true); } break;
                    case 177: { Node node = new177(); push(goTo(48), node, true); } break;
                    case 178: { Node node = new178(); push(goTo(48), node, true); } break;
                    case 179: { Node node = new179(); push(goTo(48), node, true); } break;
                    case 180: { Node node = new180(); push(goTo(48), node, true); } break;
                    case 181: { Node node = new181(); push(goTo(48), node, true); } break;
                    case 182: { Node node = new182(); push(goTo(48), node, true); } break;
                    case 183: { Node node = new183(); push(goTo(48), node, true); } break;
                    case 184: { Node node = new184(); push(goTo(48), node, true); } break;
                    case 185: { Node node = new185(); push(goTo(48), node, true); } break;
                    case 186: { Node node = new186(); push(goTo(48), node, true); } break;
                    case 187: { Node node = new187(); push(goTo(48), node, true); } break;
                    case 188: { Node node = new188(); push(goTo(48), node, true); } break;
                    case 189: { Node node = new189(); push(goTo(48), node, true); } break;
                    case 190: { Node node = new190(); push(goTo(48), node, true); } break;
                    case 191: { Node node = new191(); push(goTo(48), node, true); } break;
                    case 192: { Node node = new192(); push(goTo(48), node, true); } break;
                    case 193: { Node node = new193(); push(goTo(49), node, true); } break;
                    case 194: { Node node = new194(); push(goTo(49), node, true); } break;
                    case 195: { Node node = new195(); push(goTo(50), node, true); } break;
                    case 196: { Node node = new196(); push(goTo(50), node, true); } break;
                    case 197: { Node node = new197(); push(goTo(50), node, true); } break;
                    case 198: { Node node = new198(); push(goTo(51), node, true); } break;
                    case 199: { Node node = new199(); push(goTo(51), node, true); } break;
                    }
                    break;
                case ACCEPT:
                    {
                        EOF node2 = (EOF) lexer.next();
                        PFile node1 = (PFile) pop();
                        Start node = new Start(node1, node2);
                        return node;
                    }
                case ERROR:
                    throw new ParserException(
                        "[" + last_line + "," + last_pos + "] " +
                        errorMessages[errors[action[1]]]);
            }
        }
    }

    Node new0()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = null;
        PExtendsClause node4 = null;
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = null;
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new1()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = null;
        PExtendsClause node4 = null;
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = (XPModifier) pop();
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new2()
    {
        PModifier node2 = (PModifier) pop();
        XPModifier node1 = (XPModifier) pop();
        X1PModifier node = new X1PModifier(node1, node2);
        return node;
    }

    Node new3()
    {
        PModifier node1 = (PModifier) pop();
        X2PModifier node = new X2PModifier(node1);
        return node;
    }

    Node new4()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = null;
        PExtendsClause node4 = (PExtendsClause) pop();
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = null;
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new5()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = null;
        PExtendsClause node4 = (PExtendsClause) pop();
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = (XPModifier) pop();
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new6()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = (PImplementsClause) pop();
        PExtendsClause node4 = null;
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = null;
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new7()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = (PImplementsClause) pop();
        PExtendsClause node4 = null;
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = (XPModifier) pop();
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new8()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = (PImplementsClause) pop();
        PExtendsClause node4 = (PExtendsClause) pop();
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = null;
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new9()
    {
        PFileBody node6 = (PFileBody) pop();
        PImplementsClause node5 = (PImplementsClause) pop();
        PExtendsClause node4 = (PExtendsClause) pop();
        PClassName node3 = (PClassName) pop();
        PFileType node2 = (PFileType) pop();
        XPModifier node1 = (XPModifier) pop();
        AFile node = new AFile(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new10()
    {
        TAbstract node1 = (TAbstract) pop();
        AAbstractModifier node = new AAbstractModifier(node1);
        return node;
    }

    Node new11()
    {
        TFinal node1 = (TFinal) pop();
        AFinalModifier node = new AFinalModifier(node1);
        return node;
    }

    Node new12()
    {
        TNative node1 = (TNative) pop();
        ANativeModifier node = new ANativeModifier(node1);
        return node;
    }

    Node new13()
    {
        TPublic node1 = (TPublic) pop();
        APublicModifier node = new APublicModifier(node1);
        return node;
    }

    Node new14()
    {
        TProtected node1 = (TProtected) pop();
        AProtectedModifier node = new AProtectedModifier(node1);
        return node;
    }

    Node new15()
    {
        TPrivate node1 = (TPrivate) pop();
        APrivateModifier node = new APrivateModifier(node1);
        return node;
    }

    Node new16()
    {
        TStatic node1 = (TStatic) pop();
        AStaticModifier node = new AStaticModifier(node1);
        return node;
    }

    Node new17()
    {
        TSynchronized node1 = (TSynchronized) pop();
        ASynchronizedModifier node = new ASynchronizedModifier(node1);
        return node;
    }

    Node new18()
    {
        TTransient node1 = (TTransient) pop();
        ATransientModifier node = new ATransientModifier(node1);
        return node;
    }

    Node new19()
    {
        TVolatile node1 = (TVolatile) pop();
        AVolatileModifier node = new AVolatileModifier(node1);
        return node;
    }

    Node new20()
    {
        TClass node1 = (TClass) pop();
        AClassFileType node = new AClassFileType(node1);
        return node;
    }

    Node new21()
    {
        TInterface node1 = (TInterface) pop();
        AInterfaceFileType node = new AInterfaceFileType(node1);
        return node;
    }

    Node new22()
    {
        PClassName node2 = (PClassName) pop();
        TExtends node1 = (TExtends) pop();
        AExtendsClause node = new AExtendsClause(node1, node2);
        return node;
    }

    Node new23()
    {
        PClassNameList node2 = (PClassNameList) pop();
        TImplements node1 = (TImplements) pop();
        AImplementsClause node = new AImplementsClause(node1, node2);
        return node;
    }

    Node new24()
    {
        TRBrace node3 = (TRBrace) pop();
        XPMember node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFileBody node = new AFileBody(node1, node2, node3);
        return node;
    }

    Node new25()
    {
        TRBrace node3 = (TRBrace) pop();
        XPMember node2 = (XPMember) pop();
        TLBrace node1 = (TLBrace) pop();
        AFileBody node = new AFileBody(node1, node2, node3);
        return node;
    }

    Node new26()
    {
        PMember node2 = (PMember) pop();
        XPMember node1 = (XPMember) pop();
        X1PMember node = new X1PMember(node1, node2);
        return node;
    }

    Node new27()
    {
        PMember node1 = (PMember) pop();
        X2PMember node = new X2PMember(node1);
        return node;
    }

    Node new28()
    {
        PName node1 = (PName) pop();
        ASingleNameList node = new ASingleNameList(node1);
        return node;
    }

    Node new29()
    {
        PNameList node3 = (PNameList) pop();
        TComma node2 = (TComma) pop();
        PName node1 = (PName) pop();
        AMultiNameList node = new AMultiNameList(node1, node2, node3);
        return node;
    }

    Node new30()
    {
        PClassName node1 = (PClassName) pop();
        AClassNameSingleClassNameList node = new AClassNameSingleClassNameList(node1);
        return node;
    }

    Node new31()
    {
        PClassNameList node3 = (PClassNameList) pop();
        TComma node2 = (TComma) pop();
        PClassName node1 = (PClassName) pop();
        AClassNameMultiClassNameList node = new AClassNameMultiClassNameList(node1, node2, node3);
        return node;
    }

    Node new32()
    {
        TSemicolon node4 = (TSemicolon) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AFieldMember node = new AFieldMember(node1, node2, node3, node4);
        return node;
    }

    Node new33()
    {
        TSemicolon node4 = (TSemicolon) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AFieldMember node = new AFieldMember(node1, node2, node3, node4);
        return node;
    }

    Node new34()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new35()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new36()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new37()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new38()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = (PThrowsClause) pop();
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new39()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = (PThrowsClause) pop();
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new40()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = (PThrowsClause) pop();
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new41()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = (PThrowsClause) pop();
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        PName node3 = (PName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new42()
    {
        TVoid node1 = (TVoid) pop();
        AVoidType node = new AVoidType(node1);
        return node;
    }

    Node new43()
    {
        PNonvoidType node1 = (PNonvoidType) pop();
        ANovoidType node = new ANovoidType(node1);
        return node;
    }

    Node new44()
    {
        PParameter node1 = (PParameter) pop();
        ASingleParameterList node = new ASingleParameterList(node1);
        return node;
    }

    Node new45()
    {
        PParameterList node3 = (PParameterList) pop();
        TComma node2 = (TComma) pop();
        PParameter node1 = (PParameter) pop();
        AMultiParameterList node = new AMultiParameterList(node1, node2, node3);
        return node;
    }

    Node new46()
    {
        PNonvoidType node1 = (PNonvoidType) pop();
        AParameter node = new AParameter(node1);
        return node;
    }

    Node new47()
    {
        PClassNameList node2 = (PClassNameList) pop();
        TThrows node1 = (TThrows) pop();
        AThrowsClause node = new AThrowsClause(node1, node2);
        return node;
    }

    Node new48()
    {
        TBoolean node1 = (TBoolean) pop();
        ABooleanBaseTypeNoName node = new ABooleanBaseTypeNoName(node1);
        return node;
    }

    Node new49()
    {
        TByte node1 = (TByte) pop();
        AByteBaseTypeNoName node = new AByteBaseTypeNoName(node1);
        return node;
    }

    Node new50()
    {
        TChar node1 = (TChar) pop();
        ACharBaseTypeNoName node = new ACharBaseTypeNoName(node1);
        return node;
    }

    Node new51()
    {
        TShort node1 = (TShort) pop();
        AShortBaseTypeNoName node = new AShortBaseTypeNoName(node1);
        return node;
    }

    Node new52()
    {
        TInt node1 = (TInt) pop();
        AIntBaseTypeNoName node = new AIntBaseTypeNoName(node1);
        return node;
    }

    Node new53()
    {
        TLong node1 = (TLong) pop();
        ALongBaseTypeNoName node = new ALongBaseTypeNoName(node1);
        return node;
    }

    Node new54()
    {
        TFloat node1 = (TFloat) pop();
        AFloatBaseTypeNoName node = new AFloatBaseTypeNoName(node1);
        return node;
    }

    Node new55()
    {
        TDouble node1 = (TDouble) pop();
        ADoubleBaseTypeNoName node = new ADoubleBaseTypeNoName(node1);
        return node;
    }

    Node new56()
    {
        TBoolean node1 = (TBoolean) pop();
        ABooleanBaseType node = new ABooleanBaseType(node1);
        return node;
    }

    Node new57()
    {
        TByte node1 = (TByte) pop();
        AByteBaseType node = new AByteBaseType(node1);
        return node;
    }

    Node new58()
    {
        TChar node1 = (TChar) pop();
        ACharBaseType node = new ACharBaseType(node1);
        return node;
    }

    Node new59()
    {
        TShort node1 = (TShort) pop();
        AShortBaseType node = new AShortBaseType(node1);
        return node;
    }

    Node new60()
    {
        TInt node1 = (TInt) pop();
        AIntBaseType node = new AIntBaseType(node1);
        return node;
    }

    Node new61()
    {
        TLong node1 = (TLong) pop();
        ALongBaseType node = new ALongBaseType(node1);
        return node;
    }

    Node new62()
    {
        TFloat node1 = (TFloat) pop();
        AFloatBaseType node = new AFloatBaseType(node1);
        return node;
    }

    Node new63()
    {
        TDouble node1 = (TDouble) pop();
        ADoubleBaseType node = new ADoubleBaseType(node1);
        return node;
    }

    Node new64()
    {
        PClassName node1 = (PClassName) pop();
        AClassNameBaseType node = new AClassNameBaseType(node1);
        return node;
    }

    Node new65()
    {
        XPArrayBrackets node2 = null;
        PBaseTypeNoName node1 = (PBaseTypeNoName) pop();
        ABaseNonvoidType node = new ABaseNonvoidType(node1, node2);
        return node;
    }

    Node new66()
    {
        XPArrayBrackets node2 = (XPArrayBrackets) pop();
        PBaseTypeNoName node1 = (PBaseTypeNoName) pop();
        ABaseNonvoidType node = new ABaseNonvoidType(node1, node2);
        return node;
    }

    Node new67()
    {
        PArrayBrackets node2 = (PArrayBrackets) pop();
        XPArrayBrackets node1 = (XPArrayBrackets) pop();
        X1PArrayBrackets node = new X1PArrayBrackets(node1, node2);
        return node;
    }

    Node new68()
    {
        PArrayBrackets node1 = (PArrayBrackets) pop();
        X2PArrayBrackets node = new X2PArrayBrackets(node1);
        return node;
    }

    Node new69()
    {
        XPArrayBrackets node2 = null;
        TQuotedName node1 = (TQuotedName) pop();
        AQuotedNonvoidType node = new AQuotedNonvoidType(node1, node2);
        return node;
    }

    Node new70()
    {
        XPArrayBrackets node2 = (XPArrayBrackets) pop();
        TQuotedName node1 = (TQuotedName) pop();
        AQuotedNonvoidType node = new AQuotedNonvoidType(node1, node2);
        return node;
    }

    Node new71()
    {
        XPArrayBrackets node2 = null;
        TIdentifier node1 = (TIdentifier) pop();
        AIdentNonvoidType node = new AIdentNonvoidType(node1, node2);
        return node;
    }

    Node new72()
    {
        XPArrayBrackets node2 = (XPArrayBrackets) pop();
        TIdentifier node1 = (TIdentifier) pop();
        AIdentNonvoidType node = new AIdentNonvoidType(node1, node2);
        return node;
    }

    Node new73()
    {
        XPArrayBrackets node2 = null;
        TFullIdentifier node1 = (TFullIdentifier) pop();
        AFullIdentNonvoidType node = new AFullIdentNonvoidType(node1, node2);
        return node;
    }

    Node new74()
    {
        XPArrayBrackets node2 = (XPArrayBrackets) pop();
        TFullIdentifier node1 = (TFullIdentifier) pop();
        AFullIdentNonvoidType node = new AFullIdentNonvoidType(node1, node2);
        return node;
    }

    Node new75()
    {
        TRBracket node2 = (TRBracket) pop();
        TLBracket node1 = (TLBracket) pop();
        AArrayBrackets node = new AArrayBrackets(node1, node2);
        return node;
    }

    Node new76()
    {
        TSemicolon node1 = (TSemicolon) pop();
        AEmptyMethodBody node = new AEmptyMethodBody(node1);
        return node;
    }

    Node new77()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = null;
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new78()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = null;
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new79()
    {
        PDeclaration node2 = (PDeclaration) pop();
        XPDeclaration node1 = (XPDeclaration) pop();
        X1PDeclaration node = new X1PDeclaration(node1, node2);
        return node;
    }

    Node new80()
    {
        PDeclaration node1 = (PDeclaration) pop();
        X2PDeclaration node = new X2PDeclaration(node1);
        return node;
    }

    Node new81()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new82()
    {
        PStatement node2 = (PStatement) pop();
        XPStatement node1 = (XPStatement) pop();
        X1PStatement node = new X1PStatement(node1, node2);
        return node;
    }

    Node new83()
    {
        PStatement node1 = (PStatement) pop();
        X2PStatement node = new X2PStatement(node1);
        return node;
    }

    Node new84()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new85()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = null;
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new86()
    {
        PCatchClause node2 = (PCatchClause) pop();
        XPCatchClause node1 = (XPCatchClause) pop();
        X1PCatchClause node = new X1PCatchClause(node1, node2);
        return node;
    }

    Node new87()
    {
        PCatchClause node1 = (PCatchClause) pop();
        X2PCatchClause node = new X2PCatchClause(node1);
        return node;
    }

    Node new88()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = null;
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new89()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new90()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new91()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PLocalNameList node2 = (PLocalNameList) pop();
        PJimpleType node1 = (PJimpleType) pop();
        ADeclaration node = new ADeclaration(node1, node2, node3);
        return node;
    }

    Node new92()
    {
        TUnknown node1 = (TUnknown) pop();
        AUnknownJimpleType node = new AUnknownJimpleType(node1);
        return node;
    }

    Node new93()
    {
        PNonvoidType node1 = (PNonvoidType) pop();
        ANonvoidJimpleType node = new ANonvoidJimpleType(node1);
        return node;
    }

    Node new94()
    {
        TIdentifier node1 = (TIdentifier) pop();
        ALocalName node = new ALocalName(node1);
        return node;
    }

    Node new95()
    {
        PLocalName node1 = (PLocalName) pop();
        ASingleLocalNameList node = new ASingleLocalNameList(node1);
        return node;
    }

    Node new96()
    {
        PLocalNameList node3 = (PLocalNameList) pop();
        TComma node2 = (TComma) pop();
        PLocalName node1 = (PLocalName) pop();
        AMultiLocalNameList node = new AMultiLocalNameList(node1, node2, node3);
        return node;
    }

    Node new97()
    {
        TColon node2 = (TColon) pop();
        PLabelName node1 = (PLabelName) pop();
        ALabelStatement node = new ALabelStatement(node1, node2);
        return node;
    }

    Node new98()
    {
        TSemicolon node2 = (TSemicolon) pop();
        TBreakpoint node1 = (TBreakpoint) pop();
        ABreakpointStatement node = new ABreakpointStatement(node1, node2);
        return node;
    }

    Node new99()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TEntermonitor node1 = (TEntermonitor) pop();
        AEntermonitorStatement node = new AEntermonitorStatement(node1, node2, node3);
        return node;
    }

    Node new100()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TExitmonitor node1 = (TExitmonitor) pop();
        AExitmonitorStatement node = new AExitmonitorStatement(node1, node2, node3);
        return node;
    }

    Node new101()
    {
        TSemicolon node8 = (TSemicolon) pop();
        TRBrace node7 = (TRBrace) pop();
        XPCaseStmt node6 = (XPCaseStmt) pop();
        TLBrace node5 = (TLBrace) pop();
        TRParen node4 = (TRParen) pop();
        PImmediate node3 = (PImmediate) pop();
        TLParen node2 = (TLParen) pop();
        PSwitch node1 = (PSwitch) pop();
        ASwitchStatement node = new ASwitchStatement(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new102()
    {
        PCaseStmt node2 = (PCaseStmt) pop();
        XPCaseStmt node1 = (XPCaseStmt) pop();
        X1PCaseStmt node = new X1PCaseStmt(node1, node2);
        return node;
    }

    Node new103()
    {
        PCaseStmt node1 = (PCaseStmt) pop();
        X2PCaseStmt node = new X2PCaseStmt(node1);
        return node;
    }

    Node new104()
    {
        TSemicolon node5 = (TSemicolon) pop();
        PType node4 = (PType) pop();
        TAtIdentifier node3 = (TAtIdentifier) pop();
        TColonEquals node2 = (TColonEquals) pop();
        PLocalName node1 = (PLocalName) pop();
        AIdentityStatement node = new AIdentityStatement(node1, node2, node3, node4, node5);
        return node;
    }

    Node new105()
    {
        TSemicolon node4 = (TSemicolon) pop();
        TAtIdentifier node3 = (TAtIdentifier) pop();
        TColonEquals node2 = (TColonEquals) pop();
        PLocalName node1 = (PLocalName) pop();
        AIdentityNoTypeStatement node = new AIdentityNoTypeStatement(node1, node2, node3, node4);
        return node;
    }

    Node new106()
    {
        TSemicolon node4 = (TSemicolon) pop();
        PExpression node3 = (PExpression) pop();
        TEquals node2 = (TEquals) pop();
        PVariable node1 = (PVariable) pop();
        AAssignStatement node = new AAssignStatement(node1, node2, node3, node4);
        return node;
    }

    Node new107()
    {
        PGotoStmt node3 = (PGotoStmt) pop();
        PBoolExpr node2 = (PBoolExpr) pop();
        TIf node1 = (TIf) pop();
        AIfStatement node = new AIfStatement(node1, node2, node3);
        return node;
    }

    Node new108()
    {
        PGotoStmt node1 = (PGotoStmt) pop();
        AGotoStatement node = new AGotoStatement(node1);
        return node;
    }

    Node new109()
    {
        TSemicolon node2 = (TSemicolon) pop();
        TNop node1 = (TNop) pop();
        ANopStatement node = new ANopStatement(node1, node2);
        return node;
    }

    Node new110()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = null;
        TRet node1 = (TRet) pop();
        ARetStatement node = new ARetStatement(node1, node2, node3);
        return node;
    }

    Node new111()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TRet node1 = (TRet) pop();
        ARetStatement node = new ARetStatement(node1, node2, node3);
        return node;
    }

    Node new112()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = null;
        TReturn node1 = (TReturn) pop();
        AReturnStatement node = new AReturnStatement(node1, node2, node3);
        return node;
    }

    Node new113()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TReturn node1 = (TReturn) pop();
        AReturnStatement node = new AReturnStatement(node1, node2, node3);
        return node;
    }

    Node new114()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TThrow node1 = (TThrow) pop();
        AThrowStatement node = new AThrowStatement(node1, node2, node3);
        return node;
    }

    Node new115()
    {
        TSemicolon node2 = (TSemicolon) pop();
        PInvokeExpr node1 = (PInvokeExpr) pop();
        AInvokeStatement node = new AInvokeStatement(node1, node2);
        return node;
    }

    Node new116()
    {
        TIdentifier node1 = (TIdentifier) pop();
        ALabelName node = new ALabelName(node1);
        return node;
    }

    Node new117()
    {
        TLookupswitch node1 = (TLookupswitch) pop();
        ALookupSwitch node = new ALookupSwitch(node1);
        return node;
    }

    Node new118()
    {
        TTableswitch node1 = (TTableswitch) pop();
        ATableSwitch node = new ATableSwitch(node1);
        return node;
    }

    Node new119()
    {
        PGotoStmt node3 = (PGotoStmt) pop();
        TColon node2 = (TColon) pop();
        PCaseLabel node1 = (PCaseLabel) pop();
        ACaseStmt node = new ACaseStmt(node1, node2, node3);
        return node;
    }

    Node new120()
    {
        TIntegerConstant node3 = (TIntegerConstant) pop();
        TMinus node2 = null;
        TCase node1 = (TCase) pop();
        AConstantCaseLabel node = new AConstantCaseLabel(node1, node2, node3);
        return node;
    }

    Node new121()
    {
        TIntegerConstant node3 = (TIntegerConstant) pop();
        TMinus node2 = (TMinus) pop();
        TCase node1 = (TCase) pop();
        AConstantCaseLabel node = new AConstantCaseLabel(node1, node2, node3);
        return node;
    }

    Node new122()
    {
        TDefault node1 = (TDefault) pop();
        ADefaultCaseLabel node = new ADefaultCaseLabel(node1);
        return node;
    }

    Node new123()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PLabelName node2 = (PLabelName) pop();
        TGoto node1 = (TGoto) pop();
        AGotoStmt node = new AGotoStmt(node1, node2, node3);
        return node;
    }

    Node new124()
    {
        TSemicolon node9 = (TSemicolon) pop();
        PLabelName node8 = (PLabelName) pop();
        TWith node7 = (TWith) pop();
        PLabelName node6 = (PLabelName) pop();
        TTo node5 = (TTo) pop();
        PLabelName node4 = (PLabelName) pop();
        TFrom node3 = (TFrom) pop();
        PClassName node2 = (PClassName) pop();
        TCatch node1 = (TCatch) pop();
        ACatchClause node = new ACatchClause(node1, node2, node3, node4, node5, node6, node7, node8, node9);
        return node;
    }

    Node new125()
    {
        PNewExpr node1 = (PNewExpr) pop();
        ANewExpression node = new ANewExpression(node1);
        return node;
    }

    Node new126()
    {
        PLocalName node4 = (PLocalName) pop();
        TRParen node3 = (TRParen) pop();
        PNonvoidType node2 = (PNonvoidType) pop();
        TLParen node1 = (TLParen) pop();
        ACastExpression node = new ACastExpression(node1, node2, node3, node4);
        return node;
    }

    Node new127()
    {
        PNonvoidType node3 = (PNonvoidType) pop();
        TInstanceof node2 = (TInstanceof) pop();
        PImmediate node1 = (PImmediate) pop();
        AInstanceofExpression node = new AInstanceofExpression(node1, node2, node3);
        return node;
    }

    Node new128()
    {
        PInvokeExpr node1 = (PInvokeExpr) pop();
        AInvokeExpression node = new AInvokeExpression(node1);
        return node;
    }

    Node new129()
    {
        PReference node1 = (PReference) pop();
        AReferenceExpression node = new AReferenceExpression(node1);
        return node;
    }

    Node new130()
    {
        PBinopExpr node1 = (PBinopExpr) pop();
        ABinopExpression node = new ABinopExpression(node1);
        return node;
    }

    Node new131()
    {
        PUnopExpr node1 = (PUnopExpr) pop();
        AUnopExpression node = new AUnopExpression(node1);
        return node;
    }

    Node new132()
    {
        PImmediate node1 = (PImmediate) pop();
        AImmediateExpression node = new AImmediateExpression(node1);
        return node;
    }

    Node new133()
    {
        PBaseType node2 = (PBaseType) pop();
        TNew node1 = (TNew) pop();
        ASimpleNewExpr node = new ASimpleNewExpr(node1, node2);
        return node;
    }

    Node new134()
    {
        PFixedArrayDescriptor node5 = (PFixedArrayDescriptor) pop();
        TRParen node4 = (TRParen) pop();
        PNonvoidType node3 = (PNonvoidType) pop();
        TLParen node2 = (TLParen) pop();
        TNewarray node1 = (TNewarray) pop();
        AArrayNewExpr node = new AArrayNewExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new135()
    {
        XPArrayDescriptor node5 = (XPArrayDescriptor) pop();
        TRParen node4 = (TRParen) pop();
        PBaseType node3 = (PBaseType) pop();
        TLParen node2 = (TLParen) pop();
        TNewmultiarray node1 = (TNewmultiarray) pop();
        AMultiNewExpr node = new AMultiNewExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new136()
    {
        PArrayDescriptor node2 = (PArrayDescriptor) pop();
        XPArrayDescriptor node1 = (XPArrayDescriptor) pop();
        X1PArrayDescriptor node = new X1PArrayDescriptor(node1, node2);
        return node;
    }

    Node new137()
    {
        PArrayDescriptor node1 = (PArrayDescriptor) pop();
        X2PArrayDescriptor node = new X2PArrayDescriptor(node1);
        return node;
    }

    Node new138()
    {
        TRBracket node3 = (TRBracket) pop();
        PImmediate node2 = null;
        TLBracket node1 = (TLBracket) pop();
        AArrayDescriptor node = new AArrayDescriptor(node1, node2, node3);
        return node;
    }

    Node new139()
    {
        TRBracket node3 = (TRBracket) pop();
        PImmediate node2 = (PImmediate) pop();
        TLBracket node1 = (TLBracket) pop();
        AArrayDescriptor node = new AArrayDescriptor(node1, node2, node3);
        return node;
    }

    Node new140()
    {
        PReference node1 = (PReference) pop();
        AReferenceVariable node = new AReferenceVariable(node1);
        return node;
    }

    Node new141()
    {
        PLocalName node1 = (PLocalName) pop();
        ALocalVariable node = new ALocalVariable(node1);
        return node;
    }

    Node new142()
    {
        PBinopExpr node1 = (PBinopExpr) pop();
        ABinopBoolExpr node = new ABinopBoolExpr(node1);
        return node;
    }

    Node new143()
    {
        PUnopExpr node1 = (PUnopExpr) pop();
        AUnopBoolExpr node = new AUnopBoolExpr(node1);
        return node;
    }

    Node new144()
    {
        TRParen node7 = (TRParen) pop();
        PArgList node6 = null;
        TLParen node5 = (TLParen) pop();
        PMethodSignature node4 = (PMethodSignature) pop();
        TDot node3 = (TDot) pop();
        PLocalName node2 = (PLocalName) pop();
        PNonstaticInvoke node1 = (PNonstaticInvoke) pop();
        ANonstaticInvokeExpr node = new ANonstaticInvokeExpr(node1, node2, node3, node4, node5, node6, node7);
        return node;
    }

    Node new145()
    {
        TRParen node7 = (TRParen) pop();
        PArgList node6 = (PArgList) pop();
        TLParen node5 = (TLParen) pop();
        PMethodSignature node4 = (PMethodSignature) pop();
        TDot node3 = (TDot) pop();
        PLocalName node2 = (PLocalName) pop();
        PNonstaticInvoke node1 = (PNonstaticInvoke) pop();
        ANonstaticInvokeExpr node = new ANonstaticInvokeExpr(node1, node2, node3, node4, node5, node6, node7);
        return node;
    }

    Node new146()
    {
        TRParen node5 = (TRParen) pop();
        PArgList node4 = null;
        TLParen node3 = (TLParen) pop();
        PMethodSignature node2 = (PMethodSignature) pop();
        TStaticinvoke node1 = (TStaticinvoke) pop();
        AStaticInvokeExpr node = new AStaticInvokeExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new147()
    {
        TRParen node5 = (TRParen) pop();
        PArgList node4 = (PArgList) pop();
        TLParen node3 = (TLParen) pop();
        PMethodSignature node2 = (PMethodSignature) pop();
        TStaticinvoke node1 = (TStaticinvoke) pop();
        AStaticInvokeExpr node = new AStaticInvokeExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new148()
    {
        PImmediate node3 = (PImmediate) pop();
        PBinop node2 = (PBinop) pop();
        PImmediate node1 = (PImmediate) pop();
        ABinopExpr node = new ABinopExpr(node1, node2, node3);
        return node;
    }

    Node new149()
    {
        PImmediate node2 = (PImmediate) pop();
        PUnop node1 = (PUnop) pop();
        AUnopExpr node = new AUnopExpr(node1, node2);
        return node;
    }

    Node new150()
    {
        TSpecialinvoke node1 = (TSpecialinvoke) pop();
        ASpecialNonstaticInvoke node = new ASpecialNonstaticInvoke(node1);
        return node;
    }

    Node new151()
    {
        TVirtualinvoke node1 = (TVirtualinvoke) pop();
        AVirtualNonstaticInvoke node = new AVirtualNonstaticInvoke(node1);
        return node;
    }

    Node new152()
    {
        TInterfaceinvoke node1 = (TInterfaceinvoke) pop();
        AInterfaceNonstaticInvoke node = new AInterfaceNonstaticInvoke(node1);
        return node;
    }

    Node new153()
    {
        TCmpgt node9 = (TCmpgt) pop();
        TRParen node8 = (TRParen) pop();
        PParameterList node7 = null;
        TLParen node6 = (TLParen) pop();
        PName node5 = (PName) pop();
        PType node4 = (PType) pop();
        TColon node3 = (TColon) pop();
        PClassName node2 = (PClassName) pop();
        TCmplt node1 = (TCmplt) pop();
        AMethodSignature node = new AMethodSignature(node1, node2, node3, node4, node5, node6, node7, node8, node9);
        return node;
    }

    Node new154()
    {
        TCmpgt node9 = (TCmpgt) pop();
        TRParen node8 = (TRParen) pop();
        PParameterList node7 = (PParameterList) pop();
        TLParen node6 = (TLParen) pop();
        PName node5 = (PName) pop();
        PType node4 = (PType) pop();
        TColon node3 = (TColon) pop();
        PClassName node2 = (PClassName) pop();
        TCmplt node1 = (TCmplt) pop();
        AMethodSignature node = new AMethodSignature(node1, node2, node3, node4, node5, node6, node7, node8, node9);
        return node;
    }

    Node new155()
    {
        PArrayRef node1 = (PArrayRef) pop();
        AArrayReference node = new AArrayReference(node1);
        return node;
    }

    Node new156()
    {
        PFieldRef node1 = (PFieldRef) pop();
        AFieldReference node = new AFieldReference(node1);
        return node;
    }

    Node new157()
    {
        PFixedArrayDescriptor node2 = (PFixedArrayDescriptor) pop();
        TIdentifier node1 = (TIdentifier) pop();
        AArrayRef node = new AArrayRef(node1, node2);
        return node;
    }

    Node new158()
    {
        PFieldSignature node3 = (PFieldSignature) pop();
        TDot node2 = (TDot) pop();
        PLocalName node1 = (PLocalName) pop();
        ALocalFieldRef node = new ALocalFieldRef(node1, node2, node3);
        return node;
    }

    Node new159()
    {
        PFieldSignature node1 = (PFieldSignature) pop();
        ASigFieldRef node = new ASigFieldRef(node1);
        return node;
    }

    Node new160()
    {
        TCmpgt node6 = (TCmpgt) pop();
        PName node5 = (PName) pop();
        PType node4 = (PType) pop();
        TColon node3 = (TColon) pop();
        PClassName node2 = (PClassName) pop();
        TCmplt node1 = (TCmplt) pop();
        AFieldSignature node = new AFieldSignature(node1, node2, node3, node4, node5, node6);
        return node;
    }

    Node new161()
    {
        TRBracket node3 = (TRBracket) pop();
        PImmediate node2 = (PImmediate) pop();
        TLBracket node1 = (TLBracket) pop();
        AFixedArrayDescriptor node = new AFixedArrayDescriptor(node1, node2, node3);
        return node;
    }

    Node new162()
    {
        PImmediate node1 = (PImmediate) pop();
        ASingleArgList node = new ASingleArgList(node1);
        return node;
    }

    Node new163()
    {
        PArgList node3 = (PArgList) pop();
        TComma node2 = (TComma) pop();
        PImmediate node1 = (PImmediate) pop();
        AMultiArgList node = new AMultiArgList(node1, node2, node3);
        return node;
    }

    Node new164()
    {
        PLocalName node1 = (PLocalName) pop();
        ALocalImmediate node = new ALocalImmediate(node1);
        return node;
    }

    Node new165()
    {
        PConstant node1 = (PConstant) pop();
        AConstantImmediate node = new AConstantImmediate(node1);
        return node;
    }

    Node new166()
    {
        TIntegerConstant node2 = (TIntegerConstant) pop();
        TMinus node1 = null;
        AIntegerConstant node = new AIntegerConstant(node1, node2);
        return node;
    }

    Node new167()
    {
        TIntegerConstant node2 = (TIntegerConstant) pop();
        TMinus node1 = (TMinus) pop();
        AIntegerConstant node = new AIntegerConstant(node1, node2);
        return node;
    }

    Node new168()
    {
        PFloatExt node2 = (PFloatExt) pop();
        TMinus node1 = null;
        AFloatConstant node = new AFloatConstant(node1, node2);
        return node;
    }

    Node new169()
    {
        PFloatExt node2 = (PFloatExt) pop();
        TMinus node1 = (TMinus) pop();
        AFloatConstant node = new AFloatConstant(node1, node2);
        return node;
    }

    Node new170()
    {
        TStringConstant node1 = (TStringConstant) pop();
        AStringConstant node = new AStringConstant(node1);
        return node;
    }

    Node new171()
    {
        TFloatConstant node1 = (TFloatConstant) pop();
        AFloatCstFloatExt node = new AFloatCstFloatExt(node1);
        return node;
    }

    Node new172()
    {
        TFloatDegenerate node1 = (TFloatDegenerate) pop();
        AFloatDegenerateFloatExt node = new AFloatDegenerateFloatExt(node1);
        return node;
    }

    Node new173()
    {
        TAnd node1 = (TAnd) pop();
        AAndBinop node = new AAndBinop(node1);
        return node;
    }

    Node new174()
    {
        TOr node1 = (TOr) pop();
        AOrBinop node = new AOrBinop(node1);
        return node;
    }

    Node new175()
    {
        TXor node1 = (TXor) pop();
        AXorBinop node = new AXorBinop(node1);
        return node;
    }

    Node new176()
    {
        TMod node1 = (TMod) pop();
        AModBinop node = new AModBinop(node1);
        return node;
    }

    Node new177()
    {
        TCmp node1 = (TCmp) pop();
        ACmpBinop node = new ACmpBinop(node1);
        return node;
    }

    Node new178()
    {
        TCmpg node1 = (TCmpg) pop();
        ACmpgBinop node = new ACmpgBinop(node1);
        return node;
    }

    Node new179()
    {
        TCmpl node1 = (TCmpl) pop();
        ACmplBinop node = new ACmplBinop(node1);
        return node;
    }

    Node new180()
    {
        TCmpeq node1 = (TCmpeq) pop();
        ACmpeqBinop node = new ACmpeqBinop(node1);
        return node;
    }

    Node new181()
    {
        TCmpne node1 = (TCmpne) pop();
        ACmpneBinop node = new ACmpneBinop(node1);
        return node;
    }

    Node new182()
    {
        TCmpgt node1 = (TCmpgt) pop();
        ACmpgtBinop node = new ACmpgtBinop(node1);
        return node;
    }

    Node new183()
    {
        TCmpge node1 = (TCmpge) pop();
        ACmpgeBinop node = new ACmpgeBinop(node1);
        return node;
    }

    Node new184()
    {
        TCmplt node1 = (TCmplt) pop();
        ACmpltBinop node = new ACmpltBinop(node1);
        return node;
    }

    Node new185()
    {
        TCmple node1 = (TCmple) pop();
        ACmpleBinop node = new ACmpleBinop(node1);
        return node;
    }

    Node new186()
    {
        TShl node1 = (TShl) pop();
        AShlBinop node = new AShlBinop(node1);
        return node;
    }

    Node new187()
    {
        TShr node1 = (TShr) pop();
        AShrBinop node = new AShrBinop(node1);
        return node;
    }

    Node new188()
    {
        TUshr node1 = (TUshr) pop();
        AUshrBinop node = new AUshrBinop(node1);
        return node;
    }

    Node new189()
    {
        TPlus node1 = (TPlus) pop();
        APlusBinop node = new APlusBinop(node1);
        return node;
    }

    Node new190()
    {
        TMinus node1 = (TMinus) pop();
        AMinusBinop node = new AMinusBinop(node1);
        return node;
    }

    Node new191()
    {
        TMult node1 = (TMult) pop();
        AMultBinop node = new AMultBinop(node1);
        return node;
    }

    Node new192()
    {
        TDiv node1 = (TDiv) pop();
        ADivBinop node = new ADivBinop(node1);
        return node;
    }

    Node new193()
    {
        TLengthof node1 = (TLengthof) pop();
        ALengthofUnop node = new ALengthofUnop(node1);
        return node;
    }

    Node new194()
    {
        TNeg node1 = (TNeg) pop();
        ANegUnop node = new ANegUnop(node1);
        return node;
    }

    Node new195()
    {
        TQuotedName node1 = (TQuotedName) pop();
        AQuotedClassName node = new AQuotedClassName(node1);
        return node;
    }

    Node new196()
    {
        TIdentifier node1 = (TIdentifier) pop();
        AIdentClassName node = new AIdentClassName(node1);
        return node;
    }

    Node new197()
    {
        TFullIdentifier node1 = (TFullIdentifier) pop();
        AFullIdentClassName node = new AFullIdentClassName(node1);
        return node;
    }

    Node new198()
    {
        TQuotedName node1 = (TQuotedName) pop();
        AQuotedName node = new AQuotedName(node1);
        return node;
    }

    Node new199()
    {
        TIdentifier node1 = (TIdentifier) pop();
        AIdentName node = new AIdentName(node1);
        return node;
    }

    private static int[][][] actionTable;
/*      {
			{{-1, ERROR, 0}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {10, SHIFT, 11}, {11, SHIFT, 12}, },
			{{-1, REDUCE, 10}, },
			{{-1, REDUCE, 11}, },
			{{-1, REDUCE, 12}, },
			{{-1, REDUCE, 13}, },
			{{-1, REDUCE, 14}, },
			{{-1, REDUCE, 15}, },
			{{-1, REDUCE, 16}, },
			{{-1, REDUCE, 17}, },
			{{-1, REDUCE, 18}, },
			{{-1, REDUCE, 19}, },
			{{-1, REDUCE, 20}, },
			{{-1, REDUCE, 21}, },
			{{-1, ERROR, 13}, {94, ACCEPT, -1}, },
			{{-1, REDUCE, 3}, },
			{{-1, ERROR, 15}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 16}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {10, SHIFT, 11}, {11, SHIFT, 12}, },
			{{-1, REDUCE, 195}, },
			{{-1, REDUCE, 196}, },
			{{-1, REDUCE, 197}, },
			{{-1, ERROR, 20}, {22, SHIFT, 23}, {23, SHIFT, 24}, {56, SHIFT, 25}, },
			{{-1, REDUCE, 2}, },
			{{-1, ERROR, 22}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 23}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 24}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 25}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {12, SHIFT, 33}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {57, SHIFT, 42}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 26}, {23, SHIFT, 24}, {56, SHIFT, 25}, },
			{{-1, ERROR, 27}, {56, SHIFT, 25}, },
			{{-1, REDUCE, 0}, },
			{{-1, ERROR, 29}, {22, SHIFT, 23}, {23, SHIFT, 24}, {56, SHIFT, 25}, },
			{{-1, REDUCE, 22}, },
			{{-1, REDUCE, 23}, },
			{{-1, REDUCE, 30}, {55, SHIFT, 58}, },
			{{-1, REDUCE, 42}, },
			{{-1, REDUCE, 48}, },
			{{-1, REDUCE, 49}, },
			{{-1, REDUCE, 51}, },
			{{-1, REDUCE, 50}, },
			{{-1, REDUCE, 52}, },
			{{-1, REDUCE, 53}, },
			{{-1, REDUCE, 54}, },
			{{-1, REDUCE, 55}, },
			{{-1, REDUCE, 24}, },
			{{-1, REDUCE, 69}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 71}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 73}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 27}, },
			{{-1, ERROR, 47}, {86, SHIFT, 64}, {87, SHIFT, 65}, },
			{{-1, REDUCE, 65}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 43}, },
			{{-1, ERROR, 50}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {12, SHIFT, 33}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 51}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {12, SHIFT, 33}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {57, SHIFT, 69}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 52}, {56, SHIFT, 25}, },
			{{-1, REDUCE, 4}, },
			{{-1, REDUCE, 6}, },
			{{-1, ERROR, 55}, {23, SHIFT, 24}, {56, SHIFT, 25}, },
			{{-1, ERROR, 56}, {56, SHIFT, 25}, },
			{{-1, REDUCE, 1}, },
			{{-1, ERROR, 58}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 59}, {60, SHIFT, 76}, },
			{{-1, REDUCE, 68}, },
			{{-1, REDUCE, 70}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 72}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 74}, {59, SHIFT, 59}, },
			{{-1, REDUCE, 198}, },
			{{-1, REDUCE, 199}, },
			{{-1, ERROR, 66}, {58, SHIFT, 78}, {61, SHIFT, 79}, },
			{{-1, REDUCE, 66}, {59, SHIFT, 59}, },
			{{-1, ERROR, 68}, {86, SHIFT, 64}, {87, SHIFT, 65}, },
			{{-1, REDUCE, 25}, },
			{{-1, REDUCE, 26}, },
			{{-1, REDUCE, 8}, },
			{{-1, ERROR, 72}, {56, SHIFT, 25}, },
			{{-1, REDUCE, 5}, },
			{{-1, REDUCE, 7}, },
			{{-1, REDUCE, 31}, },
			{{-1, REDUCE, 75}, },
			{{-1, REDUCE, 67}, },
			{{-1, REDUCE, 32}, },
			{{-1, ERROR, 79}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {62, SHIFT, 82}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 80}, {58, SHIFT, 86}, {61, SHIFT, 87}, },
			{{-1, REDUCE, 9}, },
			{{-1, ERROR, 82}, {51, SHIFT, 88}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, ERROR, 83}, {62, SHIFT, 93}, },
			{{-1, REDUCE, 44}, {55, SHIFT, 94}, },
			{{-1, REDUCE, 46}, },
			{{-1, REDUCE, 33}, },
			{{-1, ERROR, 87}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {62, SHIFT, 95}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 88}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 89}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {21, SHIFT, 98}, {24, SHIFT, 99}, {26, SHIFT, 100}, {31, SHIFT, 101}, {32, SHIFT, 102}, {34, SHIFT, 103}, {35, SHIFT, 104}, {37, SHIFT, 105}, {39, SHIFT, 106}, {44, SHIFT, 107}, {45, SHIFT, 108}, {46, SHIFT, 109}, {47, SHIFT, 110}, {48, SHIFT, 111}, {49, SHIFT, 112}, {50, SHIFT, 113}, {53, SHIFT, 114}, {57, SHIFT, 115}, {76, SHIFT, 116}, {86, SHIFT, 43}, {87, SHIFT, 117}, {88, SHIFT, 45}, },
			{{-1, REDUCE, 76}, },
			{{-1, ERROR, 91}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, REDUCE, 34}, },
			{{-1, ERROR, 93}, {51, SHIFT, 88}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, ERROR, 94}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 95}, {51, SHIFT, 88}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, ERROR, 96}, {62, SHIFT, 143}, },
			{{-1, REDUCE, 47}, },
			{{-1, REDUCE, 92}, },
			{{-1, ERROR, 99}, {58, SHIFT, 144}, },
			{{-1, ERROR, 100}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 101}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, ERROR, 102}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, ERROR, 103}, {87, SHIFT, 157}, },
			{{-1, ERROR, 104}, {38, SHIFT, 159}, {40, SHIFT, 160}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 152}, },
			{{-1, REDUCE, 117}, },
			{{-1, ERROR, 107}, {58, SHIFT, 166}, },
			{{-1, ERROR, 108}, {58, SHIFT, 167}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, ERROR, 109}, {58, SHIFT, 169}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 150}, },
			{{-1, ERROR, 111}, {76, SHIFT, 171}, },
			{{-1, REDUCE, 118}, },
			{{-1, ERROR, 113}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 151}, },
			{{-1, REDUCE, 77}, },
			{{-1, ERROR, 116}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, REDUCE, 94}, {59, SHIFT, 175}, {63, REDUCE, 116}, {87, REDUCE, 71}, },
			{{-1, REDUCE, 93}, },
			{{-1, REDUCE, 80}, },
			{{-1, ERROR, 120}, {87, SHIFT, 148}, },
			{{-1, REDUCE, 141}, {64, SHIFT, 179}, {66, SHIFT, 180}, },
			{{-1, REDUCE, 83}, },
			{{-1, ERROR, 123}, {63, SHIFT, 181}, },
			{{-1, ERROR, 124}, {61, SHIFT, 182}, },
			{{-1, REDUCE, 108}, },
			{{-1, REDUCE, 87}, },
			{{-1, ERROR, 127}, {67, SHIFT, 183}, },
			{{-1, ERROR, 128}, {58, SHIFT, 184}, },
			{{-1, ERROR, 129}, {87, SHIFT, 148}, },
			{{-1, REDUCE, 140}, },
			{{-1, REDUCE, 155}, },
			{{-1, REDUCE, 156}, },
			{{-1, REDUCE, 159}, },
			{{-1, ERROR, 134}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {21, SHIFT, 98}, {24, SHIFT, 99}, {26, SHIFT, 100}, {31, SHIFT, 101}, {32, SHIFT, 102}, {34, SHIFT, 103}, {35, SHIFT, 104}, {37, SHIFT, 105}, {39, SHIFT, 106}, {44, SHIFT, 107}, {45, SHIFT, 108}, {46, SHIFT, 109}, {47, SHIFT, 110}, {48, SHIFT, 111}, {49, SHIFT, 112}, {50, SHIFT, 113}, {53, SHIFT, 114}, {57, SHIFT, 186}, {76, SHIFT, 116}, {86, SHIFT, 43}, {87, SHIFT, 117}, {88, SHIFT, 45}, },
			{{-1, ERROR, 135}, {24, SHIFT, 99}, {26, SHIFT, 100}, {31, SHIFT, 101}, {32, SHIFT, 102}, {34, SHIFT, 103}, {35, SHIFT, 104}, {37, SHIFT, 105}, {39, SHIFT, 106}, {44, SHIFT, 107}, {45, SHIFT, 108}, {46, SHIFT, 109}, {47, SHIFT, 110}, {48, SHIFT, 111}, {49, SHIFT, 112}, {50, SHIFT, 113}, {53, SHIFT, 114}, {57, SHIFT, 190}, {76, SHIFT, 116}, {87, SHIFT, 191}, },
			{{-1, ERROR, 136}, {26, SHIFT, 100}, {57, SHIFT, 194}, },
			{{-1, REDUCE, 38}, },
			{{-1, ERROR, 138}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, REDUCE, 36}, },
			{{-1, REDUCE, 45}, },
			{{-1, ERROR, 141}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, REDUCE, 35}, },
			{{-1, ERROR, 143}, {51, SHIFT, 88}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, REDUCE, 98}, },
			{{-1, ERROR, 145}, {33, SHIFT, 200}, },
			{{-1, ERROR, 146}, {85, SHIFT, 147}, {91, SHIFT, 201}, {92, SHIFT, 150}, },
			{{-1, REDUCE, 172}, },
			{{-1, REDUCE, 94}, },
			{{-1, REDUCE, 166}, },
			{{-1, REDUCE, 171}, },
			{{-1, REDUCE, 170}, },
			{{-1, REDUCE, 164}, },
			{{-1, ERROR, 153}, {58, SHIFT, 203}, },
			{{-1, REDUCE, 165}, },
			{{-1, REDUCE, 168}, },
			{{-1, ERROR, 156}, {58, SHIFT, 204}, },
			{{-1, REDUCE, 116}, },
			{{-1, ERROR, 158}, {58, SHIFT, 205}, },
			{{-1, REDUCE, 193}, },
			{{-1, REDUCE, 194}, },
			{{-1, ERROR, 161}, {34, SHIFT, 103}, },
			{{-1, REDUCE, 142}, },
			{{-1, REDUCE, 143}, },
			{{-1, ERROR, 164}, {27, SHIFT, 207}, {28, SHIFT, 208}, {29, SHIFT, 209}, {68, SHIFT, 210}, {69, SHIFT, 211}, {70, SHIFT, 212}, {71, SHIFT, 213}, {72, SHIFT, 214}, {73, SHIFT, 215}, {74, SHIFT, 216}, {75, SHIFT, 217}, {76, SHIFT, 218}, {77, SHIFT, 219}, {78, SHIFT, 220}, {79, SHIFT, 221}, {80, SHIFT, 222}, {81, SHIFT, 223}, {82, SHIFT, 224}, {83, SHIFT, 225}, {84, SHIFT, 226}, },
			{{-1, ERROR, 165}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 109}, },
			{{-1, REDUCE, 110}, },
			{{-1, ERROR, 168}, {58, SHIFT, 229}, },
			{{-1, REDUCE, 112}, },
			{{-1, ERROR, 170}, {58, SHIFT, 230}, },
			{{-1, ERROR, 171}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 172}, {61, SHIFT, 232}, },
			{{-1, ERROR, 173}, {58, SHIFT, 233}, },
			{{-1, ERROR, 174}, {63, SHIFT, 234}, },
			{{-1, ERROR, 175}, {60, SHIFT, 76}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 157}, },
			{{-1, REDUCE, 95}, {55, SHIFT, 236}, },
			{{-1, ERROR, 178}, {58, SHIFT, 237}, },
			{{-1, ERROR, 179}, {76, SHIFT, 116}, },
			{{-1, ERROR, 180}, {89, SHIFT, 239}, },
			{{-1, REDUCE, 97}, },
			{{-1, ERROR, 182}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, ERROR, 183}, {37, SHIFT, 105}, {38, SHIFT, 159}, {40, SHIFT, 160}, {41, SHIFT, 241}, {42, SHIFT, 242}, {43, SHIFT, 243}, {47, SHIFT, 110}, {48, SHIFT, 111}, {53, SHIFT, 114}, {61, SHIFT, 244}, {76, SHIFT, 116}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 245}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 115}, },
			{{-1, ERROR, 185}, {64, SHIFT, 254}, },
			{{-1, REDUCE, 78}, },
			{{-1, REDUCE, 79}, },
			{{-1, ERROR, 188}, {24, SHIFT, 99}, {26, SHIFT, 100}, {31, SHIFT, 101}, {32, SHIFT, 102}, {34, SHIFT, 103}, {35, SHIFT, 104}, {37, SHIFT, 105}, {39, SHIFT, 106}, {44, SHIFT, 107}, {45, SHIFT, 108}, {46, SHIFT, 109}, {47, SHIFT, 110}, {48, SHIFT, 111}, {49, SHIFT, 112}, {50, SHIFT, 113}, {53, SHIFT, 114}, {57, SHIFT, 255}, {76, SHIFT, 116}, {87, SHIFT, 191}, },
			{{-1, ERROR, 189}, {26, SHIFT, 100}, {57, SHIFT, 257}, },
			{{-1, REDUCE, 81}, },
			{{-1, REDUCE, 94}, {59, SHIFT, 258}, {63, REDUCE, 116}, },
			{{-1, REDUCE, 82}, },
			{{-1, ERROR, 193}, {26, SHIFT, 100}, {57, SHIFT, 259}, },
			{{-1, REDUCE, 85}, },
			{{-1, REDUCE, 86}, },
			{{-1, REDUCE, 40}, },
			{{-1, REDUCE, 39}, },
			{{-1, ERROR, 198}, {56, SHIFT, 89}, {58, SHIFT, 90}, },
			{{-1, REDUCE, 37}, },
			{{-1, ERROR, 200}, {87, SHIFT, 157}, },
			{{-1, REDUCE, 167}, },
			{{-1, REDUCE, 169}, },
			{{-1, REDUCE, 99}, },
			{{-1, REDUCE, 100}, },
			{{-1, REDUCE, 123}, },
			{{-1, REDUCE, 107}, },
			{{-1, REDUCE, 177}, },
			{{-1, REDUCE, 178}, },
			{{-1, REDUCE, 179}, },
			{{-1, REDUCE, 173}, },
			{{-1, REDUCE, 174}, },
			{{-1, REDUCE, 175}, },
			{{-1, REDUCE, 176}, },
			{{-1, REDUCE, 180}, },
			{{-1, REDUCE, 181}, },
			{{-1, REDUCE, 182}, },
			{{-1, REDUCE, 183}, },
			{{-1, REDUCE, 184}, },
			{{-1, REDUCE, 185}, },
			{{-1, REDUCE, 186}, },
			{{-1, REDUCE, 187}, },
			{{-1, REDUCE, 188}, },
			{{-1, REDUCE, 189}, },
			{{-1, REDUCE, 190}, },
			{{-1, REDUCE, 191}, },
			{{-1, REDUCE, 192}, },
			{{-1, ERROR, 227}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 149}, },
			{{-1, REDUCE, 111}, },
			{{-1, REDUCE, 113}, },
			{{-1, ERROR, 231}, {63, SHIFT, 263}, },
			{{-1, ERROR, 232}, {62, SHIFT, 264}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 114}, },
			{{-1, ERROR, 234}, {12, SHIFT, 33}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 235}, {60, SHIFT, 268}, },
			{{-1, ERROR, 236}, {87, SHIFT, 148}, },
			{{-1, REDUCE, 91}, },
			{{-1, REDUCE, 158}, },
			{{-1, ERROR, 239}, {12, SHIFT, 33}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {58, SHIFT, 270}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 240}, {62, SHIFT, 272}, },
			{{-1, ERROR, 241}, {13, SHIFT, 273}, {14, SHIFT, 274}, {15, SHIFT, 275}, {16, SHIFT, 276}, {17, SHIFT, 277}, {18, SHIFT, 278}, {19, SHIFT, 279}, {20, SHIFT, 280}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 242}, {61, SHIFT, 283}, },
			{{-1, ERROR, 243}, {61, SHIFT, 284}, },
			{{-1, ERROR, 244}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, REDUCE, 94}, {59, SHIFT, 258}, },
			{{-1, REDUCE, 164}, {64, SHIFT, 179}, },
			{{-1, ERROR, 247}, {58, SHIFT, 286}, },
			{{-1, REDUCE, 125}, },
			{{-1, REDUCE, 128}, },
			{{-1, REDUCE, 130}, },
			{{-1, REDUCE, 131}, },
			{{-1, REDUCE, 129}, },
			{{-1, REDUCE, 132}, {27, SHIFT, 207}, {28, SHIFT, 208}, {29, SHIFT, 209}, {36, SHIFT, 287}, {68, SHIFT, 210}, {69, SHIFT, 211}, {70, SHIFT, 212}, {71, SHIFT, 213}, {72, SHIFT, 214}, {73, SHIFT, 215}, {74, SHIFT, 216}, {75, SHIFT, 217}, {76, SHIFT, 218}, {77, SHIFT, 219}, {78, SHIFT, 220}, {79, SHIFT, 221}, {80, SHIFT, 222}, {81, SHIFT, 223}, {82, SHIFT, 224}, {83, SHIFT, 225}, {84, SHIFT, 226}, },
			{{-1, ERROR, 254}, {76, SHIFT, 171}, },
			{{-1, REDUCE, 84}, },
			{{-1, ERROR, 256}, {26, SHIFT, 100}, {57, SHIFT, 289}, },
			{{-1, REDUCE, 88}, },
			{{-1, ERROR, 258}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 89}, },
			{{-1, REDUCE, 41}, },
			{{-1, ERROR, 261}, {52, SHIFT, 290}, },
			{{-1, REDUCE, 148}, },
			{{-1, ERROR, 263}, {12, SHIFT, 33}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, REDUCE, 146}, },
			{{-1, ERROR, 265}, {62, SHIFT, 292}, },
			{{-1, REDUCE, 162}, {55, SHIFT, 293}, },
			{{-1, ERROR, 267}, {86, SHIFT, 64}, {87, SHIFT, 65}, },
			{{-1, REDUCE, 161}, },
			{{-1, REDUCE, 96}, },
			{{-1, REDUCE, 105}, },
			{{-1, ERROR, 271}, {58, SHIFT, 295}, },
			{{-1, ERROR, 272}, {56, SHIFT, 296}, },
			{{-1, REDUCE, 56}, },
			{{-1, REDUCE, 57}, },
			{{-1, REDUCE, 59}, },
			{{-1, REDUCE, 58}, },
			{{-1, REDUCE, 60}, },
			{{-1, REDUCE, 61}, },
			{{-1, REDUCE, 62}, },
			{{-1, REDUCE, 63}, },
			{{-1, REDUCE, 133}, },
			{{-1, REDUCE, 64}, },
			{{-1, ERROR, 283}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 284}, {13, SHIFT, 273}, {14, SHIFT, 274}, {15, SHIFT, 275}, {16, SHIFT, 276}, {17, SHIFT, 277}, {18, SHIFT, 278}, {19, SHIFT, 279}, {20, SHIFT, 280}, {86, SHIFT, 17}, {87, SHIFT, 18}, {88, SHIFT, 19}, },
			{{-1, ERROR, 285}, {62, SHIFT, 299}, },
			{{-1, REDUCE, 106}, },
			{{-1, ERROR, 287}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 288}, {61, SHIFT, 301}, },
			{{-1, REDUCE, 90}, },
			{{-1, ERROR, 290}, {87, SHIFT, 157}, },
			{{-1, ERROR, 291}, {86, SHIFT, 64}, {87, SHIFT, 65}, },
			{{-1, REDUCE, 147}, },
			{{-1, ERROR, 293}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, ERROR, 294}, {74, SHIFT, 305}, },
			{{-1, REDUCE, 104}, },
			{{-1, ERROR, 296}, {25, SHIFT, 306}, {30, SHIFT, 307}, },
			{{-1, ERROR, 297}, {62, SHIFT, 311}, },
			{{-1, ERROR, 298}, {62, SHIFT, 312}, },
			{{-1, ERROR, 299}, {87, SHIFT, 148}, },
			{{-1, REDUCE, 127}, },
			{{-1, ERROR, 301}, {62, SHIFT, 314}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, ERROR, 302}, {54, SHIFT, 316}, },
			{{-1, ERROR, 303}, {61, SHIFT, 317}, },
			{{-1, REDUCE, 163}, },
			{{-1, REDUCE, 160}, },
			{{-1, ERROR, 306}, {82, SHIFT, 318}, {91, SHIFT, 319}, },
			{{-1, REDUCE, 122}, },
			{{-1, REDUCE, 103}, },
			{{-1, ERROR, 309}, {63, SHIFT, 320}, },
			{{-1, ERROR, 310}, {25, SHIFT, 306}, {30, SHIFT, 307}, {57, SHIFT, 321}, },
			{{-1, ERROR, 311}, {59, SHIFT, 258}, },
			{{-1, ERROR, 312}, {59, SHIFT, 324}, },
			{{-1, REDUCE, 126}, },
			{{-1, REDUCE, 144}, },
			{{-1, ERROR, 315}, {62, SHIFT, 327}, },
			{{-1, ERROR, 316}, {87, SHIFT, 157}, },
			{{-1, ERROR, 317}, {13, SHIFT, 34}, {14, SHIFT, 35}, {15, SHIFT, 36}, {16, SHIFT, 37}, {17, SHIFT, 38}, {18, SHIFT, 39}, {19, SHIFT, 40}, {20, SHIFT, 41}, {62, SHIFT, 329}, {86, SHIFT, 43}, {87, SHIFT, 44}, {88, SHIFT, 45}, },
			{{-1, ERROR, 318}, {91, SHIFT, 331}, },
			{{-1, REDUCE, 120}, },
			{{-1, ERROR, 320}, {34, SHIFT, 103}, },
			{{-1, ERROR, 321}, {58, SHIFT, 333}, },
			{{-1, REDUCE, 102}, },
			{{-1, REDUCE, 134}, },
			{{-1, ERROR, 324}, {60, SHIFT, 334}, {82, SHIFT, 146}, {85, SHIFT, 147}, {87, SHIFT, 148}, {91, SHIFT, 149}, {92, SHIFT, 150}, {93, SHIFT, 151}, },
			{{-1, REDUCE, 137}, },
			{{-1, REDUCE, 135}, {59, SHIFT, 324}, },
			{{-1, REDUCE, 145}, },
			{{-1, ERROR, 328}, {58, SHIFT, 337}, },
			{{-1, ERROR, 329}, {74, SHIFT, 338}, },
			{{-1, ERROR, 330}, {62, SHIFT, 339}, },
			{{-1, REDUCE, 121}, },
			{{-1, REDUCE, 119}, },
			{{-1, REDUCE, 101}, },
			{{-1, REDUCE, 138}, },
			{{-1, ERROR, 335}, {60, SHIFT, 340}, },
			{{-1, REDUCE, 136}, },
			{{-1, REDUCE, 124}, },
			{{-1, REDUCE, 153}, },
			{{-1, ERROR, 339}, {74, SHIFT, 341}, },
			{{-1, REDUCE, 139}, },
			{{-1, REDUCE, 154}, },
        };*/
    private static int[][][] gotoTable;
/*      {
			{{-1, 13}, },
			{{-1, 14}, {16, 21}, {50, 21}, },
			{{-1, 15}, {16, 22}, },
			{{-1, 26}, {29, 55}, },
			{{-1, 27}, {26, 52}, {29, 56}, {55, 72}, },
			{{-1, 28}, {26, 53}, {27, 54}, {29, 57}, {52, 71}, {55, 73}, {56, 74}, {72, 81}, },
			{{-1, -1}, },
			{{-1, 31}, {58, 75}, {88, 97}, },
			{{-1, 46}, {51, 70}, },
			{{-1, 47}, {50, 68}, {234, 267}, {239, 271}, {263, 291}, },
			{{-1, 83}, {87, 96}, {94, 140}, {317, 330}, },
			{{-1, 84}, },
			{{-1, 91}, {93, 138}, {95, 141}, {143, 198}, },
			{{-1, 48}, },
			{{-1, 281}, {284, 298}, },
			{{-1, 49}, {79, 85}, {87, 85}, {89, 118}, {94, 85}, {134, 118}, {244, 285}, {283, 297}, {287, 300}, {317, 85}, },
			{{-1, 60}, {61, 77}, {62, 77}, {63, 77}, {67, 77}, },
			{{-1, 92}, {91, 137}, {93, 139}, {95, 142}, {138, 196}, {141, 197}, {143, 199}, {198, 260}, },
			{{-1, 119}, {134, 187}, },
			{{-1, 120}, },
			{{-1, 152}, {89, 121}, {120, 177}, {129, 185}, {134, 121}, {135, 121}, {183, 246}, {188, 121}, {236, 177}, {299, 313}, },
			{{-1, 178}, {236, 269}, },
			{{-1, 122}, {135, 192}, {188, 192}, },
			{{-1, 123}, {103, 158}, {200, 261}, {290, 302}, {316, 328}, },
			{{-1, 124}, },
			{{-1, 308}, {310, 322}, },
			{{-1, 309}, },
			{{-1, 125}, {161, 206}, {320, 332}, },
			{{-1, 126}, {136, 195}, {189, 195}, {193, 195}, {256, 195}, },
			{{-1, 247}, },
			{{-1, 248}, },
			{{-1, 325}, {326, 336}, },
			{{-1, 127}, },
			{{-1, 161}, },
			{{-1, 128}, {183, 249}, },
			{{-1, 162}, {183, 250}, },
			{{-1, 163}, {183, 251}, },
			{{-1, 129}, },
			{{-1, 172}, {254, 288}, },
			{{-1, 130}, {183, 252}, },
			{{-1, 131}, },
			{{-1, 132}, },
			{{-1, 133}, {179, 238}, },
			{{-1, 176}, {311, 323}, },
			{{-1, 265}, {293, 304}, {301, 315}, },
			{{-1, 266}, {101, 153}, {102, 156}, {104, 164}, {108, 168}, {109, 170}, {113, 173}, {165, 228}, {175, 235}, {182, 240}, {183, 253}, {227, 262}, {258, 235}, {324, 335}, },
			{{-1, 154}, },
			{{-1, 155}, {146, 202}, },
			{{-1, 227}, },
			{{-1, 165}, },
			{{-1, 32}, {15, 20}, {22, 29}, {23, 30}, {100, 145}, {116, 174}, {171, 231}, {241, 282}, {284, 282}, },
			{{-1, 66}, {68, 80}, {267, 294}, {291, 303}, },
			{{-1, 50}, {0, 16}, },
			{{-1, 51}, },
			{{-1, 62}, {43, 61}, {45, 63}, {48, 67}, },
			{{-1, 134}, },
			{{-1, 135}, {134, 188}, },
			{{-1, 136}, {134, 189}, {135, 193}, {188, 256}, },
			{{-1, 310}, },
			{{-1, 326}, },
        };*/
    private static String[] errorMessages;
/*      {
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TClass TInterface expected.",
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TClass TInterface TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TQuotedName TIdentifier TFullIdentifier expected.",
			"TQuotedName TIdentifier TFullIdentifier expected.",
			"EOF expected.",
			"TExtends TImplements TFrom TComma TLBrace TSemicolon TRParen TColon expected.",
			"TExtends TImplements TLBrace expected.",
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TRBrace TQuotedName TIdentifier TFullIdentifier expected.",
			"TImplements TLBrace expected.",
			"TLBrace expected.",
			"TComma TLBrace TSemicolon expected.",
			"TSemicolon TQuotedName TIdentifier expected.",
			"TComma TSemicolon TLBracket TRParen TQuotedName TIdentifier expected.",
			"TQuotedName TIdentifier expected.",
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TQuotedName TIdentifier TFullIdentifier expected.",
			"TRBracket expected.",
			"TSemicolon TLParen TCmpgt expected.",
			"TSemicolon TLParen expected.",
			"TLBrace TSemicolon expected.",
			"TBoolean TByte TShort TChar TInt TLong TFloat TDouble TRParen TQuotedName TIdentifier TFullIdentifier expected.",
			"TThrows TLBrace TSemicolon expected.",
			"TRParen expected.",
			"TComma TRParen expected.",
			"TBoolean TByte TShort TChar TInt TLong TFloat TDouble TUnknown TBreakpoint TCatch TEntermonitor TExitmonitor TGoto TIf TInterfaceinvoke TLookupswitch TNop TRet TReturn TSpecialinvoke TStaticinvoke TTableswitch TThrow TVirtualinvoke TRBrace TCmplt TQuotedName TIdentifier TFullIdentifier expected.",
			"TBoolean TByte TShort TChar TInt TLong TFloat TDouble TQuotedName TIdentifier TFullIdentifier expected.",
			"TIdentifier expected.",
			"TSemicolon expected.",
			"TMinus TFloatDegenerate TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TLengthof TNeg TMinus TFloatDegenerate TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TLParen expected.",
			"TSemicolon TMinus TFloatDegenerate TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TCmplt expected.",
			"TLBracket TColon TDot TColonEquals TEquals TIdentifier expected.",
			"TDot TColonEquals TEquals expected.",
			"TBreakpoint TCatch TEntermonitor TExitmonitor TGoto TIf TInterfaceinvoke TLookupswitch TNop TRet TReturn TSpecialinvoke TStaticinvoke TTableswitch TThrow TVirtualinvoke TRBrace TCmplt TIdentifier expected.",
			"TColon expected.",
			"TCatch TRBrace expected.",
			"TEquals expected.",
			"TSemicolon TEquals expected.",
			"TFrom expected.",
			"TFloatDegenerate TIntegerConstant TFloatConstant expected.",
			"TCmp TCmpg TCmpl TGoto TInstanceof TComma TSemicolon TRBracket TRParen TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TGoto TComma TSemicolon TRBracket TRParen TDot TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TGoto TComma TSemicolon TRBracket TRParen TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TTo TWith TSemicolon expected.",
			"TGoto expected.",
			"TCmp TCmpg TCmpl TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TRBracket TMinus TFloatDegenerate TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TComma TSemicolon expected.",
			"TAtIdentifier expected.",
			"TInterfaceinvoke TLengthof TNeg TNew TNewarray TNewmultiarray TSpecialinvoke TStaticinvoke TVirtualinvoke TLParen TCmplt TMinus TFloatDegenerate TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TDot expected.",
			"TLBracket TColon TDot TColonEquals TEquals expected.",
			"TBreakpoint TCase TCatch TDefault TEntermonitor TExitmonitor TGoto TIf TInterfaceinvoke TLookupswitch TNop TRet TReturn TSpecialinvoke TStaticinvoke TTableswitch TThrow TVirtualinvoke TRBrace TCmplt TIdentifier expected.",
			"TGoto TSemicolon expected.",
			"TRParen TMinus TFloatDegenerate TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TQuotedName TIdentifier TFullIdentifier expected.",
			"TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TSemicolon TQuotedName TIdentifier TFullIdentifier expected.",
			"TCmp TCmpg TCmpl TInstanceof TSemicolon TLBracket TDot TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TInstanceof TSemicolon TDot TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TInstanceof TSemicolon TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TTo expected.",
			"TSemicolon TRParen expected.",
			"TCmpgt expected.",
			"TCase TDefault expected.",
			"TWith expected.",
			"TMinus TIntegerConstant expected.",
			"TCase TDefault TRBrace expected.",
			"TLBracket expected.",
			"TIntegerConstant expected.",
			"TSemicolon TLBracket expected.",
        };*/
    private static int[] errors;
/*      {
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 1, 2, 0, 4, 4, 4, 5, 1, 2, 2, 2, 6, 7, 8, 3, 5, 7, 8, 9, 10, 11, 11, 11, 11, 11, 11, 11, 11, 3, 11, 11, 11, 6, 12, 11, 10, 13, 6, 8, 3, 3, 7, 8, 3, 2, 14, 11, 11, 11, 11, 15, 15, 16, 11, 12, 3, 6, 3, 8, 3, 3, 17, 11, 11, 6, 18, 16, 3, 19, 20, 21, 21, 6, 18, 2, 22, 6, 17, 6, 19, 23, 19, 20, 17, 24, 25, 2, 26, 26, 24, 27, 24, 28, 25, 29, 29, 24, 30, 28, 26, 24, 6, 2, 31, 24, 22, 24, 32, 33, 34, 28, 33, 35, 36, 25, 24, 36, 37, 37, 37, 22, 33, 35, 6, 17, 6, 20, 17, 6, 19, 33, 38, 39, 40, 41, 40, 40, 40, 42, 25, 40, 40, 25, 43, 25, 26, 26, 44, 44, 44, 45, 26, 33, 33, 25, 33, 25, 2, 28, 25, 34, 46, 37, 47, 25, 30, 48, 33, 26, 49, 33, 50, 6, 22, 33, 35, 6, 51, 33, 35, 6, 35, 6, 6, 17, 6, 24, 40, 40, 33, 33, 52, 33, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 26, 53, 33, 33, 34, 54, 33, 55, 14, 24, 22, 37, 56, 20, 23, 28, 28, 23, 57, 58, 25, 25, 25, 25, 25, 25, 59, 30, 6, 35, 6, 26, 6, 6, 60, 53, 55, 25, 20, 21, 12, 37, 25, 33, 25, 8, 61, 61, 61, 61, 61, 61, 61, 61, 25, 61, 23, 23, 20, 33, 23, 28, 6, 24, 12, 25, 26, 62, 33, 63, 20, 20, 24, 25, 54, 64, 28, 20, 37, 65, 34, 66, 34, 66, 67, 67, 25, 25, 20, 24, 18, 68, 34, 44, 25, 66, 25, 46, 69, 69, 25, 25, 62, 20, 34, 66, 33, 69, 14, 69, 35, 28, 62, 69, 28, 
        };*/
}
