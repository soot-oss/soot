package ca.mcgill.sable.soot.jimple.parser.lexer;

import java.io.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.jimple.parser.node.*;

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
    Token new85(String text, int line, int pos) { return new TQuotedName(text, line, pos); }
    Token new86(String text, int line, int pos) { return new TIdentifier(text, line, pos); }
    Token new87(String text, int line, int pos) { return new TAtIdentifier(text, line, pos); }
    Token new88(String text, int line, int pos) { return new TBoolConstant(text, line, pos); }
    Token new89(String text, int line, int pos) { return new TIntegerConstant(text, line, pos); }
    Token new90(String text, int line, int pos) { return new TFloatConstant(text, line, pos); }
    Token new91(String text, int line, int pos) { return new TStringConstant(text, line, pos); }
    Token new92(String text, int line, int pos) { return new TBlank(text, line, pos); }
    Token new93(String text, int line, int pos) { return new TComment(text, line, pos); }

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
                {{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {33, 33, 5}, {34, 34, 6}, {36, 36, 7}, {37, 37, 8}, {38, 38, 9}, {39, 39, 10}, {40, 40, 11}, {41, 41, 12}, {42, 42, 13}, {43, 43, 14}, {44, 44, 15}, {45, 45, 16}, {46, 46, 17}, {47, 47, 18}, {48, 48, 19}, {49, 57, 20}, {58, 58, 21}, {59, 59, 22}, {60, 60, 23}, {61, 61, 24}, {62, 62, 25}, {64, 64, 26}, {65, 90, 27}, {91, 91, 28}, {93, 93, 29}, {94, 94, 30}, {95, 95, 31}, {97, 97, 32}, {98, 98, 33}, {99, 99, 34}, {100, 100, 35}, {101, 101, 36}, {102, 102, 37}, {103, 103, 38}, {104, 104, 39}, {105, 105, 40}, {106, 107, 39}, {108, 108, 41}, {109, 109, 39}, {110, 110, 42}, {111, 111, 39}, {112, 112, 43}, {113, 113, 39}, {114, 114, 44}, {115, 115, 45}, {116, 116, 46}, {117, 117, 47}, {118, 118, 48}, {119, 119, 49}, {120, 122, 39}, {123, 123, 50}, {124, 124, 51}, {125, 125, 52}, },
                {{9, 32, -2}, },
                {{9, 32, -2}, },
                {{9, 32, -2}, },
                {{9, 32, -2}, },
                {{61, 61, 53}, },
                {{0, 33, 54}, {34, 34, 55}, {35, 65535, 54}, },
                {{36, 36, 56}, {46, 46, 57}, {48, 57, 58}, {65, 90, 59}, {95, 95, 60}, {97, 122, 61}, },
                {},
                {},
                {{0, 9, 62}, {11, 12, 62}, {14, 38, 62}, {40, 65535, 62}, },
                {},
                {},
                {},
                {},
                {},
                {},
                {},
                {{42, 42, 63}, {47, 47, 64}, },
                {{46, 46, 65}, {48, 55, 66}, {56, 57, 20}, {76, 76, 67}, {88, 88, 68}, {120, 120, 69}, },
                {{46, 46, 65}, {48, 57, 20}, {76, 76, 67}, },
                {{61, 61, 70}, },
                {},
                {{60, 60, 71}, {61, 61, 72}, {99, 99, 73}, {105, 105, 74}, },
                {{61, 61, 75}, },
                {{61, 61, 76}, {62, 62, 77}, },
                {{99, 99, 78}, {112, 112, 79}, {116, 116, 80}, },
                {{36, 122, -9}, },
                {},
                {},
                {},
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 97, 61}, {98, 98, 81}, {99, 122, 61}, },
                {{36, 95, -9}, {97, 110, 61}, {111, 111, 82}, {112, 113, 61}, {114, 114, 83}, {115, 120, 61}, {121, 121, 84}, {122, 122, 61}, },
                {{36, 95, -9}, {97, 97, 85}, {98, 103, 61}, {104, 104, 86}, {105, 107, 61}, {108, 108, 87}, {109, 109, 88}, {110, 122, 61}, },
                {{36, 95, -9}, {97, 100, 61}, {101, 101, 89}, {102, 110, 61}, {111, 111, 90}, {112, 122, 61}, },
                {{36, 95, -9}, {97, 109, 61}, {110, 110, 91}, {111, 119, 61}, {120, 120, 92}, {121, 122, 61}, },
                {{36, 95, -9}, {97, 97, 93}, {98, 104, 61}, {105, 105, 94}, {106, 107, 61}, {108, 108, 95}, {109, 113, 61}, {114, 114, 96}, {115, 122, 61}, },
                {{36, 110, -35}, {111, 111, 97}, {112, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 101, 61}, {102, 102, 98}, {103, 108, 61}, {109, 109, 99}, {110, 110, 100}, {111, 122, 61}, },
                {{36, 100, -37}, {101, 101, 101}, {102, 110, 61}, {111, 111, 102}, {112, 122, 61}, },
                {{36, 95, -9}, {97, 97, 103}, {98, 100, 61}, {101, 101, 104}, {102, 110, 61}, {111, 111, 105}, {112, 122, 61}, },
                {{36, 95, -9}, {97, 113, 61}, {114, 114, 106}, {115, 116, 61}, {117, 117, 107}, {118, 122, 61}, },
                {{36, 100, -37}, {101, 101, 108}, {102, 122, 61}, },
                {{36, 95, -9}, {97, 103, 61}, {104, 104, 109}, {105, 111, 61}, {112, 112, 110}, {113, 115, 61}, {116, 116, 111}, {117, 120, 61}, {121, 121, 112}, {122, 122, 61}, },
                {{36, 95, -9}, {97, 97, 113}, {98, 103, 61}, {104, 104, 114}, {105, 110, 61}, {111, 111, 115}, {112, 113, 61}, {114, 114, 116}, {115, 122, 61}, },
                {{36, 109, -38}, {110, 110, 117}, {111, 122, 61}, },
                {{36, 95, -9}, {97, 104, 61}, {105, 105, 118}, {106, 110, 61}, {111, 111, 119}, {112, 122, 61}, },
                {{36, 104, -50}, {105, 105, 120}, {106, 122, 61}, },
                {},
                {},
                {},
                {},
                {{0, 65535, -8}, },
                {},
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{0, 38, -12}, {39, 39, 121}, {40, 65535, 62}, },
                {{0, 41, 122}, {42, 42, 123}, {43, 65535, 122}, },
                {{0, 9, 124}, {11, 12, 124}, {14, 65535, 124}, },
                {{48, 57, 125}, },
                {{46, 76, -21}, },
                {},
                {{48, 57, 126}, {65, 70, 127}, {97, 102, 128}, },
                {{48, 102, -70}, },
                {},
                {},
                {},
                {{108, 108, 129}, },
                {{110, 110, 130}, },
                {},
                {},
                {{62, 62, 131}, },
                {{97, 97, 132}, },
                {{97, 97, 133}, },
                {{104, 104, 134}, },
                {{36, 95, -9}, {97, 114, 61}, {115, 115, 135}, {116, 122, 61}, },
                {{36, 110, -35}, {111, 111, 136}, {112, 122, 61}, },
                {{36, 100, -37}, {101, 101, 137}, {102, 122, 61}, },
                {{36, 95, -9}, {97, 115, 61}, {116, 116, 138}, {117, 122, 61}, },
                {{36, 114, -83}, {115, 115, 139}, {116, 116, 140}, {117, 122, 61}, },
                {{36, 95, -9}, {97, 97, 141}, {98, 122, 61}, },
                {{36, 95, -9}, {97, 97, 142}, {98, 122, 61}, },
                {{36, 95, -9}, {97, 111, 61}, {112, 112, 143}, {113, 122, 61}, },
                {{36, 101, -42}, {102, 102, 144}, {103, 122, 61}, },
                {{36, 95, -9}, {97, 116, 61}, {117, 117, 145}, {118, 122, 61}, },
                {{36, 115, -86}, {116, 116, 146}, {117, 122, 61}, },
                {{36, 104, -50}, {105, 105, 147}, {106, 115, 61}, {116, 116, 148}, {117, 122, 61}, },
                {{36, 95, -9}, {97, 107, 61}, {108, 108, 149}, {109, 122, 61}, },
                {{36, 109, -38}, {110, 110, 150}, {111, 122, 61}, },
                {{36, 110, -35}, {111, 111, 151}, {112, 122, 61}, },
                {{36, 110, -35}, {111, 111, 152}, {112, 122, 61}, },
                {{36, 115, -86}, {116, 116, 153}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 111, -90}, {112, 112, 154}, {113, 122, 61}, },
                {{36, 114, -83}, {115, 115, 155}, {116, 116, 156}, {117, 122, 61}, },
                {{36, 109, -38}, {110, 110, 157}, {111, 122, 61}, },
                {{36, 109, -38}, {110, 110, 158}, {111, 111, 159}, {112, 122, 61}, },
                {{36, 115, -86}, {116, 116, 160}, {117, 122, 61}, },
                {{36, 95, -9}, {97, 102, 61}, {103, 103, 161}, {104, 118, 61}, {119, 119, 162}, {120, 122, 61}, },
                {{36, 111, -90}, {112, 112, 163}, {113, 122, 61}, },
                {{36, 104, -50}, {105, 105, 164}, {106, 110, 61}, {111, 111, 165}, {112, 122, 61}, },
                {{36, 97, -34}, {98, 98, 166}, {99, 122, 61}, },
                {{36, 115, -86}, {116, 116, 167}, {117, 122, 61}, },
                {{36, 110, -35}, {111, 111, 168}, {112, 122, 61}, },
                {{36, 100, -37}, {101, 101, 169}, {102, 122, 61}, },
                {{36, 95, -9}, {97, 97, 170}, {98, 122, 61}, },
                {{36, 109, -38}, {110, 110, 171}, {111, 122, 61}, },
                {{36, 97, -34}, {98, 98, 172}, {99, 122, 61}, },
                {{36, 113, -45}, {114, 114, 173}, {115, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 97, 174}, {98, 116, 61}, {117, 117, 175}, {118, 122, 61}, },
                {{36, 95, -9}, {97, 106, 61}, {107, 107, 176}, {108, 122, 61}, },
                {{36, 113, -45}, {114, 114, 177}, {115, 122, 61}, },
                {{36, 104, -50}, {105, 105, 178}, {106, 107, 61}, {108, 108, 179}, {109, 122, 61}, },
                {{36, 115, -86}, {116, 116, 180}, {117, 122, 61}, },
                {},
                {{0, 65535, -65}, },
                {{0, 41, 181}, {42, 42, 123}, {43, 46, 181}, {47, 47, 182}, {48, 65535, 181}, },
                {{0, 65535, -66}, },
                {{48, 57, 125}, {69, 69, 183}, {70, 70, 184}, {101, 101, 185}, {102, 102, 186}, },
                {{48, 70, -70}, {76, 76, 67}, {97, 102, 128}, },
                {{48, 102, -128}, },
                {{48, 102, -128}, },
                {{105, 105, 187}, },
                {{105, 105, 188}, },
                {},
                {{117, 117, 189}, },
                {{114, 114, 190}, },
                {{105, 105, 191}, },
                {{36, 115, -86}, {116, 116, 192}, {117, 122, 61}, },
                {{36, 107, -95}, {108, 108, 193}, {109, 122, 61}, },
                {{36, 95, -9}, {97, 97, 194}, {98, 122, 61}, },
                {{36, 100, -37}, {101, 101, 195}, {102, 122, 61}, },
                {{36, 100, -37}, {101, 101, 196}, {102, 122, 61}, },
                {{36, 95, -9}, {97, 98, 61}, {99, 99, 197}, {100, 122, 61}, },
                {{36, 113, -45}, {114, 114, 198}, {115, 122, 61}, },
                {{36, 114, -83}, {115, 115, 199}, {116, 122, 61}, },
                {{36, 102, -106}, {103, 103, 200}, {104, 107, 61}, {108, 108, 201}, {109, 122, 61}, },
                {{36, 95, -9}, {97, 97, 202}, {98, 122, 61}, },
                {{36, 97, -34}, {98, 98, 203}, {99, 122, 61}, },
                {{36, 100, -37}, {101, 101, 204}, {102, 122, 61}, },
                {{36, 115, -86}, {116, 116, 205}, {117, 122, 61}, },
                {{36, 100, -37}, {101, 101, 206}, {102, 122, 61}, },
                {{36, 114, -83}, {115, 115, 207}, {116, 122, 61}, },
                {{36, 95, -9}, {97, 97, 208}, {98, 122, 61}, },
                {{36, 95, -9}, {97, 97, 209}, {98, 122, 61}, },
                {{36, 95, -9}, {97, 108, 61}, {109, 109, 210}, {110, 122, 61}, },
                {{36, 110, -35}, {111, 111, 211}, {112, 122, 61}, },
                {{36, 107, -95}, {108, 108, 212}, {109, 122, 61}, },
                {{36, 115, -86}, {116, 116, 213}, {117, 122, 61}, },
                {{36, 100, -37}, {101, 101, 214}, {102, 122, 61}, },
                {{36, 102, -106}, {103, 103, 215}, {104, 122, 61}, },
                {{36, 102, -106}, {103, 103, 216}, {104, 122, 61}, },
                {{36, 106, -119}, {107, 107, 217}, {108, 122, 61}, },
                {{36, 104, -50}, {105, 105, 218}, {106, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 97, 219}, {98, 108, 61}, {109, 109, 220}, {110, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 117, 61}, {118, 118, 221}, {119, 122, 61}, },
                {{36, 115, -86}, {116, 116, 222}, {117, 122, 61}, },
                {{36, 107, -95}, {108, 108, 223}, {109, 122, 61}, },
                {{36, 116, -92}, {117, 117, 224}, {118, 122, 61}, },
                {{36, 113, -45}, {114, 114, 225}, {115, 122, 61}, },
                {{36, 98, -142}, {99, 99, 226}, {100, 122, 61}, },
                {{36, 115, -86}, {116, 116, 227}, {117, 122, 61}, },
                {{36, 98, -142}, {99, 99, 228}, {100, 122, 61}, },
                {{36, 107, -95}, {108, 108, 229}, {109, 122, 61}, },
                {{36, 110, -35}, {111, 111, 230}, {112, 122, 61}, },
                {{36, 109, -38}, {110, 110, 231}, {111, 122, 61}, },
                {{36, 100, -37}, {101, 101, 232}, {102, 122, 61}, },
                {{36, 109, -38}, {110, 110, 233}, {111, 122, 61}, },
                {{36, 115, -86}, {116, 116, 234}, {117, 122, 61}, },
                {{36, 95, -9}, {97, 99, 61}, {100, 100, 235}, {101, 122, 61}, },
                {{36, 95, -9}, {97, 97, 236}, {98, 122, 61}, },
                {{36, 103, -47}, {104, 104, 237}, {105, 122, 61}, },
                {{0, 41, 238}, {42, 42, 239}, {43, 65535, 238}, },
                {},
                {{43, 43, 240}, {45, 45, 241}, {48, 57, 242}, },
                {},
                {{43, 57, -185}, },
                {},
                {{110, 110, 243}, },
                {{116, 116, 244}, },
                {{103, 103, 245}, },
                {{97, 97, 246}, },
                {{115, 115, 247}, },
                {{36, 113, -45}, {114, 114, 248}, {115, 122, 61}, },
                {{36, 100, -37}, {101, 101, 249}, {102, 122, 61}, },
                {{36, 106, -119}, {107, 107, 250}, {108, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 103, -47}, {104, 104, 251}, {105, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 114, -83}, {115, 115, 252}, {116, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 116, -92}, {117, 117, 253}, {118, 122, 61}, },
                {{36, 107, -95}, {108, 108, 254}, {109, 122, 61}, },
                {{36, 113, -45}, {114, 114, 255}, {115, 122, 61}, },
                {{36, 108, -154}, {109, 109, 256}, {110, 122, 61}, },
                {{36, 109, -38}, {110, 110, 257}, {111, 122, 61}, },
                {{36, 100, -37}, {101, 101, 258}, {102, 122, 61}, },
                {{36, 107, -95}, {108, 108, 259}, {109, 122, 61}, },
                {{36, 115, -86}, {116, 116, 260}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 100, -37}, {101, 101, 261}, {102, 122, 61}, },
                {{36, 95, -9}, {97, 97, 262}, {98, 122, 61}, },
                {{36, 113, -45}, {114, 114, 263}, {115, 122, 61}, },
                {{36, 115, -86}, {116, 116, 264}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 116, -92}, {117, 117, 265}, {118, 122, 61}, },
                {{36, 117, -166}, {118, 118, 266}, {119, 122, 61}, },
                {{36, 113, -45}, {114, 114, 267}, {115, 122, 61}, },
                {{36, 116, -92}, {117, 117, 268}, {118, 122, 61}, },
                {{36, 95, -9}, {97, 97, 269}, {98, 122, 61}, },
                {{36, 100, -37}, {101, 101, 270}, {102, 122, 61}, },
                {{36, 104, -50}, {105, 105, 271}, {106, 122, 61}, },
                {{36, 113, -45}, {114, 114, 272}, {115, 122, 61}, },
                {{36, 115, -86}, {116, 116, 273}, {117, 122, 61}, },
                {{36, 104, -50}, {105, 105, 274}, {106, 122, 61}, },
                {{36, 104, -50}, {105, 105, 275}, {106, 122, 61}, },
                {{36, 103, -47}, {104, 104, 276}, {105, 122, 61}, },
                {{36, 100, -37}, {101, 101, 277}, {102, 122, 61}, },
                {{36, 95, -9}, {97, 118, 61}, {119, 119, 278}, {120, 122, 61}, },
                {{36, 114, -83}, {115, 115, 279}, {116, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 110, -35}, {111, 111, 280}, {112, 122, 61}, },
                {{36, 116, -92}, {117, 117, 281}, {118, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 115, -86}, {116, 116, 282}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{0, 65535, -183}, },
                {{0, 41, 181}, {42, 42, 239}, {43, 65535, -125}, },
                {{48, 57, 242}, },
                {{48, 57, 242}, },
                {{48, 57, 242}, {70, 70, 184}, {102, 102, 186}, },
                {{105, 105, 283}, },
                {{62, 62, 284}, },
                {{104, 104, 285}, },
                {{109, 109, 286}, },
                {},
                {{36, 95, -9}, {97, 97, 287}, {98, 122, 61}, },
                {{36, 95, -9}, {97, 97, 288}, {98, 122, 61}, },
                {{36, 111, -90}, {112, 112, 289}, {113, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 107, -95}, {108, 108, 290}, {109, 122, 61}, },
                {{36, 100, -37}, {101, 101, 291}, {102, 122, 61}, },
                {{36, 108, -154}, {109, 109, 292}, {110, 122, 61}, },
                {{36, 110, -35}, {111, 111, 293}, {112, 122, 61}, },
                {{36, 99, -180}, {100, 100, 294}, {101, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 108, -154}, {109, 109, 295}, {110, 122, 61}, },
                {{36, 109, -38}, {110, 110, 296}, {111, 122, 61}, },
                {{36, 101, -42}, {102, 102, 297}, {103, 122, 61}, },
                {{36, 103, -47}, {104, 104, 298}, {105, 122, 61}, },
                {{36, 111, -90}, {112, 112, 299}, {113, 122, 61}, },
                {{36, 100, -37}, {101, 101, 300}, {102, 122, 61}, },
                {{36, 113, -45}, {114, 114, 301}, {115, 122, 61}, },
                {{36, 107, -95}, {108, 108, 302}, {109, 122, 61}, },
                {{36, 115, -86}, {116, 116, 303}, {117, 122, 61}, },
                {{36, 98, -142}, {99, 99, 304}, {100, 122, 61}, },
                {{36, 98, -142}, {99, 99, 305}, {100, 122, 61}, },
                {{36, 109, -38}, {110, 110, 306}, {111, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 97, 307}, {98, 122, 61}, },
                {{36, 98, -142}, {99, 99, 308}, {100, 122, 61}, },
                {{36, 113, -45}, {114, 114, 309}, {115, 122, 61}, },
                {{36, 114, -83}, {115, 115, 310}, {116, 122, 61}, },
                {{36, 114, -83}, {115, 115, 311}, {116, 122, 61}, },
                {{36, 104, -50}, {105, 105, 312}, {106, 122, 61}, },
                {{36, 118, -232}, {119, 119, 313}, {120, 122, 61}, },
                {{36, 95, -9}, {97, 97, 314}, {98, 122, 61}, },
                {{36, 104, -50}, {105, 105, 315}, {106, 122, 61}, },
                {{116, 116, 316}, },
                {},
                {{116, 116, 317}, },
                {{101, 101, 318}, },
                {{36, 98, -142}, {99, 99, 319}, {100, 122, 61}, },
                {{36, 109, -38}, {110, 110, 320}, {111, 122, 61}, },
                {{36, 110, -35}, {111, 111, 321}, {112, 122, 61}, },
                {{36, 115, -86}, {116, 116, 322}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 110, -35}, {111, 111, 323}, {112, 122, 61}, },
                {{36, 109, -38}, {110, 110, 324}, {111, 122, 61}, },
                {{36, 114, -83}, {115, 115, 325}, {116, 122, 61}, },
                {{36, 100, -37}, {101, 101, 326}, {102, 122, 61}, },
                {{36, 98, -142}, {99, 99, 327}, {100, 122, 61}, },
                {{36, 95, -9}, {97, 97, 328}, {98, 122, 61}, },
                {{36, 110, -35}, {111, 111, 329}, {112, 122, 61}, },
                {{36, 114, -83}, {115, 115, 330}, {116, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 97, 331}, {98, 122, 61}, },
                {{36, 115, -86}, {116, 116, 332}, {117, 122, 61}, },
                {{36, 100, -37}, {101, 101, 333}, {102, 122, 61}, },
                {{36, 115, -86}, {116, 116, 334}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 107, -95}, {108, 108, 335}, {109, 122, 61}, },
                {{36, 104, -50}, {105, 105, 336}, {106, 122, 61}, },
                {{36, 110, -35}, {111, 111, 337}, {112, 122, 61}, },
                {{36, 118, -232}, {119, 119, 338}, {120, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 100, -37}, {101, 101, 339}, {102, 122, 61}, },
                {{36, 109, -38}, {110, 110, 340}, {111, 122, 61}, },
                {{36, 107, -95}, {108, 108, 341}, {109, 122, 61}, },
                {{36, 107, -95}, {108, 108, 342}, {109, 122, 61}, },
                {{62, 62, 343}, },
                {{101, 101, 344}, },
                {{116, 116, 345}, },
                {{36, 115, -86}, {116, 116, 346}, {117, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 104, -50}, {105, 105, 347}, {106, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 109, -38}, {110, 110, 348}, {111, 122, 61}, },
                {{36, 104, -50}, {105, 105, 349}, {106, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 109, -38}, {110, 110, 350}, {111, 122, 61}, },
                {{36, 100, -37}, {101, 101, 351}, {102, 122, 61}, },
                {{36, 98, -142}, {99, 99, 352}, {100, 122, 61}, },
                {{36, 101, -42}, {102, 102, 353}, {103, 122, 61}, },
                {{36, 118, -232}, {119, 119, 354}, {120, 122, 61}, },
                {{36, 95, -9}, {97, 120, 61}, {121, 121, 355}, {122, 122, 61}, },
                {{36, 104, -50}, {105, 105, 356}, {106, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 100, -37}, {101, 101, 357}, {102, 122, 61}, },
                {{36, 104, -50}, {105, 105, 358}, {106, 122, 61}, },
                {{36, 109, -38}, {110, 110, 359}, {111, 122, 61}, },
                {{36, 109, -38}, {110, 110, 360}, {111, 122, 61}, },
                {{36, 104, -50}, {105, 105, 361}, {106, 122, 61}, },
                {{36, 109, -38}, {110, 110, 362}, {111, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 104, -50}, {105, 105, 363}, {106, 122, 61}, },
                {{36, 100, -37}, {101, 101, 364}, {102, 122, 61}, },
                {},
                {{120, 120, 365}, },
                {{101, 101, 366}, },
                {{36, 122, -9}, },
                {{36, 109, -38}, {110, 110, 367}, {111, 122, 61}, },
                {{36, 104, -50}, {105, 105, 368}, {106, 122, 61}, },
                {{36, 115, -86}, {116, 116, 369}, {117, 122, 61}, },
                {{36, 115, -86}, {116, 116, 370}, {117, 122, 61}, },
                {{36, 110, -35}, {111, 111, 371}, {112, 122, 61}, },
                {{36, 100, -37}, {101, 101, 372}, {102, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 104, -50}, {105, 105, 373}, {106, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 95, -9}, {97, 97, 374}, {98, 122, 61}, },
                {{36, 99, -180}, {100, 100, 375}, {101, 122, 61}, },
                {{36, 109, -38}, {110, 110, 376}, {111, 122, 61}, },
                {{36, 117, -166}, {118, 118, 377}, {119, 122, 61}, },
                {{36, 104, -50}, {105, 105, 378}, {106, 122, 61}, },
                {{36, 115, -86}, {116, 116, 379}, {117, 122, 61}, },
                {{36, 115, -86}, {116, 116, 380}, {117, 122, 61}, },
                {{36, 109, -38}, {110, 110, 381}, {111, 122, 61}, },
                {{36, 122, -9}, },
                {{99, 99, 382}, },
                {{114, 114, 383}, },
                {{36, 115, -86}, {116, 116, 384}, {117, 122, 61}, },
                {{36, 115, -86}, {116, 116, 385}, {117, 122, 61}, },
                {{36, 110, -35}, {111, 111, 386}, {112, 122, 61}, },
                {{36, 114, -83}, {115, 115, 387}, {116, 122, 61}, },
                {{36, 101, -42}, {102, 102, 388}, {103, 122, 61}, },
                {{36, 104, -50}, {105, 105, 389}, {106, 122, 61}, },
                {{36, 115, -86}, {116, 116, 390}, {117, 122, 61}, },
                {{36, 113, -45}, {114, 114, 391}, {115, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 117, -166}, {118, 118, 392}, {119, 122, 61}, },
                {{36, 110, -35}, {111, 111, 393}, {112, 122, 61}, },
                {{36, 95, -9}, {97, 121, 61}, {122, 122, 394}, },
                {{36, 98, -142}, {99, 99, 395}, {100, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 117, -166}, {118, 118, 396}, {119, 122, 61}, },
                {{101, 101, 397}, },
                {{48, 57, 398}, },
                {{36, 122, -9}, },
                {{36, 110, -35}, {111, 111, 399}, {112, 122, 61}, },
                {{36, 113, -45}, {114, 114, 400}, {115, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 109, -38}, {110, 110, 401}, {111, 122, 61}, },
                {{36, 98, -142}, {99, 99, 402}, {100, 122, 61}, },
                {{36, 113, -45}, {114, 114, 403}, {115, 122, 61}, },
                {{36, 110, -35}, {111, 111, 404}, {112, 122, 61}, },
                {{36, 106, -119}, {107, 107, 405}, {108, 122, 61}, },
                {{36, 100, -37}, {101, 101, 406}, {102, 122, 61}, },
                {{36, 103, -47}, {104, 104, 407}, {105, 122, 61}, },
                {{36, 110, -35}, {111, 111, 408}, {112, 122, 61}, },
                {{112, 112, 409}, },
                {{48, 57, 398}, },
                {{36, 113, -45}, {114, 114, 410}, {115, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 117, -166}, {118, 118, 411}, {119, 122, 61}, },
                {{36, 103, -47}, {104, 104, 412}, {105, 122, 61}, },
                {{36, 95, -9}, {97, 97, 413}, {98, 122, 61}, },
                {{36, 106, -119}, {107, 107, 414}, {108, 122, 61}, },
                {{36, 100, -37}, {101, 101, 415}, {102, 122, 61}, },
                {{36, 99, -180}, {100, 100, 416}, {101, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 106, -119}, {107, 107, 417}, {108, 122, 61}, },
                {{116, 116, 418}, },
                {{36, 122, -9}, },
                {{36, 110, -35}, {111, 111, 419}, {112, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 120, -333}, {121, 121, 420}, {122, 122, 61}, },
                {{36, 100, -37}, {101, 101, 421}, {102, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 100, -37}, {101, 101, 422}, {102, 122, 61}, },
                {{105, 105, 423}, },
                {{36, 106, -119}, {107, 107, 424}, {108, 122, 61}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{36, 122, -9}, },
                {{111, 111, 425}, },
                {{36, 100, -37}, {101, 101, 426}, {102, 122, 61}, },
                {{110, 110, 427}, },
                {{36, 122, -9}, },
                {},
    };*/

    private static int[][] accept;
