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
    Token new85(String text, int line, int pos) { return new TName(text, line, pos); }
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
		{{36, 36, 56}, {48, 57, 57}, {65, 90, 58}, {95, 95, 59}, {97, 122, 60}, },
		{},
		{},
		{{0, 9, 61}, {11, 12, 61}, {14, 38, 61}, {40, 65535, 61}, },
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{{42, 42, 62}, {47, 47, 63}, },
		{{46, 46, 64}, {48, 55, 65}, {56, 57, 20}, {76, 76, 66}, {88, 88, 67}, {120, 120, 68}, },
		{{46, 46, 64}, {48, 57, 20}, {76, 76, 66}, },
		{{61, 61, 69}, },
		{},
		{{60, 60, 70}, {61, 61, 71}, },
		{{61, 61, 72}, },
		{{61, 61, 73}, {62, 62, 74}, },
		{{99, 99, 75}, {112, 112, 76}, {116, 116, 77}, },
		{{36, 122, -9}, },
		{},
		{},
		{},
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 60}, {98, 98, 78}, {99, 122, 60}, },
		{{36, 95, -9}, {97, 110, 60}, {111, 111, 79}, {112, 113, 60}, {114, 114, 80}, {115, 120, 60}, {121, 121, 81}, {122, 122, 60}, },
		{{36, 95, -9}, {97, 97, 82}, {98, 103, 60}, {104, 104, 83}, {105, 107, 60}, {108, 108, 84}, {109, 109, 85}, {110, 122, 60}, },
		{{36, 95, -9}, {97, 100, 60}, {101, 101, 86}, {102, 110, 60}, {111, 111, 87}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 109, 60}, {110, 110, 88}, {111, 119, 60}, {120, 120, 89}, {121, 122, 60}, },
		{{36, 95, -9}, {97, 97, 90}, {98, 104, 60}, {105, 105, 91}, {106, 107, 60}, {108, 108, 92}, {109, 113, 60}, {114, 114, 93}, {115, 122, 60}, },
		{{36, 110, -35}, {111, 111, 94}, {112, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 101, 60}, {102, 102, 95}, {103, 108, 60}, {109, 109, 96}, {110, 110, 97}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 98}, {102, 110, 60}, {111, 111, 99}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 97, 100}, {98, 100, 60}, {101, 101, 101}, {102, 110, 60}, {111, 111, 102}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 113, 60}, {114, 114, 103}, {115, 116, 60}, {117, 117, 104}, {118, 122, 60}, },
		{{36, 100, -37}, {101, 101, 105}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 103, 60}, {104, 104, 106}, {105, 111, 60}, {112, 112, 107}, {113, 115, 60}, {116, 116, 108}, {117, 120, 60}, {121, 121, 109}, {122, 122, 60}, },
		{{36, 95, -9}, {97, 97, 110}, {98, 103, 60}, {104, 104, 111}, {105, 110, 60}, {111, 111, 112}, {112, 113, 60}, {114, 114, 113}, {115, 122, 60}, },
		{{36, 109, -38}, {110, 110, 114}, {111, 122, 60}, },
		{{36, 95, -9}, {97, 104, 60}, {105, 105, 115}, {106, 110, 60}, {111, 111, 116}, {112, 122, 60}, },
		{{36, 104, -50}, {105, 105, 117}, {106, 122, 60}, },
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
		{{0, 38, -12}, {39, 39, 118}, {40, 65535, 61}, },
		{{0, 41, 119}, {42, 42, 120}, {43, 65535, 119}, },
		{{0, 9, 121}, {11, 12, 121}, {14, 65535, 121}, },
		{{48, 57, 122}, },
		{{46, 76, -21}, },
		{},
		{{48, 57, 123}, {65, 70, 124}, {97, 102, 125}, },
		{{48, 102, -69}, },
		{},
		{},
		{},
		{},
		{},
		{{62, 62, 126}, },
		{{97, 97, 127}, },
		{{97, 97, 128}, },
		{{104, 104, 129}, },
		{{36, 95, -9}, {97, 114, 60}, {115, 115, 130}, {116, 122, 60}, },
		{{36, 110, -35}, {111, 111, 131}, {112, 122, 60}, },
		{{36, 100, -37}, {101, 101, 132}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 115, 60}, {116, 116, 133}, {117, 122, 60}, },
		{{36, 114, -80}, {115, 115, 134}, {116, 116, 135}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 97, 136}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 97, 137}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 111, 60}, {112, 112, 138}, {113, 122, 60}, },
		{{36, 101, -42}, {102, 102, 139}, {103, 122, 60}, },
		{{36, 95, -9}, {97, 116, 60}, {117, 117, 140}, {118, 122, 60}, },
		{{36, 115, -83}, {116, 116, 141}, {117, 122, 60}, },
		{{36, 104, -50}, {105, 105, 142}, {106, 115, 60}, {116, 116, 143}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 107, 60}, {108, 108, 144}, {109, 122, 60}, },
		{{36, 109, -38}, {110, 110, 145}, {111, 122, 60}, },
		{{36, 110, -35}, {111, 111, 146}, {112, 122, 60}, },
		{{36, 110, -35}, {111, 111, 147}, {112, 122, 60}, },
		{{36, 115, -83}, {116, 116, 148}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 111, -87}, {112, 112, 149}, {113, 122, 60}, },
		{{36, 114, -80}, {115, 115, 150}, {116, 116, 151}, {117, 122, 60}, },
		{{36, 109, -38}, {110, 110, 152}, {111, 122, 60}, },
		{{36, 109, -38}, {110, 110, 153}, {111, 111, 154}, {112, 122, 60}, },
		{{36, 115, -83}, {116, 116, 155}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 102, 60}, {103, 103, 156}, {104, 118, 60}, {119, 119, 157}, {120, 122, 60}, },
		{{36, 111, -87}, {112, 112, 158}, {113, 122, 60}, },
		{{36, 104, -50}, {105, 105, 159}, {106, 110, 60}, {111, 111, 160}, {112, 122, 60}, },
		{{36, 97, -34}, {98, 98, 161}, {99, 122, 60}, },
		{{36, 115, -83}, {116, 116, 162}, {117, 122, 60}, },
		{{36, 110, -35}, {111, 111, 163}, {112, 122, 60}, },
		{{36, 100, -37}, {101, 101, 164}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 97, 165}, {98, 122, 60}, },
		{{36, 109, -38}, {110, 110, 166}, {111, 122, 60}, },
		{{36, 97, -34}, {98, 98, 167}, {99, 122, 60}, },
		{{36, 113, -45}, {114, 114, 168}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 169}, {98, 116, 60}, {117, 117, 170}, {118, 122, 60}, },
		{{36, 95, -9}, {97, 106, 60}, {107, 107, 171}, {108, 122, 60}, },
		{{36, 113, -45}, {114, 114, 172}, {115, 122, 60}, },
		{{36, 104, -50}, {105, 105, 173}, {106, 107, 60}, {108, 108, 174}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 175}, {117, 122, 60}, },
		{},
		{{0, 65535, -64}, },
		{{0, 41, 176}, {42, 42, 120}, {43, 46, 176}, {47, 47, 177}, {48, 65535, 176}, },
		{{0, 65535, -65}, },
		{{48, 57, 122}, {69, 69, 178}, {70, 70, 179}, {101, 101, 180}, {102, 102, 181}, },
		{{48, 70, -69}, {76, 76, 66}, {97, 102, 125}, },
		{{48, 102, -125}, },
		{{48, 102, -125}, },
		{},
		{{117, 117, 182}, },
		{{114, 114, 183}, },
		{{105, 105, 184}, },
		{{36, 115, -83}, {116, 116, 185}, {117, 122, 60}, },
		{{36, 107, -92}, {108, 108, 186}, {109, 122, 60}, },
		{{36, 95, -9}, {97, 97, 187}, {98, 122, 60}, },
		{{36, 100, -37}, {101, 101, 188}, {102, 122, 60}, },
		{{36, 100, -37}, {101, 101, 189}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 98, 60}, {99, 99, 190}, {100, 122, 60}, },
		{{36, 113, -45}, {114, 114, 191}, {115, 122, 60}, },
		{{36, 114, -80}, {115, 115, 192}, {116, 122, 60}, },
		{{36, 102, -103}, {103, 103, 193}, {104, 107, 60}, {108, 108, 194}, {109, 122, 60}, },
		{{36, 95, -9}, {97, 97, 195}, {98, 122, 60}, },
		{{36, 97, -34}, {98, 98, 196}, {99, 122, 60}, },
		{{36, 100, -37}, {101, 101, 197}, {102, 122, 60}, },
		{{36, 115, -83}, {116, 116, 198}, {117, 122, 60}, },
		{{36, 100, -37}, {101, 101, 199}, {102, 122, 60}, },
		{{36, 114, -80}, {115, 115, 200}, {116, 122, 60}, },
		{{36, 95, -9}, {97, 97, 201}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 97, 202}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 108, 60}, {109, 109, 203}, {110, 122, 60}, },
		{{36, 110, -35}, {111, 111, 204}, {112, 122, 60}, },
		{{36, 107, -92}, {108, 108, 205}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 206}, {117, 122, 60}, },
		{{36, 100, -37}, {101, 101, 207}, {102, 122, 60}, },
		{{36, 102, -103}, {103, 103, 208}, {104, 122, 60}, },
		{{36, 102, -103}, {103, 103, 209}, {104, 122, 60}, },
		{{36, 106, -116}, {107, 107, 210}, {108, 122, 60}, },
		{{36, 104, -50}, {105, 105, 211}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 212}, {98, 108, 60}, {109, 109, 213}, {110, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 117, 60}, {118, 118, 214}, {119, 122, 60}, },
		{{36, 115, -83}, {116, 116, 215}, {117, 122, 60}, },
		{{36, 107, -92}, {108, 108, 216}, {109, 122, 60}, },
		{{36, 116, -89}, {117, 117, 217}, {118, 122, 60}, },
		{{36, 113, -45}, {114, 114, 218}, {115, 122, 60}, },
		{{36, 98, -137}, {99, 99, 219}, {100, 122, 60}, },
		{{36, 115, -83}, {116, 116, 220}, {117, 122, 60}, },
		{{36, 98, -137}, {99, 99, 221}, {100, 122, 60}, },
		{{36, 107, -92}, {108, 108, 222}, {109, 122, 60}, },
		{{36, 110, -35}, {111, 111, 223}, {112, 122, 60}, },
		{{36, 109, -38}, {110, 110, 224}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 225}, {102, 122, 60}, },
		{{36, 109, -38}, {110, 110, 226}, {111, 122, 60}, },
		{{36, 115, -83}, {116, 116, 227}, {117, 122, 60}, },
		{{36, 95, -9}, {97, 99, 60}, {100, 100, 228}, {101, 122, 60}, },
		{{36, 95, -9}, {97, 97, 229}, {98, 122, 60}, },
		{{36, 103, -47}, {104, 104, 230}, {105, 122, 60}, },
		{{0, 41, 231}, {42, 42, 232}, {43, 65535, 231}, },
		{},
		{{43, 43, 233}, {45, 45, 234}, {48, 57, 235}, },
		{},
		{{43, 57, -180}, },
		{},
		{{103, 103, 236}, },
		{{97, 97, 237}, },
		{{115, 115, 238}, },
		{{36, 113, -45}, {114, 114, 239}, {115, 122, 60}, },
		{{36, 100, -37}, {101, 101, 240}, {102, 122, 60}, },
		{{36, 106, -116}, {107, 107, 241}, {108, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 103, -47}, {104, 104, 242}, {105, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 114, -80}, {115, 115, 243}, {116, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 116, -89}, {117, 117, 244}, {118, 122, 60}, },
		{{36, 107, -92}, {108, 108, 245}, {109, 122, 60}, },
		{{36, 113, -45}, {114, 114, 246}, {115, 122, 60}, },
		{{36, 108, -149}, {109, 109, 247}, {110, 122, 60}, },
		{{36, 109, -38}, {110, 110, 248}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 249}, {102, 122, 60}, },
		{{36, 107, -92}, {108, 108, 250}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 251}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 252}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 97, 253}, {98, 122, 60}, },
		{{36, 113, -45}, {114, 114, 254}, {115, 122, 60}, },
		{{36, 115, -83}, {116, 116, 255}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 116, -89}, {117, 117, 256}, {118, 122, 60}, },
		{{36, 117, -161}, {118, 118, 257}, {119, 122, 60}, },
		{{36, 113, -45}, {114, 114, 258}, {115, 122, 60}, },
		{{36, 116, -89}, {117, 117, 259}, {118, 122, 60}, },
		{{36, 95, -9}, {97, 97, 260}, {98, 122, 60}, },
		{{36, 100, -37}, {101, 101, 261}, {102, 122, 60}, },
		{{36, 104, -50}, {105, 105, 262}, {106, 122, 60}, },
		{{36, 113, -45}, {114, 114, 263}, {115, 122, 60}, },
		{{36, 115, -83}, {116, 116, 264}, {117, 122, 60}, },
		{{36, 104, -50}, {105, 105, 265}, {106, 122, 60}, },
		{{36, 104, -50}, {105, 105, 266}, {106, 122, 60}, },
		{{36, 103, -47}, {104, 104, 267}, {105, 122, 60}, },
		{{36, 100, -37}, {101, 101, 268}, {102, 122, 60}, },
		{{36, 95, -9}, {97, 118, 60}, {119, 119, 269}, {120, 122, 60}, },
		{{36, 114, -80}, {115, 115, 270}, {116, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 271}, {112, 122, 60}, },
		{{36, 116, -89}, {117, 117, 272}, {118, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 115, -83}, {116, 116, 273}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{0, 65535, -178}, },
		{{0, 41, 176}, {42, 42, 232}, {43, 65535, -122}, },
		{{48, 57, 235}, },
		{{48, 57, 235}, },
		{{48, 57, 235}, {70, 70, 179}, {102, 102, 181}, },
		{{104, 104, 274}, },
		{{109, 109, 275}, },
		{},
		{{36, 95, -9}, {97, 97, 276}, {98, 122, 60}, },
		{{36, 95, -9}, {97, 97, 277}, {98, 122, 60}, },
		{{36, 111, -87}, {112, 112, 278}, {113, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 107, -92}, {108, 108, 279}, {109, 122, 60}, },
		{{36, 100, -37}, {101, 101, 280}, {102, 122, 60}, },
		{{36, 108, -149}, {109, 109, 281}, {110, 122, 60}, },
		{{36, 110, -35}, {111, 111, 282}, {112, 122, 60}, },
		{{36, 99, -175}, {100, 100, 283}, {101, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 108, -149}, {109, 109, 284}, {110, 122, 60}, },
		{{36, 109, -38}, {110, 110, 285}, {111, 122, 60}, },
		{{36, 101, -42}, {102, 102, 286}, {103, 122, 60}, },
		{{36, 103, -47}, {104, 104, 287}, {105, 122, 60}, },
		{{36, 111, -87}, {112, 112, 288}, {113, 122, 60}, },
		{{36, 100, -37}, {101, 101, 289}, {102, 122, 60}, },
		{{36, 113, -45}, {114, 114, 290}, {115, 122, 60}, },
		{{36, 107, -92}, {108, 108, 291}, {109, 122, 60}, },
		{{36, 115, -83}, {116, 116, 292}, {117, 122, 60}, },
		{{36, 98, -137}, {99, 99, 293}, {100, 122, 60}, },
		{{36, 98, -137}, {99, 99, 294}, {100, 122, 60}, },
		{{36, 109, -38}, {110, 110, 295}, {111, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 296}, {98, 122, 60}, },
		{{36, 98, -137}, {99, 99, 297}, {100, 122, 60}, },
		{{36, 113, -45}, {114, 114, 298}, {115, 122, 60}, },
		{{36, 114, -80}, {115, 115, 299}, {116, 122, 60}, },
		{{36, 114, -80}, {115, 115, 300}, {116, 122, 60}, },
		{{36, 104, -50}, {105, 105, 301}, {106, 122, 60}, },
		{{36, 118, -225}, {119, 119, 302}, {120, 122, 60}, },
		{{36, 95, -9}, {97, 97, 303}, {98, 122, 60}, },
		{{36, 104, -50}, {105, 105, 304}, {106, 122, 60}, },
		{{116, 116, 305}, },
		{{101, 101, 306}, },
		{{36, 98, -137}, {99, 99, 307}, {100, 122, 60}, },
		{{36, 109, -38}, {110, 110, 308}, {111, 122, 60}, },
		{{36, 110, -35}, {111, 111, 309}, {112, 122, 60}, },
		{{36, 115, -83}, {116, 116, 310}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 311}, {112, 122, 60}, },
		{{36, 109, -38}, {110, 110, 312}, {111, 122, 60}, },
		{{36, 114, -80}, {115, 115, 313}, {116, 122, 60}, },
		{{36, 100, -37}, {101, 101, 314}, {102, 122, 60}, },
		{{36, 98, -137}, {99, 99, 315}, {100, 122, 60}, },
		{{36, 95, -9}, {97, 97, 316}, {98, 122, 60}, },
		{{36, 110, -35}, {111, 111, 317}, {112, 122, 60}, },
		{{36, 114, -80}, {115, 115, 318}, {116, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 319}, {98, 122, 60}, },
		{{36, 115, -83}, {116, 116, 320}, {117, 122, 60}, },
		{{36, 100, -37}, {101, 101, 321}, {102, 122, 60}, },
		{{36, 115, -83}, {116, 116, 322}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 107, -92}, {108, 108, 323}, {109, 122, 60}, },
		{{36, 104, -50}, {105, 105, 324}, {106, 122, 60}, },
		{{36, 110, -35}, {111, 111, 325}, {112, 122, 60}, },
		{{36, 118, -225}, {119, 119, 326}, {120, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 327}, {102, 122, 60}, },
		{{36, 109, -38}, {110, 110, 328}, {111, 122, 60}, },
		{{36, 107, -92}, {108, 108, 329}, {109, 122, 60}, },
		{{36, 107, -92}, {108, 108, 330}, {109, 122, 60}, },
		{{101, 101, 331}, },
		{{116, 116, 332}, },
		{{36, 115, -83}, {116, 116, 333}, {117, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 104, -50}, {105, 105, 334}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 335}, {111, 122, 60}, },
		{{36, 104, -50}, {105, 105, 336}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 337}, {111, 122, 60}, },
		{{36, 100, -37}, {101, 101, 338}, {102, 122, 60}, },
		{{36, 98, -137}, {99, 99, 339}, {100, 122, 60}, },
		{{36, 101, -42}, {102, 102, 340}, {103, 122, 60}, },
		{{36, 118, -225}, {119, 119, 341}, {120, 122, 60}, },
		{{36, 95, -9}, {97, 120, 60}, {121, 121, 342}, {122, 122, 60}, },
		{{36, 104, -50}, {105, 105, 343}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 344}, {102, 122, 60}, },
		{{36, 104, -50}, {105, 105, 345}, {106, 122, 60}, },
		{{36, 109, -38}, {110, 110, 346}, {111, 122, 60}, },
		{{36, 109, -38}, {110, 110, 347}, {111, 122, 60}, },
		{{36, 104, -50}, {105, 105, 348}, {106, 122, 60}, },
		{{36, 109, -38}, {110, 110, 349}, {111, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 104, -50}, {105, 105, 350}, {106, 122, 60}, },
		{{36, 100, -37}, {101, 101, 351}, {102, 122, 60}, },
		{{120, 120, 352}, },
		{{101, 101, 353}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 354}, {111, 122, 60}, },
		{{36, 104, -50}, {105, 105, 355}, {106, 122, 60}, },
		{{36, 115, -83}, {116, 116, 356}, {117, 122, 60}, },
		{{36, 115, -83}, {116, 116, 357}, {117, 122, 60}, },
		{{36, 110, -35}, {111, 111, 358}, {112, 122, 60}, },
		{{36, 100, -37}, {101, 101, 359}, {102, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 104, -50}, {105, 105, 360}, {106, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 95, -9}, {97, 97, 361}, {98, 122, 60}, },
		{{36, 99, -175}, {100, 100, 362}, {101, 122, 60}, },
		{{36, 109, -38}, {110, 110, 363}, {111, 122, 60}, },
		{{36, 117, -161}, {118, 118, 364}, {119, 122, 60}, },
		{{36, 104, -50}, {105, 105, 365}, {106, 122, 60}, },
		{{36, 115, -83}, {116, 116, 366}, {117, 122, 60}, },
		{{36, 115, -83}, {116, 116, 367}, {117, 122, 60}, },
		{{36, 109, -38}, {110, 110, 368}, {111, 122, 60}, },
		{{36, 122, -9}, },
		{{99, 99, 369}, },
		{{114, 114, 370}, },
		{{36, 115, -83}, {116, 116, 371}, {117, 122, 60}, },
		{{36, 115, -83}, {116, 116, 372}, {117, 122, 60}, },
		{{36, 110, -35}, {111, 111, 373}, {112, 122, 60}, },
		{{36, 114, -80}, {115, 115, 374}, {116, 122, 60}, },
		{{36, 101, -42}, {102, 102, 375}, {103, 122, 60}, },
		{{36, 104, -50}, {105, 105, 376}, {106, 122, 60}, },
		{{36, 115, -83}, {116, 116, 377}, {117, 122, 60}, },
		{{36, 113, -45}, {114, 114, 378}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 117, -161}, {118, 118, 379}, {119, 122, 60}, },
		{{36, 110, -35}, {111, 111, 380}, {112, 122, 60}, },
		{{36, 95, -9}, {97, 121, 60}, {122, 122, 381}, },
		{{36, 98, -137}, {99, 99, 382}, {100, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 117, -161}, {118, 118, 383}, {119, 122, 60}, },
		{{101, 101, 384}, },
		{{48, 57, 385}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 386}, {112, 122, 60}, },
		{{36, 113, -45}, {114, 114, 387}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 109, -38}, {110, 110, 388}, {111, 122, 60}, },
		{{36, 98, -137}, {99, 99, 389}, {100, 122, 60}, },
		{{36, 113, -45}, {114, 114, 390}, {115, 122, 60}, },
		{{36, 110, -35}, {111, 111, 391}, {112, 122, 60}, },
		{{36, 106, -116}, {107, 107, 392}, {108, 122, 60}, },
		{{36, 100, -37}, {101, 101, 393}, {102, 122, 60}, },
		{{36, 103, -47}, {104, 104, 394}, {105, 122, 60}, },
		{{36, 110, -35}, {111, 111, 395}, {112, 122, 60}, },
		{{112, 112, 396}, },
		{{48, 57, 385}, },
		{{36, 113, -45}, {114, 114, 397}, {115, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 117, -161}, {118, 118, 398}, {119, 122, 60}, },
		{{36, 103, -47}, {104, 104, 399}, {105, 122, 60}, },
		{{36, 95, -9}, {97, 97, 400}, {98, 122, 60}, },
		{{36, 106, -116}, {107, 107, 401}, {108, 122, 60}, },
		{{36, 100, -37}, {101, 101, 402}, {102, 122, 60}, },
		{{36, 99, -175}, {100, 100, 403}, {101, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 106, -116}, {107, 107, 404}, {108, 122, 60}, },
		{{116, 116, 405}, },
		{{36, 122, -9}, },
		{{36, 110, -35}, {111, 111, 406}, {112, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 120, -321}, {121, 121, 407}, {122, 122, 60}, },
		{{36, 100, -37}, {101, 101, 408}, {102, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 100, -37}, {101, 101, 409}, {102, 122, 60}, },
		{{105, 105, 410}, },
		{{36, 106, -116}, {107, 107, 411}, {108, 122, 60}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{36, 122, -9}, },
		{{111, 111, 412}, },
		{{36, 100, -37}, {101, 101, 413}, {102, 122, 60}, },
		{{110, 110, 414}, },
		{{36, 122, -9}, },
		{},
    };*/

    private static int[][] accept;
