package soot.jimple.parser.lexer;

import java.io.*;
import ca.mcgill.sable.util.*;
import java.util.*;
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
                                getText(accept_length),
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
                                getText(accept_length),
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
    Token new21(int line, int pos) { return new TUnknown(line, pos); }
    Token new22(int line, int pos) { return new TExtends(line, pos); }
    Token new23(int line, int pos) { return new TImplements(line, pos); }
    Token new24(int line, int pos) { return new TBreakpoint(line, pos); }
    Token new25(int line, int pos) { return new TCase(line, pos); }
    Token new26(int line, int pos) { return new TCatch(line, pos); }
    Token new27(int line, int pos) { return new TCmp(line, pos); }
    Token new28(int line, int pos) { return new TCmpg(line, pos); }
    Token new29(int line, int pos) { return new TCmpl(line, pos); }
    Token new30(int line, int pos) { return new TDefault(line, pos); }
    Token new31(int line, int pos) { return new TEntermonitor(line, pos); }
    Token new32(int line, int pos) { return new TExitmonitor(line, pos); }
    Token new33(int line, int pos) { return new TFrom(line, pos); }
    Token new34(int line, int pos) { return new TGoto(line, pos); }
    Token new35(int line, int pos) { return new TIf(line, pos); }
    Token new36(int line, int pos) { return new TInstanceof(line, pos); }
    Token new37(int line, int pos) { return new TInterfaceinvoke(line, pos); }
    Token new38(int line, int pos) { return new TLengthof(line, pos); }
    Token new39(int line, int pos) { return new TLookupswitch(line, pos); }
    Token new40(int line, int pos) { return new TNeg(line, pos); }
    Token new41(int line, int pos) { return new TNew(line, pos); }
    Token new42(int line, int pos) { return new TNewarray(line, pos); }
    Token new43(int line, int pos) { return new TNewmultiarray(line, pos); }
    Token new44(int line, int pos) { return new TNop(line, pos); }
    Token new45(int line, int pos) { return new TRet(line, pos); }
    Token new46(int line, int pos) { return new TReturn(line, pos); }
    Token new47(int line, int pos) { return new TSpecialinvoke(line, pos); }
    Token new48(int line, int pos) { return new TStaticinvoke(line, pos); }
    Token new49(int line, int pos) { return new TTableswitch(line, pos); }
    Token new50(int line, int pos) { return new TThrow(line, pos); }
    Token new51(int line, int pos) { return new TThrows(line, pos); }
    Token new52(int line, int pos) { return new TTo(line, pos); }
    Token new53(int line, int pos) { return new TVirtualinvoke(line, pos); }
    Token new54(int line, int pos) { return new TWith(line, pos); }
    Token new55(int line, int pos) { return new TComma(line, pos); }
    Token new56(int line, int pos) { return new TLBrace(line, pos); }
    Token new57(int line, int pos) { return new TRBrace(line, pos); }
    Token new58(int line, int pos) { return new TSemicolon(line, pos); }
    Token new59(int line, int pos) { return new TLBracket(line, pos); }
    Token new60(int line, int pos) { return new TRBracket(line, pos); }
    Token new61(int line, int pos) { return new TLParen(line, pos); }
    Token new62(int line, int pos) { return new TRParen(line, pos); }
    Token new63(int line, int pos) { return new TColon(line, pos); }
    Token new64(int line, int pos) { return new TDot(line, pos); }
    Token new65(int line, int pos) { return new TQuote(line, pos); }
    Token new66(int line, int pos) { return new TColonEquals(line, pos); }
    Token new67(int line, int pos) { return new TEquals(line, pos); }
    Token new68(int line, int pos) { return new TAnd(line, pos); }
    Token new69(int line, int pos) { return new TOr(line, pos); }
    Token new70(int line, int pos) { return new TXor(line, pos); }
    Token new71(int line, int pos) { return new TMod(line, pos); }
    Token new72(int line, int pos) { return new TCmpeq(line, pos); }
    Token new73(int line, int pos) { return new TCmpne(line, pos); }
    Token new74(int line, int pos) { return new TCmpgt(line, pos); }
    Token new75(int line, int pos) { return new TCmpge(line, pos); }
    Token new76(int line, int pos) { return new TCmplt(line, pos); }
    Token new77(int line, int pos) { return new TCmple(line, pos); }
    Token new78(int line, int pos) { return new TShl(line, pos); }
    Token new79(int line, int pos) { return new TShr(line, pos); }
    Token new80(int line, int pos) { return new TUshr(line, pos); }
    Token new81(int line, int pos) { return new TPlus(line, pos); }
    Token new82(int line, int pos) { return new TMinus(line, pos); }
    Token new83(int line, int pos) { return new TMult(line, pos); }
    Token new84(int line, int pos) { return new TDiv(line, pos); }
    Token new85(String text, int line, int pos) { return new TFloatDegenerate(text, line, pos); }
    Token new86(String text, int line, int pos) { return new TQuotedName(text, line, pos); }
    Token new87(String text, int line, int pos) { return new TIdentifier(text, line, pos); }
    Token new88(String text, int line, int pos) { return new TFullIdentifier(text, line, pos); }
    Token new89(String text, int line, int pos) { return new TAtIdentifier(text, line, pos); }
    Token new90(String text, int line, int pos) { return new TBoolConstant(text, line, pos); }
    Token new91(String text, int line, int pos) { return new TIntegerConstant(text, line, pos); }
    Token new92(String text, int line, int pos) { return new TFloatConstant(text, line, pos); }
    Token new93(String text, int line, int pos) { return new TStringConstant(text, line, pos); }
    Token new94(String text, int line, int pos) { return new TBlank(text, line, pos); }
    Token new95(String text, int line, int pos) { return new TComment(text, line, pos); }

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
		{{36, 36, 63}, {46, 46, 64}, {48, 57, 65}, {65, 90, 66}, {95, 95, 67}, {97, 122, 68}, },
		{},
		{},
		{{0, 9, 69}, {11, 12, 69}, {14, 38, 69}, {40, 65535, 69}, },
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{{42, 42, 70}, {47, 47, 71}, },
		{{46, 46, 72}, {48, 55, 73}, {56, 57, 21}, {76, 76, 74}, {88, 88, 75}, {120, 120, 76}, },
		{{46, 46, 72}, {48, 57, 21}, {76, 76, 74}, },
		{{61, 61, 77}, },
		{},
		{{60, 60, 78}, {61, 61, 79}, {99, 99, 80}, {105, 105, 81}, },
		{{61, 61, 82}, },
		{{61, 61, 83}, {62, 62, 84}, },
		{{99, 99, 85}, {112, 112, 86}, {116, 116, 87}, },
		{{36, 122, -10}, },
		{},
		{},
		{},
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 68}, {98, 98, 88}, {99, 122, 68}, },
		{{36, 95, -10}, {97, 110, 68}, {111, 111, 89}, {112, 113, 68}, {114, 114, 90}, {115, 120, 68}, {121, 121, 91}, {122, 122, 68}, },
		{{36, 95, -10}, {97, 97, 92}, {98, 103, 68}, {104, 104, 93}, {105, 107, 68}, {108, 108, 94}, {109, 109, 95}, {110, 122, 68}, },
		{{36, 95, -10}, {97, 100, 68}, {101, 101, 96}, {102, 110, 68}, {111, 111, 97}, {112, 122, 68}, },
		{{36, 95, -10}, {97, 109, 68}, {110, 110, 98}, {111, 119, 68}, {120, 120, 99}, {121, 122, 68}, },
		{{36, 95, -10}, {97, 97, 100}, {98, 104, 68}, {105, 105, 101}, {106, 107, 68}, {108, 108, 102}, {109, 113, 68}, {114, 114, 103}, {115, 122, 68}, },
		{{36, 110, -36}, {111, 111, 104}, {112, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 101, 68}, {102, 102, 105}, {103, 108, 68}, {109, 109, 106}, {110, 110, 107}, {111, 122, 68}, },
		{{36, 100, -38}, {101, 101, 108}, {102, 110, 68}, {111, 111, 109}, {112, 122, 68}, },
		{{36, 95, -10}, {97, 97, 110}, {98, 100, 68}, {101, 101, 111}, {102, 110, 68}, {111, 111, 112}, {112, 122, 68}, },
		{{36, 95, -10}, {97, 113, 68}, {114, 114, 113}, {115, 116, 68}, {117, 117, 114}, {118, 122, 68}, },
		{{36, 100, -38}, {101, 101, 115}, {102, 122, 68}, },
		{{36, 95, -10}, {97, 103, 68}, {104, 104, 116}, {105, 111, 68}, {112, 112, 117}, {113, 115, 68}, {116, 116, 118}, {117, 120, 68}, {121, 121, 119}, {122, 122, 68}, },
		{{36, 95, -10}, {97, 97, 120}, {98, 103, 68}, {104, 104, 121}, {105, 110, 68}, {111, 111, 122}, {112, 113, 68}, {114, 114, 123}, {115, 122, 68}, },
		{{36, 109, -39}, {110, 110, 124}, {111, 122, 68}, },
		{{36, 95, -10}, {97, 104, 68}, {105, 105, 125}, {106, 110, 68}, {111, 111, 126}, {112, 122, 68}, },
		{{36, 104, -51}, {105, 105, 127}, {106, 122, 68}, },
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
		{{36, 122, -10}, },
		{{36, 36, 137}, {48, 57, 138}, {65, 90, 139}, {95, 95, 140}, {97, 122, 141}, },
		{{36, 36, 142}, {48, 57, 65}, {65, 90, 143}, {95, 95, 144}, {97, 122, 145}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{0, 38, -13}, {39, 39, 146}, {40, 65535, 69}, },
		{{0, 41, 147}, {42, 42, 148}, {43, 65535, 147}, },
		{{0, 9, 149}, {11, 12, 149}, {14, 65535, 149}, },
		{{48, 57, 150}, },
		{{46, 76, -22}, },
		{},
		{{48, 57, 151}, {65, 70, 152}, {97, 102, 153}, },
		{{48, 102, -77}, },
		{},
		{},
		{},
		{{108, 108, 154}, },
		{{110, 110, 155}, },
		{},
		{},
		{{62, 62, 156}, },
		{{97, 97, 157}, },
		{{97, 97, 158}, },
		{{104, 104, 159}, },
		{{36, 95, -10}, {97, 114, 68}, {115, 115, 160}, {116, 122, 68}, },
		{{36, 110, -36}, {111, 111, 161}, {112, 122, 68}, },
		{{36, 100, -38}, {101, 101, 162}, {102, 122, 68}, },
		{{36, 95, -10}, {97, 115, 68}, {116, 116, 163}, {117, 122, 68}, },
		{{36, 114, -90}, {115, 115, 164}, {116, 116, 165}, {117, 122, 68}, },
		{{36, 95, -10}, {97, 97, 166}, {98, 122, 68}, },
		{{36, 95, -10}, {97, 97, 167}, {98, 122, 68}, },
		{{36, 95, -10}, {97, 111, 68}, {112, 112, 168}, {113, 122, 68}, },
		{{36, 101, -43}, {102, 102, 169}, {103, 122, 68}, },
		{{36, 95, -10}, {97, 116, 68}, {117, 117, 170}, {118, 122, 68}, },
		{{36, 115, -93}, {116, 116, 171}, {117, 122, 68}, },
		{{36, 104, -51}, {105, 105, 172}, {106, 115, 68}, {116, 116, 173}, {117, 122, 68}, },
		{{36, 95, -10}, {97, 107, 68}, {108, 108, 174}, {109, 122, 68}, },
		{{36, 109, -39}, {110, 110, 175}, {111, 122, 68}, },
		{{36, 110, -36}, {111, 111, 176}, {112, 122, 68}, },
		{{36, 110, -36}, {111, 111, 177}, {112, 122, 68}, },
		{{36, 115, -93}, {116, 116, 178}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 111, -97}, {112, 112, 179}, {113, 122, 68}, },
		{{36, 114, -90}, {115, 115, 180}, {116, 116, 181}, {117, 122, 68}, },
		{{36, 109, -39}, {110, 110, 182}, {111, 122, 68}, },
		{{36, 109, -39}, {110, 110, 183}, {111, 111, 184}, {112, 122, 68}, },
		{{36, 115, -93}, {116, 116, 185}, {117, 122, 68}, },
		{{36, 95, -10}, {97, 102, 68}, {103, 103, 186}, {104, 118, 68}, {119, 119, 187}, {120, 122, 68}, },
		{{36, 111, -97}, {112, 112, 188}, {113, 122, 68}, },
		{{36, 104, -51}, {105, 105, 189}, {106, 110, 68}, {111, 111, 190}, {112, 122, 68}, },
		{{36, 97, -35}, {98, 98, 191}, {99, 122, 68}, },
		{{36, 115, -93}, {116, 116, 192}, {117, 122, 68}, },
		{{36, 110, -36}, {111, 111, 193}, {112, 122, 68}, },
		{{36, 100, -38}, {101, 101, 194}, {102, 122, 68}, },
		{{36, 95, -10}, {97, 97, 195}, {98, 122, 68}, },
		{{36, 109, -39}, {110, 110, 196}, {111, 122, 68}, },
		{{36, 97, -35}, {98, 98, 197}, {99, 122, 68}, },
		{{36, 113, -46}, {114, 114, 198}, {115, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 199}, {98, 116, 68}, {117, 117, 200}, {118, 122, 68}, },
		{{36, 95, -10}, {97, 106, 68}, {107, 107, 201}, {108, 122, 68}, },
		{{36, 113, -46}, {114, 114, 202}, {115, 122, 68}, },
		{{36, 104, -51}, {105, 105, 203}, {106, 107, 68}, {108, 108, 204}, {109, 122, 68}, },
		{{36, 115, -93}, {116, 116, 205}, {117, 122, 68}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -61}, },
		{{10, 34, -8}, {35, 47, 58}, {48, 57, 206}, {58, 64, 58}, {65, 70, 207}, {71, 91, 58}, {92, 92, 59}, {93, 96, 58}, {97, 102, 208}, {103, 126, 58}, },
		{{102, 102, 209}, },
		{{78, 78, 210}, },
		{{36, 36, 137}, {46, 46, 64}, {48, 122, -66}, },
		{{36, 36, 211}, {48, 57, 138}, {65, 90, 212}, {95, 95, 213}, {97, 122, 214}, },
		{{36, 122, -139}, },
		{{36, 122, -139}, },
		{{36, 122, -139}, },
		{{36, 122, -67}, },
		{{36, 122, -67}, },
		{{36, 122, -67}, },
		{{36, 122, -67}, },
		{},
		{{0, 65535, -72}, },
		{{0, 41, 215}, {42, 42, 148}, {43, 46, 215}, {47, 47, 216}, {48, 65535, 215}, },
		{{0, 65535, -73}, },
		{{48, 57, 150}, {69, 69, 217}, {70, 70, 218}, {101, 101, 219}, {102, 102, 220}, },
		{{48, 70, -77}, {76, 76, 74}, {97, 102, 153}, },
		{{48, 102, -153}, },
		{{48, 102, -153}, },
		{{105, 105, 221}, },
		{{105, 105, 222}, },
		{},
		{{117, 117, 223}, },
		{{114, 114, 224}, },
		{{105, 105, 225}, },
		{{36, 115, -93}, {116, 116, 226}, {117, 122, 68}, },
		{{36, 107, -102}, {108, 108, 227}, {109, 122, 68}, },
		{{36, 95, -10}, {97, 97, 228}, {98, 122, 68}, },
		{{36, 100, -38}, {101, 101, 229}, {102, 122, 68}, },
		{{36, 100, -38}, {101, 101, 230}, {102, 122, 68}, },
		{{36, 95, -10}, {97, 98, 68}, {99, 99, 231}, {100, 122, 68}, },
		{{36, 113, -46}, {114, 114, 232}, {115, 122, 68}, },
		{{36, 114, -90}, {115, 115, 233}, {116, 122, 68}, },
		{{36, 102, -113}, {103, 103, 234}, {104, 107, 68}, {108, 108, 235}, {109, 122, 68}, },
		{{36, 95, -10}, {97, 97, 236}, {98, 122, 68}, },
		{{36, 97, -35}, {98, 98, 237}, {99, 122, 68}, },
		{{36, 100, -38}, {101, 101, 238}, {102, 122, 68}, },
		{{36, 115, -93}, {116, 116, 239}, {117, 122, 68}, },
		{{36, 100, -38}, {101, 101, 240}, {102, 122, 68}, },
		{{36, 114, -90}, {115, 115, 241}, {116, 122, 68}, },
		{{36, 95, -10}, {97, 97, 242}, {98, 122, 68}, },
		{{36, 95, -10}, {97, 97, 243}, {98, 122, 68}, },
		{{36, 95, -10}, {97, 108, 68}, {109, 109, 244}, {110, 122, 68}, },
		{{36, 110, -36}, {111, 111, 245}, {112, 122, 68}, },
		{{36, 107, -102}, {108, 108, 246}, {109, 122, 68}, },
		{{36, 115, -93}, {116, 116, 247}, {117, 122, 68}, },
		{{36, 100, -38}, {101, 101, 248}, {102, 122, 68}, },
		{{36, 102, -113}, {103, 103, 249}, {104, 122, 68}, },
		{{36, 102, -113}, {103, 103, 250}, {104, 122, 68}, },
		{{36, 106, -126}, {107, 107, 251}, {108, 122, 68}, },
		{{36, 104, -51}, {105, 105, 252}, {106, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 253}, {98, 108, 68}, {109, 109, 254}, {110, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 117, 68}, {118, 118, 255}, {119, 122, 68}, },
		{{36, 115, -93}, {116, 116, 256}, {117, 122, 68}, },
		{{36, 107, -102}, {108, 108, 257}, {109, 122, 68}, },
		{{36, 116, -99}, {117, 117, 258}, {118, 122, 68}, },
		{{36, 113, -46}, {114, 114, 259}, {115, 122, 68}, },
		{{36, 98, -167}, {99, 99, 260}, {100, 122, 68}, },
		{{36, 115, -93}, {116, 116, 261}, {117, 122, 68}, },
		{{36, 98, -167}, {99, 99, 262}, {100, 122, 68}, },
		{{36, 107, -102}, {108, 108, 263}, {109, 122, 68}, },
		{{36, 110, -36}, {111, 111, 264}, {112, 122, 68}, },
		{{36, 109, -39}, {110, 110, 265}, {111, 122, 68}, },
		{{36, 100, -38}, {101, 101, 266}, {102, 122, 68}, },
		{{36, 109, -39}, {110, 110, 267}, {111, 122, 68}, },
		{{36, 115, -93}, {116, 116, 268}, {117, 122, 68}, },
		{{36, 95, -10}, {97, 99, 68}, {100, 100, 269}, {101, 122, 68}, },
		{{36, 95, -10}, {97, 97, 270}, {98, 122, 68}, },
		{{36, 103, -48}, {104, 104, 271}, {105, 122, 68}, },
		{{10, 47, -136}, {48, 57, 272}, {58, 64, 58}, {65, 70, 273}, {71, 96, -136}, {97, 102, 274}, {103, 126, 58}, },
		{{10, 126, -208}, },
		{{10, 126, -208}, },
		{{105, 105, 275}, },
		{{70, 70, 276}, {102, 102, 277}, },
		{{36, 122, -140}, },
		{{36, 122, -140}, },
		{{36, 122, -140}, },
		{{36, 122, -140}, },
		{{0, 41, 278}, {42, 42, 279}, {43, 65535, 278}, },
		{},
		{{43, 43, 280}, {45, 45, 281}, {48, 57, 282}, },
		{},
		{{43, 57, -219}, },
		{},
		{{110, 110, 283}, },
		{{116, 116, 284}, },
		{{103, 103, 285}, },
		{{97, 97, 286}, },
		{{115, 115, 287}, },
		{{36, 113, -46}, {114, 114, 288}, {115, 122, 68}, },
		{{36, 100, -38}, {101, 101, 289}, {102, 122, 68}, },
		{{36, 106, -126}, {107, 107, 290}, {108, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 103, -48}, {104, 104, 291}, {105, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 114, -90}, {115, 115, 292}, {116, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 116, -99}, {117, 117, 293}, {118, 122, 68}, },
		{{36, 107, -102}, {108, 108, 294}, {109, 122, 68}, },
		{{36, 113, -46}, {114, 114, 295}, {115, 122, 68}, },
		{{36, 108, -179}, {109, 109, 296}, {110, 122, 68}, },
		{{36, 109, -39}, {110, 110, 297}, {111, 122, 68}, },
		{{36, 100, -38}, {101, 101, 298}, {102, 122, 68}, },
		{{36, 107, -102}, {108, 108, 299}, {109, 122, 68}, },
		{{36, 115, -93}, {116, 116, 300}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 100, -38}, {101, 101, 301}, {102, 122, 68}, },
		{{36, 95, -10}, {97, 97, 302}, {98, 122, 68}, },
		{{36, 113, -46}, {114, 114, 303}, {115, 122, 68}, },
		{{36, 115, -93}, {116, 116, 304}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 116, -99}, {117, 117, 305}, {118, 122, 68}, },
		{{36, 117, -191}, {118, 118, 306}, {119, 122, 68}, },
		{{36, 113, -46}, {114, 114, 307}, {115, 122, 68}, },
		{{36, 116, -99}, {117, 117, 308}, {118, 122, 68}, },
		{{36, 95, -10}, {97, 97, 309}, {98, 122, 68}, },
		{{36, 100, -38}, {101, 101, 310}, {102, 122, 68}, },
		{{36, 104, -51}, {105, 105, 311}, {106, 122, 68}, },
		{{36, 113, -46}, {114, 114, 312}, {115, 122, 68}, },
		{{36, 115, -93}, {116, 116, 313}, {117, 122, 68}, },
		{{36, 104, -51}, {105, 105, 314}, {106, 122, 68}, },
		{{36, 104, -51}, {105, 105, 315}, {106, 122, 68}, },
		{{36, 103, -48}, {104, 104, 316}, {105, 122, 68}, },
		{{36, 100, -38}, {101, 101, 317}, {102, 122, 68}, },
		{{36, 95, -10}, {97, 118, 68}, {119, 119, 318}, {120, 122, 68}, },
		{{36, 114, -90}, {115, 115, 319}, {116, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 110, -36}, {111, 111, 320}, {112, 122, 68}, },
		{{36, 116, -99}, {117, 117, 321}, {118, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 115, -93}, {116, 116, 322}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{10, 47, -136}, {48, 57, 323}, {58, 64, 58}, {65, 70, 324}, {71, 96, -136}, {97, 102, 325}, {103, 126, 58}, },
		{{10, 126, -274}, },
		{{10, 126, -274}, },
		{{110, 110, 326}, },
		{},
		{},
		{{0, 65535, -217}, },
		{{0, 41, 215}, {42, 42, 279}, {43, 65535, -150}, },
		{{48, 57, 282}, },
		{{48, 57, 282}, },
		{{48, 57, 282}, {70, 70, 218}, {102, 102, 220}, },
		{{105, 105, 327}, },
		{{62, 62, 328}, },
		{{104, 104, 329}, },
		{{109, 109, 330}, },
		{{58, 58, 331}, },
		{{36, 95, -10}, {97, 97, 332}, {98, 122, 68}, },
		{{36, 95, -10}, {97, 97, 333}, {98, 122, 68}, },
		{{36, 111, -97}, {112, 112, 334}, {113, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 107, -102}, {108, 108, 335}, {109, 122, 68}, },
		{{36, 100, -38}, {101, 101, 336}, {102, 122, 68}, },
		{{36, 108, -179}, {109, 109, 337}, {110, 122, 68}, },
		{{36, 110, -36}, {111, 111, 338}, {112, 122, 68}, },
		{{36, 99, -205}, {100, 100, 339}, {101, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 108, -179}, {109, 109, 340}, {110, 122, 68}, },
		{{36, 109, -39}, {110, 110, 341}, {111, 122, 68}, },
		{{36, 101, -43}, {102, 102, 342}, {103, 122, 68}, },
		{{36, 103, -48}, {104, 104, 343}, {105, 122, 68}, },
		{{36, 111, -97}, {112, 112, 344}, {113, 122, 68}, },
		{{36, 100, -38}, {101, 101, 345}, {102, 122, 68}, },
		{{36, 113, -46}, {114, 114, 346}, {115, 122, 68}, },
		{{36, 107, -102}, {108, 108, 347}, {109, 122, 68}, },
		{{36, 115, -93}, {116, 116, 348}, {117, 122, 68}, },
		{{36, 98, -167}, {99, 99, 349}, {100, 122, 68}, },
		{{36, 98, -167}, {99, 99, 350}, {100, 122, 68}, },
		{{36, 109, -39}, {110, 110, 351}, {111, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 352}, {98, 122, 68}, },
		{{36, 98, -167}, {99, 99, 353}, {100, 122, 68}, },
		{{36, 113, -46}, {114, 114, 354}, {115, 122, 68}, },
		{{36, 114, -90}, {115, 115, 355}, {116, 122, 68}, },
		{{36, 114, -90}, {115, 115, 356}, {116, 122, 68}, },
		{{36, 104, -51}, {105, 105, 357}, {106, 122, 68}, },
		{{36, 118, -266}, {119, 119, 358}, {120, 122, 68}, },
		{{36, 95, -10}, {97, 97, 359}, {98, 122, 68}, },
		{{36, 104, -51}, {105, 105, 360}, {106, 122, 68}, },
		{{10, 47, -136}, {48, 57, 361}, {58, 64, 58}, {65, 70, 362}, {71, 96, -136}, {97, 102, 363}, {103, 126, 58}, },
		{{10, 126, -325}, },
		{{10, 126, -325}, },
		{{105, 105, 364}, },
		{{116, 116, 365}, },
		{},
		{{116, 116, 366}, },
		{{101, 101, 367}, },
		{},
		{{36, 98, -167}, {99, 99, 368}, {100, 122, 68}, },
		{{36, 109, -39}, {110, 110, 369}, {111, 122, 68}, },
		{{36, 110, -36}, {111, 111, 370}, {112, 122, 68}, },
		{{36, 115, -93}, {116, 116, 371}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 110, -36}, {111, 111, 372}, {112, 122, 68}, },
		{{36, 109, -39}, {110, 110, 373}, {111, 122, 68}, },
		{{36, 114, -90}, {115, 115, 374}, {116, 122, 68}, },
		{{36, 100, -38}, {101, 101, 375}, {102, 122, 68}, },
		{{36, 98, -167}, {99, 99, 376}, {100, 122, 68}, },
		{{36, 95, -10}, {97, 97, 377}, {98, 122, 68}, },
		{{36, 110, -36}, {111, 111, 378}, {112, 122, 68}, },
		{{36, 114, -90}, {115, 115, 379}, {116, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 380}, {98, 122, 68}, },
		{{36, 115, -93}, {116, 116, 381}, {117, 122, 68}, },
		{{36, 100, -38}, {101, 101, 382}, {102, 122, 68}, },
		{{36, 115, -93}, {116, 116, 383}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 107, -102}, {108, 108, 384}, {109, 122, 68}, },
		{{36, 104, -51}, {105, 105, 385}, {106, 122, 68}, },
		{{36, 110, -36}, {111, 111, 386}, {112, 122, 68}, },
		{{36, 118, -266}, {119, 119, 387}, {120, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 100, -38}, {101, 101, 388}, {102, 122, 68}, },
		{{36, 109, -39}, {110, 110, 389}, {111, 122, 68}, },
		{{36, 107, -102}, {108, 108, 390}, {109, 122, 68}, },
		{{36, 107, -102}, {108, 108, 391}, {109, 122, 68}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{10, 126, -8}, },
		{{116, 116, 392}, },
		{{62, 62, 393}, },
		{{101, 101, 394}, },
		{{116, 116, 395}, },
		{{36, 115, -93}, {116, 116, 396}, {117, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 104, -51}, {105, 105, 397}, {106, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 109, -39}, {110, 110, 398}, {111, 122, 68}, },
		{{36, 104, -51}, {105, 105, 399}, {106, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 109, -39}, {110, 110, 400}, {111, 122, 68}, },
		{{36, 100, -38}, {101, 101, 401}, {102, 122, 68}, },
		{{36, 98, -167}, {99, 99, 402}, {100, 122, 68}, },
		{{36, 101, -43}, {102, 102, 403}, {103, 122, 68}, },
		{{36, 118, -266}, {119, 119, 404}, {120, 122, 68}, },
		{{36, 95, -10}, {97, 120, 68}, {121, 121, 405}, {122, 122, 68}, },
		{{36, 104, -51}, {105, 105, 406}, {106, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 100, -38}, {101, 101, 407}, {102, 122, 68}, },
		{{36, 104, -51}, {105, 105, 408}, {106, 122, 68}, },
		{{36, 109, -39}, {110, 110, 409}, {111, 122, 68}, },
		{{36, 109, -39}, {110, 110, 410}, {111, 122, 68}, },
		{{36, 104, -51}, {105, 105, 411}, {106, 122, 68}, },
		{{36, 109, -39}, {110, 110, 412}, {111, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 104, -51}, {105, 105, 413}, {106, 122, 68}, },
		{{36, 100, -38}, {101, 101, 414}, {102, 122, 68}, },
		{{121, 121, 415}, },
		{},
		{{120, 120, 416}, },
		{{101, 101, 417}, },
		{{36, 122, -10}, },
		{{36, 109, -39}, {110, 110, 418}, {111, 122, 68}, },
		{{36, 104, -51}, {105, 105, 419}, {106, 122, 68}, },
		{{36, 115, -93}, {116, 116, 420}, {117, 122, 68}, },
		{{36, 115, -93}, {116, 116, 421}, {117, 122, 68}, },
		{{36, 110, -36}, {111, 111, 422}, {112, 122, 68}, },
		{{36, 100, -38}, {101, 101, 423}, {102, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 104, -51}, {105, 105, 424}, {106, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 95, -10}, {97, 97, 425}, {98, 122, 68}, },
		{{36, 99, -205}, {100, 100, 426}, {101, 122, 68}, },
		{{36, 109, -39}, {110, 110, 427}, {111, 122, 68}, },
		{{36, 117, -191}, {118, 118, 428}, {119, 122, 68}, },
		{{36, 104, -51}, {105, 105, 429}, {106, 122, 68}, },
		{{36, 115, -93}, {116, 116, 430}, {117, 122, 68}, },
		{{36, 115, -93}, {116, 116, 431}, {117, 122, 68}, },
		{{36, 109, -39}, {110, 110, 432}, {111, 122, 68}, },
		{{36, 122, -10}, },
		{{70, 102, -212}, },
		{{99, 99, 433}, },
		{{114, 114, 434}, },
		{{36, 115, -93}, {116, 116, 435}, {117, 122, 68}, },
		{{36, 115, -93}, {116, 116, 436}, {117, 122, 68}, },
		{{36, 110, -36}, {111, 111, 437}, {112, 122, 68}, },
		{{36, 114, -90}, {115, 115, 438}, {116, 122, 68}, },
		{{36, 101, -43}, {102, 102, 439}, {103, 122, 68}, },
		{{36, 104, -51}, {105, 105, 440}, {106, 122, 68}, },
		{{36, 115, -93}, {116, 116, 441}, {117, 122, 68}, },
		{{36, 113, -46}, {114, 114, 442}, {115, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 117, -191}, {118, 118, 443}, {119, 122, 68}, },
		{{36, 110, -36}, {111, 111, 444}, {112, 122, 68}, },
		{{36, 95, -10}, {97, 121, 68}, {122, 122, 445}, },
		{{36, 98, -167}, {99, 99, 446}, {100, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 117, -191}, {118, 118, 447}, {119, 122, 68}, },
		{{101, 101, 448}, },
		{{48, 57, 449}, },
		{{36, 122, -10}, },
		{{36, 110, -36}, {111, 111, 450}, {112, 122, 68}, },
		{{36, 113, -46}, {114, 114, 451}, {115, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 109, -39}, {110, 110, 452}, {111, 122, 68}, },
		{{36, 98, -167}, {99, 99, 453}, {100, 122, 68}, },
		{{36, 113, -46}, {114, 114, 454}, {115, 122, 68}, },
		{{36, 110, -36}, {111, 111, 455}, {112, 122, 68}, },
		{{36, 106, -126}, {107, 107, 456}, {108, 122, 68}, },
		{{36, 100, -38}, {101, 101, 457}, {102, 122, 68}, },
		{{36, 103, -48}, {104, 104, 458}, {105, 122, 68}, },
		{{36, 110, -36}, {111, 111, 459}, {112, 122, 68}, },
		{{112, 112, 460}, },
		{{48, 57, 449}, {58, 58, 461}, },
		{{36, 113, -46}, {114, 114, 462}, {115, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 117, -191}, {118, 118, 463}, {119, 122, 68}, },
		{{36, 103, -48}, {104, 104, 464}, {105, 122, 68}, },
		{{36, 95, -10}, {97, 97, 465}, {98, 122, 68}, },
		{{36, 106, -126}, {107, 107, 466}, {108, 122, 68}, },
		{{36, 100, -38}, {101, 101, 467}, {102, 122, 68}, },
		{{36, 99, -205}, {100, 100, 468}, {101, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 106, -126}, {107, 107, 469}, {108, 122, 68}, },
		{{116, 116, 470}, },
		{},
		{{36, 122, -10}, },
		{{36, 110, -36}, {111, 111, 471}, {112, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 120, -382}, {121, 121, 472}, {122, 122, 68}, },
		{{36, 100, -38}, {101, 101, 473}, {102, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 100, -38}, {101, 101, 474}, {102, 122, 68}, },
		{{105, 105, 475}, },
		{{36, 106, -126}, {107, 107, 476}, {108, 122, 68}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{36, 122, -10}, },
		{{111, 111, 477}, },
		{{36, 100, -38}, {101, 101, 478}, {102, 122, 68}, },
		{{110, 110, 479}, },
		{{36, 122, -10}, },
		{},
    };*/

    private static int[][] accept;
