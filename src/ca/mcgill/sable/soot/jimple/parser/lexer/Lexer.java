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
    Token new25(int line, int pos) { return new TNew(line, pos); }
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
    Token new41(int line, int pos) { return new TNop(line, pos); }
    Token new42(int line, int pos) { return new TRet(line, pos); }
    Token new43(int line, int pos) { return new TReturn(line, pos); }
    Token new44(int line, int pos) { return new TSpecialinvoke(line, pos); }
    Token new45(int line, int pos) { return new TStaticinvoke(line, pos); }
    Token new46(int line, int pos) { return new TTableswitch(line, pos); }
    Token new47(int line, int pos) { return new TThrow(line, pos); }
    Token new48(int line, int pos) { return new TTo(line, pos); }
    Token new49(int line, int pos) { return new TVirtualinvoke(line, pos); }
    Token new50(int line, int pos) { return new TWith(line, pos); }
    Token new51(int line, int pos) { return new TComma(line, pos); }
    Token new52(int line, int pos) { return new TLBrace(line, pos); }
    Token new53(int line, int pos) { return new TRBrace(line, pos); }
    Token new54(int line, int pos) { return new TSemicolon(line, pos); }
    Token new55(int line, int pos) { return new TLBracket(line, pos); }
    Token new56(int line, int pos) { return new TRBracket(line, pos); }
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
    Token new81(String text, int line, int pos) { return new TName(text, line, pos); }
    Token new82(String text, int line, int pos) { return new TIdentifier(text, line, pos); }
    Token new83(String text, int line, int pos) { return new TAtIdentifier(text, line, pos); }
    Token new84(String text, int line, int pos) { return new TBoolConstant(text, line, pos); }
    Token new85(String text, int line, int pos) { return new TIntegerConstant(text, line, pos); }
    Token new86(String text, int line, int pos) { return new TFloatConstant(text, line, pos); }
    Token new87(String text, int line, int pos) { return new TStringConstant(text, line, pos); }
    Token new88(String text, int line, int pos) { return new TBlank(text, line, pos); }
    Token new89(String text, int line, int pos) { return new TComment(text, line, pos); }

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
		{{9, 9, 1}, {10, 10, 2}, {13, 13, 3}, {32, 32, 4}, {33, 33, 5}, {34, 34, 6}, {37, 37, 7}, {38, 38, 8}, {39, 39, 9}, {40, 40, 10}, {41, 41, 11}, {42, 42, 12}, {43, 43, 13}, {44, 44, 14}, {45, 45, 15}, {46, 46, 16}, {47, 47, 17}, {48, 48, 18}, {49, 57, 19}, {58, 58, 20}, {59, 59, 21}, {60, 60, 22}, {61, 61, 23}, {62, 62, 24}, {64, 64, 25}, {65, 90, 26}, {91, 91, 27}, {93, 93, 28}, {94, 94, 29}, {97, 97, 30}, {98, 98, 31}, {99, 99, 32}, {100, 100, 33}, {101, 101, 34}, {102, 102, 35}, {103, 103, 36}, {104, 104, 37}, {105, 105, 38}, {106, 107, 37}, {108, 108, 39}, {109, 109, 37}, {110, 110, 40}, {111, 111, 37}, {112, 112, 41}, {113, 113, 37}, {114, 114, 42}, {115, 115, 43}, {116, 116, 44}, {117, 117, 45}, {118, 118, 46}, {119, 119, 47}, {120, 122, 37}, {123, 123, 48}, {124, 124, 49}, {125, 125, 50}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{9, 32, -2}, },
		{{61, 61, 51}, },
		{{0, 9, 52}, {11, 12, 52}, {14, 33, 52}, {34, 34, 53}, {35, 65535, 52}, },
		{},
		{},
		{{0, 9, 54}, {11, 12, 54}, {14, 38, 54}, {40, 65535, 54}, },
		{},
		{},
		{},
		{},
		{},
		{{48, 57, -2}, },
		{},
		{{42, 42, 55}, {47, 47, 56}, },
		{{46, 46, 57}, {48, 55, 58}, {56, 57, 19}, {76, 76, 59}, {88, 88, 60}, {120, 120, 61}, },
		{{46, 46, 57}, {48, 57, 19}, {76, 76, 59}, },
		{{61, 61, 62}, },
		{},
		{{60, 60, 63}, {61, 61, 64}, },
		{{61, 61, 65}, },
		{{61, 61, 66}, {62, 62, 67}, },
		{{99, 99, 68}, {112, 112, 69}, {116, 116, 70}, },
		{{48, 57, 71}, {65, 90, 72}, {95, 95, 73}, {97, 122, 74}, },
		{},
		{},
		{},
		{{48, 95, -28}, {97, 97, 74}, {98, 98, 75}, {99, 122, 74}, },
		{{48, 95, -28}, {97, 110, 74}, {111, 111, 76}, {112, 113, 74}, {114, 114, 77}, {115, 120, 74}, {121, 121, 78}, {122, 122, 74}, },
		{{48, 95, -28}, {97, 97, 79}, {98, 103, 74}, {104, 104, 80}, {105, 107, 74}, {108, 108, 81}, {109, 109, 82}, {110, 122, 74}, },
		{{48, 95, -28}, {97, 100, 74}, {101, 101, 83}, {102, 110, 74}, {111, 111, 84}, {112, 122, 74}, },
		{{48, 95, -28}, {97, 109, 74}, {110, 110, 85}, {111, 119, 74}, {120, 120, 86}, {121, 122, 74}, },
		{{48, 95, -28}, {97, 97, 87}, {98, 104, 74}, {105, 105, 88}, {106, 107, 74}, {108, 108, 89}, {109, 113, 74}, {114, 114, 90}, {115, 122, 74}, },
		{{48, 110, -33}, {111, 111, 91}, {112, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 101, 74}, {102, 102, 92}, {103, 108, 74}, {109, 109, 93}, {110, 110, 94}, {111, 122, 74}, },
		{{48, 100, -35}, {101, 101, 95}, {102, 110, 74}, {111, 111, 96}, {112, 122, 74}, },
		{{48, 95, -28}, {97, 97, 97}, {98, 100, 74}, {101, 101, 98}, {102, 110, 74}, {111, 111, 99}, {112, 122, 74}, },
		{{48, 95, -28}, {97, 113, 74}, {114, 114, 100}, {115, 116, 74}, {117, 117, 101}, {118, 122, 74}, },
		{{48, 100, -35}, {101, 101, 102}, {102, 122, 74}, },
		{{48, 95, -28}, {97, 103, 74}, {104, 104, 103}, {105, 111, 74}, {112, 112, 104}, {113, 115, 74}, {116, 116, 105}, {117, 120, 74}, {121, 121, 106}, {122, 122, 74}, },
		{{48, 95, -28}, {97, 97, 107}, {98, 103, 74}, {104, 104, 108}, {105, 110, 74}, {111, 111, 109}, {112, 113, 74}, {114, 114, 110}, {115, 122, 74}, },
		{{48, 109, -36}, {110, 110, 111}, {111, 122, 74}, },
		{{48, 95, -28}, {97, 104, 74}, {105, 105, 112}, {106, 110, 74}, {111, 111, 113}, {112, 122, 74}, },
		{{48, 104, -48}, {105, 105, 114}, {106, 122, 74}, },
		{},
		{},
		{},
		{},
		{{0, 65535, -8}, },
		{},
		{{0, 38, -11}, {39, 39, 115}, {40, 65535, 54}, },
		{{0, 41, 116}, {42, 42, 117}, {43, 65535, 116}, },
		{{0, 9, 118}, {11, 12, 118}, {14, 65535, 118}, },
		{{48, 57, 119}, },
		{{46, 76, -20}, },
		{},
		{{48, 57, 120}, {65, 70, 121}, {97, 102, 122}, },
		{{48, 102, -62}, },
		{},
		{},
		{},
		{},
		{},
		{{62, 62, 123}, },
		{{97, 97, 124}, },
		{{97, 97, 125}, },
		{{104, 104, 126}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 114, 74}, {115, 115, 127}, {116, 122, 74}, },
		{{48, 110, -33}, {111, 111, 128}, {112, 122, 74}, },
		{{48, 100, -35}, {101, 101, 129}, {102, 122, 74}, },
		{{48, 95, -28}, {97, 115, 74}, {116, 116, 130}, {117, 122, 74}, },
		{{48, 114, -77}, {115, 115, 131}, {116, 116, 132}, {117, 122, 74}, },
		{{48, 95, -28}, {97, 97, 133}, {98, 122, 74}, },
		{{48, 95, -28}, {97, 97, 134}, {98, 122, 74}, },
		{{48, 95, -28}, {97, 111, 74}, {112, 112, 135}, {113, 122, 74}, },
		{{48, 101, -40}, {102, 102, 136}, {103, 122, 74}, },
		{{48, 95, -28}, {97, 116, 74}, {117, 117, 137}, {118, 122, 74}, },
		{{48, 115, -80}, {116, 116, 138}, {117, 122, 74}, },
		{{48, 104, -48}, {105, 105, 139}, {106, 115, 74}, {116, 116, 140}, {117, 122, 74}, },
		{{48, 95, -28}, {97, 107, 74}, {108, 108, 141}, {109, 122, 74}, },
		{{48, 109, -36}, {110, 110, 142}, {111, 122, 74}, },
		{{48, 110, -33}, {111, 111, 143}, {112, 122, 74}, },
		{{48, 110, -33}, {111, 111, 144}, {112, 122, 74}, },
		{{48, 115, -80}, {116, 116, 145}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 111, -84}, {112, 112, 146}, {113, 122, 74}, },
		{{48, 114, -77}, {115, 115, 147}, {116, 116, 148}, {117, 122, 74}, },
		{{48, 109, -36}, {110, 110, 149}, {111, 122, 74}, },
		{{48, 109, -36}, {110, 110, 150}, {111, 111, 151}, {112, 122, 74}, },
		{{48, 115, -80}, {116, 116, 152}, {117, 122, 74}, },
		{{48, 95, -28}, {97, 118, 74}, {119, 119, 153}, {120, 122, 74}, },
		{{48, 111, -84}, {112, 112, 154}, {113, 122, 74}, },
		{{48, 104, -48}, {105, 105, 155}, {106, 110, 74}, {111, 111, 156}, {112, 122, 74}, },
		{{48, 97, -32}, {98, 98, 157}, {99, 122, 74}, },
		{{48, 115, -80}, {116, 116, 158}, {117, 122, 74}, },
		{{48, 110, -33}, {111, 111, 159}, {112, 122, 74}, },
		{{48, 100, -35}, {101, 101, 160}, {102, 122, 74}, },
		{{48, 95, -28}, {97, 97, 161}, {98, 122, 74}, },
		{{48, 109, -36}, {110, 110, 162}, {111, 122, 74}, },
		{{48, 97, -32}, {98, 98, 163}, {99, 122, 74}, },
		{{48, 113, -43}, {114, 114, 164}, {115, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 97, 165}, {98, 116, 74}, {117, 117, 166}, {118, 122, 74}, },
		{{48, 95, -28}, {97, 106, 74}, {107, 107, 167}, {108, 122, 74}, },
		{{48, 113, -43}, {114, 114, 168}, {115, 122, 74}, },
		{{48, 104, -48}, {105, 105, 169}, {106, 107, 74}, {108, 108, 170}, {109, 122, 74}, },
		{{48, 115, -80}, {116, 116, 171}, {117, 122, 74}, },
		{},
		{{0, 65535, -57}, },
		{{0, 41, 172}, {42, 42, 117}, {43, 46, 172}, {47, 47, 173}, {48, 65535, 172}, },
		{{0, 65535, -58}, },
		{{48, 57, 119}, {69, 69, 174}, {70, 70, 175}, {101, 101, 176}, {102, 102, 177}, },
		{{48, 70, -62}, {76, 76, 59}, {97, 102, 122}, },
		{{48, 102, -122}, },
		{{48, 102, -122}, },
		{},
		{{117, 117, 178}, },
		{{114, 114, 179}, },
		{{105, 105, 180}, },
		{{48, 115, -80}, {116, 116, 181}, {117, 122, 74}, },
		{{48, 107, -89}, {108, 108, 182}, {109, 122, 74}, },
		{{48, 95, -28}, {97, 97, 183}, {98, 122, 74}, },
		{{48, 100, -35}, {101, 101, 184}, {102, 122, 74}, },
		{{48, 100, -35}, {101, 101, 185}, {102, 122, 74}, },
		{{48, 95, -28}, {97, 98, 74}, {99, 99, 186}, {100, 122, 74}, },
		{{48, 113, -43}, {114, 114, 187}, {115, 122, 74}, },
		{{48, 114, -77}, {115, 115, 188}, {116, 122, 74}, },
		{{48, 95, -28}, {97, 102, 74}, {103, 103, 189}, {104, 107, 74}, {108, 108, 190}, {109, 122, 74}, },
		{{48, 95, -28}, {97, 97, 191}, {98, 122, 74}, },
		{{48, 97, -32}, {98, 98, 192}, {99, 122, 74}, },
		{{48, 100, -35}, {101, 101, 193}, {102, 122, 74}, },
		{{48, 115, -80}, {116, 116, 194}, {117, 122, 74}, },
		{{48, 100, -35}, {101, 101, 195}, {102, 122, 74}, },
		{{48, 114, -77}, {115, 115, 196}, {116, 122, 74}, },
		{{48, 95, -28}, {97, 97, 197}, {98, 122, 74}, },
		{{48, 95, -28}, {97, 97, 198}, {98, 122, 74}, },
		{{48, 95, -28}, {97, 108, 74}, {109, 109, 199}, {110, 122, 74}, },
		{{48, 110, -33}, {111, 111, 200}, {112, 122, 74}, },
		{{48, 107, -89}, {108, 108, 201}, {109, 122, 74}, },
		{{48, 115, -80}, {116, 116, 202}, {117, 122, 74}, },
		{{48, 100, -35}, {101, 101, 203}, {102, 122, 74}, },
		{{48, 102, -137}, {103, 103, 204}, {104, 122, 74}, },
		{{48, 102, -137}, {103, 103, 205}, {104, 122, 74}, },
		{{48, 106, -113}, {107, 107, 206}, {108, 122, 74}, },
		{{48, 104, -48}, {105, 105, 207}, {106, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 117, 74}, {118, 118, 208}, {119, 122, 74}, },
		{{48, 115, -80}, {116, 116, 209}, {117, 122, 74}, },
		{{48, 107, -89}, {108, 108, 210}, {109, 122, 74}, },
		{{48, 116, -86}, {117, 117, 211}, {118, 122, 74}, },
		{{48, 113, -43}, {114, 114, 212}, {115, 122, 74}, },
		{{48, 98, -134}, {99, 99, 213}, {100, 122, 74}, },
		{{48, 115, -80}, {116, 116, 214}, {117, 122, 74}, },
		{{48, 98, -134}, {99, 99, 215}, {100, 122, 74}, },
		{{48, 107, -89}, {108, 108, 216}, {109, 122, 74}, },
		{{48, 110, -33}, {111, 111, 217}, {112, 122, 74}, },
		{{48, 109, -36}, {110, 110, 218}, {111, 122, 74}, },
		{{48, 100, -35}, {101, 101, 219}, {102, 122, 74}, },
		{{48, 109, -36}, {110, 110, 220}, {111, 122, 74}, },
		{{48, 115, -80}, {116, 116, 221}, {117, 122, 74}, },
		{{48, 95, -28}, {97, 99, 74}, {100, 100, 222}, {101, 122, 74}, },
		{{48, 95, -28}, {97, 97, 223}, {98, 122, 74}, },
		{{48, 103, -45}, {104, 104, 224}, {105, 122, 74}, },
		{{0, 41, 225}, {42, 42, 226}, {43, 65535, 225}, },
		{},
		{{43, 43, 227}, {45, 45, 228}, {48, 57, 229}, },
		{},
		{{43, 57, -176}, },
		{},
		{{103, 103, 230}, },
		{{97, 97, 231}, },
		{{115, 115, 232}, },
		{{48, 113, -43}, {114, 114, 233}, {115, 122, 74}, },
		{{48, 100, -35}, {101, 101, 234}, {102, 122, 74}, },
		{{48, 106, -113}, {107, 107, 235}, {108, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 103, -45}, {104, 104, 236}, {105, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 114, -77}, {115, 115, 237}, {116, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 116, -86}, {117, 117, 238}, {118, 122, 74}, },
		{{48, 107, -89}, {108, 108, 239}, {109, 122, 74}, },
		{{48, 113, -43}, {114, 114, 240}, {115, 122, 74}, },
		{{48, 108, -146}, {109, 109, 241}, {110, 122, 74}, },
		{{48, 109, -36}, {110, 110, 242}, {111, 122, 74}, },
		{{48, 100, -35}, {101, 101, 243}, {102, 122, 74}, },
		{{48, 107, -89}, {108, 108, 244}, {109, 122, 74}, },
		{{48, 115, -80}, {116, 116, 245}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 100, -35}, {101, 101, 246}, {102, 122, 74}, },
		{{48, 95, -28}, {97, 97, 247}, {98, 122, 74}, },
		{{48, 113, -43}, {114, 114, 248}, {115, 122, 74}, },
		{{48, 115, -80}, {116, 116, 249}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 116, -86}, {117, 117, 250}, {118, 122, 74}, },
		{{48, 117, -157}, {118, 118, 251}, {119, 122, 74}, },
		{{48, 95, -28}, {97, 97, 252}, {98, 122, 74}, },
		{{48, 100, -35}, {101, 101, 253}, {102, 122, 74}, },
		{{48, 104, -48}, {105, 105, 254}, {106, 122, 74}, },
		{{48, 113, -43}, {114, 114, 255}, {115, 122, 74}, },
		{{48, 115, -80}, {116, 116, 256}, {117, 122, 74}, },
		{{48, 104, -48}, {105, 105, 257}, {106, 122, 74}, },
		{{48, 104, -48}, {105, 105, 258}, {106, 122, 74}, },
		{{48, 103, -45}, {104, 104, 259}, {105, 122, 74}, },
		{{48, 100, -35}, {101, 101, 260}, {102, 122, 74}, },
		{{48, 118, -100}, {119, 119, 261}, {120, 122, 74}, },
		{{48, 114, -77}, {115, 115, 262}, {116, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 110, -33}, {111, 111, 263}, {112, 122, 74}, },
		{{48, 116, -86}, {117, 117, 264}, {118, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 115, -80}, {116, 116, 265}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{0, 65535, -174}, },
		{{0, 41, 172}, {42, 42, 226}, {43, 65535, -119}, },
		{{48, 57, 229}, },
		{{48, 57, 229}, },
		{{48, 57, 229}, {70, 70, 175}, {102, 102, 177}, },
		{{104, 104, 266}, },
		{{109, 109, 267}, },
		{},
		{{48, 95, -28}, {97, 97, 268}, {98, 122, 74}, },
		{{48, 95, -28}, {97, 97, 269}, {98, 122, 74}, },
		{{48, 111, -84}, {112, 112, 270}, {113, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 107, -89}, {108, 108, 271}, {109, 122, 74}, },
		{{48, 100, -35}, {101, 101, 272}, {102, 122, 74}, },
		{{48, 108, -146}, {109, 109, 273}, {110, 122, 74}, },
		{{48, 110, -33}, {111, 111, 274}, {112, 122, 74}, },
		{{48, 99, -171}, {100, 100, 275}, {101, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 108, -146}, {109, 109, 276}, {110, 122, 74}, },
		{{48, 109, -36}, {110, 110, 277}, {111, 122, 74}, },
		{{48, 101, -40}, {102, 102, 278}, {103, 122, 74}, },
		{{48, 103, -45}, {104, 104, 279}, {105, 122, 74}, },
		{{48, 111, -84}, {112, 112, 280}, {113, 122, 74}, },
		{{48, 100, -35}, {101, 101, 281}, {102, 122, 74}, },
		{{48, 115, -80}, {116, 116, 282}, {117, 122, 74}, },
		{{48, 98, -134}, {99, 99, 283}, {100, 122, 74}, },
		{{48, 98, -134}, {99, 99, 284}, {100, 122, 74}, },
		{{48, 109, -36}, {110, 110, 285}, {111, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 95, -28}, {97, 97, 286}, {98, 122, 74}, },
		{{48, 98, -134}, {99, 99, 287}, {100, 122, 74}, },
		{{48, 113, -43}, {114, 114, 288}, {115, 122, 74}, },
		{{48, 114, -77}, {115, 115, 289}, {116, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 104, -48}, {105, 105, 290}, {106, 122, 74}, },
		{{48, 118, -100}, {119, 119, 291}, {120, 122, 74}, },
		{{48, 95, -28}, {97, 97, 292}, {98, 122, 74}, },
		{{48, 104, -48}, {105, 105, 293}, {106, 122, 74}, },
		{{116, 116, 294}, },
		{{101, 101, 295}, },
		{{48, 98, -134}, {99, 99, 296}, {100, 122, 74}, },
		{{48, 109, -36}, {110, 110, 297}, {111, 122, 74}, },
		{{48, 110, -33}, {111, 111, 298}, {112, 122, 74}, },
		{{48, 115, -80}, {116, 116, 299}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 110, -33}, {111, 111, 300}, {112, 122, 74}, },
		{{48, 109, -36}, {110, 110, 301}, {111, 122, 74}, },
		{{48, 114, -77}, {115, 115, 302}, {116, 122, 74}, },
		{{48, 100, -35}, {101, 101, 303}, {102, 122, 74}, },
		{{48, 98, -134}, {99, 99, 304}, {100, 122, 74}, },
		{{48, 95, -28}, {97, 97, 305}, {98, 122, 74}, },
		{{48, 110, -33}, {111, 111, 306}, {112, 122, 74}, },
		{{48, 114, -77}, {115, 115, 307}, {116, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 100, -35}, {101, 101, 308}, {102, 122, 74}, },
		{{48, 115, -80}, {116, 116, 309}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 107, -89}, {108, 108, 310}, {109, 122, 74}, },
		{{48, 104, -48}, {105, 105, 311}, {106, 122, 74}, },
		{{48, 110, -33}, {111, 111, 312}, {112, 122, 74}, },
		{{48, 118, -100}, {119, 119, 313}, {120, 122, 74}, },
		{{48, 100, -35}, {101, 101, 314}, {102, 122, 74}, },
		{{48, 109, -36}, {110, 110, 315}, {111, 122, 74}, },
		{{48, 107, -89}, {108, 108, 316}, {109, 122, 74}, },
		{{48, 107, -89}, {108, 108, 317}, {109, 122, 74}, },
		{{101, 101, 318}, },
		{{116, 116, 319}, },
		{{48, 115, -80}, {116, 116, 320}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 104, -48}, {105, 105, 321}, {106, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 109, -36}, {110, 110, 322}, {111, 122, 74}, },
		{{48, 104, -48}, {105, 105, 323}, {106, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 109, -36}, {110, 110, 324}, {111, 122, 74}, },
		{{48, 100, -35}, {101, 101, 325}, {102, 122, 74}, },
		{{48, 98, -134}, {99, 99, 326}, {100, 122, 74}, },
		{{48, 101, -40}, {102, 102, 327}, {103, 122, 74}, },
		{{48, 118, -100}, {119, 119, 328}, {120, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 100, -35}, {101, 101, 329}, {102, 122, 74}, },
		{{48, 104, -48}, {105, 105, 330}, {106, 122, 74}, },
		{{48, 109, -36}, {110, 110, 331}, {111, 122, 74}, },
		{{48, 109, -36}, {110, 110, 332}, {111, 122, 74}, },
		{{48, 104, -48}, {105, 105, 333}, {106, 122, 74}, },
		{{48, 109, -36}, {110, 110, 334}, {111, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 104, -48}, {105, 105, 335}, {106, 122, 74}, },
		{{48, 100, -35}, {101, 101, 336}, {102, 122, 74}, },
		{{120, 120, 337}, },
		{{101, 101, 338}, },
		{{48, 122, -28}, },
		{{48, 109, -36}, {110, 110, 339}, {111, 122, 74}, },
		{{48, 104, -48}, {105, 105, 340}, {106, 122, 74}, },
		{{48, 115, -80}, {116, 116, 341}, {117, 122, 74}, },
		{{48, 115, -80}, {116, 116, 342}, {117, 122, 74}, },
		{{48, 110, -33}, {111, 111, 343}, {112, 122, 74}, },
		{{48, 100, -35}, {101, 101, 344}, {102, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 104, -48}, {105, 105, 345}, {106, 122, 74}, },
		{{48, 99, -171}, {100, 100, 346}, {101, 122, 74}, },
		{{48, 109, -36}, {110, 110, 347}, {111, 122, 74}, },
		{{48, 117, -157}, {118, 118, 348}, {119, 122, 74}, },
		{{48, 104, -48}, {105, 105, 349}, {106, 122, 74}, },
		{{48, 115, -80}, {116, 116, 350}, {117, 122, 74}, },
		{{48, 115, -80}, {116, 116, 351}, {117, 122, 74}, },
		{{48, 109, -36}, {110, 110, 352}, {111, 122, 74}, },
		{{48, 122, -28}, },
		{{99, 99, 353}, },
		{{114, 114, 354}, },
		{{48, 115, -80}, {116, 116, 355}, {117, 122, 74}, },
		{{48, 115, -80}, {116, 116, 356}, {117, 122, 74}, },
		{{48, 110, -33}, {111, 111, 357}, {112, 122, 74}, },
		{{48, 114, -77}, {115, 115, 358}, {116, 122, 74}, },
		{{48, 101, -40}, {102, 102, 359}, {103, 122, 74}, },
		{{48, 104, -48}, {105, 105, 360}, {106, 122, 74}, },
		{{48, 115, -80}, {116, 116, 361}, {117, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 117, -157}, {118, 118, 362}, {119, 122, 74}, },
		{{48, 110, -33}, {111, 111, 363}, {112, 122, 74}, },
		{{48, 95, -28}, {97, 121, 74}, {122, 122, 364}, },
		{{48, 98, -134}, {99, 99, 365}, {100, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 117, -157}, {118, 118, 366}, {119, 122, 74}, },
		{{101, 101, 367}, },
		{{48, 57, 368}, },
		{{48, 122, -28}, },
		{{48, 110, -33}, {111, 111, 369}, {112, 122, 74}, },
		{{48, 113, -43}, {114, 114, 370}, {115, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 109, -36}, {110, 110, 371}, {111, 122, 74}, },
		{{48, 98, -134}, {99, 99, 372}, {100, 122, 74}, },
		{{48, 110, -33}, {111, 111, 373}, {112, 122, 74}, },
		{{48, 106, -113}, {107, 107, 374}, {108, 122, 74}, },
		{{48, 100, -35}, {101, 101, 375}, {102, 122, 74}, },
		{{48, 103, -45}, {104, 104, 376}, {105, 122, 74}, },
		{{48, 110, -33}, {111, 111, 377}, {112, 122, 74}, },
		{{112, 112, 378}, },
		{{48, 57, 368}, },
		{{48, 113, -43}, {114, 114, 379}, {115, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 117, -157}, {118, 118, 380}, {119, 122, 74}, },
		{{48, 103, -45}, {104, 104, 381}, {105, 122, 74}, },
		{{48, 106, -113}, {107, 107, 382}, {108, 122, 74}, },
		{{48, 100, -35}, {101, 101, 383}, {102, 122, 74}, },
		{{48, 99, -171}, {100, 100, 384}, {101, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 106, -113}, {107, 107, 385}, {108, 122, 74}, },
		{{116, 116, 386}, },
		{{48, 122, -28}, },
		{{48, 110, -33}, {111, 111, 387}, {112, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 100, -35}, {101, 101, 388}, {102, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{48, 100, -35}, {101, 101, 389}, {102, 122, 74}, },
		{{105, 105, 390}, },
		{{48, 106, -113}, {107, 107, 391}, {108, 122, 74}, },
		{{48, 122, -28}, },
		{{48, 122, -28}, },
		{{111, 111, 392}, },
		{{48, 100, -35}, {101, 101, 393}, {102, 122, 74}, },
		{{110, 110, 394}, },
		{{48, 122, -28}, },
		{},
    };

    private static int[][] accept =
    {
		{-1, 88, 88, 88, 88, -1, -1, 67, 64, 61, 57, 58, 79, 77, 51, 78, 60, 80, 85, 85, 59, 54, 72, 63, 70, -1, 82, 55, 56, 66, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 52, 65, 53, 69, -1, 87, -1, -1, 89, -1, 85, 85, -1, -1, 62, 74, 73, 68, 71, 75, -1, -1, -1, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 36, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 48, 82, 82, 82, 82, 82, 81, -1, -1, 89, 86, 85, 85, 85, 76, -1, -1, -1, 82, 82, 82, 82, 82, 82, 82, 82, 28, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 17, 82, 82, 82, 82, 25, 41, 82, 82, 82, 42, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, -1, 89, -1, 86, -1, 86, -1, -1, -1, 82, 82, 82, 14, 26, 82, 16, 82, 29, 30, 82, 82, 82, 82, 82, 82, 82, 82, 34, 35, 82, 82, 82, 82, 18, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 12, 82, 50, -1, -1, -1, -1, 86, -1, -1, 83, 82, 82, 82, 27, 10, 82, 82, 82, 82, 82, 82, 1, 19, 82, 82, 82, 82, 82, 82, 82, 82, 82, 82, 15, 82, 82, 82, 82, 47, 82, 82, 82, 82, -1, -1, 82, 82, 82, 82, 20, 82, 82, 82, 82, 82, 82, 82, 82, 2, 82, 82, 3, 43, 82, 6, 82, 82, 82, 82, 82, 82, -1, -1, 82, 13, 82, 31, 82, 82, 22, 82, 82, 82, 82, 82, 5, 82, 82, 82, 82, 82, 82, 21, 82, 82, -1, -1, 0, 82, 82, 82, 82, 82, 82, 39, 82, 82, 82, 82, 82, 82, 82, 82, 9, -1, -1, 82, 82, 82, 82, 82, 11, 82, 4, 82, 82, 82, 82, 8, 82, -1, -1, 24, 82, 82, 23, 37, 82, 82, 82, 82, 82, 82, 82, -1, 83, 82, 33, 82, 82, 82, 82, 82, 46, 82, -1, 32, 82, 40, 82, 45, 7, 82, -1, 82, 44, 49, -1, 82, -1, 38, 83, },

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
