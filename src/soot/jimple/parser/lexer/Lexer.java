package soot.jimple.parser.lexer;

import java.io.*;
import java.util.*;
import ca.mcgill.sable.util.*;
import soot.jimple.parser.node.*;

public class Lexer
{
    protected Token token;
    protected State state = State.INITIAL;

    private PushbackReader in;
    private int line;
    private int pos;
    private boolean cr;
    private boolean eof;
    private final StringBuffer text = new StringBuffer();

    protected void filter() throws LexerException, IOException
    {
    }

    public Lexer(PushbackReader in)
    {
        this.in = in;

        if(gotoTable == null)
        {
            try
            {
                DataInputStream s = new DataInputStream(
                    new BufferedInputStream(
                    Lexer.class.getResourceAsStream("lexer.dat")));

                // read gotoTable
                int length = s.readInt();
                gotoTable = new int[length][][];
                for(int i = 0; i < gotoTable.length; i++)
                {
                    length = s.readInt();
                    gotoTable[i] = new int[length][3];
                    for(int j = 0; j < gotoTable[i].length; j++)
                    {
                        for(int k = 0; k < 3; k++)
                        {
                            gotoTable[i][j][k] = s.readInt();
                        }
                    }
                }

                // read accept
                length = s.readInt();
                accept = new int[length][];
                for(int i = 0; i < accept.length; i++)
                {
                    length = s.readInt();
                    accept[i] = new int[length];
                    for(int j = 0; j < accept[i].length; j++)
                    {
                        accept[i][j] = s.readInt();
                    }
                }

                s.close();
            }
            catch(Exception e)
            {
                throw new RuntimeException("Unable to read lexer.dat.");
            }
        }
    }

    public Token peek() throws LexerException, IOException
    {
        while(token == null)
        {
            token = getToken();
            filter();
        }

        return token;
    }

    public Token next() throws LexerException, IOException
    {
        while(token == null)
        {
            token = getToken();
            filter();
        }

        Token result = token;
        token = null;
        return result;
    }