/*  {
		{-1, 94, 94, 94, 94, -1, -1, -1, 87, 71, 68, 65, 61, 62, 83, 81, 55, 82, 64, 84, 91, 91, 63, 58, 76, 67, 74, -1, 87, 59, 60, 70, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 56, 69, 57, 73, -1, -1, 93, -1, -1, -1, -1, -1, 87, -1, 87, 87, 87, 87, -1, -1, 95, -1, 91, 91, -1, -1, 66, 78, 77, -1, -1, 72, 75, 79, -1, -1, -1, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 35, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 52, 87, 87, 87, 87, 87, -1, 93, -1, -1, -1, -1, -1, -1, -1, 88, 88, 88, 88, 88, 87, 87, 87, 87, 86, -1, -1, 95, 92, 91, 91, 91, -1, -1, 80, -1, -1, -1, 87, 87, 87, 87, 87, 87, 87, 87, 27, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 17, 87, 87, 87, 87, 40, 41, 44, 87, 87, 87, 45, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, -1, -1, -1, -1, 85, 88, 88, 88, 88, -1, 95, -1, 92, -1, 92, -1, -1, -1, -1, -1, 87, 87, 87, 14, 25, 87, 16, 87, 28, 29, 87, 87, 87, 87, 87, 87, 87, 87, 33, 34, 87, 87, 87, 87, 18, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 12, 87, 54, -1, -1, -1, -1, 85, 85, -1, -1, -1, -1, 92, -1, -1, -1, -1, -1, 87, 87, 87, 26, 10, 87, 87, 87, 87, 87, 87, 1, 19, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 87, 15, 87, 87, 87, 87, 50, 87, 87, 87, 87, -1, -1, -1, -1, -1, 87, -1, -1, 89, 87, 87, 87, 87, 20, 87, 87, 87, 87, 87, 87, 87, 87, 2, 87, 87, 87, 87, 3, 46, 87, 6, 87, 87, 51, 87, 87, 87, 87, -1, -1, -1, -1, -1, -1, -1, 87, 13, 87, 30, 87, 87, 22, 87, 87, 87, 87, 87, 87, 87, 5, 87, 87, 87, 87, 87, 87, 21, 87, 87, -1, 87, -1, -1, 0, 87, 87, 87, 87, 87, 87, 38, 87, 42, 87, 87, 87, 87, 87, 87, 87, 87, 9, 85, -1, -1, 87, 87, 87, 87, 87, 11, 87, 87, 4, 87, 87, 87, 87, 8, 87, -1, -1, 24, 87, 87, 23, 36, 87, 87, 87, 87, 87, 87, 87, 87, -1, -1, 87, 32, 87, 87, 87, 87, 87, 87, 49, 87, -1, 89, 31, 87, 39, 87, 87, 48, 7, 87, -1, 87, 43, 47, 53, -1, 87, -1, 37, 89, },

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