/*  {
                {-1, 92, 92, 92, 92, -1, -1, 86, 71, 68, 65, 61, 62, 83, 81, 55, 82, 64, 84, 89, 89, 63, 58, 76, 67, 74, -1, 86, 59, 60, 70, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 56, 69, 57, 73, -1, 91, 86, 86, 86, 86, 86, 86, -1, -1, 93, -1, 89, 89, -1, -1, 66, 78, 77, -1, -1, 72, 75, 79, -1, -1, -1, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 35, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 52, 86, 86, 86, 86, 86, 85, -1, -1, 93, 90, 89, 89, 89, -1, -1, 80, -1, -1, -1, 86, 86, 86, 86, 86, 86, 86, 86, 27, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 17, 86, 86, 86, 86, 40, 41, 44, 86, 86, 86, 45, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, -1, 93, -1, 90, -1, 90, -1, -1, -1, -1, -1, 86, 86, 86, 14, 25, 86, 16, 86, 28, 29, 86, 86, 86, 86, 86, 86, 86, 86, 33, 34, 86, 86, 86, 86, 18, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 12, 86, 54, -1, -1, -1, -1, 90, -1, -1, -1, -1, 87, 86, 86, 86, 26, 10, 86, 86, 86, 86, 86, 86, 1, 19, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 15, 86, 86, 86, 86, 50, 86, 86, 86, 86, -1, 86, -1, -1, 86, 86, 86, 86, 20, 86, 86, 86, 86, 86, 86, 86, 86, 2, 86, 86, 86, 86, 3, 46, 86, 6, 86, 86, 51, 86, 86, 86, 86, -1, -1, -1, 86, 13, 86, 30, 86, 86, 22, 86, 86, 86, 86, 86, 86, 86, 5, 86, 86, 86, 86, 86, 86, 21, 86, 86, 86, -1, -1, 0, 86, 86, 86, 86, 86, 86, 38, 86, 42, 86, 86, 86, 86, 86, 86, 86, 86, 9, -1, -1, 86, 86, 86, 86, 86, 11, 86, 86, 4, 86, 86, 86, 86, 8, 86, -1, -1, 24, 86, 86, 23, 36, 86, 86, 86, 86, 86, 86, 86, 86, -1, 87, 86, 32, 86, 86, 86, 86, 86, 86, 49, 86, -1, 31, 86, 39, 86, 86, 48, 7, 86, -1, 86, 43, 47, 53, -1, 86, -1, 37, 87, },

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