    protected Token getToken() throws IOException, LexerException
    {
        int dfa_state = 0;

        int start_pos = pos;
        int start_line = line;

        int accept_state = -1;
        int accept_token = -1;
        int accept_length = -1;
        int accept_pos = -1;
        int accept_line = -1;

        text.setLength(0);

        while(true)
        {
            int c = getChar();

            if(c != -1)
            {
                switch(c)
                {
                case 10:
                    if(cr)
                    {
                        cr = false;
                    }
                    else
                    {
                        line++;
                        pos = 0;
                    }
                    break;
                case 13:
                    line++;
                    pos = 0;
                    cr = true;
                    break;
                default:
                    pos++;
                    cr = false;
                    break;
                };

                text.append((char) c);

                do
                {
                    int oldState = (dfa_state < -1) ? (-2 -dfa_state) : dfa_state;

                    dfa_state = -1;

                    int low = 0;
                    int high = gotoTable[oldState].length - 1;

                    while(low <= high)
                    {
                        int middle = (low + high) / 2;

                        if(c < gotoTable[oldState][middle][0])
                        {
                            high = middle - 1;
                        }
                        else if(c > gotoTable[oldState][middle][1])
                        {
                            low = middle + 1;
                        }
                        else
                        {
                            dfa_state = gotoTable[oldState][middle][2];
                            break;
                        }
                    }
                }while(dfa_state < -1);
            }
            else
            {
                dfa_state = -1;
            }

            if(dfa_state >= 0)
            {
                if(accept[state.id()][dfa_state] != -1)
                {
                    accept_state = dfa_state;
                    accept_token = accept[state.id()][dfa_state];
                    accept_length = text.length();
                    accept_pos = pos;
                    accept_line = line;
                }
            }
            else
            {
                if(accept_state != -1)
                {
                    switch(accept_token)
                    {
                    case 0:
                        {
                            Token token = new0(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 1:
                        {
                            Token token = new1(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 2:
                        {
                            Token token = new2(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 3:
                        {
                            Token token = new3(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 4:
                        {
                            Token token = new4(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 5:
                        {
                            Token token = new5(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 6:
                        {
                            Token token = new6(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 7:
                        {
                            Token token = new7(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 8:
                        {
                            Token token = new8(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 9:
                        {
                            Token token = new9(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 10:
                        {
                            Token token = new10(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 11:
                        {
                            Token token = new11(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 12:
                        {
                            Token token = new12(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 13:
                        {
                            Token token = new13(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 14:
                        {
                            Token token = new14(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 15:
                        {
                            Token token = new15(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 16:
                        {
                            Token token = new16(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 17:
                        {
                            Token token = new17(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 18:
                        {
                            Token token = new18(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 19:
                        {
                            Token token = new19(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 20:
                        {
                            Token token = new20(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 21:
                        {
                            Token token = new21(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 22:
                        {
                            Token token = new22(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 23:
                        {
                            Token token = new23(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 24:
                        {
                            Token token = new24(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 25:
                        {
                            Token token = new25(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 26:
                        {
                            Token token = new26(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 27:
                        {
                            Token token = new27(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 28:
                        {
                            Token token = new28(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 29:
                        {
                            Token token = new29(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 30:
                        {
                            Token token = new30(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 31:
                        {
                            Token token = new31(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 32:
                        {
                            Token token = new32(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 33:
                        {
                            Token token = new33(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 34:
                        {
                            Token token = new34(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 35:
                        {
                            Token token = new35(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 36:
                        {
                            Token token = new36(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 37:
                        {
                            Token token = new37(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 38:
                        {
                            Token token = new38(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 39:
                        {
                            Token token = new39(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 40:
                        {
                            Token token = new40(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 41:
                        {
                            Token token = new41(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 42:
                        {
                            Token token = new42(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 43:
                        {
                            Token token = new43(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 44:
                        {
                            Token token = new44(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 45:
                        {
                            Token token = new45(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 46:
                        {
                            Token token = new46(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 47:
                        {
                            Token token = new47(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 48:
                        {
                            Token token = new48(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 49:
                        {
                            Token token = new49(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 50:
                        {
                            Token token = new50(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 51:
                        {
                            Token token = new51(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 52:
                        {
                            Token token = new52(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 53:
                        {
                            Token token = new53(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 54:
                        {
                            Token token = new54(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 55:
                        {
                            Token token = new55(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 56:
                        {
                            Token token = new56(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 57:
                        {
                            Token token = new57(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 58:
                        {
                            Token token = new58(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 59:
                        {
                            Token token = new59(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 60:
                        {
                            Token token = new60(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 61:
                        {
                            Token token = new61(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 62:
                        {
                            Token token = new62(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 63:
                        {
                            Token token = new63(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 64:
                        {
                            Token token = new64(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 65:
                        {
                            Token token = new65(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 66:
                        {
                            Token token = new66(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 67:
                        {
                            Token token = new67(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 68:
                        {
                            Token token = new68(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 69:
                        {
                            Token token = new69(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 70:
                        {
                            Token token = new70(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 71:
                        {
                            Token token = new71(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 72:
                        {
                            Token token = new72(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 73:
                        {
                            Token token = new73(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 74:
                        {
                            Token token = new74(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 75:
                        {
                            Token token = new75(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 76:
                        {
                            Token token = new76(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 77:
                        {
                            Token token = new77(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 78:
                        {
                            Token token = new78(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 79:
                        {
                            Token token = new79(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 80:
                        {
                            Token token = new80(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 81:
                        {
                            Token token = new81(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 82:
                        {
                            Token token = new82(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 83:
                        {
                            Token token = new83(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 84:
                        {
                            Token token = new84(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 85:
                        {
                            Token token = new85(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 86:
                        {
                            Token token = new86(
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 87:
                        {
                            Token token = new87(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 88:
                        {
                            Token token = new88(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 89:
                        {
                            Token token = new89(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 90:
                        {
                            Token token = new90(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 91:
                        {
                            Token token = new91(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 92:
                        {
                            Token token = new92(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 93:
                        {
                            Token token = new93(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 94:
                        {
                            Token token = new94(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 95:
                        {
                            Token token = new95(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    case 96:
                        {
                            Token token = new96(
                                getText(accept_length),
                                start_line + 1,
                                start_pos + 1);
                            pushBack(accept_length);
                            pos = accept_pos;
                            line = accept_line;
                            return token;
                        }
                    }
                }
                else
                {
                    if(text.length() > 0)
                    {
                        throw new LexerException(
                            "[" + (start_line + 1) + "," + (start_pos + 1) + "]" +
                            " Unknown token: " + text);
                    }
                    else
                    {
                        EOF token = new EOF(
                            start_line + 1,
                            start_pos + 1);
                        return token;
                    }
                }
            }
        }
    }

    Token new0(int line, int pos) { return new TAbstract(line, pos); }
    Token new1(int line, int pos) { return new TFinal(line, pos); }
    Token new2(int line, int pos) { return new TNative(line, pos); }
    Token new3(int line, int pos) { return new TPublic(line, pos); }
    Token new4(int line, int pos) { return new TProtected(line, pos); }
    Token new5(int line, int pos) { return new TPrivate(line, pos); }
    Token new6(int line, int pos) { return new TStatic(line, pos); }
    Token new7(int line, int pos) { return new TSynchronized(line, pos); }
    Token new8(int line, int pos) { return new TTransient(line, pos); }
    Token new9(int line, int pos) { return new TVolatile(line, pos); }
    Token new10(int line, int pos) { return new TClass(line, pos); }
    Token new11(int line, int pos) { return new TInterface(line, pos); }
    Token new12(int line, int pos) { return new TVoid(line, pos); }
    Token new13(int line, int pos) { return new TBoolean(line, pos); }
    Token new14(int line, int pos) { return new TByte(line, pos); }
    Token new15(int line, int pos) { return new TShort(line, pos); }
    Token new16(int line, int pos) { return new TChar(line, pos); }
    Token new17(int line, int pos) { return new TInt(line, pos); }
    Token new18(int line, int pos) { return new TLong(line, pos); }
    Token new19(int line, int pos) { return new TFloat(line, pos); }
    Token new20(int line, int pos) { return new TDouble(line, pos); }
    Token new21(int line, int pos) { return new TNullType(line, pos); }
    Token new22(int line, int pos) { return new TUnknown(line, pos); }
    Token new23(int line, int pos) { return new TExtends(line, pos); }
    Token new24(int line, int pos) { return new TImplements(line, pos); }
    Token new25(int line, int pos) { return new TBreakpoint(line, pos); }
    Token new26(int line, int pos) { return new TCase(line, pos); }
    Token new27(int line, int pos) { return new TCatch(line, pos); }
    Token new28(int line, int pos) { return new TCmp(line, pos); }
    Token new29(int line, int pos) { return new TCmpg(line, pos); }
    Token new30(int line, int pos) { return new TCmpl(line, pos); }
    Token new31(int line, int pos) { return new TDefault(line, pos); }
    Token new32(int line, int pos) { return new TEntermonitor(line, pos); }
    Token new33(int line, int pos) { return new TExitmonitor(line, pos); }
    Token new34(int line, int pos) { return new TFrom(line, pos); }
    Token new35(int line, int pos) { return new TGoto(line, pos); }
    Token new36(int line, int pos) { return new TIf(line, pos); }
    Token new37(int line, int pos) { return new TInstanceof(line, pos); }
    Token new38(int line, int pos) { return new TInterfaceinvoke(line, pos); }
    Token new39(int line, int pos) { return new TLengthof(line, pos); }
    Token new40(int line, int pos) { return new TLookupswitch(line, pos); }
    Token new41(int line, int pos) { return new TNeg(line, pos); }
    Token new42(int line, int pos) { return new TNew(line, pos); }
    Token new43(int line, int pos) { return new TNewarray(line, pos); }
    Token new44(int line, int pos) { return new TNewmultiarray(line, pos); }
    Token new45(int line, int pos) { return new TNop(line, pos); }
    Token new46(int line, int pos) { return new TRet(line, pos); }
    Token new47(int line, int pos) { return new TReturn(line, pos); }
    Token new48(int line, int pos) { return new TSpecialinvoke(line, pos); }
    Token new49(int line, int pos) { return new TStaticinvoke(line, pos); }
    Token new50(int line, int pos) { return new TTableswitch(line, pos); }
    Token new51(int line, int pos) { return new TThrow(line, pos); }
    Token new52(int line, int pos) { return new TThrows(line, pos); }
    Token new53(int line, int pos) { return new TTo(line, pos); }
    Token new54(int line, int pos) { return new TVirtualinvoke(line, pos); }
    Token new55(int line, int pos) { return new TWith(line, pos); }
    Token new56(int line, int pos) { return new TNull(line, pos); }
    Token new57(int line, int pos) { return new TComma(line, pos); }
    Token new58(int line, int pos) { return new TLBrace(line, pos); }
    Token new59(int line, int pos) { return new TRBrace(line, pos); }
    Token new60(int line, int pos) { return new TSemicolon(line, pos); }
    Token new61(int line, int pos) { return new TLBracket(line, pos); }
    Token new62(int line, int pos) { return new TRBracket(line, pos); }
    Token new63(int line, int pos) { return new TLParen(line, pos); }
    Token new64(int line, int pos) { return new TRParen(line, pos); }
    Token new65(int line, int pos) { return new TColon(line, pos); }
    Token new66(int line, int pos) { return new TDot(line, pos); }
    Token new67(int line, int pos) { return new TQuote(line, pos); }
    Token new68(int line, int pos) { return new TColonEquals(line, pos); }
    Token new69(int line, int pos) { return new TEquals(line, pos); }
    Token new70(int line, int pos) { return new TAnd(line, pos); }
    Token new71(int line, int pos) { return new TOr(line, pos); }
    Token new72(int line, int pos) { return new TXor(line, pos); }
    Token new73(int line, int pos) { return new TMod(line, pos); }
    Token new74(int line, int pos) { return new TCmpeq(line, pos); }
    Token new75(int line, int pos) { return new TCmpne(line, pos); }
    Token new76(int line, int pos) { return new TCmpgt(line, pos); }
    Token new77(int line, int pos) { return new TCmpge(line, pos); }
    Token new78(int line, int pos) { return new TCmplt(line, pos); }
    Token new79(int line, int pos) { return new TCmple(line, pos); }
    Token new80(int line, int pos) { return new TShl(line, pos); }
    Token new81(int line, int pos) { return new TShr(line, pos); }
    Token new82(int line, int pos) { return new TUshr(line, pos); }
    Token new83(int line, int pos) { return new TPlus(line, pos); }
    Token new84(int line, int pos) { return new TMinus(line, pos); }
    Token new85(int line, int pos) { return new TMult(line, pos); }
    Token new86(int line, int pos) { return new TDiv(line, pos); }
    Token new87(String text, int line, int pos) { return new TFullIdentifier(text, line, pos); }
    Token new88(String text, int line, int pos) { return new TQuotedName(text, line, pos); }
    Token new89(String text, int line, int pos) { return new TIdentifier(text, line, pos); }
    Token new90(String text, int line, int pos) { return new TAtIdentifier(text, line, pos); }
    Token new91(String text, int line, int pos) { return new TBoolConstant(text, line, pos); }
    Token new92(String text, int line, int pos) { return new TIntegerConstant(text, line, pos); }
    Token new93(String text, int line, int pos) { return new TFloatConstant(text, line, pos); }
    Token new94(String text, int line, int pos) { return new TStringConstant(text, line, pos); }
    Token new95(String text, int line, int pos) { return new TBlank(text, line, pos); }
    Token new96(String text, int line, int pos) { return new TComment(text, line, pos); }

    private int getChar() throws IOException
    {
        if(eof)
        {
            return -1;
        }

        int result = in.read();

        if(result == -1)
        {
            eof = true;
        }

        return result;
    }

    private void pushBack(int acceptLength) throws IOException
    {
        int length = text.length();
        for(int i = length - 1; i >= acceptLength; i--)
        {
            eof = false;

            in.unread(text.charAt(i));
        }
    }

    protected void unread(Token token) throws IOException
    {
        String text = token.getText();
        int length = text.length();

        for(int i = length - 1; i >= 0; i--)
        {
            eof = false;

            in.unread(text.charAt(i));
        }

        pos = token.getPos() - 1;
        line = token.getLine() - 1;
    }

    private String getText(int acceptLength)
    {
        StringBuffer s = new StringBuffer(acceptLength);
        for(int i = 0; i < acceptLength; i++)
        {
            s.append(text.charAt(i));
        }

        return s.toString();
    }

    private static int[][][] gotoTable;
/*  {
		{{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {33, 33, 5}, {34, 34, 6}, {35, 35, 7}, {36, 36, 8}, {37, 37, 9}, {38, 38, 10}, {39, 39, 11}, {40, 40, 12}, {41, 41, 13}, {42, 42, 14}, {43, 43, 15}, {44, 44, 16}, {45, 45, 17}, {46, 46, 18}, {47, 47, 19}, {48, 48, 20}, {49, 57, 21}, {58, 58, 22}, {59, 59, 23}, {60, 60, 24}, {61, 61, 25}, {62, 62, 26}, {64, 64, 27}, {65, 90, 28}, {91, 91, 29}, {93, 93, 30}, {94, 94, 31}, {95, 95, 32}, {97, 97, 33}, {98, 98, 34}, {99, 99, 35}, {100, 100, 36}, {101, 101, 37}, {102, 102, 38}, {103, 103, 39}, {104, 104, 40}, {105, 105, 41}, {106, 107, 40}, {108, 108, 42}, {109, 109, 40}, {110, 110, 43}, {111, 111, 40}, {112, 112, 44}, {113, 113, 40}, {114, 114, 45}, {115, 115, 46}, {116, 116, 47}, {117, 117, 48}, {118, 118, 49}, {119, 119, 50}, {120, 122, 40}, {123, 123, 51}, {124, 124, 52}, {125, 125, 53}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{61, 61, 54}, },
		{{10, 10, 55}, {13, 13, 55}, {32, 33, 56}, {34, 34, 57}, {35, 91, 58}, {92, 92, 59}, {93, 126, 58}, },
		{{45, 45, 60}, {73, 73, 61}, {78, 78, 62}, },
		{{36, 36, 63}, {48, 57, 64}, {65, 90, 65}, {95, 95, 66}, {97, 122, 67}, },
		{},
		{},
		{{0, 9, 68}, {11, 12, 68}, {14, 38, 68}, {40, 65535, 68}, },
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{{42, 42, 69}, {47, 47, 70}, },
		{{46, 46, 71}, {48, 55, 72}, {56, 57, 21}, {76, 76, 73}, {88, 88, 74}, {120, 120, 75}, },
		{{46, 46, 71}, {48, 57, 21}, {76, 76, 73}, },
		{{61, 61, 76}, },
		{},
		{{60, 60, 77}, {61, 61, 78}, {99, 99, 79}, {105, 105, 80}, },
		{{61, 61, 81}, },
		{{61, 61, 82}, {62, 62, 83}, },
		{{99, 99, 84}, {112, 112, 85}, {116, 116, 86}, },
		{{36, 122, -10}, },
		{},
		{},
		{},
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 67}, {98, 98, 87}, {99, 122, 67}, },
		{{36, 95, -10}, {97, 110, 67}, {111, 111, 88}, {112, 113, 67}, {114, 114, 89}, {115, 120, 67}, {121, 121, 90}, {122, 122, 67}, },
		{{36, 95, -10}, {97, 97, 91}, {98, 103, 67}, {104, 104, 92}, {105, 107, 67}, {108, 108, 93}, {109, 109, 94}, {110, 122, 67}, },
		{{36, 95, -10}, {97, 100, 67}, {101, 101, 95}, {102, 110, 67}, {111, 111, 96}, {112, 122, 67}, },
		{{36, 95, -10}, {97, 109, 67}, {110, 110, 97}, {111, 119, 67}, {120, 120, 98}, {121, 122, 67}, },
		{{36, 95, -10}, {97, 97, 99}, {98, 104, 67}, {105, 105, 100}, {106, 107, 67}, {108, 108, 101}, {109, 113, 67}, {114, 114, 102}, {115, 122, 67}, },
		{{36, 110, -36}, {111, 111, 103}, {112, 122, 67}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 101, 67}, {102, 102, 104}, {103, 108, 67}, {109, 109, 105}, {110, 110, 106}, {111, 122, 67}, },
		{{36, 100, -38}, {101, 101, 107}, {102, 110, 67}, {111, 111, 108}, {112, 122, 67}, },
		{{36, 95, -10}, {97, 97, 109}, {98, 100, 67}, {101, 101, 110}, {102, 110, 67}, {111, 111, 111}, {112, 116, 67}, {117, 117, 112}, {118, 122, 67}, },
		{{36, 95, -10}, {97, 113, 67}, {114, 114, 113}, {115, 116, 67}, {117, 117, 114}, {118, 122, 67}, },
		{{36, 100, -38}, {101, 101, 115}, {102, 122, 67}, },
		{{36, 95, -10}, {97, 103, 67}, {104, 104, 116}, {105, 111, 67}, {112, 112, 117}, {113, 115, 67}, {116, 116, 118}, {117, 120, 67}, {121, 121, 119}, {122, 122, 67}, },
		{{36, 95, -10}, {97, 97, 120}, {98, 103, 67}, {104, 104, 121}, {105, 110, 67}, {111, 111, 122}, {112, 113, 67}, {114, 114, 123}, {115, 122, 67}, },
		{{36, 109, -39}, {110, 110, 124}, {111, 122, 67}, },
		{{36, 95, -10}, {97, 104, 67}, {105, 105, 125}, {106, 110, 67}, {111, 111, 126}, {112, 122, 67}, },
		{{36, 104, -51}, {105, 105, 127}, {106, 122, 67}, },
		{},
		{},
		{},
		{},
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{},
		{{10, 126, -8}, },
		{{10, 13, -8}, {32, 32, 128}, {33, 33, 56}, {34, 34, 129}, {35, 35, 130}, {36, 38, 58}, {39, 39, 131}, {40, 45, 58}, {46, 46, 132}, {47, 91, 58}, {92, 92, 133}, {93, 116, 58}, {117, 117, 134}, {118, 126, 58}, },
		{{73, 73, 61}, },
		{{110, 110, 135}, },
		{{97, 97, 136}, },
		{{36, 36, 63}, {46, 46, 137}, {48, 122, -10}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{0, 38, -13}, {39, 39, 138}, {40, 65535, 68}, },
		{{0, 41, 139}, {42, 42, 140}, {43, 65535, 139}, },
		{{0, 9, 141}, {11, 12, 141}, {14, 65535, 141}, },
		{{48, 57, 142}, },
		{{46, 76, -22}, },
		{},
		{{48, 57, 143}, {65, 70, 144}, {97, 102, 145}, },
		{{48, 102, -76}, },
		{},
		{},
		{},
		{{108, 108, 146}, },
		{{110, 110, 147}, },
		{},
		{},
		{{62, 62, 148}, },
		{{97, 97, 149}, },
		{{97, 97, 150}, },
		{{104, 104, 151}, },
		{{36, 95, -65}, {97, 114, 67}, {115, 115, 152}, {116, 122, 67}, },
		{{36, 95, -65}, {97, 110, 67}, {111, 111, 153}, {112, 122, 67}, },
		{{36, 95, -65}, {97, 100, 67}, {101, 101, 154}, {102, 122, 67}, },
		{{36, 95, -65}, {97, 115, 67}, {116, 116, 155}, {117, 122, 67}, },
		{{36, 114, -89}, {115, 115, 156}, {116, 116, 157}, {117, 122, 67}, },
		{{36, 95, -65}, {97, 97, 158}, {98, 122, 67}, },
		{{36, 95, -65}, {97, 97, 159}, {98, 122, 67}, },
		{{36, 95, -65}, {97, 111, 67}, {112, 112, 160}, {113, 122, 67}, },
		{{36, 95, -65}, {97, 101, 67}, {102, 102, 161}, {103, 122, 67}, },
		{{36, 95, -65}, {97, 116, 67}, {117, 117, 162}, {118, 122, 67}, },
		{{36, 115, -92}, {116, 116, 163}, {117, 122, 67}, },
		{{36, 95, -65}, {97, 104, 67}, {105, 105, 164}, {106, 115, 67}, {116, 116, 165}, {117, 122, 67}, },
		{{36, 95, -65}, {97, 107, 67}, {108, 108, 166}, {109, 122, 67}, },
		{{36, 95, -65}, {97, 109, 67}, {110, 110, 167}, {111, 122, 67}, },
		{{36, 110, -90}, {111, 111, 168}, {112, 122, 67}, },
		{{36, 110, -90}, {111, 111, 169}, {112, 122, 67}, },
		{{36, 115, -92}, {116, 116, 170}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 111, -96}, {112, 112, 171}, {113, 122, 67}, },
		{{36, 114, -89}, {115, 115, 172}, {116, 116, 173}, {117, 122, 67}, },
		{{36, 109, -102}, {110, 110, 174}, {111, 122, 67}, },
		{{36, 109, -102}, {110, 110, 175}, {111, 111, 176}, {112, 122, 67}, },
		{{36, 115, -92}, {116, 116, 177}, {117, 122, 67}, },
		{{36, 95, -65}, {97, 102, 67}, {103, 103, 178}, {104, 118, 67}, {119, 119, 179}, {120, 122, 67}, },
		{{36, 111, -96}, {112, 112, 180}, {113, 122, 67}, },
		{{36, 107, -101}, {108, 108, 181}, {109, 122, 67}, },
		{{36, 104, -100}, {105, 105, 182}, {106, 110, 67}, {111, 111, 183}, {112, 122, 67}, },
		{{36, 95, -65}, {97, 97, 67}, {98, 98, 184}, {99, 122, 67}, },
		{{36, 115, -92}, {116, 116, 185}, {117, 122, 67}, },
		{{36, 110, -90}, {111, 111, 186}, {112, 122, 67}, },
		{{36, 100, -91}, {101, 101, 187}, {102, 122, 67}, },
		{{36, 95, -65}, {97, 97, 188}, {98, 122, 67}, },
		{{36, 109, -102}, {110, 110, 189}, {111, 122, 67}, },
		{{36, 97, -116}, {98, 98, 190}, {99, 122, 67}, },
		{{36, 95, -65}, {97, 113, 67}, {114, 114, 191}, {115, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 95, -65}, {97, 97, 192}, {98, 116, 67}, {117, 117, 193}, {118, 122, 67}, },
		{{36, 95, -65}, {97, 106, 67}, {107, 107, 194}, {108, 122, 67}, },
		{{36, 113, -123}, {114, 114, 195}, {115, 122, 67}, },
		{{36, 104, -100}, {105, 105, 196}, {106, 107, 67}, {108, 108, 197}, {109, 122, 67}, },
		{{36, 115, -92}, {116, 116, 198}, {117, 122, 67}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -61}, },
		{{10, 34, -8}, {35, 47, 58}, {48, 57, 199}, {58, 64, 58}, {65, 70, 200}, {71, 91, 58}, {92, 92, 59}, {93, 96, 58}, {97, 102, 201}, {103, 126, 58}, },
		{{102, 102, 202}, },
		{{78, 78, 203}, },
		{{36, 36, 204}, {65, 90, 205}, {95, 95, 206}, {97, 122, 207}, },
		{},
		{{0, 65535, -71}, },
		{{0, 41, 208}, {42, 42, 140}, {43, 46, 208}, {47, 47, 209}, {48, 65535, 208}, },
		{{0, 65535, -72}, },
		{{48, 57, 142}, {69, 69, 210}, {70, 70, 211}, {101, 101, 212}, {102, 102, 213}, },
		{{48, 70, -76}, {76, 76, 73}, {97, 102, 145}, },
		{{48, 102, -145}, },
		{{48, 102, -145}, },
		{{105, 105, 214}, },
		{{105, 105, 215}, },
		{},
		{{117, 117, 216}, },
		{{114, 114, 217}, },
		{{105, 105, 218}, },
		{{36, 115, -92}, {116, 116, 219}, {117, 122, 67}, },
		{{36, 107, -101}, {108, 108, 220}, {109, 122, 67}, },
		{{36, 95, -65}, {97, 97, 221}, {98, 122, 67}, },
		{{36, 100, -91}, {101, 101, 222}, {102, 122, 67}, },
		{{36, 100, -91}, {101, 101, 223}, {102, 122, 67}, },
		{{36, 95, -65}, {97, 98, 67}, {99, 99, 224}, {100, 122, 67}, },
		{{36, 113, -123}, {114, 114, 225}, {115, 122, 67}, },
		{{36, 114, -89}, {115, 115, 226}, {116, 122, 67}, },
		{{36, 102, -112}, {103, 103, 227}, {104, 107, 67}, {108, 108, 228}, {109, 122, 67}, },
		{{36, 95, -65}, {97, 97, 229}, {98, 122, 67}, },
		{{36, 97, -116}, {98, 98, 230}, {99, 122, 67}, },
		{{36, 100, -91}, {101, 101, 231}, {102, 122, 67}, },
		{{36, 115, -92}, {116, 116, 232}, {117, 122, 67}, },
		{{36, 100, -91}, {101, 101, 233}, {102, 122, 67}, },
		{{36, 114, -89}, {115, 115, 234}, {116, 122, 67}, },
		{{36, 95, -65}, {97, 97, 235}, {98, 122, 67}, },
		{{36, 95, -65}, {97, 97, 236}, {98, 122, 67}, },
		{{36, 95, -65}, {97, 108, 67}, {109, 109, 237}, {110, 122, 67}, },
		{{36, 110, -90}, {111, 111, 238}, {112, 122, 67}, },
		{{36, 107, -101}, {108, 108, 239}, {109, 122, 67}, },
		{{36, 115, -92}, {116, 116, 240}, {117, 122, 67}, },
		{{36, 100, -91}, {101, 101, 241}, {102, 122, 67}, },
		{{36, 102, -112}, {103, 103, 242}, {104, 122, 67}, },
		{{36, 102, -112}, {103, 103, 243}, {104, 122, 67}, },
		{{36, 106, -126}, {107, 107, 244}, {108, 122, 67}, },
		{{36, 104, -100}, {105, 105, 245}, {106, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 95, -65}, {97, 97, 246}, {98, 108, 67}, {109, 109, 247}, {110, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 107, -101}, {108, 108, 248}, {109, 122, 67}, },
		{{36, 95, -65}, {97, 117, 67}, {118, 118, 249}, {119, 122, 67}, },
		{{36, 115, -92}, {116, 116, 250}, {117, 122, 67}, },
		{{36, 107, -101}, {108, 108, 251}, {109, 122, 67}, },
		{{36, 116, -98}, {117, 117, 252}, {118, 122, 67}, },
		{{36, 113, -123}, {114, 114, 253}, {115, 122, 67}, },
		{{36, 98, -159}, {99, 99, 254}, {100, 122, 67}, },
		{{36, 115, -92}, {116, 116, 255}, {117, 122, 67}, },
		{{36, 98, -159}, {99, 99, 256}, {100, 122, 67}, },
		{{36, 107, -101}, {108, 108, 257}, {109, 122, 67}, },
		{{36, 110, -90}, {111, 111, 258}, {112, 122, 67}, },
		{{36, 109, -102}, {110, 110, 259}, {111, 122, 67}, },
		{{36, 100, -91}, {101, 101, 260}, {102, 122, 67}, },
		{{36, 109, -102}, {110, 110, 261}, {111, 122, 67}, },
		{{36, 115, -92}, {116, 116, 262}, {117, 122, 67}, },
		{{36, 95, -65}, {97, 99, 67}, {100, 100, 263}, {101, 122, 67}, },
		{{36, 95, -65}, {97, 97, 264}, {98, 122, 67}, },
		{{36, 95, -65}, {97, 103, 67}, {104, 104, 265}, {105, 122, 67}, },
		{{10, 47, -136}, {48, 57, 266}, {58, 64, 58}, {65, 70, 267}, {71, 96, -136}, {97, 102, 268}, {103, 126, 58}, },
		{{10, 126, -201}, },
		{{10, 126, -201}, },
		{{105, 105, 269}, },
		{{70, 70, 270}, {102, 102, 271}, },
		{{36, 36, 272}, {48, 57, 273}, {65, 90, 274}, {95, 95, 275}, {97, 122, 276}, },
		{{36, 122, -206}, },
		{{36, 122, -206}, },
		{{36, 122, -206}, },
		{{0, 41, 277}, {42, 42, 278}, {43, 65535, 277}, },
		{},
		{{43, 43, 279}, {45, 45, 280}, {48, 57, 281}, },
		{},
		{{43, 57, -212}, },
		{},
		{{110, 110, 282}, },
		{{116, 116, 283}, },
		{{103, 103, 284}, },
		{{97, 97, 285}, },
		{{115, 115, 286}, },
		{{36, 113, -123}, {114, 114, 287}, {115, 122, 67}, },
		{{36, 100, -91}, {101, 101, 288}, {102, 122, 67}, },
		{{36, 106, -126}, {107, 107, 289}, {108, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 103, -200}, {104, 104, 290}, {105, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 114, -89}, {115, 115, 291}, {116, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 116, -98}, {117, 117, 292}, {118, 122, 67}, },
		{{36, 107, -101}, {108, 108, 293}, {109, 122, 67}, },
		{{36, 113, -123}, {114, 114, 294}, {115, 122, 67}, },
		{{36, 108, -171}, {109, 109, 295}, {110, 122, 67}, },
		{{36, 109, -102}, {110, 110, 296}, {111, 122, 67}, },
		{{36, 100, -91}, {101, 101, 297}, {102, 122, 67}, },
		{{36, 107, -101}, {108, 108, 298}, {109, 122, 67}, },
		{{36, 115, -92}, {116, 116, 299}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 100, -91}, {101, 101, 300}, {102, 122, 67}, },
		{{36, 95, -65}, {97, 97, 301}, {98, 122, 67}, },
		{{36, 113, -123}, {114, 114, 302}, {115, 122, 67}, },
		{{36, 115, -92}, {116, 116, 303}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 116, -98}, {117, 117, 304}, {118, 122, 67}, },
		{{36, 117, -184}, {118, 118, 305}, {119, 122, 67}, },
		{{36, 113, -123}, {114, 114, 306}, {115, 122, 67}, },
		{{36, 116, -98}, {117, 117, 307}, {118, 122, 67}, },
		{{36, 90, -65}, {95, 95, 308}, {97, 122, 67}, },
		{{36, 95, -65}, {97, 97, 309}, {98, 122, 67}, },
		{{36, 100, -91}, {101, 101, 310}, {102, 122, 67}, },
		{{36, 104, -100}, {105, 105, 311}, {106, 122, 67}, },
		{{36, 113, -123}, {114, 114, 312}, {115, 122, 67}, },
		{{36, 115, -92}, {116, 116, 313}, {117, 122, 67}, },
		{{36, 104, -100}, {105, 105, 314}, {106, 122, 67}, },
		{{36, 104, -100}, {105, 105, 315}, {106, 122, 67}, },
		{{36, 103, -200}, {104, 104, 316}, {105, 122, 67}, },
		{{36, 100, -91}, {101, 101, 317}, {102, 122, 67}, },
		{{36, 95, -65}, {97, 118, 67}, {119, 119, 318}, {120, 122, 67}, },
		{{36, 114, -89}, {115, 115, 319}, {116, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 110, -90}, {111, 111, 320}, {112, 122, 67}, },
		{{36, 116, -98}, {117, 117, 321}, {118, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 115, -92}, {116, 116, 322}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{10, 47, -136}, {48, 57, 323}, {58, 64, 58}, {65, 70, 324}, {71, 96, -136}, {97, 102, 325}, {103, 126, 58}, },
		{{10, 126, -268}, },
		{{10, 126, -268}, },
		{{110, 110, 326}, },
		{},
		{},
		{{36, 36, 272}, {46, 46, 137}, {48, 122, -206}, },
		{{36, 122, -274}, },
		{{36, 122, -274}, },
		{{36, 122, -274}, },
		{{36, 122, -274}, },
		{{0, 65535, -210}, },
		{{0, 41, 208}, {42, 42, 278}, {43, 65535, -142}, },
		{{48, 57, 281}, },
		{{48, 57, 281}, },
		{{48, 57, 281}, {70, 70, 211}, {102, 102, 213}, },
		{{105, 105, 327}, },
		{{62, 62, 328}, },
		{{104, 104, 329}, },
		{{109, 109, 330}, },
		{{58, 58, 331}, },
		{{36, 95, -65}, {97, 97, 332}, {98, 122, 67}, },
		{{36, 95, -65}, {97, 97, 333}, {98, 122, 67}, },
		{{36, 111, -96}, {112, 112, 334}, {113, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 107, -101}, {108, 108, 335}, {109, 122, 67}, },
		{{36, 100, -91}, {101, 101, 336}, {102, 122, 67}, },
		{{36, 108, -171}, {109, 109, 337}, {110, 122, 67}, },
		{{36, 110, -90}, {111, 111, 338}, {112, 122, 67}, },
		{{36, 99, -198}, {100, 100, 339}, {101, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 108, -171}, {109, 109, 340}, {110, 122, 67}, },
		{{36, 109, -102}, {110, 110, 341}, {111, 122, 67}, },
		{{36, 101, -97}, {102, 102, 342}, {103, 122, 67}, },
		{{36, 103, -200}, {104, 104, 343}, {105, 122, 67}, },
		{{36, 111, -96}, {112, 112, 344}, {113, 122, 67}, },
		{{36, 100, -91}, {101, 101, 345}, {102, 122, 67}, },
		{{36, 113, -123}, {114, 114, 346}, {115, 122, 67}, },
		{{36, 107, -101}, {108, 108, 347}, {109, 122, 67}, },
		{{36, 115, -92}, {116, 116, 348}, {117, 122, 67}, },
		{{36, 115, -92}, {116, 116, 349}, {117, 122, 67}, },
		{{36, 98, -159}, {99, 99, 350}, {100, 122, 67}, },
		{{36, 98, -159}, {99, 99, 351}, {100, 122, 67}, },
		{{36, 109, -102}, {110, 110, 352}, {111, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 95, -65}, {97, 97, 353}, {98, 122, 67}, },
		{{36, 98, -159}, {99, 99, 354}, {100, 122, 67}, },
		{{36, 113, -123}, {114, 114, 355}, {115, 122, 67}, },
		{{36, 114, -89}, {115, 115, 356}, {116, 122, 67}, },
		{{36, 114, -89}, {115, 115, 357}, {116, 122, 67}, },
		{{36, 104, -100}, {105, 105, 358}, {106, 122, 67}, },
		{{36, 118, -260}, {119, 119, 359}, {120, 122, 67}, },
		{{36, 95, -65}, {97, 97, 360}, {98, 122, 67}, },
		{{36, 104, -100}, {105, 105, 361}, {106, 122, 67}, },
		{{10, 47, -136}, {48, 57, 362}, {58, 64, 58}, {65, 70, 363}, {71, 96, -136}, {97, 102, 364}, {103, 126, 58}, },
		{{10, 126, -325}, },
		{{10, 126, -325}, },
		{{105, 105, 365}, },
		{{116, 116, 366}, },
		{},
		{{116, 116, 367}, },
		{{101, 101, 368}, },
		{},
		{{36, 98, -159}, {99, 99, 369}, {100, 122, 67}, },
		{{36, 109, -102}, {110, 110, 370}, {111, 122, 67}, },
		{{36, 110, -90}, {111, 111, 371}, {112, 122, 67}, },
		{{36, 115, -92}, {116, 116, 372}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 110, -90}, {111, 111, 373}, {112, 122, 67}, },
		{{36, 109, -102}, {110, 110, 374}, {111, 122, 67}, },
		{{36, 114, -89}, {115, 115, 375}, {116, 122, 67}, },
		{{36, 100, -91}, {101, 101, 376}, {102, 122, 67}, },
		{{36, 98, -159}, {99, 99, 377}, {100, 122, 67}, },
		{{36, 95, -65}, {97, 97, 378}, {98, 122, 67}, },
		{{36, 110, -90}, {111, 111, 379}, {112, 122, 67}, },
		{{36, 114, -89}, {115, 115, 380}, {116, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 95, -65}, {97, 97, 381}, {98, 122, 67}, },
		{{36, 115, -92}, {116, 116, 382}, {117, 122, 67}, },
		{{36, 95, -65}, {97, 120, 67}, {121, 121, 383}, {122, 122, 67}, },
		{{36, 100, -91}, {101, 101, 384}, {102, 122, 67}, },
		{{36, 115, -92}, {116, 116, 385}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 107, -101}, {108, 108, 386}, {109, 122, 67}, },
		{{36, 104, -100}, {105, 105, 387}, {106, 122, 67}, },
		{{36, 110, -90}, {111, 111, 388}, {112, 122, 67}, },
		{{36, 118, -260}, {119, 119, 389}, {120, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 100, -91}, {101, 101, 390}, {102, 122, 67}, },
		{{36, 109, -102}, {110, 110, 391}, {111, 122, 67}, },
		{{36, 107, -101}, {108, 108, 392}, {109, 122, 67}, },
		{{36, 107, -101}, {108, 108, 393}, {109, 122, 67}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{116, 116, 394}, },
		{{62, 62, 395}, },
		{{101, 101, 396}, },
		{{116, 116, 397}, },
		{{36, 115, -92}, {116, 116, 398}, {117, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 104, -100}, {105, 105, 399}, {106, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 109, -102}, {110, 110, 400}, {111, 122, 67}, },
		{{36, 104, -100}, {105, 105, 401}, {106, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 109, -102}, {110, 110, 402}, {111, 122, 67}, },
		{{36, 100, -91}, {101, 101, 403}, {102, 122, 67}, },
		{{36, 98, -159}, {99, 99, 404}, {100, 122, 67}, },
		{{36, 101, -97}, {102, 102, 405}, {103, 122, 67}, },
		{{36, 118, -260}, {119, 119, 406}, {120, 122, 67}, },
		{{36, 120, -350}, {121, 121, 407}, {122, 122, 67}, },
		{{36, 104, -100}, {105, 105, 408}, {106, 122, 67}, },
		{{36, 111, -96}, {112, 112, 409}, {113, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 100, -91}, {101, 101, 410}, {102, 122, 67}, },
		{{36, 104, -100}, {105, 105, 411}, {106, 122, 67}, },
		{{36, 109, -102}, {110, 110, 412}, {111, 122, 67}, },
		{{36, 109, -102}, {110, 110, 413}, {111, 122, 67}, },
		{{36, 104, -100}, {105, 105, 414}, {106, 122, 67}, },
		{{36, 109, -102}, {110, 110, 415}, {111, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 104, -100}, {105, 105, 416}, {106, 122, 67}, },
		{{36, 100, -91}, {101, 101, 417}, {102, 122, 67}, },
		{{121, 121, 418}, },
		{},
		{{120, 120, 419}, },
		{{101, 101, 420}, },
		{{36, 122, -65}, },
		{{36, 109, -102}, {110, 110, 421}, {111, 122, 67}, },
		{{36, 104, -100}, {105, 105, 422}, {106, 122, 67}, },
		{{36, 115, -92}, {116, 116, 423}, {117, 122, 67}, },
		{{36, 115, -92}, {116, 116, 424}, {117, 122, 67}, },
		{{36, 110, -90}, {111, 111, 425}, {112, 122, 67}, },
		{{36, 100, -91}, {101, 101, 426}, {102, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 104, -100}, {105, 105, 427}, {106, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 95, -65}, {97, 97, 428}, {98, 122, 67}, },
		{{36, 100, -91}, {101, 101, 429}, {102, 122, 67}, },
		{{36, 99, -198}, {100, 100, 430}, {101, 122, 67}, },
		{{36, 109, -102}, {110, 110, 431}, {111, 122, 67}, },
		{{36, 117, -184}, {118, 118, 432}, {119, 122, 67}, },
		{{36, 104, -100}, {105, 105, 433}, {106, 122, 67}, },
		{{36, 115, -92}, {116, 116, 434}, {117, 122, 67}, },
		{{36, 115, -92}, {116, 116, 435}, {117, 122, 67}, },
		{{36, 109, -102}, {110, 110, 436}, {111, 122, 67}, },
		{{36, 122, -65}, },
		{{70, 102, -205}, },
		{{99, 99, 437}, },
		{{114, 114, 438}, },
		{{36, 115, -92}, {116, 116, 439}, {117, 122, 67}, },
		{{36, 115, -92}, {116, 116, 440}, {117, 122, 67}, },
		{{36, 110, -90}, {111, 111, 441}, {112, 122, 67}, },
		{{36, 114, -89}, {115, 115, 442}, {116, 122, 67}, },
		{{36, 101, -97}, {102, 102, 443}, {103, 122, 67}, },
		{{36, 104, -100}, {105, 105, 444}, {106, 122, 67}, },
		{{36, 115, -92}, {116, 116, 445}, {117, 122, 67}, },
		{{36, 113, -123}, {114, 114, 446}, {115, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 117, -184}, {118, 118, 447}, {119, 122, 67}, },
		{{36, 110, -90}, {111, 111, 448}, {112, 122, 67}, },
		{{36, 95, -65}, {97, 121, 67}, {122, 122, 449}, },
		{{36, 98, -159}, {99, 99, 450}, {100, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 117, -184}, {118, 118, 451}, {119, 122, 67}, },
		{{101, 101, 452}, },
		{{48, 57, 453}, },
		{{36, 122, -65}, },
		{{36, 110, -90}, {111, 111, 454}, {112, 122, 67}, },
		{{36, 113, -123}, {114, 114, 455}, {115, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 109, -102}, {110, 110, 456}, {111, 122, 67}, },
		{{36, 98, -159}, {99, 99, 457}, {100, 122, 67}, },
		{{36, 113, -123}, {114, 114, 458}, {115, 122, 67}, },
		{{36, 110, -90}, {111, 111, 459}, {112, 122, 67}, },
		{{36, 106, -126}, {107, 107, 460}, {108, 122, 67}, },
		{{36, 100, -91}, {101, 101, 461}, {102, 122, 67}, },
		{{36, 103, -200}, {104, 104, 462}, {105, 122, 67}, },
		{{36, 110, -90}, {111, 111, 463}, {112, 122, 67}, },
		{{112, 112, 464}, },
		{{48, 57, 453}, {58, 58, 465}, },
		{{36, 113, -123}, {114, 114, 466}, {115, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 117, -184}, {118, 118, 467}, {119, 122, 67}, },
		{{36, 103, -200}, {104, 104, 468}, {105, 122, 67}, },
		{{36, 95, -65}, {97, 97, 469}, {98, 122, 67}, },
		{{36, 106, -126}, {107, 107, 470}, {108, 122, 67}, },
		{{36, 100, -91}, {101, 101, 471}, {102, 122, 67}, },
		{{36, 99, -198}, {100, 100, 472}, {101, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 106, -126}, {107, 107, 473}, {108, 122, 67}, },
		{{116, 116, 474}, },
		{},
		{{36, 122, -65}, },
		{{36, 110, -90}, {111, 111, 475}, {112, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 120, -350}, {121, 121, 476}, {122, 122, 67}, },
		{{36, 100, -91}, {101, 101, 477}, {102, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 100, -91}, {101, 101, 478}, {102, 122, 67}, },
		{{105, 105, 479}, },
		{{36, 106, -126}, {107, 107, 480}, {108, 122, 67}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{36, 122, -65}, },
		{{111, 111, 481}, },
		{{36, 100, -91}, {101, 101, 482}, {102, 122, 67}, },
		{{110, 110, 483}, },
		{{36, 122, -65}, },
		{},
    };*/

    private static int[][] accept;
/*  {
		{-1, 95, 95, 95, 95, -1, -1, -1, 89, 73, 70, 67, 63, 64, 85, 83, 57, 84, 66, 86, 92, 92, 65, 60, 78, 69, 76, -1, 89, 61, 62, 72, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 58, 71, 59, 75, -1, -1, 94, -1, -1, -1, -1, -1, 89, 89, 89, 89, 89, -1, -1, 96, -1, 92, 92, -1, -1, 68, 80, 79, -1, -1, 74, 77, 81, -1, -1, -1, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 36, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 53, 89, 89, 89, 89, 89, -1, 94, -1, -1, -1, -1, -1, -1, -1, -1, 88, -1, -1, 96, 93, 92, 92, 92, -1, -1, 82, -1, -1, -1, 89, 89, 89, 89, 89, 89, 89, 89, 28, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 17, 89, 89, 89, 89, 41, 42, 45, 89, 89, 89, 89, 46, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, -1, -1, -1, -1, 93, -1, -1, -1, -1, -1, 96, -1, 93, -1, 93, -1, -1, -1, -1, -1, 89, 89, 89, 14, 26, 89, 16, 89, 29, 30, 89, 89, 89, 89, 89, 89, 89, 89, 34, 35, 89, 89, 89, 89, 18, 89, 89, 89, 89, 56, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 12, 89, 55, -1, -1, -1, -1, 93, 93, 87, 87, 87, 87, 87, -1, -1, -1, -1, 93, -1, -1, -1, -1, -1, 89, 89, 89, 27, 10, 89, 89, 89, 89, 89, 89, 1, 19, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 89, 15, 89, 89, 89, 89, 51, 89, 89, 89, 89, -1, -1, -1, -1, -1, 89, -1, -1, 90, 89, 89, 89, 89, 20, 89, 89, 89, 89, 89, 89, 89, 89, 2, 89, 89, 89, 89, 89, 3, 47, 89, 6, 89, 89, 52, 89, 89, 89, 89, -1, -1, -1, -1, -1, -1, -1, 89, 13, 89, 31, 89, 89, 23, 89, 89, 89, 89, 89, 89, 89, 89, 5, 89, 89, 89, 89, 89, 89, 22, 89, 89, -1, 89, -1, -1, 0, 89, 89, 89, 89, 89, 89, 39, 89, 43, 89, 89, 89, 89, 89, 89, 89, 89, 89, 9, 93, -1, -1, 89, 89, 89, 89, 89, 11, 89, 89, 21, 4, 89, 89, 89, 89, 8, 89, -1, -1, 25, 89, 89, 24, 37, 89, 89, 89, 89, 89, 89, 89, 89, -1, -1, 89, 33, 89, 89, 89, 89, 89, 89, 50, 89, -1, 90, 32, 89, 40, 89, 89, 49, 7, 89, -1, 89, 44, 48, 54, -1, 89, -1, 38, 90, },

    };*/

    public static class State
    {
        public final static State INITIAL = new State(0);

        private int id;

        private State(int id)
        {
            this.id = id;
        }

        public int id()
        {
            return id;
        }
    }
}