/*  {
		{-1, 92, 92, 92, 92, -1, -1, 86, 71, 68, 65, 61, 62, 83, 81, 55, 82, 64, 84, 89, 89, 63, 58, 76, 67, 74, -1, 86, 59, 60, 70, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 56, 69, 57, 73, -1, 91, 86, 86, 86, 86, 86, -1, -1, 93, -1, 89, 89, -1, -1, 66, 78, 77, 72, 75, 79, -1, -1, -1, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 35, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 52, 86, 86, 86, 86, 86, 85, -1, -1, 93, 90, 89, 89, 89, 80, -1, -1, -1, 86, 86, 86, 86, 86, 86, 86, 86, 27, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 17, 86, 86, 86, 86, 40, 41, 44, 86, 86, 86, 45, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, -1, 93, -1, 90, -1, 90, -1, -1, -1, 86, 86, 86, 14, 25, 86, 16, 86, 28, 29, 86, 86, 86, 86, 86, 86, 86, 86, 33, 34, 86, 86, 86, 86, 18, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 12, 86, 54, -1, -1, -1, -1, 90, -1, -1, 87, 86, 86, 86, 26, 10, 86, 86, 86, 86, 86, 86, 1, 19, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 86, 15, 86, 86, 86, 86, 50, 86, 86, 86, 86, -1, -1, 86, 86, 86, 86, 20, 86, 86, 86, 86, 86, 86, 86, 86, 2, 86, 86, 86, 86, 3, 46, 86, 6, 86, 86, 51, 86, 86, 86, 86, -1, -1, 86, 13, 86, 30, 86, 86, 22, 86, 86, 86, 86, 86, 86, 86, 5, 86, 86, 86, 86, 86, 86, 21, 86, 86, -1, -1, 0, 86, 86, 86, 86, 86, 86, 38, 86, 42, 86, 86, 86, 86, 86, 86, 86, 86, 9, -1, -1, 86, 86, 86, 86, 86, 11, 86, 86, 4, 86, 86, 86, 86, 8, 86, -1, -1, 24, 86, 86, 23, 36, 86, 86, 86, 86, 86, 86, 86, 86, -1, 87, 86, 32, 86, 86, 86, 86, 86, 86, 49, 86, -1, 31, 86, 39, 86, 86, 48, 7, 86, -1, 86, 43, 47, 53, -1, 86, -1, 37, 87, },

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
