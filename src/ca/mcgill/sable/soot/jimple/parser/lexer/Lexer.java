package ca.mcgill.sable.soot.jimple.parser.lexer;

import java.io.*;
import ca.mcgill.sable.util.*;
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

    private Token getToken() throws IOException, LexerException
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
                                getText(accept_length),
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
                                getText(accept_length),
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
                                getText(accept_length),
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
                                getText(accept_length),
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

    Token new0(int line, int pos) { return new TPublic(line, pos); }
    Token new1(int line, int pos) { return new TProtected(line, pos); }
    Token new2(int line, int pos) { return new TPrivate(line, pos); }
    Token new3(int line, int pos) { return new TStatic(line, pos); }
    Token new4(int line, int pos) { return new TFinal(line, pos); }
    Token new5(int line, int pos) { return new TAbstract(line, pos); }
    Token new6(int line, int pos) { return new TSynchronized(line, pos); }
    Token new7(int line, int pos) { return new TTransient(line, pos); }
    Token new8(int line, int pos) { return new TVolatile(line, pos); }
    Token new9(int line, int pos) { return new TClass(line, pos); }
    Token new10(int line, int pos) { return new TInterface(line, pos); }
    Token new11(int line, int pos) { return new TVoid(line, pos); }
    Token new12(int line, int pos) { return new TBoolean(line, pos); }
    Token new13(int line, int pos) { return new TByte(line, pos); }
    Token new14(int line, int pos) { return new TShort(line, pos); }
    Token new15(int line, int pos) { return new TChar(line, pos); }
    Token new16(int line, int pos) { return new TInt(line, pos); }
    Token new17(int line, int pos) { return new TLong(line, pos); }
    Token new18(int line, int pos) { return new TFloat(line, pos); }
    Token new19(int line, int pos) { return new TDouble(line, pos); }
    Token new20(int line, int pos) { return new TUnknown(line, pos); }
    Token new21(int line, int pos) { return new TExtends(line, pos); }
    Token new22(int line, int pos) { return new TImplements(line, pos); }
    Token new23(int line, int pos) { return new TBreakpoint(line, pos); }
    Token new24(int line, int pos) { return new TNew(line, pos); }
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
    Token new38(int line, int pos) { return new TLookupswitch(line, pos); }
    Token new39(int line, int pos) { return new TNop(line, pos); }
    Token new40(int line, int pos) { return new TRet(line, pos); }
    Token new41(int line, int pos) { return new TReturn(line, pos); }
    Token new42(int line, int pos) { return new TSpecialinvoke(line, pos); }
    Token new43(int line, int pos) { return new TStaticinvoke(line, pos); }
    Token new44(int line, int pos) { return new TTableswitch(line, pos); }
    Token new45(int line, int pos) { return new TThrow(line, pos); }
    Token new46(int line, int pos) { return new TTo(line, pos); }
    Token new47(int line, int pos) { return new TVirtualinvoke(line, pos); }
    Token new48(int line, int pos) { return new TWith(line, pos); }
    Token new49(int line, int pos) { return new TComma(line, pos); }
    Token new50(int line, int pos) { return new TLBrace(line, pos); }
    Token new51(int line, int pos) { return new TRBrace(line, pos); }
    Token new52(int line, int pos) { return new TSemicolon(line, pos); }
    Token new53(int line, int pos) { return new TLBracket(line, pos); }
    Token new54(int line, int pos) { return new TRBracket(line, pos); }
    Token new55(int line, int pos) { return new TLAngledBracket(line, pos); }
    Token new56(int line, int pos) { return new TRAngledBracket(line, pos); }
    Token new57(int line, int pos) { return new TLParen(line, pos); }
    Token new58(int line, int pos) { return new TRParen(line, pos); }
    Token new59(int line, int pos) { return new TColon(line, pos); }
    Token new60(int line, int pos) { return new TDot(line, pos); }
    Token new61(int line, int pos) { return new TQuote(line, pos); }
    Token new62(int line, int pos) { return new TColonEquals(line, pos); }
    Token new63(int line, int pos) { return new TEquals(line, pos); }
    Token new64(int line, int pos) { return new TAnd(line, pos); }
    Token new65(int line, int pos) { return new TOr(line, pos); }
    Token new66(int line, int pos) { return new TXor(line, pos); }
    Token new67(int line, int pos) { return new TMod(line, pos); }
    Token new68(int line, int pos) { return new TCmpeq(line, pos); }
    Token new69(int line, int pos) { return new TCmpne(line, pos); }
    Token new70(int line, int pos) { return new TCmpgt(line, pos); }
    Token new71(int line, int pos) { return new TCmpge(line, pos); }
    Token new72(int line, int pos) { return new TCmplt(line, pos); }
    Token new73(int line, int pos) { return new TCmple(line, pos); }
    Token new74(int line, int pos) { return new TShl(line, pos); }
    Token new75(int line, int pos) { return new TShr(line, pos); }
    Token new76(int line, int pos) { return new TUshr(line, pos); }
    Token new77(int line, int pos) { return new TPlus(line, pos); }
    Token new78(int line, int pos) { return new TMinus(line, pos); }
    Token new79(int line, int pos) { return new TMult(line, pos); }
    Token new80(int line, int pos) { return new TDiv(line, pos); }
    Token new81(String text, int line, int pos) { return new TClassIdentifier(text, line, pos); }
    Token new82(String text, int line, int pos) { return new TSimpleIdentifier(text, line, pos); }
    Token new83(String text, int line, int pos) { return new TAtIdentifier(text, line, pos); }
    Token new84(String text, int line, int pos) { return new TBoolConstant(text, line, pos); }
    Token new85(String text, int line, int pos) { return new TIntegerConstant(text, line, pos); }
    Token new86(String text, int line, int pos) { return new TStringConstant(text, line, pos); }
    Token new87(String text, int line, int pos) { return new TBlank(text, line, pos); }
    Token new88(String text, int line, int pos) { return new TComment(text, line, pos); }

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

    private String getText(int acceptLength)
    {
        StringBuffer s = new StringBuffer(acceptLength);
        for(int i = 0; i < acceptLength; i++)
        {
            s.append(text.charAt(i));
        }

        return s.toString();
    }

    private static int[][][] gotoTable =
    {
		{{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {33, 33, 5}, {34, 34, 6}, {37, 37, 7}, {38, 38, 8}, {39, 39, 9}, {40, 40, 10}, {41, 41, 11}, {42, 42, 12}, {43, 43, 13}, {44, 44, 14}, {45, 45, 15}, {46, 46, 16}, {47, 47, 17}, {48, 48, 18}, {49, 57, 19}, {58, 58, 20}, {59, 59, 21}, {60, 60, 22}, {61, 61, 23}, {62, 62, 24}, {64, 64, 25}, {65, 90, 26}, {91, 91, 27}, {93, 93, 28}, {94, 94, 29}, {95, 95, 30}, {97, 97, 31}, {98, 98, 32}, {99, 99, 33}, {100, 100, 34}, {101, 101, 35}, {102, 102, 36}, {103, 103, 37}, {104, 104, 38}, {105, 105, 39}, {106, 107, 38}, {108, 108, 40}, {109, 109, 38}, {110, 110, 41}, {111, 111, 38}, {112, 112, 42}, {113, 113, 38}, {114, 114, 43}, {115, 115, 44}, {116, 116, 45}, {117, 117, 46}, {118, 118, 47}, {119, 119, 48}, {120, 122, 38}, {123, 123, 49}, {124, 124, 50}, {125, 125, 51}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{61, 61, 52}, },
		{{0, 9, 53}, {11, 12, 53}, {14, 33, 53}, {34, 34, 54}, {35, 65535, 53}, },
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{},
		{{42, 42, 55}, {47, 47, 56}, },
		{{48, 55, 57}, {56, 57, 58}, {60, 60, 59}, {62, 62, 60}, {65, 87, 61}, {88, 88, 62}, {89, 90, 61}, {95, 95, 30}, {97, 119, 63}, {120, 120, 64}, {121, 122, 63}, },
		{{48, 57, 65}, {60, 62, -20}, {65, 90, 61}, {95, 95, 30}, {97, 122, 63}, },
		{{61, 61, 66}, },
		{},
		{{48, 57, 58}, {60, 60, 67}, {61, 61, 68}, {62, 122, -21}, },
		{{61, 61, 69}, },
		{{48, 57, 58}, {60, 60, 59}, {62, 62, 70}, {65, 122, -21}, },
		{{112, 112, 71}, {116, 116, 72}, },
		{{48, 57, 73}, {60, 62, -20}, {65, 90, 74}, {95, 95, 75}, {97, 122, 76}, },
		{},
		{},
		{},
		{{48, 60, -26}, {62, 122, -21}, },
		{{48, 95, -28}, {97, 97, 76}, {98, 98, 77}, {99, 122, 76}, },
		{{48, 95, -28}, {97, 110, 76}, {111, 111, 78}, {112, 113, 76}, {114, 114, 79}, {115, 120, 76}, {121, 121, 80}, {122, 122, 76}, },
		{{48, 95, -28}, {97, 97, 81}, {98, 103, 76}, {104, 104, 82}, {105, 107, 76}, {108, 108, 83}, {109, 109, 84}, {110, 122, 76}, },
		{{48, 95, -28}, {97, 100, 76}, {101, 101, 85}, {102, 110, 76}, {111, 111, 86}, {112, 122, 76}, },
		{{48, 95, -28}, {97, 109, 76}, {110, 110, 87}, {111, 119, 76}, {120, 120, 88}, {121, 122, 76}, },
		{{48, 95, -28}, {97, 97, 89}, {98, 104, 76}, {105, 105, 90}, {106, 107, 76}, {108, 108, 91}, {109, 113, 76}, {114, 114, 92}, {115, 122, 76}, },
		{{48, 110, -34}, {111, 111, 93}, {112, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 101, 76}, {102, 102, 94}, {103, 108, 76}, {109, 109, 95}, {110, 110, 96}, {111, 122, 76}, },
		{{48, 110, -34}, {111, 111, 97}, {112, 122, 76}, },
		{{48, 100, -36}, {101, 101, 98}, {102, 110, 76}, {111, 111, 99}, {112, 122, 76}, },
		{{48, 95, -28}, {97, 113, 76}, {114, 114, 100}, {115, 116, 76}, {117, 117, 101}, {118, 122, 76}, },
		{{48, 100, -36}, {101, 101, 102}, {102, 122, 76}, },
		{{48, 95, -28}, {97, 103, 76}, {104, 104, 103}, {105, 111, 76}, {112, 112, 104}, {113, 115, 76}, {116, 116, 105}, {117, 120, 76}, {121, 121, 106}, {122, 122, 76}, },
		{{48, 95, -28}, {97, 97, 107}, {98, 103, 76}, {104, 104, 108}, {105, 110, 76}, {111, 111, 109}, {112, 113, 76}, {114, 114, 110}, {115, 122, 76}, },
		{{48, 109, -37}, {110, 110, 111}, {111, 122, 76}, },
		{{48, 95, -28}, {97, 104, 76}, {105, 105, 112}, {106, 110, 76}, {111, 111, 113}, {112, 122, 76}, },
		{{48, 104, -49}, {105, 105, 114}, {106, 122, 76}, },
		{},
		{},
		{},
		{},
		{{0, 65535, -8}, },
		{},
		{{0, 41, 115}, {42, 42, 116}, {43, 65535, 115}, },
		{{0, 9, 117}, {11, 12, 117}, {14, 65535, 117}, },
		{{48, 62, -20}, {65, 122, -21}, },
		{{48, 122, -32}, },
		{{48, 122, -32}, },
		{{48, 122, -32}, },
		{{48, 122, -32}, },
		{{48, 57, 118}, {60, 62, -20}, {65, 70, 119}, {71, 90, 61}, {95, 95, 30}, {97, 102, 120}, {103, 122, 63}, },
		{{48, 122, -32}, },
		{{48, 122, -64}, },
		{{48, 122, -21}, },
		{},
		{{48, 122, -32}, },
		{},
		{},
		{{48, 60, -26}, {62, 62, 121}, {65, 122, -21}, },
		{{97, 97, 122}, },
		{{104, 104, 123}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 114, 76}, {115, 115, 124}, {116, 122, 76}, },
		{{48, 110, -34}, {111, 111, 125}, {112, 122, 76}, },
		{{48, 100, -36}, {101, 101, 126}, {102, 122, 76}, },
		{{48, 95, -28}, {97, 115, 76}, {116, 116, 127}, {117, 122, 76}, },
		{{48, 114, -79}, {115, 115, 128}, {116, 116, 129}, {117, 122, 76}, },
		{{48, 95, -28}, {97, 97, 130}, {98, 122, 76}, },
		{{48, 95, -28}, {97, 97, 131}, {98, 122, 76}, },
		{{48, 95, -28}, {97, 111, 76}, {112, 112, 132}, {113, 122, 76}, },
		{{48, 101, -41}, {102, 102, 133}, {103, 122, 76}, },
		{{48, 95, -28}, {97, 116, 76}, {117, 117, 134}, {118, 122, 76}, },
		{{48, 115, -82}, {116, 116, 135}, {117, 122, 76}, },
		{{48, 104, -49}, {105, 105, 136}, {106, 115, 76}, {116, 116, 137}, {117, 122, 76}, },
		{{48, 95, -28}, {97, 107, 76}, {108, 108, 138}, {109, 122, 76}, },
		{{48, 109, -37}, {110, 110, 139}, {111, 122, 76}, },
		{{48, 110, -34}, {111, 111, 140}, {112, 122, 76}, },
		{{48, 110, -34}, {111, 111, 141}, {112, 122, 76}, },
		{{48, 115, -82}, {116, 116, 142}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 111, -86}, {112, 112, 143}, {113, 122, 76}, },
		{{48, 114, -79}, {115, 115, 144}, {116, 116, 145}, {117, 122, 76}, },
		{{48, 109, -37}, {110, 110, 146}, {111, 111, 147}, {112, 122, 76}, },
		{{48, 95, -28}, {97, 118, 76}, {119, 119, 148}, {120, 122, 76}, },
		{{48, 111, -86}, {112, 112, 149}, {113, 122, 76}, },
		{{48, 104, -49}, {105, 105, 150}, {106, 110, 76}, {111, 111, 151}, {112, 122, 76}, },
		{{48, 97, -33}, {98, 98, 152}, {99, 122, 76}, },
		{{48, 115, -82}, {116, 116, 153}, {117, 122, 76}, },
		{{48, 110, -34}, {111, 111, 154}, {112, 122, 76}, },
		{{48, 100, -36}, {101, 101, 155}, {102, 122, 76}, },
		{{48, 95, -28}, {97, 97, 156}, {98, 122, 76}, },
		{{48, 109, -37}, {110, 110, 157}, {111, 122, 76}, },
		{{48, 97, -33}, {98, 98, 158}, {99, 122, 76}, },
		{{48, 113, -44}, {114, 114, 159}, {115, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 97, 160}, {98, 116, 76}, {117, 117, 161}, {118, 122, 76}, },
		{{48, 95, -28}, {97, 106, 76}, {107, 107, 162}, {108, 122, 76}, },
		{{48, 113, -44}, {114, 114, 163}, {115, 122, 76}, },
		{{48, 104, -49}, {105, 105, 164}, {106, 107, 76}, {108, 108, 165}, {109, 122, 76}, },
		{{48, 115, -82}, {116, 116, 166}, {117, 122, 76}, },
		{{0, 65535, -57}, },
		{{0, 41, 167}, {42, 42, 116}, {43, 46, 167}, {47, 47, 168}, {48, 65535, 167}, },
		{{0, 65535, -58}, },
		{{48, 122, -64}, },
		{{48, 122, -64}, },
		{{48, 122, -64}, },
		{{48, 122, -32}, },
		{{114, 114, 169}, },
		{{105, 105, 170}, },
		{{48, 115, -82}, {116, 116, 171}, {117, 122, 76}, },
		{{48, 107, -91}, {108, 108, 172}, {109, 122, 76}, },
		{{48, 95, -28}, {97, 97, 173}, {98, 122, 76}, },
		{{48, 100, -36}, {101, 101, 174}, {102, 122, 76}, },
		{{48, 100, -36}, {101, 101, 175}, {102, 122, 76}, },
		{{48, 95, -28}, {97, 98, 76}, {99, 99, 176}, {100, 122, 76}, },
		{{48, 113, -44}, {114, 114, 177}, {115, 122, 76}, },
		{{48, 114, -79}, {115, 115, 178}, {116, 122, 76}, },
		{{48, 95, -28}, {97, 102, 76}, {103, 103, 179}, {104, 107, 76}, {108, 108, 180}, {109, 122, 76}, },
		{{48, 95, -28}, {97, 97, 181}, {98, 122, 76}, },
		{{48, 97, -33}, {98, 98, 182}, {99, 122, 76}, },
		{{48, 100, -36}, {101, 101, 183}, {102, 122, 76}, },
		{{48, 115, -82}, {116, 116, 184}, {117, 122, 76}, },
		{{48, 100, -36}, {101, 101, 185}, {102, 122, 76}, },
		{{48, 114, -79}, {115, 115, 186}, {116, 122, 76}, },
		{{48, 95, -28}, {97, 97, 187}, {98, 122, 76}, },
		{{48, 95, -28}, {97, 97, 188}, {98, 122, 76}, },
		{{48, 95, -28}, {97, 108, 76}, {109, 109, 189}, {110, 122, 76}, },
		{{48, 110, -34}, {111, 111, 190}, {112, 122, 76}, },
		{{48, 107, -91}, {108, 108, 191}, {109, 122, 76}, },
		{{48, 115, -82}, {116, 116, 192}, {117, 122, 76}, },
		{{48, 100, -36}, {101, 101, 193}, {102, 122, 76}, },
		{{48, 102, -134}, {103, 103, 194}, {104, 122, 76}, },
		{{48, 106, -113}, {107, 107, 195}, {108, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 117, 76}, {118, 118, 196}, {119, 122, 76}, },
		{{48, 115, -82}, {116, 116, 197}, {117, 122, 76}, },
		{{48, 107, -91}, {108, 108, 198}, {109, 122, 76}, },
		{{48, 116, -88}, {117, 117, 199}, {118, 122, 76}, },
		{{48, 113, -44}, {114, 114, 200}, {115, 122, 76}, },
		{{48, 98, -131}, {99, 99, 201}, {100, 122, 76}, },
		{{48, 115, -82}, {116, 116, 202}, {117, 122, 76}, },
		{{48, 98, -131}, {99, 99, 203}, {100, 122, 76}, },
		{{48, 107, -91}, {108, 108, 204}, {109, 122, 76}, },
		{{48, 110, -34}, {111, 111, 205}, {112, 122, 76}, },
		{{48, 109, -37}, {110, 110, 206}, {111, 122, 76}, },
		{{48, 100, -36}, {101, 101, 207}, {102, 122, 76}, },
		{{48, 109, -37}, {110, 110, 208}, {111, 122, 76}, },
		{{48, 115, -82}, {116, 116, 209}, {117, 122, 76}, },
		{{48, 95, -28}, {97, 99, 76}, {100, 100, 210}, {101, 122, 76}, },
		{{48, 95, -28}, {97, 97, 211}, {98, 122, 76}, },
		{{48, 103, -46}, {104, 104, 212}, {105, 122, 76}, },
		{{0, 41, 213}, {42, 42, 214}, {43, 65535, 213}, },
		{},
		{{97, 97, 215}, },
		{{115, 115, 216}, },
		{{48, 113, -44}, {114, 114, 217}, {115, 122, 76}, },
		{{48, 100, -36}, {101, 101, 218}, {102, 122, 76}, },
		{{48, 106, -113}, {107, 107, 219}, {108, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 103, -46}, {104, 104, 220}, {105, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 114, -79}, {115, 115, 221}, {116, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 116, -88}, {117, 117, 222}, {118, 122, 76}, },
		{{48, 107, -91}, {108, 108, 223}, {109, 122, 76}, },
		{{48, 113, -44}, {114, 114, 224}, {115, 122, 76}, },
		{{48, 108, -143}, {109, 109, 225}, {110, 122, 76}, },
		{{48, 109, -37}, {110, 110, 226}, {111, 122, 76}, },
		{{48, 100, -36}, {101, 101, 227}, {102, 122, 76}, },
		{{48, 107, -91}, {108, 108, 228}, {109, 122, 76}, },
		{{48, 115, -82}, {116, 116, 229}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 100, -36}, {101, 101, 230}, {102, 122, 76}, },
		{{48, 95, -28}, {97, 97, 231}, {98, 122, 76}, },
		{{48, 113, -44}, {114, 114, 232}, {115, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 116, -88}, {117, 117, 233}, {118, 122, 76}, },
		{{48, 95, -28}, {97, 97, 234}, {98, 122, 76}, },
		{{48, 100, -36}, {101, 101, 235}, {102, 122, 76}, },
		{{48, 104, -49}, {105, 105, 236}, {106, 122, 76}, },
		{{48, 113, -44}, {114, 114, 237}, {115, 122, 76}, },
		{{48, 115, -82}, {116, 116, 238}, {117, 122, 76}, },
		{{48, 104, -49}, {105, 105, 239}, {106, 122, 76}, },
		{{48, 104, -49}, {105, 105, 240}, {106, 122, 76}, },
		{{48, 103, -46}, {104, 104, 241}, {105, 122, 76}, },
		{{48, 100, -36}, {101, 101, 242}, {102, 122, 76}, },
		{{48, 118, -100}, {119, 119, 243}, {120, 122, 76}, },
		{{48, 114, -79}, {115, 115, 244}, {116, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 110, -34}, {111, 111, 245}, {112, 122, 76}, },
		{{48, 116, -88}, {117, 117, 246}, {118, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 115, -82}, {116, 116, 247}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{0, 65535, -169}, },
		{{0, 41, 167}, {42, 42, 214}, {43, 65535, -118}, },
		{{109, 109, 248}, },
		{},
		{{48, 95, -28}, {97, 97, 249}, {98, 122, 76}, },
		{{48, 95, -28}, {97, 97, 250}, {98, 122, 76}, },
		{{48, 111, -86}, {112, 112, 251}, {113, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 107, -91}, {108, 108, 252}, {109, 122, 76}, },
		{{48, 100, -36}, {101, 101, 253}, {102, 122, 76}, },
		{{48, 108, -143}, {109, 109, 254}, {110, 122, 76}, },
		{{48, 110, -34}, {111, 111, 255}, {112, 122, 76}, },
		{{48, 99, -166}, {100, 100, 256}, {101, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 108, -143}, {109, 109, 257}, {110, 122, 76}, },
		{{48, 109, -37}, {110, 110, 258}, {111, 122, 76}, },
		{{48, 101, -41}, {102, 102, 259}, {103, 122, 76}, },
		{{48, 111, -86}, {112, 112, 260}, {113, 122, 76}, },
		{{48, 115, -82}, {116, 116, 261}, {117, 122, 76}, },
		{{48, 98, -131}, {99, 99, 262}, {100, 122, 76}, },
		{{48, 98, -131}, {99, 99, 263}, {100, 122, 76}, },
		{{48, 109, -37}, {110, 110, 264}, {111, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 97, 265}, {98, 122, 76}, },
		{{48, 98, -131}, {99, 99, 266}, {100, 122, 76}, },
		{{48, 113, -44}, {114, 114, 267}, {115, 122, 76}, },
		{{48, 114, -79}, {115, 115, 268}, {116, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 104, -49}, {105, 105, 269}, {106, 122, 76}, },
		{{48, 118, -100}, {119, 119, 270}, {120, 122, 76}, },
		{{48, 95, -28}, {97, 97, 271}, {98, 122, 76}, },
		{{48, 104, -49}, {105, 105, 272}, {106, 122, 76}, },
		{{101, 101, 273}, },
		{{48, 98, -131}, {99, 99, 274}, {100, 122, 76}, },
		{{48, 109, -37}, {110, 110, 275}, {111, 122, 76}, },
		{{48, 110, -34}, {111, 111, 276}, {112, 122, 76}, },
		{{48, 115, -82}, {116, 116, 277}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 110, -34}, {111, 111, 278}, {112, 122, 76}, },
		{{48, 109, -37}, {110, 110, 279}, {111, 122, 76}, },
		{{48, 114, -79}, {115, 115, 280}, {116, 122, 76}, },
		{{48, 100, -36}, {101, 101, 281}, {102, 122, 76}, },
		{{48, 98, -131}, {99, 99, 282}, {100, 122, 76}, },
		{{48, 95, -28}, {97, 97, 283}, {98, 122, 76}, },
		{{48, 114, -79}, {115, 115, 284}, {116, 122, 76}, },
		{{48, 100, -36}, {101, 101, 285}, {102, 122, 76}, },
		{{48, 115, -82}, {116, 116, 286}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 107, -91}, {108, 108, 287}, {109, 122, 76}, },
		{{48, 104, -49}, {105, 105, 288}, {106, 122, 76}, },
		{{48, 110, -34}, {111, 111, 289}, {112, 122, 76}, },
		{{48, 118, -100}, {119, 119, 290}, {120, 122, 76}, },
		{{48, 100, -36}, {101, 101, 291}, {102, 122, 76}, },
		{{48, 109, -37}, {110, 110, 292}, {111, 122, 76}, },
		{{48, 107, -91}, {108, 108, 293}, {109, 122, 76}, },
		{{48, 107, -91}, {108, 108, 294}, {109, 122, 76}, },
		{{116, 116, 295}, },
		{{48, 115, -82}, {116, 116, 296}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 104, -49}, {105, 105, 297}, {106, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 109, -37}, {110, 110, 298}, {111, 122, 76}, },
		{{48, 104, -49}, {105, 105, 299}, {106, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 109, -37}, {110, 110, 300}, {111, 122, 76}, },
		{{48, 100, -36}, {101, 101, 301}, {102, 122, 76}, },
		{{48, 98, -131}, {99, 99, 302}, {100, 122, 76}, },
		{{48, 118, -100}, {119, 119, 303}, {120, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 100, -36}, {101, 101, 304}, {102, 122, 76}, },
		{{48, 104, -49}, {105, 105, 305}, {106, 122, 76}, },
		{{48, 109, -37}, {110, 110, 306}, {111, 122, 76}, },
		{{48, 109, -37}, {110, 110, 307}, {111, 122, 76}, },
		{{48, 104, -49}, {105, 105, 308}, {106, 122, 76}, },
		{{48, 109, -37}, {110, 110, 309}, {111, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 104, -49}, {105, 105, 310}, {106, 122, 76}, },
		{{48, 100, -36}, {101, 101, 311}, {102, 122, 76}, },
		{{101, 101, 312}, },
		{{48, 122, -28}, },
		{{48, 109, -37}, {110, 110, 313}, {111, 122, 76}, },
		{{48, 104, -49}, {105, 105, 314}, {106, 122, 76}, },
		{{48, 115, -82}, {116, 116, 315}, {117, 122, 76}, },
		{{48, 115, -82}, {116, 116, 316}, {117, 122, 76}, },
		{{48, 110, -34}, {111, 111, 317}, {112, 122, 76}, },
		{{48, 100, -36}, {101, 101, 318}, {102, 122, 76}, },
		{{48, 104, -49}, {105, 105, 319}, {106, 122, 76}, },
		{{48, 99, -166}, {100, 100, 320}, {101, 122, 76}, },
		{{48, 109, -37}, {110, 110, 321}, {111, 122, 76}, },
		{{48, 117, -152}, {118, 118, 322}, {119, 122, 76}, },
		{{48, 104, -49}, {105, 105, 323}, {106, 122, 76}, },
		{{48, 115, -82}, {116, 116, 324}, {117, 122, 76}, },
		{{48, 115, -82}, {116, 116, 325}, {117, 122, 76}, },
		{{48, 109, -37}, {110, 110, 326}, {111, 122, 76}, },
		{{48, 122, -28}, },
		{{114, 114, 327}, },
		{{48, 115, -82}, {116, 116, 328}, {117, 122, 76}, },
		{{48, 115, -82}, {116, 116, 329}, {117, 122, 76}, },
		{{48, 110, -34}, {111, 111, 330}, {112, 122, 76}, },
		{{48, 114, -79}, {115, 115, 331}, {116, 122, 76}, },
		{{48, 101, -41}, {102, 102, 332}, {103, 122, 76}, },
		{{48, 104, -49}, {105, 105, 333}, {106, 122, 76}, },
		{{48, 115, -82}, {116, 116, 334}, {117, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 117, -152}, {118, 118, 335}, {119, 122, 76}, },
		{{48, 110, -34}, {111, 111, 336}, {112, 122, 76}, },
		{{48, 95, -28}, {97, 121, 76}, {122, 122, 337}, },
		{{48, 98, -131}, {99, 99, 338}, {100, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 117, -152}, {118, 118, 339}, {119, 122, 76}, },
		{{48, 57, 340}, },
		{{48, 122, -28}, },
		{{48, 110, -34}, {111, 111, 341}, {112, 122, 76}, },
		{{48, 113, -44}, {114, 114, 342}, {115, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 109, -37}, {110, 110, 343}, {111, 122, 76}, },
		{{48, 98, -131}, {99, 99, 344}, {100, 122, 76}, },
		{{48, 110, -34}, {111, 111, 345}, {112, 122, 76}, },
		{{48, 106, -113}, {107, 107, 346}, {108, 122, 76}, },
		{{48, 100, -36}, {101, 101, 347}, {102, 122, 76}, },
		{{48, 103, -46}, {104, 104, 348}, {105, 122, 76}, },
		{{48, 110, -34}, {111, 111, 349}, {112, 122, 76}, },
		{{48, 57, 340}, },
		{{48, 113, -44}, {114, 114, 350}, {115, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 117, -152}, {118, 118, 351}, {119, 122, 76}, },
		{{48, 103, -46}, {104, 104, 352}, {105, 122, 76}, },
		{{48, 106, -113}, {107, 107, 353}, {108, 122, 76}, },
		{{48, 100, -36}, {101, 101, 354}, {102, 122, 76}, },
		{{48, 99, -166}, {100, 100, 355}, {101, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 106, -113}, {107, 107, 356}, {108, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 110, -34}, {111, 111, 357}, {112, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 100, -36}, {101, 101, 358}, {102, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 100, -36}, {101, 101, 359}, {102, 122, 76}, },
		{{48, 106, -113}, {107, 107, 360}, {108, 122, 76}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 100, -36}, {101, 101, 361}, {102, 122, 76}, },
		{{48, 122, -28}, },
    };

    private static int[][] accept =
    {
		{-1, 87, 87, 87, 87, -1, -1, 67, 64, 61, 57, 58, 79, 77, 49, 78, 60, 80, 81, 81, 59, 52, 55, 63, 56, -1, 81, 53, 54, 66, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 50, 65, 51, 69, -1, 86, -1, 88, 81, 81, 81, 81, 81, 81, 81, 81, 81, 62, 74, 71, 68, 75, -1, -1, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 35, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 46, 81, 81, 81, 81, 81, -1, -1, 88, 81, 81, 81, 76, -1, -1, 81, 81, 81, 81, 81, 81, 81, 81, 27, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 16, 81, 81, 24, 39, 81, 81, 81, 40, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, -1, 88, -1, -1, 81, 81, 81, 13, 25, 81, 15, 81, 28, 29, 81, 81, 81, 81, 81, 81, 81, 81, 33, 34, 81, 81, 81, 17, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 11, 81, 48, -1, -1, -1, 83, 81, 81, 81, 26, 9, 81, 81, 81, 81, 81, 81, 4, 18, 81, 81, 81, 81, 81, 81, 81, 81, 14, 81, 81, 81, 81, 45, 81, 81, 81, 81, -1, 81, 81, 81, 81, 19, 81, 81, 81, 81, 81, 81, 81, 81, 81, 0, 41, 81, 3, 81, 81, 81, 81, 81, 81, -1, 81, 12, 81, 30, 81, 81, 21, 81, 81, 81, 81, 2, 81, 81, 81, 81, 81, 81, 20, 81, 81, -1, 5, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 81, 8, -1, 81, 81, 81, 81, 81, 10, 81, 1, 81, 81, 81, 81, 7, 81, -1, 23, 81, 81, 22, 36, 81, 81, 81, 81, 81, 81, 81, 83, 81, 32, 81, 81, 81, 81, 81, 44, 81, 31, 81, 38, 81, 43, 6, 81, 81, 42, 47, 81, 37, },

    };

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
