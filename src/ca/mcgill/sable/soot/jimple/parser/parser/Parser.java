package ca.mcgill.sable.soot.jimple.parser.parser;

import ca.mcgill.sable.soot.jimple.parser.lexer.*;
import ca.mcgill.sable.soot.jimple.parser.node.*;
import ca.mcgill.sable.soot.jimple.parser.analysis.*;
import ca.mcgill.sable.util.*;

import java.util.*;
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
                    case 2: { Node node = new2(); push(goTo(47), node, false); } break;
                    case 3: { Node node = new3(); push(goTo(47), node, false); } break;
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
                    case 26: { Node node = new26(); push(goTo(48), node, false); } break;
                    case 27: { Node node = new27(); push(goTo(48), node, false); } break;
                    case 28: { Node node = new28(); push(goTo(6), node, true); } break;
                    case 29: { Node node = new29(); push(goTo(6), node, true); } break;
                    case 30: { Node node = new30(); push(goTo(7), node, true); } break;
                    case 31: { Node node = new31(); push(goTo(7), node, true); } break;
                    case 32: { Node node = new32(); push(goTo(7), node, true); } break;
                    case 33: { Node node = new33(); push(goTo(7), node, true); } break;
                    case 34: { Node node = new34(); push(goTo(7), node, true); } break;
                    case 35: { Node node = new35(); push(goTo(7), node, true); } break;
                    case 36: { Node node = new36(); push(goTo(7), node, true); } break;
                    case 37: { Node node = new37(); push(goTo(7), node, true); } break;
                    case 38: { Node node = new38(); push(goTo(7), node, true); } break;
                    case 39: { Node node = new39(); push(goTo(7), node, true); } break;
                    case 40: { Node node = new40(); push(goTo(8), node, true); } break;
                    case 41: { Node node = new41(); push(goTo(8), node, true); } break;
                    case 42: { Node node = new42(); push(goTo(9), node, true); } break;
                    case 43: { Node node = new43(); push(goTo(9), node, true); } break;
                    case 44: { Node node = new44(); push(goTo(10), node, true); } break;
                    case 45: { Node node = new45(); push(goTo(11), node, true); } break;
                    case 46: { Node node = new46(); push(goTo(12), node, true); } break;
                    case 47: { Node node = new47(); push(goTo(12), node, true); } break;
                    case 48: { Node node = new48(); push(goTo(12), node, true); } break;
                    case 49: { Node node = new49(); push(goTo(12), node, true); } break;
                    case 50: { Node node = new50(); push(goTo(12), node, true); } break;
                    case 51: { Node node = new51(); push(goTo(12), node, true); } break;
                    case 52: { Node node = new52(); push(goTo(12), node, true); } break;
                    case 53: { Node node = new53(); push(goTo(12), node, true); } break;
                    case 54: { Node node = new54(); push(goTo(12), node, true); } break;
                    case 55: { Node node = new55(); push(goTo(13), node, true); } break;
                    case 56: { Node node = new56(); push(goTo(13), node, true); } break;
                    case 57: { Node node = new57(); push(goTo(49), node, false); } break;
                    case 58: { Node node = new58(); push(goTo(49), node, false); } break;
                    case 59: { Node node = new59(); push(goTo(14), node, true); } break;
                    case 60: { Node node = new60(); push(goTo(15), node, true); } break;
                    case 61: { Node node = new61(); push(goTo(15), node, true); } break;
                    case 62: { Node node = new62(); push(goTo(15), node, true); } break;
                    case 63: { Node node = new63(); push(goTo(50), node, false); } break;
                    case 64: { Node node = new64(); push(goTo(50), node, false); } break;
                    case 65: { Node node = new65(); push(goTo(15), node, true); } break;
                    case 66: { Node node = new66(); push(goTo(51), node, false); } break;
                    case 67: { Node node = new67(); push(goTo(51), node, false); } break;
                    case 68: { Node node = new68(); push(goTo(15), node, true); } break;
                    case 69: { Node node = new69(); push(goTo(15), node, true); } break;
                    case 70: { Node node = new70(); push(goTo(52), node, false); } break;
                    case 71: { Node node = new71(); push(goTo(52), node, false); } break;
                    case 72: { Node node = new72(); push(goTo(15), node, true); } break;
                    case 73: { Node node = new73(); push(goTo(15), node, true); } break;
                    case 74: { Node node = new74(); push(goTo(15), node, true); } break;
                    case 75: { Node node = new75(); push(goTo(16), node, true); } break;
                    case 76: { Node node = new76(); push(goTo(17), node, true); } break;
                    case 77: { Node node = new77(); push(goTo(17), node, true); } break;
                    case 78: { Node node = new78(); push(goTo(18), node, true); } break;
                    case 79: { Node node = new79(); push(goTo(19), node, true); } break;
                    case 80: { Node node = new80(); push(goTo(19), node, true); } break;
                    case 81: { Node node = new81(); push(goTo(20), node, true); } break;
                    case 82: { Node node = new82(); push(goTo(20), node, true); } break;
                    case 83: { Node node = new83(); push(goTo(20), node, true); } break;
                    case 84: { Node node = new84(); push(goTo(20), node, true); } break;
                    case 85: { Node node = new85(); push(goTo(20), node, true); } break;
                    case 86: { Node node = new86(); push(goTo(53), node, false); } break;
                    case 87: { Node node = new87(); push(goTo(53), node, false); } break;
                    case 88: { Node node = new88(); push(goTo(20), node, true); } break;
                    case 89: { Node node = new89(); push(goTo(20), node, true); } break;
                    case 90: { Node node = new90(); push(goTo(20), node, true); } break;
                    case 91: { Node node = new91(); push(goTo(20), node, true); } break;
                    case 92: { Node node = new92(); push(goTo(20), node, true); } break;
                    case 93: { Node node = new93(); push(goTo(20), node, true); } break;
                    case 94: { Node node = new94(); push(goTo(20), node, true); } break;
                    case 95: { Node node = new95(); push(goTo(20), node, true); } break;
                    case 96: { Node node = new96(); push(goTo(20), node, true); } break;
                    case 97: { Node node = new97(); push(goTo(20), node, true); } break;
                    case 98: { Node node = new98(); push(goTo(20), node, true); } break;
                    case 99: { Node node = new99(); push(goTo(21), node, true); } break;
                    case 100: { Node node = new100(); push(goTo(22), node, true); } break;
                    case 101: { Node node = new101(); push(goTo(22), node, true); } break;
                    case 102: { Node node = new102(); push(goTo(23), node, true); } break;
                    case 103: { Node node = new103(); push(goTo(24), node, true); } break;
                    case 104: { Node node = new104(); push(goTo(24), node, true); } break;
                    case 105: { Node node = new105(); push(goTo(25), node, true); } break;
                    case 106: { Node node = new106(); push(goTo(26), node, true); } break;
                    case 107: { Node node = new107(); push(goTo(27), node, true); } break;
                    case 108: { Node node = new108(); push(goTo(27), node, true); } break;
                    case 109: { Node node = new109(); push(goTo(27), node, true); } break;
                    case 110: { Node node = new110(); push(goTo(27), node, true); } break;
                    case 111: { Node node = new111(); push(goTo(27), node, true); } break;
                    case 112: { Node node = new112(); push(goTo(27), node, true); } break;
                    case 113: { Node node = new113(); push(goTo(27), node, true); } break;
                    case 114: { Node node = new114(); push(goTo(27), node, true); } break;
                    case 115: { Node node = new115(); push(goTo(28), node, true); } break;
                    case 116: { Node node = new116(); push(goTo(28), node, true); } break;
                    case 117: { Node node = new117(); push(goTo(28), node, true); } break;
                    case 118: { Node node = new118(); push(goTo(54), node, false); } break;
                    case 119: { Node node = new119(); push(goTo(54), node, false); } break;
                    case 120: { Node node = new120(); push(goTo(29), node, true); } break;
                    case 121: { Node node = new121(); push(goTo(29), node, true); } break;
                    case 122: { Node node = new122(); push(goTo(30), node, true); } break;
                    case 123: { Node node = new123(); push(goTo(30), node, true); } break;
                    case 124: { Node node = new124(); push(goTo(31), node, true); } break;
                    case 125: { Node node = new125(); push(goTo(31), node, true); } break;
                    case 126: { Node node = new126(); push(goTo(32), node, true); } break;
                    case 127: { Node node = new127(); push(goTo(32), node, true); } break;
                    case 128: { Node node = new128(); push(goTo(32), node, true); } break;
                    case 129: { Node node = new129(); push(goTo(32), node, true); } break;
                    case 130: { Node node = new130(); push(goTo(33), node, true); } break;
                    case 131: { Node node = new131(); push(goTo(34), node, true); } break;
                    case 132: { Node node = new132(); push(goTo(35), node, true); } break;
                    case 133: { Node node = new133(); push(goTo(35), node, true); } break;
                    case 134: { Node node = new134(); push(goTo(35), node, true); } break;
                    case 135: { Node node = new135(); push(goTo(36), node, true); } break;
                    case 136: { Node node = new136(); push(goTo(36), node, true); } break;
                    case 137: { Node node = new137(); push(goTo(37), node, true); } break;
                    case 138: { Node node = new138(); push(goTo(37), node, true); } break;
                    case 139: { Node node = new139(); push(goTo(38), node, true); } break;
                    case 140: { Node node = new140(); push(goTo(39), node, true); } break;
                    case 141: { Node node = new141(); push(goTo(39), node, true); } break;
                    case 142: { Node node = new142(); push(goTo(40), node, true); } break;
                    case 143: { Node node = new143(); push(goTo(41), node, true); } break;
                    case 144: { Node node = new144(); push(goTo(42), node, true); } break;
                    case 145: { Node node = new145(); push(goTo(42), node, true); } break;
                    case 146: { Node node = new146(); push(goTo(43), node, true); } break;
                    case 147: { Node node = new147(); push(goTo(43), node, true); } break;
                    case 148: { Node node = new148(); push(goTo(44), node, true); } break;
                    case 149: { Node node = new149(); push(goTo(44), node, true); } break;
                    case 150: { Node node = new150(); push(goTo(44), node, true); } break;
                    case 151: { Node node = new151(); push(goTo(45), node, true); } break;
                    case 152: { Node node = new152(); push(goTo(45), node, true); } break;
                    case 153: { Node node = new153(); push(goTo(45), node, true); } break;
                    case 154: { Node node = new154(); push(goTo(45), node, true); } break;
                    case 155: { Node node = new155(); push(goTo(45), node, true); } break;
                    case 156: { Node node = new156(); push(goTo(45), node, true); } break;
                    case 157: { Node node = new157(); push(goTo(45), node, true); } break;
                    case 158: { Node node = new158(); push(goTo(45), node, true); } break;
                    case 159: { Node node = new159(); push(goTo(45), node, true); } break;
                    case 160: { Node node = new160(); push(goTo(45), node, true); } break;
                    case 161: { Node node = new161(); push(goTo(45), node, true); } break;
                    case 162: { Node node = new162(); push(goTo(45), node, true); } break;
                    case 163: { Node node = new163(); push(goTo(45), node, true); } break;
                    case 164: { Node node = new164(); push(goTo(45), node, true); } break;
                    case 165: { Node node = new165(); push(goTo(45), node, true); } break;
                    case 166: { Node node = new166(); push(goTo(45), node, true); } break;
                    case 167: { Node node = new167(); push(goTo(45), node, true); } break;
                    case 168: { Node node = new168(); push(goTo(45), node, true); } break;
                    case 169: { Node node = new169(); push(goTo(45), node, true); } break;
                    case 170: { Node node = new170(); push(goTo(45), node, true); } break;
                    case 171: { Node node = new171(); push(goTo(46), node, true); } break;
                    case 172: { Node node = new172(); push(goTo(46), node, true); } break;
                    case 173: { Node node = new173(); push(goTo(46), node, true); } break;
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node3 = (TName) pop();
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
        TName node2 = (TName) pop();
        TExtends node1 = (TExtends) pop();
        AExtendsClause node = new AExtendsClause(node1, node2);
        return node;
    }

    Node new23()
    {
        PNameList node2 = (PNameList) pop();
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
        TName node1 = (TName) pop();
        ASingleNameList node = new ASingleNameList(node1);
        return node;
    }

    Node new29()
    {
        PNameList node3 = (PNameList) pop();
        TComma node2 = (TComma) pop();
        TName node1 = (TName) pop();
        AMultiNameList node = new AMultiNameList(node1, node2, node3);
        return node;
    }

    Node new30()
    {
        TSemicolon node4 = (TSemicolon) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AFieldMember node = new AFieldMember(node1, node2, node3, node4);
        return node;
    }

    Node new31()
    {
        TSemicolon node4 = (TSemicolon) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AFieldMember node = new AFieldMember(node1, node2, node3, node4);
        return node;
    }

    Node new32()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new33()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new34()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = null;
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
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
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new36()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = (PThrowsClause) pop();
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = null;
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new37()
    {
        PMethodBody node8 = (PMethodBody) pop();
        PThrowsClause node7 = (PThrowsClause) pop();
        TRParen node6 = (TRParen) pop();
        PParameterList node5 = null;
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
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
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
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
        PParameterList node5 = (PParameterList) pop();
        TLParen node4 = (TLParen) pop();
        TName node3 = (TName) pop();
        PType node2 = (PType) pop();
        XPModifier node1 = (XPModifier) pop();
        AMethodMember node = new AMethodMember(node1, node2, node3, node4, node5, node6, node7, node8);
        return node;
    }

    Node new40()
    {
        TVoid node1 = (TVoid) pop();
        AVoidType node = new AVoidType(node1);
        return node;
    }

    Node new41()
    {
        PNonvoidType node1 = (PNonvoidType) pop();
        ANovoidType node = new ANovoidType(node1);
        return node;
    }

    Node new42()
    {
        PParameter node1 = (PParameter) pop();
        ASingleParameterList node = new ASingleParameterList(node1);
        return node;
    }

    Node new43()
    {
        PParameterList node3 = (PParameterList) pop();
        TComma node2 = (TComma) pop();
        PParameter node1 = (PParameter) pop();
        AMultiParameterList node = new AMultiParameterList(node1, node2, node3);
        return node;
    }

    Node new44()
    {
        PNonvoidType node1 = (PNonvoidType) pop();
        AParameter node = new AParameter(node1);
        return node;
    }

    Node new45()
    {
        PNameList node2 = (PNameList) pop();
        TThrows node1 = (TThrows) pop();
        AThrowsClause node = new AThrowsClause(node1, node2);
        return node;
    }

    Node new46()
    {
        TBoolean node1 = (TBoolean) pop();
        ABooleanBaseType node = new ABooleanBaseType(node1);
        return node;
    }

    Node new47()
    {
        TByte node1 = (TByte) pop();
        AByteBaseType node = new AByteBaseType(node1);
        return node;
    }

    Node new48()
    {
        TChar node1 = (TChar) pop();
        ACharBaseType node = new ACharBaseType(node1);
        return node;
    }

    Node new49()
    {
        TShort node1 = (TShort) pop();
        AShortBaseType node = new AShortBaseType(node1);
        return node;
    }

    Node new50()
    {
        TInt node1 = (TInt) pop();
        AIntBaseType node = new AIntBaseType(node1);
        return node;
    }

    Node new51()
    {
        TLong node1 = (TLong) pop();
        ALongBaseType node = new ALongBaseType(node1);
        return node;
    }

    Node new52()
    {
        TFloat node1 = (TFloat) pop();
        AFloatBaseType node = new AFloatBaseType(node1);
        return node;
    }

    Node new53()
    {
        TDouble node1 = (TDouble) pop();
        ADoubleBaseType node = new ADoubleBaseType(node1);
        return node;
    }

    Node new54()
    {
        TName node1 = (TName) pop();
        ANameBaseType node = new ANameBaseType(node1);
        return node;
    }

    Node new55()
    {
        XPArrayBrackets node2 = null;
        PBaseType node1 = (PBaseType) pop();
        ANonvoidType node = new ANonvoidType(node1, node2);
        return node;
    }

    Node new56()
    {
        XPArrayBrackets node2 = (XPArrayBrackets) pop();
        PBaseType node1 = (PBaseType) pop();
        ANonvoidType node = new ANonvoidType(node1, node2);
        return node;
    }

    Node new57()
    {
        PArrayBrackets node2 = (PArrayBrackets) pop();
        XPArrayBrackets node1 = (XPArrayBrackets) pop();
        X1PArrayBrackets node = new X1PArrayBrackets(node1, node2);
        return node;
    }

    Node new58()
    {
        PArrayBrackets node1 = (PArrayBrackets) pop();
        X2PArrayBrackets node = new X2PArrayBrackets(node1);
        return node;
    }

    Node new59()
    {
        TRBracket node2 = (TRBracket) pop();
        TLBracket node1 = (TLBracket) pop();
        AArrayBrackets node = new AArrayBrackets(node1, node2);
        return node;
    }

    Node new60()
    {
        TSemicolon node1 = (TSemicolon) pop();
        AEmptyMethodBody node = new AEmptyMethodBody(node1);
        return node;
    }

    Node new61()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = null;
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new62()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = null;
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new63()
    {
        PDeclaration node2 = (PDeclaration) pop();
        XPDeclaration node1 = (XPDeclaration) pop();
        X1PDeclaration node = new X1PDeclaration(node1, node2);
        return node;
    }

    Node new64()
    {
        PDeclaration node1 = (PDeclaration) pop();
        X2PDeclaration node = new X2PDeclaration(node1);
        return node;
    }

    Node new65()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new66()
    {
        PStatement node2 = (PStatement) pop();
        XPStatement node1 = (XPStatement) pop();
        X1PStatement node = new X1PStatement(node1, node2);
        return node;
    }

    Node new67()
    {
        PStatement node1 = (PStatement) pop();
        X2PStatement node = new X2PStatement(node1);
        return node;
    }

    Node new68()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = null;
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new69()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = null;
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new70()
    {
        PCatchClause node2 = (PCatchClause) pop();
        XPCatchClause node1 = (XPCatchClause) pop();
        X1PCatchClause node = new X1PCatchClause(node1, node2);
        return node;
    }

    Node new71()
    {
        PCatchClause node1 = (PCatchClause) pop();
        X2PCatchClause node = new X2PCatchClause(node1);
        return node;
    }

    Node new72()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = null;
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new73()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = null;
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new74()
    {
        TRBrace node5 = (TRBrace) pop();
        XPCatchClause node4 = (XPCatchClause) pop();
        XPStatement node3 = (XPStatement) pop();
        XPDeclaration node2 = (XPDeclaration) pop();
        TLBrace node1 = (TLBrace) pop();
        AFullMethodBody node = new AFullMethodBody(node1, node2, node3, node4, node5);
        return node;
    }

    Node new75()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PLocalNameList node2 = (PLocalNameList) pop();
        PJimpleType node1 = (PJimpleType) pop();
        ADeclaration node = new ADeclaration(node1, node2, node3);
        return node;
    }

    Node new76()
    {
        TUnknown node1 = (TUnknown) pop();
        AUnknownJimpleType node = new AUnknownJimpleType(node1);
        return node;
    }

    Node new77()
    {
        PNonvoidType node1 = (PNonvoidType) pop();
        ANonvoidJimpleType node = new ANonvoidJimpleType(node1);
        return node;
    }

    Node new78()
    {
        TIdentifier node1 = (TIdentifier) pop();
        ALocalName node = new ALocalName(node1);
        return node;
    }

    Node new79()
    {
        PLocalName node1 = (PLocalName) pop();
        ASingleLocalNameList node = new ASingleLocalNameList(node1);
        return node;
    }

    Node new80()
    {
        PLocalNameList node3 = (PLocalNameList) pop();
        TComma node2 = (TComma) pop();
        PLocalName node1 = (PLocalName) pop();
        AMultiLocalNameList node = new AMultiLocalNameList(node1, node2, node3);
        return node;
    }

    Node new81()
    {
        TColon node2 = (TColon) pop();
        PLabelName node1 = (PLabelName) pop();
        ALabelStatement node = new ALabelStatement(node1, node2);
        return node;
    }

    Node new82()
    {
        TSemicolon node2 = (TSemicolon) pop();
        TBreakpoint node1 = (TBreakpoint) pop();
        ABreakpointStatement node = new ABreakpointStatement(node1, node2);
        return node;
    }

    Node new83()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TEntermonitor node1 = (TEntermonitor) pop();
        AEntermonitorStatement node = new AEntermonitorStatement(node1, node2, node3);
        return node;
    }

    Node new84()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TExitmonitor node1 = (TExitmonitor) pop();
        AExitmonitorStatement node = new AExitmonitorStatement(node1, node2, node3);
        return node;
    }

    Node new85()
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

    Node new86()
    {
        PCaseStmt node2 = (PCaseStmt) pop();
        XPCaseStmt node1 = (XPCaseStmt) pop();
        X1PCaseStmt node = new X1PCaseStmt(node1, node2);
        return node;
    }

    Node new87()
    {
        PCaseStmt node1 = (PCaseStmt) pop();
        X2PCaseStmt node = new X2PCaseStmt(node1);
        return node;
    }

    Node new88()
    {
        TSemicolon node4 = (TSemicolon) pop();
        TAtIdentifier node3 = (TAtIdentifier) pop();
        TColonEquals node2 = (TColonEquals) pop();
        PLocalName node1 = (PLocalName) pop();
        AIdentityStatement node = new AIdentityStatement(node1, node2, node3, node4);
        return node;
    }

    Node new89()
    {
        TSemicolon node4 = (TSemicolon) pop();
        PExpression node3 = (PExpression) pop();
        TEquals node2 = (TEquals) pop();
        PVariable node1 = (PVariable) pop();
        AAssignStatement node = new AAssignStatement(node1, node2, node3, node4);
        return node;
    }

    Node new90()
    {
        PGotoStmt node3 = (PGotoStmt) pop();
        PBoolExpr node2 = (PBoolExpr) pop();
        TIf node1 = (TIf) pop();
        AIfStatement node = new AIfStatement(node1, node2, node3);
        return node;
    }

    Node new91()
    {
        PGotoStmt node1 = (PGotoStmt) pop();
        AGotoStatement node = new AGotoStatement(node1);
        return node;
    }

    Node new92()
    {
        TSemicolon node2 = (TSemicolon) pop();
        TNop node1 = (TNop) pop();
        ANopStatement node = new ANopStatement(node1, node2);
        return node;
    }

    Node new93()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = null;
        TRet node1 = (TRet) pop();
        ARetStatement node = new ARetStatement(node1, node2, node3);
        return node;
    }

    Node new94()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TRet node1 = (TRet) pop();
        ARetStatement node = new ARetStatement(node1, node2, node3);
        return node;
    }

    Node new95()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = null;
        TReturn node1 = (TReturn) pop();
        AReturnStatement node = new AReturnStatement(node1, node2, node3);
        return node;
    }

    Node new96()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TReturn node1 = (TReturn) pop();
        AReturnStatement node = new AReturnStatement(node1, node2, node3);
        return node;
    }

    Node new97()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PImmediate node2 = (PImmediate) pop();
        TThrow node1 = (TThrow) pop();
        AThrowStatement node = new AThrowStatement(node1, node2, node3);
        return node;
    }

    Node new98()
    {
        TSemicolon node2 = (TSemicolon) pop();
        PInvokeExpr node1 = (PInvokeExpr) pop();
        AInvokeStatement node = new AInvokeStatement(node1, node2);
        return node;
    }

    Node new99()
    {
        TIdentifier node1 = (TIdentifier) pop();
        ALabelName node = new ALabelName(node1);
        return node;
    }

    Node new100()
    {
        TLookupswitch node1 = (TLookupswitch) pop();
        ALookupSwitch node = new ALookupSwitch(node1);
        return node;
    }

    Node new101()
    {
        TTableswitch node1 = (TTableswitch) pop();
        ATableSwitch node = new ATableSwitch(node1);
        return node;
    }

    Node new102()
    {
        PGotoStmt node3 = (PGotoStmt) pop();
        TColon node2 = (TColon) pop();
        PCaseLabel node1 = (PCaseLabel) pop();
        ACaseStmt node = new ACaseStmt(node1, node2, node3);
        return node;
    }

    Node new103()
    {
        TIntegerConstant node2 = (TIntegerConstant) pop();
        TCase node1 = (TCase) pop();
        AConstantCaseLabel node = new AConstantCaseLabel(node1, node2);
        return node;
    }

    Node new104()
    {
        TDefault node1 = (TDefault) pop();
        ADefaultCaseLabel node = new ADefaultCaseLabel(node1);
        return node;
    }

    Node new105()
    {
        TSemicolon node3 = (TSemicolon) pop();
        PLabelName node2 = (PLabelName) pop();
        TGoto node1 = (TGoto) pop();
        AGotoStmt node = new AGotoStmt(node1, node2, node3);
        return node;
    }

    Node new106()
    {
        TSemicolon node9 = (TSemicolon) pop();
        PLabelName node8 = (PLabelName) pop();
        TWith node7 = (TWith) pop();
        PLabelName node6 = (PLabelName) pop();
        TTo node5 = (TTo) pop();
        PLabelName node4 = (PLabelName) pop();
        TFrom node3 = (TFrom) pop();
        TName node2 = (TName) pop();
        TCatch node1 = (TCatch) pop();
        ACatchClause node = new ACatchClause(node1, node2, node3, node4, node5, node6, node7, node8, node9);
        return node;
    }

    Node new107()
    {
        PNewExpr node1 = (PNewExpr) pop();
        ANewExpression node = new ANewExpression(node1);
        return node;
    }

    Node new108()
    {
        PLocalName node4 = (PLocalName) pop();
        TRParen node3 = (TRParen) pop();
        PNonvoidType node2 = (PNonvoidType) pop();
        TLParen node1 = (TLParen) pop();
        ACastExpression node = new ACastExpression(node1, node2, node3, node4);
        return node;
    }

    Node new109()
    {
        PNonvoidType node3 = (PNonvoidType) pop();
        TInstanceof node2 = (TInstanceof) pop();
        PImmediate node1 = (PImmediate) pop();
        AInstanceofExpression node = new AInstanceofExpression(node1, node2, node3);
        return node;
    }

    Node new110()
    {
        PInvokeExpr node1 = (PInvokeExpr) pop();
        AInvokeExpression node = new AInvokeExpression(node1);
        return node;
    }

    Node new111()
    {
        PReference node1 = (PReference) pop();
        AReferenceExpression node = new AReferenceExpression(node1);
        return node;
    }

    Node new112()
    {
        PBinopExpr node1 = (PBinopExpr) pop();
        ABinopExpression node = new ABinopExpression(node1);
        return node;
    }

    Node new113()
    {
        PUnopExpr node1 = (PUnopExpr) pop();
        AUnopExpression node = new AUnopExpression(node1);
        return node;
    }

    Node new114()
    {
        PImmediate node1 = (PImmediate) pop();
        AImmediateExpression node = new AImmediateExpression(node1);
        return node;
    }

    Node new115()
    {
        PBaseType node2 = (PBaseType) pop();
        TNew node1 = (TNew) pop();
        ASimpleNewExpr node = new ASimpleNewExpr(node1, node2);
        return node;
    }

    Node new116()
    {
        PFixedArrayDescriptor node5 = (PFixedArrayDescriptor) pop();
        TRParen node4 = (TRParen) pop();
        PNonvoidType node3 = (PNonvoidType) pop();
        TLParen node2 = (TLParen) pop();
        TNewarray node1 = (TNewarray) pop();
        AArrayNewExpr node = new AArrayNewExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new117()
    {
        XPArrayDescriptor node5 = (XPArrayDescriptor) pop();
        TRParen node4 = (TRParen) pop();
        PBaseType node3 = (PBaseType) pop();
        TLParen node2 = (TLParen) pop();
        TNewmultiarray node1 = (TNewmultiarray) pop();
        AMultiNewExpr node = new AMultiNewExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new118()
    {
        PArrayDescriptor node2 = (PArrayDescriptor) pop();
        XPArrayDescriptor node1 = (XPArrayDescriptor) pop();
        X1PArrayDescriptor node = new X1PArrayDescriptor(node1, node2);
        return node;
    }

    Node new119()
    {
        PArrayDescriptor node1 = (PArrayDescriptor) pop();
        X2PArrayDescriptor node = new X2PArrayDescriptor(node1);
        return node;
    }

    Node new120()
    {
        TRBracket node3 = (TRBracket) pop();
        PImmediate node2 = null;
        TLBracket node1 = (TLBracket) pop();
        AArrayDescriptor node = new AArrayDescriptor(node1, node2, node3);
        return node;
    }

    Node new121()
    {
        TRBracket node3 = (TRBracket) pop();
        PImmediate node2 = (PImmediate) pop();
        TLBracket node1 = (TLBracket) pop();
        AArrayDescriptor node = new AArrayDescriptor(node1, node2, node3);
        return node;
    }

    Node new122()
    {
        PReference node1 = (PReference) pop();
        AReferenceVariable node = new AReferenceVariable(node1);
        return node;
    }

    Node new123()
    {
        PLocalName node1 = (PLocalName) pop();
        ALocalVariable node = new ALocalVariable(node1);
        return node;
    }

    Node new124()
    {
        PBinopExpr node1 = (PBinopExpr) pop();
        ABinopBoolExpr node = new ABinopBoolExpr(node1);
        return node;
    }

    Node new125()
    {
        PUnopExpr node1 = (PUnopExpr) pop();
        AUnopBoolExpr node = new AUnopBoolExpr(node1);
        return node;
    }

    Node new126()
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

    Node new127()
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

    Node new128()
    {
        TRParen node5 = (TRParen) pop();
        PArgList node4 = null;
        TLParen node3 = (TLParen) pop();
        PMethodSignature node2 = (PMethodSignature) pop();
        TStaticinvoke node1 = (TStaticinvoke) pop();
        AStaticInvokeExpr node = new AStaticInvokeExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new129()
    {
        TRParen node5 = (TRParen) pop();
        PArgList node4 = (PArgList) pop();
        TLParen node3 = (TLParen) pop();
        PMethodSignature node2 = (PMethodSignature) pop();
        TStaticinvoke node1 = (TStaticinvoke) pop();
        AStaticInvokeExpr node = new AStaticInvokeExpr(node1, node2, node3, node4, node5);
        return node;
    }

    Node new130()
    {
        PImmediate node3 = (PImmediate) pop();
        PBinop node2 = (PBinop) pop();
        PImmediate node1 = (PImmediate) pop();
        ABinopExpr node = new ABinopExpr(node1, node2, node3);
        return node;
    }

    Node new131()
    {
        PImmediate node2 = (PImmediate) pop();
        PUnop node1 = (PUnop) pop();
        AUnopExpr node = new AUnopExpr(node1, node2);
        return node;
    }

    Node new132()
    {
        TSpecialinvoke node1 = (TSpecialinvoke) pop();
        ASpecialNonstaticInvoke node = new ASpecialNonstaticInvoke(node1);
        return node;
    }

    Node new133()
    {
        TVirtualinvoke node1 = (TVirtualinvoke) pop();
        AVirtualNonstaticInvoke node = new AVirtualNonstaticInvoke(node1);
        return node;
    }

    Node new134()
    {
        TInterfaceinvoke node1 = (TInterfaceinvoke) pop();
        AInterfaceNonstaticInvoke node = new AInterfaceNonstaticInvoke(node1);
        return node;
    }

    Node new135()
    {
        TCmpgt node11 = (TCmpgt) pop();
        PType node10 = (PType) pop();
        TColon node9 = (TColon) pop();
        TRParen node8 = (TRParen) pop();
        PParameterList node7 = null;
        TLParen node6 = (TLParen) pop();
        TColon node5 = (TColon) pop();
        TName node4 = (TName) pop();
        TColon node3 = (TColon) pop();
        TName node2 = (TName) pop();
        TCmplt node1 = (TCmplt) pop();
        AMethodSignature node = new AMethodSignature(node1, node2, node3, node4, node5, node6, node7, node8, node9, node10, node11);
        return node;
    }

    Node new136()
    {
        TCmpgt node11 = (TCmpgt) pop();
        PType node10 = (PType) pop();
        TColon node9 = (TColon) pop();
        TRParen node8 = (TRParen) pop();
        PParameterList node7 = (PParameterList) pop();
        TLParen node6 = (TLParen) pop();
        TColon node5 = (TColon) pop();
        TName node4 = (TName) pop();
        TColon node3 = (TColon) pop();
        TName node2 = (TName) pop();
        TCmplt node1 = (TCmplt) pop();
        AMethodSignature node = new AMethodSignature(node1, node2, node3, node4, node5, node6, node7, node8, node9, node10, node11);
        return node;
    }

    Node new137()
    {
        PArrayRef node1 = (PArrayRef) pop();
        AArrayReference node = new AArrayReference(node1);
        return node;
    }

    Node new138()
    {
        PFieldRef node1 = (PFieldRef) pop();
        AFieldReference node = new AFieldReference(node1);
        return node;
    }

    Node new139()
    {
        PFixedArrayDescriptor node2 = (PFixedArrayDescriptor) pop();
        PLocalName node1 = (PLocalName) pop();
        AArrayRef node = new AArrayRef(node1, node2);
        return node;
    }

    Node new140()
    {
        PFieldSignature node3 = (PFieldSignature) pop();
        TDot node2 = (TDot) pop();
        PLocalName node1 = (PLocalName) pop();
        ALocalFieldRef node = new ALocalFieldRef(node1, node2, node3);
        return node;
    }

    Node new141()
    {
        PFieldSignature node1 = (PFieldSignature) pop();
        ASigFieldRef node = new ASigFieldRef(node1);
        return node;
    }

    Node new142()
    {
        TCmpgt node7 = (TCmpgt) pop();
        PType node6 = (PType) pop();
        TColon node5 = (TColon) pop();
        TName node4 = (TName) pop();
        TColon node3 = (TColon) pop();
        TName node2 = (TName) pop();
        TCmplt node1 = (TCmplt) pop();
        AFieldSignature node = new AFieldSignature(node1, node2, node3, node4, node5, node6, node7);
        return node;
    }

    Node new143()
    {
        TRBracket node3 = (TRBracket) pop();
        PImmediate node2 = (PImmediate) pop();
        TLBracket node1 = (TLBracket) pop();
        AFixedArrayDescriptor node = new AFixedArrayDescriptor(node1, node2, node3);
        return node;
    }

    Node new144()
    {
        PImmediate node1 = (PImmediate) pop();
        ASingleArgList node = new ASingleArgList(node1);
        return node;
    }

    Node new145()
    {
        PArgList node3 = (PArgList) pop();
        TComma node2 = (TComma) pop();
        PImmediate node1 = (PImmediate) pop();
        AMultiArgList node = new AMultiArgList(node1, node2, node3);
        return node;
    }

    Node new146()
    {
        PLocalName node1 = (PLocalName) pop();
        ALocalImmediate node = new ALocalImmediate(node1);
        return node;
    }

    Node new147()
    {
        PConstant node1 = (PConstant) pop();
        AConstantImmediate node = new AConstantImmediate(node1);
        return node;
    }

    Node new148()
    {
        TIntegerConstant node1 = (TIntegerConstant) pop();
        AIntegerConstant node = new AIntegerConstant(node1);
        return node;
    }

    Node new149()
    {
        TFloatConstant node1 = (TFloatConstant) pop();
        AFloatConstant node = new AFloatConstant(node1);
        return node;
    }

    Node new150()
    {
        TStringConstant node1 = (TStringConstant) pop();
        AStringConstant node = new AStringConstant(node1);
        return node;
    }

    Node new151()
    {
        TAnd node1 = (TAnd) pop();
        AAndBinop node = new AAndBinop(node1);
        return node;
    }

    Node new152()
    {
        TOr node1 = (TOr) pop();
        AOrBinop node = new AOrBinop(node1);
        return node;
    }

    Node new153()
    {
        TXor node1 = (TXor) pop();
        AXorBinop node = new AXorBinop(node1);
        return node;
    }

    Node new154()
    {
        TMod node1 = (TMod) pop();
        AModBinop node = new AModBinop(node1);
        return node;
    }

    Node new155()
    {
        TCmp node1 = (TCmp) pop();
        ACmpBinop node = new ACmpBinop(node1);
        return node;
    }

    Node new156()
    {
        TCmpg node1 = (TCmpg) pop();
        ACmpgBinop node = new ACmpgBinop(node1);
        return node;
    }

    Node new157()
    {
        TCmpl node1 = (TCmpl) pop();
        ACmplBinop node = new ACmplBinop(node1);
        return node;
    }

    Node new158()
    {
        TCmpeq node1 = (TCmpeq) pop();
        ACmpeqBinop node = new ACmpeqBinop(node1);
        return node;
    }

    Node new159()
    {
        TCmpne node1 = (TCmpne) pop();
        ACmpneBinop node = new ACmpneBinop(node1);
        return node;
    }

    Node new160()
    {
        TCmpgt node1 = (TCmpgt) pop();
        ACmpgtBinop node = new ACmpgtBinop(node1);
        return node;
    }

    Node new161()
    {
        TCmpge node1 = (TCmpge) pop();
        ACmpgeBinop node = new ACmpgeBinop(node1);
        return node;
    }

    Node new162()
    {
        TCmplt node1 = (TCmplt) pop();
        ACmpltBinop node = new ACmpltBinop(node1);
        return node;
    }

    Node new163()
    {
        TCmple node1 = (TCmple) pop();
        ACmpleBinop node = new ACmpleBinop(node1);
        return node;
    }

    Node new164()
    {
        TShl node1 = (TShl) pop();
        AShlBinop node = new AShlBinop(node1);
        return node;
    }

    Node new165()
    {
        TShr node1 = (TShr) pop();
        AShrBinop node = new AShrBinop(node1);
        return node;
    }

    Node new166()
    {
        TUshr node1 = (TUshr) pop();
        AUshrBinop node = new AUshrBinop(node1);
        return node;
    }

    Node new167()
    {
        TPlus node1 = (TPlus) pop();
        APlusBinop node = new APlusBinop(node1);
        return node;
    }

    Node new168()
    {
        TMinus node1 = (TMinus) pop();
        AMinusBinop node = new AMinusBinop(node1);
        return node;
    }

    Node new169()
    {
        TMult node1 = (TMult) pop();
        AMultBinop node = new AMultBinop(node1);
        return node;
    }

    Node new170()
    {
        TDiv node1 = (TDiv) pop();
        ADivBinop node = new ADivBinop(node1);
        return node;
    }

    Node new171()
    {
        TLengthof node1 = (TLengthof) pop();
        ALengthofUnop node = new ALengthofUnop(node1);
        return node;
    }

    Node new172()
    {
        TPlus node1 = (TPlus) pop();
        APlusUnop node = new APlusUnop(node1);
        return node;
    }

    Node new173()
    {
        TMinus node1 = (TMinus) pop();
        AMinusUnop node = new AMinusUnop(node1);
        return node;
    }

    private final static int[][][] actionTable =
        {
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
			{{-1, ERROR, 13}, {91, ACCEPT, -1}, },
			{{-1, REDUCE, 3}, },
			{{-1, ERROR, 15}, {84, SHIFT, 17}, },
			{{-1, ERROR, 16}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {10, SHIFT, 11}, {11, SHIFT, 12}, },
			{{-1, ERROR, 17}, {22, SHIFT, 20}, {23, SHIFT, 21}, {55, SHIFT, 22}, },
			{{-1, REDUCE, 2}, },
			{{-1, ERROR, 19}, {84, SHIFT, 26}, },
			{{-1, ERROR, 20}, {84, SHIFT, 27}, },
			{{-1, ERROR, 21}, {84, SHIFT, 28}, },
			{{-1, ERROR, 22}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {12, SHIFT, 30}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {56, SHIFT, 39}, {84, SHIFT, 40}, },
			{{-1, ERROR, 23}, {23, SHIFT, 21}, {55, SHIFT, 22}, },
			{{-1, ERROR, 24}, {55, SHIFT, 22}, },
			{{-1, REDUCE, 0}, },
			{{-1, ERROR, 26}, {22, SHIFT, 20}, {23, SHIFT, 21}, {55, SHIFT, 22}, },
			{{-1, REDUCE, 22}, },
			{{-1, REDUCE, 28}, {54, SHIFT, 53}, },
			{{-1, REDUCE, 23}, },
			{{-1, REDUCE, 40}, },
			{{-1, REDUCE, 46}, },
			{{-1, REDUCE, 47}, },
			{{-1, REDUCE, 49}, },
			{{-1, REDUCE, 48}, },
			{{-1, REDUCE, 50}, },
			{{-1, REDUCE, 51}, },
			{{-1, REDUCE, 52}, },
			{{-1, REDUCE, 53}, },
			{{-1, REDUCE, 24}, },
			{{-1, REDUCE, 54}, },
			{{-1, REDUCE, 27}, },
			{{-1, ERROR, 42}, {84, SHIFT, 54}, },
			{{-1, REDUCE, 55}, {58, SHIFT, 55}, },
			{{-1, REDUCE, 41}, },
			{{-1, ERROR, 45}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {12, SHIFT, 30}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 46}, {0, SHIFT, 1}, {1, SHIFT, 2}, {2, SHIFT, 3}, {3, SHIFT, 4}, {4, SHIFT, 5}, {5, SHIFT, 6}, {6, SHIFT, 7}, {7, SHIFT, 8}, {8, SHIFT, 9}, {9, SHIFT, 10}, {12, SHIFT, 30}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {56, SHIFT, 59}, {84, SHIFT, 40}, },
			{{-1, ERROR, 47}, {55, SHIFT, 22}, },
			{{-1, REDUCE, 4}, },
			{{-1, REDUCE, 6}, },
			{{-1, ERROR, 50}, {23, SHIFT, 21}, {55, SHIFT, 22}, },
			{{-1, ERROR, 51}, {55, SHIFT, 22}, },
			{{-1, REDUCE, 1}, },
			{{-1, ERROR, 53}, {84, SHIFT, 28}, },
			{{-1, ERROR, 54}, {57, SHIFT, 66}, {60, SHIFT, 67}, },
			{{-1, ERROR, 55}, {59, SHIFT, 68}, },
			{{-1, REDUCE, 58}, },
			{{-1, REDUCE, 56}, {58, SHIFT, 55}, },
			{{-1, ERROR, 58}, {84, SHIFT, 70}, },
			{{-1, REDUCE, 25}, },
			{{-1, REDUCE, 26}, },
			{{-1, REDUCE, 8}, },
			{{-1, ERROR, 62}, {55, SHIFT, 22}, },
			{{-1, REDUCE, 5}, },
			{{-1, REDUCE, 7}, },
			{{-1, REDUCE, 29}, },
			{{-1, REDUCE, 30}, },
			{{-1, ERROR, 67}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {61, SHIFT, 72}, {84, SHIFT, 40}, },
			{{-1, REDUCE, 59}, },
			{{-1, REDUCE, 57}, },
			{{-1, ERROR, 70}, {57, SHIFT, 76}, {60, SHIFT, 77}, },
			{{-1, REDUCE, 9}, },
			{{-1, ERROR, 72}, {50, SHIFT, 78}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, ERROR, 73}, {61, SHIFT, 83}, },
			{{-1, REDUCE, 42}, {54, SHIFT, 84}, },
			{{-1, REDUCE, 44}, },
			{{-1, REDUCE, 31}, },
			{{-1, ERROR, 77}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {61, SHIFT, 85}, {84, SHIFT, 40}, },
			{{-1, ERROR, 78}, {84, SHIFT, 28}, },
			{{-1, ERROR, 79}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {21, SHIFT, 88}, {24, SHIFT, 89}, {26, SHIFT, 90}, {31, SHIFT, 91}, {32, SHIFT, 92}, {34, SHIFT, 93}, {35, SHIFT, 94}, {37, SHIFT, 95}, {39, SHIFT, 96}, {43, SHIFT, 97}, {44, SHIFT, 98}, {45, SHIFT, 99}, {46, SHIFT, 100}, {47, SHIFT, 101}, {48, SHIFT, 102}, {49, SHIFT, 103}, {52, SHIFT, 104}, {56, SHIFT, 105}, {75, SHIFT, 106}, {84, SHIFT, 40}, {85, SHIFT, 107}, },
			{{-1, REDUCE, 60}, },
			{{-1, ERROR, 81}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, REDUCE, 32}, },
			{{-1, ERROR, 83}, {50, SHIFT, 78}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, ERROR, 84}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 85}, {50, SHIFT, 78}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, ERROR, 86}, {61, SHIFT, 133}, },
			{{-1, REDUCE, 45}, },
			{{-1, REDUCE, 76}, },
			{{-1, ERROR, 89}, {57, SHIFT, 134}, },
			{{-1, ERROR, 90}, {84, SHIFT, 135}, },
			{{-1, ERROR, 91}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 92}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 93}, {85, SHIFT, 144}, },
			{{-1, ERROR, 94}, {38, SHIFT, 146}, {80, SHIFT, 147}, {81, SHIFT, 148}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 134}, },
			{{-1, REDUCE, 100}, },
			{{-1, ERROR, 97}, {57, SHIFT, 154}, },
			{{-1, ERROR, 98}, {57, SHIFT, 155}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 99}, {57, SHIFT, 157}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 132}, },
			{{-1, ERROR, 101}, {75, SHIFT, 159}, },
			{{-1, REDUCE, 101}, },
			{{-1, ERROR, 103}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 133}, },
			{{-1, REDUCE, 61}, },
			{{-1, ERROR, 106}, {84, SHIFT, 162}, },
			{{-1, REDUCE, 78}, {62, REDUCE, 99}, },
			{{-1, REDUCE, 77}, },
			{{-1, REDUCE, 64}, },
			{{-1, ERROR, 110}, {85, SHIFT, 136}, },
			{{-1, REDUCE, 123}, {58, SHIFT, 165}, {63, SHIFT, 166}, {65, SHIFT, 167}, },
			{{-1, REDUCE, 67}, },
			{{-1, ERROR, 113}, {62, SHIFT, 169}, },
			{{-1, ERROR, 114}, {60, SHIFT, 170}, },
			{{-1, REDUCE, 91}, },
			{{-1, REDUCE, 71}, },
			{{-1, ERROR, 117}, {66, SHIFT, 171}, },
			{{-1, ERROR, 118}, {57, SHIFT, 172}, },
			{{-1, ERROR, 119}, {85, SHIFT, 136}, },
			{{-1, REDUCE, 122}, },
			{{-1, REDUCE, 137}, },
			{{-1, REDUCE, 138}, },
			{{-1, REDUCE, 141}, },
			{{-1, ERROR, 124}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {21, SHIFT, 88}, {24, SHIFT, 89}, {26, SHIFT, 90}, {31, SHIFT, 91}, {32, SHIFT, 92}, {34, SHIFT, 93}, {35, SHIFT, 94}, {37, SHIFT, 95}, {39, SHIFT, 96}, {43, SHIFT, 97}, {44, SHIFT, 98}, {45, SHIFT, 99}, {46, SHIFT, 100}, {47, SHIFT, 101}, {48, SHIFT, 102}, {49, SHIFT, 103}, {52, SHIFT, 104}, {56, SHIFT, 174}, {75, SHIFT, 106}, {84, SHIFT, 40}, {85, SHIFT, 107}, },
			{{-1, ERROR, 125}, {24, SHIFT, 89}, {26, SHIFT, 90}, {31, SHIFT, 91}, {32, SHIFT, 92}, {34, SHIFT, 93}, {35, SHIFT, 94}, {37, SHIFT, 95}, {39, SHIFT, 96}, {43, SHIFT, 97}, {44, SHIFT, 98}, {45, SHIFT, 99}, {46, SHIFT, 100}, {47, SHIFT, 101}, {48, SHIFT, 102}, {49, SHIFT, 103}, {52, SHIFT, 104}, {56, SHIFT, 178}, {75, SHIFT, 106}, {85, SHIFT, 107}, },
			{{-1, ERROR, 126}, {26, SHIFT, 90}, {56, SHIFT, 181}, },
			{{-1, REDUCE, 36}, },
			{{-1, ERROR, 128}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, REDUCE, 34}, },
			{{-1, REDUCE, 43}, },
			{{-1, ERROR, 131}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, REDUCE, 33}, },
			{{-1, ERROR, 133}, {50, SHIFT, 78}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, REDUCE, 82}, },
			{{-1, ERROR, 135}, {33, SHIFT, 187}, },
			{{-1, REDUCE, 78}, },
			{{-1, REDUCE, 148}, },
			{{-1, REDUCE, 149}, },
			{{-1, REDUCE, 150}, },
			{{-1, REDUCE, 146}, },
			{{-1, ERROR, 141}, {57, SHIFT, 188}, },
			{{-1, REDUCE, 147}, },
			{{-1, ERROR, 143}, {57, SHIFT, 189}, },
			{{-1, REDUCE, 99}, },
			{{-1, ERROR, 145}, {57, SHIFT, 190}, },
			{{-1, REDUCE, 171}, },
			{{-1, REDUCE, 172}, },
			{{-1, REDUCE, 173}, },
			{{-1, ERROR, 149}, {34, SHIFT, 93}, },
			{{-1, REDUCE, 124}, },
			{{-1, REDUCE, 125}, },
			{{-1, ERROR, 152}, {27, SHIFT, 192}, {28, SHIFT, 193}, {29, SHIFT, 194}, {67, SHIFT, 195}, {68, SHIFT, 196}, {69, SHIFT, 197}, {70, SHIFT, 198}, {71, SHIFT, 199}, {72, SHIFT, 200}, {73, SHIFT, 201}, {74, SHIFT, 202}, {75, SHIFT, 203}, {76, SHIFT, 204}, {77, SHIFT, 205}, {78, SHIFT, 206}, {79, SHIFT, 207}, {80, SHIFT, 208}, {81, SHIFT, 209}, {82, SHIFT, 210}, {83, SHIFT, 211}, },
			{{-1, ERROR, 153}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 92}, },
			{{-1, REDUCE, 93}, },
			{{-1, ERROR, 156}, {57, SHIFT, 214}, },
			{{-1, REDUCE, 95}, },
			{{-1, ERROR, 158}, {57, SHIFT, 215}, },
			{{-1, ERROR, 159}, {84, SHIFT, 216}, },
			{{-1, ERROR, 160}, {60, SHIFT, 217}, },
			{{-1, ERROR, 161}, {57, SHIFT, 218}, },
			{{-1, ERROR, 162}, {62, SHIFT, 219}, },
			{{-1, REDUCE, 79}, {54, SHIFT, 220}, },
			{{-1, ERROR, 164}, {57, SHIFT, 221}, },
			{{-1, ERROR, 165}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 166}, {75, SHIFT, 106}, },
			{{-1, ERROR, 167}, {86, SHIFT, 224}, },
			{{-1, REDUCE, 139}, },
			{{-1, REDUCE, 81}, },
			{{-1, ERROR, 170}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 171}, {37, SHIFT, 95}, {38, SHIFT, 146}, {40, SHIFT, 226}, {41, SHIFT, 227}, {42, SHIFT, 228}, {46, SHIFT, 100}, {47, SHIFT, 101}, {52, SHIFT, 104}, {60, SHIFT, 229}, {75, SHIFT, 106}, {80, SHIFT, 147}, {81, SHIFT, 148}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 98}, },
			{{-1, ERROR, 173}, {63, SHIFT, 238}, },
			{{-1, REDUCE, 62}, },
			{{-1, REDUCE, 63}, },
			{{-1, ERROR, 176}, {24, SHIFT, 89}, {26, SHIFT, 90}, {31, SHIFT, 91}, {32, SHIFT, 92}, {34, SHIFT, 93}, {35, SHIFT, 94}, {37, SHIFT, 95}, {39, SHIFT, 96}, {43, SHIFT, 97}, {44, SHIFT, 98}, {45, SHIFT, 99}, {46, SHIFT, 100}, {47, SHIFT, 101}, {48, SHIFT, 102}, {49, SHIFT, 103}, {52, SHIFT, 104}, {56, SHIFT, 239}, {75, SHIFT, 106}, {85, SHIFT, 107}, },
			{{-1, ERROR, 177}, {26, SHIFT, 90}, {56, SHIFT, 241}, },
			{{-1, REDUCE, 65}, },
			{{-1, REDUCE, 66}, },
			{{-1, ERROR, 180}, {26, SHIFT, 90}, {56, SHIFT, 242}, },
			{{-1, REDUCE, 69}, },
			{{-1, REDUCE, 70}, },
			{{-1, REDUCE, 38}, },
			{{-1, REDUCE, 37}, },
			{{-1, ERROR, 185}, {55, SHIFT, 79}, {57, SHIFT, 80}, },
			{{-1, REDUCE, 35}, },
			{{-1, ERROR, 187}, {85, SHIFT, 144}, },
			{{-1, REDUCE, 83}, },
			{{-1, REDUCE, 84}, },
			{{-1, REDUCE, 105}, },
			{{-1, REDUCE, 90}, },
			{{-1, REDUCE, 155}, },
			{{-1, REDUCE, 156}, },
			{{-1, REDUCE, 157}, },
			{{-1, REDUCE, 151}, },
			{{-1, REDUCE, 152}, },
			{{-1, REDUCE, 153}, },
			{{-1, REDUCE, 154}, },
			{{-1, REDUCE, 158}, },
			{{-1, REDUCE, 159}, },
			{{-1, REDUCE, 160}, },
			{{-1, REDUCE, 161}, },
			{{-1, REDUCE, 162}, },
			{{-1, REDUCE, 163}, },
			{{-1, REDUCE, 164}, },
			{{-1, REDUCE, 165}, },
			{{-1, REDUCE, 166}, },
			{{-1, REDUCE, 167}, },
			{{-1, REDUCE, 168}, },
			{{-1, REDUCE, 169}, },
			{{-1, REDUCE, 170}, },
			{{-1, ERROR, 212}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 131}, },
			{{-1, REDUCE, 94}, },
			{{-1, REDUCE, 96}, },
			{{-1, ERROR, 216}, {62, SHIFT, 246}, },
			{{-1, ERROR, 217}, {61, SHIFT, 247}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 97}, },
			{{-1, ERROR, 219}, {84, SHIFT, 250}, },
			{{-1, ERROR, 220}, {85, SHIFT, 136}, },
			{{-1, REDUCE, 75}, },
			{{-1, ERROR, 222}, {59, SHIFT, 252}, },
			{{-1, REDUCE, 140}, },
			{{-1, ERROR, 224}, {57, SHIFT, 253}, },
			{{-1, ERROR, 225}, {61, SHIFT, 254}, },
			{{-1, ERROR, 226}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 227}, {60, SHIFT, 256}, },
			{{-1, ERROR, 228}, {60, SHIFT, 257}, },
			{{-1, ERROR, 229}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, REDUCE, 146}, {58, SHIFT, 165}, {63, SHIFT, 166}, },
			{{-1, ERROR, 231}, {57, SHIFT, 259}, },
			{{-1, REDUCE, 107}, },
			{{-1, REDUCE, 110}, },
			{{-1, REDUCE, 112}, },
			{{-1, REDUCE, 113}, },
			{{-1, REDUCE, 111}, },
			{{-1, REDUCE, 114}, {27, SHIFT, 192}, {28, SHIFT, 193}, {29, SHIFT, 194}, {36, SHIFT, 260}, {67, SHIFT, 195}, {68, SHIFT, 196}, {69, SHIFT, 197}, {70, SHIFT, 198}, {71, SHIFT, 199}, {72, SHIFT, 200}, {73, SHIFT, 201}, {74, SHIFT, 202}, {75, SHIFT, 203}, {76, SHIFT, 204}, {77, SHIFT, 205}, {78, SHIFT, 206}, {79, SHIFT, 207}, {80, SHIFT, 208}, {81, SHIFT, 209}, {82, SHIFT, 210}, {83, SHIFT, 211}, },
			{{-1, ERROR, 238}, {75, SHIFT, 159}, },
			{{-1, REDUCE, 68}, },
			{{-1, ERROR, 240}, {26, SHIFT, 90}, {56, SHIFT, 262}, },
			{{-1, REDUCE, 72}, },
			{{-1, REDUCE, 73}, },
			{{-1, REDUCE, 39}, },
			{{-1, ERROR, 244}, {51, SHIFT, 263}, },
			{{-1, REDUCE, 130}, },
			{{-1, ERROR, 246}, {84, SHIFT, 264}, },
			{{-1, REDUCE, 128}, },
			{{-1, ERROR, 248}, {61, SHIFT, 265}, },
			{{-1, REDUCE, 144}, {54, SHIFT, 266}, },
			{{-1, ERROR, 250}, {62, SHIFT, 267}, },
			{{-1, REDUCE, 80}, },
			{{-1, REDUCE, 143}, },
			{{-1, REDUCE, 88}, },
			{{-1, ERROR, 254}, {55, SHIFT, 268}, },
			{{-1, REDUCE, 115}, },
			{{-1, ERROR, 256}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 257}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 258}, {61, SHIFT, 271}, },
			{{-1, REDUCE, 89}, },
			{{-1, ERROR, 260}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 261}, {60, SHIFT, 273}, },
			{{-1, REDUCE, 74}, },
			{{-1, ERROR, 263}, {85, SHIFT, 144}, },
			{{-1, ERROR, 264}, {62, SHIFT, 275}, },
			{{-1, REDUCE, 129}, },
			{{-1, ERROR, 266}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 267}, {12, SHIFT, 30}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 268}, {25, SHIFT, 278}, {30, SHIFT, 279}, },
			{{-1, ERROR, 269}, {61, SHIFT, 283}, },
			{{-1, ERROR, 270}, {61, SHIFT, 284}, },
			{{-1, ERROR, 271}, {85, SHIFT, 136}, },
			{{-1, REDUCE, 109}, },
			{{-1, ERROR, 273}, {61, SHIFT, 286}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, ERROR, 274}, {53, SHIFT, 288}, },
			{{-1, ERROR, 275}, {60, SHIFT, 289}, },
			{{-1, REDUCE, 145}, },
			{{-1, ERROR, 277}, {73, SHIFT, 290}, },
			{{-1, ERROR, 278}, {88, SHIFT, 291}, },
			{{-1, REDUCE, 104}, },
			{{-1, REDUCE, 87}, },
			{{-1, ERROR, 281}, {62, SHIFT, 292}, },
			{{-1, ERROR, 282}, {25, SHIFT, 278}, {30, SHIFT, 279}, {56, SHIFT, 293}, },
			{{-1, ERROR, 283}, {58, SHIFT, 165}, },
			{{-1, ERROR, 284}, {58, SHIFT, 296}, },
			{{-1, REDUCE, 108}, },
			{{-1, REDUCE, 126}, },
			{{-1, ERROR, 287}, {61, SHIFT, 299}, },
			{{-1, ERROR, 288}, {85, SHIFT, 144}, },
			{{-1, ERROR, 289}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {61, SHIFT, 301}, {84, SHIFT, 40}, },
			{{-1, REDUCE, 142}, },
			{{-1, REDUCE, 103}, },
			{{-1, ERROR, 292}, {34, SHIFT, 93}, },
			{{-1, ERROR, 293}, {57, SHIFT, 304}, },
			{{-1, REDUCE, 86}, },
			{{-1, REDUCE, 116}, },
			{{-1, ERROR, 296}, {59, SHIFT, 305}, {85, SHIFT, 136}, {88, SHIFT, 137}, {89, SHIFT, 138}, {90, SHIFT, 139}, },
			{{-1, REDUCE, 119}, },
			{{-1, REDUCE, 117}, {58, SHIFT, 296}, },
			{{-1, REDUCE, 127}, },
			{{-1, ERROR, 300}, {57, SHIFT, 308}, },
			{{-1, ERROR, 301}, {62, SHIFT, 309}, },
			{{-1, ERROR, 302}, {61, SHIFT, 310}, },
			{{-1, REDUCE, 102}, },
			{{-1, REDUCE, 85}, },
			{{-1, REDUCE, 120}, },
			{{-1, ERROR, 306}, {59, SHIFT, 311}, },
			{{-1, REDUCE, 118}, },
			{{-1, REDUCE, 106}, },
			{{-1, ERROR, 309}, {12, SHIFT, 30}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, ERROR, 310}, {62, SHIFT, 313}, },
			{{-1, REDUCE, 121}, },
			{{-1, ERROR, 312}, {73, SHIFT, 314}, },
			{{-1, ERROR, 313}, {12, SHIFT, 30}, {13, SHIFT, 31}, {14, SHIFT, 32}, {15, SHIFT, 33}, {16, SHIFT, 34}, {17, SHIFT, 35}, {18, SHIFT, 36}, {19, SHIFT, 37}, {20, SHIFT, 38}, {84, SHIFT, 40}, },
			{{-1, REDUCE, 135}, },
			{{-1, ERROR, 315}, {73, SHIFT, 316}, },
			{{-1, REDUCE, 136}, },
        };
    private final static int[][][] gotoTable =
        {
			{{-1, 13}, },
			{{-1, 14}, {16, 18}, {45, 18}, },
			{{-1, 15}, {16, 19}, },
			{{-1, 23}, {26, 50}, },
			{{-1, 24}, {23, 47}, {26, 51}, {50, 62}, },
			{{-1, 25}, {23, 48}, {24, 49}, {26, 52}, {47, 61}, {50, 63}, {51, 64}, {62, 71}, },
			{{-1, 29}, {53, 65}, {78, 87}, },
			{{-1, 41}, {46, 60}, },
			{{-1, 42}, {45, 58}, {267, 277}, {309, 312}, {313, 315}, },
			{{-1, 73}, {77, 86}, {84, 130}, {289, 302}, },
			{{-1, 74}, },
			{{-1, 81}, {83, 128}, {85, 131}, {133, 185}, },
			{{-1, 43}, {226, 255}, {257, 270}, },
			{{-1, 44}, {67, 75}, {77, 75}, {79, 108}, {84, 75}, {124, 108}, {229, 258}, {256, 269}, {260, 272}, {289, 75}, },
			{{-1, 56}, {57, 69}, },
			{{-1, 82}, {81, 127}, {83, 129}, {85, 132}, {128, 183}, {131, 184}, {133, 186}, {185, 243}, },
			{{-1, 109}, {124, 175}, },
			{{-1, 110}, },
			{{-1, 140}, {79, 111}, {110, 163}, {119, 173}, {124, 111}, {125, 111}, {171, 230}, {176, 111}, {220, 163}, {271, 285}, },
			{{-1, 164}, {220, 251}, },
			{{-1, 112}, {125, 179}, {176, 179}, },
			{{-1, 113}, {93, 145}, {187, 244}, {263, 274}, {288, 300}, },
			{{-1, 114}, },
			{{-1, 280}, {282, 294}, },
			{{-1, 281}, },
			{{-1, 115}, {149, 191}, {292, 303}, },
			{{-1, 116}, {126, 182}, {177, 182}, {180, 182}, {240, 182}, },
			{{-1, 231}, },
			{{-1, 232}, },
			{{-1, 297}, {298, 307}, },
			{{-1, 117}, },
			{{-1, 149}, },
			{{-1, 118}, {171, 233}, },
			{{-1, 150}, {171, 234}, },
			{{-1, 151}, {171, 235}, },
			{{-1, 119}, },
			{{-1, 160}, {238, 261}, },
			{{-1, 120}, {171, 236}, },
			{{-1, 121}, },
			{{-1, 122}, },
			{{-1, 123}, {166, 223}, },
			{{-1, 168}, {283, 295}, },
			{{-1, 248}, {266, 276}, {273, 287}, },
			{{-1, 249}, {91, 141}, {92, 143}, {94, 152}, {98, 156}, {99, 158}, {103, 161}, {153, 213}, {165, 222}, {170, 225}, {171, 237}, {212, 245}, {296, 306}, },
			{{-1, 142}, },
			{{-1, 212}, },
			{{-1, 153}, },
			{{-1, 45}, {0, 16}, },
			{{-1, 46}, },
			{{-1, 57}, },
			{{-1, 124}, },
			{{-1, 125}, {124, 176}, },
			{{-1, 126}, {124, 177}, {125, 180}, {176, 240}, },
			{{-1, 282}, },
			{{-1, 298}, },
        };
    private final static String[] errorMessages =
        {
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TClass TInterface expected.",
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TClass TInterface TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TName expected.",
			"TName expected.",
			"EOF expected.",
			"TExtends TImplements TLBrace expected.",
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TRBrace TName expected.",
			"TImplements TLBrace expected.",
			"TLBrace expected.",
			"TComma TLBrace TSemicolon expected.",
			"TCmpgt TName expected.",
			"TComma TSemicolon TLBracket TRParen TCmpgt TName TIdentifier expected.",
			"TAbstract TFinal TNative TPublic TProtected TPrivate TStatic TSynchronized TTransient TVolatile TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TName expected.",
			"TSemicolon TLParen expected.",
			"TRBracket expected.",
			"TLBrace TSemicolon expected.",
			"TBoolean TByte TShort TChar TInt TLong TFloat TDouble TRParen TName expected.",
			"TThrows TLBrace TSemicolon expected.",
			"TRParen expected.",
			"TComma TRParen expected.",
			"TBoolean TByte TShort TChar TInt TLong TFloat TDouble TUnknown TBreakpoint TCatch TEntermonitor TExitmonitor TGoto TIf TInterfaceinvoke TLookupswitch TNop TRet TReturn TSpecialinvoke TStaticinvoke TTableswitch TThrow TVirtualinvoke TRBrace TCmplt TName TIdentifier expected.",
			"TBoolean TByte TShort TChar TInt TLong TFloat TDouble TName expected.",
			"TIdentifier expected.",
			"TSemicolon expected.",
			"TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TLengthof TPlus TMinus TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TLParen expected.",
			"TSemicolon TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TCmplt expected.",
			"TLBracket TColon TDot TColonEquals TEquals expected.",
			"TLBracket TDot TColonEquals TEquals expected.",
			"TBreakpoint TCatch TEntermonitor TExitmonitor TGoto TIf TInterfaceinvoke TLookupswitch TNop TRet TReturn TSpecialinvoke TStaticinvoke TTableswitch TThrow TVirtualinvoke TRBrace TCmplt TIdentifier expected.",
			"TColon expected.",
			"TCatch TRBrace expected.",
			"TEquals expected.",
			"TSemicolon TEquals expected.",
			"TFrom expected.",
			"TCmp TCmpg TCmpl TGoto TInstanceof TComma TSemicolon TLBracket TRBracket TRParen TDot TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TGoto TInstanceof TComma TSemicolon TRBracket TRParen TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TGoto TComma TSemicolon TRBracket TRParen TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TTo TWith TSemicolon expected.",
			"TGoto expected.",
			"TCmp TCmpg TCmpl TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TComma TSemicolon expected.",
			"TAtIdentifier expected.",
			"TInterfaceinvoke TLengthof TNew TNewarray TNewmultiarray TSpecialinvoke TStaticinvoke TVirtualinvoke TLParen TCmplt TPlus TMinus TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TDot expected.",
			"TBreakpoint TCase TCatch TDefault TEntermonitor TExitmonitor TGoto TIf TInterfaceinvoke TLookupswitch TNop TRet TReturn TSpecialinvoke TStaticinvoke TTableswitch TThrow TVirtualinvoke TRBrace TCmplt TIdentifier expected.",
			"TGoto TSemicolon expected.",
			"TRParen TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TCmp TCmpg TCmpl TInstanceof TSemicolon TLBracket TDot TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TCmp TCmpg TCmpl TInstanceof TSemicolon TAnd TOr TXor TMod TCmpeq TCmpne TCmpgt TCmpge TCmplt TCmple TShl TShr TUshr TPlus TMinus TMult TDiv expected.",
			"TTo expected.",
			"TVoid TBoolean TByte TShort TChar TInt TLong TFloat TDouble TName expected.",
			"TCase TDefault expected.",
			"TWith expected.",
			"TCmpgt expected.",
			"TIntegerConstant expected.",
			"TCase TDefault TRBrace expected.",
			"TLBracket expected.",
			"TRBracket TIdentifier TIntegerConstant TFloatConstant TStringConstant expected.",
			"TSemicolon TLBracket expected.",
        };
    private final static int[] errors =
        {
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 3, 1, 2, 0, 4, 1, 2, 2, 2, 5, 6, 7, 3, 4, 6, 8, 7, 9, 10, 10, 10, 10, 10, 10, 10, 10, 3, 10, 5, 2, 10, 9, 11, 5, 7, 3, 3, 6, 7, 3, 2, 12, 13, 10, 10, 2, 3, 5, 3, 7, 3, 3, 14, 5, 15, 10, 10, 12, 3, 16, 17, 18, 18, 5, 15, 2, 19, 5, 14, 5, 16, 20, 16, 17, 14, 21, 22, 2, 23, 23, 21, 24, 21, 25, 22, 26, 26, 21, 27, 25, 23, 21, 5, 2, 28, 21, 19, 21, 29, 30, 31, 25, 30, 32, 33, 22, 21, 33, 34, 34, 34, 19, 30, 32, 5, 14, 5, 17, 14, 5, 16, 30, 35, 36, 37, 37, 37, 38, 22, 37, 22, 39, 22, 23, 23, 23, 40, 40, 40, 41, 23, 30, 30, 22, 30, 22, 2, 25, 22, 31, 42, 22, 23, 27, 43, 34, 30, 23, 44, 30, 45, 5, 19, 30, 32, 5, 30, 32, 5, 32, 5, 5, 14, 5, 21, 30, 30, 46, 30, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 23, 47, 30, 30, 31, 48, 30, 2, 21, 19, 13, 34, 22, 17, 20, 25, 25, 20, 49, 22, 22, 22, 22, 22, 22, 50, 27, 5, 32, 5, 5, 5, 51, 47, 2, 22, 17, 18, 31, 22, 34, 30, 7, 22, 20, 20, 17, 30, 20, 25, 5, 21, 31, 22, 23, 52, 53, 17, 17, 21, 22, 48, 54, 25, 17, 55, 56, 31, 57, 31, 57, 58, 58, 22, 22, 17, 21, 15, 34, 31, 40, 22, 57, 22, 59, 60, 60, 22, 22, 31, 17, 57, 30, 60, 13, 60, 32, 52, 31, 60, 55, 52, 25, 55, 25, 
        };
}
